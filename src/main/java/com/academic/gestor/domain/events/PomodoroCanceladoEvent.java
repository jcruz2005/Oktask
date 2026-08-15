package com.academic.gestor.domain.events;

import com.academic.gestor.domain.model.enums.TipoSesion;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Evento de dominio que se publica cuando una sesión de Pomodoro es cancelada.
 *
 * @param sesionId ID de la sesión cancelada
 * @param tareaId ID de la tarea asociada
 * @param materiaId ID de la materia asociada
 * @param tipoSesion tipo de sesión cancelada
 * @param fechaInicio fecha y hora de inicio de la sesión
 * @param fechaFin fecha y hora de fin de la sesión (calculada)
 * @param minutosTranscurridos minutos realmente transcurridos antes de cancelar
 * @author Gestor de Tareas Académicas
 * @since 1.1.0
 */
public record PomodoroCanceladoEvent(
        UUID sesionId,
        UUID tareaId,
        UUID materiaId,
        TipoSesion tipoSesion,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        long minutosTranscurridos
) {
}
