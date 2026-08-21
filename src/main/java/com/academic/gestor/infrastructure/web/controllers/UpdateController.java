package com.academic.gestor.infrastructure.web.controllers;

import com.academic.gestor.update.UpdateChecker;
import com.academic.gestor.update.UpdateInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.awt.Desktop;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Controller para verificar actualizaciones desde el frontend.
 *
 * @author OKtask
 * @since 1.2.0
 */
@RestController
@RequestMapping("/api/update")
public class UpdateController {

    private static final Logger log = LoggerFactory.getLogger(UpdateController.class);

    /** Versión actual de la aplicación (debe coincidir con NativeLauncher). */
    private static final String APP_VERSION = "1.2.8";

    private final UpdateChecker updateChecker;

    /**
     * Constructor.
     */
    public UpdateController() {
        this.updateChecker = new UpdateChecker(APP_VERSION);
    }

    /**
     * Verifica si hay actualizaciones disponibles.
     *
     * @param platform plataforma detectada por el frontend (linux, windows, macos, android)
     * @return ResponseEntity con la información de la actualización
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkForUpdate(
            @RequestParam(required = false, defaultValue = "") String platform) {
        log.info("Verificación de actualización solicitada desde frontend (platform={})", platform);

        Map<String, Object> response = new HashMap<>();
        response.put("currentVersion", APP_VERSION);
        response.put("hasUpdate", false);

        try {
            UpdateInfo info = updateChecker.checkForUpdate(true);

            if (info != null && info.isValid()) {
                int comparison = info.compareTo(APP_VERSION);
                if (comparison > 0) {
                    response.put("hasUpdate", true);
                    response.put("version", info.getVersion());
                    response.put("releaseDate", info.getReleaseDate());
                    response.put("changelog", info.getChangelog());
                    response.put("downloadUrl", info.getDownloadUrl());

                    // Agregar info de descarga por plataforma
                    Map<String, Object> downloads = new HashMap<>();
                    for (Map.Entry<String, UpdateInfo.DownloadInfo> entry : info.getDownloads().entrySet()) {
                        Map<String, String> dlInfo = new HashMap<>();
                        dlInfo.put("url", entry.getValue().getUrl());
                        dlInfo.put("filename", entry.getValue().getFilename());
                        dlInfo.put("installCommand", entry.getValue().getInstallCommand());
                        downloads.put(entry.getKey(), dlInfo);
                    }
                    response.put("downloads", downloads);

                    // Usar platform del frontend si se proporciona, si no detectar del backend
                    String detectedPlatform = platform.isEmpty() ? UpdateInfo.detectOS() : platform;
                    response.put("currentPlatform", detectedPlatform);

                    log.info("Actualización disponible: v{}", info.getVersion());
                }
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al verificar actualizaciones: {}", e.getMessage());
            response.put("error", "No se pudo verificar actualizaciones");
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Obtiene la versión actual de la aplicación.
     *
     * @return ResponseEntity con la versión
     */
    @GetMapping("/version")
    public ResponseEntity<Map<String, String>> getCurrentVersion() {
        Map<String, String> response = new HashMap<>();
        response.put("version", APP_VERSION);
        return ResponseEntity.ok(response);
    }

    /**
     * Abre una URL en el navegador del sistema.
     *
     * @param url URL a abrir
     * @return ResponseEntity con el resultado
     */
    @GetMapping("/open-url")
    public ResponseEntity<Map<String, Object>> openUrl(@RequestParam String url) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
                response.put("success", true);
                log.info("URL abierta en navegador del sistema: {}", url);
            } else {
                String os = System.getProperty("os.name").toLowerCase();
                ProcessBuilder pb;
                if (os.contains("linux")) {
                    pb = new ProcessBuilder("xdg-open", url);
                } else if (os.contains("mac")) {
                    pb = new ProcessBuilder("open", url);
                } else if (os.contains("win")) {
                    pb = new ProcessBuilder("cmd", "/c", "start", url);
                } else {
                    response.put("success", false);
                    response.put("error", "Plataforma no soportada");
                    return ResponseEntity.ok(response);
                }
                pb.start();
                response.put("success", true);
                log.info("URL abierta via procesos del sistema: {}", url);
            }
        } catch (Exception e) {
            log.error("Error al abrir URL: {}", e.getMessage());
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Descarga e instala la actualización automáticamente.
     *
     * @param url      URL de descarga del archivo
     * @param filename nombre del archivo
     * @param platform plataforma actual
     * @return ResponseEntity con el resultado de la instalación
     */
    @GetMapping("/install")
    public ResponseEntity<Map<String, Object>> installUpdate(
            @RequestParam String url,
            @RequestParam String filename,
            @RequestParam(defaultValue = "") String platform) {

        Map<String, Object> response = new HashMap<>();
        String os = platform.isEmpty() ? detectOS() : platform;

        try {
            log.info("Iniciando instalación automática: {} para {}", filename, os);

            // Crear directorio temporal
            Path tempDir = Files.createTempDirectory("oktask-update-");
            Path downloadedFile = tempDir.resolve(filename);

            // Descargar archivo
            log.info("Descargando: {}", url);
            downloadFile(url, downloadedFile.toFile());
            log.info("Descarga completada: {} bytes", Files.size(downloadedFile));

            // Instalar según plataforma
            String installPath;
            switch (os) {
                case "linux":
                    installPath = installLinux(downloadedFile, filename);
                    break;
                case "macos":
                    installPath = installMacOS(downloadedFile, filename);
                    break;
                case "windows":
                    installPath = installWindows(downloadedFile, filename);
                    break;
                default:
                    response.put("success", false);
                    response.put("error", "Plataforma no soportada: " + os);
                    return ResponseEntity.ok(response);
            }

            // Limpiar archivos temporales
            deleteDirectory(tempDir);

            response.put("success", true);
            response.put("installPath", installPath);
            response.put("message", "Instalación completada. Reiniciá la aplicación para usar la nueva versión.");
            log.info("Instalación completada en: {}", installPath);

        } catch (Exception e) {
            log.error("Error durante la instalación: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Error durante la instalación: " + e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

    // ==================== MÉTODOS PRIVADOS ====================

    private String detectOS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("linux")) return "linux";
        if (os.contains("mac")) return "macos";
        if (os.contains("win")) return "windows";
        return "unknown";
    }

    /**
     * Descarga un archivo desde una URL.
     */
    private void downloadFile(String fileUrl, File destination) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("User-Agent", "OKtask-Updater/1.0");
        connection.setConnectTimeout(30000);
        connection.setReadTimeout(60000);
        connection.setInstanceFollowRedirects(true);

        // Seguir redirects manualmente (GitHub usa redirecciones)
        int status = connection.getResponseCode();
        while (status == 301 || status == 302) {
            String newUrl = connection.getHeaderField("Location");
            connection.disconnect();
            url = new URL(newUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "OKtask-Updater/1.0");
            status = connection.getResponseCode();
        }

        try (InputStream in = connection.getInputStream();
             FileOutputStream out = new FileOutputStream(destination)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Instala en Linux: extrae a ~/.local/share/oktask/, crea symlink y acceso directo.
     */
    private String installLinux(Path archive, String filename) throws IOException {
        Path installDir = Paths.get(System.getProperty("user.home"), ".local", "share", "oktask");
        Path binDir = Paths.get(System.getProperty("user.home"), ".local", "bin");
        Path applicationsDir = Paths.get(System.getProperty("user.home"), ".local", "share", "applications");

        // Limpiar instalación anterior
        if (Files.exists(installDir)) {
            deleteDirectory(installDir);
        }
        Files.createDirectories(installDir);

        if (filename.endsWith(".tar.gz")) {
            extractTarGz(archive, installDir);
        } else if (filename.endsWith(".zip")) {
            extractZip(archive, installDir);
        }

        // Buscar el ejecutable
        Path launcher = findLauncher(installDir, "OKtask");
        if (launcher == null) {
            throw new IOException("No se encontró el ejecutable en el paquete extraído");
        }

        // Crear symlink en ~/.local/bin/
        Files.createDirectories(binDir);
        Path symlink = binDir.resolve("oktask");
        Files.deleteIfExists(symlink);
        Files.createSymbolicLink(symlink, launcher);
        log.info("Symlink creado: {} -> {}", symlink, launcher);

        // Crear acceso directo en el escritorio
        createDesktopEntry(applicationsDir, launcher);

        return installDir.toString();
    }

    /**
     * Instala en macOS: extrae a ~/Applications/OKtask/.
     */
    private String installMacOS(Path archive, String filename) throws IOException {
        Path installDir = Paths.get(System.getProperty("user.home"), "Applications", "OKtask");

        if (Files.exists(installDir)) {
            deleteDirectory(installDir);
        }
        Files.createDirectories(installDir);

        if (filename.endsWith(".zip")) {
            extractZip(archive, installDir);
        } else if (filename.endsWith(".tar.gz")) {
            extractTarGz(archive, installDir);
        }

        // En macOS, intentar crear un alias en el escritorio
        Path desktop = Paths.get(System.getProperty("user.home"), "Desktop");
        Path appBundle = findAppBundle(installDir);
        if (appBundle != null && Files.exists(desktop)) {
            Path alias = desktop.resolve("OKtask.app");
            Files.deleteIfExists(alias);
            // No podemos crear aliases reales fácilmente, así que copiamos el .app
            copyDirectory(appBundle, alias);
            log.info("App copiada al escritorio: {}", alias);
        }

        return installDir.toString();
    }

    /**
     * Instala en Windows: extrae a %LOCALAPPDATA%\OKtask\.
     */
    private String installWindows(Path archive, String filename) throws IOException {
        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData == null) {
            localAppData = Paths.get(System.getProperty("user.home"), "AppData", "Local").toString();
        }
        Path installDir = Paths.get(localAppData, "OKtask");

        if (Files.exists(installDir)) {
            deleteDirectory(installDir);
        }
        Files.createDirectories(installDir);

        if (filename.endsWith(".zip")) {
            extractZip(archive, installDir);
        }

        // Buscar el ejecutable
        Path launcher = findLauncher(installDir, "OKtask");
        if (launcher == null) {
            throw new IOException("No se encontró el ejecutable en el paquete extraído");
        }

        // Crear acceso directo en el escritorio
        Path desktop = Paths.get(System.getenv("USERPROFILE"), "Desktop");
        if (Files.exists(desktop)) {
            Path shortcut = desktop.resolve("OKtask.lnk");
            createWindowsShortcut(launcher, shortcut);
            log.info("Acceso directo creado: {}", shortcut);
        }

        return installDir.toString();
    }

    // ==================== MÉTODOS DE EXTRACCIÓN ====================

    private void extractTarGz(Path archive, Path destination) throws IOException {
        // Usar tar del sistema
        ProcessBuilder pb = new ProcessBuilder("tar", "-xzf", archive.toString(), "-C", destination.toString());
        pb.redirectErrorStream(true);
        Process process = pb.start();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while (reader.readLine() != null) { }
        }
        try {
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new IOException("Error al extraer tar.gz (exit code: " + exitCode + ")");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Extracción interrumpida", e);
        }
    }

    private void extractZip(Path archive, Path destination) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(archive.toFile()))) {
            ZipEntry entry;
            byte[] buffer = new byte[8192];
            while ((entry = zis.getNextEntry()) != null) {
                Path entryPath = destination.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    try (FileOutputStream out = new FileOutputStream(entryPath.toFile())) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            out.write(buffer, 0, len);
                        }
                    }
                }
            }
        }
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private Path findLauncher(Path dir, String name) throws IOException {
        // Buscar ejecutable en el directorio
        try (Stream<Path> walk = Files.walk(dir)) {
            return walk.filter(p -> {
                String fileName = p.getFileName().toString().toLowerCase();
                return fileName.equals(name.toLowerCase()) ||
                       fileName.equals(name.toLowerCase() + ".exe") ||
                       fileName.equals(name.toLowerCase() + ".sh");
            }).filter(Files::isExecutable)
              .findFirst()
              .orElse(null);
        }
    }

    private Path findAppBundle(Path dir) throws IOException {
        try (Stream<Path> walk = Files.walk(dir, 2)) {
            return walk.filter(p -> p.toString().endsWith(".app"))
                       .findFirst()
                       .orElse(null);
        }
    }

    private void createDesktopEntry(Path applicationsDir, Path launcher) throws IOException {
        Files.createDirectories(applicationsDir);
        Path desktopFile = applicationsDir.resolve("oktask.desktop");

        String content = String.format("""
                [Desktop Entry]
                Name=OKtask
                Comment=OKtask - Gestor de tareas con Pomodoro
                Exec=%s
                Icon=oktask
                Type=Application
                Categories=Education;Productivity;
                Terminal=false
                StartupNotify=true
                """, launcher.toAbsolutePath());

        Files.writeString(desktopFile, content);
        log.info("Desktop entry creado: {}", desktopFile);
    }

    private void createWindowsShortcut(Path target, Path shortcut) {
        // Crear un .bat como workaround (los .lnk son binarios)
        try {
            Path batPath = shortcut.resolveSibling(shortcut.getFileName().toString().replace(".lnk", ".bat"));
            String content = String.format("""
                    @echo off
                    start "" "%s"
                    """, target.toAbsolutePath());
            Files.writeString(batPath, content);
        } catch (IOException e) {
            log.warn("No se pudo crear acceso directo Windows: {}", e.getMessage());
        }
    }

    private void copyDirectory(Path source, Path destination) throws IOException {
        if (Files.isDirectory(source)) {
            Files.createDirectories(destination);
            try (Stream<Path> stream = Files.list(source)) {
                for (Path child : stream.toList()) {
                    copyDirectory(child, destination.resolve(child.getFileName()));
                }
            }
        } else {
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void deleteDirectory(Path dir) throws IOException {
        if (!Files.exists(dir)) return;
        try (Stream<Path> walk = Files.walk(dir)) {
            walk.sorted((a, b) -> b.compareTo(a))
                .forEach(path -> {
                    try { Files.deleteIfExists(path); }
                    catch (IOException e) { log.warn("No se pudo eliminar: {}", path); }
                });
        }
    }
}
