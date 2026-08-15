# Gestor de Tareas Académicas

Sistema de gestión de tareas académicas con Pomodoro y análisis de horas de estudio.

## Características

- **Gestión de Materias**: Crear, editar y eliminar materias con colores y prioridades
- **Gestión de Tareas**: Crear tareas con fechas límite y estados (Pendiente, En Progreso, Completada)
- **Temporizador Pomodoro**: Sesiones de estudio de 25 minutos (configurables) con descansos
- **Estadísticas**: Análisis de horas estudiadas por materia y período
- **Exportación**: Exportar datos en formato CSV y JSON
- **Modo Oscuro**: Soporte para tema claro y oscuro
- **Drag & Drop**: Reordenar tareas con arrastrar y soltar

## Tecnologías

- **Backend**: Spring Boot 3.3.2 + Java 21
- **Base de datos**: SQLite
- **Frontend**: HTML5 + CSS3 + JavaScript vanilla
- **UI Framework**: Bootstrap 5
- **Gráficos**: Chart.js
- **Drag & Drop**: SortableJS

## Instalación

### Requisitos Previos

- Java 21 o superior
- Maven 3.8+

### Ejecución de Desarrollo

```bash
# Clonar el repositorio
git clone https://github.com/TU_USUARIO/academic-gestor.git
cd academic-gestor

# Ejecutar la aplicación
mvn spring-boot:run
```

La aplicación estará disponible en: http://localhost:8080

### Instalación Nativa (jpackage)

Para crear instaladores nativos para tu plataforma:

**Linux:**
```bash
chmod +x build-linux.sh
./build-linux.sh
```

**Windows:**
```cmd
build-windows.bat
```

**macOS:**
```bash
chmod +x build-macos.sh
./build-macos.sh
```

Los instaladores se crearán en `target/installers/`

## Estructura del Proyecto

```
academic-gestor/
├── src/
│   ├── main/
│   │   ├── java/com/academic/gestor/
│   │   │   ├── GestorTareasApplication.java    # Clase principal
│   │   │   ├── NativeLauncher.java             # Launcher JavaFX
│   │   │   ├── application/                    # Capa de aplicación
│   │   │   ├── domain/                         # Dominio
│   │   │   ├── infrastructure/                 # Infraestructura
│   │   │   └── shared/                         # Utilidades
│   │   └── resources/
│   │       ├── application.yml                 # Configuración
│   │       └── static/                         # Frontend
│   └── test/                                   # Tests
├── native/                                     # Assets nativos
│   ├── icons/                                  # Iconos por plataforma
│   └── scripts/                                # Scripts de build
├── build-linux.sh                              # Build Linux
├── build-windows.bat                           # Build Windows
├── build-macos.sh                              # Build macOS
└── pom.xml                                     # Configuración Maven
```

## API REST

### Materias
- `GET /api/materias` - Listar materias
- `POST /api/materias` - Crear materia
- `PUT /api/materias/{id}` - Actualizar materia
- `DELETE /api/materias/{id}` - Eliminar materia

### Tareas
- `GET /api/tareas` - Listar tareas
- `POST /api/tareas` - Crear tarea
- `PUT /api/tareas/{id}` - Actualizar tarea
- `DELETE /api/tareas/{id}` - Eliminar tarea

### Pomodoro
- `POST /api/pomodoro/iniciar` - Iniciar sesión
- `POST /api/pomodoro/{id}/finalizar` - Finalizar sesión
- `POST /api/pomodoro/{id}/cancelar` - Cancelar sesión (guarda tiempo)
- `GET /api/pomodoro/configuracion` - Obtener configuración
- `PUT /api/pomodoro/configuracion` - Actualizar configuración

### Estadísticas
- `GET /api/estadisticas/horas` - Horas por materia
- `GET /api/estadisticas/horas/periodo` - Horas por período
- `GET /api/estadisticas/resumen` - Resumen de estadísticas

## Configuración

### application.yml

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:sqlite:data/gestor-tareas.db
    driver-class-name: org.sqlite.JDBC

pomodoro:
  duracion-trabajo: 25
  duracion-descanso: 5
  duracion-descanso-largo: 15
  pomodoros-para-descanso-largo: 4
```

## Desarrollo

### Ejecutar Tests

```bash
mvn test
```

### Compilar

```bash
mvn clean compile
```

### Empaquetar

```bash
mvn clean package -DskipTests
```

## Licencia

MIT License

## Autor

Gestor de Tareas Académicas - 2024
