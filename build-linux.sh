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

# Versión de JavaFX (debe coincidir con pom.xml)
JAVAFX_VERSION="21.0.1"

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

# Generar el fat JAR
mvn clean package -DskipTests -q

if [ $? -ne 0 ]; then
    echo "[ERROR] Error al compilar el proyecto"
    exit 1
fi
echo "[OK] Fat JAR compilado exitosamente"

# Crear JAR plano (sin BOOT-INF) para jpackage
echo "[BUILD] Creando JAR plano para jpackage..."
PLAIN_JAR="$TARGET_DIR/gestor-tareas-jpackage.jar"
mkdir -p "$TARGET_DIR/jpackage-staging"
cd "$TARGET_DIR/jpackage-staging"

# Extraer clases de la aplicación y dependencias del fat JAR
jar xf "$TARGET_DIR/gestor-tareas-1.0.0-SNAPSHOT.jar" BOOT-INF/classes/ BOOT-INF/lib/

# Crear directorio plano con clases de la app
mkdir -p jpackage-contents
cp -r BOOT-INF/classes/* jpackage-contents/

# Extraer TODOS los JARs de BOOT-INF/lib/ y mezclar sus clases en el JAR plano
echo "[BUILD] Extrayendo dependencias de BOOT-INF/lib/..."
mkdir -p BOOT-INF/lib-extracted
cp BOOT-INF/lib/*.jar BOOT-INF/lib-extracted/ 2>/dev/null || true
for lib_jar in BOOT-INF/lib-extracted/*.jar; do
    if [ -f "$lib_jar" ]; then
        EXTRACTION_DIR="BOOT-INF/lib-extracted/$(basename "$lib_jar" .jar)"
        mkdir -p "$EXTRACTION_DIR"
        cd "$EXTRACTION_DIR"
        jar xf "$OLDPWD/$lib_jar" 2>/dev/null || true
        # Copiar clases (directorios y archivos)
        find . -mindepth 1 -maxdepth 1 ! -name "META-INF" -exec cp -r {} "$OLDPWD/jpackage-contents/" \;
        # Copiar metadata de Spring (spring.factories, etc.) mezclando
        if [ -d "META-INF" ]; then
            mkdir -p "$OLDPWD/jpackage-contents/META-INF"
            find META-INF -type f ! -name "MANIFEST.MF" -exec cp --parents {} "$OLDPWD/jpackage-contents/" \; 2>/dev/null || true
        fi
        cd "$OLDPWD"
    fi
done

# Generar MANIFEST.MF
mkdir -p jpackage-contents/META-INF
cat > jpackage-contents/META-INF/MANIFEST.MF << 'MANIFEST'
Manifest-Version: 1.0
Main-Class: com.academic.gestor.NativeLauncher
MANIFEST

cd jpackage-contents
jar cfm "$PLAIN_JAR" META-INF/MANIFEST.MF .
cd "$PROJECT_DIR"

# Limpiar staging temporal
rm -rf "$TARGET_DIR/jpackage-staging"
echo "[OK] JAR plano creado: gestor-tareas-jpackage.jar"

echo ""

# Crear app image con jpackage
echo "[BUILD] Creando app image para Linux..."
rm -rf "$TARGET_DIR/installers"
mkdir -p "$TARGET_DIR/installers"

# Determinar el nombre del JAR plano
JAR_NAME="$TARGET_DIR/gestor-tareas-jpackage.jar"
if [ ! -f "$JAR_NAME" ]; then
    echo "[ERROR] No se encontró el JAR plano en $JAR_NAME"
    exit 1
fi
JAR_BASENAME=$(basename "$JAR_NAME")

echo "[INFO] Usando JAR: $JAR_BASENAME"

# Crear directorio de entrada aislado para jpackage
JPACKAGE_INPUT="$TARGET_DIR/jpackage-input"
rm -rf "$JPACKAGE_INPUT"
mkdir -p "$JPACKAGE_INPUT"
cp "$JAR_NAME" "$JPACKAGE_INPUT/"

# Verificar si existe el icono
ICON_PATH=""
if [ -f "$PROJECT_DIR/native/icons/app-icon.png" ]; then
    ICON_PATH="--icon $PROJECT_DIR/native/icons/app-icon.png"
    echo "[INFO] Icono encontrado: app-icon.png"
else
    echo "[WARN] No se encontró icono, se usará el predeterminado"
fi

# Copiar SOLO los JARs de JavaFX de la versión correcta (sin duplicados)
echo "[BUILD] Copiando JavaFX v${JAVAFX_VERSION} al directorio de entrada..."
JAVAFX_JARS=$(find ~/.m2/repository/org/openjfx -name "*-${JAVAFX_VERSION}.jar" \
    -not -name "*sources*" -not -name "*javadoc*" \
    -not -name "*win*" -not -name "*mac*" 2>/dev/null)

if [ -z "$JAVAFX_JARS" ]; then
    echo "[WARN] No se encontraron JARs de JavaFX v${JAVAFX_VERSION} en .m2, buscando en classpath..."
    JAVAFX_JARS=$(mvn dependency:build-classpath -q -DincludeScope=runtime -Dmdep.outputFile=/dev/stdout 2>/dev/null | tr ':' '\n' | grep "openjfx.*${JAVAFX_VERSION}")
fi

if [ -z "$JAVAFX_JARS" ]; then
    echo "[ERROR] No se encontraron dependencias de JavaFX v${JAVAFX_VERSION}"
    exit 1
fi

# Copiar cada JAR de JavaFX al directorio de entrada
JAVAFX_COUNT=0
while IFS= read -r jar; do
    cp "$jar" "$JPACKAGE_INPUT/"
    JAVAFX_COUNT=$((JAVAFX_COUNT + 1))
done <<< "$JAVAFX_JARS"
echo "[OK] JavaFX copiado ($JAVAFX_COUNT JARs, versión $JAVAFX_VERSION)"

echo "[BUILD] Creando app image para Linux..."
jpackage \
    --type app-image \
    --name "GestorTareasAcademicas" \
    --dest "$TARGET_DIR/installers" \
    --input "$JPACKAGE_INPUT" \
    --main-class com.academic.gestor.NativeLauncher \
    --main-jar "$JAR_BASENAME" \
    $ICON_PATH \
    --java-options "-Dspring.profiles.active=native" \
    --java-options "-Xmx512m" \
    --app-version "1.0.0" \
    --vendor "Academic" \
    --description "Gestor de Tareas Académicas con Pomodoro"

# Limpiar directorio de entrada aislado
rm -rf "$JPACKAGE_INPUT"

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

# Crear acceso directo (.desktop) en el escritorio y en el menú de aplicaciones
echo ""
echo "[BUILD] Creando accesos directos..."

APP_BIN="$TARGET_DIR/installers/GestorTareasAcademicas/bin/GestorTareasAcademicas"
DESKTOP_NAME="GestorTareasAcademicas"

# Buscar icono para el .desktop (preferir PNG)
ICON_FILE=""
if [ -f "$PROJECT_DIR/native/icons/app-icon.png" ]; then
    ICON_FILE="$PROJECT_DIR/native/icons/app-icon.png"
fi

# Contenido del archivo .desktop
DESKTOP_CONTENT="[Desktop Entry]
Name=Gestor de Tareas Académicas
Comment=Gestor de Tareas Académicas con Pomodoro y Análisis de Horas
Exec=$APP_BIN
Icon=$ICON_FILE
Type=Application
Categories=Education;Productivity;
Terminal=false
StartupNotify=true
Version=1.0.0
"

# Crear en el escritorio del usuario
DESKTOP_DIR="$HOME/Desktop"
if [ -d "$DESKTOP_DIR" ]; then
    echo "$DESKTOP_CONTENT" > "$DESKTOP_DIR/$DESKTOP_NAME.desktop"
    chmod +x "$DESKTOP_DIR/$DESKTOP_NAME.desktop"
    # En algunas distribuciones, el .desktop necesita ser confiable
    gio set "$DESKTOP_DIR/$DESKTOP_NAME.desktop" "metadata::trusted" true 2>/dev/null || true
    echo "[OK] Acceso directo creado en: $DESKTOP_DIR/$DESKTOP_NAME.desktop"
else
    echo "[WARN] No se encontró el directorio del escritorio: $DESKTOP_DIR"
fi

# Crear en el menú de aplicaciones
MENU_DIR="$HOME/.local/share/applications"
mkdir -p "$MENU_DIR"
echo "$DESKTOP_CONTENT" > "$MENU_DIR/$DESKTOP_NAME.desktop"
echo "[OK] Acceso directo creado en el menú de aplicaciones: $MENU_DIR/$DESKTOP_NAME.desktop"

# Actualizar caché de iconos (opcional)
update-desktop-database "$MENU_DIR" 2>/dev/null || true

echo ""
echo "[INFO] Para ejecutar desde el menú: buscar 'Gestor de Tareas Académicas'"
echo "[INFO] Para ejecutar desde terminal: $APP_BIN"
echo ""
