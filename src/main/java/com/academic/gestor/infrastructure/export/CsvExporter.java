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
 * <p>Escapa correctamente los campos (RFC 4180) y previene la
 * inyección de fórmulas (CSV injection) al neutralizar campos que
 * comienzan con caracteres peligrosos ({@code =}, {@code +}, {@code -}, {@code @}).</p>
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
@Component
public class CsvExporter {

    private static final Logger log = LoggerFactory.getLogger(CsvExporter.class);

    /**
     * Escapa un campo CSV: envuelve en comillas si contiene separadores,
     * comillas o saltos de línea, y neutraliza fórmulas maliciosas.
     *
     * @param value valor a escapar
     * @return valor seguro para CSV
     */
    private String escapeField(final String value) {
        if (value == null) {
            return "";
        }

        String field = value;
        // Neutralizar CSV injection
        if (!field.isEmpty()) {
            final char first = field.charAt(0);
            if (first == '=' || first == '+' || first == '-' || first == '@') {
                field = "'" + field;
            }
        }

        if (field.contains(",") || field.contains("\"") || field.contains("\n")
                || field.contains("\r")) {
            field = "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }

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
                writer.println(toCsvRow(est));
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
            sb.append(toCsvRow(est)).append('\n');
        }

        return sb.toString();
    }

    /**
     * Construye una fila CSV a partir de un DTO de estadísticas.
     *
     * @param est DTO de estadísticas
     * @return fila CSV formateada
     */
    private String toCsvRow(final EstadisticaMateriaDTO est) {
        return String.format("%s,%s,%.2f,%d,%d,%d,%.1f%%",
                escapeField(est.nombreMateria()),
                escapeField(est.codigoMateria()),
                est.horasEstudiadas(),
                est.pomodorosCompletados(),
                est.tareasTotales(),
                est.tareasCompletadas(),
                est.porcentajeProgreso()
        );
    }
}
