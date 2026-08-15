package com.academic.gestor.domain.factory;

import com.academic.gestor.domain.model.entities.SesionPomodoro;
import com.academic.gestor.domain.model.enums.TipoSesion;
import com.academic.gestor.domain.model.valueobjects.DuracionMinutos;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Factory para la creación de entidades SesionPomodoro.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public final class SesionPomodoroFactory {

    private SesionPomodoroFactory() {
        throw new AssertionError("No se pueden instanciar factories");
    }

    /**
     * Crea una nueva sesión de Pomodoro.
     *
     * @param tareaIdStr ID de la tarea como cadena
     * @param materiaIdStr ID de la materia como cadena
     * @param duracionMinutos duración en minutos
     * @param tipoSesionStr tipo de sesión como cadena
     * @return nueva instancia de SesionPomodoro
     * @throws IllegalArgumentException si algún dato es inválido
     */
    public static SesionPomodoro create(final String tareaIdStr, final String materiaIdStr,
                                        final int duracionMinutos, final String tipoSesionStr) {
        final UUID tareaId = UUID.fromString(tareaIdStr);
        final UUID materiaId = UUID.fromString(materiaIdStr);
        final DuracionMinutos duracion = DuracionMinutos.of(duracionMinutos);
        final TipoSesion tipoSesion = TipoSesion.valueOf(tipoSesionStr);
        return SesionPomodoro.iniciar(tareaId, materiaId, duracion, tipoSesion);
    }

    /**
     * Reconstruye una sesión de Pomodoro existente desde sus componentes.
     *
     * @param id ID de la sesión
     * @param tareaId ID de la tarea
     * @param materiaId ID de la materia
     * @param duracionMinutos duración en minutos
     * @param tipoSesion tipo de sesión
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin (puede ser null)
     * @param completada si está completada
     * @return instancia de SesionPomodoro reconstruida
     */
    public static SesionPomodoro reconstruct(final UUID id, final UUID tareaId,
                                             final UUID materiaId, final int duracionMinutos,
                                             final TipoSesion tipoSesion,
                                             final LocalDateTime fechaInicio,
                                             final LocalDateTime fechaFin,
                                             final boolean completada) {
        return new SesionPomodoro(id, tareaId, materiaId,
                DuracionMinutos.of(duracionMinutos), tipoSesion,
                fechaInicio, fechaFin, completada);
    }
}
