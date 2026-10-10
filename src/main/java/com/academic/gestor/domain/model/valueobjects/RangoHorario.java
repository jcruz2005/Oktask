package com.academic.gestor.domain.model.valueobjects;

import com.academic.gestor.shared.kernel.ValueObject;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Value Object que representa un rango horario (inicio - fin).
 * Inmutable, valida que inicio < fin y formato correcto.
 */
public class RangoHorario implements ValueObject {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final LocalTime inicio;
    private final LocalTime fin;

    RangoHorario(LocalTime inicio, LocalTime fin) {
        this.inicio = inicio;
        this.fin = fin;
    }

    public static RangoHorario of(LocalTime inicio, LocalTime fin) {
        if (inicio == null || fin == null) {
            throw new IllegalArgumentException("Inicio y fin no pueden ser nulos");
        }
        if (!inicio.isBefore(fin)) {
            throw new IllegalArgumentException("La hora de inicio debe ser anterior a la hora de fin");
        }
        return new RangoHorario(inicio, fin);
    }

    public static RangoHorario of(String inicioStr, String finStr) {
        return of(LocalTime.parse(inicioStr, FORMATTER), LocalTime.parse(finStr, FORMATTER));
    }

    public LocalTime getInicio() {
        return inicio;
    }

    public LocalTime getFin() {
        return fin;
    }

    public int getDuracionMinutos() {
        return (int) java.time.Duration.between(inicio, fin).toMinutes();
    }

    public boolean contiene(LocalTime hora) {
        return !hora.isBefore(inicio) && hora.isBefore(fin);
    }

    public boolean solapaCon(RangoHorario otro) {
        return this.inicio.isBefore(otro.fin) && this.fin.isAfter(otro.inicio);
    }

    public boolean estaDentroDe(RangoHorario rango) {
        return !this.inicio.isBefore(rango.inicio) && !this.fin.isAfter(rango.fin);
    }

    public boolean sameValueAs(RangoHorario other) {
        return other != null && this.inicio.equals(other.inicio) && this.fin.equals(other.fin);
    }

    @Override
    public String toString() {
        return inicio.format(FORMATTER) + " - " + fin.format(FORMATTER);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RangoHorario that = (RangoHorario) obj;
        return inicio.equals(that.inicio) && fin.equals(that.fin);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(inicio, fin);
    }
}