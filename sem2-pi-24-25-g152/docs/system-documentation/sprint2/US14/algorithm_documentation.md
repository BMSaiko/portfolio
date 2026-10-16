# Railway Maintenance Route Algorithm Documentation

## Overview

This document details the algorithm implemented for the Railway Maintenance Route Finder (US14) which finds a route that passes once and only once through each railway line for maintenance purposes. This is essentially finding an [Eulerian Path](https://en.wikipedia.org/wiki/Eulerian_path) in a graph representing the railway network.

## Problem Statement

Given:
- A railway network with stations and connecting railway lines
- An option to consider all lines or only electrified lines
- Optional start and end stations

Find:
- A maintenance route that passes once and only once through each railway line 
- The route must start and end at valid stations selected by the user

## Algorithm Implementation

The solution uses Fleury's algorithm to find an Eulerian path in the railway network graph.

### Data Structures

- **Station**: Represents a station in the railway network (vertex in the graph)
- **RailwayLine**: Represents a railway line connecting two stations (edge in the graph)
- **List<Station>**: To store the final maintenance route
- **List<RailwayLine>**: To track remaining lines that need to be traversed
- **Set<Station>**: For depth-first search during bridge detection

### Algorithm Flow

1. **User Interface Flow**:
   - Prompt the user to choose whether to maintain all lines or only electrified lines
   - Display possible start stations and let user select one
   - Display possible end stations and let user select one
   - Generate and display the maintenance route

2. **Core Algorithm (Fleury's Algorithm)**:
   - Find stations with odd degree (odd number of connected lines)
   - Verify if an Eulerian path is possible:
     - If no odd-degree stations exist, start at any station (Eulerian circuit)
     - If exactly two odd-degree stations exist, start at one of them (Eulerian path)
     - If more than two odd-degree stations exist, no valid maintenance route exists
   - From the start station, traverse the graph:
     - Always prefer non-bridge edges (edges whose removal won't disconnect the graph)
     - Remove each traversed edge from the graph
     - Continue until all edges are traversed

3. **Bridge Detection**:
   - An edge is a bridge if removing it disconnects the graph
   - For each potential edge to traverse, we:
     - Temporarily remove the edge
     - Run DFS to check if the graph remains connected
     - If connected, it's not a bridge and can be safely traversed
     - If disconnected, it's a bridge and should be traversed only if no alternatives exist

### Algorithm Pseudocode

```
function getMaintenanceRoute(startStation, endStation, onlyElectrified):
    // Filter lines based on electrified status if needed
    linesToConsider = filter lines based on onlyElectrified
    
    // Create a copy of lines to track which ones have been visited
    remainingLines = copy of linesToConsider
    
    // Start building the path
    path = new List with startStation
    currentStation = startStation
    
    while remainingLines is not empty:
        nextLine = findNextLine(currentStation, remainingLines)
        
        if nextLine is null:
            return empty list // No valid route
        
        // Determine next station
        nextStation = the other end of nextLine from currentStation
        
        path.add(nextStation)
        currentStation = nextStation
        remainingLines.remove(nextLine)
        
        // Check if we reached the endStation and used all lines
        if remainingLines is empty and endStation is specified:
            if currentStation is not endStation:
                return empty list // Failed to end at specified station
    
    return path

function findNextLine(currentStation, remainingLines):
    // First try to find a non-bridge edge
    for each line connected to currentStation:
        temporarily remove line
        if graph remains connected:
            return line // Not a bridge
        restore line
    
    // If all are bridges, just return any connected edge
    for each line connected to currentStation:
        return line
    
    return null // No valid next line
```

## Time and Space Complexity Analysis

- **Time Complexity**: O(E² + E*V), where E is the number of railway lines and V is the number of stations
  - Finding odd-degree stations: O(V + E)
  - For each line (E), we may need to check if it's a bridge
  - Bridge detection via DFS: O(V + E)

- **Space Complexity**: O(V + E)
  - Storage for the graph: O(V + E)
  - Storage for the path: O(E + 1)
  - Storage for visited set during DFS: O(V)

## Special Features

1. **Filtering for Electrified Lines**:
   - The algorithm can find a maintenance route that includes either all railway lines or only electrified lines
   - When only electrified lines are considered, non-electrified lines are filtered out before running the algorithm

2. **Flexible Start/End Stations**:
   - The user can select specific start and end stations
   - The algorithm automatically identifies valid start stations (those with odd degree)
   - If no odd-degree stations exist, any station can be a start point (Eulerian circuit)

3. **Visualization**:
   - The solution generates a DOT file for Graphviz to visualize:
     - The complete railway network
     - The maintenance route with highlighted path
     - Different colors for different station types
     - Different colors for electrified vs. non-electrified lines

## Implementation Details

The implementation follows a layered architecture:
1. **UI Layer**: `RailwayMaintenanceUI` class handles user interaction
2. **Controller Layer**: `RailwayMaintenanceController` coordinates between UI and service
3. **Service Layer**: `RailwayMaintenanceService` contains the core algorithm
4. **Domain Layer**: `Station`, `RailwayLine`, and `RailwayNetwork` classes model the domain entities

The implementation uses only primitive operations and standard Java collections, without relying on existing graph library implementations, ensuring a thorough understanding of the algorithm.