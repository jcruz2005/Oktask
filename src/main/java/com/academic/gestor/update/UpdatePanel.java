package com.academic.gestor.update;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Panel modal de JavaFX para mostrar detalles de actualización y descargar.
 * Adaptado para soporte multiplataforma (Linux, Windows, macOS).
 *
 * @author OKtask
 * @since 1.2.0
 */
public class UpdatePanel {

    private final UpdateInfo updateInfo;
    private Stage dialogStage;
    private ProgressBar progressBar;
    private Label statusLabel;
    private Button downloadButton;
    private Button laterButton;
    private boolean downloadStarted = false;

    public UpdatePanel(UpdateInfo updateInfo) {
        this.updateInfo = updateInfo;
    }

    public void show() {
        Platform.runLater(this::createAndShowDialog);
    }

    private void createAndShowDialog() {
        dialogStage = new Stage();
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setResizable(false);

        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(25));
        mainLayout.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 15;");

        // Header
        Label headerLabel = new Label("Nueva versión disponible: v" + updateInfo.getVersion());
        headerLabel.setStyle("-fx-text-fill: #00d4aa; -fx-font-size: 18px; -fx-font-weight: bold;");
        headerLabel.setWrapText(true);

        // Platform info
        String os = UpdateInfo.detectOS();
        String osName = os.equals("windows") ? "Windows" : os.equals("macos") ? "macOS" : "Linux";
        Label platformLabel = new Label("Paquete para: " + osName);
        platformLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 12px;");

        // Changelog
        Label changelogTitle = new Label("Cambios:");
        changelogTitle.setStyle("-fx-text-fill: #aaa; -fx-font-size: 13px; -fx-font-weight: bold;");

        VBox changelogBox = new VBox(5);
        if (updateInfo.getChangelog() != null) {
            for (String change : updateInfo.getChangelog()) {
                Label changeLabel = new Label("  • " + change);
                changeLabel.setStyle("-fx-text-fill: #ccc; -fx-font-size: 12px;");
                changeLabel.setWrapText(true);
                changelogBox.getChildren().add(changeLabel);
            }
        }

        // Install instructions
        UpdateInfo.DownloadInfo dlInfo = updateInfo.getDownloadForCurrentPlatform();
        if (dlInfo != null && dlInfo.getInstallCommand() != null && !dlInfo.getInstallCommand().isEmpty()) {
            Label installTitle = new Label("Instalación:");
            installTitle.setStyle("-fx-text-fill: #aaa; -fx-font-size: 13px; -fx-font-weight: bold;");

            TextArea installArea = new TextArea(dlInfo.getInstallCommand());
            installArea.setEditable(false);
            installArea.setWrapText(true);
            installArea.setStyle("-fx-control-inner-background: #0f0f23; -fx-text-fill: #00d4aa; " +
                    "-fx-font-family: monospace; -fx-font-size: 11px;");
            installArea.setPrefHeight(60);

            changelogBox.getChildren().addAll(installTitle, installArea);
        }

        // Progress bar
        progressBar = new ProgressBar(0);
        progressBar.setVisible(false);
        progressBar.setPrefWidth(350);
        progressBar.setStyle("-fx-accent: #00d4aa;");

        // Status label
        statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 11px;");

        // Buttons
        downloadButton = new Button("Descargar e Instalar");
        downloadButton.setStyle("-fx-background-color: #00d4aa; -fx-text-fill: #1a1a2e; " +
                "-fx-font-weight: bold; -fx-padding: 10 30; -fx-background-radius: 8;");
        downloadButton.setOnAction(e -> startDownload());

        laterButton = new Button("Más tarde");
        laterButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #888; " +
                "-fx-padding: 10 30; -fx-background-radius: 8;");
        laterButton.setOnAction(e -> close());

        // Layout
        mainLayout.getChildren().addAll(
                headerLabel, platformLabel,
                changelogTitle, changelogBox,
                progressBar, statusLabel,
                downloadButton, laterButton
        );

        Scene scene = new Scene(mainLayout, 400, 500);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialogStage.setScene(scene);
        dialogStage.centerOnScreen();
        dialogStage.show();
    }

    private void startDownload() {
        if (downloadStarted) return;
        downloadStarted = true;

        downloadButton.setDisable(true);
        laterButton.setDisable(true);
        progressBar.setVisible(true);
        statusLabel.setText("Descargando...");

        new Thread(() -> {
            try {
                UpdateDownloader downloader = new UpdateDownloader(new UpdateDownloader.DownloadProgressListener() {
                    @Override
                    public void onProgress(int percent, long downloaded, long total) {
                        Platform.runLater(() -> {
                            if (percent >= 0) {
                                progressBar.setProgress(percent / 100.0);
                                statusLabel.setText("Descargando... " + percent + "%");
                            } else {
                                statusLabel.setText("Descargando... " + formatBytes(downloaded));
                            }
                        });
                    }

                    @Override
                    public void onComplete(java.io.File file) {
                        Platform.runLater(() -> {
                            statusLabel.setText("Descarga completa: " + file.getName());
                            progressBar.setProgress(1.0);

                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Descarga completa");
                            alert.setHeaderText(null);
                            alert.setContentText("Archivo descargado en:\n" + file.getAbsolutePath() +
                                    "\n\nPor favor, instálalo manualmente.");
                            alert.showAndWait();
                            close();
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        Platform.runLater(() -> {
                            statusLabel.setText("Error: " + e.getMessage());
                            downloadButton.setDisable(false);
                            laterButton.setDisable(false);
                        });
                    }
                });

                java.io.File targetDir = new java.io.File(System.getProperty("user.home"), ".oktask/updates");
                downloader.downloadForCurrentPlatform(updateInfo, targetDir);

            } catch (Exception e) {
                Platform.runLater(() -> {
                    statusLabel.setText("Error: " + e.getMessage());
                    downloadButton.setDisable(false);
                    laterButton.setDisable(false);
                });
            }
        }).start();
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1048576) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / 1048576.0);
    }

    private void close() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}
