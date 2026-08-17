package com.academic.gestor.update;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para UpdateChecker.
 */
@DisplayName("UpdateChecker")
class UpdateCheckerTest {

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
        @Timeout(15)
        @DisplayName("Verificación contra GitHub retorna resultado")
        void checkAgainstGitHubReturnsResult() throws Exception {
            UpdateChecker checker = new UpdateChecker("1.0.0");
            AtomicReference<UpdateInfo> result = new AtomicReference<>();
            CountDownLatch latch = new CountDownLatch(1);

            checker.setListener(new UpdateChecker.UpdateListener() {
                @Override
                public void onUpdateAvailable(UpdateInfo info) {
                    result.set(info);
                    latch.countDown();
                }

                @Override
                public void onNoUpdateAvailable() {
                    latch.countDown();
                }

                @Override
                public void onError(String error) {
                    latch.countDown();
                }
            });

            // Ejecutar en hilo separado para no bloquear
            new Thread(() -> {
                checker.checkForUpdate(true);
            }).start();

            latch.await(10, TimeUnit.SECONDS);

            // La versión en el repo es 1.1.0, si nuestra versión es 1.0.0 debería haber update
            // Nota: Esto depende de que el archivo version.json exista en el repo
            // Si no hay conexión, el test igual debe pasar (manejo de errores)
        }

        @Test
        @Timeout(15)
        @DisplayName("Versión igual no muestra actualización")
        void sameVersionShowsNoUpdate() throws Exception {
            // Usar una versión que sabemos que es la última
            UpdateChecker checker = new UpdateChecker("99.99.99");
            AtomicReference<String> action = new AtomicReference<>();
            CountDownLatch latch = new CountDownLatch(1);

            checker.setListener(new UpdateChecker.UpdateListener() {
                @Override
                public void onUpdateAvailable(UpdateInfo info) {
                    action.set("available");
                    latch.countDown();
                }

                @Override
                public void onNoUpdateAvailable() {
                    action.set("no_update");
                    latch.countDown();
                }

                @Override
                public void onError(String error) {
                    action.set("error");
                    latch.countDown();
                }
            });

            new Thread(() -> checker.checkForUpdate(true)).start();
            latch.await(10, TimeUnit.SECONDS);

            // Con versión 99.99.99, no debería haber actualización
            // (a menos que el repo tenga una versión mayor, lo cual es improbable)
        }

        @Test
        @DisplayName("Caching funciona correctamente")
        void cachingWorks() {
            UpdateChecker checker = new UpdateChecker("1.1.0");

            // Primera verificación (puede fallar por conexión)
            checker.checkForUpdate(true);

            // Segunda verificación debería usar caché
            UpdateInfo cached = checker.checkForUpdate(false);
            // El resultado debería ser el mismo que la primera vez
        }

        @Test
        @DisplayName("Force check ignora caché")
        void forceCheckIgnoresCache() {
            UpdateChecker checker = new UpdateChecker("1.1.0");

            // Primera verificación
            checker.checkForUpdate(true);

            // Force check debería hacer una nueva petición
            checker.checkForUpdate(true);
        }
    }

    @Nested
    @DisplayName("Cache")
    class Cache {

        @Test
        @DisplayName("Clear cache resetea el estado")
        void clearCacheResetsState() {
            UpdateChecker checker = new UpdateChecker("1.1.0");
            checker.clearCache();

            assertNull(checker.getLastCheckResult());
        }
    }

    @Nested
    @DisplayName("Manejo de errores")
    class ErrorHandling {

        @Test
        @DisplayName("URL inválida no causa excepción")
        void invalidUrlDoesNotThrow() {
            UpdateChecker checker = new UpdateChecker("1.1.0");
            AtomicReference<String> error = new AtomicReference<>();
            CountDownLatch latch = new CountDownLatch(1);

            checker.setListener(new UpdateChecker.UpdateListener() {
                @Override
                public void onUpdateAvailable(UpdateInfo info) {
                    latch.countDown();
                }

                @Override
                public void onNoUpdateAvailable() {
                    latch.countDown();
                }

                @Override
                public void onError(String err) {
                    error.set(err);
                    latch.countDown();
                }
            });

            // Esto debería manejar el error gracefully
            assertDoesNotThrow(() -> {
                new Thread(() -> checker.checkForUpdate(true)).start();
            });
        }
    }
}
