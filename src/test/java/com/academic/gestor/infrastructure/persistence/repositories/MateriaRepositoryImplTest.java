package com.academic.gestor.infrastructure.persistence.repositories;

import com.academic.gestor.domain.model.entities.Materia;
import com.academic.gestor.domain.model.enums.Prioridad;
import com.academic.gestor.domain.model.valueobjects.CodigoMateria;
import com.academic.gestor.domain.model.valueobjects.Color;
import com.academic.gestor.infrastructure.persistence.entities.MateriaEntity;
import com.academic.gestor.infrastructure.persistence.jpa.JpaMateriaRepository;
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
@DisplayName("Tests para MateriaRepositoryImpl")
class MateriaRepositoryImplTest {

    @Mock
    private JpaMateriaRepository jpaRepositoryMock;

    @InjectMocks
    private MateriaRepositoryImpl sut;

    private MateriaEntity entityEjemplo;
    private Materia materiaEjemplo;
    private UUID materiaId;

    @BeforeEach
    void setUp() {
        materiaId = UUID.randomUUID();
        entityEjemplo = new MateriaEntity(
                materiaId,
                "Matematica",
                "MAT001",
                "#FF5733",
                "ALTA",
                LocalDateTime.now(),
                true
        );
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
    @DisplayName("findById")
    class FindByIdTests {

        @Test
        @DisplayName("deberia retornar materia cuando existe")
        void deberiaRetornarMateriaCuandoExiste() {
            // Arrange
            when(jpaRepositoryMock.findById(materiaId)).thenReturn(Optional.of(entityEjemplo));

            // Act
            Optional<Materia> resultado = sut.findById(materiaId);

            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(materiaId, resultado.get().getId());
            assertEquals("Matematica", resultado.get().getNombre());
        }

        @Test
        @DisplayName("deberia retornar Optional vacio cuando no existe")
        void deberiaRetornarOptionalVacioCuandoNoExiste() {
            // Arrange
            when(jpaRepositoryMock.findById(materiaId)).thenReturn(Optional.empty());

            // Act
            Optional<Materia> resultado = sut.findById(materiaId);

            // Assert
            assertFalse(resultado.isPresent());
        }
    }

    @Nested
    @DisplayName("findByCodigo")
    class FindByCodigoTests {

        @Test
        @DisplayName("deberia retornar materia cuando existe por codigo")
        void deberiaRetornarMateriaCuandoExistePorCodigo() {
            // Arrange
            CodigoMateria codigo = CodigoMateria.of("MAT001");
            when(jpaRepositoryMock.findByCodigo("MAT001")).thenReturn(Optional.of(entityEjemplo));

            // Act
            Optional<Materia> resultado = sut.findByCodigo(codigo);

            // Assert
            assertTrue(resultado.isPresent());
            assertEquals("MAT001", resultado.get().getCodigo().value());
        }

        @Test
        @DisplayName("deberia retornar Optional vacio cuando codigo no existe")
        void deberiaRetornarOptionalVacioCuandoCodigoNoExiste() {
            // Arrange
            CodigoMateria codigo = CodigoMateria.of("NOEX");
            when(jpaRepositoryMock.findByCodigo("NOEX")).thenReturn(Optional.empty());

            // Act
            Optional<Materia> resultado = sut.findByCodigo(codigo);

            // Assert
            assertFalse(resultado.isPresent());
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAllTests {

        @Test
        @DisplayName("deberia retornar solo materias activas")
        void deberiaRetornarSoloMateriasActivas() {
            // Arrange
            MateriaEntity inactiva = new MateriaEntity(
                    UUID.randomUUID(), "Inactiva", "INACT", "#000000",
                    "BAJA", LocalDateTime.now(), false);
            when(jpaRepositoryMock.findAll()).thenReturn(List.of(entityEjemplo, inactiva));

            // Act
            List<Materia> resultado = sut.findAll();

            // Assert
            assertEquals(1, resultado.size());
            assertTrue(resultado.get(0).isActiva());
        }

        @Test
        @DisplayName("deberia retornar lista vacia cuando no hay materias")
        void deberiaRetornarListaVacia() {
            // Arrange
            when(jpaRepositoryMock.findAll()).thenReturn(List.of());

            // Act
            List<Materia> resultado = sut.findAll();

            // Assert
            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("findAllIncludingInactive")
    class FindAllIncludingInactiveTests {

        @Test
        @DisplayName("deberia retornar todas las materias incluyendo inactivas")
        void deberiaRetornarTodasLasMaterias() {
            // Arrange
            MateriaEntity inactiva = new MateriaEntity(
                    UUID.randomUUID(), "Inactiva", "INACT", "#000000",
                    "BAJA", LocalDateTime.now(), false);
            when(jpaRepositoryMock.findAll()).thenReturn(List.of(entityEjemplo, inactiva));

            // Act
            List<Materia> resultado = sut.findAllIncludingInactive();

            // Assert
            assertEquals(2, resultado.size());
        }
    }

    @Nested
    @DisplayName("save")
    class SaveTests {

        @Test
        @DisplayName("deberia guardar materia y retornar la entidad guardada")
        void deberiaGuardarMateria() {
            // Arrange
            when(jpaRepositoryMock.save(any(MateriaEntity.class))).thenReturn(entityEjemplo);

            // Act
            Materia resultado = sut.save(materiaEjemplo);

            // Assert
            assertNotNull(resultado);
            assertEquals("Matematica", resultado.getNombre());
            verify(jpaRepositoryMock).save(any(MateriaEntity.class));
        }
    }

    @Nested
    @DisplayName("existsById")
    class ExistsByIdTests {

        @Test
        @DisplayName("deberia retornar true cuando materia existe")
        void deberiaRetornarTrueCuandoExiste() {
            // Arrange
            when(jpaRepositoryMock.existsById(materiaId)).thenReturn(true);

            // Act
            boolean resultado = sut.existsById(materiaId);

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("deberia retornar false cuando materia no existe")
        void deberiaRetornarFalseCuandoNoExiste() {
            // Arrange
            when(jpaRepositoryMock.existsById(materiaId)).thenReturn(false);

            // Act
            boolean resultado = sut.existsById(materiaId);

            // Assert
            assertFalse(resultado);
        }
    }

    @Nested
    @DisplayName("existsByCodigo")
    class ExistsByCodigoTests {

        @Test
        @DisplayName("deberia retornar true cuando codigo existe")
        void deberiaRetornarTrueCuandoCodigoExiste() {
            // Arrange
            CodigoMateria codigo = CodigoMateria.of("MAT001");
            when(jpaRepositoryMock.existsByCodigo("MAT001")).thenReturn(true);

            // Act
            boolean resultado = sut.existsByCodigo(codigo);

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("deberia retornar false cuando codigo no existe")
        void deberiaRetornarFalseCuandoCodigoNoExiste() {
            // Arrange
            CodigoMateria codigo = CodigoMateria.of("NOEX");
            when(jpaRepositoryMock.existsByCodigo("NOEX")).thenReturn(false);

            // Act
            boolean resultado = sut.existsByCodigo(codigo);

            // Assert
            assertFalse(resultado);
        }
    }
}
