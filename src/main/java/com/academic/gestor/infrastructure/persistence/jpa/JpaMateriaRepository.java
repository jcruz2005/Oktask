package com.academic.gestor.infrastructure.persistence.jpa;

import com.academic.gestor.infrastructure.persistence.entities.MateriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad MateriaEntity.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
@Repository
public interface JpaMateriaRepository extends JpaRepository<MateriaEntity, UUID> {

    /**
     * Busca una materia por su código.
     *
     * @param codigo código de la materia
     * @return optional con la entidad encontrada
     */
    Optional<MateriaEntity> findByCodigo(String codigo);

    /**
     * Verifica si existe una materia con el código dado.
     *
     * @param codigo código a verificar
     * @return true si existe
     */
    boolean existsByCodigo(String codigo);
}
