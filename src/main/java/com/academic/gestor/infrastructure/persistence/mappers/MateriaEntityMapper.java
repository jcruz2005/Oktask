package com.academic.gestor.infrastructure.persistence.mappers;

import com.academic.gestor.domain.model.entities.Materia;
import com.academic.gestor.domain.model.enums.Prioridad;
import com.academic.gestor.domain.model.valueobjects.CodigoMateria;
import com.academic.gestor.domain.model.valueobjects.Color;
import com.academic.gestor.infrastructure.persistence.entities.MateriaEntity;

/**
 * Mapper para convertir entre MateriaEntity (JPA) y Materia (Dominio).
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public final class MateriaEntityMapper {

    private MateriaEntityMapper() {
        throw new AssertionError("No se pueden instanciar mappers");
    }

    /**
     * Convierte una entidad JPA a una entidad de dominio.
     *
     * @param entity entidad JPA
     * @return entidad de dominio
     */
    public static Materia toDomain(final MateriaEntity entity) {
        return new Materia(
                entity.getId(),
                entity.getNombre(),
                new CodigoMateria(entity.getCodigo()),
                new Color(entity.getColor()),
                Prioridad.valueOf(entity.getPrioridad()),
                entity.getFechaCreacion(),
                entity.isActiva()
        );
    }

    /**
     * Convierte una entidad de dominio a una entidad JPA.
     *
     * @param domain entidad de dominio
     * @return entidad JPA
     */
    public static MateriaEntity toEntity(final Materia domain) {
        return new MateriaEntity(
                domain.getId(),
                domain.getNombre(),
                domain.getCodigo().value(),
                domain.getColor().hex(),
                domain.getPrioridad().name(),
                domain.getFechaCreacion(),
                domain.isActiva()
        );
    }
}
