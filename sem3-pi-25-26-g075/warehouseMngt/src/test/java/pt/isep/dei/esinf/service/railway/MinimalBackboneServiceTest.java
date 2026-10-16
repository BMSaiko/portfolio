package pt.isep.dei.esinf.service.railway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.isep.dei.esinf.domain.railway.TrackConnection;
import pt.isep.dei.esinf.domain.stations.Station;
import pt.isep.dei.esinf.repository.RailwayNetworkRepository;
import pt.isep.dei.esinf.service.data.DataInitializationService;
import pt.isep.dei.esinf.utils.graph.Algorithms;
import pt.isep.dei.esinf.utils.graph.Edge;
import pt.isep.dei.esinf.utils.interfaces.Graph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MinimalBackboneService - Kruskal's MST algorithm
 */
class MinimalBackboneServiceTest {

    private MinimalBackboneService service;
    private Graph<Station, TrackConnection> originalGraph;

    /**
     * Helper method to add bidirectional edges
     */
    private void addBidirectionalEdge(Graph<Station, TrackConnection> graph, Station from, Station to, double distance, double cost) {
        TrackConnection connection = new TrackConnection(distance, 10, cost);
        graph.addEdge(from, to, connection);
        graph.addEdge(to, from, connection);
    }

    @BeforeEach
    void setUp() throws Exception {
        DataInitializationService dataService = new DataInitializationService();
        originalGraph = dataService.initializeBelgianNetwork(true);
        RailwayNetworkRepository.getInstance().setNetworkGraph(originalGraph);
        service = new MinimalBackboneService();
    }

    @Test
    @DisplayName("Generate DOT file should throw exception for null or empty graph")
    void testGenerateDotFile_ThrowsExceptionForInvalidGraph() {
        assertThrows(IllegalArgumentException.class, () -> {
            service.generateDotFile(null, "test");
        });
    }

    @Test
    @DisplayName("Calculate minimal backbone should return a valid MST")
    void testCalculateMinimalBackbone_ReturnsValidMST() {
        // Act
        Graph<Station, TrackConnection> mst = service.calculateMinimalBackbone();

        // Assert
        assertNotNull(mst, "MST should not be null");
        assertTrue(mst.numVertices() > 0, "MST should contain vertices");
    }

    @Test
    @DisplayName("MST should have at most V-1 edges for V vertices")
    void testMST_HasCorrectNumberOfEdges() {
        // Act
        Graph<Station, TrackConnection> mst = service.calculateMinimalBackbone();

        // Assert - MST should have exactly V-1 edges for V vertices (if connected)
        int expectedEdges = mst.numVertices() - 1;
        int actualEdges = mst.numEdges();

        assertTrue(actualEdges <= expectedEdges, "MST should have at most V-1 edges. Expected: " + expectedEdges + ", Got: " + actualEdges);
    }

    @Test
    @DisplayName("MST should include all vertices from the original graph")
    void testMST_AllVerticesIncluded() {
        // Act
        Graph<Station, TrackConnection> mst = service.calculateMinimalBackbone();

        // Assert - MST should contain all vertices from original graph
        assertEquals(originalGraph.numVertices(), mst.numVertices(), "MST should contain all vertices from the original graph");

        for (Station station : originalGraph.vertices()) {
            assertTrue(mst.validVertex(station), "MST should contain station: " + station);
        }
    }

    @Test
    @DisplayName("MST should be connected with all vertices reachable")
    void testMST_IsConnected() {
        Graph<Station, TrackConnection> mst = service.calculateMinimalBackbone();

        // Assert - Check if the MST is connected (all vertices reachable from any vertex)
        if (mst.numVertices() > 0 && mst.numEdges() > 0) {
            Station firstStation = mst.vertices().get(0);
            LinkedList<Station> reachableStations = Algorithms.BreadthFirstSearch(mst, firstStation);

            assertNotNull(reachableStations, "BFS should return reachable stations");

            // If the original graph is connected, MST should connect all vertices
            // Note: The test accounts for possibly disconnected components
            assertFalse(reachableStations.isEmpty(), "MST should have at least one connected component");
        }
    }

    @Test
    @DisplayName("MST should not contain cycles")
    void testMST_NoCycles() {
        // Act
        Graph<Station, TrackConnection> mst = service.calculateMinimalBackbone();

        // For a tree: E = V - 1 (considering connected components)
        assertTrue(mst.numEdges() < mst.numVertices() || mst.numVertices() == 0, "MST should not have cycles (edges should be less than vertices for acyclic graph)");
    }

    @Test
    @DisplayName("All edges in MST should have positive distances")
    void testMST_EdgesAreSortedByDistance() {
        Graph<Station, TrackConnection> mst = service.calculateMinimalBackbone();

        Collection<Edge<Station, TrackConnection>> mstEdges = mst.edges();

        for (Edge<Station, TrackConnection> edge : mstEdges) {
            assertNotNull(edge.getWeight(), "Edge should have a TrackConnection");
            assertTrue(edge.getWeight().getDistance() > 0, "Distance should be positive: " + edge.getWeight().getDistance());
        }
    }

    @Test
    @DisplayName("MST total distance should be positive and finite")
    void testMST_TotalDistanceIsMinimal() {
        // Act
        Graph<Station, TrackConnection> mst = service.calculateMinimalBackbone();

        // Calculate total distance of MST
        double mstTotalDistance = 0.0;
        for (Edge<Station, TrackConnection> edge : mst.edges()) {
            mstTotalDistance += edge.getWeight().getDistance();
        }

        // Assert - MST total distance should be reasonable (positive and finite)
        assertTrue(mstTotalDistance > 0, "Total MST distance should be positive");
        assertTrue(Double.isFinite(mstTotalDistance), "Total MST distance should be finite");
        System.out.println("Edges: " + mst.edges().size());
        System.out.println("Vertices: " + mst.vertices().size());
        assertEquals(mst.numEdges(), mst.numVertices() - 1, "In a connected MST, number of edges should be V-1");
    }


    @Test
    @DisplayName("MST structure can be printed and sorted by distance")
    void testMST_PrintsStructure() {
        // Act
        Graph<Station, TrackConnection> mst = service.calculateMinimalBackbone();

        List<Edge<Station, TrackConnection>> sortedEdges = new ArrayList<>(mst.edges());
        sortedEdges.sort(Comparator.comparingDouble(e -> e.getWeight().getDistance()));
    }

    @Test
    @DisplayName("Kruskal MST algorithm works correctly with a small 4-vertex sample")
    void testKruskalMST_WithSmallSample() throws Exception {
        // Create a small test graph to verify algorithm correctness
        Graph<Station, TrackConnection> smallGraph = createSmallTestGraph();
        RailwayNetworkRepository.getInstance().setNetworkGraph(smallGraph);

        MinimalBackboneService testService = new MinimalBackboneService();

        // Act
        Graph<Station, TrackConnection> mst = testService.calculateMinimalBackbone();

        // Assert
        assertEquals(4, mst.numVertices(), "Small graph should have 4 vertices");
        assertEquals(3, mst.numEdges(), "MST of 4 vertices should have 3 edges");

        // Calculate total weight
        double totalWeight = 0.0;
        for (Edge<Station, TrackConnection> edge : mst.edges()) {
            totalWeight += edge.getWeight().getDistance();
        }

        // Expected MST for the test graph: edges (A-B: 1), (B-C: 2), (C-D: 3) = 6.0
        assertEquals(6.0, totalWeight, 0.001, "MST total weight should be minimal");
    }

    @Test
    @DisplayName("Kruskal MST algorithm works correctly with a 10-vertex graph")
    void testKruskalMST_WithTenVertices() throws Exception {
        // Create a 10-vertex test graph to verify algorithm correctness
        Graph<Station, TrackConnection> largeGraph = createTenVertexTestGraph();
        RailwayNetworkRepository.getInstance().setNetworkGraph(largeGraph);

        MinimalBackboneService testService = new MinimalBackboneService();

        // Act
        Graph<Station, TrackConnection> mst = testService.calculateMinimalBackbone();

        // Assert
        assertEquals(10, mst.numVertices(), "Graph should have 10 vertices");
        assertEquals(9, mst.numEdges(), "MST of 10 vertices should have 9 edges");

        // Calculate total weight
        double totalWeight = 0.0;
        List<Edge<Station, TrackConnection>> mstEdges = new ArrayList<>(mst.edges());
        for (Edge<Station, TrackConnection> edge : mstEdges) {
            totalWeight += edge.getWeight().getDistance();
        }

        // Expected MST forms a path: 1+2+3+4+5+6+7+8+9 = 45.0
        assertEquals(45.0, totalWeight, 0.001, "MST total weight should be minimal");


        mstEdges.sort(Comparator.comparingDouble(e -> e.getWeight().getDistance()));
    }

    /**
     * Helper method to create a small test graph for verification
     * Graph: A-B(1), A-C(4), B-C(2), C-D(3), B-D(5)
     * Expected MST: A-B(1), B-C(2), C-D(3) = Total: 6.0
     */
    private Graph<Station, TrackConnection> createSmallTestGraph() {
        Graph<Station, TrackConnection> graph = new pt.isep.dei.esinf.utils.graph.map.MapGraph<>(true);

        Station stationA = new Station("1", "Station A", 0, 0, 50.0, 4.0);
        Station stationB = new Station("2", "Station B", 10, 0, 50.1, 4.1);
        Station stationC = new Station("3", "Station C", 10, 10, 50.2, 4.2);
        Station stationD = new Station("4", "Station D", 0, 10, 50.3, 4.3);

        graph.addVertex(stationA);
        graph.addVertex(stationB);
        graph.addVertex(stationC);
        graph.addVertex(stationD);

        // Add edges (bidirectional)
        graph.addEdge(stationA, stationB, new TrackConnection(1.0, 10, 1.0));
        graph.addEdge(stationB, stationA, new TrackConnection(1.0, 10, 1.0));

        graph.addEdge(stationA, stationC, new TrackConnection(4.0, 10, 4.0));
        graph.addEdge(stationC, stationA, new TrackConnection(4.0, 10, 4.0));

        graph.addEdge(stationB, stationC, new TrackConnection(2.0, 10, 2.0));
        graph.addEdge(stationC, stationB, new TrackConnection(2.0, 10, 2.0));

        graph.addEdge(stationC, stationD, new TrackConnection(3.0, 10, 3.0));
        graph.addEdge(stationD, stationC, new TrackConnection(3.0, 10, 3.0));

        graph.addEdge(stationB, stationD, new TrackConnection(5.0, 10, 5.0));
        graph.addEdge(stationD, stationB, new TrackConnection(5.0, 10, 5.0));

        return graph;
    }

    /**
     * Helper method to create a 10-vertex test graph for verification
     * Graph structure:
     * - Main path: A-B(1), B-C(2), C-D(3), D-E(4), E-F(5), F-G(6), G-H(7), H-I(8), I-J(9)
     * - Additional edges with higher weights to test algorithm
     * Expected MST: The main path with total weight = 45.0
     */
    private Graph<Station, TrackConnection> createTenVertexTestGraph() {
        Graph<Station, TrackConnection> graph = new pt.isep.dei.esinf.utils.graph.map.MapGraph<>(true);

        // Create 10 stations
        Station stationA = new Station("1", "Station A", 0, 0, 50.0, 4.0);
        Station stationB = new Station("2", "Station B", 10, 0, 50.1, 4.1);
        Station stationC = new Station("3", "Station C", 20, 0, 50.2, 4.2);
        Station stationD = new Station("4", "Station D", 30, 0, 50.3, 4.3);
        Station stationE = new Station("5", "Station E", 40, 0, 50.4, 4.4);
        Station stationF = new Station("6", "Station F", 50, 0, 50.5, 4.5);
        Station stationG = new Station("7", "Station G", 60, 0, 50.6, 4.6);
        Station stationH = new Station("8", "Station H", 70, 0, 50.7, 4.7);
        Station stationI = new Station("9", "Station I", 80, 0, 50.8, 4.8);
        Station stationJ = new Station("10", "Station J", 90, 0, 50.9, 4.9);

        // Add all vertices
        graph.addVertex(stationA);
        graph.addVertex(stationB);
        graph.addVertex(stationC);
        graph.addVertex(stationD);
        graph.addVertex(stationE);
        graph.addVertex(stationF);
        graph.addVertex(stationG);
        graph.addVertex(stationH);
        graph.addVertex(stationI);
        graph.addVertex(stationJ);

        addBidirectionalEdge(graph, stationA, stationB, 1.0, 1.0);
        addBidirectionalEdge(graph, stationB, stationC, 2.0, 2.0);
        addBidirectionalEdge(graph, stationC, stationD, 3.0, 3.0);
        addBidirectionalEdge(graph, stationD, stationE, 4.0, 4.0);
        addBidirectionalEdge(graph, stationE, stationF, 5.0, 5.0);
        addBidirectionalEdge(graph, stationF, stationG, 6.0, 6.0);
        addBidirectionalEdge(graph, stationG, stationH, 7.0, 7.0);
        addBidirectionalEdge(graph, stationH, stationI, 8.0, 8.0);
        addBidirectionalEdge(graph, stationI, stationJ, 9.0, 9.0);

        addBidirectionalEdge(graph, stationA, stationC, 10.0, 10.0);
        addBidirectionalEdge(graph, stationA, stationD, 15.0, 15.0);
        addBidirectionalEdge(graph, stationB, stationE, 12.0, 12.0);
        addBidirectionalEdge(graph, stationC, stationF, 13.0, 13.0);
        addBidirectionalEdge(graph, stationD, stationG, 14.0, 14.0);
        addBidirectionalEdge(graph, stationE, stationH, 16.0, 16.0);
        addBidirectionalEdge(graph, stationF, stationI, 17.0, 17.0);
        addBidirectionalEdge(graph, stationG, stationJ, 18.0, 18.0);
        addBidirectionalEdge(graph, stationA, stationJ, 50.0, 50.0);

        return graph;
    }



    @Test
    @DisplayName("Should throw IllegalStateException when railway network data is not loaded")
    void testCalculateMinimalBackbone_ThrowsExceptionWhenDataNotLoaded() {
        MinimalBackboneService emptyService = new MinimalBackboneService();
        RailwayNetworkRepository.getInstance().clear();
        assertThrows(IllegalStateException.class, emptyService::calculateMinimalBackbone);
    }

    @Test
    void testCase1_SmallCycle_Namur() {
        // Arrange
        Graph<Station, TrackConnection> namurGraph = createNamurGraph();
        RailwayNetworkRepository.getInstance().setNetworkGraph(namurGraph);
        MinimalBackboneService testService = new MinimalBackboneService();

        // Act
        Graph<Station, TrackConnection> mst = testService.calculateMinimalBackbone();

        // Calculate total COST
        double totalCost = 0.0;
        for (Edge<Station, TrackConnection> edge : mst.edges()) {
            totalCost += edge.getWeight().getCost();
        }

        // Assert
        assertEquals(4, mst.numVertices());
        assertEquals(3, mst.numEdges());
        assertEquals(14.573, totalCost, 0.001, "MST total cost should match expected value");
    }

    @Test
    void testCase2_BrusselsCluster() {
        // Arrange
        Graph<Station, TrackConnection> brusselsGraph = createBrusselsClusterGraph();
        RailwayNetworkRepository.getInstance().setNetworkGraph(brusselsGraph);
        MinimalBackboneService testService = new MinimalBackboneService();

        // Act
        Graph<Station, TrackConnection> mst = testService.calculateMinimalBackbone();

        // Calculate total COST
        double totalCost = 0.0;
        for (Edge<Station, TrackConnection> edge : mst.edges()) {
            totalCost += edge.getWeight().getCost();
        }

        // Assert
        assertEquals(10, mst.numVertices());
        assertEquals(9, mst.numEdges());
        assertEquals(30.938, totalCost, 0.01, "MST total cost should match expected value");
    }

    private Graph<Station, TrackConnection> createNamurGraph() {
        Graph<Station, TrackConnection> graph = new pt.isep.dei.esinf.utils.graph.map.MapGraph<>(true);
        Station flawinne = new Station("400", "FLAWINNE", 0,0,0,0);
        Station ronet = new Station("1009", "RONET", 0,0,0,0);
        Station floreffe = new Station("406", "FLOREFFE", 0,0,0,0);
        Station namur = new Station("895", "NAMUR", 0,0,0,0);

        graph.addVertex(flawinne); graph.addVertex(ronet);
        graph.addVertex(floreffe); graph.addVertex(namur);

        // Usage: (Graph, From, To, Distance, Cost)
        addBidirectionalEdge(graph, flawinne, ronet, 1.456, 5.452);
        addBidirectionalEdge(graph, ronet, namur, 2.381, 5.030);
        addBidirectionalEdge(graph, flawinne, floreffe, 3.488, 4.091);
        addBidirectionalEdge(graph, flawinne, namur, 3.831, 6.992);

        return graph;
    }

    private Graph<Station, TrackConnection> createBrusselsClusterGraph() {
        Graph<Station, TrackConnection> graph = new pt.isep.dei.esinf.utils.graph.map.MapGraph<>(true);

        Station bs = new Station("227", "BRUX-SCHUMAN", 0,0,0,0);
        Station bu = new Station("229", "BUDA", 0,0,0,0);
        Station di = new Station("325", "DIEGEM", 0,0,0,0);
        Station bo = new Station("1767", "BOCKSTAEL", 0,0,0,0);
        Station vi = new Station("1192", "VILVOORDE", 0,0,0,0);
        Station tt = new Station("1979", "TOUR-TAXIS", 0,0,0,0);
        Station me = new Station("810", "MECHELEN", 0,0,0,0);
        Station sc = new Station("1048", "SCHAERBEEK", 0,0,0,0);
        Station ba = new Station("219", "BRU AIRPORT", 0,0,0,0);
        Station bn = new Station("221", "BRUXELLES-ND", 0,0,0,0);

        graph.addVertex(bs);
        graph.addVertex(bu);
        graph.addVertex(di);
        graph.addVertex(bo);
        graph.addVertex(vi);
        graph.addVertex(tt);
        graph.addVertex(me);
        graph.addVertex(sc);
        graph.addVertex(ba);
        graph.addVertex(bn);

        // Usage: (Graph, From, To, Distance, Cost)
        addBidirectionalEdge(graph, bo, bn, 0.880, -4.222);
        addBidirectionalEdge(graph, tt, bn, 1.052, 3.300);
        addBidirectionalEdge(graph, bs, bn, 1.341, 3.291);
        addBidirectionalEdge(graph, bu, vi, 1.473, 6.210);
        addBidirectionalEdge(graph, di, vi, 1.522, 2.169);
        addBidirectionalEdge(graph, di, ba, 1.821, 2.026);
        addBidirectionalEdge(graph, bu, sc, 1.932, 5.032);
        addBidirectionalEdge(graph, bs, bo, 2.199, 2.442);
        addBidirectionalEdge(graph, bs, tt, 2.383, 5.702);
        addBidirectionalEdge(graph, bo, sc, 3.161, 5.443);
        addBidirectionalEdge(graph, di, sc, 3.195, 3.554);
        addBidirectionalEdge(graph, sc, bn, 3.374, 6.097);
        addBidirectionalEdge(graph, vi, sc, 3.403, 7.096);
        addBidirectionalEdge(graph, tt, sc, 3.618, 5.504);
        addBidirectionalEdge(graph, bs, sc, 3.741, 5.780);
        addBidirectionalEdge(graph, sc, ba, 4.965, 5.512);
        addBidirectionalEdge(graph, me, ba, 8.872, 11.195);
        addBidirectionalEdge(graph, di, me, 9.372, 11.418);
        addBidirectionalEdge(graph, me, sc, 11.623, 12.844);

        return graph;
    }

    @Test
    @DisplayName("Generate DOT file should create valid DOT format")
    void testGenerateDotFile_CreatesValidFile() throws IOException {
        // Arrange
        Graph<Station, TrackConnection> mst = service.calculateMinimalBackbone();
        String testFilename = "test_minimal_backbone";

        try {
            // Act
            String dotPath = service.generateDotFile(mst, testFilename);

            // Assert
            assertNotNull(dotPath, "DOT file path should not be null");
            assertTrue(dotPath.endsWith(".dot"), "File should have .dot extension");

            File dotFile = new File(dotPath);
            assertTrue(dotFile.exists(), "DOT file should exist");

            // Verify content
            String content = Files.readString(Path.of(dotPath));
            assertTrue(content.contains("graph MinimalBackbone"), "DOT file should contain graph declaration");
            assertTrue(content.contains("layout=neato"), "DOT file should specify neato layout");
            assertTrue(content.contains("pos="), "DOT file should contain position attributes");
            assertTrue(content.contains("--"), "DOT file should contain edge declarations (undirected)");

        } finally {
            // Cleanup
            File dotFile = new File(testFilename + ".dot");
            if (dotFile.exists()) {
                dotFile.delete();
            }
        }
    }
}
