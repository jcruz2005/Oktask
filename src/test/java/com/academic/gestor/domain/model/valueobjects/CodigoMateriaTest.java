package com.academic.gestor.domain.model.valueobjects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para el Value Object CodigoMateria")
class CodigoMateriaTest {

    @Nested
    @DisplayName("Constructor y validacion")
    class ConstructorTests {

        @Test
        @DisplayName("deberia crear codigo con formato valido")
        void deberiaCrearCodigoConFormatoValido() {
            // Arrange & Act
            CodigoMateria codigo = new CodigoMateria("MAT001");

            // Assert
            assertEquals("MAT001", codigo.value());
        }

        @Test
        @DisplayName("deberia crear codigo de 3 caracteres (minimo)")
        void deberiaCrearCodigoDe3Caracteres() {
            // Arrange & Act
            CodigoMateria codigo = new CodigoMateria("ABC");

            // Assert
            assertEquals("ABC", codigo.value());
        }

        @Test
        @DisplayName("deberia crear codigo de 10 caracteres (maximo)")
        void deberiaCrearCodigoDe10Caracteres() {
            // Arrange & Act
            CodigoMateria codigo = new CodigoMateria("ABCDEFGHIJ");

            // Assert
            assertEquals("ABCDEFGHIJ", codigo.value());
        }

        @Test
        @DisplayName("deberia lanzar NullPointerException cuando valor es nulo")
        void deberiaLanzarExcepcionCuandoValorEsNulo() {
            // Arrange & Act & Then
            assertThrows(NullPointerException.class, () -> new CodigoMateria(null));
        }

        @Test
        @DisplayName("deberia lanzar IllegalArgumentException cuando valor esta en blanco")
        void deberiaLanzarExcepcionCuandoValorEstaEnBlanco() {
            // Arrange & Act & Then
            assertThrows(IllegalArgumentException.class, () -> new CodigoMateria("   "));
        }

        @Test
        @DisplayName("deberia lanzar IllegalArgumentException cuando tiene menos de 3 caracteres")
        void deberiaLanzarExcepcionCuandoTieneMenosDe3Caracteres() {
            // Arrange & Act & Then
            assertThrows(IllegalArgumentException.class, () -> new CodigoMateria("AB"));
        }

        @Test
        @DisplayName("deberia lanzar IllegalArgumentException cuando tiene mas de 10 caracteres")
        void deberiaLanzarExcepcionCuandoTieneMasDe10Caracteres() {
            // Arrange & Act & Then
            assertThrows(IllegalArgumentException.class,
                    () -> new CodigoMateria("ABCDEFGHIJKL"));
        }

        @Test
        @DisplayName("deberia lanzar IllegalArgumentException cuando tiene caracteres especiales")
        void deberiaLanzarExcepcionCuandoTieneCaracteresEspeciales() {
            // Arrange & Act & Then
            assertThrows(IllegalArgumentException.class, () -> new CodigoMateria("MAT-01"));
        }

        @Test
        @DisplayName("deberia lanzar IllegalArgumentException cuando tiene minusculas")
        void deberiaLanzarExcepcionCuandoTieneMinusculas() {
            // Arrange & Act & Then
            assertThrows(IllegalArgumentException.class, () -> new CodigoMateria("mat001"));
        }
    }

    @Nested
    @DisplayName("Factory of")
    class OfTests {

        @Test
        @DisplayName("deberia crear codigo convirtiendo a mayusculas y haciendo trim")
        void deberiaCrearCodigoConMayusculasYTrim() {
            // Arrange & Act
            CodigoMateria codigo = CodigoMateria.of("  mat001  ");

            // Assert
            assertEquals("MAT001", codigo.value());
        }

        @Test
        @DisplayName("deberia crear codigo desde minusculas")
        void deberiaCrearCodigoDesdeMinusculas() {
            // Arrange & Act
            CodigoMateria codigo = CodigoMateria.of("prog2");

            // Assert
            assertEquals("PROG2", codigo.value());
        }
    }

    @Nested
    @DisplayName("Igualdad")
    class IgualdadTests {

        @Test
        @DisplayName("deberia ser igual cuando mismo valor")
        void deberiaSerIgualCuandoMismoValor() {
            // Arrange
            CodigoMateria c1 = new CodigoMateria("MAT001");
            CodigoMateria c2 = new CodigoMateria("MAT001");

            // Act & Assert
            assertEquals(c1, c2);
            assertEquals(c1.hashCode(), c2.hashCode());
        }

        @Test
        @DisplayName("deberia ser diferente cuando distinto valor")
        void deberiaSerDiferenteCuandoDistintoValor() {
            // Arrange
            CodigoMateria c1 = new CodigoMateria("MAT001");
            CodigoMateria c2 = new CodigoMateria("MAT002");

            // Act & Assert
            assertNotEquals(c1, c2);
        }
    }
}
