package com.academic.gestor.notification;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Notificación nativa que aparece encima de todas las ventanas del sistema.
 *
 * <p>Implementa una ventana JavaFX con {@code alwaysOnTop(true)} y
 * {@code StageStyle.TRANSPARENT} para garantizar que sea visible incluso
 * cuando el usuario está en otra aplicación. La notificación se posiciona
 * en la esquina superior derecha de la pantalla y se cierra automáticamente
 * después de 5 segundos o al hacer clic.</p>
 *
 * <p>Utiliza el tema visual púrpura de OKtask (gradiente #7C3AED → #6D28D9)
 * con esquinas redondeadas y sombra suave.</p>
 *
 * @author OKtask
 * @since 1.2.0
 * @see javafx.stage.Stage#setAlwaysOnTop(boolean)
 */
public final class NativeNotification {

    private static final Logger log = LoggerFactory.getLogger(NativeNotification.class);

    /** Ancho de la notificación en píxeles. */
    private static final int WIDTH = 380;

    /** Margen desde la esquina de la pantalla en píxeles. */
    private static final int MARGIN = 20;

    /** Segundos antes de cerrar automáticamente la notificación. */
    private static final double AUTO_DISMISS_SECONDS = 5.0;

    /** Duración de la animación de entrada en milisegundos. */
    private static final double FADE_IN_MS = 300;

    /** Duración de la animación de salida en milisegundos. */
    private static final double FADE_OUT_MS = 250;

    /** Color degradado primario (púrpura OKtask). */
    private static final String GRADIENT_PRIMARY = "#7C3AED";

    /** Color degradado oscuro (púrpura oscuro OKtask). */
    private static final String GRADIENT_DARK = "#6D28D9";

    /** Instancia singleton de la notificación. */
    private static final NativeNotification INSTANCE = new NativeNotification();

    /** Referencia a la stage activa para control de lifecycle. */
    private final AtomicReference<Stage> activeStage = new AtomicReference<>();

    /** Transición de auto-cierre para poder cancelarla. */
    private volatile PauseTransition autoCloseTransition;

    /**
     * Constructor privado. Utiliza patrón singleton.
     */
    private NativeNotification() {
    }

    /**
     * Obtiene la instancia singleton de {@code NativeNotification}.
     *
     * @return instancia única
     */
    public static NativeNotification getInstance() {
        return INSTANCE;
    }

    /**
     * Muestra una notificación nativa con el nombre de la tarea y un mensaje.
     *
     * <p>Es seguro llamar desde cualquier hilo. Si se invoca desde un hilo
     * diferente al de JavaFX, se reprograma automáticamente usando
     * {@code Platform.runLater()}.</p>
     *
     * @param taskName nombre de la tarea completada
     * @param message  mensaje a mostrar (generalmente "¡Pomodoro completado!")
     */
    public void show(final String taskName, final String message) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> show(taskName, message));
            return;
        }

        dismissExisting();
        createAndShowNotification(taskName, message);
    }

    /**
     * Cierra la notificación activa si existe, con animación de salida.
     */
    public void dismissExisting() {
        cancelAutoClose();

        final Stage stage = activeStage.getAndSet(null);
        if (stage == null || stage.getScene() == null) {
            return;
        }

        final VBox root = (VBox) stage.getScene().getRoot();
        final FadeTransition fadeOut = new FadeTransition(
                Duration.millis(FADE_OUT_MS), root
        );
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> safeClose(stage));
        fadeOut.play();
    }

    /**
     * Crea y muestra la notificación en la esquina superior derecha.
     *
     * @param taskName nombre de la tarea
     * @param message  mensaje de la notificación
     */
    private void createAndShowNotification(final String taskName, final String message) {
        try {
            final Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setAlwaysOnTop(true);

            final VBox content = buildNotificationContent(taskName, message);
            final Scene scene = new Scene(content);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);

            positionAtTopRight(stage);

            content.setOnMouseClicked(event -> {
                log.debug("Notificación nativa cerrada por clic del usuario");
                dismissExisting();
            });

            if (activeStage.compareAndSet(null, stage)) {
                stage.show();

                // Animación de entrada
                final FadeTransition fadeIn = new FadeTransition(
                        Duration.millis(FADE_IN_MS), content
                );
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();

                // Auto-cierre programado
                autoCloseTransition = new PauseTransition(
                        Duration.seconds(AUTO_DISMISS_SECONDS)
                );
                autoCloseTransition.setOnFinished(e -> dismissExisting());
                autoCloseTransition.play();

                log.debug("Notificación nativa mostrada para tarea: {}", taskName);
            }
        } catch (final Exception e) {
            log.error("Error al crear notificación nativa para tarea: {}", taskName, e);
        }
    }

    /**
     * Construye el contenido visual de la notificación.
     *
     * <p>Estructura:
     * <pre>
     * ┌─────────────────────────────────┐
     * │  🍅 OKtask                      │
     * │  Nombre de la Tarea             │
     * │  ¡Pomodoro completado!          │
     * │  Haz clic para cerrar           │
     * └─────────────────────────────────┘
     * </pre>
     *
     * @param taskName nombre de la tarea
     * @param message  mensaje a mostrar
     * @return VBox con el contenido estilizado
     */
    private VBox buildNotificationContent(final String taskName, final String message) {
        final VBox container = new VBox(6);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(16, 20, 14, 20));
        container.setMaxWidth(WIDTH);
        container.setMinWidth(WIDTH);
        container.setOpacity(0);

        // Fondo con gradiente púrpura y sombra
        container.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, "
                        + GRADIENT_PRIMARY + ", " + GRADIENT_DARK + ");"
                        + "-fx-background-radius: 12;"
                        + "-fx-border-radius: 12;"
                        + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.35), 16, 0, 0, 4);"
        );

        container.getChildren().addAll(
                buildHeaderLabel(),
                buildTaskLabel(taskName),
                buildMessageLabel(message),
                buildHintLabel()
        );

        return container;
    }

    /**
     * Construye la etiqueta del encabezado con el ícono de la aplicación.
     *
     * @return Label del encabezado
     */
    private Label buildHeaderLabel() {
        final Label header = new Label("\uD83C\uDF45 OKtask");
        header.setStyle(
                "-fx-text-fill: rgba(255,255,255,0.8);"
                        + "-fx-font-size: 11px;"
                        + "-fx-font-weight: bold;"
        );
        return header;
    }

    /**
     * Construye la etiqueta con el nombre de la tarea.
     *
     * @param taskName nombre de la tarea
     * @return Label del nombre de tarea
     */
    private Label buildTaskLabel(final String taskName) {
        final Label taskLabel = new Label(taskName);
        taskLabel.setWrapText(true);
        taskLabel.setMaxWidth(WIDTH - 40);
        taskLabel.setStyle(
                "-fx-text-fill: white;"
                        + "-fx-font-size: 15px;"
                        + "-fx-font-weight: bold;"
        );
        return taskLabel;
    }

    /**
     * Construye la etiqueta con el mensaje principal.
     *
     * @param message mensaje a mostrar
     * @return Label del mensaje
     */
    private Label buildMessageLabel(final String message) {
        final Label msgLabel = new Label(message);
        msgLabel.setWrapText(true);
        msgLabel.setMaxWidth(WIDTH - 40);
        msgLabel.setStyle(
                "-fx-text-fill: rgba(255,255,255,0.92);"
                        + "-fx-font-size: 13px;"
        );
        return msgLabel;
    }

    /**
     * Construye la etiqueta de ayuda para cerrar la notificación.
     *
     * @return Label de ayuda
     */
    private Label buildHintLabel() {
        final Label hint = new Label("Haz clic para cerrar");
        hint.setStyle(
                "-fx-text-fill: rgba(255,255,255,0.45);"
                        + "-fx-font-size: 10px;"
        );
        return hint;
    }

    /**
     * Posiciona la stage en la esquina superior derecha de la pantalla primaria.
     *
     * @param stage stage a posicionar
     */
    private void positionAtTopRight(final Stage stage) {
        final Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        stage.setX(bounds.getMaxX() - WIDTH - MARGIN);
        stage.setY(bounds.getMinY() + MARGIN);
    }

    /**
     * Cancela la transición de auto-cierre si está activa.
     */
    private void cancelAutoClose() {
        final PauseTransition transition = autoCloseTransition;
        if (transition != null) {
            transition.stop();
            autoCloseTransition = null;
        }
    }

    /**
     * Cierra una stage de forma segura, capturando cualquier excepción.
     *
     * @param stage stage a cerrar
     */
    private void safeClose(final Stage stage) {
        try {
            if (stage.isShowing()) {
                stage.close();
            }
        } catch (final Exception e) {
            log.debug("Error al cerrar stage de notificación: {}", e.getMessage());
        }
    }
}
