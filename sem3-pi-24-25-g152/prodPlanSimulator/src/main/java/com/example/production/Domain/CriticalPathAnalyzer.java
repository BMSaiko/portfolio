package com.example.production.Domain;

import java.util.*;

/**
 * Represents a utility to identify and prioritize critical path operations in a production tree.
 * Also calculates scheduling info (ES, EF, LS, LF) for operation nodes.
 */
public class CriticalPathAnalyzer {

    /**
     * Identifies operations sorted by their depth using a heap-based priority queue.
     *
     * @param productionTree The production tree to analyze.
     * @return A list of operations sorted by their depth in descending order.
     */
    public List<ProductionNode> getOperationsByDepth(ProductionTree productionTree) {
        if (productionTree == null || productionTree.getRoot() == null) {
            throw new IllegalArgumentException("Production tree or root node cannot be null.");
        }

        PriorityQueue<ProductionNodeDepth> heap = new PriorityQueue<>(
                (a, b) -> Integer.compare(b.depth, a.depth)
        );

        traverseTree(productionTree.getRoot(), 0, heap);

        List<ProductionNode> operationsByDepth = new ArrayList<>();
        while (!heap.isEmpty()) {
            ProductionNode node = heap.poll().node;
            if (node.getType() == ProductionNode.NodeType.OPERATION) {
                operationsByDepth.add(node);
            }
        }
        return operationsByDepth;
    }

    /**
     * Helper method to traverse the production tree and calculate the depth of each node.
     *
     * @param node  The current node being traversed.
     * @param depth The current depth of the node in the tree.
     * @param heap  The priority queue to store nodes by their depth.
     */
    private void traverseTree(ProductionNode node, int depth, PriorityQueue<ProductionNodeDepth> heap) {
        if (node == null) {
            return;
        }

        heap.add(new ProductionNodeDepth(node, depth));

        for (ProductionNode child : node.getChildren()) {
            traverseTree(child, depth + 1, heap);
        }
    }

    /**
     * Helper class to store a production node and its depth.
     */
    private static class ProductionNodeDepth {
        private final ProductionNode node;
        private final int depth;

        public ProductionNodeDepth(ProductionNode node, int depth) {
            this.node = node;
            this.depth = depth;
        }
    }

    /**
     * Identifies the critical path operations in the production tree based on the longest path.
     *
     * @param productionTree The production tree to analyze.
     * @return A list of operations representing the critical path from root to leaf.
     */
    public List<ProductionNode> getCriticalPath(ProductionTree productionTree) {
        if (productionTree == null || productionTree.getRoot() == null) {
            throw new IllegalArgumentException("Production tree or root node cannot be null.");
        }

        List<ProductionNode> currentPath = new ArrayList<>();
        List<ProductionNode> criticalPath = new ArrayList<>();
        findCriticalPath(productionTree.getRoot(), currentPath, criticalPath);
        return criticalPath;
    }

    /**
     * Recursively traverses the tree to find the longest path from root to leaf.
     *
     * @param node         The current node being traversed.
     * @param currentPath  The current path from root to this node.
     * @param criticalPath The longest path found so far.
     */
    private void findCriticalPath(ProductionNode node, List<ProductionNode> currentPath, List<ProductionNode> criticalPath) {
        if (node == null) {
            return;
        }

        currentPath.add(node);

        if (node.getChildren().isEmpty()) {
            // É folha, verifica se o path atual é maior
            if (currentPath.size() > criticalPath.size()) {
                criticalPath.clear();
                criticalPath.addAll(new ArrayList<>(currentPath));
            }
        } else {
            for (ProductionNode child : node.getChildren()) {
                findCriticalPath(child, currentPath, criticalPath);
            }
        }
        currentPath.remove(currentPath.size() - 1);
    }

    // =============================================================
    //      MÉTODOS PARA CALCULAR ES, EF, LS, LF, SLACK
    // =============================================================

    /**
     * Método principal que faz:
     *  1) Forward Pass (ES, EF)
     *  2) Acha o maior EF (projectFinish)
     *  3) Backward Pass (LS, LF)
     *  4) Calcula Slack (LS - ES)
     *
     * @param productionTree a árvore com root e todos os nós
     */
    public void calculateEarliestAndLatest(ProductionTree productionTree) {
        if (productionTree == null || productionTree.getRoot() == null) {
            throw new IllegalArgumentException("Production tree or root node cannot be null.");
        }

        // Obter todos os nós via nodeMap para iterar facilmente
        Collection<ProductionNode> allNodes = productionTree.getNodeMap().values();

        // 1) Forward Pass
        forwardPass(productionTree);

        // 2) Achar o maior EF de todas as OPERATIONs = projectFinish
        int projectFinish = 0;
        for (ProductionNode node : allNodes) {
            if (node.getType() == ProductionNode.NodeType.OPERATION) {
                if (node.getEF() > projectFinish) {
                    projectFinish = node.getEF();
                }
            }
        }

        // 3) Backward Pass
        backwardPass(productionTree, projectFinish);

        // 4) Calcular Slack
        for (ProductionNode node : allNodes) {
            if (node.getType() == ProductionNode.NodeType.OPERATION) {
                int slack = node.getLS() - node.getES(); // ou node.getLF() - node.getEF()
                node.setSlack(slack);
            }
        }
    }

    /**
     * Forward Pass:
     * ES(node) = max(EF(pai1), EF(pai2), ...)  (se houver vários pais)
     * EF(node) = ES(node) + duration
     *
     * Fazemos um BFS a partir do root. (Assumindo que seja árvore.)
     */
    private void forwardPass(ProductionTree productionTree) {
        Collection<ProductionNode> allNodes = productionTree.getNodeMap().values();

        // Zerar ES, EF antes de começar
        for (ProductionNode node : allNodes) {
            node.setES(0);
            node.setEF(0);
        }

        // Construir mapa de pais
        Map<ProductionNode, List<ProductionNode>> parentsMap = buildParentsMap(allNodes);

        // BFS a partir do root
        Queue<ProductionNode> queue = new LinkedList<>();
        queue.offer(productionTree.getRoot());

        while (!queue.isEmpty()) {
            ProductionNode current = queue.poll();

            // Se for OPERATION, calcular ES, EF
            if (current.getType() == ProductionNode.NodeType.OPERATION) {
                int maxEFofParents = 0;
                List<ProductionNode> parents = parentsMap.get(current);
                if (parents != null) {
                    for (ProductionNode p : parents) {
                        if (p.getEF() > maxEFofParents) {
                            maxEFofParents = p.getEF();
                        }
                    }
                }
                current.setES(maxEFofParents);
                current.setEF(current.getES() + current.getDuration());
            }

            // Adicionar filhos na fila
            for (ProductionNode child : current.getChildren()) {
                queue.offer(child);
            }
        }
    }

    /**
     * Backward Pass:
     * - Se um nó não tem filhos (folha), LF = projectFinish
     * - Caso contrário, LF = min(LS(filho1), LS(filho2), ...)
     * - LS = LF - duration
     */
    private void backwardPass(ProductionTree productionTree, int projectFinish) {
        Collection<ProductionNode> allNodes = productionTree.getNodeMap().values();

        // Zerar LS, LF
        for (ProductionNode node : allNodes) {
            node.setLS(0);
            node.setLF(0);
        }

        // Construir parentsMap novamente
        Map<ProductionNode, List<ProductionNode>> parentsMap = buildParentsMap(allNodes);

        // Encontrar nós "folha" (OPERATION sem filhos) para começar
        Queue<ProductionNode> queue = new LinkedList<>();
        for (ProductionNode node : allNodes) {
            if (node.getType() == ProductionNode.NodeType.OPERATION) {
                if (node.getChildren().isEmpty()) {
                    node.setLF(projectFinish);
                    node.setLS(node.getLF() - node.getDuration());
                    queue.offer(node);
                }
            }
        }

        // BFS "de baixo pra cima"
        while (!queue.isEmpty()) {
            ProductionNode current = queue.poll();

            // Pegar os pais do current
            List<ProductionNode> parents = parentsMap.get(current);
            if (parents == null) continue;

            for (ProductionNode parent : parents) {
                if (parent.getType() == ProductionNode.NodeType.OPERATION) {
                    // Ajustar LF do parent
                    if (parent.getLF() == 0) {
                        parent.setLF(current.getLS());
                    } else {
                        parent.setLF(Math.min(parent.getLF(), current.getLS()));
                    }
                    // LS
                    parent.setLS(parent.getLF() - parent.getDuration());

                    // Adiciona o parent na fila
                    queue.offer(parent);
                }
            }
        }
    }

    /**
     * Constrói um mapa: Node -> Lista de seus pais.
     */
    private Map<ProductionNode, List<ProductionNode>> buildParentsMap(Collection<ProductionNode> allNodes) {
        Map<ProductionNode, List<ProductionNode>> parentsMap = new HashMap<>();
        for (ProductionNode node : allNodes) {
            parentsMap.put(node, new ArrayList<>());
        }
        for (ProductionNode node : allNodes) {
            for (ProductionNode child : node.getChildren()) {
                parentsMap.get(child).add(node);
            }
        }
        return parentsMap;
    }

    /**
     * Método opcional para imprimir/visualizar resultados (ES, EF, LS, LF, Slack).
     */
    public void printSchedulingInfo(ProductionTree tree) {
        for (ProductionNode node : tree.getNodeMap().values()) {
            if (node.getType() == ProductionNode.NodeType.OPERATION) {
                System.out.printf(
                        "Operation '%s' [duration=%d] -> ES=%d, EF=%d, LS=%d, LF=%d, Slack=%d\n",
                        node.getName(),
                        node.getDuration(),
                        node.getES(),
                        node.getEF(),
                        node.getLS(),
                        node.getLF(),
                        node.getSlack()
                );
            }
        }
    }
}
