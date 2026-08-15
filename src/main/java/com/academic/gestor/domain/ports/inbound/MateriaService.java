package com.academic.gestor.domain.ports.inbound;

import com.academic.gestor.domain.model.entities.Materia;
import com.academic.gestor.domain.model.enums.Prioridad;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de entrada para el caso de uso de gestión de materias.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public interface MateriaService {

    /**
     * Crea una nueva materia.
     *
     * @param nombre nombre de la materia
     * @param codigo código de la materia
     * @param colorStr color hexadecimal
     * @param prioridad prioridad de la materia
     * @return materia creada
     * @throws IllegalArgumentException si el código ya existe o los datos son inválidos
     */
    Materia crearMateria(String nombre, String codigo, String colorStr, Prioridad prioridad);

    /**
     * Edita una materia existente.
     *
     * @param id ID de la materia a editar
     * @param nombre nuevo nombre (puede ser null para no cambiar)
     * @param colorStr nuevo color (puede ser null para no cambiar)
     * @param prioridad nueva prioridad (puede ser null para no cambiar)
     * @return materia editada
     * @throws com.academic.gestor.domain.exceptions.MateriaNotFoundException si no se encuentra
     */
    Materia editarMateria(UUID id, String nombre, String colorStr, Prioridad prioridad);

    /**
     * Elimina una materia por soft delete (desactiva).
     *
     * @param id ID de la materia a eliminar
     * @throws com.academic.gestor.domain.exceptions.MateriaNotFoundException si no se encuentra
     */
    void eliminarMateria(UUID id);

    /**
     * Lista todas las materias activas.
     *
     * @return lista de materias activas
     */
    List<Materia> listarMaterias();

    /**
     * Lista todas las materias (activas e inactivas).
     *
     * @return lista completa de materias
     */
    List<Materia> listarTodasLasMaterias();

    /**
     * Obtiene una materia por su ID.
     *
     * @param id ID de la materia
     * @return optional con la materia encontrada
     */
    Optional<Materia> obtenerMateria(UUID id);

    /**
     * Obtiene una materia por su código.
     *
     * @param codigo código de la materia
     * @return optional con la materia encontrada
     */
    Optional<Materia> obtenerMateriaPorCodigo(String codigo);
}
