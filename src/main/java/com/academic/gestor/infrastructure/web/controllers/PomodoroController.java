package com.academic.gestor.infrastructure.web.controllers;

import com.academic.gestor.domain.model.entities.ConfiguracionPomodoro;
import com.academic.gestor.domain.model.entities.SesionPomodoro;
import com.academic.gestor.domain.ports.inbound.SesionPomodoroService;
import com.academic.gestor.infrastructure.web.dto.request.ActualizarConfiguracionPomodoroRequest;
import com.academic.gestor.infrastructure.web.dto.request.CancelarSesionPomodoroRequest;
import com.academic.gestor.infrastructure.web.dto.request.CrearSesionPomodoroRequest;
import com.academic.gestor.infrastructure.web.dto.response.SesionPomodoroResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller REST para la gestión de sesiones de Pomodoro.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/pomodoro")
public class PomodoroController {

    private static final Logger log = LoggerFactory.getLogger(PomodoroController.class);

    private final SesionPomodoroService sesionService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param sesionService servicio de sesiones Pomodoro
     */
    public PomodoroController(final SesionPomodoroService sesionService) {
        this.sesionService = sesionService;
    }

    /**
     * Inicia una nueva sesión de Pomodoro.
     *
     * @param request datos de la sesión a crear
     * @return sesión iniciada con código 201
     */
    @PostMapping("/iniciar")
    public ResponseEntity<SesionPomodoroResponse> iniciarSesion(
            @Valid @RequestBody final CrearSesionPomodoroRequest request) {
        log.info("POST /api/pomodoro/iniciar - Iniciando sesión para tarea {}", request.tareaId());

        final SesionPomodoro sesion = sesionService.iniciarSesion(
                request.tareaId(),
                request.materiaId(),
                request.duracionMinutos()
        );

        final SesionPomodoroResponse response = toResponse(sesion);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Finaliza una sesión de Pomodoro.
     *
     * @param sesionId ID de la sesión a finalizar
     * @return sesión finalizada
     */
    @PostMapping("/{sesionId}/finalizar")
    public ResponseEntity<SesionPomodoroResponse> finalizarSesion(
            @PathVariable final UUID sesionId) {
        log.info("POST /api/pomodoro/{}/finalizar - Finalizando sesión", sesionId);

        final SesionPomodoro sesion = sesionService.finalizarSesion(sesionId);
        return ResponseEntity.ok(toResponse(sesion));
    }

    /**
     * Cancela una sesión de Pomodoro, guardando el tiempo transcurrido.
     *
     * @param sesionId ID de la sesión a cancelar
     * @param request datos de la cancelación (minutos transcurridos)
     * @return sesión cancelada
     */
    @PostMapping("/{sesionId}/cancelar")
    public ResponseEntity<SesionPomodoroResponse> cancelarSesion(
            @PathVariable final UUID sesionId,
            @Valid @RequestBody final CancelarSesionPomodoroRequest request) {
        log.info("POST /api/pomodoro/{}/cancelar - Cancelando sesión con {} minutos",
                sesionId, request.minutosTranscurridos());

        final SesionPomodoro sesion = sesionService.cancelarSesion(sesionId, request.minutosTranscurridos());
        return ResponseEntity.ok(toResponse(sesion));
    }

    /**
     * Obtiene las sesiones de una tarea específica.
     *
     * @param tareaId ID de la tarea
     * @return lista de sesiones de la tarea
     */
    @GetMapping("/tarea/{tareaId}")
    public ResponseEntity<List<SesionPomodoroResponse>> obtenerSesionesTarea(
            @PathVariable final UUID tareaId) {
        log.info("GET /api/pomodoro/tarea/{} - Obteniendo sesiones", tareaId);

        final List<SesionPomodoroResponse> sesiones = sesionService.obtenerSesionesTarea(tareaId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(sesiones);
    }

    /**
     * Obtiene las sesiones de una materia específica.
     *
     * @param materiaId ID de la materia
     * @return lista de sesiones de la materia
     */
    @GetMapping("/materia/{materiaId}")
    public ResponseEntity<List<SesionPomodoroResponse>> obtenerSesionesMateria(
            @PathVariable final UUID materiaId) {
        log.info("GET /api/pomodoro/materia/{} - Obteniendo sesiones", materiaId);

        final List<SesionPomodoroResponse> sesiones = sesionService.obtenerSesionesMateria(materiaId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(sesiones);
    }

    /**
     * Obtiene una sesión por su ID.
     *
     * @param id ID de la sesión
     * @return sesión encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<SesionPomodoroResponse> obtenerSesion(@PathVariable final UUID id) {
        log.info("GET /api/pomodoro/{} - Obteniendo sesión", id);

        return sesionService.obtenerSesion(id)
                .map(sesion -> ResponseEntity.ok(toResponse(sesion)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene la configuración activa del Pomodoro.
     *
     * @return configuración del Pomodoro
     */
    @GetMapping("/configuracion")
    public ResponseEntity<?> obtenerConfiguracion() {
        log.info("GET /api/pomodoro/configuracion - Obteniendo configuración");

        final var configuracion = sesionService.obtenerConfiguracion();
        return ResponseEntity.ok(configuracion);
    }

    /**
     * Convierte una entidad SesionPomodoro a su respuesta HTTP.
     *
     * @param sesion entidad de dominio
     * @return DTO de respuesta
     */
    private SesionPomodoroResponse toResponse(final SesionPomodoro sesion) {
        return new SesionPomodoroResponse(
                sesion.getId(),
                sesion.getTareaId(),
                null, // Se puede enriquecer con el título de la tarea
                sesion.getMateriaId(),
                null, // Se puede enriquecer con el nombre de la materia
                sesion.getDuracion().minutos(),
                sesion.getTipoSesion(),
                sesion.getFechaInicio(),
                sesion.getFechaFin(),
                sesion.isCompletada()
        );
    }

    /**
     * Actualiza la configuración del Pomodoro.
     *
     * @param request datos de configuración a actualizar
     * @return configuración actualizada
     */
    @PutMapping("/configuracion")
    public ResponseEntity<?> actualizarConfiguracion(
            @Valid @RequestBody final ActualizarConfiguracionPomodoroRequest request) {
        log.info("PUT /api/pomodoro/configuracion - Actualizando configuración");
        
        final ConfiguracionPomodoro actualizada = sesionService.actualizarConfiguracion(
                request.duracionTrabajo(),
                request.duracionDescanso(),
                request.duracionDescansoLargo(),
                request.pomodorosParaDescansoLargo()
        );
        
        return ResponseEntity.ok(actualizada);
    }
}
