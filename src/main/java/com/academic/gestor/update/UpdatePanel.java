package com.academic.gestor.update;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

/**
 * Panel modal que muestra detalles de actualización con descarga directa.
 *
 * @author OKtask
 * @since 1.2.0
 */
public class UpdatePanel {

    private Stage panelStage;
    private UpdateDownloader downloader;
    private UpdateInfo updateInfo;

    // UI Components
    private ProgressBar progressBar;
    private Label progressLabel;
    private Label statusLabel;
    private Button downloadBtn;
    private Button cancelBtn;
    private Button installBtn;
    private Button closeBtn;

    /** Archivo descargado. */
    private String downloadedFilePath;

    /**
     * Constructor.
     */
    public UpdatePanel() {
        this.downloader = new UpdateDownloader();
    }

    /**
     * Muestra el panel de actualización.
     *
     * @param info información de la actualización
     */
    public void show(UpdateInfo info) {
        if (info == null || !info.isValid()) return;
        this.updateInfo = info;

        panelStage = new Stage();
        panelStage.initStyle(StageStyle.DECORATED);
        panelStage.initModality(Modality.APPLICATION_MODAL);
        panelStage.setTitle("Actualización Disponible");
        panelStage.setMinWidth(480);
        panelStage.setMinHeight(450);

        VBox content = createContent(info);
        Scene scene = new Scene(content, 480, 450);
        panelStage.setScene(scene);
        panelStage.show();
    }

    public void close() {
        if (downloader != null) downloader.cancel();
        if (panelStage != null && panelStage.isShowing()) {
            panelStage.close();
        }
    }

    private VBox createContent(UpdateInfo info) {
        // Header
        javafx.scene.shape.Rectangle headerBg = new javafx.scene.shape.Rectangle();
        headerBg.setWidth(480);
        headerBg.setHeight(120);
        headerBg.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#7C3AED")),
                new Stop(1, Color.web("#6D28D9"))));

        Label headerIcon = new Label("\uD83D\uDD04");
        headerIcon.setFont(Font.font(36));

        Label headerTitle = new Label("Nueva Versión Disponible");
        headerTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 22));
        headerTitle.setTextFill(Color.WHITE);

        Label headerVersion = new Label("v" + info.getVersion());
        headerVersion.setFont(Font.font("SansSerif", FontWeight.NORMAL, 16));
        headerVersion.setTextFill(Color.web("#DDD6FE"));

        Label headerDate = new Label(info.getReleaseDate() != null
                ? "Publicado: " + info.getReleaseDate() : "");
        headerDate.setFont(Font.font("SansSerif", FontWeight.NORMAL, 12));
        headerDate.setTextFill(Color.web("#C4B5FD"));

        VBox headerText = new VBox(4, headerTitle, headerVersion, headerDate);
        headerText.setAlignment(Pos.CENTER_LEFT);

        HBox headerContent = new HBox(16, headerIcon, headerText);
        headerContent.setAlignment(Pos.CENTER_LEFT);
        headerContent.setPadding(new Insets(20, 24, 20, 24));

        StackPane header = new StackPane(headerBg, headerContent);
        StackPane.setAlignment(headerContent, Pos.CENTER_LEFT);

        // Changelog
        Label changelogTitle = new Label("Cambios en esta versión:");
        changelogTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        changelogTitle.setPadding(new Insets(16, 0, 8, 0));

        VBox changelogBox = new VBox(6);
        if (info.getChangelog() != null && !info.getChangelog().isEmpty()) {
            for (String change : info.getChangelog()) {
                Label item = new Label("  •  " + change);
                item.setFont(Font.font("SansSerif", 13));
                item.setWrapText(true);
                item.setMaxWidth(Double.MAX_VALUE);
                changelogBox.getChildren().add(item);
            }
        } else {
            Label noChanges = new Label("  No hay información de cambios disponible.");
            noChanges.setFont(Font.font("SansSerif", 13));
            noChanges.setTextFill(Color.web("#6B7280"));
            changelogBox.getChildren().add(noChanges);
        }

        ScrollPane scrollPane = new ScrollPane(changelogBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPrefHeight(140);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        // Progress section (initially hidden)
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(Double.MAX_VALUE);
        progressBar.setPrefHeight(20);
        progressBar.setVisible(false);
        progressBar.setManaged(false);

        progressLabel = new Label("Descargando...");
        progressLabel.setFont(Font.font("SansSerif", 12));
        progressLabel.setTextFill(Color.web("#6B7280"));
        progressLabel.setVisible(false);
        progressLabel.setManaged(false);

        statusLabel = new Label("");
        statusLabel.setFont(Font.font("SansSerif", FontWeight.BOLD, 13));
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);

        VBox progressBox = new VBox(6, progressBar, progressLabel, statusLabel);
        progressBox.setPadding(new Insets(0, 24, 0, 24));

        // Buttons
        downloadBtn = new Button("Descargar e Instalar");
        downloadBtn.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        downloadBtn.setStyle("-fx-background-color: #7C3AED; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-padding: 10 24;");
        downloadBtn.setOnMouseEntered(e ->
                downloadBtn.setStyle("-fx-background-color: #6D28D9; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-padding: 10 24; -fx-cursor: hand;"));
        downloadBtn.setOnMouseExited(e ->
                downloadBtn.setStyle("-fx-background-color: #7C3AED; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-padding: 10 24;"));
        downloadBtn.setOnAction(e -> startDownload());

        cancelBtn = new Button("Cancelar");
        cancelBtn.setFont(Font.font("SansSerif", 13));
        cancelBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-padding: 10 16;");
        cancelBtn.setVisible(false);
        cancelBtn.setManaged(false);
        cancelBtn.setOnAction(e -> cancelDownload());

        installBtn = new Button("Instalar Ahora");
        installBtn.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        installBtn.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-padding: 10 24;");
        installBtn.setVisible(false);
        installBtn.setManaged(false);
        installBtn.setOnAction(e -> installUpdate());

        closeBtn = new Button("Cerrar");
        closeBtn.setFont(Font.font("SansSerif", 13));
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #6B7280; " +
                "-fx-padding: 10 16;");
        closeBtn.setOnAction(e -> close());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttons = new HBox(12, closeBtn, spacer, cancelBtn, downloadBtn, installBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(16, 24, 16, 24));

        // Layout
        VBox layout = new VBox(header, changelogTitle, scrollPane, progressBox, buttons);
        layout.setStyle("-fx-background-color: #FAFAFA;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return layout;
    }

    private void startDownload() {
        if (updateInfo == null || updateInfo.getDownloadUrl() == null) return;

        String fileName = "OKtask-" + updateInfo.getVersion() + ".deb";

        downloadBtn.setVisible(false);
        downloadBtn.setManaged(false);
        cancelBtn.setVisible(true);
        cancelBtn.setManaged(true);
        progressBar.setVisible(true);
        progressBar.setManaged(true);
        progressLabel.setVisible(true);
        progressLabel.setManaged(true);

        downloader.setListener(new UpdateDownloader.ProgressListener() {
            @Override
            public void onDownloadStarted(long totalBytes) {
                Platform.runLater(() -> {
                    progressBar.setProgress(0);
                    progressLabel.setText("Iniciando descarga...");
                });
            }

            @Override
            public void onProgressUpdate(long bytesDownloaded, long totalBytes, int percentage) {
                Platform.runLater(() -> {
                    progressBar.setProgress(percentage / 100.0);
                    String downloaded = formatBytes(bytesDownloaded);
                    String total = totalBytes > 0 ? formatBytes(totalBytes) : "desconocido";
                    progressLabel.setText(downloaded + " / " + total + " (" + percentage + "%)");
                });
            }

            @Override
            public void onDownloadComplete(String filePath) {
                Platform.runLater(() -> {
                    downloadedFilePath = filePath;
                    progressBar.setProgress(1.0);
                    progressLabel.setText("Descarga completada");
                    statusLabel.setText("✓ Archivo listo para instalar");
                    statusLabel.setTextFill(Color.web("#10B981"));
                    statusLabel.setVisible(true);
                    statusLabel.setManaged(true);
                    cancelBtn.setVisible(false);
                    cancelBtn.setManaged(false);
                    installBtn.setVisible(true);
                    installBtn.setManaged(true);
                });
            }

            @Override
            public void onDownloadFailed(String error) {
                Platform.runLater(() -> {
                    statusLabel.setText("✗ " + error);
                    statusLabel.setTextFill(Color.web("#EF4444"));
                    statusLabel.setVisible(true);
                    statusLabel.setManaged(true);
                    cancelBtn.setVisible(false);
                    cancelBtn.setManaged(false);
                    downloadBtn.setVisible(true);
                    downloadBtn.setManaged(true);
                });
            }

            @Override
            public void onDownloadCancelled() {
                Platform.runLater(() -> {
                    progressBar.setProgress(0);
                    progressLabel.setText("Descarga cancelada");
                    cancelBtn.setVisible(false);
                    cancelBtn.setManaged(false);
                    downloadBtn.setVisible(true);
                    downloadBtn.setManaged(true);
                });
            }
        });

        new Thread(() -> downloader.download(updateInfo.getDownloadUrl(), fileName)).start();
    }

    private void cancelDownload() {
        downloader.cancel();
    }

    private void installUpdate() {
        if (downloadedFilePath == null) return;

        statusLabel.setText("Instalando... (se requerirá contraseña de administrador)");
        statusLabel.setTextFill(Color.web("#7C3AED"));
        installBtn.setVisible(false);
        installBtn.setManaged(false);

        new Thread(() -> {
            try {
                // Detectar gestor de paquetes
                String distro = detectDistro();
                ProcessBuilder pb;

                if ("debian".equals(distro) || "ubuntu".equals(distro)) {
                    // Debian/Ubuntu: pkexec dpkg -i
                    pb = new ProcessBuilder("pkexec", "dpkg", "-i", downloadedFilePath);
                } else if ("fedora".equals(distro) || "rhel".equals(distro)) {
                    // Fedora/RHEL: pkexec rpm -i
                    pb = new ProcessBuilder("pkexec", "rpm", "-i", downloadedFilePath);
                } else {
                    // Fallback: intentar dpkg
                    pb = new ProcessBuilder("pkexec", "dpkg", "-i", downloadedFilePath);
                }

                pb.redirectErrorStream(true);
                Process process = pb.start();
                int exitCode = process.waitFor();

                Platform.runLater(() -> {
                    if (exitCode == 0) {
                        statusLabel.setText("✓ Instalación completada. Reiniciando...");
                        statusLabel.setTextFill(Color.web("#10B981"));
                        // Reiniciar app después de 2 segundos
                        new Thread(() -> {
                            try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
                            Platform.runLater(() -> {
                                Runtime.getRuntime().exit(0);
                            });
                        }).start();
                    } else {
                        statusLabel.setText("✗ Error en la instalación (código: " + exitCode + ")");
                        statusLabel.setTextFill(Color.web("#EF4444"));
                        closeBtn.setVisible(true);
                    }
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("✗ Error: " + e.getMessage());
                    statusLabel.setTextFill(Color.web("#EF4444"));
                    closeBtn.setVisible(true);
                });
            }
        }).start();
    }

    private String detectDistro() {
        try {
            File osRelease = new File("/etc/os-release");
            if (osRelease.exists()) {
                java.util.Scanner scanner = new java.util.Scanner(osRelease);
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("ID=")) {
                        String id = line.substring(3).toLowerCase().replace("\"", "");
                        scanner.close();
                        return id;
                    }
                }
                scanner.close();
            }
        } catch (Exception ignored) {}
        return "debian"; // fallback
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024));
    }
}
