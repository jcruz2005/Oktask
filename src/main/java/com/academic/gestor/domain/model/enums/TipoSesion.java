package com.academic.gestor.domain.model.enums;

/**
 * Enum que representa los tipos de sesión de Pomodoro.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public enum TipoSesion {

    /**
     * Sesión de trabajo concentrado (típicamente 25 minutos).
     */
    TRABAJO("Trabajo"),

    /**
     * Sesión de descanso corto (típicamente 5 minutos).
     */
    DESCANSO("Descanso");

    private final String descripcion;

    TipoSesion(final String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la descripción legible del tipo de sesión.
     *
     * @return descripción del tipo de sesión
     */
    public String getDescripcion() {
        return descripcion;
    }
}
