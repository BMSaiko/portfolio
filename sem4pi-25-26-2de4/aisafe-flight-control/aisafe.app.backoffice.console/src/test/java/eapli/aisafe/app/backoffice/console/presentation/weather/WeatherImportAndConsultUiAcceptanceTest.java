package eapli.aisafe.app.backoffice.console.presentation.weather;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.domain.GeoBoundary;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;
import eapli.aisafe.generic.domain.PhoneNumber;
import eapli.aisafe.generic.domain.TimePeriod;
import eapli.aisafe.generic.domain.UserName;
import eapli.aisafe.persistence.impl.inmemory.InMemoryWeatherDataRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import eapli.aisafe.weather.application.ConsultWeatherDataController;
import eapli.aisafe.weather.application.ImportWeatherDataController;
import eapli.aisafe.weather.application.WeatherImportRecord;
import eapli.aisafe.weather.application.WeatherImportResult;
import eapli.aisafe.weather.domain.Humidity;
import eapli.aisafe.weather.domain.Pressure;
import eapli.aisafe.weather.domain.Temperature;
import eapli.aisafe.weather.domain.WeatherData;
import eapli.aisafe.weather.domain.WindDirection;
import eapli.aisafe.weather.domain.WindSpeed;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UI acceptance tests for US042 and US043.
 *
 * <p>The tests use in-memory/stub collaborators so that the console behaviour
 * is verified without depending on JPA, external CSV files or bootstrap data.</p>
 */
class WeatherImportAndConsultUiAcceptanceTest {

    private final PrintStream originalOut = System.out;
    private final java.io.InputStream originalIn = System.in;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(new InMemoryUserRepository());
    }

    @AfterEach
    void restoreConsole() {
        System.setOut(originalOut);
        System.setIn(originalIn);
        authService.logout();
    }

    @Test
    void us042ImportBulkWeatherDataShouldPrintSuccessfulSummary() {
        CapturingImportController controller = new CapturingImportController(authService);
        WeatherImportResult result = new WeatherImportResult();
        result.recordImported();
        result.recordImported();
        result.recordImported();
        result.recordImported();
        controller.resultToReturn = result;

        ConsoleResult console = runUiWithInput(
                "weather-imports\\aisafe_weather_complete.csv\n",
                () -> new ImportWeatherDataUI(controller).show());

        assertEquals(Path.of("weather-imports\\aisafe_weather_complete.csv"), controller.capturedPath);
        assertTrue(console.output.contains("Import bulk weather data"));
        assertTrue(console.output.contains("Import finished."));
        assertTrue(console.output.contains("Imported records: 4"));
        assertTrue(console.output.contains("Rejected records: 0"));
    }

    @Test
    void us042ImportBulkWeatherDataShouldPrintRejectedRowsAndErrors() {
        CapturingImportController controller = new CapturingImportController(authService);
        WeatherImportResult result = new WeatherImportResult();
        result.recordImported();
        result.recordError("Line 3: Air control area not found: UNKNOWN_AREA");
        result.recordError("Line 4: Temperature must be between -50 and 60");
        controller.resultToReturn = result;

        ConsoleResult console = runUiWithInput(
                "weather-imports\\us42_mixed.csv\n",
                () -> new ImportWeatherDataUI(controller).show());

        assertTrue(console.output.contains("Imported records: 1"));
        assertTrue(console.output.contains("Rejected records: 2"));
        assertTrue(console.output.contains("Errors:"));
        assertTrue(console.output.contains("Line 3: Air control area not found: UNKNOWN_AREA"));
        assertTrue(console.output.contains("Line 4: Temperature must be between -50 and 60"));
    }

    @Test
    void us042ImportBulkWeatherDataShouldPrintReadErrors() {
        CapturingImportController controller = new CapturingImportController(authService);
        controller.errorToThrow = new IllegalArgumentException("Could not read weather data file");

        ConsoleResult console = runUiWithInput(
                "missing.csv\n",
                () -> new ImportWeatherDataUI(controller).show());

        assertTrue(console.output.contains("Error: Could not read weather data file"));
    }

    @Test
    void us043ConsultWeatherDataShouldPrintRecordsForSelectedAreaAndDate() {
        InMemoryAreaRepository areaRepository = new InMemoryAreaRepository();
        AirControlArea lisbon = area("LIS_AREA", "Lisbon Area");
        areaRepository.save(lisbon);
        areaRepository.save(area("OPO_AREA", "Porto Area"));

        CapturingConsultController controller = new CapturingConsultController(authService);
        controller.resultsToReturn = List.of(weatherData(
                lisbon,
                LocalDateTime.of(2026, 5, 22, 10, 0),
                LocalDateTime.of(2026, 5, 22, 11, 0),
                18.5,
                12.0,
                270.0,
                65.0,
                1013.0));

        ConsoleResult console = runUiWithInput(
                "1\n2026-05-22\n",
                () -> new ConsultWeatherDataUI(controller, areaRepository).show());

        assertEquals("LIS_AREA", controller.capturedAreaCode);
        assertEquals(LocalDate.of(2026, 5, 22), controller.capturedDate);
        assertTrue(console.output.contains("Weather data for LIS_AREA on 2026-05-22:"));
        assertTrue(console.output.contains("Temp: 18,50 C") || console.output.contains("Temp: 18.50 C"));
        assertTrue(console.output.contains("Wind: 12,00 m/s 270,00 deg")
                || console.output.contains("Wind: 12.00 m/s 270.00 deg"));
        assertTrue(console.output.contains("Humidity: 65,00 %")
                || console.output.contains("Humidity: 65.00 %"));
        assertTrue(console.output.contains("Pressure: 1013,00 hPa")
                || console.output.contains("Pressure: 1013.00 hPa"));
    }

    @Test
    void us043ConsultWeatherDataShouldPrintEmptyResultMessage() {
        InMemoryAreaRepository areaRepository = new InMemoryAreaRepository();
        areaRepository.save(area("LPPC", "Lisbon Oceanic"));

        CapturingConsultController controller = new CapturingConsultController(authService);
        controller.resultsToReturn = List.of();

        ConsoleResult console = runUiWithInput(
                "1\n2026-05-22\n",
                () -> new ConsultWeatherDataUI(controller, areaRepository).show());

        assertEquals("LPPC", controller.capturedAreaCode);
        assertEquals(LocalDate.of(2026, 5, 22), controller.capturedDate);
        assertTrue(console.output.contains("No weather data found."));
    }

    @Test
    void us043ConsultWeatherDataShouldRejectInvalidAreaOptionWithoutCallingController() {
        InMemoryAreaRepository areaRepository = new InMemoryAreaRepository();
        areaRepository.save(area("LIS_AREA", "Lisbon Area"));

        CapturingConsultController controller = new CapturingConsultController(authService);

        ConsoleResult console = runUiWithInput(
                "9\n",
                () -> new ConsultWeatherDataUI(controller, areaRepository).show());

        assertFalse(controller.consultCalled);
        assertTrue(console.output.contains("Invalid option."));
    }

    @Test
    void us043ConsultWeatherDataShouldRejectInvalidDateWithoutCallingController() {
        InMemoryAreaRepository areaRepository = new InMemoryAreaRepository();
        areaRepository.save(area("LIS_AREA", "Lisbon Area"));

        CapturingConsultController controller = new CapturingConsultController(authService);

        ConsoleResult console = runUiWithInput(
                "1\n22-05-2026\n",
                () -> new ConsultWeatherDataUI(controller, areaRepository).show());

        assertFalse(controller.consultCalled);
        assertTrue(console.output.contains("Invalid date format."));
    }

    private ConsoleResult runUiWithInput(String input, Runnable uiAction) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        uiAction.run();
        return new ConsoleResult(output.toString(StandardCharsets.UTF_8));
    }

    private static AirControlArea area(String code, String name) {
        return new AirControlArea(
                AirControlAreaCode.of(code),
                name,
                500.0,
                new GeoBoundary(47.0, 36.0, -6.0, -20.0));
    }

    private static WeatherData weatherData(AirControlArea area,
                                           LocalDateTime start,
                                           LocalDateTime end,
                                           double temperature,
                                           double windSpeed,
                                           double windDirection,
                                           double humidity,
                                           double pressure) {
        return new WeatherData(
                area,
                TimePeriod.of(start, end),
                Temperature.of(temperature),
                WindSpeed.of(windSpeed),
                WindDirection.of(windDirection),
                Humidity.of(humidity),
                Pressure.of(pressure));
    }

    private record ConsoleResult(String output) {
    }

    /**
     * Test double for the import controller that captures the received file
     * path and returns a preconfigured result.
     */
    private static class CapturingImportController extends ImportWeatherDataController {
        private Path capturedPath;
        private WeatherImportResult resultToReturn = new WeatherImportResult();
        private RuntimeException errorToThrow;

        CapturingImportController(AuthService authService) {
            super(authService,
                    new InMemoryAreaRepository(),
                    new InMemoryWeatherDataRepository(),
                    path -> List.<WeatherImportRecord>of());
        }

        @Override
        public WeatherImportResult importFrom(Path path) {
            capturedPath = path;
            if (errorToThrow != null) {
                throw errorToThrow;
            }
            return resultToReturn;
        }
    }

    /**
     * Test double for the consult controller that captures the chosen area and
     * date before returning configured weather data records.
     */
    private static class CapturingConsultController extends ConsultWeatherDataController {
        private boolean consultCalled;
        private String capturedAreaCode;
        private LocalDate capturedDate;
        private Iterable<WeatherData> resultsToReturn = List.of();

        CapturingConsultController(AuthService authService) {
            super(authService, new InMemoryAreaRepository(), new InMemoryWeatherDataRepository());
        }

        @Override
        public Iterable<WeatherData> consult(String areaCode, LocalDate date) {
            consultCalled = true;
            capturedAreaCode = areaCode;
            capturedDate = date;
            return resultsToReturn;
        }
    }

    /**
     * Small in-memory repository used to feed the consult UI area menu.
     */
    private static class InMemoryAreaRepository implements AirControlAreaRepository {
        private final Map<String, AirControlArea> areas = new LinkedHashMap<>();

        @Override
        public Optional<AirControlArea> findByCode(AirControlAreaCode code) {
            return Optional.ofNullable(areas.get(code.value()));
        }

        @Override
        public <S extends AirControlArea> S save(S entity) {
            areas.put(entity.identity().value(), entity);
            return entity;
        }

        @Override
        public Iterable<AirControlArea> findAll() {
            return new ArrayList<>(areas.values());
        }

        @Override
        public Optional<AirControlArea> findById(AirControlAreaCode id) {
            return findByCode(id);
        }

        @Override
        public void delete(AirControlArea entity) {
            areas.remove(entity.identity().value());
        }

        @Override
        public void deleteById(AirControlAreaCode id) {
            areas.remove(id.value());
        }

        @Override
        public long count() {
            return areas.size();
        }
    }

    /**
     * Minimal user repository required by AuthService construction.
     */
    private static class InMemoryUserRepository implements UserRepository {
        private final Map<String, User> users = new HashMap<>();

        @Override
        public Optional<User> findByEmail(Email email) {
            return Optional.ofNullable(users.get(email.value()));
        }

        @Override
        public Optional<User> findActiveByEmail(Email email) {
            return findByEmail(email).filter(User::isActive);
        }

        @Override
        public Iterable<User> findActiveByRole(Role role) {
            return users.values().stream()
                    .filter(User::isActive)
                    .filter(user -> user.hasRole(role))
                    .toList();
        }

        @Override
        public boolean existsByEmail(Email email) {
            return users.containsKey(email.value());
        }

        @Override
        public <S extends User> S save(S entity) {
            users.put(entity.email().value(), entity);
            return entity;
        }

        @Override
        public Iterable<User> findAll() {
            return new ArrayList<>(users.values());
        }

        @Override
        public Optional<User> findById(Email id) {
            return findByEmail(id);
        }

        @Override
        public void delete(User entity) {
            users.remove(entity.email().value());
        }

        @Override
        public void deleteById(Email id) {
            users.remove(id.value());
        }

        @Override
        public long count() {
            return users.size();
        }

        @SuppressWarnings("unused")
        void addUser(String email, Role role) {
            save(new User(
                    new Email(email),
                    "Password123",
                    UserName.of("Test User"),
                    PhoneNumber.of("123456789"),
                    Set.of(role)));
        }
    }
}
