package pt.ipp.isep.dei.performance.export;

import pt.ipp.isep.dei.performance.PerformanceResult;
import pt.ipp.isep.dei.performance.core.PerformanceTestConfig;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Handles CSV export of performance test results in various formats.
 * Supports both detailed format and Dataset_US30 compatible format.
 */
public class CsvExporter {
    
    /**
     * Exports results in detailed format with all columns.
     * Format: Scenario,Algorithm,InputSize,StationCount,LineCount,ExecutionTime_ms,Result
     * 
     * @param results the performance results to export
     * @param fileName the output file name
     * @throws IOException if file writing fails
     */
    public void exportDetailed(List<PerformanceResult> results, String fileName) throws IOException {
        Path outputFile = PerformanceTestConfig.getOutputFilePath(fileName);
        ensureOutputDirectoryExists();
        
        try (FileWriter writer = new FileWriter(outputFile.toFile())) {
            writer.write(PerformanceTestConfig.DETAILED_CSV_HEADER + "\n");
            
            for (PerformanceResult result : results) {
                writer.write(formatDetailedRow(result) + "\n");
            }
        }
    }
    
    /**
     * Exports results in Dataset_US30 compatible format.
     * Format: size,time (input size in units, time in seconds)
     * Only includes valid results (positive execution times).
     * 
     * @param results the performance results to export
     * @param fileName the output file name
     * @throws IOException if file writing fails
     */
    public void exportDatasetFormat(List<PerformanceResult> results, String fileName) throws IOException {
        Path outputFile = PerformanceTestConfig.getOutputFilePath(fileName);
        ensureOutputDirectoryExists();
        
        try (FileWriter writer = new FileWriter(outputFile.toFile())) {
            writer.write(PerformanceTestConfig.DATASET_CSV_HEADER + "\n");
            
            List<PerformanceResult> validResults = results.stream()
                .filter(r -> r.getExecutionTimeMs() > 0)
                .sorted((r1, r2) -> Integer.compare(r1.getInputSize(), r2.getInputSize()))
                .collect(Collectors.toList());
            
            for (PerformanceResult result : validResults) {
                writer.write(formatDatasetRow(result) + "\n");
            }
        }
    }
    
    /**
     * Exports combined results by summing execution times of US13 and US14 for each scenario.
     * 
     * @param results the performance results to combine and export
     * @param fileName the output file name
     * @throws IOException if file writing fails
     */
    public void exportCombined(List<PerformanceResult> results, String fileName) throws IOException {
        List<PerformanceResult> combinedResults = createCombinedResults(results);
        exportDatasetFormat(combinedResults, fileName);
    }
    
    /**
     * Exports results separated by algorithm to individual files.
     * 
     * @param results all performance results
     * @throws IOException if file writing fails
     */
    public void exportByAlgorithm(List<PerformanceResult> results) throws IOException {
        Map<String, List<PerformanceResult>> resultsByAlgorithm = results.stream()
            .collect(Collectors.groupingBy(PerformanceResult::getAlgorithm));
        
        for (Map.Entry<String, List<PerformanceResult>> entry : resultsByAlgorithm.entrySet()) {
            String algorithm = entry.getKey();
            List<PerformanceResult> algorithmResults = entry.getValue();
            
            // Export detailed format
            String detailedFileName = algorithm + "_performance_data.csv";
            exportDetailed(algorithmResults, detailedFileName);
            
            // Export dataset format
            String datasetFileName = "Dataset_" + algorithm + "_US30_format.csv";
            exportDatasetFormat(algorithmResults, datasetFileName);
        }
    }
    
    /**
     * Exports asymptotic analysis files for each algorithm.
     * 
     * @param results all performance results
     * @throws IOException if file writing fails
     */
    public void exportAsymptoticAnalysis(List<PerformanceResult> results) throws IOException {
        Path analysisDir = PerformanceTestConfig.getAsymptoticAnalysisPath();
        Files.createDirectories(analysisDir);
        
        Map<String, List<PerformanceResult>> resultsByAlgorithm = results.stream()
            .filter(r -> r.getExecutionTimeMs() > 0)
            .collect(Collectors.groupingBy(PerformanceResult::getAlgorithm));
        
        for (Map.Entry<String, List<PerformanceResult>> entry : resultsByAlgorithm.entrySet()) {
            String algorithm = entry.getKey().toLowerCase();
            List<PerformanceResult> algorithmResults = entry.getValue()
                .stream()
                .sorted((r1, r2) -> Integer.compare(r1.getInputSize(), r2.getInputSize()))
                .collect(Collectors.toList());
            
            Path analysisFile = analysisDir.resolve(algorithm + "_asymptotic_data.csv");
            try (FileWriter writer = new FileWriter(analysisFile.toFile())) {
                writer.write("size,time\n");
                for (PerformanceResult result : algorithmResults) {
                    writer.write(String.format("%d,%.10f%n",
                        result.getInputSize(),
                        PerformanceTestConfig.millisecondsToSeconds(result.getExecutionTimeMs())));
                }
            }
        }
    }
    
    /**
     * Creates combined results by summing US13 and US14 execution times for each scenario.
     * 
     * @param results all performance results
     * @return list of combined results
     */
    private List<PerformanceResult> createCombinedResults(List<PerformanceResult> results) {
        Map<String, PerformanceResult> us13Map = results.stream()
            .filter(r -> "US13".equals(r.getAlgorithm()))
            .collect(Collectors.toMap(PerformanceResult::getScenarioName, r -> r));
            
        Map<String, PerformanceResult> us14Map = results.stream()
            .filter(r -> "US14".equals(r.getAlgorithm()))
            .collect(Collectors.toMap(PerformanceResult::getScenarioName, r -> r));
        
        Set<String> allScenarios = new HashSet<>();
        allScenarios.addAll(us13Map.keySet());
        allScenarios.addAll(us14Map.keySet());
        
        List<PerformanceResult> combinedResults = new ArrayList<>();
        
        for (String scenarioName : allScenarios) {
            PerformanceResult us13Result = us13Map.get(scenarioName);
            PerformanceResult us14Result = us14Map.get(scenarioName);
            
            if (us13Result != null && us14Result != null) {
                // Both results available - sum execution times
                double combinedTime = us13Result.getExecutionTimeMs() + us14Result.getExecutionTimeMs();
                boolean combinedSuccess = us13Result.isSuccess() && us14Result.isSuccess();
                
                PerformanceResult combinedResult = new PerformanceResult(
                    scenarioName,
                    us13Result.getStationCount(),
                    us13Result.getLineCount(),
                    combinedTime,
                    combinedSuccess,
                    "US13+US14"
                );
                combinedResults.add(combinedResult);
                
            } else if (us13Result != null) {
                // Only US13 available
                PerformanceResult combinedResult = new PerformanceResult(
                    scenarioName,
                    us13Result.getStationCount(),
                    us13Result.getLineCount(),
                    us13Result.getExecutionTimeMs(),
                    us13Result.isSuccess(),
                    "US13_only"
                );
                combinedResults.add(combinedResult);
                
            } else if (us14Result != null) {
                // Only US14 available
                PerformanceResult combinedResult = new PerformanceResult(
                    scenarioName,
                    us14Result.getStationCount(),
                    us14Result.getLineCount(),
                    us14Result.getExecutionTimeMs(),
                    us14Result.isSuccess(),
                    "US14_only"
                );
                combinedResults.add(combinedResult);
            }
        }
        
        // Sort by input size for better analysis
        combinedResults.sort((r1, r2) -> Integer.compare(r1.getInputSize(), r2.getInputSize()));
        return combinedResults;
    }
    
    /**
     * Formats a result as a detailed CSV row.
     * 
     * @param result the performance result
     * @return formatted CSV row
     */
    private String formatDetailedRow(PerformanceResult result) {
        return String.format("%s,%s,%d,%d,%d,%.6f,%s",
            result.getScenarioName(),
            result.getAlgorithm(),
            result.getInputSize(),
            result.getStationCount(),
            result.getLineCount(),
            result.getExecutionTimeMs(),
            result.isSuccess()
        );
    }
    
    /**
     * Formats a result as a dataset CSV row (size,time in seconds).
     * 
     * @param result the performance result
     * @return formatted CSV row
     */
    private String formatDatasetRow(PerformanceResult result) {
        return String.format("%d,%.10f",
            result.getInputSize(),
            PerformanceTestConfig.millisecondsToSeconds(result.getExecutionTimeMs())
        );
    }
    
    /**
     * Ensures the output directory exists.
     * 
     * @throws IOException if directory creation fails
     */
    private void ensureOutputDirectoryExists() throws IOException {
        Files.createDirectories(PerformanceTestConfig.getOutputPath());
    }
}