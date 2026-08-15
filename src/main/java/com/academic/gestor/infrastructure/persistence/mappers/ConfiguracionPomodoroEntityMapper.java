package com.academic.gestor.infrastructure.persistence.mappers;

import com.academic.gestor.domain.model.entities.ConfiguracionPomodoro;
import com.academic.gestor.domain.model.valueobjects.DuracionMinutos;
import com.academic.gestor.infrastructure.persistence.entities.ConfiguracionPomodoroEntity;

/**
 * Mapper para convertir entre ConfiguracionPomodoroEntity (JPA) y ConfiguracionPomodoro (Dominio).
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public final class ConfiguracionPomodoroEntityMapper {

    private ConfiguracionPomodoroEntityMapper() {
        throw new AssertionError("No se pueden instanciar mappers");
    }

    /**
     * Convierte una entidad JPA a una entidad de dominio.
     *
     * @param entity entidad JPA
     * @return entidad de dominio
     */
    public static ConfiguracionPomodoro toDomain(final ConfiguracionPomodoroEntity entity) {
        return new ConfiguracionPomodoro(
                entity.getId(),
                DuracionMinutos.of(entity.getDuracionTrabajo()),
                DuracionMinutos.of(entity.getDuracionDescanso()),
                DuracionMinutos.of(entity.getDuracionDescansoLargo()),
                entity.getPomodorosParaDescansoLargo(),
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
    public static ConfiguracionPomodoroEntity toEntity(final ConfiguracionPomodoro domain) {
        return new ConfiguracionPomodoroEntity(
                domain.getId(),
                domain.getDuracionTrabajo().minutos(),
                domain.getDuracionDescanso().minutos(),
                domain.getDuracionDescansoLargo().minutos(),
                domain.getPomodorosParaDescansoLargo(),
                domain.getFechaCreacion(),
                domain.isActiva()
        );
    }
}
