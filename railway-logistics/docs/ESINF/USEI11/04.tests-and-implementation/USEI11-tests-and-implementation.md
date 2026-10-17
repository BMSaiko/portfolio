# USEI11 - Station Upgrade Order with Dependency Analysis

## 4. Tests

**Test 1:** Check that topological order is computed correctly for acyclic graph

```java
@Test
void testComputeUpgradePlanAcyclic() {
    RailwayUpgradeService service = new RailwayUpgradeService();
    UpgradePlanResult result = service.computeUpgradePlan();
    
    assertNotNull(result);
    assertFalse(result.isHasCycles());
    assertNotNull(result.getTopologicalOrder());
    assertEquals(result.getVerticesCount(), result.getTopologicalOrder().size());
}
```

**Test 2:** Check that topological order respects dependencies

```java
@Test
void testTopologicalOrderRespectsDependencies() {
    RailwayUpgradeService service = new RailwayUpgradeService();
    UpgradePlanResult result = service.computeUpgradePlan();
    
    if (!result.isHasCycles()) {
        List<Station> order = result.getTopologicalOrder();
        Map<Station, Integer> position = new HashMap<>();
        
        for (int i = 0; i < order.size(); i++) {
            position.put(order.get(i), i);
        }
        
        Graph<Station, TrackConnection> graph = service.getRepositoryGraph();
        for (Station from : graph.vertices()) {
            for (Edge<Station, TrackConnection> edge : graph.outgoingEdges(from)) {
                Station to = edge.getVDest();
                assertTrue(position.get(from) < position.get(to),
                        from.getName() + " should come before " + to.getName());
            }
        }
    }
}
```

**Test 3:** Check that cycles are detected correctly

```java
@Test
void testCycleDetection() {
    // Create graph with cycle: A -> B -> C -> A
    Graph<Station, TrackConnection> graph = new MapGraph<>(true);
    Station a = new Station(1, "A");
    Station b = new Station(2, "B");
    Station c = new Station(3, "C");
    
    graph.addVertex(a);
    graph.addVertex(b);
    graph.addVertex(c);
    
    graph.addEdge(a, b, new TrackConnection(10, 100, 5));
    graph.addEdge(b, c, new TrackConnection(10, 100, 5));
    graph.addEdge(c, a, new TrackConnection(10, 100, 5));
    
    RailwayUpgradeService service = new RailwayUpgradeService(graph);
    UpgradePlanResult result = service.computeUpgradePlan();
    
    assertTrue(result.isHasCycles());
    assertEquals(3, result.getCycleStations().size());
}
```

**Test 4:** Check that cycle stations are identified correctly

```java
@Test
void testCycleStationsIdentification() {
    RailwayUpgradeService service = new RailwayUpgradeService();
    UpgradePlanResult result = service.computeUpgradePlan();
    
    if (result.isHasCycles()) {
        assertFalse(result.getCycleStations().isEmpty());
        assertFalse(result.getCycleEdges().isEmpty());
        
        for (Station station : result.getCycleStations()) {
            assertNotNull(station.getName());
        }
    }
}
```

**Test 5:** Check that all vertices are counted correctly

```java
@Test
void testGraphStatistics() {
    RailwayUpgradeService service = new RailwayUpgradeService();
    UpgradePlanResult result = service.computeUpgradePlan();
    
    assertTrue(result.getVerticesCount() > 0);
    assertTrue(result.getEdgesCount() >= 0);
    
    Graph<Station, TrackConnection> graph = service.getRepositoryGraph();
    assertEquals(graph.numVertices(), result.getVerticesCount());
    assertEquals(graph.numEdges(), result.getEdgesCount());
}
```

## 5. Construction (Implementation)

### 5.1 RailwayUpgradeService Class

```java
private void buildInternalStructures() {
    Graph<Station, TrackConnection> graph = repository.getNetworkGraph();

    inDegree.clear();
    adjacencyList.clear();

    for (Station station : graph.vertices()) {
        inDegree.put(station, 0);
        adjacencyList.put(station, new ArrayList<>());
    }

    for (Station from : graph.vertices()) {
        for (Edge<Station, TrackConnection> edge : graph.outgoingEdges(from)) {
            Station to = edge.getVDest();
            adjacencyList.get(from).add(to);
            inDegree.put(to, inDegree.get(to) + 1);
        }
    }
}
```

### 5.2 Topological Sort Implementation

```java
public UpgradePlanResult computeUpgradePlan() {
    buildInternalStructures();
    Graph<Station, TrackConnection> graph = repository.getNetworkGraph();

    UpgradePlanResult result = new UpgradePlanResult();
    result.setVerticesCount(graph.numVertices());
    result.setEdgesCount(graph.numEdges());

    List<Station> topologicalOrder = new ArrayList<>();
    Queue<Station> queue = new LinkedList<>();

    Map<Station, Integer> tempInDegree = new HashMap<>(inDegree);

    for (Station station : graph.vertices()) {
        if (tempInDegree.get(station) == 0) {
            queue.add(station);
        }
    }

    while (!queue.isEmpty()) {
        Station current = queue.poll();
        topologicalOrder.add(current);

        for (Station neighbor : adjacencyList.get(current)) {
            int newInDegree = tempInDegree.get(neighbor) - 1;
            tempInDegree.put(neighbor, newInDegree);

            if (newInDegree == 0) {
                queue.add(neighbor);
            }
        }
    }

    if (topologicalOrder.size() != graph.numVertices()) {
        result.setHasCycles(true);
        result.setCycleStations(findCycleStations(tempInDegree));
        result.setCycleEdges(findCycleEdges(tempInDegree));
    } else {
        result.setTopologicalOrder(topologicalOrder);
        result.setHasCycles(false);
    }

    return result;
}
```

### 5.3 Cycle Detection

```java
private List<Station> findCycleStations(Map<Station, Integer> remainingInDegree) {
    List<Station> cycleStations = new ArrayList<>();
    for (Map.Entry<Station, Integer> entry : remainingInDegree.entrySet()) {
        if (entry.getValue() > 0) {
            cycleStations.add(entry.getKey());
        }
    }
    return cycleStations;
}

private List<String> findCycleEdges(Map<Station, Integer> remainingInDegree) {
    Graph<Station, TrackConnection> graph = repository.getNetworkGraph();
    List<String> cycleEdges = new ArrayList<>();
    Set<Station> cycleStationSet = new HashSet<>(findCycleStations(remainingInDegree));

    for (Station from : cycleStationSet) {
        for (Station to : cycleStationSet) {
            Edge<Station, TrackConnection> edge = graph.edge(from, to);
            if (edge != null) {
                TrackConnection connection = edge.getWeight();
                cycleEdges.add(String.format("%s -> %s (Dist: %.2f km)",
                        from.getName(), to.getName(), connection.getDistance()));
            }
        }
    }
    return cycleEdges;
}
```

### 5.4 Utility Methods

```java
public String getGraphStatistics() {
    Graph<Station, TrackConnection> graph = repository.getNetworkGraph();
    StringBuilder sb = new StringBuilder();
    sb.append("=== Railway Network Statistics ===\\n");
    sb.append(String.format("Stations: %d\\n", graph.numVertices()));
    sb.append(String.format("Track Connections: %d\\n", graph.numEdges()));

    double totalOutDegree = 0;
    for (Station station : graph.vertices()) {
        totalOutDegree += graph.outDegree(station);
    }
    double avgOutDegree = totalOutDegree / graph.numVertices();
    sb.append(String.format("Average Out-Degree: %.2f\\n", avgOutDegree));

    return sb.toString();
}

public List<String> validateGraph() {
    Graph<Station, TrackConnection> graph = repository.getNetworkGraph();
    List<String> issues = new ArrayList<>();

    for (Station station : graph.vertices()) {
        if (graph.outDegree(station) == 0 && graph.inDegree(station) == 0) {
            issues.add("Isolated station: " + station.getName());
        }
    }

    for (Station station : graph.vertices()) {
        if (graph.inDegree(station) > 10) {
            issues.add("High dependency station (in-degree " +
                    graph.inDegree(station) + "): " + station.getName());
        }
    }

    return issues;
}
```
