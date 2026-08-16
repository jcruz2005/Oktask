package com.academic.gestor;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Launcher principal que crea ventana nativa con JavaFX WebView.
 *
 * <p>Esta clase NO extiende Application para compatibilidad con jpackage.
 * Usa Platform.startup() para inicializar JavaFX desde el classpath.</p>
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public class NativeLauncher {

    private static final Logger log = LoggerFactory.getLogger(NativeLauncher.class);

    /** Título de la ventana nativa. */
    private static final String APP_TITLE = "Gestor de Tareas Académicas";

    /** Ancho por defecto de la ventana. */
    private static final int DEFAULT_WIDTH = 1200;

    /** Alto por defecto de la ventana. */
    private static final int DEFAULT_HEIGHT = 800;

    /** Ancho mínimo de la ventana. */
    private static final int MIN_WIDTH = 800;

    /** Alto mínimo de la ventana. */
    private static final int MIN_HEIGHT = 600;

    /** Número máximo de intentos para esperar el servidor. */
    private static final int MAX_SERVER_ATTEMPTS = 60;

    /** Intervalo entre intentos de conexión al servidor en milisegundos. */
    private static final int SERVER_POLL_INTERVAL_MS = 1000;

    /** Intervalo inicial de espera en milisegundos. */
    private static final int INITIAL_WAIT_MS = 500;

    /** Puerto del servidor Spring Boot (configurable via -Dserver.port). */
    private static final String SERVER_PORT = System.getProperty("server.port", "8080");

    /** URL del servidor Spring Boot. */
    private static final String SERVER_URL = "http://localhost:" + SERVER_PORT;

    /** Ruta al recurso CSS para la ventana nativa. */
    private static final String CSS_RESOURCE = "/native-window.css";

    private WebView webView;
    private WebEngine webEngine;

    /**
     * Inicia la ventana nativa JavaFX.
     *
     * <p>Inicia el servidor Spring Boot en un hilo separado,
     * espera a que esté listo, y configura la ventana JavaFX con
     * un WebView que carga la aplicación web.</p>
     */
    private void launchApp() {
        // Iniciar Spring Boot en un hilo separado
        startSpringBootServer();

        // Esperar a que el servidor esté listo
        waitForServer();

        // Crear ventana en el hilo de JavaFX
        Platform.runLater(() -> {
            Stage primaryStage = new Stage();

            // Configurar WebView
            configureWebView();

            // Configurar ventana
            configureStage(primaryStage);

            // Crear y configurar escena
            Scene scene = createScene();
            applyStylesheet(scene);

            primaryStage.setScene(scene);
            primaryStage.show();
        });
    }

    /**
     * Inicia el servidor Spring Boot en un hilo daemon separado.
     */
    private void startSpringBootServer() {
        Thread serverThread = new Thread(() -> {
            try {
                GestorTareasApplication.main(new String[]{});
            } catch (Exception e) {
                log.error("Error al iniciar el servidor Spring Boot", e);
                javafx.application.Platform.exit();
            }
        }, "spring-boot-server");
        serverThread.setDaemon(true);
        serverThread.start();
    }

    /**
     * Configura el WebView con el motor de renderizado web.
     */
    private void configureWebView() {
        webView = new WebView();
        webEngine = webView.getEngine();
        webEngine.load(SERVER_URL);
    }

    /**
     * Configura las propiedades de la ventana principal.
     *
     * @param primaryStage la ventana a configurar
     */
    private void configureStage(Stage primaryStage) {
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);

        // Manejar cierre de la aplicación
        primaryStage.setOnCloseRequest(event -> {
            handleApplicationClose();
        });
    }

    /**
     * Maneja el cierre limpio de la aplicación.
     * Cancela la carga del WebView y termina la JVM.
     */
    private void handleApplicationClose() {
        log.info("Cerrando aplicación...");

        if (webEngine != null && webEngine.getLoadWorker() != null) {
            webEngine.getLoadWorker().cancel();
        }

        // Usar Platform.exit() para cierre graceful de JavaFX
        // El shutdown hook de Spring Boot se encargará del resto
        javafx.application.Platform.setImplicitExit(true);
        javafx.application.Platform.exit();
    }

    /**
     * Crea la escena principal con el WebView en un StackPane.
     *
     * @return la escena configurada
     */
    private Scene createScene() {
        StackPane root = new StackPane(webView);
        return new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * Aplica el archivo CSS a la escena si existe.
     *
     * @param scene la escena a la que se aplicará el CSS
     */
    private void applyStylesheet(Scene scene) {
        try {
            URL cssUrl = getClass().getResource(CSS_RESOURCE);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
                log.debug("CSS aplicado: {}", CSS_RESOURCE);
            } else {
                log.debug("No se encontró CSS: {}", CSS_RESOURCE);
            }
        } catch (Exception e) {
            log.warn("Error al aplicar CSS: {}", e.getMessage());
        }
    }

    /**
     * Espera a que el servidor Spring Boot esté disponible.
     *
     * <p>Realiza intentos de conexión al servidor con un intervalo creciente.
     * Lanza una RuntimeException si el servidor no responde después del
     * número máximo de intentos.</p>
     *
     * @throws RuntimeException si el servidor no inicia a tiempo o hay
     *                          interrupción en el hilo
     */
    private void waitForServer() {
        log.info("Esperando servidor en {}...", SERVER_URL);
        int attempt = 0;

        while (attempt < MAX_SERVER_ATTEMPTS) {
            if (isServerReady()) {
                log.info("Servidor listo después de {} intentos", attempt + 1);
                return;
            }
            attempt++;
            sleepBeforeNextAttempt();
        }

        throw new RuntimeException(
                "El servidor no inició en " + MAX_SERVER_ATTEMPTS + " segundos"
        );
    }

    /**
     * Verifica si el servidor está disponible para recibir conexiones.
     *
     * @return true si el servidor responde, false en caso contrario
     */
    private boolean isServerReady() {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(SERVER_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(1000);
            connection.setReadTimeout(1000);
            connection.setRequestMethod("HEAD");

            int responseCode = connection.getResponseCode();

            // Esperar un poco más para asegurar que todo esté inicializado
            if (responseCode == 200) {
                Thread.sleep(INITIAL_WAIT_MS);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Pausa el hilo antes del siguiente intento de conexión.
     */
    private void sleepBeforeNextAttempt() {
        try {
            Thread.sleep(SERVER_POLL_INTERVAL_MS);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupción esperando servidor", ie);
        }
    }

    /**
     * Punto de entrada estático para lanzar la aplicación.
     * Usa Platform.startup() en lugar de Application.launch()
     * para compatibilidad con jpackage (classpath sin module path).
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        Platform.startup(() -> {
            new NativeLauncher().launchApp();
        });
    }
}
