package com.academic.gestor.infrastructure.config;

import com.academic.gestor.domain.ports.inbound.EstadisticaService;
import com.academic.gestor.domain.ports.inbound.MateriaService;
import com.academic.gestor.domain.ports.inbound.SesionPomodoroService;
import com.academic.gestor.domain.ports.inbound.TareaService;
import com.academic.gestor.domain.ports.outbound.ConfiguracionPomodoroRepository;
import com.academic.gestor.domain.ports.outbound.MateriaRepository;
import com.academic.gestor.domain.ports.outbound.SesionPomodoroRepository;
import com.academic.gestor.domain.ports.outbound.TareaRepository;
import com.academic.gestor.application.services.EstadisticaServiceImpl;
import com.academic.gestor.application.services.MateriaServiceImpl;
import com.academic.gestor.application.services.SesionPomodoroServiceImpl;
import com.academic.gestor.application.services.TareaServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de servicios de la aplicación.
 *
 * <p>Registra los beans de los servicios de caso de uso,
     estableciendo las dependencias con los repositorios.</p>
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
@Configuration
public class ServiceConfig {

    /**
     * Crea el bean del servicio de materias.
     *
     * @param materiaRepository repositorio de materias
     * @return servicio de materias
     */
    @Bean
    public MateriaService materiaService(final MateriaRepository materiaRepository) {
        return new MateriaServiceImpl(materiaRepository);
    }

    /**
     * Crea el bean del servicio de tareas.
     *
     * @param tareaRepository repositorio de tareas
     * @param materiaRepository repositorio de materias (para validación)
     * @return servicio de tareas
     */
    @Bean
    public TareaService tareaService(final TareaRepository tareaRepository,
                                     final MateriaRepository materiaRepository) {
        return new TareaServiceImpl(tareaRepository, materiaRepository);
    }

    /**
     * Crea el bean del servicio de sesiones Pomodoro.
     *
     * @param sesionRepository repositorio de sesiones
     * @param configuracionRepository repositorio de configuración
     * @param tareaRepository repositorio de tareas
     * @return servicio de sesiones Pomodoro
     */
    @Bean
    public SesionPomodoroService sesionPomodoroService(
            final SesionPomodoroRepository sesionRepository,
            final ConfiguracionPomodoroRepository configuracionRepository,
            final TareaRepository tareaRepository) {
        return new SesionPomodoroServiceImpl(sesionRepository, configuracionRepository, tareaRepository);
    }

    /**
     * Crea el bean del servicio de estadísticas.
     *
     * @param materiaRepository repositorio de materias
     * @param tareaRepository repositorio de tareas
     * @param sesionRepository repositorio de sesiones
     * @return servicio de estadísticas
     */
    @Bean
    public EstadisticaService estadisticaService(
            final MateriaRepository materiaRepository,
            final TareaRepository tareaRepository,
            final SesionPomodoroRepository sesionRepository) {
        return new EstadisticaServiceImpl(materiaRepository, tareaRepository, sesionRepository);
    }
}
