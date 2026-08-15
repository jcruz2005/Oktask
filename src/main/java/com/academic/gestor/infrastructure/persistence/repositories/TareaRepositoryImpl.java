package com.academic.gestor.infrastructure.persistence.repositories;

import com.academic.gestor.domain.model.entities.Tarea;
import com.academic.gestor.domain.model.enums.EstadoTarea;
import com.academic.gestor.domain.ports.outbound.TareaRepository;
import com.academic.gestor.infrastructure.persistence.entities.TareaEntity;
import com.academic.gestor.infrastructure.persistence.jpa.JpaTareaRepository;
import com.academic.gestor.infrastructure.persistence.mappers.TareaEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del repositorio de tareas usando JPA.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
@Repository
public class TareaRepositoryImpl implements TareaRepository {

    private final JpaTareaRepository jpaRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param jpaRepository repositorio JPA de Spring Data
     */
    public TareaRepositoryImpl(final JpaTareaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Tarea> findById(final UUID id) {
        return jpaRepository.findById(id)
                .map(TareaEntityMapper::toDomain);
    }

    @Override
    public List<Tarea> findByMateriaId(final UUID materiaId) {
        return jpaRepository.findByMateriaId(materiaId).stream()
                .map(TareaEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tarea> findByEstado(final EstadoTarea estado) {
        return jpaRepository.findByEstado(estado.name()).stream()
                .map(TareaEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Tarea> findAll() {
        return jpaRepository.findAll().stream()
                .map(TareaEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Tarea save(final Tarea tarea) {
        final TareaEntity entity = TareaEntityMapper.toEntity(tarea);
        final TareaEntity saved = jpaRepository.save(entity);
        return TareaEntityMapper.toDomain(saved);
    }

    @Override
    public void deleteById(final UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(final UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public long countByMateriaIdAndEstado(final UUID materiaId, final EstadoTarea estado) {
        return jpaRepository.countByMateriaIdAndEstado(materiaId, estado.name());
    }
}
