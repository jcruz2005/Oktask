package com.academic.gestor.application.services;

import com.academic.gestor.domain.exceptions.MateriaNotFoundException;
import com.academic.gestor.domain.exceptions.SolapamientoException;
import com.academic.gestor.domain.model.entities.BloqueHorario;
import com.academic.gestor.domain.model.valueobjects.DiaSemana;
import com.academic.gestor.domain.ports.inbound.AgendaService;
import com.academic.gestor.domain.ports.outbound.BloqueHorarioRepository;
import com.academic.gestor.domain.ports.outbound.MateriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de agenda semanal.
 */
public class AgendaServiceImpl implements AgendaService {

    private static final Logger log = LoggerFactory.getLogger(AgendaServiceImpl.class);

    private final BloqueHorarioRepository bloqueRepository;
    private final MateriaRepository materiaRepository;

    public AgendaServiceImpl(BloqueHorarioRepository bloqueRepository,
                             MateriaRepository materiaRepository) {
        this.bloqueRepository = bloqueRepository;
        this.materiaRepository = materiaRepository;
    }

    @Override
    public BloqueHorario crearBloque(UUID materiaId, DiaSemana diaSemana,
                                     LocalTime horaInicio, LocalTime horaFin, String aula)
            throws SolapamientoException {
        log.info("Creando bloque horario para materia {} el {} {}-{}", materiaId, diaSemana, horaInicio, horaFin);

        validarMateriaExiste(materiaId);
        validarNoSolapamiento(materiaId, diaSemana, horaInicio, horaFin, null);

        BloqueHorario bloque = BloqueHorario.create(materiaId, diaSemana, horaInicio, horaFin, aula);
        BloqueHorario guardado = bloqueRepository.save(bloque);

        log.info("Bloque horario creado con ID: {}", guardado.getId());
        return guardado;
    }

    @Override
    public BloqueHorario actualizarBloque(UUID id, UUID materiaId, DiaSemana diaSemana,
                                          LocalTime horaInicio, LocalTime horaFin, String aula)
            throws SolapamientoException {
        log.info("Actualizando bloque horario: {}", id);

        BloqueHorario bloque = bloqueRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bloque horario no encontrado: " + id));

        if (!bloque.getMateriaId().equals(materiaId)) {
            validarMateriaExiste(materiaId);
        }

        validarNoSolapamiento(materiaId, diaSemana, horaInicio, horaFin, id);

        bloque.actualizar(diaSemana, horaInicio, horaFin, aula);
        BloqueHorario guardado = bloqueRepository.save(bloque);

        log.info("Bloque horario actualizado: {}", guardado.getId());
        return guardado;
    }

    @Override
    public BloqueHorario moverBloque(UUID id, DiaSemana nuevoDia, LocalTime nuevoInicio, LocalTime nuevoFin)
            throws SolapamientoException {
        log.info("Moviendo bloque horario: {} a {} {}-{}", id, nuevoDia, nuevoInicio, nuevoFin);

        BloqueHorario bloque = bloqueRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bloque horario no encontrado: " + id));

        validarNoSolapamiento(bloque.getMateriaId(), nuevoDia, nuevoInicio, nuevoFin, id);

        bloque.mover(nuevoDia, nuevoInicio, nuevoFin);
        BloqueHorario guardado = bloqueRepository.save(bloque);

        log.info("Bloque horario movido: {}", guardado.getId());
        return guardado;
    }

    @Override
    public void eliminarBloque(UUID id) {
        log.info("Eliminando (desactivando) bloque horario: {}", id);

        BloqueHorario bloque = bloqueRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bloque horario no encontrado: " + id));

        bloque.desactivar();
        bloqueRepository.save(bloque);

        log.info("Bloque horario desactivado: {}", id);
    }

    @Override
    public Optional<BloqueHorario> obtenerBloque(UUID id) {
        return bloqueRepository.findById(id);
    }

    @Override
    public List<BloqueHorario> listarBloques() {
        return bloqueRepository.findAll();
    }

    @Override
    public List<BloqueHorario> listarBloquesActivos() {
        return bloqueRepository.findAllActivos();
    }

    @Override
    public List<BloqueHorario> listarPorMateria(UUID materiaId) {
        return bloqueRepository.findActivosByMateriaId(materiaId);
    }

    @Override
    public List<BloqueHorario> listarPorDia(DiaSemana diaSemana) {
        return bloqueRepository.findActivosByDiaSemana(diaSemana);
    }

    @Override
    public List<BloqueHorario> listarPorMateriaYDia(UUID materiaId, DiaSemana diaSemana) {
        return bloqueRepository.findByMateriaIdAndDiaSemana(materiaId, diaSemana)
                .stream()
                .filter(BloqueHorario::isActivo)
                .collect(Collectors.toList());
    }

    @Override
    public SolapamientoException.ValidacionResponse validarDisponibilidad(UUID materiaId, DiaSemana diaSemana,
                                                                          LocalTime horaInicio, LocalTime horaFin) {
        log.debug("Validando disponibilidad para materia {} el {} {}-{}", materiaId, diaSemana, horaInicio, horaFin);

        List<BloqueHorario> conflictos = bloqueRepository.findConflictos(materiaId, diaSemana, horaInicio, horaFin);
        List<SolapamientoException.HuecoLibre> sugerencias = calcularHuecosLibres(diaSemana, horaInicio, horaFin, conflictos);

        if (conflictos.isEmpty()) {
            return SolapamientoException.ValidacionResponse.valido(sugerencias);
        }

        List<SolapamientoException.Conflicto> conflictosDto = conflictos.stream()
                .map(b -> {
                    var materia = materiaRepository.findById(b.getMateriaId()).orElse(null);
                    return new SolapamientoException.Conflicto(
                            b.getId(),
                            b.getMateriaId(),
                            materia != null ? materia.getNombre() : "Desconocida",
                            materia != null ? materia.getColor().hex() : "#666666",
                            b.getHoraInicio(),
                            b.getHoraFin()
                    );
                })
                .collect(Collectors.toList());

        return SolapamientoException.ValidacionResponse.invalido(conflictosDto, sugerencias);
    }

    private void validarMateriaExiste(UUID materiaId) {
        if (!materiaRepository.existsById(materiaId)) {
            throw new MateriaNotFoundException(materiaId.toString());
        }
    }

    private void validarNoSolapamiento(UUID materiaId, DiaSemana diaSemana,
                                       LocalTime horaInicio, LocalTime horaFin, UUID excluirId)
            throws SolapamientoException {
        List<BloqueHorario> conflictos = bloqueRepository.findConflictos(materiaId, diaSemana, horaInicio, horaFin);

        if (excluirId != null) {
            conflictos = conflictos.stream()
                    .filter(b -> !b.getId().equals(excluirId))
                    .collect(Collectors.toList());
        }

        if (!conflictos.isEmpty()) {
            List<SolapamientoException.HuecoLibre> sugerencias = calcularHuecosLibres(diaSemana, horaInicio, horaFin, conflictos);
            List<SolapamientoException.Conflicto> conflictosDto = conflictos.stream()
                    .map(b -> {
                        var materia = materiaRepository.findById(b.getMateriaId()).orElse(null);
                        return new SolapamientoException.Conflicto(
                                b.getId(),
                                b.getMateriaId(),
                                materia != null ? materia.getNombre() : "Desconocida",
                                materia != null ? materia.getColor().hex() : "#666666",
                                b.getHoraInicio(),
                                b.getHoraFin()
                        );
                    })
                    .collect(Collectors.toList());

            throw new SolapamientoException(materiaId, diaSemana, horaInicio, horaFin, conflictosDto, sugerencias);
        }
    }

    private List<SolapamientoException.HuecoLibre> calcularHuecosLibres(DiaSemana diaSemana,
                                                                        LocalTime horaInicio, LocalTime horaFin,
                                                                        List<BloqueHorario> bloquesOcupados) {
        var todosBloques = bloqueRepository.findActivosByDiaSemana(diaSemana);
        var bloquesDelDia = todosBloques.stream()
                .filter(b -> bloquesOcupados.stream().noneMatch(o -> o.getId().equals(b.getId())))
                .sorted((a, b) -> a.getHoraInicio().compareTo(b.getHoraInicio()))
                .collect(Collectors.toList());

        return encontrarHuecos(bloquesDelDia, horaInicio, horaFin);
    }

    private List<SolapamientoException.HuecoLibre> encontrarHuecos(List<BloqueHorario> bloquesOrdenados,
                                                                   LocalTime inicioBusqueda, LocalTime finBusqueda) {
        var huecos = new java.util.ArrayList<SolapamientoException.HuecoLibre>();
        LocalTime cursor = inicioBusqueda;

        for (BloqueHorario bloque : bloquesOrdenados) {
            if (bloque.getHoraFin().isBefore(inicioBusqueda) || bloque.getHoraInicio().isAfter(finBusqueda)) {
                continue;
            }
            if (cursor.isBefore(bloque.getHoraInicio())) {
                huecos.add(new SolapamientoException.HuecoLibre(cursor, bloque.getHoraInicio()));
            }
            if (bloque.getHoraFin().isAfter(cursor)) {
                cursor = bloque.getHoraFin();
            }
        }

        if (cursor.isBefore(finBusqueda)) {
            huecos.add(new SolapamientoException.HuecoLibre(cursor, finBusqueda));
        }

        return huecos.stream()
                .filter(h -> h.getDuracionMinutos() >= 30)
                .collect(Collectors.toList());
    }
}