package com.academic.gestor.infrastructure.web.controllers;

import com.academic.gestor.domain.model.entities.Materia;
import com.academic.gestor.domain.model.enums.Prioridad;
import com.academic.gestor.domain.model.valueobjects.CodigoMateria;
import com.academic.gestor.domain.model.valueobjects.Color;
import com.academic.gestor.domain.ports.inbound.MateriaService;
import com.academic.gestor.infrastructure.web.dto.request.CrearMateriaRequest;
import com.academic.gestor.infrastructure.web.dto.request.EditarMateriaRequest;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para MateriaController")
class MateriaControllerTest {

    @Mock
    private MateriaService materiaServiceMock;

    @InjectMocks
    private MateriaController sut;

    private Materia materiaEjemplo;
    private UUID materiaId;

    @BeforeEach
    void setUp() {
        materiaId = UUID.randomUUID();
        materiaEjemplo = new Materia(
                materiaId,
                "Matematica",
                CodigoMateria.of("MAT001"),
                Color.of("#FF5733"),
                Prioridad.ALTA,
                LocalDateTime.now(),
                true
        );
    }

    @Nested
    @DisplayName("crearMateria")
    class CrearMateriaTests {

        @Test
        @DisplayName("deberia crear materia y retornar 201")
        void deberiaCrearMateriaYRetornar201() {
            // Arrange
            CrearMateriaRequest request = new CrearMateriaRequest(
                    "Matematica", "MAT001", "#FF5733", Prioridad.ALTA);
            when(materiaServiceMock.crearMateria(anyString(), anyString(), anyString(), any(Prioridad.class)))
                    .thenReturn(materiaEjemplo);

            // Act
            ResponseEntity<?> response = sut.crearMateria(request);

            // Assert
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertNotNull(response.getBody());
            verify(materiaServiceMock).crearMateria(anyString(), anyString(), anyString(), any(Prioridad.class));
        }
    }

    @Nested
    @DisplayName("listarMaterias")
    class ListarMateriasTests {

        @Test
        @DisplayName("deberia listar materias y retornar 200")
        void deberiaListarMateriasYRetornar200() {
            // Arrange
            when(materiaServiceMock.listarMaterias()).thenReturn(List.of(materiaEjemplo));

            // Act
            ResponseEntity<?> response = sut.listarMaterias();

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }

        @Test
        @DisplayName("deberia retornar lista vacia cuando no hay materias")
        void deberiaRetornarListaVacia() {
            // Arrange
            when(materiaServiceMock.listarMaterias()).thenReturn(List.of());

            // Act
            ResponseEntity<?> response = sut.listarMaterias();

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("obtenerMateria")
    class ObtenerMateriaTests {

        @Test
        @DisplayName("deberia retornar materia cuando existe")
        void deberiaRetornarMateriaCuandoExiste() {
            // Arrange
            when(materiaServiceMock.obtenerMateria(materiaId)).thenReturn(Optional.of(materiaEjemplo));

            // Act
            ResponseEntity<?> response = sut.obtenerMateria(materiaId);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }

        @Test
        @DisplayName("deberia retornar 404 cuando materia no existe")
        void deberiaRetornar404CuandoMateriaNoExiste() {
            // Arrange
            when(materiaServiceMock.obtenerMateria(materiaId)).thenReturn(Optional.empty());

            // Act
            ResponseEntity<?> response = sut.obtenerMateria(materiaId);

            // Assert
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Nested
    @DisplayName("editarMateria")
    class EditarMateriaTests {

        @Test
        @DisplayName("deberia editar materia y retornar 200")
        void deberiaEditarMateriaYRetornar200() {
            // Arrange
            EditarMateriaRequest request = new EditarMateriaRequest("Fisica", "#00FF00", Prioridad.BAJA);
            when(materiaServiceMock.editarMateria(any(UUID.class), anyString(), anyString(), any(Prioridad.class)))
                    .thenReturn(materiaEjemplo);

            // Act
            ResponseEntity<?> response = sut.editarMateria(materiaId, request);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }
    }

    @Nested
    @DisplayName("eliminarMateria")
    class EliminarMateriaTests {

        @Test
        @DisplayName("deberia eliminar materia y retornar 204")
        void deberiaEliminarMateriaYRetornar204() {
            // Arrange
            doNothing().when(materiaServiceMock).eliminarMateria(materiaId);

            // Act
            ResponseEntity<Void> response = sut.eliminarMateria(materiaId);

            // Assert
            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
            assertNull(response.getBody());
            verify(materiaServiceMock).eliminarMateria(materiaId);
        }
    }
}
