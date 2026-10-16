package com.example.production.Domain;

import com.example.production.Utils.ConsolePrinter;
import com.example.production.Utils.Printer;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class SimulationTest {

    @Test
    void testSimulationRunSimple() {
        // Arrange
        List<Article> articles = new ArrayList<>();
        List<Workstation> workstations = new ArrayList<>();

        // Artigo com duas operações: CUT (10s) e POLISH (15s)
        Article article = new Article(1, "NORMAL", Arrays.asList("CUT", "POLISH"));
        articles.add(article);

        Workstation wsCut = new Workstation("ws1", "CUT", 10);
        Workstation wsPolish = new Workstation("ws2", "POLISH", 15);
        workstations.add(wsCut);
        workstations.add(wsPolish);

        Printer printer = new ConsolePrinter();
        Simulation simulation = new Simulation(articles, workstations, printer, Simulation.SimulationStrategy.FIFO);

        // Setup ProductionTree
        ProductionTree productionTree = new ProductionTree();
        ProductionNode root = new ProductionNode("root", "Root Operation", ProductionNode.NodeType.OPERATION, 1);
        ProductionNode materialA = new ProductionNode("matA", "Material A", ProductionNode.NodeType.MATERIAL, 10);
        ProductionNode operationB = new ProductionNode("opB", "Operation B", ProductionNode.NodeType.OPERATION, 2);
        ProductionNode materialC = new ProductionNode("matC", "Material C", ProductionNode.NodeType.MATERIAL, 5);
        root.addChild(materialA);
        materialA.addChild(operationB);
        operationB.addChild(materialC);
        productionTree.setRoot(root);

        // Associar a árvore de produção com a simulação
        simulation.setProductionTrees(Map.of("1", productionTree));

        // Act
        simulation.runSimulation();

        int totalTime = simulation.getTotalProductionTime();
        assertEquals(25, totalTime, "O tempo total de produção deve ser 25 segundos.");

        Map<String, Integer> operationTimes = simulation.getOperationTimes();
        assertEquals(10, operationTimes.get("CUT"), "Tempo de operação CUT deve ser 10 segundos.");
        assertEquals(15, operationTimes.get("POLISH"), "Tempo de operação POLISH deve ser 15 segundos.");

        Map<String, Integer> workstationUsage = simulation.getWorkstationUsage();
        assertEquals(10, workstationUsage.get("ws1"), "Uso da workstation ws1 deve ser 10 segundos.");
        assertEquals(15, workstationUsage.get("ws2"), "Uso da workstation ws2 deve ser 15 segundos.");
    }
}
