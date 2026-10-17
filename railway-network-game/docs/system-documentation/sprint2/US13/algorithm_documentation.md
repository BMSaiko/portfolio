# US13 - Railway Connectivity Algorithm Documentation

## Overview

This document details the algorithms implemented for US13 - Railway Connectivity Checker, which determines if specific train types (Steam, Diesel, or Electric) can travel between stations in a railway network, considering train-specific operational constraints.

## Problem Statement

**Given:**
- A railway network represented as an undirected graph with stations (vertices) and railway lines (edges)
- Railway lines with properties: distance, electrification status
- Three train types with different operational constraints
- Source and destination stations within the network

**Determine:**
1. If a specified train type can travel from source to destination
2. The actual path taken (if exists)
3. All stations of a specific type reachable from a source station

**Constraints:**
- **Steam and Diesel trains**: Can operate on any railway line (electrified or non-electrified)
- **Electric trains**: Can only operate on electrified railway lines
- Railway lines are bidirectional (undirected graph)

## Algorithm Implementation

### Core Algorithm: Breadth-First Search (BFS)

The solution employs BFS for graph traversal, which guarantees finding a path if one exists and explores the graph level by level, ensuring optimal exploration for unweighted connectivity problems.

#### Why BFS was chosen:
1. **Completeness**: Guarantees finding a solution if one exists
2. **Optimality**: Finds the shortest path in terms of number of edges
3. **Simplicity**: Straightforward implementation with clear logic flow
4. **Memory Efficiency**: O(V) space complexity

### Data Structures

```java
// Core data structures used in the algorithm
Queue<Station> explorationQueue;           // BFS traversal queue
Set<Station> visitedStations;              // Cycle prevention
Map<Station, Station> predecessorMap;      // Path reconstruction
```

### Algorithm Implementation Details

#### 1. Connectivity Check Algorithm

```java
function canTravel(source, destination, trainType):
    // Base case: same station
    if source equals destination:
        return true
    
    // Initialize BFS data structures
    queue = new LinkedList<Station>()
    visited = new HashSet<Station>()
    
    // Start BFS from source
    queue.add(source)
    visited.add(source)
    
    // BFS traversal
    while queue is not empty:
        current = queue.poll()
        
        // Explore all connected railway lines
        for each line in network.getLinesForStation(current):
            // Apply train type constraints
            if not trainType.canOperateOn(line):
                continue to next line
            
            // Get the connected station
            next = line.getOtherStation(current)
            
            // Check if station not yet visited
            if next not in visited:
                visited.add(next)
                queue.add(next)
                
                // Check if destination reached
                if next equals destination:
                    return true
    
    // No path found
    return false
```

#### 2. Path Reconstruction Algorithm

```java
function getPath(source, destination, trainType):
    // Base case
    if source equals destination:
        return [source]
    
    // Initialize structures
    queue = new LinkedList<Station>()
    visited = new HashSet<Station>()
    previous = new HashMap<Station, Station>()
    
    queue.add(source)
    visited.add(source)
    pathFound = false
    
    // BFS with path tracking
    while queue is not empty and not pathFound:
        current = queue.poll()
        
        for each line in network.getLinesForStation(current):
            if not trainType.canOperateOn(line):
                continue
            
            next = line.getOtherStation(current)
            
            if next not in visited:
                visited.add(next)
                previous.put(next, current)  // Track predecessor
                queue.add(next)
                
                if next equals destination:
                    pathFound = true
                    break
    
    // Reconstruct path if found
    if not pathFound:
        return empty list
    
    path = new ArrayList<Station>()
    current = destination
    
    // Trace back from destination to source
    while current is not null:
        path.add(current)
        current = previous.get(current)
    
    // Reverse to get source-to-destination order
    reverse(path)
    return path
```

#### 3. Reachable Stations by Type Algorithm

```java
function findReachableStationsOfType(source, stationType, trainType):
    reachableStations = new ArrayList<Station>()
    
    // Standard BFS setup
    queue = new LinkedList<Station>()
    visited = new HashSet<Station>()
    
    queue.add(source)
    visited.add(source)
    
    // BFS traversal
    while queue is not empty:
        current = queue.poll()
        
        // Check if current station matches desired type (exclude source)
        if current.getType() equals stationType and current != source:
            reachableStations.add(current)
        
        // Continue exploration
        for each line in network.getLinesForStation(current):
            if not trainType.canOperateOn(line):
                continue
            
            next = line.getOtherStation(current)
            
            if next not in visited:
                visited.add(next)
                queue.add(next)
    
    return reachableStations
```

### Train Type Constraint Implementation

The constraint checking is encapsulated in the `TrainType` enum:

```java
public enum TrainType {
    STEAM, DIESEL, ELECTRIC;
    
    public boolean canOperateOn(RailwayLine line) {
        // Electric trains require electrified lines
        if (this == ELECTRIC) {
            return line.isElectrified();
        }
        // Steam and diesel can operate on any line
        return true;
    }
}
```

## Complexity Analysis

### Time Complexity
- **All algorithms**: O(V + E)
  - V = number of stations (vertices)
  - E = number of railway lines (edges)
  - Worst case: visit all stations and examine all lines

### Space Complexity
- **All algorithms**: O(V)
  - Queue storage: O(V) in worst case
  - Visited set: O(V)
  - Predecessor map: O(V)

### Complexity Justification
- **BFS Time**: Each vertex is visited at most once, each edge examined at most twice (once from each endpoint)
- **BFS Space**: Queue size bounded by number of vertices in worst case (all vertices at same level)

## Network Representation

### Graph Model
- **Vertices**: Railway stations with properties (name, type)
- **Edges**: Railway lines with properties (distance, electrification status)
- **Graph Type**: Undirected (railway lines are bidirectional)

### Station Types
```java
public enum StationType {
    DEPOT("D_"),     // Maintenance and storage facilities
    STATION("S_"),   // Regular passenger stations  
    TERMINAL("T_");  // End-of-line stations
}
```

## Implementation Architecture

### Separation of Concerns

1. **Domain Layer**
   - `Station`: Represents railway stations with type and identification
   - `RailwayLine`: Represents connections with distance and electrification
   - `TrainType`: Encapsulates operational constraints
   - `RailwayNetwork`: Graph container with adjacency operations

2. **Service Layer**
   - `RailwayConnectivityService`: Core BFS algorithms implementation
   - Stateless design for reusability
   - Clear separation of connectivity logic from business rules

3. **Controller Layer**
   - `RailwayConnectivityController`: Coordinates between UI and service
   - Input validation and error handling
   - Result formatting and response management

4. **UI Layer**
   - `RailwayConnectivityUI`: User interaction and input collection
   - Result presentation and visualization coordination

### Design Principles Applied

1. **Single Responsibility**: Each class has one clear purpose
2. **Open/Closed**: Train types can be extended without modifying core algorithms
3. **Dependency Inversion**: Controllers depend on service abstractions
4. **Encapsulation**: Internal algorithm details hidden from clients

## Visualization and Output

### Graphviz Integration
Results are visualized using DOT format files with:
- **Station visualization**: Different colors by type (Depot: lightblue, Station: lightgreen, Terminal: lightcoral)
- **Line visualization**: Blue for electrified lines, black for non-electrified
- **Path highlighting**: Bold red lines for computed paths
- **Station highlighting**: Bold borders for stations in computed paths

### Output Examples
```
Path: [S_StationA, S_StationB, T_Terminal1]
Distance: 3 stations, 45 km total
Visualization: railway_path.dot generated
```

## Error Handling and Edge Cases

### Handled Cases
1. **Same source and destination**: Returns immediately with single-station path
2. **No path exists**: Returns empty path list
3. **Invalid stations**: Proper error messages
4. **Electric train on non-electrified network**: No valid paths found
5. **Isolated stations**: Detected and reported

### Input Validation
- Station existence verification
- Train type validation
- Network connectivity verification

## Performance Characteristics

### Best Case: O(1)
- Source equals destination

### Average Case: O(V + E)
- Typical network exploration

### Worst Case: O(V + E)  
- Must explore entire connected component

### Memory Usage
- Constant factor overhead
- Scales linearly