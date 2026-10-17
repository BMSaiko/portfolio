package pt.ipp.isep.dei.service.us27;

import pt.ipp.isep.dei.domain.us13.RailwayLine;
import pt.ipp.isep.dei.domain.us13.RailwayNetwork;
import pt.ipp.isep.dei.domain.us13.Station;

import java.util.*;

/**
 * Service for finding shortest paths using Floyd-Warshall algorithm with transitive closure.
 * This implementation uses matrix-based approach to compute all-pairs shortest paths.
 */
public class TransitiveClosureShortestPathService {
    private final RailwayNetwork network;
    private final List<Station> stationsList;
    private final Map<Station, Integer> stationIndex;
    private int[][] distanceMatrix;
    private int[][] nextMatrix;
    private boolean isComputed = false;
    
    // Constants for matrix representation
    private static final int INFINITY = Integer.MAX_VALUE / 2; // Avoid overflow in addition
    
    /**
     * Creates a new service with the specified network.
     *
     * @param network the railway network
     */
    public TransitiveClosureShortestPathService(RailwayNetwork network) {
        this.network = network;
        this.stationsList = new ArrayList<>(network.getAllStations());
        this.stationIndex = new HashMap<>();
        
        // Build station index mapping
        for (int i = 0; i < stationsList.size(); i++) {
            stationIndex.put(stationsList.get(i), i);
        }
        
        // Initialize matrices
        initializeMatrices();
    }
    
    /**
     * Gets all stations in the network.
     *
     * @return a list of all stations
     */
    public List<Station> getAllStations() {
        return new ArrayList<>(stationsList);
    }
    
    /**
     * Gets all railway lines in the network.
     *
     * @return a list of all railway lines
     */
    public List<RailwayLine> getAllLines() {
        return network.getAllLines();
    }
    
    /**
     * Initializes the distance and next matrices with initial values.
     */
    private void initializeMatrices() {
        int n = stationsList.size();
        distanceMatrix = new int[n][n];
        nextMatrix = new int[n][n];
        
        // Initialize all distances to infinity and next pointers to -1
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = 0;
                    nextMatrix[i][j] = i;
                } else {
                    distanceMatrix[i][j] = INFINITY;
                    nextMatrix[i][j] = -1;
                }
            }
        }
        
        // Fill in direct connections from railway lines
        for (RailwayLine line : network.getAllLines()) {
            Station source = line.getSource();
            Station destination = line.getDestination();
            
            int sourceIndex = stationIndex.get(source);
            int destIndex = stationIndex.get(destination);
            int distance = line.getDistance();
            
            // Update both directions since railway lines are bidirectional
            if (distance < distanceMatrix[sourceIndex][destIndex]) {
                distanceMatrix[sourceIndex][destIndex] = distance;
                nextMatrix[sourceIndex][destIndex] = destIndex;
            }
            
            if (distance < distanceMatrix[destIndex][sourceIndex]) {
                distanceMatrix[destIndex][sourceIndex] = distance;
                nextMatrix[destIndex][sourceIndex] = sourceIndex;
            }
        }
    }
    
    /**
     * Computes the transitive closure using Floyd-Warshall algorithm.
     * This must be called before finding shortest paths.
     */
    public void computeTransitiveClosure() {
        int n = stationsList.size();
        
        // Floyd-Warshall algorithm
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    // Check if path through k is shorter
                    if (distanceMatrix[i][k] != INFINITY && 
                        distanceMatrix[k][j] != INFINITY &&
                        distanceMatrix[i][k] + distanceMatrix[k][j] < distanceMatrix[i][j]) {
                        
                        distanceMatrix[i][j] = distanceMatrix[i][k] + distanceMatrix[k][j];
                        nextMatrix[i][j] = nextMatrix[i][k];
                    }
                }
            }
        }
        
        isComputed = true;
    }
    
    /**
     * Finds the shortest path between source and destination stations through specified waypoints.
     * Uses both Floyd-Warshall (transitive closure) and Dijkstra algorithms for optimal pathfinding.
     *
     * @param source the source station
     * @param destination the destination station
     * @param waypoints the waypoints to visit in order
     * @return a list of stations representing the path, or an empty list if no path exists
     */
    public List<Station> findShortestPathWithWaypoints(Station source, Station destination, List<Station> waypoints) {
        // If no waypoints, use Dijkstra for single shortest path
        if (waypoints == null || waypoints.isEmpty()) {
            return findShortestPathDijkstra(source, destination);
        }
        
        // For multiple waypoints, combine Dijkstra with waypoint ordering
        List<Station> orderedStations = new ArrayList<>();
        orderedStations.add(source);
        orderedStations.addAll(waypoints);
        orderedStations.add(destination);
        
        // Find shortest path through all waypoints using Dijkstra for each segment
        List<Station> fullPath = new ArrayList<>();
        
        for (int i = 0; i < orderedStations.size() - 1; i++) {
            Station currentStation = orderedStations.get(i);
            Station nextStation = orderedStations.get(i + 1);
            
            List<Station> segment = findShortestPathDijkstra(currentStation, nextStation);
            
            if (segment.isEmpty()) {
                // If any segment is impossible, the entire path is impossible
                return Collections.emptyList();
            }
            
            // Add the segment to the full path (skip first station except for first segment)
            if (i == 0) {
                fullPath.addAll(segment);
            } else {
                fullPath.addAll(segment.subList(1, segment.size()));
            }
        }
        
        return fullPath;
    }
    
    /**
     * Finds the shortest path between two stations using Dijkstra's algorithm.
     * This provides optimal paths considering actual distances.
     *
     * @param source the source station
     * @param destination the destination station
     * @return a list of stations representing the path, or an empty list if no path exists
     */
    private List<Station> findShortestPathDijkstra(Station source, Station destination) {
        // If source and destination are the same, return just the source
        if (source.equals(destination)) {
            return List.of(source);
        }
        
        // Maps to store distances and previous stations
        Map<Station, Integer> distances = new HashMap<>();
        Map<Station, Station> previousStations = new HashMap<>();
        PriorityQueue<StationDistance> queue = new PriorityQueue<>(Comparator.comparing(sd -> sd.distance));
        Set<Station> visited = new HashSet<>();
        
        // Initialize distances
        for (Station station : stationsList) {
            distances.put(station, Integer.MAX_VALUE);
        }
        distances.put(source, 0);
        queue.add(new StationDistance(source, 0));
        
        // Dijkstra's algorithm
        while (!queue.isEmpty()) {
            StationDistance current = queue.poll();
            
            // Skip if we've already processed this station with a better distance
            if (visited.contains(current.station)) {
                continue;
            }
            
            visited.add(current.station);
            
            // If we've reached the destination, we can stop
            if (current.station.equals(destination)) {
                break;
            }
            
            // Explore neighbors
            for (RailwayLine line : network.getLinesForStation(current.station)) {
                Station neighbor = line.getOtherStation(current.station);
                
                if (!visited.contains(neighbor)) {
                    int newDistance = current.distance + line.getDistance();
                    
                    if (newDistance < distances.get(neighbor)) {
                        distances.put(neighbor, newDistance);
                        previousStations.put(neighbor, current.station);
                        queue.add(new StationDistance(neighbor, newDistance));
                    }
                }
            }
        }
        
        // If no path found
        if (!previousStations.containsKey(destination)) {
            return Collections.emptyList();
        }
        
        // Reconstruct path
        List<Station> path = new ArrayList<>();
        Station current = destination;
        
        while (current != null) {
            path.add(0, current);
            current = previousStations.get(current);
        }
        
        return path;
    }
    
    /**
     * Utility class to store a station and its distance from the source in Dijkstra's algorithm.
     */
    private static class StationDistance {
        private final Station station;
        private final int distance;
        
        public StationDistance(Station station, int distance) {
            this.station = station;
            this.distance = distance;
        }
    }
    
    /**
     * Reconstructs the shortest path between two stations using the next matrix.
     *
     * @param source the source station
     * @param destination the destination station
     * @return a list of stations representing the path, or an empty list if no path exists
     */
    private List<Station> reconstructPath(Station source, Station destination) {
        if (source.equals(destination)) {
            return List.of(source);
        }
        
        Integer sourceIdx = stationIndex.get(source);
        Integer destIdx = stationIndex.get(destination);
        
        if (sourceIdx == null || destIdx == null) {
            return Collections.emptyList();
        }
        
        // Check if path exists
        if (distanceMatrix[sourceIdx][destIdx] == INFINITY) {
            return Collections.emptyList();
        }
        
        // Reconstruct path using next matrix
        List<Station> path = new ArrayList<>();
        int current = sourceIdx;
        
        while (current != destIdx) {
            path.add(stationsList.get(current));
            current = nextMatrix[current][destIdx];
            
            if (current == -1) {
                // Should not happen if matrices are correctly computed
                return Collections.emptyList();
            }
        }
        
        path.add(stationsList.get(destIdx));
        return path;
    }
    
    /**
     * Gets the shortest distance between two stations using Dijkstra's algorithm.
     * This provides the actual shortest distance considering line distances.
     *
     * @param source the source station
     * @param destination the destination station
     * @return the shortest distance, or -1 if no path exists
     */
    public int getShortestDistance(Station source, Station destination) {
        if (source.equals(destination)) {
            return 0;
        }
        
        // Use Dijkstra for accurate distance calculation
        Map<Station, Integer> distances = new HashMap<>();
        PriorityQueue<StationDistance> queue = new PriorityQueue<>(Comparator.comparing(sd -> sd.distance));
        Set<Station> visited = new HashSet<>();
        
        // Initialize distances
        for (Station station : stationsList) {
            distances.put(station, Integer.MAX_VALUE);
        }
        distances.put(source, 0);
        queue.add(new StationDistance(source, 0));
        
        // Dijkstra's algorithm
        while (!queue.isEmpty()) {
            StationDistance current = queue.poll();
            
            if (visited.contains(current.station)) {
                continue;
            }
            
            visited.add(current.station);
            
            // If we've reached the destination, return the distance
            if (current.station.equals(destination)) {
                return current.distance;
            }
            
            // Explore neighbors
            for (RailwayLine line : network.getLinesForStation(current.station)) {
                Station neighbor = line.getOtherStation(current.station);
                
                if (!visited.contains(neighbor)) {
                    int newDistance = current.distance + line.getDistance();
                    
                    if (newDistance < distances.get(neighbor)) {
                        distances.put(neighbor, newDistance);
                        queue.add(new StationDistance(neighbor, newDistance));
                    }
                }
            }
        }
        
        return -1; // No path found
    }
    
    /**
     * Checks if there is a path between two stations.
     *
     * @param source the source station
     * @param destination the destination station
     * @return true if a path exists, false otherwise
     */
    public boolean hasPath(Station source, Station destination) {
        return getShortestDistance(source, destination) != -1;
    }
    
    /**
     * Gets all stations reachable from a given source station.
     *
     * @param source the source station
     * @return a list of all reachable stations (excluding the source)
     */
    public List<Station> getReachableStations(Station source) {
        if (!isComputed) {
            computeTransitiveClosure();
        }
        
        Integer sourceIdx = stationIndex.get(source);
        if (sourceIdx == null) {
            return Collections.emptyList();
        }
        
        List<Station> reachable = new ArrayList<>();
        for (int i = 0; i < stationsList.size(); i++) {
            if (i != sourceIdx && distanceMatrix[sourceIdx][i] != INFINITY) {
                reachable.add(stationsList.get(i));
            }
        }
        
        return reachable;
    }
    
    /**
     * Prints the distance matrix for debugging purposes.
     */
    public void printDistanceMatrix() {
        if (!isComputed) {
            System.out.println("Transitive closure not computed yet. Call computeTransitiveClosure() first.");
            return;
        }
        
        int n = stationsList.size();
        System.out.println("Distance Matrix:");
        System.out.print("      ");
        
        // Print column headers
        for (int j = 0; j < n; j++) {
            System.out.printf("%8s", stationsList.get(j).getName().substring(0, Math.min(6, stationsList.get(j).getName().length())));
        }
        System.out.println();
        
        // Print rows
        for (int i = 0; i < n; i++) {
            System.out.printf("%6s", stationsList.get(i).getName().substring(0, Math.min(6, stationsList.get(i).getName().length())));
            for (int j = 0; j < n; j++) {
                if (distanceMatrix[i][j] == INFINITY) {
                    System.out.printf("%8s", "∞");
                } else {
                    System.out.printf("%8d", distanceMatrix[i][j]);
                }
            }
            System.out.println();
        }
    }
    
    /**
     * Gets statistics about the network using Dijkstra-based calculations.
     *
     * @return a string containing statistics
     */
    public String getStatistics() {
        int n = stationsList.size();
        int totalPairs = n * (n - 1); // Exclude self-pairs
        int reachablePairs = 0;
        int minDistance = Integer.MAX_VALUE;
        int maxDistance = 0;
        long totalDistance = 0;
        
        // Use Dijkstra to compute actual statistics
        for (int i = 0; i < n; i++) {
            Station source = stationsList.get(i);
            
            // Run Dijkstra from this source to find all reachable stations
            Map<Station, Integer> distances = dijkstraFromSource(source);
            
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    Station destination = stationsList.get(j);
                    Integer distance = distances.get(destination);
                    
                    if (distance != null && distance != Integer.MAX_VALUE) {
                        reachablePairs++;
                        minDistance = Math.min(minDistance, distance);
                        maxDistance = Math.max(maxDistance, distance);
                        totalDistance += distance;
                    }
                }
            }
        }
        
        double connectivity = (double) reachablePairs / totalPairs * 100;
        double avgDistance = reachablePairs > 0 ? (double) totalDistance / reachablePairs : 0;
        
        return String.format(
            "Network Statistics (Dijkstra-based):\n" +
            "- Stations: %d\n" +
            "- Railway Lines: %d\n" +
            "- Connectivity: %.1f%% (%d/%d pairs)\n" +
            "- Shortest Distance: %d km\n" +
            "- Longest Distance: %d km\n" +
            "- Average Distance: %.1f km",
            n, network.getAllLines().size(), connectivity, reachablePairs, totalPairs,
            minDistance == Integer.MAX_VALUE ? 0 : minDistance, maxDistance, avgDistance
        );
    }
    
    /**
     * Runs Dijkstra's algorithm from a source station to find distances to all other stations.
     *
     * @param source the source station
     * @return a map of stations to their shortest distances from source
     */
    private Map<Station, Integer> dijkstraFromSource(Station source) {
        Map<Station, Integer> distances = new HashMap<>();
        PriorityQueue<StationDistance> queue = new PriorityQueue<>(Comparator.comparing(sd -> sd.distance));
        Set<Station> visited = new HashSet<>();
        
        // Initialize distances
        for (Station station : stationsList) {
            distances.put(station, Integer.MAX_VALUE);
        }
        distances.put(source, 0);
        queue.add(new StationDistance(source, 0));
        
        // Dijkstra's algorithm
        while (!queue.isEmpty()) {
            StationDistance current = queue.poll();
            
            if (visited.contains(current.station)) {
                continue;
            }
            
            visited.add(current.station);
            
            // Explore neighbors
            for (RailwayLine line : network.getLinesForStation(current.station)) {
                Station neighbor = line.getOtherStation(current.station);
                
                if (!visited.contains(neighbor)) {
                    int newDistance = current.distance + line.getDistance();
                    
                    if (newDistance < distances.get(neighbor)) {
                        distances.put(neighbor, newDistance);
                        queue.add(new StationDistance(neighbor, newDistance));
                    }
                }
            }
        }
        
        return distances;
    }
}