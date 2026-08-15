package com.academic.gestor.application.mappers;

import com.academic.gestor.application.dto.SesionPomodoroDTO;
import com.academic.gestor.domain.model.entities.SesionPomodoro;

/**
 * Mapper para convertir entre la entidad SesionPomodoro y su DTO.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public final class SesionPomodoroMapper {

    private SesionPomodoroMapper() {
        throw new AssertionError("No se pueden instanciar mappers");
    }

    /**
     * Convierte una entidad SesionPomodoro a su DTO de salida.
     *
     * @param sesion entidad a convertir
     * @return DTO correspondiente
     */
    public static SesionPomodoroDTO toDTO(final SesionPomodoro sesion) {
        return new SesionPomodoroDTO(
                sesion.getId(),
                sesion.getTareaId(),
                null, // El título de tarea se enriquece en el servicio
                sesion.getMateriaId(),
                null, // El nombre de materia se enriquece en el servicio
                sesion.getDuracion().minutos(),
                sesion.getTipoSesion(),
                sesion.getFechaInicio(),
                sesion.getFechaFin(),
                sesion.isCompletada()
        );
    }
}
