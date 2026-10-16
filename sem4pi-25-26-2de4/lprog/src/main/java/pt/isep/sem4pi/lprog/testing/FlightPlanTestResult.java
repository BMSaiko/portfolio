package pt.isep.sem4pi.lprog.testing;

import java.util.Collections;
import java.util.List;

/**
 * Holds the result returned by the {@code flight_tester} C process.
 *
 * <p>Mirrors the JSON output protocol:
 * <pre>
 *   { "passed": false, "issues": [ { "field": "...", "message": "..." } ] }
 * </pre>
 *
 * <p>Also covers error cases where the process could not be started or
 * produced unexpected output (see {@link #executionError(String)}).
 */
public class FlightPlanTestResult {

    private final boolean       passed;
    private final List<PhysicalIssue> issues;
    private final boolean       isExecutionError;
    private final String        executionErrorMessage;

    /* ── Private constructor — use factory methods ────────────────────────── */

    private FlightPlanTestResult(boolean passed,
                                 List<PhysicalIssue> issues,
                                 boolean isExecutionError,
                                 String executionErrorMessage) {
        this.passed               = passed;
        this.issues               = Collections.unmodifiableList(issues);
        this.isExecutionError     = isExecutionError;
        this.executionErrorMessage = executionErrorMessage;
    }

    /* ── Factory methods ─────────────────────────────────────────────────── */

    /** All physical checks passed. */
    public static FlightPlanTestResult passed() {
        return new FlightPlanTestResult(true, List.of(), false, null);
    }

    /** One or more physical issues found. */
    public static FlightPlanTestResult failed(List<PhysicalIssue> issues) {
        if (issues == null || issues.isEmpty())
            throw new IllegalArgumentException("failed result must have at least one issue");
        return new FlightPlanTestResult(false, issues, false, null);
    }

    /** The C process could not be launched or produced invalid output. */
    public static FlightPlanTestResult executionError(String message) {
        if (message == null || message.isBlank())
            throw new IllegalArgumentException("executionErrorMessage must not be blank");
        return new FlightPlanTestResult(false, List.of(), true, message);
    }

    /* ── Accessors ───────────────────────────────────────────────────────── */

    /** Returns {@code true} if all physical checks passed. */
    public boolean isPassed() { return passed; }

    /** Returns the list of physical issues (empty if passed or execution error). */
    public List<PhysicalIssue> getIssues() { return issues; }

    /**
     * Returns {@code true} if the C process could not be started or
     * its output could not be parsed. In this case {@link #getIssues()}
     * is empty and {@link #getExecutionErrorMessage()} has details.
     */
    public boolean isExecutionError() { return isExecutionError; }

    /**
     * Returns the execution error message, or {@code null} if this is a
     * normal (passed or failed) result.
     */
    public String getExecutionErrorMessage() { return executionErrorMessage; }

    @Override
    public String toString() {
        if (isExecutionError) return "FlightPlanTestResult[EXECUTION_ERROR: " + executionErrorMessage + "]";
        if (passed)           return "FlightPlanTestResult[PASSED]";
        return "FlightPlanTestResult[FAILED, issues=" + issues.size() + "]";
    }
}
