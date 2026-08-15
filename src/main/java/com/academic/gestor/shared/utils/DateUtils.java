package com.academic.gestor.shared.utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilidades para operaciones con fechas y horas.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public final class DateUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateUtils() {
        throw new AssertionError("No se pueden instanciar utilidades");
    }

    /**
     * Obtiene la fecha y hora actual.
     *
     * @return fecha y hora actual
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * Obtiene la fecha actual.
     *
     * @return fecha actual
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * Calcula la duración en minutos entre dos fechas y horas.
     *
     * @param inicio fecha y hora de inicio
     * @param fin fecha y hora de fin
     * @return duración en minutos redondeada al entero más cercano
     */
    public static long minutesBetween(final LocalDateTime inicio, final LocalDateTime fin) {
        final Duration duration = Duration.between(inicio, fin);
        return duration.toMinutes();
    }

    /**
     * Calcula la duración en horas entre dos fechas y horas.
     *
     * @param inicio fecha y hora de inicio
     * @param fin fecha y hora de fin
     * @return duración en horas con decimales
     */
    public static double hoursBetween(final LocalDateTime inicio, final LocalDateTime fin) {
        final Duration duration = Duration.between(inicio, fin);
        return duration.toHours() + (duration.toMinutesPart() / 60.0);
    }

    /**
     * Formatea una fecha como cadena con el formato yyyy-MM-dd.
     *
     * @param date fecha a formatear
     * @return cadena formateada
     */
    public static String formatDate(final LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    /**
     * Formatea una fecha y hora como cadena con el formato yyyy-MM-dd HH:mm:ss.
     *
     * @param dateTime fecha y hora a formatear
     * @return cadena formateada
     */
    public static String formatDateTime(final LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMATTER);
    }

    /**
     * Obtiene el inicio del día (00:00:00) para una fecha dada.
     *
     * @param date fecha
     * @return fecha y hora al inicio del día
     */
    public static LocalDateTime startOfDay(final LocalDate date) {
        return date.atStartOfDay();
    }

    /**
     * Obtiene el final del día (23:59:59) para una fecha dada.
     *
     * @param date fecha
     * @return fecha y hora al final del día
     */
    public static LocalDateTime endOfDay(final LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }
}
