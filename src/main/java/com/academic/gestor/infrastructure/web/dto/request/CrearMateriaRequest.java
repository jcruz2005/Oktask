package com.academic.gestor.infrastructure.web.dto.request;

import com.academic.gestor.domain.model.enums.Prioridad;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para la creación de una materia.
 *
 * @param nombre nombre de la materia
 * @param codigo código único de la materia
 * @param color color hexadecimal de la materia
 * @param prioridad prioridad de la materia
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public record CrearMateriaRequest(
        @NotBlank(message = "El nombre de la materia no puede estar vacío")
        String nombre,

        @NotBlank(message = "El código de la materia no puede estar vacío")
        String codigo,

        @NotBlank(message = "El color de la materia no puede estar vacío")
        String color,

        @NotNull(message = "La prioridad no puede ser nula")
        Prioridad prioridad
) {
}
