package com.academic.gestor.domain.ports.outbound;

import com.academic.gestor.domain.model.entities.ConfiguracionPomodoro;

import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para la persistencia de configuración de Pomodoro.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public interface ConfiguracionPomodoroRepository {

    /**
     * Busca la configuración activa del Pomodoro.
     *
     * @return optional con la configuración activa
     */
    Optional<ConfiguracionPomodoro> findConfiguracionActiva();

    /**
     * Busca una configuración por su ID.
     *
     * @param id identificador de la configuración
     * @return optional con la configuración encontrada
     */
    Optional<ConfiguracionPomodoro> findById(UUID id);

    /**
     * Guarda una configuración de Pomodoro.
     *
     * @param configuracion configuración a guardar
     * @return configuración guardada
     */
    ConfiguracionPomodoro save(ConfiguracionPomodoro configuracion);
}
