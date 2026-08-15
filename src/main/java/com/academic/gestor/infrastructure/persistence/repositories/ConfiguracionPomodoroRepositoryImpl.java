package com.academic.gestor.infrastructure.persistence.repositories;

import com.academic.gestor.domain.model.entities.ConfiguracionPomodoro;
import com.academic.gestor.domain.ports.outbound.ConfiguracionPomodoroRepository;
import com.academic.gestor.infrastructure.persistence.entities.ConfiguracionPomodoroEntity;
import com.academic.gestor.infrastructure.persistence.jpa.JpaConfiguracionPomodoroRepository;
import com.academic.gestor.infrastructure.persistence.mappers.ConfiguracionPomodoroEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementación del repositorio de configuración de Pomodoro usando JPA.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
@Repository
public class ConfiguracionPomodoroRepositoryImpl implements ConfiguracionPomodoroRepository {

    private final JpaConfiguracionPomodoroRepository jpaRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param jpaRepository repositorio JPA de Spring Data
     */
    public ConfiguracionPomodoroRepositoryImpl(final JpaConfiguracionPomodoroRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<ConfiguracionPomodoro> findConfiguracionActiva() {
        return jpaRepository.findByActivaTrue()
                .map(ConfiguracionPomodoroEntityMapper::toDomain);
    }

    @Override
    public Optional<ConfiguracionPomodoro> findById(final UUID id) {
        return jpaRepository.findById(id)
                .map(ConfiguracionPomodoroEntityMapper::toDomain);
    }

    @Override
    public ConfiguracionPomodoro save(final ConfiguracionPomodoro configuracion) {
        final ConfiguracionPomodoroEntity entity = ConfiguracionPomodoroEntityMapper.toEntity(configuracion);
        final ConfiguracionPomodoroEntity saved = jpaRepository.save(entity);
        return ConfiguracionPomodoroEntityMapper.toDomain(saved);
    }
}
