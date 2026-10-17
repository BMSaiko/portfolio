# USEI13 - Hub Centrality Analysis - Complexity Analysis

## Overall Complexity

The hub centrality analysis consists of several phases, each with different complexity:

### 1. Degree Centrality - O(V)
- **Operation**: For each vertex, sum in-degree and out-degree
- **Time Complexity**: O(V) where V is the number of vertices
- **Space Complexity**: O(V) for the degree map
- **Justification**: Single pass through all vertices

### 2. Betweenness Centrality - O(V² × (V + E))
- **Operation**: For each source vertex, compute shortest paths to all destinations
- **Shortest Paths Computation**: O(V + E) using Dijkstra with priority queue for each source
- **Path Processing**: O(V) to process each path and update betweenness scores
- **Total**: O(V) sources × O(V + E) shortest paths + O(V²) path processing
- **Simplified**: O(V² × (V + E))
- **Space Complexity**: O(V²) for paths and distances storage
- **Justification**: 
  - For each of V source vertices
  - Compute shortest paths to all other vertices: O((V + E) log V) with heap
  - Process each of V paths to count intermediate vertices: O(V²)

### 3. Harmonic Closeness Centrality - O(V × (V + E))
- **Operation**: For each source vertex, compute shortest paths to all destinations
- **Shortest Paths Computation**: O(V + E) using Dijkstra for each source
- **Distance Summation**: O(V) to sum inverse distances
- **Total**: O(V) sources × O(V + E) shortest paths
- **Simplified**: O(V² + V × E)
- **Space Complexity**: O(V²) for paths and distances storage
- **Justification**:
  - For each of V source vertices
  - Compute shortest paths: O((V + E) log V) with heap
  - Sum inverse distances: O(V)

### 4. Normalization - O(V)
- **Operation**: Find min/max and normalize values for three metrics
- **Min/Max Finding**: O(V) for each metric
- **Normalization**: O(V) for each metric
- **Total**: 3 × 2 × O(V) = O(V)
- **Space Complexity**: O(V) for normalized maps
- **Justification**: Linear scan for each metric

### 5. Hub Score Computation - O(V)
- **Operation**: Combine normalized metrics for each vertex
- **Score Calculation**: O(1) per vertex
- **Total**: O(V)
- **Space Complexity**: O(V) for hub score map
- **Justification**: Single calculation per vertex

### 6. Result Creation and Sorting - O(V log V)
- **Result Creation**: O(V) to create HubScoreResult objects
- **Sorting**: O(V log V) using Collections.sort
- **Total**: O(V log V)
- **Space Complexity**: O(V) for result list
- **Justification**: Comparison-based sorting

## Total Complexity

**Time Complexity**: O(V² × (V + E))
- Dominated by betweenness centrality computation
- For dense graphs (E ≈ V²): O(V³)
- For sparse graphs (E ≈ V): O(V³)

**Space Complexity**: O(V² + E)
- O(V²) for storing all shortest paths and distances
- O(E) for graph representation
- O(V) for various maps and results

## Detailed Breakdown

### Betweenness Centrality Analysis

For a graph with V vertices and E edges:

```
betweennessCentrality():
    Initialize betweenness map: O(V)
    
    For each source (V iterations):                          // O(V)
        Compute shortest paths:                              // O((V + E) log V)
            - Dijkstra's algorithm with priority queue
        
        For each destination (V iterations):                 // O(V)
            Process path:                                    // O(V) worst case
                For each intermediate vertex:                // O(V) worst case
                    Increment betweenness                    // O(1)
    
    Total: O(V) + O(V × ((V + E) log V + V²))
    Simplified: O(V³) for dense graphs or O(V² × E) for sparse graphs
```

### Harmonic Closeness Analysis

```
closenessCentrality():
    Initialize closeness map: O(V)
    
    For each source (V iterations):                          // O(V)
        Compute shortest paths:                              // O((V + E) log V)
            - Dijkstra's algorithm with priority queue
        
        For each destination (V iterations):                 // O(V)
            Add 1/distance to sum                            // O(1)
    
    Total: O(V) + O(V × ((V + E) log V + V))
    Simplified: O(V² log V + V × E log V)
```

### Complete Algorithm Flow

```
computeHubRanking():
    1. Degree centrality:          O(V)
    2. Betweenness centrality:     O(V³) or O(V² × E)
    3. Closeness centrality:       O(V² log V + V × E log V)
    4. Normalize degree:           O(V)
    5. Normalize betweenness:      O(V)
    6. Normalize closeness:        O(V)
    7. Compute hub scores:         O(V)
    8. Create results:             O(V)
    9. Sort results:               O(V log V)
    
    Total: Dominated by betweenness centrality
    Final: O(V³) for dense graphs
```

## Optimization Considerations

### Possible Optimizations:
1. **Parallel Processing**: Centrality computations for different sources are independent
2. **Sampling**: Approximate betweenness using random sampling for very large graphs
3. **Incremental Updates**: Maintain centrality metrics and update incrementally when network changes
4. **Graph Simplification**: Remove low-degree vertices for approximate results

### Trade-offs:
- **Accuracy vs Speed**: Sampling reduces accuracy but improves performance
- **Memory vs Time**: Caching shortest paths improves time but increases memory
- **Exact vs Approximate**: Approximate algorithms can achieve O(V²) or better

## Scalability Analysis

For different network sizes:

| Vertices (V) | Edges (E) | Time Complexity | Estimated Runtime |
|-------------|-----------|-----------------|-------------------|
| 100         | ~1,000    | ~10⁶ ops       | < 1 second        |
| 1,000       | ~10,000   | ~10⁹ ops       | ~10 seconds       |
| 10,000      | ~100,000  | ~10¹² ops      | ~15 minutes       |
| 100,000     | ~1M       | ~10¹⁵ ops      | ~several hours    |

For large networks (V > 10,000), consider:
- Approximate betweenness algorithms
- Parallel processing
- Graph sampling techniques
- Distributed computation
