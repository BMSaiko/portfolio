package pt.isep.sem4pi.lprog.testing;

import org.junit.jupiter.api.Test;
import pt.isep.sem4pi.lprog.model.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FlightPlanTestInputBuilder}.
 *
 * Two categories:
 *   1. Conversion helpers  — test normaliseAltitude / normaliseFuel directly
 *                            (package-private access, same package as Builder).
 *   2. Model mapping       — build real FlightPlan objects and verify the DTO.
 */
class FlightPlanTestInputBuilderTest {

    // ─────────────────────────────────────────────────────────────────────────
    // ALTITUDE CONVERSION
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void normaliseAltitude_feet_converted() {
        // The builder's default unit is "ft".
        // 10000 ft × (1/3.28084) ≈ 3048 m
        double result = FlightPlanTestInputBuilder.normaliseAltitude(10000.0, "ft");
        assertEquals(3048.0, result, 1.0); // 1 m tolerance
    }

    @Test
    void normaliseAltitude_metres_unchanged() {
        assertEquals(8000.0, FlightPlanTestInputBuilder.normaliseAltitude(8000.0, "m"), 0.001);
    }

    @Test
    void normaliseAltitude_kilometres_converted() {
        assertEquals(8000.0, FlightPlanTestInputBuilder.normaliseAltitude(8.0, "km"), 0.001);
    }

    @Test
    void normaliseAltitude_nullUnit_returnsUnchanged() {
        assertEquals(500.0, FlightPlanTestInputBuilder.normaliseAltitude(500.0, null), 0.001);
    }

    @Test
    void normaliseAltitude_unknownUnit_returnsUnchanged() {
        assertEquals(500.0, FlightPlanTestInputBuilder.normaliseAltitude(500.0, "nautical"), 0.001);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FUEL CONVERSION
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void normaliseFuel_kilograms_unchanged() {
        assertEquals(14500.0, FlightPlanTestInputBuilder.normaliseFuel(14500.0, "kg"), 0.001);
    }

    @Test
    void normaliseFuel_litres_converted() {
        // 1000 l × 0.804 kg/l = 804 kg
        assertEquals(804.0, FlightPlanTestInputBuilder.normaliseFuel(1000.0, "l"), 0.001);
    }

    @Test
    void normaliseFuel_liter_converted() {
        assertEquals(804.0, FlightPlanTestInputBuilder.normaliseFuel(1000.0, "liter"), 0.001);
    }

    @Test
    void normaliseFuel_nullUnit_returnsUnchanged() {
        assertEquals(5000.0, FlightPlanTestInputBuilder.normaliseFuel(5000.0, null), 0.001);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MODEL MAPPING
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void from_nullPlan_returnsNull() {
        assertNull(FlightPlanTestInputBuilder.from(null));
    }

    @Test
    void from_setsFlightId() {
        FlightPlan plan = buildPlan("TP100");
        assertEquals("TP100", FlightPlanTestInputBuilder.from(plan).getFlightId());
    }

    @Test
    void from_legsCountMatches() {
        FlightPlan plan = buildPlan("TP200");
        plan.addLeg(buildLeg("LPPT", "LPPR", 14500.0, "kg"));
        plan.addLeg(buildLeg("LPPR", "LEMD", 12000.0, "kg"));

        assertEquals(2, FlightPlanTestInputBuilder.from(plan).getLegs().size());
    }

    @Test
    void from_fuelKg_kgUnit_passesThrough() {
        FlightPlan plan = buildPlan("TP300");
        plan.addLeg(buildLeg("LPPT", "LPPR", 14500.0, "kg"));

        assertEquals(14500.0,
                FlightPlanTestInputBuilder.from(plan).getLegs().get(0).getFuelKg(), 0.001);
    }

    @Test
    void from_fuelKg_litreUnit_isConverted() {
        FlightPlan plan = buildPlan("TP301");
        plan.addLeg(buildLeg("LPPT", "LPPR", 1000.0, "l"));

        // 1000 l × 0.804 = 804 kg
        assertEquals(804.0,
                FlightPlanTestInputBuilder.from(plan).getLegs().get(0).getFuelKg(), 0.001);
    }

    @Test
    void from_airportCodesAreMapped() {
        FlightPlan plan = buildPlan("TP400");
        plan.addLeg(buildLeg("LPPT", "LEMD", 14500.0, "kg"));

        FlightPlanTestInput.Leg leg = FlightPlanTestInputBuilder.from(plan).getLegs().get(0);
        assertEquals("LPPT", leg.getDepartureAirport());
        assertEquals("LEMD", leg.getArrivalAirport());
    }

    @Test
    void from_segmentCountMatches() {
        FlightPlan plan = buildPlan("TP500");
        FlightLeg leg = buildLegWithSegments("LPPT", "LPPR", 14500.0, "kg", List.of(
                buildSegment("CLIMB",   0.0,    8000.0),
                buildSegment("CRUISE",  8000.0, 8000.0),
                buildSegment("DESCEND", 8000.0, 0.0)
        ));
        plan.addLeg(leg);

        assertEquals(3,
                FlightPlanTestInputBuilder.from(plan).getLegs().get(0).getSegments().size());
    }

    @Test
    void from_segmentMode_isUppercased() {
        FlightPlan plan = buildPlan("TP501");
        FlightLeg leg = buildLegWithSegments("LPPT", "LPPR", 14500.0, "kg",
                List.of(buildSegment("climb", 0.0, 8000.0)));
        plan.addLeg(leg);

        String mode = FlightPlanTestInputBuilder.from(plan)
                .getLegs().get(0).getSegments().get(0).getMode();
        assertEquals("CLIMB", mode);
    }

    @Test
    void from_segmentCoordinatesAreMapped() {
        FlightPlan plan = buildPlan("TP502");
        FlightSegment seg = new FlightSegment(
                "CRUISE",
                new Coordinate(38.78, -9.13, 0.0), // altitude 0 — unit is ft, so 0 ft = 0 m
                new Coordinate(41.23, -8.68, 0.0),
                0.0, 1
        );
        FlightLeg leg = buildLegWithSegments("LPPT", "LPPR", 14500.0, "kg", List.of(seg));
        plan.addLeg(leg);

        FlightPlanTestInput.Segment result =
                FlightPlanTestInputBuilder.from(plan).getLegs().get(0).getSegments().get(0);

        assertEquals(38.78, result.getStartLat(), 0.0001);
        assertEquals(-9.13, result.getStartLon(), 0.0001);
        assertEquals(41.23, result.getEndLat(),   0.0001);
        assertEquals(-8.68, result.getEndLon(),   0.0001);
    }

    @Test
    void from_altitudeIsConvertedFromFeet() {
        // The builder uses "ft" as default unit.
        // 26247 ft × (1/3.28084) ≈ 7999.9 m ≈ 8000 m
        FlightPlan plan = buildPlan("TP503");
        FlightSegment seg = new FlightSegment(
                "CRUISE",
                new Coordinate(38.78, -9.13, 26247.0), // 26247 ft ≈ 8000 m
                new Coordinate(41.23, -8.68, 26247.0),
                0.0, 1
        );
        FlightLeg leg = buildLegWithSegments("LPPT", "LPPR", 14500.0, "kg", List.of(seg));
        plan.addLeg(leg);

        FlightPlanTestInput.Segment result =
                FlightPlanTestInputBuilder.from(plan).getLegs().get(0).getSegments().get(0);

        assertEquals(8000.0, result.getStartAltM(), 5.0); // 5 m tolerance
        assertEquals(8000.0, result.getEndAltM(),   5.0);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // JSON SERIALISATION
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void toJson_containsFlightId() {
        FlightPlan plan = buildPlan("TP999");
        plan.addLeg(buildLeg("LPPT", "LPPR", 14500.0, "kg"));
        String json = FlightPlanTestInputBuilder.from(plan).toJson();
        assertTrue(json.contains("\"flightId\":\"TP999\""),
                "Expected flightId in JSON. Got: " + json);
    }

    @Test
    void toJson_containsAirportCodes() {
        FlightPlan plan = buildPlan("TP998");
        plan.addLeg(buildLeg("LPPT", "LEMD", 14500.0, "kg"));
        String json = FlightPlanTestInputBuilder.from(plan).toJson();
        assertTrue(json.contains("LPPT"), "Expected LPPT in JSON");
        assertTrue(json.contains("LEMD"), "Expected LEMD in JSON");
    }

    @Test
    void toJson_containsSegmentMode() {
        FlightPlan plan = buildPlan("TP997");
        FlightLeg leg = buildLegWithSegments("LPPT", "LPPR", 14500.0, "kg",
                List.of(buildSegment("CLIMB", 0.0, 8000.0)));
        plan.addLeg(leg);
        String json = FlightPlanTestInputBuilder.from(plan).toJson();
        assertTrue(json.contains("\"mode\":\"CLIMB\""),
                "Expected mode CLIMB in JSON. Got: " + json);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private FlightPlan buildPlan(String id) {
        FlightPlan plan = new FlightPlan();
        plan.setId(id);
        plan.setType("regular");
        return plan;
    }

    /** Leg with a single CRUISE segment at sea level — enough for mapping tests. */
    private FlightLeg buildLeg(String dep, String arr, double fuel, String fuelUnit) {
        return buildLegWithSegments(dep, arr, fuel, fuelUnit,
                List.of(buildSegment("CRUISE", 0.0, 0.0)));
    }

    private FlightLeg buildLegWithSegments(String dep, String arr, double fuel,
                                           String fuelUnit, List<FlightSegment> segments) {
        FlightLeg leg = new FlightLeg();
        leg.setDeparture(new Departure(dep, LocalDateTime.now(), 1));
        leg.setArrival(new Arrival(arr, LocalDateTime.now(), 2));
        leg.setFuel(new Fuel(fuel, fuelUnit, 3, 1));
        for (FlightSegment seg : segments) leg.addSegment(seg);
        return leg;
    }

    private FlightSegment buildSegment(String mode, double startAlt, double endAlt) {
        return new FlightSegment(
                mode,
                new Coordinate(38.78, -9.13, startAlt),
                new Coordinate(41.23, -8.68, endAlt),
                0.0, 1
        );
    }
}