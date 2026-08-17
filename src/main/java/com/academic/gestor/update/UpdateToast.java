package com.academic.gestor.update;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Notificación toast que aparece cuando hay una nueva versión disponible.
 *
 * <p>Muestra un popup temporal en la esquina superior derecha con
 * información de la nueva versión y un botón para ver detalles.</p>
 *
 * @author OKtask
 * @since 1.2.0
 */
public class UpdateToast {

    /** Duración que el toast permanece visible (en segundos). */
    private static final int DISPLAY_SECONDS = 8;

    /** Referencia al toast actual para evitar múltiples instancias. */
    private static final AtomicReference<Stage> currentToast = new AtomicReference<>();

    /** Callback cuando el usuario hace click en "Ver detalles". */
    private Runnable onDetailsAction;

    /**
     * Constructor.
     *
     * @param onDetailsAction callback a ejecutar cuando se hace click en "Ver detalles"
     */
    public UpdateToast(Runnable onDetailsAction) {
        this.onDetailsAction = onDetailsAction;
    }

    /**
     * Muestra el toast con la información de la versión disponible.
     *
     * @param updateInfo información de la actualización
     */
    public void show(UpdateInfo updateInfo) {
        if (updateInfo == null || !updateInfo.isValid()) return;

        // Cerrar toast anterior si existe
        Stage existing = currentToast.getAndSet(null);
        if (existing != null && existing.isShowing()) {
            existing.close();
        }

        Platform.runLater(() -> {
            try {
                Stage toastStage = createToastStage(updateInfo);
                currentToast.set(toastStage);
                toastStage.show();

                // Auto-cerrar después de DISPLAY_SECONDS
                PauseTransition pause = new PauseTransition(Duration.seconds(DISPLAY_SECONDS));
                pause.setOnFinished(e -> closeToast(toastStage));
                pause.play();

            } catch (Exception e) {
                // Silenciar errores de UI
            }
        });
    }

    /**
     * Cierra el toast actual si está visible.
     */
    public void dismiss() {
        Stage toast = currentToast.getAndSet(null);
        if (toast != null && toast.isShowing()) {
            Platform.runLater(() -> {
                FadeTransition fade = createFadeOut(toast);
                fade.setOnFinished(e -> toast.close());
                fade.play();
            });
        }
    }

    private Stage createToastStage(UpdateInfo updateInfo) {
        Stage stage = new Stage();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);

        // Contenido del toast
        VBox content = createToastContent(updateInfo);
        StackPane root = new StackPane(content);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);

        // Posicionar en esquina superior derecha
        positionToast(stage);

        // Click en el toast → abrir detalles
        content.setOnMouseClicked(e -> {
            if (onDetailsAction != null) {
                onDetailsAction.run();
            }
            closeToast(stage);
        });

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), content);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        return stage;
    }

    private VBox createToastContent(UpdateInfo updateInfo) {
        // Fondo con gradiente
        javafx.scene.shape.Rectangle background = new javafx.scene.shape.Rectangle();
        background.setWidth(360);
        background.setHeight(100);
        background.setArcWidth(16);
        background.setArcHeight(16);
        background.setFill(new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#7C3AED")),
                new Stop(1, Color.web("#6D28D9"))));
        background.setEffect(new DropShadow(12, Color.rgb(0, 0, 0, 0.3)));

        // Ícono de actualización
        Label icon = new Label("\uD83D\uDD04"); // 🔄
        icon.setFont(Font.font(24));

        // Texto principal
        Label title = new Label("Nueva versión disponible");
        title.setFont(Font.font("SansSerif", FontWeight.BOLD, 14));
        title.setTextFill(Color.WHITE);

        // Versión
        Label version = new Label("v" + updateInfo.getVersion());
        version.setFont(Font.font("SansSerif", FontWeight.NORMAL, 12));
        version.setTextFill(Color.web("#DDD6FE"));

        // Texto de acción
        Label action = new Label("Hacé clic para ver detalles");
        action.setFont(Font.font("SansSerif", FontWeight.NORMAL, 11));
        action.setTextFill(Color.web("#C4B5FD"));

        VBox textBox = new VBox(2, title, version, action);
        textBox.setAlignment(Pos.CENTER_LEFT);

        HBox container = new HBox(12, icon, textBox);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(12, 16, 12, 16));

        StackPane root = new StackPane(background, container);
        StackPane.setAlignment(container, Pos.CENTER_LEFT);

        VBox wrapper = new VBox(root);
        wrapper.setAlignment(Pos.TOP_RIGHT);

        return wrapper;
    }

    private void positionToast(Stage stage) {
        var bounds = Screen.getPrimary().getVisualBounds();
        double x = bounds.getMaxX() - 380;
        double y = bounds.getMinY() + 20;
        stage.setX(x);
        stage.setY(y);
    }

    private void closeToast(Stage stage) {
        if (stage != null && stage.isShowing()) {
            FadeTransition fade = createFadeOut(stage);
            fade.setOnFinished(e -> {
                stage.close();
                currentToast.compareAndSet(stage, null);
            });
            fade.play();
        }
    }

    private FadeTransition createFadeOut(Stage stage) {
        FadeTransition fade = new FadeTransition(Duration.millis(250), stage.getScene().getRoot());
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        return fade;
    }
}
