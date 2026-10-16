package eapli.aisafe.app.backoffice.console.presentation.flightplanmanagement;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.flightplanmanagement.application.CreateFlightPlanController;
import eapli.aisafe.flightplanmanagement.domain.FlightPlan;
import eapli.aisafe.flightplanmanagement.dto.FlightPlanRegistrationDTO;
import eapli.aisafe.routemanagement.domain.FlightRoute;
import eapli.framework.io.util.Console;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Console UI for creating a flight plan manually (US080).
 *
 * <p>Prompts the Pilot to select an active route, enter an aircraft
 * registration, a pilot email, a departure date/time, and fuel quantity.
 * Delegates all business-rule enforcement to
 * {@link CreateFlightPlanController}.</p>
 */
public class CreateFlightPlanUI extends AbstractUI {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final CreateFlightPlanController controller;

    public CreateFlightPlanUI(final CreateFlightPlanController controller) {
        this.controller = controller;
    }

    @Override
    protected boolean doShow() {
        System.out.println("\n=== Create Flight Plan ===");

        try {
            // 1 ── Show available active routes
            final List<FlightRoute> routes = new ArrayList<>();
            controller.activeRoutes().forEach(routes::add);

            final List<FlightRoute> available = routes.stream()
                    .filter(this::canStillBeSelected)
                    .toList();

            if (available.isEmpty()) {
                System.out.println("No available routes found. "
                        + "Please register a route first (US073).");
                return false;
            }

            System.out.println("\nAvailable routes:");
            for (int i = 0; i < available.size(); i++) {
                final FlightRoute r = available.get(i);
                System.out.printf("  %d. %s  (%s -> %s)  [%s]%s%n",
                        i + 1,
                        r.identity(),
                        r.origin().identity(),
                        r.destination().identity(),
                        r.company().iataCode(),
                        r.isActive() ? "" : " deactivated from " + r.deactivatedFrom());
            }
            System.out.println("  0. Cancel");

            final int routeChoice = Console.readInteger("\nSelect route: ");
            if (routeChoice <= 0 || routeChoice > available.size()) {
                System.out.println("Cancelled.");
                return false;
            }
            final String routeDesignator = available.get(routeChoice - 1).identity().value();

            // 2 ── Aircraft registration
            final String aircraftReg =
                    Console.readLine("Aircraft registration (e.g. CS-TKX): ").trim();
            if (aircraftReg.isBlank()) {
                System.out.println("Cancelled — aircraft registration is required.");
                return false;
            }

            // 3 ── Pilot email
            final String pilotEmail =
                    Console.readLine("Pilot e-mail: ").trim();
            if (pilotEmail.isBlank()) {
                System.out.println("Cancelled — pilot e-mail is required.");
                return false;
            }

            // 4 ── Departure date/time
            LocalDateTime departureDateTime = null;
            while (departureDateTime == null) {
                final String raw =
                        Console.readLine("Departure date/time (yyyy-MM-dd HH:mm): ").trim();
                try {
                    departureDateTime = LocalDateTime.parse(raw, DATE_FMT);
                } catch (DateTimeParseException e) {
                    System.out.println(
                            "  Invalid format. Please use yyyy-MM-dd HH:mm (e.g. 2026-07-01 10:00).");
                }
            }

            // 5 ── Fuel quantity
            final double fuel = Console.readDouble("Estimated fuel on board (kg): ");

            // 6 ── Build DTO and delegate to controller
            final FlightPlanRegistrationDTO dto = new FlightPlanRegistrationDTO(
                    routeDesignator, aircraftReg, pilotEmail, departureDateTime, fuel);

            final FlightPlan created = controller.createFlightPlan(dto);

            System.out.println("\nFlight plan created successfully!");
            System.out.println("  " + created);

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("\nERROR: " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("\nACCESS DENIED: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nUNEXPECTED ERROR: " + e.getMessage());
        }

        return false;
    }

    private boolean canStillBeSelected(final FlightRoute route) {
        return route.isActive()
                || (route.deactivatedFrom() != null
                && route.deactivatedFrom().isAfter(LocalDate.now()));
    }
}
