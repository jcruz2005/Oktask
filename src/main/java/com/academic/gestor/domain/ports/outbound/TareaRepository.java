package com.academic.gestor.domain.ports.outbound;

import com.academic.gestor.domain.model.entities.Tarea;
import com.academic.gestor.domain.model.enums.EstadoTarea;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para la persistencia de tareas.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public interface TareaRepository {

    /**
     * Busca una tarea por su ID.
     *
     * @param id identificador de la tarea
     * @return optional con la tarea encontrada
     */
    Optional<Tarea> findById(UUID id);

    /**
     * Obtiene todas las tareas de una materia específica.
     *
     * @param materiaId ID de la materia
     * @return lista de tareas de la materia
     */
    List<Tarea> findByMateriaId(UUID materiaId);

    /**
     * Obtiene todas las tareas con un estado específico.
     *
     * @param estado estado de las tareas
     * @return lista de tareas con el estado dado
     */
    List<Tarea> findByEstado(EstadoTarea estado);

    /**
     * Obtiene todas las tareas.
     *
     * @return lista completa de tareas
     */
    List<Tarea> findAll();

    /**
     * Guarda una tarea (crea o actualiza).
     *
     * @param tarea tarea a guardar
     * @return tarea guardada
     */
    Tarea save(Tarea tarea);

    /**
     * Elimina una tarea por su ID.
     *
     * @param id identificador de la tarea a eliminar
     */
    void deleteById(UUID id);

    /**
     * Verifica si existe una tarea con el ID dado.
     *
     * @param id identificador a verificar
     * @return true si existe
     */
    boolean existsById(UUID id);

    /**
     * Cuenta las tareas de una materia con un estado específico.
     *
     * @param materiaId ID de la materia
     * @param estado estado de las tareas
     * @return cantidad de tareas
     */
    long countByMateriaIdAndEstado(UUID materiaId, EstadoTarea estado);
}
