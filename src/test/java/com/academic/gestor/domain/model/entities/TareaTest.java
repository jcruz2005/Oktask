package com.academic.gestor.domain.model.entities;

import com.academic.gestor.domain.events.TareaCompletadaEvent;
import com.academic.gestor.domain.model.enums.EstadoTarea;
import com.academic.gestor.domain.model.enums.Prioridad;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para la entidad Tarea")
class TareaTest {

    private static final UUID MATERIA_ID = UUID.randomUUID();
    private static final String TITULO_DEFAULT = "Entrega TP1";
    private static final String DESCRIPCION_DEFAULT = "Resolver ejercicios del cap 1";
    private static final LocalDate FECHA_LIMITE_DEFAULT = LocalDate.now().plusDays(7);
    private static final Prioridad PRIORIDAD_DEFAULT = Prioridad.ALTA;

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("deberia crear tarea con datos validos")
        void deberiaCrearTareaConDatosValidos() {
            // Arrange
            UUID id = UUID.randomUUID();
            LocalDateTime fechaCreacion = LocalDateTime.now();
            LocalDateTime fechaCompletado = LocalDateTime.now();

            // Act
            Tarea tarea = new Tarea(id, TITULO_DEFAULT, DESCRIPCION_DEFAULT,
                    MATERIA_ID, FECHA_LIMITE_DEFAULT, PRIORIDAD_DEFAULT,
                    EstadoTarea.EN_PROGRESO, fechaCreacion, fechaCompletado, 0);

            // Assert
            assertNotNull(tarea);
            assertEquals(id, tarea.getId());
            assertEquals(TITULO_DEFAULT, tarea.getTitulo());
            assertEquals(DESCRIPCION_DEFAULT, tarea.getDescripcion());
            assertEquals(MATERIA_ID, tarea.getMateriaId());
            assertEquals(FECHA_LIMITE_DEFAULT, tarea.getFechaLimite());
            assertEquals(PRIORIDAD_DEFAULT, tarea.getPrioridad());
            assertEquals(EstadoTarea.EN_PROGRESO, tarea.getEstado());
            assertEquals(fechaCreacion, tarea.getFechaCreacion());
            assertEquals(fechaCompletado, tarea.getFechaCompletado());
        }

        @Test
        @DisplayName("deberia permitir fechaCompletado null")
        void deberiaPermitirFechaCompletadoNull() {
            // Arrange & Act
            Tarea tarea = new Tarea(UUID.randomUUID(), TITULO_DEFAULT, DESCRIPCION_DEFAULT,
                    MATERIA_ID, FECHA_LIMITE_DEFAULT, PRIORIDAD_DEFAULT,
                    EstadoTarea.PENDIENTE, LocalDateTime.now(), null, 0);

            // Assert
            assertNull(tarea.getFechaCompletado());
        }

        @Test
        @DisplayName("deberia lanzar NullPointerException cuando titulo es null")
        void deberiaLanzarExcepcionCuandoTituloEsNull() {
            // Arrange & Act & Then
            assertThrows(IllegalArgumentException.class,
                    () -> new Tarea(UUID.randomUUID(), null, DESCRIPCION_DEFAULT,
                            MATERIA_ID, FECHA_LIMITE_DEFAULT, PRIORIDAD_DEFAULT,
                            EstadoTarea.PENDIENTE, LocalDateTime.now(), null, 0));
        }

        @Test
        @DisplayName("deberia lanzar NullPointerException cuando materiaId es nulo")
        void deberiaLanzarExcepcionCuandoMateriaIdEsNulo() {
            // Arrange & Act & Then
            assertThrows(NullPointerException.class,
                    () -> new Tarea(UUID.randomUUID(), TITULO_DEFAULT, DESCRIPCION_DEFAULT,
                            null, FECHA_LIMITE_DEFAULT, PRIORIDAD_DEFAULT,
                            EstadoTarea.PENDIENTE, LocalDateTime.now(), null, 0));
        }

        @Test
        @DisplayName("deberia lanzar NullPointerException cuando fechaLimite es nula")
        void deberiaLanzarExcepcionCuandoFechaLimiteEsNula() {
            // Arrange & Act & Then
            assertThrows(NullPointerException.class,
                    () -> new Tarea(UUID.randomUUID(), TITULO_DEFAULT, DESCRIPCION_DEFAULT,
                            MATERIA_ID, null, PRIORIDAD_DEFAULT,
                            EstadoTarea.PENDIENTE, LocalDateTime.now(), null, 0));
        }

        @Test
        @DisplayName("deberia lanzar NullPointerException cuando prioridad es nula")
        void deberiaLanzarExcepcionCuandoPrioridadEsNula() {
            // Arrange & Act & Then
            assertThrows(NullPointerException.class,
                    () -> new Tarea(UUID.randomUUID(), TITULO_DEFAULT, DESCRIPCION_DEFAULT,
                            MATERIA_ID, FECHA_LIMITE_DEFAULT, null,
                            EstadoTarea.PENDIENTE, LocalDateTime.now(), null, 0));
        }

        @Test
        @DisplayName("deberia lanzar NullPointerException cuando estado es nulo")
        void deberiaLanzarExcepcionCuandoEstadoEsNulo() {
            // Arrange & Act & Then
            assertThrows(NullPointerException.class,
                    () -> new Tarea(UUID.randomUUID(), TITULO_DEFAULT, DESCRIPCION_DEFAULT,
                            MATERIA_ID, FECHA_LIMITE_DEFAULT, PRIORIDAD_DEFAULT,
                            null, LocalDateTime.now(), null, 0));
        }
    }

    @Nested
    @DisplayName("Factory create")
    class CreateTests {

        @Test
        @DisplayName("deberia crear tarea con factory method en estado PENDIENTE")
        void deberiaCrearTareaConFactoryMethod() {
            // Arrange & Act
            Tarea tarea = Tarea.create(TITULO_DEFAULT, DESCRIPCION_DEFAULT,
                    MATERIA_ID, FECHA_LIMITE_DEFAULT, PRIORIDAD_DEFAULT);

            // Assert
            assertNotNull(tarea);
            assertNotNull(tarea.getId());
            assertEquals(TITULO_DEFAULT, tarea.getTitulo());
            assertEquals(DESCRIPCION_DEFAULT, tarea.getDescripcion());
            assertEquals(MATERIA_ID, tarea.getMateriaId());
            assertEquals(FECHA_LIMITE_DEFAULT, tarea.getFechaLimite());
            assertEquals(PRIORIDAD_DEFAULT, tarea.getPrioridad());
            assertEquals(EstadoTarea.PENDIENTE, tarea.getEstado());
            assertNotNull(tarea.getFechaCreacion());
            assertNull(tarea.getFechaCompletado());
        }
    }

    @Nested
    @DisplayName("setTitulo")
    class SetTituloTests {

        @Test
        @DisplayName("deberia actualizar titulo cuando es valido")
        void deberiaActualizarTitulo() {
            // Arrange
            Tarea tarea = Tarea.create(TITULO_DEFAULT, DESCRIPCION_DEFAULT,
                    MATERIA_ID, FECHA_LIMITE_DEFAULT, PRIORIDAD_DEFAULT);

            // Act
            tarea.setTitulo("Nuevo titulo");

            // Assert
            assertEquals("Nuevo titulo", tarea.getTitulo());
        }

        @Test
        @DisplayName("deberia trim el titulo con espacios")
        void deberiaTrimElTitulo() {
            // Arrange
            Tarea tarea = Tarea.create(TITULO_DEFAULT, DESCRIPCION_DEFAULT,
                    MATERIA_ID, FECHA_LIMITE_DEFAULT, PRIORIDAD_DEFAULT);

            // Act
            tarea.setTitulo("  Titulo con espacios  ");

            // Assert
            assertEquals("Titulo con espacios", tarea.getTitulo());
        }

        @Test
        @DisplayName("deberia lanzar IllegalArgumentException cuando titulo es null")
        void deberiaLanzarExcepcionCuandoTituloEsNull() {
            // Arrange
            Tarea tarea = Tarea.create(TITULO_DEFAULT, DESCRIPCION_DEFAULT,
                    MATERIA_ID, FECHA_LIMITE_DEFAULT, PRIORIDAD_DEFAULT);

            // Act & Then
            assertThrows(IllegalArgumentException.class, () -> tarea.setTitulo(null));
        }

        @Test
        @DisplayName("deberia lanzar IllegalArgumentException cuando titulo esta en blanco")
        void deberiaLanzarExcepcionCuandoTituloEstaEnBlanco() {
            // Arrange
            Tarea tarea = Tarea.create(TITULO_DEFAULT, DESCRIPCION_DEFAULT,
                    MATERIA_ID, FECHA_LIMITE_DEFAULT, PRIORIDAD_DEFAULT);

            // Act & Then
            assertThrows(IllegalArgumentException.class, () -> tarea.setTitulo("   "));
        }
    }

    @Nested
    @DisplayName("cambiarEstado")
    class CambiarEstadoTests {

        @Test
        @DisplayName("deberia cambiar de PENDIENTE a EN_PROGRESO")
        void deberiaCambiarDePendienteAEnProgreso() {
            // Arrange
            Tarea tarea = Tarea.create(TITULO_DEFAULT, DESCRIPCION_DEFAULT,
                    MATERIA_ID, FECHA_LIMITE_DEFAULT, PRIORIDAD_DEFAULT);

            // Act
            tarea.cambiarEstado(EstadoTarea.EN_PROGRESO);

            // Assert
            assertEquals(EstadoTarea.EN_PROGRESO, tarea.getEstado());
            assertNull(tarea.getFechaCompletado());
        }

        @Test
        @DisplayName("deberia cambiar a COMPLETADA y registrar fecha y evento")
        void deberiaCambiarACompletada() {
            // Arrange
            Tarea tarea = Tarea.create(TITULO_DEFAULT, DESCRIPCION_DEFAULT,
                    MATERIA_ID, FECHA_LIMITE_DEFAULT, PRIORIDAD_DEFAULT);

            // Act
            tarea.cambiarEstado(EstadoTarea.COMPLETADA);

            // Assert
            assertEquals(EstadoTarea.COMPLETADA, tarea.getEstado());
            assertNotNull(tarea.getFechaCompletado());
            assertFalse(tarea.getDomainEvents().isEmpty());

            Object evento = tarea.getDomainEvents().get(0);
            assertInstanceOf(TareaCompletadaEvent.class, evento);
            TareaCompletadaEvent event = (TareaCompletadaEvent) evento;
            assertEquals(tarea.getId(), event.tareaId());
            assertEquals(MATERIA_ID, event.materiaId());
            assertNotNull(event.fechaCompletado());
        }

        @Test
        @DisplayName("deberia lanzar NullPointerException cuando estado es nulo")
        void deberiaLanzarExcepcionCuandoEstadoEsNulo() {
            // Arrange
            Tarea tarea = Tarea.create(TITULO_DEFAULT, DESCRIPCION_DEFAULT,
                    MATERIA_ID, FECHA_LIMITE_DEFAULT, PRIORIDAD_DEFAULT);

            // Act & Then
            assertThrows(NullPointerException.class, () -> tarea.cambiarEstado(null));
        }
    }

    @Nested
    @DisplayName("isAtrasada")
    class IsAtrasadaTests {

        @Test
        @DisplayName("deberia retornar false cuando fecha limite es futura")
        void deberiaRetornarFalseCuandoFechaLimiteEsFutura() {
            // Arrange
            Tarea tarea = Tarea.create(TITULO_DEFAULT, DESCRIPCION_DEFAULT,
                    MATERIA_ID, LocalDate.now().plusDays(10), PRIORIDAD_DEFAULT);

            // Act & Assert
            assertFalse(tarea.isAtrasada());
        }

        @Test
        @DisplayName("deberia retornar false cuando esta completada aunque fecha paso")
        void deberiaRetornarFalseCuandoEstaCompletada() {
            // Arrange
            Tarea tarea = Tarea.create(TITULO_DEFAULT, DESCRIPCION_DEFAULT,
                    MATERIA_ID, LocalDate.now().minusDays(1), PRIORIDAD_DEFAULT);
            tarea.cambiarEstado(EstadoTarea.COMPLETADA);

            // Act & Assert
            assertFalse(tarea.isAtrasada());
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
            Tarea t1 = new Tarea(id, "T1", "D1", MATERIA_ID, FECHA_LIMITE_DEFAULT,
                    PRIORIDAD_DEFAULT, EstadoTarea.PENDIENTE, LocalDateTime.now(), null, 0);
            Tarea t2 = new Tarea(id, "T2", "D2", UUID.randomUUID(),
                    LocalDate.now().plusDays(20), Prioridad.BAJA,
                    EstadoTarea.COMPLETADA, LocalDateTime.now(), LocalDateTime.now(), 0);

            // Act & Assert
            assertEquals(t1, t2);
            assertEquals(t1.hashCode(), t2.hashCode());
        }

        @Test
        @DisplayName("deberia ser diferente cuando distinto ID")
        void deberiaSerDiferenteCuandoDistintoId() {
            // Arrange
            Tarea t1 = Tarea.create(TITULO_DEFAULT, DESCRIPCION_DEFAULT,
                    MATERIA_ID, FECHA_LIMITE_DEFAULT, PRIORIDAD_DEFAULT);
            Tarea t2 = Tarea.create(TITULO_DEFAULT, DESCRIPCION_DEFAULT,
                    MATERIA_ID, FECHA_LIMITE_DEFAULT, PRIORIDAD_DEFAULT);

            // Act & Assert
            assertNotEquals(t1, t2);
        }
    }
}
