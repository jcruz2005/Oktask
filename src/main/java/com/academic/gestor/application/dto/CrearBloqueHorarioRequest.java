package com.academic.gestor.application.dto;

import com.academic.gestor.domain.model.valueobjects.DiaSemana;

import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.UUID;

/**
 * DTO para la creación de un bloque horario.
 */
public record CrearBloqueHorarioRequest(
        @NotNull(message = "El ID de la materia es obligatorio")
        UUID materiaId,

        @NotNull(message = "El día de la semana es obligatorio")
        DiaSemana diaSemana,

        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime horaInicio,

        @NotNull(message = "La hora de fin es obligatoria")
        LocalTime horaFin,

        String aula
) {
}