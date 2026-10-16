package pt.isep.dei.esinf.service.spatial;

import pt.isep.dei.esinf.domain.stations.*;
import pt.isep.dei.esinf.utils.AVL;
import pt.isep.dei.esinf.utils.BST;
import pt.isep.dei.esinf.utils.KDTree;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Service for building balanced 2D-tree for stations
 */
public class BalancedTreeBuilder {

    /**
     * Builds a balanced 2D-tree using median-based recursive partitioning
     * @param stations List of stations to build the tree from
     * @return Balanced KDTree containing all stations
     */    public KDTree<Station> buildBalancedKDTree(List<Station> stations) {
        if (stations == null || stations.isEmpty()) {
            return new KDTree<>();
        }

        List<Station> validStations = stations.stream()
                .filter(Station::isValid)
                .toList();

        System.out.println("Building balanced 2D-tree with " + validStations.size() + " valid stations");

        KDTree<Station> kdTree = new KDTree<>();

        if (!validStations.isEmpty()) {
            buildBalancedKDTreeRecursive(kdTree, validStations, 0);
        }

        printTreeStatistics(kdTree, validStations.size());

        return kdTree;
    }

    /**
     * Recursive method to build balanced KD-tree using median selection
     * @param kdTree The KD-tree being built
     * @param stations List of stations to insert
     * @param depth Current depth in the tree (determines splitting dimension)
     */
    private void buildBalancedKDTreeRecursive(KDTree<Station> kdTree, List<Station> stations, int depth) {
        if (stations.isEmpty()) {
            return;
        }

        int dimension = depth % 2;

        List<Station> sortedStations = new ArrayList<>(stations);
        if (dimension == 0) {
            sortedStations.sort(Comparator.comparingDouble(s -> s.getCoordinates().getLongitude()));
        } else {
            sortedStations.sort(Comparator.comparingDouble(s -> s.getCoordinates().getLatitude()));
        }

        int medianIndex = sortedStations.size() / 2;
        Station medianStation = sortedStations.get(medianIndex);

        kdTree.insert(medianStation);

        List<Station> leftStations = sortedStations.subList(0, medianIndex);
        List<Station> rightStations = sortedStations.subList(medianIndex + 1, sortedStations.size());

        buildBalancedKDTreeRecursive(kdTree, leftStations, depth + 1);
        buildBalancedKDTreeRecursive(kdTree, rightStations, depth + 1);
    }

    /** Alias to improved balanced method */
    public KDTree<Station> buildBalancedKDTreeDirect(List<Station> stations) {
        return buildBalancedKDTree(stations);
    }

    /** Builds an optimized balanced KDTree using bulk loading */
    public KDTree<Station> buildOptimizedBalancedKDTree(List<Station> stations) {
        if (stations == null || stations.isEmpty()) {
            return new KDTree<>();
        }

        List<Station> validStations = stations.stream()
                .filter(Station::isValid)
                .toList();

        System.out.println("Building optimized balanced 2D-tree with " + validStations.size() + " valid stations");

        KDTree<Station> kdTree = new KDTree<>();

        if (!validStations.isEmpty()) {
            bulkLoadKDTree(kdTree, validStations, 0);
        }

        printDetailedTreeStatistics(kdTree, validStations.size());

        return kdTree;
    }

    /** Bulk loading helper */
    private void bulkLoadKDTree(KDTree<Station> kdTree, List<Station> stations, int depth) {
        if (stations.isEmpty()) {
            return;
        }

        int dimension = depth % 2;

        List<Station> sorted = new ArrayList<>(stations);
        if (dimension == 0) {
            sorted.sort(Comparator.comparingDouble(s -> s.getCoordinates().getLongitude()));
        } else {
            sorted.sort(Comparator.comparingDouble(s -> s.getCoordinates().getLatitude()));
        }

        int medianIndex = sorted.size() / 2;
        Station median = sorted.get(medianIndex);

        kdTree.insert(median);

        List<Station> left = sorted.subList(0, medianIndex);
        List<Station> right = sorted.subList(medianIndex + 1, sorted.size());

        bulkLoadKDTree(kdTree, left, depth + 1);
        bulkLoadKDTree(kdTree, right, depth + 1);
    }

    /** Prints basic tree statistics */
    private void printTreeStatistics(KDTree<Station> kdTree, int totalStations) {
        System.out.println("=== 2D-Tree Statistics ===");
        System.out.println("Total stations: " + totalStations);
        System.out.println("Tree height: " + kdTree.height());

        double optimalHeight = Math.ceil(Math.log(totalStations) / Math.log(2));
        double balanceRatio = kdTree.height() / optimalHeight;

        System.out.println("Optimal height (for balanced tree): " + optimalHeight);
        System.out.println("Balance ratio: " + String.format("%.2f", balanceRatio));

        if (balanceRatio <= 1.5) {
            System.out.println("✓ Tree is well balanced");
        } else if (balanceRatio <= 2.0) {
            System.out.println("○ Tree is reasonably balanced");
        } else {
            System.out.println("⚠ Tree could be more balanced");
        }
    }

    /** Prints detailed tree statistics */
    private void printDetailedTreeStatistics(KDTree<Station> kdTree, int totalStations) {
        printTreeStatistics(kdTree, totalStations);

        int height = kdTree.height();
        double efficiency = (Math.pow(2, height) - 1) / (double) totalStations;

        System.out.println("Tree efficiency: " + String.format("%.2f", efficiency));
        System.out.println("Theoretical min height: " + (int)Math.floor(Math.log(totalStations) / Math.log(2)));
        System.out.println("Theoretical max height: " + totalStations);
    }

    /** Builds AVL ordered by latitude (USEI06 compatibility) */
    public AVL<CoordinateStationKey> buildLatitudeAVLTree(List<Station> stations) {
        AVL<CoordinateStationKey> tree = new AVL<>();

        for (Station station : stations) {
            if (station.getCoordinates() != null && station.isValid()) {
                CoordinateStationKey key = new CoordinateStationKey(
                        station.getCoordinates(),
                        station.getName(),
                        station
                );
                tree.insert(key);
            }
        }

        System.out.println("Latitude AVL tree - Size: " + tree.size() + ", Height: " + tree.height());
        return tree;
    }

    /** Builds AVL ordered by longitude (USEI06 compatibility) */
    public AVL<CoordinateStationKey> buildLongitudeAVLTree(List<Station> stations) {
        AVL<CoordinateStationKey> tree = new AVL<>();

        for (Station station : stations) {
            if (station.getCoordinates() != null && station.isValid()) {
                CoordinateStationKey key = new CoordinateStationKey(
                        station.getCoordinates(),
                        station.getName(),
                        station
                );
                tree.insert(key);
            }
        }

        System.out.println("Longitude AVL tree - Size: " + tree.size() + ", Height: " + tree.height());
        return tree;
    }
}
