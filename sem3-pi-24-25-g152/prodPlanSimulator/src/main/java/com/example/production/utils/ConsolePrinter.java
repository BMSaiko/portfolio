package com.example.production.Utils;

import com.example.production.Domain.ProductionTree;
import com.example.production.Domain.ProductionNode;
import com.example.production.Domain.Simulation;
import com.example.production.Utils.Printer;
import org.fusesource.jansi.Ansi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Implementation of the Printer interface that prints to the console.
 */
public class ConsolePrinter implements Printer {

    private boolean ansiSupported;

    public ConsolePrinter() {
        this.ansiSupported = checkAnsiSupport();
        if (ansiSupported) {
            System.out.println("ANSI codes are supported.");
        } else {
            System.out.println("ANSI codes are not supported. Color output is disabled.");
        }
    }

    private boolean checkAnsiSupport() {
        String os = System.getProperty("os.name").toLowerCase();
        return !os.contains("win");
    }

    /**
     * Clears the console screen.
     */
    @Override
    public void clearScreen() {
        if (ansiSupported) {
            System.out.print("\033[H\033[2J");
            System.out.flush();
        }
    }

    @Override
    public void printSimulationHeader() {
        System.out.println();
        System.out.println(Ansi.ansi().fgBrightBlue().a("==== Starting Production Simulation ====").reset());
        System.out.printf("%-10s | %-15s | %-15s | %-20s | %-10s | %-10s%n",
                "Article", "Operation", "Workstation ID", "Workstation Operation", "Start", "Finish");
        System.out.println("----------------------------------------------------------------------------------------");
    }

    @Override
    public void printProcessingEvent(String articleId, String operation, String workstationId, String workstationOperation, int startTime, int finishTime) {
        System.out.printf("%-10s | %-15s | %-15s | %-20s | %-10d | %-10d%n",
                articleId, operation, workstationId, workstationOperation, startTime, finishTime);
    }

    @Override
    public void printSimulationSummary(int totalProductionTime, Map<String, Integer> workstationUsageTracker) {
        System.out.println();
        System.out.println(Ansi.ansi().fgBrightBlue().a("==== Simulation Summary ====").reset());
        System.out.println("Total Production Time: " + totalProductionTime + " seconds\n");

        System.out.println("Workstation Usage:");
        System.out.printf("%-15s | %-20s%n", "Workstation ID", "Usage Time (s)");
        System.out.println("------------------------------------------");
        workstationUsageTracker.forEach((workstationId, time) -> {
            System.out.printf("%-15s | %-20d%n", workstationId, time);
        });
    }

    @Override
    public void printTotalProductionTime(int totalTime) {
        System.out.println("\nTotal Production Time: " + totalTime + " seconds\n");
    }

    @Override
    public void printOperationTimes(Map<String, Integer> operationTimes, Simulation simulation) {
        System.out.println("Operation Times (Sorted by Usage Percentage Ascending):");
        System.out.printf("%-20s | %-20s | %-25s%n",
                "Operation", "Total Time (s)", "Usage Percentage (%)");
        System.out.println("--------------------------------------------------------------------------");

        List<String> operationNames = new ArrayList<>(operationTimes.keySet());

        operationNames.sort(Comparator.comparingDouble(simulation::getOperationUsagePercentage));


        for (String operation : operationNames) {
            int time = operationTimes.get(operation);
            double percentage = simulation.getOperationUsagePercentage(operation);
            System.out.printf("%-20s | %-20d | %-25.2f%%%n",
                    operation, time, percentage);
        }

        System.out.println();
    }

    @Override
    public void printWorkstationUsage(Map<String, Integer> workstationUsage, Simulation simulation) {
        System.out.println("Workstation Usage (Sorted by Usage Percentage Ascending):");
        System.out.printf("%-15s | %-20s | %-25s%n",
                "Workstation ID", "Usage Time (s)", "Usage Percentage (%)");
        System.out.println("--------------------------------------------------------------------------");

        List<String> workstationIds = new ArrayList<>(workstationUsage.keySet());

        workstationIds.sort(Comparator.comparingDouble(simulation::getWorkstationUsagePercentage));

        for (String workstationId : workstationIds) {
            int time = workstationUsage.get(workstationId);
            double percentage = simulation.getWorkstationUsagePercentage(workstationId);
            System.out.printf("%-15s | %-20d | %-25.2f%%%n",
                    workstationId, time, percentage);
        }

        System.out.println();
    }

    @Override
    public void printWelcomeMessage() {
        System.out.println("\nWelcome to the Production Simulator!\n");
    }

    @Override
    public void printHelp() {
        System.out.println("\n===== Help - Available Commands =====");
        System.out.println("1. Help - Display this help message.");
        System.out.println("2. Load Data - Load articles and workstations data from CSV files.");
        System.out.println("3. Run Simulation - Execute the production simulation.");
        System.out.println("4. Show Results - Display the results of the simulation.");
        System.out.println("5. Load Production Tree - Load production trees from CSV files.");
        System.out.println("6. Show Production Tree - Display a specific production tree.");
        System.out.println("7. View Critical Path - View the critical path of a production tree.");
        System.out.println("8. Update Material Quantities - Update the quantity of materials in a production tree.");
        System.out.println("9. Generate Graph - Generate and export the production graph.");
        System.out.println("10. Build PERT/CPM Graph from CSV - Import and build PERT/CPM graph from a CSV file.");
        System.out.println("11. Calculate Scheduling (ES, EF, LS, LF) - Calculate scheduling metrics.");
        System.out.println("12. Put Components into Production (AVL Tree) - Organize components using an AVL tree.");
        System.out.println("13. Exit - Exit the simulator.");
        System.out.println("14. Import BOO and BOM from Oracle SGBD - Import BOO and BOM data from Oracle database.");
        System.out.println("15. Update Average Production Times - Calculate and update average production times in the database.");
        System.out.println("====================================\n");
    }

    @Override
    public void printLoadDataSuccess(int numberOfArticles, int numberOfWorkstations) {
        System.out.println("Data loaded successfully.");
        System.out.println("Number of Articles: " + numberOfArticles);
        System.out.println("Number of Workstations: " + numberOfWorkstations + "\n");
    }

    @Override
    public void printLoadDataFailure(String errorMessage) {
        System.out.println(errorMessage + "\n");
    }

    @Override
    public void printUnknownCommandMessage() {
        System.out.println("Unknown command. Please select a valid option from the menu.\n");
    }

    @Override
    public void printSimulationNotExecutedMessage() {
        System.out.println("Simulation not executed. Use the 'Run Simulation' option first.\n");
    }

    @Override
    public void printSimulationExecutedMessage() {
        System.out.println("Simulation executed successfully.\n");
    }

    @Override
    public void printSimulationExitedMessage() {
        System.out.println("\nExiting the simulator. Goodbye!\n");
    }

    @Override
    public void printProductionTree(ProductionTree productionTree) {
        System.out.println("\n==== Production Tree ====");
        ProductionNode root = productionTree.getRoot();
        printNode(root, 0);
        System.out.println("==========================\n");
    }

    /**
     * Recursively prints a node and its children with indentation to represent hierarchy.
     *
     * @param node  The current ProductionNode to print.
     * @param level The current level in the tree for indentation.
     */
    private void printNode(ProductionNode node, int level) {
        String indent = "  ".repeat(level);
        String nodeType = node.getType() == ProductionNode.NodeType.OPERATION ? "Op" : "Mat";
        String nodeId = node.getId() != null && !node.getId().isEmpty() ? node.getId() : "Unknown ID";
        System.out.println(indent + nodeType + ": " + node.getName() + " (ID: " + nodeId + ", Qty: " + node.getQuantity() + ")");
        for (ProductionNode child : node.getChildren()) {
            printNode(child, level + 1);
        }
    }
}
