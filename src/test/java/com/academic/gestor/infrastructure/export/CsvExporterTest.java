package com.academic.gestor.infrastructure.export;

import com.academic.gestor.application.dto.EstadisticaMateriaDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para CsvExporter, enfocados en escape de campos y CSV injection.
 */
@DisplayName("Tests para CsvExporter")
class CsvExporterTest {

    private static final UUID MATERIA_ID = UUID.randomUUID();

    private EstadisticaMateriaDTO estadistica(final String nombre, final String codigo) {
        return new EstadisticaMateriaDTO(
                MATERIA_ID, nombre, codigo, 2.5, 3, 10, 7, 70.0
        );
    }

    @Nested
    @DisplayName("toCsv")
    class ToCsvTests {

        @Test
        @DisplayName("deberia generar encabezados y filas")
        void deberiaGenerarEncabezadosYFilas() {
            // Arrange
            List<EstadisticaMateriaDTO> datos = List.of(
                    estadistica("Matemática", "MAT-101")
            );

            // Act
            String csv = new CsvExporter().toCsv(datos);

            // Assert
            assertTrue(csv.startsWith("Materia,Código,Horas Estudiadas,Pomodoros,Tareas Total,Tareas Completadas,Progreso %"));
            assertTrue(csv.contains("Matemática"));
            assertTrue(csv.contains("MAT-101"));
            assertTrue(csv.contains("2.50"));
        }

        @Test
        @DisplayName("deberia neutralizar CSV injection en campos con = + - @")
        void deberiaNeutralizarCsvInjection() {
            // Arrange
            List<EstadisticaMateriaDTO> datos = List.of(
                    estadistica("=SUM(A1:A10)", "@comando"),
                    estadistica("-2+3", "+DDE")
            );

            // Act
            String csv = new CsvExporter().toCsv(datos);

            // Assert
            assertTrue(csv.contains("'=SUM(A1:A10)"));
            assertTrue(csv.contains("'@comando"));
            assertTrue(csv.contains("'-2+3"));
            assertTrue(csv.contains("'+DDE"));
        }

        @Test
        @DisplayName("deberia escapar comillas dobles en campos")
        void deberiaEscaparComillas() {
            // Arrange
            List<EstadisticaMateriaDTO> datos = List.of(
                    estadistica("Análisis \"Matemático\"", "MAT-101")
            );

            // Act
            String csv = new CsvExporter().toCsv(datos);

            // Assert
            assertTrue(csv.contains("\"Análisis \"\"Matemático\"\"\""));
        }
    }
}