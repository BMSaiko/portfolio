package eapli.aisafe.app.backoffice.console.presentation.aircraftmodelmanagement;

import eapli.aisafe.aircraftmodelmanagement.application.AddEngineToAircraftModelController;
import eapli.aisafe.aircraftmodelmanagement.application.RegisterAircraftModelController;
import eapli.aisafe.aircraftmodelmanagement.application.RemoveEngineFromAircraftModelController;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftType;
import eapli.aisafe.aircraftmodelmanagement.repositories.AircraftModelRepository;
import eapli.aisafe.enginemanagement.domain.EngineModel;
import eapli.aisafe.enginemanagement.domain.EngineModelId;
import eapli.aisafe.enginemanagement.domain.EngineType;
import eapli.aisafe.enginemanagement.domain.FuelType;
import eapli.aisafe.enginemanagement.repositories.EngineModelRepository;
import eapli.aisafe.fleetmanagement.domain.Aircraft;
import eapli.aisafe.fleetmanagement.repositories.AircraftRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UI acceptance-style tests for aircraft model management user stories:
 *
 * <ul>
 *   <li>US055 – Register Aircraft Model</li>
 *   <li>US057 – Add Engine Model to Aircraft Model</li>
 *   <li>US058 – Remove Engine Model from Aircraft Model</li>
 * </ul>
 */
class AircraftModelManagementUiAcceptanceTest {

    private final PrintStream originalOut = System.out;

    private InMemoryUserRepository userRepo;
    private InMemoryAircraftModelRepository aircraftModelRepo;
    private InMemoryEngineModelRepository engineModelRepo;
    private InMemoryAircraftRepository aircraftRepo;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepo          = new InMemoryUserRepository();
        authService       = new AuthService(userRepo);
        aircraftModelRepo = new InMemoryAircraftModelRepository();
        engineModelRepo   = new InMemoryEngineModelRepository();
        aircraftRepo      = new InMemoryAircraftRepository();
    }

    @AfterEach
    void restoreConsole() {
        System.setOut(originalOut);
        authService.logout();
    }

    // ==================== HELPERS ====================

    private ConsoleResult runWithInput(String input, Consumer<Scanner> uiAction) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buf, true, StandardCharsets.UTF_8));
        Scanner scanner = new Scanner(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        uiAction.accept(scanner);
        return new ConsoleResult(buf.toString(StandardCharsets.UTF_8));
    }

    private User user(String email, String name, String phone, Role role) {
        return new User(new Email(email), "Password123", eapli.aisafe.generic.domain.UserName.of(name), eapli.aisafe.generic.domain.PhoneNumber.of(phone), Set.of(role));
    }

    private static EngineModel makeEngine(String name, String makerId) {
        return new EngineModel(
                EngineModelId.of(name, makerId),
                EngineType.TURBOFAN,
                eapli.aisafe.generic.domain.Thrust.of(489_000.0),
                eapli.aisafe.generic.domain.Thrust.of(300_000.0),
                eapli.aisafe.generic.domain.Speed.of(0.84),
                eapli.aisafe.generic.domain.TSFC.of(1.6E-4),
                eapli.aisafe.generic.domain.Coefficient.of(1.0),
                FuelType.JET_A);
    }

    private static AircraftModel makeAircraftModel(String modelId, String makerName,
                                                   Set<EngineModel> engines) {
        AircraftModelId id = AircraftModelId.of(modelId, makerName);
        return new AircraftModel(
                id,
                AircraftType.values()[0],
                eapli.aisafe.generic.domain.Weight.of(50_000), eapli.aisafe.generic.domain.Weight.of(100_000), eapli.aisafe.generic.domain.Weight.of(80_000), eapli.aisafe.generic.domain.Weight.of(20_000),
                eapli.aisafe.generic.domain.Altitude.of(41_000), eapli.aisafe.generic.domain.Speed.of(0.84), eapli.aisafe.generic.domain.Area.of(300.0), 60.0, 9.0, eapli.aisafe.generic.domain.Coefficient.of(0.85), eapli.aisafe.generic.domain.Coefficient.of(0.017), eapli.aisafe.generic.domain.Coefficient.of(0.5),
                2, 200, 8_000.0,
                engines);
    }

    private static String registerAircraftInput(int aircraftTypeChoice, String engineIndices) {
        return "777-200ER\nBoeing\n" + aircraftTypeChoice + "\ny\n"
                + "50000\n100000\n80000\n20000\ny\n"
                + "41000\n0.84\n8000\ny\n"
                + "300.0\n60.0\n9.0\n0.85\n0.017\n0.5\ny\n"
                + "2\n200\ny\n"
                + engineIndices + "\ny\n";
    }

    private record ConsoleResult(String output) {}

    // ==================== US055 – REGISTER AIRCRAFT MODEL ====================

    @Test
    void us055_RegisterAircraftModelSuccessfully() {
        User operator = user("op@aisafe.com", "Operator", "123456789", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("op@aisafe.com", "Password123");

        EngineModel cfm56 = makeEngine("CFM56", "CFM");
        engineModelRepo.save(cfm56);

        CapturingRegisterAircraftModelController controller =
                new CapturingRegisterAircraftModelController(authService, aircraftModelRepo, engineModelRepo);

        ConsoleResult result = runWithInput(
                registerAircraftInput(1, "1"),
                scanner -> new RegisterAircraftModelUI(controller, scanner).show());

        assertEquals("777-200ER", controller.capturedModelId);
        assertEquals("Boeing", controller.capturedMakerName);
        assertNotNull(controller.capturedEngineIds);
        assertFalse(controller.capturedEngineIds.isEmpty());
        assertTrue(result.output.contains("Success!"));
    }

    @Test
    void us055_DuplicateModelIdAndMakerShouldFail() {
        User operator = user("op@aisafe.com", "Operator", "123456789", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("op@aisafe.com", "Password123");

        EngineModel cfm56 = makeEngine("CFM56", "CFM");
        engineModelRepo.save(cfm56);

        aircraftModelRepo.save(makeAircraftModel("777-200ER", "Boeing",
                new HashSet<>(Set.of(cfm56))));

        RegisterAircraftModelController controller =
                new RegisterAircraftModelController(authService, aircraftModelRepo, engineModelRepo);

        ConsoleResult result = runWithInput(
                registerAircraftInput(1, "1"),
                scanner -> new RegisterAircraftModelUI(controller, scanner).show());

        assertTrue(result.output.contains("ERROR:"));
        assertTrue(result.output.contains("already exists"));
        assertEquals(1, aircraftModelRepo.count());
    }

    @Test
    void us055_WithoutRequiredRoleShouldThrowSecurityException() {
        User pilot = user("pilot@aisafe.com", "Pilot", "123456789", Role.PILOT);
        userRepo.addUser(pilot);
        authService.login("pilot@aisafe.com", "Password123");

        EngineModel cfm56 = makeEngine("CFM56", "CFM");
        engineModelRepo.save(cfm56);

        RegisterAircraftModelController controller =
                new RegisterAircraftModelController(authService, aircraftModelRepo, engineModelRepo);

        assertThrows(SecurityException.class, () ->
                runWithInput(
                        registerAircraftInput(1, "1"),
                        scanner -> new RegisterAircraftModelUI(controller, scanner).show()));
    }

    // ==================== US057 – ADD ENGINE MODEL TO AIRCRAFT MODEL ====================

    @Test
    void us057_AddEngineToAircraftModelSuccessfully() {
        User operator = user("op@aisafe.com", "Operator", "123456789", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("op@aisafe.com", "Password123");

        EngineModel cfm56 = makeEngine("CFM56", "CFM");
        EngineModel ge90  = makeEngine("GE90", "GE");
        engineModelRepo.save(cfm56);
        engineModelRepo.save(ge90);

        aircraftModelRepo.save(makeAircraftModel("777-200ER", "Boeing",
                new HashSet<>(Set.of(cfm56))));

        CapturingAddEngineController controller =
                new CapturingAddEngineController(authService, aircraftModelRepo, engineModelRepo);

        ConsoleResult result = runWithInput("1\n2\ny\n",
                scanner -> new AddEngineToAircraftModelUI(controller, scanner).show());

        assertNotNull(controller.capturedAircraftModelId);
        assertNotNull(controller.capturedEngineModelId);
        assertTrue(result.output.contains("Success!"));
        assertTrue(result.output.contains("GE90"));
    }

    @Test
    void us057_NoAircraftModelsRegistered() {
        User operator = user("op@aisafe.com", "Operator", "123456789", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("op@aisafe.com", "Password123");

        AddEngineToAircraftModelController controller =
                new AddEngineToAircraftModelController(authService, aircraftModelRepo, engineModelRepo);

        ConsoleResult result = runWithInput("",
                scanner -> new AddEngineToAircraftModelUI(controller, scanner).show());

        assertTrue(result.output.contains("No aircraft models registered"));
    }

    @Test
    void us057_NoEngineModelsAvailableToAdd() {
        User operator = user("op@aisafe.com", "Operator", "123456789", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("op@aisafe.com", "Password123");

        EngineModel cfm56 = makeEngine("CFM56", "CFM");
        aircraftModelRepo.save(makeAircraftModel("777-200ER", "Boeing",
                new HashSet<>(Set.of(cfm56))));

        AddEngineToAircraftModelController controller =
                new AddEngineToAircraftModelController(authService, aircraftModelRepo, engineModelRepo);

        ConsoleResult result = runWithInput("1\n",
                scanner -> new AddEngineToAircraftModelUI(controller, scanner).show());

        assertTrue(result.output.contains("No engine models registered"));
    }

    @Test
    void us057_AddEngineAlreadyCertifiedShouldFail() {
        User operator = user("op@aisafe.com", "Operator", "123456789", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("op@aisafe.com", "Password123");

        EngineModel cfm56 = makeEngine("CFM56", "CFM");
        engineModelRepo.save(cfm56);

        aircraftModelRepo.save(makeAircraftModel("777-200ER", "Boeing",
                new HashSet<>(Set.of(cfm56))));

        AddEngineToAircraftModelController controller =
                new AddEngineToAircraftModelController(authService, aircraftModelRepo, engineModelRepo);

        ConsoleResult result = runWithInput("1\n1\ny\n",
                scanner -> new AddEngineToAircraftModelUI(controller, scanner).show());

        assertTrue(result.output.contains("ERROR:"));
    }

    @Test
    void us057_AddEngineCancelledByOperator() {
        User operator = user("op@aisafe.com", "Operator", "123456789", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("op@aisafe.com", "Password123");

        EngineModel cfm56 = makeEngine("CFM56", "CFM");
        EngineModel ge90  = makeEngine("GE90", "GE");
        engineModelRepo.save(cfm56);
        engineModelRepo.save(ge90);
        aircraftModelRepo.save(makeAircraftModel("777-200ER", "Boeing",
                new HashSet<>(Set.of(cfm56))));

        CapturingAddEngineController controller =
                new CapturingAddEngineController(authService, aircraftModelRepo, engineModelRepo);

        ConsoleResult result = runWithInput("1\n2\nn\n",
                scanner -> new AddEngineToAircraftModelUI(controller, scanner).show());

        assertNull(controller.capturedAircraftModelId);
        assertTrue(result.output.contains("Operation cancelled."));
    }

    @Test
    void us057_WithoutRequiredRoleShouldThrowSecurityException() {
        User pilot = user("pilot@aisafe.com", "Pilot", "123456789", Role.PILOT);
        userRepo.addUser(pilot);
        authService.login("pilot@aisafe.com", "Password123");

        AddEngineToAircraftModelController controller =
                new AddEngineToAircraftModelController(authService, aircraftModelRepo, engineModelRepo);

        assertThrows(SecurityException.class, () ->
                runWithInput("",
                        scanner -> new AddEngineToAircraftModelUI(controller, scanner).show()));
    }

    // ==================== US058 – REMOVE ENGINE MODEL FROM AIRCRAFT MODEL ====================

    @Test
    void us058_RemoveEngineFromAircraftModelSuccessfully() {
        User operator = user("op@aisafe.com", "Operator", "123456789", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("op@aisafe.com", "Password123");

        EngineModel cfm56 = makeEngine("CFM56", "CFM");
        EngineModel ge90  = makeEngine("GE90", "GE");
        engineModelRepo.save(cfm56);
        engineModelRepo.save(ge90);

        AircraftModel aircraft = makeAircraftModel("777-200ER", "Boeing",
                new HashSet<>(Set.of(cfm56, ge90)));
        aircraftModelRepo.save(aircraft);

        CapturingRemoveEngineController controller =
                new CapturingRemoveEngineController(authService, aircraftModelRepo,
                        engineModelRepo, aircraftRepo);

        ConsoleResult result = runWithInput("1\n1\ny\n",
                scanner -> new RemoveEngineFromAircraftModelUI(controller, scanner).show());

        assertNotNull(controller.capturedAircraftModelId);
        assertNotNull(controller.capturedEngineModelId);
        assertTrue(result.output.contains("Success!"));
    }

    @Test
    void us058_NoAircraftModelsRegistered() {
        User operator = user("op@aisafe.com", "Operator", "123456789", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("op@aisafe.com", "Password123");

        RemoveEngineFromAircraftModelController controller =
                new RemoveEngineFromAircraftModelController(authService, aircraftModelRepo,
                        engineModelRepo, aircraftRepo);

        ConsoleResult result = runWithInput("",
                scanner -> new RemoveEngineFromAircraftModelUI(controller, scanner).show());

        assertTrue(result.output.contains("No aircraft models registered"));
    }

    @Test
    void us058_OnlyOneEnginePreventsRemoval() {
        User operator = user("op@aisafe.com", "Operator", "123456789", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("op@aisafe.com", "Password123");

        EngineModel cfm56 = makeEngine("CFM56", "CFM");
        aircraftModelRepo.save(makeAircraftModel("777-200ER", "Boeing",
                new HashSet<>(Set.of(cfm56))));

        RemoveEngineFromAircraftModelController controller =
                new RemoveEngineFromAircraftModelController(authService, aircraftModelRepo,
                        engineModelRepo, aircraftRepo);

        ConsoleResult result = runWithInput("1\n",
                scanner -> new RemoveEngineFromAircraftModelUI(controller, scanner).show());

        assertTrue(result.output.contains("WARNING"));
        assertTrue(result.output.contains("at least one must remain"));
    }

    @Test
    void us058_CannotRemoveWhenActualAircraftInstancesExist() {
        User operator = user("op@aisafe.com", "Operator", "123456789", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("op@aisafe.com", "Password123");

        EngineModel cfm56 = makeEngine("CFM56", "CFM");
        EngineModel ge90  = makeEngine("GE90", "GE");
        engineModelRepo.save(cfm56);
        engineModelRepo.save(ge90);

        AircraftModel aircraft = makeAircraftModel("777-200ER", "Boeing",
                new HashSet<>(Set.of(cfm56, ge90)));
        aircraftModelRepo.save(aircraft);

        AircraftModelId modelId = AircraftModelId.of("777-200ER", "Boeing");
        aircraftRepo.registerAircraftForModel(modelId);

        RemoveEngineFromAircraftModelController controller =
                new RemoveEngineFromAircraftModelController(authService, aircraftModelRepo,
                        engineModelRepo, aircraftRepo);

        ConsoleResult result = runWithInput("1\n1\ny\n",
                scanner -> new RemoveEngineFromAircraftModelUI(controller, scanner).show());

        assertTrue(result.output.contains("ERROR:"));
        assertTrue(result.output.contains("actual aircraft"));
    }

    @Test
    void us058_RemoveEngineCancelledByOperator() {
        User operator = user("op@aisafe.com", "Operator", "123456789", Role.BACKOFFICE_OPERATOR);
        userRepo.addUser(operator);
        authService.login("op@aisafe.com", "Password123");

        EngineModel cfm56 = makeEngine("CFM56", "CFM");
        EngineModel ge90  = makeEngine("GE90", "GE");
        aircraftModelRepo.save(makeAircraftModel("777-200ER", "Boeing",
                new HashSet<>(Set.of(cfm56, ge90))));

        CapturingRemoveEngineController controller =
                new CapturingRemoveEngineController(authService, aircraftModelRepo,
                        engineModelRepo, aircraftRepo);

        ConsoleResult result = runWithInput("1\n1\nn\n",
                scanner -> new RemoveEngineFromAircraftModelUI(controller, scanner).show());

        assertNull(controller.capturedAircraftModelId);
        assertTrue(result.output.contains("Operation cancelled."));
    }

    @Test
    void us058_WithoutRequiredRoleShouldThrowSecurityException() {
        User pilot = user("pilot@aisafe.com", "Pilot", "123456789", Role.PILOT);
        userRepo.addUser(pilot);
        authService.login("pilot@aisafe.com", "Password123");

        RemoveEngineFromAircraftModelController controller =
                new RemoveEngineFromAircraftModelController(authService, aircraftModelRepo,
                        engineModelRepo, aircraftRepo);

        assertThrows(SecurityException.class, () ->
                runWithInput("",
                        scanner -> new RemoveEngineFromAircraftModelUI(controller, scanner).show()));
    }

    // ==================== IN-MEMORY REPOSITORIES ====================

    /**
     * InMemoryUserRepository — implements UserRepository which extends
     * eapli.aisafe.generic.repositories.Repository<User, Email>.
     * That base interface declares: save, findAll, findById, delete.
     * The framework DomainRepository adds: ofIdentity, containsOfIdentity, deleteOfIdentity, size/count.
     * We implement all of them defensively (no @Override on framework-only methods).
     */
    private static class InMemoryUserRepository implements UserRepository {
        private final Map<String, User> users = new LinkedHashMap<>();

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
        @Override public Iterable<User> findAll() { return new ArrayList<>(users.values()); }
        @Override public User save(User entity) {
            users.put(entity.email().value(), entity); return entity;
        }
        @Override public Optional<User> findById(Email email) { return findByEmail(email); }
        @Override public void delete(User entity) { users.remove(entity.email().value()); }
        // Framework-only methods (no @Override — not declared in project's Repository<T,K>)
        public void deleteById(Email email) { users.remove(email.value()); }
        public long count() { return users.size(); }
        public Optional<User> ofIdentity(Email email) { return findByEmail(email); }
        public boolean containsOfIdentity(Email email) { return users.containsKey(email.value()); }
        public void deleteOfIdentity(Email email) { users.remove(email.value()); }
        public long size() { return users.size(); }
    }

    /**
     * InMemoryAircraftModelRepository — AircraftModelRepository does NOT extend any framework
     * interface, so only the 5 declared methods are needed. No @Override issues.
     */
    private static class InMemoryAircraftModelRepository implements AircraftModelRepository {
        private final Map<AircraftModelId, AircraftModel> store = new LinkedHashMap<>();

        @Override public Optional<AircraftModel> findById(AircraftModelId id) {
            return Optional.ofNullable(store.get(id));
        }
        @Override public Iterable<AircraftModel> findAll() {
            return new ArrayList<>(store.values());
        }
        @Override public AircraftModel save(AircraftModel model) {
            store.put(AircraftModelId.of(model.modelId(), model.makerName()), model);
            return model;
        }
        @Override public Iterable<String> findAllMakers() {
            return store.values().stream().map(AircraftModel::makerName).distinct().toList();
        }
        @Override public Iterable<AircraftModel> findByMaker(String makerName) {
            return store.values().stream()
                    .filter(m -> m.makerName().equals(makerName))
                    .toList();
        }

        long count() { return store.size(); }
    }

    /**
     * InMemoryEngineModelRepository — EngineModelRepository extends
     * eapli.aisafe.generic.repositories.Repository<EngineModel, EngineModelId>.
     * Declared methods: findById, findAll, findByType, save.
     * Base Repository adds: delete, deleteById, count (+ framework DomainRepository methods).
     */
    private static class InMemoryEngineModelRepository implements EngineModelRepository {
        private final Map<EngineModelId, EngineModel> store = new LinkedHashMap<>();

        @Override public Optional<EngineModel> findById(EngineModelId id) {
            return Optional.ofNullable(store.get(id));
        }
        @Override public Iterable<EngineModel> findAll() {
            return new ArrayList<>(store.values());
        }
        @Override public Iterable<EngineModel> findByType(EngineType type) {
            return store.values().stream().filter(e -> e.engineType() == type).toList();
        }
        @Override public EngineModel save(EngineModel model) {
            store.put(model.identity(), model); return model;
        }
        // Framework-only / not declared in EngineModelRepository (no @Override)
        public void delete(EngineModel entity) { store.remove(entity.identity()); }
        public void deleteById(EngineModelId id) { store.remove(id); }
        public long count() { return store.size(); }
        public Optional<EngineModel> ofIdentity(EngineModelId id) { return findById(id); }
        public boolean containsOfIdentity(EngineModelId id) { return store.containsKey(id); }
        public void deleteOfIdentity(EngineModelId id) { store.remove(id); }
        public long size() { return store.size(); }
    }

    /**
     * InMemoryAircraftRepository — AircraftRepository extends the EAPLI framework's
     * Repository<Aircraft, String>.
     *
     * Only methods explicitly declared in the project's Repository<T,K> interface
     * (save, findAll, findById, delete) and in AircraftRepository itself
     * (existsByAircraftModelId, existsByRegistration, findByCompany) receive @Override.
     *
     * Framework-only methods (ofIdentity, containsOfIdentity, deleteOfIdentity,
     * deleteById, count, size) are provided as plain public methods — no @Override —
     * because they are not redeclared in the project interface hierarchy visible to
     * the compiler.
     */
    private static class InMemoryAircraftRepository implements AircraftRepository {
        private final Set<AircraftModelId> modelsWithInstances = new HashSet<>();
        private final Map<String, Aircraft> store = new HashMap<>();

        /** Helper used only by tests — not part of the repository interface. */
        void registerAircraftForModel(AircraftModelId id) {
            modelsWithInstances.add(id);
        }

        // ---------- AircraftRepository-specific methods ----------

        @Override
        public boolean existsByAircraftModelId(AircraftModelId id) {
            return modelsWithInstances.contains(id);
        }

        @Override
        public boolean existsByRegistration(String registration) {
            return store.containsKey(registration);
        }

        @Override
        public Iterable<Aircraft> findByCompany(String companyIataCode) {
            return store.values().stream()
                    .filter(a -> companyIataCode.equals(a.company().iataCode()))
                    .toList();
        }

        @Override
        public Optional<Aircraft> findByRegistration(String registration) {
            return Optional.ofNullable(store.get(registration));
        }

        // ---------- Repository<Aircraft, String> declared methods ----------

        @Override
        public Aircraft save(Aircraft entity) {
            store.put(entity.identity(), entity);
            return entity;
        }

        @Override
        public Iterable<Aircraft> findAll() {
            return new ArrayList<>(store.values());
        }

        // DomainRepository uses ofIdentity — findById is not declared here (no @Override)
        public Optional<Aircraft> findById(String id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public void delete(Aircraft entity) {
            store.remove(entity.identity());
        }

        // ---------- Framework-only methods (no @Override) ----------

        public Optional<Aircraft> ofIdentity(String id) {
            return Optional.ofNullable(store.get(id));
        }

        public boolean containsOfIdentity(String id) {
            return store.containsKey(id);
        }

        public void deleteOfIdentity(String id) {
            store.remove(id);
        }

        public void deleteById(String id) {
            store.remove(id);
        }

        public long count() {
            return store.size();
        }

        public long size() {
            return store.size();
        }
    }

    // ==================== CAPTURING CONTROLLERS ====================

    private static class CapturingRegisterAircraftModelController
            extends RegisterAircraftModelController {

        String capturedModelId;
        String capturedMakerName;
        List<EngineModelId> capturedEngineIds;

        CapturingRegisterAircraftModelController(AuthService auth,
                                                 AircraftModelRepository aircraftRepo,
                                                 EngineModelRepository engineRepo) {
            super(auth, aircraftRepo, engineRepo);
        }

        @Override
        public AircraftModel registerAircraftModel(
                final String modelId,
                final String makerName,
                final String aircraftType,
                final double emptyWeight,
                final double mtow,
                final double mzfw,
                final double maxFuelCapacity,
                final double serviceCeiling,
                final double cruiseSpeedMach,
                final double wingArea,
                final double wingSpan,
                final double aspectRatio,
                final double oswaldFactor,
                final double dragCoefficient,
                final double liftCoefficient,
                final int numberOfEngines,
                final int maxPassengerCapacity,
                final double maxRange,
                final List<EngineModelId> engineIds) {
            this.capturedModelId   = modelId;
            this.capturedMakerName = makerName;
            this.capturedEngineIds = engineIds;
            return super.registerAircraftModel(
                    modelId, makerName, aircraftType,
                    emptyWeight, mtow, mzfw, maxFuelCapacity,
                    serviceCeiling, cruiseSpeedMach,
                    wingArea, wingSpan, aspectRatio, oswaldFactor,
                    dragCoefficient, liftCoefficient,
                    numberOfEngines, maxPassengerCapacity, maxRange,
                    engineIds);
        }
    }

    private static class CapturingAddEngineController extends AddEngineToAircraftModelController {

        AircraftModelId capturedAircraftModelId;
        EngineModelId   capturedEngineModelId;

        CapturingAddEngineController(AuthService auth,
                                     AircraftModelRepository aircraftRepo,
                                     EngineModelRepository engineRepo) {
            super(auth, aircraftRepo, engineRepo);
        }

        @Override
        public AircraftModel addEngineToAircraftModel(final AircraftModelId aircraftModelId,
                                                      final EngineModelId engineModelId) {
            this.capturedAircraftModelId = aircraftModelId;
            this.capturedEngineModelId   = engineModelId;
            return super.addEngineToAircraftModel(aircraftModelId, engineModelId);
        }
    }

    private static class CapturingRemoveEngineController
            extends RemoveEngineFromAircraftModelController {

        AircraftModelId capturedAircraftModelId;
        EngineModelId   capturedEngineModelId;

        CapturingRemoveEngineController(AuthService auth,
                                        AircraftModelRepository aircraftRepo,
                                        EngineModelRepository engineRepo,
                                        AircraftRepository fleetRepo) {
            super(auth, aircraftRepo, engineRepo, fleetRepo);
        }

        @Override
        public AircraftModel removeEngineFromAircraftModel(final AircraftModelId aircraftModelId,
                                                           final EngineModelId engineModelId) {
            this.capturedAircraftModelId = aircraftModelId;
            this.capturedEngineModelId   = engineModelId;
            return super.removeEngineFromAircraftModel(aircraftModelId, engineModelId);
        }
    }
}