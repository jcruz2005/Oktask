package com.academic.gestor.domain.ports.inbound;

import com.academic.gestor.domain.exceptions.SolapamientoException;
import com.academic.gestor.domain.model.entities.BloqueHorario;
import com.academic.gestor.domain.model.valueobjects.DiaSemana;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de entrada para los casos de uso de la agenda semanal.
 * Define la API de la capa de aplicación para gestión de bloques horarios.
 */
public interface AgendaService {

    BloqueHorario crearBloque(UUID materiaId, DiaSemana diaSemana,
                              LocalTime horaInicio, LocalTime horaFin, String aula)
            throws SolapamientoException;

    BloqueHorario actualizarBloque(UUID id, UUID materiaId, DiaSemana diaSemana,
                                   LocalTime horaInicio, LocalTime horaFin, String aula)
            throws SolapamientoException;

    BloqueHorario moverBloque(UUID id, DiaSemana nuevoDia, LocalTime nuevoInicio, LocalTime nuevoFin)
            throws SolapamientoException;

    void eliminarBloque(UUID id);

    Optional<BloqueHorario> obtenerBloque(UUID id);

    List<BloqueHorario> listarBloques();

    List<BloqueHorario> listarBloquesActivos();

    List<BloqueHorario> listarPorMateria(UUID materiaId);

    List<BloqueHorario> listarPorDia(DiaSemana diaSemana);

    List<BloqueHorario> listarPorMateriaYDia(UUID materiaId, DiaSemana diaSemana);

    SolapamientoException.ValidacionResponse validarDisponibilidad(UUID materiaId, DiaSemana diaSemana,
                                                                   LocalTime horaInicio, LocalTime horaFin);
}