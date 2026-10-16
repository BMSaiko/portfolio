package pt.ipp.isep.dei;

import pt.ipp.isep.dei.controller.RailwayConnectivityController;
import pt.ipp.isep.dei.controller.RailwayMaintenanceController;
import pt.ipp.isep.dei.domain.us13.Station;
import pt.ipp.isep.dei.domain.us13.TrainType;
import pt.ipp.isep.dei.ui.console.RailwayConnectivityUI;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Main class for the Railway Network application.
 */
public class Main {
    
    /**
     * Main method to start the application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        // Check if this is a performance test
        if (args.length >= 4 && "PERFORMANCE_TEST".equals(args[0])) {
            executePerformanceTest(args[1], args[2], args[3], 
                                  args.length > 4 ? args[4] : null);
            return;
        }
        
        // Normal application flow
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Railway Network Application ===");
        
        System.out.println("Select a scenario:");
        System.out.println("1. Scenario 1");
        System.out.println("2. Scenario 2");
        System.out.println("3. Scenario 3");
        System.out.println("4. Scenario 4");
        System.out.print("Enter your choice (1-4): ");
        
        String choice = scanner.nextLine().trim();
        
        String stationsFile;
        String linesFile;
        
        switch (choice) {
            case "2":
                stationsFile = "scenario2_stations.csv";
                linesFile = "scenario2_lines.csv";
                break;
            case "3":
                stationsFile = "scenario3_stations.csv";
                linesFile = "scenario3_lines.csv";
                break;
            case "4":
                stationsFile = "scenario4_stations.csv";
                linesFile = "scenario4_lines.csv";
                break;
            default:
                stationsFile = "scenario1_stations.csv";
                linesFile = "scenario1_lines.csv";
                break;
        }
        
        System.out.println("\nLoading scenario from:");
        System.out.println(" - Stations: " + stationsFile);
        System.out.println(" - Lines: " + linesFile);
        
        try {
            // Initialize the railway connectivity controller and UI
            RailwayConnectivityController controller = new RailwayConnectivityController(stationsFile, linesFile);
            RailwayConnectivityUI ui = new RailwayConnectivityUI(controller, stationsFile, linesFile);
            
            // Run the UI
            ui.run();
        } catch (IOException e) {
            System.err.println("Error loading railway network: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
    
    /**
     * Execute performance test for US29 asymptotic analysis
     */
    private static void executePerformanceTest(String stationsFile, String linesFile, 
                                             String testType, String param) {
        try {
            long startTime = System.nanoTime();
            boolean result = false;
            
            if ("US13".equals(testType)) {
                // Test US13 - Railway Connectivity
                RailwayConnectivityController controller = 
                    new RailwayConnectivityController(stationsFile, linesFile);
                
                List<Station> stations = controller.getAllStations();
                if (stations.size() >= 2) {
                    Station source = stations.get(0);
                    Station destination = stations.get(stations.size() - 1);
                    
                    // Parse train type
                    TrainType trainType;
                    try {
                        trainType = TrainType.valueOf(param != null ? param.toUpperCase() : "DIESEL");
                    } catch (IllegalArgumentException e) {
                        trainType = TrainType.DIESEL;
                    }
                    
                    result = controller.canTravel(source, destination, trainType);
                } else {
                    result = true; // Trivial case
                }
                
            } else if ("US14".equals(testType)) {
                // Test US14 - Maintenance Route Planning
                RailwayMaintenanceController controller = 
                    new RailwayMaintenanceController(stationsFile, linesFile);
                
                boolean electrifiedOnly = param != null ? Boolean.parseBoolean(param) : false;
                List<Station> route = controller.getMaintenanceRoute(electrifiedOnly);
                result = !route.isEmpty();
            }
            
            long endTime = System.nanoTime();
            double executionTime = (endTime - startTime) / 1_000_000.0; // Convert to milliseconds
            
            // Output results for Python script to parse
            System.out.println("EXECUTION_TIME:" + executionTime);
            System.out.println("RESULT:" + result);
            System.err.println("Performance test completed: " + testType + " in " + executionTime + "ms");
            
        } catch (Exception e) {
            System.err.println("Performance test error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}