package com.academic.gestor.domain.model.enums;

/**
 * Enum que representa los posibles estados de una tarea.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public enum EstadoTarea {

    /**
     * La tarea aún no ha sido iniciada.
     */
    PENDIENTE("Pendiente"),

    /**
     * La tarea está en proceso de ejecución.
     */
    EN_PROGRESO("En Progreso"),

    /**
     * La tarea ha sido completada exitosamente.
     */
    COMPLETADA("Completada");

    private final String descripcion;

    EstadoTarea(final String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la descripción legible del estado.
     *
     * @return descripción del estado
     */
    public String getDescripcion() {
        return descripcion;
    }
}
