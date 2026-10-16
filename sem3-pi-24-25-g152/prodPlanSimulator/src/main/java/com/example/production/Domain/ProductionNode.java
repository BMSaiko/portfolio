package com.example.production.Domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a node in the production tree, which can be either an operation or a material.
 */
public class ProductionNode {
    public enum NodeType {
        OPERATION,
        MATERIAL
    }

    private final String id;
    private final String name;
    private final NodeType type;
    private double quantity;
    private final double originalQuantity; // Stores the original quantity for calculations
    private final List<ProductionNode> children;
    private ProductionNode parent; // Reference to the parent node

    // -------------------------------------------
    // CAMPOS PARA SCHEDULING (ES, EF, LS, LF, ETC.)
    // -------------------------------------------
    private int duration = 0;  // Duração da atividade (apenas faz sentido se type == OPERATION)
    private int ES;            // Earliest Start
    private int EF;            // Earliest Finish
    private int LS;            // Latest Start
    private int LF;            // Latest Finish
    private int slack;         // Slack (folga)
    // -------------------------------------------

    /**
     * Constructs a ProductionNode with the specified parameters.
     *
     * @param id       The unique identifier of the node.
     * @param name     The name of the node.
     * @param type     The type of the node (OPERATION or MATERIAL).
     * @param quantity The initial quantity of the node.
     */
    public ProductionNode(String id, String name, NodeType type, double quantity) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.quantity = quantity;
        this.originalQuantity = quantity; // Define the original quantity
        this.children = new ArrayList<>();
        this.parent = null;
    }

    // ---------------------------------------------------------
    // Getters e Setters (já existentes + para scheduling fields)
    // ---------------------------------------------------------
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public NodeType getType() {
        return type;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getOriginalQuantity() {
        return originalQuantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public List<ProductionNode> getChildren() {
        return children;
    }

    public ProductionNode getParent() {
        return parent;
    }

    public void setParent(ProductionNode parent) {
        this.parent = parent;
    }

    public String getLabel() {
        if (type == NodeType.MATERIAL) {
            if (name.equals("varnish")) {
                return String.format("%s [%dml]", name, (int) quantity);
            } else {
                return String.format("%s [%d]", name, (int) quantity);
            }
        }
        return name;
    }

    // ---------------------------------------------------------
    // CAMPOS E METODOS ADICIONAIS PARA SCHEDULING
    // ---------------------------------------------------------
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getES() {
        return ES;
    }

    public void setES(int ES) {
        this.ES = ES;
    }

    public int getEF() {
        return EF;
    }

    public void setEF(int EF) {
        this.EF = EF;
    }

    public int getLS() {
        return LS;
    }

    public void setLS(int LS) {
        this.LS = LS;
    }

    public int getLF() {
        return LF;
    }

    public void setLF(int LF) {
        this.LF = LF;
    }

    public int getSlack() {
        return slack;
    }

    public void setSlack(int slack) {
        this.slack = slack;
    }
    // ---------------------------------------------------------

    private Map<ProductionNode, Integer> childQuantities = new HashMap<>();

    public void addChildWithQuantity(ProductionNode child, int quantity) {
        this.addChild(child);
        childQuantities.put(child, quantity);
    }

    public int getChildQuantity(ProductionNode child) {
        return childQuantities.getOrDefault(child, 1); // Default to 1 if not specified
    }

    /**
     * Adds a child node to this node and sets this node as the parent of the child.
     *
     * @param child The child node to be added.
     */
    public void addChild(ProductionNode child) {
        this.children.add(child);
        child.setParent(this);
    }
}
