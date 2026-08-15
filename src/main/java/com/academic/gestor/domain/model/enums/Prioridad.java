package com.academic.gestor.domain.model.enums;

/**
 * Enum que representa los niveles de prioridad para materias y tareas.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public enum Prioridad {

    /**
     * Prioridad alta. Indica que el elemento requiere atención inmediata.
     */
    ALTA("Alta"),

    /**
     * Prioridad media. Indica que el elemento es importante pero no urgente.
     */
    MEDIA("Media"),

    /**
     * Prioridad baja. Indica que el elemento puede esperar.
     */
    BAJA("Baja");

    private final String descripcion;

    Prioridad(final String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la descripción legible de la prioridad.
     *
     * @return descripción de la prioridad
     */
    public String getDescripcion() {
        return descripcion;
    }
}
