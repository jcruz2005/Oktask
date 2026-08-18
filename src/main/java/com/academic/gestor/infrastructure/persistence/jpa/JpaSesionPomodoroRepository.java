package com.academic.gestor.infrastructure.persistence.jpa;

import com.academic.gestor.infrastructure.persistence.entities.SesionPomodoroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
