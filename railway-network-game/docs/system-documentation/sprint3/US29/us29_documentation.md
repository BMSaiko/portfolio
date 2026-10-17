# US29 - Performance Analysis of Railway Algorithms

## Overview

**User Story ID:** US29  
**Title:** Asymptotic Behavior Analysis of US13 and US14 Algorithms  
**Sprint:** 3  
**Category:** Cargo and Algorithm Statistical Analysis

## Description

As a Product Owner, I want to run tests for variable-sized inputs to observe graphically the asymptotic behavior of the execution time of the US13 and US14 procedures (excluding graphic visualization).

## Acceptance Criteria

**AC01:** The graphic referring to the asymptotic behavior of the execution running time tests, as a function of the input size, should be presented in a time unit that allows to distinguish the running times of all tested examples (use any package, for example, Gnuplot).

## Technical Implementation

### Architecture Overview

The US29 implementation follows a modular architecture with specialized classes for different aspects of performance testing:

```
pt.ipp.isep.dei.performance/
├── core/
│   ├── PerformanceTestConfig.java
│   ├── PerformanceTestExecutor.java
├── algorithms/
│   ├── US13PerformanceTest.java
│   ├── US14PerformanceTest.java
├── export/
│   ├── CsvExporter.java
│   ├── ReportGenerator.java
├── PerformanceResult.java
├── TestScenario.java
└── US29PerformanceTestRunner.java
```

### Key Components

#### 1. US29PerformanceTestRunner
- **Purpose:** Main orchestrator for performance testing
- **Responsibilities:**
  - Coordinate execution of US13 and US14 tests
  - Manage test scenarios discovery
  - Generate comprehensive analysis reports
  - Export results in multiple formats

#### 2. TestScenario
- **Purpose:** Represents a test case with network data
- **Key Features:**
  - Automatic analysis of CSV files (stations and lines)
  - Input size calculation (V + E where V=vertices, E=edges)
  - Graph density computation
  - Complexity categorization

#### 3. PerformanceResult
- **Purpose:** Encapsulates performance test execution data
- **Metrics Collected:**
  - Execution time (milliseconds and nanoseconds)
  - Input size characteristics
  - Success/failure status
  - Complexity estimates
  - Efficiency ratios

#### 4. Algorithm-Specific Tests

##### US13PerformanceTest
- **Algorithm:** Railway Connectivity Analysis
- **Test Method:** Connectivity between first and last stations using DIESEL train type
- **Complexity Expected:** Depends on graph traversal implementation

##### US14PerformanceTest
- **Algorithm:** Railway Maintenance Route Planning
- **Test Method:** Find route covering all railway lines
- **Complexity Expected:** Related to Eulerian path finding

### Performance Testing Process

#### 1. Test Scenario Discovery
```java
// Automatically scans for CSV file pairs
// Pattern: {name}_stations.csv + {name}_lines.csv
Path scenariosPath = PerformanceTestConfig.getTestScenariosPath();
List<TestScenario> scenarios = scanTestScenarios();
```

#### 2. Test Execution
```java
// Multiple runs for statistical accuracy
int testRuns = PerformanceTestConfig.DEFAULT_TEST_RUNS; // 5 runs
PerformanceResult result = executor.executeTest(scenario, algorithmName, testFunction);
```

#### 3. Timing Methodology
- **Precision:** Nanosecond-level timing using `System.nanoTime()`
- **Averaging:** Multiple runs to reduce measurement variance
- **Error Handling:** Failed executions marked with negative time values

### Input Size Analysis

#### Size Calculation
```java
// Combined measure for graph algorithms
int inputSize = stationCount + lineCount; // V + E
```

#### Complexity Categories
- **SMALL:** ≤ 50 units
- **MEDIUM:** ≤ 200 units  
- **LARGE:** ≤ 500 units
- **EXTRA_LARGE:** ≤ 1000 units
- **MASSIVE:** > 1000 units

### Output Formats

#### 1. Dataset US30 Compatible Format
```csv
size,time
100,0.0045230000
150,0.0067840000
200,0.0089120000
```

#### 2. Detailed Analysis Format
```csv
Scenario,Algorithm,InputSize,StationCount,LineCount,ExecutionTime_ms,Result
scenario1,US13,100,50,50,4.523000,true
scenario1,US14,100,50,50,6.784000,true
```

#### 3. Asymptotic Analysis Files
- Separate files for each algorithm in `asymptotic_analysis/` directory
- Format optimized for plotting tools (Gnuplot, matplotlib, etc.)

### Configuration Parameters

#### File Paths
```java
public static final String TEST_SCENARIOS_PATH = "src/test/resources/test_scenarios";
public static final String OUTPUT_PATH = "target/performance_results";
```

#### Output Files
```java
public static final String US13_OUTPUT_FILE = "US13_performance_data.csv";
public static final String US14_OUTPUT_FILE = "US14_performance_data.csv";
public static final String COMBINED_DATASET_FILE = "Dataset_US29_combined_US30_format.csv";
```

#### Test Parameters
```java
public static final int DEFAULT_TEST_RUNS = 5;
public static final double NANOSECONDS_TO_MILLISECONDS = 1_000_000.0;
```

### Analysis Capabilities

#### 1. Performance Metrics
- **Execution Time:** Precise timing in milliseconds
- **Efficiency Ratio:** Input size / execution time
- **Graph Density:** Actual edges / maximum possible edges
- **Complexity Estimation:** Heuristic classification

#### 2. Statistical Analysis
- **Descriptive Statistics:** Mean, median, standard deviation
- **Outlier Detection:** Identification of anomalous execution times
- **Trend Analysis:** Performance degradation with input size

#### 3. Comparative Analysis
- **Algorithm Comparison:** US13 vs US14 performance
- **Input Size Scaling:** Performance across different problem sizes
- **Combined Analysis:** Total execution time for both algorithms

### Error Handling

#### Test Failures
```java
// Error results marked with negative execution time
PerformanceResult errorResult = new PerformanceResult(
    scenario.getName(),
    scenario.getStationCount(),
    scenario.getLineCount(),
    -1.0, // Indicates error
    false,
    algorithmName
);
```

#### Data Validation
- **File Validation:** Ensures CSV files exist and are readable
- **Format Validation:** Verifies expected CSV structure
- **Size Validation:** Confirms non-empty test scenarios

### Usage Instructions

#### 1. Setup Test Scenarios
```
src/test/resources/test_scenarios/
├── small_network_stations.csv
├── small_network_lines.csv
├── medium_network_stations.csv
├── medium_network_lines.csv
└── ...
```

#### 2. Execute Tests
```java
@Test
void testUS13Performance() {
    // Automatically discovers scenarios and runs tests
    List<PerformanceResult> results = executeAlgorithmTests(us13Test, "US13");
}

@Test
void testUS14Performance() {
    // Automatically discovers scenarios and runs tests
    List<PerformanceResult> results = executeAlgorithmTests(us14Test, "US14");
}

@Test
void generateAnalysisReports() {
    // Generates comprehensive analysis and exports
    generateAllExports();
    generateSummaryReport();
}
```

#### 3. Analyze Results
The framework automatically generates:
- Individual algorithm performance files
- Combined analysis datasets
- Asymptotic behavior data for plotting
- Human-readable summary reports

### Expected Outputs

#### 1. Performance Data Files
- `US13_performance_data.csv` - Detailed US13 results
- `US14_performance_data.csv` - Detailed US14 results
- `Dataset_US29_combined_US30_format.csv` - Combined analysis

#### 2. Asymptotic Analysis
- `asymptotic_analysis/us13_asymptotic_data.csv`
- `asymptotic_analysis/us14_asymptotic_data.csv`

#### 3. Summary Report
- `US29_performance_summary.txt` - Human-readable analysis with insights

### Best Practices

#### 1. Test Scenario Design
- **Size Variety:** Include scenarios of different complexity categories
- **Graph Types:** Test sparse and dense networks
- **Edge Cases:** Include disconnected graphs and trivial cases

#### 2. Measurement Accuracy
- **Multiple Runs:** Average over several executions
- **JVM Warmup:** Consider JIT compilation effects
- **System Load:** Run tests under consistent system conditions

#### 3. Result Interpretation
- **Time Units:** Use appropriate precision (milliseconds vs. nanoseconds)
- **Statistical Significance:** Consider variance in measurements
- **Complexity Analysis:** Compare with theoretical expectations

### Integration with US30

The US29 framework is designed to integrate seamlessly with US30 (curve fitting analysis):

- **Compatible Data Format:** Outputs match US30 input requirements
- **Size-Time Pairs:** Clean datasets for regression analysis
- **Multiple Algorithms:** Separate analysis for different complexity classes

### Troubleshooting

#### Common Issues
1. **No Test Scenarios Found:**
   - Verify CSV file naming convention
   - Check file permissions and accessibility

2. **Negative Execution Times:**
   - Algorithm implementation errors
   - Invalid input data format

3. **Inconsistent Results:**
   - System load variations
   - Insufficient test runs for averaging

#### Performance Considerations
- **Memory Usage:** Large graphs may require increased heap size
- **Execution Time:** Complex scenarios may take significant time
- **File I/O:** CSV parsing overhead for large datasets

### Future Enhancements

#### Potential Improvements
1. **Parallel Execution:** Run multiple scenarios concurrently
2. **Real-time Monitoring:** Live progress tracking
3. **Advanced Metrics:** Memory usage, cache performance
4. **Automated Plotting:** Direct graph generation
5. **Regression Testing:** Detect performance degradations

## Dependencies

### Required Libraries
- **JUnit 5:** Test framework
- **Java NIO:** File operations
- **Standard Java Collections:** Data structures

### External Tools (for visualization)
- **Gnuplot:** Recommended for plotting asymptotic behavior
- **Python/matplotlib:** Alternative plotting solution
- **R:** Statistical analysis capabilities

## Conclusion

The US29 implementation provides a comprehensive framework for analyzing the asymptotic behavior of railway algorithms. It combines automated test discovery, precise timing measurements, statistical analysis, and flexible output formats to support both immediate analysis and integration with advanced curve-fitting tools (US30).

The modular design ensures maintainability and extensibility, while the standardized output formats facilitate comparison with theoretical complexity analysis and support decision-making for algorithm optimization.