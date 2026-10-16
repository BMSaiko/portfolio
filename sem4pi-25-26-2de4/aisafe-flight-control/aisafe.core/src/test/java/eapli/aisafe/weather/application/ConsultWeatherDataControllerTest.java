package eapli.aisafe.weather.application;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.domain.GeoBoundary;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;
import eapli.aisafe.generic.domain.PhoneNumber;
import eapli.aisafe.generic.domain.TimePeriod;
import eapli.aisafe.generic.domain.UserName;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import eapli.aisafe.weather.domain.Humidity;
import eapli.aisafe.weather.domain.Pressure;
import eapli.aisafe.weather.domain.Temperature;
import eapli.aisafe.weather.domain.WeatherData;
import eapli.aisafe.weather.domain.WindDirection;
import eapli.aisafe.weather.domain.WindSpeed;
import eapli.aisafe.weather.repositories.WeatherDataRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ConsultWeatherDataControllerTest {

    private AuthService authService;
    private InMemoryUserRepository userRepo;
    private InMemoryAreaRepository areaRepo;
    private InMemoryWeatherRepository weatherRepo;
    private ConsultWeatherDataController controller;

    private AirControlArea lppc;
    private AirControlArea oppo;

    @BeforeEach
    void setUp() {
        userRepo = new InMemoryUserRepository();
        authService = new AuthService(userRepo);
        areaRepo = new InMemoryAreaRepository();
        weatherRepo = new InMemoryWeatherRepository();

        controller = new ConsultWeatherDataController(authService, areaRepo, weatherRepo);

        lppc = new AirControlArea(
                AirControlAreaCode.of("LPPC"),
                "Lisbon Oceanic",
                5000.0,
                new GeoBoundary(47.0, 36.0, -6.0, -20.0)
        );

        oppo = new AirControlArea(
                AirControlAreaCode.of("OPPO"),
                "Porto Area",
                4000.0,
                new GeoBoundary(45.0, 39.0, -5.0, -12.0)
        );

        areaRepo.save(lppc);
        areaRepo.save(oppo);

        userRepo.save(new User(
                new Email("weather@aisafe.com"),
                "Weather123",
                UserName.of("Weather Person"),
                PhoneNumber.of("123456789"),
                java.util.Set.of(Role.WEATHER_PERSON)
        ));

        userRepo.save(new User(
                new Email("pilot@aisafe.com"),
                "Pilot123",
                UserName.of("Pilot"),
                PhoneNumber.of("123456788"),
                java.util.Set.of(Role.PILOT)
        ));

        userRepo.save(new User(
                new Email("flightcontrol@aisafe.com"),
                "Flight123",
                UserName.of("Flight Control"),
                PhoneNumber.of("123456787"),
                java.util.Set.of(Role.FLIGHT_CONTROL_OPERATOR)
        ));

        userRepo.save(new User(
                new Email("company@aisafe.com"),
                "Company123",
                UserName.of("Company User"),
                PhoneNumber.of("123456786"),
                java.util.Set.of(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR)
        ));
    }

    @AfterEach
    void tearDown() {
        authService.logout();
    }

    @Test
    void consultShouldReturnWeatherDataForGivenAreaAndDate() {
        authService.login("weather@aisafe.com", "Weather123");

        WeatherData first = weatherData(lppc,
                LocalDateTime.of(2026, 6, 22, 5, 0),
                LocalDateTime.of(2026, 6, 22, 8, 15),
                18.5);

        WeatherData second = weatherData(lppc,
                LocalDateTime.of(2026, 6, 22, 8, 15),
                LocalDateTime.of(2026, 6, 22, 12, 0),
                20.0);

        weatherRepo.save(first);
        weatherRepo.save(second);

        List<WeatherData> result = toList(controller.consult("LPPC", LocalDate.of(2026, 6, 22)));

        assertEquals(2, result.size());
        assertTrue(result.contains(first));
        assertTrue(result.contains(second));
    }

    @Test
    void consultShouldFilterByArea() {
        authService.login("weather@aisafe.com", "Weather123");

        WeatherData lppcWeather = weatherData(lppc,
                LocalDateTime.of(2026, 6, 22, 5, 0),
                LocalDateTime.of(2026, 6, 22, 8, 15),
                18.5);

        WeatherData oppoWeather = weatherData(oppo,
                LocalDateTime.of(2026, 6, 22, 5, 0),
                LocalDateTime.of(2026, 6, 22, 8, 15),
                16.0);

        weatherRepo.save(lppcWeather);
        weatherRepo.save(oppoWeather);

        List<WeatherData> result = toList(controller.consult("LPPC", LocalDate.of(2026, 6, 22)));

        assertEquals(1, result.size());
        assertEquals(lppcWeather, result.get(0));
    }

    @Test
    void consultShouldFilterByDateUsingPeriodStart() {
        authService.login("weather@aisafe.com", "Weather123");

        WeatherData previousDay = weatherData(lppc,
                LocalDateTime.of(2026, 6, 21, 23, 0),
                LocalDateTime.of(2026, 6, 22, 1, 0),
                17.0);

        WeatherData selectedDay = weatherData(lppc,
                LocalDateTime.of(2026, 6, 22, 0, 0),
                LocalDateTime.of(2026, 6, 22, 2, 0),
                18.0);

        WeatherData nextDay = weatherData(lppc,
                LocalDateTime.of(2026, 6, 23, 0, 0),
                LocalDateTime.of(2026, 6, 23, 2, 0),
                19.0);

        weatherRepo.save(previousDay);
        weatherRepo.save(selectedDay);
        weatherRepo.save(nextDay);

        List<WeatherData> result = toList(controller.consult("LPPC", LocalDate.of(2026, 6, 22)));

        assertEquals(1, result.size());
        assertEquals(selectedDay, result.get(0));
    }

    @Test
    void consultShouldAllowPilot() {
        authService.login("pilot@aisafe.com", "Pilot123");

        WeatherData data = weatherData(lppc,
                LocalDateTime.of(2026, 6, 22, 5, 0),
                LocalDateTime.of(2026, 6, 22, 8, 15),
                18.5);

        weatherRepo.save(data);

        List<WeatherData> result = toList(controller.consult("LPPC", LocalDate.of(2026, 6, 22)));

        assertEquals(1, result.size());
    }

    @Test
    void consultShouldAllowFlightControlOperator() {
        authService.login("flightcontrol@aisafe.com", "Flight123");

        WeatherData data = weatherData(lppc,
                LocalDateTime.of(2026, 6, 22, 5, 0),
                LocalDateTime.of(2026, 6, 22, 8, 15),
                18.5);

        weatherRepo.save(data);

        List<WeatherData> result = toList(controller.consult("LPPC", LocalDate.of(2026, 6, 22)));

        assertEquals(1, result.size());
    }

    @Test
    void consultShouldThrowWhenUserIsNotAuthenticated() {
        assertThrows(SecurityException.class,
                () -> controller.consult("LPPC", LocalDate.of(2026, 6, 22)));
    }

    @Test
    void consultShouldThrowWhenUserDoesNotHaveRequiredRole() {
        authService.login("company@aisafe.com", "Company123");

        assertThrows(SecurityException.class,
                () -> controller.consult("LPPC", LocalDate.of(2026, 6, 22)));
    }

    @Test
    void consultShouldThrowWhenAreaCodeIsNull() {
        authService.login("weather@aisafe.com", "Weather123");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.consult(null, LocalDate.of(2026, 6, 22)));

        assertEquals("Air control area code is required.", ex.getMessage());
    }

    @Test
    void consultShouldThrowWhenAreaCodeIsBlank() {
        authService.login("weather@aisafe.com", "Weather123");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.consult("   ", LocalDate.of(2026, 6, 22)));

        assertEquals("Air control area code is required.", ex.getMessage());
    }

    @Test
    void consultShouldThrowWhenDateIsNull() {
        authService.login("weather@aisafe.com", "Weather123");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.consult("LPPC", null));

        assertEquals("Date is required.", ex.getMessage());
    }

    @Test
    void consultShouldThrowWhenAreaDoesNotExist() {
        authService.login("weather@aisafe.com", "Weather123");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.consult("UNKNOWN", LocalDate.of(2026, 6, 22)));

        assertTrue(ex.getMessage().contains("Air control area not found: UNKNOWN"));
    }

    @Test
    void consultShouldReturnEmptyWhenNoWeatherDataExistsForAreaAndDate() {
        authService.login("weather@aisafe.com", "Weather123");

        weatherRepo.save(weatherData(lppc,
                LocalDateTime.of(2026, 6, 21, 5, 0),
                LocalDateTime.of(2026, 6, 21, 8, 15),
                18.5));

        List<WeatherData> result = toList(controller.consult("LPPC", LocalDate.of(2026, 6, 22)));

        assertTrue(result.isEmpty());
    }

    private WeatherData weatherData(AirControlArea area,
                                    LocalDateTime start,
                                    LocalDateTime end,
                                    double temperature) {
        return new WeatherData(
                area,
                TimePeriod.of(start, end),
                Temperature.of(temperature),
                WindSpeed.of(12.0),
                WindDirection.of(90.0),
                Humidity.of(65.0),
                Pressure.of(1013.0)
        );
    }

    private List<WeatherData> toList(Iterable<WeatherData> iterable) {
        List<WeatherData> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
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
                    .filter(user -> user.hasRole(role))
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
                    .filter(weatherData -> weatherData.area().sameAs(area))
                    .filter(weatherData -> !weatherData.period().start().isBefore(start))
                    .filter(weatherData -> weatherData.period().start().isBefore(end))
                    .toList();
        }
    }
}