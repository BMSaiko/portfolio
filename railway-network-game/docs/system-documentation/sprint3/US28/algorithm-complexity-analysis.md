# Algorithm Complexity Analysis

This document provides the worst-case time complexity analysis for the core algorithms implemented in the `TransitiveClosureShortestPathService` class.

---

## 1. Floyd-Warshall Algorithm (Transitive Closure)

### Pseudocode

```plaintext
ALGORITHM FloydWarshall
INPUT: adjacency matrix distance[n][n]
OUTPUT: shortest path matrix distance[n][n], next hop matrix next[n][n]

BEGIN
    FOR k ← 0 TO n-1 DO
        FOR i ← 0 TO n-1 DO
            FOR j ← 0 TO n-1 DO
                IF distance[i][k] + distance[k][j] < distance[i][j] THEN
                    distance[i][j] ← distance[i][k] + distance[k][j]
                    next[i][j] ← next[i][k]
                END IF
            END FOR
        END FOR
    END FOR
END

```

Time Complexity
- Worst-case: O(n³)
  - Where n is the number of stations.


## 2. Dijkstra’s Algorithm (findShortestPathDijkstra)

### Pseudocode

```plaintext
ALGORITHM Dijkstra
INPUT: graph G, source vertex s, destination vertex t
OUTPUT: shortest path from s to t

BEGIN
    FOR each vertex v IN G DO
        distance[v] ← ∞
        previous[v] ← undefined
    END FOR
    
    distance[s] ← 0
    Q ← empty priority queue
    INSERT (s, 0) INTO Q
    
    WHILE Q is not empty DO
        u ← EXTRACT-MIN(Q)
        
        IF u = t THEN
            BREAK
        END IF
        
        FOR each neighbor v of u DO
            alt ← distance[u] + weight(u, v)
            IF alt < distance[v] THEN
                distance[v] ← alt
                previous[v] ← u
                INSERT (v, alt) INTO Q
            END IF
        END FOR
    END WHILE
    
    path ← RECONSTRUCT-PATH(previous, s, t)
    RETURN path
END
```

Time Complexity
- Worst-case: O((V + E) * log V) using a priority queue (heap)

    - V = number of stations
    - E = number of railway lines

## 3. Shortest Path with Waypoints

### Pseudocode

```plaintext

ALGORITHM ShortestPathWithWaypoints
INPUT: source s, destination t, waypoints W[]
OUTPUT: complete path visiting all waypoints

BEGIN
    orderedPoints ← [s] + W + [t]
    fullPath ← empty list
    
    FOR i ← 0 TO length(orderedPoints) - 2 DO
        segment ← Dijkstra(orderedPoints[i], orderedPoints[i+1])
        
        IF segment is empty THEN
            RETURN empty path
        END IF
        
        APPEND segment TO fullPath (excluding duplicate endpoints)
    END FOR
    
    RETURN fullPath
END
```

Time Complexity
- Worst-case: O(k * (V + E) * log V)
  - Where k is the number of segments (number of waypoints + 1)


## 4. Reachability Check using Floyd-Warshall Matrix

### Pseudocode

```plaintext

ALGORITHM GetReachableStations
INPUT: source station s
OUTPUT: set of reachable stations from s

BEGIN
    IF transitive closure not computed THEN
        CALL FloydWarshall()
    END IF
    
    reachableStations ← empty set
    
    FOR i ← 0 TO n-1 DO
        IF distance[s][i] ≠ ∞ THEN
            ADD i TO reachableStations
        END IF
    END FOR
    
    RETURN reachableStations
END
```

Time Complexity

- Worst-case (after precomputation): O(n)
- Preprocessing (Floyd-Warshall): O(n³)

## 5. Network Statistics (getStatistics)

### Pseudocode

```plaintext

ALGORITHM GetNetworkStatistics
INPUT: network graph G
OUTPUT: connectivity statistics

BEGIN
    totalConnections ← 0
    maxDistance ← 0
    minDistance ← ∞
    
    FOR each station i IN G DO
        distances ← Dijkstra(i, all other stations)
        
        FOR each station j ≠ i DO
            IF distances[j] ≠ ∞ THEN
                totalConnections ← totalConnections + 1
                maxDistance ← MAX(maxDistance, distances[j])
                minDistance ← MIN(minDistance, distances[j])
            END IF
        END FOR
    END FOR
    
    stats ← {
        totalConnections: totalConnections,
        maxDistance: maxDistance,
        minDistance: minDistance,
        averageDistance: totalConnections / (n × (n-1))
    }
    
    RETURN stats
END
```

Time Complexity
- Worst-case: O(V * (V + E) * log V)

