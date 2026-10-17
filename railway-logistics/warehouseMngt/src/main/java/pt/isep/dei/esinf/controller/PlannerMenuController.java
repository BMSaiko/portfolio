package pt.isep.dei.esinf.controller;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import pt.isep.dei.esinf.domain.enums.EligibilityMode;
import pt.isep.dei.esinf.domain.enums.PackingHeuristic;
import pt.isep.dei.esinf.domain.railway.TrackConnection;
import pt.isep.dei.esinf.domain.results.AllocationResult;
import pt.isep.dei.esinf.domain.results.HubScoreResult;
import pt.isep.dei.esinf.domain.stations.Station;
import pt.isep.dei.esinf.domain.stations.StationFilter;
import pt.isep.dei.esinf.domain.warehouse.Bay;
import pt.isep.dei.esinf.domain.warehouse.Box;
import pt.isep.dei.esinf.domain.warehouse.Warehouse;
import pt.isep.dei.esinf.repository.RailwayNetworkRepository;
import pt.isep.dei.esinf.service.StationSearchService;
import pt.isep.dei.esinf.service.data.DataInitializationService;
import pt.isep.dei.esinf.service.railway.HubCentralityAnalysisService;
import pt.isep.dei.esinf.service.railway.MinimalBackboneService;
import pt.isep.dei.esinf.service.warehouse.WarehouseManagementService;
import pt.isep.dei.esinf.ui.PlannerMenu;
import pt.isep.dei.esinf.ui.SearchStationsMenu;
import pt.isep.dei.esinf.utils.graph.Edge;
import pt.isep.dei.esinf.utils.interfaces.Graph;

/**
 * Controller class responsible for handling the main menu and user interactions
 */
public class PlannerMenuController {

    private final Scanner scanner;
    private final DataInitializationService dataInitService;
    private final WarehouseManagementService warehouseService;
    private final StationSearchService stationSearchService;
    private final MinimalBackboneService minimalBackboneService;
    private final RailwayNetworkRepository repository;

    private Warehouse warehouse;
    private AllocationResult lastAllocationResult;

    public PlannerMenuController(Scanner scanner) {
        this.scanner = scanner;
        this.dataInitService = new DataInitializationService();
        this.warehouseService = new WarehouseManagementService();
        this.stationSearchService = new StationSearchService();
        this.minimalBackboneService = new MinimalBackboneService();
        this.repository = RailwayNetworkRepository.getInstance();
    }

    /**
     * Displays the main menu and handles user input
     */
    public void show() throws Exception {
        this.warehouse = dataInitService.initializeWarehouse();
        loadBelgianNetwork();

        while (true) {
            new PlannerMenu().run();
            String choice = scanner.nextLine();

            switch (choice) {
                case "1" -> handleViewBayContents();
                case "2" -> handleDispatchSku();
                case "3" -> handleRelocateBox();
                case "4" -> handleViewInventory();
                case "5" -> handleProcessOrders();
                case "6" -> handlePackAllocations();
                case "7" -> handleSearchStations();
                case "8" -> new UpgradePlanController(scanner).show();
                case "9" -> handleMinimalBackbone();
                case "10" -> handleHubAnalysis();
                case "0" -> {
                    return;
                }
                default -> System.out.println("Invalid option, please try again!");
            }
        }
    }

    private void loadBelgianNetwork() {
        try {
            if (repository.isDataLoaded()) {
                System.out.println("✓ Data already loaded in memory.");
                return;
            }
            System.out.println("\nLoading Belgian railway network data...");

            Graph<Station, TrackConnection> graph = dataInitService.initializeBelgianNetwork();
            repository.setNetworkGraph(graph);

            System.out.println("✓ Data loaded successfully!");


        } catch (Exception e) {
            System.err.println("\n✗ Error loading data: " + e.getMessage());
            System.out.println("\nPlease ensure CSV files exist in /data/ folder:");
            System.out.println("  • stations.csv - Station information");
            System.out.println("  • lines.csv - Directed dependencies for upgrades");
        }
    }

    private void handleViewBayContents() {
        warehouseService.displayBayContents(warehouse);
    }

    private void handleDispatchSku() {
        var inventory = warehouse.getInventoryBySku();
        if (inventory.isEmpty()) {
            System.out.println("No SKUs available in warehouse.");
            return;
        }

        System.out.println("\n=== Available SKUs ===");
        System.out.println("SKU\t\tTotal Qty\tBoxes");
        System.out.println("========================================");
        inventory.forEach((skuKey, boxes) -> {
            int totalQty = boxes.stream().mapToInt(Box::getQuantity).sum();
            System.out.printf("%s\t\t%d\t\t%d%n", skuKey, totalQty, boxes.size());
        });

        System.out.print("\nEnter SKU to dispatch: ");
        String sku = scanner.nextLine().trim();

        if (!inventory.containsKey(sku)) {
            System.out.println("✗ SKU not found in warehouse.");
            return;
        }

        System.out.print("Enter quantity to dispatch: ");
        try {
            int qty = Integer.parseInt(scanner.nextLine());
            if (!warehouseService.dispatchSku(warehouse, sku, qty)) {
                System.out.println("✗ Quantity must be greater than 0.");
            }
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid quantity format.");
        }
    }

    private void handleRelocateBox() {
        Map<String, String> allBoxes = warehouseService.getAllAvailableBoxes(warehouse);

        if (allBoxes.isEmpty()) {
            System.out.println("No boxes available in warehouse.");
            return;
        }

        System.out.println("\n=== Available Boxes ===");
        System.out.println("Box ID\t\tSKU\t\tQty\tLocation");
        System.out.println("========================================================");
        allBoxes.forEach((boxId, info) -> System.out.println(info));

        System.out.print("\nEnter boxId to relocate: ");
        String boxId = scanner.nextLine().trim();

        if (!allBoxes.containsKey(boxId)) {
            System.out.println("✗ Box not found in warehouse.");
            return;
        }

        System.out.println("\n=== Available Bays ===");
        System.out.println("Location\tCapacity\tCurrent/Max");
        System.out.println("========================================");
        for (Bay bay : warehouse.getAllBays()) {
            System.out.printf("%s\t\t%d\t\t%d/%d%n", bay.getLocationCode(), bay.getCapacityBoxes(), bay.getStoredBoxes().size(), bay.getCapacityBoxes());
        }

        System.out.print("\nEnter new bay location code (e.g., W1-1-5): ");
        String newBay = scanner.nextLine().trim();

        boolean success = warehouseService.relocateBox(warehouse, boxId, newBay);
        if (success) {
            System.out.println("✓ Relocated successfully to " + newBay);
        } else {
            System.out.println("✗ Relocation failed. Bay location not found or bay at full capacity.");
        }
    }

    private void handleViewInventory() {
        warehouseService.displayInventoryBySku(warehouse);
    }

    private void handleProcessOrders() {
        System.out.println("Available orders: " + dataInitService.getCsvImportService().getOrders().size());

        System.out.print("Select mode (1=STRICT, 2=PARTIAL) [default=1]: ");
        String modeInput = scanner.nextLine().trim();
        EligibilityMode mode = "2".equals(modeInput) ? EligibilityMode.PARTIAL : EligibilityMode.STRICT;

        lastAllocationResult = warehouseService.processOrders(dataInitService.getCsvImportService().getOrdersList(), warehouse, mode);
    }

    private void handlePackAllocations() {
        if (lastAllocationResult == null || lastAllocationResult.getAllocations().isEmpty()) {
            System.out.println("✗ No allocation results available. Please run USEI02 first.");
            return;
        }

        System.out.println("Available allocations: " + lastAllocationResult.getAllocations().size());

        System.out.print("Enter trolley capacity (kg) [default=50.0]: ");
        String capacityInput = scanner.nextLine().trim();
        double trolleyCapacity = capacityInput.isEmpty() ? 50.0 : Double.parseDouble(capacityInput);


        System.out.println("\nSelect packing heuristic:");
        System.out.println("1 - First Fit (FF)");
        System.out.println("2 - First Fit Decreasing (FFD)");
        System.out.println("3 - Best Fit Decreasing (BFD)");
        System.out.print("Choose [default=1]: ");
        String heuristicInput = scanner.nextLine().trim();

        PackingHeuristic selectedHeuristic = PackingHeuristic.FIRST_FIT;
        boolean validSelection = true;

        switch (heuristicInput) {
            case "1" -> {
            }
            case "2" -> selectedHeuristic = PackingHeuristic.FIRST_FIT_DECREASING;
            case "3" -> selectedHeuristic = PackingHeuristic.BEST_FIT_DECREASING;
            default -> {
                System.out.println("Invalid heuristic selection.");
                validSelection = false;
            }
        }

        if (validSelection) {
            warehouseService.packTrolleys(lastAllocationResult, trolleyCapacity, selectedHeuristic, dataInitService.getCsvImportService().getItems());
        }
    }

    private void handleSearchStations() throws Exception {
        dataInitService.initializeStationsData();
        while (true) {
            new SearchStationsMenu().run();

            String searchChoice = scanner.nextLine().trim();

            try {
                switch (searchChoice) {
                    case "1" -> searchByTimeZoneGroup();
                    case "2" -> searchByCoordinates();
                    case "3" -> searchByCountry();
                    case "4" -> searchByCoordinatesRange();
                    case "5" -> viewStationStatistics();
                    default -> {
                        return;
                    }
                }
            } catch (IllegalArgumentException e) {
                System.out.println("✗ Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("✗ Unexpected error: " + e.getMessage());
            }
        }
    }

    private void searchByTimeZoneGroup() {
        System.out.print("Enter time zone group (e.g., CET, WET/GMT, EET): ");
        String group = scanner.nextLine().trim();

        if (group.isEmpty()) {
            System.out.println("✗ Time zone group cannot be empty.");
            return;
        }

        List<Station> stations = stationSearchService.getStationsByTimeZoneGroup(group);

        if (stations.isEmpty()) {
            System.out.println("✗ No stations found for time zone group: " + group);
        } else {
            System.out.println("\n✓ Found " + stations.size() + " station(s) in time zone group '" + group + "'");
            System.out.println("\nCountry\tStation Name\t\t\t\t\t\t\tCoordinates\t\t\t\tMain\tCity\tAirport");
            System.out.println("=".repeat(120));

            String lastCountry = "";
            for (Station station : stations) {
                String country = station.getTimeZone() != null ? station.getTimeZone().getCountry() : "N/A";
                String coords = String.format("(%.5f, %.5f)", station.getCoordinates().getLatitude(), station.getCoordinates().getLongitude());

                // Print country only if it changed
                String countryDisplay = country.equals(lastCountry) ? "" : country;
                lastCountry = country;

                System.out.printf("%s\t%-40s\t%s\t%s\t\t%s\t\t%s%n", countryDisplay, truncate(station.getName(), 40), coords, station.isMainStation() ? "✓" : "✗", station.isCity() ? "✓" : "✗", station.isAirport() ? "✓" : "✗");
            }
        }
    }

    private void searchByCoordinates() {
        System.out.print("Enter latitude (between -90 and 90): ");
        String latInput = scanner.nextLine().trim();

        System.out.print("Enter longitude (between -180 and 180): ");
        String lonInput = scanner.nextLine().trim();

        try {
            double latitude = Double.parseDouble(latInput);
            double longitude = Double.parseDouble(lonInput);

            List<Station> stations = stationSearchService.getStationsByCoordinates(latitude, longitude);

            if (stations.isEmpty()) {
                System.out.println("✗ No stations found at coordinates (" + latitude + ", " + longitude + ")");
            } else {
                System.out.println("\n✓ Found " + stations.size() + " station(s) at coordinates (" + latitude + ", " + longitude + ")");
                System.out.println("\nStation Name\t\t\t\t\t\t\t\tTz Group\t\t\tMain\tCity\tAirport");
                System.out.println("=".repeat(120));

                for (Station station : stations) {
                    String country = station.getTimeZone() != null ? station.getTimeZone().getCountry() : "N/A";
                    String group = station.getTimeZone() != null ? station.getTimeZone().getGroup() : "N/A";

                    System.out.printf("%-40s\t%s\t%-15s\t%s\t%s\t%s%n", truncate(station.getName(), 40), country, group, station.isMainStation() ? "✓" : "✗", station.isCity() ? "✓" : "✗", station.isAirport() ? "✓" : "✗");
                }

                if (stations.size() > 1) {
                    System.out.println("\nMultiple stations at same coordinates (sorted by name)");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid coordinate format. Please enter numeric values.");
        }
    }

    private void searchByCountry() {
        System.out.print("Enter country code (e.g., PT, FR, ES): ");
        String country = scanner.nextLine().trim();

        if (country.isEmpty()) {
            System.out.println("✗ Country cannot be empty.");
            return;
        }

        List<Station> stations = stationSearchService.getStationsByCountry(country);

        if (stations.isEmpty()) {
            System.out.println("✗ No stations found for country: " + country);
        } else {
            System.out.println("\n✓ Found " + stations.size() + " station(s) in country '" + country + "'");
            System.out.println("\nStation Name\t\t\t\t\t\t\t\tTz Group\t\tCoordinates\t\t\t\t\tMain\tCity\tAirport");
            System.out.println("=".repeat(120));

            for (Station station : stations) {
                String group = "N/A";

                if (station.getTimeZone() != null) {
                    group = station.getTimeZone().getGroup();
                }

                String coords = String.format("(%.8f, %.8f)", station.getCoordinates().getLatitude(), station.getCoordinates().getLongitude());

                System.out.printf("%-40s\t%-12s\t%s\t%s\t\t%s\t\t%s%n", truncate(station.getName(), 40), group, coords, station.isMainStation() ? "✓" : "✗", station.isCity() ? "✓" : "✗", station.isAirport() ? "✓" : "✗");
            }
        }
    }

    private void viewStationStatistics() {
        String stats = stationSearchService.getStatistics();
        System.out.println(stats);
    }

    private void searchByCoordinatesRange() {
        System.out.print("Enter minimum latitude (between -90 and 90): ");
        String latMinInput = scanner.nextLine().trim();
        System.out.print("Enter maximum latitude (between -90 and 90): ");
        String latMaxInput = scanner.nextLine().trim();

        System.out.print("Enter minimum longitude (between -180 and 180): ");
        String lonMinInput = scanner.nextLine().trim();
        System.out.print("Enter maximum longitude (between -180 and 180): ");
        String lonMaxInput = scanner.nextLine().trim();

        try {
            double latMin = Double.parseDouble(latMinInput);
            double latMax = Double.parseDouble(latMaxInput);
            double lonMin = Double.parseDouble(lonMinInput);
            double lonMax = Double.parseDouble(lonMaxInput);

            // Optional filters
            StationFilter filter = new StationFilter();

            System.out.print("\nApply filters? (y/n) [default=n]: ");
            String applyFilters = scanner.nextLine().trim().toLowerCase();

            if (applyFilters.equals("y") || applyFilters.equals("yes")) {
                // is_city filter
                System.out.print("Filter by city? (yes/no/skip) [default=skip]: ");
                String cityFilter = scanner.nextLine().trim().toLowerCase();

                if (cityFilter.equals("yes") || cityFilter.equals("y")) filter.setIsCity(true);
                else if (cityFilter.equals("no") || cityFilter.equals("n")) filter.setIsCity(false);

                // is_main_station filter
                System.out.print("Filter by main station? (yes/no/skip) [default=skip]: ");
                String mainStationFilter = scanner.nextLine().trim().toLowerCase();
                if (mainStationFilter.equals("yes") || mainStationFilter.equals("y")) filter.setIsMainStation(true);
                else if (mainStationFilter.equals("no") || mainStationFilter.equals("n"))
                    filter.setIsMainStation(false);


                // country filter
                System.out.print("Filter by country (PT/ES/all) [default=all]: ");
                String countryFilter = scanner.nextLine().trim().toUpperCase();
                if (!countryFilter.isEmpty() && !countryFilter.equals("ALL")) filter.setCountry(countryFilter);
            }

            List<Station> stations = stationSearchService.getStationsByRange(latMin, latMax, lonMin, lonMax, filter);

            if (stations.isEmpty()) {
                System.out.println("\n✗ No stations found in the specified range.");
            } else {
                System.out.println("\n✓ Found " + stations.size() + " station(s) in range:");
                System.out.println("  Latitude: [" + latMin + ", " + latMax + "]");
                System.out.println("  Longitude: [" + lonMin + ", " + lonMax + "]");

                if (filter.hasActiveFilters()) {
                    System.out.println("  Filters: " + filter);
                }

                System.out.println("\nCountry\tStation Name\t\t\t\t\t\t\tCoordinates\t\t\t\tMain\tCity\tAirport");
                System.out.println("=".repeat(120));

                String lastCountry = "";
                for (Station station : stations) {
                    String country = station.getTimeZone() != null ? station.getTimeZone().getCountry() : "N/A";
                    String countryDisplay = country.equals(lastCountry) ? "" : country;
                    lastCountry = country;

                    String stationName = truncate(station.getName(), 50);
                    String coords = station.getCoordinates() != null ? station.getCoordinates().toString() : "N/A";
                    String coordsDisplay = truncate(coords, 30);

                    System.out.printf("%s\t%-50s\t%-30s\t%s\t%s\t%s%n", countryDisplay, stationName, coordsDisplay, station.isMainStation() ? "✓" : "✗", station.isCity() ? "✓" : "✗", station.isAirport() ? "✓" : "✗");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("✗ Invalid coordinate format. Please enter numeric values.");
        }
    }

    /**
     * Handles the computation of the minimal backbone network
     */
    private void handleMinimalBackbone() {
        System.out.println("\n=======================================================");
        System.out.println("       MINIMAL BACKBONE NETWORK COMPUTATION           ");
        System.out.println("=======================================================\n");

        try {
            System.out.println("Computing Minimum Spanning Tree (MST)...\n");

            // Calculate the MST
            Graph<Station, TrackConnection> mst = minimalBackboneService.calculateMinimalBackbone();

            System.out.println("✓ Minimal Backbone Network computed successfully!");
            System.out.println("\n=== MST Statistics ===");
            System.out.println("Stations (vertices): " + mst.numVertices());
            System.out.println("Track connections (edges): " + mst.numEdges());

            double totalCost = 0;
            double totalDistance = 0;
            for (Edge<Station, TrackConnection> edge : mst.edges()) {
                totalCost += edge.getWeight().getCost();
                totalDistance += edge.getWeight().getDistance();
            }

            System.out.printf("Total track length: %.2f km%n", totalDistance);
            System.out.printf("Total cost: %.2f%n", totalCost);

            System.out.print("\nEnter filename for output files (without extension): ");
            String filename = scanner.nextLine().trim();

            if (filename.isEmpty()) {
                filename = "minimal_backbone";
                System.out.println("Using default filename: " + filename);
            }

            System.out.println("\n=== Generating Visualization Files ===");
            String svgPath = minimalBackboneService.generateMinimalBackboneVisualization(filename, mst);

            if (svgPath != null) {
                System.out.println("\n✓ Complete! SVG file generated.");
                System.out.println("\nYou can now open the SVG file in a web browser for visualization. The file is placed in the project root directory.");
            } else {
                System.out.println("\n⚠ Note: Graphviz 'neato' command not found.");
                System.out.println("DOT file has been created. To generate SVG:");
                System.err.println("  1. Make sure Graphviz is installed and 'neato' is in your PATH.");
                System.out.println("  2. Install Graphviz from: https://graphviz.org/download/");
                System.out.println("  3. Run: neato -Tsvg " + filename + ".dot -o " + filename + ".svg");
            }

        } catch (IllegalStateException e) {
            System.out.println("\n✗ Error: " + e.getMessage());
            System.out.println("Please ensure railway network data is loaded.");
        } catch (Exception e) {
            System.out.println("\n✗ An error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void handleHubAnalysis() {
        HubCentralityAnalysisService hubService = new HubCentralityAnalysisService();
        List<HubScoreResult> results = hubService.computeHubRanking();
        System.out.println("\n=== TOP 10 HUBS IN BELGIAN RAILWAY NETWORK ===");
        System.out.printf("%-10s %-30s %8s %12s %12s %10s%n", "ID", "Name", "Degree", "Betweenness", "Closeness", "HubScore");
        System.out.println("-".repeat(90));

        for (int i = 0; i < Math.min(10, results.size()); i++) {
            HubScoreResult result = results.get(i);
            System.out.printf(
                "%-10s %-30s %8d %12.2f %12.2f %10.6f%n",
                result.getStid(),
                result.getStname().substring(0, Math.min(30, result.getStname().length())),
                result.getDegree(),
                result.getBetweenness(),
                result.getHarmonicCloseness(),
                result.getHubScore()
            );
        }
    }

    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() > maxLength ? str.substring(0, maxLength - 3) + "..." : str;
    }
}