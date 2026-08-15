package com.academic.gestor.infrastructure.web.dto.request;

import com.academic.gestor.domain.model.enums.Prioridad;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO de entrada para la creación de una tarea.
 *
 * @param titulo título de la tarea
 * @param descripcion descripción de la tarea
 * @param materiaId ID de la materia asociada
 * @param fechaLimite fecha límite de entrega
 * @param prioridad prioridad de la tarea
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public record CrearTareaRequest(
        @NotBlank(message = "El título de la tarea no puede estar vacío")
        String titulo,

        String descripcion,

        @NotNull(message = "El ID de la materia no puede ser nulo")
        UUID materiaId,

        @NotNull(message = "La fecha límite no puede ser nula")
        LocalDate fechaLimite,

        @NotNull(message = "La prioridad no puede ser nula")
        Prioridad prioridad
) {
}
