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
import eapli.aisafe.fleetmanagement.repositories.AircraftRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import eapli.aisafe.generic.domain.*;

class RemoveEngineFromAircraftModelControllerTest {

    // ── stubs ─────────────────────────────────────────────────────────────────

    private static final AuthService AUTHORIZED = new AuthService(null) {
        @Override public void ensureHasAnyRole(Role... roles) { /* allowed */ }
    };

    private static final AuthService UNAUTHORIZED = new AuthService(null) {
        @Override public void ensureHasAnyRole(Role... roles) {
            throw new SecurityException("Access denied.");
        }
    };

    // ── fixtures ──────────────────────────────────────────────────────────────

    private AircraftModelRepository aircraftModelRepo;
    private EngineModelRepository engineRepo;
    private AircraftRepository aircraftRepo;
    private RemoveEngineFromAircraftModelController controller;

    private AircraftModelId boeing777Id;
    private EngineModelId ge90bId;
    private EngineModelId trentId;
    private AircraftModel boeing777;
    private EngineModel ge90b;
    private EngineModel trent;

    @BeforeEach
    void setUp() {
        aircraftModelRepo = mock(AircraftModelRepository.class);
        engineRepo        = mock(EngineModelRepository.class);
        aircraftRepo      = mock(AircraftRepository.class);
        controller        = new RemoveEngineFromAircraftModelController(
                AUTHORIZED, aircraftModelRepo, engineRepo, aircraftRepo);

        ge90bId     = EngineModelId.of("GE 90B", "GE");
        trentId     = EngineModelId.of("RR Trent 970", "RR");
        boeing777Id = AircraftModelId.of("777-200ER", "Boeing");

        ge90b = new EngineModel(ge90bId, EngineType.TURBOFAN, Thrust.of(489_000), Thrust.of(300_000), Speed.of(0.84), TSFC.of(1.6e-4), Coefficient.of(1.0), FuelType.JET_A1);
        trent = new EngineModel(trentId, EngineType.TURBOFAN, Thrust.of(348_310), Thrust.of(180_000), Speed.of(0.85), TSFC.of(1.6e-4), Coefficient.of(0.96), FuelType.JET_A1);

        final Set<EngineModel> twoEngines = new HashSet<>();
        twoEngines.add(ge90b);
        twoEngines.add(trent);

        boeing777 = new AircraftModel(boeing777Id, AircraftType.PASSENGER, Weight.of(145_149), Weight.of(347_452), Weight.of(224_982), Weight.of(145_538), Altitude.of(43_100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 13_080, twoEngines);

        when(aircraftModelRepo.findById(boeing777Id))
                .thenReturn(Optional.of(boeing777));
        when(engineRepo.findById(ge90bId))
                .thenReturn(Optional.of(ge90b));
        when(engineRepo.findById(trentId))
                .thenReturn(Optional.of(trent));
        when(aircraftModelRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(aircraftRepo.existsByAircraftModelId(any())).thenReturn(false);
    }

    // ── happy path ────────────────────────────────────────────────────────────

    @Test
    void givenTwoEngines_noAircraft_engineIsRemoved() {
        final AircraftModel updated =
                controller.removeEngineFromAircraftModel(boeing777Id, trentId);
        assertEquals(1, updated.certifiedEngines().size());
    }

    @Test
    void givenTwoEngines_noAircraft_correctEngineRemains() {
        final AircraftModel updated =
                controller.removeEngineFromAircraftModel(boeing777Id, trentId);
        assertTrue(updated.certifiedEngines().contains(ge90b));
    }

    @Test
    void givenTwoEngines_noAircraft_saveIsCalled() {
        controller.removeEngineFromAircraftModel(boeing777Id, trentId);
        verify(aircraftModelRepo, times(1)).save(boeing777);
    }

    // ── actual aircraft block ─────────────────────────────────────────────────

    @Test
    void givenAircraftUsingModel_throwsIllegalState() {
        when(aircraftRepo.existsByAircraftModelId(boeing777Id)).thenReturn(true);
        assertThrows(IllegalStateException.class,
                () -> controller.removeEngineFromAircraftModel(boeing777Id, trentId));
    }

    @Test
    void givenAircraftUsingModel_saveIsNeverCalled() {
        when(aircraftRepo.existsByAircraftModelId(boeing777Id)).thenReturn(true);
        try {
            controller.removeEngineFromAircraftModel(boeing777Id, trentId);
        } catch (IllegalStateException ignored) {}
        verify(aircraftModelRepo, never()).save(any());
    }

    // ── domain invariants ─────────────────────────────────────────────────────

    @Test
    void givenLastEngine_removalThrowsIllegalState() {
        controller.removeEngineFromAircraftModel(boeing777Id, trentId);
        assertThrows(IllegalStateException.class,
                () -> controller.removeEngineFromAircraftModel(boeing777Id, ge90bId));
    }

    @Test
    void givenEngineNotCertified_throwsIllegalState() {
        final EngineModelId unknownId = EngineModelId.of("Unknown", "Nobody");
        final EngineModel unknown = new EngineModel(unknownId, EngineType.TURBOFAN, Thrust.of(200_000), Thrust.of(100_000), Speed.of(0.80), TSFC.of(1.6e-4), Coefficient.of(1.0), FuelType.JET_A1);
        when(engineRepo.findById(unknownId)).thenReturn(Optional.of(unknown));
        assertThrows(IllegalStateException.class,
                () -> controller.removeEngineFromAircraftModel(boeing777Id, unknownId));
    }

    // ── not found ─────────────────────────────────────────────────────────────

    @Test
    void givenNonExistentAircraftModel_throwsIllegalState() {
        final AircraftModelId unknown = AircraftModelId.of("Unknown", "Nobody");
        when(aircraftModelRepo.findById(unknown)).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class,
                () -> controller.removeEngineFromAircraftModel(unknown, trentId));
    }

    @Test
    void givenNonExistentEngineModel_throwsIllegalState() {
        final EngineModelId unknown = EngineModelId.of("Unknown", "Nobody");
        when(engineRepo.findById(unknown)).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class,
                () -> controller.removeEngineFromAircraftModel(boeing777Id, unknown));
    }

    // ── authorization ─────────────────────────────────────────────────────────

    @Test
    void givenUnauthorizedUser_throwsSecurityException() {
        final RemoveEngineFromAircraftModelController unauthorized =
                new RemoveEngineFromAircraftModelController(
                        UNAUTHORIZED, aircraftModelRepo, engineRepo, aircraftRepo);
        assertThrows(SecurityException.class,
                () -> unauthorized.removeEngineFromAircraftModel(boeing777Id, trentId));
    }

    @Test
    void givenUnauthorizedUser_saveIsNeverCalled() {
        final RemoveEngineFromAircraftModelController unauthorized =
                new RemoveEngineFromAircraftModelController(
                        UNAUTHORIZED, aircraftModelRepo, engineRepo, aircraftRepo);
        try {
            unauthorized.removeEngineFromAircraftModel(boeing777Id, trentId);
        } catch (SecurityException ignored) {}
        verify(aircraftModelRepo, never()).save(any());
    }
}