package com.academic.gestor.domain.ports.inbound;

import com.academic.gestor.domain.model.entities.SesionPomodoro;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de entrada para el caso de uso de gestión de sesiones Pomodoro.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public interface SesionPomodoroService {

    /**
     * Inicia una nueva sesión de Pomodoro para una tarea.
     *
     * @param tareaId ID de la tarea
     * @param materiaId ID de la materia
     * @param duracionMinutos duración de la sesión en minutos
     * @return sesión iniciada
     * @throws com.academic.gestor.domain.exceptions.TareaNotFoundException si la tarea no existe
     * @throws com.academic.gestor.domain.exceptions.SesionPomodoroException si hay un error al iniciar
     */
    SesionPomodoro iniciarSesion(UUID tareaId, UUID materiaId, int duracionMinutos);

    /**
     * Finaliza una sesión de Pomodoro en curso.
     *
     * @param sesionId ID de la sesión a finalizar
     * @return sesión finalizada
     * @throws com.academic.gestor.domain.exceptions.SesionPomodoroException si la sesión no existe o ya está completada
     */
    SesionPomodoro finalizarSesion(UUID sesionId);

    /**
     * Cancela una sesión de Pomodoro, guardando el tiempo transcurrido.
     *
     * @param sesionId ID de la sesión a cancelar
     * @param minutosTranscurridos minutos realmente transcurridos
     * @return sesión cancelada (completada=true)
     * @throws com.academic.gestor.domain.exceptions.SesionPomodoroException si la sesión no existe o ya está completada
     */
    SesionPomodoro cancelarSesion(UUID sesionId, long minutosTranscurridos);

    /**
     * Obtiene todas las sesiones de una tarea específica.
     *
     * @param tareaId ID de la tarea
     * @return lista de sesiones de la tarea
     */
    List<SesionPomodoro> obtenerSesionesTarea(UUID tareaId);

    /**
     * Obtiene todas las sesiones de una materia específica.
     *
     * @param materiaId ID de la materia
     * @return lista de sesiones de la materia
     */
    List<SesionPomodoro> obtenerSesionesMateria(UUID materiaId);

    /**
     * Obtiene una sesión por su ID.
     *
     * @param id ID de la sesión
     * @return optional con la sesión encontrada
     */
    Optional<SesionPomodoro> obtenerSesion(UUID id);

    /**
     * Obtiene la configuración activa del Pomodoro.
     *
     * @return configuración activa
     */
    com.academic.gestor.domain.model.entities.ConfiguracionPomodoro obtenerConfiguracion();

    /**
     * Actualiza la configuración activa del Pomodoro.
     *
     * @param duracionTrabajo duración de trabajo en minutos
     * @param duracionDescanso duración de descanso en minutos
     * @param duracionDescansoLargo duración de descanso largo en minutos
     * @param pomodorosParaDescansoLargo cantidad de pomodoros antes del descanso largo
     * @return configuración actualizada
     */
    com.academic.gestor.domain.model.entities.ConfiguracionPomodoro actualizarConfiguracion(
            int duracionTrabajo, int duracionDescanso,
            int duracionDescansoLargo, int pomodorosParaDescansoLargo);
}
