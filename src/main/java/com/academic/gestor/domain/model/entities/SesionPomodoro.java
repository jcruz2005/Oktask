package com.academic.gestor.domain.model.entities;

import com.academic.gestor.domain.events.PomodoroCanceladoEvent;
import com.academic.gestor.domain.events.PomodoroCompletadoEvent;
import com.academic.gestor.domain.model.enums.TipoSesion;
import com.academic.gestor.domain.model.valueobjects.DuracionMinutos;
import com.academic.gestor.shared.kernel.AggregateRoot;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad que representa una sesión de Pomodoro.
 *
 * <p>Una sesión está vinculada a una tarea y una materia, y registra
 * la duración, tipo y estado de la sesión de estudio.</p>
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public class SesionPomodoro extends AggregateRoot<SesionPomodoro> {

    private final UUID tareaId;
    private final UUID materiaId;
    private final DuracionMinutos duracion;
    private final TipoSesion tipoSesion;
    private final LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private boolean completada;

    /**
     * Constructor completo de SesionPomodoro.
     *
     * @param id identificador único
     * @param tareaId ID de la tarea asociada
     * @param materiaId ID de la materia asociada
     * @param duracion duración de la sesión
     * @param tipoSesion tipo de sesión (trabajo o descanso)
     * @param fechaInicio fecha y hora de inicio
     * @param fechaFin fecha y hora de fin (puede ser null si está en curso)
     * @param completada si la sesión fue completada
     */
    public SesionPomodoro(final UUID id, final UUID tareaId, final UUID materiaId,
                          final DuracionMinutos duracion, final TipoSesion tipoSesion,
                          final LocalDateTime fechaInicio, final LocalDateTime fechaFin,
                          final boolean completada) {
        super(id);
        this.tareaId = Objects.requireNonNull(tareaId, "El ID de tarea no puede ser nulo");
        this.materiaId = Objects.requireNonNull(materiaId, "El ID de materia no puede ser nulo");
        this.duracion = Objects.requireNonNull(duracion, "La duración no puede ser nula");
        this.tipoSesion = Objects.requireNonNull(tipoSesion, "El tipo de sesión no puede ser nulo");
        this.fechaInicio = Objects.requireNonNull(fechaInicio, "La fecha de inicio no puede ser nula");
        this.fechaFin = fechaFin;
        this.completada = completada;
    }

    /**
     * Factory method que crea una nueva sesión de Pomodoro en curso.
     *
     * @param tareaId ID de la tarea asociada
     * @param materiaId ID de la materia asociada
     * @param duracion duración de la sesión
     * @param tipoSesion tipo de sesión
     * @return nueva instancia de SesionPomodoro
     */
    public static SesionPomodoro iniciar(final UUID tareaId, final UUID materiaId,
                                         final DuracionMinutos duracion, final TipoSesion tipoSesion) {
        return new SesionPomodoro(
                UUID.randomUUID(),
                tareaId,
                materiaId,
                duracion,
                tipoSesion,
                LocalDateTime.now(),
                null,
                false
        );
    }

    /**
     * Marca la sesión como completada y registra la fecha de fin.
     *
     * @throws com.academic.gestor.domain.exceptions.SesionPomodoroException si la sesión ya estaba completada
     */
    public void completar() {
        if (this.completada) {
            throw new com.academic.gestor.domain.exceptions.SesionPomodoroException(
                    "La sesión de Pomodoro ya está completada"
            );
        }
        this.fechaFin = LocalDateTime.now();
        this.completada = true;

        registerEvent(new PomodoroCompletadoEvent(
                this.id,
                this.tareaId,
                this.materiaId,
                this.tipoSesion,
                this.fechaInicio,
                this.fechaFin
        ));
    }

    /**
     * Marca la sesión como completada por cancelación, registrando el tiempo transcurrido.
     *
     * @param minutosTranscurridos minutos realmente transcurridos antes de cancelar
     * @throws com.academic.gestor.domain.exceptions.SesionPomodoroException si la sesión ya estaba completada
     */
    public void cancelar(final long minutosTranscurridos) {
        if (this.completada) {
            throw new com.academic.gestor.domain.exceptions.SesionPomodoroException(
                    "La sesión de Pomodoro ya está completada"
            );
        }
        if (minutosTranscurridos < 0) {
            throw new com.academic.gestor.domain.exceptions.SesionPomodoroException(
                    "Los minutos transcurridos no pueden ser negativos"
            );
        }

        this.fechaFin = this.fechaInicio.plusMinutes(Math.max(minutosTranscurridos, 1));
        this.completada = true;

        registerEvent(new PomodoroCanceladoEvent(
                this.id,
                this.tareaId,
                this.materiaId,
                this.tipoSesion,
                this.fechaInicio,
                this.fechaFin,
                minutosTranscurridos
        ));
    }

    /**
     * Calcula la duración de la sesión en minutos.
     * Usa la duración real transcurrida entre fechaInicio y fechaFin.
     * Si no está completada, devuelve la duración programada.
     *
     * @return duración en minutos (mínimo 1 si fue completada)
     */
    public long calcularDuracionReal() {
        if (!completada || fechaFin == null) {
            return duracion.minutos();
        }
        long real = java.time.Duration.between(fechaInicio, fechaFin).toMinutes();
        return Math.max(real, 1);
    }

    /**
     * Obtiene el ID de la tarea asociada.
     *
     * @return tareaId
     */
    public UUID getTareaId() {
        return tareaId;
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
     * Obtiene la duración programada de la sesión.
     *
     * @return duración
     */
    public DuracionMinutos getDuracion() {
        return duracion;
    }

    /**
     * Obtiene el tipo de sesión.
     *
     * @return tipo de sesión
     */
    public TipoSesion getTipoSesion() {
        return tipoSesion;
    }

    /**
     * Obtiene la fecha de inicio de la sesión.
     *
     * @return fecha de inicio
     */
    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    /**
     * Obtiene la fecha de fin de la sesión.
     *
     * @return fecha de fin, o null si está en curso
     */
    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    /**
     * Indica si la sesión fue completada.
     *
     * @return true si fue completada
     */
    public boolean isCompletada() {
        return completada;
    }
}
