package com.academic.gestor.infrastructure.web.dto.request;

import com.academic.gestor.domain.model.enums.Prioridad;

/**
 * DTO de entrada para la edición de una materia.
 *
 * @param nombre nuevo nombre (puede ser null para no cambiar)
 * @param color nuevo color hexadecimal (puede ser null para no cambiar)
 * @param prioridad nueva prioridad (puede ser null para no cambiar)
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public record EditarMateriaRequest(
        String nombre,
        String color,
        Prioridad prioridad
) {
}
