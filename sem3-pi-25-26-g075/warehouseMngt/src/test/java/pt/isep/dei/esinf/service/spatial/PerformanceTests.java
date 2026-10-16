package pt.isep.dei.esinf.service.spatial;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pt.isep.dei.esinf.domain.stations.Station;
import pt.isep.dei.esinf.service.StationSearchService;
import pt.isep.dei.esinf.service.data.DataInitializationService;
import pt.isep.dei.esinf.service.spatial.BalancedTreeBuilder;
import pt.isep.dei.esinf.utils.KDTree;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PerformanceTests {

    private static StationSearchService service;

    @BeforeAll
    static void setUp() throws Exception {
        DataInitializationService dataService = new DataInitializationService();
        dataService.initializeStationsData();
        service = new StationSearchService();
    }

    @Test
    @DisplayName("Performance: KD-tree range search should be efficient")
    void kdTreeRangeSearch_shouldBeEfficient() {
        // Get stations from a region
        List<Station> stations = service.getStationsByRange(48.0, 49.0, 2.0, 3.0, null);

        if (!stations.isEmpty()) {
            BalancedTreeBuilder builder = new BalancedTreeBuilder();
            KDTree<Station> kdTree = builder.buildBalancedKDTreeDirect(stations);

            // Test with different search area sizes
            double[] areaSizes = {0.1, 1.0, 5.0};

            for (double size : areaSizes) {
                long startTime = System.nanoTime();

                List<Station> results = kdTree.rangeSearch(
                        48.0, 48.0 + size,
                        2.0, 2.0 + size,
                        null
                );

                long endTime = System.nanoTime();
                long duration = (endTime - startTime) / 1000; // convert to microseconds

                System.out.printf("Area size %.1f: %d stations found in %d ms%n",
                        size, results.size(), duration);

                // Search time should be reasonable
                assertTrue(duration < 100000,
                        "KD-tree search should be efficient. Time: " + duration + "ms for area size " + size);
            }
        }
    }

    @Test
    @DisplayName("Performance: Tree construction should be efficient")
    void treeConstruction_shouldBeEfficient() {
        // Test with different numbers of stations
        int[] sizes = {100, 1000, 5000};

        for (int size : sizes) {
            // Get stations from a region (may not get exactly the requested size)
            List<Station> stations = service.getStationsByRange(40.0, 50.0, -10.0, 10.0, null);

            if (stations.size() > size) {
                stations = stations.subList(0, size);
            }

            long startTime = System.nanoTime();

            BalancedTreeBuilder builder = new BalancedTreeBuilder();
            KDTree<Station> testTree = builder.buildBalancedKDTreeDirect(stations);

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1000000; // convert to milliseconds

            System.out.printf("Tree construction for %d stations: %d ms%n", stations.size(), duration);

            assertTrue(duration < 10000,
                    "Tree construction should be efficient. Time: " + duration + "ms for " + stations.size() + " stations");
        }
    }
}