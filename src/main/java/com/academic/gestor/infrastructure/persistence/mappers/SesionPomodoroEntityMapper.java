package com.academic.gestor.infrastructure.persistence.mappers;

import com.academic.gestor.domain.model.entities.SesionPomodoro;
import com.academic.gestor.domain.model.enums.TipoSesion;
import com.academic.gestor.domain.model.valueobjects.DuracionMinutos;
import com.academic.gestor.infrastructure.persistence.entities.SesionPomodoroEntity;

/**
 * Mapper para convertir entre SesionPomodoroEntity (JPA) y SesionPomodoro (Dominio).
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public final class SesionPomodoroEntityMapper {

    private SesionPomodoroEntityMapper() {
        throw new AssertionError("No se pueden instanciar mappers");
    }

    /**
     * Convierte una entidad JPA a una entidad de dominio.
     *
     * @param entity entidad JPA
     * @return entidad de dominio
     */
    public static SesionPomodoro toDomain(final SesionPomodoroEntity entity) {
        return new SesionPomodoro(
                entity.getId(),
                entity.getTareaId(),
                entity.getMateriaId(),
                DuracionMinutos.of(entity.getDuracionMinutos()),
                TipoSesion.valueOf(entity.getTipoSesion()),
                entity.getFechaInicio(),
                entity.getFechaFin(),
                entity.isCompletada()
        );
    }

    /**
     * Convierte una entidad de dominio a una entidad JPA.
     *
     * @param domain entidad de dominio
     * @return entidad JPA
     */
    public static SesionPomodoroEntity toEntity(final SesionPomodoro domain) {
        return new SesionPomodoroEntity(
                domain.getId(),
                domain.getTareaId(),
                domain.getMateriaId(),
                domain.getDuracion().minutos(),
                domain.getTipoSesion().name(),
                domain.getFechaInicio(),
                domain.getFechaFin(),
                domain.isCompletada()
        );
    }
}
