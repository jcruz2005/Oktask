package com.academic.gestor.infrastructure.web.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de respuesta para la configuración del Pomodoro.
 *
 * <p>Evita exponer la entidad de dominio directamente en la capa web.
 * El frontend acepta tanto el valor entero como el objeto
 * {@code { "minutos": N }} para cada duración.</p>
 *
 * @param id identificador único
 * @param duracionTrabajo duración de trabajo en minutos
 * @param duracionDescanso duración de descanso en minutos
 * @param duracionDescansoLargo duración de descanso largo en minutos
 * @param pomodorosParaDescansoLargo pomodoros antes del descanso largo
 * @param fechaCreacion fecha de creación
 * @param activa si es la configuración activa
 * @author OKtask
 * @since 1.2.0
 */
public record ConfiguracionPomodoroResponse(
        UUID id,
        int duracionTrabajo,
        int duracionDescanso,
        int duracionDescansoLargo,
        int pomodorosParaDescansoLargo,
        LocalDateTime fechaCreacion,
        boolean activa
) {
}