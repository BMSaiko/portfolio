package com.example.production.Domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductionNodeTest {

    @Test
    void testProductionNodeCreation() {
        ProductionNode node = new ProductionNode("100", "Assemble Product", ProductionNode.NodeType.OPERATION, 1);
        assertEquals("100", node.getId());
        assertEquals("Assemble Product", node.getName());
        assertEquals(ProductionNode.NodeType.OPERATION, node.getType());
        assertEquals(1, node.getQuantity());
        assertTrue(node.getChildren().isEmpty());
    }

    @Test
    void testAddChild() {
        ProductionNode parent = new ProductionNode("100", "Assemble Product", ProductionNode.NodeType.OPERATION, 1);
        ProductionNode child = new ProductionNode("200", "Subassembly", ProductionNode.NodeType.MATERIAL, 2);
        parent.addChild(child);
        assertEquals(1, parent.getChildren().size());
        assertEquals(child, parent.getChildren().get(0));
    }
}
