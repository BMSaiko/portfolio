package eapli.aisafe.weather.application;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Contract for components capable of parsing external weather data sources.
 *
 * <p>
 * This interface supports the extensibility requirement of the bulk weather
 * import use case. Different external providers may expose weather data using
 * different file formats or column structures, so each provider-specific parser
 * can implement this interface and convert its input into a common list of
 * {@link WeatherImportRecord} objects.
 * </p>
 *
 * <p>
 * Implementations should focus only on reading and translating the external
 * data source. Domain validation and persistence are handled by
 * {@link ImportWeatherDataController}.
 * </p>
 */
public interface WeatherDataImporter {

    /**
     * Parses an external weather data source into import records.
     *
     * <p>
     * The returned records represent raw import data in a normalized structure.
     * They may still contain invalid values from a domain perspective; those
     * values are validated later when the controller creates domain objects.
     * </p>
     *
     * @param path path to the file or external data source to parse
     * @return a list of parsed weather import records
     * @throws IOException if the source cannot be read
     * @throws IllegalArgumentException if the source format is unsupported or
     *                                  structurally invalid
     */
    List<WeatherImportRecord> parse(Path path) throws IOException;
}