package pt.isep.sem4pi.lprog.semantic.rules;

import org.junit.jupiter.api.Test;
import pt.isep.sem4pi.lprog.error.DSLError;
import pt.isep.sem4pi.lprog.model.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LegLevelRules (US120).
 * Covers: intra-leg time coherence, fuel positivity, segment presence.
 * Note: fuel positivity is also covered in SemanticValidatorTest (Sprint 2)
 * via the full pipeline — these tests validate the rule class directly.
 */
public class LegLevelRulesTest {

    private final LegLevelRules rules = new LegLevelRules();

    // ── Happy path ───────────────────────────────────────────────────────────

    @Test
    void departureBeforeArrival_ShouldProduceNoErrors() {
        FlightPlan plan = buildPlanWithTimes(
                LocalDateTime.of(2025, 6, 1, 10, 0),
                LocalDateTime.of(2025, 6, 1, 12, 0));
        assertTrue(rules.validate(plan).isEmpty());
    }

    @Test
    void nullTimes_ShouldProduceNoErrors() {
        // AT is optional in the grammar — null times are valid
        FlightPlan plan = buildPlanWithTimes(null, null);
        assertTrue(rules.validate(plan).isEmpty());
    }

    @Test
    void positiveFuel_ShouldProduceNoErrors() {
        FlightPlan plan = buildPlanWithFuel(5000.0);
        assertTrue(rules.validate(plan).isEmpty());
    }

    // ── Error conditions ─────────────────────────────────────────────────────

    @Test
    void departureAfterArrival_ShouldProduceError() {
        FlightPlan plan = buildPlanWithTimes(
                LocalDateTime.of(2025, 6, 1, 14, 0),
                LocalDateTime.of(2025, 6, 1, 12, 0));
        List<DSLError> errors = rules.validate(plan);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).getMessage().contains("hora de partida"));
    }

    @Test
    void departureEqualToArrival_ShouldProduceError() {
        LocalDateTime same = LocalDateTime.of(2025, 6, 1, 10, 0);
        FlightPlan plan = buildPlanWithTimes(same, same);
        assertFalse(rules.validate(plan).isEmpty());
    }

    @Test
    void zeroFuel_ShouldProduceError() {
        FlightPlan plan = buildPlanWithFuel(0.0);
        List<DSLError> errors = rules.validate(plan);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).getMessage().contains("estritamente positiva"));
    }

    @Test
    void negativeFuel_ShouldProduceError() {
        FlightPlan plan = buildPlanWithFuel(-100.0);
        assertFalse(rules.validate(plan).isEmpty());
    }

    @Test
    void emptySegments_ShouldProduceError() {
        FlightPlan plan = buildPlanWithFuel(5000.0);
        plan.getLegs().get(0).getSegments().clear();
        List<DSLError> errors = rules.validate(plan);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).getMessage().contains("pelo menos um segmento"));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private FlightPlan buildPlanWithTimes(LocalDateTime depTime,
                                          LocalDateTime arrTime) {
        FlightPlan plan = new FlightPlan();
        plan.setId("TP001");
        plan.setType("regular");
        plan.setLine(1);
        plan.setColumn(0);

        FlightLeg leg = new FlightLeg();
        leg.setDeparture(new Departure("OPO", depTime, 3));
        leg.setArrival(new Arrival("MAD", arrTime, 4));
        leg.setRoute(new Route("OPO", "MAD", 5));
        leg.addSegment(new FlightSegment("cruise",
                new Coordinate(41.2, -8.6, 10000),
                new Coordinate(40.4, -3.7, 10000),
                10.0, 6));
        leg.setFuel(new Fuel(5000.0, "kg", 10, 0));

        plan.addLeg(leg);
        return plan;
    }

    private FlightPlan buildPlanWithFuel(double quantity) {
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
        leg.addSegment(new FlightSegment("cruise",
                new Coordinate(41.2, -8.6, 10000),
                new Coordinate(40.4, -3.7, 10000),
                10.0, 6));
        leg.setFuel(new Fuel(quantity, "kg", 10, 0));

        plan.addLeg(leg);
        return plan;
    }
}