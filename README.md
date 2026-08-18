# OKtask

Gestor de tareas con Pomodoro y análisis de minutos de estudio.

[![Release](https://img.shields.io/badge/Release-v1.2.0-blue.svg)](https://github.com/jcruz2005/Oktask/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](#licencia)
[![Java 21](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot 3.3](https://img.shields.io/badge/Spring%20Boot-3.3.2-green.svg)](https://spring.io/projects/spring-boot)

## Características

- **Gestión de Materias**: Crear, editar y eliminar materias con colores y prioridades
- **Gestión de Tareas**: Crear tareas con fechas límite y estados (Pendiente, En Progreso, Completada)
- **Temporizador Pomodoro**: Sesiones de estudio configurables con descansos
- **Estadísticas**: Gráfico de minutos estudiados por materia y período
- **Exportación**: Exportar datos en formato CSV y JSON
- **Modo Oscuro**: Soporte para tema claro y oscuro
- **Drag & Drop**: Reordenar tareas con arrastrar y soltar
- **Persistencia**: Base de datos SQLite que conserva datos entre sesiones
- **Actualizaciones**: Sistema de notificación y descarga de actualizaciones

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

### Opción 1 — Descargar desde GitHub Releases (recomendado)

La forma más rápida. No necesitás tener Java ni Maven instalado.

1. Andá a [Releases](https://github.com/jcruz2005/Oktask/releases)
2. Descargá el archivo para tu sistema operativo:

| Sistema operativo | Archivo |
|---|---|
| **Linux** (Ubuntu, Fedora, Arch) | `OKtask-1.2.0-linux-x64.tar.gz` |
| **Windows** (10, 11) | `OKtask-1.2.0.msi` |
| **macOS** (Ventura, Sonoma, Sequoia) | `OKtask-1.2.0.dmg` |

3. Seguí las instrucciones de instalación para tu SO:

#### Linux
```bash
tar -xzf OKtask-1.2.0-linux-x64.tar.gz
sudo cp -r OKtask /opt/
sudo ln -sf /opt/OKtask/bin/OKtask /usr/bin/oktask
oktask
```

#### Windows
Doble clic en `OKtask-1.2.0.msi` y seguir el asistente.

#### macOS
Abrir el `.dmg` y arrastrar OKtask a Applications.

---

### Opción 2 — Ejecutar desde el código fuente

Si querés modificar el código o contribuir al desarrollo.

#### Requisitos previos

| Requisito | Versión mínima | Cómo verificar |
|---|---|---|
| **Java (JDK)** | 21 o superior | `java -version` |
| **Maven** | 3.8+ | `mvn --version` |

> **No necesitás instalar SQLite** — la base de datos se crea automáticamente al iniciar.

#### Clonar y ejecutar

```bash
git clone https://github.com/jcruz2005/Oktask.git
cd Oktask
mvn spring-boot:run
```

La aplicación estará disponible en **http://localhost:8080**

#### Generar el JAR ejecutable (opcional)

```bash
mvn clean package -DskipTests
java -jar target/oktask-1.2.0.jar
```

---

### Opción 3 — Aplicación nativa con jpackage

Los scripts de build crean una **aplicación nativa** con su propio runtime de Java incrustado. El usuario final no necesita tener Java instalado.

#### Requisitos previos para compilar

| Requisito | Versión mínima | Notas |
|---|---|---|
| **JDK** | 21 o superior | Debe incluir `jpackage` (incluido desde JDK 14+) |
| **Maven** | 3.8+ | |
| **Xcode CLI Tools** | — | Solo en macOS (`xcode-select --install`) |

---

#### Linux

**Distribuciones probadas:** Ubuntu 22.04+, Fedora 38+, Arch Linux, CachyOS

##### Instalar Java 21 (si no lo tenés)

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

##### Compilar

```bash
git clone https://github.com/jcruz2005/Oktask.git
cd Oktask
chmod +x build-linux.sh
./build-linux.sh
```

##### Ejecutar

```bash
oktask
```

O directamente:
```bash
./target/installers/OKtask/bin/OKtask
```

---

#### Windows

**Versiones probadas:** Windows 10 (22H2+), Windows 11

##### Instalar Java 21 (si no lo tenés)

1. Descargá el instalador desde [Adoptium](https://adoptium.net/temurin/releases/?version=21) (recomendado)
2. Durante la instalación, marcá **"Add to PATH"** y **"Set JAVA_HOME"**
3. Reiniciá la terminal

También necesitás [Maven](https://maven.apache.org/download.cgi) — descargar, descomprimir y agregar la carpeta `bin` al PATH.

##### Compilar

Abrí **Command Prompt** o **PowerShell**:

```cmd
git clone https://github.com/jcruz2005/Oktask.git
cd Oktask
build-windows.bat
```

##### Ejecutar

```cmd
target\installers\OKtask\OKtask.exe
```

> **Nota:** Windows puede mostrar una alerta de SmartScreen la primera vez. Hacé clic en **"Más información" → "Ejecutar de todas formas"**.

---

#### macOS

**Versiones probadas:** macOS Ventura (13+), Sonoma (14+), Sequoia (15+)

##### Instalar Java 21 (si no lo tenés)

**Con Homebrew** (recomendado):
```bash
brew install openjdk@21 maven
```

**Manual:** Descargá el `.pkg` desde [Adoptium](https://adoptium.net/temurin/releases/?version=21)

##### Instalar Xcode Command Line Tools

```bash
xcode-select --install
```

##### Compilar

```bash
git clone https://github.com/jcruz2005/Oktask.git
cd Oktask
chmod +x build-macos.sh
./build-macos.sh
```

##### Ejecutar

```bash
open target/installers/OKtask.app
```

---

## Base de datos

La aplicación usa **SQLite** (base de datos embebida) que se crea automáticamente:

| Entorno | Ubicación |
|---|---|
| Desarrollo (`mvn spring-boot:run`) | `data/oktask.db` |
| App nativa | `~/.oktask/data/oktask.db` |

**Importante:** La app nativa usa `ddl-auto: update`, por lo que tus materias, tareas y horas de estudio se preservan entre actualizaciones de la aplicación.

---

## Estructura del Proyecto

```
Oktask/
├── src/
│   ├── main/
│   │   ├── java/com/academic/gestor/
│   │   │   ├── GestorTareasApplication.java    # Clase principal (Spring Boot)
│   │   │   ├── NativeLauncher.java             # Launcher JavaFX para app nativa
│   │   │   ├── application/                    # Capa de aplicación (servicios, DTOs)
│   │   │   ├── domain/                         # Dominio (entidades, repositorios)
│   │   │   ├── infrastructure/                 # Infraestructura (JPA, controllers)
│   │   │   ├── update/                         # Sistema de actualizaciones
│   │   │   └── shared/                         # Utilidades compartidas
│   │   └── resources/
│   │       ├── application.yml                 # Configuración (dev)
│   │       ├── application-native.yml          # Configuración (app nativa)
│   │       └── static/                         # Frontend (HTML, CSS, JS)
│   └── test/                                   # Tests unitarios
├── native/
│   └── icons/                                  # Iconos por plataforma
├── .github/workflows/release.yml               # Build multiplataforma automático
├── version.json                                # Info de versiones para actualizaciones
├── build-linux.sh                              # Build para Linux
├── build-windows.bat                           # Build para Windows
├── build-macos.sh                              # Build para macOS
└── pom.xml                                     # Configuración Maven
```

---

## Actualizaciones

La aplicación verifica automáticamente si hay nuevas versiones disponibles. Cuando hay una actualización, aparece un botón 🔄 en el header.

### Para desarrolladores — Publicar una actualización

```bash
# 1. Subir el código
git add -A
git commit -m "feat: nueva función"
git push

# 2. Crear el tag (esto activa el build multiplataforma)
git tag -a v1.3.0 -m "v1.3.0"
git push origin v1.3.0
```

GitHub Actions buildea automáticamente para Linux, Windows y macOS, y sube los 3 instaladores a la Release.

---

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
| `GET` | `/api/estadisticas/horas` | Minutos estudiados por materia |
| `GET` | `/api/estadisticas/horas/periodo` | Minutos por período de tiempo |
| `GET` | `/api/estadisticas/resumen` | Resumen general de estadísticas |

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
| App nativa no inicia | Verificá que `java -version` muestre Java 21+ |
| `oktask` no funciona en terminal | Ejecutá `source ~/.bashrc` o abrí una nueva terminal |

---

## Licencia

MIT License — ver [LICENSE](LICENSE) para detalles.

## Autor

**jcruz2005** — [GitHub](https://github.com/jcruz2005)
