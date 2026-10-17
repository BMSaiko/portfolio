package eapli.aisafe.weather.application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

/**
 * Weather data importer that automatically detects the supported CSV format and
 * delegates parsing to the appropriate provider-specific importer.
 *
 * <p>
 * This class supports the extensibility requirement of US042 by keeping format
 * detection separate from the actual parsing logic. The controller can depend
 * on a single {@link WeatherDataImporter}, while this implementation chooses
 * the correct concrete importer based on the file header.
 * </p>
 *
 * <p>
 * Currently supported formats are:
 * </p>
 *
 * <ul>
 *     <li>Standard AISafe weather CSV, handled by {@link CsvWeatherDataImporter};</li>
 *     <li>CW wind provider CSV, handled by {@link CwWindWeatherDataImporter}.</li>
 * </ul>
 */
public class AutoDetectWeatherDataImporter implements WeatherDataImporter {

    /**
     * Importer used for the standard AISafe weather CSV format.
     */
    private final WeatherDataImporter simpleCsvImporter;

    /**
     * Importer used for the CW wind provider CSV format.
     */
    private final WeatherDataImporter cwWindImporter;

    /**
     * Creates an auto-detecting importer.
     *
     * @param simpleCsvImporter importer for the standard weather CSV format
     * @param cwWindImporter importer for the CW wind provider format
     * @throws IllegalArgumentException if any importer is {@code null}
     */
    public AutoDetectWeatherDataImporter(WeatherDataImporter simpleCsvImporter,
                                         WeatherDataImporter cwWindImporter) {
        if (simpleCsvImporter == null || cwWindImporter == null) {
            throw new IllegalArgumentException("Importers cannot be null.");
        }

        this.simpleCsvImporter = simpleCsvImporter;
        this.cwWindImporter = cwWindImporter;
    }

    /**
     * Detects the weather data CSV format and parses the file using the matching
     * importer.
     *
     * <p>
     * The detection is based on the first line of the file, usually the header.
     * The line is normalized before comparison so that leading/trailing spaces,
     * byte order marks and letter casing do not affect format detection.
     * </p>
     *
     * @param path path to the weather data file
     * @return parsed weather import records
     * @throws IOException if the file cannot be read
     * @throws IllegalArgumentException if the file is empty
     * @throws IllegalArgumentException if the first line does not match any
     *                                  supported weather data CSV format
     */
    @Override
    public List<WeatherImportRecord> parse(Path path) throws IOException {
        String firstLine = Files.lines(path)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Weather data file is empty."));

        String normalized = normalize(firstLine);

        if (normalized.startsWith("AREA_CODE")) {
            return simpleCsvImporter.parse(path);
        }

        if (normalized.startsWith("ACA,")
                && normalized.contains("DIRECTION")
                && normalized.contains("VALUE")
                && normalized.contains("DAY")) {
            return cwWindImporter.parse(path);
        }

        throw new IllegalArgumentException("Unsupported weather data CSV format. First line was: " + firstLine);
    }

    /**
     * Normalizes a CSV header line before format detection.
     *
     * <p>
     * The normalization removes a possible UTF-8 byte order mark, trims
     * surrounding whitespace and converts the line to upper case using
     * {@link Locale#ROOT}.
     * </p>
     *
     * @param line the line to normalize
     * @return normalized line
     */
    private String normalize(String line) {
        return line
                .replace("\uFEFF", "")
                .trim()
                .toUpperCase(Locale.ROOT);
    }
}