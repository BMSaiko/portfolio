package eapli.aisafe.flightplanmanagement.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FileFormatValidator}.
 */
class FileFormatValidatorTest {

    private FileFormatValidator validator;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        validator = new FileFormatValidator();
    }

    // ── Valid files ───────────────────────────────────────────────────────────

    @Test
    void validate_validDslFile_returnsSuccess() throws IOException {
        final Path file = tempDir.resolve("valid.dsl");
        Files.writeString(file, "flight FP_TEST {\n    type: regular;\n}\n");

        final FormatValidationResult result = validator.validate(file);

        assertTrue(result.isValid());
        assertEquals(FileFormatType.FLIGHT_DSL, result.formatType());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    void validate_uppercaseExtension_returnsSuccess() throws IOException {
        final Path file = tempDir.resolve("UPPER.DSL");
        Files.writeString(file, "flight FP_TEST {\n    type: regular;\n}\n");

        final FormatValidationResult result = validator.validate(file);

        assertTrue(result.isValid());
        assertEquals(FileFormatType.FLIGHT_DSL, result.formatType());
    }

    // ── Wrong extension ──────────────────────────────────────────────────────

    @Test
    void validate_wrongExtension_returnsUnknown() throws IOException {
        final Path file = tempDir.resolve("document.txt");
        Files.writeString(file, "flight FP_TEST {\n    type: regular;\n}\n");

        final FormatValidationResult result = validator.validate(file);

        assertFalse(result.isValid());
        assertEquals(FileFormatType.UNKNOWN, result.formatType());
        assertFalse(result.errors().isEmpty());
        assertTrue(result.errors().get(0).contains("extension"));
    }

    @Test
    void validate_noExtension_returnsUnknown() throws IOException {
        final Path file = tempDir.resolve("noextension");
        Files.writeString(file, "flight FP_TEST {\n    type: regular;\n}\n");

        final FormatValidationResult result = validator.validate(file);

        assertFalse(result.isValid());
        assertEquals(FileFormatType.UNKNOWN, result.formatType());
    }

    // ── Empty file ────────────────────────────────────────────────────────────

    @Test
    void validate_emptyFile_returnsInvalid() throws IOException {
        final Path file = tempDir.resolve("empty.dsl");
        Files.writeString(file, "");

        final FormatValidationResult result = validator.validate(file);

        assertFalse(result.isValid());
        assertEquals(FileFormatType.INVALID, result.formatType());
        assertTrue(result.errors().get(0).contains("empty"));
    }

    // ── Non-existent file ─────────────────────────────────────────────────────

    @Test
    void validate_nonExistentFile_returnsInvalid() throws IOException {
        final Path file = tempDir.resolve("nonexistent.dsl");

        final FormatValidationResult result = validator.validate(file);

        assertFalse(result.isValid());
        assertEquals(FileFormatType.INVALID, result.formatType());
        assertTrue(result.errors().get(0).contains("does not exist"));
    }

    // ── Null path ─────────────────────────────────────────────────────────────

    @Test
    void validate_nullPath_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> validator.validate(null));
    }

    // ── BOM detection ─────────────────────────────────────────────────────────

    @Test
    void validate_bomFile_returnsInvalid() throws IOException {
        final Path file = tempDir.resolve("bom.dsl");
        byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] content = "flight FP_TEST {\n    type: regular;\n}\n".getBytes();
        byte[] combined = new byte[bom.length + content.length];
        System.arraycopy(bom, 0, combined, 0, bom.length);
        System.arraycopy(content, 0, combined, bom.length, content.length);
        Files.write(file, combined);

        final FormatValidationResult result = validator.validate(file);

        assertFalse(result.isValid());
        assertEquals(FileFormatType.INVALID, result.formatType());
        assertTrue(result.errors().get(0).contains("BOM"));
    }

    // ── FormatValidationResult value object ───────────────────────────────────

    @Test
    void formatValidationResult_equals_sameValues_areEqual() {
        final FormatValidationResult r1 = new FormatValidationResult(
                true, FileFormatType.FLIGHT_DSL, java.util.List.of());
        final FormatValidationResult r2 = new FormatValidationResult(
                true, FileFormatType.FLIGHT_DSL, java.util.List.of());

        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void formatValidationResult_equals_differentValues_areNotEqual() {
        final FormatValidationResult r1 = new FormatValidationResult(
                true, FileFormatType.FLIGHT_DSL, java.util.List.of());
        final FormatValidationResult r2 = new FormatValidationResult(
                false, FileFormatType.UNKNOWN, java.util.List.of("error"));

        assertNotEquals(r1, r2);
    }

    @Test
    void formatValidationResult_errors_returnsUnmodifiableList() {
        final FormatValidationResult result = new FormatValidationResult(
                false, FileFormatType.INVALID, java.util.List.of("error1"));

        assertThrows(UnsupportedOperationException.class, () ->
                result.errors().add("error2"));
    }
}
