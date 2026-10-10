package com.academic.gestor.domain.model.valueobjects;

import com.academic.gestor.shared.kernel.ValueObject;

import java.util.Arrays;
import java.util.List;

/**
 * Value Object que representa un día de la semana.
 * Inmutable y con validación de valores permitidos.
 */
public class DiaSemana implements ValueObject {

    public static final List<String> VALORES_VALIDOS = List.of(
            "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO", "DOMINGO"
    );

    private final String valor;

    private DiaSemana(String valor) {
        this.valor = valor;
    }

    public static DiaSemana of(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El día de la semana no puede ser nulo o vacío");
        }
        String normalizado = valor.trim().toUpperCase();
        if (!VALORES_VALIDOS.contains(normalizado)) {
            throw new IllegalArgumentException(
                    "Día de semana inválido: " + valor + ". Valores permitidos: " + VALORES_VALIDOS);
        }
        return new DiaSemana(normalizado);
    }

    public static DiaSemana fromJavaDayOfWeek(java.time.DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> of("LUNES");
            case TUESDAY -> of("MARTES");
            case WEDNESDAY -> of("MIERCOLES");
            case THURSDAY -> of("JUEVES");
            case FRIDAY -> of("VIERNES");
            case SATURDAY -> of("SABADO");
            case SUNDAY -> of("DOMINGO");
        };
    }

    public String getValor() {
        return valor;
    }

    public int getOrden() {
        return VALORES_VALIDOS.indexOf(valor);
    }

    public boolean esLaboral() {
        return getOrden() < 5; // LUNES a VIERNES
    }

    public boolean sameValueAs(DiaSemana other) {
        return other != null && this.valor.equals(other.valor);
    }

    @Override
    public String toString() {
        return valor;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        DiaSemana that = (DiaSemana) obj;
        return valor.equals(that.valor);
    }

    @Override
    public int hashCode() {
        return valor.hashCode();
    }
}