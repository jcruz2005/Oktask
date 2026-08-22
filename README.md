# OKtask

Gestor de tareas con Pomodoro y análisis de minutos de estudio.

[![Release](https://img.shields.io/badge/Release-v1.2.11-blue.svg)](https://github.com/jcruz2005/Oktask/releases/latest)
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
- **Actualizaciones automáticas**: El sistema detecta nuevas versiones y las instala sin intervención del usuario

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

## Instalación (usuario final)

No necesitás tener Java ni Maven instalado. Descargá el archivo para tu sistema operativo desde [Releases](https://github.com/jcruz2005/Oktask/releases/latest) y seguí los pasos de abajo.

> **Actualizaciones:** La app detecta automáticamente nuevas versiones. Cuando haya una, aparece un botón de actualización que descarga e instala todo solo.

---

### Linux

**Archivos probados:** Ubuntu 22.04+, Fedora 38+, Arch Linux, CachyOS

**Archivo a descargar:** `OKtask-1.2.11-linux-x64.tar.gz`

#### Instalación automática (desde la app)

Si ya tenés OKtask instalado, la app detecta la actualización y la instala sola en `~/.local/share/oktask/`. Solo tenés que reiniciar.

#### Instalación manual

```bash
# 1. Descargá el archivo desde releases
# https://github.com/jcruz2005/Oktask/releases/latest

# 2. Extraé el archivo
tar -xzf OKtask-1.2.11-linux-x64.tar.gz

# 3. Mové a una ubicación permanente
mkdir -p ~/.local/share
mv OKtask ~/.local/share/oktask-app

# 4. Creá un symlink para ejecutar desde terminal (opcional)
mkdir -p ~/.local/bin
ln -sf ~/.local/share/oktask-app/bin/OKtask ~/.local/bin/oktask

# 5. Ejecutá
~/.local/share/oktask-app/bin/OKtask
# o si creaste el symlink:
oktask
```

> **Nota:** Si `oktask` no se reconoce en la terminal, cerrá y abrí una nueva terminal, o ejecutá `source ~/.bashrc`.

---

### Windows

**Versiones probadas:** Windows 10 (22H2+), Windows 11

**Archivo a descargar:** `OKtask-1.2.11-windows-x64.zip`

> **Nota:** La versión de Windows puede no estar disponible en releases aún. Si no está, compilá desde el código fuente (ver [Instalación como desarrollador](#instalación-como-desarrollador)).

#### Instalación manual

```powershell
# 1. Descargá el archivo desde releases
# https://github.com/jcruz2005/Oktask/releases/latest

# 2. Extraé el .zip en la ubicación que prefieras
# Por ejemplo: C:\Users\<tu-usuario>\AppData\Local\OKtask\

# 3. Ejecutá OKtask.exe desde la carpeta extraída
# Crea un acceso directo en el escritorio si lo preferís
```

#### Desde la app (si ya tenés una versión instalada)

La app detecta la actualización, la descarga, la extrae en `%LOCALAPPDATA%\OKtask\` y crea un acceso directo en el escritorio y en el Menú Inicio.

> **Windows SmartScreen:** Puede mostrar una alerta la primera vez. Hacé clic en **"Más información" → "Ejecutar de todas formas"**.

---

### macOS

**Versiones probadas:** macOS Ventura (13+), Sonoma (14+), Sequoia (15+)

**Archivo a descargar:** `OKtask-1.2.11-macos-arm64.zip`

#### Instalación manual

```bash
# 1. Descargá el archivo desde releases
# https://github.com/jcruz2005/Oktask/releases/latest

# 2. Extraé el .zip
unzip OKtask-1.2.11-macos-arm64.zip -d ~/Applications/

# 3. Abrí desde el Finder o terminal
open ~/Applications/OKtask.app
```

#### Desde la app (si ya tenés una versión instalada)

La app detecta la actualización, la extrae en `~/Applications/OKtask/` y copia el `.app` al escritorio.

> **Gatekeeper:** Si macOS dice "aplicación dañada", andá a **Ajustes → Privacidad y Seguridad → Permitir**. La app elimina automáticamente el atributo quarantine al instalarse.

---

### Android

**Archivo a descargar:** `OKtask-1.2.11-android.apk`

#### Instalación

1. Descargá el APK desde [Releases](https://github.com/jcruz2005/Oktask/releases/latest)
2. Abrilo desde la notificación o el administrador de archivos
3. Si pide permiso, activá **"Fuentes desconocidas"** en la configuración de seguridad
4. Tocá **"Instalar"**

#### Desde la app (si ya tenés una versión instalada)

La app detecta la actualización y descarga el nuevo APK directamente. Al finalizar la descarga, Android te pregunta si querés instalarlo.

---

## Instalación como desarrollador

Si querés modificar el código, contribuir al desarrollo, o simplemente ejecutar desde el código fuente.

### Requisitos previos

| Requisito | Versión mínima | Cómo verificar |
|---|---|---|
| **Java (JDK)** | 21 o superior | `java -version` |
| **Maven** | 3.8+ | `mvn --version` |
| **Git** | cualquier versión | `git --version` |

> **No necesitás instalar SQLite** — la base de datos se crea automáticamente al iniciar.

---

### Instalar Java 21 y Maven

#### Ubuntu / Debian
```bash
sudo apt update
sudo apt install openjdk-21-jdk maven
```

#### Fedora / RHEL
```bash
sudo dnf install java-21-openjdk-devel maven
```

#### Arch Linux
```bash
sudo pacman -S jdk21-openjdk maven
```

#### Windows
1. Descargá el instalador desde [Adoptium](https://adoptium.net/temurin/releases/?version=21)
2. Durante la instalación, marcá **"Add to PATH"** y **"Set JAVA_HOME"**
3. Reiniciá la terminal
4. Descargá [Maven](https://maven.apache.org/download.cgi), descomprimí y agregá la carpeta `bin` al PATH

#### macOS
```bash
brew install openjdk@21 maven
```

---

### Ejecutar en modo desarrollo

```bash
git clone https://github.com/jcruz205/Oktask.git
cd Oktask
mvn spring-boot:run
```

La aplicación estará disponible en **http://localhost:8080**

Los datos se guardan en `data/oktask.db` dentro del proyecto.

---

### Generar el JAR ejecutable

```bash
mvn clean package -DskipTests
java -jar target/oktask-1.2.0.jar
```

---

### Compilar app nativa (sin necesidad de Java instalado)

Los scripts de build crean una aplicación nativa con su propio runtime de Java incrustado.

#### Linux
```bash
chmod +x build-linux.sh
./build-linux.sh
```
Resultado en `target/installers/OKtask/bin/OKtask`

#### Windows (PowerShell)
```cmd
build-windows.bat
```
Resultado en `target\installers\OKtask\OKtask.exe`

#### macOS
```bash
chmod +x build-macos.sh
./build-macos.sh
```
Resultado en `target/installers/OKtask.app`

> **Requisito extra para macOS:** `xcode-select --install`

---

### Publicar una nueva versión

```bash
# 1. Actualizar la versión en estos archivos:
#    - src/main/resources/static/js/update.js (mobile overrides)
#    - version.json
#    - src/main/java/com/academic/gestor/infrastructure/web/controllers/UpdateController.java
#    - src/main/java/com/academic/gestor/NativeLauncher.java

# 2. Subir el código
git add -A
git commit -m "feat: nueva función"
git push

# 3. Crear el tag (esto activa el build multiplataforma)
git tag -a v1.3.0 -m "v1.3.0"
git push origin v1.3.0
```

GitHub Actions buildea automáticamente para Linux, macOS y Android, y sube los instaladores a la Release.

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
├── mobile/                                     # App Android/iOS (Capacitor)
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
| La app no detecta actualizaciones | Verificá tu conexión a internet. La app consulta `raw.githubusercontent.com` |

---

## Licencia

MIT License — ver [LICENSE](LICENSE) para detalles.

## Autor

**jcruz2005** — [GitHub](https://github.com/jcruz2005)
