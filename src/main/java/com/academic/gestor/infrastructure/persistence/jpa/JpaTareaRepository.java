package com.academic.gestor.infrastructure.persistence.jpa;

import com.academic.gestor.infrastructure.persistence.entities.TareaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad TareaEntity.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
@Repository
public interface JpaTareaRepository extends JpaRepository<TareaEntity, UUID> {

    /**
     * Busca todas las tareas de una materia.
     *
     * @param materiaId ID de la materia
     * @return lista de tareas de la materia
     */
    List<TareaEntity> findByMateriaId(UUID materiaId);

    /**
     * Busca todas las tareas con un estado específico.
     *
     * @param estado estado de las tareas
     * @return lista de tareas con el estado dado
     */
    List<TareaEntity> findByEstado(String estado);

    /**
     * Cuenta las tareas de una materia con un estado específico.
     *
     * @param materiaId ID de la materia
     * @param estado estado de las tareas
     * @return cantidad de tareas
     */
    long countByMateriaIdAndEstado(UUID materiaId, String estado);
}
