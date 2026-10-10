package com.academic.gestor.application.dto;

import com.academic.gestor.domain.model.valueobjects.DiaSemana;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * DTO de respuesta para un bloque horario.
 * Incluye información de la materia para evitar llamadas adicionales.
 */
public record BloqueHorarioResponse(
        UUID id,
        UUID materiaId,
        String materiaNombre,
        String colorHex,
        DiaSemana diaSemana,
        LocalTime horaInicio,
        LocalTime horaFin,
        String aula,
        boolean activo,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion
) {
}