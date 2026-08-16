#!/bin/bash
# build-linux.sh - Build nativo para Linux con jpackage + JavaFX wrapper
#
# @author Gestor de Tareas Académicas
# @since 1.0.2

set -e

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TARGET_DIR="$PROJECT_DIR/target"
JAVAFX_VERSION="21.0.1"

echo "=========================================="
echo "  BUILD NATIVO PARA LINUX"
echo "=========================================="
echo ""

# Verificar herramientas
echo "[BUILD] Verificando herramientas..."
java -version 2>&1 | grep -q "21" || { echo "[ERROR] Se requiere Java 21"; exit 1; }
command -v mvn &>/dev/null || { echo "[ERROR] Maven no encontrado"; exit 1; }
command -v jpackage &>/dev/null || { echo "[ERROR] jpackage no encontrado"; exit 1; }
echo "[OK] Todas las herramientas detectadas"
echo ""

# Compilar
echo "[BUILD] Compilando..."
cd "$PROJECT_DIR"
mvn clean package -DskipTests -q
echo "[OK] JAR compilado"

# Crear JAR plano preservando metadata de Spring Boot
echo ""
echo "[BUILD] Creando JAR plano para jpackage..."
PLAIN_JAR="$TARGET_DIR/gestor-tareas-jpackage.jar"
STAGING="$TARGET_DIR/jpackage-staging"
rm -rf "$STAGING" "$PLAIN_JAR"
mkdir -p "$STAGING/app"

echo "[BUILD] Extrayendo fat JAR..."
(cd "$STAGING" && jar xf "$TARGET_DIR/gestor-tareas-1.0.0-SNAPSHOT.jar")
cp -r "$STAGING/BOOT-INF/classes/"* "$STAGING/app/"

echo "[BUILD] Extrayendo dependencias..."
for lib_jar in "$STAGING/BOOT-INF/lib/"*.jar; do
    [ -f "$lib_jar" ] || continue
    EXTRACTION_DIR="$STAGING/tmp_extract"
    rm -rf "$EXTRACTION_DIR"
    mkdir -p "$EXTRACTION_DIR"
    cd "$EXTRACTION_DIR"
    jar xf "$lib_jar" 2>/dev/null || true
    find . -mindepth 1 -maxdepth 1 ! -name "META-INF" -exec cp -r {} "$STAGING/app/" \; 2>/dev/null || true
    if [ -d "META-INF" ]; then
        [ -d "META-INF/spring" ] && mkdir -p "$STAGING/app/META-INF/spring" && cp -r META-INF/spring/* "$STAGING/app/META-INF/spring/" 2>/dev/null || true
        [ -f "META-INF/spring.factories" ] && mkdir -p "$STAGING/app/META-INF" && cp META-INF/spring.factories "$STAGING/app/META-INF/spring.factories" 2>/dev/null || true
    fi
    cd "$STAGING"
    rm -rf "$EXTRACTION_DIR"
done

mkdir -p "$STAGING/app/META-INF"
cat > "$STAGING/app/META-INF/MANIFEST.MF" << 'MANIFEST'
Manifest-Version: 1.0
Main-Class: com.academic.gestor.NativeLauncher
MANIFEST

cd "$STAGING/app"
jar cfm "$PLAIN_JAR" META-INF/MANIFEST.MF .
cd "$PROJECT_DIR"

rm -rf "$STAGING"

echo "[VERIFY] Verificando metadata..."
jar tf "$PLAIN_JAR" | grep -q "AutoConfiguration.imports" && echo "[OK] AutoConfiguration.imports" || echo "[WARN] AutoConfiguration.imports NO encontrado"
jar tf "$PLAIN_JAR" | grep -q "spring.factories" && echo "[OK] spring.factories" || echo "[WARN] spring.factories NO encontrado"
echo "[OK] JAR plano creado"

# Crear jpackage input con JavaFX
echo ""
echo "[BUILD] Preparando jpackage..."
JPACKAGE_INPUT="$TARGET_DIR/jpackage-input"
rm -rf "$JPACKAGE_INPUT"
mkdir -p "$JPACKAGE_INPUT"
cp "$PLAIN_JAR" "$JPACKAGE_INPUT/"

# Copiar JavaFX Linux JARs
JAVAFX_COUNT=0
for jar in $(find ~/.m2/repository/org/openjfx -name "*${JAVAFX_VERSION}*linux*.jar" -type f 2>/dev/null); do
    cp "$jar" "$JPACKAGE_INPUT/"
    JAVAFX_COUNT=$((JAVAFX_COUNT + 1))
done
for jar in $(find ~/.m2/repository/org/openjfx -name "javafx-*${JAVAFX_VERSION}.jar" -not -name "*linux*" -not -name "*sources*" -not -name "*javadoc*" -type f 2>/dev/null); do
    cp "$jar" "$JPACKAGE_INPUT/"
done
echo "[OK] JavaFX copiado ($JAVAFX_COUNT JARs con nativas)"

# Crear app image
echo "[BUILD] Creando app image..."
rm -rf "$TARGET_DIR/installers"
jpackage \
    --type app-image \
    --name "GestorTareasAcademicas" \
    --dest "$TARGET_DIR/installers" \
    --input "$JPACKAGE_INPUT" \
    --main-class com.academic.gestor.NativeLauncher \
    --main-jar "gestor-tareas-jpackage.jar" \
    --java-options "-Dspring.profiles.active=native" \
    --java-options "-Xmx512m" \
    --app-version "1.0.0" \
    --vendor "Academic" \
    --description "Gestor de Tareas Académicas con Pomodoro" 2>&1

rm -rf "$JPACKAGE_INPUT"

# Crear wrapper shell
echo "[BUILD] Creando launcher wrapper..."

APPDIR="$TARGET_DIR/installers/GestorTareasAcademicas/lib/app"
RUNTIME_DIR="$TARGET_DIR/installers/GestorTareasAcademicas/lib/runtime"
LAUNCHER_BIN="$TARGET_DIR/installers/GestorTareasAcademicas/bin/GestorTareasAcademicas"

mv "$LAUNCHER_BIN" "$LAUNCHER_BIN.jpackage-bin" 2>/dev/null || true

cat > "$LAUNCHER_BIN" << 'WRAPPER'
#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APPDIR="$SCRIPT_DIR/../lib/app"
RUNTIME="$SCRIPT_DIR/../lib/runtime"

MODULE_PATH="$APPDIR/javafx-base-JAVAFX_VER-linux.jar:$APPDIR/javafx-graphics-JAVAFX_VER-linux.jar:$APPDIR/javafx-controls-JAVAFX_VER-linux.jar:$APPDIR/javafx-media-JAVAFX_VER-linux.jar:$APPDIR/javafx-web-JAVAFX_VER-linux.jar"

if [ -f "$RUNTIME/bin/java" ]; then
    JAVA_CMD="$RUNTIME/bin/java"
else
    JAVA_CMD="java"
fi

exec "$JAVA_CMD" \
    --module-path "$MODULE_PATH" \
    --add-modules javafx.controls,javafx.web,javafx.media \
    -cp "$APPDIR/gestor-tareas-jpackage.jar" \
    -Djava.library.path="$APPDIR" \
    -Dspring.profiles.active=native \
    -Xmx512m \
    com.academic.gestor.NativeLauncher "$@"
WRAPPER

# Reemplazar placeholder con versión real
sed -i "s/JAVAFX_VER/$JAVAFX_VERSION/g" "$LAUNCHER_BIN"
chmod +x "$LAUNCHER_BIN"

echo ""
echo "=========================================="
echo "  BUILD COMPLETADO"
echo "=========================================="

# Accesos directos
echo "[BUILD] Creando accesos directos..."
DESKTOP_NAME="GestorTareasAcademicas"
ICON_FILE=""
[ -f "$PROJECT_DIR/native/icons/app-icon.png" ] && ICON_FILE="$PROJECT_DIR/native/icons/app-icon.png"

DESKTOP_CONTENT="[Desktop Entry]
Name=Gestor de Tareas Académicas
Comment=Gestor de Tareas Académicas con Pomodoro y Análisis de Horas
Exec=$LAUNCHER_BIN
Icon=$ICON_FILE
Type=Application
Categories=Education;Productivity;
Terminal=false
StartupNotify=true
Version=1.0.0
"

[ -d "$HOME/Desktop" ] && echo "$DESKTOP_CONTENT" > "$HOME/Desktop/$DESKTOP_NAME.desktop" && chmod +x "$HOME/Desktop/$DESKTOP_NAME.desktop" && echo "[OK] Escritorio"
mkdir -p "$HOME/.local/share/applications"
echo "$DESKTOP_CONTENT" > "$HOME/.local/share/applications/$DESKTOP_NAME.desktop"
echo "[OK] Menú de aplicaciones"
update-desktop-database "$HOME/.local/share/applications" 2>/dev/null || true

echo ""
echo "[INFO] Ejecutar: $LAUNCHER_BIN"
echo ""
