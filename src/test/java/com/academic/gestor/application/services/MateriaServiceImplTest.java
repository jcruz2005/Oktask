package com.academic.gestor.application.services;

import com.academic.gestor.domain.exceptions.MateriaNotFoundException;
import com.academic.gestor.domain.model.entities.Materia;
import com.academic.gestor.domain.model.enums.Prioridad;
import com.academic.gestor.domain.model.valueobjects.CodigoMateria;
import com.academic.gestor.domain.model.valueobjects.Color;
import com.academic.gestor.domain.ports.outbound.MateriaRepository;
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
@DisplayName("Tests para MateriaServiceImpl")
class MateriaServiceImplTest {

    @Mock
    private MateriaRepository materiaRepositoryMock;

    @InjectMocks
    private MateriaServiceImpl sut;

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
        @DisplayName("deberia crear materia cuando codigo no existe")
        void deberiaCrearMateriaCuandoCodigoNoExiste() {
            // Arrange
            when(materiaRepositoryMock.existsByCodigo(any(CodigoMateria.class))).thenReturn(false);
            when(materiaRepositoryMock.save(any(Materia.class))).thenReturn(materiaEjemplo);

            // Act
            Materia resultado = sut.crearMateria("Matematica", "MAT001", "#FF5733", Prioridad.ALTA);

            // Assert
            assertNotNull(resultado);
            assertEquals("Matematica", resultado.getNombre());
            verify(materiaRepositoryMock).existsByCodigo(any(CodigoMateria.class));
            verify(materiaRepositoryMock).save(any(Materia.class));
        }

        @Test
        @DisplayName("deberia lanzar IllegalArgumentException cuando codigo ya existe")
        void deberiaLanzarExcepcionCuandoCodigoYaExiste() {
            // Arrange
            when(materiaRepositoryMock.existsByCodigo(any(CodigoMateria.class))).thenReturn(true);

            // Act & Then
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> sut.crearMateria("Matematica", "MAT001", "#FF5733", Prioridad.ALTA));
            assertTrue(ex.getMessage().contains("MAT001"));
            verify(materiaRepositoryMock).existsByCodigo(any(CodigoMateria.class));
            verify(materiaRepositoryMock, never()).save(any());
        }

        @Test
        @DisplayName("deberia lanzar excepcion cuando color es invalido")
        void deberiaLanzarExcepcionCuandoColorEsInvalido() {
            // Arrange & Act & Then
            assertThrows(IllegalArgumentException.class,
                    () -> sut.crearMateria("Matematica", "MAT001", "invalido", Prioridad.ALTA));
        }

        @Test
        @DisplayName("deberia lanzar excepcion cuando codigo es invalido")
        void deberiaLanzarExcepcionCuandoCodigoEsInvalido() {
            // Arrange & Act & Then
            assertThrows(IllegalArgumentException.class,
                    () -> sut.crearMateria("Matematica", "AB", "#FF5733", Prioridad.ALTA));
        }
    }

    @Nested
    @DisplayName("editarMateria")
    class EditarMateriaTests {

        @Test
        @DisplayName("deberia editar materia existente")
        void deberiaEditarMateriaExistente() {
            // Arrange
            when(materiaRepositoryMock.findById(materiaId)).thenReturn(Optional.of(materiaEjemplo));
            when(materiaRepositoryMock.save(any(Materia.class))).thenReturn(materiaEjemplo);

            // Act
            Materia resultado = sut.editarMateria(materiaId, "Fisica", "#00FF00", Prioridad.BAJA);

            // Assert
            assertNotNull(resultado);
            assertEquals("Fisica", resultado.getNombre());
            verify(materiaRepositoryMock).findById(materiaId);
            verify(materiaRepositoryMock).save(any(Materia.class));
        }

        @Test
        @DisplayName("deberia lanzar MateriaNotFoundException cuando materia no existe")
        void deberiaLanzarExcepcionCuandoMateriaNoExiste() {
            // Arrange
            when(materiaRepositoryMock.findById(materiaId)).thenReturn(Optional.empty());

            // Act & Then
            assertThrows(MateriaNotFoundException.class,
                    () -> sut.editarMateria(materiaId, "Fisica", "#00FF00", Prioridad.BAJA));
        }

        @Test
        @DisplayName("deberia no cambiar campos cuando son null")
        void deberiaNoCambiarCamposCuandoSonNull() {
            // Arrange
            when(materiaRepositoryMock.findById(materiaId)).thenReturn(Optional.of(materiaEjemplo));
            when(materiaRepositoryMock.save(any(Materia.class))).thenReturn(materiaEjemplo);

            // Act
            sut.editarMateria(materiaId, null, null, null);

            // Assert
            verify(materiaRepositoryMock).save(any(Materia.class));
        }

        @Test
        @DisplayName("deberia no cambiar campos cuando estan en blanco")
        void deberiaNoCambiarCamposCuandoEstanEnBlanco() {
            // Arrange
            when(materiaRepositoryMock.findById(materiaId)).thenReturn(Optional.of(materiaEjemplo));
            when(materiaRepositoryMock.save(any(Materia.class))).thenReturn(materiaEjemplo);

            // Act
            sut.editarMateria(materiaId, "   ", "  ", null);

            // Assert
            verify(materiaRepositoryMock).save(any(Materia.class));
        }
    }

    @Nested
    @DisplayName("eliminarMateria")
    class EliminarMateriaTests {

        @Test
        @DisplayName("deberia desactivar materia existente")
        void deberiaDesactivarMateriaExistente() {
            // Arrange
            when(materiaRepositoryMock.findById(materiaId)).thenReturn(Optional.of(materiaEjemplo));
            when(materiaRepositoryMock.save(any(Materia.class))).thenReturn(materiaEjemplo);

            // Act
            sut.eliminarMateria(materiaId);

            // Assert
            verify(materiaRepositoryMock).findById(materiaId);
            verify(materiaRepositoryMock).save(argThat(m -> !m.isActiva()));
        }

        @Test
        @DisplayName("deberia lanzar MateriaNotFoundException cuando materia no existe")
        void deberiaLanzarExcepcionCuandoMateriaNoExiste() {
            // Arrange
            when(materiaRepositoryMock.findById(materiaId)).thenReturn(Optional.empty());

            // Act & Then
            assertThrows(MateriaNotFoundException.class,
                    () -> sut.eliminarMateria(materiaId));
        }
    }

    @Nested
    @DisplayName("listarMaterias")
    class ListarMateriasTests {

        @Test
        @DisplayName("deberia retornar lista de materias activas")
        void deberiaRetornarListaDeMateriasActivas() {
            // Arrange
            when(materiaRepositoryMock.findAll()).thenReturn(List.of(materiaEjemplo));

            // Act
            List<Materia> resultado = sut.listarMaterias();

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(materiaRepositoryMock).findAll();
        }

        @Test
        @DisplayName("deberia retornar lista vacia cuando no hay materias")
        void deberiaRetornarListaVaciaCuandoNoHayMaterias() {
            // Arrange
            when(materiaRepositoryMock.findAll()).thenReturn(List.of());

            // Act
            List<Materia> resultado = sut.listarMaterias();

            // Assert
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("listarTodasLasMaterias")
    class ListarTodasLasMateriasTests {

        @Test
        @DisplayName("deberia retornar todas las materias incluyendo inactivas")
        void deberiaRetornarTodasLasMaterias() {
            // Arrange
            when(materiaRepositoryMock.findAllIncludingInactive()).thenReturn(List.of(materiaEjemplo));

            // Act
            List<Materia> resultado = sut.listarTodasLasMaterias();

            // Assert
            assertEquals(1, resultado.size());
            verify(materiaRepositoryMock).findAllIncludingInactive();
        }
    }

    @Nested
    @DisplayName("obtenerMateria")
    class ObtenerMateriaTests {

        @Test
        @DisplayName("deberia retornar materia cuando existe")
        void deberiaRetornarMateriaCuandoExiste() {
            // Arrange
            when(materiaRepositoryMock.findById(materiaId)).thenReturn(Optional.of(materiaEjemplo));

            // Act
            Optional<Materia> resultado = sut.obtenerMateria(materiaId);

            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(materiaId, resultado.get().getId());
        }

        @Test
        @DisplayName("deberia retornar Optional vacio cuando no existe")
        void deberiaRetornarOptionalVacioCuandoNoExiste() {
            // Arrange
            when(materiaRepositoryMock.findById(materiaId)).thenReturn(Optional.empty());

            // Act
            Optional<Materia> resultado = sut.obtenerMateria(materiaId);

            // Assert
            assertFalse(resultado.isPresent());
        }
    }

    @Nested
    @DisplayName("obtenerMateriaPorCodigo")
    class ObtenerMateriaPorCodigoTests {

        @Test
        @DisplayName("deberia retornar materia cuando existe por codigo")
        void deberiaRetornarMateriaCuandoExistePorCodigo() {
            // Arrange
            when(materiaRepositoryMock.findByCodigo(any(CodigoMateria.class)))
                    .thenReturn(Optional.of(materiaEjemplo));

            // Act
            Optional<Materia> resultado = sut.obtenerMateriaPorCodigo("MAT001");

            // Assert
            assertTrue(resultado.isPresent());
            assertEquals("MAT001", resultado.get().getCodigo().value());
        }

        @Test
        @DisplayName("deberia retornar Optional vacio cuando codigo no existe")
        void deberiaRetornarOptionalVacioCuandoCodigoNoExiste() {
            // Arrange
            when(materiaRepositoryMock.findByCodigo(any(CodigoMateria.class)))
                    .thenReturn(Optional.empty());

            // Act
            Optional<Materia> resultado = sut.obtenerMateriaPorCodigo("NOEX");

            // Assert
            assertFalse(resultado.isPresent());
        }
    }
}
