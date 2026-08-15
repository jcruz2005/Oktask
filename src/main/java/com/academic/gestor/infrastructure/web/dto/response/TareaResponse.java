package com.academic.gestor.infrastructure.web.dto.response;

import com.academic.gestor.domain.model.enums.EstadoTarea;
import com.academic.gestor.domain.model.enums.Prioridad;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de respuesta para una tarea.
 *
 * @param id identificador único
 * @param titulo título de la tarea
 * @param descripcion descripción de la tarea
 * @param materiaId ID de la materia asociada
 * @param nombreMateria nombre de la materia asociada
 * @param fechaLimite fecha límite de entrega
 * @param prioridad prioridad de la tarea
 * @param estado estado actual de la tarea
 * @param fechaCreacion fecha de creación
 * @param fechaCompletado fecha de completado
 * @param minutosPomodoro minutos acumulados de pomodoro
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public record TareaResponse(
        UUID id,
        String titulo,
        String descripcion,
        UUID materiaId,
        String nombreMateria,
        LocalDate fechaLimite,
        Prioridad prioridad,
        EstadoTarea estado,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaCompletado,
        int minutosPomodoro
) {
}
