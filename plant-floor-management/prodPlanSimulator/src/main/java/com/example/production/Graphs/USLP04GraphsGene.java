package com.example.production.Graphs;

import com.example.production.Domain.ProductionNode;
import com.example.production.Domain.ProductionTree;

import java.io.FileWriter;
import java.io.IOException;

public class USLP04GraphsGene {

    private ProductionTree productionTree;

    public USLP04GraphsGene(ProductionTree productionTree) {
        this.productionTree = productionTree;
    }

    /**
     * Generates a DOT file representing the ProductionTree for Graphviz visualization.
     *
     * @param outputFilePath The file path to save the DOT file.
     */
    public void generateDotFile(String outputFilePath) {
        StringBuilder dotBuilder = new StringBuilder();
        dotBuilder.append("digraph ProductionTree {\n");

        if (productionTree.getRoot() != null) {
            addNodeAndChildrenToDot(dotBuilder, productionTree.getRoot());
        }

        dotBuilder.append("}");

        try (FileWriter writer = new FileWriter(outputFilePath)) {
            writer.write(dotBuilder.toString());
        } catch (IOException e) {
            System.err.println("Error writing DOT file: " + e.getMessage());
        }
    }

    /**
     * Recursively adds nodes and edges to the DOT representation.
     *
     * @param dotBuilder The StringBuilder for the DOT content.
     * @param node       The current node being processed.
     */
    private void addNodeAndChildrenToDot(StringBuilder dotBuilder, ProductionNode node) {

        dotBuilder.append(String.format("\"%s\" [label=\"%s\"];\n", node.getId(), node.getLabel()));

        for (ProductionNode child : node.getChildren()) {

            dotBuilder.append(String.format("\"%s\" -> \"%s\" [label=\"%d\"];\n",
                    node.getId(), child.getId(), node.getChildQuantity(child)));

            addNodeAndChildrenToDot(dotBuilder, child);
        }
    }
}
