package com.example.production.Domain;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CriticalPathAnalyzerTest {
    @Test
    public void testProductionNodeCreation() {
        // Criar um nó de exemplo
        ProductionNode node = new ProductionNode("N1", "Operation 1", ProductionNode.NodeType.OPERATION, 5);

        // Verificar se os atributos foram corretamente atribuídos
        assertEquals("N1", node.getId());
        assertEquals("Operation 1", node.getName());
        assertEquals(ProductionNode.NodeType.OPERATION, node.getType());
        assertEquals(5, node.getQuantity());
        assertTrue(node.getChildren().isEmpty()); // Deve começar sem filhos
    }

    @Test
    public void testAddChildToProductionNode() {
        // Criar nós pai e filho
        ProductionNode parent = new ProductionNode("P1", "Parent Operation", ProductionNode.NodeType.OPERATION, 10);
        ProductionNode child = new ProductionNode("C1", "Child Material", ProductionNode.NodeType.MATERIAL, 2);

        // Adicionar o filho ao nó pai
        parent.addChild(child);

        // Verificar se o filho foi corretamente adicionado
        List<ProductionNode> children = parent.getChildren();
        assertEquals(1, children.size());
        assertEquals(child, children.get(0));
    }

    @Test
    public void testProductionTreeCreation() {
        // Criar uma árvore de produção
        ProductionTree tree = new ProductionTree();

        // Verificar se a árvore está vazia inicialmente
        assertNull(tree.getRoot());
    }

    @Test
    public void testSetRootNodeInProductionTree() {
        // Criar um nó raiz
        ProductionNode root = new ProductionNode("R1", "Root Operation", ProductionNode.NodeType.OPERATION, 15);

        // Criar uma árvore de produção e definir o nó raiz
        ProductionTree tree = new ProductionTree();
        tree.setRoot(root);

        // Verificar se o nó raiz foi corretamente atribuído
        assertEquals(root, tree.getRoot());
        assertEquals(root, tree.getNodeById("R1")); // Também deve ser acessível pelo mapa
    }

    @Test
    public void testAddNodeToProductionTree() {
        // Criar nós de exemplo
        ProductionNode node1 = new ProductionNode("N1", "Operation 1", ProductionNode.NodeType.OPERATION, 5);
        ProductionNode node2 = new ProductionNode("N2", "Material 1", ProductionNode.NodeType.MATERIAL, 3);

        // Criar a árvore e adicionar os nós
        ProductionTree tree = new ProductionTree();
        tree.addNode(node1);
        tree.addNode(node2);

        // Verificar se os nós foram corretamente adicionados
        assertEquals(node1, tree.getNodeById("N1"));
        assertEquals(node2, tree.getNodeById("N2"));
    }

    @Test
    public void testProductionTreeHierarchy() {
        // Criar nós para uma hierarquia de exemplo
        ProductionNode root = new ProductionNode("R1", "Root Operation", ProductionNode.NodeType.OPERATION, 15);
        ProductionNode child1 = new ProductionNode("C1", "Child Operation 1", ProductionNode.NodeType.OPERATION, 5);
        ProductionNode child2 = new ProductionNode("C2", "Child Material 2", ProductionNode.NodeType.MATERIAL, 3);

        // Construir a hierarquia
        root.addChild(child1);
        root.addChild(child2);

        // Criar a árvore e definir o nó raiz
        ProductionTree tree = new ProductionTree();
        tree.setRoot(root);

        // Verificar se a hierarquia está correta
        assertEquals(root, tree.getRoot());
        assertEquals(2, root.getChildren().size());
        assertTrue(root.getChildren().contains(child1));
        assertTrue(root.getChildren().contains(child2));
    }

    @Test
    public void testToStringMethods() {
        // Criar nós de exemplo
        ProductionNode node = new ProductionNode("N1", "Operation 1", ProductionNode.NodeType.OPERATION, 5);
        ProductionTree tree = new ProductionTree();
        tree.setRoot(node);

        // Verificar as representações em string
        String nodeString = node.toString();
        String treeString = tree.toString();

        assertTrue(nodeString.contains("id='N1'"));
        assertTrue(nodeString.contains("name='Operation 1'"));
        assertTrue(treeString.contains("root="));
        assertTrue(treeString.contains("nodesMap="));
    }
}
