package com.academic.gestor.application.services;

import com.academic.gestor.application.dto.EstadisticaMateriaDTO;
import com.academic.gestor.domain.model.entities.Materia;
import com.academic.gestor.domain.model.enums.EstadoTarea;
import com.academic.gestor.domain.model.enums.Prioridad;
import com.academic.gestor.domain.model.valueobjects.CodigoMateria;
import com.academic.gestor.domain.model.valueobjects.Color;
import com.academic.gestor.domain.ports.outbound.MateriaRepository;
import com.academic.gestor.domain.ports.outbound.SesionPomodoroRepository;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para EstadisticaServiceImpl")
class EstadisticaServiceImplTest {

    @Mock
    private MateriaRepository materiaRepositoryMock;

    @Mock
    private TareaRepository tareaRepositoryMock;

    @Mock
    private SesionPomodoroRepository sesionRepositoryMock;

    @InjectMocks
    private EstadisticaServiceImpl sut;

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
    @DisplayName("obtenerHorasPorMateria")
    class ObtenerHorasPorMateriaTests {

        @Test
        @DisplayName("deberia retornar estadisticas de horas por materia")
        void deberiaRetornarEstadisticasDeHorasPorMateria() {
            // Arrange
            when(materiaRepositoryMock.findAll()).thenReturn(List.of(materiaEjemplo));
            when(sesionRepositoryMock.sumDuracionByMateriaId(materiaId)).thenReturn(120L);
            when(tareaRepositoryMock.countByMateriaIdAndEstado(materiaId, EstadoTarea.PENDIENTE)).thenReturn(2L);
            when(tareaRepositoryMock.countByMateriaIdAndEstado(materiaId, EstadoTarea.EN_PROGRESO)).thenReturn(1L);
            when(tareaRepositoryMock.countByMateriaIdAndEstado(materiaId, EstadoTarea.COMPLETADA)).thenReturn(3L);

            // Act
            List<EstadisticaMateriaDTO> resultado = sut.obtenerHorasPorMateria();

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals(materiaId, resultado.get(0).materiaId());
            assertEquals("Matematica", resultado.get(0).nombreMateria());
            assertEquals(2.0, resultado.get(0).horasEstudiadas(), 0.01);
            assertEquals(6, resultado.get(0).tareasTotales());
            assertEquals(3, resultado.get(0).tareasCompletadas());
            assertEquals(50.0, resultado.get(0).porcentajeProgreso(), 0.01);
        }

        @Test
        @DisplayName("deberia retornar lista vacia cuando no hay materias")
        void deberiaRetornarListaVaciaCuandoNoHayMaterias() {
            // Arrange
            when(materiaRepositoryMock.findAll()).thenReturn(List.of());

            // Act
            List<EstadisticaMateriaDTO> resultado = sut.obtenerHorasPorMateria();

            // Assert
            assertTrue(resultado.isEmpty());
        }

        @Test
        @DisplayName("deberia retornar 0 horas cuando no hay sesiones")
        void deberiaRetornar0HorasCuandoNoHaySesiones() {
            // Arrange
            when(materiaRepositoryMock.findAll()).thenReturn(List.of(materiaEjemplo));
            when(sesionRepositoryMock.sumDuracionByMateriaId(materiaId)).thenReturn(0L);
            when(tareaRepositoryMock.countByMateriaIdAndEstado(materiaId, EstadoTarea.PENDIENTE)).thenReturn(0L);
            when(tareaRepositoryMock.countByMateriaIdAndEstado(materiaId, EstadoTarea.EN_PROGRESO)).thenReturn(0L);
            when(tareaRepositoryMock.countByMateriaIdAndEstado(materiaId, EstadoTarea.COMPLETADA)).thenReturn(0L);

            // Act
            List<EstadisticaMateriaDTO> resultado = sut.obtenerHorasPorMateria();

            // Assert
            assertEquals(0.0, resultado.get(0).horasEstudiadas(), 0.01);
            assertEquals(0.0, resultado.get(0).porcentajeProgreso(), 0.01);
        }
    }

    @Nested
    @DisplayName("obtenerHorasPorPeriodo")
    class ObtenerHorasPorPeriodoTests {

        @Test
        @DisplayName("deberia retornar estadisticas en un periodo especifico")
        void deberiaRetornarEstadisticasEnPeriodo() {
            // Arrange
            LocalDate inicio = LocalDate.now().minusDays(7);
            LocalDate fin = LocalDate.now();
            when(materiaRepositoryMock.findAll()).thenReturn(List.of(materiaEjemplo));
            when(sesionRepositoryMock.sumDuracionByMateriaIdAndFechaInRange(
                    eq(materiaId), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(60L);
            when(sesionRepositoryMock.findCompletadasByFechaInRange(
                    any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of());
            when(tareaRepositoryMock.countByMateriaIdAndEstado(materiaId, EstadoTarea.PENDIENTE)).thenReturn(0L);
            when(tareaRepositoryMock.countByMateriaIdAndEstado(materiaId, EstadoTarea.EN_PROGRESO)).thenReturn(0L);
            when(tareaRepositoryMock.countByMateriaIdAndEstado(materiaId, EstadoTarea.COMPLETADA)).thenReturn(0L);

            // Act
            List<EstadisticaMateriaDTO> resultado = sut.obtenerHorasPorPeriodo(inicio, fin);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals(1.0, resultado.get(0).horasEstudiadas(), 0.01);
        }
    }

    @Nested
    @DisplayName("obtenerProgresoTareas")
    class ObtenerProgresoTareasTests {

        @Test
        @DisplayName("deberia retornar progreso de tareas por materia")
        void deberiaRetornarProgresoDeTareasPorMateria() {
            // Arrange
            when(materiaRepositoryMock.findAll()).thenReturn(List.of(materiaEjemplo));
            when(tareaRepositoryMock.countByMateriaIdAndEstado(materiaId, EstadoTarea.PENDIENTE)).thenReturn(2L);
            when(tareaRepositoryMock.countByMateriaIdAndEstado(materiaId, EstadoTarea.EN_PROGRESO)).thenReturn(1L);
            when(tareaRepositoryMock.countByMateriaIdAndEstado(materiaId, EstadoTarea.COMPLETADA)).thenReturn(2L);

            // Act
            List<EstadisticaMateriaDTO> resultado = sut.obtenerProgresoTareas();

            // Assert
            assertEquals(1, resultado.size());
            assertEquals(5, resultado.get(0).tareasTotales());
            assertEquals(2, resultado.get(0).tareasCompletadas());
            assertEquals(40.0, resultado.get(0).porcentajeProgreso(), 0.01);
        }

        @Test
        @DisplayName("deberia retornar 0 progreso cuando no hay tareas")
        void deberiaRetornar0ProgresoCuandoNoHayTareas() {
            // Arrange
            when(materiaRepositoryMock.findAll()).thenReturn(List.of(materiaEjemplo));
            when(tareaRepositoryMock.countByMateriaIdAndEstado(materiaId, EstadoTarea.PENDIENTE)).thenReturn(0L);
            when(tareaRepositoryMock.countByMateriaIdAndEstado(materiaId, EstadoTarea.EN_PROGRESO)).thenReturn(0L);
            when(tareaRepositoryMock.countByMateriaIdAndEstado(materiaId, EstadoTarea.COMPLETADA)).thenReturn(0L);

            // Act
            List<EstadisticaMateriaDTO> resultado = sut.obtenerProgresoTareas();

            // Assert
            assertEquals(0.0, resultado.get(0).porcentajeProgreso(), 0.01);
        }
    }

    @Nested
    @DisplayName("obtenerTotalHorasPeriodo")
    class ObtenerTotalHorasPeriodoTests {

        @Test
        @DisplayName("deberia retornar total de horas en un periodo")
        void deberiaRetornarTotalDeHorasEnPeriodo() {
            // Arrange
            when(sesionRepositoryMock.sumDuracionByFechaInRange(
                    any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(150L);

            // Act
            double resultado = sut.obtenerTotalHorasPeriodo(
                    LocalDate.now().minusDays(7), LocalDate.now());

            // Assert
            assertEquals(2.5, resultado, 0.01);
        }

        @Test
        @DisplayName("deberia retornar 0 horas cuando no hay sesiones")
        void deberiaRetornar0HorasCuandoNoHaySesiones() {
            // Arrange
            when(sesionRepositoryMock.sumDuracionByFechaInRange(
                    any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(0L);

            // Act
            double resultado = sut.obtenerTotalHorasPeriodo(
                    LocalDate.now().minusDays(7), LocalDate.now());

            // Assert
            assertEquals(0.0, resultado, 0.01);
        }
    }

    @Nested
    @DisplayName("obtenerTotalPomodorosPeriodo")
    class ObtenerTotalPomodorosPeriodoTests {

        @Test
        @DisplayName("deberia retornar total de pomodoros en un periodo")
        void deberiaRetornarTotalDePomodorosEnPeriodo() {
            // Arrange - sesiones mock que simuladen pomodoros TRABAJO
            when(sesionRepositoryMock.findCompletadasByFechaInRange(
                    any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(List.of());

            // Act
            long resultado = sut.obtenerTotalPomodorosPeriodo(
                    LocalDate.now().minusDays(7), LocalDate.now());

            // Assert
            assertEquals(0, resultado);
        }
    }
}
