# USEI07 - Code Complexity Analysis

## 1. Source Code with Complexity Annotations

### 1.1 BalancedTreeBuilder.java
```java
public class BalancedTreeBuilder {

    /**
     * Complexity: O(n log² n)
     * - Filtering: O(n)
     * - Recursive construction: O(n log² n)
     */
    public KDTree<Station> buildBalancedKDTree(List<Station> stations) {
        if (stations == null || stations.isEmpty()) {  // O(1)
            return new KDTree<>();  // O(1)
        }

        // Filtering: O(n)
        List<Station> validStations = stations.stream()
                .filter(Station::isValid)  // O(n)
                .toList();

        System.out.println("Building balanced 2D-tree with " + validStations.size() + " valid stations");

        KDTree<Station> kdTree = new KDTree<>();  // O(1)

        if (!validStations.isEmpty()) {
            buildBalancedKDTreeRecursive(kdTree, validStations, 0);  // O(n log² n)
        }

        printTreeStatistics(kdTree, validStations.size());  // O(h) where h = tree height

        return kdTree;
    }

    /**
     * Complexity: O(n log² n) in total
     * - Each level: O(n log n) for sorting
     * - Height: O(log n) levels
     * - Total: O(n log n × log n) = O(n log² n)
     */
    private void buildBalancedKDTreeRecursive(KDTree<Station> kdTree,
                                             List<Station> stations, int depth) {
        if (stations.isEmpty()) {  // O(1)
            return;
        }

        int dimension = depth % 2;  // O(1)

        // Sorting: O(n log n) - dominant operation
        List<Station> sortedStations = new ArrayList<>(stations);
        if (dimension == 0) {
            sortedStations.sort(Comparator.comparingDouble(
                s -> s.getCoordinates().getLongitude()));  // O(n log n)
        } else {
            sortedStations.sort(Comparator.comparingDouble(
                s -> s.getCoordinates().getLatitude()));  // O(n log n)
        }

        int medianIndex = sortedStations.size() / 2;  // O(1)
        Station medianStation = sortedStations.get(medianIndex);  // O(1)

        kdTree.insert(medianStation);  // O(log n) - insertion in balanced tree

        // Splitting: O(n) - creating sublists
        List<Station> leftStations = sortedStations.subList(0, medianIndex);
        List<Station> rightStations = sortedStations.subList(medianIndex + 1,
                                                            sortedStations.size());

        // Recursive calls: 2 calls with n/2 elements each
        buildBalancedKDTreeRecursive(kdTree, leftStations, depth + 1);  // T(n/2)
        buildBalancedKDTreeRecursive(kdTree, rightStations, depth + 1); // T(n/2)

        // Recurrence: T(n) = 2T(n/2) + O(n log n)
        // Solution: O(n log² n)
    }

    /**
     * Complexity: O(n log² n) - same as main method
     */
    public KDTree<Station> buildBalancedKDTreeDirect(List<Station> stations) {
        return buildBalancedKDTree(stations);  // O(n log² n)
    }

    /**
     * Complexity: O(n log² n) - similar to the basic method
     */
    public KDTree<Station> buildOptimizedBalancedKDTree(List<Station> stations) {
        if (stations == null || stations.isEmpty()) {  // O(1)
            return new KDTree<>();  // O(1)
        }

        // Filtering: O(n)
        List<Station> validStations = stations.stream()
                .filter(Station::isValid)  // O(n)
                .toList();

        System.out.println("Building optimized balanced 2D-tree with " +
                          validStations.size() + " valid stations");

        KDTree<Station> kdTree = new KDTree<>();  // O(1)

        if (!validStations.isEmpty()) {
            bulkLoadKDTree(kdTree, validStations, 0);  // O(n log² n)
        }

        printDetailedTreeStatistics(kdTree, validStations.size());  // O(h)

        return kdTree;
    }

    /**
     * Complexity: O(n log² n) - same logic as the basic recursive method
     */
    private void bulkLoadKDTree(KDTree<Station> kdTree,
                               List<Station> stations, int depth) {
        if (stations.isEmpty()) {  // O(1)
            return;
        }

        int dimension = depth % 2;  // O(1)

        // Sorting: O(n log n)
        List<Station> sorted = new ArrayList<>(stations);
        if (dimension == 0) {
            sorted.sort(Comparator.comparingDouble(
                s -> s.getCoordinates().getLongitude()));  // O(n log n)
        } else {
            sorted.sort(Comparator.comparingDouble(
                s -> s.getCoordinates().getLatitude()));  // O(n log n)
        }

        int medianIndex = sorted.size() / 2;  // O(1)
        Station median = sorted.get(medianIndex);  // O(1)

        kdTree.insert(median);  // O(log n)

        // Splitting: O(n)
        List<Station> left = sorted.subList(0, medianIndex);
        List<Station> right = sorted.subList(medianIndex + 1, sorted.size());

        // Recursion: 2T(n/2)
        bulkLoadKDTree(kdTree, left, depth + 1);   // T(n/2)
        bulkLoadKDTree(kdTree, right, depth + 1);  // T(n/2)

        // T(n) = 2T(n/2) + O(n log n) = O(n log² n)
    }

    /**
     * Complexity: O(h) where h = tree height
     */
    private void printTreeStatistics(KDTree<Station> kdTree, int totalStations) {
        System.out.println("=== 2D-Tree Statistics ===");
        System.out.println("Total stations: " + totalStations);  // O(1)
        System.out.println("Tree height: " + kdTree.height());   // O(h) - traverses the tree

        // Mathematical calculations: O(1)
        double optimalHeight = Math.ceil(Math.log(totalStations) / Math.log(2));  // O(1)
        double balanceRatio = kdTree.height() / optimalHeight;  // O(1)

        System.out.println("Optimal height: " + optimalHeight);  // O(1)
        System.out.println("Balance ratio: " + String.format("%.2f", balanceRatio));  // O(1)

        // Quality evaluation: O(1)
        if (balanceRatio <= 1.5) {
            System.out.println("✓ Tree is well balanced");
        } else if (balanceRatio <= 2.0) {
            System.out.println("○ Tree is reasonably balanced");
        } else {
            System.out.println("⚠ Tree could be more balanced");
        }
    }
}
```

### 1.2 KDTree.java (Relevant Methods)
```java
public class KDTree<E extends Comparable<E> & KDTreeNodeInterface> extends BST<E> {

    /**
     * Complexity: O(log n) for balanced tree
     * - Traverses the tree to depth O(log n)
     */
    @Override
    public void insert(E element) {
        if (element == null || element.getCoords() == null) {  // O(1)
            throw new IllegalArgumentException("Element and coordinates cannot be null");
        }

        KDNode<E> newNode = new KDNode<>(element, element.getCoords(), null, null);  // O(1)

        if (root == null) {  // O(1)
            root = newNode;  // O(1)
        } else {
            insert((KDNode<E>) root, newNode, true);  // O(log n)
        }
    }

    /**
     * Complexity: O(log n) - follows a path down the tree
     */
    private void insert(KDNode<E> currentNode, KDNode<E> node, boolean divX) {
        // Comparison: O(1)
        if (node.getCoords().equals(currentNode.getCoords())) {  // O(1)
            return;
        }

        // Axis-based comparison: O(1)
        int cmpResult = (divX ? cmpX : cmpY).compare(node, currentNode);  // O(1)

        if (cmpResult < 0) {
            if (currentNode.getLeft() == null) {  // O(1)
                currentNode.setLeft(node);  // O(1)
            } else {
                // Recursion: max depth O(log n)
                insert(currentNode.getLeft(), node, !divX);  // O(log n)
            }
        } else {
            if (currentNode.getRight() == null) {  // O(1)
                currentNode.setRight(node);  // O(1)
            } else {
                // Recursion: max depth O(log n)
                insert(currentNode.getRight(), node, !divX);  // O(log n)
            }
        }
    }

    /**
     * Complexity: O(√n + k) where k = number of results
     * - Best case: O(log n + k)
     * - Worst case: O(n + k) for degenerate tree
     */
    public List<E> rangeSearch(double latMin, double latMax,
                              double lonMin, double lonMax,
                              Predicate<E> filter) {
        // Validations: O(1)
        if (latMin > latMax) throw new IllegalArgumentException("latMin must be <= latMax");
        if (lonMin > lonMax) throw new IllegalArgumentException("lonMin must be <= lonMax");

        List<E> results = new ArrayList<>();  // O(1)
        // Recursive search: O(√n + k)
        rangeSearchRecursive((KDNode<E>) root, latMin, latMax, lonMin, lonMax,
                           true, results, filter);
        return results;
    }

    /**
     * Complexity: O(√n + k) for balanced tree
     */
    private void rangeSearchRecursive(KDNode<E> node,
                                    double latMin, double latMax,
                                    double lonMin, double lonMax,
                                    boolean divX, List<E> results,
                                    Predicate<E> filter) {
        if (node == null) {  // O(1)
            return;
        }

        Point2D.Double coords = node.getCoords();  // O(1)
        double x = coords.x; // longitude  // O(1)
        double y = coords.y; // latitude   // O(1)

        // Check if inside range: O(1)
        if (x >= lonMin && x <= lonMax && y >= latMin && y <= latMax) {
            E element = node.getElement();  // O(1)
            if (filter == null || filter.test(element)) {  // O(1) for simple filter
                results.add(element);  // O(1) amortized
            }
        }

        // Decide which subtrees to explore
        if (divX) {
            // Split by X (longitude)
            if (lonMin <= x) {
                rangeSearchRecursive(node.getLeft(), latMin, latMax,
                                   lonMin, lonMax, false, results, filter);
            }
            if (lonMax >= x) {
                rangeSearchRecursive(node.getRight(), latMin, latMax,
                                   lonMin, lonMax, false, results, filter);
            }
        } else {
            // Split by Y (latitude)
            if (latMin <= y) {
                rangeSearchRecursive(node.getLeft(), latMin, latMax,
                                   lonMin, lonMax, true, results, filter);
            }
            if (latMax >= y) {
                rangeSearchRecursive(node.getRight(), latMin, latMax,
                                   lonMin, lonMax, true, results, filter);
            }
        }
    }
}
```

### 1.3 BST.java (Height Methods)
```java
public class BST<E extends Comparable<E>> implements BSTInterface<E> {

    /**
     * Complexity: O(n) - traverses all nodes
     */
    public int height() {
        if (this.root == null) {  // O(1)
            return -1;  // O(1)
        }
        return height(root());  // O(n)
    }

    /**
     * Complexity: O(n) - traverses the entire subtree
     */
    protected int height(Node<E> node) {
        if (node == null) {  // O(1)
            return -1;  // O(1)
        }

        // Recursive calls: O(n)
        int leftHeight = height(node.getLeft());   // T(n/2) on average
        int rightHeight = height(node.getRight()); // T(n/2) on average

        return 1 + Math.max(leftHeight, rightHeight);  // O(1)
    }
}
```

## 2. Detailed Complexity Analysis

### 2.1 Recurrence for Construction
For buildBalancedKDTreeRecursive:

T(n) = 2 T(n/2) + O(n log n)

Resolution by levels:

- Level 0: O(n log n)  
- Level 1: 2 × O((n/2) log(n/2)) = O(n log(n/2))  
- Level 2: 4 × O((n/4) log(n/4)) = O(n log(n/4))  
- ...  
- Total levels: O(log n)

Sum total:
T(n) = O(n log n) + O(n log(n/2)) + O(n log(n/4)) + ... + O(n log 1)
     = O(n × log² n) (approximately)

### 2.2 Factors Affecting Actual Complexity
1. Geographic distribution:
   - Uniform data: O(n log² n)
   - Clustered data: can degrade performance (worst-case behavior may approach O(n²) for pathological splits)

2. Dataset size:
   - 1k stations: ~10 ms (example)
   - 10k stations: ~100 ms (example)
   - 64k stations: ~1–3 s (example)

3. Tree balance:
   - Balanced: range queries ~ O(√n + k)
   - Degenerate: range queries ~ O(n + k)

### 2.3 Memory Complexity
M(n) = O(n) (tree storage)
     + O(n) (temporary lists during construction)
     + O(log n) (recursion stack)
     = O(n)

### 2.4 Comparison with Alternative Approaches
- KD-Tree (balanced): Construction O(n log² n), Query O(√n + k), Memory O(n)  
- KD-Tree (simple): Construction O(n log n), Query O(n) worst-case, Memory O(n)  
- Linear list: Construction O(1), Query O(n), Memory O(n)  
- Spatial grid: Construction O(n), Query O(1) per cell, Memory O(n + m²)

## 3. Conclusion
O(n log² n) construction cost is acceptable because:

- Construction is typically done once at initialization
- It optimizes subsequent search operations
- It scales to large datasets (e.g., 64k+ stations)
- Produces well-balanced trees (balance ratio often ≤ 3.0)

Trade-off: higher build cost in exchange for much faster repeated spatial queries.
