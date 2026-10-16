package eapli.aisafe.flightplanmanagement.domain;

import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftType;
import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.airportmanagement.domain.Airport;
import eapli.aisafe.airportmanagement.domain.Coordinates;
import eapli.aisafe.enginemanagement.domain.EngineModel;
import eapli.aisafe.enginemanagement.domain.EngineModelId;
import eapli.aisafe.enginemanagement.domain.EngineType;
import eapli.aisafe.enginemanagement.domain.FuelType;
import eapli.aisafe.fleetmanagement.domain.Aircraft;
import eapli.aisafe.fleetmanagement.domain.CabinConfiguration;
import eapli.aisafe.fleetmanagement.domain.OperationalStatus;
import eapli.aisafe.generic.domain.*;
import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.pilotmanagement.domain.Pilot;
import eapli.aisafe.routemanagement.domain.FlightRoute;
import eapli.aisafe.routemanagement.domain.RouteDesignator;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.User;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FlightPlanTest {

    // =========================================================================
    // Helpers
    // =========================================================================

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

    private AirTransportCompany dummyCompany() {
        return new AirTransportCompany("TAP Air Portugal", "TP", "TAP");
    }

    private Airport dummyAirport(final String iata) {
        return mock_Airport(iata);
    }

    /** Cria um Airport válido utilizando mocks seguros para evitar quebras de validação de nulos */
    private Airport mock_Airport(final String iataCode) {
        AirControlArea mockedArea = mock(AirControlArea.class);
        Coordinates mockedCoordinates = mock(Coordinates.class);

        return new Airport(
                IataCode.of(iataCode),
                IcaoCode.of("L" + iataCode),
                "Airport " + iataCode,
                "City " + iataCode,
                "Country " + iataCode,
                mockedCoordinates,
                mockedArea
        );
    }

    private FlightRoute dummyRoute(final AirTransportCompany company) {
        final RouteDesignator designator = new RouteDesignator("TP123", "TP");
        return new FlightRoute(designator, company, mock_Airport("LIS"), mock_Airport("OPO"));
    }

    private Aircraft dummyAircraft(final AirTransportCompany company) {
        // Corrigido para os 3 parâmetros obrigatórios de CabinConfiguration
        return new Aircraft(
                "CS-TKX", createDummyAircraftModel(), company,
                "Portugal", new CabinConfiguration(12, 24, 150),
                OperationalStatus.ACTIVE, 2018);
    }

    private Pilot dummyPilot(final AirTransportCompany company) {
        final User user = new User(
                new Email("pilot@tap.pt"), "Pilot123",
                UserName.of("Test Pilot"), PhoneNumber.of("+351910000000"),
                Set.of(eapli.aisafe.usermanagement.domain.Role.PILOT));
        return new Pilot(user, new Email("pilot@tap.pt"), "Test Pilot",
                "Captain", company, Set.of(createDummyAircraftModel()));
    }

    private List<FlightLeg> createDummyLegs() {
        final Waypoint wp1 = Waypoint.of(40.6413, -7.3772, 10000);
        final Waypoint wp2 = Waypoint.of(38.7223, -9.1393, 10000);
        final FlightSegment seg = FlightSegment.of("cruise", wp1, wp2);
        final List<FlightSegment> segments = new ArrayList<>();
        segments.add(seg);
        final FlightLeg leg = FlightLeg.of("LIS", "OPO", 5000.0, segments);
        final List<FlightLeg> legs = new ArrayList<>();
        legs.add(leg);
        return legs;
    }

    private LocalDateTime futureDateTime() {
        return LocalDateTime.now().plusDays(7);
    }

    // =========================================================================
    // US081 constructor (DSL import) — existing tests, preserved
    // =========================================================================

    @Test(expected = IllegalArgumentException.class)
    public void testFlightPlanNullId() {
        new FlightPlan(null, "LIS-OPO", "LIS", "OPO",
                createDummyAircraftModel(), createDummyLegs(), 278.0, 5000.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFlightPlanEmptyRoute() {
        new FlightPlan(FlightPlanId.of("FP-001"), "", "LIS", "OPO",
                createDummyAircraftModel(), createDummyLegs(), 278.0, 5000.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFlightPlanNullRoute() {
        new FlightPlan(FlightPlanId.of("FP-001"), null, "LIS", "OPO",
                createDummyAircraftModel(), createDummyLegs(), 278.0, 5000.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFlightPlanEmptyDeparture() {
        new FlightPlan(FlightPlanId.of("FP-001"), "LIS-OPO", "", "OPO",
                createDummyAircraftModel(), createDummyLegs(), 278.0, 5000.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFlightPlanEmptyArrival() {
        new FlightPlan(FlightPlanId.of("FP-001"), "LIS-OPO", "LIS", "",
                createDummyAircraftModel(), createDummyLegs(), 278.0, 5000.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFlightPlanNullAircraftModel() {
        new FlightPlan(FlightPlanId.of("FP-001"), "LIS-OPO", "LIS", "OPO",
                null, createDummyLegs(), 278.0, 5000.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFlightPlanEmptyLegs() {
        new FlightPlan(FlightPlanId.of("FP-001"), "LIS-OPO", "LIS", "OPO",
                createDummyAircraftModel(), new ArrayList<>(), 278.0, 5000.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFlightPlanNullLegs() {
        new FlightPlan(FlightPlanId.of("FP-001"), "LIS-OPO", "LIS", "OPO",
                createDummyAircraftModel(), null, 278.0, 5000.0);
    }

    @Test
    public void testFlightPlanCreation() {
        final FlightPlan fp = new FlightPlan(
                FlightPlanId.of("FP-001"), "LIS-OPO", "LIS", "OPO",
                createDummyAircraftModel(), createDummyLegs(), 278.0, 5000.0);
        assertEquals(FlightPlanId.of("FP-001"), fp.identity());

        // Corrigido: Validamos diretamente os aeroportos uma vez que o método .route() não existe no domínio.
        assertEquals("LIS", fp.departureAirport());
        assertEquals("OPO", fp.arrivalAirport());
        assertEquals(FlightPlanStatus.DRAFT, fp.status());
        assertEquals(278.0, fp.totalDistance(), 0.0001);
        assertEquals(5000.0, fp.estimatedFuel(), 0.0001);
    }

    @Test
    public void testFlightPlanValidate() {
        final FlightPlan fp = new FlightPlan(
                FlightPlanId.of("FP-001"), "LIS-OPO", "LIS", "OPO",
                createDummyAircraftModel(), createDummyLegs(), 278.0, 5000.0);
        fp.validate();
        assertEquals(FlightPlanStatus.VALIDATED, fp.status());
    }

    @Test
    public void testFlightPlanApprove() {
        final FlightPlan fp = new FlightPlan(
                FlightPlanId.of("FP-001"), "LIS-OPO", "LIS", "OPO",
                createDummyAircraftModel(), createDummyLegs(), 278.0, 5000.0);
        fp.approve();
        assertEquals(FlightPlanStatus.APPROVED, fp.status());
    }

    @Test
    public void testFlightPlanInvalidate() {
        final FlightPlan fp = new FlightPlan(
                FlightPlanId.of("FP-001"), "LIS-OPO", "LIS", "OPO",
                createDummyAircraftModel(), createDummyLegs(), 278.0, 5000.0);
        fp.invalidate();
        assertEquals(FlightPlanStatus.FAILED, fp.status());
    }

    @Test
    public void testFlightPlanLifecycle_draftToValidatedToApproved() {
        final FlightPlan fp = new FlightPlan(
                FlightPlanId.of("FP-001"), "LIS-OPO", "LIS", "OPO",
                createDummyAircraftModel(), createDummyLegs(), 278.0, 5000.0);
        assertEquals(FlightPlanStatus.DRAFT, fp.status());
        fp.validate();
        assertEquals(FlightPlanStatus.VALIDATED, fp.status());
        fp.approve();
        assertEquals(FlightPlanStatus.APPROVED, fp.status());
    }

    @Test
    public void testFlightPlanInvalidate_fromValidated() {
        final FlightPlan fp = new FlightPlan(
                FlightPlanId.of("FP-001"), "LIS-OPO", "LIS", "OPO",
                createDummyAircraftModel(), createDummyLegs(), 278.0, 5000.0);
        fp.validate();
        assertEquals(FlightPlanStatus.VALIDATED, fp.status());
        fp.invalidate();
        assertEquals(FlightPlanStatus.FAILED, fp.status());
    }

    @Test
    public void testFlightPlanStoreDsl() {
        final FlightPlan fp = new FlightPlan(
                FlightPlanId.of("FP-001"), "LIS-OPO", "LIS", "OPO",
                createDummyAircraftModel(), createDummyLegs(), 278.0, 5000.0);
        fp.storeDsl("flight FP_TEST { type: regular; }");
        assertNotNull(fp.dslContent());
        assertFalse(fp.dslContent().isBlank());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFlightPlanStoreDsl_null_throwsException() {
        final FlightPlan fp = new FlightPlan(
                FlightPlanId.of("FP-001"), "LIS-OPO", "LIS", "OPO",
                createDummyAircraftModel(), createDummyLegs(), 278.0, 5000.0);
        fp.storeDsl(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFlightPlanStoreDsl_blank_throwsException() {
        final FlightPlan fp = new FlightPlan(
                FlightPlanId.of("FP-001"), "LIS-OPO", "LIS", "OPO",
                createDummyAircraftModel(), createDummyLegs(), 278.0, 5000.0);
        fp.storeDsl("   ");
    }

    @Test
    public void testFlightPlanEquals() {
        final FlightPlan fp1 = new FlightPlan(
                FlightPlanId.of("FP-001"), "LIS-OPO", "LIS", "OPO",
                createDummyAircraftModel(), createDummyLegs(), 278.0, 5000.0);
        final FlightPlan fp2 = new FlightPlan(
                FlightPlanId.of("FP-001"), "LIS-OPO", "LIS", "OPO",
                createDummyAircraftModel(), createDummyLegs(), 278.0, 5000.0);
        assertEquals(fp1, fp2);
        assertEquals(fp1.hashCode(), fp2.hashCode());
    }

    // =========================================================================
    // US080 constructor (manual creation) — new tests
    // =========================================================================

    @Test
    public void ensureManualFlightPlanIsCreatedWithDraftStatus() {
        final AirTransportCompany company = dummyCompany();
        final FlightPlan fp = new FlightPlan(
                FlightPlanId.of("FP-080"),
                dummyRoute(company),
                dummyAircraft(company),
                dummyPilot(company),
                futureDateTime(),
                15_000.0);

        assertEquals(FlightPlanStatus.DRAFT, fp.status());
    }

    @Test
    public void ensureManualFlightPlanHasNoLegsAtCreation() {
        final AirTransportCompany company = dummyCompany();
        final FlightPlan fp = new FlightPlan(
                FlightPlanId.of("FP-080"),
                dummyRoute(company),
                dummyAircraft(company),
                dummyPilot(company),
                futureDateTime(),
                15_000.0);

        assertTrue(fp.legs().isEmpty());
    }

    @Test
    public void ensureManualFlightPlanDepartureAndArrivalDerivedFromRoute() {
        final AirTransportCompany company = dummyCompany();
        final FlightPlan fp = new FlightPlan(
                FlightPlanId.of("FP-080"),
                dummyRoute(company),
                dummyAircraft(company),
                dummyPilot(company),
                futureDateTime(),
                15_000.0);

        assertEquals("LIS", fp.departureAirport());
        assertEquals("OPO", fp.arrivalAirport());
    }

    @Test
    public void ensureManualFlightPlanTotalDistanceIsZeroAtCreation() {
        final AirTransportCompany company = dummyCompany();
        final FlightPlan fp = new FlightPlan(
                FlightPlanId.of("FP-080"),
                dummyRoute(company),
                dummyAircraft(company),
                dummyPilot(company),
                futureDateTime(),
                15_000.0);

        assertEquals(0.0, fp.totalDistance(), 0.0001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureManualFlightPlanRejectsNullId() {
        final AirTransportCompany company = dummyCompany();
        new FlightPlan(null, dummyRoute(company), dummyAircraft(company),
                dummyPilot(company), futureDateTime(), 15_000.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureManualFlightPlanRejectsNullRoute() {
        final AirTransportCompany company = dummyCompany();
        new FlightPlan(FlightPlanId.of("FP-080"), null, dummyAircraft(company),
                dummyPilot(company), futureDateTime(), 15_000.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureManualFlightPlanRejectsNullAircraft() {
        final AirTransportCompany company = dummyCompany();
        new FlightPlan(FlightPlanId.of("FP-080"), dummyRoute(company), null,
                dummyPilot(company), futureDateTime(), 15_000.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureManualFlightPlanRejectsNullPilot() {
        final AirTransportCompany company = dummyCompany();
        new FlightPlan(FlightPlanId.of("FP-080"), dummyRoute(company),
                dummyAircraft(company), null, futureDateTime(), 15_000.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureManualFlightPlanRejectsNullDepartureDateTime() {
        final AirTransportCompany company = dummyCompany();
        new FlightPlan(FlightPlanId.of("FP-080"), dummyRoute(company),
                dummyAircraft(company), dummyPilot(company), null, 15_000.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureManualFlightPlanRejectsZeroFuel() {
        final AirTransportCompany company = dummyCompany();
        new FlightPlan(FlightPlanId.of("FP-080"), dummyRoute(company),
                dummyAircraft(company), dummyPilot(company), futureDateTime(), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureManualFlightPlanRejectsNegativeFuel() {
        final AirTransportCompany company = dummyCompany();
        new FlightPlan(FlightPlanId.of("FP-080"), dummyRoute(company),
                dummyAircraft(company), dummyPilot(company), futureDateTime(), -100.0);
    }
}