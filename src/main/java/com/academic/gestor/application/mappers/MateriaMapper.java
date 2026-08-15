package com.academic.gestor.application.mappers;

import com.academic.gestor.application.dto.MateriaDTO;
import com.academic.gestor.domain.model.entities.Materia;

/**
 * Mapper para convertir entre la entidad Materia y su DTO.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public final class MateriaMapper {

    private MateriaMapper() {
        throw new AssertionError("No se pueden instanciar mappers");
    }

    /**
     * Convierte una entidad Materia a su DTO de salida.
     *
     * @param materia entidad a convertir
     * @return DTO correspondiente
     */
    public static MateriaDTO toDTO(final Materia materia) {
        return new MateriaDTO(
                materia.getId(),
                materia.getNombre(),
                materia.getCodigo().value(),
                materia.getColor().hex(),
                materia.getPrioridad(),
                materia.getFechaCreacion(),
                materia.isActiva()
        );
    }
}
