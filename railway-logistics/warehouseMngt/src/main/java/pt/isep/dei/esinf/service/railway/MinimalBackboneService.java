package pt.isep.dei.esinf.service.railway;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import pt.isep.dei.esinf.domain.railway.TrackConnection;
import pt.isep.dei.esinf.domain.stations.Station;
import pt.isep.dei.esinf.repository.RailwayNetworkRepository;
import pt.isep.dei.esinf.utils.graph.Algorithms;
import pt.isep.dei.esinf.utils.graph.Edge;
import pt.isep.dei.esinf.utils.graph.map.MapGraph;
import pt.isep.dei.esinf.utils.interfaces.Graph;

public class MinimalBackboneService {

    private final RailwayNetworkRepository railwayNetworkRepository;

    public MinimalBackboneService() { this.railwayNetworkRepository = RailwayNetworkRepository.getInstance(); }

    /**
     * Calculate the minimal backbone of the railway network using Kruskal's algorithm
     *
     * @return Graph representing the minimal backbone
     */
    public Graph<Station, TrackConnection> calculateMinimalBackbone() {
        if (!railwayNetworkRepository.isDataLoaded()) {
            throw new IllegalStateException("Railway network data is not loaded.");
        }

        return kruskalMST();
    }

    /**
     * Kruskal's algorithm to find the Minimum Spanning Tree (MST)
     * @return Graph representing the MST
     */
    private Graph<Station, TrackConnection> kruskalMST() {
        Graph<Station, TrackConnection> railwayGraph = railwayNetworkRepository.getNetworkGraph();
        Graph<Station, TrackConnection> tempMst = new MapGraph<>(false);

        for (Station v : railwayGraph.vertices()) {
            tempMst.addVertex(v);
        }

        List<Edge<Station, TrackConnection>> edges = new ArrayList<>(railwayGraph.edges());
        edges.sort(Comparator.comparingDouble(e -> e.getWeight().getCost()));

        for (Edge<Station, TrackConnection> edge : edges) {
            Station vOrig = edge.getVOrig();
            Station vDest = edge.getVDest();

            LinkedList<Station> connectedVertices = Algorithms.DepthFirstSearch(tempMst, vOrig);

            if (connectedVertices != null && !connectedVertices.contains(vDest)) {
                tempMst.addEdge(vOrig, vDest, edge.getWeight());
            }
        }

        Graph<Station, TrackConnection> finalMst = new MapGraph<>(true);

        for (Station v : tempMst.vertices()) {
            finalMst.addVertex(v);
        }

        for (Edge<Station, TrackConnection> edge : tempMst.edges()) {
            if (edge.getVOrig().getName().compareTo(edge.getVDest().getName()) < 0) {
                finalMst.addEdge(edge.getVOrig(), edge.getVDest(), edge.getWeight());
            }
        }

        return finalMst;
    }

    /**
     * Generates a DOT file representation of the given graph with XY coordinates
     *
     * @param graph The graph to export
     * @param filename The output filename (without extension)
     * @return The path to the generated DOT file
     * @throws IOException if file writing fails
     */
    public String generateDotFile(Graph<Station, TrackConnection> graph, String filename) throws IOException {
        if (graph == null || graph.numVertices() == 0) {
            throw new IllegalArgumentException("Graph cannot be null or empty");
        }

        String dotFilePath = filename + ".dot";
        try (FileWriter writer = new FileWriter(dotFilePath)) {
            writer.write("graph MinimalBackbone {\n");
            writer.write("    // Graph configuration for neato layout\n");
            writer.write("    layout=neato;\n");
            writer.write("    overlap=false;\n");
            writer.write("    splines=true;\n");
            writer.write("    node [shape=circle, style=filled, fillcolor=lightblue, fontsize=10];\n");
            writer.write("    edge [color=navy, penwidth=1.5];\n\n");

            List<Edge<Station, TrackConnection>> processedEdges = new ArrayList<>();
            List<Station> connectedStations = new ArrayList<>();
            
            for (Edge<Station, TrackConnection> edge : graph.edges()) {
                Station vOrig = edge.getVOrig();
                Station vDest = edge.getVDest();
                
                boolean alreadyProcessed = processedEdges
                    .stream()
                    .anyMatch(e -> (e.getVOrig().equals(vOrig) && e.getVDest().equals(vDest)) || (e.getVOrig().equals(vDest) && e.getVDest().equals(vOrig)));
                
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

            writer.write("    // Vertices with coordinates\n");
            for (Station station : connectedStations) {
                double x = station.getCoordX();
                double y = station.getCoordY();
                
                writer.write(
                    String.format(
                        "    \"%s\" [pos=\"%f,%f!\", label=\"%s\"];\n",
                        station.getName(),
                        x * 0.1,
                        y * 0.1, 
                        station.getName()
                    )
                );
            }

            writer.write("\n    // Edges\n");
            
            for (Edge<Station, TrackConnection> edge : processedEdges) {
                Station vOrig = edge.getVOrig();
                Station vDest = edge.getVDest();
                TrackConnection connection = edge.getWeight();
                
                writer.write(
                    String.format(
                    "    \"%s\" -- \"%s\" [label=\"%.2f\", fontsize=8];\n",
                        vOrig.getName(),
                        vDest.getName(),
                        connection.getCost()
                    )
                );
            }

            writer.write("}\n");
        }

        return dotFilePath;
    }

    /**
     * Generates an SVG file from a DOT file using Graphviz neato
     *
     * @param dotFilePath Path to the DOT file
     * @param outputSvgPath Path for the output SVG file
     * @return true if successful, false otherwise
     */
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
            return false;
        }
    }

    /**
     * Complete workflow: calculate MST, generate DOT file, and create SVG visualization
     *
     * @param baseFilename Base filename for outputs (without extension)
     * @return Path to the generated SVG file, or null if failed
     */
    public String generateMinimalBackboneVisualization(String baseFilename, Graph<Station, TrackConnection> mst) {
        try {

            String dotPath = generateDotFile(mst, baseFilename);
            System.out.println("✓ DOT file generated: " + dotPath);
            
            String svgPath = baseFilename + ".svg";

            boolean svgGenerated = generateSvgFromDot(dotPath, svgPath);

            if (svgGenerated) {
                System.out.println("✓ SVG file generated: " + svgPath);
                return svgPath;
            } else {
                System.out.println("⚠ SVG generation failed. Dot file is available at the project root directory.");
                return null;
            }
        } catch (IOException e) {
            System.err.println("Error during visualization generation: " + e.getMessage());
            return null;
        }
    }
}
