package com.academic.gestor.shared.kernel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Clase base abstracta para raíces de agregado del dominio.
 *
 * <p>Una raíz de agregado es una entidad que es la puerta de entrada
 * a un agregado del dominio. Es responsable de mantener la consistencia
 * de las reglas de negocio dentro del agregado y de publicar eventos
 * de dominio cuando ocurren cambios significativos.</p>
 *
 * @param <T> tipo de la raíz de agregado que extiende esta clase
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
public abstract class AggregateRoot<T extends AggregateRoot<T>> extends Entity<T> {

    /**
     * Lista de eventos de dominio pendientes de publicar.
     */
    private final List<Object> domainEvents = new ArrayList<>();

    /**
     * Constructor con ID proporcionado.
     *
     * @param id identificador único de la raíz de agregado, no puede ser nulo
     */
    protected AggregateRoot(final UUID id) {
        super(id);
    }

    /**
     * Registra un evento de dominio para ser publicado posteriormente.
     *
     * @param event evento de dominio a registrar, no puede ser nulo
     */
    protected void registerEvent(final Object event) {
        Objects.requireNonNull(event, "El evento de dominio no puede ser nulo");
        this.domainEvents.add(event);
    }

    /**
     * Obtiene la lista de eventos de dominio pendientes de publicar.
     *
     * @return lista inmutable de eventos de dominio
     */
    public List<Object> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * Limpia todos los eventos de dominio pendientes.
     * Se invoca después de que los eventos hayan sido publicados.
     */
    public void clearDomainEvents() {
        this.domainEvents.clear();
    }
}
