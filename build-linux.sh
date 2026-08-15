#!/bin/bash
# build-linux.sh - Script de build nativo para Linux
#
# Este script compila la aplicación Spring Boot y crea una imagen
# de aplicación nativa usando jpackage para distribución en Linux.
#
# Requisitos:
# - Java 21 o superior
# - Maven 3.8+
# - jpackage (incluido en JDK 14+)
#
# Uso:
#   chmod +x build-linux.sh
#   ./build-linux.sh
#
# @author Gestor de Tareas Académicas
# @since 1.0.0

set -e

# Configuración de rutas
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$SCRIPT_DIR"
TARGET_DIR="$PROJECT_DIR/target"

echo "=========================================="
echo "  BUILD NATIVO PARA LINUX"
echo "=========================================="
echo ""

# Verificar herramientas necesarias
echo "[BUILD] Verificando herramientas..."

# Verificar Java 21
if ! java -version 2>&1 | grep -q "21"; then
    echo "[ERROR] Se requiere Java 21 o superior"
    echo "[INFO] Versión actual:"
    java -version 2>&1
    exit 1
fi
echo "[OK] Java 21 detectado"

# Verificar Maven
if ! command -v mvn &> /dev/null; then
    echo "[ERROR] Maven no encontrado en el PATH"
    exit 1
fi
echo "[OK] Maven detectado"

# Verificar jpackage
if ! command -v jpackage &> /dev/null; then
    echo "[ERROR] jpackage no encontrado. Asegúrese de usar JDK 14+"
    exit 1
fi
echo "[OK] jpackage detectado"

echo ""

# Compilar JAR
echo "[BUILD] Compilando JAR con Maven..."
cd "$PROJECT_DIR"
mvn clean package -Dmaven.test.skip=true -q

if [ $? -ne 0 ]; then
    echo "[ERROR] Error al compilar el proyecto"
    exit 1
fi
echo "[OK] JAR compilado exitosamente"

echo ""

# Crear app image con jpackage
echo "[BUILD] Creando app image para Linux..."
mkdir -p "$TARGET_DIR/installers"

# Determinar el nombre del JAR compilado
JAR_NAME=$(ls -1 "$TARGET_DIR"/gestor-tareas-*.jar 2>/dev/null | head -1)
if [ -z "$JAR_NAME" ]; then
    echo "[ERROR] No se encontró el JAR compilado en $TARGET_DIR"
    exit 1
fi
JAR_BASENAME=$(basename "$JAR_NAME")

echo "[INFO] Usando JAR: $JAR_BASENAME"

# Verificar si existe el icono
ICON_PATH=""
if [ -f "$PROJECT_DIR/native/icons/app-icon.png" ]; then
    ICON_PATH="--icon $PROJECT_DIR/native/icons/app-icon.png"
    echo "[INFO] Icono encontrado: app-icon.png"
else
    echo "[WARN] No se encontró icono, se usará el predeterminado"
fi

jpackage \
    --type app-image \
    --name "GestorTareasAcademicas" \
    --dest "$TARGET_DIR/installers" \
    --input "$TARGET_DIR" \
    --main-class com.academic.gestor.NativeLauncher \
    --main-jar "$JAR_BASENAME" \
    $ICON_PATH \
    --java-options "-Dspring.profiles.active=native" \
    --java-options "-Xmx512m" \
    --app-version "1.0.0" \
    --vendor "Academic" \
    --description "Gestor de Tareas Académicas con Pomodoro"

if [ $? -ne 0 ]; then
    echo "[ERROR] Error al crear la app image"
    exit 1
fi

echo ""
echo "=========================================="
echo "  BUILD COMPLETADO EXITOSAMENTE"
echo "=========================================="
echo "[INFO] App image creada en: $TARGET_DIR/installers/"
echo "[INFO] Para ejecutar: $TARGET_DIR/installers/GestorTareasAcademicas/bin/GestorTareasAcademicas"
echo ""

# Mostrar contenido del directorio de instaladores
echo "[INFO] Contenido del directorio de instaladores:"
ls -la "$TARGET_DIR/installers/" 2>/dev/null || echo "  (directorio vacío)"
