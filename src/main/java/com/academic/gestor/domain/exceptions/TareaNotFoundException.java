package com.academic.gestor.domain.exceptions;

/**
 * Excepción que se lanza cuando no se encuentra una tarea.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public class TareaNotFoundException extends RuntimeException {

    private final String tareaId;

    /**
     * Construye la excepción con el ID de la tarea no encontrada.
     *
     * @param tareaId ID de la tarea que no fue encontrada
     */
    public TareaNotFoundException(final String tareaId) {
        super("Tarea no encontrada con ID: " + tareaId);
        this.tareaId = tareaId;
    }

    /**
     * Obtiene el ID de la tarea no encontrada.
     *
     * @return ID de la tarea
     */
    public String getTareaId() {
        return tareaId;
    }
}
