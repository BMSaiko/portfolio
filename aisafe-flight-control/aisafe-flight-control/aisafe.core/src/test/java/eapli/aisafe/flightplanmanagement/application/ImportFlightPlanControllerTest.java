package eapli.aisafe.flightplanmanagement.application;

import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftType;
import eapli.aisafe.aircraftmodelmanagement.repositories.AircraftModelRepository;
import eapli.aisafe.enginemanagement.domain.EngineModel;
import eapli.aisafe.enginemanagement.domain.EngineModelId;
import eapli.aisafe.enginemanagement.domain.EngineType;
import eapli.aisafe.enginemanagement.domain.FuelType;
import eapli.aisafe.flightplanmanagement.domain.*;
import eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository;
import eapli.aisafe.generic.domain.*;
import eapli.aisafe.usermanagement.application.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ImportFlightPlanController}.
 *
 * Tests cover: happy path import, negative fuel, unknown aircraft model,
 * draft status, leg count, and persistence verification.
 */
class ImportFlightPlanControllerTest {

    private AuthService authService;
    private FlightPlanRepository flightPlanRepository;
    private AircraftModelRepository aircraftModelRepository;
    private ImportFlightPlanController controller;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        flightPlanRepository = mock(FlightPlanRepository.class);
        aircraftModelRepository = mock(AircraftModelRepository.class);
        controller = new ImportFlightPlanController(
                authService, flightPlanRepository, aircraftModelRepository);
    }

    private EngineModel dummyEngine() {
        return new EngineModel(
                EngineModelId.of("CFM56-7B", "CFM"), EngineType.TURBOFAN,
                Thrust.of(121_000), Thrust.of(26_000), Speed.of(0.78),
                TSFC.of(1.6e-4), Coefficient.of(1.0), FuelType.JET_A1);
    }

    private AircraftModel createDummyAircraftModel() {
        final Set<EngineModel> engines = new HashSet<>();
        engines.add(dummyEngine());
        return new AircraftModel(
                AircraftModelId.of("BOEING-737", "Boeing"), AircraftType.PASSENGER,
                Weight.of(41_413), Weight.of(79_016), Weight.of(62_732), Weight.of(26_020),
                Altitude.of(41_000), Speed.of(0.785), Area.of(338),
                35.79, 9.45, Coefficient.of(0.85), Coefficient.of(0.021),
                Coefficient.of(0.5), 2, 189, 5_600, engines);
    }

    // ── Happy path ────────────────────────────────────────────────────────────

    @Test
    void import_validDslFile_createsFlightPlan() {
        final AircraftModel aircraft = createDummyAircraftModel();
        when(aircraftModelRepository.findById(any(AircraftModelId.class)))
                .thenReturn(Optional.of(aircraft));
        when(flightPlanRepository.save(any(FlightPlan.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final Path path = Paths.get("src/test/resources/valid_flight.dsl");
        final FlightPlan result =
                controller.importFlightPlan(path, "BOEING-737", "Boeing", 2340.5);

        assertNotNull(result);
        assertEquals(FlightPlanId.of("FP_TEST_001"), result.identity());
        assertEquals("LIS", result.departureAirport());
        assertEquals("FRA", result.arrivalAirport());
        assertEquals(1, result.legs().size());
        assertEquals(3, result.legs().get(0).segments().size());
        assertEquals(15000.0, result.estimatedFuel(), 0.001);
        assertNotNull(result.departureDateTime());
        assertEquals(FlightPlanStatus.DRAFT, result.status());
        verify(flightPlanRepository).save(any(FlightPlan.class));
    }

    // ── Validation failures ───────────────────────────────────────────────────

    @Test
    void import_negativeFuel_throwsIllegalArgumentException() {
        final AircraftModel aircraft = createDummyAircraftModel();
        when(aircraftModelRepository.findById(any(AircraftModelId.class)))
                .thenReturn(Optional.of(aircraft));
        when(flightPlanRepository.save(any(FlightPlan.class)))
                .thenThrow(new IllegalArgumentException("Fuel cannot be negative."));

        final Path path = Paths.get("src/test/resources/valid_flight.dsl");

        assertThrows(IllegalArgumentException.class, () ->
                controller.importFlightPlan(path, "BOEING-737", "Boeing", -10.0));
    }

    @Test
    void import_unknownAircraftModel_throwsIllegalStateException() {
        when(aircraftModelRepository.findById(any(AircraftModelId.class)))
                .thenReturn(Optional.empty());

        final Path path = Paths.get("src/test/resources/valid_flight.dsl");

        assertThrows(IllegalStateException.class, () ->
                controller.importFlightPlan(path, "UNKNOWN", "Unknown", 2340.5));
    }

    // ── Result state ──────────────────────────────────────────────────────────

    @Test
    void import_resultHasDraftStatus() {
        final AircraftModel aircraft = createDummyAircraftModel();
        when(aircraftModelRepository.findById(any(AircraftModelId.class)))
                .thenReturn(Optional.of(aircraft));
        when(flightPlanRepository.save(any(FlightPlan.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final Path path = Paths.get("src/test/resources/valid_flight.dsl");
        final FlightPlan result =
                controller.importFlightPlan(path, "BOEING-737", "Boeing", 2340.5);

        assertEquals(FlightPlanStatus.DRAFT, result.status());
    }

    @Test
    void import_resultHasAtLeastOneLeg() {
        final AircraftModel aircraft = createDummyAircraftModel();
        when(aircraftModelRepository.findById(any(AircraftModelId.class)))
                .thenReturn(Optional.of(aircraft));
        when(flightPlanRepository.save(any(FlightPlan.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final Path path = Paths.get("src/test/resources/valid_flight.dsl");
        final FlightPlan result =
                controller.importFlightPlan(path, "BOEING-737", "Boeing", 2340.5);

        assertFalse(result.legs().isEmpty());
    }

    @Test
    void import_callsRepositorySave() {
        final AircraftModel aircraft = createDummyAircraftModel();
        when(aircraftModelRepository.findById(any(AircraftModelId.class)))
                .thenReturn(Optional.of(aircraft));
        when(flightPlanRepository.save(any(FlightPlan.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final Path path = Paths.get("src/test/resources/valid_flight.dsl");
        controller.importFlightPlan(path, "BOEING-737", "Boeing", 2340.5);

        verify(flightPlanRepository, times(1)).save(any(FlightPlan.class));
    }
}
