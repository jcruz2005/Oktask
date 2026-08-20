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

import java.util.HashMap;
import java.util.Map;

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
    private static final String APP_VERSION = "1.2.7";

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
}
