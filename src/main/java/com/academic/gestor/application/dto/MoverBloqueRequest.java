package com.academic.gestor.application.dto;

import com.academic.gestor.domain.model.valueobjects.DiaSemana;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

/**
 * DTO para mover un bloque horario (drag & drop).
 */
public record MoverBloqueRequest(
        @NotNull(message = "El nuevo día es obligatorio")
        DiaSemana diaSemana,

        @NotNull(message = "La nueva hora de inicio es obligatoria")
        LocalTime horaInicio,

        @NotNull(message = "La nueva hora de fin es obligatoria")
        LocalTime horaFin
) {
}