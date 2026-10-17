package eapli.aisafe.enginemanagement.application;

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

class RegisterEngineModelControllerTest {

    // ── stubs ─────────────────────────────────────────────────────────────────

    private static final AuthService AUTHORIZED = new AuthService(null) {
        @Override public void ensureHasAnyRole(Role... roles) { /* allowed */ }
    };

    private static final AuthService UNAUTHORIZED = new AuthService(null) {
        @Override public void ensureHasAnyRole(Role... roles) {
            throw new SecurityException("Access denied.");
        }
    };

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

    private InMemoryEngineModelRepository repository;
    private RegisterEngineModelController controller;

    @BeforeEach
    void setUp() {
        repository = new InMemoryEngineModelRepository();
        controller = new RegisterEngineModelController(AUTHORIZED, repository);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private EngineModel registerGE90B() {
        return controller.registerEngineModel(
                "GE 90B", "GE", "TURBOFAN",
                489_000, 300_000, 0.84, 1.6e-4, 1.0,
                FuelType.JET_A1);
    }

    // ── happy path ────────────────────────────────────────────────────────────

    @Test
    void givenValidData_engineModelIsRegistered() {
        final EngineModel model = registerGE90B();
        assertNotNull(model);
        assertEquals("GE 90B", model.name());
        assertEquals("GE", model.makerId());
        assertEquals(EngineType.TURBOFAN, model.engineType());
        assertEquals(FuelType.JET_A1, model.fuelType());
    }

    @Test
    void givenValidData_engineModelIsPersisted() {
        registerGE90B();
        assertTrue(repository.findById(EngineModelId.of("GE 90B", "GE")).isPresent());
    }

    @Test
    void givenTwoDifferentModels_bothAreRegistered() {
        registerGE90B();
        controller.registerEngineModel(
                "RR Trent 970", "RR", "TURBOFAN",
                348_310, 180_000, 0.85, 1.6e-4, 0.96,
                FuelType.JET_A1);
        int count = 0;
        for (EngineModel ignored : repository.findAll()) count++;
        assertEquals(2, count);
    }

    @Test
    void givenSameNameDifferentMaker_bothAreRegistered() {
        assertDoesNotThrow(() -> {
            controller.registerEngineModel("Trent 970", "RR", "TURBOFAN",
                    348_310, 180_000, 0.85, 1.6e-4, 0.96, FuelType.JET_A1);
            controller.registerEngineModel("Trent 970", "GE", "TURBOFAN",
                    400_000, 250_000, 0.84, 1.6e-4, 1.0, FuelType.JET_A1);
        });
    }

    // ── uniqueness ────────────────────────────────────────────────────────────

    @Test
    void givenDuplicateNameAndMaker_throwsIllegalState() {
        registerGE90B();
        assertThrows(IllegalStateException.class, () -> registerGE90B());
    }

    @Test
    void givenDuplicateNameCaseInsensitive_throwsIllegalState() {
        registerGE90B();
        assertThrows(IllegalStateException.class, () ->
                controller.registerEngineModel("ge 90b", "GE", "TURBOFAN",
                        500_000, 310_000, 0.84, 1.6e-4, 1.0, FuelType.JET_A1));
    }

    // ── engine type parsing ───────────────────────────────────────────────────

    @Test
    void givenLowerCaseEngineType_isAccepted() {
        final EngineModel model = controller.registerEngineModel(
                "GE 90B", "GE", "turbofan",
                489_000, 300_000, 0.84, 1.6e-4, 1.0, FuelType.JET_A1);
        assertEquals(EngineType.TURBOFAN, model.engineType());
    }

    @Test
    void givenUnknownEngineType_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                controller.registerEngineModel("GE 90B", "GE", "ROCKET",
                        489_000, 300_000, 0.84, 1.6e-4, 1.0, FuelType.JET_A1));
    }

    // ── fuel type ─────────────────────────────────────────────────────────────

    @Test
    void givenAllFuelTypes_engineModelIsRegistered() {
        int counter = 1;
        for (final FuelType fuel : FuelType.values()) {
            final int i = counter++;
            assertDoesNotThrow(() ->
                    controller.registerEngineModel(
                            "Engine" + i, "Maker", "TURBOFAN",
                            489_000, 300_000, 0.84, 1.6e-4, 1.0, fuel));
        }
    }

    @Test
    void givenNullFuelType_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                controller.registerEngineModel("GE 90B", "GE", "TURBOFAN",
                        489_000, 300_000, 0.84, 1.6e-4, 1.0, null));
    }

    // ── physical parameter validation ─────────────────────────────────────────

    @Test
    void givenCruiseThrustExceedsStandstill_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                controller.registerEngineModel("GE 90B", "GE", "TURBOFAN",
                        300_000, 400_000, 0.84, 1.6e-4, 1.0, FuelType.JET_A1));
    }

    @Test
    void givenNegativeTsfc_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                controller.registerEngineModel("GE 90B", "GE", "TURBOFAN",
                        489_000, 300_000, 0.84, -1e-4, 1.0, FuelType.JET_A1));
    }

    @Test
    void givenNegativeLapseFactor_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                controller.registerEngineModel("GE 90B", "GE", "TURBOFAN",
                        489_000, 300_000, 0.84, 1.6e-4, -1.0, FuelType.JET_A1));
    }

    // ── authorization ─────────────────────────────────────────────────────────

    @Test
    void givenUnauthorizedUser_throwsSecurityException() {
        final RegisterEngineModelController unauthorized =
                new RegisterEngineModelController(UNAUTHORIZED, repository);
        assertThrows(SecurityException.class, () ->
                unauthorized.registerEngineModel("GE 90B", "GE", "TURBOFAN",
                        489_000, 300_000, 0.84, 1.6e-4, 1.0, FuelType.JET_A1));
    }

    @Test
    void givenUnauthorizedUser_nothingIsPersisted() {
        final RegisterEngineModelController unauthorized =
                new RegisterEngineModelController(UNAUTHORIZED, repository);
        try {
            unauthorized.registerEngineModel("GE 90B", "GE", "TURBOFAN",
                    489_000, 300_000, 0.84, 1.6e-4, 1.0, FuelType.JET_A1);
        } catch (SecurityException ignored) {}
        int count = 0;
        for (EngineModel ignored : repository.findAll()) count++;
        assertEquals(0, count);
    }

    // ── allEngineModels / allFuelTypes ────────────────────────────────────────

    @Test
    void allEngineModels_afterRegistration_returnsOne() {
        registerGE90B();
        int count = 0;
        for (EngineModel ignored : controller.allEngineModels()) count++;
        assertEquals(1, count);
    }

    @Test
    void allFuelTypes_returnsAllDefinedFuels() {
        assertEquals(FuelType.values().length, controller.allFuelTypes().length);
    }
}