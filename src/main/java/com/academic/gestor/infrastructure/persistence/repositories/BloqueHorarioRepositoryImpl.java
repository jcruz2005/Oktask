package com.academic.gestor.infrastructure.persistence.repositories;

import com.academic.gestor.domain.model.entities.BloqueHorario;
import com.academic.gestor.domain.model.valueobjects.DiaSemana;
import com.academic.gestor.domain.ports.outbound.BloqueHorarioRepository;
import com.academic.gestor.infrastructure.persistence.entities.BloqueHorarioEntity;
import com.academic.gestor.infrastructure.persistence.jpa.JpaBloqueHorarioRepository;
import com.academic.gestor.infrastructure.persistence.mappers.BloqueHorarioEntityMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del repositorio de bloques horarios usando JPA.
 */
@Repository
public class BloqueHorarioRepositoryImpl implements BloqueHorarioRepository {

    private final JpaBloqueHorarioRepository jpaRepository;

    public BloqueHorarioRepositoryImpl(JpaBloqueHorarioRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<BloqueHorario> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(BloqueHorarioEntityMapper::toDomain);
    }

    @Override
    public List<BloqueHorario> findAll() {
        return jpaRepository.findAll().stream()
                .map(BloqueHorarioEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BloqueHorario> findByMateriaId(UUID materiaId) {
        return jpaRepository.findByMateriaId(materiaId).stream()
                .map(BloqueHorarioEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BloqueHorario> findByDiaSemana(DiaSemana diaSemana) {
        return jpaRepository.findByDiaSemana(diaSemana).stream()
                .map(BloqueHorarioEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BloqueHorario> findByMateriaIdAndDiaSemana(UUID materiaId, DiaSemana diaSemana) {
        return jpaRepository.findByMateriaIdAndDiaSemana(materiaId, diaSemana).stream()
                .map(BloqueHorarioEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BloqueHorario> findActivosByMateriaId(UUID materiaId) {
        return jpaRepository.findByMateriaIdAndActivoTrue(materiaId).stream()
                .map(BloqueHorarioEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BloqueHorario> findActivosByDiaSemana(DiaSemana diaSemana) {
        return jpaRepository.findByDiaSemanaAndActivoTrue(diaSemana).stream()
                .map(BloqueHorarioEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BloqueHorario> findConflictos(UUID materiaId, DiaSemana diaSemana,
                                              LocalTime horaInicio, LocalTime horaFin) {
        return jpaRepository.findConflictos(materiaId, diaSemana, horaInicio, horaFin).stream()
                .map(BloqueHorarioEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BloqueHorario> findAllActivos() {
        return jpaRepository.findByActivoTrue().stream()
                .map(BloqueHorarioEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public BloqueHorario save(BloqueHorario bloque) {
        BloqueHorarioEntity entity;
        if (jpaRepository.existsById(bloque.getId())) {
            entity = jpaRepository.findById(bloque.getId()).orElseThrow();
            BloqueHorarioEntityMapper.updateEntity(bloque, entity);
        } else {
            entity = BloqueHorarioEntityMapper.toEntity(bloque);
        }
        BloqueHorarioEntity saved = jpaRepository.save(entity);
        return BloqueHorarioEntityMapper.toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public long countByMateriaId(UUID materiaId) {
        return jpaRepository.countByMateriaId(materiaId);
    }
}