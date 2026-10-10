package com.academic.gestor.infrastructure.persistence.jpa;

import com.academic.gestor.domain.model.valueobjects.DiaSemana;
import com.academic.gestor.infrastructure.persistence.entities.BloqueHorarioEntity;
import com.academic.gestor.infrastructure.persistence.entities.MateriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para BloqueHorarioEntity.
 */
@Repository
public interface JpaBloqueHorarioRepository extends JpaRepository<BloqueHorarioEntity, UUID> {

    Optional<BloqueHorarioEntity> findById(UUID id);

    List<BloqueHorarioEntity> findByMateriaId(UUID materiaId);

    List<BloqueHorarioEntity> findByDiaSemana(DiaSemana diaSemana);

    List<BloqueHorarioEntity> findByMateriaIdAndDiaSemana(UUID materiaId, DiaSemana diaSemana);

    List<BloqueHorarioEntity> findByMateriaIdAndActivoTrue(UUID materiaId);

    List<BloqueHorarioEntity> findByDiaSemanaAndActivoTrue(DiaSemana diaSemana);

    List<BloqueHorarioEntity> findByActivoTrue();

    @Query("SELECT b FROM BloqueHorarioEntity b " +
           "WHERE b.materia.id = :materiaId " +
           "AND b.diaSemana = :diaSemana " +
           "AND b.activo = true " +
           "AND b.horaInicio < :horaFin " +
           "AND b.horaFin > :horaInicio")
    List<BloqueHorarioEntity> findConflictos(@Param("materiaId") UUID materiaId,
                                              @Param("diaSemana") DiaSemana diaSemana,
                                              @Param("horaInicio") LocalTime horaInicio,
                                              @Param("horaFin") LocalTime horaFin);

    boolean existsById(UUID id);

    long countByMateriaId(UUID materiaId);

    void deleteById(UUID id);
}