package com.academic.gestor.update;

import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para UpdateChecker usando un servidor HTTP local,
 * sin dependencia de la red ni de GitHub.
 */
@DisplayName("UpdateChecker")
class UpdateCheckerTest {

    private static final String VERSION_JSON = """
            {
              "version": "2.0.0",
              "releaseDate": "2026-08-18",
              "changelog": ["Cambio de prueba"],
              "minVersion": "1.0.0",
              "downloads": {
                "linux": {"url": "https://example.com/oktask.tar.gz", "filename": "oktask.tar.gz"},
                "windows": {"url": "https://example.com/oktask.msi", "filename": "oktask.msi"},
                "macos": {"url": "https://example.com/oktask.dmg", "filename": "oktask.dmg"}
              }
            }
            """;

    private HttpServer server;
    private String baseUrl;

    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/version.json", exchange -> {
            byte[] body = VERSION_JSON.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        });
        server.start();
        baseUrl = "http://localhost:" + server.getAddress().getPort() + "/version.json";
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    @Nested
    @DisplayName("Constructor")
    class Constructor {

        @Test
        @DisplayName("Constructor establece versión actual")
        void constructorSetsCurrentVersion() {
            UpdateChecker checker = new UpdateChecker("1.1.0");
            assertEquals("1.1.0", checker.getCurrentVersion());
        }

        @Test
        @DisplayName("Resultado inicial es null")
        void initialResultIsNull() {
            UpdateChecker checker = new UpdateChecker("1.1.0");
            assertNull(checker.getLastCheckResult());
        }
    }

    @Nested
    @DisplayName("Verificación de actualizaciones")
    class CheckForUpdate {

        @Test
        @DisplayName("Detecta actualización cuando la versión remota es mayor")
        void detectsUpdateWhenRemoteIsNewer() throws Exception {
            // Arrange
            UpdateChecker checker = new UpdateChecker("1.0.0", baseUrl);
            AtomicReference<UpdateInfo> result = new AtomicReference<>();

            checker.setListener(new UpdateChecker.UpdateListener() {
                @Override
                public void onUpdateAvailable(UpdateInfo info) {
                    result.set(info);
                }

                @Override
                public void onNoUpdateAvailable() {
                }

                @Override
                public void onError(String error) {
                }
            });

            // Act
            UpdateInfo info = checker.checkForUpdate(true);

            // Assert
            assertNotNull(info);
            assertEquals("2.0.0", info.getVersion());
            assertEquals("2.0.0", result.get().getVersion());
        }

        @Test
        @DisplayName("No muestra actualización cuando la versión actual es mayor o igual")
        void noUpdateWhenCurrentIsNewer() throws Exception {
            // Arrange
            UpdateChecker checker = new UpdateChecker("99.99.99", baseUrl);
            AtomicReference<String> action = new AtomicReference<>();

            checker.setListener(new UpdateChecker.UpdateListener() {
                @Override
                public void onUpdateAvailable(UpdateInfo info) {
                    action.set("available");
                }

                @Override
                public void onNoUpdateAvailable() {
                    action.set("no_update");
                }

                @Override
                public void onError(String error) {
                    action.set("error");
                }
            });

            // Act
            UpdateInfo result = checker.checkForUpdate(true);

            // Assert
            assertNull(result, "No debe retornar actualización");
            assertEquals("no_update", action.get());
        }

        @Test
        @DisplayName("El caching devuelve el mismo resultado sin nueva petición")
        void cachingWorks() throws Exception {
            // Arrange
            UpdateChecker checker = new UpdateChecker("1.0.0", baseUrl);

            // Act
            UpdateInfo first = checker.checkForUpdate(true);
            UpdateInfo cached = checker.checkForUpdate(false);

            // Assert
            assertNotNull(first);
            assertSame(first, cached, "La segunda verificación debe usar el cache");
        }

        @Test
        @DisplayName("Force check ignora caché y hace una nueva petición")
        void forceCheckIgnoresCache() throws Exception {
            // Arrange
            UpdateChecker checker = new UpdateChecker("1.0.0", baseUrl);

            // Act
            checker.checkForUpdate(true);
            UpdateInfo forced = checker.checkForUpdate(true);

            // Assert
            assertNotNull(forced);
            assertEquals("2.0.0", forced.getVersion());
        }

        @Test
        @DisplayName("Error de conexión retorna null y notifica error")
        void connectionErrorReturnsNull() {
            // Arrange
            UpdateChecker checker = new UpdateChecker("1.0.0",
                    "http://localhost:1/no-existe.json");
            AtomicReference<String> error = new AtomicReference<>();

            checker.setListener(new UpdateChecker.UpdateListener() {
                @Override
                public void onUpdateAvailable(UpdateInfo info) {
                }

                @Override
                public void onNoUpdateAvailable() {
                }

                @Override
                public void onError(String err) {
                    error.set(err);
                }
            });

            // Act
            UpdateInfo result = checker.checkForUpdate(true);

            // Assert
            assertNull(result);
            assertNotNull(error.get());
        }
    }

    @Nested
    @DisplayName("Cache")
    class Cache {

        @Test
        @DisplayName("Clear cache resetea el estado")
        void clearCacheResetsState() {
            UpdateChecker checker = new UpdateChecker("1.1.0", baseUrl);
            checker.clearCache();

            assertNull(checker.getLastCheckResult());
        }
    }
}