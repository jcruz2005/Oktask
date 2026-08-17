package com.academic.gestor.update;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para UpdateInfo.
 */
@DisplayName("UpdateInfo")
class UpdateInfoTest {

    @Nested
    @DisplayName("Constructor")
    class Constructor {

        @Test
        @DisplayName("Constructor por defecto inicializa changelog vacío")
        void defaultConstructorInitializesEmptyChangelog() {
            UpdateInfo info = new UpdateInfo();
            assertNotNull(info.getChangelog());
            assertTrue(info.getChangelog().isEmpty());
        }

        @Test
        @DisplayName("Constructor con parámetros asigna todos los campos")
        void parameterizedConstructorAssignsAllFields() {
            UpdateInfo info = new UpdateInfo(
                    "1.2.0", "2026-08-17",
                    Arrays.asList("Bug fix", "New feature"),
                    "https://example.com/download", "1.0.0"
            );

            assertEquals("1.2.0", info.getVersion());
            assertEquals("2026-08-17", info.getReleaseDate());
            assertEquals(2, info.getChangelog().size());
            assertEquals("https://example.com/download", info.getDownloadUrl());
            assertEquals("1.0.0", info.getMinVersion());
        }

        @Test
        @DisplayName("Constructor maneja changelog null")
        void constructorHandlesNullChangelog() {
            UpdateInfo info = new UpdateInfo("1.0.0", null, null, null, null);
            assertNotNull(info.getChangelog());
            assertTrue(info.getChangelog().isEmpty());
        }
    }

    @Nested
    @DisplayName("Comparación de versiones")
    class VersionComparison {

        @Test
        @DisplayName("Versión mayor es detectada correctamente")
        void higherVersionDetected() {
            UpdateInfo info = new UpdateInfo();
            info.setVersion("1.2.0");

            assertTrue(info.compareTo("1.1.0") > 0);
        }

        @Test
        @DisplayName("Versión menor es detectada correctamente")
        void lowerVersionDetected() {
            UpdateInfo info = new UpdateInfo();
            info.setVersion("1.0.0");

            assertTrue(info.compareTo("1.1.0") < 0);
        }

        @Test
        @DisplayName("Versión igual retorna 0")
        void equalVersionReturnsZero() {
            UpdateInfo info = new UpdateInfo();
            info.setVersion("1.2.0");

            assertEquals(0, info.compareTo("1.2.0"));
        }

        @Test
        @DisplayName("Comparación con patch mayor")
        void patchVersionComparison() {
            UpdateInfo info = new UpdateInfo();
            info.setVersion("1.0.2");

            assertTrue(info.compareTo("1.0.1") > 0);
        }

        @Test
        @DisplayName("Comparación con major mayor")
        void majorVersionComparison() {
            UpdateInfo info = new UpdateInfo();
            info.setVersion("2.0.0");

            assertTrue(info.compareTo("1.9.9") > 0);
        }

        @Test
        @DisplayName("Comparación con null retorna 1")
        void comparisonWithNullReturnsOne() {
            UpdateInfo info = new UpdateInfo();
            info.setVersion("1.0.0");

            assertEquals(1, info.compareTo(null));
        }

        @Test
        @DisplayName("Comparación maneja versiones con pre-release")
        void preReleaseComparison() {
            UpdateInfo info = new UpdateInfo();
            info.setVersion("1.0.1");

            assertTrue(info.compareTo("1.0.0-beta") > 0);
        }

        @Test
        @DisplayName("Comparación maneja versiones con partes faltantes")
        void missingPartsComparison() {
            UpdateInfo info = new UpdateInfo();
            info.setVersion("1.0");

            assertTrue(info.compareTo("1.0.0") == 0);
        }
    }

    @Nested
    @DisplayName("Validación")
    class Validation {

        @Test
        @DisplayName("UpdateInfo con versión y URL es válido")
        void validUpdateInfo() {
            UpdateInfo info = new UpdateInfo();
            info.setVersion("1.2.0");
            info.setDownloadUrl("https://example.com/download");

            assertTrue(info.isValid());
        }

        @Test
        @DisplayName("UpdateInfo sin versión no es válido")
        void invalidWithoutVersion() {
            UpdateInfo info = new UpdateInfo();
            info.setDownloadUrl("https://example.com/download");

            assertFalse(info.isValid());
        }

        @Test
        @DisplayName("UpdateInfo sin URL no es válido")
        void invalidWithoutUrl() {
            UpdateInfo info = new UpdateInfo();
            info.setVersion("1.2.0");

            assertFalse(info.isValid());
        }

        @Test
        @DisplayName("UpdateInfo vacío no es válido")
        void emptyUpdateInfoIsInvalid() {
            UpdateInfo info = new UpdateInfo();
            assertFalse(info.isValid());
        }
    }

    @Nested
    @DisplayName("Equals y HashCode")
    class EqualsHashCode {

        @Test
        @DisplayName("Mismas versiones son iguales")
        void sameVersionsAreEqual() {
            UpdateInfo info1 = new UpdateInfo();
            info1.setVersion("1.2.0");

            UpdateInfo info2 = new UpdateInfo();
            info2.setVersion("1.2.0");

            assertEquals(info1, info2);
            assertEquals(info1.hashCode(), info2.hashCode());
        }

        @Test
        @DisplayName("Diferentes versiones no son iguales")
        void differentVersionsNotEqual() {
            UpdateInfo info1 = new UpdateInfo();
            info1.setVersion("1.2.0");

            UpdateInfo info2 = new UpdateInfo();
            info2.setVersion("1.3.0");

            assertNotEquals(info1, info2);
        }
    }
}
