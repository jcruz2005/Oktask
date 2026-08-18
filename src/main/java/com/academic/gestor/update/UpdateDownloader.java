package com.academic.gestor.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Descargador de actualizaciones con soporte multiplataforma.
 * Descarga el paquete correcto según el sistema operativo.
 *
 * @author OKtask
 * @since 1.2.0
 */
public class UpdateDownloader {

    private static final int CONNECT_TIMEOUT_MS = 10000;
    private static final int READ_TIMEOUT_MS = 30000;
    private static final int BUFFER_SIZE = 8192;

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

        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        File outputFile = new File(targetDir, filename);

        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
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

                    fos.write(buffer, 0, bytesRead);
                    downloadedBytes += bytesRead;

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
     * Interfaz para escuchar el progreso de descarga.
     */
    public interface DownloadProgressListener {
        void onProgress(int percent, long downloaded, long total);
        void onComplete(File file);
        default void onError(Exception e) {}
    }
}
