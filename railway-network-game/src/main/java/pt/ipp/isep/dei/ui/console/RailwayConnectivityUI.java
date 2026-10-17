package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.RailwayConnectivityController;
import pt.ipp.isep.dei.controller.RailwayMaintenanceController;
import pt.ipp.isep.dei.controller.us27.TransitiveClosureShortestPathController;
import pt.ipp.isep.dei.domain.us13.RailwayLine;
import pt.ipp.isep.dei.domain.us13.Station;
import pt.ipp.isep.dei.domain.us13.StationType;
import pt.ipp.isep.dei.domain.us13.TrainType;
import pt.ipp.isep.dei.ui.console.us27.ShortestPathWithWaypointsUI;
import pt.ipp.isep.dei.ui.gui.GraphvizUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * UI for US13 - checking if a specific train can travel between stations.
 */
public class RailwayConnectivityUI {
    private final RailwayConnectivityController controller;
    private final Scanner scanner;
    private final String stationsFile;
    private final String linesFile;
    
    /**
     * Creates a new UI with the specified controller.
     * 
     * @param controller the controller
     * @param stationsFile the stations file path
     * @param linesFile the lines file path
     */
    public RailwayConnectivityUI(RailwayConnectivityController controller, String stationsFile, String linesFile) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
        this.stationsFile = stationsFile;
        this.linesFile = linesFile;
    }
    
    /**
     * Runs the UI.
     */
    public void run() {
        System.out.println("=== Railway Connectivity Checker ===");
        
        try {
            // Generate and display the network visualization
            String dotFile = "railway_network.dot";
            GraphvizUtil.generateDotFile(controller.getAllStations(), controller.getAllLines(), null, dotFile);
            System.out.println("Network visualization generated: " + dotFile);
            System.out.println("You can use Graphviz to view this file, or convert it to an image using:");
            System.out.println("dot -Tpng " + dotFile + " -o railway_network.png");
            
            showMainMenu();
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Shows the main menu with options.
     */
    private void showMainMenu() throws IOException {
        while (true) {
            System.out.println("\nSelect an option:");
            System.out.println("1. Check if a train can travel between two stations");
            System.out.println("2. Find stations of a specific type reachable from a source station");
            System.out.println("3. Find maintenance route that passes once through each railway line");
            System.out.println("4. Find shortest path with waypoints");
            System.out.println("5. Meet the dev team");
            System.out.println("6. Exit\n\n");
            
            System.out.print("Enter your choice (1-6): ");
            String input = scanner.nextLine().trim();
            
            switch (input) {
                case "1":
                    checkTrainCanTravel();
                    break;
                case "2":
                    findReachableStationsOfType();
                    break;
                case "3":
                    findMaintenanceRoute();
                    break;
                case "4":
                    findShortestPathWithWaypoints();
                    break;
                case "5":
                    meetDevTeam();
                    break;
                case "6":
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    /**
     * Find a shortest path with waypoints.
     */
    private void findShortestPathWithWaypoints() throws IOException {
        // Create the shortest path controller and UI
        TransitiveClosureShortestPathController shortestPathController = 
            new TransitiveClosureShortestPathController(stationsFile, linesFile);
        ShortestPathWithWaypointsUI shortestPathUI = new ShortestPathWithWaypointsUI(shortestPathController);
        
        // Run the shortest path UI
        shortestPathUI.run();
    }

    /**
     * Find a railway maintenance route.
     */
    private void findMaintenanceRoute() throws IOException {
        // Create the maintenance controller and UI
        RailwayMaintenanceController maintenanceController = 
            new RailwayMaintenanceController(stationsFile, linesFile);
        RailwayMaintenanceUI maintenanceUI = new RailwayMaintenanceUI(maintenanceController);
        
        // Run the maintenance UI
        maintenanceUI.run();
    }

    private void meetDevTeam(){
        DevTeamUI devTeamUI = new DevTeamUI();
        devTeamUI.run();
    }

    /**
     * Checks if a train can travel between two stations.
     */
    private void checkTrainCanTravel() throws IOException {
        // Get train type
        TrainType trainType = getTrainType();
        
        // Get list of stations reachable by this train type
        List<Station> reachableStations = filterStationsByTrainType(controller.getAllStations(), trainType);
        
        if (reachableStations.isEmpty()) {
            System.out.println("No stations available for " + trainType + " trains.");
            return;
        }
        
        // Get source station
        System.out.println("\nSelect source station:");
        Station source = getStationFromList(reachableStations);
        if (source == null) {
            System.out.println("No stations available.");
            return;
        }
        
        // Get destination station - should filter for stations reachable from source with this train type
        List<Station> potentialDestinations = filterStationsByTrainType(controller.getAllStations(), trainType);
        // Remove the source station from potential destinations
        potentialDestinations.removeIf(s -> s.equals(source));
        
        System.out.println("\nSelect destination station:");
        Station destination = getStationFromList(potentialDestinations);
        if (destination == null) {
            System.out.println("No destination stations available.");
            return;
        }
        
        // Check if travel is possible
        boolean canTravel = controller.canTravel(source, destination, trainType);
        System.out.println("\nCan a " + trainType + " train travel from " + source.getName() + 
                        " to " + destination.getName() + "? " + (canTravel ? "YES" : "NO"));
        
        if (canTravel) {
            List<Station> path = controller.getPath(source, destination, trainType);
            System.out.println("\nPath: " + path);
            
            // Generate visualization with the path highlighted
            String pathDotFile = "railway_path.dot";
            GraphvizUtil.generateDotFile(controller.getAllStations(), controller.getAllLines(), path, pathDotFile);
            System.out.println("Path visualization generated: " + pathDotFile);
            System.out.println("You can use Graphviz to view this file, or convert it to an image using:");
            System.out.println("dot -Tpng " + pathDotFile + " -o railway_path.png");
        }
    }
    
    /**
     * Filter stations based on train type accessibility
    */
    private List<Station> filterStationsByTrainType(Collection<Station> allStations, TrainType trainType) {
        List<Station> reachableStations = new ArrayList<>();
        
        for (Station station : allStations) {
            // For each station, check if there's at least one line that this train type can operate on
            List<RailwayLine> lines = controller.getAllLines().stream()
                .filter(line -> line.getSource().equals(station) || line.getDestination().equals(station))
                .filter(line -> trainType.canOperateOn(line))
                .collect(Collectors.toList());
            
            if (!lines.isEmpty() || trainType != TrainType.ELECTRIC) {
                // Add station if it has at least one compatible line or if train is not ELECTRIC 
                // (steam and diesel can operate on any station, even if no lines yet)
                reachableStations.add(station);
            }
        }
        
        return reachableStations;
    }

    /**
     * Gets a station from a specific list of stations based on user input.
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

    /**
     * Finds stations of a specific type reachable from a source station.
    */
    private void findReachableStationsOfType() throws IOException {
        // Get train type
        TrainType trainType = getTrainType();
        
        // Get list of stations reachable by this train type
        List<Station> reachableStations = filterStationsByTrainType(controller.getAllStations(), trainType);
        
        if (reachableStations.isEmpty()) {
            System.out.println("No stations available for " + trainType + " trains.");
            return;
        }
        
        // Get source station
        System.out.println("\nSelect source station:");
        Station source = getStationFromList(reachableStations);
        if (source == null) {
            System.out.println("No stations available.");
            return;
        }
        
        // Get station type to find
        StationType stationType = getStationType("target");
        
        // Find reachable stations of the specified type
        List<Station> reachableStationsOfType = controller.findReachableStationsOfType(source, stationType, trainType);
        
        if (reachableStationsOfType.isEmpty()) {
            System.out.println("\nNo " + stationType + " stations reachable from " + source.getName() + 
                            " using a " + trainType + " train.");
            return;
        }
        
        System.out.println("\nReachable " + stationType + " stations from " + source.getName() + 
                        " using a " + trainType + " train:");
        for (Station station : reachableStationsOfType) {
            System.out.println("- " + station.getName());
        }
        
        // Generate visualization with the reachable stations highlighted
        String pathDotFile = "reachable_stations.dot";
        // Add the source station to the list of highlighted stations
        List<Station> highlightedStations = new java.util.ArrayList<>(reachableStationsOfType);
        highlightedStations.add(source);
        GraphvizUtil.generateDotFile(controller.getAllStations(), controller.getAllLines(), 
                                    highlightedStations, pathDotFile);
        System.out.println("Reachable stations visualization generated: " + pathDotFile);
        System.out.println("You can use Graphviz to view this file, or convert it to an image using:");
        System.out.println("dot -Tpng " + pathDotFile + " -o reachable_stations.png");
    }
    
    /**
     * Gets the train type from user input.
     */
    private TrainType getTrainType() {
        System.out.println("\nSelect train type:");
        System.out.println("1. Steam");
        System.out.println("2. Diesel");
        System.out.println("3. Electric");
        
        while (true) {
            System.out.print("Enter your choice (1-3): ");
            String input = scanner.nextLine().trim();
            
            switch (input) {
                case "1":
                    return TrainType.STEAM;
                case "2":
                    return TrainType.DIESEL;
                case "3":
                    return TrainType.ELECTRIC;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    /**
     * Gets a station type from user input.
     */
    private StationType getStationType(String stationRole) {
        System.out.println("\nSelect " + stationRole + " station type:");
        System.out.println("1. Depot");
        System.out.println("2. Station");
        System.out.println("3. Terminal");
        
        while (true) {
            System.out.print("Enter your choice (1-3): ");
            String input = scanner.nextLine().trim();
            
            switch (input) {
                case "1":
                    return StationType.DEPOT;
                case "2":
                    return StationType.STATION;
                case "3":
                    return StationType.TERMINAL;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    /**
     * Gets a station from user input.
     */
    private Station getStation() {
        List<Station> stations = controller.getAllStations();
        
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