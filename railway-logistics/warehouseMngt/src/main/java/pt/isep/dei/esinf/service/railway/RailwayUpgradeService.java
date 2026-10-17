package pt.isep.dei.esinf.service.railway;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import pt.isep.dei.esinf.domain.railway.TrackConnection;
import pt.isep.dei.esinf.domain.railway.upgrade.UpgradePlanResult;
import pt.isep.dei.esinf.domain.stations.Station;
import pt.isep.dei.esinf.repository.RailwayNetworkRepository;
import pt.isep.dei.esinf.utils.graph.Edge;
import pt.isep.dei.esinf.utils.interfaces.Graph;

/**
 * Service for railway upgrade planning
 */
public class RailwayUpgradeService {
    private final RailwayNetworkRepository repository;
    private final Map<Station, Integer> inDegree;
    private final Map<Station, List<Station>> adjacencyList;

    public RailwayUpgradeService() {
        this.repository = RailwayNetworkRepository.getInstance();
        this.inDegree = new HashMap<>();
        this.adjacencyList = new HashMap<>();
    }

    /**
     * Constructor for backward compatibility or testing with specific graph
     */
    public RailwayUpgradeService(Graph<Station, TrackConnection> graph) {
        this.repository = RailwayNetworkRepository.getInstance();
        this.repository.setNetworkGraph(graph);
        this.inDegree = new HashMap<>();
        this.adjacencyList = new HashMap<>();
    }

    /**
     * Build internal structures for topological sorting
     */
    private void buildInternalStructures() {
        Graph<Station, TrackConnection> graph = repository.getNetworkGraph();

        inDegree.clear();
        adjacencyList.clear();

        for (Station station : graph.vertices()) {
            inDegree.put(station, 0);
            adjacencyList.put(station, new ArrayList<>());
        }

        for (Station from : graph.vertices()) {
            for (Edge<Station, TrackConnection> edge : graph.outgoingEdges(from)) {
                Station to = edge.getVDest();
                adjacencyList.get(from).add(to);
                inDegree.put(to, inDegree.get(to) + 1);
            }
        }
    }

    /**
     * Compute upgrade plan using topological sort (Kahn's algorithm)
     */
    public UpgradePlanResult computeUpgradePlan() {
        buildInternalStructures();
        Graph<Station, TrackConnection> graph = repository.getNetworkGraph();

        UpgradePlanResult result = new UpgradePlanResult();
        result.setVerticesCount(graph.numVertices());
        result.setEdgesCount(graph.numEdges());

        List<Station> topologicalOrder = new ArrayList<>();
        Queue<Station> queue = new LinkedList<>();

        Map<Station, Integer> tempInDegree = new HashMap<>(inDegree);

        for (Station station : graph.vertices()) {
            if (tempInDegree.get(station) == 0) {
                queue.add(station);
            }
        }

        while (!queue.isEmpty()) {
            Station current = queue.poll();
            topologicalOrder.add(current);

            for (Station neighbor : adjacencyList.get(current)) {
                int newInDegree = tempInDegree.get(neighbor) - 1;
                tempInDegree.put(neighbor, newInDegree);

                if (newInDegree == 0) {
                    queue.add(neighbor);
                }
            }
        }

        if (topologicalOrder.size() != graph.numVertices()) {
            result.setHasCycles(true);
            result.setCycleStations(findCycleStations(tempInDegree));
            result.setCycleEdges(findCycleEdges(tempInDegree));
        } else {
            result.setTopologicalOrder(topologicalOrder);
            result.setHasCycles(false);
        }

        return result;
    }

    /**
     * Find stations involved in cycles (stations with remaining in-degree > 0)
     */
    private List<Station> findCycleStations(Map<Station, Integer> remainingInDegree) {
        List<Station> cycleStations = new ArrayList<>();
        for (Map.Entry<Station, Integer> entry : remainingInDegree.entrySet()) {
            if (entry.getValue() > 0) {
                cycleStations.add(entry.getKey());
            }
        }
        return cycleStations;
    }

    /**
     * Find edges involved in cycles
     */
    private List<String> findCycleEdges(Map<Station, Integer> remainingInDegree) {
        Graph<Station, TrackConnection> graph = repository.getNetworkGraph();
        List<String> cycleEdges = new ArrayList<>();
        Set<Station> cycleStationSet = new HashSet<>(findCycleStations(remainingInDegree));

        for (Station from : cycleStationSet) {
            for (Station to : cycleStationSet) {
                Edge<Station, TrackConnection> edge = graph.edge(from, to);
                if (edge != null) {
                    TrackConnection connection = edge.getWeight();
                    cycleEdges.add(String.format("%s -> %s (Dist: %.2f km)",
                            from.getName(), to.getName(), connection.getDistance()));
                }
            }
        }
        return cycleEdges;
    }

    /**
     * Get graph statistics
     */
    public String getGraphStatistics() {
        Graph<Station, TrackConnection> graph = repository.getNetworkGraph();
        StringBuilder sb = new StringBuilder();
        sb.append("=== Railway Network Statistics ===\n");
        sb.append(String.format("Stations: %d\n", graph.numVertices()));
        sb.append(String.format("Track Connections: %d\n", graph.numEdges()));

        double totalOutDegree = 0;
        for (Station station : graph.vertices()) {
            totalOutDegree += graph.outDegree(station);
        }
        double avgOutDegree = totalOutDegree / graph.numVertices();
        sb.append(String.format("Average Out-Degree: %.2f\n", avgOutDegree));

        return sb.toString();
    }

    /**
     * Get the graph from repository for testing purposes
     */
    public Graph<Station, TrackConnection> getRepositoryGraph() {
        return repository.getNetworkGraph();
    }

    /**
     * Get complexity analysis
     */
    public String getComplexityAnalysis() {
        Graph<Station, TrackConnection> graph = repository.getNetworkGraph();
        return """
                === Complexity Analysis ===
                Algorithm: Kahn's Topological Sort
                
                Time Complexity: O(V + E)
                - V = Number of stations (%d)
                - E = Number of connections (%d)
                - We visit each station once (O(V))
                - We traverse each connection once (O(E))
                
                Space Complexity: O(V + E)
                - We store adjacency list (O(V + E))
                - We store in-degree map (O(V))
                - Queue can hold up to V stations (O(V))
                
                Where:
                V = vertices (stations) = %d
                E = edges (connections) = %d
                """.formatted(graph.numVertices(), graph.numEdges(),
                graph.numVertices(), graph.numEdges());
    }

    /**
     * Validate graph for common issues
     */
    public List<String> validateGraph() {
        Graph<Station, TrackConnection> graph = repository.getNetworkGraph();
        List<String> issues = new ArrayList<>();

        for (Station station : graph.vertices()) {
            if (graph.outDegree(station) == 0 && graph.inDegree(station) == 0) {
                issues.add("Isolated station: " + station.getName());
            }
        }

        for (Station station : graph.vertices()) {
            if (graph.inDegree(station) > 10) {
                issues.add("High dependency station (in-degree " +
                        graph.inDegree(station) + "): " + station.getName());
            }
        }

        return issues;
    }
}