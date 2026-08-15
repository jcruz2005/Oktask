package com.academic.gestor.domain.model.valueobjects;

import com.academic.gestor.shared.kernel.ValueObject;

import java.util.Objects;

/**
 * Value Object que representa una duración en minutos.
 *
 * <p>La duración es siempre un valor positivo y proporciona
 * métodos de conversión a horas.</p>
 *
 * @param minutos cantidad de minutos
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public record DuracionMinutos(int minutos) implements ValueObject {

    /**
     * Constructor compacto con validación.
     *
     * @param minutos cantidad de minutos
     * @throws IllegalArgumentException si los minutos son negativos o cero
     */
    public DuracionMinutos {
        if (minutos <= 0) {
            throw new IllegalArgumentException(
                    "La duración en minutos debe ser mayor a 0. Valor recibido: " + minutos
            );
        }
    }

    /**
     * Crea una DuracionMinutos validada.
     *
     * @param minutos cantidad de minutos
     * @return nueva DuracionMinutos
     * @throws IllegalArgumentException si los minutos son negativos o cero
     */
    public static DuracionMinutos of(final int minutos) {
        return new DuracionMinutos(minutos);
    }

    /**
     * Convierte los minutos a horas con decimales.
     *
     * @return duración en horas
     */
    public double toHours() {
        return minutos / 60.0;
    }

    /**
     * Obtiene las horas completas de la duración.
     *
     * @return parte entera de las horas
     */
    public int toHoursInt() {
        return minutos / 60;
    }

    /**
     * Obtiene los minutos restantes después de convertir a horas.
     *
     * @return minutos restantes
     */
    public int remainingMinutes() {
        return minutos % 60;
    }

    /**
     * Suma otra duración a esta.
     *
     * @param otra otra duración a sumar
     * @return nueva DuracionMinutos con la suma
     */
    public DuracionMinutos sumar(final DuracionMinutos otra) {
        return new DuracionMinutos(this.minutos + otra.minutos);
    }

    /**
     * Formatea la duración como "Xh Ym" (ej: "2h 30m").
     *
     * @return cadena formateada
     */
    public String toFormattedString() {
        return toHoursInt() + "h " + remainingMinutes() + "m";
    }
}
