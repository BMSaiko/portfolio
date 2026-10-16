package com.example.production.Domain;

import com.example.production.Service.AverageProductionTimeService;
import com.example.production.Utils.FlowDependency;
import com.example.production.Utils.ConsolePrinter;
import com.example.production.Domain.*;
import com.example.production.Utils.Event;
import com.example.production.Utils.Printer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class that simulates the production process.
 * Manages articles, workstations, events, and production trees in a simulated factory environment.
 */
public class Simulation {

    private static final Logger logger = LogManager.getLogger(Simulation.class);

    private final Map<String, Queue<Article>> operationQueues = new ConcurrentHashMap<>();
    private final Map<String, List<Workstation>> availableWorkstations = new ConcurrentHashMap<>();
    private final Map<String, Integer> operationTimeTracker = new ConcurrentHashMap<>();
    private final Map<String, Integer> workstationUsageTracker = new ConcurrentHashMap<>();
    private final Map<String, Integer> workstationNextAvailableTime = new ConcurrentHashMap<>();
    private final PriorityBlockingQueue<Event> eventQueue = new PriorityBlockingQueue<>();
    private final Printer printer;
    private final ExecutorService executorService;
    private final FlowDependency flowDependency = new FlowDependency();
    private final AverageProductionTimeService averageProductionTimeService = new AverageProductionTimeService();

    private final AtomicInteger currentTime = new AtomicInteger(0);
    private final AtomicInteger maxFinishTime = new AtomicInteger(0);
    private int factoryStartTime = 0;
    private int factoryEndTime = 0;

    private SimulationStrategy strategy;

    private Map<String, ProductionTree> productionTrees;

    public enum SimulationStrategy {
        FIFO,
        PRIORITY
    }

    /**
     * Constructs a Simulation instance with the specified articles, workstations, printer, and strategy.
     *
     * @param articles     List of articles to be processed.
     * @param workstations List of available workstations.
     * @param printer      Implementation of Printer for console or other outputs.
     * @param strategy     Strategy for processing (FIFO or PRIORITY).
     */
    public Simulation(List<Article> articles, List<Workstation> workstations, Printer printer, SimulationStrategy strategy) {
        this.printer = printer;
        this.strategy = strategy;
        this.executorService = Executors.newCachedThreadPool();

        initializeOperationQueues(articles);
        initializeWorkstations(workstations);

        logger.info("Simulation initialized with {} articles and {} workstations.", articles.size(), workstations.size());
    }

    /**
     * Constructs a Simulation instance with the specified articles and workstations.
     * Uses ConsolePrinter as the default Printer implementation.
     *
     * @param articles     List of articles to be processed.
     * @param workstations List of available workstations.
     * @param strategy     Strategy for processing (FIFO ou PRIORITY).
     */
    public Simulation(List<Article> articles, List<Workstation> workstations, SimulationStrategy strategy) {
        this(articles, workstations, new ConsolePrinter(), strategy);
    }


    public Map<String, ProductionTree> getProductionTrees() {
        return productionTrees;
    }

    /**
     * Sets the production trees for the simulation.
     *
     * @param productionTrees Map of ProductionTree objects keyed by product ID.
     */
    public void setProductionTrees(Map<String, ProductionTree> productionTrees) {
        this.productionTrees = productionTrees;
        logger.info("Production trees associated with the simulation.");
    }

    /**
     * Retrieves the production tree for a specific article.
     *
     * @param articleId The ID of the article.
     * @return The ProductionTree associated with the article, or null if not found.
     */
    public ProductionTree getProductionTree(String articleId) {
        if (productionTrees != null) {
            return productionTrees.get(articleId);
        }
        return null;
    }

    /**
     * Gets the total production time (factory operating time).
     *
     * @return The total production time in seconds.
     */
    public int getTotalProductionTime() {
        return factoryEndTime - factoryStartTime;
    }  

    /**
     * Gets the total times for each operation.
     *
     * @return An unmodifiable map with operation names as keys and their total processing times as values.
     */
    public Map<String, Integer> getOperationTimes() {
        return Collections.unmodifiableMap(operationTimeTracker);
    }

    /**
     * Gets the usage times for each workstation.
     *
     * @return An unmodifiable map with workstation IDs as keys and their total usage times as values.
     */
    public Map<String, Integer> getWorkstationUsage() {
        return Collections.unmodifiableMap(workstationUsageTracker);
    }

    /**
     * Calculates the usage percentage of a specific workstation.
     *
     * @param workstationId The ID of the workstation.
     * @return The usage percentage of the workstation.
     */
    public double getWorkstationUsagePercentage(String workstationId) {
        int usageTime = workstationUsageTracker.getOrDefault(workstationId, 0);
        int totalTime = getTotalProductionTime();
        return totalTime > 0 ? (usageTime * 100.0) / totalTime : 0;
    }

    /**
     * Calculates the usage percentage of a specific operation.
     *
     * @param operationName The name of the operation.
     * @return The usage percentage of the operation.
     */
    public double getOperationUsagePercentage(String operationName) {
        int operationTime = operationTimeTracker.getOrDefault(operationName, 0);
        int totalTime = getTotalProductionTime();
        return totalTime > 0 ? (operationTime * 100.0) / totalTime : 0;
    }

    /**
     * Initializes the operation queues based on the provided articles, respeitando a ordem de entrada.
     *
     * @param articles List of articles to be added to the queues.
     */
    private void initializeOperationQueues(List<Article> articles) {
        for (Article article : articles) {
            String firstOperation = article.getCurrentOperation();
            if (firstOperation != null) {
                // Initialize the operation queue based on the strategy
                if (strategy == SimulationStrategy.FIFO) {
                    operationQueues.computeIfAbsent(firstOperation, k -> {
                        logger.info("Creating FIFO queue for operation '{}'.", firstOperation);
                        return new ConcurrentLinkedQueue<>();
                    }).offer(article);
                } else if (strategy == SimulationStrategy.PRIORITY) {
                    operationQueues.computeIfAbsent(firstOperation, k -> {
                        logger.info("Creating Priority queue for operation '{}'.", firstOperation);
                        return new PriorityBlockingQueue<>(11, Comparator.comparingInt(Article::getPriorityLevel).reversed());
                    }).offer(article);
                }

                logger.debug("Article {} added to the queue of operation '{}'.", article.getArticleId(), firstOperation);
            }
        }
    }

    /**
     * Initializes the workstations and categorizes them by operation type.
     *
     * @param workstations List of available workstations for processing.
     */
    private void initializeWorkstations(List<Workstation> workstations) {
        for (Workstation workstation : workstations) {
            String operation = workstation.getOperationName();
            availableWorkstations.computeIfAbsent(operation, k -> {
                logger.info("Registering workstations for operation '{}'.", operation);
                return Collections.synchronizedList(new ArrayList<>());
            }).add(workstation);
            workstationNextAvailableTime.put(workstation.getWorkstationId(), 0);
            logger.debug("Workstation {} registered for operation '{}'.", workstation.getWorkstationId(), operation);
        }
    }

     /**
     * Calcula os tempos médios e atualiza no banco de dados.
     */
    public void calculateAndUpdateAverageTimes() {
        Map<String, Integer> operationTimeTracker = getOperationTimes();
        averageProductionTimeService.updateAverageProductionTimes(operationTimeTracker);
    }

    /**
     * Runs the production simulation.
     * Initializes and processes events in chronological order.
     */
    public void runSimulation() {
        logger.info("Starting production simulation.");
        printer.printSimulationHeader();
        scheduleInitialEvents();

        if (!eventQueue.isEmpty()) {
            factoryStartTime = eventQueue.peek().getTime();
            logger.debug("Factory start time set to: {}", factoryStartTime);
        } else {
            factoryStartTime = 0;
            logger.debug("Factory start time set to default: 0");
        }
    
        while (!eventQueue.isEmpty() || ((ThreadPoolExecutor) executorService).getActiveCount() > 0) {
            try {
                Event event = eventQueue.poll(1, TimeUnit.SECONDS);
                if (event != null) {
                    currentTime.set(event.getTime());
                    logger.debug("Processing event at time: {}", currentTime.get());
                    if (event.isStart()) {
                        executorService.submit(() -> processArticle(event.getArticle(), event.getOperation(), event.getWorkstation()));
                    } else {
                        finishArticleProcessing(event.getArticle(), event.getOperation(), event.getWorkstation());
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Simulation interrupted: {}", e.getMessage(), e);
            }
        }
    
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

        factoryEndTime = maxFinishTime.get();
    
        logger.info("Simulation completed. Total production time: {} seconds.", getTotalProductionTime());
        printer.printSimulationSummary(getTotalProductionTime(), workstationUsageTracker);
    }    

    /**
     * Schedules the initial events for all operations.
     */
    private void scheduleInitialEvents() {
        for (String operation : operationQueues.keySet()) {
            Queue<Article> queue = operationQueues.get(operation);
            List<Workstation> workstationsForOperation = availableWorkstations.getOrDefault(operation, Collections.emptyList());

            if (queue == null || queue.isEmpty()) continue;

            workstationsForOperation.sort(Comparator.comparingInt(Workstation::getTime));

            for (Workstation workstation : workstationsForOperation) {
                if (!queue.isEmpty()) {
                    Article article = queue.poll();

                    if (article != null) {
                        int startTime = workstationNextAvailableTime.get(workstation.getWorkstationId());
                        eventQueue.add(new Event(startTime, workstation, article, operation, true));
                        logger.debug("Initial event: Article {} -> Operation '{}' -> Workstation {}.",
                                article.getArticleId(), operation, workstation.getWorkstationId());
                    }
                }
            }
        }
    }

       /**
     * Processes a single article and updates simulation data.
     * Sends the article to the specified workstation for the given operation.
     *
     * @param article     The article being processed.
     * @param operation   The operation being performed.
     * @param workstation The workstation processing the article.
     */
    private void processArticle(Article article, String operation, Workstation workstation) {
        synchronized (workstation) {
            int currentAvailableTime = workstationNextAvailableTime.get(workstation.getWorkstationId());
            int processingTime = workstation.getTime();

            int startTime = Math.max(currentAvailableTime, currentTime.get());
            int finishTime = startTime + processingTime;

            workstationNextAvailableTime.put(workstation.getWorkstationId(), finishTime);
            
            // Obter operationName a partir da Workstation
            String operationName = workstation.getOperationName(); // Usar operationName como chave
            operationTimeTracker.merge(operationName, processingTime, Integer::sum);
            workstationUsageTracker.merge(workstation.getWorkstationId(), processingTime, Integer::sum);

            workstation.addUsageTime(processingTime);

            flowDependency.recordFlow(article.getArticleId(), workstation.getWorkstationId());

            maxFinishTime.updateAndGet(currentMax -> Math.max(currentMax, finishTime));

            eventQueue.add(new Event(finishTime, workstation, article, operation, false));

            logger.info("Article {} assigned to workstation {} for operation '{}'. Start: {}, Finish: {}",
                    article.getArticleId(), workstation.getWorkstationId(), operation, startTime, finishTime);

            printer.printProcessingEvent(
                    String.valueOf(article.getArticleId()),
                    operation,
                    workstation.getWorkstationId(),
                    workstation.getOperationName(),
                    startTime,
                    finishTime
            );
        }
    }

    /**
     * Handles the completion of an article's processing and schedules subsequent operations if applicable.
     *
     * @param article     The article that has finished processing.
     * @param operation   The operation that was completed.
     * @param workstation The workstation that completed the processing.
     */
    private void finishArticleProcessing(Article article, String operation, Workstation workstation) {
        logger.info("Article {} completed operation '{}' at workstation {}.", article.getArticleId(), operation, workstation.getWorkstationId());

        if (article.moveToNextOperation()) {
            String nextOperation = article.getCurrentOperation();
            operationQueues.computeIfAbsent(nextOperation, k -> {
                logger.info("Creating queue for operation '{}'.", nextOperation);
                return new ConcurrentLinkedQueue<>();
            }).offer(article);

            logger.debug("Article {} moved to next operation '{}'.", article.getArticleId(), nextOperation);

            scheduleNextOperation(article, nextOperation);
        } else {
            logger.debug("Article {} has completed all operations.", article.getArticleId());
        }

        scheduleAvailableWorkstations(operation);
    }

    /**
     * Schedules the next operation for an article.
     *
     * @param article       The article to be processed.
     * @param nextOperation The next operation to be performed.
     */
    private void scheduleNextOperation(Article article, String nextOperation) {
        List<Workstation> workstationsForNextOperation = availableWorkstations.get(nextOperation);
        if (workstationsForNextOperation != null) {
            workstationsForNextOperation.sort(Comparator.comparingInt(Workstation::getTime));
            for (Workstation nextWorkstation : workstationsForNextOperation) {
                int nextAvailableTime = workstationNextAvailableTime.get(nextWorkstation.getWorkstationId());
                if (nextAvailableTime <= currentTime.get()) {
                    eventQueue.add(new Event(currentTime.get(), nextWorkstation, article, nextOperation, true));
                    logger.debug("Start event scheduled for article {} on operation '{}' at workstation {}.",
                            article.getArticleId(), nextOperation, nextWorkstation.getWorkstationId());
                    break;
                }
            }
        } else {
            logger.warn("No workstation available for the next operation '{}'.", nextOperation);
        }
    }

    /**
     * Schedules articles for available workstations in a given operation.
     *
     * @param operation The operation for which workstations are being scheduled.
     */
    private void scheduleAvailableWorkstations(String operation) {
        List<Workstation> workstationsForOperation = availableWorkstations.get(operation);
        if (workstationsForOperation != null) {
            Queue<Article> queue = operationQueues.get(operation);
            for (Workstation availableWorkstation : workstationsForOperation) {
                int nextAvailableTime = workstationNextAvailableTime.get(availableWorkstation.getWorkstationId());
                if (queue != null && !queue.isEmpty() && nextAvailableTime <= currentTime.get()) {
                    Article nextArticle = queue.poll();

                    if (nextArticle != null) {
                        eventQueue.add(new Event(currentTime.get(), availableWorkstation, nextArticle, operation, true));
                        logger.debug("Article {} scheduled on workstation {} for operation '{}'.",
                                nextArticle.getArticleId(), availableWorkstation.getWorkstationId(), operation);
                    }
                }
            }
        }
    }

    /**
     * Displays the flow dependencies report.
     */
    public void displayFlowDependencies() {
        logger.info("Displaying flow dependencies.");
        flowDependency.displayFlowDependencies();
    }
}
