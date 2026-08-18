package com.academic.gestor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para scripts de build")
class BuildScriptsTest {

    @TempDir
    Path tempDir;

    @Nested
    @DisplayName("build-linux.sh")
    class BuildLinuxTests {

        @Test
        @DisplayName("deberia contener set -e para detener en errores")
        void deberiaContenerSetE() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-linux.sh");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("set -e"),
                    "El script debe contener 'set -e' para detenerse en errores");
        }

        @Test
        @DisplayName("deberia verificar Java 21")
        void deberiaVerificarJava21() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-linux.sh");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("java -version"),
                    "El script debe verificar la version de Java");
            assertTrue(contenido.contains("21"),
                    "El script debe verificar Java 21 o superior");
        }

        @Test
        @DisplayName("deberia verificar Maven")
        void deberiaVerificarMaven() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-linux.sh");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("mvn"),
                    "El script debe verificar que Maven esta disponible");
        }

        @Test
        @DisplayName("deberia verificar jpackage")
        void deberiaVerificarJpackage() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-linux.sh");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("jpackage"),
                    "El script debe verificar que jpackage esta disponible");
        }

        @Test
        @DisplayName("deberia compilar JAR con Maven")
        void deberiaCompilarJarConMaven() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-linux.sh");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("mvn clean package"),
                    "El script debe compilar el JAR con Maven");
        }

        @Test
        @DisplayName("deberia usar jpackage con type app-image")
        void deberiaUsarJpackageConTypeAppImage() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-linux.sh");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("--type app-image"),
                    "El script debe crear app-image con jpackage");
        }

        @Test
        @DisplayName("deberia configurar main-class a NativeLauncher")
        void deberiaConfigurarMainClass() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-linux.sh");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("NativeLauncher"),
                    "El script debe configurar NativeLauncher como main-class");
        }

        @Test
        @DisplayName("deberia configurar spring profile native")
        void deberiaConfigurarSpringProfileNative() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-linux.sh");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("spring.profiles.active=native"),
                    "El script debe configurar el profile native de Spring");
        }

        @Test
        @DisplayName("deberia configurar memoria maxima 512m")
        void deberiaConfigurarMemoriaMaxima() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-linux.sh");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("-Xmx512m"),
                    "El script debe configurar -Xmx512m");
        }

        @Test
        @DisplayName("deberia buscar JAR compilado en target")
        void deberiaBuscarJarEnTarget() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-linux.sh");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("oktask-"),
                    "El script debe buscar el JAR compilado con el nombre oktask-");
        }

        @Test
        @DisplayName("deberia verificar existencia de icono")
        void deberiaVerificarExistenciaDeIcono() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-linux.sh");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("icon.png"),
                    "El script debe verificar la existencia del icono");
        }

        @Test
        @DisplayName("deberia tener shebang bash")
        void deberiaTenerShebangBash() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-linux.sh");

            // Act
            String primeraLinea = Files.readAllLines(scriptPath, StandardCharsets.UTF_8).get(0);

            // Assert
            assertTrue(primeraLinea.startsWith("#!/bin/bash"),
                    "El script debe tener shebang #!/bin/bash");
        }
    }

    @Nested
    @DisplayName("build-windows.bat")
    class BuildWindowsTests {

        @Test
        @DisplayName("deberia contener setlocal enabledelayedexpansion")
        void deberiaContenerSetLocal() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-windows.bat");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("setlocal enabledelayedexpansion"),
                    "El script debe contener setlocal enabledelayedexpansion");
        }

        @Test
        @DisplayName("deberia verificar Java 21 con findstr")
        void deberiaVerificarJava21ConFindstr() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-windows.bat");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("findstr /C:\"21\""),
                    "El script debe verificar Java 21 con findstr");
        }

        @Test
        @DisplayName("deberia usar jpackage con type app-image")
        void deberiaUsarJpackageConTypeAppImage() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-windows.bat");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("--type app-image"),
                    "El script debe crear app-image con jpackage");
        }

        @Test
        @DisplayName("deberia configurar win-menu y win-shortcut")
        void deberiaConfigurarWinMenuYShortcut() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-windows.bat");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("--win-menu"),
                    "El script debe configurar --win-menu");
            assertTrue(contenido.contains("--win-shortcut"),
                    "El script debe configurar --win-shortcut");
        }

        @Test
        @DisplayName("deberia configurar win-menu-group")
        void deberiaConfigurarWinMenuGroup() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-windows.bat");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("--win-menu-group \"OKtask\""),
                    "El script debe configurar --win-menu-group OKtask");
        }

        @Test
        @DisplayName("deberia verificar icono .ico")
        void deberiaVerificarIconoIco() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-windows.bat");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("app.ico"),
                    "El script debe verificar el icono .ico");
        }

        @Test
        @DisplayName("deberia usar oktask-1.1.0.jar")
        void deberiaUsarNombreJarEspecifico() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-windows.bat");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("oktask-%APP_VERSION%.jar"),
                    "El script debe usar el nombre del JAR oktask-%APP_VERSION%.jar");
        }

        @Test
        @DisplayName("deberia usar call mvn para compilar")
        void deberiaUsarCallMvn() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-windows.bat");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("call mvn"),
                    "El script debe usar 'call mvn' para compilar");
        }

        @Test
        @DisplayName("deberia tener endlocal al final")
        void deberiaTenerEndlocal() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-windows.bat");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("endlocal"),
                    "El script debe terminar con endlocal");
        }
    }

    @Nested
    @DisplayName("build-macos.sh")
    class BuildMacosTests {

        @Test
        @DisplayName("deberia contener set -e para detener en errores")
        void deberiaContenerSetE() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-macos.sh");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("set -e"),
                    "El script debe contener 'set -e' para detenerse en errores");
        }

        @Test
        @DisplayName("deberia verificar que es macOS con uname Darwin")
        void deberiaVerificarMacosConUname() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-macos.sh");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("uname"),
                    "El script debe verificar el sistema operativo");
            assertTrue(contenido.contains("Darwin"),
                    "El script debe verificar si es macOS (Darwin)");
        }

        @Test
        @DisplayName("deberia configurar mac-package-identifier")
        void deberiaConfigurarMacPackageIdentifier() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-macos.sh");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("--mac-package-identifier"),
                    "El script debe configurar --mac-package-identifier");
            assertTrue(contenido.contains("com.oktask.app"),
                    "El identifier debe ser com.oktask.app");
        }

        @Test
        @DisplayName("deberia configurar mac-package-name")
        void deberiaConfigurarMacPackageName() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-macos.sh");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("--mac-package-name"),
                    "El script debe configurar --mac-package-name");
        }

        @Test
        @DisplayName("deberia buscar icono .icns")
        void deberiaBuscarIconoIcns() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-macos.sh");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("app.icns"),
                    "El script debe buscar el icono .icns para macOS");
        }

        @Test
        @DisplayName("deberia tener shebang bash")
        void deberiaTenerShebangBash() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-macos.sh");

            // Act
            String primeraLinea = Files.readAllLines(scriptPath, StandardCharsets.UTF_8).get(0);

            // Assert
            assertTrue(primeraLinea.startsWith("#!/bin/bash"),
                    "El script debe tener shebang #!/bin/bash");
        }

        @Test
        @DisplayName("deberia usar jpackage con type app-image")
        void deberiaUsarJpackageConTypeAppImage() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-macos.sh");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("--type app-image"),
                    "El script debe crear app-image con jpackage");
        }

        @Test
        @DisplayName("deberia incluir instrucciones de pkgbuild")
        void deberiaIncluirInstruccionesPkgbuild() throws IOException {
            // Arrange
            Path scriptPath = Path.of("build-macos.sh");

            // Act
            String contenido = Files.readString(scriptPath, StandardCharsets.UTF_8);

            // Assert
            assertTrue(contenido.contains("pkgbuild"),
                    "El script debe incluir instrucciones para crear .pkg con pkgbuild");
        }
    }

    @Nested
    @DisplayName("Consistencia cross-platform")
    class ConsistenciaCrossPlatformTests {

        @Test
        @DisplayName("todos los scripts deben usar la misma main-class")
        void todosLosScriptsDebenUsarLaMismaMainClass() throws IOException {
            // Arrange
            String linux = Files.readString(Path.of("build-linux.sh"), StandardCharsets.UTF_8);
            String windows = Files.readString(Path.of("build-windows.bat"), StandardCharsets.UTF_8);
            String macos = Files.readString(Path.of("build-macos.sh"), StandardCharsets.UTF_8);

            // Act & Assert
            assertTrue(linux.contains("NativeLauncher"), "Linux: main-class incorrecto");
            assertTrue(windows.contains("NativeLauncher"), "Windows: main-class incorrecto");
            assertTrue(macos.contains("NativeLauncher"), "macOS: main-class incorrecto");
        }

        @Test
        @DisplayName("todos los scripts deben usar spring profile native")
        void todosLosScriptsDebenUsarSpringProfileNative() throws IOException {
            // Arrange
            String linux = Files.readString(Path.of("build-linux.sh"), StandardCharsets.UTF_8);
            String windows = Files.readString(Path.of("build-windows.bat"), StandardCharsets.UTF_8);
            String macos = Files.readString(Path.of("build-macos.sh"), StandardCharsets.UTF_8);

            // Act & Assert
            assertTrue(linux.contains("spring.profiles.active=native"), "Linux: falta profile native");
            assertTrue(windows.contains("spring.profiles.active=native"), "Windows: falta profile native");
            assertTrue(macos.contains("spring.profiles.active=native"), "macOS: falta profile native");
        }

        @Test
        @DisplayName("todos los scripts deben configurar Xmx512m")
        void todosLosScriptsDebenConfigurarMemoria() throws IOException {
            // Arrange
            String linux = Files.readString(Path.of("build-linux.sh"), StandardCharsets.UTF_8);
            String windows = Files.readString(Path.of("build-windows.bat"), StandardCharsets.UTF_8);
            String macos = Files.readString(Path.of("build-macos.sh"), StandardCharsets.UTF_8);

            // Act & Assert
            assertTrue(linux.contains("-Xmx512m"), "Linux: falta configuracion de memoria");
            assertTrue(windows.contains("-Xmx512m"), "Windows: falta configuracion de memoria");
            assertTrue(macos.contains("-Xmx512m"), "macOS: falta configuracion de memoria");
        }

        @Test
        @DisplayName("todos los scripts deben usar app-version 1.1.0")
        void todosLosScriptsDebenUsarAppVersion() throws IOException {
            // Arrange
            String linux = Files.readString(Path.of("build-linux.sh"), StandardCharsets.UTF_8);
            String windows = Files.readString(Path.of("build-windows.bat"), StandardCharsets.UTF_8);
            String macos = Files.readString(Path.of("build-macos.sh"), StandardCharsets.UTF_8);

            // Act & Assert
            assertTrue(linux.contains("APP_VERSION=\"1.1.0\""), "Linux: falta version");
            assertTrue(windows.contains("APP_VERSION=1.1.0"), "Windows: falta version");
            assertTrue(macos.contains("APP_VERSION=\"1.1.0\""), "macOS: falta version");
        }

        @Test
        @DisplayName("todos los scripts deben usar vendor OKtask")
        void todosLosScriptsDebenUsarVendor() throws IOException {
            // Arrange
            String linux = Files.readString(Path.of("build-linux.sh"), StandardCharsets.UTF_8);
            String windows = Files.readString(Path.of("build-windows.bat"), StandardCharsets.UTF_8);
            String macos = Files.readString(Path.of("build-macos.sh"), StandardCharsets.UTF_8);

            // Act & Assert
            assertTrue(linux.contains("--vendor \"OKtask\""), "Linux: falta vendor");
            assertTrue(windows.contains("--vendor \"OKtask\""), "Windows: falta vendor");
            assertTrue(macos.contains("--vendor \"OKtask\""), "macOS: falta vendor");
        }

        @Test
        @DisplayName("todos los scripts deben crear directorio installers")
        void todosLosScriptsDebenCrearDirectorioInstallers() throws IOException {
            // Arrange
            String linux = Files.readString(Path.of("build-linux.sh"), StandardCharsets.UTF_8);
            String windows = Files.readString(Path.of("build-windows.bat"), StandardCharsets.UTF_8);
            String macos = Files.readString(Path.of("build-macos.sh"), StandardCharsets.UTF_8);

            // Act & Assert
            assertTrue(linux.contains("installers"), "Linux: falta directorio installers");
            assertTrue(windows.contains("installers"), "Windows: falta directorio installers");
            assertTrue(macos.contains("installers"), "macOS: falta directorio installers");
        }

        @Test
        @DisplayName("todos los scripts deben usar -DskipTests")
        void todosLosScriptsDebenUsarSkipTests() throws IOException {
            // Arrange
            String linux = Files.readString(Path.of("build-linux.sh"), StandardCharsets.UTF_8);
            String windows = Files.readString(Path.of("build-windows.bat"), StandardCharsets.UTF_8);
            String macos = Files.readString(Path.of("build-macos.sh"), StandardCharsets.UTF_8);

            // Act & Assert
            assertTrue(linux.contains("-DskipTests"), "Linux: falta -DskipTests");
            assertTrue(windows.contains("-DskipTests"), "Windows: falta -DskipTests");
            assertTrue(macos.contains("-DskipTests"), "macOS: falta -DskipTests");
        }
    }
}
