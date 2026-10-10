package com.academic.gestor.application.dto;

import com.academic.gestor.domain.exceptions.SolapamientoException;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de respuesta para la validación de disponibilidad horaria.
 */
public record ValidacionDisponibilidadResponse(
        boolean valido,
        List<ConflictoResponse> conflictos,
        List<HuecoLibreResponse> sugerencias
) {

    public record ConflictoResponse(
            UUID bloqueId,
            UUID materiaId,
            String materiaNombre,
            String colorHex,
            LocalTime inicio,
            LocalTime fin
    ) {
        public static ConflictoResponse from(SolapamientoException.Conflicto c) {
            return new ConflictoResponse(c.getBloqueId(), c.getMateriaId(),
                    c.getMateriaNombre(), c.getColorHex(), c.getInicio(), c.getFin());
        }
    }

    public record HuecoLibreResponse(
            LocalTime inicio,
            LocalTime fin,
            int duracionMinutos
    ) {
        public static HuecoLibreResponse from(SolapamientoException.HuecoLibre h) {
            return new HuecoLibreResponse(h.getInicio(), h.getFin(), h.getDuracionMinutos());
        }
    }

    public static ValidacionDisponibilidadResponse from(SolapamientoException.ValidacionResponse v) {
        return new ValidacionDisponibilidadResponse(
                v.isValido(),
                v.getConflictos().stream().map(ConflictoResponse::from).toList(),
                v.getSugerencias().stream().map(HuecoLibreResponse::from).toList()
        );
    }
}