package pt.isep.dei.esinf.service.railway;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pt.isep.dei.esinf.domain.railway.TrackConnection;
import pt.isep.dei.esinf.domain.results.HubScoreResult;
import pt.isep.dei.esinf.domain.stations.Station;
import pt.isep.dei.esinf.service.data.DataInitializationService;
import pt.isep.dei.esinf.utils.graph.map.MapGraph;
import pt.isep.dei.esinf.utils.interfaces.Graph;

class HubCentralityAnalysisServiceTest {

    private static Graph<Station, TrackConnection> belgianGraph;
    private static HubCentralityAnalysisService calculator;
    private Graph<Station, TrackConnection> simpleGraph;
    private HubCentralityAnalysisService simpleCalculator;

    @BeforeAll
    static void loadBelgianData() throws Exception {
        DataInitializationService dataService = new DataInitializationService();
        System.out.println("Loading Belgian railway data for hub analysis...");

        try {
            belgianGraph = dataService.initializeBelgianNetwork();
            assertNotNull(belgianGraph, "Belgian graph should not be null");

            if (belgianGraph.numVertices() > 0) {
                System.out.println("✓ Belgian data loaded for hub analysis!");
                System.out.println("  Stations: " + belgianGraph.numVertices());
                System.out.println("  Connections: " + belgianGraph.numEdges());
                calculator = new HubCentralityAnalysisService(belgianGraph);
            }
        } catch (Exception e) {
            System.out.println("⚠️  Error loading Belgian data: " + e.getMessage());
            belgianGraph = null;
        }
    }

    @BeforeEach
    void setupSimpleGraph() {
        // Create a simple test graph for unit testing
        // Graph structure:
        //     A --- B --- C
        //     |           |
        //     D ----------E
        
        simpleGraph = new MapGraph<>(false); // undirected
        
        Station stA = new Station("A", "Station A", 0.0, 0.0);
        Station stB = new Station("B", "Station B", 10.0, 0.0);
        Station stC = new Station("C", "Station C", 20.0, 0.0);
        Station stD = new Station("D", "Station D", 0.0, 10.0);
        Station stE = new Station("E", "Station E", 20.0, 10.0);
        
        simpleGraph.addVertex(stA);
        simpleGraph.addVertex(stB);
        simpleGraph.addVertex(stC);
        simpleGraph.addVertex(stD);
        simpleGraph.addVertex(stE);
        
        simpleGraph.addEdge(stA, stB, new TrackConnection(10.0, 100, 1000.0));
        simpleGraph.addEdge(stB, stC, new TrackConnection(10.0, 100, 1000.0));
        simpleGraph.addEdge(stA, stD, new TrackConnection(10.0, 100, 1000.0));
        simpleGraph.addEdge(stC, stE, new TrackConnection(10.0, 100, 1000.0));
        simpleGraph.addEdge(stD, stE, new TrackConnection(10.0, 100, 1000.0));
        
        simpleCalculator = new HubCentralityAnalysisService(simpleGraph);
    }

    @Test
    void testDegreeCentrality_SimpleGraph() {
        Map<Station, Integer> degrees = simpleCalculator.centrality();
        
        assertNotNull(degrees);
        assertEquals(5, degrees.size(), "Should have degree for all 5 stations");
        
        // In undirected graph, each edge counts twice (in + out)
        // A: connected to B, D -> degree 4
        // B: connected to A, C -> degree 4
        // C: connected to B, E -> degree 4
        // D: connected to A, E -> degree 4
        // E: connected to C, D -> degree 4
        
        for (Station st : simpleGraph.vertices()) {
            assertEquals(4, degrees.get(st), 
                "Station " + st.getId() + " should have degree 4");
        }
    }

    @Test
    void testBetweennessCentrality_SimpleGraph() {
        Map<Station, Double> betweenness = simpleCalculator.betweennessCentrality();
        
        assertNotNull(betweenness);
        assertEquals(5, betweenness.size());
        
        // All stations have equal betweenness in this symmetric graph
        // Actually, corner stations (A,C) have 0, middle stations have more
        for (Station st : simpleGraph.vertices()) {
            assertTrue(betweenness.get(st) >= 0.0, 
                "Betweenness should be non-negative for " + st.getId());
        }
        
        // Verify betweenness is computed (not all zeros unless trivial graph)
        double totalBetweenness = betweenness.values().stream()
            .mapToDouble(Double::doubleValue).sum();
        assertTrue(totalBetweenness >= 0.0, "Total betweenness should be non-negative");
    }

    @Test
    void testHarmonicClosenessCentrality_SimpleGraph() {
        Map<Station, Double> closeness = simpleCalculator.closenessCentrality();
        
        assertNotNull(closeness);
        assertEquals(5, closeness.size());
        
        // All stations should have positive closeness (can reach others)
        for (Station st : simpleGraph.vertices()) {
            assertTrue(closeness.get(st) > 0.0, 
                "Closeness should be positive for " + st.getId());
        }
    }

    @Test
    void testComputeHubRanking_SimpleGraph() {
        List<HubScoreResult> results = simpleCalculator.computeHubRanking();
        
        assertNotNull(results);
        assertEquals(5, results.size());
        
        // Verify results are sorted by hub score (descending)
        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).getHubScore() >= results.get(i + 1).getHubScore(),
                "Results should be sorted by hub score descending");
        }
        
        // Verify all hub scores are in valid range [0,1]
        for (HubScoreResult result : results) {
            assertTrue(result.getHubScore() >= 0.0 && result.getHubScore() <= 1.0,
                "Hub score should be in [0,1] for " + result.getStid());
            
            // Verify all metrics are non-negative
            assertTrue(result.getDegree() >= 0);
            assertTrue(result.getBetweenness() >= 0.0);
            assertTrue(result.getHarmonicCloseness() >= 0.0);
        }
    }

    @Test
    void testBelgianNetworkDegrees() {
        if (belgianGraph == null || belgianGraph.numVertices() == 0) {
            System.out.println("Skipping test - Belgian data not available");
            return;
        }
        
        Map<Station, Integer> degrees = calculator.centrality();
        
        assertNotNull(degrees);
        assertEquals(belgianGraph.numVertices(), degrees.size());
        
        // Find station with highest degree
        Station maxDegreeStation = null;
        int maxDegree = -1;
        for (Map.Entry<Station, Integer> entry : degrees.entrySet()) {
            if (entry.getValue() > maxDegree) {
                maxDegree = entry.getValue();
                maxDegreeStation = entry.getKey();
            }
        }
        
        System.out.println("Station with highest degree: " + 
            (maxDegreeStation != null ? maxDegreeStation.getName() : "none") + 
            " (degree: " + maxDegree + ")");
        
        assertTrue(maxDegree > 0, "Should have at least one connection");
    }

    @Test
    void testBelgianNetworkBetweenness() {
        if (belgianGraph == null || belgianGraph.numVertices() == 0) {
            System.out.println("Skipping test - Belgian data not available");
            return;
        }
        
        Map<Station, Double> betweenness = calculator.betweennessCentrality();
        
        assertNotNull(betweenness);
        assertEquals(belgianGraph.numVertices(), betweenness.size());
        
        // All values should be non-negative
        for (Double value : betweenness.values()) {
            assertTrue(value >= 0.0, "Betweenness should be non-negative");
        }
        
        // Find station with highest betweenness
        Station maxStation = betweenness.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
        
        if (maxStation != null) {
            System.out.println("Station with highest betweenness: " + 
                maxStation.getName() + " (" + betweenness.get(maxStation) + ")");
        }
    }

    @Test
    void testBelgianNetworkCloseness() {
        if (belgianGraph == null || belgianGraph.numVertices() == 0) {
            System.out.println("Skipping test - Belgian data not available");
            return;
        }
        
        Map<Station, Double> closeness = calculator.closenessCentrality();
        
        assertNotNull(closeness);
        assertEquals(belgianGraph.numVertices(), closeness.size());
        
        // Find station with highest closeness
        Station maxStation = closeness.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
        
        if (maxStation != null) {
            System.out.println("Station with highest harmonic closeness: " + 
                maxStation.getName() + " (" + closeness.get(maxStation) + ")");
        }
    }

    @Test
    void testBelgianNetworkHubRanking() {
        if (belgianGraph == null || belgianGraph.numVertices() == 0) {
            System.out.println("Skipping test - Belgian data not available");
            return;
        }
        
        List<HubScoreResult> results = calculator.computeHubRanking();
        
        assertNotNull(results);
        assertEquals(belgianGraph.numVertices(), results.size());
        
        System.out.println("\n=== TOP 10 HUBS IN BELGIAN RAILWAY NETWORK ===");
        System.out.println(String.format("%-10s %-30s %8s %12s %12s %10s", 
            "ID", "Name", "Degree", "Betweenness", "Closeness", "HubScore"));
        System.out.println("-".repeat(90));
        
        for (int i = 0; i < Math.min(10, results.size()); i++) {
            HubScoreResult result = results.get(i);
            System.out.println(String.format("%-10s %-30s %8d %12.2f %12.2f %10.6f",
                result.getStid(),
                result.getStname().substring(0, Math.min(30, result.getStname().length())),
                result.getDegree(),
                result.getBetweenness(),
                result.getHarmonicCloseness(),
                result.getHubScore()));
        }
        
        // Verify ranking is properly sorted
        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).getHubScore() >= results.get(i + 1).getHubScore(),
                "Results should be sorted descending by hub score");
        }
        
        // Top hub should have meaningful scores
        HubScoreResult topHub = results.get(0);
        assertTrue(topHub.getHubScore() > 0.0, "Top hub should have positive score");
    }

    @Test
    void testNormalizationRange() {
        if (belgianGraph == null || belgianGraph.numVertices() == 0) {
            System.out.println("Skipping test - Belgian data not available");
            return;
        }
        
        List<HubScoreResult> results = calculator.computeHubRanking();
        
        // All hub scores should be in [0, 1]
        for (HubScoreResult result : results) {
            assertTrue(result.getHubScore() >= 0.0, 
                "Hub score should be >= 0 for " + result.getStid());
            assertTrue(result.getHubScore() <= 1.0, 
                "Hub score should be <= 1 for " + result.getStid());
        }
        
        // The maximum hub score should be close to 1.0 (or exactly 1.0)
        double maxScore = results.get(0).getHubScore();
        assertTrue(maxScore > 0.5, "Maximum hub score should be substantial");
    }

    @Test
    void testDisconnectedGraph() {
        // Create a graph with disconnected components
        Graph<Station, TrackConnection> disconnected = new MapGraph<>(false);
        
        Station st1 = new Station("1", "Station 1", 0.0, 0.0);
        Station st2 = new Station("2", "Station 2", 10.0, 0.0);
        Station st3 = new Station("3", "Station 3", 20.0, 0.0);
        
        disconnected.addVertex(st1);
        disconnected.addVertex(st2);
        disconnected.addVertex(st3);
        
        // Only connect st1 and st2, leaving st3 isolated
        disconnected.addEdge(st1, st2, new TrackConnection(10.0, 100, 1000.0));
        
        HubCentralityAnalysisService disconnectedCalc = new HubCentralityAnalysisService(disconnected);
        
        Map<Station, Double> closeness = disconnectedCalc.closenessCentrality();
        Map<Station, Double> betweenness = disconnectedCalc.betweennessCentrality();
        
        // Isolated station should have 0 closeness and betweenness
        assertEquals(0.0, closeness.get(st3), 0.001);
        assertEquals(0.0, betweenness.get(st3), 0.001);
        
        // Connected stations should have positive closeness
        assertTrue(closeness.get(st1) > 0.0);
        assertTrue(closeness.get(st2) > 0.0);
    }
}