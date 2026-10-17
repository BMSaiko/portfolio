package eapli.aisafe.app.backoffice.console.presentation.flightplanmanagement;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.flightplanmanagement.application.ValidateFlightPlanController;
import eapli.aisafe.flightplanmanagement.domain.FlightPlan;
import eapli.framework.io.util.Console;
import pt.isep.sem4pi.lprog.application.US085Result;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Console UI for US085 — Test/Validate a Flight Plan.
 *
 * <p>Primary flow (aligned with professor's clarification):
 * the Pilot selects an existing FlightPlan by ID; the DSL already
 * stored in it is used for validation.</p>
 *
 * <p>Fallback flow: if the selected plan has no DSL stored yet
 * (e.g. created via US080 without DSL), the Pilot may provide
 * a .dsl file path to supply it.</p>
 */
public class ValidateFlightPlanUI extends AbstractUI {

    private final ValidateFlightPlanController controller;

    public ValidateFlightPlanUI(final ValidateFlightPlanController controller) {
        this.controller = controller;
    }

    @Override
    protected boolean doShow() {
        System.out.println("\n=== Validate Flight Plan (US085) ===");

        // ── Step 1: List existing flight plans ────────────────────────────
        System.out.println("\nAvailable flight plans:");
        System.out.printf("  %-36s  %-6s  %-6s  %-12s%n",
                "ID", "FROM", "TO", "STATUS");
        System.out.println("  " + "-".repeat(70));

        boolean anyFound = false;
        for (FlightPlan plan : controller.listFlightPlans()) {
            System.out.printf("  %-36s  %-6s  %-6s  %-12s%n",
                    plan.identity().value(),
                    plan.departureAirport(),
                    plan.arrivalAirport(),
                    plan.status());
            anyFound = true;
        }

        if (!anyFound) {
            System.out.println("  (no flight plans found)");
            return false;
        }

        // ── Step 2: Pilot selects a plan by ID ───────────────────────────
        final String planId = Console.readLine("\nFlight Plan ID to validate: ");
        if (planId == null || planId.isBlank()) {
            System.out.println("Cancelled.");
            return false;
        }

        // ── Step 3: Try to validate using stored DSL ─────────────────────
        System.out.println("\nRunning validation...");

        try {
            US085Result result = controller.validateById(planId.trim());

            // If no DSL is stored, offer to supply a file
            if (result.getState() == US085Result.State.IO_ERROR
                    && result.getErrorMessage() != null
                    && result.getErrorMessage().contains("no DSL stored")) {

                System.out.println("\n  This plan has no DSL stored yet.");
                System.out.println("  Provide a .dsl file to associate DSL with this plan.");
                final String pathInput = Console.readLine("  Path to DSL file (or blank to cancel): ");

                if (pathInput == null || pathInput.isBlank()) {
                    System.out.println("Cancelled.");
                    return false;
                }

                Path dslPath = Paths.get(pathInput.trim());
                if (!Files.exists(dslPath)) {
                    System.out.println("File not found: " + dslPath.toAbsolutePath());
                    return false;
                }

                String dslContent;
                try {
                    dslContent = Files.readString(dslPath);
                } catch (IOException e) {
                    System.out.println("Could not read file: " + e.getMessage());
                    return false;
                }

                // Re-run with the provided DSL (also stores it in the entity)
                result = controller.validate(dslContent, planId.trim());
            }

            // ── Step 4: Display result ─────────────────────────────────
            printResult(result, planId.trim());

        } catch (SecurityException e) {
            System.out.println("\nACCESS DENIED: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nUNEXPECTED ERROR: " + e.getMessage());
        }

        System.out.println();
        return false;
    }

    private void printResult(final US085Result result, final String planId) {
        System.out.println();
        if (result.isSuccess()) {
            System.out.println("  [SUCCESS] Flight plan passed all validation checks.");
            System.out.println("  Flight plan " + planId + " status updated to VALIDATED.");
        } else {
            switch (result.getState()) {
                case DSL_FAILURE -> {
                    System.out.printf("  [FAILED] DSL validation — %d error(s):%n",
                            result.getDslErrors().size());
                    result.getDslErrors().forEach(e ->
                            System.out.printf("    • [%s L%d:%d] %s%n",
                                    e.getType(), e.getLine(),
                                    e.getColumn(), e.getMessage()));
                }
                case PHYSICAL_FAILURE -> {
                    System.out.printf("  [FAILED] Physical test — %d issue(s):%n",
                            result.getPhysicalIssues().size());
                    result.getPhysicalIssues().forEach(i ->
                            System.out.printf("    • [%s] %s%n",
                                    i.getField(), i.getMessage()));
                    System.out.println("  Flight plan " + planId + " status updated to FAILED.");
                }
                case EXECUTION_ERROR ->
                        System.out.println("  [ERROR] Could not run C tester: "
                                + result.getErrorMessage());
                case IO_ERROR ->
                        System.out.println("  [ERROR] " + result.getErrorMessage());
                default ->
                        System.out.println("  [FAILED] Validation failed.");
            }
        }
    }
}