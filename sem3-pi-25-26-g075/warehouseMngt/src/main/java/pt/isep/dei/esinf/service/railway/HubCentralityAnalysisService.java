package pt.isep.dei.esinf.service.railway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pt.isep.dei.esinf.domain.railway.TrackConnection;
import pt.isep.dei.esinf.domain.results.HubScoreResult;
import pt.isep.dei.esinf.domain.stations.Station;
import pt.isep.dei.esinf.repository.RailwayNetworkRepository;
import pt.isep.dei.esinf.utils.graph.Algorithms;
import pt.isep.dei.esinf.utils.interfaces.Graph;

public class HubCentralityAnalysisService {

    private final Graph<Station, TrackConnection> graph;
    private final RailwayNetworkRepository railwayNetworkRepository;

    public HubCentralityAnalysisService() {
        railwayNetworkRepository = RailwayNetworkRepository.getInstance();
        this.graph = railwayNetworkRepository.getNetworkGraph();
    }

    public HubCentralityAnalysisService(Graph<Station, TrackConnection> graph) {
        this.graph = graph;
        this.railwayNetworkRepository = RailwayNetworkRepository.getInstance();
    }

    /**
     * Betweenness centrality measures how often a station appears on shortest paths between other stations.
     * Higher values indicate stations that act as bridges or connectors in the network.
     * 
     * @return Map of stations to their betweenness centrality scores (unnormalized)
     */
    public Map<Station, Double> betweennessCentrality() {
        Map<Station, Double> betweenness = new HashMap<>();


        for (Station v : graph.vertices()) {
            betweenness.putIfAbsent(v, 0.0);
        }

        // For each source station
        for (Station source : graph.vertices()) {
            ArrayList<LinkedList<Station>> paths = new ArrayList<>();
            ArrayList<TrackConnection> distances = new ArrayList<>();
            
            Algorithms.shortestPaths(
                    graph,
                    source,
                    Comparator.comparingDouble(TrackConnection::getDistance),
                    (a, b) -> new TrackConnection(a.getDistance() + b.getDistance(), 0, 0),
                    new TrackConnection(0.0, 0, 0),
                    paths,
                    distances
            );

            for (int destinationIndex = 0; destinationIndex < graph.numVertices(); destinationIndex++) {
                Station dest = graph.vertex(destinationIndex);
                
                if (source.equals(dest) || distances.get(destinationIndex) == null) {
                    continue;
                }

                LinkedList<Station> path = paths.get(destinationIndex);
                
                for (int i = 1; i < path.size() - 1; i++) {
                    Station intermediate = path.get(i);
                    betweenness.put(intermediate, betweenness.get(intermediate) + 1.0);
                }
            }
        }

        return betweenness;
    }

    /**
     * Harmonic closeness centrality measures how easily a station can reach all other stations.
     * 
     * @return Map of stations to their harmonic closeness centrality scores (unnormalized)
     */
    public Map<Station, Double> closenessCentrality() {
        Map<Station, Double> closeness = new HashMap<>();

        // For each station, compute sum of inverse distances
        for (Station source : graph.vertices()) {
            double harmonicSum = 0.0;

            ArrayList<LinkedList<Station>> paths = new ArrayList<>();
            ArrayList<TrackConnection> distances = new ArrayList<>();

            Algorithms.shortestPaths(
                    graph,
                    source,
                    Comparator.comparingDouble(TrackConnection::getDistance),
                    (a, b) -> new TrackConnection(a.getDistance() + b.getDistance(), 0, 0),
                    new TrackConnection(0.0, 0, 0),
                    paths,
                    distances
            );

            // Sum up 1/distance for all reachable stations
            for (int destinationIndex = 0; destinationIndex < graph.numVertices(); destinationIndex++) {
                TrackConnection distance = distances.get(destinationIndex);

                if (distance != null && distance.getDistance() > 0) {
                    harmonicSum += 1 / distance.getDistance();
                }
            }

            closeness.put(source, harmonicSum);
        }

        return closeness;
    }


    /**
     * Normalize values in a map to the range [0,1].
     * Formula: normalized(v) = (value(v) - min) / (max - min)
     * If all values are the same, returns 0.0 for all.
     * 
     * @param values Map of values to normalize
     * @return Map with normalized values in [0,1]
     */
    private Map<Station, Double> normalize(Map<Station, Double> values) {
        if (values.isEmpty()) {
            return new HashMap<>();
        }

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        
        for (Double value : values.values()) {
            if (value < min) min = value;
            if (value > max) max = value;
        }

        Map<Station, Double> normalized = new HashMap<>();
        double range = max - min;
        
        // If all values are the same, set all to 0
        if (range == 0) {
            for (Station station : values.keySet()) {
                normalized.put(station, 0.0);
            }
        } else {
            for (Map.Entry<Station, Double> entry : values.entrySet()) {
                double normalizedValue = (entry.getValue() - min) / range;
                normalized.put(entry.getKey(), normalizedValue);
            }
        }
        
        return normalized;
    }

    /**
     * Normalize integer values to the range [0,1].
     */
    private Map<Station, Double> normalizeInt(Map<Station, Integer> values) {
        Map<Station, Double> doubleValues = new HashMap<>();
        for (Map.Entry<Station, Integer> entry : values.entrySet()) {
            doubleValues.put(entry.getKey(), entry.getValue().doubleValue());
        }
        return normalize(doubleValues);
    }


    /**
     * Compute degree centrality (strength) for all stations.
     * Degree is the sum of in-degree and out-degree for each station.
     * @return Map of stations to their degree centrality (strength) values
     */
    public Map<Station, Integer> centrality() {
        Map<Station, Integer> degreeMap = new HashMap<>();
        for (Station station : graph.vertices()) {
            int deg = graph.outDegree(station) + graph.inDegree(station);
            degreeMap.put(station, deg);
        }
        return degreeMap;
    }

    /**
     * Compute hub ranking for all stations based on multiple centrality metrics.
     * Combines normalized betweenness, harmonic closeness, and degree/strength.
     * <br>
     * HubScore formula: 0.35*betweenness_norm + 0.35*harmonic_closeness_norm + 0.30*strength_norm
     * <br>
     * All metrics are normalized to [0,1] before combination.
     * 
     * @return List of HubScoreResult objects sorted by hub score (descending)
     */
    public List<HubScoreResult> computeHubRanking() {
        System.out.println("Computing degree/strength...");
        Map<Station, Integer> degreeRaw = centrality();

        System.out.println("Computing betweenness...");
        Map<Station, Double> betweennessRaw = betweennessCentrality();

        System.out.println("Computing harmonic closeness...");
        Map<Station, Double> closenessRaw = closenessCentrality();

        System.out.println("Normalizing metrics to [0,1]...");
        Map<Station, Double> degreeNorm = normalizeInt(degreeRaw);
        Map<Station, Double> betweennessNorm = normalize(betweennessRaw);
        Map<Station, Double> closenessNorm = normalize(closenessRaw);

        System.out.println("Computing hub scores...");

        Map<Station, Double> hubScore = new HashMap<>();
        for (Station v : graph.vertices()) {
            double score = (0.35 * betweennessNorm.get(v)) + 
                          (0.35 * closenessNorm.get(v)) + 
                          (0.30 * degreeNorm.get(v));
            hubScore.put(v, score);
        }

        List<HubScoreResult> results = new ArrayList<>();
        for (Station v : graph.vertices()) {
            HubScoreResult info = new HubScoreResult(
                    v.getId(),
                    v.getName(),
                    degreeRaw.get(v),
                    betweennessRaw.get(v),
                    closenessRaw.get(v),
                    hubScore.get(v)
            );
            results.add(info);
        }

        Collections.sort(results);
        return results;
    }
}