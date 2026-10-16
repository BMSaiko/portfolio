package eapli.aisafe.flightplanmanagement.domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Validates the format of a flight plan file before DSL processing.
 *
 * <p>This validation runs as a first gate before ANTLR parsing,
 * providing clear error messages for format-level issues such as
 * unrecognized extensions, empty files, encoding problems, or BOM markers.</p>
 */
public class FileFormatValidator {

    private static final String EXPECTED_EXTENSION = ".dsl";

    /**
     * Validates the format of the file at the given path.
     *
     * @param filePath path to the file to validate
     * @return a {@link FormatValidationResult} with the validation outcome
     * @throws IllegalArgumentException if the path is null
     * @throws java.io.IOException if the file cannot be read
     */
    public FormatValidationResult validate(final Path filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path must not be null.");
        }

        final List<String> errors = new ArrayList<>();

        if (!Files.exists(filePath)) {
            errors.add("File does not exist: " + filePath);
            return new FormatValidationResult(false, FileFormatType.INVALID, errors);
        }

        if (!Files.isReadable(filePath)) {
            errors.add("File is not readable: " + filePath);
            return new FormatValidationResult(false, FileFormatType.INVALID, errors);
        }

        final String fileName = filePath.getFileName().toString();
        final String extension = getExtension(fileName);

        if (!EXPECTED_EXTENSION.equalsIgnoreCase(extension)) {
            errors.add("Unrecognized file extension '" + extension + "'. Expected '" + EXPECTED_EXTENSION + "'.");
            return new FormatValidationResult(false, FileFormatType.UNKNOWN, errors);
        }

        final long size = Files.size(filePath);
        if (size == 0) {
            errors.add("File is empty: " + filePath);
            return new FormatValidationResult(false, FileFormatType.INVALID, errors);
        }

        if (hasBom(filePath)) {
            errors.add("File has a BOM (Byte Order Mark) which may cause parsing issues. Save as UTF-8 without BOM.");
            return new FormatValidationResult(false, FileFormatType.INVALID, errors);
        }

        return new FormatValidationResult(true, FileFormatType.FLIGHT_DSL, errors);
    }

    /**
     * Checks whether the file starts with a UTF-8 BOM (EF BB BF).
     *
     * @param filePath path to the file
     * @return true if a BOM is detected
     * @throws IOException if the file cannot be read
     */
    private boolean hasBom(final Path filePath) throws IOException {
        final byte[] header = Files.readAllBytes(filePath);
        return header.length >= 3
                && (header[0] & 0xFF) == 0xEF
                && (header[1] & 0xFF) == 0xBB
                && (header[2] & 0xFF) == 0xBF;
    }

    private static String getExtension(final String fileName) {
        final int lastDot = fileName.lastIndexOf('.');
        if (lastDot < 0) {
            return "";
        }
        return fileName.substring(lastDot);
    }
}

