package eapli.aisafe.app.backoffice.console.presentation.airportmanagement;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.domain.GeoBoundary;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;
import eapli.aisafe.airportmanagement.application.RegisterAirportController;
import eapli.aisafe.airportmanagement.domain.*;
import eapli.aisafe.generic.domain.IataCode;
import eapli.aisafe.generic.domain.IcaoCode;

import eapli.aisafe.airportmanagement.repositories.AirportRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de aceitação baseados na UI para a US052 - Registar Aeroporto.
 */
class RegisterAirportUiAcceptanceTest {

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

    private ConsoleResult runUiWithInput(String input, Consumer<Scanner> uiAction) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

        Scanner scanner = new Scanner(System.in);
        uiAction.accept(scanner);
        return new ConsoleResult(output.toString(StandardCharsets.UTF_8));
    }

    private User user(String email, String name, String phone, Role role) {
        return new User(new Email(email), "Password123", eapli.aisafe.generic.domain.UserName.of(name), eapli.aisafe.generic.domain.PhoneNumber.of(phone), Set.of(role));
    }

    private static AirControlArea mockArea(String code) {
        return new AirControlArea(
                AirControlAreaCode.of(code), "Test Area", 500.0,
                new GeoBoundary(45.0, 35.0, -5.0, -10.0));
    }

    private String airportInput(String iata, String icao, String name, String town,
                                String country, String lat, String lon, String alt, String areaCode) {
        return String.join("\n", iata, icao, name, town, country, lat, lon, alt, areaCode) + "\n";
    }

    private record ConsoleResult(String output) {}

    // ==================== US052 - REGISTER AIRPORT ====================

    @Test
    void us052RegisterAirportShouldSucceedWithValidInputAsOperator() {
        User operator = user("operator@aisafe.com", "Backoffice Operator", "123456789", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("operator@aisafe.com", "Password123");

        InMemoryAreaRepository areaRepo = new InMemoryAreaRepository();
        areaRepo.addArea(mockArea("LPPC"));

        InMemoryAirportRepository airportRepo = new InMemoryAirportRepository();
        CapturingAirportController controller = new CapturingAirportController(authService, airportRepo, areaRepo);

        ConsoleResult result = runUiWithInput(
                airportInput("LIS", "LPPT", "Humberto Delgado", "Lisbon", "Portugal", "38.7742", "-9.1342", "113.0", "LPPC"),
                (scanner) -> new RegisterAirportUI(controller, scanner).show()
        );

        assertEquals("LIS", controller.capturedIata);
        assertEquals("LPPT", controller.capturedIcao);
        assertEquals("Humberto Delgado", controller.capturedName);
        assertEquals("Lisbon", controller.capturedTown);
        assertEquals("Portugal", controller.capturedCountry);
        assertEquals(38.7742, controller.capturedLatitude);
        assertEquals(-9.1342, controller.capturedLongitude);
        assertEquals(113.0, controller.capturedAltitude);
        assertEquals("LPPC", controller.capturedAreaCode);

        assertTrue(result.output.contains("Success! Registered:"));
    }

    @Test
    void us052RegisterAirportShouldFailWhenUnauthorized() {
        User pilot = user("pilot@aisafe.com", "Pilot", "123456789", Role.PILOT);
        userRepo.addUser(pilot);
        authService.login("pilot@aisafe.com", "Password123");

        InMemoryAreaRepository areaRepo = new InMemoryAreaRepository();
        InMemoryAirportRepository airportRepo = new InMemoryAirportRepository();
        CapturingAirportController controller = new CapturingAirportController(authService, airportRepo, areaRepo);

        ConsoleResult result = runUiWithInput(
                airportInput("LIS", "LPPT", "Humberto Delgado", "Lisbon", "Portugal", "38.7742", "-9.1342", "113.0", "LPPC"),
                (scanner) -> new RegisterAirportUI(controller, scanner).show()
        );

        assertTrue(result.output.contains("ACCESS DENIED:"));
    }

    @Test
    void us052RegisterAirportShouldFailWhenIataAlreadyExists() {
        User operator = user("operator@aisafe.com", "Backoffice Operator", "123456789", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("operator@aisafe.com", "Password123");

        InMemoryAreaRepository areaRepo = new InMemoryAreaRepository();
        AirControlArea area = mockArea("LPPC");
        areaRepo.addArea(area);

        InMemoryAirportRepository airportRepo = new InMemoryAirportRepository();
        Airport existing = new Airport(IataCode.of("LIS"), IcaoCode.of("LPPT"), "Existing", "Lisbon", "PT", new Coordinates(38.0, -9.0, 10.0), area);
        airportRepo.save(existing);

        CapturingAirportController controller = new CapturingAirportController(authService, airportRepo, areaRepo);

        ConsoleResult result = runUiWithInput(
                airportInput("LIS", "LPPV", "New Airport", "Lisbon", "Portugal", "38.7742", "-9.1342", "113.0", "LPPC"),
                (scanner) -> new RegisterAirportUI(controller, scanner).show()
        );

        assertTrue(result.output.contains("ERROR: An airport with IATA code 'LIS' already exists."));
    }

    @Test
    void us052RegisterAirportShouldFailWhenAreaDoesNotExist() {
        User operator = user("operator@aisafe.com", "Backoffice Operator", "123456789", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("operator@aisafe.com", "Password123");

        InMemoryAreaRepository areaRepo = new InMemoryAreaRepository();
        InMemoryAirportRepository airportRepo = new InMemoryAirportRepository();
        CapturingAirportController controller = new CapturingAirportController(authService, airportRepo, areaRepo);

        ConsoleResult result = runUiWithInput(
                airportInput("LIS", "LPPT", "Humberto Delgado", "Lisbon", "Portugal", "38.7742", "-9.1342", "113.0", "UNKNOWN"),
                (scanner) -> new RegisterAirportUI(controller, scanner).show()
        );

        assertTrue(result.output.contains("ERROR: Air Control Area 'UNKNOWN' does not exist."));
    }

    @Test
    void us052RegisterAirportShouldFailWhenCoordinatesAreOutsideBoundary() {
        User operator = user("operator@aisafe.com", "Backoffice Operator", "123456789", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("operator@aisafe.com", "Password123");

        InMemoryAreaRepository areaRepo = new InMemoryAreaRepository();
        areaRepo.addArea(mockArea("LPPC"));

        InMemoryAirportRepository airportRepo = new InMemoryAirportRepository();
        CapturingAirportController controller = new CapturingAirportController(authService, airportRepo, areaRepo);

        ConsoleResult result = runUiWithInput(
                airportInput("LIS", "LPPT", "Humberto Delgado", "Lisbon", "Portugal", "0.0", "0.0", "113.0", "LPPC"),
                (scanner) -> new RegisterAirportUI(controller, scanner).show()
        );

        assertTrue(result.output.contains("ERROR: Coordinates (0.0, 0.0) are outside the boundary of Air Control Area 'LPPC'."));
    }

    @Test
    void us052RegisterAirportShouldRecoverFromInvalidIataFormat() {
        User operator = user("operator@aisafe.com", "Backoffice Operator", "123456789", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("operator@aisafe.com", "Password123");

        InMemoryAreaRepository areaRepo = new InMemoryAreaRepository();
        areaRepo.addArea(mockArea("LPPC"));
        InMemoryAirportRepository airportRepo = new InMemoryAirportRepository();
        CapturingAirportController controller = new CapturingAirportController(authService, airportRepo, areaRepo);

        String input = String.join("\n", "LI", "LIS", "LPPT", "Humberto Delgado", "Lisbon", "Portugal", "38.7742", "-9.1342", "113.0", "LPPC") + "\n";

        ConsoleResult result = runUiWithInput(
                input,
                (scanner) -> new RegisterAirportUI(controller, scanner).show()
        );

        assertTrue(result.output.contains("IATA code must be exactly 3 uppercase letters."));
        assertTrue(result.output.contains("Success!"));
    }

    // ==================== INNER CLASSES FOR MOCKING ====================

    private static class InMemoryUserRepository implements UserRepository {
        private final Map<String, User> users = new HashMap<>();
        void addUser(User user) { users.put(user.email().value(), user); }
        @Override public Optional<User> findByEmail(Email email) { return Optional.ofNullable(users.get(email.value())); }
        @Override public Optional<User> findActiveByEmail(Email email) { return findByEmail(email); }
        @Override public Iterable<User> findActiveByRole(Role role) { return Collections.emptyList(); }
        @Override public boolean existsByEmail(Email email) { return users.containsKey(email.value()); }
        @Override public Iterable<User> findAll() { return users.values(); }
        @Override public User save(User entity) { users.put(entity.email().value(), entity); return entity; }
        @Override public void deleteById(Email email) {}
        @Override public long count() { return users.size(); }
        @Override public void delete(User entity) {}
        @Override public Optional<User> findById(Email email) { return findByEmail(email); }
    }

    private static class InMemoryAreaRepository implements AirControlAreaRepository {
        private final Map<String, AirControlArea> areas = new LinkedHashMap<>();
        void addArea(AirControlArea area) { areas.put(area.identity().value(), area); }
        @Override public Optional<AirControlArea> findByCode(AirControlAreaCode code) { return Optional.ofNullable(areas.get(code.value())); }
        @Override public Iterable<AirControlArea> findAll() { return new ArrayList<>(areas.values()); }
        @Override public AirControlArea save(AirControlArea entity) { areas.put(entity.identity().value(), entity); return entity; }
        @Override public void deleteById(AirControlAreaCode code) {}
        @Override public long count() { return areas.size(); }
        @Override public Optional<AirControlArea> findById(AirControlAreaCode code) { return findByCode(code); }
        @Override public void delete(AirControlArea entity) {}
    }

    private static class InMemoryAirportRepository implements AirportRepository {
        private final Map<String, Airport> airports = new LinkedHashMap<>();

        @Override
        public Optional<Airport> findByIataCode(IataCode iataCode) {
            return airports.values().stream()
                    .filter(a -> a.identity().value().equals(iataCode.value()))
                    .findFirst();
        }

        @Override
        public Optional<Airport> findByIcaoCode(IcaoCode icaoCode) {
            return Optional.empty();
        }

        @Override
        public Iterable<Airport> findAll() {
            return airports.values();
        }

        @Override
        public Airport save(Airport entity) {
            airports.put(entity.identity().value(), entity);
            return entity;
        }
    }

    private static class CapturingAirportController extends RegisterAirportController {
        private final AuthService authService;
        private final AirportRepository airportRepository;
        private final AirControlAreaRepository areaRepository;

        String capturedIata;
        String capturedIcao;
        String capturedName;
        String capturedTown;
        String capturedCountry;
        double capturedLatitude;
        double capturedLongitude;
        double capturedAltitude;
        String capturedAreaCode;

        CapturingAirportController(AuthService authService, AirportRepository airportRepo, AirControlAreaRepository areaRepo) {
            super(authService, airportRepo, areaRepo);
            this.authService = authService;
            this.airportRepository = airportRepo;
            this.areaRepository = areaRepo;
        }

        @Override
        public Airport registerAirport(String iata, String icao, String name, String town, String country,
                                       double latitude, double longitude, double altitude, String areaCode) {
            this.capturedIata = iata;
            this.capturedIcao = icao;
            this.capturedName = name;
            this.capturedTown = town;
            this.capturedCountry = country;
            this.capturedLatitude = latitude;
            this.capturedLongitude = longitude;
            this.capturedAltitude = altitude;
            this.capturedAreaCode = areaCode;

            authService.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);

            final IataCode iataCode = IataCode.of(iata);
            final IcaoCode icaoCode = IcaoCode.of(icao);

            if (airportRepository.findByIataCode(iataCode).isPresent())
                throw new IllegalStateException("An airport with IATA code '" + iata + "' already exists.");

            if (airportRepository.findByIcaoCode(icaoCode).isPresent())
                throw new IllegalStateException("An airport with ICAO code '" + icao + "' already exists.");

            final AirControlArea area = areaRepository
                    .findByCode(AirControlAreaCode.of(areaCode))
                    .orElseThrow(() -> new IllegalStateException("Air Control Area '" + areaCode + "' does not exist."));

            if (!area.geoBoundary().contains(latitude, longitude))
                throw new IllegalStateException("Coordinates (" + latitude + ", " + longitude + ") are outside the boundary of Air Control Area '" + areaCode + "'.");

            return new Airport(iataCode, icaoCode, name, town, country, new Coordinates(latitude, longitude, altitude), area);
        }
    }
}