package com.academic.gestor.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA para la persistencia de tareas.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
@Entity
@Table(name = "tareas")
public class TareaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private UUID materiaId;

    @Column(nullable = false)
    private LocalDate fechaLimite;

    @Column(nullable = false)
    private String prioridad;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaCompletado;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int minutosPomodoro;

    /**
     * Constructor por defecto para JPA.
     */
    protected TareaEntity() {
    }

    /**
     * Constructor completo.
     *
     * @param id ID de la tarea
     * @param titulo título de la tarea
     * @param descripcion descripción de la tarea
     * @param materiaId ID de la materia asociada
     * @param fechaLimite fecha límite
     * @param prioridad prioridad
     * @param estado estado
     * @param fechaCreacion fecha de creación
     * @param fechaCompletado fecha de completado
     * @param minutosPomodoro minutos acumulados de pomodoro
     */
    public TareaEntity(final UUID id, final String titulo, final String descripcion,
                       final UUID materiaId, final LocalDate fechaLimite,
                       final String prioridad, final String estado,
                       final LocalDateTime fechaCreacion, final LocalDateTime fechaCompletado,
                       final int minutosPomodoro) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.materiaId = materiaId;
        this.fechaLimite = fechaLimite;
        this.prioridad = prioridad;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaCompletado = fechaCompletado;
        this.minutosPomodoro = minutosPomodoro;
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(final String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(final String descripcion) {
        this.descripcion = descripcion;
    }

    public UUID getMateriaId() {
        return materiaId;
    }

    public void setMateriaId(final UUID materiaId) {
        this.materiaId = materiaId;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(final LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(final String prioridad) {
        this.prioridad = prioridad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(final String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(final LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaCompletado() {
        return fechaCompletado;
    }

    public void setFechaCompletado(final LocalDateTime fechaCompletado) {
        this.fechaCompletado = fechaCompletado;
    }

    public int getMinutosPomodoro() {
        return minutosPomodoro;
    }

    public void setMinutosPomodoro(final int minutosPomodoro) {
        this.minutosPomodoro = minutosPomodoro;
    }
}
