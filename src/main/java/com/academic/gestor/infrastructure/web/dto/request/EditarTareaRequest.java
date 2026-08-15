package com.academic.gestor.infrastructure.web.dto.request;

import com.academic.gestor.domain.model.enums.EstadoTarea;
import com.academic.gestor.domain.model.enums.Prioridad;

import java.time.LocalDate;

/**
 * DTO de entrada para la edición de una tarea.
 *
 * @param titulo nuevo título (puede ser null para no cambiar)
 * @param descripcion nueva descripción (puede ser null para no cambiar)
 * @param fechaLimite nueva fecha límite (puede ser null para no cambiar)
 * @param prioridad nueva prioridad (puede ser null para no cambiar)
 * @param estado nuevo estado (puede ser null para no cambiar)
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public record EditarTareaRequest(
        String titulo,
        String descripcion,
        LocalDate fechaLimite,
        Prioridad prioridad,
        EstadoTarea estado
) {
}
