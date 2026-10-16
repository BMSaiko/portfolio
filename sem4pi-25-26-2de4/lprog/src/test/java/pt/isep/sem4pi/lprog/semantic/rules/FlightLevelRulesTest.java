package pt.isep.sem4pi.lprog.semantic.rules;

import org.junit.jupiter.api.Test;
import pt.isep.sem4pi.lprog.error.DSLError;
import pt.isep.sem4pi.lprog.model.FlightPlan;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FlightLevelRules (US120).
 * Covers: flight ID presence, flight type validity.
 */
public class FlightLevelRulesTest {

    private final FlightLevelRules rules = new FlightLevelRules();

    // ── Happy path ───────────────────────────────────────────────────────────

    @Test
    void validTypeRegular_ShouldProduceNoErrors() {
        FlightPlan plan = buildPlan("TP001", "regular");
        assertTrue(rules.validate(plan).isEmpty());
    }

    @Test
    void validTypeCharter_ShouldProduceNoErrors() {
        FlightPlan plan = buildPlan("TP001", "charter");
        assertTrue(rules.validate(plan).isEmpty());
    }

    @Test
    void validTypeCaseInsensitive_ShouldProduceNoErrors() {
        FlightPlan plan = buildPlan("TP001", "REGULAR");
        assertTrue(rules.validate(plan).isEmpty());
    }

    // ── Error conditions ─────────────────────────────────────────────────────

    @Test
    void invalidType_ShouldProduceError() {
        FlightPlan plan = buildPlan("TP001", "express");
        List<DSLError> errors = rules.validate(plan);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).getMessage().contains("Tipo de voo inválido"));
    }

    @Test
    void nullType_ShouldProduceError() {
        FlightPlan plan = buildPlan("TP001", null);
        assertFalse(rules.validate(plan).isEmpty());
    }

    @Test
    void blankId_ShouldProduceError() {
        FlightPlan plan = buildPlan("", "regular");
        List<DSLError> errors = rules.validate(plan);
        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).getMessage().contains("identificador do voo"));
    }

    @Test
    void nullId_ShouldProduceError() {
        FlightPlan plan = buildPlan(null, "regular");
        assertFalse(rules.validate(plan).isEmpty());
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private FlightPlan buildPlan(String id, String type) {
        FlightPlan plan = new FlightPlan();
        plan.setId(id);
        plan.setType(type);
        plan.setLine(1);
        plan.setColumn(0);
        return plan;
    }
}