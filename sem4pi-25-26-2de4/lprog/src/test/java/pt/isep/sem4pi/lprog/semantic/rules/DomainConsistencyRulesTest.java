package pt.isep.sem4pi.lprog.semantic.rules;

import org.junit.jupiter.api.Test;
import pt.isep.sem4pi.lprog.error.DSLError;
import pt.isep.sem4pi.lprog.model.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DomainConsistencyRules (US120).
 * Covers: airport code format (IATA/ICAO), calendar date validity.
 */
public class DomainConsistencyRulesTest {

    private final DomainConsistencyRules rules = new DomainConsistencyRules();

    // ── Happy path ───────────────────────────────────────────────────────────

    @Test
    void validIATACode_ShouldProduceNoErrors() {
        FlightPlan plan = buildPlanWithAirports("OPO", "MAD");
        assertTrue(rules.validate(plan).isEmpty());
    }

    @Test
    void validICAOCode_ShouldProduceNoErrors() {
        FlightPlan plan = buildPlanWithAirports("LPPR", "LEMD");
        assertTrue(rules.validate(plan).isEmpty());
    }

    @Test
    void validCalendarDate_ShouldProduceNoErrors() {
        FlightPlan plan = buildPlanWithAirports("OPO", "MAD");
        assertTrue(rules.validate(plan).isEmpty());
    }

    // ── Airport code error conditions ─────────────────────────────────────────

    @Test
    void twoLetterCode_ShouldProduceError() {
        FlightPlan plan = buildPlanWithAirports("OP", "MAD");
        List<DSLError> errors = rules.validate(plan);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(
                e -> e.getMessage().contains("código de aeroporto inválido")));
    }

    @Test
    void fiveLetterCode_ShouldProduceError() {
        FlightPlan plan = buildPlanWithAirports("OPORTO", "MAD");
        assertFalse(rules.validate(plan).isEmpty());
    }

    @Test
    void lowercaseCode_ShouldProduceError() {
        FlightPlan plan = buildPlanWithAirports("opo", "MAD");
        List<DSLError> errors = rules.validate(plan);
        assertFalse(errors.isEmpty());
        assertTrue(errors.stream().anyMatch(
                e -> e.getMessage().contains("IATA") ||
                        e.getMessage().contains("ICAO")));
    }

    @Test
    void codeWithDigits_ShouldProduceError() {
        FlightPlan plan = buildPlanWithAirports("OP1", "MAD");
        assertFalse(rules.validate(plan).isEmpty());
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private FlightPlan buildPlanWithAirports(String depCode, String arrCode) {
        FlightPlan plan = new FlightPlan();
        plan.setId("TP001");
        plan.setType("regular");
        plan.setLine(1);
        plan.setColumn(0);

        FlightLeg leg = new FlightLeg();
        leg.setDeparture(new Departure(depCode,
                LocalDateTime.of(2025, 6, 1, 10, 0), 3));
        leg.setArrival(new Arrival(arrCode,
                LocalDateTime.of(2025, 6, 1, 12, 0), 4));
        leg.setRoute(new Route(depCode, arrCode, 5));
        leg.addSegment(new FlightSegment("cruise",
                new Coordinate(41.2, -8.6, 10000),
                new Coordinate(40.4, -3.7, 10000),
                10.0, 6));
        leg.setFuel(new Fuel(5000.0, "kg", 10, 0));

        plan.addLeg(leg);
        return plan;
    }
}