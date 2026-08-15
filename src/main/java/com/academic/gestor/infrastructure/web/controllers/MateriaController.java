package com.academic.gestor.infrastructure.web.controllers;

import com.academic.gestor.domain.model.entities.Materia;
import com.academic.gestor.domain.ports.inbound.MateriaService;
import com.academic.gestor.infrastructure.web.dto.request.CrearMateriaRequest;
import com.academic.gestor.infrastructure.web.dto.request.EditarMateriaRequest;
import com.academic.gestor.infrastructure.web.dto.response.MateriaResponse;
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
 * Controller REST para la gestión de materias.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/materias")
public class MateriaController {

    private static final Logger log = LoggerFactory.getLogger(MateriaController.class);

    private final MateriaService materiaService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param materiaService servicio de materias
     */
    public MateriaController(final MateriaService materiaService) {
        this.materiaService = materiaService;
    }

    /**
     * Crea una nueva materia.
     *
     * @param request datos de la materia a crear
     * @return materia creada con código 201
     */
    @PostMapping
    public ResponseEntity<MateriaResponse> crearMateria(
            @Valid @RequestBody final CrearMateriaRequest request) {
        log.info("POST /api/materias - Creando materia: {}", request.nombre());

        final Materia materia = materiaService.crearMateria(
                request.nombre(),
                request.codigo(),
                request.color(),
                request.prioridad()
        );

        final MateriaResponse response = toResponse(materia);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Lista todas las materias activas.
     *
     * @return lista de materias
     */
    @GetMapping
    public ResponseEntity<List<MateriaResponse>> listarMaterias() {
        log.info("GET /api/materias - Listando materias");

        final List<MateriaResponse> materias = materiaService.listarMaterias().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(materias);
    }

    /**
     * Obtiene una materia por su ID.
     *
     * @param id ID de la materia
     * @return materia encontrada
     */
    @GetMapping("/{id}")
    public ResponseEntity<MateriaResponse> obtenerMateria(@PathVariable final UUID id) {
        log.info("GET /api/materias/{} - Obteniendo materia", id);

        return materiaService.obtenerMateria(id)
                .map(materia -> ResponseEntity.ok(toResponse(materia)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Edita una materia existente.
     *
     * @param id ID de la materia a editar
     * @param request datos a actualizar
     * @return materia editada
     */
    @PutMapping("/{id}")
    public ResponseEntity<MateriaResponse> editarMateria(
            @PathVariable final UUID id,
            @Valid @RequestBody final EditarMateriaRequest request) {
        log.info("PUT /api/materias/{} - Editando materia", id);

        final Materia materia = materiaService.editarMateria(
                id,
                request.nombre(),
                request.color(),
                request.prioridad()
        );

        return ResponseEntity.ok(toResponse(materia));
    }

    /**
     * Elimina (desactiva) una materia.
     *
     * @param id ID de la materia a eliminar
     * @return código 204 sin contenido
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMateria(@PathVariable final UUID id) {
        log.info("DELETE /api/materias/{} - Eliminando materia", id);

        materiaService.eliminarMateria(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Convierte una entidad Materia a su respuesta HTTP.
     *
     * @param materia entidad de dominio
     * @return DTO de respuesta
     */
    private MateriaResponse toResponse(final Materia materia) {
        return new MateriaResponse(
                materia.getId(),
                materia.getNombre(),
                materia.getCodigo().value(),
                materia.getColor().hex(),
                materia.getPrioridad(),
                materia.getFechaCreacion(),
                materia.isActiva()
        );
    }
}
