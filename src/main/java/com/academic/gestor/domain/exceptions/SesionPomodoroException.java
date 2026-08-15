package com.academic.gestor.domain.exceptions;

/**
 * Excepción que se lanza cuando hay un error en una sesión de Pomodoro.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public class SesionPomodoroException extends RuntimeException {

    /**
     * Construye la excepción con un mensaje descriptivo.
     *
     * @param mensaje mensaje de error
     */
    public SesionPomodoroException(final String mensaje) {
        super(mensaje);
    }

    /**
     * Construye la excepción con un mensaje y una causa.
     *
     * @param mensaje mensaje de error
     * @param cause causa de la excepción
     */
    public SesionPomodoroException(final String mensaje, final Throwable cause) {
        super(mensaje, cause);
    }
}
