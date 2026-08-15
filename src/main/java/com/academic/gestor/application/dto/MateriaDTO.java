package com.academic.gestor.application.dto;

import com.academic.gestor.domain.model.enums.Prioridad;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de salida para representar una materia.
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
public record MateriaDTO(
        UUID id,
        String nombre,
        String codigo,
        String color,
        Prioridad prioridad,
        LocalDateTime fechaCreacion,
        boolean activa
) {
}
