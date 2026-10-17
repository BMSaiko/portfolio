# USEI12 - Minimal Backbone Network

## 4. Tests

**Test 1:** Check that MST is calculated correctly

```java
@Test
void testKruskalMSTCalculation() {
    MinimalBackboneService service = new MinimalBackboneService();
    Graph<Station, TrackConnection> mst = service.calculateMinimalBackbone();
    
    assertNotNull(mst);
    assertTrue(mst.numVertices() > 0);
    assertTrue(mst.numEdges() > 0);
}
```

**Test 2:** Check that MST has no cycles

```java
@Test
void testMSTNoCycles() {
    MinimalBackboneService service = new MinimalBackboneService();
    Graph<Station, TrackConnection> mst = service.kruskalMST();
    
    // For a tree: numEdges = numVertices - 1
    assertEquals(mst.numVertices() - 1, mst.numEdges(),
            "MST should have exactly V-1 edges for V vertices");
}
```

**Test 3:** Check that all vertices are included in MST

```java
@Test
void testMSTContainsAllVertices() {
    RailwayNetworkRepository repository = RailwayNetworkRepository.getInstance();
    Graph<Station, TrackConnection> originalGraph = repository.getNetworkGraph();
    
    MinimalBackboneService service = new MinimalBackboneService();
    Graph<Station, TrackConnection> mst = service.kruskalMST();
    
    assertEquals(originalGraph.numVertices(), mst.numVertices(),
            "MST should contain all vertices from original graph");
}
```

**Test 4:** Check DOT file generation

```java
@Test
void testDotFileGeneration() throws IOException {
    MinimalBackboneService service = new MinimalBackboneService();
    Graph<Station, TrackConnection> mst = service.calculateMinimalBackbone();
    
    String dotPath = service.generateDotFile(mst, "test_minimal_backbone");
    
    assertNotNull(dotPath);
    assertTrue(new File(dotPath).exists());
    
    // Verify DOT file contains expected content
    String content = Files.readString(Path.of(dotPath));
    assertTrue(content.contains("graph MinimalBackbone"));
    assertTrue(content.contains("layout=neato"));
    assertTrue(content.contains("pos="));
}
```

**Test 5:** Check that edges are sorted by distance

```java
@Test
void testEdgesSortedByDistance() {
    MinimalBackboneService service = new MinimalBackboneService();
    RailwayNetworkRepository repository = RailwayNetworkRepository.getInstance();
    Graph<Station, TrackConnection> railwayGraph = repository.getNetworkGraph();
    
    List<Edge<Station, TrackConnection>> edges = new ArrayList<>(railwayGraph.edges());
    edges.removeIf(e -> e.getVOrig().getName().compareTo(e.getVDest().getName()) > 0);
    edges.sort(Comparator.comparingDouble(e -> e.getWeight().getDistance()));
    
    for (int i = 0; i < edges.size() - 1; i++) {
        assertTrue(edges.get(i).getWeight().getDistance() <= 
                   edges.get(i + 1).getWeight().getDistance(),
                "Edges should be sorted by distance");
    }
}
```

## 5. Construction (Implementation)

### 5.1 MinimalBackboneService Class

```java
public Graph<Station, TrackConnection> kruskalMST() {
    // Get the current graph from repository (loaded data)
    Graph<Station, TrackConnection> railwayGraph = railwayNetworkRepository.getNetworkGraph();
    Graph<Station, TrackConnection> mst = new MapGraph<>(true);

    for (Station v : railwayGraph.vertices()) {
        mst.addVertex(v);
    }

    List<Edge<Station, TrackConnection>> edges = new ArrayList<>(railwayGraph.edges());

    edges.removeIf(e -> e.getVOrig().getName().compareTo(e.getVDest().getName()) > 0);
    edges.sort(Comparator.comparingDouble(e -> e.getWeight().getDistance()));

    for (Edge<Station, TrackConnection> edge : edges) {
        Station vOrig = edge.getVOrig();
        Station vDest = edge.getVDest();

        LinkedList<Station> connectedVertices = Algorithms.DepthFirstSearch(mst, vOrig);

        if (!mst.validVertex(vOrig)) {
            mst.addVertex(vOrig);
        }
        if (!mst.validVertex(vDest)) {
            mst.addVertex(vDest);
        }

        if (connectedVertices != null && !connectedVertices.contains(vDest)) {
            mst.addEdge(vOrig, vDest, edge.getWeight());
        }
    }

    return mst;
}
```

### 5.2 DOT File Generation

```java
public String generateDotFile(Graph<Station, TrackConnection> graph, String filename) throws IOException {
    if (graph == null || graph.numVertices() == 0) {
        throw new IllegalArgumentException("Graph cannot be null or empty");
    }

    String dotFilePath = filename + ".dot";
    try (FileWriter writer = new FileWriter(dotFilePath)) {
        writer.write("graph MinimalBackbone {\\n");
        writer.write("    // Graph configuration for neato layout\\n");
        writer.write("    layout=neato;\\n");
        writer.write("    overlap=false;\\n");
        writer.write("    splines=true;\\n");
        writer.write("    node [shape=circle, style=filled, fillcolor=lightblue, fontsize=10];\\n");
        writer.write("    edge [color=navy, penwidth=1.5];\\n\\n");

        // Collect all edges first to determine which stations are connected
        List<Edge<Station, TrackConnection>> processedEdges = new ArrayList<>();
        List<Station> connectedStations = new ArrayList<>();
        
        for (Edge<Station, TrackConnection> edge : graph.edges()) {
            Station vOrig = edge.getVOrig();
            Station vDest = edge.getVDest();
            
            boolean alreadyProcessed = processedEdges
                .stream()
                .anyMatch(e -> (e.getVOrig().equals(vOrig) && e.getVDest().equals(vDest)) || 
                               (e.getVOrig().equals(vDest) && e.getVDest().equals(vOrig)));
            
            if (!alreadyProcessed) {
                processedEdges.add(edge);
                if (!connectedStations.contains(vOrig)) {
                    connectedStations.add(vOrig);
                }
                if (!connectedStations.contains(vDest)) {
                    connectedStations.add(vDest);
                }
            }
        }

        // Add only vertices that have edges
        writer.write("    // Vertices with coordinates\\n");
        for (Station station : connectedStations) {
            double x = station.getCoordX();
            double y = station.getCoordY();
            
            // Scale coordinates for better visualization
            // The '!' forces the position
            writer.write(String.format("    \\"%s\\" [pos=\\"%f,%f!\\", label=\\"%s\\"];\\n",
                    station.getName(),
                    x * 0.1, // Scale factor for X
                    y * 0.1, // Scale factor for Y
                    station.getName()));
        }

        writer.write("\\n    // Edges\\n");
        
        // Add edges (duplicates already filtered)
        for (Edge<Station, TrackConnection> edge : processedEdges) {
            Station vOrig = edge.getVOrig();
            Station vDest = edge.getVDest();
            TrackConnection connection = edge.getWeight();
            
            writer.write(String.format("    \\"%s\\" -- \\"%s\\" [label=\\"%.2f\\", fontsize=8];\\n",
                    vOrig.getName(),
                    vDest.getName(),
                    connection.getCost()));
        }

        writer.write("}\\n");
    }

    return dotFilePath;
}
```

### 5.3 SVG Generation

```java
public boolean generateSvgFromDot(String dotFilePath, String outputSvgPath) {
    try {
        ProcessBuilder processBuilder = new ProcessBuilder(
            "neato",
            "-Tsvg",
            dotFilePath,
            "-o",
            outputSvgPath
        );
        
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        
        return exitCode == 0;
    } catch (IOException | InterruptedException e) {
        System.err.println("Error generating SVG: " + e.getMessage());
        System.err.println("Make sure Graphviz is installed and 'neato' is in your PATH.");
        return false;
    }
}
```

### 5.4 Complete Workflow

```java
public String generateMinimalBackboneVisualization(String baseFilename) {
    try {
        // Calculate the minimal backbone
        Graph<Station, TrackConnection> mst = calculateMinimalBackbone();
        
        // Generate DOT file
        String dotPath = generateDotFile(mst, baseFilename);
        System.out.println("✓ DOT file generated: " + dotPath);
        
        // Generate SVG
        String svgPath = baseFilename + ".svg";
        boolean svgSuccess = generateSvgFromDot(dotPath, svgPath);
        
        if (svgSuccess) {
            System.out.println("✓ SVG file generated: " + svgPath);
            return svgPath;
        } else {
            System.out.println("⚠ SVG generation failed. DOT file is still available at: " + dotPath);
            return null;
        }
    } catch (IOException e) {
        System.err.println("Error during visualization generation: " + e.getMessage());
        return null;
    }
}
```
