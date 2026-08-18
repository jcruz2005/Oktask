package com.academic.gestor.domain.model.entities;

import com.academic.gestor.domain.events.PomodoroCompletadoEvent;
import com.academic.gestor.domain.exceptions.SesionPomodoroException;
import com.academic.gestor.domain.model.enums.TipoSesion;
import com.academic.gestor.domain.model.valueobjects.DuracionMinutos;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para la entidad SesionPomodoro")
class SesionPomodoroTest {

    private static final UUID TAREA_ID = UUID.randomUUID();
    private static final UUID MATERIA_ID = UUID.randomUUID();
    private static final DuracionMinutos DURACION_DEFAULT = DuracionMinutos.of(25);

    @Nested
    @DisplayName("Constructor")
    class ConstructorTests {

        @Test
        @DisplayName("deberia crear sesion con datos validos")
        void deberiaCrearSesionConDatosValidos() {
            // Arrange
            UUID id = UUID.randomUUID();
            LocalDateTime fechaInicio = LocalDateTime.now();
            LocalDateTime fechaFin = LocalDateTime.now().plusMinutes(25);

            // Act
            SesionPomodoro sesion = new SesionPomodoro(id, TAREA_ID, MATERIA_ID,
                    DURACION_DEFAULT, TipoSesion.TRABAJO, fechaInicio, fechaFin, true);

            // Assert
            assertNotNull(sesion);
            assertEquals(id, sesion.getId());
            assertEquals(TAREA_ID, sesion.getTareaId());
            assertEquals(MATERIA_ID, sesion.getMateriaId());
            assertEquals(DURACION_DEFAULT, sesion.getDuracion());
            assertEquals(TipoSesion.TRABAJO, sesion.getTipoSesion());
            assertEquals(fechaInicio, sesion.getFechaInicio());
            assertEquals(fechaFin, sesion.getFechaFin());
            assertTrue(sesion.isCompletada());
        }

        @Test
        @DisplayName("deberia lanzar NullPointerException cuando tareaId es nulo")
        void deberiaLanzarExcepcionCuandoTareaIdEsNulo() {
            // Arrange & Act & Then
            assertThrows(NullPointerException.class,
                    () -> new SesionPomodoro(UUID.randomUUID(), null, MATERIA_ID,
                            DURACION_DEFAULT, TipoSesion.TRABAJO,
                            LocalDateTime.now(), null, false));
        }

        @Test
        @DisplayName("deberia lanzar NullPointerException cuando materiaId es nulo")
        void deberiaLanzarExcepcionCuandoMateriaIdEsNulo() {
            // Arrange & Act & Then
            assertThrows(NullPointerException.class,
                    () -> new SesionPomodoro(UUID.randomUUID(), TAREA_ID, null,
                            DURACION_DEFAULT, TipoSesion.TRABAJO,
                            LocalDateTime.now(), null, false));
        }

        @Test
        @DisplayName("deberia lanzar NullPointerException cuando duracion es nula")
        void deberiaLanzarExcepcionCuandoDuracionEsNula() {
            // Arrange & Act & Then
            assertThrows(NullPointerException.class,
                    () -> new SesionPomodoro(UUID.randomUUID(), TAREA_ID, MATERIA_ID,
                            null, TipoSesion.TRABAJO,
                            LocalDateTime.now(), null, false));
        }

        @Test
        @DisplayName("deberia lanzar NullPointerException cuando tipoSesion es nulo")
        void deberiaLanzarExcepcionCuandoTipoSesionEsNulo() {
            // Arrange & Act & Then
            assertThrows(NullPointerException.class,
                    () -> new SesionPomodoro(UUID.randomUUID(), TAREA_ID, MATERIA_ID,
                            DURACION_DEFAULT, null,
                            LocalDateTime.now(), null, false));
        }

        @Test
        @DisplayName("deberia lanzar NullPointerException cuando fechaInicio es nula")
        void deberiaLanzarExcepcionCuandoFechaInicioEsNula() {
            // Arrange & Act & Then
            assertThrows(NullPointerException.class,
                    () -> new SesionPomodoro(UUID.randomUUID(), TAREA_ID, MATERIA_ID,
                            DURACION_DEFAULT, TipoSesion.TRABAJO,
                            null, null, false));
        }
    }

    @Nested
    @DisplayName("Factory iniciar")
    class IniciarTests {

        @Test
        @DisplayName("deberia crear sesion en curso con factory method")
        void deberiaCrearSesionEnCurso() {
            // Arrange & Act
            SesionPomodoro sesion = SesionPomodoro.iniciar(
                    TAREA_ID, MATERIA_ID, DURACION_DEFAULT, TipoSesion.TRABAJO);

            // Assert
            assertNotNull(sesion);
            assertNotNull(sesion.getId());
            assertEquals(TAREA_ID, sesion.getTareaId());
            assertEquals(MATERIA_ID, sesion.getMateriaId());
            assertEquals(DURACION_DEFAULT, sesion.getDuracion());
            assertEquals(TipoSesion.TRABAJO, sesion.getTipoSesion());
            assertNotNull(sesion.getFechaInicio());
            assertNull(sesion.getFechaFin());
            assertFalse(sesion.isCompletada());
        }
    }

    @Nested
    @DisplayName("completar")
    class CompletarTests {

        @Test
        @DisplayName("deberia completar sesion y registrar evento")
        void deberiaCompletarSesion() {
            // Arrange
            SesionPomodoro sesion = SesionPomodoro.iniciar(
                    TAREA_ID, MATERIA_ID, DURACION_DEFAULT, TipoSesion.TRABAJO);

            // Act
            sesion.completar();

            // Assert
            assertTrue(sesion.isCompletada());
            assertNotNull(sesion.getFechaFin());
            assertFalse(sesion.getDomainEvents().isEmpty());

            Object evento = sesion.getDomainEvents().get(0);
            assertInstanceOf(PomodoroCompletadoEvent.class, evento);
            PomodoroCompletadoEvent event = (PomodoroCompletadoEvent) evento;
            assertEquals(sesion.getId(), event.sesionId());
            assertEquals(TAREA_ID, event.tareaId());
            assertEquals(MATERIA_ID, event.materiaId());
            assertEquals(TipoSesion.TRABAJO, event.tipoSesion());
        }

        @Test
        @DisplayName("deberia lanzar SesionPomodoroException cuando sesion ya esta completada")
        void deberiaLanzarExcepcionCuandoSesionYaEstaCompletada() {
            // Arrange
            SesionPomodoro sesion = SesionPomodoro.iniciar(
                    TAREA_ID, MATERIA_ID, DURACION_DEFAULT, TipoSesion.TRABAJO);
            sesion.completar();

            // Act & Then
            SesionPomodoroException ex = assertThrows(SesionPomodoroException.class,
                    sesion::completar);
            assertEquals("La sesión de Pomodoro ya está completada", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("calcularDuracionReal")
    class CalcularDuracionRealTests {

        @Test
        @DisplayName("deberia retornar duracion programada cuando no esta completada")
        void deberiaRetornarDuracionProgramadaCuandoNoEstaCompletada() {
            // Arrange
            SesionPomodoro sesion = SesionPomodoro.iniciar(
                    TAREA_ID, MATERIA_ID, DURACION_DEFAULT, TipoSesion.TRABAJO);

            // Act
            long duracion = sesion.calcularDuracionReal();

            // Assert
            assertEquals(25, duracion);
        }

        @Test
        @DisplayName("deberia retornar duracion real cuando esta completada")
        void deberiaRetornarDuracionRealCuandoEstaCompletada() {
            // Arrange
            LocalDateTime inicio = LocalDateTime.now().minusMinutes(30);
            LocalDateTime fin = LocalDateTime.now();
            SesionPomodoro sesion = new SesionPomodoro(
                    UUID.randomUUID(), TAREA_ID, MATERIA_ID,
                    DURACION_DEFAULT, TipoSesion.TRABAJO,
                    inicio, fin, true);

            // Act
            long duracion = sesion.calcularDuracionReal();

            // Assert
            assertTrue(duracion > 0);
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
            SesionPomodoro s1 = new SesionPomodoro(id, TAREA_ID, MATERIA_ID,
                    DURACION_DEFAULT, TipoSesion.TRABAJO,
                    LocalDateTime.now(), null, false);
            SesionPomodoro s2 = new SesionPomodoro(id, UUID.randomUUID(), UUID.randomUUID(),
                    DuracionMinutos.of(10), TipoSesion.DESCANSO,
                    LocalDateTime.now(), LocalDateTime.now(), true);

            // Act & Assert
            assertEquals(s1, s2);
            assertEquals(s1.hashCode(), s2.hashCode());
        }

        @Test
        @DisplayName("deberia ser diferente cuando distinto ID")
        void deberiaSerDiferenteCuandoDistintoId() {
            // Arrange
            SesionPomodoro s1 = SesionPomodoro.iniciar(
                    TAREA_ID, MATERIA_ID, DURACION_DEFAULT, TipoSesion.TRABAJO);
            SesionPomodoro s2 = SesionPomodoro.iniciar(
                    TAREA_ID, MATERIA_ID, DURACION_DEFAULT, TipoSesion.TRABAJO);

            // Act & Assert
            assertNotEquals(s1, s2);
        }
    }
}
