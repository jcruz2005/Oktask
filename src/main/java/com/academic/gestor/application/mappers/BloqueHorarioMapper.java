package com.academic.gestor.application.mappers;

import com.academic.gestor.application.dto.ActualizarBloqueHorarioRequest;
import com.academic.gestor.application.dto.BloqueHorarioResponse;
import com.academic.gestor.application.dto.CrearBloqueHorarioRequest;
import com.academic.gestor.application.dto.MoverBloqueRequest;
import com.academic.gestor.domain.model.entities.BloqueHorario;
import com.academic.gestor.domain.model.valueobjects.DiaSemana;
import com.academic.gestor.domain.model.valueobjects.RangoHorario;

import java.time.LocalTime;
import java.util.UUID;

/**
 * Mapper entre DTOs de aplicación y entidades de dominio para BloqueHorario.
 */
public class BloqueHorarioMapper {

    private BloqueHorarioMapper() {}

    public static BloqueHorario toDomain(CrearBloqueHorarioRequest request) {
        return BloqueHorario.create(
                request.materiaId(),
                request.diaSemana(),
                request.horaInicio(),
                request.horaFin(),
                request.aula()
        );
    }

    public static BloqueHorarioResponse toResponse(BloqueHorario bloque, String materiaNombre, String colorHex) {
        return new BloqueHorarioResponse(
                bloque.getId(),
                bloque.getMateriaId(),
                materiaNombre,
                colorHex,
                bloque.getDiaSemana(),
                bloque.getHoraInicio(),
                bloque.getHoraFin(),
                bloque.getAula(),
                bloque.isActivo(),
                bloque.getFechaCreacion(),
                bloque.getFechaActualizacion()
        );
    }

    public static void applyToDomain(ActualizarBloqueHorarioRequest request, BloqueHorario bloque) {
        bloque.actualizar(request.diaSemana(), request.horaInicio(), request.horaFin(), request.aula());
    }

    public static void applyMove(MoverBloqueRequest request, BloqueHorario bloque) {
        bloque.mover(request.diaSemana(), request.horaInicio(), request.horaFin());
    }
}