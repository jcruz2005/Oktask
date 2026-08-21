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
     * Patrón estricto de UUID canónico (8-4-4-4-12, solo hexadecimal).
     */
    private static final java.util.regex.Pattern UUID_PATTERN =
            java.util.regex.Pattern.compile(
                    "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
            );

    /**
     * Valida si una cadena es un UUID canónico válido.
     *
     * <p>{@link UUID#fromString(String)} acepta formatos laxos
     * (p.ej. {@code 1-1-1-1-1}); este método exige el formato
     * canónico 8-4-4-4-12 con caracteres hexadecimales.</p>
     *
     * @param value cadena a validar
     * @return true si es un UUID válido, false de lo contrario
     */
    public static boolean isValid(final String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        return UUID_PATTERN.matcher(value.trim()).matches();
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
