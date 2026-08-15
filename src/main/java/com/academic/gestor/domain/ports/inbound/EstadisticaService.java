package com.academic.gestor.domain.ports.inbound;

import com.academic.gestor.application.dto.EstadisticaMateriaDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Puerto de entrada para el caso de uso de estadísticas y análisis de horas.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public interface EstadisticaService {

    /**
     * Obtiene las horas totales estudiadas por materia.
     *
     * @return lista de estadísticas por materia
     */
    List<EstadisticaMateriaDTO> obtenerHorasPorMateria();

    /**
     * Obtiene las horas estudiadas en un período específico.
     *
     * @param fechaInicio fecha de inicio del período
     * @param fechaFin fecha de fin del período
     * @return lista de estadísticas por materia en el período
     */
    List<EstadisticaMateriaDTO> obtenerHorasPorPeriodo(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Obtiene el progreso de tareas (completadas vs pendientes) por materia.
     *
     * @return lista de estadísticas de progreso por materia
     */
    List<EstadisticaMateriaDTO> obtenerProgresoTareas();

    /**
     * Obtiene las horas totales estudiadas en un período.
     *
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return total de horas estudiadas
     */
    double obtenerTotalHorasPeriodo(LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Obtiene el total de pomodoros completados en un período.
     *
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return cantidad de pomodoros completados
     */
    long obtenerTotalPomodorosPeriodo(LocalDate fechaInicio, LocalDate fechaFin);
}
