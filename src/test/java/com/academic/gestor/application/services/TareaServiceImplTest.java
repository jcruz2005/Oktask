package com.academic.gestor.application.services;

import com.academic.gestor.domain.exceptions.MateriaNotFoundException;
import com.academic.gestor.domain.exceptions.TareaNotFoundException;
import com.academic.gestor.domain.model.entities.Materia;
import com.academic.gestor.domain.model.entities.Tarea;
import com.academic.gestor.domain.model.enums.EstadoTarea;
import com.academic.gestor.domain.model.enums.Prioridad;
import com.academic.gestor.domain.model.valueobjects.CodigoMateria;
import com.academic.gestor.domain.model.valueobjects.Color;
import com.academic.gestor.domain.ports.outbound.MateriaRepository;
import com.academic.gestor.domain.ports.outbound.TareaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para TareaServiceImpl")
class TareaServiceImplTest {

    @Mock
    private TareaRepository tareaRepositoryMock;

    @Mock
    private MateriaRepository materiaRepositoryMock;

    @InjectMocks
    private TareaServiceImpl sut;

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
        @DisplayName("deberia crear tarea cuando materia existe")
        void deberiaCrearTareaCuandoMateriaExiste() {
            // Arrange
            when(materiaRepositoryMock.existsById(materiaId)).thenReturn(true);
            when(tareaRepositoryMock.save(any(Tarea.class))).thenReturn(tareaEjemplo);

            // Act
            Tarea resultado = sut.crearTarea("Entrega TP1", "Resolver ejercicios",
                    materiaId, LocalDate.now().plusDays(7), Prioridad.ALTA);

            // Assert
            assertNotNull(resultado);
            assertEquals("Entrega TP1", resultado.getTitulo());
            verify(materiaRepositoryMock).existsById(materiaId);
            verify(tareaRepositoryMock).save(any(Tarea.class));
        }

        @Test
        @DisplayName("deberia lanzar MateriaNotFoundException cuando materia no existe")
        void deberiaLanzarExcepcionCuandoMateriaNoExiste() {
            // Arrange
            when(materiaRepositoryMock.existsById(materiaId)).thenReturn(false);

            // Act & Then
            assertThrows(MateriaNotFoundException.class,
                    () -> sut.crearTarea("Entrega TP1", "Resolver ejercicios",
                            materiaId, LocalDate.now().plusDays(7), Prioridad.ALTA));
            verify(tareaRepositoryMock, never()).save(any());
        }
    }

    @Nested
    @DisplayName("editarTarea")
    class EditarTareaTests {

        @Test
        @DisplayName("deberia editar tarea existente")
        void deberiaEditarTareaExistente() {
            // Arrange
            when(tareaRepositoryMock.findById(tareaId)).thenReturn(Optional.of(tareaEjemplo));
            when(tareaRepositoryMock.save(any(Tarea.class))).thenReturn(tareaEjemplo);

            // Act
            Tarea resultado = sut.editarTarea(tareaId, "Nuevo titulo", "Nueva desc",
                    LocalDate.now().plusDays(14), Prioridad.BAJA);

            // Assert
            assertNotNull(resultado);
            verify(tareaRepositoryMock).findById(tareaId);
            verify(tareaRepositoryMock).save(any(Tarea.class));
        }

        @Test
        @DisplayName("deberia lanzar TareaNotFoundException cuando tarea no existe")
        void deberiaLanzarExcepcionCuandoTareaNoExiste() {
            // Arrange
            when(tareaRepositoryMock.findById(tareaId)).thenReturn(Optional.empty());

            // Act & Then
            assertThrows(TareaNotFoundException.class,
                    () -> sut.editarTarea(tareaId, "Nuevo titulo", "Nueva desc",
                            LocalDate.now().plusDays(14), Prioridad.BAJA));
        }

        @Test
        @DisplayName("deberia no cambiar campos cuando son null")
        void deberiaNoCambiarCamposCuandoSonNull() {
            // Arrange
            when(tareaRepositoryMock.findById(tareaId)).thenReturn(Optional.of(tareaEjemplo));
            when(tareaRepositoryMock.save(any(Tarea.class))).thenReturn(tareaEjemplo);

            // Act
            sut.editarTarea(tareaId, null, null, null, null);

            // Assert
            verify(tareaRepositoryMock).save(argThat(t -> t.getTitulo().equals("Entrega TP1")));
        }

        @Test
        @DisplayName("deberia no cambiar titulo cuando esta en blanco")
        void deberiaNoCambiarTituloCuandoEstaEnBlanco() {
            // Arrange
            when(tareaRepositoryMock.findById(tareaId)).thenReturn(Optional.of(tareaEjemplo));
            when(tareaRepositoryMock.save(any(Tarea.class))).thenReturn(tareaEjemplo);

            // Act
            sut.editarTarea(tareaId, "   ", null, null, null);

            // Assert
            verify(tareaRepositoryMock).save(argThat(t -> t.getTitulo().equals("Entrega TP1")));
        }
    }

    @Nested
    @DisplayName("cambiarEstado")
    class CambiarEstadoTests {

        @Test
        @DisplayName("deberia cambiar estado de tarea")
        void deberiaCambiarEstadoDeTarea() {
            // Arrange
            when(tareaRepositoryMock.findById(tareaId)).thenReturn(Optional.of(tareaEjemplo));
            when(tareaRepositoryMock.save(any(Tarea.class))).thenReturn(tareaEjemplo);

            // Act
            Tarea resultado = sut.cambiarEstado(tareaId, EstadoTarea.EN_PROGRESO);

            // Assert
            assertNotNull(resultado);
            verify(tareaRepositoryMock).save(any(Tarea.class));
        }

        @Test
        @DisplayName("deberia lanzar TareaNotFoundException cuando tarea no existe")
        void deberiaLanzarExcepcionCuandoTareaNoExiste() {
            // Arrange
            when(tareaRepositoryMock.findById(tareaId)).thenReturn(Optional.empty());

            // Act & Then
            assertThrows(TareaNotFoundException.class,
                    () -> sut.cambiarEstado(tareaId, EstadoTarea.COMPLETADA));
        }
    }

    @Nested
    @DisplayName("eliminarTarea")
    class EliminarTareaTests {

        @Test
        @DisplayName("deberia eliminar tarea existente")
        void deberiaEliminarTareaExistente() {
            // Arrange
            when(tareaRepositoryMock.existsById(tareaId)).thenReturn(true);

            // Act
            sut.eliminarTarea(tareaId);

            // Assert
            verify(tareaRepositoryMock).deleteById(tareaId);
        }

        @Test
        @DisplayName("deberia lanzar TareaNotFoundException cuando tarea no existe")
        void deberiaLanzarExcepcionCuandoTareaNoExiste() {
            // Arrange
            when(tareaRepositoryMock.existsById(tareaId)).thenReturn(false);

            // Act & Then
            assertThrows(TareaNotFoundException.class,
                    () -> sut.eliminarTarea(tareaId));
            verify(tareaRepositoryMock, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("listarTareas")
    class ListarTareasTests {

        @Test
        @DisplayName("deberia retornar todas las tareas")
        void deberiaRetornarTodasLasTareas() {
            // Arrange
            when(tareaRepositoryMock.findAll()).thenReturn(List.of(tareaEjemplo));

            // Act
            List<Tarea> resultado = sut.listarTareas();

            // Assert
            assertEquals(1, resultado.size());
        }

        @Test
        @DisplayName("deberia retornar lista vacia cuando no hay tareas")
        void deberiaRetornarListaVacia() {
            // Arrange
            when(tareaRepositoryMock.findAll()).thenReturn(List.of());

            // Act
            List<Tarea> resultado = sut.listarTareas();

            // Assert
            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("listarTareasPorMateria")
    class ListarTareasPorMateriaTests {

        @Test
        @DisplayName("deberia retornar tareas de una materia")
        void deberiaRetornarTareasDeUnaMateria() {
            // Arrange
            when(tareaRepositoryMock.findByMateriaId(materiaId)).thenReturn(List.of(tareaEjemplo));

            // Act
            List<Tarea> resultado = sut.listarTareasPorMateria(materiaId);

            // Assert
            assertEquals(1, resultado.size());
            verify(tareaRepositoryMock).findByMateriaId(materiaId);
        }
    }

    @Nested
    @DisplayName("listarTareasPorEstado")
    class ListarTareasPorEstadoTests {

        @Test
        @DisplayName("deberia retornar tareas por estado")
        void deberiaRetornarTareasPorEstado() {
            // Arrange
            when(tareaRepositoryMock.findByEstado(EstadoTarea.PENDIENTE)).thenReturn(List.of(tareaEjemplo));

            // Act
            List<Tarea> resultado = sut.listarTareasPorEstado(EstadoTarea.PENDIENTE);

            // Assert
            assertEquals(1, resultado.size());
            verify(tareaRepositoryMock).findByEstado(EstadoTarea.PENDIENTE);
        }
    }

    @Nested
    @DisplayName("obtenerTarea")
    class ObtenerTareaTests {

        @Test
        @DisplayName("deberia retornar tarea cuando existe")
        void deberiaRetornarTareaCuandoExiste() {
            // Arrange
            when(tareaRepositoryMock.findById(tareaId)).thenReturn(Optional.of(tareaEjemplo));

            // Act
            Optional<Tarea> resultado = sut.obtenerTarea(tareaId);

            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(tareaId, resultado.get().getId());
        }

        @Test
        @DisplayName("deberia retornar Optional vacio cuando no existe")
        void deberiaRetornarOptionalVacioCuandoNoExiste() {
            // Arrange
            when(tareaRepositoryMock.findById(tareaId)).thenReturn(Optional.empty());

            // Act
            Optional<Tarea> resultado = sut.obtenerTarea(tareaId);

            // Assert
            assertFalse(resultado.isPresent());
        }
    }
}
