package com.academic.gestor.domain.ports.outbound;

import com.academic.gestor.domain.model.entities.SesionPomodoro;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para la persistencia de sesiones de Pomodoro.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public interface SesionPomodoroRepository {

    /**
     * Busca una sesión por su ID.
     *
     * @param id identificador de la sesión
     * @return optional con la sesión encontrada
     */
    Optional<SesionPomodoro> findById(UUID id);

    /**
     * Obtiene todas las sesiones de una tarea específica.
     *
     * @param tareaId ID de la tarea
     * @return lista de sesiones de la tarea
     */
    List<SesionPomodoro> findByTareaId(UUID tareaId);

    /**
     * Obtiene todas las sesiones de una materia específica.
     *
     * @param materiaId ID de la materia
     * @return lista de sesiones de la materia
     */
    List<SesionPomodoro> findByMateriaId(UUID materiaId);

    /**
     * Obtiene las sesiones en un rango de fechas.
     *
     * @param inicio fecha y hora de inicio del rango
     * @param fin fecha y hora de fin del rango
     * @return lista de sesiones en el rango
     */
    List<SesionPomodoro> findByFechaInRange(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Obtiene las sesiones completadas en un rango de fechas.
     *
     * @param inicio fecha y hora de inicio del rango
     * @param fin fecha y hora de fin del rango
     * @return lista de sesiones completadas en el rango
     */
    List<SesionPomodoro> findCompletadasByFechaInRange(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Guarda una sesión de Pomodoro.
     *
     * @param sesion sesión a guardar
     * @return sesión guardada
     */
    SesionPomodoro save(SesionPomodoro sesion);

    /**
     * Suma la duración total de sesiones completadas de trabajo para una materia.
     *
     * @param materiaId ID de la materia
     * @return total de minutos trabajados
     */
    long sumDuracionByMateriaId(UUID materiaId);

    /**
     * Suma la duración total de sesiones completadas de trabajo en un rango de fechas.
     *
     * @param inicio fecha de inicio
     * @param fin fecha de fin
     * @return total de minutos trabajados
     */
    long sumDuracionByFechaInRange(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Suma la duración de sesiones de trabajo para una materia en un rango de fechas.
     *
     * @param materiaId ID de la materia
     * @param inicio fecha de inicio
     * @param fin fecha de fin
     * @return total de minutos trabajados
     */
    long sumDuracionByMateriaIdAndFechaInRange(UUID materiaId, LocalDateTime inicio, LocalDateTime fin);
}
