package com.academic.gestor.infrastructure.web.controllers;

import com.academic.gestor.application.dto.BloqueHorarioResponse;
import com.academic.gestor.application.dto.CrearBloqueHorarioRequest;
import com.academic.gestor.application.dto.MoverBloqueRequest;
import com.academic.gestor.application.dto.ValidacionDisponibilidadResponse;
import com.academic.gestor.application.mappers.BloqueHorarioMapper;
import com.academic.gestor.domain.exceptions.MateriaNotFoundException;
import com.academic.gestor.domain.exceptions.SolapamientoException;
import com.academic.gestor.domain.model.entities.BloqueHorario;
import com.academic.gestor.domain.model.entities.Materia;
import com.academic.gestor.domain.model.valueobjects.DiaSemana;
import com.academic.gestor.domain.ports.inbound.AgendaService;
import com.academic.gestor.domain.ports.inbound.MateriaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller REST para la agenda semanal.
 */
@RestController
@RequestMapping("/api/agenda/bloques")
public class AgendaController {

    private static final Logger log = LoggerFactory.getLogger(AgendaController.class);

    private final AgendaService agendaService;
    private final MateriaService materiaService;

    public AgendaController(AgendaService agendaService, MateriaService materiaService) {
        this.agendaService = agendaService;
        this.materiaService = materiaService;
    }

    @PostMapping
    public ResponseEntity<BloqueHorarioResponse> crearBloque(
            @Valid @RequestBody CrearBloqueHorarioRequest request) {
        log.info("POST /api/agenda/bloques - Creando bloque para materia: {}", request.materiaId());

        try {
            BloqueHorario bloque = agendaService.crearBloque(
                    request.materiaId(),
                    request.diaSemana(),
                    request.horaInicio(),
                    request.horaFin(),
                    request.aula()
            );

            Materia materia = materiaService.obtenerMateria(bloque.getMateriaId())
                    .orElseThrow(() -> new MateriaNotFoundException(bloque.getMateriaId().toString()));

            BloqueHorarioResponse response = BloqueHorarioMapper.toResponse(
                    bloque, materia.getNombre(), materia.getColor().hex());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (SolapamientoException e) {
            log.warn("Solapamiento al crear bloque: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .header("X-Conflictos", String.valueOf(e.getConflictos().size()))
                    .body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<BloqueHorarioResponse>> listarBloques(
            @RequestParam(required = false) UUID materiaId,
            @RequestParam(required = false) DiaSemana diaSemana) {
        log.info("GET /api/agenda/bloques - Listando bloques (materiaId={}, diaSemana={})", materiaId, diaSemana);

        List<BloqueHorario> bloques;
        if (materiaId != null && diaSemana != null) {
            bloques = agendaService.listarPorMateriaYDia(materiaId, diaSemana);
        } else if (materiaId != null) {
            bloques = agendaService.listarPorMateria(materiaId);
        } else if (diaSemana != null) {
            bloques = agendaService.listarPorDia(diaSemana);
        } else {
            bloques = agendaService.listarBloquesActivos();
        }

        List<BloqueHorarioResponse> response = bloques.stream()
                .map(this::toResponseWithMateria)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/semana")
    public ResponseEntity<List<BloqueHorarioResponse>> obtenerVistaSemanal() {
        log.info("GET /api/agenda/bloques/semana - Vista semanal completa");

        List<BloqueHorario> bloques = agendaService.listarBloquesActivos();

        List<BloqueHorarioResponse> response = bloques.stream()
                .map(this::toResponseWithMateria)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BloqueHorarioResponse> obtenerBloque(@PathVariable UUID id) {
        log.info("GET /api/agenda/bloques/{} - Obteniendo bloque", id);

        return agendaService.obtenerBloque(id)
                .map(this::toResponseWithMateria)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<BloqueHorarioResponse> actualizarBloque(
            @PathVariable UUID id,
            @Valid @RequestBody CrearBloqueHorarioRequest request) {
        log.info("PUT /api/agenda/bloques/{} - Actualizando bloque", id);

        try {
            BloqueHorario bloque = agendaService.actualizarBloque(
                    id,
                    request.materiaId(),
                    request.diaSemana(),
                    request.horaInicio(),
                    request.horaFin(),
                    request.aula()
            );

            Materia materia = materiaService.obtenerMateria(bloque.getMateriaId())
                    .orElseThrow(() -> new MateriaNotFoundException(bloque.getMateriaId().toString()));

            return ResponseEntity.ok(BloqueHorarioMapper.toResponse(
                    bloque, materia.getNombre(), materia.getColor().hex()));

        } catch (SolapamientoException e) {
            log.warn("Solapamiento al actualizar bloque: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PatchMapping("/{id}/mover")
    public ResponseEntity<BloqueHorarioResponse> moverBloque(
            @PathVariable UUID id,
            @Valid @RequestBody MoverBloqueRequest request) {
        log.info("PATCH /api/agenda/bloques/{}/mover - Moviendo bloque", id);

        try {
            BloqueHorario bloque = agendaService.moverBloque(
                    id,
                    request.diaSemana(),
                    request.horaInicio(),
                    request.horaFin()
            );

            Materia materia = materiaService.obtenerMateria(bloque.getMateriaId())
                    .orElseThrow(() -> new MateriaNotFoundException(bloque.getMateriaId().toString()));

            return ResponseEntity.ok(BloqueHorarioMapper.toResponse(
                    bloque, materia.getNombre(), materia.getColor().hex()));

        } catch (SolapamientoException e) {
            log.warn("Solapamiento al mover bloque: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarBloque(@PathVariable UUID id) {
        log.info("DELETE /api/agenda/bloques/{} - Eliminando bloque", id);

        agendaService.eliminarBloque(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validar")
    public ResponseEntity<ValidacionDisponibilidadResponse> validarDisponibilidad(
            @RequestParam UUID materiaId,
            @RequestParam DiaSemana diaSemana,
            @RequestParam LocalTime horaInicio,
            @RequestParam LocalTime horaFin) {
        log.info("GET /api/agenda/bloques/validar - Validando disponibilidad");

        ValidacionDisponibilidadResponse response = ValidacionDisponibilidadResponse.from(
                agendaService.validarDisponibilidad(materiaId, diaSemana, horaInicio, horaFin)
        );

        return ResponseEntity.ok(response);
    }

    private BloqueHorarioResponse toResponseWithMateria(BloqueHorario bloque) {
        Optional<Materia> materiaOpt = materiaService.obtenerMateria(bloque.getMateriaId());
        if (materiaOpt.isPresent()) {
            Materia materia = materiaOpt.get();
            return BloqueHorarioMapper.toResponse(bloque, materia.getNombre(), materia.getColor().hex());
        }
        return BloqueHorarioMapper.toResponse(bloque, "Desconocida", "#666666");
    }
}