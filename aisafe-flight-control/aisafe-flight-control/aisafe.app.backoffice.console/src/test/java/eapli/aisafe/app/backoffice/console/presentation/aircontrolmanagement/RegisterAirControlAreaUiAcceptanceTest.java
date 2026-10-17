package eapli.aisafe.app.backoffice.console.presentation.aircontrolmanagement;

import eapli.aisafe.aircontrolmanagement.application.RegisterAirControlAreaController;
import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.domain.GeoBoundary;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;
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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de aceitação baseados na UI para a US050 - Registar Área de Controlo Aéreo.
 */
class RegisterAirControlAreaUiAcceptanceTest {

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

    private ConsoleResult runUiWithInput(String input, java.util.function.Consumer<Scanner> uiAction) {
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

    private static AirControlArea existingArea(String code, String name) {
        return new AirControlArea(
                AirControlAreaCode.of(code), name, 500.0,
                new GeoBoundary(40.0, 30.0, -5.0, -15.0));
    }

    private String areaInput(String code, String name, String minFuel,
                             String north, String south, String east, String west) {
        return String.join("\n", code, name, minFuel, north, south, east, west) + "\n";
    }

    private record ConsoleResult(String output) {}

    // ==================== US050 - REGISTER AIR CONTROL AREA ====================

    @Test
    void us050RegisterAreaShouldSucceedWithValidInputAsOperator() {
        User operator = user("operator@aisafe.com", "Backoffice Operator", "123456789", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("operator@aisafe.com", "Password123");

        InMemoryAreaRepository areaRepo = new InMemoryAreaRepository();
        CapturingAirControlAreaController controller = new CapturingAirControlAreaController(authService, areaRepo);

        ConsoleResult result = runUiWithInput(
                areaInput("LPPC", "Lisbon FIR", "150.0", "47.0", "36.0", "-6.0", "-20.0"),
                (scanner) -> new RegisterAirControlAreaUI(controller, scanner).show()
        );

        assertEquals("LPPC", controller.capturedCode);
        assertEquals("Lisbon FIR", controller.capturedName);
        assertEquals(150.0, controller.capturedMinimumFuel);
        assertEquals(47.0, controller.capturedNorthLat);
        assertEquals(36.0, controller.capturedSouthLat);
        assertEquals(-6.0, controller.capturedEastLon);
        assertEquals(-20.0, controller.capturedWestLon);

        assertTrue(result.output.contains("Success! Registered:"));
    }

    @Test
    void us050RegisterAreaShouldSucceedAsAdmin() {
        User admin = user("admin@aisafe.com", "Admin", "123456789", Role.ADMIN);
        userRepo.addUser(admin);
        authService.login("admin@aisafe.com", "Password123");

        InMemoryAreaRepository areaRepo = new InMemoryAreaRepository();
        CapturingAirControlAreaController controller = new CapturingAirControlAreaController(authService, areaRepo);

        ConsoleResult result = runUiWithInput(
                areaInput("LPPR", "Porto FIR", "200.0", "42.0", "41.0", "-8.0", "-9.0"),
                (scanner) -> new RegisterAirControlAreaUI(controller, scanner).show()
        );

        assertEquals("LPPR", controller.capturedCode);
        assertTrue(result.output.contains("Success!"));
    }

    @Test
    void us050RegisterAreaShouldFailWhenUnauthorized() {
        User pilot = user("pilot@aisafe.com", "Pilot", "123456789", Role.PILOT);
        userRepo.addUser(pilot);
        authService.login("pilot@aisafe.com", "Password123");

        InMemoryAreaRepository areaRepo = new InMemoryAreaRepository();
        CapturingAirControlAreaController controller = new CapturingAirControlAreaController(authService, areaRepo);

        ConsoleResult result = runUiWithInput(
                areaInput("LPPC", "Lisbon FIR", "150.0", "47.0", "36.0", "-6.0", "-20.0"),
                (scanner) -> new RegisterAirControlAreaUI(controller, scanner).show()
        );

        assertTrue(result.output.contains("ACCESS DENIED:"));
    }

    @Test
    void us050RegisterAreaShouldFailWhenCodeAlreadyExists() {
        User operator = user("operator@aisafe.com", "Backoffice Operator", "123456789", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("operator@aisafe.com", "Password123");

        InMemoryAreaRepository areaRepo = new InMemoryAreaRepository();
        areaRepo.addArea(existingArea("LPPC", "Existing Lisbon"));

        CapturingAirControlAreaController controller = new CapturingAirControlAreaController(authService, areaRepo);

        ConsoleResult result = runUiWithInput(
                areaInput("LPPC", "New Lisbon", "100.0", "49.0", "48.0", "-2.0", "-3.0"),
                (scanner) -> new RegisterAirControlAreaUI(controller, scanner).show()
        );

        assertTrue(result.output.contains("ERROR: An Air Control Area with code 'LPPC' already exists."));
    }

    @Test
    void us050RegisterAreaShouldFailWhenOverlappingBoundaries() {
        User operator = user("operator@aisafe.com", "Backoffice Operator", "123456789", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("operator@aisafe.com", "Password123");

        InMemoryAreaRepository areaRepo = new InMemoryAreaRepository();
        // Área existente entre Lat 30 e 40, Lon -15 e -5
        areaRepo.addArea(existingArea("LPPC", "Existing Lisbon"));

        CapturingAirControlAreaController controller = new CapturingAirControlAreaController(authService, areaRepo);

        // Input tenta registar uma área que se sobrepõe (ex: Lat 35 a 45)
        ConsoleResult result = runUiWithInput(
                areaInput("LPPR", "Overlapping Area", "100.0", "45.0", "35.0", "-6.0", "-10.0"),
                (scanner) -> new RegisterAirControlAreaUI(controller, scanner).show()
        );

        assertTrue(result.output.contains("ERROR: The boundary overlaps with existing area:"));
    }

    @Test
    void us050RegisterAreaShouldRecoverFromInvalidCodeFormat() {
        User operator = user("operator@aisafe.com", "Backoffice Operator", "123456789", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("operator@aisafe.com", "Password123");

        InMemoryAreaRepository areaRepo = new InMemoryAreaRepository();
        CapturingAirControlAreaController controller = new CapturingAirControlAreaController(authService, areaRepo);

        // "L" é inválido (tamanho mínimo 2), depois "LPPC" é válido
        String input = String.join("\n", "L", "LPPC", "Lisbon", "150.0", "47.0", "36.0", "-6.0", "-20.0") + "\n";

        ConsoleResult result = runUiWithInput(
                input,
                (scanner) -> new RegisterAirControlAreaUI(controller, scanner).show()
        );

        assertTrue(result.output.contains("Code must be 2-10 uppercase alphanumeric characters."));
        assertTrue(result.output.contains("Success!"));
    }

    @Test
    void us050RegisterAreaShouldRecoverFromInvalidSouthLatitude() {
        User operator = user("operator@aisafe.com", "Backoffice Operator", "123456789", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("operator@aisafe.com", "Password123");

        InMemoryAreaRepository areaRepo = new InMemoryAreaRepository();
        CapturingAirControlAreaController controller = new CapturingAirControlAreaController(authService, areaRepo);

        // North = 40.0. South = 50.0 é inválido (maior que North). Depois 30.0 é válido.
        String input = String.join("\n", "LPPC", "Lisbon", "150.0", "40.0", "50.0", "30.0", "-6.0", "-20.0") + "\n";

        ConsoleResult result = runUiWithInput(
                input,
                (scanner) -> new RegisterAirControlAreaUI(controller, scanner).show()
        );

        assertTrue(result.output.contains("South must be less than North (40.0)."));
        assertTrue(result.output.contains("Success!"));
    }

    // ==================== INNER CLASSES FOR MOCKING ====================

    private static class InMemoryUserRepository implements UserRepository {
        private final Map<String, User> users = new HashMap<>();

        void addUser(User user) {
            users.put(user.email().value(), user);
        }

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

        void addArea(AirControlArea area) {
            areas.put(area.identity().value(), area);
        }

        @Override
        public Optional<AirControlArea> findByCode(AirControlAreaCode code) {
            return Optional.ofNullable(areas.get(code.value()));
        }

        @Override
        public Iterable<AirControlArea> findAll() {
            return new ArrayList<>(areas.values());
        }

        @Override
        public AirControlArea save(AirControlArea entity) {
            areas.put(entity.identity().value(), entity);
            return entity;
        }

        @Override public void deleteById(AirControlAreaCode code) {}
        @Override public long count() { return areas.size(); }
        @Override public Optional<AirControlArea> findById(AirControlAreaCode code) { return findByCode(code); }
        @Override public void delete(AirControlArea entity) {}
    }

    private static class CapturingAirControlAreaController extends RegisterAirControlAreaController {
        private final AuthService authService;
        private final AirControlAreaRepository repository;

        String capturedCode;
        String capturedName;
        double capturedMinimumFuel;
        double capturedNorthLat;
        double capturedSouthLat;
        double capturedEastLon;
        double capturedWestLon;

        CapturingAirControlAreaController(AuthService authService, AirControlAreaRepository repository) {
            super(authService, repository);
            this.authService = authService;
            this.repository = repository;
        }

        @Override
        public AirControlArea registerAirControlArea(String code, String name, double minimumFuel,
                                                     double northLat, double southLat, double eastLon, double westLon) {
            this.capturedCode = code;
            this.capturedName = name;
            this.capturedMinimumFuel = minimumFuel;
            this.capturedNorthLat = northLat;
            this.capturedSouthLat = southLat;
            this.capturedEastLon = eastLon;
            this.capturedWestLon = westLon;


            authService.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);

            final AirControlAreaCode areaCode = AirControlAreaCode.of(code);
            if (repository.findByCode(areaCode).isPresent()) {
                throw new IllegalStateException("An Air Control Area with code '" + code + "' already exists.");
            }

            final GeoBoundary newBoundary = new GeoBoundary(northLat, southLat, eastLon, westLon);
            for (AirControlArea existing : repository.findAll()) {
                if (existing.geoBoundary().overlapsWith(newBoundary)) {
                    throw new IllegalStateException("The boundary overlaps with existing area: " + existing.identity());
                }
            }

            return new AirControlArea(areaCode, name, minimumFuel, newBoundary);
        }
    }
}
