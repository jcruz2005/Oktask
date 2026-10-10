package com.academic.gestor.domain.model.entities;

import com.academic.gestor.domain.model.valueobjects.DiaSemana;
import com.academic.gestor.domain.model.valueobjects.RangoHorario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BloqueHorario Entity Tests")
class BloqueHorarioTest {

    private static final UUID MATERIA_ID = UUID.randomUUID();
    private static final UUID OTRA_MATERIA_ID = UUID.randomUUID();

    @Nested
    @DisplayName("Creación")
    class Creacion {

        @Test
        @DisplayName("Debe crear bloque horario válido")
        void debeCrearBloqueValido() {
            BloqueHorario bloque = BloqueHorario.create(
                    MATERIA_ID, DiaSemana.of("LUNES"), LocalTime.of(12, 0), LocalTime.of(14, 0), "Aula 101");

            assertNotNull(bloque.getId());
            assertEquals(MATERIA_ID, bloque.getMateriaId());
            assertEquals(DiaSemana.of("LUNES"), bloque.getDiaSemana());
            assertEquals(LocalTime.of(12, 0), bloque.getHoraInicio());
            assertEquals(LocalTime.of(14, 0), bloque.getHoraFin());
            assertEquals("Aula 101", bloque.getAula());
            assertTrue(bloque.isActivo());
            assertNotNull(bloque.getFechaCreacion());
            assertNotNull(bloque.getFechaActualizacion());
        }

        @Test
        @DisplayName("Debe crear bloque sin aula")
        void debeCrearBloqueSinAula() {
            BloqueHorario bloque = BloqueHorario.create(
                    MATERIA_ID, DiaSemana.of("MARTES"), LocalTime.of(8, 0), LocalTime.of(10, 0), null);

            assertNull(bloque.getAula());
        }

        @Test
        @DisplayName("Debe lanzar excepción si materiaId es nulo")
        void debeLanzarExcepcionSiMateriaIdNulo() {
            assertThrows(NullPointerException.class, () ->
                    BloqueHorario.create(null, DiaSemana.of("LUNES"), LocalTime.of(12, 0), LocalTime.of(14, 0), null));
        }

        @Test
        @DisplayName("Debe lanzar excepción si diaSemana es nulo")
        void debeLanzarExcepcionSiDiaSemanaNulo() {
            assertThrows(NullPointerException.class, () ->
                    BloqueHorario.create(MATERIA_ID, null, LocalTime.of(12, 0), LocalTime.of(14, 0), null));
        }

        @Test
        @DisplayName("Debe lanzar excepción si horas son inválidas")
        void debeLanzarExcepcionSiHorasInvalidas() {
            assertThrows(IllegalArgumentException.class, () ->
                    BloqueHorario.create(MATERIA_ID, DiaSemana.of("LUNES"), LocalTime.of(14, 0), LocalTime.of(12, 0), null));
        }
    }

    @Nested
    @DisplayName("Actualización")
    class Actualizacion {

        @Test
        @DisplayName("Debe actualizar todos los campos")
        void debeActualizarTodosLosCampos() {
            BloqueHorario bloque = BloqueHorario.create(
                    MATERIA_ID, DiaSemana.of("LUNES"), LocalTime.of(12, 0), LocalTime.of(14, 0), "Aula 101");

            LocalDateTime antes = bloque.getFechaActualizacion();
            bloque.actualizar(DiaSemana.of("MIERCOLES"), LocalTime.of(10, 0), LocalTime.of(12, 0), "Aula 202");

            assertEquals(DiaSemana.of("MIERCOLES"), bloque.getDiaSemana());
            assertEquals(LocalTime.of(10, 0), bloque.getHoraInicio());
            assertEquals(LocalTime.of(12, 0), bloque.getHoraFin());
            assertEquals("Aula 202", bloque.getAula());
            assertTrue(bloque.getFechaActualizacion().isAfter(antes));
        }
    }

    @Nested
    @DisplayName("Mover (Drag & Drop)")
    class Mover {

        @Test
        @DisplayName("Debe mover a nuevo día y hora")
        void debeMoverANuevoDiaYHora() {
            BloqueHorario bloque = BloqueHorario.create(
                    MATERIA_ID, DiaSemana.of("LUNES"), LocalTime.of(12, 0), LocalTime.of(14, 0), "Aula 101");

            LocalDateTime antes = bloque.getFechaActualizacion();
            bloque.mover(DiaSemana.of("JUEVES"), LocalTime.of(16, 0), LocalTime.of(18, 0));

            assertEquals(DiaSemana.of("JUEVES"), bloque.getDiaSemana());
            assertEquals(LocalTime.of(16, 0), bloque.getHoraInicio());
            assertEquals(LocalTime.of(18, 0), bloque.getHoraFin());
            assertEquals("Aula 101", bloque.getAula()); // aula se mantiene
            assertTrue(bloque.getFechaActualizacion().isAfter(antes));
        }
    }

    @Nested
    @DisplayName("Activación/Desactivación")
    class Activacion {

        @Test
        @DisplayName("Debe desactivar (soft delete)")
        void debeDesactivar() {
            BloqueHorario bloque = BloqueHorario.create(
                    MATERIA_ID, DiaSemana.of("LUNES"), LocalTime.of(12, 0), LocalTime.of(14, 0), null);

            bloque.desactivar();
            assertFalse(bloque.isActivo());
        }

        @Test
        @DisplayName("Debe reactivar")
        void debeReactivar() {
            BloqueHorario bloque = BloqueHorario.create(
                    MATERIA_ID, DiaSemana.of("LUNES"), LocalTime.of(12, 0), LocalTime.of(14, 0), null);
            bloque.desactivar();

            bloque.activar();
            assertTrue(bloque.isActivo());
        }
    }

    @Nested
    @DisplayName("Detección de solapamiento")
    class Solapamiento {

        @Test
        @DisplayName("Debe detectar solapamiento mismo día")
        void debeDetectarSolapamientoMismoDia() {
            BloqueHorario b1 = BloqueHorario.create(MATERIA_ID, DiaSemana.of("LUNES"),
                    LocalTime.of(12, 0), LocalTime.of(14, 0), null);
            BloqueHorario b2 = BloqueHorario.create(MATERIA_ID, DiaSemana.of("LUNES"),
                    LocalTime.of(13, 0), LocalTime.of(15, 0), null);

            assertTrue(b1.solapaCon(b2));
            assertTrue(b2.solapaCon(b1));
        }

        @Test
        @DisplayName("No debe solaparse si diferente día")
        void noDebeSolaparseSiDiferenteDia() {
            BloqueHorario b1 = BloqueHorario.create(MATERIA_ID, DiaSemana.of("LUNES"),
                    LocalTime.of(12, 0), LocalTime.of(14, 0), null);
            BloqueHorario b2 = BloqueHorario.create(MATERIA_ID, DiaSemana.of("MARTES"),
                    LocalTime.of(12, 0), LocalTime.of(14, 0), null);

            assertFalse(b1.solapaCon(b2));
        }

        @Test
        @DisplayName("No debe solaparse si misma materia pero horas adyacentes")
        void noDebeSolaparseSiHorasAdyacentes() {
            BloqueHorario b1 = BloqueHorario.create(MATERIA_ID, DiaSemana.of("LUNES"),
                    LocalTime.of(12, 0), LocalTime.of(14, 0), null);
            BloqueHorario b2 = BloqueHorario.create(MATERIA_ID, DiaSemana.of("LUNES"),
                    LocalTime.of(14, 0), LocalTime.of(16, 0), null);

            assertFalse(b1.solapaCon(b2));
        }

        @Test
        @DisplayName("Debe detectar solapamiento con otra materia mismo día")
        void debeDetectarSolapamientoOtraMateria() {
            BloqueHorario b1 = BloqueHorario.create(MATERIA_ID, DiaSemana.of("LUNES"),
                    LocalTime.of(12, 0), LocalTime.of(14, 0), null);
            BloqueHorario b2 = BloqueHorario.create(OTRA_MATERIA_ID, DiaSemana.of("LUNES"),
                    LocalTime.of(13, 0), LocalTime.of(15, 0), null);

            assertTrue(b1.solapaCon(b2));
        }
    }

    @Nested
    @DisplayName("Igualdad")
    class Igualdad {

        @Test
        @DisplayName("Dos bloques con mismos datos de negocio son iguales")
        void mismosDatosSonIguales() {
            UUID id = UUID.randomUUID();
            BloqueHorario b1 = new BloqueHorario(id, MATERIA_ID, DiaSemana.of("LUNES"),
                    RangoHorario.of(LocalTime.of(12,0), LocalTime.of(14,0)),
                    "Aula 101", true, LocalDateTime.now(), LocalDateTime.now());
            BloqueHorario b2 = new BloqueHorario(id, MATERIA_ID, DiaSemana.of("LUNES"),
                    RangoHorario.of(LocalTime.of(12,0), LocalTime.of(14,0)),
                    "Aula 101", true, LocalDateTime.now(), LocalDateTime.now());

            assertEquals(b1, b2);
        }
    }
}