package pt.ipp.isep.dei.ui.console.us27;

import pt.ipp.isep.dei.controller.us27.TransitiveClosureShortestPathController;
import pt.ipp.isep.dei.domain.us13.Station;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * UI for US27 - finding one of the shortest routes between two stations,
 * which goes through an ordered list of stations chosen by the player.
 */
public class ShortestPathWithWaypointsUI {
    private final TransitiveClosureShortestPathController controller;
    private final Scanner scanner;
    
    /**
     * Creates a new UI with the specified controller.
     * 
     * @param controller the controller
     */
    public ShortestPathWithWaypointsUI(TransitiveClosureShortestPathController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Runs the UI.
     */
    public void run() throws IOException {
        System.out.println("=== Shortest Path with Waypoints ===");
        
        // Get source station
        System.out.println("\nSelect source station:");
        Station source = getStationFromList(controller.getAllStations());
        if (source == null) {
            System.out.println("No stations available.");
            return;
        }
        
        // Get destination station
        System.out.println("\nSelect destination station:");
        List<Station> availableDestinations = new ArrayList<>(controller.getAllStations());
        availableDestinations.remove(source);
        Station destination = getStationFromList(availableDestinations);
        if (destination == null) {
            System.out.println("No destination stations available.");
            return;
        }
        
        // Get waypoints
        List<Station> waypoints = new ArrayList<>();
        List<Station> availableWaypoints = new ArrayList<>(controller.getAllStations());
        availableWaypoints.remove(source);
        availableWaypoints.remove(destination);
        
        boolean addMoreWaypoints = true;
        while (addMoreWaypoints && !availableWaypoints.isEmpty()) {
            System.out.println("\nDo you want to add a waypoint? (y/n)");
            String answer = scanner.nextLine().trim().toLowerCase();
            
            if (answer.equals("y")) {
                System.out.println("\nSelect waypoint station:");
                Station waypoint = getStationFromList(availableWaypoints);
                
                if (waypoint != null) {
                    waypoints.add(waypoint);
                    availableWaypoints.remove(waypoint);
                    System.out.println("Waypoint added: " + waypoint.getName());
                } else {
                    System.out.println("No more stations available as waypoints.");
                    addMoreWaypoints = false;
                }
            } else {
                addMoreWaypoints = false;
            }
        }
        
        // Find shortest path with waypoints
        List<Station> path = controller.findShortestPathWithWaypoints(source, destination, waypoints);
        
        if (path.isEmpty()) {
            System.out.println("\nNo path found from " + source.getName() + 
                               " to " + destination.getName() + 
                               " through the specified waypoints.");
            return;
        }
        
        // Display the path
        System.out.println("\nShortest path found:");
        int totalDistance = 0;
        for (int i = 0; i < path.size(); i++) {
            System.out.println((i + 1) + ". " + path.get(i).getName() + " (" + path.get(i).getType() + ")");
        }
        
        // Generate visualization
        String pathDotFile = "shortest_path_with_waypoints.dot";
        controller.generateVisualization(path, pathDotFile);
        System.out.println("\nPath visualization generated: " + pathDotFile);
        System.out.println("You can use Graphviz to view this file, or convert it to an image using:");
        System.out.println("dot -Tpng " + pathDotFile + " -o shortest_path_with_waypoints.png");
    }
    
    /**
     * Gets a station from a list based on user input.
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