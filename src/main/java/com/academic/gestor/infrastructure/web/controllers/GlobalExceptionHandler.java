package com.academic.gestor.infrastructure.web.controllers;

import com.academic.gestor.domain.exceptions.ConfiguracionInvalidaException;
import com.academic.gestor.domain.exceptions.MateriaNotFoundException;
import com.academic.gestor.domain.exceptions.SesionPomodoroException;
import com.academic.gestor.domain.exceptions.TareaNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para la API REST.
 *
 * <p>Captura las excepciones del dominio y las convierte en
 * respuestas HTTP adecuadas con códigos de error informativos.</p>
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja excepciones de materia no encontrada.
     *
     * @param ex excepción capturada
     * @return respuesta HTTP 404
     */
    @ExceptionHandler(MateriaNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleMateriaNotFound(
            final MateriaNotFoundException ex) {
        log.warn("Materia no encontrada: {}", ex.getMessage());

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "MATERIA_NO_ENCONTRADA",
                ex.getMessage()
        );
    }

    /**
     * Maneja excepciones de tarea no encontrada.
     *
     * @param ex excepción capturada
     * @return respuesta HTTP 404
     */
    @ExceptionHandler(TareaNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTareaNotFound(
            final TareaNotFoundException ex) {
        log.warn("Tarea no encontrada: {}", ex.getMessage());

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                "TAREA_NO_ENCONTRADA",
                ex.getMessage()
        );
    }

    /**
     * Maneja excepciones de sesión de Pomodoro.
     *
     * @param ex excepción capturada
     * @return respuesta HTTP 400
     */
    @ExceptionHandler(SesionPomodoroException.class)
    public ResponseEntity<Map<String, Object>> handleSesionPomodoro(
            final SesionPomodoroException ex) {
        log.warn("Error en sesión de Pomodoro: {}", ex.getMessage());

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "SESION_POMODORO_ERROR",
                ex.getMessage()
        );
    }

    /**
     * Maneja excepciones de configuración inválida.
     *
     * @param ex excepción capturada
     * @return respuesta HTTP 400
     */
    @ExceptionHandler(ConfiguracionInvalidaException.class)
    public ResponseEntity<Map<String, Object>> handleConfiguracionInvalida(
            final ConfiguracionInvalidaException ex) {
        log.warn("Configuración inválida: {}", ex.getMessage());

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "CONFIGURACION_INVALIDA",
                ex.getMessage()
        );
    }

    /**
     * Maneja excepciones de argumentos ilegales.
     *
     * @param ex excepción capturada
     * @return respuesta HTTP 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(
            final IllegalArgumentException ex) {
        log.warn("Argumento inválido: {}", ex.getMessage());

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "ARGUMENTO_INVALIDO",
                ex.getMessage()
        );
    }

    /**
     * Maneja errores de validación de argumentos.
     *
     * @param ex excepción capturada
     * @return respuesta HTTP 400 con detalles de validación
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            final MethodArgumentNotValidException ex) {
        log.warn("Errores de validación: {} campo(s) inválido(s)",
                ex.getBindingResult().getAllErrors().size());

        final Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            final String fieldName = error instanceof FieldError
                    ? ((FieldError) error).getField()
                    : error.getObjectName();
            final String errorMessage = error.getDefaultMessage();
            errores.put(fieldName, errorMessage);
        });

        final Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "VALIDACION_FALLIDA");
        body.put("message", "Error de validación en los campos");
        body.put("errores", errores);

        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Maneja violaciones de integridad referencial de la base de datos.
     *
     * @param ex excepción capturada
     * @return respuesta HTTP 409 (conflicto)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(
            final DataIntegrityViolationException ex) {
        log.warn("Violación de integridad de datos: {}", ex.getMessage());

        return buildErrorResponse(
                HttpStatus.CONFLICT,
                "VIOLACION_INTEGRIDAD",
                "La operación viola restricciones de integridad de datos."
        );
    }

    /**
     * Maneja violaciones de restricciones de validación a nivel de parámetros.
     *
     * @param ex excepción capturada
     * @return respuesta HTTP 400
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(
            final ConstraintViolationException ex) {
        log.warn("Violación de restricción: {}", ex.getMessage());

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "RESTRICCION_INVALIDA",
                ex.getMessage()
        );
    }

    /**
     * Maneja excepciones no controladas.
     *
     * @param ex excepción capturada
     * @return respuesta HTTP 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(final Exception ex) {
        log.error("Error inesperado: ", ex);

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "ERROR_INTERNO",
                "Ocurrió un error inesperado. Por favor, intente nuevamente."
        );
    }

    /**
     * Construye una respuesta de error estandarizada.
     *
     * @param status código de estado HTTP
     * @param errorCode código de error de la aplicación
     * @param message mensaje descriptivo del error
     * @return respuesta HTTP con el error
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            final HttpStatus status, final String errorCode, final String message) {
        final Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", errorCode);
        body.put("message", message);

        return ResponseEntity.status(status).body(body);
    }
}
