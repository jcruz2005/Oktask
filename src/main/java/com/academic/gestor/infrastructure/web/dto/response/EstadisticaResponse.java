package com.academic.gestor.infrastructure.web.dto.response;

import java.util.UUID;

/**
 * DTO de respuesta para estadísticas de materia.
 *
 * @param materiaId ID de la materia
 * @param nombreMateria nombre de la materia
 * @param codigoMateria código de la materia
 * @param horasEstudiadas horas totales estudiadas
 * @param pomodorosCompletados pomodoros completados
 * @param tareasTotales total de tareas
 * @param tareasCompletadas tareas completadas
 * @param porcentajeProgreso porcentaje de progreso
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public record EstadisticaResponse(
        UUID materiaId,
        String nombreMateria,
        String codigoMateria,
        double horasEstudiadas,
        long pomodorosCompletados,
        long tareasTotales,
        long tareasCompletadas,
        double porcentajeProgreso
) {
}
