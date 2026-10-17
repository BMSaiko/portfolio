package pt.isep.dei.esinf.upgrade;

import org.junit.jupiter.api.BeforeAll;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UpgradePlanPerformanceTest {

    private static DataInitializationService dataService;
    private static Graph<Station, TrackConnection> fullBelgianGraph;

    @BeforeAll
    static void setUp() throws Exception {
        dataService = new DataInitializationService();
        fullBelgianGraph = dataService.initializeBelgianNetwork();

        assertNotNull(fullBelgianGraph, "Full Belgian graph should be loaded");
        System.out.println("Performance test setup complete");
        System.out.println("  Base graph size: " + fullBelgianGraph.numVertices() + " stations");
    }

    @Test
    @DisplayName("USEI11 Performance: Topological sort scaling with network size")
    void topologicalSort_scalingWithNetworkSize() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("TOPOLOGICAL SORT PERFORMANCE TEST");
        System.out.println("Testing with different network sizes");
        System.out.println("=".repeat(70));

        // Test with different subsets of the network
        int[] testSizes = {10, 50, 100, 500, 1000};

        for (int targetSize : testSizes) {
            // Create subset of the graph
            Graph<Station, TrackConnection> testGraph = createSubsetGraph(targetSize);

            if (testGraph.numVertices() < targetSize * 0.5) {
                System.out.printf("Skipping size %d - insufficient data (only %d stations)%n",
                        targetSize, testGraph.numVertices());
                continue;
            }

            RailwayUpgradeService service = new RailwayUpgradeService(testGraph);

            // Warm-up
            service.computeUpgradePlan();

            // Actual measurement
            long startTime = System.nanoTime();

            UpgradePlanResult result = service.computeUpgradePlan();

            long endTime = System.nanoTime();
            long durationMs = (endTime - startTime) / 1_000_000;

            System.out.printf("\nNetwork size: %d stations, %d connections%n",
                    testGraph.numVertices(), testGraph.numEdges());
            System.out.printf("  Computation time: %d ms%n", durationMs);
            System.out.printf("  Has cycles: %s%n", result.isHasCycles() ? "YES" : "NO");

            // Performance assertion - should scale linearly
            if (testGraph.numVertices() > 100) {
                assertTrue(durationMs < 5000,
                        String.format("Topological sort should scale efficiently. " +
                                        "Time: %d ms for %d stations",
                                durationMs, testGraph.numVertices()));
            }
        }
    }

    @Test
    @DisplayName("USEI11 Performance: Cycle detection efficiency")
    void cycleDetection_efficiencyTest() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("CYCLE DETECTION PERFORMANCE TEST");
        System.out.println("Testing with graphs containing cycles");
        System.out.println("=".repeat(70));

        // Test with increasing cycle complexity
        int[] cycleSizes = {3, 5, 10, 20};

        for (int cycleSize : cycleSizes) {
            Graph<Station, TrackConnection> cyclicGraph = createCyclicGraph(cycleSize);
            RailwayUpgradeService service = new RailwayUpgradeService(cyclicGraph);

            long startTime = System.nanoTime();

            UpgradePlanResult result = service.computeUpgradePlan();

            long endTime = System.nanoTime();
            long durationMs = (endTime - startTime) / 1_000_000;

            System.out.printf("\nCycle size: %d stations%n", cycleSize);
            System.out.printf("  Detection time: %d ms%n", durationMs);
            System.out.printf("  Cycles detected: %s%n", result.isHasCycles() ? "YES" : "NO");

            if (result.isHasCycles()) {
                System.out.printf("  Stations in cycles: %d%n", result.getCycleStations().size());
            }

            // Cycle detection should be fast even for larger cycles
            assertTrue(durationMs < 100,
                    String.format("Cycle detection should be efficient. " +
                                    "Time: %d ms for cycle size %d",
                            durationMs, cycleSize));
        }
    }

    @Test
    @DisplayName("USEI11 Performance: Memory usage estimation")
    void memoryUsage_estimationTest() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("MEMORY USAGE ESTIMATION");
        System.out.println("=".repeat(70));

        // Get initial memory
        Runtime runtime = Runtime.getRuntime();
        runtime.gc(); // Suggest garbage collection

        long initialMemory = runtime.totalMemory() - runtime.freeMemory();

        // Create service with full graph
        RailwayUpgradeService service = new RailwayUpgradeService(fullBelgianGraph);

        // Compute upgrade plan
        UpgradePlanResult result = service.computeUpgradePlan();

        runtime.gc(); // Suggest garbage collection again
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = finalMemory - initialMemory;

        System.out.printf("\nGraph statistics:%n");
        System.out.printf("  Stations: %d%n", fullBelgianGraph.numVertices());
        System.out.printf("  Connections: %d%n", fullBelgianGraph.numEdges());
        System.out.printf("  Estimated memory usage: ~%d MB%n", memoryUsed / (1024 * 1024));
        System.out.printf("  Has cycles: %s%n", result.isHasCycles() ? "YES" : "NO");

        // Memory usage should be reasonable (under 500MB for large networks)
        assertTrue(memoryUsed < 500 * 1024 * 1024,
                String.format("Memory usage should be reasonable. Used: %d MB",
                        memoryUsed / (1024 * 1024)));
    }

    @Test
    @DisplayName("USEI11 Performance: Repeated computations consistency")
    void repeatedComputations_consistencyTest() {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("REPEATED COMPUTATIONS CONSISTENCY");
        System.out.println("Testing result consistency across multiple runs");
        System.out.println("=".repeat(70));

        RailwayUpgradeService service = new RailwayUpgradeService(fullBelgianGraph);

        long totalTime = 0;
        long minTime = Long.MAX_VALUE;
        long maxTime = 0;
        UpgradePlanResult firstResult = null;

        int iterations = 5; // Reduced iterations for faster test

        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();

            UpgradePlanResult result = service.computeUpgradePlan();

            long endTime = System.nanoTime();
            long durationMs = (endTime - startTime) / 1_000_000;

            totalTime += durationMs;
            minTime = Math.min(minTime, durationMs);
            maxTime = Math.max(maxTime, durationMs);

            // Store first result for comparison
            if (i == 0) {
                firstResult = result;
            } else {
                // Compare results for consistency
                assertEquals(firstResult.isHasCycles(), result.isHasCycles(),
                        String.format("Cycle detection inconsistent on iteration %d", i + 1));

                if (!result.isHasCycles()) {
                    assertEquals(firstResult.getTopologicalOrder().size(),
                            result.getTopologicalOrder().size(),
                            String.format("Topological order size inconsistent on iteration %d", i + 1));
                }
            }

            System.out.printf("  Run %d: %d ms, Has cycles: %s%n",
                    i + 1, durationMs, result.isHasCycles() ? "YES" : "NO");
        }

        long avgTime = totalTime / iterations;

        System.out.printf("\nSummary (%d runs):%n", iterations);
        System.out.printf("  Average time: %d ms%n", avgTime);
        System.out.printf("  Range: %d - %d ms%n", minTime, maxTime);
        System.out.printf("  All results were consistent: ✓%n");

        // Only verify that performance is reasonable, not necessarily consistent
        assertTrue(maxTime < 30000,
                String.format("Computation should complete in reasonable time. Max: %d ms", maxTime));

        // Verify algorithm correctness through first result
        assertNotNull(firstResult, "Should have at least one result");
        assertEquals(fullBelgianGraph.numVertices(), firstResult.getVerticesCount(),
                "Vertex count should match");
        assertEquals(fullBelgianGraph.numEdges(), firstResult.getEdgesCount(),
                "Edge count should match");
    }

    // Helper method to create subset graph - CORRIGIDO
    private Graph<Station, TrackConnection> createSubsetGraph(int targetSize) {
        Graph<Station, TrackConnection> subsetGraph = new MapGraph<>(true);

        // Get vertices from full graph
        List<Station> allStations = new ArrayList<>();
        fullBelgianGraph.vertices().forEach(allStations::add);

        // Take subset
        int actualSize = Math.min(targetSize, allStations.size());
        List<Station> subset = allStations.subList(0, actualSize);

        // Add vertices to subset graph
        for (Station station : subset) {
            subsetGraph.addVertex(station);
        }

        // Add edges between vertices in subset - CORREÇÃO AQUI
        for (Station from : subset) {
            for (Station to : subset) {
                Edge<Station, TrackConnection> edge = fullBelgianGraph.edge(from, to);
                if (edge != null) {
                    subsetGraph.addEdge(from, to, edge.getWeight());
                }
            }
        }

        return subsetGraph;
    }

    // Helper method to create cyclic graph of specified size
    private Graph<Station, TrackConnection> createCyclicGraph(int size) {
        Graph<Station, TrackConnection> graph = new MapGraph<>(true);

        // Create stations
        List<Station> stations = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Station station = new Station(
                    "C" + i,
                    "Cycle Station " + i,
                    i * 10,
                    0,
                    50.0 + i * 0.1,
                    4.0 + i * 0.1
            );
            stations.add(station);
            graph.addVertex(station);
        }

        // Create a cycle
        for (int i = 0; i < size; i++) {
            int next = (i + 1) % size;
            graph.addEdge(
                    stations.get(i),
                    stations.get(next),
                    new TrackConnection(10 + i, 5, 2.5)
            );
        }

        // Add some additional edges (not part of cycle)
        if (size > 3) {
            for (int i = 0; i < size - 2; i += 2) {
                graph.addEdge(
                        stations.get(i),
                        stations.get(i + 2),
                        new TrackConnection(15, 3, 3.8)
                );
            }
        }

        return graph;
    }
}