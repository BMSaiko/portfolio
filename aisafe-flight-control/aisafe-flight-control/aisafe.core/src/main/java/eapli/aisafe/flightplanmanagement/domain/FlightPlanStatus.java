package eapli.aisafe.flightplanmanagement.domain;

/**
 * Enum representing the possible statuses of a flight plan.
 *
 * <p>Status lifecycle:</p>
 * <pre>
 *   DRAFT → VALIDATED → APPROVED → ACTIVE → COMPLETED
 *                ↓                              ↓
 *             FAILED                        CANCELLED
 * </pre>
 *
 * <p>{@code FAILED} is set by US085 when DSL validation or the physical
 * test component reports errors. The plan may be edited and re-validated.</p>
 */
public enum FlightPlanStatus {

    /** Initial state when a flight plan is created. */
    DRAFT,

    /** Flight plan has passed DSL and physical validation (US085). */
    VALIDATED,

    /**
     * Validation failed — DSL errors or physical test issues found (US085).
     * The plan remains editable and can be re-submitted for validation.
     */
    FAILED,

    /** Flight plan has been approved for execution. */
    APPROVED,

    /** Flight plan is currently active (in execution). */
    ACTIVE,

    /** Flight plan has been completed. */
    COMPLETED,

    /** Flight plan has been cancelled. */
    CANCELLED
}