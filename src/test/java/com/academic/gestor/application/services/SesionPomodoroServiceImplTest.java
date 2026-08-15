package com.academic.gestor.application.services;

import com.academic.gestor.domain.exceptions.ConfiguracionInvalidaException;
import com.academic.gestor.domain.exceptions.SesionPomodoroException;
import com.academic.gestor.domain.model.entities.ConfiguracionPomodoro;
import com.academic.gestor.domain.model.entities.SesionPomodoro;
import com.academic.gestor.domain.model.enums.TipoSesion;
import com.academic.gestor.domain.model.valueobjects.DuracionMinutos;
import com.academic.gestor.domain.ports.outbound.ConfiguracionPomodoroRepository;
import com.academic.gestor.domain.ports.outbound.SesionPomodoroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para SesionPomodoroServiceImpl")
class SesionPomodoroServiceImplTest {

    @Mock
    private SesionPomodoroRepository sesionRepositoryMock;

    @Mock
    private ConfiguracionPomodoroRepository configuracionRepositoryMock;

    @InjectMocks
    private SesionPomodoroServiceImpl sut;

    private UUID tareaId;
    private UUID materiaId;
    private UUID sesionId;
    private SesionPomodoro sesionEjemplo;

    @BeforeEach
    void setUp() {
        tareaId = UUID.randomUUID();
        materiaId = UUID.randomUUID();
        sesionId = UUID.randomUUID();
        sesionEjemplo = SesionPomodoro.iniciar(
                tareaId, materiaId, DuracionMinutos.of(25), TipoSesion.TRABAJO);
    }

    @Nested
    @DisplayName("iniciarSesion")
    class IniciarSesionTests {

        @Test
        @DisplayName("deberia iniciar sesion con duracion valida")
        void deberiaIniciarSesionConDuracionValida() {
            // Arrange
            when(sesionRepositoryMock.save(any(SesionPomodoro.class))).thenReturn(sesionEjemplo);

            // Act
            SesionPomodoro resultado = sut.iniciarSesion(tareaId, materiaId, 25);

            // Assert
            assertNotNull(resultado);
            assertEquals(tareaId, resultado.getTareaId());
            assertEquals(materiaId, resultado.getMateriaId());
            assertFalse(resultado.isCompletada());
            verify(sesionRepositoryMock).save(any(SesionPomodoro.class));
        }

        @Test
        @DisplayName("deberia lanzar ConfiguracionInvalidaException cuando duracion es 0")
        void deberiaLanzarExcepcionCuandoDuracionEsCero() {
            // Arrange & Act & Then
            assertThrows(ConfiguracionInvalidaException.class,
                    () -> sut.iniciarSesion(tareaId, materiaId, 0));
        }

        @Test
        @DisplayName("deberia lanzar ConfiguracionInvalidaException cuando duracion es negativa")
        void deberiaLanzarExcepcionCuandoDuracionEsNegativa() {
            // Arrange & Act & Then
            assertThrows(ConfiguracionInvalidaException.class,
                    () -> sut.iniciarSesion(tareaId, materiaId, -5));
        }
    }

    @Nested
    @DisplayName("finalizarSesion")
    class FinalizarSesionTests {

        @Test
        @DisplayName("deberia finalizar sesion existente en curso")
        void deberiaFinalizarSesionExistente() {
            // Arrange
            when(sesionRepositoryMock.findById(sesionId)).thenReturn(Optional.of(sesionEjemplo));
            when(sesionRepositoryMock.save(any(SesionPomodoro.class))).thenReturn(sesionEjemplo);

            // Act
            SesionPomodoro resultado = sut.finalizarSesion(sesionId);

            // Assert
            assertNotNull(resultado);
            verify(sesionRepositoryMock).findById(sesionId);
            verify(sesionRepositoryMock).save(any(SesionPomodoro.class));
        }

        @Test
        @DisplayName("deberia lanzar SesionPomodoroException cuando sesion no existe")
        void deberiaLanzarExcepcionCuandoSesionNoExiste() {
            // Arrange
            when(sesionRepositoryMock.findById(sesionId)).thenReturn(Optional.empty());

            // Act & Then
            assertThrows(SesionPomodoroException.class,
                    () -> sut.finalizarSesion(sesionId));
        }

        @Test
        @DisplayName("deberia lanzar SesionPomodoroException cuando sesion ya esta completada")
        void deberiaLanzarExcepcionCuandoSesionYaCompletada() {
            // Arrange
            sesionEjemplo.completar();
            when(sesionRepositoryMock.findById(sesionId)).thenReturn(Optional.of(sesionEjemplo));

            // Act & Then
            assertThrows(SesionPomodoroException.class,
                    () -> sut.finalizarSesion(sesionId));
        }
    }

    @Nested
    @DisplayName("obtenerSesionesTarea")
    class ObtenerSesionesTareaTests {

        @Test
        @DisplayName("deberia retornar sesiones de una tarea")
        void deberiaRetornarSesionesDeUnaTarea() {
            // Arrange
            when(sesionRepositoryMock.findByTareaId(tareaId)).thenReturn(List.of(sesionEjemplo));

            // Act
            List<SesionPomodoro> resultado = sut.obtenerSesionesTarea(tareaId);

            // Assert
            assertEquals(1, resultado.size());
            verify(sesionRepositoryMock).findByTareaId(tareaId);
        }

        @Test
        @DisplayName("deberia retornar lista vacia cuando no hay sesiones")
        void deberiaRetornarListaVacia() {
            // Arrange
            when(sesionRepositoryMock.findByTareaId(tareaId)).thenReturn(List.of());

            // Act
            List<SesionPomodoro> resultado = sut.obtenerSesionesTarea(tareaId);

            // Assert
            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("obtenerSesionesMateria")
    class ObtenerSesionesMateriaTests {

        @Test
        @DisplayName("deberia retornar sesiones de una materia")
        void deberiaRetornarSesionesDeUnaMateria() {
            // Arrange
            when(sesionRepositoryMock.findByMateriaId(materiaId)).thenReturn(List.of(sesionEjemplo));

            // Act
            List<SesionPomodoro> resultado = sut.obtenerSesionesMateria(materiaId);

            // Assert
            assertEquals(1, resultado.size());
            verify(sesionRepositoryMock).findByMateriaId(materiaId);
        }
    }

    @Nested
    @DisplayName("obtenerSesion")
    class ObtenerSesionTests {

        @Test
        @DisplayName("deberia retornar sesion cuando existe")
        void deberiaRetornarSesionCuandoExiste() {
            // Arrange
            when(sesionRepositoryMock.findById(sesionId)).thenReturn(Optional.of(sesionEjemplo));

            // Act
            Optional<SesionPomodoro> resultado = sut.obtenerSesion(sesionId);

            // Assert
            assertTrue(resultado.isPresent());
        }

        @Test
        @DisplayName("deberia retornar Optional vacio cuando no existe")
        void deberiaRetornarOptionalVacioCuandoNoExiste() {
            // Arrange
            when(sesionRepositoryMock.findById(sesionId)).thenReturn(Optional.empty());

            // Act
            Optional<SesionPomodoro> resultado = sut.obtenerSesion(sesionId);

            // Assert
            assertFalse(resultado.isPresent());
        }
    }

    @Nested
    @DisplayName("obtenerConfiguracion")
    class ObtenerConfiguracionTests {

        @Test
        @DisplayName("deberia retornar configuracion activa cuando existe")
        void deberiaRetornarConfiguracionActivaCuandoExiste() {
            // Arrange
            ConfiguracionPomodoro config = ConfiguracionPomodoro.createDefault();
            when(configuracionRepositoryMock.findConfiguracionActiva()).thenReturn(Optional.of(config));

            // Act
            ConfiguracionPomodoro resultado = sut.obtenerConfiguracion();

            // Assert
            assertNotNull(resultado);
            assertEquals(25, resultado.getDuracionTrabajo().minutos());
            verify(configuracionRepositoryMock, never()).save(any());
        }

        @Test
        @DisplayName("deberia crear configuracion por defecto cuando no hay activa")
        void deberiaCrearConfiguracionPorDefectoCuandoNoHayActiva() {
            // Arrange
            when(configuracionRepositoryMock.findConfiguracionActiva()).thenReturn(Optional.empty());
            when(configuracionRepositoryMock.save(any(ConfiguracionPomodoro.class)))
                    .thenReturn(ConfiguracionPomodoro.createDefault());

            // Act
            ConfiguracionPomodoro resultado = sut.obtenerConfiguracion();

            // Assert
            assertNotNull(resultado);
            verify(configuracionRepositoryMock).save(any(ConfiguracionPomodoro.class));
        }
    }
}
