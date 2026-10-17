package com.example.production.UI;

import com.example.production.Domain.*;
import com.example.production.Graphs.Edge;
import com.example.production.Graphs.Graph;
import com.example.production.Graphs.PertCmpImporter;
import com.example.production.Graphs.USLP04GraphsGene;
import com.example.production.Service.DataImportService;
import com.example.production.Utils.CSVReader;
import com.example.production.Utils.ConsolePrinter;
import com.example.production.Utils.Printer;
import com.example.production.Utils.RepPathUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.Parser;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Class that represents the text-based interface to interact with the production simulator.
 */
public class MainInterface {
    private static final Logger logger = LogManager.getLogger(MainInterface.class);
    private LineReader reader;
    private Simulation simulation;
    private Printer printer;

    // Estruturas para o grafo PERT/CPM
    private Graph<String, String> pertCpmGraph;
    private List<Activity> activities;
    private Map<String, Activity> activityMap;

    // Constantes de caminho de ficheiros (exemplo)
    private static final String ARTICLES_CSV_PATH = "documentation/ESINF/USEI01/articles.csv";
    private static final String WORKSTATIONS_CSV_PATH = "documentation/ESINF/USEI01/workstations.csv";
    private static final String BOO_CSV_PATH = "documentation/ESINF/USEI08/boo_v2.csv";
    private static final String ITEMS_CSV_PATH = "documentation/ESINF/USEI08/items.csv";
    private static final String OPERATIONS_CSV_PATH = "documentation/ESINF/USEI08/operations.csv";

    private enum CommandOption {
        HELP("1"),
        LOAD_DATA("2"),
        RUN_SIMULATION("3"),
        SHOW_RESULTS("4"),
        LOAD_PRODUCTION_TREE("5"),
        SHOW_PRODUCTION_TREE("6"),
        VIEW_CRITICAL_PATH("7"),
        UPDATE_MATERIAL_QUANTITIES("8"),
        GENERATE_GRAPH("9"),
        BUILD_PERT_GRAPH_FROM_CSV("10"),
        CALCULATE_SCHEDULING("11"),
        PUT_COMPONENTS_INTO_PRODUCTION("12"),
        EXIT("13"),
        IMPORT_DATA_FROM_ORACLE("14"), // Existing option
        UPDATE_AVERAGE_PRODUCTION_TIMES("15"); // Nova opção para USLP07
    
        private final String code;
    
        CommandOption(String code) {
            this.code = code;
        }
    
        public String getCode() {
            return code;
        }
    
        public static Optional<CommandOption> fromCode(String code) {
            return Arrays.stream(values())
                    .filter(option -> option.code.equals(code))
                    .findFirst();
        }
    }
    

    /**
     * Constructor that allows injection of Printer dependencies.
     *
     * @param printer Implementation of Printer.
     * @throws Exception If an error occurs while initializing the terminal.
     */
    public MainInterface(Printer printer) throws Exception {
        Terminal terminal = TerminalBuilder.builder()
                .system(true)
                .build();
        Parser parser = new DefaultParser();
        reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .parser(parser)
                .build();
        this.printer = printer;
    }

    /**
     * Default constructor that initializes with ConsolePrinter.
     *
     * @throws Exception If an error occurs while initializing the terminal.
     */
    public MainInterface() throws Exception {
        this(new ConsolePrinter());
    }

    /**
     * Starts the text-based interface.
     */
    public void start() {
        printer.printWelcomeMessage();
        while (true) {
            displayMainMenu();
            String input;
            try {
                input = reader.readLine("Select an option: ").trim();
                handleCommand(input);
            } catch (UserInterruptException | EndOfFileException e) {
                printer.printSimulationExitedMessage();
                logger.info("Exiting the text interface.");
                break;
            } catch (Exception e) {
                logger.error("Error processing command: ", e);
                System.out.println("An error occurred while processing the command: " + e.getMessage());
            }
        }
    }

    /**
 * Displays the main menu with enumerated options.
 */
private void displayMainMenu() {
    System.out.println();
    System.out.println("==== Production Simulator Menu ====");
    System.out.println("1. Help");
    System.out.println("2. Load Data");
    System.out.println("3. Run Simulation");
    System.out.println("4. Show Results");
    System.out.println("5. Load Production Tree");
    System.out.println("6. Show Production Tree");
    System.out.println("7. View Critical Path");
    System.out.println("8. Update Material Quantities");
    System.out.println("9. Generate Graph");
    System.out.println("10. Build PERT/CPM Graph from CSV");
    System.out.println("11. Calculate Scheduling (ES, EF, LS, LF)");
    System.out.println("12. Put Components into Production (AVL Tree)");
    System.out.println("13. Exit");
    System.out.println("14. Import BOO and BOM from Oracle SGBD");
    System.out.println("15. Update Average Production Times"); // Nova opção
    System.out.println("====================================");
}



    /**
     * Processes the command entered by the user.
     *
     * @param command The entered command.
     */
    /**
     * Processes the command entered by the user.
     *
     * @param command The entered command.
     */
    public void handleCommand(String command) {
        Optional<CommandOption> option = CommandOption.fromCode(command);
    
        if (!option.isPresent()) {
            printer.printUnknownCommandMessage();
            logger.warn("Unknown command entered: '{}'", command);
            return;
        }
    
        try {
            switch (option.get()) {
                case HELP:
                    printer.clearScreen();
                    showHelp();
                    break;
                case LOAD_DATA:
                    loadData();
                    break;
                case RUN_SIMULATION:
                    runSimulation();
                    break;
                case SHOW_RESULTS:
                    showResults();
                    break;
                case LOAD_PRODUCTION_TREE:
                    loadProductionTree();
                    break;
                case SHOW_PRODUCTION_TREE:
                    showProductionTree();
                    break;
                case VIEW_CRITICAL_PATH:
                    viewCriticalPath();
                    break;
                case UPDATE_MATERIAL_QUANTITIES:
                    updateMaterialQuantities();
                    break;
                case GENERATE_GRAPH:
                    generateGraph();
                    break;
                case BUILD_PERT_GRAPH_FROM_CSV:
                    importPertCpmFromCSV();
                    break;
                case CALCULATE_SCHEDULING:
                    calculateScheduling();
                    break;
                case PUT_COMPONENTS_INTO_PRODUCTION:
                    putComponentsIntoProduction();
                    break;
                case IMPORT_DATA_FROM_ORACLE:
                    importDataFromOracle();
                    break;
                case UPDATE_AVERAGE_PRODUCTION_TIMES:
                    updateAverageProductionTimes();
                    break;
                case EXIT:
                    printer.printSimulationExitedMessage();
                    logger.info("Simulator exited by the user.");
                    System.exit(0);
                    break;
            }
        } catch (Exception e) {
            logger.error("Error executing command '{}': ", command, e);
            System.out.println("An error occurred while processing the command: " + e.getMessage());
        }
    }
    
/**
 * Atualiza os tempos médios de produção no banco de dados Oracle.
 */
private void updateAverageProductionTimes() {
    if (simulation == null) {
        System.out.println("\nPlease load and run the simulation first using the 'Load Data' and 'Run Simulation' options.\n");
        logger.warn("Attempted to update average production times without running simulation.");
        return;
    }

    try {
        simulation.calculateAndUpdateAverageTimes();
        System.out.println("\nAverage production times have been successfully updated in the database.\n");
        logger.info("Average production times updated successfully.");
    } catch (Exception e) {
        logger.error("Error updating average production times: ", e);
        System.out.println("\nFailed to update average production times: " + e.getMessage() + "\n");
    }
}


    /**
     * Displays the help with available commands.
     */
    private void showHelp() {
        printer.printHelp();
    }

    /**
     * Loads the data of articles and workstations from predefined CSV files.
     */
    private void loadData() {
        List<Article> articles = new ArrayList<>();
        List<Workstation> workstations = new ArrayList<>();

        try {
            String articlesFile = RepPathUtils.getAbsolutePath(ARTICLES_CSV_PATH);
            String workstationsFile = RepPathUtils.getAbsolutePath(WORKSTATIONS_CSV_PATH);

            CSVReader.readCSVFile(articlesFile, "article", articles, workstations, null);
            CSVReader.readCSVFile(workstationsFile, "workstation", articles, workstations, null);

            System.out.println("\nChoose the simulation strategy:");
            System.out.println("1. FIFO (First In, First Out)");
            System.out.println("2. Priority (Based on article priority)");

            String input = reader.readLine("Choose the strategy (1 for FIFO, 2 for Priority): ").trim();

            Simulation.SimulationStrategy strategy;

            switch (input) {
                case "1":
                    strategy = Simulation.SimulationStrategy.FIFO;
                    System.out.println("\nSimulation strategy set to: FIFO\n");
                    logger.info("Simulation strategy set to FIFO.");
                    break;
                case "2":
                    strategy = Simulation.SimulationStrategy.PRIORITY;
                    System.out.println("\nSimulation strategy set to: Priority\n");
                    logger.info("Simulation strategy set to Priority.");
                    break;
                default:
                    System.out.println("\nInvalid input. Loading canceled.\n");
                    logger.warn("Invalid input for simulation strategy: '{}'", input);
                    return;
            }

            simulation = new Simulation(articles, workstations, strategy);

            System.out.println("Data loaded successfully: " + articles.size() + " articles, " + workstations.size() + " workstations.\n");
            logger.info("Data loaded: {} articles, {} workstations.", articles.size(), workstations.size());

        } catch (IOException e) {
            logger.error("Error reading CSV files: ", e);
            System.out.println("\nFailed to load data. Please check the file paths.\n");
        }
    }

    /**
     * Loads the production trees from the specified CSV files.
     */
    private void loadProductionTree() {
        if (simulation == null) {
            System.out.println("\nPlease load the simulation data first using the 'Load Data' option.\n");
            logger.warn("Attempted to load production tree without loading simulation data.");
            return;
        }

        try {
            System.out.println("\nLoading the production tree...");

            String booFile = RepPathUtils.getAbsolutePath(BOO_CSV_PATH);
            String itemsFile = RepPathUtils.getAbsolutePath(ITEMS_CSV_PATH);
            String operationsFile = RepPathUtils.getAbsolutePath(OPERATIONS_CSV_PATH);

            Map<String, ProductionTree> productionTrees = CSVReader.readProductionTrees(booFile, itemsFile, operationsFile);

            simulation.setProductionTrees(productionTrees);

            System.out.println("Production tree loaded successfully for " + productionTrees.size() + " products.\n");
            logger.info("Production tree loaded for {} products.", productionTrees.size());

        } catch (IOException e) {
            logger.error("Error loading production tree: ", e);
            System.out.println("\nFailed to load the production tree. Please check the file paths.\n");
        }
    }

    /**
     * Displays the production tree of a specific article.
     */
    private void showProductionTree() {
        if (simulation == null || simulation.getProductionTrees() == null || simulation.getProductionTrees().isEmpty()) {
            System.out.println("\nProduction tree not loaded. Use the 'Load Production Tree' option first.\n");
            logger.warn("Attempted to show production tree without loading it.");
            return;
        }

        try {
            Map<String, ProductionTree> productionTrees = simulation.getProductionTrees();
            List<String> productIds = new ArrayList<>(productionTrees.keySet());

            if (productIds.isEmpty()) {
                System.out.println("\nNo production trees available to display.\n");
                logger.warn("No production trees available to display.");
                return;
            }

            System.out.println("\nAvailable Products:");
            for (int i = 0; i < productIds.size(); i++) {
                String productId = productIds.get(i);
                String productName = productionTrees.get(productId).getRoot().getName();
                System.out.println((i + 1) + ". " + productName + " (ID: " + productId + ")");
            }

            String input = reader.readLine("Select a product to display its production tree (enter number): ").trim();
            int selection;
            try {
                selection = Integer.parseInt(input);
                if (selection < 1 || selection > productIds.size()) {
                    System.out.println("\nInvalid selection. Please try again.\n");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid input. Please enter a number corresponding to the product.\n");
                return;
            }

            String selectedProductId = productIds.get(selection - 1);
            ProductionTree selectedTree = productionTrees.get(selectedProductId);
            printer.printProductionTree(selectedTree);

        } catch (Exception e) {
            logger.error("Error displaying production tree: ", e);
            System.out.println("\nFailed to display the production tree.\n");
        }
    }

    /**
     * Runs the production simulation.
     */
    private void runSimulation() {
        if (simulation == null) {
            System.out.println("\nPlease load the simulation data first using the 'Load Data' option.\n");
            logger.warn("Attempted to run simulation without loading data.");
            return;
        }

        logger.info("Executing the simulation.");
        simulation.runSimulation();
        simulation.calculateAndUpdateAverageTimes();
        System.out.println("\nSimulation executed successfully.\n");
    }

    /**
     * Displays the results of the simulation.
     */
    private void showResults() {
        if (simulation == null) {
            System.out.println("\nPlease run the simulation first using the 'Run Simulation' option.\n");
            logger.warn("Attempted to show results without running simulation.");
            return;
        }

        int totalTime = simulation.getTotalProductionTime();
        printer.printTotalProductionTime(totalTime);

        Map<String, Integer> operationTimes = simulation.getOperationTimes();
        printer.printOperationTimes(operationTimes, simulation);

        Map<String, Integer> workstationUsage = simulation.getWorkstationUsage();
        printer.printWorkstationUsage(workstationUsage, simulation);

        logger.info("Simulation results displayed to the user.");
    }

    /**
     * Allows the user to view the critical path of a specific production tree.
     */
    private void viewCriticalPath() {
        if (simulation == null || simulation.getProductionTrees() == null || simulation.getProductionTrees().isEmpty()) {
            System.out.println("\nProduction tree not loaded. Use the 'Load Production Tree' option first.\n");
            logger.warn("Attempted to view critical path without loading the production tree.");
            return;
        }

        try {
            Map<String, ProductionTree> productionTrees = simulation.getProductionTrees();
            List<String> productIds = new ArrayList<>(productionTrees.keySet());

            if (productIds.isEmpty()) {
                System.out.println("\nNo production trees available to display.\n");
                logger.warn("No production trees available to display.");
                return;
            }

            System.out.println("\nAvailable Products:");
            for (int i = 0; i < productIds.size(); i++) {
                String productId = productIds.get(i);
                String productName = productionTrees.get(productId).getRoot().getName();
                System.out.println((i + 1) + ". " + productName + " (ID: " + productId + ")");
            }

            String input = reader.readLine("Select a product to view its critical path (enter number): ").trim();
            int selection;
            try {
                selection = Integer.parseInt(input);
                if (selection < 1 || selection > productIds.size()) {
                    System.out.println("\nInvalid selection. Please try again.\n");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid input. Please enter a number corresponding to the product.\n");
                return;
            }

            String selectedProductId = productIds.get(selection - 1);
            ProductionTree selectedTree = productionTrees.get(selectedProductId);

            CriticalPathAnalyzer analyzer = new CriticalPathAnalyzer();

            List<ProductionNode> operationsByDepth = analyzer.getOperationsByDepth(selectedTree);

            System.out.println("\nOperations sorted by depth (from highest depth to lowest):");
            for (ProductionNode node : operationsByDepth) {
                int depth = getNodeDepth(selectedTree.getRoot(), node, 0);
                System.out.println("Depth: " + depth +
                        ", Operation ID: " + node.getId() + ", Name: " + node.getName());
            }

            List<ProductionNode> criticalPathNodes = analyzer.getCriticalPath(selectedTree);

            System.out.println("\nCritical Path (longest path from root to leaf):");
            for (ProductionNode node : criticalPathNodes) {
                if (node.getType() == ProductionNode.NodeType.OPERATION) {
                    System.out.println("Operation ID: " + node.getId() + ", Name: " + node.getName());
                }
            }
        } catch (Exception e) {
            logger.error("Error displaying critical path: ", e);
            System.out.println("\nFailed to display the critical path.\n");
        }
    }

    /**
     * Allows the user to update the quantity of a specific material in the production tree.
     */
    private void updateMaterialQuantities() {
        if (simulation == null || simulation.getProductionTrees() == null || simulation.getProductionTrees().isEmpty()) {
            System.out.println("\nProduction tree not loaded. Use the 'Load Production Tree' option first.\n");
            logger.warn("Attempted to update material quantities without loading the production tree.");
            return;
        }

        try {
            Map<String, ProductionTree> productionTrees = simulation.getProductionTrees();
            List<String> productIds = new ArrayList<>(productionTrees.keySet());

            if (productIds.isEmpty()) {
                System.out.println("\nNo production trees available to update.\n");
                logger.warn("No production trees available to update.");
                return;
            }

            System.out.println("\nAvailable Products:");
            for (int i = 0; i < productIds.size(); i++) {
                String productId = productIds.get(i);
                String productName = productionTrees.get(productId).getRoot().getName();
                System.out.println((i + 1) + ". " + productName + " (ID: " + productId + ")");
            }

            String input = reader.readLine("Select a product to update its materials (enter number): ").trim();
            int selection;
            try {
                selection = Integer.parseInt(input);
                if (selection < 1 || selection > productIds.size()) {
                    System.out.println("\nInvalid selection. Please try again.\n");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid input. Please enter a number corresponding to the product.\n");
                return;
            }

            String selectedProductId = productIds.get(selection - 1);
            ProductionTree selectedTree = productionTrees.get(selectedProductId);

            // Display all materials in the production tree
            List<ProductionNode> materials = getAllMaterials(selectedTree.getRoot());
            if (materials.isEmpty()) {
                System.out.println("\nNo materials found in the selected production tree.\n");
                logger.warn("No materials found in the selected production tree.");
                return;
            }

            System.out.println("\nMaterials in Production Tree:");
            for (int i = 0; i < materials.size(); i++) {
                ProductionNode material = materials.get(i);
                System.out.println((i + 1) + ". " + material.getName() + " (ID: " + material.getId()
                        + ", Current Quantity: " + material.getQuantity() + ")");
            }

            String materialInput = reader.readLine("Select a material to update (enter number): ").trim();
            int materialSelection;
            try {
                materialSelection = Integer.parseInt(materialInput);
                if (materialSelection < 1 || materialSelection > materials.size()) {
                    System.out.println("\nInvalid selection. Please try again.\n");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid input. Please enter a number corresponding to the material.\n");
                return;
            }

            ProductionNode selectedMaterial = materials.get(materialSelection - 1);

            String newQuantityInput = reader.readLine("Enter the new quantity for "
                    + selectedMaterial.getName() + ": ").trim();
            int newQuantity;
            try {
                newQuantity = Integer.parseInt(newQuantityInput);
                if (newQuantity < 0) {
                    System.out.println("\nQuantity cannot be negative. Please try again.\n");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid input. Please enter a valid integer for the quantity.\n");
                return;
            }

            // Update the quantity with cascading changes
            updateMaterialQuantityWithCascade(selectedTree, selectedMaterial.getId(), newQuantity);

            System.out.println("\nMaterial quantity updated successfully with cascading changes.\n");
            logger.info("Material quantity updated with cascading changes.");

            // Display the updated production tree
            printer.printProductionTree(selectedTree);

        } catch (Exception e) {
            logger.error("Error updating material quantities: ", e);
            System.out.println("\nFailed to update material quantities. Please try again.\n");
        }
    }

    /**
     * Recursively retrieves all material nodes in the production tree.
     */
    private List<ProductionNode> getAllMaterials(ProductionNode node) {
        List<ProductionNode> materials = new ArrayList<>();
        if (node.getType() == ProductionNode.NodeType.MATERIAL) {
            materials.add(node);
        }
        for (ProductionNode child : node.getChildren()) {
            materials.addAll(getAllMaterials(child));
        }
        return materials;
    }

    /**
     * Updates the quantity of a specific material and propagates the changes to dependent nodes.
     */
    private void updateMaterialQuantityWithCascade(ProductionTree tree, String materialId, int newQuantity) {
        ProductionNode materialNode = tree.getNodeById(materialId);
        if (materialNode != null) {
            materialNode.setQuantity(newQuantity);
            logger.info("Material '{}' (ID: {}) quantity updated to {}.",
                    materialNode.getName(), materialId, newQuantity);

            // Propagar mudanças
            propagateQuantityChange(tree, materialNode);
        } else {
            System.out.println("\nMaterial not found in the production tree.\n");
            logger.warn("Material with ID '{}' not found in the production tree.", materialId);
        }
    }

    /**
     * Recursively propagates quantity changes to dependent nodes.
     */
    private void propagateQuantityChange(ProductionTree tree, ProductionNode node) {
        // Find dependent nodes
        List<ProductionNode> dependentNodes = findDependentNodes(tree.getRoot(), node);

        for (ProductionNode dependent : dependentNodes) {
            // Exemplo de lógica de cascata
            if (dependent.getType() == ProductionNode.NodeType.OPERATION) {
                double adjustedQuantity = node.getQuantity() * dependent.getQuantity();
                dependent.setQuantity(adjustedQuantity);
                logger.info("Dependent operation '{}' (ID: {}) quantity adjusted to {}.",
                        dependent.getName(), dependent.getId(), adjustedQuantity);
            }
            propagateQuantityChange(tree, dependent);
        }
    }

    /**
     * Finds all nodes that depend on the given node.
     */
    private List<ProductionNode> findDependentNodes(ProductionNode current, ProductionNode target) {
        List<ProductionNode> dependents = new ArrayList<>();
        for (ProductionNode child : current.getChildren()) {
            if (child.getChildren().contains(target)) {
                dependents.add(child);
            }
            dependents.addAll(findDependentNodes(child, target));
        }
        return dependents;
    }

    /**
     * Helper method to get the depth of a node from the root.
     */
    private int getNodeDepth(ProductionNode current, ProductionNode target, int depth) {
        if (current == null) {
            return -1;
        }
        if (current.equals(target)) {
            return depth;
        }
        for (ProductionNode child : current.getChildren()) {
            int result = getNodeDepth(child, target, depth + 1);
            if (result != -1) {
                return result;
            }
        }
        return -1;
    }

    /**
     * Gera o grafo de um produto específico e exporta para .dot/.svg (GraphViz).
     */
    private void generateGraph() {
        if (simulation == null || simulation.getProductionTrees() == null || simulation.getProductionTrees().isEmpty()) {
            System.out.println("\nProduction tree not loaded. Use the 'Load Production Tree' option first.\n");
            logger.warn("Attempted to generate graph without loading the production tree.");
            return;
        }

        try {
            Map<String, ProductionTree> productionTrees = simulation.getProductionTrees();
            List<String> productIds = new ArrayList<>(productionTrees.keySet());

            if (productIds.isEmpty()) {
                System.out.println("\nNo production trees available to generate graphs.\n");
                logger.warn("No production trees available for graph generation.");
                return;
            }

            System.out.println("\nAvailable Products:");
            for (int i = 0; i < productIds.size(); i++) {
                String productId = productIds.get(i);
                String productName = productionTrees.get(productId).getRoot().getName();
                System.out.println((i + 1) + ". " + productName + " (ID: " + productId + ")");
            }

            String input = reader.readLine("Select a product to generate its graph (enter number): ").trim();
            int selection;
            try {
                selection = Integer.parseInt(input);
                if (selection < 1 || selection > productIds.size()) {
                    System.out.println("\nInvalid selection. Please try again.\n");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid input. Please enter a number corresponding to the product.\n");
                return;
            }

            String selectedProductId = productIds.get(selection - 1);
            ProductionTree selectedTree = productionTrees.get(selectedProductId);

            String outputDirectory = "graphs/";
            File directory = new File(outputDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String dotFilePath = outputDirectory + selectedProductId + "_production_tree.dot";
            String svgFilePath = outputDirectory + selectedProductId + "_production_tree.svg";

            USLP04GraphsGene graphGenerator = new USLP04GraphsGene(selectedTree);
            graphGenerator.generateDotFile(dotFilePath);

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "dot", "-Tsvg", dotFilePath, "-o", svgFilePath
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Graph generated for Product ID: " + selectedProductId);
                System.out.println("DOT File: " + dotFilePath);
                System.out.println("SVG File: " + svgFilePath);
            } else {
                System.out.println("\nFailed to generate the SVG image. Ensure Graphviz is installed and configured properly.\n");
                logger.error("Graphviz failed with exit code: " + exitCode);
            }
        } catch (Exception e) {
            logger.error("Error generating graph: ", e);
            System.out.println("\nFailed to generate the graph.\n");
        }
    }

    /**
     * Método principal para calcular o cronograma (ES, EF, LS, LF, Slack).
     * Verifica se o grafo PERT/CPM já foi carregado e chama o ScheduleCalculator.
     */
    private void calculateScheduling() {
        // Verifica se já existe um grafo PERT/CPM carregado
        if (pertCpmGraph == null || activities == null || activities.isEmpty()) {
            System.out.println("\nPERT/CPM graph not loaded. Use the 'Build PERT/CPM Graph from CSV' option first.\n");
            logger.warn("Attempted to calculate scheduling without loading PERT/CPM graph.");
            return;
        }

        try {
            // Resetar tempos antes do cálculo
            for (Activity a : activities) {
                a.setEarliestStart(0);
                a.setEarliestFinish(0);
                a.setLatestStart(0);
                a.setLatestFinish(0);
                a.setSlack(0);
            }

            // Executar cálculos de cronograma
            ScheduleCalculator.calculateSchedule(pertCpmGraph, activities);

            // Exibir resultados em tabela formatada
            System.out.println("===== TEMPOS DE AGENDAMENTO =====");

            // Cabeçalho da tabela
            // %-2s  -> campo de largura 2, alinhado à esquerda, para ID
            // %-6s  -> campo de largura 6, alinhado à esquerda, para ES, EF, etc.
            System.out.printf("%-2s %-6s %-6s %-6s %-6s %-6s%n",
                    "ID", "ES", "EF", "LS", "LF", "Slack");

            // Corpo da tabela (cada atividade)
            // %.2f -> imprime valor double com 2 casas decimais
            for (Activity a : activities) {
                System.out.printf("%-2s %-6.2f %-6.2f %-6.2f %-6.2f %-6.2f%n",
                        a.getId(),
                        a.getEarliestStart(),
                        a.getEarliestFinish(),
                        a.getLatestStart(),
                        a.getLatestFinish(),
                        a.getSlack()
                );
            }

            System.out.println("===============================");

            logger.info("Scheduling calculations completed and displayed to the user.");
        } catch (IllegalArgumentException e) {
            System.out.println("Erro no cálculo do cronograma: " + e.getMessage());
            logger.error("Error during scheduling calculations: ", e);
        } catch (Exception e) {
            System.out.println("Ocorreu um erro durante o cálculo do cronograma: " + e.getMessage());
            logger.error("Unexpected error during scheduling calculations: ", e);
        }
    }


    /**
     * Importa o ficheiro CSV para construir o grafo PERT/CPM.
     * Aqui o programa PEDE o caminho ao utilizador.
     */
    private void importPertCpmFromCSV() {
        System.out.println("\n=== Importar PERT/CPM de CSV ===");

        // Lê o caminho do ficheiro a partir do utilizador
        String csvPath = reader.readLine("Digite o caminho do CSV (ex: /home/user/pert_data.csv): ").trim();

        if (csvPath.isEmpty()) {
            System.out.println("Caminho inválido. Operação cancelada.");
            return;
        }

        // Instancia o importador e constrói o grafo
        PertCmpImporter importer = new PertCmpImporter(csvPath);
        try {
            importer.importAndBuildGraph();

            // Atribui ao campo da classe para uso posterior
            this.pertCpmGraph = importer.getPertCpmGraph();
            this.activities = importer.getActivitiesList();

            System.out.println("\n===== GRAFO PERT/CPM =====");
            for (String vertex : pertCpmGraph.vertices()) {
                System.out.print(vertex + " -> ");

                Collection<Edge<String, String>> outgoing = pertCpmGraph.outgoingEdges(vertex);
                List<String> adjacents = new ArrayList<>();
                if (outgoing != null) {
                    for (Edge<String, String> edge : outgoing) {
                        adjacents.add(edge.getVDest());
                    }
                }
                System.out.print(String.join(", ", adjacents));
                System.out.println();
            }
            System.out.println("==========================");

            // Atividades importadas
            System.out.println("\nAtividades importadas:");
            for (Activity a : activities) {
                System.out.println(a);
            }

        } catch (IOException e) {
            System.out.println("Erro ao ler o CSV: " + e.getMessage());
            e.printStackTrace(); // Para depuração detalhada
        } catch (Exception e) {
            System.out.println("Ocorreu um erro: " + e.getMessage());
            e.printStackTrace(); // Para depuração detalhada
        }
    }

    /**
     * Extracts the BOO tree from the production tree using a hierarchy identifier strategy.
     */
    private ProductionTree extractBooTree(String productId) {
        Map<String, ProductionTree> productionTrees = simulation.getProductionTrees();

        if (!productionTrees.containsKey(productId)) {
            System.out.println("Product ID not found.");
            return null;
        }

        ProductionTree productionTree = productionTrees.get(productId);
        ProductionTree booTree = new ProductionTree();

        // Define the hierarchy identifier strategy
        ProductionNode root = productionTree.getRoot();
        if (root != null) {
            booTree.setRoot(root); // Start the BOO tree from the root
            extractBooTreeHelper(root, booTree);
        }

        return booTree;
    }

    /**
     * Helper method for extracting the BOO tree recursively.
     */
    private void extractBooTreeHelper(ProductionNode currentNode, ProductionTree booTree) {
        for (ProductionNode child : currentNode.getChildren()) {
            if (child.getType() == ProductionNode.NodeType.OPERATION) {
                booTree.addNode(child); // Add only operations to the BOO tree
                extractBooTreeHelper(child, booTree);
            }
        }
    }

    /**
     * Assigns dependency levels to operations using BFS traversal.
     *
     * @param rootOpNode The root node of the production tree.
     * @return A map of operation nodes to their dependency levels.
     */
    private Map<ProductionNode, Integer> assignDependencyLevels(ProductionNode rootOpNode) {
        Map<ProductionNode, Integer> opLevels = new HashMap<>();
        Queue<ProductionNode> queue = new LinkedList<>();
        Queue<Integer> levels = new LinkedList<>();

        queue.add(rootOpNode);
        levels.add(0);

        Set<String> visited = new HashSet<>();

        while (!queue.isEmpty()) {
            ProductionNode current = queue.poll();
            int level = levels.poll();

            if (current == null || visited.contains(current.getId())) {
                continue;
            }

            visited.add(current.getId());

            if (current.getType() == ProductionNode.NodeType.OPERATION) {
                opLevels.put(current, level);
            }

            for (ProductionNode child : current.getChildren()) {
                queue.add(child);
                levels.add(level + 1);
            }
        }

        return opLevels;
    }

    /**
     * Stores operations in an AVL Tree based on their dependency levels.
     */
    private AVLTree<Integer, List<ProductionNode>> storeOperationsInAVL(ProductionTree booTree) {
        AVLTree<Integer, List<ProductionNode>> avlTree = new AVLTree<>();
        Map<ProductionNode, Integer> dependencyLevels = assignDependencyLevels(booTree.getRoot());

        for (Map.Entry<ProductionNode, Integer> entry : dependencyLevels.entrySet()) {
            int level = entry.getValue();
            ProductionNode operation = entry.getKey();

            List<ProductionNode> operationsAtLevel = avlTree.get(level);
            if (operationsAtLevel == null) {
                operationsAtLevel = new ArrayList<>();
            }

            operationsAtLevel.add(operation);
            avlTree.insert(level, operationsAtLevel);
        }

        return avlTree;
    }

    private void putComponentsIntoProduction() {
        try {
            System.out.println("\n=== Put Components into Production ===");
            System.out.print("Enter the Product ID to extract BOO and prepare components for production: ");
            String productId = reader.readLine().trim();

            ProductionTree booTree = extractBooTree(productId);
            if (booTree == null) {
                System.out.println("Failed to extract BOO tree. Ensure the Product ID is correct.");
                return;
            }

            AVLTree<Integer, List<ProductionNode>> avlTree = storeOperationsInAVL(booTree);

            System.out.println("\nGenerating CSV-like output...");
            List<String> outputLines = new ArrayList<>();
            outputLines.add("article;priority;name_oper1;name_oper2;name_oper3;name_oper4;name_oper5;name_oper6");

            // Example: Loop through the AVL Tree and generate lines
            for (Integer level : avlTree.inOrderKeys()) {
                List<ProductionNode> operations = avlTree.get(level);

                for (ProductionNode operation : operations) {
                    StringBuilder line = new StringBuilder();

                    // Add article ID and priority (mocked for now; you can replace with actual values)
                    line.append(operation.getId()) // Replace with actual article ID logic
                            .append(";")
                            .append("NORMAL;"); // Replace with actual priority logic

                    // Add operation names
                    List<String> operationNames = getOperationSequenceForNode(operation);
                    for (String opName : operationNames) {
                        line.append(opName).append(";");
                    }

                    // Fill empty columns for up to 6 operations
                    for (int i = operationNames.size(); i < 6; i++) {
                        line.append(";");
                    }

                    // Remove trailing semicolon and add the line to the output
                    outputLines.add(line.toString().replaceAll(";$", ""));
                }
            }

            // Print the output to the console
            System.out.println("\nCSV Output:");
            for (String line : outputLines) {
                System.out.println(line);
            }

        } catch (Exception e) {
            logger.error("Error during 'Put Components into Production': ", e);
            System.out.println("An error occurred while putting components into production: " + e.getMessage());
        }
    }

    /**
     * Initiates the import process from Oracle DB.
     */
    private void importDataFromOracle() {
        try {
            System.out.println("\n=== Import BOO and BOM from Oracle SGBD ===");

            // Confirm with the user before proceeding
            System.out.print("This process will overwrite existing CSV files. Do you wish to continue? (y/n): ");
            String confirmation = reader.readLine().trim().toLowerCase();
            if (!confirmation.equals("y")) {
                System.out.println("Operation canceled by the user.");
                return;
            }

            // Initialize DataImportService
            DataImportService dataImportService = new DataImportService();

            // Import data and generate CSVs
            dataImportService.importFromOracleAndGenerateCSV();

            System.out.println("BOO and BOM data imported and CSV files generated successfully.");
            logger.info("BOO and BOM data imported and CSV files generated successfully.");

        } catch (Exception e) {
            logger.error("Error during 'Import BOO and BOM from Oracle SGBD': ", e);
            System.out.println("An error occurred during the import process: " + e.getMessage());
        }
    }


    /**
     * Retrieves the sequence of operation names for a given ProductionNode.
     */
    private List<String> getOperationSequenceForNode(ProductionNode node) {
        List<String> operationNames = new ArrayList<>();
        while (node != null && node.getType() == ProductionNode.NodeType.OPERATION) {
            operationNames.add(node.getName());
            node = node.getParent(); // Use getParent instead of getParentNode
        }
        Collections.reverse(operationNames); // Reverse to maintain proper sequence
        return operationNames;
    }



}
