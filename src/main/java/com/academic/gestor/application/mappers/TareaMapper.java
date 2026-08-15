package com.academic.gestor.application.mappers;

import com.academic.gestor.application.dto.TareaDTO;
import com.academic.gestor.domain.model.entities.Tarea;

/**
 * Mapper para convertir entre la entidad Tarea y su DTO.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public final class TareaMapper {

    private TareaMapper() {
        throw new AssertionError("No se pueden instanciar mappers");
    }

    /**
     * Convierte una entidad Tarea a su DTO de salida.
     *
     * @param tarea entidad a convertir
     * @return DTO correspondiente
     */
    public static TareaDTO toDTO(final Tarea tarea) {
        return new TareaDTO(
                tarea.getId(),
                tarea.getTitulo(),
                tarea.getDescripcion(),
                tarea.getMateriaId(),
                null, // El nombre de materia se enriquece en el servicio
                tarea.getFechaLimite(),
                tarea.getPrioridad(),
                tarea.getEstado(),
                tarea.getFechaCreacion(),
                tarea.getFechaCompletado(),
                tarea.getMinutosPomodoro()
        );
    }
}
