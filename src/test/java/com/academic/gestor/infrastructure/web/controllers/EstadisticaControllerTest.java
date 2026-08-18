package com.academic.gestor.infrastructure.web.controllers;

import com.academic.gestor.application.dto.EstadisticaMateriaDTO;
import com.academic.gestor.domain.ports.inbound.EstadisticaService;
import com.academic.gestor.infrastructure.web.dto.response.EstadisticaResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests para EstadisticaController, incluyendo validación de rango de fechas.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para EstadisticaController")
class EstadisticaControllerTest {

    @Mock
    private EstadisticaService estadisticaServiceMock;

    @InjectMocks
    private EstadisticaController sut;

    @Nested
    @DisplayName("obtenerHorasPorPeriodo")
    class ObtenerHorasPorPeriodoTests {

        @Test
        @DisplayName("deberia rechazar rango invalido (inicio despues de fin)")
        void deberiaRechazarRangoInvalido() {
            // Arrange
            LocalDate inicio = LocalDate.of(2026, 8, 20);
            LocalDate fin = LocalDate.of(2026, 8, 10);

            // Act & Assert
            assertThrows(IllegalArgumentException.class,
                    () -> sut.obtenerHorasPorPeriodo(inicio, fin));
            verify(estadisticaServiceMock, never()).obtenerHorasPorPeriodo(any(), any());
        }

        @Test
        @DisplayName("deberia aceptar rango valido")
        void deberiaAceptarRangoValido() {
            // Arrange
            LocalDate inicio = LocalDate.of(2026, 8, 10);
            LocalDate fin = LocalDate.of(2026, 8, 20);
            when(estadisticaServiceMock.obtenerHorasPorPeriodo(inicio, fin))
                    .thenReturn(List.of());

            // Act
            ResponseEntity<List<EstadisticaResponse>> response = sut.obtenerHorasPorPeriodo(inicio, fin);

            // Assert
            assertEquals(200, response.getStatusCode().value());
        }
    }

    @Nested
    @DisplayName("obtenerResumen")
    class ObtenerResumenTests {

        @Test
        @DisplayName("deberia rechazar rango invalido")
        void deberiaRechazarRangoInvalido() {
            // Arrange
            LocalDate inicio = LocalDate.of(2026, 8, 20);
            LocalDate fin = LocalDate.of(2026, 8, 10);

            // Act & Assert
            assertThrows(IllegalArgumentException.class,
                    () -> sut.obtenerResumen(inicio, fin));
            verify(estadisticaServiceMock, never()).obtenerTotalHorasPeriodo(any(), any());
        }

        @Test
        @DisplayName("deberia aceptar rango valido")
        void deberiaAceptarRangoValido() {
            // Arrange
            LocalDate inicio = LocalDate.of(2026, 8, 10);
            LocalDate fin = LocalDate.of(2026, 8, 20);
            when(estadisticaServiceMock.obtenerTotalHorasPeriodo(inicio, fin)).thenReturn(5.0);
            when(estadisticaServiceMock.obtenerTotalPomodorosPeriodo(inicio, fin)).thenReturn(3L);

            // Act
            ResponseEntity<Map<String, Object>> response = sut.obtenerResumen(inicio, fin);

            // Assert
            assertEquals(200, response.getStatusCode().value());
            assertEquals(5.0, response.getBody().get("totalHoras"));
            assertEquals(3L, response.getBody().get("totalPomodoros"));
        }
    }
}