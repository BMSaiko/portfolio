package com.example.production.Domain;

import com.example.production.Domain.ProductionNode;
import com.example.production.Domain.ProductionTree;
import com.example.production.Domain.Operation;
import com.example.production.Domain.Article;

import java.util.*;

/**
 * Classe responsável por processar a árvore de produção,
 * atribuir níveis de dependência às operações,
 * e gerar o output.
 */
public class TreeOperations {
    private final ProductionTree productionTree;
    private final Map<ProductionNode, Integer> operationLevels;

    public TreeOperations(ProductionTree productionTree) {
        this.productionTree = productionTree;
        this.operationLevels = new HashMap<>();
    }

    /**
     * Gera o output de produção no formato especificado.
     * Formato: article;name_oper1;name_oper2;...
     *
     * @return Lista de linhas de output.
     */
    public List<String> generateProductionOutput() {
        List<String> output = new ArrayList<>();
        output.add("article;name_oper1;name_oper2;name_oper3;name_oper4;name_oper5;name_oper6");

        List<ProductionNode> rootOpNodes = getRootOperationNodes();

        Map<Integer, List<Operation>> levelOpsMap = new HashMap<>();

        for (ProductionNode rootOpNode : rootOpNodes) {
            Map<ProductionNode, Integer> opLevels = assignDependencyLevels(rootOpNode);
            operationLevels.putAll(opLevels);
        }

        Set<String> processedArticles = new HashSet<>();

        for (ProductionNode node : productionTree.getNodeMap().values()) {
            if (node.getType() == ProductionNode.NodeType.OPERATION) {
                List<ProductionNode> itemNodes = getItemNodesFromOperation(node);
                for (ProductionNode itemNode : itemNodes) {
                    String articleId = itemNode.getId();

                    if (processedArticles.contains(articleId)) {
                        continue;
                    }

                    List<String> operationNames = getOperationSequenceForItem(itemNode);

                    StringBuilder line = new StringBuilder();
                    line.append(articleId).append(";");

                    for (String opName : operationNames) {
                        line.append(opName).append(";");
                    }

                    int operationCount = operationNames.size();
                    for (int i = operationCount; i < 6; i++) {
                        line.append(";");
                    }

                    String lineStr = line.toString();
                    if (lineStr.endsWith(";")) {
                        lineStr = lineStr.substring(0, lineStr.length() - 1);
                    }

                    output.add(lineStr);
                    processedArticles.add(articleId);
                }
            }
        }

        return output;
    }

    /**
     * Atribui níveis de dependência às operações usando travessia BFS.
     *
     * @param rootOpNode Nó raiz da operação.
     * @return Mapa de nós de operação para seus níveis de dependência.
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
     * Recupera todas as operações que são raízes (não são filhas de outras operações).
     *
     * @return Lista de nós de operações raiz.
     */
    private List<ProductionNode> getRootOperationNodes() {
        List<ProductionNode> roots = new ArrayList<>();
        Set<String> childOperationKeys = new HashSet<>();

        for (ProductionNode node : productionTree.getNodeMap().values()) {
            if (node.getType() == ProductionNode.NodeType.OPERATION) {
                for (ProductionNode child : node.getChildren()) {
                    if (child.getType() == ProductionNode.NodeType.OPERATION) {
                        childOperationKeys.add(child.getId());
                    }
                }
            }
        }

        for (ProductionNode node : productionTree.getNodeMap().values()) {
            if (node.getType() == ProductionNode.NodeType.OPERATION && !childOperationKeys.contains(node.getId())) {
                roots.add(node);
            }
        }

        return roots;
    }

    /**
     * Recupera os nós de material (artigos) produzidos por uma operação específica.
     *
     * @param operationNode Nó de operação.
     * @return Lista de nós de materiais.
     */
    private List<ProductionNode> getItemNodesFromOperation(ProductionNode operationNode) {
        List<ProductionNode> items = new ArrayList<>();
        for (ProductionNode child : operationNode.getChildren()) {
            if (child.getType() == ProductionNode.NodeType.MATERIAL) {
                items.add(child);
            }
        }
        return items;
    }

    /**
     * Recupera a sequência de nomes de operações para um nó de material específico.
     *
     * @param itemNode Nó de material.
     * @return Lista de nomes de operações.
     */
    private List<String> getOperationSequenceForItem(ProductionNode itemNode) {
        List<String> operationNames = new ArrayList<>();
        ProductionNode parentOpNode = itemNode.getParent();

        while (parentOpNode != null && parentOpNode.getType() == ProductionNode.NodeType.OPERATION) {
            operationNames.add(parentOpNode.getName());
            parentOpNode = parentOpNode.getParent();
        }

        Collections.reverse(operationNames);

        return operationNames;
    }
}
