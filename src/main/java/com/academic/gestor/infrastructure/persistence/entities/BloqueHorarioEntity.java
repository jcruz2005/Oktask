package com.academic.gestor.infrastructure.persistence.entities;

import com.academic.gestor.domain.model.valueobjects.DiaSemana;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Entidad JPA para la tabla bloques_horario.
 */
@Entity
@Table(name = "bloques_horario", indexes = {
        @Index(name = "idx_bloques_materia_dia", columnList = "materia_id, dia_semana"),
        @Index(name = "idx_bloques_dia_hora", columnList = "dia_semana, hora_inicio, hora_fin")
})
public class BloqueHorarioEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "materia_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_bloques_materia"))
    private MateriaEntity materia;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 20)
    private DiaSemana diaSemana;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(name = "aula", length = 100)
    private String aula;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    protected BloqueHorarioEntity() {}

    public BloqueHorarioEntity(UUID id, MateriaEntity materia, DiaSemana diaSemana,
                               LocalTime horaInicio, LocalTime horaFin, String aula,
                               boolean activo, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.materia = materia;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.aula = aula;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public MateriaEntity getMateria() { return materia; }
    public void setMateria(MateriaEntity materia) { this.materia = materia; }

    public DiaSemana getDiaSemana() { return diaSemana; }
    public void setDiaSemana(DiaSemana diaSemana) { this.diaSemana = diaSemana; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public String getAula() { return aula; }
    public void setAula(String aula) { this.aula = aula; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}