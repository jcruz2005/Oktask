package com.academic.gestor.domain.ports.outbound;

import com.academic.gestor.domain.model.entities.BloqueHorario;
import com.academic.gestor.domain.model.valueobjects.DiaSemana;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para el repositorio de bloques horarios.
 * Define las operaciones de persistencia sin acoplar a tecnología específica.
 */
public interface BloqueHorarioRepository {

    Optional<BloqueHorario> findById(UUID id);

    List<BloqueHorario> findAll();

    List<BloqueHorario> findByMateriaId(UUID materiaId);

    List<BloqueHorario> findByDiaSemana(DiaSemana diaSemana);

    List<BloqueHorario> findByMateriaIdAndDiaSemana(UUID materiaId, DiaSemana diaSemana);

    List<BloqueHorario> findActivosByMateriaId(UUID materiaId);

    List<BloqueHorario> findActivosByDiaSemana(DiaSemana diaSemana);

    List<BloqueHorario> findConflictos(UUID materiaId, DiaSemana diaSemana,
                                       LocalTime horaInicio, LocalTime horaFin);

    List<BloqueHorario> findAllActivos();

    BloqueHorario save(BloqueHorario bloque);

    void deleteById(UUID id);

    boolean existsById(UUID id);

    long countByMateriaId(UUID materiaId);
}