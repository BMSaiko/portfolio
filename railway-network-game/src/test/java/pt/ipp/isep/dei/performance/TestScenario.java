package pt.ipp.isep.dei.performance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Represents a test scenario for performance testing.
 * 
 * A test scenario consists of a pair of CSV files:
 * - Stations file: contains station definitions
 * - Lines file: contains railway line connections
 * 
 * This class automatically analyzes the CSV files to determine
 * the input size characteristics for asymptotic analysis.
 */
public class TestScenario {
    
    private final String name;
    private final String stationsFile;
    private final String linesFile;
    private final int stationCount;
    private final int lineCount;
    private final int inputSize;
    
    /**
     * Creates a new test scenario by analyzing the provided CSV files.
     * 
     * @param name the scenario name (usually derived from filename)
     * @param stationsFile path to the stations CSV file
     * @param linesFile path to the lines CSV file
     * @throws IOException if files cannot be read or analyzed
     */
    public TestScenario(String name, String stationsFile, String linesFile) throws IOException {
        this.name = name;
        this.stationsFile = stationsFile;
        this.linesFile = linesFile;
        
        // Analyze files to determine counts
        this.stationCount = countStationsInFile(stationsFile);
        this.lineCount = countLinesInFile(linesFile);
        
        // Calculate input size (combined measure for complexity analysis)
        // Using V + E where V = stations (vertices) and E = lines (edges)
        this.inputSize = stationCount + lineCount;
        
        validateScenario();
    }
    
    /**
     * Counts the number of stations in the stations CSV file.
     * 
     * @param stationsFile path to stations file
     * @return number of unique stations
     * @throws IOException if file cannot be read
     */
    private int countStationsInFile(String stationsFile) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(stationsFile))) {
            String line = reader.readLine();
            
            if (line == null || line.trim().isEmpty()) {
                return 0;
            }
            
            // Check if stations are separated by semicolons in a single line
            if (line.contains(";")) {
                // Count semicolon-separated stations
                String[] stations = line.split(";");
                int count = 0;
                for (String station : stations) {
                    if (!station.trim().isEmpty()) {
                        count++;
                    }
                }
                return count;
            } else {
                // Count line-by-line stations
                int count = 0;
                if (!line.trim().isEmpty()) {
                    count = 1;
                }
                
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        count++;
                    }
                }
                return count;
            }
        }
    }
    
    /**
     * Counts the number of railway lines in the lines CSV file.
     * Expected format: source;destination;electrified;distance
     * 
     * @param linesFile path to lines file
     * @return number of railway lines
     * @throws IOException if file cannot be read
     */
    private int countLinesInFile(String linesFile) throws IOException {
        int count = 0;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(linesFile))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Skip empty lines
                if (line.isEmpty()) {
                    continue;
                }
                
                // Check if line has the expected format (at least 4 parts)
                String[] parts = line.split(";");
                if (parts.length >= 4) {
                    // Validate that we have source, destination, electrified flag, and distance
                    String source = parts[0].trim();
                    String destination = parts[1].trim();
                    String electrified = parts[2].trim();
                    String distance = parts[3].trim();
                    
                    if (!source.isEmpty() && !destination.isEmpty() && 
                        !electrified.isEmpty() && !distance.isEmpty()) {
                        count++;
                    }
                }
            }
        }
        
        return count;
    }
    
    /**
     * Validates that the scenario has valid data.
     * 
     * @throws IllegalStateException if scenario is invalid
     */
    private void validateScenario() {
        if (stationCount <= 0) {
            throw new IllegalStateException("Scenario " + name + " has no valid stations");
        }
        
        if (lineCount < 0) {
            throw new IllegalStateException("Scenario " + name + " has invalid line count");
        }
        
        // A connected network should have at least (stations - 1) lines
        // But we allow disconnected networks for testing purposes
        if (lineCount > 0 && stationCount < 2) {
            throw new IllegalStateException("Scenario " + name + " has lines but insufficient stations");
        }
    }
    
    /**
     * Gets the scenario name.
     * 
     * @return scenario name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Gets the path to the stations CSV file.
     * 
     * @return stations file path
     */
    public String getStationsFile() {
        return stationsFile;
    }
    
    /**
     * Gets the path to the lines CSV file.
     * 
     * @return lines file path
     */
    public String getLinesFile() {
        return linesFile;
    }
    
    /**
     * Gets the number of stations in this scenario.
     * 
     * @return station count
     */
    public int getStationCount() {
        return stationCount;
    }
    
    /**
     * Gets the number of railway lines in this scenario.
     * 
     * @return line count
     */
    public int getLineCount() {
        return lineCount;
    }
    
    /**
     * Gets the combined input size (stations + lines) for complexity analysis.
     * This represents the total graph size for algorithm analysis.
     * 
     * @return input size (V + E where V = vertices/stations, E = edges/lines)
     */
    public int getInputSize() {
        return inputSize;
    }
    
    /**
     * Gets the graph density as a measure of how connected the network is.
     * Density = actual_edges / max_possible_edges
     * For undirected graph: max_edges = n*(n-1)/2
     * 
     * @return graph density between 0.0 and 1.0
     */
    public double getGraphDensity() {
        if (stationCount < 2) {
            return 0.0;
        }
        
        int maxPossibleEdges = (stationCount * (stationCount - 1)) / 2;
        return (double) lineCount / maxPossibleEdges;
    }
    
    /**
     * Determines the scenario complexity category based on input size.
     * 
     * @return complexity category string
     */
    public String getComplexityCategory() {
        if (inputSize <= 50) {
            return "SMALL";
        } else if (inputSize <= 200) {
            return "MEDIUM";
        } else if (inputSize <= 500) {
            return "LARGE";
        } else {
            return "EXTRA_LARGE";
        }
    }
    
    /**
     * Checks if this scenario represents a potentially connected graph.
     * A graph can be connected if it has at least (vertices - 1) edges.
     * 
     * @return true if the graph could potentially be connected
     */
    public boolean isPotentiallyConnected() {
        return lineCount >= (stationCount - 1);
    }
    
    /**
     * Returns a detailed string representation of the scenario.
     * 
     * @return detailed scenario information
     */
    @Override
    public String toString() {
        return String.format("TestScenario{name='%s', stations=%d, lines=%d, inputSize=%d, " +
                           "density=%.3f, category=%s, potentiallyConnected=%s}",
                           name, stationCount, lineCount, inputSize, 
                           getGraphDensity(), getComplexityCategory(), isPotentiallyConnected());
    }
    
    /**
     * Returns a compact string representation for logging.
     * 
     * @return compact scenario description
     */
    public String toCompactString() {
        return String.format("%s[S:%d,L:%d,Size:%d]", 
                           name, stationCount, lineCount, inputSize);
    }
}