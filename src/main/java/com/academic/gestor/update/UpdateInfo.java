package com.academic.gestor.update;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Modelo de datos para la información de una versión de la aplicación.
 *
 * <p>Representa los datos parseados desde el archivo version.json
 * ubicado en el repositorio de GitHub.</p>
 *
 * @author OKtask
 * @since 1.2.0
 */
public class UpdateInfo {

    /** Versión de la aplicación en formato semver (MAJOR.MINOR.PATCH). */
    private String version;

    /** Fecha de release en formato ISO (YYYY-MM-DD). */
    private String releaseDate;

    /** Lista de cambios de la versión. */
    private List<String> changelog;

    /** URL de descarga del instalador. */
    private String downloadUrl;

    /** Versión mínima compatible con esta actualización. */
    private String minVersion;

    /**
     * Constructor por defecto.
     */
    public UpdateInfo() {
        this.changelog = Collections.emptyList();
    }

    /**
     * Constructor con todos los campos.
     *
     * @param version     versión en formato semver
     * @param releaseDate fecha de release
     * @param changelog   lista de cambios
     * @param downloadUrl URL de descarga
     * @param minVersion  versión mínima compatible
     */
    public UpdateInfo(String version, String releaseDate, List<String> changelog,
                      String downloadUrl, String minVersion) {
        this.version = version;
        this.releaseDate = releaseDate;
        this.changelog = changelog != null ? changelog : Collections.emptyList();
        this.downloadUrl = downloadUrl;
        this.minVersion = minVersion;
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

    /**
     * Compara esta versión con otra usando Semantic Versioning.
     *
     * @param other versión a comparar (formato "X.Y.Z")
     * @return -1 si esta versión es menor, 0 si son iguales, 1 si es mayor
     */
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

    /**
     * Parsea una parte de la versión manejando pre-release labels.
     *
     * @param part parte de la versión (ej: "0", "1", "2-beta")
     * @return valor numérico para comparación
     */
    private int parseVersionPart(String part) {
        if (part == null || part.isEmpty()) return 0;
        // Tomar solo la parte numérica antes de cualquier guión
        String numeric = part.split("-")[0];
        try {
            return Integer.parseInt(numeric);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Verifica si esta información de actualización es válida.
     *
     * @return true si tiene versión y URL de descarga
     */
    public boolean isValid() {
        return version != null && !version.isEmpty()
                && downloadUrl != null && !downloadUrl.isEmpty();
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
}
