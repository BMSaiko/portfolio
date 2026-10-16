package pt.ipp.isep.dei.performance;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents the result of a performance test execution.
 * 
 * This class encapsulates all the data collected during a single
 * performance test run, including timing information, input characteristics,
 * and algorithm results for asymptotic behavior analysis.
 */
public class PerformanceResult {
    
    private final String scenarioName;
    private final int stationCount;
    private final int lineCount;
    private final int inputSize;
    private final double executionTimeMs;
    private final boolean success;
    private final String algorithm;
    private final LocalDateTime timestamp;
    private final String complexityCategory;
    
    // Additional metrics for analysis
    private final double executionTimeNs;
    private final double inputSizeLog;
    private final double inputSizeSquared;
    
    /**
     * Creates a new performance result with detailed metrics.
     * 
     * @param scenarioName name of the test scenario
     * @param stationCount number of stations (vertices) in the graph
     * @param lineCount number of lines (edges) in the graph
     * @param executionTimeMs execution time in milliseconds
     * @param success whether the algorithm execution was successful
     * @param algorithm name of the algorithm tested (US13 or US14)
     */
    public PerformanceResult(String scenarioName, int stationCount, int lineCount, 
                           double executionTimeMs, boolean success, String algorithm) {
        this.scenarioName = scenarioName;
        this.stationCount = stationCount;
        this.lineCount = lineCount;
        this.inputSize = stationCount + lineCount; // V + E for graph analysis
        this.executionTimeMs = executionTimeMs;
        this.executionTimeNs = executionTimeMs * 1_000_000.0; // Convert to nanoseconds
        this.success = success;
        this.algorithm = algorithm;
        this.timestamp = LocalDateTime.now();
        
        // Pre-calculate complexity analysis metrics
        this.inputSizeLog = Math.log(Math.max(1, inputSize)) / Math.log(2); // log₂(n)
        this.inputSizeSquared = Math.pow(inputSize, 2); // n²
        this.complexityCategory = determineComplexityCategory();
    }
    
    /**
     * Alternative constructor with nanosecond precision.
     * 
     * @param scenarioName name of the test scenario
     * @param stationCount number of stations in the graph
     * @param lineCount number of lines in the graph
     * @param executionTimeNs execution time in nanoseconds
     * @param success whether the algorithm execution was successful
     * @param algorithm name of the algorithm tested
     */
    public PerformanceResult(String scenarioName, int stationCount, int lineCount, 
                           long executionTimeNs, boolean success, String algorithm) {
        this(scenarioName, stationCount, lineCount, 
             executionTimeNs / 1_000_000.0, success, algorithm);
    }
    
    /**
     * Determines the complexity category based on input size.
     * 
     * @return complexity category string
     */
    private String determineComplexityCategory() {
        if (inputSize <= 50) {
            return "SMALL";
        } else if (inputSize <= 200) {
            return "MEDIUM";
        } else if (inputSize <= 500) {
            return "LARGE";
        } else if (inputSize <= 1000) {
            return "EXTRA_LARGE";
        } else {
            return "MASSIVE";
        }
    }
    
    // Getters for basic properties
    
    public String getScenarioName() {
        return scenarioName;
    }
    
    public int getStationCount() {
        return stationCount;
    }
    
    public int getLineCount() {
        return lineCount;
    }
    
    public int getInputSize() {
        return inputSize;
    }
    
    public double getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    public double getExecutionTimeNs() {
        return executionTimeNs;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public String getAlgorithm() {
        return algorithm;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getComplexityCategory() {
        return complexityCategory;
    }
    
    // Computed properties for complexity analysis
    
    /**
     * Gets the logarithmic input size (log₂(n)) for complexity analysis.
     * Useful for analyzing O(log n) algorithms.
     * 
     * @return log base 2 of input size
     */
    public double getInputSizeLog() {
        return inputSizeLog;
    }
    
    /**
     * Gets the squared input size (n²) for complexity analysis.
     * Useful for analyzing O(n²) algorithms.
     * 
     * @return input size squared
     */
    public double getInputSizeSquared() {
        return inputSizeSquared;
    }
    
    /**
     * Gets the graph density (actual edges / maximum possible edges).
     * For undirected graph: max edges = n*(n-1)/2
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
     * Calculates execution time per input unit for efficiency comparison.
     * 
     * @return milliseconds per input unit
     */
    public double getExecutionTimePerInputUnit() {
        if (inputSize == 0) {
            return 0.0;
        }
        return executionTimeMs / inputSize;
    }
    
    /**
     * Gets the efficiency ratio (input size / execution time).
     * Higher values indicate better performance.
     * 
     * @return efficiency ratio (size/ms)
     */
    public double getEfficiencyRatio() {
        if (executionTimeMs <= 0) {
            return 0.0;
        }
        return inputSize / executionTimeMs;
    }
    
    // Analysis methods
    
    /**
     * Checks if this result represents a successful execution with valid timing.
     * 
     * @return true if execution was successful and timing is valid
     */
    public boolean isValidResult() {
        return success && executionTimeMs > 0;
    }
    
    /**
     * Checks if this is an error result (negative execution time indicates error).
     * 
     * @return true if this represents an error case
     */
    public boolean isError() {
        return executionTimeMs < 0;
    }
    
    /**
     * Estimates the asymptotic complexity category based on execution behavior.
     * This is a heuristic estimation for preliminary analysis.
     * 
     * @return estimated complexity class
     */
    public String getEstimatedComplexityClass() {
        if (!isValidResult()) {
            return "UNKNOWN";
        }
        
        double timePerUnit = getExecutionTimePerInputUnit();
        
        if (timePerUnit < 0.001) {
            return "O(1) or O(log n)";
        } else if (timePerUnit < 0.01) {
            return "O(n) or O(n log n)";
        } else if (timePerUnit < 0.1) {
            return "O(n²) or similar";
        } else {
            return "O(n³) or higher";
        }
    }
    
    // Formatting methods
    
    /**
     * Formats the result as a CSV row compatible with Dataset_US30.xlsx structure.
     * 
     * @return CSV formatted string
     */
    public String toCsvRow() {
        return String.format("%s,%s,%d,%d,%d,%.6f,%s",
            scenarioName,
            algorithm,
            inputSize,
            stationCount,
            lineCount,
            executionTimeMs,
            success
        );
    }
    
    /**
     * Formats the result as an extended CSV row with additional metrics.
     * 
     * @return extended CSV formatted string
     */
    public String toExtendedCsvRow() {
        return String.format("%s,%s,%d,%d,%d,%.6f,%.3f,%.3f,%.6f,%.6f,%s,%s,%s",
            scenarioName,
            algorithm,
            inputSize,
            stationCount,
            lineCount,
            executionTimeMs,
            inputSizeLog,
            inputSizeSquared,
            getGraphDensity(),
            getEfficiencyRatio(),
            complexityCategory,
            getEstimatedComplexityClass(),
            success
        );
    }
    
    /**
     * Returns a human-readable summary of the performance result.
     * 
     * @return formatted summary string
     */
    public String toSummaryString() {
        if (isError()) {
            return String.format("%s [%s]: ERROR - Failed to execute", 
                               scenarioName, algorithm);
        }
        
        return String.format("%s [%s]: %.3f ms (Size: %d, Density: %.3f, %s)", 
                           scenarioName, algorithm, executionTimeMs, 
                           inputSize, getGraphDensity(), complexityCategory);
    }
    
    /**
     * Returns detailed information about the performance result.
     * 
     * @return detailed string representation
     */
    @Override
    public String toString() {
        return String.format("PerformanceResult{scenario='%s', algorithm='%s', " +
                           "inputSize=%d, stations=%d, lines=%d, executionTime=%.3fms, " +
                           "success=%s, category='%s', timestamp='%s'}",
                           scenarioName, algorithm, inputSize, stationCount, lineCount,
                           executionTimeMs, success, complexityCategory,
                           timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
    
    // Comparison methods for sorting and analysis
    
    /**
     * Compares results by input size for sorting.
     * 
     * @param other another performance result
     * @return comparison result
     */
    public int compareByInputSize(PerformanceResult other) {
        return Integer.compare(this.inputSize, other.inputSize);
    }
    
    /**
     * Compares results by execution time for sorting.
     * 
     * @param other another performance result
     * @return comparison result
     */
    public int compareByExecutionTime(PerformanceResult other) {
        return Double.compare(this.executionTimeMs, other.executionTimeMs);
    }
    
    /**
     * Compares results by efficiency ratio for sorting.
     * 
     * @param other another performance result
     * @return comparison result
     */
    public int compareByEfficiency(PerformanceResult other) {
        return Double.compare(other.getEfficiencyRatio(), this.getEfficiencyRatio());
    }
}