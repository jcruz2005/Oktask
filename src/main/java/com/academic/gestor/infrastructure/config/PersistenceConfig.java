package com.academic.gestor.infrastructure.config;

import com.academic.gestor.domain.model.entities.ConfiguracionPomodoro;
import com.academic.gestor.domain.ports.outbound.ConfiguracionPomodoroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de persistencia e inicialización de datos.
 *
 * <p>Se encarga de inicializar datos por defecto al arrancar la aplicación.</p>
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
@Configuration
public class PersistenceConfig {

    private static final Logger log = LoggerFactory.getLogger(PersistenceConfig.class);

    /**
     * Crea un runner que inicializa datos por defecto al arrancar la aplicación.
     *
     * @param configuracionRepository repositorio de configuración Pomodoro
     * @return CommandLineRunner
     */
    @Bean
    public CommandLineRunner initData(final ConfiguracionPomodoroRepository configuracionRepository) {
        return args -> {
            log.info("Verificando configuración inicial del Pomodoro...");

            configuracionRepository.findConfiguracionActiva()
                    .ifPresentOrElse(
                            config -> log.info("Configuración de Pomodoro ya existe: {}", config.getId()),
                            () -> {
                                log.info("Creando configuración por defecto del Pomodoro...");
                                final ConfiguracionPomodoro defaultConfig = ConfiguracionPomodoro.createDefault();
                                configuracionRepository.save(defaultConfig);
                                log.info("Configuración por defecto creada: {}", defaultConfig.getId());
                            }
                    );
        };
    }
}
