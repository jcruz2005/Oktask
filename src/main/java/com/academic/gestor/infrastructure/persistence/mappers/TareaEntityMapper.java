package com.academic.gestor.infrastructure.persistence.mappers;

import com.academic.gestor.domain.model.entities.Tarea;
import com.academic.gestor.domain.model.enums.EstadoTarea;
import com.academic.gestor.domain.model.enums.Prioridad;
import com.academic.gestor.infrastructure.persistence.entities.TareaEntity;

/**
 * Mapper para convertir entre TareaEntity (JPA) y Tarea (Dominio).
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public final class TareaEntityMapper {

    private TareaEntityMapper() {
        throw new AssertionError("No se pueden instanciar mappers");
    }

    /**
     * Convierte una entidad JPA a una entidad de dominio.
     *
     * @param entity entidad JPA
     * @return entidad de dominio
     */
    public static Tarea toDomain(final TareaEntity entity) {
        return new Tarea(
                entity.getId(),
                entity.getTitulo(),
                entity.getDescripcion(),
                entity.getMateriaId(),
                entity.getFechaLimite(),
                Prioridad.valueOf(entity.getPrioridad()),
                EstadoTarea.valueOf(entity.getEstado()),
                entity.getFechaCreacion(),
                entity.getFechaCompletado(),
                entity.getMinutosPomodoro()
        );
    }

    /**
     * Convierte una entidad de dominio a una entidad JPA.
     *
     * @param domain entidad de dominio
     * @return entidad JPA
     */
    public static TareaEntity toEntity(final Tarea domain) {
        return new TareaEntity(
                domain.getId(),
                domain.getTitulo(),
                domain.getDescripcion(),
                domain.getMateriaId(),
                domain.getFechaLimite(),
                domain.getPrioridad().name(),
                domain.getEstado().name(),
                domain.getFechaCreacion(),
                domain.getFechaCompletado(),
                domain.getMinutosPomodoro()
        );
    }
}
