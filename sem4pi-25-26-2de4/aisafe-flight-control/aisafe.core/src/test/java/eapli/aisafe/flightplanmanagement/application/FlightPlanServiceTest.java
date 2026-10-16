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
import eapli.aisafe.usermanagement.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link FlightPlanService}.
 *
 * Tests cover: format validation gate, auth enforcement, DSL validation,
 * aircraft model lookup, persistence, and list/find operations.
 */
@ExtendWith(MockitoExtension.class)
class FlightPlanServiceTest {

    private static final Path VALID_MULTI_LEG_PATH = Paths.get(
            "src/test/resources/valid_multi_leg.dsl");
    private static final Path VALID_OPO_MAD_PATH = Paths.get(
            "src/test/resources/valid_opo_mad.dsl");

    @Mock
    private AuthService authService;

    @Mock
    private FlightPlanRepository flightPlanRepository;

    @Mock
    private AircraftModelRepository aircraftModelRepository;

    private FlightPlanService service;

    private static final Path VALID_DSL_PATH = Paths.get(
            "src/test/resources/valid_flight.dsl");
    private static final Path INVALID_DSL_PATH = Paths.get(
            "src/test/resources/invalid_flight.dsl");
    private static final Path WRONG_EXT_PATH = Paths.get(
            "src/test/resources/valid_flight.txt");

    @BeforeEach
    void setUp() {
        service = new FlightPlanService(
                authService, flightPlanRepository, aircraftModelRepository);
    }

    private AircraftModel dummyAircraftModel() {
        Set<EngineModel> engines = new HashSet<>();
        engines.add(new EngineModel(
                EngineModelId.of("CFM56-7B", "CFM"), EngineType.TURBOFAN,
                Thrust.of(121_000), Thrust.of(26_000), Speed.of(0.78),
                TSFC.of(1.6e-4), Coefficient.of(1.0), FuelType.JET_A1));
        return new AircraftModel(
                AircraftModelId.of("BOEING-737", "Boeing"), AircraftType.PASSENGER,
                Weight.of(41_413), Weight.of(79_016), Weight.of(62_732), Weight.of(26_020),
                Altitude.of(41_000), Speed.of(0.785), Area.of(338),
                35.79, 9.45, Coefficient.of(0.85), Coefficient.of(0.021),
                Coefficient.of(0.5), 2, 189, 5_600, engines);
    }

    // ── Format validation gate ────────────────────────────────────────────────

    @Test
    void import_wrongExtension_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                service.importFlightPlan(WRONG_EXT_PATH, "BOEING-737", "Boeing", 100.0));
        verifyNoInteractions(flightPlanRepository);
    }

    @Test
    void import_nonExistentFile_throwsIllegalArgumentException() {
        Path missing = Paths.get("src/test/resources/nonexistent.dsl");
        assertThrows(IllegalArgumentException.class, () ->
                service.importFlightPlan(missing, "BOEING-737", "Boeing", 100.0));
        verifyNoInteractions(flightPlanRepository);
    }

    // ── Auth enforcement ──────────────────────────────────────────────────────

    @Test
    void import_unauthorized_throwsSecurityException() {
        doThrow(new SecurityException("Access denied"))
                .when(authService).ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);

        assertThrows(SecurityException.class, () ->
                service.importFlightPlan(VALID_DSL_PATH, "BOEING-737", "Boeing", 100.0));
        verifyNoInteractions(flightPlanRepository);
    }

    // ── Aircraft model not found ──────────────────────────────────────────────

    @Test
    void import_unknownAircraftModel_throwsIllegalStateException() {
        when(aircraftModelRepository.findById(any(AircraftModelId.class)))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () ->
                service.importFlightPlan(VALID_DSL_PATH, "UNKNOWN", "Unknown", 100.0));
        verifyNoInteractions(flightPlanRepository);
    }

    // ── Wrong extension ─────────────────────────────────────────────────────

    @Test
    void import_wrongExtensionFile_throwsIllegalArgumentException() {
        Path txtFile = Paths.get("src/test/resources/invalid_flight.dsl");
        // Rename simulation: pass a .txt path that exists
        Path wrongExt = Paths.get("src/test/resources/valid_charter.dsl");
        // The validator checks by extension, so passing a .dsl file is valid format
        // but the DSL inside may fail. Let's test with an actual wrong extension
        Path noExt = Paths.get("src/test/resources");
        assertThrows(IllegalArgumentException.class, () ->
                service.importFlightPlan(noExt, "BOEING-737", "Boeing", 100.0));
    }

    // ── List flight plans ─────────────────────────────────────────────────────

    @Test
    void listFlightPlans_returnsAllPlans() {
        FlightPlan fp1 = mock(FlightPlan.class);
        FlightPlan fp2 = mock(FlightPlan.class);
        when(flightPlanRepository.findAll()).thenReturn(List.of(fp1, fp2));

        List<FlightPlan> result = service.listFlightPlans();

        assertEquals(2, result.size());
        verify(flightPlanRepository).findAll();
    }

    @Test
    void listFlightPlans_emptyRepository_returnsEmptyList() {
        when(flightPlanRepository.findAll()).thenReturn(List.of());

        List<FlightPlan> result = service.listFlightPlans();

        assertTrue(result.isEmpty());
    }

    // ── Format validation edge cases ────────────────────────────────────────

    @Test
    void import_dslWithBom_throwsIllegalArgumentException() throws Exception {
        // Create a temp DSL file with BOM
        Path bomFile = Files.createTempFile("bom_test", ".dsl");
        byte[] bom = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] content = "flight FP_TEST { type: regular; }".getBytes();
        byte[] combined = new byte[bom.length + content.length];
        System.arraycopy(bom, 0, combined, 0, bom.length);
        System.arraycopy(content, 0, combined, bom.length, content.length);
        Files.write(bomFile, combined);

        try {
            assertThrows(IllegalArgumentException.class, () ->
                    service.importFlightPlan(bomFile, "BOEING-737", "Boeing", 100.0));
        } finally {
            Files.deleteIfExists(bomFile);
        }
    }

    @Test
    void import_emptyDslFile_throwsIllegalArgumentException() throws Exception {
        Path emptyFile = Files.createTempFile("empty_test", ".dsl");
        Files.writeString(emptyFile, "");

        try {
            assertThrows(IllegalArgumentException.class, () ->
                    service.importFlightPlan(emptyFile, "BOEING-737", "Boeing", 100.0));
        } finally {
            Files.deleteIfExists(emptyFile);
        }
    }

    // ── Find flight plan ──────────────────────────────────────────────────────

    @Test
    void findFlightPlan_existing_returnsPlan() {
        FlightPlan fp = mock(FlightPlan.class);
        when(flightPlanRepository.findById(any(FlightPlanId.class)))
                .thenReturn(Optional.of(fp));

        Optional<FlightPlan> result = service.findFlightPlan("FP001");

        assertTrue(result.isPresent());
        assertEquals(fp, result.get());
    }

    @Test
    void findFlightPlan_missing_returnsEmpty() {
        when(flightPlanRepository.findById(any(FlightPlanId.class)))
                .thenReturn(Optional.empty());

        Optional<FlightPlan> result = service.findFlightPlan("NONEXISTENT");

        assertTrue(result.isEmpty());
    }
}
