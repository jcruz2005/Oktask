package com.academic.gestor.update;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para UpdateDownloader, enfocados en la sanitización
 * de nombres de archivo (prevención de path traversal).
 */
@DisplayName("UpdateDownloader")
class UpdateDownloaderTest {

    @Nested
    @DisplayName("sanitizeFilename")
    class SanitizeFilenameTests {

        @Test
        @DisplayName("deberia aceptar nombres de archivo normales")
        void deberiaAceptarNombresNormales() {
            assertEquals("OKtask-1.2.0-linux-x64.tar.gz",
                    UpdateDownloader.sanitizeFilename("OKtask-1.2.0-linux-x64.tar.gz"));
            assertEquals("OKtask-1.2.0.msi",
                    UpdateDownloader.sanitizeFilename("OKtask-1.2.0.msi"));
            assertEquals("OKtask-1.2.0.dmg",
                    UpdateDownloader.sanitizeFilename("OKtask-1.2.0.dmg"));
        }

        @Test
        @DisplayName("deberia rechazar null y vacio")
        void deberiaRechazarNullYVacio() {
            assertNull(UpdateDownloader.sanitizeFilename(null));
            assertNull(UpdateDownloader.sanitizeFilename(""));
            assertNull(UpdateDownloader.sanitizeFilename("   "));
        }

        @Test
        @DisplayName("deberia rechazar path traversal con ../")
        void deberiaRechazarPathTraversal() {
            assertNull(UpdateDownloader.sanitizeFilename("../../etc/passwd"));
            assertNull(UpdateDownloader.sanitizeFilename("..\\..\\windows\\system32\\evil.exe"));
            assertNull(UpdateDownloader.sanitizeFilename(".."));
            assertNull(UpdateDownloader.sanitizeFilename("a..b.exe"));
        }

        @Test
        @DisplayName("deberia extraer solo el ultimo componente de la ruta")
        void deberiaExtraerUltimoComponente() {
            assertEquals("oktask.tar.gz",
                    UpdateDownloader.sanitizeFilename("/downloads/oktask.tar.gz"));
            assertEquals("oktask.msi",
                    UpdateDownloader.sanitizeFilename("C:\\downloads\\oktask.msi"));
        }

        @Test
        @DisplayName("deberia rechazar caracteres peligrosos")
        void deberiaRechazarCaracteresPeligrosos() {
            assertNull(UpdateDownloader.sanitizeFilename("oktask; rm -rf /"));
            assertNull(UpdateDownloader.sanitizeFilename("oktask$(whoami).msi"));
            assertNull(UpdateDownloader.sanitizeFilename("oktask|sh"));
        }

        @Test
        @DisplayName("deberia rechazar extensiones no permitidas")
        void deberiaRechazarExtensionesNoPermitidas() {
            assertNull(UpdateDownloader.sanitizeFilename("malware.sh"));
            assertNull(UpdateDownloader.sanitizeFilename("notas.txt"));
            assertNull(UpdateDownloader.sanitizeFilename("script.py"));
        }
    }

    @Nested
    @DisplayName("validación de URL")
    class UrlValidationTests {

        @Test
        @DisplayName("deberia lanzar excepcion con URL HTTP (no HTTPS)")
        void deberiaRechazarHttp() throws Exception {
            UpdateDownloader downloader = new UpdateDownloader();
            Exception ex = assertThrows(IllegalArgumentException.class,
                    () -> downloader.download(
                            "http://example.com/oktask.tar.gz",
                            "oktask.tar.gz",
                            java.nio.file.Files.createTempDirectory("oktask-test").toFile()
                    ));
            assertTrue(ex.getMessage().contains("HTTPS"));
        }
    }
}