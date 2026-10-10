package com.academic.gestor.domain.model.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RangoHorario Value Object Tests")
class RangoHorarioTest {

    @Nested
    @DisplayName("Creación válida")
    class CreacionValida {

        @Test
        @DisplayName("Debe crear desde LocalTime")
        void debeCrearDesdeLocalTime() {
            RangoHorario rango = RangoHorario.of(LocalTime.of(12, 0), LocalTime.of(14, 0));
            assertEquals(LocalTime.of(12, 0), rango.getInicio());
            assertEquals(LocalTime.of(14, 0), rango.getFin());
        }

        @Test
        @DisplayName("Debe crear desde String")
        void debeCrearDesdeString() {
            RangoHorario rango = RangoHorario.of("08:30", "10:00");
            assertEquals(LocalTime.of(8, 30), rango.getInicio());
            assertEquals(LocalTime.of(10, 0), rango.getFin());
        }

        @Test
        @DisplayName("Debe calcular duración en minutos")
        void debeCalcularDuracion() {
            assertEquals(120, RangoHorario.of(LocalTime.of(12, 0), LocalTime.of(14, 0)).getDuracionMinutos());
            assertEquals(90, RangoHorario.of(LocalTime.of(8, 30), LocalTime.of(10, 0)).getDuracionMinutos());
            assertEquals(30, RangoHorario.of(LocalTime.of(12, 0), LocalTime.of(12, 30)).getDuracionMinutos());
        }
    }

    @Nested
    @DisplayName("Validaciones")
    class Validaciones {

        @Test
        @DisplayName("Debe lanzar excepción si inicio es nulo")
        void debeLanzarExcepcionSiInicioNulo() {
            assertThrows(IllegalArgumentException.class, () -> RangoHorario.of(null, LocalTime.of(14, 0)));
        }

        @Test
        @DisplayName("Debe lanzar excepción si fin es nulo")
        void debeLanzarExcepcionSiFinNulo() {
            assertThrows(IllegalArgumentException.class, () -> RangoHorario.of(LocalTime.of(12, 0), null));
        }

        @Test
        @DisplayName("Debe lanzar excepción si inicio >= fin")
        void debeLanzarExcepcionSiInicioMayorIgualFin() {
            assertThrows(IllegalArgumentException.class, () -> RangoHorario.of(LocalTime.of(14, 0), LocalTime.of(12, 0)));
            assertThrows(IllegalArgumentException.class, () -> RangoHorario.of(LocalTime.of(12, 0), LocalTime.of(12, 0)));
        }
    }

    @Nested
    @DisplayName("Lógica de solapamiento")
    class LogicaSolapamiento {

        @Test
        @DisplayName("Debe detectar solapamiento parcial al inicio")
        void debeDetectarSolapamientoParcialInicio() {
            RangoHorario r1 = RangoHorario.of(LocalTime.of(12, 0), LocalTime.of(14, 0));
            RangoHorario r2 = RangoHorario.of(LocalTime.of(13, 0), LocalTime.of(15, 0));
            assertTrue(r1.solapaCon(r2));
            assertTrue(r2.solapaCon(r1));
        }

        @Test
        @DisplayName("Debe detectar solapamiento parcial al fin")
        void debeDetectarSolapamientoParcialFin() {
            RangoHorario r1 = RangoHorario.of(LocalTime.of(12, 0), LocalTime.of(14, 0));
            RangoHorario r2 = RangoHorario.of(LocalTime.of(10, 0), LocalTime.of(13, 0));
            assertTrue(r1.solapaCon(r2));
        }

        @Test
        @DisplayName("Debe detectar contención total")
        void debeDetectarContencionTotal() {
            RangoHorario r1 = RangoHorario.of(LocalTime.of(12, 0), LocalTime.of(14, 0));
            RangoHorario r2 = RangoHorario.of(LocalTime.of(10, 0), LocalTime.of(16, 0));
            assertTrue(r1.solapaCon(r2));
            assertTrue(r2.solapaCon(r1));
        }

        @Test
        @DisplayName("Debe detectar rango contenido")
        void debeDetectarRangoContenido() {
            RangoHorario r1 = RangoHorario.of(LocalTime.of(10, 0), LocalTime.of(16, 0));
            RangoHorario r2 = RangoHorario.of(LocalTime.of(12, 0), LocalTime.of(14, 0));
            assertTrue(r1.solapaCon(r2));
        }

        @Test
        @DisplayName("No debe solaparse si son adyacentes (fin == inicio)")
        void noDebeSolaparseSiAdyacentes() {
            RangoHorario r1 = RangoHorario.of(LocalTime.of(12, 0), LocalTime.of(14, 0));
            RangoHorario r2 = RangoHorario.of(LocalTime.of(14, 0), LocalTime.of(16, 0));
            assertFalse(r1.solapaCon(r2));
            assertFalse(r2.solapaCon(r1));
        }

        @Test
        @DisplayName("No debe solaparse si son separados")
        void noDebeSolaparseSiSeparados() {
            RangoHorario r1 = RangoHorario.of(LocalTime.of(12, 0), LocalTime.of(14, 0));
            RangoHorario r2 = RangoHorario.of(LocalTime.of(16, 0), LocalTime.of(18, 0));
            assertFalse(r1.solapaCon(r2));
        }
    }

    @Nested
    @DisplayName("Contiene hora")
    class ContieneHora {

        @Test
        @DisplayName("Debe retornar true si hora está dentro del rango")
        void debeRetornarTrueSiHoraDentro() {
            RangoHorario rango = RangoHorario.of(LocalTime.of(12, 0), LocalTime.of(14, 0));
            assertTrue(rango.contiene(LocalTime.of(12, 0)));
            assertTrue(rango.contiene(LocalTime.of(13, 30)));
            assertFalse(rango.contiene(LocalTime.of(14, 0))); // fin es exclusivo
            assertFalse(rango.contiene(LocalTime.of(11, 59)));
        }
    }

    @Nested
    @DisplayName("Igualdad")
    class Igualdad {

        @Test
        @DisplayName("Debe ser igual por valor")
        void debeSerIgualPorValor() {
            RangoHorario r1 = RangoHorario.of(LocalTime.of(12, 0), LocalTime.of(14, 0));
            RangoHorario r2 = RangoHorario.of(LocalTime.of(12, 0), LocalTime.of(14, 0));
            assertEquals(r1, r2);
            assertEquals(r1.hashCode(), r2.hashCode());
        }

        @Test
        @DisplayName("toString debe tener formato correcto")
        void toStringFormatoCorrecto() {
            assertEquals("12:00 - 14:00", RangoHorario.of(LocalTime.of(12, 0), LocalTime.of(14, 0)).toString());
            assertEquals("08:30 - 10:00", RangoHorario.of("08:30", "10:00").toString());
        }
    }
}