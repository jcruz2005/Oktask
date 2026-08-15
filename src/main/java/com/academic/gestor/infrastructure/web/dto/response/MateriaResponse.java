package com.academic.gestor.infrastructure.web.dto.response;

import com.academic.gestor.domain.model.enums.Prioridad;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de respuesta para una materia.
 *
 * @param id identificador único
 * @param nombre nombre de la materia
 * @param codigo código de la materia
 * @param color color hexadecimal
 * @param prioridad prioridad de la materia
 * @param fechaCreacion fecha de creación
 * @param activa si la materia está activa
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public record MateriaResponse(
        UUID id,
        String nombre,
        String codigo,
        String color,
        Prioridad prioridad,
        LocalDateTime fechaCreacion,
        boolean activa
) {
}
