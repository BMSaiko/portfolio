package eapli.aisafe.app.backoffice.console.presentation.enginemanagement;

import eapli.aisafe.enginemanagement.application.RegisterEngineModelController;
import eapli.aisafe.enginemanagement.domain.EngineModel;
import eapli.aisafe.enginemanagement.domain.EngineModelId;
import eapli.aisafe.enginemanagement.domain.EngineType;
import eapli.aisafe.enginemanagement.domain.FuelType;
import eapli.aisafe.enginemanagement.repositories.EngineModelRepository;
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
 * UI acceptance-style tests for engine model stories:
 *
 * <ul>
 *     <li>US056 - Create an aircraft engine model</li>
 * </ul>
 */
class RegisterEngineModelAcceptanceTest {

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

    // ── helpers ──────────────────────────────────────────────────────────────

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

    /**
     * Builds a valid multi-line console input for the RegisterEngineModelUI.
     */
    private String engineInput(String name, String makerId,
                               String engineTypeChoice, String fuelTypeChoice,
                               String thrustStandstill, String thrustCruise,
                               String cruiseMach, String tsfc, String lapseFactor) {
        return String.join("\n",
                name, makerId,
                engineTypeChoice, fuelTypeChoice,
                thrustStandstill, thrustCruise, cruiseMach,
                tsfc, lapseFactor) + "\n";
    }

    private record ConsoleResult(String output) {}

    // ==================== US056 – REGISTER ENGINE MODEL ====================

    @Test
    void us056RegisterEngineModelShouldSucceedWithValidInputAsBackofficeOperator() {
        User operator = user("operator@aisafe.com", "Backoffice Op", "912345678", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("operator@aisafe.com", "Password123");

        InMemoryEngineModelRepository repo = new InMemoryEngineModelRepository();
        CapturingEngineModelController controller = new CapturingEngineModelController(authService, repo);

        ConsoleResult result = runUiWithInput(
                engineInput("GE 90B", "GE", "2", "1",
                        "489000", "300000", "0.84", "1.6E-4", "1.0"),
                () -> new RegisterEngineModelUI(controller, new java.util.Scanner(System.in)).show()
        );

        assertEquals("GE 90B", controller.capturedName);
        assertEquals("GE", controller.capturedMakerId);
        assertEquals(489000.0, controller.capturedThrustStandstill);
        assertEquals(300000.0, controller.capturedThrustCruise);
        assertEquals(0.84, controller.capturedCruiseMach);
        assertEquals(1.6E-4, controller.capturedTsfc);
        assertEquals(1.0, controller.capturedLapseFactor);
        assertTrue(result.output.contains("Success!"));
        assertTrue(result.output.contains("GE 90B"));
    }

    @Test
    void us056RegisterEngineModelShouldSucceedAsAdmin() {
        User admin = user("admin@aisafe.com", "Admin", "911111111", Role.ADMIN);
        userRepo.addUser(admin);
        authService.login("admin@aisafe.com", "Password123");

        InMemoryEngineModelRepository repo = new InMemoryEngineModelRepository();
        CapturingEngineModelController controller = new CapturingEngineModelController(authService, repo);

        ConsoleResult result = runUiWithInput(
                engineInput("RR Trent 970", "RR", "2", "1",
                        "348310", "180000", "0.85", "1.6E-4", "0.96"),
                () -> new RegisterEngineModelUI(controller, new java.util.Scanner(System.in)).show()
        );

        assertEquals("RR Trent 970", controller.capturedName);
        assertTrue(result.output.contains("Success!"));
    }

    @Test
    void us056RegisterEngineModelShouldFailWhenUnauthorizedRole() {
        User pilot = user("pilot@aisafe.com", "Pilot", "999999999", Role.PILOT);
        userRepo.addUser(pilot);
        authService.login("pilot@aisafe.com", "Password123");

        InMemoryEngineModelRepository repo = new InMemoryEngineModelRepository();
        CapturingEngineModelController controller = new CapturingEngineModelController(authService, repo);

        ConsoleResult result = runUiWithInput(
                engineInput("GE 90B", "GE", "2", "1",
                        "489000", "300000", "0.84", "1.6E-4", "1.0"),
                () -> new RegisterEngineModelUI(controller, new java.util.Scanner(System.in)).show()
        );

        assertTrue(result.output.contains("ACCESS DENIED") || result.output.contains("Error"));
        assertFalse(result.output.contains("Success!"));
    }

    @Test
    void us056RegisterEngineModelShouldFailWhenDuplicateNameAndMaker() {
        User operator = user("operator@aisafe.com", "Backoffice Op", "912345678", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("operator@aisafe.com", "Password123");

        InMemoryEngineModelRepository repo = new InMemoryEngineModelRepository();
        EngineModelId existingId = EngineModelId.of("GE 90B", "GE");
        repo.preLoad(new EngineModel(existingId, EngineType.TURBOFAN,
                eapli.aisafe.generic.domain.Thrust.of(489000.0), eapli.aisafe.generic.domain.Thrust.of(300000.0), eapli.aisafe.generic.domain.Speed.of(0.84), eapli.aisafe.generic.domain.TSFC.of(1.6E-4), eapli.aisafe.generic.domain.Coefficient.of(1.0), FuelType.JET_A));

        CapturingEngineModelController controller = new CapturingEngineModelController(authService, repo);

        ConsoleResult result = runUiWithInput(
                engineInput("GE 90B", "GE", "2", "1",
                        "489000", "300000", "0.84", "1.6E-4", "1.0"),
                () -> new RegisterEngineModelUI(controller, new java.util.Scanner(System.in)).show()
        );

        assertTrue(result.output.contains("ERROR:"));
        assertTrue(result.output.contains("already exists"));
    }

    @Test
    void us056RegisterEngineModelShouldRejectCruiseThrustGreaterThanStandstill() {
        User operator = user("operator@aisafe.com", "Backoffice Op", "912345678", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("operator@aisafe.com", "Password123");

        InMemoryEngineModelRepository repo = new InMemoryEngineModelRepository();
        CapturingEngineModelController controller = new CapturingEngineModelController(authService, repo);

        // thrustCruise (999999) > thrustStandstill (489000) → UI reprompts; second attempt valid
        String input = String.join("\n",
                "GE 90B", "GE", "2", "1",
                "489000",
                "999999",   // invalid: greater than standstill
                "300000",   // valid retry
                "0.84", "1.6E-4", "1.0") + "\n";

        ConsoleResult result = runUiWithInput(input,
                () -> new RegisterEngineModelUI(controller, new java.util.Scanner(System.in)).show());

        assertTrue(result.output.contains("Cruise thrust must be less than standstill thrust"));
        assertTrue(result.output.contains("Success!"));
    }

    @Test
    void us056RegisterEngineModelShouldRejectNonPositiveThrustAtStandstill() {
        User operator = user("operator@aisafe.com", "Backoffice Op", "912345678", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("operator@aisafe.com", "Password123");

        InMemoryEngineModelRepository repo = new InMemoryEngineModelRepository();
        CapturingEngineModelController controller = new CapturingEngineModelController(authService, repo);

        String input = String.join("\n",
                "GE 90B", "GE", "2", "1",
                "0",        // invalid: must be positive
                "489000",   // valid retry
                "300000", "0.84", "1.6E-4", "1.0") + "\n";

        ConsoleResult result = runUiWithInput(input,
                () -> new RegisterEngineModelUI(controller, new java.util.Scanner(System.in)).show());

        assertTrue(result.output.contains("Value must be positive"));
        assertTrue(result.output.contains("Success!"));
    }

    @Test
    void us056RegisterEngineModelShouldRejectInvalidEngineTypeChoice() {
        User operator = user("operator@aisafe.com", "Backoffice Op", "912345678", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("operator@aisafe.com", "Password123");

        InMemoryEngineModelRepository repo = new InMemoryEngineModelRepository();
        CapturingEngineModelController controller = new CapturingEngineModelController(authService, repo);

        String input = String.join("\n",
                "GE 90B", "GE",
                "99",   // invalid choice
                "2",    // valid retry (TURBOFAN)
                "1",    // fuelType
                "489000", "300000", "0.84", "1.6E-4", "1.0") + "\n";

        ConsoleResult result = runUiWithInput(input,
                () -> new RegisterEngineModelUI(controller, new java.util.Scanner(System.in)).show());

        assertTrue(result.output.contains("Invalid choice"));
        assertTrue(result.output.contains("Success!"));
    }

    @Test
    void us056RegisterEngineModelShouldRejectNonNumericTsfc() {
        User operator = user("operator@aisafe.com", "Backoffice Op", "912345678", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("operator@aisafe.com", "Password123");

        InMemoryEngineModelRepository repo = new InMemoryEngineModelRepository();
        CapturingEngineModelController controller = new CapturingEngineModelController(authService, repo);

        String input = String.join("\n",
                "GE 90B", "GE", "2", "1",
                "489000", "300000", "0.84",
                "notanumber",   // invalid TSFC
                "1.6E-4",       // valid retry
                "1.0") + "\n";

        ConsoleResult result = runUiWithInput(input,
                () -> new RegisterEngineModelUI(controller, new java.util.Scanner(System.in)).show());

        assertTrue(result.output.contains("Invalid number"));
        assertTrue(result.output.contains("Success!"));
    }

    @Test
    void us056RegisterEngineModelShouldRejectNegativeLapseFactor() {
        User operator = user("operator@aisafe.com", "Backoffice Op", "912345678", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("operator@aisafe.com", "Password123");

        InMemoryEngineModelRepository repo = new InMemoryEngineModelRepository();
        CapturingEngineModelController controller = new CapturingEngineModelController(authService, repo);

        String input = String.join("\n",
                "GE 90B", "GE", "2", "1",
                "489000", "300000", "0.84", "1.6E-4",
                "-1.0",  // invalid: must be >= 0
                "1.0") + "\n";

        ConsoleResult result = runUiWithInput(input,
                () -> new RegisterEngineModelUI(controller, new java.util.Scanner(System.in)).show());

        assertTrue(result.output.contains("Value must be zero or positive"));
        assertTrue(result.output.contains("Success!"));
    }

    @Test
    void us056RegisterEngineModelShouldDisplayEngineTypeAndFuelTypeOptions() {
        User operator = user("operator@aisafe.com", "Backoffice Op", "912345678", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("operator@aisafe.com", "Password123");

        InMemoryEngineModelRepository repo = new InMemoryEngineModelRepository();
        CapturingEngineModelController controller = new CapturingEngineModelController(authService, repo);

        ConsoleResult result = runUiWithInput(
                engineInput("GE 90B", "GE", "2", "1",
                        "489000", "300000", "0.84", "1.6E-4", "1.0"),
                () -> new RegisterEngineModelUI(controller, new java.util.Scanner(System.in)).show()
        );

        assertTrue(result.output.contains("Engine Type:"));
        assertTrue(result.output.contains("Fuel Type:"));
    }

    // ==================== CONTROLLER-LEVEL UNIT TESTS ====================

    @Test
    void controllerShouldPersistEngineModelWhenValid() {
        User operator = user("operator@aisafe.com", "Backoffice Op", "912345678", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("operator@aisafe.com", "Password123");

        InMemoryEngineModelRepository repo = new InMemoryEngineModelRepository();
        RegisterEngineModelController controller = new RegisterEngineModelController(authService, repo);

        EngineModel saved = controller.registerEngineModel(
                "GE 90B", "GE", "TURBOFAN",
                489000, 300000, 0.84,
                1.6E-4, 1.0, FuelType.JET_A);

        assertNotNull(saved);
        assertEquals(1, repo.count());
    }

    @Test
    void controllerShouldThrowWhenDuplicateEngineModel() {
        User operator = user("operator@aisafe.com", "Backoffice Op", "912345678", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("operator@aisafe.com", "Password123");

        InMemoryEngineModelRepository repo = new InMemoryEngineModelRepository();
        RegisterEngineModelController controller = new RegisterEngineModelController(authService, repo);

        controller.registerEngineModel("GE 90B", "GE", "TURBOFAN",
                489000, 300000, 0.84, 1.6E-4, 1.0, FuelType.JET_A);

        assertThrows(IllegalStateException.class, () ->
                controller.registerEngineModel("GE 90B", "GE", "TURBOFAN",
                        489000, 300000, 0.84, 1.6E-4, 1.0, FuelType.JET_A));
    }

    @Test
    void controllerShouldThrowWhenPilotTriesToRegister() {
        User pilot = user("pilot@aisafe.com", "Pilot", "999999999", Role.PILOT);
        userRepo.addUser(pilot);
        authService.login("pilot@aisafe.com", "Password123");

        InMemoryEngineModelRepository repo = new InMemoryEngineModelRepository();
        RegisterEngineModelController controller = new RegisterEngineModelController(authService, repo);

        assertThrows(SecurityException.class, () ->
                controller.registerEngineModel("GE 90B", "GE", "TURBOFAN",
                        489000, 300000, 0.84, 1.6E-4, 1.0, FuelType.JET_A));
    }

    @Test
    void controllerShouldReturnAllFuelTypes() {
        InMemoryEngineModelRepository repo = new InMemoryEngineModelRepository();
        RegisterEngineModelController controller = new RegisterEngineModelController(authService, repo);

        FuelType[] types = controller.allFuelTypes();
        assertNotNull(types);
        assertTrue(types.length > 0);
    }

    @Test
    void controllerAllEngineModelsShouldRequireAuthorisation() {
        User pilot = user("pilot@aisafe.com", "Pilot", "999999999", Role.PILOT);
        userRepo.addUser(pilot);
        authService.login("pilot@aisafe.com", "Password123");

        InMemoryEngineModelRepository repo = new InMemoryEngineModelRepository();
        RegisterEngineModelController controller = new RegisterEngineModelController(authService, repo);

        assertThrows(SecurityException.class, controller::allEngineModels);
    }

    // ==================== INNER CLASSES ====================

    private static class InMemoryUserRepository implements UserRepository {
        private final Map<String, User> users = new HashMap<>();

        void addUser(User user) { users.put(user.email().value(), user); }

        @Override public Optional<User> findByEmail(Email email) {
            return Optional.ofNullable(users.get(email.value()));
        }
        @Override public Optional<User> findActiveByEmail(Email email) {
            User u = users.get(email.value());
            return (u != null && u.isActive()) ? Optional.of(u) : Optional.empty();
        }
        @Override public Iterable<User> findActiveByRole(Role role) {
            return users.values().stream()
                    .filter(User::isActive)
                    .filter(u -> u.hasRole(role))
                    .toList();
        }
        @Override public boolean existsByEmail(Email email) {
            return users.containsKey(email.value());
        }
        @Override public Iterable<User> findAll() {
            return new ArrayList<>(users.values());
        }
        @Override public User save(User entity) {
            users.put(entity.email().value(), entity);
            return entity;
        }
        @Override public Optional<User> findById(Email email) {
            return findByEmail(email);
        }
        @Override public void delete(User entity) {
            users.remove(entity.email().value());
        }
        @Override public void deleteById(Email email) {
            users.remove(email.value());
        }
        @Override public long count() {
            return users.size();
        }
    }

    private static class InMemoryEngineModelRepository implements EngineModelRepository {
        private final Map<String, EngineModel> store = new LinkedHashMap<>();

        void preLoad(EngineModel model) {
            store.put(model.identity().toString(), model);
        }

        @Override public Optional<EngineModel> findById(EngineModelId id) {
            return Optional.ofNullable(store.get(id.toString()));
        }
        @Override public Iterable<EngineModel> findAll() {
            return new ArrayList<>(store.values());
        }
        @Override public Iterable<EngineModel> findByType(EngineType type) {
            return store.values().stream()
                    .filter(e -> e.engineType() == type)
                    .toList();
        }
        @Override public EngineModel save(EngineModel entity) {
            store.put(entity.identity().toString(), entity);
            return entity;
        }
        public void delete(EngineModel entity) {
            store.remove(entity.identity().toString());
        }
        public void deleteById(EngineModelId id) {
            store.remove(id.toString());
        }
        public long count() { return store.size(); }
    }

    /**
     * Capturing controller — records arguments passed by the UI and delegates
     * to the real implementation so all domain invariants are still enforced.
     */
    private static class CapturingEngineModelController extends RegisterEngineModelController {
        String capturedName;
        String capturedMakerId;
        String capturedEngineType;
        double capturedThrustStandstill;
        double capturedThrustCruise;
        double capturedCruiseMach;
        double capturedTsfc;
        double capturedLapseFactor;
        FuelType capturedFuelType;

        CapturingEngineModelController(AuthService auth, EngineModelRepository repo) {
            super(auth, repo);
        }

        @Override
        public EngineModel registerEngineModel(
                String name, String makerId, String engineType,
                double thrustAtStandstill, double thrustAtCruiseSpeed, double cruiseSpeedMach,
                double tsfc, double lapseRateFactor, FuelType fuelType) {

            this.capturedName = name;
            this.capturedMakerId = makerId;
            this.capturedEngineType = engineType;
            this.capturedThrustStandstill = thrustAtStandstill;
            this.capturedThrustCruise = thrustAtCruiseSpeed;
            this.capturedCruiseMach = cruiseSpeedMach;
            this.capturedTsfc = tsfc;
            this.capturedLapseFactor = lapseRateFactor;
            this.capturedFuelType = fuelType;

            return super.registerEngineModel(name, makerId, engineType,
                    thrustAtStandstill, thrustAtCruiseSpeed, cruiseSpeedMach,
                    tsfc, lapseRateFactor, fuelType);
        }
    }
}