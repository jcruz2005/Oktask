package com.academic.gestor.application.services;

import com.academic.gestor.domain.exceptions.MateriaNotFoundException;
import com.academic.gestor.domain.model.entities.Materia;
import com.academic.gestor.domain.model.enums.Prioridad;
import com.academic.gestor.domain.model.valueobjects.CodigoMateria;
import com.academic.gestor.domain.model.valueobjects.Color;
import com.academic.gestor.domain.ports.inbound.MateriaService;
import com.academic.gestor.domain.ports.outbound.MateriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementación del servicio de gestión de materias.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public class MateriaServiceImpl implements MateriaService {

    private static final Logger log = LoggerFactory.getLogger(MateriaServiceImpl.class);

    private final MateriaRepository materiaRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param materiaRepository repositorio de materias
     */
    public MateriaServiceImpl(final MateriaRepository materiaRepository) {
        this.materiaRepository = materiaRepository;
    }

    @Override
    public Materia crearMateria(final String nombre, final String codigo,
                                final String colorStr, final Prioridad prioridad) {
        log.info("Creando materia: {} con código {}", nombre, codigo);

        final CodigoMateria codigoObj = CodigoMateria.of(codigo);
        if (materiaRepository.existsByCodigo(codigoObj)) {
            throw new IllegalArgumentException(
                    "Ya existe una materia con el código: " + codigo
            );
        }

        final Color color = Color.of(colorStr);
        final Materia materia = Materia.create(nombre, codigoObj, color, prioridad);
        final Materia guardada = materiaRepository.save(materia);

        log.info("Materia creada con ID: {}", guardada.getId());
        return guardada;
    }

    @Override
    public Materia editarMateria(final UUID id, final String nombre,
                                 final String colorStr, final Prioridad prioridad) {
        log.info("Editando materia: {}", id);

        final Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new MateriaNotFoundException(id.toString()));

        if (nombre != null && !nombre.isBlank()) {
            materia.setNombre(nombre);
        }
        if (colorStr != null && !colorStr.isBlank()) {
            materia.setColor(Color.of(colorStr));
        }
        if (prioridad != null) {
            materia.setPrioridad(prioridad);
        }

        final Materia guardada = materiaRepository.save(materia);
        log.info("Materia editada: {}", guardada.getId());
        return guardada;
    }

    @Override
    public void eliminarMateria(final UUID id) {
        log.info("Eliminando (desactivando) materia: {}", id);

        final Materia materia = materiaRepository.findById(id)
                .orElseThrow(() -> new MateriaNotFoundException(id.toString()));

        materia.desactivar();
        materiaRepository.save(materia);

        log.info("Materia desactivada: {}", id);
    }

    @Override
    public List<Materia> listarMaterias() {
        return materiaRepository.findAll();
    }

    @Override
    public List<Materia> listarTodasLasMaterias() {
        return materiaRepository.findAllIncludingInactive();
    }

    @Override
    public Optional<Materia> obtenerMateria(final UUID id) {
        return materiaRepository.findById(id);
    }

    @Override
    public Optional<Materia> obtenerMateriaPorCodigo(final String codigo) {
        return materiaRepository.findByCodigo(CodigoMateria.of(codigo));
    }
}
