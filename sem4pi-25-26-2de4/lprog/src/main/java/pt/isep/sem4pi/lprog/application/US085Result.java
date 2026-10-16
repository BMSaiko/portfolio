package pt.isep.sem4pi.lprog.application;

import pt.isep.sem4pi.lprog.error.DSLError;
import pt.isep.sem4pi.lprog.testing.PhysicalIssue;

import java.util.Collections;
import java.util.List;

/**
 * Aggregated result of the US085 validation pipeline.
 *
 * <p>A result is always in exactly one of these states:
 * <ul>
 *   <li>{@link #ioError} — the DSL file could not be read.</li>
 *   <li>{@link #dslFailure} — lexical, syntactic, or semantic DSL errors found;
 *       C component was not invoked.</li>
 *   <li>{@link #physicalFailure} — DSL is valid, but the C component found
 *       physical issues.</li>
 *   <li>{@link #executionError} — DSL is valid, but the C component could not
 *       be launched or produced invalid output.</li>
 *   <li>{@link #success} — DSL valid and all physical checks passed.</li>
 * </ul>
 */
public class US085Result {

    public enum State {
        SUCCESS, DSL_FAILURE, PHYSICAL_FAILURE, EXECUTION_ERROR, IO_ERROR
    }

    private final State             state;
    private final List<DSLError>    dslErrors;
    private final List<PhysicalIssue> physicalIssues;
    private final String            errorMessage;   /* for IO_ERROR / EXECUTION_ERROR */

    /* ── Private constructor ─────────────────────────────────────────────── */

    private US085Result(State state,
                        List<DSLError> dslErrors,
                        List<PhysicalIssue> physicalIssues,
                        String errorMessage) {
        this.state          = state;
        this.dslErrors      = Collections.unmodifiableList(dslErrors);
        this.physicalIssues = Collections.unmodifiableList(physicalIssues);
        this.errorMessage   = errorMessage;
    }

    /* ── Factory methods ─────────────────────────────────────────────────── */

    public static US085Result success() {
        return new US085Result(State.SUCCESS, List.of(), List.of(), null);
    }

    public static US085Result dslFailure(List<DSLError> dslErrors) {
        return new US085Result(State.DSL_FAILURE, dslErrors, List.of(), null);
    }

    public static US085Result physicalFailure(List<PhysicalIssue> physicalIssues) {
        return new US085Result(State.PHYSICAL_FAILURE, List.of(), physicalIssues, null);
    }

    public static US085Result executionError(String message) {
        return new US085Result(State.EXECUTION_ERROR, List.of(), List.of(), message);
    }

    public static US085Result ioError(String message) {
        return new US085Result(State.IO_ERROR, List.of(), List.of(), message);
    }

    /* ── Accessors ───────────────────────────────────────────────────────── */

    public State               getState()          { return state;          }
    public boolean             isSuccess()         { return state == State.SUCCESS; }
    public List<DSLError>      getDslErrors()      { return dslErrors;      }
    public List<PhysicalIssue> getPhysicalIssues() { return physicalIssues; }
    public String              getErrorMessage()   { return errorMessage;   }

    /* ── Human-readable summary ──────────────────────────────────────────── */

    public void printSummary() {
        switch (state) {
            case SUCCESS -> System.out.println("Flight plan is VALID — all checks passed.");

            case DSL_FAILURE -> {
                System.out.printf("DSL validation FAILED — %d error(s):%n", dslErrors.size());
                dslErrors.forEach(e -> System.out.println("  " + e));
            }

            case PHYSICAL_FAILURE -> {
                System.out.printf("Physical test FAILED — %d issue(s):%n", physicalIssues.size());
                physicalIssues.forEach(i -> System.out.println("  " + i));
            }

            case EXECUTION_ERROR ->
                    System.out.println("EXECUTION ERROR — could not run physical test: " + errorMessage);

            case IO_ERROR ->
                    System.out.println("IO ERROR — could not read DSL file: " + errorMessage);
        }
    }

    @Override
    public String toString() {
        return "US085Result[" + state + "]";
    }
}
