package com.academic.gestor.infrastructure.persistence.jpa;

import com.academic.gestor.infrastructure.persistence.entities.SesionPomodoroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad SesionPomodoroEntity.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
@Repository
public interface JpaSesionPomodoroRepository extends JpaRepository<SesionPomodoroEntity, UUID> {

    /**
     * Busca todas las sesiones de una tarea.
     *
     * @param tareaId ID de la tarea
     * @return lista de sesiones de la tarea
     */
    List<SesionPomodoroEntity> findByTareaId(UUID tareaId);

    /**
     * Busca todas las sesiones de una materia.
     *
     * @param materiaId ID de la materia
     * @return lista de sesiones de la materia
     */
    List<SesionPomodoroEntity> findByMateriaId(UUID materiaId);

    /**
     * Busca sesiones completadas de tipo TRABAJO en un rango de fechas.
     *
     * @param tipoSesion tipo de sesión
     * @param completada si está completada
     * @param fechaInicio fecha de inicio del rango
     * @param fechaFin fecha de fin del rango
     * @return lista de sesiones en el rango
     */
    List<SesionPomodoroEntity> findByTipoSesionAndCompletadaAndFechaInicioBetween(
            String tipoSesion, boolean completada,
            LocalDateTime fechaInicio, LocalDateTime fechaFin
    );

    /**
     * Suma la duración REAL (fechaFin - fechaInicio) de sesiones completadas de trabajo para una materia.
     *
     * @param materiaId ID de la materia
     * @return total de minutos reales estudiados
     */
    @Query(value = "SELECT COALESCE(SUM(CAST((julianday(s.fecha_fin) - julianday(s.fecha_inicio)) * 24 * 60 AS INTEGER)), 0) " +
            "FROM sesiones_pomodoro s " +
            "WHERE s.materia_id = :materiaId AND s.tipo_sesion = 'TRABAJO' AND s.completada = 1 " +
            "AND s.fecha_fin IS NOT NULL",
            nativeQuery = true)
    long sumDuracionRealByMateriaId(@Param("materiaId") UUID materiaId);

    /**
     * Suma la duración REAL (fechaFin - fechaInicio) de sesiones completadas de trabajo en un rango de fechas.
     *
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return total de minutos reales estudiados
     */
    @Query(value = "SELECT COALESCE(SUM(CAST((julianday(s.fecha_fin) - julianday(s.fecha_inicio)) * 24 * 60 AS INTEGER)), 0) " +
            "FROM sesiones_pomodoro s " +
            "WHERE s.tipo_sesion = 'TRABAJO' AND s.completada = 1 " +
            "AND s.fecha_fin IS NOT NULL " +
            "AND s.fecha_inicio BETWEEN :fechaInicio AND :fechaFin",
            nativeQuery = true)
    long sumDuracionRealByFechaInRange(@Param("fechaInicio") LocalDateTime fechaInicio,
                                       @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Suma la duración REAL (fechaFin - fechaInicio) de sesiones de trabajo para una materia en un rango de fechas.
     *
     * @param materiaId ID de la materia
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return total de minutos reales estudiados
     */
    @Query(value = "SELECT COALESCE(SUM(CAST((julianday(s.fecha_fin) - julianday(s.fecha_inicio)) * 24 * 60 AS INTEGER)), 0) " +
            "FROM sesiones_pomodoro s " +
            "WHERE s.materia_id = :materiaId AND s.tipo_sesion = 'TRABAJO' " +
            "AND s.completada = 1 AND s.fecha_fin IS NOT NULL " +
            "AND s.fecha_inicio BETWEEN :fechaInicio AND :fechaFin",
            nativeQuery = true)
    long sumDuracionRealByMateriaIdAndFechaInRange(@Param("materiaId") UUID materiaId,
                                                   @Param("fechaInicio") LocalDateTime fechaInicio,
                                                   @Param("fechaFin") LocalDateTime fechaFin);
}
