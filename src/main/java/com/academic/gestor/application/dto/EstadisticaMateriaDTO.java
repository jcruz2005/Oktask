package com.academic.gestor.application.dto;

import java.util.UUID;

/**
 * DTO de salida para estadísticas de una materia.
 *
 * @param materiaId ID de la materia
 * @param nombreMateria nombre de la materia
 * @param codigoMateria código de la materia
 * @param horasEstudiadas total de horas estudiadas
 * @param pomodorosCompletados total de pomodoros completados
 * @param tareasTotales total de tareas de la materia
 * @param tareasCompletadas tareas completadas
 * @param porcentajeProgreso porcentaje de progreso (0-100)
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public record EstadisticaMateriaDTO(
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
