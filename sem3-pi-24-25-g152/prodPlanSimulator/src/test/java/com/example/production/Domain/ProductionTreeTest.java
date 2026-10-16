package com.example.production.Domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductionTreeTest {
    private ProductionTree productionTree;

    @BeforeEach
    void setUp() {
        /*
         * Configuração da árvore de produção:
         *
         *         Root Operation (Op, Qty: 1)
         *                |
         *        Material A (Mat, Qty: 10)
         *                |
         *        Operation B (Op, Qty: 2)
         *                |
         *        Material C (Mat, Qty: 5)
         */
        ProductionNode root = new ProductionNode("1", "Root Operation", ProductionNode.NodeType.OPERATION, 1);
        ProductionNode materialA = new ProductionNode("2", "Material A", ProductionNode.NodeType.MATERIAL, 10);
        ProductionNode operationB = new ProductionNode("3", "Operation B", ProductionNode.NodeType.OPERATION, 2);
        ProductionNode materialC = new ProductionNode("4", "Material C", ProductionNode.NodeType.MATERIAL, 5);

        // Construindo a árvore corretamente
        root.addChild(materialA);
        materialA.addChild(operationB); // Operation B agora é filho de Material A
        operationB.addChild(materialC);

        // Inicializando a árvore de produção
        productionTree = new ProductionTree();
        productionTree.setRoot(root);
    }

    @Test
    void testSetAndGetRoot() {
        ProductionTree tree = new ProductionTree();
        ProductionNode root = new ProductionNode("100", "Assemble Product", ProductionNode.NodeType.OPERATION, 1);
        tree.setRoot(root);
        assertEquals(root, tree.getRoot(), "O nó raiz deve ser definido e recuperado corretamente.");
    }

    @Test
    void testAddNodeAndGetNodeById() {
        ProductionTree tree = new ProductionTree();
        ProductionNode node = new ProductionNode("200", "Subassembly", ProductionNode.NodeType.MATERIAL, 2);
        tree.addNode(node);
        assertEquals(node, tree.getNodeById("200"), "O nó deve ser adicionado e recuperado corretamente pelo ID.");
    }

    @Test
    void testUpdateMaterialQuantity() {
        // Atualizar a quantidade de Material A de 10 para 20
        ProductionNode materialA = productionTree.getNodeById("2");
        assertNotNull(materialA, "Material A deve existir na árvore de produção.");
        assertEquals(10, materialA.getQuantity(), "Quantidade inicial de Material A deve ser 10.");

        // Atualização
        productionTree.updateMaterialQuantityWithCascade("2", 20);

        // Verificar a atualização
        assertEquals(20, materialA.getQuantity(), "Quantidade de Material A deve ser atualizada para 20.");
    }

    @Test
    void testCascadingUpdateOnOperation() {
        /*
         * Neste teste, ao atualizar a quantidade de Material A, a quantidade de Operation B deve ser ajustada
         * de acordo com a lógica de propagação definida (qty_op = qty_mat * original_qty_opB)
         *
         * Quantidade inicial de Operation B: 2
         * Quantidade de Material A: 10
         * Após atualização para 20, quantidade de Operation B: 20 * 2 = 40
         */

        ProductionNode materialA = productionTree.getNodeById("2");
        ProductionNode operationB = productionTree.getNodeById("3");
        assertNotNull(materialA, "Material A deve existir na árvore de produção.");
        assertNotNull(operationB, "Operation B deve existir na árvore de produção.");
        assertEquals(10, materialA.getQuantity(), "Quantidade inicial de Material A deve ser 10.");
        assertEquals(2, operationB.getQuantity(), "Quantidade inicial de Operation B deve ser 2.");

        // Atualização
        productionTree.updateMaterialQuantityWithCascade("2", 20);

        // Verificar as atualizações
        assertEquals(20, materialA.getQuantity(), "Quantidade de Material A deve ser atualizada para 20.");
        assertEquals(40.0, operationB.getQuantity(), "Quantidade de Operation B deve ser atualizada para 40.0.");
    }

    @Test
    void testCascadingUpdateOnMaterialC() {
        /*
         * Atualizar a quantidade de Material C e verificar se Operation B NÃO é ajustada.
         *
         * Quantidade inicial de Material C: 5
         * Operation B: 2
         * Após atualização para 10, quantidade de Operation B deve permanecer 2.0
         */

        ProductionNode materialC = productionTree.getNodeById("4");
        ProductionNode operationB = productionTree.getNodeById("3");
        assertNotNull(materialC, "Material C deve existir na árvore de produção.");
        assertNotNull(operationB, "Operation B deve existir na árvore de produção.");
        assertEquals(5, materialC.getQuantity(), "Quantidade inicial de Material C deve ser 5.");
        assertEquals(2, operationB.getQuantity(), "Quantidade inicial de Operation B deve ser 2.");

        // Atualização
        productionTree.updateMaterialQuantityWithCascade("4", 10);

        // Verificar as atualizações
        assertEquals(10, materialC.getQuantity(), "Quantidade de Material C deve ser atualizada para 10.");
        assertEquals(2.0, operationB.getQuantity(), "Quantidade de Operation B deve permanecer 2.0.");
    }

    @Test
    void testUpdateNonExistentMaterial() {
        // Tentar atualizar um material que não existe
        ProductionNode nonExistent = productionTree.getNodeById("999");
        assertNull(nonExistent, "Material com ID '999' não deve existir na árvore de produção.");

        // Atualização
        productionTree.updateMaterialQuantityWithCascade("999", 50);

        // Nenhuma alteração deve ocorrer, já que o material não existe
        ProductionNode root = productionTree.getNodeById("1");
        ProductionNode operationB = productionTree.getNodeById("3");

        assertEquals(1, root.getQuantity(), "Quantidade de Root deve permanecer 1.");
        assertEquals(2, operationB.getQuantity(), "Quantidade de Operation B deve permanecer 2.");
    }

    @Test
    void testNegativeQuantityUpdate() {
        /*
         * Tentar atualizar a quantidade de um material para um valor negativo deve lançar uma exceção
         */

        ProductionNode materialA = productionTree.getNodeById("2");
        assertNotNull(materialA, "Material A deve existir na árvore de produção.");
        assertEquals(10, materialA.getQuantity(), "Quantidade inicial de Material A deve ser 10.");

        // Atualização para um valor negativo
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productionTree.updateMaterialQuantityWithCascade("2", -5);
        });

        String expectedMessage = "Quantidade não pode ser negativa.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage), "Deve lançar uma exceção para quantidade negativa.");
    }
}
