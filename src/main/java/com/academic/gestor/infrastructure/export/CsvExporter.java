package com.academic.gestor.infrastructure.export;

import com.academic.gestor.application.dto.EstadisticaMateriaDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Exportador de datos a formato CSV.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
@Component
public class CsvExporter {

    private static final Logger log = LoggerFactory.getLogger(CsvExporter.class);

    /**
     * Exporta una lista de estadísticas a un archivo CSV.
     *
     * @param estadísticas datos a exportar
     * @param filePath ruta del archivo de salida
     * @throws IOException si hay error al escribir el archivo
     */
    public void exportarEstadisticas(final List<EstadisticaMateriaDTO> estadísticas,
                                     final String filePath) throws IOException {
        log.info("Exportando estadísticas a CSV: {}", filePath);

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Encabezados
            writer.println("Materia,Código,Horas Estudiadas,Pomodoros,Tareas Total,Tareas Completadas,Progreso %");

            // Datos
            for (final EstadisticaMateriaDTO est : estadísticas) {
                writer.printf("%s,%s,%.2f,%d,%d,%d,%.1f%%%n",
                        est.nombreMateria(),
                        est.codigoMateria(),
                        est.horasEstudiadas(),
                        est.pomodorosCompletados(),
                        est.tareasTotales(),
                        est.tareasCompletadas(),
                        est.porcentajeProgreso()
                );
            }
        }

        log.info("Estadísticas exportadas exitosamente a {}", filePath);
    }

    /**
     * Convierte una lista de estadísticas a cadena CSV.
     *
     * @param estadísticas datos a convertir
     * @return cadena CSV
     */
    public String toCsv(final List<EstadisticaMateriaDTO> estadísticas) {
        final StringBuilder sb = new StringBuilder();

        // Encabezados
        sb.append("Materia,Código,Horas Estudiadas,Pomodoros,Tareas Total,Tareas Completadas,Progreso %\n");

        // Datos
        for (final EstadisticaMateriaDTO est : estadísticas) {
            sb.append(String.format("%s,%s,%.2f,%d,%d,%d,%.1f%%%n",
                    est.nombreMateria(),
                    est.codigoMateria(),
                    est.horasEstudiadas(),
                    est.pomodorosCompletados(),
                    est.tareasTotales(),
                    est.tareasCompletadas(),
                    est.porcentajeProgreso()
            ));
        }

        return sb.toString();
    }
}
