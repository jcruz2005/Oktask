package com.academic.gestor.update;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
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

/**
 * Panel modal que muestra los detalles de una actualización disponible.
 *
 * <p>Incluye changelog, fecha de release, y botón para descargar.</p>
 *
 * @author OKtask
 * @since 1.2.0
 */
public class UpdatePanel {

    /** Callback cuando el usuario hace click en "Descargar". */
    private Runnable onDownloadAction;

    /** Referencia al stage del panel. */
    private Stage panelStage;

    /**
     * Constructor.
     *
     * @param onDownloadAction callback a ejecutar cuando se hace click en "Descargar"
     */
    public UpdatePanel(Runnable onDownloadAction) {
        this.onDownloadAction = onDownloadAction;
    }

    /**
     * Muestra el panel de actualización con la información de la versión.
     *
     * @param updateInfo información de la actualización a mostrar
     */
    public void show(UpdateInfo updateInfo) {
        if (updateInfo == null || !updateInfo.isValid()) return;

        panelStage = new Stage();
        panelStage.initStyle(StageStyle.DECORATED);
        panelStage.initModality(Modality.APPLICATION_MODAL);
        panelStage.setTitle("Actualización Disponible");
        panelStage.setMinWidth(450);
        panelStage.setMinHeight(400);

        VBox content = createContent(updateInfo);
        Scene scene = new Scene(content, 450, 400);
        panelStage.setScene(scene);
        panelStage.show();
    }

    /**
     * Cierra el panel si está abierto.
     */
    public void close() {
        if (panelStage != null && panelStage.isShowing()) {
            panelStage.close();
        }
    }

    private VBox createContent(UpdateInfo updateInfo) {
        // Header con gradiente
        javafx.scene.shape.Rectangle headerBg = new javafx.scene.shape.Rectangle();
        headerBg.setWidth(450);
        headerBg.setHeight(120);
        headerBg.setFill(new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#7C3AED")),
                new Stop(1, Color.web("#6D28D9"))));

        Label headerIcon = new Label("\uD83D\uDD04");
        headerIcon.setFont(Font.font(36));

        Label headerTitle = new Label("Nueva Versión Disponible");
        headerTitle.setFont(Font.font("SansSerif", FontWeight.BOLD, 22));
        headerTitle.setTextFill(Color.WHITE);

        Label headerVersion = new Label("v" + updateInfo.getVersion());
        headerVersion.setFont(Font.font("SansSerif", FontWeight.NORMAL, 16));
        headerVersion.setTextFill(Color.web("#DDD6FE"));

        Label headerDate = new Label(updateInfo.getReleaseDate() != null
                ? "Publicado: " + updateInfo.getReleaseDate() : "");
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
        if (updateInfo.getChangelog() != null && !updateInfo.getChangelog().isEmpty()) {
            for (String change : updateInfo.getChangelog()) {
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
        scrollPane.setPrefHeight(180);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        // Botones
        Button downloadBtn = new Button("Descargar Actualización");
        downloadBtn.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        downloadBtn.setStyle("-fx-background-color: #7C3AED; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-padding: 10 24;");
        downloadBtn.setOnMouseEntered(e ->
                downloadBtn.setStyle("-fx-background-color: #6D28D9; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-padding: 10 24; -fx-cursor: hand;"));
        downloadBtn.setOnMouseExited(e ->
                downloadBtn.setStyle("-fx-background-color: #7C3AED; -fx-text-fill: white; " +
                        "-fx-background-radius: 8; -fx-padding: 10 24;"));
        downloadBtn.setOnAction(e -> {
            if (onDownloadAction != null) {
                onDownloadAction.run();
            }
            openDownloadUrl(updateInfo.getDownloadUrl());
        });

        Button closeBtn = new Button("Actualizar más tarde");
        closeBtn.setFont(Font.font("SansSerif", 13));
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #6B7280; " +
                "-fx-padding: 10 16;");
        closeBtn.setOnAction(e -> close());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttons = new HBox(12, closeBtn, spacer, downloadBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(16, 24, 16, 24));

        // Layout principal
        VBox layout = new VBox(header, changelogTitle, scrollPane, buttons);
        layout.setStyle("-fx-background-color: #FAFAFA;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return layout;
    }

    private void openDownloadUrl(String url) {
        if (url == null || url.isEmpty()) return;
        try {
            java.awt.Desktop.getDesktop().browse(new java.net.URI(url));
        } catch (Exception e) {
            // Silenciar errores de apertura de navegador
        }
    }
}
