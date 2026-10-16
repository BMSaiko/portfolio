package eapli.aisafe.app.backoffice.console.presentation.flightplanmanagement;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.flightplanmanagement.application.FlightPlanService;
import eapli.aisafe.flightplanmanagement.domain.FlightPlan;

import java.util.List;

/**
 * Console UI for listing all flight plans.
 */
public class ListFlightPlansUI extends AbstractUI {

    private final FlightPlanService flightPlanService;

    public ListFlightPlansUI(final FlightPlanService flightPlanService) {
        this.flightPlanService = flightPlanService;
    }

    @Override
    protected boolean doShow() {
        System.out.println("\n=== List Flight Plans ===");

        try {
            final List<FlightPlan> flightPlans = flightPlanService.listFlightPlans();

            if (flightPlans.isEmpty()) {
                System.out.println("No flight plans registered.");
            } else {
                System.out.println("Found " + flightPlans.size() + " flight plan(s):\n");
                for (final FlightPlan fp : flightPlans) {
                    System.out.println("  " + fp);
                }
            }
        } catch (SecurityException e) {
            System.out.println("\nACCESS DENIED: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nERROR: " + e.getMessage());
        }

        return false;
    }
}
