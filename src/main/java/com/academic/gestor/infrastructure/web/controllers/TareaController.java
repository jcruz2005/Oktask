package com.academic.gestor.infrastructure.web.controllers;

import com.academic.gestor.domain.model.entities.Tarea;
import com.academic.gestor.domain.model.enums.EstadoTarea;
import com.academic.gestor.domain.ports.inbound.TareaService;
import com.academic.gestor.infrastructure.web.dto.request.CrearTareaRequest;
import com.academic.gestor.infrastructure.web.dto.request.EditarTareaRequest;
import com.academic.gestor.infrastructure.web.dto.response.TareaResponse;
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
 * Controller REST para la gestión de tareas.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    private static final Logger log = LoggerFactory.getLogger(TareaController.class);

    private final TareaService tareaService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param tareaService servicio de tareas
     */
    public TareaController(final TareaService tareaService) {
        this.tareaService = tareaService;
    }

    /**
     * Crea una nueva tarea.
     *
     * @param request datos de la tarea a crear
     * @return tarea creada con código 201
     */
    @PostMapping
    public ResponseEntity<TareaResponse> crearTarea(
            @Valid @RequestBody final CrearTareaRequest request) {
        log.info("POST /api/tareas - Creando tarea: {}", request.titulo());

        final Tarea tarea = tareaService.crearTarea(
                request.titulo(),
                request.descripcion(),
                request.materiaId(),
                request.fechaLimite(),
                request.prioridad()
        );

        final TareaResponse response = toResponse(tarea);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista todas las tareas.
     *
     * @return lista de tareas
     */
    @GetMapping
    public ResponseEntity<List<TareaResponse>> listarTareas() {
        log.info("GET /api/tareas - Listando tareas");

        final List<TareaResponse> tareas = tareaService.listarTareas().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(tareas);
    }

    /**
     * Lista las tareas de una materia específica.
     *
     * @param materiaId ID de la materia
     * @return lista de tareas de la materia
     */
    @GetMapping("/materia/{materiaId}")
    public ResponseEntity<List<TareaResponse>> listarTareasPorMateria(
            @PathVariable final UUID materiaId) {
        log.info("GET /api/tareas/materia/{} - Listando tareas por materia", materiaId);

        final List<TareaResponse> tareas = tareaService.listarTareasPorMateria(materiaId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(tareas);
    }

    /**
     * Lista las tareas con un estado específico.
     *
     * @param estado estado de las tareas
     * @return lista de tareas con el estado dado
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<TareaResponse>> listarTareasPorEstado(
            @PathVariable final EstadoTarea estado) {
        log.info("GET /api/tareas/estado/{} - Listando tareas por estado", estado);

        final List<TareaResponse> tareas = tareaService.listarTareasPorEstado(estado).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(tareas);
    }

    /**
     * Obtiene una tarea por su ID.
     *
     * @param id ID de la tarea
     * @return tarea encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<TareaResponse> obtenerTarea(@PathVariable final UUID id) {
        log.info("GET /api/tareas/{} - Obteniendo tarea", id);

        return tareaService.obtenerTarea(id)
                .map(tarea -> ResponseEntity.ok(toResponse(tarea)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Edita una tarea existente.
     *
     * @param id ID de la tarea a editar
     * @param request datos a actualizar
     * @return tarea editada
     */
    @PutMapping("/{id}")
    public ResponseEntity<TareaResponse> editarTarea(
            @PathVariable final UUID id,
            @Valid @RequestBody final EditarTareaRequest request) {
        log.info("PUT /api/tareas/{} - Editando tarea", id);

        Tarea tarea = tareaService.editarTarea(
                id,
                request.titulo(),
                request.descripcion(),
                request.fechaLimite(),
                request.prioridad()
        );

        if (request.estado() != null) {
            tarea = tareaService.cambiarEstado(id, request.estado());
        }

        return ResponseEntity.ok(toResponse(tarea));
    }

    /**
     * Elimina una tarea.
     *
     * @param id ID de la tarea a eliminar
     * @return código 204 sin contenido
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTarea(@PathVariable final UUID id) {
        log.info("DELETE /api/tareas/{} - Eliminando tarea", id);

        tareaService.eliminarTarea(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Convierte una entidad Tarea a su respuesta HTTP.
     *
     * @param tarea entidad de dominio
     * @return DTO de respuesta
     */
    private TareaResponse toResponse(final Tarea tarea) {
        return new TareaResponse(
                tarea.getId(),
                tarea.getTitulo(),
                tarea.getDescripcion(),
                tarea.getMateriaId(),
                null, // El nombre de materia se puede enriquecer si se necesita
                tarea.getFechaLimite(),
                tarea.getPrioridad(),
                tarea.getEstado(),
                tarea.getFechaCreacion(),
                tarea.getFechaCompletado(),
                tarea.getMinutosPomodoro()
        );
    }
}
