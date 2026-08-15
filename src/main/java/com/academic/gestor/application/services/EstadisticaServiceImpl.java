package com.academic.gestor.application.services;

import com.academic.gestor.application.dto.EstadisticaMateriaDTO;
import com.academic.gestor.domain.model.entities.Materia;
import com.academic.gestor.domain.model.enums.EstadoTarea;
import com.academic.gestor.domain.ports.inbound.EstadisticaService;
import com.academic.gestor.domain.ports.outbound.MateriaRepository;
import com.academic.gestor.domain.ports.outbound.SesionPomodoroRepository;
import com.academic.gestor.domain.ports.outbound.TareaRepository;
import com.academic.gestor.shared.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de estadísticas y análisis de horas.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public class EstadisticaServiceImpl implements EstadisticaService {

    private static final Logger log = LoggerFactory.getLogger(EstadisticaServiceImpl.class);

    private final MateriaRepository materiaRepository;
    private final TareaRepository tareaRepository;
    private final SesionPomodoroRepository sesionRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param materiaRepository repositorio de materias
     * @param tareaRepository repositorio de tareas
     * @param sesionRepository repositorio de sesiones
     */
    public EstadisticaServiceImpl(final MateriaRepository materiaRepository,
                                  final TareaRepository tareaRepository,
                                  final SesionPomodoroRepository sesionRepository) {
        this.materiaRepository = materiaRepository;
        this.tareaRepository = tareaRepository;
        this.sesionRepository = sesionRepository;
    }

    @Override
    public List<EstadisticaMateriaDTO> obtenerHorasPorMateria() {
        log.info("Obteniendo horas totales por materia");

        final List<Materia> materias = materiaRepository.findAll();

        return materias.stream()
                .map(this::construirEstadisticaMateria)
                .collect(Collectors.toList());
    }

    @Override
    public List<EstadisticaMateriaDTO> obtenerHorasPorPeriodo(final LocalDate fechaInicio,
                                                              final LocalDate fechaFin) {
        log.info("Obteniendo horas por período: {} a {}", fechaInicio, fechaFin);

        final LocalDateTime inicio = DateUtils.startOfDay(fechaInicio);
        final LocalDateTime fin = DateUtils.endOfDay(fechaFin);

        final List<Materia> materias = materiaRepository.findAll();

        return materias.stream()
                .map(materia -> construirEstadisticaMateriaEnPeriodo(materia, inicio, fin))
                .collect(Collectors.toList());
    }

    @Override
    public List<EstadisticaMateriaDTO> obtenerProgresoTareas() {
        log.info("Obteniendo progreso de tareas por materia");

        final List<Materia> materias = materiaRepository.findAll();

        return materias.stream()
                .map(this::construirEstadisticaProgreso)
                .collect(Collectors.toList());
    }

    @Override
    public double obtenerTotalHorasPeriodo(final LocalDate fechaInicio, final LocalDate fechaFin) {
        final LocalDateTime inicio = DateUtils.startOfDay(fechaInicio);
        final LocalDateTime fin = DateUtils.endOfDay(fechaFin);

        final long totalMinutos = sesionRepository.sumDuracionByFechaInRange(inicio, fin);
        return totalMinutos / 60.0;
    }

    @Override
    public long obtenerTotalPomodorosPeriodo(final LocalDate fechaInicio, final LocalDate fechaFin) {
        final LocalDateTime inicio = DateUtils.startOfDay(fechaInicio);
        final LocalDateTime fin = DateUtils.endOfDay(fechaFin);

        return sesionRepository.findCompletadasByFechaInRange(inicio, fin).stream()
                .filter(s -> s.getTipoSesion() == com.academic.gestor.domain.model.enums.TipoSesion.TRABAJO)
                .count();
    }

    /**
     * Construye las estadísticas completas de una materia.
     *
     * @param materia materia a analizar
     * @return DTO de estadísticas
     */
    private EstadisticaMateriaDTO construirEstadisticaMateria(final Materia materia) {
        final UUID materiaId = materia.getId();
        final long totalMinutos = sesionRepository.sumDuracionByMateriaId(materiaId);
        final double horas = totalMinutos / 60.0;

        final long tareasTotales = tareaRepository.countByMateriaIdAndEstado(materiaId, EstadoTarea.PENDIENTE)
                + tareaRepository.countByMateriaIdAndEstado(materiaId, EstadoTarea.EN_PROGRESO)
                + tareaRepository.countByMateriaIdAndEstado(materiaId, EstadoTarea.COMPLETADA);

        final long tareasCompletadas = tareaRepository.countByMateriaIdAndEstado(materiaId, EstadoTarea.COMPLETADA);

        final double porcentaje = tareasTotales > 0 ? (tareasCompletadas * 100.0 / tareasTotales) : 0.0;

        return new EstadisticaMateriaDTO(
                materiaId,
                materia.getNombre(),
                materia.getCodigo().value(),
                horas,
                0, // Se calcula en el contexto del período
                tareasTotales,
                tareasCompletadas,
                porcentaje
        );
    }

    /**
     * Construye las estadísticas de una materia en un período específico.
     *
     * @param materia materia a analizar
     * @param inicio fecha de inicio del período
     * @param fin fecha de fin del período
     * @return DTO de estadísticas del período
     */
    private EstadisticaMateriaDTO construirEstadisticaMateriaEnPeriodo(final Materia materia,
                                                                       final LocalDateTime inicio,
                                                                       final LocalDateTime fin) {
        final UUID materiaId = materia.getId();
        final long totalMinutos = sesionRepository.sumDuracionByMateriaIdAndFechaInRange(
                materiaId, inicio, fin
        );
        final double horas = totalMinutos / 60.0;

        final long pomodoros = sesionRepository.findCompletadasByFechaInRange(inicio, fin).stream()
                .filter(s -> s.getMateriaId().equals(materiaId))
                .filter(s -> s.getTipoSesion() == com.academic.gestor.domain.model.enums.TipoSesion.TRABAJO)
                .count();

        final long tareasTotales = tareaRepository.countByMateriaIdAndEstado(materiaId, EstadoTarea.PENDIENTE)
                + tareaRepository.countByMateriaIdAndEstado(materiaId, EstadoTarea.EN_PROGRESO)
                + tareaRepository.countByMateriaIdAndEstado(materiaId, EstadoTarea.COMPLETADA);

        final long tareasCompletadas = tareaRepository.countByMateriaIdAndEstado(materiaId, EstadoTarea.COMPLETADA);

        final double porcentaje = tareasTotales > 0 ? (tareasCompletadas * 100.0 / tareasTotales) : 0.0;

        return new EstadisticaMateriaDTO(
                materiaId,
                materia.getNombre(),
                materia.getCodigo().value(),
                horas,
                pomodoros,
                tareasTotales,
                tareasCompletadas,
                porcentaje
        );
    }

    /**
     * Construye las estadísticas de progreso de tareas de una materia.
     *
     * @param materia materia a analizar
     * @return DTO de estadísticas de progreso
     */
    private EstadisticaMateriaDTO construirEstadisticaProgreso(final Materia materia) {
        final UUID materiaId = materia.getId();

        final long tareasTotales = tareaRepository.countByMateriaIdAndEstado(materiaId, EstadoTarea.PENDIENTE)
                + tareaRepository.countByMateriaIdAndEstado(materiaId, EstadoTarea.EN_PROGRESO)
                + tareaRepository.countByMateriaIdAndEstado(materiaId, EstadoTarea.COMPLETADA);

        final long tareasCompletadas = tareaRepository.countByMateriaIdAndEstado(materiaId, EstadoTarea.COMPLETADA);

        final double porcentaje = tareasTotales > 0 ? (tareasCompletadas * 100.0 / tareasTotales) : 0.0;

        return new EstadisticaMateriaDTO(
                materiaId,
                materia.getNombre(),
                materia.getCodigo().value(),
                0.0,
                0,
                tareasTotales,
                tareasCompletadas,
                porcentaje
        );
    }
}
