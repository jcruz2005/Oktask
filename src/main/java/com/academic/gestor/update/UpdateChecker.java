package com.academic.gestor.update;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Servicio que verifica actualizaciones consultando el archivo version.json
 * del repositorio de GitHub.
 *
 * <p>Implementa caching en memoria para evitar consultas repetitivas
 * y respeta un intervalo mínimo entre verificaciones.</p>
 *
 * @author OKtask
 * @since 1.2.0
 */
public class UpdateChecker {

    private static final Logger log = LoggerFactory.getLogger(UpdateChecker.class);

    /** URL del archivo version.json en el repositorio (raw.githubusercontent.com). */
    private static final String VERSION_URL =
            "https://raw.githubusercontent.com/jcruz2005/Oktask/main/version.json";

    /** Intervalo mínimo entre verificaciones en horas. */
    private static final int CHECK_INTERVAL_HOURS = 4;

    /** Timeout de conexión en milisegundos. */
    private static final int CONNECT_TIMEOUT_MS = 5000;

    /** Timeout de lectura en milisegundos. */
    private static final int READ_TIMEOUT_MS = 10000;

    /** ObjectMapper para parsear JSON. */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /** Versión actual de la aplicación. */
    private final String currentVersion;

    /** Fuente remota de version.json (sobreescribible para tests). */
    private final String versionUrl;

    /** Última verificación realizada. */
    private final AtomicReference<Instant> lastCheck = new AtomicReference<>(Instant.MIN);

    /** Último resultado de verificación. */
    private final AtomicReference<UpdateInfo> lastResult = new AtomicReference<>();

    /** Callback para notificar cuando se detecta una actualización. */
    private UpdateListener listener;

    /**
     * Interfaz para recibir notificaciones de actualizaciones.
     */
    public interface UpdateListener {
        /**
         * Called when an update is available.
         *
         * @param updateInfo information about the available update
         */
        void onUpdateAvailable(UpdateInfo updateInfo);

        /**
         * Called when no update is available.
         */
        void onNoUpdateAvailable();

        /**
         * Called when an error occurs during the check.
         *
         * @param error the error message
         */
        void onError(String error);
    }

    /**
     * Constructor.
     *
     * @param currentVersion versión actual de la aplicación
     */
    public UpdateChecker(String currentVersion) {
        this(currentVersion, VERSION_URL);
    }

    /**
     * Constructor con URL de version.json personalizada.
     *
     * @param currentVersion versión actual de la aplicación
     * @param versionUrl URL del archivo version.json remoto
     */
    public UpdateChecker(String currentVersion, String versionUrl) {
        this.currentVersion = currentVersion;
        this.versionUrl = versionUrl;
    }

    /**
     * Establece el listener para notificaciones de actualización.
     *
     * @param listener el listener a configurar
     */
    public void setListener(UpdateListener listener) {
        this.listener = listener;
    }

    /**
     * Verifica si hay actualizaciones disponibles.
     *
     * <p>Respeta el intervalo mínimo entre verificaciones.
     * Si la última verificación fue hace menos de CHECK_INTERVAL_HOURS,
     * retorna el resultado缓存ado.</p>
     *
     * @return UpdateInfo si hay una versión disponible, null si no hay o si hay error
     */
    public UpdateInfo checkForUpdate() {
        return checkForUpdate(false);
    }

    /**
     * Verifica si hay actualizaciones disponibles.
     *
     * @param forceCheck true para forzar verificación ignorando el intervalo
     * @return UpdateInfo si hay una versión disponible, null si no hay o si hay error
     */
    public UpdateInfo checkForUpdate(boolean forceCheck) {
        Instant now = Instant.now();
        Instant last = lastCheck.get();

        // Verificar si debemos usar el缓存
        if (!forceCheck && lastResult.get() != null
                && Duration.between(last, now).toHours() < CHECK_INTERVAL_HOURS) {
            log.debug("Usando resultado缓存ado (última verificación: {})", last);
            return lastResult.get();
        }

        log.info("Verificando actualizaciones (versión actual: {})", currentVersion);

        try {
            UpdateInfo remoteInfo = fetchRemoteVersion();

            if (remoteInfo == null || !remoteInfo.isValid()) {
                log.warn("Respuesta inválida del servidor de actualizaciones");
                notifyError("Respuesta inválida del servidor");
                return null;
            }

            lastCheck.set(now);
            lastResult.set(remoteInfo);

            int comparison = remoteInfo.compareTo(currentVersion);

            if (comparison > 0) {
                log.info("Nueva versión disponible: {} (actual: {})",
                        remoteInfo.getVersion(), currentVersion);
                notifyUpdateAvailable(remoteInfo);
                return remoteInfo;
            } else {
                log.info("Ya tienes la última versión: {}", currentVersion);
                notifyNoUpdateAvailable();
                return null;
            }

        } catch (Exception e) {
            log.error("Error al verificar actualizaciones: {}", e.getMessage());
            notifyError("Error de conexión: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene la información de la versión remota desde GitHub.
     *
     * @return UpdateInfo parseado, o null si hay error
     */
    private UpdateInfo fetchRemoteVersion() throws Exception {
        HttpURLConnection connection = null;
        try {
            // Cache-buster para evitar caché de GitHub
            String cacheBuster = versionUrl.contains("?") ? "&t=" : "?t=";
            URL url = new URL(versionUrl + cacheBuster + System.currentTimeMillis());
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("User-Agent", "OKtask-UpdateChecker/1.0");
            connection.setInstanceFollowRedirects(false);

            int responseCode = connection.getResponseCode();

            // Manejar redirecciones (302, 301)
            if (responseCode == 301 || responseCode == 302) {
                String newUrl = connection.getHeaderField("Location");
                if (newUrl != null) {
                    connection.disconnect();
                    connection = (HttpURLConnection) new URL(newUrl).openConnection();
                    connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
                    connection.setReadTimeout(READ_TIMEOUT_MS);
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestProperty("User-Agent", "OKtask-UpdateChecker/1.0");
                    responseCode = connection.getResponseCode();
                }
            }

            if (responseCode != 200) {
                log.warn("HTTP {} al obtener version.json", responseCode);
                return null;
            }

            try (InputStream is = connection.getInputStream()) {
                JsonNode root = objectMapper.readTree(is);

                UpdateInfo info = new UpdateInfo();
                info.setVersion(root.path("version").asText(null));
                info.setReleaseDate(root.path("releaseDate").asText(null));
                info.setDownloadUrl(root.path("downloadUrl").asText(null));
                info.setMinVersion(root.path("minVersion").asText(null));

                // Parsear changelog
                JsonNode changelogNode = root.path("changelog");
                if (changelogNode.isArray()) {
                    List<String> changelog = new ArrayList<>();
                    for (JsonNode item : changelogNode) {
                        changelog.add(item.asText());
                    }
                    info.setChangelog(changelog);
                }

                // Parsear downloads por plataforma
                JsonNode downloadsNode = root.path("downloads");
                if (downloadsNode.isObject()) {
                    java.util.Map<String, UpdateInfo.DownloadInfo> downloads = new java.util.HashMap<>();
                    for (String os : new String[]{"linux", "windows", "macos"}) {
                        JsonNode osNode = downloadsNode.path(os);
                        if (osNode.isObject()) {
                            UpdateInfo.DownloadInfo dlInfo = new UpdateInfo.DownloadInfo();
                            dlInfo.setUrl(osNode.path("url").asText(null));
                            dlInfo.setFilename(osNode.path("filename").asText(null));
                            dlInfo.setInstallCommand(osNode.path("installCommand").asText(null));
                            downloads.put(os, dlInfo);
                        }
                    }
                    info.setDownloads(downloads);

                    // Establecer downloadUrl basado en la plataforma actual si no hay URL genérica
                    if (info.getDownloadUrl() == null) {
                        String os = UpdateInfo.detectOS();
                        UpdateInfo.DownloadInfo platformDl = downloads.get(os);
                        if (platformDl != null) {
                            info.setDownloadUrl(platformDl.getUrl());
                        }
                    }
                }

                return info;
            }

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Obtiene la versión actual de la aplicación.
     *
     * @return versión en formato semver
     */
    public String getCurrentVersion() {
        return currentVersion;
    }

    /**
     * Obtiene la última información de actualización缓存ada.
     *
     * @return UpdateInfo缓存ado, o null si no hay
     */
    public UpdateInfo getLastCheckResult() {
        return lastResult.get();
    }

    /**
     * Limpia el缓存 de verificación.
     */
    public void clearCache() {
        lastCheck.set(Instant.MIN);
        lastResult.set(null);
    }

    private void notifyUpdateAvailable(UpdateInfo info) {
        if (listener != null) {
            listener.onUpdateAvailable(info);
        }
    }

    private void notifyNoUpdateAvailable() {
        if (listener != null) {
            listener.onNoUpdateAvailable();
        }
    }

    private void notifyError(String error) {
        if (listener != null) {
            listener.onError(error);
        }
    }
}
