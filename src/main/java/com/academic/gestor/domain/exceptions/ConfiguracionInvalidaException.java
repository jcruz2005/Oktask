package com.academic.gestor.domain.exceptions;

/**
 * Excepción que se lanza cuando la configuración del Pomodoro es inválida.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public class ConfiguracionInvalidaException extends RuntimeException {

    /**
     * Construye la excepción con un mensaje descriptivo.
     *
     * @param mensaje mensaje de error
     */
    public ConfiguracionInvalidaException(final String mensaje) {
        super(mensaje);
    }

    /**
     * Construye la excepción con un mensaje y una causa.
     *
     * @param mensaje mensaje de error
     * @param cause causa de la excepción
     */
    public ConfiguracionInvalidaException(final String mensaje, final Throwable cause) {
        super(mensaje, cause);
    }
}
