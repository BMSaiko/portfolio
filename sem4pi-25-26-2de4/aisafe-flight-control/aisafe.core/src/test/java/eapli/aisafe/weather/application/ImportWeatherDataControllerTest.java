package eapli.aisafe.weather.application;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.domain.GeoBoundary;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;
import eapli.aisafe.generic.domain.PhoneNumber;
import eapli.aisafe.generic.domain.UserName;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import eapli.aisafe.weather.domain.WeatherData;
import eapli.aisafe.weather.repositories.WeatherDataRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ImportWeatherDataControllerTest {

    private AuthService authService;
    private InMemoryUserRepository userRepo;
    private InMemoryAreaRepository areaRepo;
    private InMemoryWeatherRepository weatherRepo;
    private StubWeatherDataImporter importer;
    private ImportWeatherDataController controller;

    private final Path path = Path.of("weather.csv");

    @BeforeEach
    void setUp() {
        userRepo = new InMemoryUserRepository();
        authService = new AuthService(userRepo);
        areaRepo = new InMemoryAreaRepository();
        weatherRepo = new InMemoryWeatherRepository();
        importer = new StubWeatherDataImporter();

        controller = new ImportWeatherDataController(authService, areaRepo, weatherRepo, importer);

        userRepo.save(new User(
                new Email("weather@aisafe.com"),
                "Weather123",
                UserName.of("Weather Person"),
                PhoneNumber.of("+351933333333"),
                Set.of(Role.WEATHER_PERSON)
        ));

        areaRepo.save(new AirControlArea(
                AirControlAreaCode.of("LPPC"),
                "Lisbon Oceanic",
                5000.0,
                new GeoBoundary(47.0, 36.0, -6.0, -20.0)
        ));
    }

    @AfterEach
    void tearDown() {
        authService.logout();
    }

    @Test
    void importShouldSaveAllValidRecords() {
        authService.login("weather@aisafe.com", "Weather123");

        importer.records = List.of(
                new WeatherImportRecord("LPPC",
                        LocalDateTime.of(2026, 6, 22, 5, 0),
                        LocalDateTime.of(2026, 6, 22, 8, 15),
                        18.5, 12.0, 90.0, 65.0, 1013.0, "AISAFE"),
                new WeatherImportRecord("LPPC",
                        LocalDateTime.of(2026, 6, 22, 8, 15),
                        LocalDateTime.of(2026, 6, 22, 12, 0),
                        20.0, 10.0, 120.0, 60.0, 1012.0, "AISAFE")
        );

        WeatherImportResult result = controller.importFrom(path);

        assertEquals(2, result.importedRecords());
        assertEquals(0, result.rejectedRecords());
        assertEquals(2, weatherRepo.count());
    }

    @Test
    void importShouldRejectRecordWhenAreaDoesNotExist() {
        authService.login("weather@aisafe.com", "Weather123");

        importer.records = List.of(
                new WeatherImportRecord("UNKNOWN",
                        LocalDateTime.of(2026, 6, 22, 5, 0),
                        LocalDateTime.of(2026, 6, 22, 8, 15),
                        18.5, 12.0, 90.0, 65.0, 1013.0, "AISAFE")
        );

        WeatherImportResult result = controller.importFrom(path);

        assertEquals(0, result.importedRecords());
        assertEquals(1, result.rejectedRecords());
        assertTrue(result.errors().get(0).contains("Air control area not found"));
        assertEquals(0, weatherRepo.count());
    }

    @Test
    void importShouldRejectInvalidWeatherValuesAndContinue() {
        authService.login("weather@aisafe.com", "Weather123");

        importer.records = List.of(
                new WeatherImportRecord("LPPC",
                        LocalDateTime.of(2026, 6, 22, 5, 0),
                        LocalDateTime.of(2026, 6, 22, 8, 15),
                        70.0, 12.0, 90.0, 65.0, 1013.0, "AISAFE"),
                new WeatherImportRecord("LPPC",
                        LocalDateTime.of(2026, 6, 22, 8, 15),
                        LocalDateTime.of(2026, 6, 22, 12, 0),
                        20.0, 10.0, 120.0, 60.0, 1012.0, "AISAFE")
        );

        WeatherImportResult result = controller.importFrom(path);

        assertEquals(1, result.importedRecords());
        assertEquals(1, result.rejectedRecords());
        assertTrue(result.errors().get(0).contains("Temperature must be between -50 and 60"));
        assertEquals(1, weatherRepo.count());
    }

    @Test
    void importShouldThrowWhenUserIsUnauthorized() {
        assertThrows(SecurityException.class, () -> controller.importFrom(path));
        assertFalse(importer.wasCalled);
        assertEquals(0, weatherRepo.count());
    }

    @Test
    void importShouldThrowWhenPathIsNull() {
        authService.login("weather@aisafe.com", "Weather123");

        assertThrows(IllegalArgumentException.class, () -> controller.importFrom(null));
        assertFalse(importer.wasCalled);
        assertEquals(0, weatherRepo.count());
    }

    @Test
    void importShouldThrowWhenFileCannotBeRead() {
        authService.login("weather@aisafe.com", "Weather123");

        importer.exception = new IOException("file not found");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.importFrom(path));

        assertTrue(ex.getMessage().contains("Could not read weather data file"));
        assertEquals(0, weatherRepo.count());
    }

    private static class StubWeatherDataImporter implements WeatherDataImporter {
        private List<WeatherImportRecord> records = List.of();
        private IOException exception;
        private boolean wasCalled;

        @Override
        public List<WeatherImportRecord> parse(Path path) throws IOException {
            wasCalled = true;
            if (exception != null) {
                throw exception;
            }
            return records;
        }
    }

    private static class InMemoryUserRepository implements UserRepository {
        private final Map<String, User> users = new HashMap<>();

        @Override
        public Optional<User> findByEmail(Email email) {
            return Optional.ofNullable(users.get(email.value()));
        }

        @Override
        public Optional<User> findActiveByEmail(Email email) {
            User user = users.get(email.value());
            return user != null && user.isActive() ? Optional.of(user) : Optional.empty();
        }

        @Override
        public Iterable<User> findActiveByRole(Role role) {
            return users.values().stream()
                    .filter(User::isActive)
                    .filter(u -> u.hasRole(role))
                    .toList();
        }

        @Override
        public boolean existsByEmail(Email email) {
            return users.containsKey(email.value());
        }

        @Override
        public User save(User entity) {
            users.put(entity.email().value(), entity);
            return entity;
        }

        @Override
        public Iterable<User> findAll() {
            return new ArrayList<>(users.values());
        }

        @Override
        public Optional<User> findById(Email email) {
            return findByEmail(email);
        }

        @Override
        public void delete(User entity) {
            users.remove(entity.email().value());
        }

        @Override
        public void deleteById(Email email) {
            users.remove(email.value());
        }

        @Override
        public long count() {
            return users.size();
        }
    }

    private static class InMemoryAreaRepository implements AirControlAreaRepository {
        private final Map<String, AirControlArea> areas = new HashMap<>();

        @Override
        public Optional<AirControlArea> findByCode(AirControlAreaCode code) {
            return Optional.ofNullable(areas.get(code.value()));
        }

        @Override
        public AirControlArea save(AirControlArea entity) {
            areas.put(entity.identity().value(), entity);
            return entity;
        }

        @Override
        public Iterable<AirControlArea> findAll() {
            return new ArrayList<>(areas.values());
        }

        @Override
        public Optional<AirControlArea> findById(AirControlAreaCode code) {
            return findByCode(code);
        }

        @Override
        public void delete(AirControlArea entity) {
            areas.remove(entity.identity().value());
        }

        @Override
        public void deleteById(AirControlAreaCode code) {
            areas.remove(code.value());
        }

        @Override
        public long count() {
            return areas.size();
        }
    }

    private static class InMemoryWeatherRepository implements WeatherDataRepository {
        private final Map<Long, WeatherData> store = new HashMap<>();
        private long nextId = 1;

        @Override
        public WeatherData save(WeatherData entity) {
            store.put(nextId++, entity);
            return entity;
        }

        @Override
        public Optional<WeatherData> findById(Long id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public Iterable<WeatherData> findAll() {
            return new ArrayList<>(store.values());
        }

        @Override
        public void delete(WeatherData entity) {
            store.values().remove(entity);
        }

        @Override
        public void deleteById(Long id) {
            store.remove(id);
        }

        @Override
        public long count() {
            return store.size();
        }

        @Override
        public Iterable<WeatherData> findByAreaAndPeriodStartBetween(
                AirControlArea area,
                LocalDateTime start,
                LocalDateTime end) {
            return store.values().stream()
                    .filter(w -> w.area().sameAs(area))
                    .filter(w -> !w.period().start().isBefore(start))
                    .filter(w -> w.period().start().isBefore(end))
                    .toList();
        }
    }
}