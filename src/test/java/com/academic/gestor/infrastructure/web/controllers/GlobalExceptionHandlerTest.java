package com.academic.gestor.infrastructure.web.controllers;

import com.academic.gestor.domain.exceptions.MateriaNotFoundException;
import com.academic.gestor.domain.exceptions.TareaNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.validation.ConstraintViolationException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el manejador global de excepciones.
 */
@DisplayName("Tests para GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler sut;

    @BeforeEach
    void setUp() {
        sut = new GlobalExceptionHandler();
    }

    @Nested
    @DisplayName("Errores de dominio")
    class ErroresDominioTests {

        @Test
        @DisplayName("materia no encontrada retorna 404")
        void materiaNoEncontradaRetorna404() {
            ResponseEntity<Map<String, Object>> response =
                    sut.handleMateriaNotFound(new MateriaNotFoundException("MAT-001"));

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("MATERIA_NO_ENCONTRADA", response.getBody().get("error"));
        }

        @Test
        @DisplayName("tarea no encontrada retorna 404")
        void tareaNoEncontradaRetorna404() {
            ResponseEntity<Map<String, Object>> response =
                    sut.handleTareaNotFound(new TareaNotFoundException("abc"));

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("TAREA_NO_ENCONTRADA", response.getBody().get("error"));
        }
    }

    @Nested
    @DisplayName("Errores de validación")
    class ErroresValidacionTests {

        @Test
        @DisplayName("validacion con FieldError incluye el campo")
        void validacionConFieldErrorIncluyeCampo() throws Exception {
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(
                    new Object(), "objeto");
            bindingResult.addError(new FieldError("objeto", "titulo", "no puede estar vacío"));
            MethodArgumentNotValidException ex =
                    new MethodArgumentNotValidException(metodoValidador(), bindingResult);

            ResponseEntity<Map<String, Object>> response = sut.handleValidationErrors(ex);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            @SuppressWarnings("unchecked")
            Map<String, String> errores = (Map<String, String>) response.getBody().get("errores");
            assertEquals("no puede estar vacío", errores.get("titulo"));
        }

        @Test
        @DisplayName("validacion con ObjectError (sin campo) no lanza ClassCastException")
        void validacionConObjectErrorNoLanzaClassCast() throws Exception {
            BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(
                    new Object(), "objeto");
            bindingResult.addError(new ObjectError("objeto", "error global"));
            MethodArgumentNotValidException ex =
                    new MethodArgumentNotValidException(metodoValidador(), bindingResult);

            ResponseEntity<Map<String, Object>> response = sut.handleValidationErrors(ex);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        private static org.springframework.core.MethodParameter metodoValidador()
                throws NoSuchMethodException {
            return new org.springframework.core.MethodParameter(
                    GlobalExceptionHandler.class.getMethod(
                            "handleValidationErrors", MethodArgumentNotValidException.class),
                    0);
        }
    }

    @Nested
    @DisplayName("Violaciones de integridad")
    class ViolacionesIntegridadTests {

        @Test
        @DisplayName("DataIntegrityViolationException retorna 409")
        void dataIntegrityViolationRetorna409() {
            ResponseEntity<Map<String, Object>> response =
                    sut.handleDataIntegrityViolation(new DataIntegrityViolationException("fk"));

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertEquals("VIOLACION_INTEGRIDAD", response.getBody().get("error"));
        }

        @Test
        @DisplayName("ConstraintViolationException retorna 400")
        void constraintViolationRetorna400() {
            ResponseEntity<Map<String, Object>> response =
                    sut.handleConstraintViolation(new ConstraintViolationException("inválido", null));

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("RESTRICCION_INVALIDA", response.getBody().get("error"));
        }
    }
}