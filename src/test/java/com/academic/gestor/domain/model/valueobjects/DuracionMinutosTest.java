package com.academic.gestor.domain.model.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para el Value Object DuracionMinutos")
class DuracionMinutosTest {

    @Nested
    @DisplayName("Constructor y validacion")
    class ConstructorTests {

        @Test
        @DisplayName("deberia crear duracion con minutos validos")
        void deberiaCrearDuracionConMinutosValidos() {
            // Arrange & Act
            DuracionMinutos duracion = new DuracionMinutos(25);

            // Assert
            assertEquals(25, duracion.minutos());
        }

        @Test
        @DisplayName("deberia crear duracion con 1 minuto (minimo)")
        void deberiaCrearDuracionCon1Minuto() {
            // Arrange & Act
            DuracionMinutos duracion = new DuracionMinutos(1);

            // Assert
            assertEquals(1, duracion.minutos());
        }

        @Test
        @DisplayName("deberia lanzar IllegalArgumentException cuando minutos es 0")
        void deberiaLanzarExcepcionCuandoMinutosEs0() {
            // Arrange & Act & Then
            assertThrows(IllegalArgumentException.class, () -> new DuracionMinutos(0));
        }

        @Test
        @DisplayName("deberia lanzar IllegalArgumentException cuando minutos es negativo")
        void deberiaLanzarExcepcionCuandoMinutosEsNegativo() {
            // Arrange & Act & Then
            assertThrows(IllegalArgumentException.class, () -> new DuracionMinutos(-5));
        }
    }

    @Nested
    @DisplayName("Factory of")
    class OfTests {

        @Test
        @DisplayName("deberia crear duracion desde factory method")
        void deberiaCrearDuracionDesdeFactoryMethod() {
            // Arrange & Act
            DuracionMinutos duracion = DuracionMinutos.of(30);

            // Assert
            assertEquals(30, duracion.minutos());
        }
    }

    @Nested
    @DisplayName("Conversiones")
    class ConversionesTests {

        @Test
        @DisplayName("deberia convertir a horas correctamente")
        void deberiaConvertirAHorasCorrectamente() {
            // Arrange
            DuracionMinutos duracion = DuracionMinutos.of(120);

            // Act
            double horas = duracion.toHours();

            // Assert
            assertEquals(2.0, horas, 0.001);
        }

        @Test
        @DisplayName("deberia retornar horas como entero")
        void deberiaRetornarHorasComoEntero() {
            // Arrange
            DuracionMinutos duracion = DuracionMinutos.of(90);

            // Act
            int horas = duracion.toHoursInt();

            // Assert
            assertEquals(1, horas);
        }

        @Test
        @DisplayName("deberia calcular minutos restantes")
        void deberiaCalcularMinutosRestantes() {
            // Arrange
            DuracionMinutos duracion = DuracionMinutos.of(90);

            // Act
            int restantes = duracion.remainingMinutes();

            // Assert
            assertEquals(30, restantes);
        }

        @Test
        @DisplayName("deberia retornar 0 minutos restantes cuando es multiplo de 60")
        void deberiaRetornar0MinutosRestantesCuandoEsMultiploDe60() {
            // Arrange
            DuracionMinutos duracion = DuracionMinutos.of(120);

            // Act
            int restantes = duracion.remainingMinutes();

            // Assert
            assertEquals(0, restantes);
        }
    }

    @Nested
    @DisplayName("Sumar")
    class SumarTests {

        @Test
        @DisplayName("deberia sumar dos duraciones")
        void deberiaSumarDosDuraciones() {
            // Arrange
            DuracionMinutos d1 = DuracionMinutos.of(25);
            DuracionMinutos d2 = DuracionMinutos.of(5);

            // Act
            DuracionMinutos resultado = d1.sumar(d2);

            // Assert
            assertEquals(30, resultado.minutos());
        }

        @Test
        @DisplayName("deberia crear nueva instancia al sumar (inmutabilidad)")
        void deberiaCrearNuevaInstanciaAlSumar() {
            // Arrange
            DuracionMinutos d1 = DuracionMinutos.of(25);
            DuracionMinutos d2 = DuracionMinutos.of(5);

            // Act
            DuracionMinutos resultado = d1.sumar(d2);

            // Assert
            assertNotEquals(d1, resultado);
            assertEquals(25, d1.minutos());
        }
    }

    @Nested
    @DisplayName("toFormattedString")
    class ToFormattedStringTests {

        @Test
        @DisplayName("deberia formatear duracion con horas y minutos")
        void deberiaFormatearDuracionConHorasYMinutos() {
            // Arrange
            DuracionMinutos duracion = DuracionMinutos.of(90);

            // Act
            String resultado = duracion.toFormattedString();

            // Assert
            assertEquals("1h 30m", resultado);
        }

        @Test
        @DisplayName("deberia formatear duracion solo con minutos")
        void deberiaFormatearDuracionSoloConMinutos() {
            // Arrange
            DuracionMinutos duracion = DuracionMinutos.of(25);

            // Act
            String resultado = duracion.toFormattedString();

            // Assert
            assertEquals("0h 25m", resultado);
        }
    }

    @Nested
    @DisplayName("Igualdad")
    class IgualdadTests {

        @Test
        @DisplayName("deberia ser igual cuando mismo valor")
        void deberiaSerIgualCuandoMismoValor() {
            // Arrange
            DuracionMinutos d1 = new DuracionMinutos(25);
            DuracionMinutos d2 = new DuracionMinutos(25);

            // Act & Assert
            assertEquals(d1, d2);
            assertEquals(d1.hashCode(), d2.hashCode());
        }

        @Test
        @DisplayName("deberia ser diferente cuando distinto valor")
        void deberiaSerDiferenteCuandoDistintoValor() {
            // Arrange
            DuracionMinutos d1 = new DuracionMinutos(25);
            DuracionMinutos d2 = new DuracionMinutos(30);

            // Act & Assert
            assertNotEquals(d1, d2);
        }
    }
}
