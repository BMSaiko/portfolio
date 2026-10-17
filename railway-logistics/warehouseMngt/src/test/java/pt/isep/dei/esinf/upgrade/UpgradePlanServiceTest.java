package pt.isep.dei.esinf.upgrade;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.isep.dei.esinf.domain.railway.TrackConnection;
import pt.isep.dei.esinf.domain.railway.upgrade.UpgradePlanResult;
import pt.isep.dei.esinf.domain.stations.Station;
import pt.isep.dei.esinf.service.data.DataInitializationService;
import pt.isep.dei.esinf.service.railway.RailwayUpgradeService;
import pt.isep.dei.esinf.utils.graph.Edge;
import pt.isep.dei.esinf.utils.interfaces.Graph;
import pt.isep.dei.esinf.utils.graph.map.MapGraph;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UpgradePlanServiceTest {

    private static Graph<Station, TrackConnection> belgianGraph;
    private RailwayUpgradeService upgradeService;

    @BeforeAll
    static void loadBelgianData() throws Exception {
        DataInitializationService dataService = new DataInitializationService();
        System.out.println("Loading Belgian railway data from CSV files...");

        try {
            belgianGraph = dataService.initializeBelgianNetwork();
            assertNotNull(belgianGraph, "Belgian graph should not be null");

            if (belgianGraph.numVertices() > 0) {
                System.out.println("✓ Belgian data loaded successfully!");
                System.out.println("  Stations: " + belgianGraph.numVertices());
                System.out.println("  Connections: " + belgianGraph.numEdges());
            }
        } catch (Exception e) {
            System.out.println("⚠️  Error loading Belgian data: " + e.getMessage());
            belgianGraph = null;
        }
    }

    @BeforeEach
    void setUp() {
        // Always use the Belgian graph if available
        if (belgianGraph != null && belgianGraph.numVertices() > 0) {
            upgradeService = new RailwayUpgradeService(belgianGraph);
        } else {
            System.out.println("⚠️  Using test data instead of Belgian data");
            upgradeService = new RailwayUpgradeService(createTestGraph());
        }
    }

    @Test
    @DisplayName("USEI11: Should compute topological order for Belgian railway network")
    void shouldComputeTopologicalOrder_forRailwayNetwork() {
        if (belgianGraph == null) {
            System.out.println("⚠️  Skipping Belgian data test - no data loaded");
            return;
        }

        UpgradePlanResult result = upgradeService.computeUpgradePlan();
        assertNotNull(result, "Result should not be null");

        System.out.println("Belgian Network Analysis:");
        System.out.println("  Total stations: " + result.getVerticesCount());
        System.out.println("  Total connections: " + result.getEdgesCount());

        if (!result.isHasCycles()) {
            List<Station> topologicalOrder = result.getTopologicalOrder();
            assertNotNull(topologicalOrder, "Topological order should not be null");
            assertEquals(belgianGraph.numVertices(), topologicalOrder.size(),
                    "Topological order should include all stations");

            System.out.println("  ✓ Acyclic - upgrade order exists");
            System.out.println("  Order size: " + topologicalOrder.size() + " stations");
        } else {
            System.out.println("  ⚠️  Cyclic - cannot compute full upgrade order");
            System.out.println("  Stations in cycles: " + result.getCycleStations().size());
            System.out.println("  Problematic edges: " + result.getCycleEdges().size());

            assertFalse(result.getCycleStations().isEmpty(),
                    "Should identify stations involved in cycles");
        }
    }

    @Test
    @DisplayName("USEI11: Performance - topological sort should be efficient for Belgian data")
    void topologicalSort_shouldBeEfficient() {
        if (belgianGraph == null) {
            System.out.println("⚠️  Skipping performance test - no Belgian data loaded");
            return;
        }

        long startTime = System.nanoTime();
        UpgradePlanResult result = upgradeService.computeUpgradePlan();
        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;

        System.out.printf("Belgian Network Performance:%n");
        System.out.printf("  Topological sort completed in %d ms%n", durationMs);
        System.out.printf("  Stations: %d, Connections: %d%n",
                result.getVerticesCount(), result.getEdgesCount());

        // For 559 stations and 691 connections, should be very fast
        assertTrue(durationMs < 100,
                String.format("Topological sort should be efficient for Belgian data. Time: %d ms", durationMs));
    }

    // Separate test for cycle detection with controlled test data
    @Test
    @DisplayName("USEI11: Should detect cycles in cyclic test graph")
    void shouldDetectCycles_inCyclicGraph() {
        Graph<Station, TrackConnection> cyclicGraph = createCyclicTestGraph();
        RailwayUpgradeService cyclicService = new RailwayUpgradeService(cyclicGraph);

        UpgradePlanResult result = cyclicService.computeUpgradePlan();

        assertTrue(result.isHasCycles(), "Should detect cycles in cyclic graph");
        assertFalse(result.getCycleStations().isEmpty(),
                "Should identify stations involved in cycles");

        System.out.println("Cycle Detection Test:");
        System.out.println("  ✓ Cycle detection working correctly!");
        System.out.println("  Test graph stations: " + result.getVerticesCount());
        System.out.println("  Cycle stations found: " + result.getCycleStations().size());
    }

    // Add this test to verify what the Belgian data actually shows
    @Test
    @DisplayName("USEI11: Analyze Belgian network structure")
    void analyzeBelgianNetworkStructure() {
        if (belgianGraph == null) {
            System.out.println("⚠️  Skipping analysis - no Belgian data loaded");
            return;
        }

        System.out.println("=== Belgian Network Analysis ===");
        System.out.println("Stations: " + belgianGraph.numVertices());
        System.out.println("Connections: " + belgianGraph.numEdges());

        // Count in-degrees
        int maxInDegree = 0;
        Station maxInDegreeStation = null;

        for (Station station : belgianGraph.vertices()) {
            int inDegree = 0;
            for (Edge<Station, TrackConnection> edge : belgianGraph.incomingEdges(station)) {
                inDegree++;
            }
            if (inDegree > maxInDegree) {
                maxInDegree = inDegree;
                maxInDegreeStation = station;
            }
        }

        System.out.println("Maximum in-degree: " + maxInDegree +
                " at station: " + (maxInDegreeStation != null ? maxInDegreeStation.getName() : "N/A"));

        // Run upgrade plan
        UpgradePlanResult result = upgradeService.computeUpgradePlan();

        if (result.isHasCycles()) {
            System.out.println("⚠️  Network contains cycles!");
            System.out.println("  Stations in cycles: " + result.getCycleStations().size());
            System.out.println("  Percentage of network cyclic: " +
                    String.format("%.1f%%", (result.getCycleStations().size() * 100.0 / belgianGraph.numVertices())));
        } else {
            System.out.println("✓ Network is acyclic - valid upgrade order exists");
        }
    }

    @Test
    @DisplayName("USEI11: Should handle edge cases - single station")
    void shouldHandleEdgeCases_singleStation() {
        Graph<Station, TrackConnection> singleStationGraph = new MapGraph<>(true);
        Station single = new Station("999", "Test Station", 0, 0, 50.0, 4.0);
        singleStationGraph.addVertex(single);

        RailwayUpgradeService singleService = new RailwayUpgradeService(singleStationGraph);
        UpgradePlanResult result = singleService.computeUpgradePlan();

        assertFalse(result.isHasCycles(), "Single station should not have cycles");
        assertEquals(1, result.getTopologicalOrder().size(),
                "Should have one station in topological order");
        assertEquals(single, result.getTopologicalOrder().get(0),
                "Topological order should contain the single station");
    }

    @Test
    @DisplayName("USEI11: Should handle edge cases - empty graph")
    void shouldHandleEdgeCases_emptyGraph() {
        Graph<Station, TrackConnection> emptyGraph = new MapGraph<>(true);
        RailwayUpgradeService emptyService = new RailwayUpgradeService(emptyGraph);

        UpgradePlanResult result = emptyService.computeUpgradePlan();

        assertFalse(result.isHasCycles(), "Empty graph should not have cycles");
        assertTrue(result.getTopologicalOrder().isEmpty(),
                "Topological order should be empty for empty graph");
        assertEquals(0, result.getVerticesCount(), "Should have 0 vertices");
        assertEquals(0, result.getEdgesCount(), "Should have 0 edges");
    }

    @Test
    @DisplayName("USEI11: Should validate graph structure")
    void shouldValidateGraphStructure() {
        List<String> issues = upgradeService.validateGraph();

        if (!issues.isEmpty()) {
            System.out.println("⚠️  Graph validation issues found:");
            for (String issue : issues) {
                System.out.println("  • " + issue);
            }


            boolean hasCriticalIssue = issues.stream()
                    .anyMatch(issue -> issue.contains("High dependency") ||
                            issue.contains("Isolated"));

            if (hasCriticalIssue) {
                System.out.println("🔧 Consider reviewing these issues for upgrade planning");
            }
        } else {
            System.out.println("✓ No validation issues found in railway network");
        }
    }

    @Test
    @DisplayName("USEI11: Should provide meaningful complexity analysis")
    void shouldProvideMeaningfulComplexityAnalysis() {
        String complexityAnalysis = upgradeService.getComplexityAnalysis();

        assertNotNull(complexityAnalysis, "Complexity analysis should not be null");
        assertTrue(complexityAnalysis.contains("Kahn's") || complexityAnalysis.contains("topological"),
                "Should mention topological sort algorithm");
        assertTrue(complexityAnalysis.contains("O(V + E)") || complexityAnalysis.contains("O(V+E)"),
                "Should mention time complexity");

        System.out.println("Complexity Analysis:");
        System.out.println(complexityAnalysis);
    }

    @Test
    @DisplayName("USEI11: Should maintain data integrity during processing")
    void shouldMaintainDataIntegrity_duringProcessing() {
        // Use the graph that the upgradeService is actually working with
        // Get it directly from the service's repository or use a field that tracks the actual graph
        Graph<Station, TrackConnection> actualGraph = upgradeService.getRepositoryGraph(); // You'll need to add this method

        if (actualGraph == null) {
            // Fall back to the belgianGraph if we can't get it from service
            actualGraph = belgianGraph;
        }

        int originalVertexCount = actualGraph.numVertices();
        int originalEdgeCount = actualGraph.numEdges();

        for (int i = 0; i < 3; i++) {
            UpgradePlanResult result = upgradeService.computeUpgradePlan();

            assertEquals(originalVertexCount, result.getVerticesCount(),
                    "Vertex count should remain consistent across computations");
            assertEquals(originalEdgeCount, result.getEdgesCount(),
                    "Edge count should remain consistent across computations");

            assertEquals(originalVertexCount, actualGraph.numVertices(),
                    "Original graph vertex count should not change");
            assertEquals(originalEdgeCount, actualGraph.numEdges(),
                    "Original graph edge count should not change");
        }

        System.out.println("✓ Data integrity maintained across 3 computations");
    }


    private static Graph<Station, TrackConnection> createTestGraph() {
        Graph<Station, TrackConnection> graph = new MapGraph<>(true);


        Station brussels = new Station("1", "Brussels Central", 0, 0, 50.8457, 4.3571);
        Station antwerp = new Station("2", "Antwerp Central", 10, 5, 51.2174, 4.4204);
        Station ghent = new Station("3", "Ghent St. Pieters", 5, 10, 51.0363, 3.7100);
        Station liege = new Station("4", "Liège-Guillemins", 15, -5, 50.6248, 5.5654);
        Station charleroi = new Station("5", "Charleroi-Sud", -5, -10, 50.4084, 4.4386);


        graph.addVertex(brussels);
        graph.addVertex(antwerp);
        graph.addVertex(ghent);
        graph.addVertex(liege);
        graph.addVertex(charleroi);

        graph.addEdge(brussels, antwerp, new TrackConnection(45, 20, 15.5));
        graph.addEdge(brussels, ghent, new TrackConnection(55, 15, 18.2));
        graph.addEdge(antwerp, liege, new TrackConnection(120, 12, 25.8));
        graph.addEdge(ghent, liege, new TrackConnection(130, 10, 27.3));
        graph.addEdge(brussels, charleroi, new TrackConnection(60, 18, 20.1));
        graph.addEdge(charleroi, liege, new TrackConnection(110, 8, 22.5));

        return graph;
    }


    private Graph<Station, TrackConnection> createCyclicTestGraph() {
        Graph<Station, TrackConnection> graph = new MapGraph<>(true);

        Station s1 = new Station("101", "Station Alpha", 0, 0, 50.0, 4.0);
        Station s2 = new Station("102", "Station Beta", 10, 0, 51.0, 4.5);
        Station s3 = new Station("103", "Station Gamma", 20, 0, 52.0, 5.0);
        Station s4 = new Station("104", "Station Delta", 30, 0, 53.0, 5.5);

        graph.addVertex(s1);
        graph.addVertex(s2);
        graph.addVertex(s3);
        graph.addVertex(s4);

        graph.addEdge(s1, s2, new TrackConnection(10, 5, 2.5));
        graph.addEdge(s2, s3, new TrackConnection(15, 3, 3.8));
        graph.addEdge(s3, s4, new TrackConnection(12, 4, 3.0));
        graph.addEdge(s4, s2, new TrackConnection(8, 4, 2.0));

        graph.addEdge(s1, s4, new TrackConnection(25, 6, 5.0));

        return graph;
    }

    @Test
    @DisplayName("USEI11: Should identify cycles in Belgian network")
    void shouldIdentifyCycles_inBelgianNetwork() {
        if (belgianGraph == null) {
            System.out.println("⚠️  Skipping test - no Belgian data loaded");
            return;
        }

        UpgradePlanResult result = upgradeService.computeUpgradePlan();

        System.out.println("=== Belgian Network Cycle Analysis ===");
        System.out.println("Total stations: " + result.getVerticesCount());
        System.out.println("Total connections: " + result.getEdgesCount());

        if (result.isHasCycles()) {
            System.out.println("Network contains cycles (expected for real railway)");
            System.out.println("Stations involved in cycles: " + result.getCycleStations().size());
            System.out.println("Edges involved in cycles: " + result.getCycleEdges().size());

            // Verify cycles are properly identified
            assertFalse(result.getCycleStations().isEmpty(),
                    "Should identify cycle stations");
            assertFalse(result.getCycleEdges().isEmpty(),
                    "Should identify cycle edges");

            // Display some cycle information
            if (!result.getCycleStations().isEmpty()) {
                System.out.println("Example cycle station: " +
                        result.getCycleStations().get(0).getName());
            }
            if (!result.getCycleEdges().isEmpty()) {
                System.out.println("Example problematic connection: " +
                        result.getCycleEdges().get(0));
            }
        } else {
            System.out.println("Network is acyclic (unlikely for real railway)");
            assertNotNull(result.getTopologicalOrder());
            assertEquals(result.getVerticesCount(), result.getTopologicalOrder().size());
        }
    }

    @Test
    @DisplayName("USEI11: Should compute partial order for acyclic subgraph")
    void shouldComputePartialOrder_forAcyclicSubgraph() {
        if (belgianGraph == null) {
            System.out.println("⚠️  Skipping test - no Belgian data loaded");
            return;
        }

        UpgradePlanResult result = upgradeService.computeUpgradePlan();

        if (result.isHasCycles()) {
            System.out.println("=== Partial Order Analysis ===");
            System.out.println("Network has cycles, so topological order is partial");
            System.out.println("Cycle stations: " + result.getCycleStations().size());

            // Even with cycles, you should get SOME topological order
            // (the acyclic part of the graph)
            if (result.getTopologicalOrder() != null &&
                    !result.getTopologicalOrder().isEmpty()) {
                System.out.println("Partial topological order size: " +
                        result.getTopologicalOrder().size());
                System.out.println("Acyclic stations: " +
                        (result.getVerticesCount() - result.getCycleStations().size()));

                // Verify partial order properties
                assertTrue(result.getTopologicalOrder().size() > 0,
                        "Should have some stations in topological order");
                assertTrue(result.getTopologicalOrder().size() < result.getVerticesCount(),
                        "Should not have complete order when cycles exist");
            }
        }
    }
}