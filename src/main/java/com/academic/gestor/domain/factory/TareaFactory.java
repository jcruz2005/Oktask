package com.academic.gestor.domain.factory;

import com.academic.gestor.domain.model.entities.Tarea;
import com.academic.gestor.domain.model.enums.EstadoTarea;
import com.academic.gestor.domain.model.enums.Prioridad;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Factory para la creación de entidades Tarea.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public final class TareaFactory {

    private TareaFactory() {
        throw new AssertionError("No se pueden instanciar factories");
    }

    /**
     * Crea una nueva tarea con los datos proporcionados.
     *
     * @param titulo título de la tarea
     * @param descripcion descripción de la tarea
     * @param materiaIdStr ID de la materia como cadena
     * @param fechaLimiteStr fecha límite como cadena ISO (yyyy-MM-dd)
     * @param prioridad prioridad de la tarea
     * @return nueva instancia de Tarea
     * @throws IllegalArgumentException si algún dato es inválido
     */
    public static Tarea create(final String titulo, final String descripcion,
                               final String materiaIdStr, final String fechaLimiteStr,
                               final Prioridad prioridad) {
        final UUID materiaId = UUID.fromString(materiaIdStr);
        final LocalDate fechaLimite = LocalDate.parse(fechaLimiteStr);
        return Tarea.create(titulo, descripcion, materiaId, fechaLimite, prioridad);
    }

    /**
     * Reconstruye una tarea existente desde sus componentes individuales.
     *
     * @param id ID de la tarea
     * @param titulo título de la tarea
     * @param descripcion descripción de la tarea
     * @param materiaId ID de la materia
     * @param fechaLimite fecha límite
     * @param prioridad prioridad
     * @param estado estado de la tarea
     * @param fechaCreacion fecha de creación
     * @param fechaCompletado fecha de completado (puede ser null)
     * @param minutosPomodoro minutos acumulados de pomodoro
     * @return instancia de Tarea reconstruida
     */
    public static Tarea reconstruct(final UUID id, final String titulo, final String descripcion,
                                    final UUID materiaId, final LocalDate fechaLimite,
                                    final Prioridad prioridad, final EstadoTarea estado,
                                    final LocalDateTime fechaCreacion,
                                    final LocalDateTime fechaCompletado,
                                    final int minutosPomodoro) {
        return new Tarea(id, titulo, descripcion, materiaId, fechaLimite,
                prioridad, estado, fechaCreacion, fechaCompletado, minutosPomodoro);
    }
}
