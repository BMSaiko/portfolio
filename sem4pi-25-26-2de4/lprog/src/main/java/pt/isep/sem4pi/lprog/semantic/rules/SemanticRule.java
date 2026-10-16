package pt.isep.sem4pi.lprog.semantic.rules;

import pt.isep.sem4pi.lprog.error.DSLError;
import pt.isep.sem4pi.lprog.model.FlightPlan;

import java.util.List;

/**
 * Contract for a single group of semantic validation rules.
 * Each implementation is responsible for one cohesive set of rules
 * (e.g. leg-level, route coherence, domain consistency).
 *
 * All errors found must be collected and returned — never throw on first error.
 */
public interface SemanticRule {

    /**
     * Validates the given flight plan against this rule set.
     *
     * @param plan the fully-built domain object produced by the visitor
     * @return list of semantic errors found; empty if the plan is valid
     */
    List<DSLError> validate(FlightPlan plan);
}
