package pt.isep.sem4pi.lprog.application;

import pt.isep.sem4pi.lprog.FlightDSLValidator;
import pt.isep.sem4pi.lprog.model.FlightPlan;
import pt.isep.sem4pi.lprog.testing.FlightPlanTestInput;
import pt.isep.sem4pi.lprog.testing.FlightPlanTestInputBuilder;
import pt.isep.sem4pi.lprog.testing.FlightPlanTestResult;
import pt.isep.sem4pi.lprog.testing.FlightPlanTestRunner;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Orchestrates the full US085 validation pipeline for a flight plan.
 *
 * <h3>Pipeline</h3>
 * <ol>
 *   <li><strong>DSL validation</strong> — lexical, syntactic, and semantic
 *       analysis via {@link FlightDSLValidator}.</li>
 *   <li><strong>Model extraction</strong> — if DSL is valid, the parse tree
 *       is walked by {@code FlightDSLVisitor} to produce {@link FlightPlan}
 *       objects.</li>
 *   <li><strong>Physical test</strong> — each flight plan is passed to the C
 *       {@code flight_tester} process via {@link FlightPlanTestRunner}.</li>
 * </ol>
 *
 * <p>If any stage fails, the pipeline stops and the result carries the
 * relevant errors.  The C component is only invoked when the DSL is valid.
 *
 * <h3>Usage (Eixo 1 — standalone / tests)</h3>
 * <pre>
 *   ValidateFlightPlanService service = new ValidateFlightPlanService();
 *   US085Result result = service.validate(Path.of("flight.dsl"));
 *   result.printSummary();
 * </pre>
 *
 * <h3>Usage (Eixo 2 — EAPLI controller, future)</h3>
 * The EAPLI {@code ValidateFlightPlanController} will call this service and
 * then update the {@code FlightPlan} domain object status in the repository.
 * No changes to this service are required for that integration.
 */
public class ValidateFlightPlanService {

    private final FlightDSLValidator   dslValidator;
    private final FlightPlanTestRunner testRunner;

    /* ── Constructors ────────────────────────────────────────────────────── */

    /** Production constructor — uses default components. */
    public ValidateFlightPlanService() {
        this(new FlightDSLValidator(), new FlightPlanTestRunner());
    }

    /**
     * Injection constructor — allows substituting components in tests
     * (e.g. a mock {@link FlightPlanTestRunner} that does not launch a real process).
     */
    public ValidateFlightPlanService(FlightDSLValidator dslValidator,
                                     FlightPlanTestRunner testRunner) {
        this.dslValidator = dslValidator;
        this.testRunner   = testRunner;
    }

    /* ── Public API ──────────────────────────────────────────────────────── */

    /**
     * Validates the flight plan DSL file at the given path.
     *
     * @param filePath path to the {@code .flight} (or {@code .dsl}) file
     * @return a {@link US085Result} — never {@code null}
     */
    public US085Result validate(Path filePath) {
        // ── Step 1: DSL validation ────────────────────────────────────────
        FlightDSLValidator.ValidationResult dslResult;
        try {
            dslResult = dslValidator.validate(filePath);
        } catch (IOException e) {
            return US085Result.ioError("Cannot read file '" + filePath + "': " + e.getMessage());
        }

        if (!dslResult.isValid()) {
            return US085Result.dslFailure(dslResult.getErrors());
        }

        // ── Step 2: Extract model objects ────────────────────────────────
        List<FlightPlan> flightPlans = dslResult.getFlightPlans();
        if (flightPlans.isEmpty()) {
            return US085Result.dslFailure(List.of()); // grammar passed but nothing parsed
        }

        // ── Step 3: Physical test for each flight plan ────────────────────
        return runPhysicalTests(flightPlans);
    }

    /**
     * Validates a flight plan DSL given as a string.
     * Useful for unit tests that do not need a file on disk.
     *
     * @param dslSource the DSL source code
     * @return a {@link US085Result} — never {@code null}
     */
    public US085Result validateString(String dslSource) {
        // ── Step 1: DSL validation ────────────────────────────────────────
        FlightDSLValidator.ValidationResult dslResult = dslValidator.validateString(dslSource);

        if (!dslResult.isValid()) {
            return US085Result.dslFailure(dslResult.getErrors());
        }

        // ── Step 2: Extract model objects ────────────────────────────────
        List<FlightPlan> flightPlans = dslResult.getFlightPlans();
        if (flightPlans.isEmpty()) {
            return US085Result.dslFailure(List.of());
        }

        // ── Step 3: Physical test for each flight plan ────────────────────
        return runPhysicalTests(flightPlans);
    }

    /* ── Private helpers ─────────────────────────────────────────────────── */

    private US085Result runPhysicalTests(List<FlightPlan> flightPlans) {
        for (FlightPlan plan : flightPlans) {
            FlightPlanTestInput input = FlightPlanTestInputBuilder.from(plan);
            FlightPlanTestResult testResult = testRunner.run(input);

            if (testResult.isExecutionError()) {
                return US085Result.executionError(testResult.getExecutionErrorMessage());
            }

            if (!testResult.isPassed()) {
                return US085Result.physicalFailure(testResult.getIssues());
            }
        }
        return US085Result.success();
    }
}
