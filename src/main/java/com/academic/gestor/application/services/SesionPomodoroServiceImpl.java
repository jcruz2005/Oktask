package com.academic.gestor.application.services;

import com.academic.gestor.domain.exceptions.ConfiguracionInvalidaException;
import com.academic.gestor.domain.exceptions.SesionPomodoroException;
import com.academic.gestor.domain.exceptions.TareaNotFoundException;
import com.academic.gestor.domain.model.entities.ConfiguracionPomodoro;
import com.academic.gestor.domain.model.entities.SesionPomodoro;
import com.academic.gestor.domain.model.entities.Tarea;
import com.academic.gestor.domain.model.enums.TipoSesion;
import com.academic.gestor.domain.model.valueobjects.DuracionMinutos;
import com.academic.gestor.domain.ports.inbound.SesionPomodoroService;
import com.academic.gestor.domain.ports.outbound.ConfiguracionPomodoroRepository;
import com.academic.gestor.domain.ports.outbound.SesionPomodoroRepository;
import com.academic.gestor.domain.ports.outbound.TareaRepository;
import com.academic.gestor.notification.NativeNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementación del servicio de gestión de sesiones de Pomodoro.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public class SesionPomodoroServiceImpl implements SesionPomodoroService {

    private static final Logger log = LoggerFactory.getLogger(SesionPomodoroServiceImpl.class);

    private final SesionPomodoroRepository sesionRepository;
    private final ConfiguracionPomodoroRepository configuracionRepository;
    private final TareaRepository tareaRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param sesionRepository repositorio de sesiones
     * @param configuracionRepository repositorio de configuración
     * @param tareaRepository repositorio de tareas
     */
    public SesionPomodoroServiceImpl(final SesionPomodoroRepository sesionRepository,
                                     final ConfiguracionPomodoroRepository configuracionRepository,
                                     final TareaRepository tareaRepository) {
        this.sesionRepository = sesionRepository;
        this.configuracionRepository = configuracionRepository;
        this.tareaRepository = tareaRepository;
    }

    @Override
    public SesionPomodoro iniciarSesion(final UUID tareaId, final UUID materiaId,
                                         final int duracionMinutos) {
        log.info("Iniciando sesión de Pomodoro para tarea {} con {} minutos",
                tareaId, duracionMinutos);

        if (duracionMinutos <= 0) {
            throw new ConfiguracionInvalidaException(
                    "La duración de la sesión debe ser mayor a 0 minutos"
            );
        }

        // Validar que la tarea exista antes de iniciar la sesión
        if (!tareaRepository.existsById(tareaId)) {
            throw new TareaNotFoundException("Tarea no encontrada: " + tareaId);
        }

        // Evitar sesiones concurrentes sobre la misma tarea
        final boolean sesionActiva = sesionRepository.findByTareaId(tareaId).stream()
                .anyMatch(s -> !s.isCompletada());
        if (sesionActiva) {
            throw new SesionPomodoroException(
                    "Ya existe una sesión de Pomodoro en curso para esta tarea"
            );
        }

        final DuracionMinutos duracion = DuracionMinutos.of(duracionMinutos);
        final SesionPomodoro sesion = SesionPomodoro.iniciar(
                tareaId, materiaId, duracion, TipoSesion.TRABAJO
        );

        final SesionPomodoro guardada = sesionRepository.save(sesion);
        log.info("Sesión de Pomodoro iniciada con ID: {}", guardada.getId());
        return guardada;
    }

    @Override
    public SesionPomodoro finalizarSesion(final UUID sesionId) {
        log.info("Finalizando sesión de Pomodoro: {}", sesionId);

        final SesionPomodoro sesion = sesionRepository.findById(sesionId)
                .orElseThrow(() -> new SesionPomodoroException(
                        "Sesión de Pomodoro no encontrada: " + sesionId
                ));

        sesion.completar();
        final SesionPomodoro guardada = sesionRepository.save(sesion);

        // Actualizar minutos de pomodoro en la tarea y notificar
        if (sesion.getTipoSesion() == TipoSesion.TRABAJO) {
            tareaRepository.findById(sesion.getTareaId()).ifPresent(tarea -> {
                tarea.agregarMinutosPomodoro((int) sesion.calcularDuracionReal());
                tareaRepository.save(tarea);
                log.info("Actualizados {} minutos pomodoro en tarea {}",
                        sesion.calcularDuracionReal(), tarea.getId());

                // Mostrar notificación nativa siempre encima de otras ventanas
                NativeNotification.getInstance().show(
                        tarea.getTitulo(),
                        "¡Pomodoro completado! Tomá un descanso."
                );
            });
        }

        log.info("Sesión de Pomodoro finalizada: {}", guardada.getId());
        return guardada;
    }

    @Override
    public SesionPomodoro cancelarSesion(final UUID sesionId, final long minutosTranscurridos) {
        log.info("Cancelando sesión de Pomodoro: {} con {} minutos transcurridos", sesionId, minutosTranscurridos);

        final SesionPomodoro sesion = sesionRepository.findById(sesionId)
                .orElseThrow(() -> new SesionPomodoroException(
                        "Sesión de Pomodoro no encontrada: " + sesionId
                ));

        sesion.cancelar(minutosTranscurridos);
        final SesionPomodoro guardada = sesionRepository.save(sesion);

        // Actualizar minutos de pomodoro en la tarea
        if (sesion.getTipoSesion() == TipoSesion.TRABAJO) {
            tareaRepository.findById(sesion.getTareaId()).ifPresent(tarea -> {
                tarea.agregarMinutosPomodoro((int) minutosTranscurridos);
                tareaRepository.save(tarea);
                log.info("Actualizados {} minutos pomodoro en tarea {}", minutosTranscurridos, tarea.getId());
            });
        }

        log.info("Sesión de Pomodoro cancelada: {}", guardada.getId());
        return guardada;
    }

    @Override
    public List<SesionPomodoro> obtenerSesionesTarea(final UUID tareaId) {
        return sesionRepository.findByTareaId(tareaId);
    }

    @Override
    public List<SesionPomodoro> obtenerSesionesMateria(final UUID materiaId) {
        return sesionRepository.findByMateriaId(materiaId);
    }

    @Override
    public Optional<SesionPomodoro> obtenerSesion(final UUID id) {
        return sesionRepository.findById(id);
    }

    @Override
    public ConfiguracionPomodoro obtenerConfiguracion() {
        return configuracionRepository.findConfiguracionActiva()
                .orElseGet(() -> {
                    log.info("No hay configuración activa, creando configuración por defecto");
                    final ConfiguracionPomodoro defaultConfig = ConfiguracionPomodoro.createDefault();
                    return configuracionRepository.save(defaultConfig);
                });
    }

    @Override
    public ConfiguracionPomodoro actualizarConfiguracion(final int duracionTrabajo,
                                                        final int duracionDescanso,
                                                        final int duracionDescansoLargo,
                                                        final int pomodorosParaDescansoLargo) {
        log.info("Actualizando configuración del Pomodoro: trabajo={}, descanso={}, largo={}, pomodoros={}",
                duracionTrabajo, duracionDescanso, duracionDescansoLargo, pomodorosParaDescansoLargo);

        if (duracionTrabajo <= 0 || duracionDescanso <= 0 || duracionDescansoLargo <= 0) {
            throw new ConfiguracionInvalidaException(
                    "Las duraciones deben ser mayores a 0 minutos"
            );
        }

        if (pomodorosParaDescansoLargo <= 0) {
            throw new ConfiguracionInvalidaException(
                    "Los pomodoros para descanso largo deben ser mayores a 0"
            );
        }

        final ConfiguracionPomodoro actual = obtenerConfiguracion();
        actual.setDuracionTrabajo(com.academic.gestor.domain.model.valueobjects.DuracionMinutos.of(duracionTrabajo));
        actual.setDuracionDescanso(com.academic.gestor.domain.model.valueobjects.DuracionMinutos.of(duracionDescanso));
        actual.setDuracionDescansoLargo(com.academic.gestor.domain.model.valueobjects.DuracionMinutos.of(duracionDescansoLargo));
        actual.setPomodorosParaDescansoLargo(pomodorosParaDescansoLargo);

        final ConfiguracionPomodoro guardada = configuracionRepository.save(actual);
        log.info("Configuración del Pomodoro actualizada: {}", guardada.getId());
        return guardada;
    }
}
