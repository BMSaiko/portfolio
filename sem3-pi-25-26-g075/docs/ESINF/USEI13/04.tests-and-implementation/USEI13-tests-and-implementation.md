````markdown
# USEI13 - Hub Centrality Analysis

## 4. Tests

**Test 1:** Check that degree centrality is computed correctly

```java
@Test
void testDegreeCentrality() {
    HubCentralityAnalysisService service = new HubCentralityAnalysisService();
    Map<Station, Integer> degreeMap = service.centrality();
    
    assertNotNull(degreeMap);
    assertFalse(degreeMap.isEmpty());
    
    // Verify all stations have non-negative degree
    for (Integer degree : degreeMap.values()) {
        assertTrue(degree >= 0);
    }
}
```

**Test 2:** Check that betweenness centrality is non-negative

```java
@Test
void testBetweennessCentrality() {
    HubCentralityAnalysisService service = new HubCentralityAnalysisService();
    Map<Station, Double> betweenness = service.betweennessCentrality();
    
    assertNotNull(betweenness);
    assertFalse(betweenness.isEmpty());
    
    // All betweenness values should be non-negative
    for (Double value : betweenness.values()) {
        assertTrue(value >= 0.0);
    }
}
```

**Test 3:** Check that harmonic closeness is computed correctly

```java
@Test
void testClosenessCentrality() {
    HubCentralityAnalysisService service = new HubCentralityAnalysisService();
    Map<Station, Double> closeness = service.closenessCentrality();
    
    assertNotNull(closeness);
    assertFalse(closeness.isEmpty());
    
    // All closeness values should be non-negative
    for (Double value : closeness.values()) {
        assertTrue(value >= 0.0);
    }
}
```

**Test 4:** Check that hub ranking returns results for all stations

```java
@Test
void testHubRankingCompleteness() {
    HubCentralityAnalysisService service = new HubCentralityAnalysisService();
    List<HubScoreResult> ranking = service.computeHubRanking();
    
    assertNotNull(ranking);
    assertFalse(ranking.isEmpty());
    
    Graph<Station, TrackConnection> graph = 
        RailwayNetworkRepository.getInstance().getNetworkGraph();
    assertEquals(graph.numVertices(), ranking.size());
}
```

**Test 5:** Check that hub scores are in valid range

```java
@Test
void testHubScoreRange() {
    HubCentralityAnalysisService service = new HubCentralityAnalysisService();
    List<HubScoreResult> ranking = service.computeHubRanking();
    
    for (HubScoreResult result : ranking) {
        assertTrue(result.getHubScore() >= 0.0);
        assertTrue(result.getHubScore() <= 1.0);
    }
}
```

**Test 6:** Check that results are properly sorted

```java
@Test
void testHubRankingSorting() {
    HubCentralityAnalysisService service = new HubCentralityAnalysisService();
    List<HubScoreResult> ranking = service.computeHubRanking();
    
    for (int i = 0; i < ranking.size() - 1; i++) {
        assertTrue(ranking.get(i).getHubScore() >= 
                   ranking.get(i + 1).getHubScore());
    }
}
```

**Test 7:** Check betweenness for isolated station

```java
@Test
void testBetweennessIsolatedStation() {
    Graph<Station, TrackConnection> graph = new MapGraph<>(false);
    Station isolated = new Station(1, "Isolated");
    graph.addVertex(isolated);
    
    HubCentralityAnalysisService service = 
        new HubCentralityAnalysisService(graph);
    Map<Station, Double> betweenness = service.betweennessCentrality();
    
    assertEquals(0.0, betweenness.get(isolated));
}
```

**Test 8:** Check normalization with uniform values

```java
@Test
void testNormalizationUniformValues() {
    // Create graph where all stations have same metrics
    Graph<Station, TrackConnection> graph = createUniformGraph();
    
    HubCentralityAnalysisService service = 
        new HubCentralityAnalysisService(graph);
    List<HubScoreResult> ranking = service.computeHubRanking();
    
    // All hub scores should be 0.0 when all metrics are equal
    for (HubScoreResult result : ranking) {
        assertEquals(0.0, result.getHubScore(), 0.001);
    }
}
```

## 5. Construction (Implementation)

### 5.1 HubCentralityAnalysisService Class

```java
public class HubCentralityAnalysisService {
    private final Graph<Station, TrackConnection> graph;
    private final RailwayNetworkRepository railwayNetworkRepository;

    public HubCentralityAnalysisService() {
        railwayNetworkRepository = RailwayNetworkRepository.getInstance();
        this.graph = railwayNetworkRepository.getNetworkGraph();
    }

    public HubCentralityAnalysisService(Graph<Station, TrackConnection> graph) {
        this.graph = graph;
        this.railwayNetworkRepository = RailwayNetworkRepository.getInstance();
    }
}
```

### 5.2 Betweenness Centrality Implementation

```java
public Map<Station, Double> betweennessCentrality() {
    Map<Station, Double> betweenness = new HashMap<>();

    for (Station v : graph.vertices()) {
        betweenness.putIfAbsent(v, 0.0);
    }

    // For each source station
    for (Station source : graph.vertices()) {
        ArrayList<LinkedList<Station>> paths = new ArrayList<>();
        ArrayList<TrackConnection> distances = new ArrayList<>();
        
        Algorithms.shortestPaths(
                graph,
                source,
                Comparator.comparingDouble(TrackConnection::getDistance),
                (a, b) -> new TrackConnection(a.getDistance() + b.getDistance(), 0, 0),
                new TrackConnection(0.0, 0, 0),
                paths,
                distances
        );

        for (int destinationIndex = 0; destinationIndex < graph.numVertices(); destinationIndex++) {
            Station dest = graph.vertex(destinationIndex);
            
            if (source.equals(dest) || distances.get(destinationIndex) == null) {
                continue;
            }

            LinkedList<Station> path = paths.get(destinationIndex);
            
            for (int i = 1; i < path.size() - 1; i++) {
                Station intermediate = path.get(i);
                betweenness.put(intermediate, betweenness.get(intermediate) + 1.0);
            }
        }
    }

    return betweenness;
}
```

### 5.3 Harmonic Closeness Centrality Implementation

```java
public Map<Station, Double> closenessCentrality() {
    Map<Station, Double> closeness = new HashMap<>();

    // For each station, compute sum of inverse distances
    for (Station source : graph.vertices()) {
        double harmonicSum = 0.0;

        ArrayList<LinkedList<Station>> paths = new ArrayList<>();
        ArrayList<TrackConnection> distances = new ArrayList<>();

        Algorithms.shortestPaths(
                graph,
                source,
                Comparator.comparingDouble(TrackConnection::getDistance),
                (a, b) -> new TrackConnection(a.getDistance() + b.getDistance(), 0, 0),
                new TrackConnection(0.0, 0, 0),
                paths,
                distances
        );

        // Sum up 1/distance for all reachable stations
        for (int destinationIndex = 0; destinationIndex < graph.numVertices(); destinationIndex++) {
            TrackConnection distance = distances.get(destinationIndex);

            if (distance != null && distance.getDistance() > 0) {
                harmonicSum += 1 / distance.getDistance();
            }
        }

        closeness.put(source, harmonicSum);
    }

    return closeness;
}
```

### 5.4 Degree Centrality Implementation

```java
public Map<Station, Integer> centrality() {
    Map<Station, Integer> degreeMap = new HashMap<>();
    for (Station station : graph.vertices()) {
        int deg = graph.outDegree(station) + graph.inDegree(station);
        degreeMap.put(station, deg);
    }
    return degreeMap;
}
```

### 5.5 Normalization Implementation

```java
private Map<Station, Double> normalize(Map<Station, Double> values) {
    if (values.isEmpty()) {
        return new HashMap<>();
    }

    double min = Double.MAX_VALUE;
    double max = Double.MIN_VALUE;
    
    for (Double value : values.values()) {
        if (value < min) min = value;
        if (value > max) max = value;
    }

    Map<Station, Double> normalized = new HashMap<>();
    double range = max - min;
    
    // If all values are the same, set all to 0
    if (range == 0) {
        for (Station station : values.keySet()) {
            normalized.put(station, 0.0);
        }
    } else {
        for (Map.Entry<Station, Double> entry : values.entrySet()) {
            double normalizedValue = (entry.getValue() - min) / range;
            normalized.put(entry.getKey(), normalizedValue);
        }
    }
    
    return normalized;
}

private Map<Station, Double> normalizeInt(Map<Station, Integer> values) {
    Map<Station, Double> doubleValues = new HashMap<>();
    for (Map.Entry<Station, Integer> entry : values.entrySet()) {
        doubleValues.put(entry.getKey(), entry.getValue().doubleValue());
    }
    return normalize(doubleValues);
}
```

### 5.6 Hub Ranking Computation

```java
public List<HubScoreResult> computeHubRanking() {
    System.out.println("Computing degree/strength...");
    Map<Station, Integer> degreeRaw = centrality();

    System.out.println("Computing betweenness...");
    Map<Station, Double> betweennessRaw = betweennessCentrality();

    System.out.println("Computing harmonic closeness...");
    Map<Station, Double> closenessRaw = closenessCentrality();

    System.out.println("Normalizing metrics to [0,1]...");
    Map<Station, Double> degreeNorm = normalizeInt(degreeRaw);
    Map<Station, Double> betweennessNorm = normalize(betweennessRaw);
    Map<Station, Double> closenessNorm = normalize(closenessRaw);

    System.out.println("Computing hub scores...");

    Map<Station, Double> hubScore = new HashMap<>();
    for (Station v : graph.vertices()) {
        double score = (0.35 * betweennessNorm.get(v)) + 
                      (0.35 * closenessNorm.get(v)) + 
                      (0.30 * degreeNorm.get(v));
        hubScore.put(v, score);
    }

    List<HubScoreResult> results = new ArrayList<>();
    for (Station v : graph.vertices()) {
        HubScoreResult info = new HubScoreResult(
                v.getId(),
                v.getName(),
                degreeRaw.get(v),
                betweennessRaw.get(v),
                closenessRaw.get(v),
                hubScore.get(v)
        );
        results.add(info);
    }

    Collections.sort(results);
    return results;
}
```

## 6. Integration

The HubCentralityAnalysisService integrates with:
- **RailwayNetworkRepository**: To access the railway network graph
- **Algorithms.shortestPaths**: To compute shortest paths for betweenness and closeness
- **HubScoreResult**: To package and return analysis results
- **Graph interface**: To access network topology and structure
````
