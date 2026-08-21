package com.academic.gestor.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Descargador de actualizaciones con soporte multiplataforma.
 * Descarga el paquete correcto según el sistema operativo.
 *
 * <p>Valida que la URL de descarga use HTTPS, sanitiza el nombre del
 * archivo destino para prevenir path traversal y limita el tamaño
 * de la descarga para evitar abusos.</p>
 *
 * @author OKtask
 * @since 1.2.0
 */
public class UpdateDownloader {

    private static final int CONNECT_TIMEOUT_MS = 10000;
    private static final int READ_TIMEOUT_MS = 30000;
    private static final int BUFFER_SIZE = 8192;

    /** Tamaño máximo de descarga aceptado (500 MB). */
    private static final long MAX_DOWNLOAD_BYTES = 500L * 1024 * 1024;

    /** Extensiones de archivo permitidas para los instaladores. */
    private static final String[] ALLOWED_EXTENSIONS = {".tar.gz", ".msi", ".dmg", ".exe", ".zip", ".pkg"};

    private DownloadProgressListener listener;
    private volatile boolean cancelled = false;

    public UpdateDownloader() {
        this.listener = null;
    }

    public UpdateDownloader(DownloadProgressListener listener) {
        this.listener = listener;
    }

    public void setListener(DownloadProgressListener listener) {
        this.listener = listener;
    }

    public void cancel() {
        this.cancelled = true;
    }

    /**
     * Descarga la actualización correcta para la plataforma actual.
     *
     * @param info información de la actualización
     * @param targetDir directorio destino para la descarga
     * @return archivo descargado, o null si hubo error o cancelación
     */
    public File downloadForCurrentPlatform(UpdateInfo info, File targetDir) throws Exception {
        if (info == null || !info.isValid()) {
            throw new IllegalArgumentException("Información de actualización inválida");
        }

        String os = UpdateInfo.detectOS();
        UpdateInfo.DownloadInfo downloadInfo = info.getDownloads().get(os);

        if (downloadInfo == null || downloadInfo.getUrl() == null) {
            throw new IllegalStateException("No hay descarga disponible para " + os);
        }

        return download(downloadInfo.getUrl(), downloadInfo.getFilename(), targetDir);
    }

    /**
     * Descarga un archivo desde una URL con seguimiento de progreso.
     *
     * @param urlString URL de descarga
     * @param filename nombre del archivo destino
     * @param targetDir directorio destino
     * @return archivo descargado, o null si hubo error o cancelación
     */
    public File download(String urlString, String filename, File targetDir) throws Exception {
        if (urlString == null || urlString.isEmpty()) {
            throw new IllegalArgumentException("URL de descarga no válida");
        }

        String safeFilename = sanitizeFilename(filename);
        if (safeFilename == null) {
            throw new IllegalArgumentException("Nombre de archivo de descarga inválido");
        }

        URL url = new URL(urlString);
        validateDownloadUrl(url);

        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        File outputFile = new File(targetDir, safeFilename);

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "OKtask-UpdateDownloader/1.0");
            connection.setInstanceFollowRedirects(false);

            int responseCode = connection.getResponseCode();

            // Manejar redirecciones (302, 301)
            if (responseCode == 301 || responseCode == 302) {
                String newUrl = connection.getHeaderField("Location");
                if (newUrl != null) {
                    connection.disconnect();
                    url = new URL(newUrl);
                    validateDownloadUrl(url);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
                    connection.setReadTimeout(READ_TIMEOUT_MS);
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("User-Agent", "OKtask-UpdateDownloader/1.0");
                    responseCode = connection.getResponseCode();
                }
            }

            if (responseCode != 200) {
                throw new Exception("Error HTTP " + responseCode + " al descargar");
            }

            long totalBytes = connection.getContentLengthLong();
            if (totalBytes > MAX_DOWNLOAD_BYTES) {
                throw new Exception("El archivo de descarga excede el tamaño máximo permitido");
            }
            if (totalBytes <= 0) {
                totalBytes = -1; // Desconocido
            }

            try (InputStream is = connection.getInputStream();
                 FileOutputStream fos = new FileOutputStream(outputFile)) {

                byte[] buffer = new byte[BUFFER_SIZE];
                long downloadedBytes = 0;
                int bytesRead;

                while ((bytesRead = is.read(buffer)) != -1) {
                    if (cancelled) {
                        fos.close();
                        outputFile.delete();
                        return null;
                    }

                    downloadedBytes += bytesRead;
                    if (downloadedBytes > MAX_DOWNLOAD_BYTES) {
                        fos.close();
                        outputFile.delete();
                        throw new Exception("El archivo de descarga excede el tamaño máximo permitido");
                    }

                    fos.write(buffer, 0, bytesRead);

                    if (listener != null) {
                        int progress = totalBytes > 0
                                ? (int) ((downloadedBytes * 100) / totalBytes)
                                : -1;
                        listener.onProgress(progress, downloadedBytes, totalBytes);
                    }
                }

                fos.flush();
            }

            if (listener != null) {
                listener.onComplete(outputFile);
            }

            return outputFile;

        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * Sanitiza el nombre de archivo para prevenir path traversal.
     * Elimina separadores de ruta, componentes ".." y caracteres peligrosos.
     *
     * @param filename nombre original (posiblemente provisto por un servidor remoto)
     * @return nombre seguro, o null si el nombre es inválido
     */
    static String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return null;
        }

        String original = filename.replace('\\', '/').trim();

        // Rechazar cualquier intento de path traversal (componentes "..")
        if (original.contains("..")) {
            return null;
        }

        // Tomar solo el último componente de la ruta (descarta directorios)
        String name = original.substring(original.lastIndexOf('/') + 1);

        if (name.isEmpty() || name.equals(".") || name.equals("..")) {
            return null;
        }

        // Solo permitir caracteres alfanuméricos, puntos, guiones y guiones bajos
        if (!name.matches("[A-Za-z0-9._\\-]+")) {
            return null;
        }

        // Validar extensión conocida de instalador
        String lower = name.toLowerCase(Locale.ROOT);
        for (String ext : ALLOWED_EXTENSIONS) {
            if (lower.endsWith(ext)) {
                return name;
            }
        }
        return null;
    }

    /**
     * Valida que la URL de descarga use HTTPS.
     *
     * @param url URL a validar
     * @throws IllegalArgumentException si la URL no es HTTPS
     */
    private void validateDownloadUrl(URL url) {
        if (!"https".equalsIgnoreCase(url.getProtocol())) {
            throw new IllegalArgumentException("La URL de descarga debe usar HTTPS");
        }
    }

    /**
     * Interfaz para escuchar el progreso de descarga.
     */
    public interface DownloadProgressListener {
        void onProgress(int percent, long downloaded, long total);
        void onComplete(File file);
        default void onError(Exception e) {}
    }
}
