package com.example.production.Utils;

import com.example.production.Domain.ProductionTree;
import com.example.production.Domain.Simulation;

import java.util.Map;

/**
 * Interface for different implementations of log and summary printing.
 */
public interface Printer {
    /**
     * Prints the simulation header information.
     */
    void printSimulationHeader();

    /**
     * Prints a processing event with details about the article, operation, workstation and timing.
     * 
     * @param articleId The ID of the article being processed
     * @param operation The operation being performed
     * @param workstationId The ID of the workstation
     * @param workstationOperation The operation performed by the workstation
     * @param startTime The start time of the operation
     * @param finishTime The finish time of the operation
     */
    void printProcessingEvent(String articleId, String operation, String workstationId, String workstationOperation, int startTime, int finishTime);

    /**
     * Prints the simulation summary including total production time and workstation usage.
     * 
     * @param totalProductionTime The total time taken for production
     * @param workstationUsageTracker Map containing workstation usage statistics
     */
    void printSimulationSummary(int totalProductionTime, Map<String, Integer> workstationUsageTracker);

    /**
     * Prints the total production time.
     * 
     * @param totalTime The total time to be printed
     */
    void printTotalProductionTime(int totalTime);

    /**
     * Prints the time taken for each operation.
     * 
     * @param operationTimes Map containing operation times
     * @param simulation The simulation instance
     */
    void printOperationTimes(Map<String, Integer> operationTimes, Simulation simulation);

    /**
     * Prints the usage statistics for each workstation.
     * 
     * @param workstationUsage Map containing workstation usage data
     * @param simulation The simulation instance
     */
    void printWorkstationUsage(Map<String, Integer> workstationUsage, Simulation simulation);

    /**
     * Prints the welcome message when the application starts.
     */
    void printWelcomeMessage();

    /**
     * Prints the help information with available commands.
     */
    void printHelp();

    /**
     * Prints a success message when data is loaded successfully.
     * 
     * @param numberOfArticles The number of articles loaded
     * @param numberOfWorkstations The number of workstations loaded
     */
    void printLoadDataSuccess(int numberOfArticles, int numberOfWorkstations);

    /**
     * Prints an error message when data loading fails.
     * 
     * @param errorMessage The error message to be displayed
     */
    void printLoadDataFailure(String errorMessage);

    /**
     * Prints a message when an unknown command is entered.
     */
    void printUnknownCommandMessage();

    /**
     * Prints a message when attempting to access simulation results before execution.
     */
    void printSimulationNotExecutedMessage();

    /**
     * Prints a message when the simulation is successfully executed.
     */
    void printSimulationExecutedMessage();

    /**
     * Prints a message when exiting the simulation.
     */
    void printSimulationExitedMessage();

    /**
     * Prints the production tree of a specific article.
     *
     * @param productionTree The ProductionTree to be printed
     */
    void printProductionTree(ProductionTree productionTree);

    /**
     * Clears the console screen.
     */
    void clearScreen();
}