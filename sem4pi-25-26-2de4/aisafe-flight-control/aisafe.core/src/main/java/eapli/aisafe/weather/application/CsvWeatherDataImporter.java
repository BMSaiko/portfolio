package eapli.aisafe.weather.application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Weather data importer for the standard CSV format used by the initial
 * weather import implementation.
 *
 * <p>
 * This importer reads a comma-separated file and converts each valid line into
 * a {@link WeatherImportRecord}. It is intended to support the simple CSV
 * format defined for the first version of US042 while keeping the import
 * mechanism extensible for other providers.
 * </p>
 *
 * <p>
 * Expected column order:
 * </p>
 *
 * <pre>
 * AREA_CODE,START,END,TEMPERATURE,WIND_SPEED,WIND_DIRECTION,HUMIDITY,PRESSURE,PROVIDER
 * </pre>
 *
 * <p>
 * The {@code PROVIDER} column is optional. When it is not present, the provider
 * is set to {@code UNKNOWN}. Date-time values must use the
 * {@code yyyy-MM-dd HH:mm} format.
 * </p>
 */
public class CsvWeatherDataImporter implements WeatherDataImporter {

    /**
     * Date-time format expected in the standard CSV weather import file.
     */
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.ROOT);

    /**
     * Parses a standard weather CSV file into weather import records.
     *
     * <p>
     * Empty lines are ignored. If the first line starts with
     * {@code AREA_CODE}, it is treated as a header and skipped.
     * </p>
     *
     * <p>
     * This method performs structural parsing only. Domain validation, such as
     * checking temperature ranges or whether the air control area exists, is
     * performed later by {@link ImportWeatherDataController}.
     * </p>
     *
     * @param path path to the CSV file
     * @return parsed weather import records
     * @throws IOException if the file cannot be read
     * @throws IllegalArgumentException if a non-empty data line has fewer than
     *                                  eight required columns
     * @throws java.time.format.DateTimeParseException if a date-time value does
     *                                                 not match the expected
     *                                                 format
     * @throws NumberFormatException if a numeric column cannot be parsed
     */
    @Override
    public List<WeatherImportRecord> parse(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        List<WeatherImportRecord> records = new ArrayList<>();

        int startIndex = hasHeader(lines) ? 1 : 0;

        for (int i = startIndex; i < lines.size(); i++) {
            String line = lines.get(i).trim();

            if (line.isEmpty()) {
                continue;
            }

            String[] columns = line.split(",");

            if (columns.length < 8) {
                throw new IllegalArgumentException("Line " + (i + 1) + " has fewer than 8 columns.");
            }

            records.add(new WeatherImportRecord(
                    columns[0].trim(),
                    LocalDateTime.parse(columns[1].trim(), DATE_TIME_FORMAT),
                    LocalDateTime.parse(columns[2].trim(), DATE_TIME_FORMAT),
                    Double.parseDouble(columns[3].trim()),
                    Double.parseDouble(columns[4].trim()),
                    Double.parseDouble(columns[5].trim()),
                    Double.parseDouble(columns[6].trim()),
                    Double.parseDouble(columns[7].trim()),
                    columns.length >= 9 ? columns[8].trim() : "UNKNOWN"
            ));
        }

        return records;
    }

    /**
     * Checks whether the CSV content starts with a header row.
     *
     * @param lines all lines read from the CSV file
     * @return {@code true} if the first line appears to be a header;
     * otherwise {@code false}
     */
    private boolean hasHeader(List<String> lines) {
        if (lines.isEmpty()) {
            return false;
        }

        String first = lines.get(0).trim().toUpperCase(Locale.ROOT);
        return first.startsWith("AREA_CODE");
    }
}