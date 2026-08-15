@echo off
REM build-windows.bat - Script de build nativo para Windows
REM
REM Este script compila la aplicacion Spring Boot y crea una imagen
REM de aplicacion nativa usando jpackage para distribucion en Windows.
REM
REM Requisitos:
REM - Java 21 o superior
REM - Maven 3.8+
REM - jpackage (incluido en JDK 14+)
REM
REM Uso:
REM   build-windows.bat
REM
REM @author Gestor de Tareas Academicas
REM @since 1.0.0

setlocal enabledelayedexpansion

REM Configuracion de rutas
set SCRIPT_DIR=%~dp0
set PROJECT_DIR=%SCRIPT_DIR%
set TARGET_DIR=%PROJECT_DIR%target

echo ==========================================
echo   BUILD NATIVO PARA WINDOWS
echo ==========================================
echo.

REM Verificar herramientas necesarias
echo [BUILD] Verificando herramientas...

REM Verificar Java 21
java -version 2>&1 | findstr /C:"21" >nul
if errorlevel 1 (
    echo [ERROR] Se requiere Java 21 o superior
    echo [INFO] Version actual:
    java -version 2>&1
    exit /b 1
)
echo [OK] Java 21 detectado

REM Verificar Maven
mvn --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Maven no encontrado en el PATH
    exit /b 1
)
echo [OK] Maven detectado

REM Verificar jpackage
jpackage --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] jpackage no encontrado. Asegurese de usar JDK 14+
    exit /b 1
)
echo [OK] jpackage detectado

echo.

REM Compilar JAR
echo [BUILD] Compilando JAR con Maven...
cd /d "%PROJECT_DIR%"
call mvn clean package -Dmaven.test.skip=true -q

if errorlevel 1 (
    echo [ERROR] Error al compilar el proyecto
    exit /b 1
)
echo [OK] JAR compilado exitosamente

echo.

REM Crear app image con jpackage
echo [BUILD] Creando app image para Windows...
if exist "%TARGET_DIR%\installers" rmdir /s /q "%TARGET_DIR%\installers"
mkdir "%TARGET_DIR%\installers"

REM Determinar el nombre del JAR compilado
set "JAR_NAME="
for %%f in ("%TARGET_DIR%\gestor-tareas-*.jar") do (
    set "JAR_NAME=%%~nxf"
    goto :found_jar
)

:found_jar
if "%JAR_NAME%"=="" (
    echo [ERROR] No se encontro el JAR compilado en %TARGET_DIR%
    exit /b 1
)
echo [INFO] Usando JAR: %JAR_NAME%

REM Verificar si existe el icono
set "ICON_OPTION="
if exist "%PROJECT_DIR%\native\icons\app.ico" (
    set "ICON_OPTION=--icon %PROJECT_DIR%\native\icons\app.ico"
    echo [INFO] Icono encontrado: app.ico
) else (
    echo [WARN] No se encontro icono, se usara el predeterminado
)

jpackage ^
    --type app-image ^
    --name "GestorTareasAcademicas" ^
    --dest "%TARGET_DIR%\installers" ^
    --input "%TARGET_DIR%" ^
    --main-class com.academic.gestor.NativeLauncher ^
    --main-jar "%JAR_NAME%" ^
    %ICON_OPTION% ^
    --java-options "-Djava.library.path=%APPDIR%\..\runtime\lib" ^
    --java-options "-Dspring.profiles.active=native" ^
    --java-options "-Xmx512m" ^
    --app-version "1.0.0" ^
    --vendor "Academic" ^
    --description "Gestor de Tareas Academicas con Pomodoro" ^
    --win-menu ^
    --win-menu-group "Academic" ^
    --win-shortcut

if errorlevel 1 (
    echo [ERROR] Error al crear la app image
    exit /b 1
)

echo.
echo ==========================================
echo   BUILD COMPLETADO EXITOSAMENTE
echo ==========================================
echo [INFO] App image creada en: %TARGET_DIR%\installers\
echo [INFO] Para ejecutar: %TARGET_DIR%\installers\GestorTareasAcademicas\GestorTareasAcademicas.exe
echo.

REM Mostrar contenido del directorio de instaladores
echo [INFO] Contenido del directorio de instaladores:
dir "%TARGET_DIR%\installers\" 2>nul || echo   (directorio vacio)

cd "%PROJECT_DIR%"
endlocal
