package pt.isep.dei.esinf.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import pt.isep.dei.esinf.domain.railway.TrackConnection;
import pt.isep.dei.esinf.domain.railway.upgrade.UpgradePlanResult;
import pt.isep.dei.esinf.domain.stations.Station;
import pt.isep.dei.esinf.repository.RailwayNetworkRepository;
import pt.isep.dei.esinf.service.railway.RailwayUpgradeService;
import pt.isep.dei.esinf.ui.UpgradePlanMenu;
import pt.isep.dei.esinf.utils.graph.Edge;
import pt.isep.dei.esinf.utils.graph.map.MapGraph;
import pt.isep.dei.esinf.utils.interfaces.Graph;

/**
 * Controller for upgrade plan operations and menu interactions
 */
public class UpgradePlanController {

    private final Scanner scanner;
    private final RailwayUpgradeService upgradeService;
    private final RailwayNetworkRepository repository;

    public UpgradePlanController(Scanner scanner) {
        this.scanner = scanner;
        this.repository = RailwayNetworkRepository.getInstance();
        this.upgradeService = new RailwayUpgradeService();
    }

    /**
     * Displays the menu and handles user input
     */
    public void show() {
        int option;
        checkBidirectionalEdges();

        do {
            displayMenu();
            option = readOption();
            processOption(option);
        } while (option != 0);
    }

    private void displayMenu() {
        new UpgradePlanMenu().run();
    }

    private int readOption() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void processOption(int option) {
        switch (option) {
            case 1 -> handleComputeUpgradePlan();
            case 2 -> handleDetailedCycleAnalysis();
            case 3 -> handleShowStatistics();
            case 4 -> handleValidateGraph();
            case 0 -> System.out.println("\nReturning to main menu...");
            default -> System.out.println("\n✗ Invalid option! Please choose 0-5.");
        }
    }


    private void checkBidirectionalEdges() {
        Graph<Station, TrackConnection> graph = repository.getNetworkGraph();
        int bidirectionalCount = 0;
        
        for (Station s1 : graph.vertices()) {
            for (Station s2 : graph.vertices()) {
                if (!s1.equals(s2)) {
                    if (graph.edge(s1, s2) != null && graph.edge(s2, s1) != null) {
                        bidirectionalCount++;
                    }
                }
            }
        }
        
        if (bidirectionalCount > 0) {
            System.out.println("\n!  NOTE: Found " + bidirectionalCount + " bidirectional dependencies");
            System.out.println("   This means some stations depend on each other (A→B and B→A)");
            System.out.println("   These create 2-node cycles that prevent topological ordering");
        }
    }

    private void handleComputeUpgradePlan() {
        if (!repository.isDataLoaded()) {
            System.out.println("\n  No data loaded. Please load data first (option 1).");
            return;
        }
        
        System.out.println("\nComputing upgrade plan for Belgian railway network...");
        System.out.println("Analyzing " + repository.getStationCount() + 
                          " stations and " + repository.getConnectionCount() + " dependencies...");
        
        long startTime = System.currentTimeMillis();
        UpgradePlanResult result = upgradeService.computeUpgradePlan();
        long endTime = System.currentTimeMillis();
        
        System.out.println("\n===================================================");
        System.out.println("           UPGRADE PLAN ANALYSIS REPORT");
        System.out.println("===================================================");
        
        displayUpgradePlanResult(result, endTime - startTime);
    }

    private void displayUpgradePlanResult(UpgradePlanResult result, long computationTime) {
        System.out.println("\n=== NETWORK METRICS ===");
        System.out.println("   • Total Stations: " + result.getVerticesCount());
        System.out.println("   • Total Dependencies: " + result.getEdgesCount());
        System.out.println("   • Computation Time: " + computationTime + " ms");
        
        if (result.isHasCycles()) {
            System.out.println("\n=== RESULT: CYCLES DETECTED ===");
            System.out.println("   Cannot determine upgrade order due to circular dependencies.");
            
            List<Station> cycleStations = result.getCycleStations();
            List<String> cycleEdges = result.getCycleEdges();
            
            System.out.println("\n=== STATIONS INVOLVED IN CYCLES (" + cycleStations.size() + ") ===");
            System.out.println("   " + formatPercentage(cycleStations.size(), result.getVerticesCount()) + 
                             " of all stations are in dependency cycles");
            
            if (!cycleStations.isEmpty()) {
                System.out.println("\n   Top 10 stations by dependency complexity:");
                for (int i = 0; i < Math.min(10, cycleStations.size()); i++) {
                    Station station = cycleStations.get(i);
                    System.out.println(String.format("   %2d. %s", i + 1, station.getName()));
                }
                
                if (cycleStations.size() > 10) {
                    System.out.println("   ... and " + (cycleStations.size() - 10) + " more stations");
                }
            }
            
            if (!cycleEdges.isEmpty()) {
                System.out.println("\n=== PROBLEMATIC DEPENDENCIES (" + cycleEdges.size() + ") ===");
                System.out.println("   Sample of circular dependencies:");
                for (int i = 0; i < Math.min(5, cycleEdges.size()); i++) {
                    System.out.println("   • " + cycleEdges.get(i));
                }
                if (cycleEdges.size() > 5) {
                    System.out.println("   ... and " + (cycleEdges.size() - 5) + " more dependencies");
                }
            }
            
            System.out.println("\n=== RECOMMENDATIONS ===");
            System.out.println("   1. Review dependency directions in lines.csv");
            System.out.println("   2. Identify and break dependency cycles");
            System.out.println("   3. Consider if bidirectional dependencies are necessary");
            
        } else {
            System.out.println("\n=== RESULT: VALID UPGRADE ORDER FOUND ===");
            System.out.println("   Stations can be upgraded in topological order.");
            
            List<Station> order = result.getTopologicalOrder();
            System.out.println("\n=== ORDER OF ALL VERTICES (" + order.size() + " stations) ===");
            
            int groupSize = 20;
            int groups = (int) Math.ceil((double) order.size() / groupSize);
            
            for (int group = 0; group < groups; group++) {
                int start = group * groupSize;
                int end = Math.min(start + groupSize, order.size());
                
                System.out.println("\n   Phase " + (group + 1) + " (Stations " + (start + 1) + "-" + end + "):");
                for (int i = start; i < end; i++) {
                    Station station = order.get(i);
                    System.out.println(String.format("      %3d. %s", i + 1, station.getName()));
                }
            }
            
            System.out.println("\n=== UPGRADE SCHEDULING STRATEGY ===");
            System.out.println("   • Start with Phase 1 stations (no incoming dependencies)");
            System.out.println("   • Complete each phase before moving to the next");
            System.out.println("   • Phase " + groups + " stations depend on all previous phases");
        }
        
        System.out.println("\n=== TEMPORAL ANALYSIS COMPLEXITY ===");
        System.out.println("   • Algorithm: Kahn's Topological Sort");
        System.out.println("   • Time Complexity: O(V + E) = O(" + 
                          result.getVerticesCount() + " + " + result.getEdgesCount() + ")");
        System.out.println("   • Space Complexity: O(V + E)");
        System.out.println("   • Computation verified in " + computationTime + " ms");
        
        System.out.println("\n==================================================");
    }

    private void handleDetailedCycleAnalysis() {
        if (!repository.isDataLoaded()) {
            System.out.println("\n!  No data loaded. Please load data first (option 1).");
            return;
        }
        
        System.out.println("\n=== DETAILED CYCLE ANALYSIS ===");
        System.out.println("===========================");
        
        UpgradePlanResult result = upgradeService.computeUpgradePlan();
        
        if (!result.isHasCycles()) {
            System.out.println("\n=== No cycles detected in the dependency graph ===");
            System.out.println("   All stations can be ordered topologically.");
            return;
        }
        
        List<Station> cycleStations = result.getCycleStations();
        List<String> cycleEdges = result.getCycleEdges();
        
        System.out.println("\n=== CYCLE ANALYSIS STATISTICS ===");
        System.out.println("   • Stations in cycles: " + cycleStations.size() + 
                        "/" + result.getVerticesCount() + 
                        " (" + formatPercentage(cycleStations.size(), result.getVerticesCount()) + ")");
        System.out.println("   • Problematic dependencies: " + cycleEdges.size() + 
                        "/" + result.getEdgesCount() + 
                        " (" + formatPercentage(cycleEdges.size(), result.getEdgesCount()) + ")");
        
        System.out.println("\n🏷️  ALL STATIONS IN CYCLES");
        for (int i = 0; i < cycleStations.size(); i++) {
            Station station = cycleStations.get(i);
            if (i % 5 == 0 && i > 0) System.out.println();
            System.out.print(String.format(" %3d. %-25s", i + 1, 
                truncate(station.getName(), 25)));
            if ((i + 1) % 5 == 0 || i == cycleStations.size() - 1) System.out.println();
        }
        
        System.out.println("\n=== CIRCULAR DEPENDENCY PATTERNS ===");
        
        try {
            Graph<Station, TrackConnection> graph = repository.getNetworkGraph();
            List<String> bidirectionalPairs = new ArrayList<>();
            
            List<Station> stationsList = new ArrayList<>();
            for (Station station : graph.vertices()) {
                stationsList.add(station);
            }
            
            for (int i = 0; i < stationsList.size(); i++) {
                Station s1 = stationsList.get(i);
                for (int j = i + 1; j < stationsList.size(); j++) {
                    Station s2 = stationsList.get(j);
                    
                    if (s1.getId() != null && s2.getId() != null) {
                        Edge<Station, TrackConnection> edge1 = graph.edge(s1, s2);
                        Edge<Station, TrackConnection> edge2 = graph.edge(s2, s1);
                        
                        if (edge1 != null && edge2 != null) {
                            bidirectionalPairs.add(s1.getName() + " ↔ " + s2.getName());
                        }
                    }
                }
            }
            
            System.out.println("   Most common cycle patterns:");
            System.out.println("   • 2-node cycles (A↔B): " + bidirectionalPairs.size() + " pairs");
            
            if (!bidirectionalPairs.isEmpty() && bidirectionalPairs.size() <= 10) {
                System.out.println("   Example pairs:");
                for (String pair : bidirectionalPairs) {
                    System.out.println("     • " + pair);
                }
            } else if (bidirectionalPairs.size() > 10) {
                System.out.println("   Example pairs (first 10):");
                for (int i = 0; i < 10 && i < bidirectionalPairs.size(); i++) {
                    System.out.println("     • " + bidirectionalPairs.get(i));
                }
                System.out.println("     ... and " + (bidirectionalPairs.size() - 10) + " more");
            }
            
            int estimatedLargerCycles = Math.max(0, cycleEdges.size() - (bidirectionalPairs.size() * 2));
            System.out.println("   • Larger cycles (>2 nodes): ~" + estimatedLargerCycles + " dependencies");
            
        } catch (Exception e) {
            System.err.println("   Error analyzing cycle patterns: " + e.getMessage());
            System.out.println("   • Cycle pattern analysis skipped due to data issues");
        }
        
        System.out.println("\n=== CYCLE RESOLUTION STRATEGIES ===");
        System.out.println("   1. For A↔B cycles: Choose one direction based on:");
        System.out.println("      • Which station is larger/more critical");
        System.out.println("      • Geographic/logical flow direction");
        System.out.println("      • Existing infrastructure hierarchy");
        System.out.println("   2. For larger cycles: Identify central station");
        System.out.println("   3. Consider temporary bypasses during upgrade");
    }

    private void handleShowStatistics() {
        if (!repository.isDataLoaded()) {
            System.out.println("\n!  No data loaded. Please load data first (option 1).");
            return;
        }
        
        System.out.println("\n=== NETWORK STATISTICS ===");
        System.out.println("=======================");
        
        String stats = upgradeService.getGraphStatistics();
        System.out.println(stats);
        
        Graph<Station, TrackConnection> graph = repository.getNetworkGraph();
        
        System.out.println("\n=== DEPENDENCY ANALYSIS ===");
        System.out.println("   • Stations with no dependencies (can start immediately): " + 
                          countStationsWithZeroInDegree(graph));
        System.out.println("   • Stations that are critical (many depend on them): " + 
                          countStationsWithHighOutDegree(graph, 10));
        System.out.println("   • Average dependencies per station: " + 
                          String.format("%.2f", (double)graph.numEdges() / graph.numVertices()));
        
        System.out.println("\n=== UPGRADE PLANNING INSIGHTS ===");
        System.out.println("   • Maximum parallel upgrades: " + countStationsWithZeroInDegree(graph));
        System.out.println("   • Critical path length: Requires topological analysis");
        System.out.println("   • Bottleneck stations: Those with high in-degree");
    }

    private void handleValidateGraph() {
        if (!repository.isDataLoaded()) {
            System.out.println("\n!  No data loaded. Please load data first (option 1).");
            return;
        }
        
        System.out.println("\n=== GRAPH VALIDATION ===");
        System.out.println("====================");
        
        List<String> issues = upgradeService.validateGraph();
        
        if (issues.isEmpty()) {
            System.out.println("\nAll structural checks passed.");
            System.out.println("Graph is valid for dependency analysis.");
        } else {
            System.out.println("\nFound " + issues.size() + " structural issues:");
            for (String issue : issues) {
                System.out.println("   • " + issue);
            }
        }
        
        Graph<Station, TrackConnection> graph = repository.getNetworkGraph();
        int isolated = 0;
        for (Station station : graph.vertices()) {
            if (graph.outDegree(station) == 0 && graph.inDegree(station) == 0) {
                isolated++;
            }
        }
        
        if (isolated > 0) {
            System.out.println("\n!  Found " + isolated + " isolated stations");
            System.out.println("   These have no dependencies and depend on nothing");
        }
    }

    private void handleShowComplexityAnalysis() {
        if (!repository.isDataLoaded()) {
            System.out.println("\n!  No data loaded. Please load data first (option 1).");
            return;
        }
        
        System.out.println("\n=== ALGORITHM COMPLEXITY ===");
        System.out.println("=======================");
        
        String analysis = upgradeService.getComplexityAnalysis();
        System.out.println(analysis);
        
        System.out.println("\n=== PERFORMANCE AT SCALE ===");
        System.out.println("   • Current network: " + repository.getStationCount() + 
                          "V, " + repository.getConnectionCount() + "E");
        System.out.println("   • Time for 10x larger: ~" + 
                          repository.getStationCount() * 10 + " operations");
        System.out.println("   • Memory usage: ~" + 
                          (repository.getStationCount() + repository.getConnectionCount()) + 
                          " units");
        System.out.println("   • Suitable for: National railway networks");
    }

    private String formatPercentage(int part, int total) {
        return String.format("%.1f%%", (part * 100.0) / total);
    }
    
    private String truncate(String text, int length) {
        if (text.length() <= length) return text;
        return text.substring(0, length - 3) + "...";
    }
    
    private int countStationsWithZeroInDegree(Graph<Station, TrackConnection> graph) {
        int count = 0;
        for (Station station : graph.vertices()) {
            if (graph.inDegree(station) == 0) {
                count++;
            }
        }
        return count;
    }
    
    private int countStationsWithHighOutDegree(Graph<Station, TrackConnection> graph, int threshold) {
        int count = 0;
        for (Station station : graph.vertices()) {
            if (graph.outDegree(station) >= threshold) {
                count++;
            }
        }
        return count;
    }

    private Graph<Station, TrackConnection> createDirectedAcyclicGraph(
        Graph<Station, TrackConnection> original) {
        
        Graph<Station, TrackConnection> dag = new MapGraph<>(true);
        
        for (Station station : original.vertices()) {
            dag.addVertex(station);
        }
        
        int keptEdges = 0;
        int removedEdges = 0;
        
        List<Station> stations = new ArrayList<>();
        for (Station station : original.vertices()) {
            stations.add(station);
        }
        
        for (int i = 0; i < stations.size(); i++) {
            for (int j = i + 1; j < stations.size(); j++) {
                Station stationA = stations.get(i);
                Station stationB = stations.get(j);
                
                Edge<Station, TrackConnection> edgeAB = original.edge(stationA, stationB);
                Edge<Station, TrackConnection> edgeBA = original.edge(stationB, stationA);
                
                if (edgeAB != null && edgeBA != null) {
                    try {
                        int idA = Integer.parseInt(stationA.getId());
                        int idB = Integer.parseInt(stationB.getId());
                        
                        if (idA < idB) {
                            dag.addEdge(stationA, stationB, edgeAB.getWeight());
                            keptEdges++;
                            removedEdges++;
                        } else {
                            dag.addEdge(stationB, stationA, edgeBA.getWeight());
                            keptEdges++;
                            removedEdges++;
                        }
                    } catch (NumberFormatException e) {
                        dag.addEdge(stationA, stationB, edgeAB.getWeight());
                        keptEdges++;
                        removedEdges++;
                    }
                }
                else if (edgeAB != null) {
                    dag.addEdge(stationA, stationB, edgeAB.getWeight());
                    keptEdges++;
                }
                else if (edgeBA != null) {
                    dag.addEdge(stationB, stationA, edgeBA.getWeight());
                    keptEdges++;
                }
            }
        }
        
        for (Station from : original.vertices()) {
            for (Edge<Station, TrackConnection> edge : original.outgoingEdges(from)) {
                Station to = edge.getVDest();
                
                boolean alreadyProcessed = false;
                for (int i = 0; i < stations.size(); i++) {
                    for (int j = i + 1; j < stations.size(); j++) {
                        Station s1 = stations.get(i);
                        Station s2 = stations.get(j);
                        if ((s1.equals(from) && s2.equals(to)) || 
                            (s1.equals(to) && s2.equals(from))) {
                            alreadyProcessed = true;
                            break;
                        }
                    }
                    if (alreadyProcessed) break;
                }
                
                if (!alreadyProcessed) {
                    dag.addEdge(from, to, edge.getWeight());
                    keptEdges++;
                }
            }
        }
        
        System.out.println("\n=== Transformation statistics ===");
        System.out.println("   • Kept edges: " + keptEdges);
        System.out.println("   • Removed reverse edges: " + removedEdges);
        System.out.println("   • Total DAG edges: " + dag.numEdges());
        
        return dag;
    }

    /**
     * Check if data is loaded
     */
    private boolean isDataLoaded() {
        return !repository.isDataLoaded();
    }

    /**
     * Validate graph
     */
    private List<String> validate() {
        if (isDataLoaded()) {
            return List.of("No data loaded. Please load data first.");
        }
        return upgradeService.validateGraph();
    }
}