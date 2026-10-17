package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.RailwayMaintenanceController;
import pt.ipp.isep.dei.domain.us13.RailwayLine;
import pt.ipp.isep.dei.domain.us13.Station;
import pt.ipp.isep.dei.ui.gui.GraphvizUtil;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * UI for finding a maintenance route that passes once through each railway line.
 */
public class RailwayMaintenanceUI {
    private final RailwayMaintenanceController controller;
    private final Scanner scanner;

    /**
     * Creates a new UI with the specified controller.
     *
     * @param controller the controller
     */
    public RailwayMaintenanceUI(RailwayMaintenanceController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Runs the UI.
     */
    public void run() throws IOException {
        System.out.println("=== Railway Maintenance Route Finder ===");

        // Step 1: Choose whether to include all lines or only electrified lines
        System.out.println("\n=== Step 1: Select Lines to Maintain ===");
        boolean onlyElectrified = getMaintenanceOption();

        // Step 2: Choose the origin station
        System.out.println("\n=== Step 2: Select Origin Station ===");
        List<Station> possibleStartStations = controller.getPossibleStartStations(onlyElectrified);

        if (possibleStartStations.isEmpty()) {
            System.out.println("No valid start stations found for a maintenance route.");
            return;
        }

        Station startStation = getStationFromList(possibleStartStations);
        if (startStation == null) {
            System.out.println("Invalid selection. Returning to main menu.");
            return;
        }

        // Step 3: Choose the destination station
        System.out.println("\n=== Step 3: Select Destination Station ===");
        List<Station> possibleEndStations = controller.getPossibleEndStations(startStation, onlyElectrified);

        if (possibleEndStations.isEmpty()) {
            System.out.println("No valid end stations found for a maintenance route starting from " + startStation.getName() + ".");
            return;
        }

        Station endStation = getStationFromList(possibleEndStations);
        if (endStation == null) {
            System.out.println("Invalid selection. Returning to main menu.");
            return;
        }

        // Step 4: Generate and display the maintenance route
        System.out.println("\n=== Step 4: Generating Maintenance Route ===");
        List<Station> route = controller.getMaintenanceRoute(startStation, endStation, onlyElectrified);

        if (route.isEmpty()) {
            System.out.println("No valid maintenance route found from " + startStation.getName() +
                    " to " + endStation.getName() + ".");
            return;
        }

        displayMaintenanceRoute(route, onlyElectrified);
    }

    /**
     * Displays the maintenance route.
     *
     * @param route the route
     * @param onlyElectrified whether only electrified lines were considered
     */
    private void displayMaintenanceRoute(List<Station> route, boolean onlyElectrified) throws IOException {
        System.out.println("\nMaintenance Route:");
        for (int i = 0; i < route.size(); i++) {
            System.out.println((i + 1) + ". " + route.get(i).getName() + " (" + route.get(i).getType() + ")");
        }

        // Generate visualization of the route
        String routeDotFile = onlyElectrified ? "electrified_maintenance_route.dot" : "maintenance_route.dot";
        GraphvizUtil.generateDotFile(controller.getAllStations(), controller.getAllLines(), route, routeDotFile);
        System.out.println("\nMaintenance route visualization generated: " + routeDotFile);
        System.out.println("You can use Graphviz to view this file, or convert it to an image using:");
        System.out.println("dot -Tpng " + routeDotFile + " -o maintenance_route.png");
    }

    /**
     * Gets the maintenance option from the user.
     *
     * @return true if only electrified lines should be considered, false otherwise
     */
    private boolean getMaintenanceOption() {
        System.out.println("Select maintenance option:");
        System.out.println("1. Maintain all railway lines");
        System.out.println("2. Maintain only electrified railway lines");

        while (true) {
            System.out.print("Enter your choice (1-2): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    return false;
                case "2":
                    return true;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Gets a station from a list of stations.
     *
     * @param stations the list of stations
     * @return the selected station, or null if no selection was made
     */
    private Station getStationFromList(List<Station> stations) {
        if (stations.isEmpty()) {
            return null;
        }

        System.out.println("Available stations:");
        for (int i = 0; i < stations.size(); i++) {
            System.out.println((i + 1) + ". " + stations.get(i).getName() +
                    " (" + stations.get(i).getType() + ")");
        }

        while (true) {
            System.out.print("Enter your choice (1-" + stations.size() + "): ");
            String input = "";
            try {
                input = scanner.nextLine().trim();
                int choice = Integer.parseInt(input);
                if (choice >= 1 && choice <= stations.size()) {
                    return stations.get(choice - 1);
                }
            } catch (NumberFormatException e) {
                // Ignore parsing errors
            } catch (NoSuchElementException e) {
                // Handle case where there's no more input
                System.out.println("Error reading input. Please try again.");
                scanner.reset();
                if (scanner.hasNextLine()) {
                    scanner.nextLine(); // Clear the buffer
                }
                continue;
            }
            System.out.println("Invalid choice. Please try again.");
        }
    }
}