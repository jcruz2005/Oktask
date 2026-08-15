package com.academic.gestor.infrastructure.web.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para la cancelación de una sesión de Pomodoro.
 *
 * @param minutosTranscurridos minutos realmente transcurridos antes de cancelar
 * @author Gestor de Tareas Académicas
 * @since 1.1.0
 */
public record CancelarSesionPomodoroRequest(
        @NotNull(message = "Los minutos transcurridos no pueden ser nulos")
        @Min(value = 0, message = "Los minutos transcurridos no pueden ser negativos")
        Long minutosTranscurridos
) {
}
