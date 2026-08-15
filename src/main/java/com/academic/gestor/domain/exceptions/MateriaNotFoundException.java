package com.academic.gestor.domain.exceptions;

/**
 * Excepción que se lanza cuando no se encuentra una materia.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public class MateriaNotFoundException extends RuntimeException {

    private final String materiaId;

    /**
     * Construye la excepción con el ID de la materia no encontrada.
     *
     * @param materiaId ID de la materia que no fue encontrada
     */
    public MateriaNotFoundException(final String materiaId) {
        super("Materia no encontrada con ID: " + materiaId);
        this.materiaId = materiaId;
    }

    /**
     * Construye la excepción con el código de la materia no encontrada.
     *
     * @param codigo código de la materia que no fue encontrada
     * @param byCode true si se buscó por código
     */
    public MateriaNotFoundException(final String codigo, final boolean byCode) {
        super("Materia no encontrada con código: " + codigo);
        this.materiaId = codigo;
    }

    /**
     * Obtiene el ID de la materia no encontrada.
     *
     * @return ID de la materia
     */
    public String getMateriaId() {
        return materiaId;
    }
}
