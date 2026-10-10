package com.academic.gestor.domain.exceptions;

import com.academic.gestor.domain.model.valueobjects.DiaSemana;
import com.academic.gestor.domain.model.valueobjects.RangoHorario;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * Excepción lanzada cuando se detecta un solapamiento de bloques horarios.
 * Contiene información sobre los conflictos y sugerencias de huecos libres.
 */
public class SolapamientoException extends RuntimeException {

    private final UUID materiaId;
    private final DiaSemana diaSemana;
    private final LocalTime horaInicio;
    private final LocalTime horaFin;
    private final List<Conflicto> conflictos;
    private final List<HuecoLibre> sugerencias;

    public SolapamientoException(UUID materiaId, DiaSemana diaSemana,
                                 LocalTime horaInicio, LocalTime horaFin,
                                 List<Conflicto> conflictos, List<HuecoLibre> sugerencias) {
        super(buildMessage(materiaId, diaSemana, horaInicio, horaFin, conflictos));
        this.materiaId = materiaId;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.conflictos = List.copyOf(conflictos);
        this.sugerencias = List.copyOf(sugerencias);
    }

    private static String buildMessage(UUID materiaId, DiaSemana diaSemana,
                                       LocalTime inicio, LocalTime fin,
                                       List<Conflicto> conflictos) {
        StringBuilder sb = new StringBuilder();
        sb.append("Solapamiento detectado para materia ").append(materiaId)
                .append(" el ").append(diaSemana)
                .append(" en horario ").append(inicio).append("-").append(fin)
                .append(". Conflictos: ").append(conflictos.size());
        return sb.toString();
    }

    public UUID getMateriaId() {
        return materiaId;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public List<Conflicto> getConflictos() {
        return conflictos;
    }

    public List<HuecoLibre> getSugerencias() {
        return sugerencias;
    }

    public static class Conflicto {
        private final UUID bloqueId;
        private final UUID materiaId;
        private final String materiaNombre;
        private final String colorHex;
        private final LocalTime inicio;
        private final LocalTime fin;

        public Conflicto(UUID bloqueId, UUID materiaId, String materiaNombre,
                         String colorHex, LocalTime inicio, LocalTime fin) {
            this.bloqueId = bloqueId;
            this.materiaId = materiaId;
            this.materiaNombre = materiaNombre;
            this.colorHex = colorHex;
            this.inicio = inicio;
            this.fin = fin;
        }

        public UUID getBloqueId() { return bloqueId; }
        public UUID getMateriaId() { return materiaId; }
        public String getMateriaNombre() { return materiaNombre; }
        public String getColorHex() { return colorHex; }
        public LocalTime getInicio() { return inicio; }
        public LocalTime getFin() { return fin; }
    }

    public static class HuecoLibre {
        private final LocalTime inicio;
        private final LocalTime fin;
        private final int duracionMinutos;

        public HuecoLibre(LocalTime inicio, LocalTime fin) {
            this.inicio = inicio;
            this.fin = fin;
            this.duracionMinutos = (int) java.time.Duration.between(inicio, fin).toMinutes();
        }

        public LocalTime getInicio() { return inicio; }
        public LocalTime getFin() { return fin; }
        public int getDuracionMinutos() { return duracionMinutos; }
    }

    public static class ValidacionResponse {
        private final boolean valido;
        private final List<Conflicto> conflictos;
        private final List<HuecoLibre> sugerencias;

        public ValidacionResponse(boolean valido, List<Conflicto> conflictos, List<HuecoLibre> sugerencias) {
            this.valido = valido;
            this.conflictos = List.copyOf(conflictos);
            this.sugerencias = List.copyOf(sugerencias);
        }

        public boolean isValido() { return valido; }
        public List<Conflicto> getConflictos() { return conflictos; }
        public List<HuecoLibre> getSugerencias() { return sugerencias; }

        public static ValidacionResponse valido(List<HuecoLibre> sugerencias) {
            return new ValidacionResponse(true, List.of(), sugerencias);
        }

        public static ValidacionResponse invalido(List<Conflicto> conflictos, List<HuecoLibre> sugerencias) {
            return new ValidacionResponse(false, conflictos, sugerencias);
        }
    }
}