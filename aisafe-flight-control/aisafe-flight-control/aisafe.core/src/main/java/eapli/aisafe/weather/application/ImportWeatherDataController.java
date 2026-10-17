package eapli.aisafe.weather.application;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;
import eapli.aisafe.generic.domain.TimePeriod;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.weather.domain.Humidity;
import eapli.aisafe.weather.domain.Pressure;
import eapli.aisafe.weather.domain.Temperature;
import eapli.aisafe.weather.domain.WeatherData;
import eapli.aisafe.weather.domain.WindDirection;
import eapli.aisafe.weather.domain.WindSpeed;
import eapli.aisafe.weather.repositories.WeatherDataRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Application controller responsible for importing bulk weather data into the
 * system.
 *
 * <p>
 * This controller implements the application logic for US042. It coordinates
 * authentication, file parsing, air control area lookup, domain object creation
 * and persistence.
 * </p>
 *
 * <p>
 * The actual file parsing is delegated to a {@link WeatherDataImporter}. This
 * makes the import process extensible, because new external weather data
 * providers can be supported by adding new importer implementations without
 * changing the controller logic.
 * </p>
 *
 * <p>
 * Each parsed record is processed independently. Valid records are persisted as
 * {@link WeatherData}; invalid records are rejected and registered in the
 * returned {@link WeatherImportResult}. This allows an import operation to
 * partially succeed instead of failing completely when only some records are
 * invalid.
 * </p>
 */
public class ImportWeatherDataController {

    /**
     * Authentication and authorization service used to ensure that only users
     * with the required roles can import weather data.
     */
    private final AuthService auth;

    /**
     * Repository used to resolve the air control area referenced by each import
     * record.
     */
    private final AirControlAreaRepository areaRepo;

    /**
     * Repository used to persist valid weather data records.
     */
    private final WeatherDataRepository weatherRepo;

    /**
     * Importer responsible for parsing the external weather data file.
     */
    private final WeatherDataImporter importer;

    /**
     * Creates the controller with all required dependencies.
     *
     * @param auth authentication and authorization service
     * @param areaRepo repository used to find air control areas
     * @param weatherRepo repository used to persist weather data
     * @param importer parser/importer for the external weather data source
     * @throws IllegalArgumentException if any dependency is {@code null}
     */
    public ImportWeatherDataController(AuthService auth,
                                       AirControlAreaRepository areaRepo,
                                       WeatherDataRepository weatherRepo,
                                       WeatherDataImporter importer) {
        if (auth == null || areaRepo == null || weatherRepo == null || importer == null) {
            throw new IllegalArgumentException("Dependencies cannot be null.");
        }

        this.auth = auth;
        this.areaRepo = areaRepo;
        this.weatherRepo = weatherRepo;
        this.importer = importer;
    }

    /**
     * Imports weather data from the given file path.
     *
     * <p>
     * Only users with the {@link Role#WEATHER_PERSON} or {@link Role#ADMIN}
     * role are allowed to execute this operation.
     * </p>
     *
     * <p>
     * The method parses the file, validates each record through the domain value
     * objects, persists valid records and collects errors for rejected records.
     * The CSV line number reported in each error assumes that line 1 contains
     * the header, so the first data record is reported as line 2.
     * </p>
     *
     * @param path path to the external weather data file
     * @return import result containing the number of imported and rejected
     * records, as well as rejection messages
     * @throws SecurityException if the authenticated user does not have one of
     *                           the required roles
     * @throws IllegalArgumentException if {@code path} is {@code null}
     * @throws IllegalArgumentException if the file cannot be read
     */
    public WeatherImportResult importFrom(Path path) {
        auth.ensureHasAnyRole(Role.WEATHER_PERSON, Role.ADMIN);

        if (path == null) {
            throw new IllegalArgumentException("File path is required.");
        }

        WeatherImportResult result = new WeatherImportResult();
        List<WeatherImportRecord> records;

        try {
            records = importer.parse(path);
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read weather data file: " + e.getMessage(), e);
        }

        for (int i = 0; i < records.size(); i++) {
            WeatherImportRecord record = records.get(i);
            int csvLine = i + 2;

            try {
                AirControlArea area = areaRepo.findByCode(AirControlAreaCode.of(record.areaCode))
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Air control area not found: " + record.areaCode));

                WeatherData data = new WeatherData(
                        area,
                        TimePeriod.of(record.start, record.end),
                        Temperature.of(record.temperature),
                        WindSpeed.of(record.windSpeed),
                        WindDirection.of(record.windDirection),
                        Humidity.of(record.humidity),
                        Pressure.of(record.pressure)
                );

                weatherRepo.save(data);
                result.recordImported();
            } catch (Exception e) {
                result.recordError("Line " + csvLine + ": " + e.getMessage());
            }
        }

        return result;
    }
}