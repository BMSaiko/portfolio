package pt.ipp.isep.dei.performance.core;

import pt.ipp.isep.dei.performance.PerformanceResult;
import pt.ipp.isep.dei.performance.TestScenario;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Generic executor for performance tests.
 * Handles the common logic of running multiple test iterations and calculating averages.
 */
public class PerformanceTestExecutor {
    
    private final int testRuns;
    
    /**
     * Creates a new performance test executor with default number of runs.
     */
    public PerformanceTestExecutor() {
        this(PerformanceTestConfig.DEFAULT_TEST_RUNS);
    }
    
    /**
     * Creates a new performance test executor with specified number of runs.
     * 
     * @param testRuns number of test runs to perform for averaging
     */
    public PerformanceTestExecutor(int testRuns) {
        this.testRuns = testRuns;
    }
    
    /**
     * Executes a performance test for a given scenario and algorithm.
     * Runs the test multiple times and returns the average execution time.
     * 
     * @param scenario the test scenario
     * @param algorithmName the name of the algorithm being tested
     * @param testFunction the function that performs the actual test
     * @return PerformanceResult with averaged execution time
     */
    public PerformanceResult executeTest(TestScenario scenario, String algorithmName, 
                                       Supplier<TestExecutionResult> testFunction) {
        
        List<Long> executionTimes = new ArrayList<>();
        boolean lastResult = false;
        Exception lastException = null;
        
        for (int run = 0; run < testRuns; run++) {
            try {
                long startTime = System.nanoTime();
                TestExecutionResult result = testFunction.get();
                long endTime = System.nanoTime();
                
                long executionTime = endTime - startTime;
                executionTimes.add(executionTime);
                lastResult = result.isSuccess();
                
            } catch (Exception e) {
                lastException = e;
                // Add a negative time to indicate error, but continue with other runs
                executionTimes.add(-1L);
            }
        }
        
        // If all runs failed, return error result
        if (executionTimes.stream().allMatch(time -> time < 0)) {
            return createErrorResult(scenario, algorithmName, lastException);
        }
        
        // Calculate average of successful runs only
        double avgExecutionTime = executionTimes.stream()
            .filter(time -> time >= 0)
            .mapToLong(Long::longValue)
            .average()
            .orElse(-1.0);
        
        double executionTimeMs = PerformanceTestConfig.nanosecondsToMilliseconds((long) avgExecutionTime);
        
        return new PerformanceResult(
            scenario.getName(),
            scenario.getStationCount(),
            scenario.getLineCount(),
            executionTimeMs,
            lastResult,
            algorithmName
        );
    }
    
    /**
     * Creates an error result when test execution fails.
     * 
     * @param scenario the test scenario
     * @param algorithmName the algorithm name
     * @param exception the exception that occurred (can be null)
     * @return PerformanceResult indicating error
     */
    private PerformanceResult createErrorResult(TestScenario scenario, String algorithmName, Exception exception) {
        return new PerformanceResult(
            scenario.getName(),
            scenario.getStationCount(),
            scenario.getLineCount(),
            -1.0, // Negative time indicates error
            false,
            algorithmName
        );
    }
    
    /**
     * Result of a single test execution.
     */
    public static class TestExecutionResult {
        private final boolean success;
        private final Object result;
        
        public TestExecutionResult(boolean success, Object result) {
            this.success = success;
            this.result = result;
        }
        
        public TestExecutionResult(boolean success) {
            this(success, null);
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public Object getResult() {
            return result;
        }
        
        /**
         * Creates a successful test result.
         * 
         * @param result the result object
         * @return TestExecutionResult indicating success
         */
        public static TestExecutionResult success(Object result) {
            return new TestExecutionResult(true, result);
        }
        
        /**
         * Creates a successful test result without result object.
         * 
         * @return TestExecutionResult indicating success
         */
        public static TestExecutionResult success() {
            return new TestExecutionResult(true);
        }
        
        /**
         * Creates a failed test result.
         * 
         * @return TestExecutionResult indicating failure
         */
        public static TestExecutionResult failure() {
            return new TestExecutionResult(false);
        }
    }
}