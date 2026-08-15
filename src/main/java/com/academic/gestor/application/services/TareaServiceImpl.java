package com.academic.gestor.application.services;

import com.academic.gestor.domain.exceptions.MateriaNotFoundException;
import com.academic.gestor.domain.exceptions.TareaNotFoundException;
import com.academic.gestor.domain.model.entities.Tarea;
import com.academic.gestor.domain.model.enums.EstadoTarea;
import com.academic.gestor.domain.model.enums.Prioridad;
import com.academic.gestor.domain.ports.inbound.TareaService;
import com.academic.gestor.domain.ports.outbound.MateriaRepository;
import com.academic.gestor.domain.ports.outbound.TareaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementación del servicio de gestión de tareas.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public class TareaServiceImpl implements TareaService {

    private static final Logger log = LoggerFactory.getLogger(TareaServiceImpl.class);

    private final TareaRepository tareaRepository;
    private final MateriaRepository materiaRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param tareaRepository repositorio de tareas
     * @param materiaRepository repositorio de materias (para validación)
     */
    public TareaServiceImpl(final TareaRepository tareaRepository,
                            final MateriaRepository materiaRepository) {
        this.tareaRepository = tareaRepository;
        this.materiaRepository = materiaRepository;
    }

    @Override
    public Tarea crearTarea(final String titulo, final String descripcion,
                            final UUID materiaId, final LocalDate fechaLimite,
                            final Prioridad prioridad) {
        log.info("Creando tarea '{}' para materia {}", titulo, materiaId);

        if (!materiaRepository.existsById(materiaId)) {
            throw new MateriaNotFoundException(materiaId.toString());
        }

        final Tarea tarea = Tarea.create(titulo, descripcion, materiaId, fechaLimite, prioridad);
        final Tarea guardada = tareaRepository.save(tarea);

        log.info("Tarea creada con ID: {}", guardada.getId());
        return guardada;
    }

    @Override
    public Tarea editarTarea(final UUID id, final String titulo, final String descripcion,
                             final LocalDate fechaLimite, final Prioridad prioridad) {
        log.info("Editando tarea: {}", id);

        final Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new TareaNotFoundException(id.toString()));

        if (titulo != null && !titulo.isBlank()) {
            tarea.setTitulo(titulo);
        }
        if (descripcion != null) {
            tarea.setDescripcion(descripcion);
        }
        if (fechaLimite != null) {
            tarea.setFechaLimite(fechaLimite);
        }
        if (prioridad != null) {
            tarea.setPrioridad(prioridad);
        }

        final Tarea guardada = tareaRepository.save(tarea);
        log.info("Tarea editada: {}", guardada.getId());
        return guardada;
    }

    @Override
    public Tarea cambiarEstado(final UUID id, final EstadoTarea nuevoEstado) {
        log.info("Cambiando estado de tarea {} a {}", id, nuevoEstado);

        final Tarea tarea = tareaRepository.findById(id)
                .orElseThrow(() -> new TareaNotFoundException(id.toString()));

        tarea.cambiarEstado(nuevoEstado);
        final Tarea guardada = tareaRepository.save(tarea);

        log.info("Estado de tarea {} cambiado a {}", id, nuevoEstado);
        return guardada;
    }

    @Override
    public void eliminarTarea(final UUID id) {
        log.info("Eliminando tarea: {}", id);

        if (!tareaRepository.existsById(id)) {
            throw new TareaNotFoundException(id.toString());
        }

        tareaRepository.deleteById(id);
        log.info("Tarea eliminada: {}", id);
    }

    @Override
    public List<Tarea> listarTareas() {
        return tareaRepository.findAll();
    }

    @Override
    public List<Tarea> listarTareasPorMateria(final UUID materiaId) {
        return tareaRepository.findByMateriaId(materiaId);
    }

    @Override
    public List<Tarea> listarTareasPorEstado(final EstadoTarea estado) {
        return tareaRepository.findByEstado(estado);
    }

    @Override
    public Optional<Tarea> obtenerTarea(final UUID id) {
        return tareaRepository.findById(id);
    }
}
