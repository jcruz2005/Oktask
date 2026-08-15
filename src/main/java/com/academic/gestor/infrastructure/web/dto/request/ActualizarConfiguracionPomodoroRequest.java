package com.academic.gestor.infrastructure.web.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO de entrada para la actualización de la configuración del Pomodoro.
 *
 * @param duracionTrabajo duración de las sesiones de trabajo en minutos
 * @param duracionDescanso duración de los descansos cortos en minutos
 * @param duracionDescansoLargo duración del descanso largo en minutos
 * @param pomodorosParaDescansoLargo cantidad de pomodoros antes de un descanso largo
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public record ActualizarConfiguracionPomodoroRequest(
        @NotNull(message = "La duración de trabajo no puede ser nula")
        @Min(value = 1, message = "La duración de trabajo debe ser al menos 1 minuto")
        Integer duracionTrabajo,

        @NotNull(message = "La duración de descanso no puede ser nula")
        @Min(value = 1, message = "La duración de descanso debe ser al menos 1 minuto")
        Integer duracionDescanso,

        @NotNull(message = "La duración de descanso largo no puede ser nula")
        @Min(value = 1, message = "La duración de descanso largo debe ser al menos 1 minuto")
        Integer duracionDescansoLargo,

        @NotNull(message = "Los pomodoros para descanso largo no pueden ser nulos")
        @Min(value = 1, message = "Los pomodoros para descanso largo deben ser al menos 1")
        Integer pomodorosParaDescansoLargo
) {
}
