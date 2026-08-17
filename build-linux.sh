#!/bin/bash
# build-linux.sh - Build nativo para Linux con jpackage + JavaFX wrapper
#
# @author Gestor de Tareas Académicas
# @since 1.1.0

set -e

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TARGET_DIR="$PROJECT_DIR/target"
JAVAFX_VERSION="21.0.1"
APP_VERSION="1.1.0"

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
    # CR-003: Saltar JARs JavaFX - van en module-path, no en classpath
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
        # MERGEAR spring.factories (no sobreescribir)
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
        # MERGEAR services/ (SLF4J, JDBC, etc. - cada dependencia agrega sus providers)
        if [ -d "META-INF/services" ]; then
            mkdir -p "$STAGING/app/META-INF/services"
            for svc_file in META-INF/services/*; do
                [ -f "$svc_file" ] || continue
                svc_name=$(basename "$svc_file")
                if [ -f "$STAGING/app/META-INF/services/$svc_name" ]; then
                    # Merge: agregar lines que no estén ya
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
# CR-003b: Eliminar module-info.class del flat JAR (provoca conflicto con JPMS)
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

# Copiar SOLO JARs JavaFX Linux (contienen clases + nativas)
JAVAFX_COUNT=0
for jar in $(find ~/.m2/repository/org/openjfx -name "*${JAVAFX_VERSION}*linux*.jar" -type f 2>/dev/null); do
    cp "$jar" "$JPACKAGE_INPUT/"
    JAVAFX_COUNT=$((JAVAFX_COUNT + 1))
done
echo "[OK] JavaFX copiado ($JAVAFX_COUNT JARs linux)"

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
    --java-options "-Dspring.profiles.active=native" \
    --java-options "-Xmx512m" \
    --app-version "$APP_VERSION" \
    --vendor "Academic" \
    --description "OKtask - Gestor de tareas con Pomodoro" 2>&1

rm -rf "$JPACKAGE_INPUT"

# Crear wrapper shell
echo "[BUILD] Creando launcher wrapper..."

APPDIR="$TARGET_DIR/installers/OKtask/lib/app"
RUNTIME_DIR="$TARGET_DIR/installers/OKtask/lib/runtime"
LAUNCHER_BIN="$TARGET_DIR/installers/OKtask/bin/OKtask"

mv "$LAUNCHER_BIN" "$LAUNCHER_BIN.jpackage-bin" 2>/dev/null || true

cat > "$LAUNCHER_BIN" << 'WRAPPER'
#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APPDIR="$SCRIPT_DIR/../lib/app"
RUNTIME="$SCRIPT_DIR/../lib/runtime"

# CR-001: Solo JARs -linux (contienen clases + nativas). Los sin -linux están vacíos.
MODULE_PATH=$(echo "$APPDIR"/*-linux.jar | tr ' ' ':')

# Buscar java en múltiples ubicaciones (PATH puede no estar disponible desde atajos)
if [ -f "$RUNTIME/bin/java" ]; then
    JAVA_CMD="$RUNTIME/bin/java"
elif command -v java &>/dev/null; then
    JAVA_CMD="java"
elif [ -f "/usr/bin/java" ]; then
    JAVA_CMD="/usr/bin/java"
elif [ -f "/usr/lib/jvm/java-21-openjdk/bin/java" ]; then
    JAVA_CMD="/usr/lib/jvm/java-21-openjdk/bin/java"
elif [ -f "/usr/lib/jvm/java-21-openjdk-amd64/bin/java" ]; then
    JAVA_CMD="/usr/lib/jvm/java-21-openjdk-amd64/bin/java"
else
    # Buscar java en el sistema
    JAVA_CMD=$(find /usr/lib/jvm -name "java" -type f 2>/dev/null | head -1)
    if [ -z "$JAVA_CMD" ]; then
        echo "ERROR: No se encontró Java. Instalá Java 21 o superior."
        exit 1
    fi
fi

exec "$JAVA_CMD" \
    --module-path "$MODULE_PATH" \
    --add-modules javafx.controls,javafx.web,javafx.media \
    -cp "$APPDIR/oktask-jpackage.jar" \
    -Djava.library.path="$APPDIR" \
    -Dspring.profiles.active=native \
    -Xmx512m \
    com.academic.gestor.NativeLauncher "$@"
WRAPPER

chmod +x "$LAUNCHER_BIN"

echo ""
echo "=========================================="
echo "  BUILD COMPLETADO"
echo "=========================================="

# Accesos directos
echo "[BUILD] Creando accesos directos..."
DESKTOP_NAME="OKtask"
ICON_FILE=""
[ -f "$PROJECT_DIR/native/icons/app-icon.png" ] && ICON_FILE="$PROJECT_DIR/native/icons/app-icon.png"

DESKTOP_CONTENT="[Desktop Entry]
Name=OKtask
Comment=OKtask - Gestor de tareas con Pomodoro
Exec=$LAUNCHER_BIN
Icon=$ICON_FILE
Type=Application
Categories=Education;Productivity;
Terminal=false
StartupNotify=true
Version=$APP_VERSION
"

[ -d "$HOME/Desktop" ] && echo "$DESKTOP_CONTENT" > "$HOME/Desktop/$DESKTOP_NAME.desktop" && chmod +x "$HOME/Desktop/$DESKTOP_NAME.desktop" && echo "[OK] Escritorio"
mkdir -p "$HOME/.local/share/applications"
echo "$DESKTOP_CONTENT" > "$HOME/.local/share/applications/$DESKTOP_NAME.desktop"
echo "[OK] Menú de aplicaciones"
update-desktop-database "$HOME/.local/share/applications" 2>/dev/null || true

echo ""
echo "[INFO] Ejecutar: $LAUNCHER_BIN"
echo ""
