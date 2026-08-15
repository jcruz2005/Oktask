package com.academic.gestor.domain.ports.outbound;

import com.academic.gestor.domain.model.entities.Materia;
import com.academic.gestor.domain.model.valueobjects.CodigoMateria;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para la persistencia de materias.
 *
 * <p>Define las operaciones disponibles para acceder y manipular
 * datos de materias en el almacenamiento persistente.</p>
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public interface MateriaRepository {

    /**
     * Busca una materia por su ID.
     *
     * @param id identificador de la materia
     * @return optional con la materia encontrada
     */
    Optional<Materia> findById(UUID id);

    /**
     * Busca una materia por su código.
     *
     * @param codigo código de la materia
     * @return optional con la materia encontrada
     */
    Optional<Materia> findByCodigo(CodigoMateria codigo);

    /**
     * Obtiene todas las materias activas.
     *
     * @return lista de materias activas
     */
    List<Materia> findAll();

    /**
     * Obtiene todas las materias (activas e inactivas).
     *
     * @return lista completa de materias
     */
    List<Materia> findAllIncludingInactive();

    /**
     * Guarda una materia (crea o actualiza).
     *
     * @param materia materia a guardar
     * @return materia guardada
     */
    Materia save(Materia materia);

    /**
     * Elimina una materia por su ID.
     *
     * @param id identificador de la materia a eliminar
     */
    void deleteById(UUID id);

    /**
     * Verifica si existe una materia con el ID dado.
     *
     * @param id identificador a verificar
     * @return true si existe
     */
    boolean existsById(UUID id);

    /**
     * Verifica si existe una materia con el código dado.
     *
     * @param codigo código a verificar
     * @return true si existe
     */
    boolean existsByCodigo(CodigoMateria codigo);
}
