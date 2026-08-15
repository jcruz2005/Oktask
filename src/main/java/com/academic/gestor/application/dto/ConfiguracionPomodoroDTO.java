package com.academic.gestor.application.dto;

import java.util.UUID;

/**
 * DTO de salida para representar la configuración del Pomodoro.
 *
 * @param id identificador único
 * @param duracionTrabajo duración de trabajo en minutos
 * @param duracionDescanso duración de descanso en minutos
 * @param duracionDescansoLargo duración de descanso largo en minutos
 * @param pomodorosParaDescansoLargo cantidad de pomodoros para descanso largo
 * @param activa si esta es la configuración activa
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public record ConfiguracionPomodoroDTO(
        UUID id,
        int duracionTrabajo,
        int duracionDescanso,
        int duracionDescansoLargo,
        int pomodorosParaDescansoLargo,
        boolean activa
) {
}
