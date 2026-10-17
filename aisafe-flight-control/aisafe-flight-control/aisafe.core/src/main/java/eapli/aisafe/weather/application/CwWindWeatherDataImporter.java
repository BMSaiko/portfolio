package eapli.aisafe.weather.application;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Weather data importer for the CW wind dataset format.
 *
 * <p>
 * This importer supports a provider-specific CSV structure where the available
 * weather information is mainly wind-related. The importer converts each input
 * line into the common {@link WeatherImportRecord} format used by the weather
 * import application service.
 * </p>
 *
 * <p>
 * Because this source does not provide all weather attributes required by the
 * domain model, default values are used for temperature, humidity and pressure.
 * Wind speed is converted from knots to meters per second.
 * </p>
 *
 * <p>
 * This class is an example of the extensibility required by US042: new weather
 * provider formats can be supported by implementing the {@link WeatherDataImporter}
 * interface without changing the import controller.
 * </p>
 */
public class CwWindWeatherDataImporter implements WeatherDataImporter {

    /**
     * Date format used by the CW wind dataset.
     */
    private static final DateTimeFormatter DAY_FORMAT =
            DateTimeFormatter.ofPattern("M/d/yyyy");

    /**
     * Time format used by the CW wind dataset.
     */
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("H:mm");

    /**
     * Conversion factor from knots to meters per second.
     */
    private static final double KNOT_TO_METERS_PER_SECOND = 0.514444;

    /**
     * Default temperature used when the provider does not supply this value.
     */
    private static final double DEFAULT_TEMPERATURE = 15.0;

    /**
     * Default humidity used when the provider does not supply this value.
     */
    private static final double DEFAULT_HUMIDITY = 50.0;

    /**
     * Default atmospheric pressure used when the provider does not supply this
     * value.
     */
    private static final double DEFAULT_PRESSURE = 1013.0;

    /**
     * Parses the CW wind dataset into normalized weather import records.
     *
     * <p>
     * The first line is assumed to be a header and is skipped. Each following
     * non-empty line must contain at least twelve columns. The importer reads
     * the air control area identifier, wind direction, wind speed in knots,
     * date, start time and end time from the provider-specific columns.
     * </p>
     *
     * <p>
     * If the parsed end time is not after the start time, the end timestamp is
     * considered to belong to the following day. This supports intervals that
     * cross midnight.
     * </p>
     *
     * @param path path to the CW wind CSV file
     * @return parsed and normalized weather import records
     * @throws IOException if the file cannot be read
     * @throws IllegalArgumentException if a non-empty data line has fewer than
     *                                  twelve columns
     * @throws java.time.format.DateTimeParseException if the date or time values
     *                                                 do not match the expected
     *                                                 provider format
     * @throws NumberFormatException if wind direction or wind speed cannot be
     *                               parsed as numeric values
     */
    @Override
    public List<WeatherImportRecord> parse(Path path) throws IOException {
        List<String> lines = Files.readAllLines(path);
        List<WeatherImportRecord> records = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();

            if (line.isEmpty()) {
                continue;
            }

            String[] columns = line.split(",");

            if (columns.length < 12) {
                throw new IllegalArgumentException("Line " + (i + 1) + " has fewer than 12 columns.");
            }

            String acaId = columns[0].trim();
            String areaCode = mapAcaToAreaCode(acaId);

            double windDirection = Double.parseDouble(columns[7].trim());
            double windSpeedKnots = Double.parseDouble(columns[8].trim());
            double windSpeedMetersPerSecond = windSpeedKnots * KNOT_TO_METERS_PER_SECOND;

            LocalDate day = LocalDate.parse(columns[9].trim(), DAY_FORMAT);
            LocalTime startTime = LocalTime.parse(columns[10].trim(), TIME_FORMAT);
            LocalTime endTime = LocalTime.parse(columns[11].trim(), TIME_FORMAT);

            LocalDateTime start = LocalDateTime.of(day, startTime);
            LocalDateTime end = LocalDateTime.of(day, endTime);

            if (!end.isAfter(start)) {
                end = end.plusDays(1);
            }

            records.add(new WeatherImportRecord(
                    areaCode,
                    start,
                    end,
                    DEFAULT_TEMPERATURE,
                    windSpeedMetersPerSecond,
                    windDirection,
                    DEFAULT_HUMIDITY,
                    DEFAULT_PRESSURE,
                    "CW-WIND"
            ));
        }

        return records;
    }

    /**
     * Maps a provider-specific air control area identifier to the system air
     * control area code.
     *
     * <p>
     * The CW dataset uses numeric area identifiers. Known identifiers are mapped
     * to the corresponding system codes. Unknown identifiers are preserved using
     * the {@code ACA_} prefix, allowing the import controller to later reject
     * them if no matching air control area exists.
     * </p>
     *
     * @param acaId provider-specific air control area identifier
     * @return system air control area code candidate
     */
    private String mapAcaToAreaCode(String acaId) {
        if ("121".equals(acaId)) {
            return "LPPC";
        }

        return "ACA_" + acaId;
    }
}