package com.academic.gestor.domain.model.entities;

import com.academic.gestor.domain.events.TareaCompletadaEvent;
import com.academic.gestor.domain.model.enums.EstadoTarea;
import com.academic.gestor.domain.model.enums.Prioridad;
import com.academic.gestor.shared.kernel.AggregateRoot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad que representa una tarea académica.
 *
 * <p>Una tarea está asociada a una materia y tiene un estado que
 * evoluciona desde PENDIENTE hasta COMPLETADA, pasando por EN_PROGRESO.</p>
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public class Tarea extends AggregateRoot<Tarea> {

    private String titulo;
    private String descripcion;
    private UUID materiaId;
    private LocalDate fechaLimite;
    private Prioridad prioridad;
    private EstadoTarea estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaCompletado;
    private int minutosPomodoro;

    /**
     * Constructor completo de Tarea.
     *
     * @param id identificador único
     * @param titulo título de la tarea
     * @param descripcion descripción detallada
     * @param materiaId ID de la materia asociada
     * @param fechaLimite fecha límite de entrega
     * @param prioridad nivel de prioridad
     * @param estado estado actual de la tarea
     * @param fechaCreacion fecha de creación
     * @param fechaCompletado fecha en que se completó (puede ser null)
     * @param minutosPomodoro minutos acumulados de pomodoro
     */
    public Tarea(final UUID id, final String titulo, final String descripcion,
                 final UUID materiaId, final LocalDate fechaLimite,
                 final Prioridad prioridad, final EstadoTarea estado,
                 final LocalDateTime fechaCreacion, final LocalDateTime fechaCompletado,
                 final int minutosPomodoro) {
        super(id);
        setTitulo(titulo);
        this.descripcion = descripcion;
        this.materiaId = Objects.requireNonNull(materiaId, "El ID de materia no puede ser nulo");
        this.fechaLimite = Objects.requireNonNull(fechaLimite, "La fecha límite no puede ser nula");
        this.prioridad = Objects.requireNonNull(prioridad, "La prioridad no puede ser nula");
        this.estado = Objects.requireNonNull(estado, "El estado no puede ser nulo");
        this.fechaCreacion = Objects.requireNonNull(fechaCreacion, "La fecha de creación no puede ser nula");
        this.fechaCompletado = fechaCompletado;
        this.minutosPomodoro = minutosPomodoro;
    }

    /**
     * Factory method que crea una nueva Tarea con estado PENDIENTE.
     *
     * @param titulo título de la tarea
     * @param descripcion descripción detallada
     * @param materiaId ID de la materia asociada
     * @param fechaLimite fecha límite de entrega
     * @param prioridad nivel de prioridad
     * @return nueva instancia de Tarea
     */
    public static Tarea create(final String titulo, final String descripcion,
                               final UUID materiaId, final LocalDate fechaLimite,
                               final Prioridad prioridad) {
        return new Tarea(
                UUID.randomUUID(),
                titulo,
                descripcion,
                materiaId,
                fechaLimite,
                prioridad,
                EstadoTarea.PENDIENTE,
                LocalDateTime.now(),
                null,
                0
        );
    }

    /**
     * Actualiza el título de la tarea.
     *
     * @param titulo nuevo título
     */
    public void setTitulo(final String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título de la tarea no puede estar vacío");
        }
        this.titulo = titulo.trim();
    }

    /**
     * Actualiza la descripción de la tarea.
     *
     * @param descripcion nueva descripción
     */
    public void setDescripcion(final String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Actualiza la fecha límite de la tarea.
     *
     * @param fechaLimite nueva fecha límite
     */
    public void setFechaLimite(final LocalDate fechaLimite) {
        this.fechaLimite = Objects.requireNonNull(fechaLimite, "La fecha límite no puede ser nula");
    }

    /**
     * Actualiza la prioridad de la tarea.
     *
     * @param prioridad nueva prioridad
     */
    public void setPrioridad(final Prioridad prioridad) {
        this.prioridad = Objects.requireNonNull(prioridad, "La prioridad no puede ser nula");
    }

    /**
     * Cambia el estado de la tarea.
     *
     * <p>Si el estado cambia a COMPLETADA, se registra la fecha de completado
     * y se publica un evento de dominio.</p>
     *
     * @param nuevoEstado nuevo estado de la tarea
     */
    public void cambiarEstado(final EstadoTarea nuevoEstado) {
        this.estado = Objects.requireNonNull(nuevoEstado, "El estado no puede ser nulo");

        if (nuevoEstado == EstadoTarea.COMPLETADA) {
            this.fechaCompletado = LocalDateTime.now();
            registerEvent(new TareaCompletadaEvent(
                    this.id,
                    this.materiaId,
                    this.fechaCompletado
            ));
        }
    }

    /**
     * Verifica si la tarea está atrasada respecto a su fecha límite.
     *
     * @return true si la fecha actual es posterior a la fecha límite
     */
    public boolean isAtrasada() {
        return LocalDate.now().isAfter(fechaLimite) && estado != EstadoTarea.COMPLETADA;
    }

    /**
     * Obtiene el título de la tarea.
     *
     * @return título
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Obtiene la descripción de la tarea.
     *
     * @return descripción
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Obtiene el ID de la materia asociada.
     *
     * @return materiaId
     */
    public UUID getMateriaId() {
        return materiaId;
    }

    /**
     * Obtiene la fecha límite de la tarea.
     *
     * @return fecha límite
     */
    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    /**
     * Obtiene la prioridad de la tarea.
     *
     * @return prioridad
     */
    public Prioridad getPrioridad() {
        return prioridad;
    }

    /**
     * Obtiene el estado actual de la tarea.
     *
     * @return estado
     */
    public EstadoTarea getEstado() {
        return estado;
    }

    /**
     * Obtiene la fecha de creación de la tarea.
     *
     * @return fecha de creación
     */
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * Obtiene la fecha en que se completó la tarea.
     *
     * @return fecha de completado, o null si no está completada
     */
    public LocalDateTime getFechaCompletado() {
        return fechaCompletado;
    }

    /**
     * Obtiene los minutos acumulados de pomodoro.
     *
     * @return minutos pomodoro
     */
    public int getMinutosPomodoro() {
        return minutosPomodoro;
    }

    /**
     * Agrega minutos de pomodoro a la tarea.
     *
     * @param minutos minutos a agregar
     */
    public void agregarMinutosPomodoro(final int minutos) {
        this.minutosPomodoro += minutos;
    }

    /**
     * Establece los minutos de pomodoro.
     *
     * @param minutosPomodoro nuevos minutos
     */
    public void setMinutosPomodoro(final int minutosPomodoro) {
        this.minutosPomodoro = minutosPomodoro;
    }
}
