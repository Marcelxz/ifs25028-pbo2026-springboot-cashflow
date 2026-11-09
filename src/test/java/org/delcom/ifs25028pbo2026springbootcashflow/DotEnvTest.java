package org.delcom.ifs25028pbo2026springbootcashflow;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DotEnvTest {

    private static final List<String> KEYS = List.of(
            "CF_PLAIN", "CF_DQ", "CF_SQ", "CF_MIX1", "CF_MIX2", "CF_SHORT", "CF_EMPTY", "CF_PRESET");

    @TempDir
    Path tempDir;

    @AfterEach
    void cleanUp() {
        KEYS.forEach(System::clearProperty);
    }

    @Test
    void loadReadsKeyValueLinesAndStripsQuotes() throws IOException {
        Path file = tempDir.resolve(".env");
        Files.write(file, List.of(
                "# komentar",
                "",
                "TANPA_SAMA_DENGAN",
                "=tanpa_key",
                "CF_PLAIN=abc",
                "CF_DQ=\"hello\"",
                "CF_SQ='world'",
                "CF_MIX1=\"abc",
                "CF_MIX2='abc",
                "CF_SHORT=x",
                "CF_EMPTY=",
                "PATH=tidak_boleh_menimpa",
                "CF_PRESET=baru"));
        System.setProperty("CF_PRESET", "lama");

        DotEnv.load(file);

        assertEquals("abc", System.getProperty("CF_PLAIN"));
        assertEquals("hello", System.getProperty("CF_DQ"));
        assertEquals("world", System.getProperty("CF_SQ"));
        assertEquals("\"abc", System.getProperty("CF_MIX1"));
        assertEquals("'abc", System.getProperty("CF_MIX2"));
        assertEquals("x", System.getProperty("CF_SHORT"));
        assertEquals("", System.getProperty("CF_EMPTY"));
        assertEquals("lama", System.getProperty("CF_PRESET"));
        assertNull(System.getProperty("PATH"));
    }

    @Test
    void loadIgnoresMissingFile() {
        assertDoesNotThrow(() -> DotEnv.load(tempDir.resolve("tidak-ada.env")));
    }

    @Test
    void loadWithoutArgumentUsesDefaultEnvFile() {
        assertDoesNotThrow((org.junit.jupiter.api.function.Executable) DotEnv::load);
    }
}