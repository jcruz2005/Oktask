package com.oktask.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Plugin nativo para descargar e instalar APKs directamente desde la app.
 */
@CapacitorPlugin(name = "ApkInstaller")
public class ApkInstallerPlugin extends Plugin {

    /**
     * Descarga un APK desde una URL y lo abre con el instalador de paquetes de Android.
     * @param call Debe incluir "url" (a descargar) y "fileName" (nombre del archivo local).
     */
    @PluginMethod
    public void downloadAndInstall(PluginCall call) {
        String urlStr = call.getString("url");
        String fileName = call.getString("fileName", "update.apk");

        if (urlStr == null || urlStr.isEmpty()) {
            call.reject("Se requiere la URL del APK (url)");
            return;
        }

        // Ejecutar descarga en hilo background
        new Thread(() -> {
            try {
                Context context = getContext();
                File cacheDir = new File(context.getCacheDir(), "updates");
                if (!cacheDir.exists()) cacheDir.mkdirs();

                File apkFile = new File(cacheDir, fileName);

                // Descargar con HttpURLConnection (maneja redirects nativamente)
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setInstanceFollowRedirects(true);
                conn.connect();

                int responseCode = conn.getResponseCode();

                // Seguir redirects manualmente si es necesario
                if (responseCode == 301 || responseCode == 302) {
                    String newUrl = conn.getHeaderField("Location");
                    conn.disconnect();
                    url = new URL(newUrl);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setInstanceFollowRedirects(true);
                    conn.connect();
                    responseCode = conn.getResponseCode();
                }

                if (responseCode != 200) {
                    call.reject("Error HTTP: " + responseCode);
                    return;
                }

                int fileLength = conn.getContentLength();

                // Descargar archivo
                InputStream input = conn.getInputStream();
                FileOutputStream output = new FileOutputStream(apkFile);

                byte[] buffer = new byte[8192];
                long total = 0;
                int count;
                int lastProgress = 0;

                while ((count = input.read(buffer)) != -1) {
                    total += count;
                    output.write(buffer, 0, count);

                    // Notificar progreso cada 5%
                    if (fileLength > 0) {
                        int progress = (int) (total * 100 / fileLength);
                        if (progress - lastProgress >= 5) {
                            lastProgress = progress;
                            JSObject progressData = new JSObject();
                            progressData.put("progress", progress);
                            progressData.put("downloaded", total);
                            progressData.put("total", fileLength);
                            // Usar save para emitir evento (si es posible)
                        }
                    }
                }

                output.flush();
                output.close();
                input.close();
                conn.disconnect();

                // Abrir con instalador de paquetes
                Uri apkUri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".fileprovider",
                    apkFile
                );

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                getActivity().startActivity(intent);

                JSObject result = new JSObject();
                result.put("success", true);
                result.put("filePath", apkFile.getAbsolutePath());
                call.resolve(result);

            } catch (Exception e) {
                call.reject("Error: " + e.getMessage(), e);
            }
        }).start();
    }

    /**
     * Abre un archivo APK ya descargado con el instalador de paquetes.
     * @param call Debe incluir "filePath" con la ruta absoluta al archivo APK.
     */
    @PluginMethod
    public void installApk(PluginCall call) {
        String filePath = call.getString("filePath");
        if (filePath == null || filePath.isEmpty()) {
            call.reject("Se requiere la ruta del archivo APK (filePath)");
            return;
        }

        File apkFile = new File(filePath);
        if (!apkFile.exists()) {
            call.reject("El archivo APK no existe: " + filePath);
            return;
        }

        try {
            Context context = getContext();
            Uri apkUri = FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".fileprovider",
                apkFile
            );

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            getActivity().startActivity(intent);

            JSObject result = new JSObject();
            result.put("success", true);
            result.put("filePath", filePath);
            call.resolve(result);
        } catch (Exception e) {
            call.reject("Error al abrir el instalador: " + e.getMessage(), e);
        }
    }
}
