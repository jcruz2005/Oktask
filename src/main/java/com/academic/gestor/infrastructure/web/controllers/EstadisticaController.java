package com.academic.gestor.infrastructure.web.controllers;

import com.academic.gestor.application.dto.EstadisticaMateriaDTO;
import com.academic.gestor.domain.ports.inbound.EstadisticaService;
import com.academic.gestor.infrastructure.web.dto.response.EstadisticaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller REST para estadísticas y análisis de horas.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticaController {

    private static final Logger log = LoggerFactory.getLogger(EstadisticaController.class);

    private final EstadisticaService estadisticaService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param estadisticaService servicio de estadísticas
     */
    public EstadisticaController(final EstadisticaService estadisticaService) {
        this.estadisticaService = estadisticaService;
    }

    /**
     * Obtiene las horas totales estudiadas por materia.
     *
     * @return lista de estadísticas por materia
     */
    @GetMapping("/horas")
    public ResponseEntity<List<EstadisticaResponse>> obtenerHorasPorMateria() {
        log.info("GET /api/estadisticas/horas - Obteniendo horas por materia");

        final List<EstadisticaResponse> estadisticas = estadisticaService.obtenerHorasPorMateria()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(estadisticas);
    }

    /**
     * Obtiene las horas estudiadas en un período específico.
     *
     * @param fechaInicio fecha de inicio del período
     * @param fechaFin fecha de fin del período
     * @return lista de estadísticas por materia en el período
     */
    @GetMapping("/horas/periodo")
    public ResponseEntity<List<EstadisticaResponse>> obtenerHorasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate fechaFin) {
        log.info("GET /api/estadisticas/horas/periodo - Desde {} hasta {}", fechaInicio, fechaFin);

        validarRangoFechas(fechaInicio, fechaFin);

        final List<EstadisticaResponse> estadisticas = estadisticaService.obtenerHorasPorPeriodo(
                fechaInicio, fechaFin
        ).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(estadisticas);
    }

    /**
     * Obtiene el progreso de tareas por materia.
     *
     * @return lista de estadísticas de progreso
     */
    @GetMapping("/progreso")
    public ResponseEntity<List<EstadisticaResponse>> obtenerProgresoTareas() {
        log.info("GET /api/estadisticas/progreso - Obteniendo progreso de tareas");

        final List<EstadisticaResponse> estadisticas = estadisticaService.obtenerProgresoTareas()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(estadisticas);
    }

    /**
     * Obtiene un resumen general de estadísticas.
     *
     * @param fechaInicio fecha de inicio (opcional)
     * @param fechaFin fecha de fin (opcional)
     * @return mapa con el resumen de estadísticas
     */
    @GetMapping("/resumen")
    public ResponseEntity<Map<String, Object>> obtenerResumen(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            final LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            final LocalDate fechaFin) {
        log.info("GET /api/estadisticas/resumen - Obteniendo resumen");

        final LocalDate inicio = fechaInicio != null ? fechaInicio : LocalDate.now().withDayOfMonth(1);
        final LocalDate fin = fechaFin != null ? fechaFin : LocalDate.now();

        validarRangoFechas(inicio, fin);

        final double totalHoras = estadisticaService.obtenerTotalHorasPeriodo(inicio, fin);
        final long totalPomodoros = estadisticaService.obtenerTotalPomodorosPeriodo(inicio, fin);

        final Map<String, Object> resumen = new HashMap<>();
        resumen.put("fechaInicio", inicio);
        resumen.put("fechaFin", fin);
        resumen.put("totalHoras", totalHoras);
        resumen.put("totalPomodoros", totalPomodoros);
        resumen.put("promedioHorasDiarias", totalHoras / Math.max(1, java.time.temporal.ChronoUnit.DAYS.between(inicio, fin) + 1));

        return ResponseEntity.ok(resumen);
    }

    /**
     * Valida que la fecha de inicio no sea posterior a la fecha de fin.
     *
     * @param fechaInicio fecha de inicio del período
     * @param fechaFin fecha de fin del período
     * @throws IllegalArgumentException si el rango es inválido
     */
    private void validarRangoFechas(final LocalDate fechaInicio, final LocalDate fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException(
                    "La fecha de inicio no puede ser posterior a la fecha de fin"
            );
        }
    }

    /**
     * Convierte un DTO de estadísticas a su respuesta HTTP.
     *
     * @param dto DTO de estadísticas
     * @return DTO de respuesta
     */
    private EstadisticaResponse toResponse(final EstadisticaMateriaDTO dto) {
        return new EstadisticaResponse(
                dto.materiaId(),
                dto.nombreMateria(),
                dto.codigoMateria(),
                dto.horasEstudiadas(),
                dto.pomodorosCompletados(),
                dto.tareasTotales(),
                dto.tareasCompletadas(),
                dto.porcentajeProgreso()
        );
    }
}
