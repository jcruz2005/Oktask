package com.academic.gestor.domain.model.valueobjects;

import com.academic.gestor.shared.kernel.ValueObject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object que representa el código único de una materia.
 *
 * <p>El código debe seguir un formato alfanumérico específico (ej: "MAT001", "PROG2").</p>
 *
 * @param value cadena que representa el código de la materia
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public record CodigoMateria(String value) implements ValueObject {

    /**
     * Patrón para validar el formato del código de materia.
     * Acepta entre 3 y 10 caracteres alfanuméricos en mayúsculas.
     */
    private static final Pattern CODIGO_PATTERN = Pattern.compile("^[A-Z0-9]{3,10}$");

    /**
     * Constructor compacto con validación de formato.
     *
     * @param value código de la materia
     * @throws NullPointerException si el valor es nulo
     * @throws IllegalArgumentException si el valor no cumple el formato
     */
    public CodigoMateria {
        Objects.requireNonNull(value, "El código de materia no puede ser nulo");
        if (value.isBlank()) {
            throw new IllegalArgumentException("El código de materia no puede estar vacío");
        }
        if (!CODIGO_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException(
                    "El código de materia '" + value + "' no tiene un formato válido. " +
                    "Debe tener entre 3 y 10 caracteres alfanuméricos en mayúsculas."
            );
        }
    }

    /**
     * Crea un nuevo CodigoMateria desde una cadena, haciendo trim y convirtiendo a mayúsculas.
     *
     * @param value cadena con el código
     * @return nuevo CodigoMateria
     * @throws IllegalArgumentException si el valor no cumple el formato
     */
    public static CodigoMateria of(final String value) {
        return new CodigoMateria(value.trim().toUpperCase());
    }
}
