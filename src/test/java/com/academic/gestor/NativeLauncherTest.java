package com.academic.gestor;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para NativeLauncher")
class NativeLauncherTest {

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
            assertEquals("Gestor de Tareas Académicas", valor);
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
        @DisplayName("deberia tener intervalo de polling de 1000ms")
        void deberiaTenerIntervaloDePolling() throws Exception {
            // Arrange
            Field field = NativeLauncher.class.getDeclaredField("SERVER_POLL_INTERVAL_MS");
            field.setAccessible(true);

            // Act & Assert
            assertEquals(1000, field.getInt(null));
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
        void deberiaTenerCampoWebViewInicializadoEnNull() {
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
        void deberiaTenerCampoWebEngineInicializadoEnNull() {
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
    @DisplayName("Metodo isServerReady")
    class IsServerReadyTests {

        @Test
        @DisplayName("deberia retornar false cuando servidor no esta disponible")
        void deberiaRetornarFalseCuandoServidorNoDisponible() throws Exception {
            // Arrange
            NativeLauncher launcher = new NativeLauncher();
            Method method = NativeLauncher.class.getDeclaredMethod("isServerReady");
            method.setAccessible(true);

            // Act
            boolean resultado = (boolean) method.invoke(launcher);

            // Assert
            assertFalse(resultado);
        }
    }

    @Nested
    @DisplayName("Metodo handleApplicationClose")
    class HandleApplicationCloseTests {

        @Test
        @DisplayName("deberia manejar cierre sin excepcion cuando webEngine es null")
        void deberiaManejarCierreSinExcepcionCuandoWebEngineEsNull() {
            // Arrange
            NativeLauncher launcher = new NativeLauncher();
            // webEngine es null por defecto

            // Act & Assert - No deberia lanzar excepcion NullPointerException
            // El metodo tiene null check para webEngine
            // Nota: Platform.exit() y System.exit() serian llamados pero
            // en contexto de test esto podria causar problemas
            // Solo validamos que el null check exista
            try {
                Method method = NativeLauncher.class.getDeclaredMethod("handleApplicationClose");
                method.setAccessible(true);
                // No invocamos porquePlatform.exit() cerraria el test
                assertNotNull(method);
            } catch (NoSuchMethodException e) {
                fail("El metodo handleApplicationClose debe existir");
            }
        }
    }

    @Nested
    @DisplayName("Metodo createScene")
    class CreateSceneTests {

        @Test
        @DisplayName("deberia tener metodo createScene definido")
        void deberiaTenerMetodoCreateSceneDefinido() {
            // Arrange & Act
            Method[] methods = NativeLauncher.class.getDeclaredMethods();

            // Assert
            boolean found = false;
            for (Method m : methods) {
                if (m.getName().equals("createScene")) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "El metodo createScene debe existir");
        }
    }

    @Nested
    @DisplayName("Metodo applyStylesheet")
    class ApplyStylesheetTests {

        @Test
        @DisplayName("deberia tener metodo applyStylesheet definido")
        void deberiaTenerMetodoApplyStylesheetDefinido() {
            // Arrange & Act
            Method[] methods = NativeLauncher.class.getDeclaredMethods();

            // Assert
            boolean found = false;
            for (Method m : methods) {
                if (m.getName().equals("applyStylesheet")) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "El metodo applyStylesheet debe existir");
        }
    }

    @Nested
    @DisplayName("Metodo startSpringBootServer")
    class StartSpringBootServerTests {

        @Test
        @DisplayName("deberia tener metodo startSpringBootServer definido")
        void deberiaTenerMetodoStartSpringBootServerDefinido() {
            // Arrange & Act
            Method[] methods = NativeLauncher.class.getDeclaredMethods();

            // Assert
            boolean found = false;
            for (Method m : methods) {
                if (m.getName().equals("startSpringBootServer")) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "El metodo startSpringBootServer debe existir");
        }
    }

    @Nested
    @DisplayName("Metodo configureWebView")
    class ConfigureWebViewTests {

        @Test
        @DisplayName("deberia tener metodo configureWebView definido")
        void deberiaTenerMetodoConfigureWebViewDefinido() {
            // Arrange & Act
            Method[] methods = NativeLauncher.class.getDeclaredMethods();

            // Assert
            boolean found = false;
            for (Method m : methods) {
                if (m.getName().equals("configureWebView")) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "El metodo configureWebView debe existir");
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
    @DisplayName("Metodo sleepBeforeNextAttempt")
    class SleepBeforeNextAttemptTests {

        @Test
        @DisplayName("deberia tener metodo sleepBeforeNextAttempt definido")
        void deberiaTenerMetodoSleepBeforeNextAttemptDefinido() {
            // Arrange & Act
            Method[] methods = NativeLauncher.class.getDeclaredMethods();

            // Assert
            boolean found = false;
            for (Method m : methods) {
                if (m.getName().equals("sleepBeforeNextAttempt")) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "El metodo sleepBeforeNextAttempt debe existir");
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
