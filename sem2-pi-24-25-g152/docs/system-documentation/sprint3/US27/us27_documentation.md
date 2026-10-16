# US27 - Shortest Path with Waypoints

## Overview

**User Story:** As a Player, given a scenario with stations and railway lines, I want to get one of the shortest routes between two stations, which goes through an ordered list of stations I choose.

**Acceptance Criteria:**
- AC01: Visualization of scenario with highlighted shortest route
- AC02: Use only primitive operations (no Java library functions)

## Implementation Architecture

```
controller/us27/TransitiveClosureShortestPathController.java
service/us27/TransitiveClosureShortestPathService.java  
ui/console/us27/ShortestPathWithWaypointsUI.java
```

## Core Algorithms

### 1. Dijkstra's Algorithm (Primary)

**Purpose:** Find shortest paths between stations with waypoints

**Pseudocode:**
```plaintext
function findShortestPathDijkstra(source, destination)
    distance[source] = 0, all others = ∞
    queue = priority queue with (source, 0)
    visited = empty set

    while queue not empty:
        current = queue.poll()
        if current in visited: continue
        visited.add(current)
        
        if current == destination: break
            
        for each neighbor of current:
            newDistance = distance[current] + edge_weight
            if newDistance < distance[neighbor]:
                distance[neighbor] = newDistance
                previous[neighbor] = current
                queue.add(neighbor)

    return reconstructPath(previous, source, destination)
```

**Complexity:** O((V + E) log V)

### 2. Floyd-Warshall Algorithm (Supporting)

**Purpose:** Precompute all-pairs shortest distances for connectivity analysis

**Pseudocode:**
```plaintext
function computeTransitiveClosure()
    for k from 0 to n-1:
        for i from 0 to n-1:
            for j from 0 to n-1:
                if distance[i][k] + distance[k][j] < distance[i][j]:
                    distance[i][j] = distance[i][k] + distance[k][j]
```

**Complexity:** O(V³)

### 3. Waypoint Path Finding

**Pseudocode:**
```plaintext
function findShortestPathWithWaypoints(source, destination, waypoints)
    orderedStations = [source] + waypoints + [destination]
    fullPath = []
    
    for i from 0 to orderedStations.length - 2:
        segment = dijkstra(orderedStations[i], orderedStations[i+1])
        if segment is empty: return []
        append segment to fullPath (avoiding duplicates)
    
    return fullPath
```

**Complexity:** O(k × (V + E) log V) where k = number of waypoints + 1

## Key Components

### TransitiveClosureShortestPathController
- Loads railway network from CSV files
- Coordinates pathfinding operations
- Generates Graphviz visualizations
- Provides network analysis

### TransitiveClosureShortestPathService  
- Implements Dijkstra and Floyd-Warshall algorithms
- Manages distance/next matrices
- Handles waypoint routing logic
- Computes network statistics

### ShortestPathWithWaypointsUI
- Interactive station selection
- Waypoint management
- Path result display
- Visualization file generation

## Data Structures

```java
private int[][] distanceMatrix;     // All-pairs distances (Floyd-Warshall)
private int[][] nextMatrix;         // Path reconstruction
private Map<Station, Integer> stationIndex;  // Station-to-index mapping
private List<Station> stationsList; // Ordered stations list
```

## Algorithm Selection Strategy

| Use Case | Algorithm | Reason |
|----------|-----------|---------|
| Single path query | Dijkstra | Optimal for one-time queries |
| Multiple path queries | Floyd-Warshall + lookup | O(1) after O(V³) precomputation |
| Waypoint routing | Dijkstra segments | Optimal paths through waypoints |
| Connectivity analysis | Floyd-Warshall | All-pairs information needed |

## Usage Example

```java
// Initialize controller
TransitiveClosureShortestPathController controller = 
    new TransitiveClosureShortestPathController("stations.csv", "lines.csv");

// Find path with waypoints
List<Station> waypoints = Arrays.asList(station1, station2);
List<Station> path = controller.findShortestPathWithWaypoints(source, dest, waypoints);

// Generate visualization
controller.generateVisualization(path, "output.dot");
```

## Complexity Summary

| Operation | Time Complexity | Space Complexity |
|-----------|----------------|------------------|
| Dijkstra (single query) | O((V + E) log V) | O(V) |
| Floyd-Warshall (precompute) | O(V³) | O(V²) |
| Waypoint path (k waypoints) | O(k × (V + E) log V) | O(V) |
| Distance query | O((V + E) log V) | O(V) |
| Connectivity check | O(1) after precompute | O(V²) |

## Visualization

- Generates DOT files for Graphviz
- Highlights shortest path in different color
- Shows station types and line connections
- Exports to multiple image formats

## Error Handling

- Returns empty list for impossible paths
- Validates station existence
- Handles disconnected network components
- Manages null/empty waypoint lists gracefully