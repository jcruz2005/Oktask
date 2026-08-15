package com.academic.gestor.infrastructure.persistence.jpa;

import com.academic.gestor.infrastructure.persistence.entities.ConfiguracionPomodoroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad ConfiguracionPomodoroEntity.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
@Repository
public interface JpaConfiguracionPomodoroRepository extends JpaRepository<ConfiguracionPomodoroEntity, UUID> {

    /**
     * Busca la configuración activa del Pomodoro.
     *
     * @return optional con la configuración activa
     */
    Optional<ConfiguracionPomodoroEntity> findByActivaTrue();
}
