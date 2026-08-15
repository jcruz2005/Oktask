package com.academic.gestor.domain.model.valueobjects;

import com.academic.gestor.shared.kernel.ValueObject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object que representa un color en formato hexadecimal.
 *
 * <p>El color debe seguir el formato #RRGGBB (ej: "#FF5733", "#3498DB").</p>
 *
 * @param hex cadena que representa el color en hexadecimal
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public record Color(String hex) implements ValueObject {

    /**
     * Patrón para validar el formato hexadecimal del color.
     */
    private static final Pattern HEX_PATTERN = Pattern.compile("^#[0-9A-Fa-f]{6}$");

    /**
     * Constructor compacto con validación de formato.
     *
     * @param hex cadena hexadecimal del color
     * @throws NullPointerException si el valor es nulo
     * @throws IllegalArgumentException si el valor no cumple el formato #RRGGBB
     */
    public Color {
        Objects.requireNonNull(hex, "El color no puede ser nulo");
        if (!HEX_PATTERN.matcher(hex).matches()) {
            throw new IllegalArgumentException(
                    "El color '" + hex + "' no tiene un formato válido. " +
                    "Debe seguir el formato #RRGGBB."
            );
        }
    }

    /**
     * Crea un nuevo Color desde una cadena, asegurando el prefijo #.
     *
     * @param value cadena del color (con o sin #)
     * @return nuevo Color
     * @throws IllegalArgumentException si el valor no es válido
     */
    public static Color of(final String value) {
        final String normalized = value.startsWith("#") ? value : "#" + value;
        return new Color(normalized.toUpperCase());
    }

    /**
     * Obtiene el componente rojo del color.
     *
     * @return valor del componente rojo (0-255)
     */
    public int getRed() {
        return Integer.parseInt(hex.substring(1, 3), 16);
    }

    /**
     * Obtiene el componente verde del color.
     *
     * @return valor del componente verde (0-255)
     */
    public int getGreen() {
        return Integer.parseInt(hex.substring(3, 5), 16);
    }

    /**
     * Obtiene el componente azul del color.
     *
     * @return valor del componente azul (0-255)
     */
    public int getBlue() {
        return Integer.parseInt(hex.substring(5, 7), 16);
    }
}
