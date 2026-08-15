package com.academic.gestor.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA para la persistencia de configuración de Pomodoro.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
@Entity
@Table(name = "configuracion_pomodoro")
public class ConfiguracionPomodoroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private int duracionTrabajo;

    @Column(nullable = false)
    private int duracionDescanso;

    @Column(nullable = false)
    private int duracionDescansoLargo;

    @Column(nullable = false)
    private int pomodorosParaDescansoLargo;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private boolean activa;

    /**
     * Constructor por defecto para JPA.
     */
    protected ConfiguracionPomodoroEntity() {
    }

    /**
     * Constructor completo.
     *
     * @param id ID de la configuración
     * @param duracionTrabajo duración de trabajo
     * @param duracionDescanso duración de descanso
     * @param duracionDescansoLargo duración de descanso largo
     * @param pomodorosParaDescansoLargo pomodoros para descanso largo
     * @param fechaCreacion fecha de creación
     * @param activa si está activa
     */
    public ConfiguracionPomodoroEntity(final UUID id, final int duracionTrabajo,
                                       final int duracionDescanso,
                                       final int duracionDescansoLargo,
                                       final int pomodorosParaDescansoLargo,
                                       final LocalDateTime fechaCreacion,
                                       final boolean activa) {
        this.id = id;
        this.duracionTrabajo = duracionTrabajo;
        this.duracionDescanso = duracionDescanso;
        this.duracionDescansoLargo = duracionDescansoLargo;
        this.pomodorosParaDescansoLargo = pomodorosParaDescansoLargo;
        this.fechaCreacion = fechaCreacion;
        this.activa = activa;
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public int getDuracionTrabajo() {
        return duracionTrabajo;
    }

    public void setDuracionTrabajo(final int duracionTrabajo) {
        this.duracionTrabajo = duracionTrabajo;
    }

    public int getDuracionDescanso() {
        return duracionDescanso;
    }

    public void setDuracionDescanso(final int duracionDescanso) {
        this.duracionDescanso = duracionDescanso;
    }

    public int getDuracionDescansoLargo() {
        return duracionDescansoLargo;
    }

    public void setDuracionDescansoLargo(final int duracionDescansoLargo) {
        this.duracionDescansoLargo = duracionDescansoLargo;
    }

    public int getPomodorosParaDescansoLargo() {
        return pomodorosParaDescansoLargo;
    }

    public void setPomodorosParaDescansoLargo(final int pomodorosParaDescansoLargo) {
        this.pomodorosParaDescansoLargo = pomodorosParaDescansoLargo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(final LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(final boolean activa) {
        this.activa = activa;
    }
}
