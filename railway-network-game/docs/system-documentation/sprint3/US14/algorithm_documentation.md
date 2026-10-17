# US14 Railway Maintenance Route Algorithm - Time Complexity Analysis

## Overview

Here is datailed a worst-case time complexity analysis of the Railway Maintenance Route Algorithm (US14) that finds an Eulerian path in a railway network graph using Fleury's Agorithm.

## Algorithm Components and Pseudocode

### 1. Main Algorithm - `getMaintenanceRoute`

```pseudocode
function getMaintenanceRoute(startStation, endStation, onlyElectrified):
    // Filter lines based on electrification requirement
    linesToConsider = new List()
    for each line in network.getAllLines():                    // O(E)
        if not onlyElectrified or line.isElectrified():
            linesToConsider.add(line)
    
    if linesToConsider.isEmpty():
        return empty list
    
    // Create working copy of lines
    remainingLines = copy of linesToConsider                   // O(E)
    path = new List([startStation])
    currentStation = startStation
    
    // Main traversal loop
    while remainingLines is not empty:                         // O(E) iterations
        nextLine = findNextLine(currentStation, remainingLines) // O(E * (V + E))
        
        if nextLine is null:
            return empty list
        
        // Move to next station
        nextStation = getOtherEnd(nextLine, currentStation)    // O(1)
        path.add(nextStation)                                  // O(1)
        currentStation = nextStation
        remainingLines.remove(nextLine)                        // O(E)
        
        // Check end condition
        if remainingLines.isEmpty() and endStation != null:    // O(1)
            if currentStation != endStation:
                return empty list
    
    return path
```

### 2. Next Line Selection - `findNextLine`

```pseudocode
function findNextLine(currentStation, remainingLines):
    // First pass: look for bridge edges
    for each line in remainingLines:                           // O(E)
        if line connects to currentStation:
            tempRemaining = copy of remainingLines             // O(E)
            tempRemaining.remove(line)                         // O(E)
            
            if isBridge(line, tempRemaining):                  // O(V + E)
                return line
    
    // Second pass: return any available edge
    for each line in remainingLines:                           // O(E)
        if line connects to currentStation:
            return line
    
    return null
```

### 3. Bridge Detection - `isBridge`

```pseudocode
function isBridge(line, remainingLines):
    if remainingLines.isEmpty():
        return true
    
    // Build station set from remaining lines
    stations = new Set()
    for each l in remainingLines:                              // O(E)
        stations.add(l.getSource())                            // O(1)
        stations.add(l.getDestination())                       // O(1)
    
    // Perform DFS to check connectivity
    visited = new Set()
    startStation = remainingLines.get(0).getSource()          // O(1)
    dfs(startStation, remainingLines, visited)                // O(V + E)
    
    // Check if all stations were visited
    return visited.size() != stations.size()                  // O(1)
```

### 4. Depth-First Search - `dfs`

```pseudocode
function dfs(current, lines, visited):
    visited.add(current)                                       // O(1)
    
    for each line in lines:                                    // O(E)
        if line.getSource() == current and 
           not visited.contains(line.getDestination()):        // O(1)
            dfs(line.getDestination(), lines, visited)         // Recursive call
        else if line.getDestination() == current and 
                not visited.contains(line.getSource()):        // O(1)
            dfs(line.getSource(), lines, visited)              // Recursive call
```


### 5. Helper functions
```pseudocode
function getPossibleStartStations(onlyElectrified):
    possibleStarts = new List()
    linesToConsider = new List()
    for each line in network.getAllLines():                    // O(E)
        if not onlyElectrified or line.isElectrified():
            linesToConsider.add(line)             // O(E)
    

    if linesToConsider is empty:
        return empty list

    for each station in network.getAllStations():             // O(V)
        degree = countStationDegree(station, linesToConsider)  // O(E)
        if degree > 0 and degree % 2 != 0:
            possibleStarts.add(station)
    
    // Handle Eulerian circuit case
    if possibleStarts is empty:                               // O(V)
        for each station in network.getAllStations():
            degree = countStationDegree(station, linesToConsider)
            if degree > 0:
                possibleStarts.add(station)
                break  

    
    return possibleStarts

function countStationDegree(station, lines):
    degree = 0
    for each line in lines:                                    // O(E)
        if line.getSource() == station or line.getDestination() == station:
            degree++
    return degree
```

## Detailed Complexity Analysis

### Individual Function Complexities

1. **`dfs(current, lines, visited)`**:
   - In the worst case, visits all V stations and examines all E lines
   - **Time Complexity: O(V + E)**

2. **`isBridge(line, remainingLines)`**:
   - Building stations set: O(E)
   - DFS traversal: O(V + E)
   - **Time Complexity: O(V + E)**

3. **`findNextLine(currentStation, remainingLines)`**:
   - First loop examines up to E lines
   - For each line, creates copy of remainingLines: O(E)
   - For each line, calls isBridge: O(V + E)
   - **Time Complexity: O(E × (E + (V + E))) = O(E² + E×V)**

4. **`countStationDegree(station, lines)`**:
   - Examines all E lines once
   - **Time Complexity: O(E)**

5. **`getPossibleStartStations(onlyElectrified)`**:
   - Filters lines: O(E)
   - For each of V stations, counts degree: O(E)
   - **Time Complexity: O(V × E)**

### Overall Algorithm Complexity

The main algorithm `getMaintenanceRoute` has the following complexity breakdown:

1. **Initial filtering and setup**: O(E)
2. **Main while loop**: 
   - Executes E times (once per edge)
   - Each iteration calls `findNextLine`: O(E² + E×V)
   - Each iteration removes a line: O(E)
   - **Loop complexity: O(E × (E² + E×V + E)) = O(E³ + E²×V)**

3. **Supporting operations** (when called):
   - `getPossibleStartStations`: O(V × E)
   - `getPossibleEndStations`: O(V × E)

### Final Worst-Case Time Complexity

**Primary Algorithm: O(E³ + E²×V)**

Where:
- **E** = number of railway lines (edges)
- **V** = number of stations (vertices)


## Conclusion

The theoretical time complexity of Fleury's algorithm is typically O(E²) when using efficient bridge detection. However, our implementation has higher complexity due to:

1. **Naive bridge detection**: Each bridge check requires O(V + E) DFS
2. **List operations**: Creating copies of remaining lines for each check
3. **Linear search**: Finding connected edges through linear iteration


The current implementation of the Railway Maintenance Route Algorithm has a worst-case time complexity of **O(E³ + E²×V)**, which is higher than the theoretical optimal for Fleury's algorithm. This is primarily due to the bridge detection strategy and data structure choices. While functional and correct, the algorithm could benefit from optimization for larger railway networks.

For typical railway networks where E ≈ V (sparse graphs), the complexity simplifies to approximately **O(E³)**, making it suitable for moderately-sized networks but potentially requiring optimization for very large railway systems.