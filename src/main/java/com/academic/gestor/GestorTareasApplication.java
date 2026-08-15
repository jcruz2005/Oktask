package com.academic.gestor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Gestor de Tareas Académicas.
 *
 * <p>Implementa un sistema completo de gestión de tareas académicas
 * con integración de temporizador Pomodoro y análisis de horas de estudio.</p>
 *
 * @author Gestor de Tareas Académicas
 * @since 1.0.0
 */
@SpringBootApplication
public class GestorTareasApplication {

    /**
     * Punto de entrada principal de la aplicación.
     *
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(GestorTareasApplication.class, args);
    }
}
