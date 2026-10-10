package com.academic.gestor.domain.model.entities;

import com.academic.gestor.domain.model.valueobjects.DiaSemana;
import com.academic.gestor.domain.model.valueobjects.RangoHorario;
import com.academic.gestor.shared.kernel.Entity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad que representa un bloque horario de una materia en la agenda semanal.
 * Una materia puede tener múltiples bloques en diferentes días y horas.
 */
public class BloqueHorario extends Entity<BloqueHorario> {

    private UUID materiaId;
    private DiaSemana diaSemana;
    private RangoHorario rangoHorario;
    private String aula;
    private boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public BloqueHorario(UUID id, UUID materiaId, DiaSemana diaSemana,
                          RangoHorario rangoHorario, String aula,
                          boolean activo, LocalDateTime fechaCreacion,
                          LocalDateTime fechaActualizacion) {
        super(id);
        this.materiaId = Objects.requireNonNull(materiaId, "materiaId no puede ser nulo");
        this.diaSemana = Objects.requireNonNull(diaSemana, "diaSemana no puede ser nulo");
        this.rangoHorario = Objects.requireNonNull(rangoHorario, "rangoHorario no puede ser nulo");
        this.aula = aula != null ? aula.trim() : null;
        this.activo = activo;
        this.fechaCreacion = Objects.requireNonNull(fechaCreacion, "fechaCreacion no puede ser nula");
        this.fechaActualizacion = Objects.requireNonNull(fechaActualizacion, "fechaActualizacion no puede ser nula");
    }

    public static BloqueHorario create(UUID materiaId, DiaSemana diaSemana,
                                       LocalTime inicio, LocalTime fin, String aula) {
        RangoHorario rango = RangoHorario.of(inicio, fin);
        LocalDateTime now = LocalDateTime.now();
        return new BloqueHorario(
                UUID.randomUUID(),
                materiaId,
                diaSemana,
                rango,
                aula,
                true,
                now,
                now
        );
    }

    public void actualizar(DiaSemana diaSemana, LocalTime inicio, LocalTime fin, String aula) {
        this.diaSemana = Objects.requireNonNull(diaSemana, "diaSemana no puede ser nulo");
        this.rangoHorario = RangoHorario.of(
                Objects.requireNonNull(inicio, "inicio no puede ser nulo"),
                Objects.requireNonNull(fin, "fin no puede ser nulo")
        );
        this.aula = aula != null ? aula.trim() : null;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void mover(DiaSemana nuevoDia, LocalTime nuevoInicio, LocalTime nuevoFin) {
        this.diaSemana = Objects.requireNonNull(nuevoDia, "nuevoDia no puede ser nulo");
        this.rangoHorario = RangoHorario.of(
                Objects.requireNonNull(nuevoInicio, "nuevoInicio no puede ser nulo"),
                Objects.requireNonNull(nuevoFin, "nuevoFin no puede ser nulo")
        );
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void desactivar() {
        this.activo = false;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void activar() {
        this.activo = true;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public UUID getMateriaId() {
        return materiaId;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public RangoHorario getRangoHorario() {
        return rangoHorario;
    }

    public LocalTime getHoraInicio() {
        return rangoHorario.getInicio();
    }

    public LocalTime getHoraFin() {
        return rangoHorario.getFin();
    }

    public String getAula() {
        return aula;
    }

    public boolean isActivo() {
        return activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public boolean solapaCon(BloqueHorario otro) {
        if (!this.diaSemana.equals(otro.diaSemana)) {
            return false;
        }
        return this.rangoHorario.solapaCon(otro.rangoHorario);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        if (!super.equals(obj)) return false;
        BloqueHorario that = (BloqueHorario) obj;
        return Objects.equals(materiaId, that.materiaId)
                && Objects.equals(diaSemana, that.diaSemana)
                && Objects.equals(rangoHorario, that.rangoHorario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), materiaId, diaSemana, rangoHorario);
    }
}