package com.academic.gestor.infrastructure.export;

import com.academic.gestor.application.dto.EstadisticaMateriaDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Exportador de datos a formato JSON.
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
@Component
public class JsonExporter {

    private static final Logger log = LoggerFactory.getLogger(JsonExporter.class);

    private final ObjectMapper objectMapper;

    /**
     * Constructor que configura el ObjectMapper.
     */
    public JsonExporter() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Exporta una lista de estadísticas a un archivo JSON.
     *
     * @param estadísticas datos a exportar
     * @param filePath ruta del archivo de salida
     * @throws IOException si hay error al escribir el archivo
     */
    public void exportarEstadisticas(final List<EstadisticaMateriaDTO> estadísticas,
                                     final String filePath) throws IOException {
        log.info("Exportando estadísticas a JSON: {}", filePath);

        final File outputFile = new File(filePath);
        objectMapper.writeValue(outputFile, estadísticas);

        log.info("Estadísticas exportadas exitosamente a {}", filePath);
    }

    /**
     * Convierte una lista de estadísticas a cadena JSON.
     *
     * @param estadísticas datos a convertir
     * @return cadena JSON formateada
     * @throws IOException si hay error en la serialización
     */
    public String toJson(final List<EstadisticaMateriaDTO> estadísticas) throws IOException {
        return objectMapper.writeValueAsString(estadísticas);
    }
}
