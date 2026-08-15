package com.academic.gestor.domain.ports.inbound;

import com.academic.gestor.domain.model.entities.Tarea;
import com.academic.gestor.domain.model.enums.EstadoTarea;
import com.academic.gestor.domain.model.enums.Prioridad;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de entrada para el caso de uso de gestión de tareas.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public interface TareaService {

    /**
     * Crea una nueva tarea.
     *
     * @param titulo título de la tarea
     * @param descripcion descripción de la tarea
     * @param materiaId ID de la materia asociada
     * @param fechaLimite fecha límite de entrega
     * @param prioridad prioridad de la tarea
     * @return tarea creada
     * @throws com.academic.gestor.domain.exceptions.MateriaNotFoundException si la materia no existe
     */
    Tarea crearTarea(String titulo, String descripcion, UUID materiaId,
                     LocalDate fechaLimite, Prioridad prioridad);

    /**
     * Edita una tarea existente.
     *
     * @param id ID de la tarea a editar
     * @param titulo nuevo título (puede ser null para no cambiar)
     * @param descripcion nueva descripción (puede ser null para no cambiar)
     * @param fechaLimite nueva fecha límite (puede ser null para no cambiar)
     * @param prioridad nueva prioridad (puede ser null para no cambiar)
     * @return tarea editada
     * @throws com.academic.gestor.domain.exceptions.TareaNotFoundException si no se encuentra
     */
    Tarea editarTarea(UUID id, String titulo, String descripcion,
                      LocalDate fechaLimite, Prioridad prioridad);

    /**
     * Cambia el estado de una tarea.
     *
     * @param id ID de la tarea
     * @param nuevoEstado nuevo estado
     * @return tarea con el estado actualizado
     * @throws com.academic.gestor.domain.exceptions.TareaNotFoundException si no se encuentra
     */
    Tarea cambiarEstado(UUID id, EstadoTarea nuevoEstado);

    /**
     * Elimina una tarea por su ID.
     *
     * @param id ID de la tarea a eliminar
     * @throws com.academic.gestor.domain.exceptions.TareaNotFoundException si no se encuentra
     */
    void eliminarTarea(UUID id);

    /**
     * Lista todas las tareas.
     *
     * @return lista de tareas
     */
    List<Tarea> listarTareas();

    /**
     * Lista todas las tareas de una materia específica.
     *
     * @param materiaId ID de la materia
     * @return lista de tareas de la materia
     */
    List<Tarea> listarTareasPorMateria(UUID materiaId);

    /**
     * Lista todas las tareas con un estado específico.
     *
     * @param estado estado de las tareas
     * @return lista de tareas con el estado dado
     */
    List<Tarea> listarTareasPorEstado(EstadoTarea estado);

    /**
     * Obtiene una tarea por su ID.
     *
     * @param id ID de la tarea
     * @return optional con la tarea encontrada
     */
    Optional<Tarea> obtenerTarea(UUID id);
}
