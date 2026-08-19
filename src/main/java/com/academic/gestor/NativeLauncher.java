package com.academic.gestor;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.image.Image;

import com.academic.gestor.update.UpdateChecker;
import com.academic.gestor.update.UpdateInfo;
import com.academic.gestor.update.UpdateToast;
import com.academic.gestor.update.UpdatePanel;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Launcher optimizado para inicio rápido.
 *
 * <p>Muestra la ventana inmediatamente con splash screen mientras
 * Spring Boot se inicializa en background. Una vez listo, carga
 * la URL en el WebView de forma transparente.</p>
 *
 * @author OKtask
 * @since 1.1.0
 */
public class NativeLauncher extends Application {

    private static final Logger log = LoggerFactory.getLogger(NativeLauncher.class);

    private static final String APP_TITLE = "OKtask";
    private static final int DEFAULT_WIDTH = 1200;
    private static final int DEFAULT_HEIGHT = 800;
    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;
    private static final int MAX_SERVER_ATTEMPTS = 60;
    private static final int SERVER_POLL_INTERVAL_MS = 500;
    private static final String SERVER_PORT = System.getProperty("server.port", "8080");
    private static final String SERVER_URL = "http://localhost:" + SERVER_PORT;
    private static final String CSS_RESOURCE = "/native-window.css";

    private static final String APP_VERSION = "1.2.0";

    private WebView webView;
    private WebEngine webEngine;
    private static final AtomicBoolean serverReady = new AtomicBoolean(false);

    /** Servicio de verificación de actualizaciones. */
    private UpdateChecker updateChecker;

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        System.setProperty("spring.profiles.active", "native");
        System.out.println("[OKtask] Iniciando...");

        ensureDataDirectoryExists();

        // Spring Boot en hilo daemon
        Thread serverThread = new Thread(() -> {
            long bootStart = System.currentTimeMillis();
            try {
                GestorTareasApplication.main(new String[]{});
                System.out.println("[OKtask] Servidor listo en " +
                    (System.currentTimeMillis() - bootStart) + "ms");
            } catch (Exception e) {
                System.err.println("[OKtask] Error: " + e.getMessage());
                System.exit(1);
            }
        }, "spring-boot-server");
        serverThread.setDaemon(true);
        serverThread.start();

        // JavaFX INMEDIATAMENTE
        launch(args);

        System.out.println("[OKtask] Inicio completo en " +
            (System.currentTimeMillis() - startTime) + "ms");
    }

    @Override
    public void start(Stage primaryStage) {
        long startFx = System.currentTimeMillis();

        // Splash screen inmediato
        StackPane splash = createSplashScreen();
        configureStage(primaryStage);

        Scene scene = new Scene(splash, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();

        System.out.println("[OKtask] Ventana visible en " +
            (System.currentTimeMillis() - startFx) + "ms");

        // Background: esperar servidor y cargar WebView
        new Thread(() -> {
            waitForServer();
            Platform.runLater(() -> loadWebView(primaryStage));
        }, "server-watcher").start();

        // Verificar actualizaciones en background (no bloquea)
        checkForUpdates();
    }

    private StackPane createSplashScreen() {
        // Fondo gradiente violeta
        javafx.scene.shape.Rectangle bg = new javafx.scene.shape.Rectangle(
            DEFAULT_WIDTH, DEFAULT_HEIGHT);
        bg.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0, Color.web("#7C3AED")),
            new Stop(1, Color.web("#6D28D9"))));

        // Logo OK
        Label logo = new Label("OK");
        logo.setFont(Font.font("SansSerif", FontWeight.BOLD, 120));
        logo.setTextFill(Color.WHITE);
        logo.setEffect(new DropShadow(20, Color.rgb(0, 0, 0, 0.3)));

        Label subtitle = new Label("task");
        subtitle.setFont(Font.font("SansSerif", FontWeight.NORMAL, 40));
        subtitle.setTextFill(Color.web("#DDD6FE"));

        Label loading = new Label("Cargando...");
        loading.setFont(Font.font("SansSerif", FontWeight.NORMAL, 18));
        loading.setTextFill(Color.web("#C4B5FD"));

        VBox content = new VBox(10, logo, subtitle, loading);
        content.setAlignment(Pos.CENTER);

        StackPane splash = new StackPane(bg, content);
        StackPane.setAlignment(content, Pos.CENTER);

        // Animación de puntos
        new Thread(() -> {
            int dots = 0;
            while (!serverReady.get()) {
                try {
                    Thread.sleep(400);
                    int count = (dots % 3) + 1;
                    Platform.runLater(() ->
                        loading.setText("Cargando" + ".".repeat(count)));
                    dots++;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();

        return splash;
    }

    /**
     * Verifica actualizaciones en background y muestra toast si hay una nueva versión.
     */
    private void checkForUpdates() {
        new Thread(() -> {
            try {
                updateChecker = new UpdateChecker(APP_VERSION);
                updateChecker.setListener(new UpdateChecker.UpdateListener() {
                    @Override
                    public void onUpdateAvailable(UpdateInfo info) {
                        Platform.runLater(() -> {
                            UpdateToast toast = new UpdateToast(() -> {
                                UpdatePanel panel = new UpdatePanel(info);
                                panel.show();
                            });
                            toast.show(info);
                        });
                    }

                    @Override
                    public void onNoUpdateAvailable() {
                        System.out.println("[OKtask] Ya tienes la última versión");
                    }

                    @Override
                    public void onError(String error) {
                        System.out.println("[OKtask] No se pudo verificar actualizaciones: " + error);
                    }
                });

                updateChecker.checkForUpdate();
            } catch (Exception e) {
                System.out.println("[OKtask] Error verificando actualizaciones: " + e.getMessage());
            }
        }, "update-checker").start();
    }

    /**
     * Abre una URL en el navegador predeterminado.
     */
    private void openUrl(String url) {
        if (url == null || url.isEmpty()) return;
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) {
            System.out.println("[OKtask] Error abriendo URL: " + e.getMessage());
        }
    }

    private void loadWebView(Stage stage) {
        long startLoad = System.currentTimeMillis();

        webView = new WebView();
        webEngine = webView.getEngine();

        // Optimizaciones WebView
        webView.setZoom(1.0);

        webEngine.load(SERVER_URL);

        webEngine.getLoadWorker().stateProperty().addListener((obs, old, next) -> {
            if (next == javafx.concurrent.Worker.State.SUCCEEDED) {
                System.out.println("[OKtask] Página cargada en " +
                    (System.currentTimeMillis() - startLoad) + "ms");
            }
        });

        // Reemplazar splash con WebView
        StackPane root = new StackPane(webView);
        stage.getScene().setRoot(root);

        System.out.println("[OKtask] WebView conectado en " +
            (System.currentTimeMillis() - startLoad) + "ms");
    }

    private void configureStage(Stage primaryStage) {
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setMaximized(true);
        primaryStage.getIcons().addAll(loadAppIcons());
        primaryStage.setOnCloseRequest(e -> {
            if (webEngine != null && webEngine.getLoadWorker() != null) {
                webEngine.getLoadWorker().cancel();
            }
            Platform.exit();
            System.exit(0);
        });
    }

    private List<Image> loadAppIcons() {
        List<Image> icons = new ArrayList<>();
        for (String size : new String[]{"16", "32", "48", "64", "128", "256", "512"}) {
            try {
                InputStream is = getClass().getResourceAsStream(
                    "/icons/oktask-" + size + "x" + size + ".png");
                if (is != null) icons.add(new Image(is));
            } catch (Exception ignored) {}
        }
        return icons;
    }

    private void waitForServer() {
        System.out.println("[OKtask] Esperando servidor...");
        int attempt = 0;
        while (attempt < MAX_SERVER_ATTEMPTS) {
            if (checkServer()) {
                serverReady.set(true);
                System.out.println("[OKtask] Servidor detectado en ~" +
                    (attempt * SERVER_POLL_INTERVAL_MS) + "ms");
                return;
            }
            attempt++;
            try { Thread.sleep(SERVER_POLL_INTERVAL_MS); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
        }

        // El servidor no respondió a tiempo: avisar al usuario en lugar de colgarse
        System.err.println("[OKtask] El servidor no respondió dentro de " +
            (MAX_SERVER_ATTEMPTS * SERVER_POLL_INTERVAL_MS / 1000) + "s");
        Platform.runLater(() -> {
            try {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR
                );
                alert.setTitle("Error de inicio");
                alert.setHeaderText("No se pudo iniciar el servidor local");
                alert.setContentText("El servidor de OKtask no respondió a tiempo. " +
                    "Revisá que el puerto " + SERVER_PORT + " esté libre e intentá de nuevo.");
                alert.showAndWait();
                Platform.exit();
                System.exit(1);
            } catch (Exception e) {
                System.exit(1);
            }
        });
    }

    private static boolean checkServer() {
        try {
            HttpURLConnection c = (HttpURLConnection)
                new URL(SERVER_URL).openConnection();
            c.setConnectTimeout(300);
            c.setReadTimeout(300);
            c.setRequestMethod("HEAD");
            int code = c.getResponseCode();
            c.disconnect();
            return code == 200 || code == 302;
        } catch (Exception e) {
            return false;
        }
    }

    private static void ensureDataDirectoryExists() {
        try {
            Path dataDir = Paths.get(System.getProperty("user.home"), ".oktask", "data");
            if (!Files.exists(dataDir)) Files.createDirectories(dataDir);

            Path db = dataDir.resolve("oktask.db");
            if (!Files.exists(db)) {
                Path old = Paths.get(System.getProperty("user.home"),
                    ".gestor-tareas", "data", "gestor-tareas.db");
                if (Files.exists(old)) Files.copy(old, db);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creando directorio de datos", e);
        }
    }
}
