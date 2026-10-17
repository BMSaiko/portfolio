package eapli.aisafe.app.backoffice.console.presentation.weather;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.domain.GeoBoundary;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import eapli.aisafe.weather.application.RegisterWeatherDataController;
import eapli.aisafe.weather.domain.WeatherData;
import eapli.aisafe.weather.domain.Temperature;
import eapli.aisafe.weather.domain.WindSpeed;
import eapli.aisafe.weather.domain.WindDirection;
import eapli.aisafe.weather.domain.Humidity;
import eapli.aisafe.weather.domain.Pressure;
import eapli.aisafe.generic.domain.TimePeriod;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class WeatherUiAcceptanceTest {

    private final PrintStream originalOut = System.out;
    private final java.io.InputStream originalIn = System.in;

    private InMemoryUserRepository userRepo;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepo = new InMemoryUserRepository();
        authService = new AuthService(userRepo);
    }

    @AfterEach
    void restoreConsole() {
        System.setOut(originalOut);
        System.setIn(originalIn);
        authService.logout();
    }

    private ConsoleResult runUiWithInput(String input, Runnable uiAction) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        uiAction.run();
        return new ConsoleResult(output.toString(StandardCharsets.UTF_8));
    }

    private User user(String email, String name, String phone, Role role) {
        return new User(new Email(email), "Password123", eapli.aisafe.generic.domain.UserName.of(name), eapli.aisafe.generic.domain.PhoneNumber.of(phone), Set.of(role));
    }

    private static AirControlArea area(String code, String name) {
        return new AirControlArea(AirControlAreaCode.of(code), name, 500.0,
                new GeoBoundary(47.0, 36.0, -6.0, -20.0));
    }

    private String weatherInput(String areaIdx, String startTs, String endTs, String temp,
                                 String windSpd, String windDir, String humidity, String pressure) {
        return String.join("\n", areaIdx, startTs, endTs, temp, windSpd, windDir, humidity, pressure) + "\n";
    }

    private record ConsoleResult(String output) {}

    @Test
    void us041RegisterWeatherDataShouldSucceedWithValidInput() {
        User wp = user("weather@aisafe.com", "WP", "123456789", Role.WEATHER_PERSON);
        userRepo.addUser(wp);
        authService.login("weather@aisafe.com", "Password123");
        InMemoryAreaRepository areaRepo = new InMemoryAreaRepository();
        areaRepo.addArea(area("LIS_AREA", "Lisbon Area"));
        CapturingWeatherController ctrl = new CapturingWeatherController(authService, areaRepo);
        ConsoleResult r = runUiWithInput(
                weatherInput("1", "2025-01-15 14:00", "2025-01-15 14:30", "20.5", "10.0", "180.0", "65.0", "1013.0"),
                () -> new RegisterWeatherDataUI(ctrl, areaRepo).show());
        assertEquals("LIS_AREA", ctrl.capturedAreaCode);
        assertNotNull(ctrl.capturedStart);
        assertNotNull(ctrl.capturedEnd);
        assertEquals(20.5, ctrl.capturedTemperature);
        assertEquals(10.0, ctrl.capturedWindSpeed);
        assertEquals(180.0, ctrl.capturedWindDirection);
        assertEquals(65.0, ctrl.capturedHumidity);
        assertEquals(1013.0, ctrl.capturedPressure);
        assertTrue(r.output.contains("Select air control area:"));
        assertTrue(r.output.contains("Lisbon Area"));
        assertTrue(r.output.contains("Weather data registered successfully."));
    }

    @Test
    void us041RegisterWeatherDataShouldSucceedWhenAdmin() {
        User admin = user("admin@aisafe.com", "Admin", "123456789", Role.ADMIN);
        userRepo.addUser(admin);
        authService.login("admin@aisafe.com", "Password123");
        InMemoryAreaRepository areaRepo = new InMemoryAreaRepository();
        areaRepo.addArea(area("POR_AREA", "Porto Area"));
        CapturingWeatherController ctrl = new CapturingWeatherController(authService, areaRepo);
        ConsoleResult r = runUiWithInput(
                weatherInput("1", "2025-03-20 09:00", "2025-03-20 10:00", "15.0", "5.0", "270.0", "80.0", "1005.0"),
                () -> new RegisterWeatherDataUI(ctrl, areaRepo).show());
        assertEquals("POR_AREA", ctrl.capturedAreaCode);
        assertTrue(r.output.contains("Weather data registered successfully."));
    }

    @Test
    void us041RegisterWeatherDataShouldFailWhenUnauthorized() {
        User pilot = user("pilot@aisafe.com", "Pilot", "123456789", Role.PILOT);
        userRepo.addUser(pilot);
        authService.login("pilot@aisafe.com", "Password123");
        InMemoryAreaRepository areaRepo = new InMemoryAreaRepository();
        areaRepo.addArea(area("LIS_AREA", "Lisbon Area"));
        CapturingWeatherController ctrl = new CapturingWeatherController(authService, areaRepo);
        ConsoleResult r = runUiWithInput(
                weatherInput("1", "2025-01-15 14:00", "2025-01-15 14:30", "20.5", "10.0", "180.0", "65.0", "1013.0"),
                () -> new RegisterWeatherDataUI(ctrl, areaRepo).show());
        assertTrue(r.output.contains("Error: User does not have required role"));
    }

    @Test
    void us041RegisterWeatherDataShouldShowNoAreasMessage() {
        User wp = user("weather@aisafe.com", "WP", "123456789", Role.WEATHER_PERSON);
        userRepo.addUser(wp);
        authService.login("weather@aisafe.com", "Password123");
        InMemoryAreaRepository areaRepo = new InMemoryAreaRepository();
        CapturingWeatherController ctrl = new CapturingWeatherController(authService, areaRepo);
        ConsoleResult r = runUiWithInput("", () -> new RegisterWeatherDataUI(ctrl, areaRepo).show());
        assertTrue(r.output.contains("No air control areas available. Please create one first (US050)."));
    }

    @Test
    void us041RegisterWeatherDataShouldFailWithInvalidAreaSelection() {
        User wp = user("weather@aisafe.com", "WP", "123456789", Role.WEATHER_PERSON);
        userRepo.addUser(wp);
        authService.login("weather@aisafe.com", "Password123");
        InMemoryAreaRepository areaRepo = new InMemoryAreaRepository();
        areaRepo.addArea(area("LIS_AREA", "Lisbon Area"));
        CapturingWeatherController ctrl = new CapturingWeatherController(authService, areaRepo);
        String input = String.join("\n", "99", "1", "2025-01-15 14:00", "2025-01-15 14:30", "20.5", "10.0", "180.0", "65.0", "1013.0") + "\n";
        ConsoleResult r = runUiWithInput(input, () -> new RegisterWeatherDataUI(ctrl, areaRepo).show());
        assertTrue(r.output.contains("Invalid option."));
    }

    @Test
    void us041RegisterWeatherDataShouldFailWithInvalidDateFormat() {
        User wp = user("weather@aisafe.com", "WP", "123456789", Role.WEATHER_PERSON);
        userRepo.addUser(wp);
        authService.login("weather@aisafe.com", "Password123");
        InMemoryAreaRepository areaRepo = new InMemoryAreaRepository();
        areaRepo.addArea(area("LIS_AREA", "Lisbon Area"));
        CapturingWeatherController ctrl = new CapturingWeatherController(authService, areaRepo);
        String input = String.join("\n", "1", "15-01-2025", "2025-01-15 14:30", "20.5", "10.0", "180.0", "65.0", "1013.0") + "\n";
        ConsoleResult r = runUiWithInput(input, () -> new RegisterWeatherDataUI(ctrl, areaRepo).show());
        assertTrue(r.output.contains("Invalid date/time format. Use yyyy-MM-dd HH:mm."));
    }

    @Test
    void us041RegisterWeatherDataShouldFailWithInvalidNumberFormat() {
        User wp = user("weather@aisafe.com", "WP", "123456789", Role.WEATHER_PERSON);
        userRepo.addUser(wp);
        authService.login("weather@aisafe.com", "Password123");
        InMemoryAreaRepository areaRepo = new InMemoryAreaRepository();
        areaRepo.addArea(area("LIS_AREA", "Lisbon Area"));
        CapturingWeatherController ctrl = new CapturingWeatherController(authService, areaRepo);
        String input = String.join("\n", "1", "2025-01-15 14:00", "2025-01-15 14:30", "notanumber", "10.0", "180.0", "65.0", "1013.0") + "\n";
        ConsoleResult r = runUiWithInput(input, () -> new RegisterWeatherDataUI(ctrl, areaRepo).show());
        assertTrue(r.output.contains("Invalid number format."));
    }

    @Test
    void us041RegisterWeatherDataShouldFailWithOutOfRangeTemperature() {
        User wp = user("weather@aisafe.com", "WP", "123456789", Role.WEATHER_PERSON);
        userRepo.addUser(wp);
        authService.login("weather@aisafe.com", "Password123");
        InMemoryAreaRepository areaRepo = new InMemoryAreaRepository();
        areaRepo.addArea(area("LIS_AREA", "Lisbon Area"));
        CapturingWeatherController ctrl = new CapturingWeatherController(authService, areaRepo);
        String input = String.join("\n", "1", "2025-01-15 14:00", "2025-01-15 14:30", "-60.0", "10.0", "180.0", "65.0", "1013.0") + "\n";
        ConsoleResult r = runUiWithInput(input, () -> new RegisterWeatherDataUI(ctrl, areaRepo).show());
        assertTrue(r.output.contains("Error:"));
        assertTrue(r.output.contains("Temperature must be between -50 and 60"));
    }

    @Test
    void us041RegisterWeatherDataShouldAllowFutureForecastTimestamp() {
        User wp = user("weather@aisafe.com", "WP", "123456789", Role.WEATHER_PERSON);
        userRepo.addUser(wp);
        authService.login("weather@aisafe.com", "Password123");

        InMemoryAreaRepository areaRepo = new InMemoryAreaRepository();
        areaRepo.addArea(area("LIS_AREA", "Lisbon Area"));

        CapturingWeatherController ctrl = new CapturingWeatherController(authService, areaRepo);

        String input = String.join("\n",
                "1",
                "2099-12-31 23:00",
                "2099-12-31 23:59",
                "20.5",
                "10.0",
                "180.0",
                "65.0",
                "1013.0"
        ) + "\n";

        ConsoleResult r = runUiWithInput(input, () -> new RegisterWeatherDataUI(ctrl, areaRepo).show());

        assertEquals("LIS_AREA", ctrl.capturedAreaCode);
        assertEquals(LocalDateTime.of(2099, 12, 31, 23, 0), ctrl.capturedStart);
        assertEquals(LocalDateTime.of(2099, 12, 31, 23, 59), ctrl.capturedEnd);
        assertTrue(r.output.contains("Weather data registered successfully."));
    }

    // ==================== INNER CLASSES ====================

    private static class InMemoryUserRepository implements UserRepository {
        private final Map<String, User> users = new HashMap<>();
        void addUser(User user) { users.put(user.email().value(), user); }
        @Override public Optional<User> findByEmail(Email email) { return Optional.ofNullable(users.get(email.value())); }
        @Override public Optional<User> findActiveByEmail(Email email) { User u = users.get(email.value()); return (u != null && u.isActive()) ? Optional.of(u) : Optional.empty(); }
        @Override public Iterable<User> findActiveByRole(Role role) { return users.values().stream().filter(User::isActive).filter(u -> u.hasRole(role)).toList(); }
        @Override public boolean existsByEmail(Email email) { return users.containsKey(email.value()); }
        @Override public Iterable<User> findAll() { return new ArrayList<>(users.values()); }
        @Override public User save(User entity) { users.put(entity.email().value(), entity); return entity; }
        @Override public void deleteById(Email email) { users.remove(email.value()); }
        @Override public long count() { return users.size(); }
        @Override public void delete(User entity) { users.remove(entity.email().value()); }
        @Override public Optional<User> findById(Email email) { return findByEmail(email); }
    }

    private static class InMemoryAreaRepository implements AirControlAreaRepository {
        private final Map<String, AirControlArea> areas = new LinkedHashMap<>();
        void addArea(AirControlArea area) { areas.put(area.identity().value(), area); }
        @Override public Optional<AirControlArea> findByCode(AirControlAreaCode code) { return Optional.ofNullable(areas.get(code.value())); }
        @Override public Iterable<AirControlArea> findAll() { return new ArrayList<>(areas.values()); }
        @Override public AirControlArea save(AirControlArea entity) { areas.put(entity.identity().value(), entity); return entity; }
        @Override public void deleteById(AirControlAreaCode code) { areas.remove(code.value()); }
        @Override public long count() { return areas.size(); }
        public Optional<AirControlArea> findById(AirControlAreaCode code) { return findByCode(code); }
        @Override public void delete(AirControlArea entity) { areas.remove(entity.identity().value()); }
    }

    private static class CapturingWeatherController extends RegisterWeatherDataController {
        private final AuthService authService;
        private final AirControlAreaRepository areaRepository;
        String capturedAreaCode;
        LocalDateTime capturedStart;
        LocalDateTime capturedEnd;
        double capturedTemperature;
        double capturedWindSpeed;
        double capturedWindDirection;
        double capturedHumidity;
        double capturedPressure;

        CapturingWeatherController(AuthService auth, AirControlAreaRepository areaRepo) {
            super(auth, areaRepo, null);
            this.authService = auth;
            this.areaRepository = areaRepo;
        }

        @Override
        public void register(String areaCode, LocalDateTime start, LocalDateTime end,
                             double temperature, double windSpeed, double windDirection,
                             double humidity, double pressure) {
            this.capturedAreaCode = areaCode;
            this.capturedStart = start;
            this.capturedEnd = end;
            this.capturedTemperature = temperature;
            this.capturedWindSpeed = windSpeed;
            this.capturedWindDirection = windDirection;
            this.capturedHumidity = humidity;
            this.capturedPressure = pressure;
            authService.ensureHasAnyRole(Role.WEATHER_PERSON, Role.ADMIN);
            AirControlArea area = areaRepository.findByCode(AirControlAreaCode.of(areaCode))
                    .orElseThrow(() -> new IllegalArgumentException("Air control area not found"));
            new WeatherData(area, TimePeriod.of(start, end), Temperature.of(temperature),
                    WindSpeed.of(windSpeed), WindDirection.of(windDirection),
                    Humidity.of(humidity), Pressure.of(pressure));
        }
    }
}
