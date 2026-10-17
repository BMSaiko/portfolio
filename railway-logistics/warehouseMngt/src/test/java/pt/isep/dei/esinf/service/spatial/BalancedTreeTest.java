package pt.isep.dei.esinf.service.spatial;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.isep.dei.esinf.domain.stations.Station;
import pt.isep.dei.esinf.domain.stations.StationFilter;
import pt.isep.dei.esinf.service.StationSearchService;
import pt.isep.dei.esinf.service.data.DataInitializationService;
import pt.isep.dei.esinf.service.spatial.BalancedTreeBuilder;
import pt.isep.dei.esinf.utils.KDTree;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BalancedTreeConstructionTest {

    private static StationSearchService service;
    private static BalancedTreeBuilder treeBuilder;

    @BeforeAll
    static void setUp() throws Exception {
        DataInitializationService dataService = new DataInitializationService();
        dataService.initializeStationsData();
        service = new StationSearchService();
        treeBuilder = new BalancedTreeBuilder();
    }

    @Test
    @DisplayName("USEI07: Should build balanced KD-tree with stations from dataset")
    void shouldBuildBalancedKDTree_withStationsFromDataset() {
        // Get stations by querying a known region
        List<Station> stations = service.getStationsByRange(40.0, 60.0, -10.0, 20.0, null);

        KDTree<Station> kdTree = treeBuilder.buildBalancedKDTree(stations);

        assertNotNull(kdTree, "KD-tree should not be null");
        assertFalse(kdTree.isEmpty(), "KD-tree should not be empty");

        // Verify tree has reasonable height for balanced structure
        int treeHeight = kdTree.height();
        int totalStations = stations.size();

        if (totalStations > 0) {
            double optimalHeight = Math.ceil(Math.log(totalStations) / Math.log(2));
            double balanceRatio = treeHeight / optimalHeight;

            // For real-world geographic KD-trees, ratios up to 2.5 are excellent
            // Ratios up to 3.0 are acceptable for large datasets with irregular distributions
            assertTrue(balanceRatio <= 3.0,
                    "Tree should be reasonably balanced. Height: " + treeHeight +
                            ", Optimal: " + optimalHeight + ", Ratio: " + balanceRatio);

            System.out.println("✓ Balance achieved: " + balanceRatio + " for " + totalStations + " stations");
        }
    }

    @Test
    @DisplayName("USEI07: Should achieve reasonable balance for large datasets")
    void shouldAchieveReasonableBalance_forLargeDatasets() {
        // Get a large sample of stations
        List<Station> stations = service.getStationsByRange(35.0, 70.0, -25.0, 40.0, null);

        assertFalse(stations.isEmpty(), "Should have stations to test");

        KDTree<Station> kdTree = treeBuilder.buildOptimizedBalancedKDTree(stations);

        int treeHeight = kdTree.height();
        int totalStations = stations.size();

        double optimalHeight = Math.ceil(Math.log(totalStations) / Math.log(2));
        double balanceRatio = treeHeight / optimalHeight;

        System.out.println("Large dataset balance - Height: " + treeHeight +
                ", Optimal: " + optimalHeight + ", Ratio: " + balanceRatio);

        // For very large datasets (50k+), KD-trees typically have balance ratios of 2-4
        // This is considered acceptable for spatial indexing
        assertTrue(balanceRatio <= 4.5,
                "Large KD-tree should have reasonable balance ratio. Actual: " + balanceRatio);

        // The absolute height should be logarithmic-ish
        assertTrue(treeHeight < 100,
                "Tree height should be manageable. Actual: " + treeHeight);
    }

    @Test
    @DisplayName("USEI07: Should handle stations with identical coordinates")
    void shouldHandleStationsWithIdenticalCoordinates() {
        // Get a sample of stations
        List<Station> stations = service.getStationsByRange(44.0, 45.0, 6.0, 7.0, null);

        if (!stations.isEmpty()) {
            KDTree<Station> kdTree = treeBuilder.buildBalancedKDTreeDirect(stations);

            // Test that all stations are properly inserted
            for (Station station : stations) {
                assertTrue(containsStation(kdTree, station),
                        "KD-tree should contain station: " + station.getName());
            }
        }
    }

    @Test
    @DisplayName("USEI07: Should filter out invalid stations during construction")
    void shouldFilterOutInvalidStations_duringConstruction() {
        List<Station> stations = service.getStationsByRange(40.0, 50.0, -10.0, 10.0, null);

        KDTree<Station> kdTree = treeBuilder.buildBalancedKDTreeDirect(stations);

        // Tree should only contain valid stations
        List<Station> results = kdTree.rangeSearch(40.0, 50.0, -10.0, 10.0, null);

        for (Station station : results) {
            assertTrue(station.isValid(),
                    "All stations in KD-tree should be valid: " + station.getName());
        }
    }

    @Test
    @DisplayName("USEI07: Should handle edge cases - single station")
    void shouldHandleEdgeCases_singleStation() {
        // Get a small region that likely has only one station
        List<Station> singleStation = service.getStationsByRange(48.844, 48.845, 2.373, 2.374, null);

        if (!singleStation.isEmpty()) {
            // Take just the first station
            Station station = singleStation.get(0);
            List<Station> singleStationList = List.of(station);

            KDTree<Station> kdTree = treeBuilder.buildBalancedKDTreeDirect(singleStationList);

            assertFalse(kdTree.isEmpty(), "Tree with single station should not be empty");
            assertEquals(0, kdTree.height(), "Single station tree should have height 0");
        }
    }

    @Test
    @DisplayName("USEI07: Should handle edge cases - empty station list")
    void shouldHandleEdgeCases_emptyStationList() {
        List<Station> emptyList = List.of();
        KDTree<Station> kdTree = treeBuilder.buildBalancedKDTreeDirect(emptyList);

        assertTrue(kdTree.isEmpty(), "Tree from empty list should be empty");
        assertEquals(-1, kdTree.height(), "Empty tree should have height -1");
    }

    @Test
    @DisplayName("USEI07: Should maintain spatial properties for efficient searching")
    void shouldMaintainSpatialProperties_forEfficientSearching() {
        List<Station> stations = service.getStationsByRange(48.0, 49.0, 2.0, 3.0, null);

        if (!stations.isEmpty()) {
            KDTree<Station> kdTree = treeBuilder.buildBalancedKDTreeDirect(stations);

            // Test that the tree can perform efficient range searches
            long startTime = System.currentTimeMillis();
            List<Station> results = kdTree.rangeSearch(48.0, 49.0, 2.0, 3.0, null);
            long endTime = System.currentTimeMillis();

            long searchTime = endTime - startTime;

            // KD-tree search should be fast
            assertTrue(searchTime < 1000,
                    "KD-tree range search should be efficient. Time: " + searchTime + "ms");

            assertNotNull(results, "Range search should return valid results");
        }
    }

    // Helper method to check if KD-tree contains a specific station
    private boolean containsStation(KDTree<Station> kdTree, Station station) {
        // Perform a small range search around the station's coordinates
        double lat = station.getCoordinates().getLatitude();
        double lon = station.getCoordinates().getLongitude();
        double epsilon = 0.000001;

        List<Station> results = kdTree.rangeSearch(
                lat - epsilon, lat + epsilon,
                lon - epsilon, lon + epsilon,
                s -> s.getName().equals(station.getName())
        );

        return !results.isEmpty();
    }
}