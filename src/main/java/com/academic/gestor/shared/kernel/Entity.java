package com.academic.gestor.shared.kernel;

import java.util.Objects;
import java.util.UUID;

/**
 * Clase base abstracta para entidades del dominio.
 *
 * <p>Proporciona comportamiento común para todas las entidades del sistema,
 * incluyendo identidad basada en UUID y comparación por igualdad.</p>
 *
 * @param <T> tipo de la entidad que extiende esta clase
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public abstract class Entity<T extends Entity<T>> {

    /**
     * Identificador único de la entidad.
     */
    protected final UUID id;

    /**
     * Constructor con ID proporcionado.
     *
     * @param id identificador único de la entidad, no puede ser nulo
     */
    protected Entity(final UUID id) {
        this.id = Objects.requireNonNull(id, "El ID de la entidad no puede ser nulo");
    }

    /**
     * Obtiene el identificador único de la entidad.
     *
     * @return UUID de la entidad
     */
    public UUID getId() {
        return id;
    }

    /**
     * Compara igualdad basada en el tipo y el ID.
     *
     * @param o objeto a comparar
     * @return true si son la misma entidad (mismo tipo y mismo ID)
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    /**
     * Calcula el hash code basado en el ID.
     *
     * @return hash code de la entidad
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Representación en cadena de la entidad.
     *
     * @return cadena con el tipo y el ID de la entidad
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + "}";
    }
}
