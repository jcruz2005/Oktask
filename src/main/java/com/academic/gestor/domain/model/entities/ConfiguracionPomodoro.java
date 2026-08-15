package com.academic.gestor.domain.model.entities;

import com.academic.gestor.domain.model.valueobjects.DuracionMinutos;
import com.academic.gestor.shared.kernel.Entity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad que representa la configuración del temporizador Pomodoro.
 *
 * <p>Define las duraciones de trabajo, descanso y descanso largo,
 * así como la cantidad de pomodoros antes de un descanso largo.</p>
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public class ConfiguracionPomodoro extends Entity<ConfiguracionPomodoro> {

    private DuracionMinutos duracionTrabajo;
    private DuracionMinutos duracionDescanso;
    private DuracionMinutos duracionDescansoLargo;
    private int pomodorosParaDescansoLargo;
    private LocalDateTime fechaCreacion;
    private boolean activa;

    /**
     * Constructor completo de ConfiguracionPomodoro.
     *
     * @param id identificador único
     * @param duracionTrabajo duración de las sesiones de trabajo
     * @param duracionDescanso duración de los descansos cortos
     * @param duracionDescansoLargo duración del descanso largo
     * @param pomodorosParaDescansoLargo número de pomodoros antes del descanso largo
     * @param fechaCreacion fecha de creación de la configuración
     * @param activa si esta es la configuración activa
     */
    public ConfiguracionPomodoro(final UUID id, final DuracionMinutos duracionTrabajo,
                                 final DuracionMinutos duracionDescanso,
                                 final DuracionMinutos duracionDescansoLargo,
                                 final int pomodorosParaDescansoLargo,
                                 final LocalDateTime fechaCreacion, final boolean activa) {
        super(id);
        this.duracionTrabajo = Objects.requireNonNull(duracionTrabajo, "La duración de trabajo no puede ser nula");
        this.duracionDescanso = Objects.requireNonNull(duracionDescanso, "La duración de descanso no puede ser nula");
        this.duracionDescansoLargo = Objects.requireNonNull(duracionDescansoLargo, "La duración de descanso largo no puede ser nula");
        if (pomodorosParaDescansoLargo <= 0) {
            throw new IllegalArgumentException("Los pomodoros para descanso largo deben ser mayor a 0");
        }
        this.pomodorosParaDescansoLargo = pomodorosParaDescansoLargo;
        this.fechaCreacion = Objects.requireNonNull(fechaCreacion, "La fecha de creación no puede ser nula");
        this.activa = activa;
    }

    /**
     * Crea una configuración por defecto.
     *
     * @return configuración con valores estándar (25/5/15 min)
     */
    public static ConfiguracionPomodoro createDefault() {
        return new ConfiguracionPomodoro(
                UUID.randomUUID(),
                DuracionMinutos.of(25),
                DuracionMinutos.of(5),
                DuracionMinutos.of(15),
                4,
                LocalDateTime.now(),
                true
        );
    }

    /**
     * Crea una configuración personalizada.
     *
     * @param duracionTrabajo duración de trabajo en minutos
     * @param duracionDescanso duración de descanso en minutos
     * @param duracionDescansoLargo duración de descanso largo en minutos
     * @param pomodorosParaDescansoLargo número de pomodoros para descanso largo
     * @return nueva configuración
     */
    public static ConfiguracionPomodoro create(final int duracionTrabajo, final int duracionDescanso,
                                               final int duracionDescansoLargo,
                                               final int pomodorosParaDescansoLargo) {
        return new ConfiguracionPomodoro(
                UUID.randomUUID(),
                DuracionMinutos.of(duracionTrabajo),
                DuracionMinutos.of(duracionDescanso),
                DuracionMinutos.of(duracionDescansoLargo),
                pomodorosParaDescansoLargo,
                LocalDateTime.now(),
                true
        );
    }

    /**
     * Actualiza la duración de trabajo.
     *
     * @param duracionTrabajo nueva duración de trabajo
     */
    public void setDuracionTrabajo(final DuracionMinutos duracionTrabajo) {
        this.duracionTrabajo = Objects.requireNonNull(duracionTrabajo);
    }

    /**
     * Actualiza la duración de descanso.
     *
     * @param duracionDescanso nueva duración de descanso
     */
    public void setDuracionDescanso(final DuracionMinutos duracionDescanso) {
        this.duracionDescanso = Objects.requireNonNull(duracionDescanso);
    }

    /**
     * Actualiza la duración de descanso largo.
     *
     * @param duracionDescansoLargo nueva duración de descanso largo
     */
    public void setDuracionDescansoLargo(final DuracionMinutos duracionDescansoLargo) {
        this.duracionDescansoLargo = Objects.requireNonNull(duracionDescansoLargo);
    }

    /**
     * Actualiza la cantidad de pomodoros para descanso largo.
     *
     * @param pomodorosParaDescansoLargo nueva cantidad
     */
    public void setPomodorosParaDescansoLargo(final int pomodorosParaDescansoLargo) {
        if (pomodorosParaDescansoLargo <= 0) {
            throw new IllegalArgumentException("Los pomodoros para descanso largo deben ser mayor a 0");
        }
        this.pomodorosParaDescansoLargo = pomodorosParaDescansoLargo;
    }

    /**
     * Desactiva esta configuración.
     */
    public void desactivar() {
        this.activa = false;
    }

    /**
     * Activa esta configuración.
     */
    public void activar() {
        this.activa = true;
    }

    public DuracionMinutos getDuracionTrabajo() {
        return duracionTrabajo;
    }

    public DuracionMinutos getDuracionDescanso() {
        return duracionDescanso;
    }

    public DuracionMinutos getDuracionDescansoLargo() {
        return duracionDescansoLargo;
    }

    public int getPomodorosParaDescansoLargo() {
        return pomodorosParaDescansoLargo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public boolean isActiva() {
        return activa;
    }
}
