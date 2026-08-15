package com.academic.gestor.infrastructure.web.controllers;

import com.academic.gestor.domain.model.entities.Tarea;
import com.academic.gestor.domain.model.enums.EstadoTarea;
import com.academic.gestor.domain.model.enums.Prioridad;
import com.academic.gestor.domain.ports.inbound.TareaService;
import com.academic.gestor.infrastructure.web.dto.request.CrearTareaRequest;
import com.academic.gestor.infrastructure.web.dto.request.EditarTareaRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para TareaController")
class TareaControllerTest {

    @Mock
    private TareaService tareaServiceMock;

    @InjectMocks
    private TareaController sut;

    private Tarea tareaEjemplo;
    private UUID tareaId;
    private UUID materiaId;

    @BeforeEach
    void setUp() {
        tareaId = UUID.randomUUID();
        materiaId = UUID.randomUUID();
        tareaEjemplo = new Tarea(
                tareaId,
                "Entrega TP1",
                "Resolver ejercicios",
                materiaId,
                LocalDate.now().plusDays(7),
                Prioridad.ALTA,
                EstadoTarea.PENDIENTE,
                LocalDateTime.now(),
                null,
                0
        );
    }

    @Nested
    @DisplayName("crearTarea")
    class CrearTareaTests {

        @Test
        @DisplayName("deberia crear tarea y retornar 201")
        void deberiaCrearTareaYRetornar201() {
            // Arrange
            CrearTareaRequest request = new CrearTareaRequest(
                    "Entrega TP1", "Resolver ejercicios", materiaId,
                    LocalDate.now().plusDays(7), Prioridad.ALTA);
            when(tareaServiceMock.crearTarea(anyString(), anyString(), any(UUID.class),
                    any(LocalDate.class), any(Prioridad.class)))
                    .thenReturn(tareaEjemplo);

            // Act
            ResponseEntity<?> response = sut.crearTarea(request);

            // Assert
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            verify(tareaServiceMock).crearTarea(anyString(), anyString(), any(UUID.class),
                    any(LocalDate.class), any(Prioridad.class));
        }
    }

    @Nested
    @DisplayName("listarTareas")
    class ListarTareasTests {

        @Test
        @DisplayName("deberia listar tareas y retornar 200")
        void deberiaListarTareasYRetornar200() {
            // Arrange
            when(tareaServiceMock.listarTareas()).thenReturn(List.of(tareaEjemplo));

            // Act
            ResponseEntity<?> response = sut.listarTareas();

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }

        @Test
        @DisplayName("deberia retornar lista vacia cuando no hay tareas")
        void deberiaRetornarListaVacia() {
            // Arrange
            when(tareaServiceMock.listarTareas()).thenReturn(List.of());

            // Act
            ResponseEntity<?> response = sut.listarTareas();

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("listarTareasPorMateria")
    class ListarTareasPorMateriaTests {

        @Test
        @DisplayName("deberia listar tareas por materia y retornar 200")
        void deberiaListarTareasPorMateriaYRetornar200() {
            // Arrange
            when(tareaServiceMock.listarTareasPorMateria(materiaId)).thenReturn(List.of(tareaEjemplo));

            // Act
            ResponseEntity<?> response = sut.listarTareasPorMateria(materiaId);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(tareaServiceMock).listarTareasPorMateria(materiaId);
        }
    }

    @Nested
    @DisplayName("listarTareasPorEstado")
    class ListarTareasPorEstadoTests {

        @Test
        @DisplayName("deberia listar tareas por estado y retornar 200")
        void deberiaListarTareasPorEstadoYRetornar200() {
            // Arrange
            when(tareaServiceMock.listarTareasPorEstado(EstadoTarea.PENDIENTE))
                    .thenReturn(List.of(tareaEjemplo));

            // Act
            ResponseEntity<?> response = sut.listarTareasPorEstado(EstadoTarea.PENDIENTE);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(tareaServiceMock).listarTareasPorEstado(EstadoTarea.PENDIENTE);
        }
    }

    @Nested
    @DisplayName("obtenerTarea")
    class ObtenerTareaTests {

        @Test
        @DisplayName("deberia retornar tarea cuando existe")
        void deberiaRetornarTareaCuandoExiste() {
            // Arrange
            when(tareaServiceMock.obtenerTarea(tareaId)).thenReturn(Optional.of(tareaEjemplo));

            // Act
            ResponseEntity<?> response = sut.obtenerTarea(tareaId);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }

        @Test
        @DisplayName("deberia retornar 404 cuando tarea no existe")
        void deberiaRetornar404CuandoTareaNoExiste() {
            // Arrange
            when(tareaServiceMock.obtenerTarea(tareaId)).thenReturn(Optional.empty());

            // Act
            ResponseEntity<?> response = sut.obtenerTarea(tareaId);

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("editarTarea")
    class EditarTareaTests {

        @Test
        @DisplayName("deberia editar tarea sin cambio de estado")
        void deberiaEditarTareaSinCambioDeEstado() {
            // Arrange
            EditarTareaRequest request = new EditarTareaRequest(
                    "Nuevo titulo", "Nueva desc", LocalDate.now().plusDays(14),
                    Prioridad.BAJA, null);
            when(tareaServiceMock.editarTarea(any(UUID.class), anyString(), anyString(),
                    any(LocalDate.class), any(Prioridad.class)))
                    .thenReturn(tareaEjemplo);

            // Act
            ResponseEntity<?> response = sut.editarTarea(tareaId, request);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }

        @Test
        @DisplayName("deberia editar tarea con cambio de estado")
        void deberiaEditarTareaConCambioDeEstado() {
            // Arrange
            EditarTareaRequest request = new EditarTareaRequest(
                    "Nuevo titulo", "Nueva desc", LocalDate.now().plusDays(14),
                    Prioridad.BAJA, EstadoTarea.EN_PROGRESO);
            when(tareaServiceMock.editarTarea(any(UUID.class), anyString(), anyString(),
                    any(LocalDate.class), any(Prioridad.class)))
                    .thenReturn(tareaEjemplo);
            when(tareaServiceMock.cambiarEstado(any(UUID.class), any(EstadoTarea.class)))
                    .thenReturn(tareaEjemplo);

            // Act
            ResponseEntity<?> response = sut.editarTarea(tareaId, request);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            verify(tareaServiceMock).cambiarEstado(tareaId, EstadoTarea.EN_PROGRESO);
        }
    }

    @Nested
    @DisplayName("eliminarTarea")
    class EliminarTareaTests {

        @Test
        @DisplayName("deberia eliminar tarea y retornar 204")
        void deberiaEliminarTareaYRetornar204() {
            // Arrange
            doNothing().when(tareaServiceMock).eliminarTarea(tareaId);

            // Act
            ResponseEntity<Void> response = sut.eliminarTarea(tareaId);

            // Assert
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertNull(response.getBody());
            verify(tareaServiceMock).eliminarTarea(tareaId);
        }
    }
}
