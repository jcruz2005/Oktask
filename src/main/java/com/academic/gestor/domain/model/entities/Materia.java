package com.academic.gestor.domain.model.entities;

import com.academic.gestor.domain.model.enums.Prioridad;
import com.academic.gestor.domain.model.valueobjects.Color;
import com.academic.gestor.domain.model.valueobjects.CodigoMateria;
import com.academic.gestor.shared.kernel.AggregateRoot;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidad que representa una materia académica.
 *
 * <p>Una materia tiene un código único, un nombre, un color para
 * identificación visual y un nivel de prioridad.</p>
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public class Materia extends AggregateRoot<Materia> {

    private String nombre;
    private CodigoMateria codigo;
    private Color color;
    private Prioridad prioridad;
    private LocalDateTime fechaCreacion;
    private boolean activa;

    /**
     * Constructor completo de Materia.
     *
     * @param id identificador único
     * @param nombre nombre de la materia
     * @param codigo código único de la materia
     * @param color color representativo
     * @param prioridad nivel de prioridad
     * @param fechaCreacion fecha de creación
     * @param activa si la materia está activa
     */
    public Materia(final UUID id, final String nombre, final CodigoMateria codigo,
                   final Color color, final Prioridad prioridad,
                   final LocalDateTime fechaCreacion, final boolean activa) {
        super(id);
        setNombre(nombre);
        this.codigo = Objects.requireNonNull(codigo, "El código de materia no puede ser nulo");
        this.color = Objects.requireNonNull(color, "El color no puede ser nulo");
        this.prioridad = Objects.requireNonNull(prioridad, "La prioridad no puede ser nula");
        this.fechaCreacion = Objects.requireNonNull(fechaCreacion, "La fecha de creación no puede ser nula");
        this.activa = activa;
    }

    /**
     * Factory method que crea una nueva Materia con valores por defecto.
     *
     * @param nombre nombre de la materia
     * @param codigo código único de la materia
     * @param color color representativo
     * @param prioridad nivel de prioridad
     * @return nueva instancia de Materia
     */
    public static Materia create(final String nombre, final CodigoMateria codigo,
                                 final Color color, final Prioridad prioridad) {
        return new Materia(
                UUID.randomUUID(),
                nombre,
                codigo,
                color,
                prioridad,
                LocalDateTime.now(),
                true
        );
    }

    /**
     * Actualiza el nombre de la materia.
     *
     * @param nombre nuevo nombre
     */
    public void setNombre(final String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la materia no puede estar vacío");
        }
        this.nombre = nombre.trim();
    }

    /**
     * Actualiza el color de la materia.
     *
     * @param color nuevo color
     */
    public void setColor(final Color color) {
        this.color = Objects.requireNonNull(color, "El color no puede ser nulo");
    }

    /**
     * Actualiza la prioridad de la materia.
     *
     * @param prioridad nueva prioridad
     */
    public void setPrioridad(final Prioridad prioridad) {
        this.prioridad = Objects.requireNonNull(prioridad, "La prioridad no puede ser nula");
    }

    /**
     * Desactiva la materia (soft delete).
     */
    public void desactivar() {
        this.activa = false;
    }

    /**
     * Reactiva la materia.
     */
    public void activar() {
        this.activa = true;
    }

    /**
     * Obtiene el nombre de la materia.
     *
     * @return nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Obtiene el código de la materia.
     *
     * @return código
     */
    public CodigoMateria getCodigo() {
        return codigo;
    }

    /**
     * Obtiene el color de la materia.
     *
     * @return color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Obtiene la prioridad de la materia.
     *
     * @return prioridad
     */
    public Prioridad getPrioridad() {
        return prioridad;
    }

    /**
     * Obtiene la fecha de creación de la materia.
     *
     * @return fecha de creación
     */
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * Indica si la materia está activa.
     *
     * @return true si está activa
     */
    public boolean isActiva() {
        return activa;
    }
}
