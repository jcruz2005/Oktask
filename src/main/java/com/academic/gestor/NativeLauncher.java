package com.academic.gestor;

import javafx.application.Application;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Launcher principal que crea ventana nativa con JavaFX WebView.
 *
 * <p>Extiende {@link Application} para compatibilidad con jpackage.
 * Inicia Spring Boot en un hilo daemon, espera a que el servidor
 * esté listo, y luego muestra la ventana con WebView.</p>
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public class NativeLauncher extends Application {

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
     * Punto de entrada principal.
     *
     * <p>Inicia Spring Boot en un hilo daemon antes de lanzar JavaFX.</p>
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        log.info("Iniciando Gestor de Tareas Académicas...");

        // Crear directorio de datos si no existe
        ensureDataDirectoryExists();

        // Iniciar Spring Boot en hilo daemon ANTES de launch()
        Thread serverThread = new Thread(() -> {
            try {
                GestorTareasApplication.main(new String[]{});
            } catch (Exception e) {
                log.error("Error al iniciar el servidor Spring Boot", e);
                System.exit(1);
            }
        }, "spring-boot-server");
        serverThread.setDaemon(true);
        serverThread.start();

        // Esperar a que el servidor esté listo
        waitForServer();

        // Lanzar JavaFX (bloquea hasta que se cierre la ventana)
        launch(args);
    }

    /**
     * Callback de JavaFX llamado después de launch().
     *
     * <p>Crea y muestra la ventana principal con WebView.</p>
     *
     * @param primaryStage la ventana principal proporcionada por JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        configureWebView();
        configureStage(primaryStage);

        Scene scene = createScene();
        applyStylesheet(scene);

        primaryStage.setScene(scene);
        primaryStage.show();
        log.info("Ventana nativa mostrada");
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

        Platform.setImplicitExit(true);
        Platform.exit();
        System.exit(0);
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
    private static void waitForServer() {
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
    private static boolean isServerReady() {
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
    private static void sleepBeforeNextAttempt() {
        try {
            Thread.sleep(SERVER_POLL_INTERVAL_MS);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupción esperando servidor", ie);
        }
    }

    /**
     * Asegura que el directorio de datos exista antes de iniciar Spring Boot.
     *
     * <p>La base de datos SQLite requiere que el directorio padre exista
     * antes de crear el archivo .db. Este método crea la estructura
     * {@code ~/.gestor-tareas/data/} si no existe.</p>
     */
    private static void ensureDataDirectoryExists() {
        try {
            String userHome = System.getProperty("user.home");
            Path dataDir = Paths.get(userHome, ".gestor-tareas", "data");

            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
                log.info("Directorio de datos creado: {}", dataDir);
            } else {
                log.debug("Directorio de datos ya existe: {}", dataDir);
            }
        } catch (Exception e) {
            log.error("Error al crear directorio de datos", e);
            throw new RuntimeException("No se pudo crear el directorio de datos", e);
        }
    }
}
