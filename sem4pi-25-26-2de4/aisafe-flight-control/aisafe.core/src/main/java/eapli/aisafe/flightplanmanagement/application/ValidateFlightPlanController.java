package eapli.aisafe.flightplanmanagement.application;

import eapli.aisafe.flightplanmanagement.domain.FlightPlan;
import eapli.aisafe.flightplanmanagement.domain.FlightPlanId;
import eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository;
import pt.isep.sem4pi.lprog.application.US085Result;
import pt.isep.sem4pi.lprog.application.ValidateFlightPlanService;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Application controller for US085 — Test/Validate a Flight Plan (Eixo 2 — EAPLI).
 *
 * <p>Flow (aligned with professor's clarification):</p>
 * <ol>
 *   <li>Pilot selects a FlightPlan by ID.</li>
 *   <li>Controller reads the DSL already stored in the FlightPlan entity
 *       (stored there by US080 or US081).</li>
 *   <li>Delegates to {@link ValidateFlightPlanService} — DSL validation (ANTLR)
 *       + physical test (C component).</li>
 *   <li>Updates FlightPlan status: VALIDATED on success, FAILED otherwise
 *       (only when DSL was parseable).</li>
 * </ol>
 */
public class ValidateFlightPlanController {

    private static final Logger LOGGER =
            Logger.getLogger(ValidateFlightPlanController.class.getName());

    private final ValidateFlightPlanService validateService;
    private final FlightPlanRepository flightPlanRepository;

    public ValidateFlightPlanController(final FlightPlanRepository flightPlanRepository) {
        this.validateService      = new ValidateFlightPlanService();
        this.flightPlanRepository = flightPlanRepository;
    }

    /**
     * Returns all flight plans — for the UI to present a selection list.
     */
    public Iterable<FlightPlan> listFlightPlans() {
        return flightPlanRepository.findAll();
    }

    /**
     * Validates the DSL already stored in the FlightPlan identified by
     * {@code flightPlanId}.
     *
     * <p>This is the primary entry point for US085. The Pilot selects a plan
     * by ID; the DSL comes from the entity itself (stored by US080/US081).</p>
     *
     * @param flightPlanId the ID of the plan to validate
     * @return the full validation result — never {@code null}
     */
    public US085Result validateById(final String flightPlanId) {
        if (flightPlanId == null || flightPlanId.isBlank())
            return US085Result.ioError("Flight plan ID must not be empty.");

        Optional<FlightPlan> optional =
                flightPlanRepository.findById(FlightPlanId.of(flightPlanId));

        if (optional.isEmpty())
            return US085Result.ioError("Flight plan not found: " + flightPlanId);

        FlightPlan plan = optional.get();
        String dslContent = plan.dslContent();

        if (dslContent == null || dslContent.isBlank())
            return US085Result.ioError(
                    "Flight plan " + flightPlanId
                            + " has no DSL stored. Please update the plan with a DSL first (US080/US081).");

        return runAndPersist(plan, dslContent);
    }

    /**
     * Validates a DSL string directly, updating the given FlightPlan.
     *
     * <p>Fallback entry point for cases where the Pilot provides a DSL file
     * (e.g. if the plan was created without DSL via US080 and the Pilot wants
     * to supply one now). The DSL is stored in the entity after validation.</p>
     *
     * @param dslContent   raw DSL source text
     * @param flightPlanId plan to update, or {@code null} for standalone check
     * @return the full validation result — never {@code null}
     */
    public US085Result validate(final String dslContent, final String flightPlanId) {
        if (dslContent == null || dslContent.isBlank())
            return US085Result.ioError("DSL content must not be empty.");

        // No ID → standalone check, no persistence
        if (flightPlanId == null || flightPlanId.isBlank())
            return validateService.validateString(dslContent);

        Optional<FlightPlan> optional =
                flightPlanRepository.findById(FlightPlanId.of(flightPlanId));

        if (optional.isEmpty())
            return US085Result.ioError("Flight plan not found: " + flightPlanId);

        FlightPlan plan = optional.get();

        // Store the DSL on the entity (so future calls to validateById() work)
        try {
            plan.storeDsl(dslContent);
        } catch (IllegalArgumentException e) {
            return US085Result.ioError(e.getMessage());
        }

        return runAndPersist(plan, dslContent);
    }

    /** Validates DSL without updating any persisted entity. */
    public US085Result validateOnly(final String dslContent) {
        if (dslContent == null || dslContent.isBlank())
            return US085Result.ioError("DSL content must not be empty.");
        return validateService.validateString(dslContent);
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private US085Result runAndPersist(final FlightPlan plan, final String dslContent) {
        // Run Eixo 1 pipeline
        US085Result result = validateService.validateString(dslContent);

        // Persist only when DSL was parseable
        boolean dslParsed = result.getState() != US085Result.State.IO_ERROR
                && result.getState() != US085Result.State.DSL_FAILURE;

        if (dslParsed) {
            try {
                if (result.isSuccess()) {
                    plan.validate();   // → VALIDATED
                } else {
                    plan.invalidate(); // → FAILED
                }
                flightPlanRepository.save(plan);
            } catch (Exception e) {
                LOGGER.log(Level.WARNING,
                        "Failed to persist validation result for plan "
                                + plan.identity(), e);
            }
        }

        return result;
    }
}