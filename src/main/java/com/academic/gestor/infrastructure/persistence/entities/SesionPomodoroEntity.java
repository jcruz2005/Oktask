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
 * Entidad JPA para la persistencia de sesiones de Pomodoro.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
@Entity
@Table(name = "sesiones_pomodoro")
public class SesionPomodoroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID tareaId;

    @Column(nullable = false)
    private UUID materiaId;

    @Column(nullable = false)
    private int duracionMinutos;

    @Column(nullable = false)
    private String tipoSesion;

    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    @Column(nullable = false)
    private boolean completada;

    /**
     * Constructor por defecto para JPA.
     */
    protected SesionPomodoroEntity() {
    }

    /**
     * Constructor completo.
     *
     * @param id ID de la sesión
     * @param tareaId ID de la tarea
     * @param materiaId ID de la materia
     * @param duracionMinutos duración en minutos
     * @param tipoSesion tipo de sesión
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @param completada si está completada
     */
    public SesionPomodoroEntity(final UUID id, final UUID tareaId, final UUID materiaId,
                                final int duracionMinutos, final String tipoSesion,
                                final LocalDateTime fechaInicio, final LocalDateTime fechaFin,
                                final boolean completada) {
        this.id = id;
        this.tareaId = tareaId;
        this.materiaId = materiaId;
        this.duracionMinutos = duracionMinutos;
        this.tipoSesion = tipoSesion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.completada = completada;
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public UUID getTareaId() {
        return tareaId;
    }

    public void setTareaId(final UUID tareaId) {
        this.tareaId = tareaId;
    }

    public UUID getMateriaId() {
        return materiaId;
    }

    public void setMateriaId(final UUID materiaId) {
        this.materiaId = materiaId;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(final int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    public String getTipoSesion() {
        return tipoSesion;
    }

    public void setTipoSesion(final String tipoSesion) {
        this.tipoSesion = tipoSesion;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(final LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(final LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(final boolean completada) {
        this.completada = completada;
    }
}
