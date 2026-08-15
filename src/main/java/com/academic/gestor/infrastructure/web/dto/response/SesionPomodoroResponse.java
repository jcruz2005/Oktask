package com.academic.gestor.infrastructure.web.dto.response;

import com.academic.gestor.domain.model.enums.TipoSesion;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de respuesta para una sesión de Pomodoro.
 *
 * @param id identificador único
 * @param tareaId ID de la tarea asociada
 * @param tituloTarea título de la tarea asociada
 * @param materiaId ID de la materia asociada
 * @param nombreMateria nombre de la materia asociada
 * @param duracionMinutos duración en minutos
 * @param tipoSesion tipo de sesión
 * @param fechaInicio fecha de inicio
 * @param fechaFin fecha de fin
 * @param completada si la sesión fue completada
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public record SesionPomodoroResponse(
        UUID id,
        UUID tareaId,
        String tituloTarea,
        UUID materiaId,
        String nombreMateria,
        int duracionMinutos,
        TipoSesion tipoSesion,
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        boolean completada
) {
}
