package pt.ipp.isep.dei.ui.gui;

import pt.ipp.isep.dei.domain.us13.RailwayLine;
import pt.ipp.isep.dei.domain.us13.Station;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for generating Graphviz DOT files to visualize railway networks.
 */
public class GraphvizUtil {
    
    /**
     * Generates a DOT file for visualizing a railway network.
     * 
     * @param stations all stations in the network
     * @param lines all railway lines in the network
     * @param highlightedStations stations to highlight (e.g., path or reachable stations)
     * @param outputFile the path to the output DOT file
     * @throws IOException if an I/O error occurs
     */
    public static void generateDotFile(Collection<Station> stations, Collection<RailwayLine> lines, 
                                      List<Station> highlightedStations, String outputFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            // Start the graph
            writer.write("digraph RailwayNetwork {\n");
            writer.write("    graph [overlap=false, splines=true, bgcolor=\"white\"];\n");
            writer.write("    node [shape=box, style=filled];\n");
            writer.write("    edge [arrowhead=none];\n\n");
            
            // Whether there's a path to highlight
            boolean hasPath = highlightedStations != null && highlightedStations.size() > 1;
            
            // Map to track edges in the path
            Map<String, Boolean> pathEdges = new HashMap<>();
            if (hasPath) {
                for (int i = 0; i < highlightedStations.size() - 1; i++) {
                    String edgeKey = getEdgeKey(highlightedStations.get(i), highlightedStations.get(i + 1));
                    pathEdges.put(edgeKey, true);
                }
            }
            
            // Output stations
            for (Station station : stations) {
                String color;
                switch (station.getType()) {
                    case DEPOT:
                        color = "lightblue";
                        break;
                    case STATION:
                        color = "lightgreen";
                        break;
                    case TERMINAL:
                        color = "lightcoral";
                        break;
                    default:
                        color = "white";
                }
                
                // If this station is highlighted, make it more prominent
                boolean isHighlighted = highlightedStations != null && highlightedStations.contains(station);
                String style = isHighlighted ? ", penwidth=3.0" : "";
                
                writer.write("    \"" + station.getName() + "\" [fillcolor=\"" + color + "\", label=\"" + 
                           station.getName() + "\\n(" + station.getType() + ")\"" + style + "];\n");
            }
            
            writer.write("\n");
            
            // Output railway lines
            for (RailwayLine line : lines) {
                String source = line.getSource().getName();
                String dest = line.getDestination().getName();
                String edgeKey = getEdgeKey(line.getSource(), line.getDestination());
                
                boolean isPathEdge = pathEdges.containsKey(edgeKey);
                
                // Line attributes based on electrification and whether it's part of a path
                String color = line.isElectrified() ? "blue" : "black";
                String style = isPathEdge ? ", penwidth=3.0, color=\"red\"" : "";
                String label = "\" (" + line.getDistance() + "km" + 
                              (line.isElectrified() ? ", E" : "") + ")\"";
                
                writer.write("    \"" + source + "\" -> \"" + dest + 
                           "\" [dir=none, color=\"" + color + "\"" + style + 
                           ", label=" + label + "];\n");
            }
            
            writer.write("}\n");
        }
    }
    
    /**
     * Gets a unique key for an edge between two stations.
     * 
     * @param a the first station
     * @param b the second station
     * @return a unique key for the edge
     */
    private static String getEdgeKey(Station a, Station b) {
        // Sort station names to ensure consistent keys regardless of direction
        if (a.getName().compareTo(b.getName()) <= 0) {
            return a.getName() + " -- " + b.getName();
        } else {
            return b.getName() + " -- " + a.getName();
        }
    }
}