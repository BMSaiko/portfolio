package pt.ipp.isep.dei.performance.core;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Centralized configuration for performance tests.
 * Contains all constants and configurable parameters used across the performance testing framework.
 */
public final class PerformanceTestConfig {
    
    // Directory paths
    public static final String TEST_SCENARIOS_PATH = "src/test/resources/test_scenarios";
    public static final String OUTPUT_PATH = "target/performance_results";
    
    // Output file names
    public static final String US13_OUTPUT_FILE = "US13_performance_data.csv";
    public static final String US14_OUTPUT_FILE = "US14_performance_data.csv";
    public static final String COMBINED_OUTPUT_FILE = "US29_combined_performance_data.csv";
    public static final String SUMMARY_REPORT_FILE = "US29_performance_summary.txt";
    
    // Dataset US30 format files
    public static final String US13_DATASET_FILE = "Dataset_US13_US30_format.csv";
    public static final String US14_DATASET_FILE = "Dataset_US14_US30_format.csv";
    public static final String COMBINED_DATASET_FILE = "Dataset_US29_combined_US30_format.csv";
    
    // Test execution parameters
    public static final int DEFAULT_TEST_RUNS = 5;
    public static final double NANOSECONDS_TO_MILLISECONDS = 1_000_000.0;
    public static final double MILLISECONDS_TO_SECONDS = 1000.0;
    
    // CSV file patterns
    public static final String STATIONS_FILE_SUFFIX = "_stations.csv";
    public static final String LINES_FILE_SUFFIX = "_lines.csv";
    
    // CSV headers
    public static final String DETAILED_CSV_HEADER = "Scenario,Algorithm,InputSize,StationCount,LineCount,ExecutionTime_ms,Result";
    public static final String DATASET_CSV_HEADER = "size,time";
    
    // Analysis directories
    public static final String ASYMPTOTIC_ANALYSIS_DIR = "asymptotic_analysis";
    
    private PerformanceTestConfig() {
        // Utility class - prevent instantiation
    }
    
    /**
     * Gets the path to the test scenarios directory.
     * 
     * @return Path to test scenarios
     */
    public static Path getTestScenariosPath() {
        return Paths.get(TEST_SCENARIOS_PATH);
    }
    
    /**
     * Gets the path to the output directory.
     * 
     * @return Path to output directory
     */
    public static Path getOutputPath() {
        return Paths.get(OUTPUT_PATH);
    }
    
    /**
     * Gets the path to the asymptotic analysis directory.
     * 
     * @return Path to asymptotic analysis directory
     */
    public static Path getAsymptoticAnalysisPath() {
        return getOutputPath().resolve(ASYMPTOTIC_ANALYSIS_DIR);
    }
    
    /**
     * Gets the full path for an output file.
     * 
     * @param fileName the output file name
     * @return full path to the output file
     */
    public static Path getOutputFilePath(String fileName) {
        return getOutputPath().resolve(fileName);
    }
    
    /**
     * Converts nanoseconds to milliseconds.
     * 
     * @param nanoseconds time in nanoseconds
     * @return time in milliseconds
     */
    public static double nanosecondsToMilliseconds(long nanoseconds) {
        return nanoseconds / NANOSECONDS_TO_MILLISECONDS;
    }
    
    /**
     * Converts milliseconds to seconds.
     * 
     * @param milliseconds time in milliseconds
     * @return time in seconds
     */
    public static double millisecondsToSeconds(double milliseconds) {
        return milliseconds / MILLISECONDS_TO_SECONDS;
    }
}