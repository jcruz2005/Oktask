package com.academic.gestor.domain.events;

import com.academic.gestor.domain.model.enums.TipoSesion;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de dominio que se publica cuando una sesión de Pomodoro es completada.
 *
 * @param sesionId ID de la sesión completada
 * @param tareaId ID de la tarea asociada
 * @param materiaId ID de la materia asociada
 * @param tipoSesion tipo de sesión completada
 * @param fechaInicio fecha y hora de inicio de la sesión
 * @param fechaFin fecha y hora de fin de la sesión
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public record PomodoroCompletadoEvent(
        UUID sesionId,
        UUID tareaId,
        UUID materiaId,
        TipoSesion tipoSesion,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin
) {
}
