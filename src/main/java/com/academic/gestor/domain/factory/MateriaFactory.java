package com.academic.gestor.domain.factory;

import com.academic.gestor.domain.model.entities.Materia;
import com.academic.gestor.domain.model.enums.Prioridad;
import com.academic.gestor.domain.model.valueobjects.Color;
import com.academic.gestor.domain.model.valueobjects.CodigoMateria;

/**
 * Factory para la creación de entidades Materia.
 *
 * <p>Centraliza la lógica de creación de materias, asegurando que
 * todas las instancias se creen con valores válidos y consistentes.</p>
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public final class MateriaFactory {

    private MateriaFactory() {
        throw new AssertionError("No se pueden instanciar factories");
    }

    /**
     * Crea una nueva materia con los datos proporcionados.
     *
     * @param nombre nombre de la materia
     * @param codigoStr código de la materia como cadena
     * @param colorStr color hexadecimal de la materia
     * @param prioridad prioridad de la materia
     * @return nueva instancia de Materia
     * @throws IllegalArgumentException si algún dato es inválido
     */
    public static Materia create(final String nombre, final String codigoStr,
                                 final String colorStr, final Prioridad prioridad) {
        final CodigoMateria codigo = CodigoMateria.of(codigoStr);
        final Color color = Color.of(colorStr);
        return Materia.create(nombre, codigo, color, prioridad);
    }

    /**
     * Reconstruye una materia existente desde sus componentes individuales.
     * Útil para la deserialización desde persistencia.
     *
     * @param id ID de la materia
     * @param nombre nombre de la materia
     * @param codigoStr código de la materia como cadena
     * @param colorStr color hexadecimal
     * @param prioridad prioridad de la materia
     * @param fechaCreacionStr fecha de creación como cadena ISO
     * @param activa si la materia está activa
     * @return instancia de Materia reconstruida
     */
    public static Materia reconstruct(final java.util.UUID id, final String nombre,
                                      final String codigoStr, final String colorStr,
                                      final Prioridad prioridad,
                                      final java.time.LocalDateTime fechaCreacion,
                                      final boolean activa) {
        final CodigoMateria codigo = new CodigoMateria(codigoStr);
        final Color color = new Color(colorStr);
        return new Materia(id, nombre, codigo, color, prioridad, fechaCreacion, activa);
    }
}
