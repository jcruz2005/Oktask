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
     * Suma la duración total de sesiones completadas de trabajo para una materia.
     *
     * @param materiaId ID de la materia
     * @param tipoSesion tipo de sesión
     * @param completada si está completada
     * @return total de minutos
     */
    @Query("SELECT COALESCE(SUM(s.duracionMinutos), 0) FROM SesionPomodoroEntity s " +
            "WHERE s.materiaId = :materiaId AND s.tipoSesion = :tipoSesion AND s.completada = :completada")
    long sumDuracionByMateriaId(@Param("materiaId") UUID materiaId,
                                @Param("tipoSesion") String tipoSesion,
                                @Param("completada") boolean completada);

    /**
     * Suma la duración total de sesiones completadas de trabajo en un rango de fechas.
     *
     * @param tipoSesion tipo de sesión
     * @param completada si está completada
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return total de minutos
     */
    @Query("SELECT COALESCE(SUM(s.duracionMinutos), 0) FROM SesionPomodoroEntity s " +
            "WHERE s.tipoSesion = :tipoSesion AND s.completada = :completada " +
            "AND s.fechaInicio BETWEEN :fechaInicio AND :fechaFin")
    long sumDuracionByFechaInRange(@Param("tipoSesion") String tipoSesion,
                                   @Param("completada") boolean completada,
                                   @Param("fechaInicio") LocalDateTime fechaInicio,
                                   @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Suma la duración de sesiones de trabajo para una materia en un rango de fechas.
     *
     * @param materiaId ID de la materia
     * @param tipoSesion tipo de sesión
     * @param completada si está completada
     * @param fechaInicio fecha de inicio
     * @param fechaFin fecha de fin
     * @return total de minutos
     */
    @Query("SELECT COALESCE(SUM(s.duracionMinutos), 0) FROM SesionPomodoroEntity s " +
            "WHERE s.materiaId = :materiaId AND s.tipoSesion = :tipoSesion " +
            "AND s.completada = :completada " +
            "AND s.fechaInicio BETWEEN :fechaInicio AND :fechaFin")
    long sumDuracionByMateriaIdAndFechaInRange(@Param("materiaId") UUID materiaId,
                                                @Param("tipoSesion") String tipoSesion,
                                                @Param("completada") boolean completada,
                                                @Param("fechaInicio") LocalDateTime fechaInicio,
                                                @Param("fechaFin") LocalDateTime fechaFin);
}
