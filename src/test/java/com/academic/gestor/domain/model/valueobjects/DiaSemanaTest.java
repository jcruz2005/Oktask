package com.academic.gestor.domain.model.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.DayOfWeek;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DiaSemana Value Object Tests")
class DiaSemanaTest {

    @Nested
    @DisplayName("Creación válida")
    class CreacionValida {

        @ParameterizedTest
        @ValueSource(strings = {"LUNES", "lunes", "Lunes", "  MARTES  ", "miercoles", "MIERCOLES"})
        @DisplayName("Debe crear DiaSemana desde string válido (case insensitive)")
        void debeCrearDesdeStringValido(String input) {
            DiaSemana dia = DiaSemana.of(input);
            assertNotNull(dia);
            assertEquals(input.trim().toUpperCase(), dia.getValor());
        }

        @Test
        @DisplayName("Debe crear desde DayOfWeek de Java")
        void debeCrearDesdeDayOfWeek() {
            assertEquals(DiaSemana.of("LUNES"), DiaSemana.fromJavaDayOfWeek(DayOfWeek.MONDAY));
            assertEquals(DiaSemana.of("MARTES"), DiaSemana.fromJavaDayOfWeek(DayOfWeek.TUESDAY));
            assertEquals(DiaSemana.of("MIERCOLES"), DiaSemana.fromJavaDayOfWeek(DayOfWeek.WEDNESDAY));
            assertEquals(DiaSemana.of("JUEVES"), DiaSemana.fromJavaDayOfWeek(DayOfWeek.THURSDAY));
            assertEquals(DiaSemana.of("VIERNES"), DiaSemana.fromJavaDayOfWeek(DayOfWeek.FRIDAY));
            assertEquals(DiaSemana.of("SABADO"), DiaSemana.fromJavaDayOfWeek(DayOfWeek.SATURDAY));
            assertEquals(DiaSemana.of("DOMINGO"), DiaSemana.fromJavaDayOfWeek(DayOfWeek.SUNDAY));
        }
    }

    @Nested
    @DisplayName("Validaciones")
    class Validaciones {

        @Test
        @DisplayName("Debe lanzar excepción para null")
        void debeLanzarExcepcionParaNull() {
            assertThrows(IllegalArgumentException.class, () -> DiaSemana.of(null));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", "   ", "FUNDAY", "LUNESS", "MONDAY"})
        @DisplayName("Debe lanzar excepción para valores inválidos")
        void debeLanzarExcepcionParaInvalidos(String input) {
            assertThrows(IllegalArgumentException.class, () -> DiaSemana.of(input));
        }
    }

    @Nested
    @DisplayName("Comportamiento")
    class Comportamiento {

        @Test
        @DisplayName("Debe retornar orden correcto (0-6)")
        void debeRetornarOrdenCorrecto() {
            assertEquals(0, DiaSemana.of("LUNES").getOrden());
            assertEquals(1, DiaSemana.of("MARTES").getOrden());
            assertEquals(6, DiaSemana.of("DOMINGO").getOrden());
        }

        @Test
        @DisplayName("Debe identificar días laborales correctamente")
        void debeIdentificarDiasLaborales() {
            assertTrue(DiaSemana.of("LUNES").esLaboral());
            assertTrue(DiaSemana.of("VIERNES").esLaboral());
            assertFalse(DiaSemana.of("SABADO").esLaboral());
            assertFalse(DiaSemana.of("DOMINGO").esLaboral());
        }

        @Test
        @DisplayName("Debe ser igual por valor")
        void debeSerIgualPorValor() {
            DiaSemana d1 = DiaSemana.of("LUNES");
            DiaSemana d2 = DiaSemana.of("lunes");
            assertEquals(d1, d2);
            assertEquals(d1.hashCode(), d2.hashCode());
        }

        @Test
        @DisplayName("toString debe retornar valor")
        void toStringDebeRetornarValor() {
            assertEquals("LUNES", DiaSemana.of("LUNES").toString());
        }
    }
}