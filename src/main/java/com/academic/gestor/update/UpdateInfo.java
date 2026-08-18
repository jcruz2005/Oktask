package com.academic.gestor.update;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Modelo de datos para la información de una versión de la aplicación.
 *
 * @author OKtask
 * @since 1.2.0
 */
public class UpdateInfo {

    private String version;
    private String releaseDate;
    private List<String> changelog;
    private String downloadUrl;
    private String minVersion;
    private Map<String, DownloadInfo> downloads;

    public UpdateInfo() {
        this.changelog = Collections.emptyList();
        this.downloads = Collections.emptyMap();
    }

    public UpdateInfo(String version, String releaseDate, List<String> changelog,
                      String downloadUrl, String minVersion) {
        this.version = version;
        this.releaseDate = releaseDate;
        this.changelog = changelog != null ? changelog : Collections.emptyList();
        this.downloadUrl = downloadUrl;
        this.minVersion = minVersion;
        this.downloads = Collections.emptyMap();
    }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public List<String> getChangelog() { return changelog; }
    public void setChangelog(List<String> changelog) {
        this.changelog = changelog != null ? changelog : Collections.emptyList();
    }

    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

    public String getMinVersion() { return minVersion; }
    public void setMinVersion(String minVersion) { this.minVersion = minVersion; }

    public Map<String, DownloadInfo> getDownloads() { return downloads; }
    public void setDownloads(Map<String, DownloadInfo> downloads) {
        this.downloads = downloads != null ? downloads : Collections.emptyMap();
    }

    /**
     * Obtiene la información de descarga para la plataforma actual.
     *
     * @return DownloadInfo para la plataforma, o null si no hay
     */
    public DownloadInfo getDownloadForCurrentPlatform() {
        String os = detectOS();
        return downloads.get(os);
    }

    /**
     * Detecta el sistema operativo actual.
     *
     * @return "linux", "windows" o "macos"
     */
    public static String detectOS() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) return "windows";
        if (os.contains("mac") || os.contains("darwin")) return "macos";
        return "linux";
    }

    public int compareTo(String other) {
        if (other == null) return 1;

        String[] thisParts = this.version != null ? this.version.split("\\.") : new String[]{"0"};
        String[] otherParts = other.split("\\.");

        int maxLength = Math.max(thisParts.length, otherParts.length);

        for (int i = 0; i < maxLength; i++) {
            int thisPart = i < thisParts.length ? parseVersionPart(thisParts[i]) : 0;
            int otherPart = i < otherParts.length ? parseVersionPart(otherParts[i]) : 0;

            if (thisPart != otherPart) {
                return Integer.compare(thisPart, otherPart);
            }
        }
        return 0;
    }

    private int parseVersionPart(String part) {
        if (part == null || part.isEmpty()) return 0;
        String numeric = part.split("-")[0];
        try {
            return Integer.parseInt(numeric);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public boolean isValid() {
        return version != null && !version.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateInfo that = (UpdateInfo) o;
        return Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version);
    }

    @Override
    public String toString() {
        return "UpdateInfo{version='" + version + "', releaseDate='" + releaseDate + "'}";
    }

    /**
     * Modelo para información de descarga por plataforma.
     */
    public static class DownloadInfo {
        private String url;
        private String filename;
        private String installCommand;

        public DownloadInfo() {}

        public DownloadInfo(String url, String filename, String installCommand) {
            this.url = url;
            this.filename = filename;
            this.installCommand = installCommand;
        }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }

        public String getFilename() { return filename; }
        public void setFilename(String filename) { this.filename = filename; }

        public String getInstallCommand() { return installCommand; }
        public void setInstallCommand(String installCommand) { this.installCommand = installCommand; }
    }
}
