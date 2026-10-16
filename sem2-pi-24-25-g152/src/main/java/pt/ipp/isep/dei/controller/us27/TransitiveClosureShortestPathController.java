package pt.ipp.isep.dei.controller.us27;

import pt.ipp.isep.dei.domain.us13.RailwayLine;
import pt.ipp.isep.dei.domain.us13.RailwayNetwork;
import pt.ipp.isep.dei.domain.us13.Station;
import pt.ipp.isep.dei.repository.RailwayNetworkRepository;
import pt.ipp.isep.dei.service.us27.TransitiveClosureShortestPathService;
import pt.ipp.isep.dei.ui.gui.GraphvizUtil;

import java.io.IOException;
import java.util.List;

/**
 * Controller for US27 - finding shortest routes with waypoints using transitive closure (Floyd-Warshall).
 * This controller uses matrix-based approach for computing all-pairs shortest paths.
 */
public class TransitiveClosureShortestPathController {
    private final RailwayNetwork network;
    private final TransitiveClosureShortestPathService pathService;

    /**
     * Creates a new controller with the specified network repository.
     *
     * @param stationsFile the path to the stations CSV file
     * @param linesFile the path to the lines CSV file
     * @throws IOException if an I/O error occurs while loading the network
     */
    public TransitiveClosureShortestPathController(String stationsFile, String linesFile) throws IOException {
        RailwayNetworkRepository repository = new RailwayNetworkRepository();
        this.network = repository.loadFromCsv(stationsFile, linesFile);
        this.pathService = new TransitiveClosureShortestPathService(network);
    }

    /**
     * Gets all stations in the network.
     *
     * @return a list of all stations
     */
    public List<Station> getAllStations() {
        return pathService.getAllStations();
    }

    /**
     * Gets all railway lines in the network.
     *
     * @return a list of all railway lines
     */
    public List<RailwayLine> getAllLines() {
        return pathService.getAllLines();
    }

    /**
     * Computes the transitive closure for all-pairs shortest paths using Floyd-Warshall.
     * Also demonstrates Dijkstra algorithm integration for individual path queries.
     * This operation has O(V³) time complexity and should be called once.
     */
    public void computeTransitiveClosure() {
        System.out.println("Computing transitive closure using Floyd-Warshall algorithm...");
        System.out.println("Individual shortest paths will be computed using Dijkstra algorithm...");
        
        long startTime = System.currentTimeMillis();
        
        pathService.computeTransitiveClosure();
        
        long endTime = System.currentTimeMillis();
        System.out.println("Transitive closure computed in " + (endTime - startTime) + " ms");
        System.out.println("Dijkstra algorithm will be used for optimal pathfinding queries.");
    }

    /**
     * Finds the shortest path between source and destination stations, going through the specified waypoints.
     * Uses Dijkstra algorithm for optimal pathfinding considering actual line distances.
     *
     * @param source the source station
     * @param destination the destination station
     * @param waypoints the waypoints to visit in order
     * @return a list of stations representing the path, or an empty list if no path exists
     */
    public List<Station> findShortestPathWithWaypoints(Station source, Station destination, List<Station> waypoints) {
        return pathService.findShortestPathWithWaypoints(source, destination, waypoints);
    }

    /**
     * Gets the shortest distance between two stations using Dijkstra algorithm.
     *
     * @param source the source station
     * @param destination the destination station
     * @return the shortest distance, or -1 if no path exists
     */
    public int getShortestDistance(Station source, Station destination) {
        return pathService.getShortestDistance(source, destination);
    }

    /**
     * Checks if there is a path between two stations.
     *
     * @param source the source station
     * @param destination the destination station
     * @return true if a path exists, false otherwise
     */
    public boolean hasPath(Station source, Station destination) {
        return pathService.hasPath(source, destination);
    }

    /**
     * Gets all stations reachable from a given source station.
     *
     * @param source the source station
     * @return a list of all reachable stations (excluding the source)
     */
    public List<Station> getReachableStations(Station source) {
        return pathService.getReachableStations(source);
    }

    /**
     * Generates a visualization of the network with the shortest path highlighted.
     *
     * @param path the path to highlight
     * @param outputFile the output file for the visualization
     * @throws IOException if an I/O error occurs while writing the file
     */
    public void generateVisualization(List<Station> path, String outputFile) throws IOException {
        GraphvizUtil.generateDotFile(network.getAllStations(), network.getAllLines(), path, outputFile);
    }

    /**
     * Prints the distance matrix for debugging purposes.
     */
    public void printDistanceMatrix() {
        pathService.printDistanceMatrix();
    }

    /**
     * Gets statistics about the network and computed transitive closure.
     *
     * @return a string containing network statistics
     */
    public String getNetworkStatistics() {
        return pathService.getStatistics();
    }

    /**
     * Finds shortest paths from a source to multiple destinations.
     * This is efficient with transitive closure as all distances are pre-computed.
     *
     * @param source the source station
     * @param destinations the list of destination stations
     * @return a map of destination stations to their shortest paths from source
     */
    public java.util.Map<Station, List<Station>> findMultipleShortestPaths(Station source, List<Station> destinations) {
        java.util.Map<Station, List<Station>> paths = new java.util.HashMap<>();
        
        for (Station destination : destinations) {
            List<Station> path = pathService.findShortestPathWithWaypoints(source, destination, null);
            if (!path.isEmpty()) {
                paths.put(destination, path);
            }
        }
        
        return paths;
    }

    /**
     * Analyzes the network connectivity and provides a detailed report.
     *
     * @return a detailed connectivity analysis report
     */
    public String analyzeNetworkConnectivity() {
        StringBuilder analysis = new StringBuilder();
        
        analysis.append("=== Network Connectivity Analysis ===\n\n");
        analysis.append(getNetworkStatistics()).append("\n\n");
        
        List<Station> stations = getAllStations();
        int totalStations = stations.size();
        int totalPossibleConnections = totalStations * (totalStations - 1);
        int actualConnections = 0;
        
        // Count actual connections
        for (Station source : stations) {
            for (Station dest : stations) {
                if (!source.equals(dest) && hasPath(source, dest)) {
                    actualConnections++;
                }
            }
        }
        
        double connectivityRatio = (double) actualConnections / totalPossibleConnections * 100;
        
        analysis.append("Detailed Analysis:\n");
        analysis.append("- Total possible station pairs: ").append(totalPossibleConnections).append("\n");
        analysis.append("- Connected station pairs: ").append(actualConnections).append("\n");
        analysis.append("- Network connectivity ratio: ").append(String.format("%.2f", connectivityRatio)).append("%\n");
        
        if (connectivityRatio == 100.0) {
            analysis.append("- Network status: FULLY CONNECTED\n");
            analysis.append("- All stations can reach all other stations\n");
        } else if (connectivityRatio >= 90.0) {
            analysis.append("- Network status: HIGHLY CONNECTED\n");
            analysis.append("- Most stations can reach most other stations\n");
        } else if (connectivityRatio >= 50.0) {
            analysis.append("- Network status: MODERATELY CONNECTED\n");
            analysis.append("- Some isolated components may exist\n");
        } else {
            analysis.append("- Network status: POORLY CONNECTED\n");
            analysis.append("- Many isolated components exist\n");
        }
        
        return analysis.toString();
    }
}