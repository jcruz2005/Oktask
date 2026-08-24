package com.academic.gestor.update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Servicio de reinicio de la aplicación.
 *
 * <p>Lanza un nuevo proceso con la versión actualizada y cierra
 * la instancia actual de forma segura.</p>
 *
 * @author OKtask
 * @since 1.3.0
 */
public class RestartService {

    private static final Logger log = LoggerFactory.getLogger(RestartService.class);

    private static final int RESTART_DELAY_SECONDS = 5;
    private static final int SHUTDOWN_TIMEOUT_MS = 5000;

    private final String installPath;
    private final String platform;
    private volatile boolean restartInProgress = false;

    public RestartService(String installPath, String platform) {
        this.installPath = installPath;
        this.platform = platform;
    }

    /**
     * Ejecuta el reinicio completo: espera 5s, lanza nuevo proceso, cierra el actual.
     * Se ejecuta en un hilo separado.
     *
     * @param onComplete callback después de lanzar el reinicio
     */
    public void executeRestart(Runnable onComplete) {
        if (restartInProgress) return;
        restartInProgress = true;

        new Thread(() -> {
            try {
                // Countdown 5 segundos
                for (int i = RESTART_DELAY_SECONDS; i > 0; i--) {
                    log.info("Reiniciando en {} segundos...", i);
                    Thread.sleep(1000);
                }

                // Lanzar nuevo proceso
                launchNewProcess();

                // Notificar al frontend
                if (onComplete != null) {
                    onComplete.run();
                }

                // Esperar un momento y salir
                Thread.sleep(500);
                log.info("Cerrando instancia actual...");
                System.exit(0);

            } catch (Exception e) {
                log.error("Error durante el reinicio: {}", e.getMessage(), e);
                restartInProgress = false;
            }
        }, "restart-thread").start();
    }

    /**
     * Lanza el ejecutable de la nueva versión según la plataforma.
     */
    private void launchNewProcess() throws IOException {
        String os = platform.isEmpty() ? detectOS() : platform;

        ProcessBuilder pb;
        switch (os) {
            case "linux":
                pb = buildLinuxProcess();
                break;
            case "macos":
                pb = buildMacOsProcess();
                break;
            case "windows":
                pb = buildWindowsProcess();
                break;
            default:
                throw new IOException("Plataforma no soportada para reinicio: " + os);
        }

        log.info("Lanzando nuevo proceso en {}...", os);
        pb.start();
    }

    private ProcessBuilder buildLinuxProcess() {
        // Buscar en ~/.local/share/oktask/OKtask/bin/OKtask
        Path launcher = Paths.get(
            System.getProperty("user.home"),
            ".local", "share", "oktask", "OKtask", "bin", "OKtask"
        );

        if (!Files.exists(launcher)) {
            // Fallback: symlink en ~/.local/bin/
            launcher = Paths.get(
                System.getProperty("user.home"),
                ".local", "bin", "oktask"
            );
        }

        log.info("Ejecutable Linux: {}", launcher);
        ProcessBuilder pb = new ProcessBuilder(launcher.toAbsolutePath().toString());
        pb.inheritIO();
        return pb;
    }

    private ProcessBuilder buildMacOsProcess() {
        // Buscar el .app bundle
        Path appBundle = Paths.get(
            System.getProperty("user.home"),
            "Applications", "OKtask.app"
        );

        if (Files.exists(appBundle)) {
            log.info("Bundle macOS: {}", appBundle);
            return new ProcessBuilder("open", appBundle.toString());
        }

        // Fallback: symlink en /usr/local/bin/
        Path launcher = Paths.get("/usr/local/bin/oktask");
        if (Files.exists(launcher)) {
            return new ProcessBuilder(launcher.toAbsolutePath().toString());
        }

        // Fallback: installPath
        return new ProcessBuilder("open", installPath);
    }

    private ProcessBuilder buildWindowsProcess() {
        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData == null) {
            localAppData = Paths.get(
                System.getProperty("user.home"),
                "AppData", "Local"
            ).toString();
        }

        Path launcher = Paths.get(localAppData, "OKtask", "OKtask.exe");

        if (!Files.exists(launcher)) {
            launcher = Paths.get(installPath, "OKtask.exe");
        }

        log.info("Ejecutable Windows: {}", launcher);
        ProcessBuilder pb = new ProcessBuilder(launcher.toAbsolutePath().toString());
        pb.inheritIO();
        return pb;
    }

    private String detectOS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("linux")) return "linux";
        if (os.contains("mac")) return "macos";
        if (os.contains("win")) return "windows";
        return "unknown";
    }

    public boolean isRestartInProgress() {
        return restartInProgress;
    }

    public int getDelaySeconds() {
        return RESTART_DELAY_SECONDS;
    }
}
