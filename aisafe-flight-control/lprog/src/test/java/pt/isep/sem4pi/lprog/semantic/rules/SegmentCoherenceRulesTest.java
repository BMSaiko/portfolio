package pt.isep.sem4pi.lprog.semantic.rules;

import org.junit.jupiter.api.Test;
import pt.isep.sem4pi.lprog.error.DSLError;
import pt.isep.sem4pi.lprog.model.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SegmentCoherenceRules (US120).
 * Covers: start != end coordinates, altitude positivity, wind speed non-negative.
 * Note: start==end is also covered in SemanticValidatorTest (Sprint 2) via
 * the full pipeline — these tests additionally verify the richer context
 * messages ("Leg X, Segmento Y") introduced in the new rule class.
 */
public class SegmentCoherenceRulesTest {

    private final SegmentCoherenceRules rules = new SegmentCoherenceRules();

    // ── Happy path ───────────────────────────────────────────────────────────

    @Test
    void validSegment_ShouldProduceNoErrors() {
        FlightPlan plan = buildPlanWithSegment(
                new Coordinate(41.2, -8.6, 10000),
                new Coordinate(40.4, -3.7, 10000),
                10.0);
        assertTrue(rules.validate(plan).isEmpty());
    }

    @Test
    void zeroWindSpeed_ShouldProduceNoErrors() {
        // Wind speed of 0 is valid — calm conditions
        FlightPlan plan = buildPlanWithSegment(
                new Coordinate(41.2, -8.6, 10000),
                new Coordinate(40.4, -3.7, 10000),
                0.0);
        assertTrue(rules.validate(plan).isEmpty());
    }

    // ── Error conditions ─────────────────────────────────────────────────────

    @Test
    void identicalStartEnd_ShouldProduceErrorWithContext() {
        Coordinate same = new Coordinate(41.2, -8.6, 10000);
        FlightPlan plan = buildPlanWithSegment(same, same, 10.0);
        List<DSLError> errors = rules.validate(plan);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).getMessage().contains("Leg 1, Segmento 1"));
        assertTrue(errors.get(0).getMessage().contains("início e fim"));
    }

    @Test
    void negativeStartAltitude_ShouldProduceError() {
        FlightPlan plan = buildPlanWithSegment(
                new Coordinate(41.2, -8.6, -100),
                new Coordinate(40.4, -3.7, 10000),
                10.0);
        List<DSLError> errors = rules.validate(plan);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).getMessage().contains("altitude da coordenada de início"));
    }

    @Test
    void negativeEndAltitude_ShouldProduceError() {
        FlightPlan plan = buildPlanWithSegment(
                new Coordinate(41.2, -8.6, 10000),
                new Coordinate(40.4, -3.7, -50),
                10.0);
        List<DSLError> errors = rules.validate(plan);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).getMessage().contains("altitude da coordenada de fim"));
    }

    @Test
    void negativeWindSpeed_ShouldProduceError() {
        FlightPlan plan = buildPlanWithSegment(
                new Coordinate(41.2, -8.6, 10000),
                new Coordinate(40.4, -3.7, 10000),
                -5.0);
        List<DSLError> errors = rules.validate(plan);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).getMessage().contains("velocidade do vento"));
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private FlightPlan buildPlanWithSegment(Coordinate start,
                                            Coordinate end,
                                            double windSpeed) {
        FlightPlan plan = new FlightPlan();
        plan.setId("TP001");
        plan.setType("regular");
        plan.setLine(1);
        plan.setColumn(0);

        FlightLeg leg = new FlightLeg();
        leg.setDeparture(new Departure("OPO",
                LocalDateTime.of(2025, 6, 1, 10, 0), 3));
        leg.setArrival(new Arrival("MAD",
                LocalDateTime.of(2025, 6, 1, 12, 0), 4));
        leg.setRoute(new Route("OPO", "MAD", 5));
        leg.addSegment(new FlightSegment("cruise", start, end, windSpeed, 6));
        leg.setFuel(new Fuel(5000.0, "kg", 10, 0));

        plan.addLeg(leg);
        return plan;
    }
}