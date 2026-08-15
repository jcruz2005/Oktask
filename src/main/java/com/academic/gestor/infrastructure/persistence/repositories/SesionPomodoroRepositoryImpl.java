package com.academic.gestor.infrastructure.persistence.repositories;

import com.academic.gestor.domain.model.entities.SesionPomodoro;
import com.academic.gestor.domain.model.enums.TipoSesion;
import com.academic.gestor.domain.ports.outbound.SesionPomodoroRepository;
import com.academic.gestor.infrastructure.persistence.entities.SesionPomodoroEntity;
import com.academic.gestor.infrastructure.persistence.jpa.JpaSesionPomodoroRepository;
import com.academic.gestor.infrastructure.persistence.mappers.SesionPomodoroEntityMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del repositorio de sesiones de Pomodoro usando JPA.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
@Repository
public class SesionPomodoroRepositoryImpl implements SesionPomodoroRepository {

    private final JpaSesionPomodoroRepository jpaRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param jpaRepository repositorio JPA de Spring Data
     */
    public SesionPomodoroRepositoryImpl(final JpaSesionPomodoroRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<SesionPomodoro> findById(final UUID id) {
        return jpaRepository.findById(id)
                .map(SesionPomodoroEntityMapper::toDomain);
    }

    @Override
    public List<SesionPomodoro> findByTareaId(final UUID tareaId) {
        return jpaRepository.findByTareaId(tareaId).stream()
                .map(SesionPomodoroEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SesionPomodoro> findByMateriaId(final UUID materiaId) {
        return jpaRepository.findByMateriaId(materiaId).stream()
                .map(SesionPomodoroEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SesionPomodoro> findByFechaInRange(final LocalDateTime inicio, final LocalDateTime fin) {
        return jpaRepository.findByTipoSesionAndCompletadaAndFechaInicioBetween(
                TipoSesion.TRABAJO.name(), true, inicio, fin
        ).stream()
                .map(SesionPomodoroEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SesionPomodoro> findCompletadasByFechaInRange(final LocalDateTime inicio,
                                                              final LocalDateTime fin) {
        return jpaRepository.findByTipoSesionAndCompletadaAndFechaInicioBetween(
                TipoSesion.TRABAJO.name(), true, inicio, fin
        ).stream()
                .map(SesionPomodoroEntityMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public SesionPomodoro save(final SesionPomodoro sesion) {
        final SesionPomodoroEntity entity = SesionPomodoroEntityMapper.toEntity(sesion);
        final SesionPomodoroEntity saved = jpaRepository.save(entity);
        return SesionPomodoroEntityMapper.toDomain(saved);
    }

    @Override
    public long sumDuracionByMateriaId(final UUID materiaId) {
        return jpaRepository.sumDuracionByMateriaId(
                materiaId, TipoSesion.TRABAJO.name(), true
        );
    }

    @Override
    public long sumDuracionByFechaInRange(final LocalDateTime inicio, final LocalDateTime fin) {
        return jpaRepository.sumDuracionByFechaInRange(
                TipoSesion.TRABAJO.name(), true, inicio, fin
        );
    }

    @Override
    public long sumDuracionByMateriaIdAndFechaInRange(final UUID materiaId,
                                                       final LocalDateTime inicio,
                                                       final LocalDateTime fin) {
        return jpaRepository.sumDuracionByMateriaIdAndFechaInRange(
                materiaId, TipoSesion.TRABAJO.name(), true, inicio, fin
        );
    }
}
