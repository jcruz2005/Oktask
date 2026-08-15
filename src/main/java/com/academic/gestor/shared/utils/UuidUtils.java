package com.academic.gestor.shared.utils;

import java.util.UUID;

/**
 * Utilidades para operaciones con UUID.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public final class UuidUtils {

    private UuidUtils() {
        throw new AssertionError("No se pueden instanciar utilidades");
    }

    /**
     * Genera un nuevo UUID aleatorio.
     *
     * @return UUID único generado
     */
    public static UUID generate() {
        return UUID.randomUUID();
    }

    /**
     * Valida si una cadena es un UUID válido.
     *
     * @param value cadena a validar
     * @return true si es un UUID válido, false de lo contrario
     */
    public static boolean isValid(final String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        try {
            UUID.fromString(value);
            return true;
        } catch (final IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Convierte una cadena a UUID, lanzando excepción si no es válido.
     *
     * @param value cadena a convertir
     * @return UUID parseado
     * @throws IllegalArgumentException si la cadena no es un UUID válido
     */
    public static UUID fromString(final String value) {
        if (!isValid(value)) {
            throw new IllegalArgumentException("UUID inválido: " + value);
        }
        return UUID.fromString(value);
    }
}
