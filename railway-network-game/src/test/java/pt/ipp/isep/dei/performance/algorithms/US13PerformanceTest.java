package pt.ipp.isep.dei.performance.algorithms;

import pt.ipp.isep.dei.controller.RailwayConnectivityController;
import pt.ipp.isep.dei.domain.us13.Station;
import pt.ipp.isep.dei.domain.us13.TrainType;
import pt.ipp.isep.dei.performance.PerformanceResult;
import pt.ipp.isep.dei.performance.TestScenario;
import pt.ipp.isep.dei.performance.core.PerformanceTestExecutor;

import java.io.IOException;
import java.util.List;

/**
 * Performance test implementation for US13 - Railway Connectivity algorithm.
 * Tests the connectivity checking functionality between stations.
 */
public class US13PerformanceTest {
    
    private static final String ALGORITHM_NAME = "US13";
    private final PerformanceTestExecutor executor;
    
    /**
     * Creates a new US13 performance test with default executor.
     */
    public US13PerformanceTest() {
        this.executor = new PerformanceTestExecutor();
    }
    
    /**
     * Creates a new US13 performance test with custom executor.
     * 
     * @param executor the performance test executor to use
     */
    public US13PerformanceTest(PerformanceTestExecutor executor) {
        this.executor = executor;
    }
    
    /**
     * Executes the US13 performance test for a given scenario.
     * Tests connectivity between the first and last stations using DIESEL train type.
     * 
     * @param scenario the test scenario to execute
     * @return PerformanceResult containing execution metrics
     */
    public PerformanceResult executeTest(TestScenario scenario) {
        return executor.executeTest(scenario, ALGORITHM_NAME, () -> {
            try {
                return performConnectivityTest(scenario);
            } catch (IOException e) {
                throw new RuntimeException("Failed to load network for scenario: " + scenario.getName(), e);
            }
        });
    }
    
    /**
     * Performs the actual connectivity test for the scenario.
     * 
     * @param scenario the test scenario
     * @return TestExecutionResult with the connectivity test outcome
     * @throws IOException if network loading fails
     */
    private PerformanceTestExecutor.TestExecutionResult performConnectivityTest(TestScenario scenario) throws IOException {
        RailwayConnectivityController controller = new RailwayConnectivityController(
            scenario.getStationsFile(), 
            scenario.getLinesFile()
        );
        
        List<Station> stations = controller.getAllStations();
        
        // Handle edge cases
        if (stations.size() < 2) {
            // Trivial case - consider as successful
            return PerformanceTestExecutor.TestExecutionResult.success(true);
        }
        
        // Test connectivity between first and last stations
        Station source = stations.get(0);
        Station destination = stations.get(stations.size() - 1);
        
        boolean canTravel = controller.canTravel(source, destination, TrainType.DIESEL);
        
        return PerformanceTestExecutor.TestExecutionResult.success(canTravel);
    }
    
    /**
     * Gets the algorithm name.
     * 
     * @return the algorithm name "US13"
     */
    public String getAlgorithmName() {
        return ALGORITHM_NAME;
    }
}