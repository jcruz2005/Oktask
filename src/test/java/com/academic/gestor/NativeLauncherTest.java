package com.academic.gestor;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para NativeLauncher")
class NativeLauncherTest {

    /**
     * Indica si {@link NativeLauncher} declara un metodo con el nombre dado.
     *
     * @param nombre nombre del metodo
     * @return true si el metodo existe
     */
    private static boolean existeMetodo(final String nombre) {
        for (Method m : NativeLauncher.class.getDeclaredMethods()) {
            if (m.getName().equals(nombre)) {
                return true;
            }
        }
        return false;
    }

    @Nested
    @DisplayName("Constantes de configuración")
    class ConstantesTests {

        @Test
        @DisplayName("deberia tener titulo de aplicacion correcto")
        void deberiaTenertituloDeAplicacionCorrecto() throws Exception {
            // Arrange
            Field field = NativeLauncher.class.getDeclaredField("APP_TITLE");
            field.setAccessible(true);
            String valor = (String) field.get(null);

            // Act & Assert
            assertEquals("OKtask", valor);
        }

        @Test
        @DisplayName("deberia tener dimension por defecto 1200x800")
        void deberiaTenerDimensionPorDefecto() throws Exception {
            // Arrange
            Field widthField = NativeLauncher.class.getDeclaredField("DEFAULT_WIDTH");
            Field heightField = NativeLauncher.class.getDeclaredField("DEFAULT_HEIGHT");
            widthField.setAccessible(true);
            heightField.setAccessible(true);

            // Act & Assert
            assertEquals(1200, widthField.getInt(null));
            assertEquals(800, heightField.getInt(null));
        }

        @Test
        @DisplayName("deberia tener dimension minima 800x600")
        void deberiaTenerDimensionMinima() throws Exception {
            // Arrange
            Field minWidthField = NativeLauncher.class.getDeclaredField("MIN_WIDTH");
            Field minHeightField = NativeLauncher.class.getDeclaredField("MIN_HEIGHT");
            minWidthField.setAccessible(true);
            minHeightField.setAccessible(true);

            // Act & Assert
            assertEquals(800, minWidthField.getInt(null));
            assertEquals(600, minHeightField.getInt(null));
        }

        @Test
        @DisplayName("deberia tener maximo de intentos del servidor en 60")
        void deberiaTenerMaximoIntentosDelServidor() throws Exception {
            // Arrange
            Field field = NativeLauncher.class.getDeclaredField("MAX_SERVER_ATTEMPTS");
            field.setAccessible(true);

            // Act & Assert
            assertEquals(60, field.getInt(null));
        }

        @Test
        @DisplayName("deberia tener intervalo de polling de 500ms")
        void deberiaTenerIntervaloDePolling() throws Exception {
            // Arrange
            Field field = NativeLauncher.class.getDeclaredField("SERVER_POLL_INTERVAL_MS");
            field.setAccessible(true);

            // Act & Assert
            assertEquals(500, field.getInt(null));
        }

        @Test
        @DisplayName("deberia tener URL del servidor en localhost:8080")
        void deberiaTenerUrlDelServidor() throws Exception {
            // Arrange
            Field field = NativeLauncher.class.getDeclaredField("SERVER_URL");
            field.setAccessible(true);
            String valor = (String) field.get(null);

            // Act & Assert
            assertEquals("http://localhost:8080", valor);
        }

        @Test
        @DisplayName("deberia tener ruta al recurso CSS correcta")
        void deberiaTenerRutaAlRecursoCss() throws Exception {
            // Arrange
            Field field = NativeLauncher.class.getDeclaredField("CSS_RESOURCE");
            field.setAccessible(true);
            String valor = (String) field.get(null);

            // Act & Assert
            assertEquals("/native-window.css", valor);
        }
    }

    @Nested
    @DisplayName("Campos privados")
    class CamposPrivadosTests {

        @Test
        @DisplayName("deberia tener campo webView inicializado en null")
        void deberiaTenerCampoWebViewInicializadoEnNull() throws Exception {
            // Arrange
            NativeLauncher launcher = new NativeLauncher();

            // Act
            java.lang.reflect.Field field = NativeLauncher.class.getDeclaredField("webView");
            field.setAccessible(true);
            Object webView = field.get(launcher);

            // Assert
            assertNull(webView);
        }

        @Test
        @DisplayName("deberia tener campo webEngine inicializado en null")
        void deberiaTenerCampoWebEngineInicializadoEnNull() throws Exception {
            // Arrange
            NativeLauncher launcher = new NativeLauncher();

            // Act
            java.lang.reflect.Field field = NativeLauncher.class.getDeclaredField("webEngine");
            field.setAccessible(true);
            Object webEngine = field.get(launcher);

            // Assert
            assertNull(webEngine);
        }
    }

    @Nested
    @DisplayName("Campo serverReady")
    class ServerReadyTests {

        @Test
        @DisplayName("deberia iniciar en false cuando el servidor no esta disponible")
        void deberiaIniciarEnFalseCuandoServidorNoDisponible() throws Exception {
            // Arrange
            java.lang.reflect.Field field = NativeLauncher.class.getDeclaredField("serverReady");
            field.setAccessible(true);

            // Act
            AtomicBoolean serverReady = (AtomicBoolean) field.get(null);

            // Assert
            assertNotNull(serverReady);
            assertFalse(serverReady.get());
        }
    }

    @Nested
    @DisplayName("Metodo createSplashScreen")
    class CreateSplashScreenTests {

        @Test
        @DisplayName("deberia tener metodo createSplashScreen definido")
        void deberiaTenerMetodoCreateSplashScreenDefinido() {
            // Arrange & Act
            boolean found = existeMetodo("createSplashScreen");

            // Assert
            assertTrue(found, "El metodo createSplashScreen debe existir");
        }
    }

    @Nested
    @DisplayName("Metodo loadWebView")
    class LoadWebViewTests {

        @Test
        @DisplayName("deberia tener metodo loadWebView con parametro Stage")
        void deberiaTenerMetodoLoadWebViewConStage() throws Exception {
            // Arrange & Act
            Method method = NativeLauncher.class.getDeclaredMethod("loadWebView", Stage.class);

            // Assert
            assertNotNull(method);
            assertTrue(java.lang.reflect.Modifier.isPrivate(method.getModifiers()));
        }
    }

    @Nested
    @DisplayName("Metodo waitForServer")
    class WaitForServerTests {

        @Test
        @DisplayName("deberia tener metodo waitForServer definido")
        void deberiaTenerMetodoWaitForServerDefinido() {
            // Arrange & Act
            boolean found = existeMetodo("waitForServer");

            // Assert
            assertTrue(found, "El metodo waitForServer debe existir");
        }
    }

    @Nested
    @DisplayName("Metodo checkServer")
    class CheckServerTests {

        @Test
        @DisplayName("deberia tener metodo checkServer estatico")
        void deberiaTenerMetodoCheckServerEstatico() throws Exception {
            // Arrange & Act
            Method method = NativeLauncher.class.getDeclaredMethod("checkServer");

            // Assert
            assertNotNull(method);
            assertTrue(java.lang.reflect.Modifier.isStatic(method.getModifiers()));
            assertEquals(boolean.class, method.getReturnType());
        }
    }

    @Nested
    @DisplayName("Metodo ensureDataDirectoryExists")
    class EnsureDataDirectoryExistsTests {

        @Test
        @DisplayName("deberia tener metodo ensureDataDirectoryExists estatico")
        void deberiaTenerMetodoEnsureDataDirectoryExistsEstatico() throws Exception {
            // Arrange & Act
            Method method = NativeLauncher.class.getDeclaredMethod("ensureDataDirectoryExists");

            // Assert
            assertNotNull(method);
            assertTrue(java.lang.reflect.Modifier.isStatic(method.getModifiers()));
        }
    }

    @Nested
    @DisplayName("Metodo loadAppIcons")
    class LoadAppIconsTests {

        @Test
        @DisplayName("deberia tener metodo loadAppIcons definido")
        void deberiaTenerMetodoLoadAppIconsDefinido() {
            // Arrange & Act
            boolean found = existeMetodo("loadAppIcons");

            // Assert
            assertTrue(found, "El metodo loadAppIcons debe existir");
        }
    }

    @Nested
    @DisplayName("Metodo checkForUpdates")
    class CheckForUpdatesTests {

        @Test
        @DisplayName("deberia tener metodo checkForUpdates definido")
        void deberiaTenerMetodoCheckForUpdatesDefinido() {
            // Arrange & Act
            boolean found = existeMetodo("checkForUpdates");

            // Assert
            assertTrue(found, "El metodo checkForUpdates debe existir");
        }
    }

    @Nested
    @DisplayName("Metodo configureStage")
    class ConfigureStageTests {

        @Test
        @DisplayName("deberia tener metodo configureStage definido")
        void deberiaTenerMetodoConfigureStageDefinido() {
            // Arrange & Act
            Method[] methods = NativeLauncher.class.getDeclaredMethods();

            // Assert
            boolean found = false;
            for (Method m : methods) {
                if (m.getName().equals("configureStage")) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "El metodo configureStage debe existir");
        }
    }

    @Nested
    @DisplayName("Metodo main")
    class MainTests {

        @Test
        @DisplayName("deberia tener metodo main estatico")
        void deberiaTenerMetodoMainEstatico() throws Exception {
            // Arrange & Act
            Method method = NativeLauncher.class.getMethod("main", String[].class);

            // Assert
            assertNotNull(method);
            assertTrue(java.lang.reflect.Modifier.isStatic(method.getModifiers()));
        }
    }
}
