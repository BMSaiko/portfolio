package eapli.aisafe.app.backoffice.console.presentation.routemanagement;

import eapli.aisafe.airportmanagement.domain.Airport;
import eapli.aisafe.routemanagement.application.CreateFlightRouteController;
import eapli.aisafe.routemanagement.domain.FlightRoute;
import eapli.aisafe.routemanagement.dto.FlightRouteRegistrationDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console UI for the "Create Flight Route" use case (US073).
 *
 * <p>Follows the same pattern as {@code RegisterAirportUI}: no framework
 * base class, plain Scanner, void show().</p>
 */
public class CreateFlightRouteUI {

    private final CreateFlightRouteController controller;
    private final Scanner scanner;

    public CreateFlightRouteUI(final CreateFlightRouteController controller,
                               final Scanner scanner) {
        this.controller = controller;
        this.scanner    = scanner;
    }

    public void show() {
        System.out.println("\n=== Create Flight Route ===");

        // 1 — List available airports
        final List<Airport> airports = new ArrayList<>();
        System.out.println("\nAvailable airports:");
        int idx = 1;
        for (Airport a : controller.getAvailableAirports()) {
            System.out.printf("  %d. [%s] %s, %s%n",
                    idx, a.identity(), a.name(), a.country());
            airports.add(a);
            idx++;
        }

        if (airports.isEmpty()) {
            System.out.println("\nNo airports registered in the system. "
                    + "Please register airports first (US052).");
            return;
        }

        // 2 — Select origin
        final String originIata = selectAirport("Select origin airport (number): ", airports);
        if (originIata == null) return;

        // 3 — Select destination
        final String destinationIata = selectAirport("Select destination airport (number): ", airports);
        if (destinationIata == null) return;

        // 4 — Collect designator
        final String designator = readCode(
                "Route Designator (2 letters + 1-4 digits, e.g. TP123): ",
                "[A-Z]{2}[0-9]{1,4}",
                "Designator must be 2 uppercase letters followed by 1-4 digits.");

        // 5 — Delegate to controller
        try {
            final FlightRoute created = controller.createRoute(
                    new FlightRouteRegistrationDTO(designator, originIata, destinationIata));

            System.out.println("\nSuccess! Route " + created.identity()
                    + " (" + created.origin().identity()
                    + " -> " + created.destination().identity()
                    + ") created successfully.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("\nERROR: " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("\nACCESS DENIED: " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private String selectAirport(final String prompt, final List<Airport> airports) {
        while (true) {
            System.out.print(prompt);
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= airports.size()) {
                    return airports.get(choice - 1).identity().value();
                }
                System.out.println("  Please enter a number between 1 and " + airports.size() + ".");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number. Please try again.");
            }
        }
    }

    private String readCode(final String prompt, final String regex, final String errorMsg) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim().toUpperCase();
            if (value.matches(regex)) return value;
            System.out.println("  " + errorMsg);
        }
    }
}