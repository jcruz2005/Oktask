package com.academic.gestor.domain.events;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de dominio que se publica cuando una tarea es completada.
 *
 * @param tareaId ID de la tarea completada
 * @param materiaId ID de la materia asociada
 * @param fechaCompletado fecha y hora en que se completó
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public record TareaCompletadaEvent(
        UUID tareaId,
        UUID materiaId,
        LocalDateTime fechaCompletado
) {
}
