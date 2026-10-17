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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import eapli.aisafe.generic.domain.*;

class RegisterAircraftModelControllerTest {

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
        public Iterable<AircraftModel> findAll() {
            return new ArrayList<>(store);
        }

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
                    .collect(Collectors.toList());
        }

        @Override
        public Iterable<AircraftModel> findByMaker(final String makerName) {
            return store.stream()
                    .filter(m -> m.makerName().equalsIgnoreCase(makerName))
                    .collect(Collectors.toList());
        }
    }

    private static class InMemoryEngineModelRepository implements EngineModelRepository {
        private final List<EngineModel> store = new ArrayList<>();

        @Override
        public Optional<EngineModel> findById(final EngineModelId id) {
            return store.stream().filter(e -> e.identity().equals(id)).findFirst();
        }

        @Override
        public Iterable<EngineModel> findAll() {
            return new ArrayList<>(store);
        }

        @Override
        public Iterable<EngineModel> findByType(final EngineType type) {
            return store.stream()
                    .filter(e -> e.engineType() == type)
                    .collect(Collectors.toList());
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
    private RegisterAircraftModelController controller;

    private EngineModelId ge90bId;

    @BeforeEach
    void setUp() {
        aircraftRepo = new InMemoryAircraftModelRepository();
        engineRepo   = new InMemoryEngineModelRepository();
        controller   = new RegisterAircraftModelController(AUTHORIZED, aircraftRepo, engineRepo);

        ge90bId = EngineModelId.of("GE 90B", "GE");
        engineRepo.save(new EngineModel(ge90bId, EngineType.TURBOFAN, Thrust.of(489_000), Thrust.of(300_000), Speed.of(0.84), TSFC.of(1.6e-4), Coefficient.of(1.0), FuelType.JET_A1));
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private AircraftModel register777() {
        return controller.registerAircraftModel(
                "777-200ER", "Boeing", "PASSENGER",
                145_149, 347_452, 224_982, 145_538,
                43_100, 0.84, 440, 60.93, 9.0, 0.87,
                0.017, 0.5,
                2, 400, 13_080,
                List.of(ge90bId));
    }

    // ── happy path ────────────────────────────────────────────────────────────

    @Test
    void givenValidData_aircraftModelIsRegistered() {
        final AircraftModel model = register777();
        assertNotNull(model);
        assertEquals("777-200ER", model.modelId());
        assertEquals("Boeing", model.makerName());
        assertEquals(AircraftType.PASSENGER, model.aircraftType());
    }

    @Test
    void givenValidData_aircraftModelIsPersisted() {
        register777();
        final Optional<AircraftModel> found = aircraftRepo.findById(
                AircraftModelId.of("777-200ER", "Boeing"));
        assertTrue(found.isPresent());
    }

    @Test
    void givenTwoDifferentModels_bothAreRegistered() {
        register777();

        final EngineModelId trentId = EngineModelId.of("RR Trent 970", "RR");
        engineRepo.save(new EngineModel(trentId, EngineType.TURBOFAN, Thrust.of(348_310), Thrust.of(180_000), Speed.of(0.85), TSFC.of(1.6e-4), Coefficient.of(0.96), FuelType.JET_A1));

        controller.registerAircraftModel(
                "A380", "Airbus", "PASSENGER",
                276_800, 575_000, 361_000, 256_000,
                43_000, 0.85, 845, 79.75, 7.53, 0.84,
                0.020, 0.45,
                4, 555, 15_200,
                List.of(trentId));

        int count = 0;
        for (AircraftModel ignored : aircraftRepo.findAll()) count++;
        assertEquals(2, count);
    }

    // ── uniqueness ────────────────────────────────────────────────────────────

    @Test
    void givenDuplicateModelIdAndMaker_throwsIllegalState() {
        register777();
        assertThrows(IllegalStateException.class, () -> register777());
    }

    @Test
    void givenDuplicateCaseInsensitive_throwsIllegalState() {
        register777();
        assertThrows(IllegalStateException.class, () ->
                controller.registerAircraftModel(
                        "777-200er", "boeing", "PASSENGER",
                        145_149, 347_452, 224_982, 145_538,
                        43_100, 0.84, 440, 60.93, 9.0, 0.87,
                        0.017, 0.5, 2, 400, 13_080,
                        List.of(ge90bId)));
    }

    // ── engine validation ─────────────────────────────────────────────────────

    @Test
    void givenNullEngineList_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                controller.registerAircraftModel(
                        "777-200ER", "Boeing", "PASSENGER",
                        145_149, 347_452, 224_982, 145_538,
                        43_100, 0.84, 440, 60.93, 9.0, 0.87,
                        0.017, 0.5, 2, 400, 13_080,
                        null));
    }

    @Test
    void givenEmptyEngineList_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                controller.registerAircraftModel(
                        "777-200ER", "Boeing", "PASSENGER",
                        145_149, 347_452, 224_982, 145_538,
                        43_100, 0.84, 440, 60.93, 9.0, 0.87,
                        0.017, 0.5, 2, 400, 13_080,
                        List.of()));
    }

    @Test
    void givenNonExistentEngine_throwsIllegalState() {
        assertThrows(IllegalStateException.class, () ->
                controller.registerAircraftModel(
                        "777-200ER", "Boeing", "PASSENGER",
                        145_149, 347_452, 224_982, 145_538,
                        43_100, 0.84, 440, 60.93, 9.0, 0.87,
                        0.017, 0.5, 2, 400, 13_080,
                        List.of(EngineModelId.of("Unknown", "Nobody"))));
    }

    @Test
    void givenEnginesOfDifferentTypes_throwsIllegalArgument() {
        final EngineModelId turbopropId = EngineModelId.of("SomeProp", "Maker");
        engineRepo.save(new EngineModel(turbopropId, EngineType.TURBOPROP, Thrust.of(100_000), Thrust.of(60_000), Speed.of(0.5), TSFC.of(1.8e-4), Coefficient.of(1.0), FuelType.JET_A1));

        assertThrows(IllegalArgumentException.class, () ->
                controller.registerAircraftModel(
                        "777-200ER", "Boeing", "PASSENGER",
                        145_149, 347_452, 224_982, 145_538,
                        43_100, 0.84, 440, 60.93, 9.0, 0.87,
                        0.017, 0.5, 2, 400, 13_080,
                        List.of(ge90bId, turbopropId)));
    }

    // ── aircraft type parsing ─────────────────────────────────────────────────

    @Test
    void givenUnknownAircraftType_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                controller.registerAircraftModel(
                        "777-200ER", "Boeing", "HELICOPTER",
                        145_149, 347_452, 224_982, 145_538,
                        43_100, 0.84, 440, 60.93, 9.0, 0.87,
                        0.017, 0.5, 2, 400, 13_080,
                        List.of(ge90bId)));
    }

    @Test
    void givenLowerCaseAircraftType_isAccepted() {
        final AircraftModel model = controller.registerAircraftModel(
                "777-200ER", "Boeing", "passenger",
                145_149, 347_452, 224_982, 145_538,
                43_100, 0.84, 440, 60.93, 9.0, 0.87,
                0.017, 0.5, 2, 400, 13_080,
                List.of(ge90bId));
        assertEquals(AircraftType.PASSENGER, model.aircraftType());
    }

    // ── authorization ─────────────────────────────────────────────────────────

    @Test
    void givenUnauthorizedUser_throwsSecurityException() {
        final RegisterAircraftModelController unauthorized =
                new RegisterAircraftModelController(UNAUTHORIZED, aircraftRepo, engineRepo);
        assertThrows(SecurityException.class, () ->
                unauthorized.registerAircraftModel(
                        "777-200ER", "Boeing", "PASSENGER",
                        145_149, 347_452, 224_982, 145_538,
                        43_100, 0.84, 440, 60.93, 9.0, 0.87,
                        0.017, 0.5, 2, 400, 13_080,
                        List.of(ge90bId)));
    }

    @Test
    void givenUnauthorizedUser_nothingIsPersisted() {
        final RegisterAircraftModelController unauthorized =
                new RegisterAircraftModelController(UNAUTHORIZED, aircraftRepo, engineRepo);
        try {
            unauthorized.registerAircraftModel(
                    "777-200ER", "Boeing", "PASSENGER",
                    145_149, 347_452, 224_982, 145_538,
                    43_100, 0.84, 440, 60.93, 9.0, 0.87,
                    0.017, 0.5, 2, 400, 13_080,
                    List.of(ge90bId));
        } catch (SecurityException ignored) {}

        int count = 0;
        for (AircraftModel ignored : aircraftRepo.findAll()) count++;
        assertEquals(0, count);
    }

    // ── allAircraftModels / allEngineModels ───────────────────────────────────

    @Test
    void allAircraftModels_afterRegistration_returnsOne() {
        register777();
        int count = 0;
        for (AircraftModel ignored : controller.allAircraftModels()) count++;
        assertEquals(1, count);
    }

    @Test
    void allEngineModels_returnsPreLoadedEngines() {
        int count = 0;
        for (EngineModel ignored : controller.allEngineModels()) count++;
        assertEquals(1, count);
    }
}