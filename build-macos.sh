#!/bin/bash
# build-macos.sh - Build nativo para macOS con jpackage
#
# @author Gestor de Tareas Académicas
# @since 1.1.0

set -e

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TARGET_DIR="$PROJECT_DIR/target"
JAVAFX_VERSION="21.0.1"
APP_VERSION="1.2.0"

echo "=========================================="
echo "  BUILD NATIVO PARA macOS"
echo "=========================================="
echo ""

# Verificar que es macOS
if [ "$(uname)" != "Darwin" ]; then
    echo "[ERROR] Este script solo puede ejecutarse en macOS"
    exit 1
fi

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
PLAIN_JAR="$TARGET_DIR/oktask-jpackage.jar"
STAGING="$TARGET_DIR/jpackage-staging"
rm -rf "$STAGING" "$PLAIN_JAR"
mkdir -p "$STAGING/app"

echo "[BUILD] Extrayendo fat JAR..."
(cd "$STAGING" && jar xf "$TARGET_DIR/oktask-$APP_VERSION.jar")
cp -r "$STAGING/BOOT-INF/classes/"* "$STAGING/app/"

echo "[BUILD] Extrayendo dependencias (excluyendo JavaFX)..."
for lib_jar in "$STAGING/BOOT-INF/lib/"*.jar; do
    [ -f "$lib_jar" ] || continue
    if echo "$(basename "$lib_jar")" | grep -q "^javafx-"; then
        echo "[SKIP] $(basename "$lib_jar") → module-path"
        continue
    fi
    EXTRACTION_DIR="$STAGING/tmp_extract"
    rm -rf "$EXTRACTION_DIR"
    mkdir -p "$EXTRACTION_DIR"
    cd "$EXTRACTION_DIR"
    jar xf "$lib_jar" 2>/dev/null || true
    find . -mindepth 1 -maxdepth 1 ! -name "META-INF" -exec cp -r {} "$STAGING/app/" \; 2>/dev/null || true
    if [ -d "META-INF" ]; then
        [ -d "META-INF/spring" ] && mkdir -p "$STAGING/app/META-INF/spring" && cp -r META-INF/spring/* "$STAGING/app/META-INF/spring/" 2>/dev/null || true
        if [ -f "META-INF/spring.factories" ]; then
            mkdir -p "$STAGING/app/META-INF"
            if [ -f "$STAGING/app/META-INF/spring.factories" ]; then
                cat "$STAGING/app/META-INF/spring.factories" > "$STAGING/app/META-INF/spring.factories.merged"
                while IFS= read -r line; do
                    if ! grep -qF "$line" "$STAGING/app/META-INF/spring.factories.merged" 2>/dev/null; then
                        echo "$line" >> "$STAGING/app/META-INF/spring.factories.merged"
                    fi
                done < META-INF/spring.factories
                mv "$STAGING/app/META-INF/spring.factories.merged" "$STAGING/app/META-INF/spring.factories"
            else
                cp META-INF/spring.factories "$STAGING/app/META-INF/spring.factories"
            fi
        fi
        if [ -d "META-INF/services" ]; then
            mkdir -p "$STAGING/app/META-INF/services"
            for svc_file in META-INF/services/*; do
                [ -f "$svc_file" ] || continue
                svc_name=$(basename "$svc_file")
                if [ -f "$STAGING/app/META-INF/services/$svc_name" ]; then
                    while IFS= read -r line; do
                        if ! grep -qF "$line" "$STAGING/app/META-INF/services/$svc_name" 2>/dev/null; then
                            echo "$line" >> "$STAGING/app/META-INF/services/$svc_name"
                        fi
                    done < "$svc_file"
                else
                    cp "$svc_file" "$STAGING/app/META-INF/services/$svc_name"
                fi
            done
        fi
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
rm -f module-info.class
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

JAVAFX_COUNT=0
for jar in $(find ~/.m2/repository/org/openjfx -name "*${JAVAFX_VERSION}*mac*.jar" -type f 2>/dev/null | grep -v sources | grep -v javadoc); do
    cp "$jar" "$JPACKAGE_INPUT/"
    JAVAFX_COUNT=$((JAVAFX_COUNT + 1))
done
echo "[OK] JavaFX copiado ($JAVAFX_COUNT JARs mac)"

# Verificar si existe el icono
ICON_PATH=""
if [ -f "$PROJECT_DIR/native/icons/app.icns" ]; then
    ICON_PATH="--icon $PROJECT_DIR/native/icons/app.icns"
    echo "[INFO] Icono encontrado: app.icns"
fi

# Crear app image
echo "[BUILD] Creando app image..."
rm -rf "$TARGET_DIR/installers"
jpackage \
    --type app-image \
    --name "OKtask" \
    --dest "$TARGET_DIR/installers" \
    --input "$JPACKAGE_INPUT" \
    --main-class com.academic.gestor.NativeLauncher \
    --main-jar "oktask-jpackage.jar" \
    $ICON_PATH \
    --java-options "-Dspring.profiles.active=native" \
    --java-options "-Xmx512m" \
    --app-version "$APP_VERSION" \
    --vendor "OKtask" \
    --description "OKtask - Gestor de tareas con Pomodoro" \
    --mac-package-identifier "com.oktask.app" \
    --mac-package-name "OKtask" 2>&1

rm -rf "$JPACKAGE_INPUT"

echo ""
echo "=========================================="
echo "  BUILD COMPLETADO"
echo "=========================================="
echo "[INFO] App image creada en: $TARGET_DIR/installers/"
echo "[INFO] Para ejecutar: open $TARGET_DIR/installers/OKtask.app"
echo ""
echo "[INFO] Para crear un instalador .pkg:"
echo "  pkgbuild --component $TARGET_DIR/installers/OKtask.app \\"
echo "    --install-location /Applications \\"
echo "    --identifier com.oktask.app \\"
echo "    --version $APP_VERSION \\"
echo "    oktask-$APP_VERSION.pkg"
echo ""
