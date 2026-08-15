package com.academic.gestor.domain.model.entities;

import com.academic.gestor.domain.model.enums.Prioridad;
import com.academic.gestor.domain.model.valueobjects.Color;
import com.academic.gestor.domain.model.valueobjects.CodigoMateria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para la entidad Materia")
class MateriaTest {

    private static final String NOMBRE_DEFAULT = "Matematica";
    private static final CodigoMateria CODIGO_DEFAULT = CodigoMateria.of("MAT001");
    private static final Color COLOR_DEFAULT = Color.of("#FF5733");
    private static final Prioridad PRIORIDAD_DEFAULT = Prioridad.ALTA;

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("deberia crear materia con datos validos")
        void deberiaCrearMateriaConDatosValidos() {
            // Arrange
            UUID id = UUID.randomUUID();
            String nombre = "Programacion";
            CodigoMateria codigo = CodigoMateria.of("PROG2");
            Color color = Color.of("#3498DB");
            Prioridad prioridad = Prioridad.MEDIA;
            LocalDateTime fechaCreacion = LocalDateTime.now();

            // Act
            Materia materia = new Materia(id, nombre, codigo, color, prioridad, fechaCreacion, true);

            // Assert
            assertNotNull(materia);
            assertEquals(id, materia.getId());
            assertEquals(nombre, materia.getNombre());
            assertEquals(codigo, materia.getCodigo());
            assertEquals(color, materia.getColor());
            assertEquals(prioridad, materia.getPrioridad());
            assertEquals(fechaCreacion, materia.getFechaCreacion());
            assertTrue(materia.isActiva());
        }

        @Test
        @DisplayName("deberia lanzar NullPointerException cuando codigo es nulo")
        void deberiaLanzarExcepcionCuandoCodigoEsNulo() {
            // Arrange & Act & Then
            assertThrows(NullPointerException.class,
                    () -> new Materia(UUID.randomUUID(), "Mat", null,
                            COLOR_DEFAULT, PRIORIDAD_DEFAULT, LocalDateTime.now(), true));
        }

        @Test
        @DisplayName("deberia lanzar NullPointerException cuando color es nulo")
        void deberiaLanzarExcepcionCuandoColorEsNulo() {
            // Arrange & Act & Then
            assertThrows(NullPointerException.class,
                    () -> new Materia(UUID.randomUUID(), "Mat", CODIGO_DEFAULT,
                            null, PRIORIDAD_DEFAULT, LocalDateTime.now(), true));
        }

        @Test
        @DisplayName("deberia lanzar NullPointerException cuando prioridad es nula")
        void deberiaLanzarExcepcionCuandoPrioridadEsNula() {
            // Arrange & Act & Then
            assertThrows(NullPointerException.class,
                    () -> new Materia(UUID.randomUUID(), "Mat", CODIGO_DEFAULT,
                            COLOR_DEFAULT, null, LocalDateTime.now(), true));
        }

        @Test
        @DisplayName("deberia lanzar NullPointerException cuando fechaCreacion es nula")
        void deberiaLanzarExcepcionCuandoFechaCreacionEsNula() {
            // Arrange & Act & Then
            assertThrows(NullPointerException.class,
                    () -> new Materia(UUID.randomUUID(), "Mat", CODIGO_DEFAULT,
                            COLOR_DEFAULT, PRIORIDAD_DEFAULT, null, true));
        }
    }

    @Nested
    @DisplayName("Factory create")
    class CreateTests {

        @Test
        @DisplayName("deberia crear materia con factory method con valores por defecto")
        void deberiaCrearMateriaConFactoryMethod() {
            // Arrange & Act
            Materia materia = Materia.create(NOMBRE_DEFAULT, CODIGO_DEFAULT,
                    COLOR_DEFAULT, PRIORIDAD_DEFAULT);

            // Assert
            assertNotNull(materia);
            assertNotNull(materia.getId());
            assertEquals(NOMBRE_DEFAULT, materia.getNombre());
            assertEquals(CODIGO_DEFAULT, materia.getCodigo());
            assertEquals(COLOR_DEFAULT, materia.getColor());
            assertEquals(PRIORIDAD_DEFAULT, materia.getPrioridad());
            assertNotNull(materia.getFechaCreacion());
            assertTrue(materia.isActiva());
        }
    }

    @Nested
    @DisplayName("setNombre")
    class SetNombreTests {

        @Test
        @DisplayName("deberia actualizar nombre cuando es valido")
        void deberiaActualizarNombreCuandoEsValido() {
            // Arrange
            Materia materia = Materia.create(NOMBRE_DEFAULT, CODIGO_DEFAULT,
                    COLOR_DEFAULT, PRIORIDAD_DEFAULT);

            // Act
            materia.setNombre("Fisica");

            // Assert
            assertEquals("Fisica", materia.getNombre());
        }

        @Test
        @DisplayName("deberia trim el nombre con espacios")
        void deberiaTrimElNombreConEspacios() {
            // Arrange
            Materia materia = Materia.create(NOMBRE_DEFAULT, CODIGO_DEFAULT,
                    COLOR_DEFAULT, PRIORIDAD_DEFAULT);

            // Act
            materia.setNombre("  Quimica  ");

            // Assert
            assertEquals("Quimica", materia.getNombre());
        }

        @Test
        @DisplayName("deberia lanzar IllegalArgumentException cuando nombre es null")
        void deberiaLanzarExcepcionCuandoNombreEsNull() {
            // Arrange
            Materia materia = Materia.create(NOMBRE_DEFAULT, CODIGO_DEFAULT,
                    COLOR_DEFAULT, PRIORIDAD_DEFAULT);

            // Act & Then
            assertThrows(IllegalArgumentException.class, () -> materia.setNombre(null));
        }

        @Test
        @DisplayName("deberia lanzar IllegalArgumentException cuando nombre esta en blanco")
        void deberiaLanzarExcepcionCuandoNombreEstaEnBlanco() {
            // Arrange
            Materia materia = Materia.create(NOMBRE_DEFAULT, CODIGO_DEFAULT,
                    COLOR_DEFAULT, PRIORIDAD_DEFAULT);

            // Act & Then
            assertThrows(IllegalArgumentException.class, () -> materia.setNombre("   "));
        }
    }

    @Nested
    @DisplayName("setColor")
    class SetColorTests {

        @Test
        @DisplayName("deberia actualizar color cuando es valido")
        void deberiaActualizarColor() {
            // Arrange
            Materia materia = Materia.create(NOMBRE_DEFAULT, CODIGO_DEFAULT,
                    COLOR_DEFAULT, PRIORIDAD_DEFAULT);
            Color nuevoColor = Color.of("#00FF00");

            // Act
            materia.setColor(nuevoColor);

            // Assert
            assertEquals(nuevoColor, materia.getColor());
        }

        @Test
        @DisplayName("deberia lanzar NullPointerException cuando color es nulo")
        void deberiaLanzarExcepcionCuandoColorEsNulo() {
            // Arrange
            Materia materia = Materia.create(NOMBRE_DEFAULT, CODIGO_DEFAULT,
                    COLOR_DEFAULT, PRIORIDAD_DEFAULT);

            // Act & Then
            assertThrows(NullPointerException.class, () -> materia.setColor(null));
        }
    }

    @Nested
    @DisplayName("setPrioridad")
    class SetPrioridadTests {

        @Test
        @DisplayName("deberia actualizar prioridad cuando es valida")
        void deberiaActualizarPrioridad() {
            // Arrange
            Materia materia = Materia.create(NOMBRE_DEFAULT, CODIGO_DEFAULT,
                    COLOR_DEFAULT, PRIORIDAD_DEFAULT);

            // Act
            materia.setPrioridad(Prioridad.BAJA);

            // Assert
            assertEquals(Prioridad.BAJA, materia.getPrioridad());
        }

        @Test
        @DisplayName("deberia lanzar NullPointerException cuando prioridad es nula")
        void deberiaLanzarExcepcionCuandoPrioridadEsNula() {
            // Arrange
            Materia materia = Materia.create(NOMBRE_DEFAULT, CODIGO_DEFAULT,
                    COLOR_DEFAULT, PRIORIDAD_DEFAULT);

            // Act & Then
            assertThrows(NullPointerException.class, () -> materia.setPrioridad(null));
        }
    }

    @Nested
    @DisplayName("desactivar y activar")
    class ActivarDesactivarTests {

        @Test
        @DisplayName("deberia desactivar materia activa")
        void deberiaDesactivarMateriaActiva() {
            // Arrange
            Materia materia = Materia.create(NOMBRE_DEFAULT, CODIGO_DEFAULT,
                    COLOR_DEFAULT, PRIORIDAD_DEFAULT);
            assertTrue(materia.isActiva());

            // Act
            materia.desactivar();

            // Assert
            assertFalse(materia.isActiva());
        }

        @Test
        @DisplayName("deberia reactivar materia desactivada")
        void deberiaReactivarMateriaDesactivada() {
            // Arrange
            Materia materia = Materia.create(NOMBRE_DEFAULT, CODIGO_DEFAULT,
                    COLOR_DEFAULT, PRIORIDAD_DEFAULT);
            materia.desactivar();
            assertFalse(materia.isActiva());

            // Act
            materia.activar();

            // Assert
            assertTrue(materia.isActiva());
        }
    }

    @Nested
    @DisplayName("Igualdad")
    class IgualdadTests {

        @Test
        @DisplayName("deberia ser igual cuando mismo ID")
        void deberiaSerIgualCuandoMismoId() {
            // Arrange
            UUID id = UUID.randomUUID();
            Materia m1 = new Materia(id, "Mat1", CODIGO_DEFAULT, COLOR_DEFAULT,
                    PRIORIDAD_DEFAULT, LocalDateTime.now(), true);
            Materia m2 = new Materia(id, "Mat2", CodigoMateria.of("OTRO"),
                    Color.of("#000000"), Prioridad.BAJA, LocalDateTime.now(), false);

            // Act & Assert
            assertEquals(m1, m2);
            assertEquals(m1.hashCode(), m2.hashCode());
        }

        @Test
        @DisplayName("deberia ser diferente cuando distinto ID")
        void deberiaSerDiferenteCuandoDistintoId() {
            // Arrange
            Materia m1 = Materia.create("Mat", CODIGO_DEFAULT, COLOR_DEFAULT, PRIORIDAD_DEFAULT);
            Materia m2 = Materia.create("Mat", CODIGO_DEFAULT, COLOR_DEFAULT, PRIORIDAD_DEFAULT);

            // Act & Assert
            assertNotEquals(m1, m2);
        }
    }
}
