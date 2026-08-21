@echo off
REM build-windows.bat - Build nativo para Windows con jpackage
REM
REM @author Gestor de Tareas Académicas
REM @since 1.1.0

setlocal enabledelayedexpansion

set "PROJECT_DIR=%~dp0"
set "TARGET_DIR=%PROJECT_DIR%target"
set "APP_VERSION=1.2.0"

echo ==========================================
echo   BUILD NATIVO PARA WINDOWS
echo ==========================================
echo.

REM Verificar herramientas
echo [BUILD] Verificando herramientas...
java -version 2>&1 | findstr /C:"21" >nul
if errorlevel 1 (
    echo [ERROR] Se requiere Java 21
    exit /b 1
)
mvn --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven no encontrado
    exit /b 1
)
jpackage --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] jpackage no encontrado
    exit /b 1
)
echo [OK] Todas las herramientas detectadas
echo.

REM Compilar
echo [BUILD] Compilando...
cd /d "%PROJECT_DIR%"
call mvn clean package -DskipTests -q
echo [OK] JAR compilado

REM Crear JAR plano preservando metadata de Spring Boot
echo.
echo [BUILD] Creando JAR plano para jpackage...
set "PLAIN_JAR=%TARGET_DIR%\oktask-jpackage.jar"
set "STAGING=%TARGET_DIR%\jpackage-staging"
if exist "%STAGING%" rmdir /s /q "%STAGING%"
if exist "%PLAIN_JAR%" del "%PLAIN_JAR%"
mkdir "%STAGING%\app"

echo [BUILD] Extrayendo fat JAR...
cd /d "%STAGING%"
jar xf "%TARGET_DIR%\oktask-%APP_VERSION%.jar"
xcopy /E /Y /Q "BOOT-INF\classes\*" "%STAGING%\app\" >nul

echo [BUILD] Extrayendo dependencias (excluyendo JavaFX)...
for %%j in ("BOOT-INF\lib\*.jar") do (
    echo %%~nxj | findstr /b "javafx-" >nul
    if errorlevel 1 (
        set "EXTRACT_DIR=%STAGING%\tmp_extract"
        if exist "!EXTRACT_DIR!" rmdir /s /q "!EXTRACT_DIR!"
        mkdir "!EXTRACT_DIR!"
        cd /d "!EXTRACT_DIR!"
        jar xf "%%j" 2>nul
        xcopy /E /Y /Q "." "%STAGING%\app\" >nul 2>nul
        if exist "META-INF\spring" (
            mkdir "%STAGING%\app\META-INF\spring" 2>nul
            xcopy /E /Y /Q "META-INF\spring\*" "%STAGING%\app\META-INF\spring\" >nul 2>nul
        )
        if exist "META-INF\spring.factories" (
            mkdir "%STAGING%\app\META-INF" 2>nul
            copy /y "META-INF\spring.factories" "%STAGING%\app\META-INF\spring.factories" >nul 2>nul
        )
        if exist "META-INF\services" (
            mkdir "%STAGING%\app\META-INF\services" 2>nul
            xcopy /E /Y /Q "META-INF\services\*" "%STAGING%\app\META-INF\services\" >nul 2>nul
        )
        cd /d "%STAGING%"
        rmdir /s /q "!EXTRACT_DIR!" 2>nul
    ) else (
        echo [SKIP] %%~nxj
    )
)

mkdir "%STAGING%\app\META-INF" 2>nul
echo Manifest-Version: 1.0> "%STAGING%\app\META-INF\MANIFEST.MF"
echo Main-Class: com.academic.gestor.NativeLauncher>> "%STAGING%\app\META-INF\MANIFEST.MF"

cd /d "%STAGING%\app"
if exist "module-info.class" del "module-info.class"
jar cfm "%PLAIN_JAR%" META-INF\MANIFEST.MF .
cd /d "%PROJECT_DIR%"

rmdir /s /q "%STAGING%" 2>nul

echo [VERIFY] Verificando metadata...
jar tf "%PLAIN_JAR%" | findstr /q "AutoConfiguration.imports" && echo [OK] AutoConfiguration.imports || echo [WARN] AutoConfiguration.imports NO encontrado
jar tf "%PLAIN_JAR%" | findstr /q "spring.factories" && echo [OK] spring.factories || echo [WARN] spring.factories NO encontrado
echo [OK] JAR plano creado

REM Crear jpackage input con JavaFX
echo.
echo [BUILD] Preparando jpackage...
set "JPACKAGE_INPUT=%TARGET_DIR%\jpackage-input"
if exist "%JPACKAGE_INPUT%" rmdir /s /q "%JPACKAGE_INPUT%"
mkdir "%JPACKAGE_INPUT%"
copy "%PLAIN_JAR%" "%JPACKAGE_INPUT%\" >nul

set JAVAFX_COUNT=0
for /r "%USERPROFILE%\.m2\repository\org\openjfx" %%j in (*.jar) do (
    echo %%~nxj | findstr /i "sources javadoc" >nul
    if errorlevel 1 (
        echo %%~nxj | findstr /i "mac linux" >nul
        if errorlevel 1 (
            echo %%~nxj | findstr /i "%APP_VERSION%" >nul
            if not errorlevel 1 (
                copy "%%j" "%JPACKAGE_INPUT%\" >nul
                set /a JAVAFX_COUNT+=1
            )
        )
    )
)
echo [OK] JavaFX copiado (!JAVAFX_COUNT! JARs windows)

REM Verificar icono
set "ICON_OPTION="
if exist "%PROJECT_DIR%native\icons\app.ico" (
    set "ICON_OPTION=--icon %PROJECT_DIR%native\icons\app.ico"
    echo [INFO] Icono encontrado: app.ico
)

REM Crear app image
echo [BUILD] Creando app image...
if exist "%TARGET_DIR%\installers" rmdir /s /q "%TARGET_DIR%\installers"
jpackage ^
    --type app-image ^
    --name "OKtask" ^
    --dest "%TARGET_DIR%\installers" ^
    --input "%JPACKAGE_INPUT%" ^
    --main-class com.academic.gestor.NativeLauncher ^
    --main-jar "oktask-jpackage.jar" ^
    !ICON_OPTION! ^
    --java-options "-Dspring.profiles.active=native" ^
    --java-options "-Xmx512m" ^
    --app-version "%APP_VERSION%" ^
    --vendor "OKtask" ^
    --description "OKtask - Gestor de tareas con Pomodoro" ^
    --win-menu ^
    --win-menu-group "OKtask" ^
    --win-shortcut

if exist "%JPACKAGE_INPUT%" rmdir /s /q "%JPACKAGE_INPUT%"

echo.
echo ==========================================
echo   BUILD COMPLETADO
echo ==========================================
echo [INFO] Para ejecutar: %TARGET_DIR%\installers\OKtask\OKtask.exe
echo.

cd /d "%PROJECT_DIR%"
endlocal
