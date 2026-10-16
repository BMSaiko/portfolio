# US13 Railway Connectivity Algorithm - Time Complexity Analysis

## Overview

Here is datailed a worst-case time complexity analysis of the Railway Connectivity Algorithm (US13) that checks connectivity and finds paths in a railway network graph. 

## Algorithm Components and Pseudocode

### 1. Main Algorithm - `canTravel`

```pseudocode
function canTravel(source, destination, trainType):
    if source equals destination:                              // O(1)
        return true
    
    // Initialize BFS data structures
    queue = new Queue()                                        // O(1)
    visited = new Set()                                        // O(1)
    previous = new Map()                                       // O(1)
    
    queue.add(source)                                          // O(1)
    visited.add(source)                                        // O(1)
    
    // BFS traversal
    while queue is not empty:                                  // O(V) iterations
        current = queue.poll()                                 // O(1)
        
        for each line in network.getLinesForStation(current): // O(E) total across all iterations
            if not trainType.canOperateOn(line):               // O(1)
                continue
            
            next = line.getOtherStation(current)               // O(1)
            
            if next not in visited:                            // O(1)
                visited.add(next)                              // O(1)
                previous.put(next, current)                    // O(1)
                queue.add(next)                                // O(1)
                
                if next equals destination:                    // O(1)
                    return true
    
    return false
```

### 2. Reachable Stations Algorithm - `findReachableStationsOfType`

```pseudocode
function findReachableStationsOfType(source, stationType, trainType):
    reachableStations = new List()                             // O(1)
    queue = new Queue()                                        // O(1)
    visited = new Set()                                        // O(1)
    
    queue.add(source)                                          // O(1)
    visited.add(source)                                        // O(1)
    
    // BFS traversal
    while queue is not empty:                                  // O(V) iterations
        current = queue.poll()                                 // O(1)
        
        // Check if station matches criteria
        if current.getType() == stationType and               // O(1)
           not current.equals(source):                         // O(1)
            reachableStations.add(current)                     // O(1)
        
        for each line in network.getLinesForStation(current): // O(E) total across all iterations
            if not trainType.canOperateOn(line):               // O(1)
                continue
            
            next = line.getOtherStation(current)               // O(1)
            
            if next not in visited:                            // O(1)
                visited.add(next)                              // O(1)
                queue.add(next)                                // O(1)
    
    return reachableStations
```

### 3. Path Finding Algorithm - `getPath`

```pseudocode
function getPath(source, destination, trainType):
    if source equals destination:                              // O(1)
        return [source]
    
    // Initialize BFS data structures
    queue = new Queue()                                        // O(1)
    visited = new Set()                                        // O(1)
    previous = new Map()                                       // O(1)
    
    queue.add(source)                                          // O(1)
    visited.add(source)                                        // O(1)
    pathFound = false                                          // O(1)
    
    // BFS traversal
    while queue is not empty and not pathFound:               // O(V) iterations
        current = queue.poll()                                 // O(1)
        
        for each line in network.getLinesForStation(current): // O(E) total across all iterations
            if not trainType.canOperateOn(line):               // O(1)
                continue
            
            next = line.getOtherStation(current)               // O(1)
            
            if next not in visited:                            // O(1)
                visited.add(next)                              // O(1)
                previous.put(next, current)                    // O(1)
                queue.add(next)                                // O(1)
                
                if next equals destination:                    // O(1)
                    pathFound = true
                    break
    
    if not pathFound:
        return empty list
    
    // Reconstruct path
    path = new List()                                          // O(1)
    current = destination                                      // O(1)
    
    while current is not null:                                 // O(V) iterations
        path.add(current)                                      // O(1)
        current = previous.get(current)                        // O(1)
    
    reverse(path)                                              // O(V)
    
    return path
```

### 4. Network Access Functions

```pseudocode
function getLinesForStation(station):
    // Assumes this returns all lines connected to the station
    // Implementation depends on network data structure
    // In worst case, examines all lines
    lines = new List()
    for each line in network.getAllLines():                    // O(E)
        if line.getSource() == station or line.getDestination() == station:
            lines.add(line)
    return lines
```

## Detailed Complexity Analysis

### Individual Function Complexities

1. **`canTravel(source, destination, trainType)`**:
   - BFS visits each vertex at most once: O(V)
   - Each edge is examined at most twice (once from each endpoint): O(E)
   - **Time Complexity: O(V + E)**

2. **`findReachableStationsOfType(source, stationType, trainType)`**:
   - BFS visits each vertex at most once: O(V)
   - Each edge is examined at most twice: O(E)
   - Additional operations for checking station type: O(V)
   - **Time Complexity: O(V + E)**

3. **`getPath(source, destination, trainType)`**:
   - BFS traversal: O(V + E)
   - Path reconstruction: O(V)
   - Path reversal: O(V)
   - **Time Complexity: O(V + E)**

4. **`getLinesForStation(station)`** (if implemented naively):
   - Examines all lines in the network
   - **Time Complexity: O(E)**

### Network Data Structure Impact

The complexity analysis assumes different possible implementations of `getLinesForStation`:

**Case 1: Adjacency List Implementation**
- Each station maintains a list of connected lines
- `getLinesForStation`: O(degree(station)) ≈ O(E/V) on average
- Overall BFS complexity remains O(V + E)

**Case 2: Naive Implementation (as shown in pseudocode)**
- Must search through all lines for each station
- `getLinesForStation`: O(E)
- This would increase overall complexity to O(V × E)

### Overall Algorithm Complexity

Assuming an efficient adjacency-based network representation:

1. **`canTravel`**: **O(V + E)**
2. **`findReachableStationsOfType`**: **O(V + E)**
3. **`getPath`**: **O(V + E)**



### Standard Graph Algorithms

The implemented algorithms are variations of standard graph traversal algorithms:

1. **Breadth-First Search (BFS)**: The theoretical optimal time complexity for BFS is O(V + E)
2. **Connectivity checking**: Optimal complexity is O(V + E) using BFS or DFS
3. **Shortest path (unweighted)**: BFS provides optimal O(V + E) complexity

Our implementation achieves these theoretical bounds, making it optimal for the given problems.


## Conclusion

The Railway Connectivity Service algorithms have optimal worst-case time complexity of **O(V + E)** for all three main operations (connectivity checking, reachable stations finding, and path finding), where:

- **V** = number of stations (vertices)
- **E** = number of railway lines (edges)

This matches the theoretical lower bound for graph traversal problems and represents an efficient implementation. The algorithms use standard BFS techniques with appropriate optimizations for early termination and efficient data structure usage.

The space complexity of **O(V)** is also optimal, using only the necessary space for tracking visited nodes and reconstructing paths. These algorithms are well-suited for railway networks of any practical size and provide excellent performance characteristics.