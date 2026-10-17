package com.example.production.Domain;

import java.util.*;

/**
 * Represents the entire production tree structure.
 */
public class ProductionTree {
    private ProductionNode root;
    private Map<String, ProductionNode> nodeMap; // For quick access to nodes by ID

    public ProductionTree() {
        this.nodeMap = new HashMap<>();
    }

    public ProductionNode getRoot() {
        return root;
    }

    /**
     * Sets the root of the production tree and adds it to the nodeMap.
     *
     * @param root The root node of the ProductionTree.
     */
    public void setRoot(ProductionNode root) {
        this.root = root;
        addNode(root); // Recursively add all child nodes
    }

    /**
     * Adds a node and its children to the nodeMap for quick access.
     *
     * @param node The node to be added.
     */
    public void addNode(ProductionNode node) {
        if (node != null) {
            nodeMap.put(node.getId(), node);
            for (ProductionNode child : node.getChildren()) {
                addNode(child); // Recursively add all child nodes
            }
        }
    }

    /**
     * Retrieves a node by its ID.
     *
     * @param id The ID of the node.
     * @return The ProductionNode with the specified ID, or null if not found.
     */
    public ProductionNode getNodeById(String id) {
        return nodeMap.get(id);
    }

    /**
     * Updates the quantity of a specific material and propagates the changes to dependent operations.
     *
     * @param materialId  The ID of the material to be updated.
     * @param newQuantity The new quantity to be set.
     */
    public void updateMaterialQuantityWithCascade(String materialId, int newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantidade não pode ser negativa.");
        }

        ProductionNode materialNode = getNodeById(materialId);
        if (materialNode != null && materialNode.getType() == ProductionNode.NodeType.MATERIAL) {
            double oldQuantity = materialNode.getQuantity();
            materialNode.setQuantity(newQuantity);
            System.out.println("Material '" + materialNode.getName() + "' quantity updated from " + oldQuantity + " to " + newQuantity);
            // Propagate changes to dependent operations (children)
            propagateQuantityChange(materialNode);
        } else {
            System.err.println("Material com ID '" + materialId + "' não encontrado ou não é do tipo MATERIAL.");
        }
    }

    /**
     * Returns an unmodifiable view of the internal nodeMap.
     */
    public Map<String, ProductionNode> getNodeMap() {
        // Se quiser devolver uma cópia, use new HashMap<>(nodeMap)
        // Se quiser devolver o próprio map, retorne nodeMap direto
        // Se quiser que seja apenas leitura, use Collections.unmodifiableMap
        return Collections.unmodifiableMap(nodeMap);
    }


    /**
     * Propagates the quantity change to dependent operations (child nodes).
     *
     * @param node The node whose quantity was updated.
     */
    private void propagateQuantityChange(ProductionNode node) {
        if (node.getType() == ProductionNode.NodeType.MATERIAL) {
            for (ProductionNode child : node.getChildren()) {
                if (child.getType() == ProductionNode.NodeType.OPERATION) {
                    double newOpQuantity = node.getQuantity() * child.getOriginalQuantity();
                    double oldOpQuantity = child.getQuantity();
                    child.setQuantity(newOpQuantity);
                    System.out.println("Operation '" + child.getName() + "' quantity updated from " + oldOpQuantity + " to " + newOpQuantity);
                    // Recursively propagate if there are further child operations
                    propagateQuantityChange(child);
                }
            }
        }
    }
}



