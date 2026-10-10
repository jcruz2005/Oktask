package com.academic.gestor.infrastructure.persistence.mappers;

import com.academic.gestor.domain.model.entities.BloqueHorario;
import com.academic.gestor.domain.model.valueobjects.DiaSemana;
import com.academic.gestor.domain.model.valueobjects.RangoHorario;
import com.academic.gestor.infrastructure.persistence.entities.BloqueHorarioEntity;
import com.academic.gestor.infrastructure.persistence.entities.MateriaEntity;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Mapper entre BloqueHorario (Domain) y BloqueHorarioEntity (JPA).
 */
public class BloqueHorarioEntityMapper {

    private BloqueHorarioEntityMapper() {}

    public static BloqueHorario toDomain(BloqueHorarioEntity entity) {
        if (entity == null) return null;

        return new BloqueHorario(
                entity.getId(),
                entity.getMateria().getId(),
                entity.getDiaSemana(),
                RangoHorario.of(entity.getHoraInicio(), entity.getHoraFin()),
                entity.getAula(),
                entity.isActivo(),
                entity.getFechaCreacion(),
                entity.getFechaActualizacion()
        );
    }

    public static BloqueHorarioEntity toEntity(BloqueHorario domain) {
        if (domain == null) return null;

        MateriaEntity materiaEntity = new MateriaEntity(
                domain.getMateriaId(),
                "", // nombre - no necesario para FK
                "", // codigo - no necesario para FK
                "", // color - no necesario para FK
                "MEDIA", // prioridad - no necesario para FK
                LocalDateTime.now(), // fechaCreacion - no necesario para FK
                true // activa - no necesario para FK
        );

        return new BloqueHorarioEntity(
                domain.getId(),
                materiaEntity,
                domain.getDiaSemana(),
                domain.getHoraInicio(),
                domain.getHoraFin(),
                domain.getAula(),
                domain.isActivo(),
                domain.getFechaCreacion(),
                domain.getFechaActualizacion()
        );
    }

    public static void updateEntity(BloqueHorario domain, BloqueHorarioEntity entity) {
        entity.setDiaSemana(domain.getDiaSemana());
        entity.setHoraInicio(domain.getHoraInicio());
        entity.setHoraFin(domain.getHoraFin());
        entity.setAula(domain.getAula());
        entity.setActivo(domain.isActivo());
        entity.setFechaActualizacion(domain.getFechaActualizacion());
    }
}