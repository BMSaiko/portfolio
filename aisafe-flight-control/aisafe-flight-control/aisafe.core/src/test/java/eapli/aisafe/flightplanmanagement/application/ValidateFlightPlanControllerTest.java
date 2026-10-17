package eapli.aisafe.flightplanmanagement.application;

import eapli.aisafe.enginemanagement.domain.*;
import eapli.aisafe.aircraftmodelmanagement.domain.*;
import eapli.aisafe.flightplanmanagement.domain.*;
import eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository;
import eapli.aisafe.generic.domain.*;
import eapli.aisafe.pilotmanagement.domain.Pilot;
import eapli.aisafe.routemanagement.domain.FlightRoute;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.isep.sem4pi.lprog.application.US085Result;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ValidateFlightPlanControllerTest {

    private InMemoryFlightPlanRepository flightPlanRepository;
    private ValidateFlightPlanController controller;

    // Usar 'flight123' em minúsculas evita colisão com AIRPORT_CODE no ANTLR
    private static final String VALID_DSL =
            "flight flight123 {\n" +
                    "  type: regular;\n" +
                    "  leg {\n" +
                    "    departure: LPPT at 2026-06-01 08:00;\n" +
                    "    arrival: LPPR at 2026-06-01 08:45;\n" +
                    "    route: LPPT -> LPPR;\n" +
                    "    segment {\n" +
                    "      mode: cruise;\n" +
                    "      start: (38.78, -9.13, 8000 m);\n" +
                    "      end: (41.23, -8.68, 8000 m);\n" +
                    "    }\n" +
                    "    fuel: 15000 kg;\n" +
                    "  }\n" +
                    "}";

    private static final String INVALID_DSL =
            "flight { this is not valid DSL !!!";

    @BeforeEach
    void setUp() {
        flightPlanRepository = new InMemoryFlightPlanRepository();
        controller = new ValidateFlightPlanController(flightPlanRepository);
    }

    // ── NULL / BLANK GUARDS ───────────────────────────────────────────────────

    @Test
    void validate_nullDsl_returnsIoError() {
        US085Result result = controller.validate(null, null);
        assertEquals(US085Result.State.IO_ERROR, result.getState());
        assertEquals(0, flightPlanRepository.saveCallCount());
    }

    @Test
    void validate_blankDsl_returnsIoError() {
        US085Result result = controller.validate("   ", null);
        assertEquals(US085Result.State.IO_ERROR, result.getState());
        assertEquals(0, flightPlanRepository.saveCallCount());
    }

    @Test
    void validateById_blankId_returnsIoError() {
        US085Result result = controller.validateById("   ");
        assertEquals(US085Result.State.IO_ERROR, result.getState());
        assertEquals(0, flightPlanRepository.saveCallCount());
    }

    @Test
    void validateById_nullId_returnsIoError() {
        US085Result result = controller.validateById(null);
        assertEquals(US085Result.State.IO_ERROR, result.getState());
        assertEquals(0, flightPlanRepository.saveCallCount());
    }

    // ── DSL FAILURE ──────────────────────────────────────────────────────────

    @Test
    void validate_invalidDsl_noId_returnsDslFailure_repositoryNotSaved() {
        US085Result result = controller.validate(INVALID_DSL, null);

        assertEquals(US085Result.State.DSL_FAILURE, result.getState());
        assertFalse(result.getDslErrors().isEmpty());
        assertEquals(0, flightPlanRepository.saveCallCount());
    }

    @Test
    void validateOnly_invalidDsl_returnsDslFailure() {
        US085Result result = controller.validateOnly(INVALID_DSL);
        assertEquals(US085Result.State.DSL_FAILURE, result.getState());
        assertEquals(0, flightPlanRepository.saveCallCount());
    }

    // ── NO FLIGHT PLAN ID ────────────────────────────────────────────────────

    @Test
    void validate_noFlightPlanId_repositoryNeverSaved() {
        controller.validate(INVALID_DSL, null);
        assertEquals(0, flightPlanRepository.saveCallCount());
    }

    @Test
    void validate_blankFlightPlanId_repositoryNeverSaved() {
        controller.validate(INVALID_DSL, "   ");
        assertEquals(0, flightPlanRepository.saveCallCount());
    }

    // ── FLIGHT PLAN NOT FOUND ─────────────────────────────────────────────────

    @Test
    void validateById_planNotFound_returnsIoError() {
        US085Result result = controller.validateById("NONEXISTENT");
        assertEquals(US085Result.State.IO_ERROR, result.getState());
        assertEquals(0, flightPlanRepository.saveCallCount());
    }

    // ── PLAN WITHOUT DSL STORED ───────────────────────────────────────────────

    @Test
    void validateById_planWithoutDsl_returnsIoError() {
        FlightPlan plan = buildMinimalFlightPlan("PLAN-001");
        flightPlanRepository.save(plan);
        flightPlanRepository.resetSaveCount();

        US085Result result = controller.validateById("PLAN-001");

        assertEquals(US085Result.State.IO_ERROR, result.getState());
        assertEquals(0, flightPlanRepository.saveCallCount());
    }

    // ── PERSISTENCE — STATUS TRANSITIONS ─────────────────────────────────────

    @Test
    void validate_invalidDsl_withExistingPlan_dslFailure_planNotSaved() {
        FlightPlan plan = buildMinimalFlightPlan("PLAN-002");
        flightPlanRepository.save(plan);
        flightPlanRepository.resetSaveCount();

        US085Result result = controller.validate(INVALID_DSL, "PLAN-002");

        assertEquals(US085Result.State.DSL_FAILURE, result.getState());
        assertEquals(FlightPlanStatus.DRAFT, plan.status());
        assertEquals(0, flightPlanRepository.saveCallCount());
    }

    @Test
    void validateById_planWithValidDsl_statusBecomesValidated() {
        FlightPlan plan = buildMinimalFlightPlan("PLAN-003");
        plan.storeDsl(VALID_DSL);
        flightPlanRepository.save(plan);
        flightPlanRepository.resetSaveCount();

        US085Result result = controller.validateById("PLAN-003");

        assertNotEquals(US085Result.State.IO_ERROR, result.getState());
        assertNotEquals(US085Result.State.DSL_FAILURE, result.getState());
        assertNotEquals(FlightPlanStatus.DRAFT, plan.status());
        assertEquals(1, flightPlanRepository.saveCallCount());
    }

    // ── HELPERS EM MEMÓRIA ───────────────────────────────────────────────────

    private FlightPlan buildMinimalFlightPlan(final String idValue) {
        return new FlightPlan(
                FlightPlanId.of(idValue),
                "flight123",
                "LPPT",
                "LPPR",
                dummyMinimalAircraftModel(),
                List.of(dummyLeg()),
                0.0,
                15000.0
        );
    }

    private FlightLeg dummyLeg() {
        return FlightLeg.of(
                "LPPT",
                "LPPR",
                15000.0,
                List.of(dummySegment())
        );
    }

    private FlightSegment dummySegment() {
        return FlightSegment.of(
                "cruise",
                Waypoint.of(38.78, -9.13, 8000),
                Waypoint.of(41.23, -8.68, 8000)
        );
    }

    private AircraftModel dummyMinimalAircraftModel() {
        AircraftModelId modelId = AircraftModelId.of("B738", "Boeing");

        java.util.Set<EngineModel> engines = new java.util.HashSet<>();

        // Classe anónima evita que tenhamos de instanciar os 9 VOs obrigatórios do construtor do EngineModel
        engines.add(new EngineModel() {
            @Override
            public EngineModelId identity() {
                return EngineModelId.of("CFM56", "CFM");
            }
        });

        return new AircraftModel(
                modelId,
                AircraftType.PASSENGER,
                Weight.of(40000),
                Weight.of(70000),
                Weight.of(60000),
                Weight.of(20000),
                Altitude.of(41000),
                Speed.of(0.78),
                Area.of(125),
                35.8,
                9.4,
                Coefficient.of(0.8),
                Coefficient.of(0.02),
                Coefficient.of(0.5),
                2,
                189,
                5000,
                engines // Set preenchido com uma instância válida!
        );
    }

    // ── IN-MEMORY REPOSITORY ─────────────────────────────────────────────────

    private static class InMemoryFlightPlanRepository implements FlightPlanRepository {

        private final Map<FlightPlanId, FlightPlan> store = new HashMap<>();
        private int saveCount = 0;

        public int saveCallCount() { return saveCount; }
        public void resetSaveCount() { saveCount = 0; }

        @Override
        public Optional<FlightPlan> findById(final FlightPlanId id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public Iterable<FlightPlan> findAll() {
            return new ArrayList<>(store.values());
        }

        @Override
        public FlightPlan save(final FlightPlan plan) {
            store.put(plan.identity(), plan);
            saveCount++;
            return plan;
        }

        @Override
        public void deleteById(final FlightPlanId id) {
            store.remove(id);
        }

        @Override
        public long count() { return store.size(); }

        @Override
        public boolean existsPlannedForRouteFrom(final FlightRoute route, final LocalDate date) {
            return false;
        }

        @Override
        public List<FlightPlan> findByPilot(final Pilot pilot) { return List.of(); }

        @Override
        public List<FlightPlan> findByRoute(final FlightRoute route) { return List.of(); }

        @Override
        public long countByPilot(final Pilot pilot) { return 0; }
    }
}