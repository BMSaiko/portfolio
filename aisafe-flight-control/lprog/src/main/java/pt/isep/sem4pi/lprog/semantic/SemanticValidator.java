package pt.isep.sem4pi.lprog.semantic;

import org.antlr.v4.runtime.tree.ParseTreeWalker;
import pt.isep.sem4pi.lprog.FlightDSLParser;
import pt.isep.sem4pi.lprog.error.DSLError;
import pt.isep.sem4pi.lprog.listener.FlightDSLListener;
import pt.isep.sem4pi.lprog.model.FlightPlan;
import pt.isep.sem4pi.lprog.semantic.rules.*;
import pt.isep.sem4pi.lprog.visitor.FlightDSLVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates the full semantic validation pipeline for the Flight DSL.
 *
 * Pipeline:
 *   1. FlightDSLListener  — structural checks that need the whole file at once
 *                           (e.g. duplicate flight IDs across plans).
 *   2. FlightDSLVisitor   — builds the internal domain model (List<FlightPlan>).
 *   3. SemanticRule chain — each rule class validates one cohesive group of
 *                           rules against every FlightPlan; all errors are
 *                           collected before returning (no fail-fast).
 *
 * Rule execution order (all errors collected regardless of order):
 *   FlightLevelRules       → flight ID presence, type validity
 *   LegLevelRules          → per-leg structure, fuel positivity, intra-leg times
 *   LegSequenceRules       → inter-leg airport and time coherence
 *   RouteCoherenceRules    → route vs. legs coherence, no airport visited twice
 *   SegmentCoherenceRules  → segment coordinates and numeric values
 *   DomainConsistencyRules → airport code format, calendar date validity
 */
public class SemanticValidator {

    private final List<SemanticRule> rules = List.of(
            new FlightLevelRules(),
            new LegLevelRules(),
            new LegSequenceRules(),
            new RouteCoherenceRules(),
            new SegmentCoherenceRules(),
            new DomainConsistencyRules()
    );

    /**
     * Validates the parse tree against all semantic rules.
     *
     * @param tree the root of the ANTLR parse tree (after successful
     *             lexical and syntactic analysis)
     * @return list of semantic errors; empty if the plan is valid
     */
    public List<DSLError> validate(FlightDSLParser.FlightFileContext tree) {
        List<DSLError> errors = new ArrayList<>();

        // ── Step 1: Listener — file-wide structural checks ───────────────────
        // The listener has visibility over all FlightPlans simultaneously,
        // making it the right place for cross-plan rules (e.g. duplicate IDs).
        FlightDSLListener listener = new FlightDSLListener();
        ParseTreeWalker.DEFAULT.walk(listener, tree);
        errors.addAll(listener.getSemanticErrors());

        // ── Step 2: Visitor — build the domain model ─────────────────────────
        FlightDSLVisitor visitor = new FlightDSLVisitor();
        List<FlightPlan> flightPlans = visitor.visitFlightFile(tree);

        // ── Step 3: Rule chain — validate each plan against every rule ────────
        // All rules run for every plan; errors accumulate across all rules.
        for (FlightPlan plan : flightPlans) {
            for (SemanticRule rule : rules) {
                errors.addAll(rule.validate(plan));
            }
        }

        return errors;
    }
}