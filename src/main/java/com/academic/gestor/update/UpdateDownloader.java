package com.academic.gestor.update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Servicio de descarga de actualizaciones con progreso.
 *
 * <p>Descarga el instalador desde la URL proporcionada y reporta
 * el progreso a un listener.</p>
 *
 * @author OKtask
 * @since 1.2.0
 */
public class UpdateDownloader {

    private static final Logger log = LoggerFactory.getLogger(UpdateDownloader.class);

    /** Directorio donde se almacenan las descargas. */
    private static final String DOWNLOAD_DIR = ".oktask/updates";

    /** Timeout de conexión en milisegundos. */
    private static final int CONNECT_TIMEOUT_MS = 10000;

    /** Timeout de lectura en milisegundos. */
    private static final int READ_TIMEOUT_MS = 30000;

    /** Tamaño del buffer de lectura. */
    private static final int BUFFER_SIZE = 8192;

    /** Listener de progreso. */
    private ProgressListener listener;

    /** Flag para cancelar la descarga. */
    private volatile boolean cancelled = false;

    /** Conexión HTTP actual (para poder cancelar). */
    private volatile HttpURLConnection currentConnection;

    /**
     * Interfaz para recibir actualizaciones de progreso.
     */
    public interface ProgressListener {
        /**
         * Called when download starts.
         *
         * @param totalBytes total bytes to download (-1 if unknown)
         */
        void onDownloadStarted(long totalBytes);

        /**
         * Called when progress is updated.
         *
         * @param bytesDownloaded bytes downloaded so far
         * @param totalBytes total bytes to download (-1 if unknown)
         * @param percentage percentage complete (0-100)
         */
        void onProgressUpdate(long bytesDownloaded, long totalBytes, int percentage);

        /**
         * Called when download completes.
         *
         * @param filePath path to the downloaded file
         */
        void onDownloadComplete(String filePath);

        /**
         * Called when download fails.
         *
         * @param error error message
         */
        void onDownloadFailed(String error);

        /**
         * Called when download is cancelled.
         */
        void onDownloadCancelled();
    }

    /**
     * Constructor.
     */
    public UpdateDownloader() {
    }

    /**
     * Establece el listener de progreso.
     *
     * @param listener el listener a configurar
     */
    public void setListener(ProgressListener listener) {
        this.listener = listener;
    }

    /**
     * Descarga un archivo desde la URL proporcionada.
     *
     * @param urlStr URL de descarga
     * @param fileName nombre del archivo local
     * @return ruta completa del archivo descargado, o null si falló
     */
    public String download(String urlStr, String fileName) {
        cancelled = false;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            // Crear directorio de descargas
            String userHome = System.getProperty("user.home");
            Path downloadDir = Paths.get(userHome, DOWNLOAD_DIR);
            if (!Files.exists(downloadDir)) {
                Files.createDirectories(downloadDir);
            }

            File outputFile = downloadDir.resolve(fileName).toFile();
            log.info("Descargando {} a {}", urlStr, outputFile.getAbsolutePath());

            // Conectar
            URL url = new URL(urlStr);
            currentConnection = (HttpURLConnection) url.openConnection();
            currentConnection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            currentConnection.setReadTimeout(READ_TIMEOUT_MS);
            currentConnection.setRequestMethod("GET");
            currentConnection.setRequestProperty("User-Agent", "OKtask-Updater/1.0");
            currentConnection.setInstanceFollowRedirects(true);

            int responseCode = currentConnection.getResponseCode();

            // Manejar redirecciones
            if (responseCode == 301 || responseCode == 302 || responseCode == 307) {
                String newUrl = currentConnection.getHeaderField("Location");
                if (newUrl != null) {
                    currentConnection.disconnect();
                    currentConnection = (HttpURLConnection) new URL(newUrl).openConnection();
                    currentConnection.setConnectTimeout(CONNECT_TIMEOUT_MS);
                    currentConnection.setReadTimeout(READ_TIMEOUT_MS);
                    currentConnection.setRequestProperty("User-Agent", "OKtask-Updater/1.0");
                    responseCode = currentConnection.getResponseCode();
                }
            }

            if (responseCode != 200) {
                String error = "Error HTTP " + responseCode;
                log.error(error);
                notifyFailed(error);
                return null;
            }

            // Obtener tamaño total
            long totalBytes = currentConnection.getContentLengthLong();
            log.info("Tamaño del archivo: {} bytes", totalBytes);

            notifyStarted(totalBytes);

            // Descargar
            inputStream = currentConnection.getInputStream();
            outputStream = new FileOutputStream(outputFile);

            byte[] buffer = new byte[BUFFER_SIZE];
            long bytesDownloaded = 0;
            int bytesRead;
            int lastPercentage = 0;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                if (cancelled) {
                    log.info("Descarga cancelada");
                    notifyCancelled();
                    // Limpiar archivo parcial
                    outputFile.delete();
                    return null;
                }

                outputStream.write(buffer, 0, bytesRead);
                bytesDownloaded += bytesRead;

                // Calcular porcentaje
                int percentage = 0;
                if (totalBytes > 0) {
                    percentage = (int) ((bytesDownloaded * 100) / totalBytes);
                }

                // Notificar progreso solo si cambió
                if (percentage > lastPercentage) {
                    lastPercentage = percentage;
                    notifyProgress(bytesDownloaded, totalBytes, percentage);
                }
            }

            outputStream.flush();
            outputStream.close();
            outputStream = null;

            log.info("Descarga completada: {} bytes", bytesDownloaded);
            notifyComplete(outputFile.getAbsolutePath());

            return outputFile.getAbsolutePath();

        } catch (Exception e) {
            log.error("Error durante la descarga: {}", e.getMessage());
            notifyFailed("Error de descarga: " + e.getMessage());
            return null;

        } finally {
            if (inputStream != null) {
                try { inputStream.close(); } catch (Exception ignored) {}
            }
            if (outputStream != null) {
                try { outputStream.close(); } catch (Exception ignored) {}
            }
            if (currentConnection != null) {
                currentConnection.disconnect();
            }
        }
    }

    /**
     * Cancela la descarga en curso.
     */
    public void cancel() {
        cancelled = true;
        if (currentConnection != null) {
            currentConnection.disconnect();
        }
    }

    /**
     * Obtiene el directorio de descargas.
     *
     * @return ruta del directorio de descargas
     */
    public String getDownloadDirectory() {
        String userHome = System.getProperty("user.home");
        return Paths.get(userHome, DOWNLOAD_DIR).toString();
    }

    private void notifyStarted(long totalBytes) {
        if (listener != null) listener.onDownloadStarted(totalBytes);
    }

    private void notifyProgress(long downloaded, long total, int percentage) {
        if (listener != null) listener.onProgressUpdate(downloaded, total, percentage);
    }

    private void notifyComplete(String filePath) {
        if (listener != null) listener.onDownloadComplete(filePath);
    }

    private void notifyFailed(String error) {
        if (listener != null) listener.onDownloadFailed(error);
    }

    private void notifyCancelled() {
        if (listener != null) listener.onDownloadCancelled();
    }
}
