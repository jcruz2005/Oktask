package com.academic.gestor.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad JPA para la persistencia de materias.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
@Entity
@Table(name = "materias")
public class MateriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String prioridad;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private boolean activa;

    /**
     * Constructor por defecto para JPA.
     */
    protected MateriaEntity() {
    }

    /**
     * Constructor completo.
     *
     * @param id ID de la materia
     * @param nombre nombre de la materia
     * @param codigo código de la materia
     * @param color color hexadecimal
     * @param prioridad prioridad
     * @param fechaCreacion fecha de creación
     * @param activa si está activa
     */
    public MateriaEntity(final UUID id, final String nombre, final String codigo,
                         final String color, final String prioridad,
                         final LocalDateTime fechaCreacion, final boolean activa) {
        this.id = id;
        this.nombre = nombre;
        this.codigo = codigo;
        this.color = color;
        this.prioridad = prioridad;
        this.fechaCreacion = fechaCreacion;
        this.activa = activa;
    }

    public UUID getId() {
        return id;
    }

    public void setId(final UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(final String nombre) {
        this.nombre = nombre;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(final String codigo) {
        this.codigo = codigo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(final String color) {
        this.color = color;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(final String prioridad) {
        this.prioridad = prioridad;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(final LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(final boolean activa) {
        this.activa = activa;
    }
}
