# Gestor de Tareas Académicas

Sistema de gestión de tareas académicas con Pomodoro y análisis de horas de estudio.

[![Release](https://img.shields.io/github/v/release/jcruz2005/academic-gestor?style=flat-square)](https://github.com/jcruz2005/academic-gestor/releases/tag/v1.0.0)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](#licencia)
[![Java 21](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot 3.3](https://img.shields.io/badge/Spring%20Boot-3.3.2-green.svg)](https://spring.io/projects/spring-boot)

## Características

- **Gestión de Materias**: Crear, editar y eliminar materias con colores y prioridades
- **Gestión de Tareas**: Crear tareas con fechas límite y estados (Pendiente, En Progreso, Completada)
- **Temporizador Pomodoro**: Sesiones de estudio de 25 minutos (configurables) con descansos
- **Estadísticas**: Análisis de horas estudiadas por materia y período
- **Exportación**: Exportar datos en formato CSV y JSON
- **Modo Oscuro**: Soporte para tema claro y oscuro
- **Drag & Drop**: Reordenar tareas con arrastrar y soltar

## Tecnologías

| Capa | Tecnología |
|---|---|
| Backend | Spring Boot 3.3.2 + Java 21 |
| Base de datos | SQLite (embebida, sin configuración) |
| Frontend | HTML5 + CSS3 + JavaScript vanilla |
| UI Framework | Bootstrap 5 |
| Gráficos | Chart.js |
| Drag & Drop | SortableJS |

---

## Instalación

### Opción 1 — Ejecutar desde el JAR (todas las plataformas)

Esta es la forma más rápida de usar la aplicación sin compilar nada.

#### 1. Requisitos previos

| Requisito | Versión mínima | Cómo verificar |
|---|---|---|
| **Java (JRE o JDK)** | 21 o superior | `java -version` |
| **Maven** | 3.8+ (solo para compilar) | `mvn --version` |

> **No necesitas instalar SQLite** — la base de datos se crea automáticamente al iniciar.

#### 2. Clonar el repositorio

```bash
git clone https://github.com/jcruz2005/academic-gestor.git
cd academic-gestor
```

#### 3. Ejecutar

```bash
mvn spring-boot:run
```

La aplicación estará disponible en **http://localhost:8080**

#### 4. Generar el JAR ejecutable (opcional)

Si querés generar un archivo `.jar` portátil para ejecutar sin Maven:

```bash
mvn clean package -DskipTests
java -jar target/gestor-tareas-1.0.0-SNAPSHOT.jar
```

---

### Opción 2 — Aplicación nativa con jpackage

Los scripts de build crean una **aplicación nativa empaquetada** con su propio runtime de Java incrustado. El usuario final no necesita tener Java instalado.

#### Requisitos previos para compilar

| Requisito | Versión mínima | Notas |
|---|---|---|
| **JDK** | 21 o superior | Debe incluir `jpackage` (disponible desde JDK 14+) |
| **Maven** | 3.8+ | |
| **Xcode CLI Tools** | — | Solo en macOS (`xcode-select --install`) |

---

### 🐧 Linux

**Distribuciones probadas:** Ubuntu 22.04+, Fedora 38+, Arch Linux, CachyOS

#### Instalar Java 21 (si no lo tenés)

**Ubuntu / Debian:**
```bash
sudo apt update
sudo apt install openjdk-21-jdk maven
```

**Fedora / RHEL:**
```bash
sudo dnf install java-21-openjdk-devel maven
```

**Arch Linux:**
```bash
sudo pacman -S jdk21-openjdk maven
```

#### Compilar y ejecutar

```bash
# Clonar
git clone https://github.com/jcruz2005/academic-gestor.git
cd academic-gestor

# Compilar imagen nativa
chmod +x build-linux.sh
./build-linux.sh
```

#### Resultado

```
target/installers/GestorTareasAcademicas/
├── bin/
│   └── GestorTareasAcademicas      ← ejecutable
├── lib/
└── runtime/                         ← JRE embebido (el usuario no necesita Java)
```

#### Ejecutar

```bash
./target/installers/GestorTareasAcademicas/bin/GestorTareasAcademicas
```

#### Crear paquete `.deb` (opcional)

```bash
sudo apt install dpkg
dpkg-deb --build target/installers/GestorTareasAcademicas gestor-tareas-1.0.0.deb
sudo dpkg -i gestor-tareas-1.0.0.deb
```

---

### 🪟 Windows

**Versiones probadas:** Windows 10 (22H2+), Windows 11

#### Instalar Java 21 (si no lo tenés)

1. Descargá el instalador desde [Adoptium](https://adoptium.net/temurin/releases/?version=21) (recomendado) o [Oracle](https://www.oracle.com/java/technologies/downloads/)
2. Durante la instalación, marcá **"Add to PATH"** y **"Set JAVA_HOME"**
3. Reiniciá la terminal (o el sistema)

También necesitás [Maven](https://maven.apache.org/download.cgi) — descargar, descomprimir y agregar la carpeta `bin` al PATH.

#### Compilar

Abrí **Command Prompt** o **PowerShell** como administrador:

```cmd
cd C:\Users\TU_USUARIO\academic-gestor
build-windows.bat
```

#### Resultado

```
target\installers\GestorTareasAcademicas\
├── GestorTareasAcademicas.exe      ← ejecutable
├── lib\
└── runtime\                         ← JRE embebido
```

#### Ejecutar

```cmd
target\installers\GestorTareasAcademicas\GestorTareasAcademicas.exe
```

> **Nota:** Windows puede mostrar una alerta de SmartScreen la primera vez. Hacé clic en **"Más informações" → "Ejecutar de todas formas"**.

---

### 🍎 macOS

**Versiones probadas:** macOS Ventura (13+), Sonoma (14+), Sequoia (15+)

#### Instalar Java 21 (si no lo tenés)

**Con Homebrew** (recomendado):
```bash
brew install openjdk@21 maven
```

**Manual:**
1. Descargá el `.pkg` desde [Adoptium](https://adoptium.net/temurin/releases/?version=21) (para Apple Silicon o Intel según tu Mac)

#### Instalar Xcode Command Line Tools

```bash
xcode-select --install
```

#### Compilar y ejecutar

```bash
# Clonar
git clone https://github.com/jcruz2005/academic-gestor.git
cd academic-gestor

# Compilar imagen nativa
chmod +x build-macos.sh
./build-macos.sh
```

#### Resultado

```
target/installers/GestorTareasAcademicas.app    ← aplicación para Arrastrar a /Applications
```

#### Ejecutar

```bash
open target/installers/GestorTareasAcademicas.app
```

#### Crear instalador `.pkg` (opcional, para distribuir)

```bash
pkgbuild \
  --component target/installers/GestorTareasAcademicas.app \
  --install-location /Applications \
  --identifier com.academic.gestor-tareas \
  --version 1.0.0 \
  gestor-tareas-1.0.0.pkg
```

> **Nota en Apple Silicon (M1/M2/M3):** macOS puede bloquear la ejecución por "desarrollador no identificado". Andá a **Ajustes del Sistema → Privacidad y Seguridad** y hacé clic en **"Permitir"** junto a la app.

---

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
│   └── icons/                                  # Iconos por plataforma
├── build-linux.sh                              # Build Linux
├── build-windows.bat                           # Build Windows
├── build-macos.sh                              # Build macOS
└── pom.xml                                     # Configuración Maven
```

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

La aplicación expone una API REST bajo el prefijo `/api/`.

### Materias
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/materias` | Listar todas las materias |
| `POST` | `/api/materias` | Crear una materia |
| `PUT` | `/api/materias/{id}` | Actualizar una materia |
| `DELETE` | `/api/materias/{id}` | Eliminar una materia |

### Tareas
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/tareas` | Listar todas las tareas |
| `POST` | `/api/tareas` | Crear una tarea |
| `PUT` | `/api/tareas/{id}` | Actualizar una tarea |
| `DELETE` | `/api/tareas/{id}` | Eliminar una tarea |

### Pomodoro
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/pomodoro/iniciar` | Iniciar una sesión Pomodoro |
| `POST` | `/api/pomodoro/{id}/finalizar` | Finalizar una sesión |
| `POST` | `/api/pomodoro/{id}/cancelar` | Cancelar sesión (guarda tiempo parcial) |
| `GET` | `/api/pomodoro/configuracion` | Obtener configuración del Pomodoro |
| `PUT` | `/api/pomodoro/configuracion` | Actualizar configuración del Pomodoro |

### Estadísticas
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/api/estadisticas/horas` | Horas estudiadas por materia |
| `GET` | `/api/estadisticas/horas/periodo` | Horas por período de tiempo |
| `GET` | `/api/estadisticas/resumen` | Resumen general de estadísticas |

---

## Configuración

La configuración está en `src/main/resources/application.yml`:

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:sqlite:data/gestor-tareas.db    # DB embebida, sin instalar nada
    driver-class-name: org.sqlite.JDBC

pomodoro:
  duracion-trabajo: 25            # minutos
  duracion-descanso: 5            # minutos
  duracion-descanso-largo: 15     # minutos (tras 4 pomodoros)
  pomodoros-para-descanso-largo: 4
```

> La base de datos SQLite se crea automáticamente en la carpeta `data/` al iniciar por primera vez. No requiere configuración adicional.

---

## Desarrollo

```bash
# Ejecutar tests
mvn test

# Compilar sin tests
mvn clean compile

# Empaquetar JAR (sin tests)
mvn clean package -DskipTests

# Ejecutar en modo desarrollo (con hot-reload)
mvn spring-boot:run
```

---

## Resolución de problemas

| Problema | Solución |
|----------|----------|
| `java: command not found` | Instalá Java 21+ y verificá que esté en el PATH (`java -version`) |
| `mvn: command not found` | Instalá Maven 3.8+ y agregalo al PATH |
| `jpackage not found` | Usá un JDK completo (no JRE). jpackage viene incluido desde JDK 14 |
| SmartScreen bloquea en Windows | Clic en "Más información" → "Ejecutar de todas formas" |
| macOS dice "aplicación dañada" | Ve a Ajustes → Privacidad y Seguridad → Permitir |
| Puerto 8080 ocupado | Cambiá el puerto en `application.yml` (`server.port: 8081`) |

---

## Licencia

MIT License — ver [LICENSE](LICENSE) para detalles.

## Autor

**jcruz2005** — [GitHub](https://github.com/jcruz2005)
