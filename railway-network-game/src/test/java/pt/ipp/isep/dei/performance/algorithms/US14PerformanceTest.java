package pt.ipp.isep.dei.performance.algorithms;

import pt.ipp.isep.dei.controller.RailwayMaintenanceController;
import pt.ipp.isep.dei.domain.us13.Station;
import pt.ipp.isep.dei.performance.PerformanceResult;
import pt.ipp.isep.dei.performance.TestScenario;
import pt.ipp.isep.dei.performance.core.PerformanceTestExecutor;

import java.io.IOException;
import java.util.List;

/**
 * Performance test implementation for US14 - Railway Maintenance algorithm.
 * Tests the maintenance route planning functionality for covering all railway lines.
 */
public class US14PerformanceTest {
    
    private static final String ALGORITHM_NAME = "US14";
    private static final boolean TEST_ALL_LINES = false; // Test all lines, not just electrified
    
    private final PerformanceTestExecutor executor;
    
    /**
     * Creates a new US14 performance test with default executor.
     */
    public US14PerformanceTest() {
        this.executor = new PerformanceTestExecutor();
    }
    
    /**
     * Creates a new US14 performance test with custom executor.
     * 
     * @param executor the performance test executor to use
     */
    public US14PerformanceTest(PerformanceTestExecutor executor) {
        this.executor = executor;
    }
    
    /**
     * Executes the US14 performance test for a given scenario.
     * Tests maintenance route planning to cover all railway lines.
     * 
     * @param scenario the test scenario to execute
     * @return PerformanceResult containing execution metrics
     */
    public PerformanceResult executeTest(TestScenario scenario) {
        return executor.executeTest(scenario, ALGORITHM_NAME, () -> {
            try {
                return performMaintenanceTest(scenario);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load network for scenario: " + scenario.getName(), e);
            }
        });
    }
    
    /**
     * Performs the actual maintenance route test for the scenario.
     * 
     * @param scenario the test scenario
     * @return TestExecutionResult with the maintenance test outcome
     * @throws IOException if network loading fails
     */
    private PerformanceTestExecutor.TestExecutionResult performMaintenanceTest(TestScenario scenario) throws IOException {
        RailwayMaintenanceController controller = new RailwayMaintenanceController(
            scenario.getStationsFile(), 
            scenario.getLinesFile()
        );
        
        List<Station> route = controller.getMaintenanceRoute(TEST_ALL_LINES);
        
        // Route is successful if it's not empty
        boolean routeFound = !route.isEmpty();
        
        return PerformanceTestExecutor.TestExecutionResult.success(routeFound);
    }
    
    /**
     * Gets the algorithm name.
     * 
     * @return the algorithm name "US14"
     */
    public String getAlgorithmName() {
        return ALGORITHM_NAME;
    }
}