package eapli.aisafe.aircraftmodelmanagement.application;

import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftType;
import eapli.aisafe.aircraftmodelmanagement.repositories.AircraftModelRepository;
import eapli.aisafe.enginemanagement.domain.EngineModel;
import eapli.aisafe.enginemanagement.domain.EngineModelId;
import eapli.aisafe.enginemanagement.domain.EngineType;
import eapli.aisafe.enginemanagement.domain.FuelType;
import eapli.aisafe.enginemanagement.repositories.EngineModelRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import eapli.aisafe.generic.domain.*;

class AddEngineToAircraftModelControllerTest {

    // ── stubs ─────────────────────────────────────────────────────────────────

    private static final AuthService AUTHORIZED = new AuthService(null) {
        @Override public void ensureHasAnyRole(Role... roles) { /* allowed */ }
    };

    private static final AuthService UNAUTHORIZED = new AuthService(null) {
        @Override public void ensureHasAnyRole(Role... roles) {
            throw new SecurityException("Access denied.");
        }
    };

    private static class InMemoryAircraftModelRepository implements AircraftModelRepository {
        private final List<AircraftModel> store = new ArrayList<>();

        @Override
        public Optional<AircraftModel> findById(final AircraftModelId id) {
            return store.stream().filter(m -> m.identity().equals(id)).findFirst();
        }

        @Override
        public Iterable<AircraftModel> findAll() { return new ArrayList<>(store); }

        @Override
        public AircraftModel save(final AircraftModel model) {
            store.removeIf(m -> m.identity().equals(model.identity()));
            store.add(model);
            return model;
        }

        @Override
        public Iterable<String> findAllMakers() {
            return store.stream()
                    .map(AircraftModel::makerName)
                    .distinct()
                    .collect(java.util.stream.Collectors.toList());
        }

        @Override
        public Iterable<AircraftModel> findByMaker(final String makerName) {
            return store.stream()
                    .filter(m -> m.makerName().equalsIgnoreCase(makerName))
                    .collect(java.util.stream.Collectors.toList());
        }
    }

    private static class InMemoryEngineModelRepository implements EngineModelRepository {
        private final List<EngineModel> store = new ArrayList<>();

        @Override
        public Optional<EngineModel> findById(final EngineModelId id) {
            return store.stream().filter(e -> e.identity().equals(id)).findFirst();
        }

        @Override
        public Iterable<EngineModel> findAll() { return new ArrayList<>(store); }

        @Override
        public Iterable<EngineModel> findByType(final EngineType type) {
            return store.stream().filter(e -> e.engineType() == type)
                    .collect(java.util.stream.Collectors.toList());
        }

        @Override
        public EngineModel save(final EngineModel model) {
            store.removeIf(e -> e.identity().equals(model.identity()));
            store.add(model);
            return model;
        }
    }

    // ── fixtures ──────────────────────────────────────────────────────────────

    private InMemoryAircraftModelRepository aircraftRepo;
    private InMemoryEngineModelRepository engineRepo;
    private AddEngineToAircraftModelController controller;

    private AircraftModelId boeing777Id;
    private EngineModelId ge90bId;
    private EngineModelId trentId;
    private EngineModelId turbopropId;

    @BeforeEach
    void setUp() {
        aircraftRepo = new InMemoryAircraftModelRepository();
        engineRepo   = new InMemoryEngineModelRepository();
        controller   = new AddEngineToAircraftModelController(
                AUTHORIZED, aircraftRepo, engineRepo);

        // engines
        ge90bId = EngineModelId.of("GE 90B", "GE");
        trentId = EngineModelId.of("RR Trent 970", "RR");
        turbopropId = EngineModelId.of("SomeProp", "Maker");

        engineRepo.save(new EngineModel(ge90bId, EngineType.TURBOFAN, Thrust.of(489_000), Thrust.of(300_000), Speed.of(0.84), TSFC.of(1.6e-4), Coefficient.of(1.0), FuelType.JET_A1));
        engineRepo.save(new EngineModel(trentId, EngineType.TURBOFAN, Thrust.of(348_310), Thrust.of(180_000), Speed.of(0.85), TSFC.of(1.6e-4), Coefficient.of(0.96), FuelType.JET_A1));
        engineRepo.save(new EngineModel(turbopropId, EngineType.TURBOPROP, Thrust.of(100_000), Thrust.of(60_000), Speed.of(0.5), TSFC.of(1.8e-4), Coefficient.of(1.0), FuelType.JET_A1));

        // aircraft pre-loaded with GE 90B
        boeing777Id = AircraftModelId.of("777-200ER", "Boeing");
        final Set<EngineModel> initialEngines = new HashSet<>();
        initialEngines.add(engineRepo.findById(ge90bId).get());
        aircraftRepo.save(new AircraftModel(boeing777Id, AircraftType.PASSENGER, Weight.of(145_149), Weight.of(347_452), Weight.of(224_982), Weight.of(145_538), Altitude.of(43_100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 13_080, initialEngines));
    }

    // ── happy path ────────────────────────────────────────────────────────────

    @Test
    void givenCompatibleEngine_engineIsAdded() {
        final AircraftModel updated =
                controller.addEngineToAircraftModel(boeing777Id, trentId);
        assertEquals(2, updated.certifiedEngines().size());
    }

    @Test
    void givenCompatibleEngine_aircraftIsPersisted() {
        controller.addEngineToAircraftModel(boeing777Id, trentId);
        final AircraftModel persisted = aircraftRepo.findById(boeing777Id).get();
        assertEquals(2, persisted.certifiedEngines().size());
    }

    @Test
    void givenCompatibleEngine_returnedModelContainsNewEngine() {
        final AircraftModel updated =
                controller.addEngineToAircraftModel(boeing777Id, trentId);
        final EngineModel trent = engineRepo.findById(trentId).get();
        assertTrue(updated.certifiedEngines().contains(trent));
    }

    // ── not found ─────────────────────────────────────────────────────────────

    @Test
    void givenNonExistentAircraftModel_throwsIllegalState() {
        final AircraftModelId unknown = AircraftModelId.of("Unknown", "Nobody");
        assertThrows(IllegalStateException.class,
                () -> controller.addEngineToAircraftModel(unknown, trentId));
    }

    @Test
    void givenNonExistentEngineModel_throwsIllegalState() {
        final EngineModelId unknown = EngineModelId.of("Unknown", "Nobody");
        assertThrows(IllegalStateException.class,
                () -> controller.addEngineToAircraftModel(boeing777Id, unknown));
    }

    // ── domain invariants ─────────────────────────────────────────────────────

    @Test
    void givenIncompatibleEngineType_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class,
                () -> controller.addEngineToAircraftModel(boeing777Id, turbopropId));
    }

    @Test
    void givenDuplicateEngine_throwsIllegalState() {
        assertThrows(IllegalStateException.class,
                () -> controller.addEngineToAircraftModel(boeing777Id, ge90bId));
    }

    // ── authorization ─────────────────────────────────────────────────────────

    @Test
    void givenUnauthorizedUser_throwsSecurityException() {
        final AddEngineToAircraftModelController unauthorized =
                new AddEngineToAircraftModelController(UNAUTHORIZED, aircraftRepo, engineRepo);
        assertThrows(SecurityException.class,
                () -> unauthorized.addEngineToAircraftModel(boeing777Id, trentId));
    }

    @Test
    void givenUnauthorizedUser_aircraftIsNotModified() {
        final AddEngineToAircraftModelController unauthorized =
                new AddEngineToAircraftModelController(UNAUTHORIZED, aircraftRepo, engineRepo);
        try {
            unauthorized.addEngineToAircraftModel(boeing777Id, trentId);
        } catch (SecurityException ignored) {}

        final AircraftModel unchanged = aircraftRepo.findById(boeing777Id).get();
        assertEquals(1, unchanged.certifiedEngines().size());
    }

    // ── listing ───────────────────────────────────────────────────────────────

    @Test
    void allAircraftModels_returnsRegisteredModels() {
        int count = 0;
        for (AircraftModel ignored : controller.allAircraftModels()) count++;
        assertEquals(1, count);
    }

    @Test
    void allEngineModels_returnsAllEngines() {
        int count = 0;
        for (EngineModel ignored : controller.allEngineModels()) count++;
        assertEquals(3, count);
    }
}
