package com.academic.gestor.infrastructure.web.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * DTO de entrada para la creación de una sesión de Pomodoro.
 *
 * @param tareaId ID de la tarea asociada
 * @param materiaId ID de la materia asociada
 * @param duracionMinutos duración de la sesión en minutos
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public record CrearSesionPomodoroRequest(
        @NotNull(message = "El ID de la tarea no puede ser nulo")
        UUID tareaId,

        @NotNull(message = "El ID de la materia no puede ser nulo")
        UUID materiaId,

        @NotNull(message = "La duración no puede ser nula")
        @Min(value = 1, message = "La duración debe ser al menos 1 minuto")
        Integer duracionMinutos
) {
}
