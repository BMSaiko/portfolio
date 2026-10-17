# USEI07 - Balanced 2D-Tree Construction for Stations

## 4. Tests and Implementation

### 4.1 Test strategy
Focus areas:
- Correctness of balanced KD-tree construction using a real dataset (64k+ stations).
- Tree balance metrics and height optimization.
- Construction performance with large datasets.
- Edge cases: single station, empty list, identical coordinates.
- Station validation during construction.

### 4.2 Unit tests

BalancedTreeConstructionTest (uses train_stations_europe.csv, 64,039 stations via DataInitializationService)

Core balanced-construction tests (8):

- shouldBuildBalancedKDTree_withStationsFromDataset
    - Input: stations in lat 40–60, lon -10–20
    - Verify: tree non-null, non-empty, balance ratio ≤ 3.0
    - Metrics: actual height vs optimal (log2(n))

- shouldAchieveReasonableBalance_forLargeDatasets
    - Input: large sample (lat 35–70, lon -25–40)
    - Verify: balance ratio ≤ 4.5, tree height < 100 for 50k+ stations
    - Output: detailed balance stats

- shouldHandleStationsWithIdenticalCoordinates
    - Input: many stations sharing coordinates (e.g., Lisbon)
    - Verify: all stations inserted and retrievable via small-range searches

- shouldFilterOutInvalidStations_duringConstruction
    - Input: mixed valid/invalid stations
    - Verify: only station.isValid() == true are in tree

- shouldHandleEdgeCases_singleStation
    - Input: single station region
    - Verify: tree height = 0, single-node tree

- shouldHandleEdgeCases_emptyStationList
    - Input: empty station list
    - Verify: tree empty, height = -1 (or defined empty-tree sentinel)

- shouldMaintainSpatialProperties_forEfficientSearching
    - Input: regional set (e.g., lat 48–49, lon 2–3)
    - Verify: range-search latency < 1000 ms, correct results

- shouldConstructTree_withVariousRegionSizes
    - Input: multiple region sizes
    - Verify: consistent balance trends; record construction time vs dataset size

Performance & validation tests (6):

- testConstructionPerformance_LargeDataset
    - Inputs: 10k, 20k, 50k; target: < 10s for 50k

- testBalanceRatio_OptimalRanges
    - Goal: categorize balance ratio (1.0–2.0 excellent, ≤3.0 good, ≤4.5 acceptable)

- testStationValidation_DuringConstruction
    - Verify: all validation rules (Station.isValid) applied

- testTreeStatistics_Accuracy
    - Verify: reported size, height, optimal height, balance ratio match tree

- testBulkLoading_vsRecursiveConstruction
    - Compare buildBalancedKDTree vs buildOptimizedBalancedKDTree on same dataset

- testCoordinateSorting_MedianSelection
    - Verify median selection and alternating axis sorting correctness

### 4.3 Implementation details

Key classes and signatures:

- BalancedTreeBuilder (service.spatial)
```java
public class BalancedTreeBuilder {
        public KDTree<Station> buildBalancedKDTree(List<Station> stations);
        public KDTree<Station> buildBalancedKDTreeDirect(List<Station> stations);
        public KDTree<Station> buildOptimizedBalancedKDTree(List<Station> stations);
        // internal helpers: recursive build, bulk load, statistics
}
```

- KDTree<E extends KDTreeNodeInterface> (utils)
```java
public class KDTree<E extends Comparable<E> & KDTreeNodeInterface> extends BST<E> {
        public void insert(E element);
        // inherits height(), size()
}
```

- KDNode<E> (domain.tree)
```java
public class KDNode<E extends KDTreeNodeInterface> extends Node<E> {
        private Point2D.Double coords;
        public double getX(); // longitude
        public double getY(); // latitude
}
```

- KDTreeNodeInterface (utils)
```java
public interface KDTreeNodeInterface {
        Point2D.Double getCoords(); // x=longitude, y=latitude
}
```

- Station (domain.stations)
```java
public class Station implements Comparable<Station>, KDTreeNodeInterface {
        public Point2D.Double getCoords(); // from Coordinates (lon, lat)
        public boolean isValid(); // name != empty, coordinates valid, timezone optional-valid
}
```

- Coordinates
```java
public class Coordinates implements Comparable<Coordinates> {
        public boolean isValid(); // lat in [-90,90], lon in [-180,180]
}
```

- StationsRepository (repository)
```java
public class StationsRepository {
        private KDTree<Station> kdTree = new KDTree<>();
        public void addStation(TimeZone tz, Station station) {
                if (!station.isValid()) throw new IllegalArgumentException(...);
                kdTree.insert(station);
        }
        public KDTree<Station> getKdTree() { return kdTree; }
}
```

### 4.4 Construction algorithm (balanced recursive approach)

Core recursive builder (median-of-list by alternating axis):

```java
private void buildBalancedKDTreeRecursive(KDTree<Station> kdTree,
                                                                                 List<Station> stations, int depth) {
        if (stations == null || stations.isEmpty()) return;
        int axis = depth % 2; // 0 = longitude (X), 1 = latitude (Y)

        List<Station> sorted = new ArrayList<>(stations);
        if (axis == 0) {
                sorted.sort(Comparator.comparingDouble(s -> s.getCoords().x));
        } else {
                sorted.sort(Comparator.comparingDouble(s -> s.getCoords().y));
        }

        int median = sorted.size() / 2;
        Station root = sorted.get(median);
        kdTree.insert(root);

        buildBalancedKDTreeRecursive(kdTree, sorted.subList(0, median), depth + 1);
        buildBalancedKDTreeRecursive(kdTree, sorted.subList(median + 1, sorted.size()), depth + 1);
}
```

Balance metrics / reporting:

```java
private void printTreeStatistics(KDTree<Station> kdTree, int totalStations) {
        int height = kdTree.height();
        double optimal = Math.ceil(Math.log(totalStations) / Math.log(2));
        double ratio = height / optimal;
        // Print size, height, optimal, ratio and a simple quality label
}
```

Notes:
- Filter invalid stations before building (station.isValid()).
- For performance, consider bulk-load or nth-element median selection to avoid full sorts at each recursion.
- Tests should assert deterministic metrics (size, height boundaries, balance ratio thresholds) and verify correctness of range queries after construction.
- Ensure unit tests include reproducible region definitions and seeds for sampling when using subsets.
