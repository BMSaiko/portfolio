package pt.isep.dei.esinf.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import pt.isep.dei.esinf.domain.freight.Freight;
import pt.isep.dei.esinf.domain.freight.FreightRoute;
import pt.isep.dei.esinf.repository.FreightRepository;
import pt.isep.dei.esinf.service.FreightRoutePlannerService;
import pt.isep.dei.esinf.ui.FreightManagerMenu;

/**
 * Controller for Freight Manager menu and operations
 */
public class FreightManagerMenuController {
    private final Scanner scanner;
    private FreightRoutePlannerService routePlannerService;
    private FreightRepository freightRepository;

    public FreightManagerMenuController(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Display menu and handle user input
     */
    public void show() throws Exception {
        String url = "jdbc:oracle:thin:@Pinto:1521:orcl";
        String username = "SYS as sysdba";
        String password = "database";

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            this.freightRepository = new FreightRepository(conn);
            this.routePlannerService = new FreightRoutePlannerService(freightRepository);

            while (true) {
                new FreightManagerMenu().run();
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1" -> handlePlanRoute();
                    case "2" -> handleViewAllFreights();
                    case "3" -> handleViewPendingFreights();
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("Invalid option, please try again!");
                }
            }
        } catch (Exception e) {
            System.err.println("✗ Error connecting to database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * View pending freights (not yet assigned to trains)
     */
    private void handleViewPendingFreights() {
        try {
            List<Freight> freights = routePlannerService.getPendingFreights();

            if (freights.isEmpty()) {
                System.out.println("\n✓ No pending freights found.");
                return;
            }

            System.out.println("\n===================================================================================");
            System.out.println("                            PENDING FREIGHTS                                      ");
            System.out.println("===================================================================================");
            System.out.println("   ID       Date            Origin                  Destination                   ");
            System.out.println("===================================================================================");

            for (Freight freight : freights) {
                System.out.printf(" %-6d   %-10s   %-23s   %-33s%n",
                    freight.getFreightId(),
                    freight.getFreightDate(),
                    truncate(freight.getOrigin().getName(), 23),
                    truncate(freight.getDestination().getName(), 33)
                );
            }

            System.out.println("===================================================================================");
            System.out.printf("\nTotal pending freights: %d%n", freights.size());

        } catch (Exception e) {
            System.err.println("✗ Error retrieving pending freights: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * View all freights (including assigned ones)
     */
    private void handleViewAllFreights() {
        try {
            List<Freight> freights = freightRepository.getAllFreights();
            routePlannerService.displayFreights(freights);
        } catch (Exception e) {
            System.err.println("✗ Error retrieving freights: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handlePlanRoute() {
        System.out.println("\nSelect route planning option:");
        System.out.println("1. Plan simple route for a single freight");
        System.out.println("2. Plan complex route for multiple freights");
        System.out.print("Enter choice (1-2): ");

        String choice = scanner.nextLine();

        switch (choice) {
            case "1" -> handlePlanRouteSimple();
            case "2" -> handlePlanComplexRoute();
            default -> System.out.println("Invalid option, please try again!");
        }
    }

    /**
     * Plan a simple route for a single freight
     */
    private void handlePlanRouteSimple() {
        try {
            System.out.print("\nEnter Freight ID to plan route: ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("✗ Freight ID cannot be empty.");
                return;
            }

            int freightId = Integer.parseInt(input);
            Freight freight = routePlannerService.getFreightById(freightId);

            if (freight == null) {
                System.out.println("✗ Freight not found.");
                return;
            }

            // Check if freight is pending
            if (!routePlannerService.isFreightPending(freightId)) {
                System.out.println("✗ Freight #" + freightId + " is already assigned to a train.");
                return;
            }

            System.out.println("\n✓ Planning simple route for freight #" + freightId + "...");
            FreightRoute route = routePlannerService.planSimpleRoute(freight);
            routePlannerService.displayRoute(route);

            // Ask if user wants to assign this route
            handleRouteAssignment(route);

        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid freight ID format. Please enter a number.");
        } catch (Exception e) {
            System.err.println("✗ Error planning route: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Plan a complex route for multiple freights
     */
    private void handlePlanComplexRoute() {
        try {
            List<Freight> pendingFreights = routePlannerService.getPendingFreights();

            if (pendingFreights.isEmpty()) {
                System.out.println("\n✗ No pending freights available.");
                return;
            }

            routePlannerService.displayFreights(pendingFreights);

            System.out.print("\nEnter Freight IDs (comma-separated, e.g., 2001,2002,2003): ");
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                System.out.println("✗ No freight IDs entered.");
                return;
            }

            String[] idStrings = input.split(",");
            List<Freight> selectedFreights = new ArrayList<>();

            for (String idStr : idStrings) {
                try {
                    int id = Integer.parseInt(idStr.trim());
                    Freight freight = routePlannerService.getFreightById(id);

                    if (freight == null) {
                        System.out.println("⚠ Warning: Freight #" + id + " not found. Skipping.");
                    } else if (!routePlannerService.isFreightPending(id)) {
                        System.out.println("⚠ Warning: Freight #" + id + " is already assigned. Skipping.");
                    } else {
                        selectedFreights.add(freight);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("⚠ Warning: Invalid freight ID '" + idStr.trim() + "'. Skipping.");
                }
            }

            if (selectedFreights.isEmpty()) {
                System.out.println("✗ No valid pending freights selected.");
                return;
            }

            if (selectedFreights.size() < 2) {
                System.out.println("⚠ Complex routes require at least 2 freights. Use 'Plan simple route' for single freight.");
                return;
            }

            System.out.println("\n✓ Planning complex route for " + selectedFreights.size() + " freights...");
            FreightRoute route = routePlannerService.planComplexRoute(selectedFreights);
            routePlannerService.displayRoute(route);

            // Ask if user wants to assign this route
            handleRouteAssignment(route);

        } catch (Exception e) {
            System.err.println("✗ Error planning complex route: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handle route assignment to a train
     */
    private void handleRouteAssignment(FreightRoute route) {
        try {
            System.out.print("\nDo you want to assign this route to a train? (y/n): ");
            String response = scanner.nextLine().trim().toLowerCase();

            if (!response.equals("y") && !response.equals("yes")) {
                System.out.println("✓ Route not assigned.");
                return;
            }

            // Validate all freights are still pending
            List<Integer> alreadyAssigned = routePlannerService.validateRoutePendingStatus(route);
            if (!alreadyAssigned.isEmpty()) {
                System.out.println("✗ Cannot assign route: The following freights are already assigned:");
                alreadyAssigned.forEach(id -> System.out.println("  - Freight #" + id));
                return;
            }

            System.out.print("Enter Train ID to assign this route: ");
            String trainIdInput = scanner.nextLine().trim();

            if (trainIdInput.isEmpty()) {
                System.out.println("✗ Train ID cannot be empty.");
                return;
            }

            int trainId = Integer.parseInt(trainIdInput);

            // Assign route
            int assignedCount = routePlannerService.assignRouteToTrain(route, trainId);

            System.out.println("\n=======================================================================");
            System.out.println("                    ROUTE ASSIGNMENT SUCCESSFUL                       ");
            System.out.println("=======================================================================");
            System.out.printf(" Train ID: %-58d%n", trainId);
            System.out.printf(" Route: %-61s%n", truncate(route.getRouteName(), 61));
            System.out.printf(" Freights Assigned: %-49d%n", assignedCount);
            System.out.println("=======================================================================");
            System.out.println(" Status: All selected freights are now removed from pending          ");
            System.out.println("=======================================================================");

        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid train ID format. Please enter a number.");
        } catch (Exception e) {
            System.err.println("✗ Error assigning route: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Truncate string to max length with ellipsis
     */
    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }
}

