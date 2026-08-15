package com.academic.gestor.infrastructure.persistence.repositories;

import com.academic.gestor.domain.model.entities.Materia;
import com.academic.gestor.domain.model.valueobjects.CodigoMateria;
import com.academic.gestor.domain.ports.outbound.MateriaRepository;
import com.academic.gestor.infrastructure.persistence.entities.MateriaEntity;
import com.academic.gestor.infrastructure.persistence.jpa.JpaMateriaRepository;
import com.academic.gestor.infrastructure.persistence.mappers.MateriaEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del repositorio de materias usando JPA.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
@Repository
public class MateriaRepositoryImpl implements MateriaRepository {

    private final JpaMateriaRepository jpaRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param jpaRepository repositorio JPA de Spring Data
     */
    public MateriaRepositoryImpl(final JpaMateriaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Materia> findById(final UUID id) {
        return jpaRepository.findById(id)
                .map(MateriaEntityMapper::toDomain);
    }

    @Override
    public Optional<Materia> findByCodigo(final CodigoMateria codigo) {
        return jpaRepository.findByCodigo(codigo.value())
                .map(MateriaEntityMapper::toDomain);
    }

    @Override
    public List<Materia> findAll() {
        return jpaRepository.findAll().stream()
                .filter(MateriaEntity::isActiva)
                .map(MateriaEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Materia> findAllIncludingInactive() {
        return jpaRepository.findAll().stream()
                .map(MateriaEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Materia save(final Materia materia) {
        final MateriaEntity entity = MateriaEntityMapper.toEntity(materia);
        final MateriaEntity saved = jpaRepository.save(entity);
        return MateriaEntityMapper.toDomain(saved);
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
    public boolean existsByCodigo(final CodigoMateria codigo) {
        return jpaRepository.existsByCodigo(codigo.value());
    }
}
