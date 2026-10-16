package com.example.production.Domain;

import com.example.production.Graphs.Edge;
import com.example.production.Graphs.Graph;

import java.util.*;

/**
 * Classe responsável por calcular os tempos de ES, EF, LS, LF e Slack
 * (passagens forward e backward) no grafo PERT/CPM.
 */
public class ScheduleCalculator {

    /**
     * Executa todo o processo de cálculo de ES, EF, LS, LF e Slack:
     * 1) Ordenação topológica do grafo
     * 2) Forward pass (ES, EF)
     * 3) Determina o "projectFinish" (max EF do projeto)
     * 4) Backward pass (LS, LF)
     * 5) Calcula Slack (LS - ES)
     *
     * @param graph      Grafo PERT/CPM
     * @param activities Lista de atividades (cada Activity tem ID, Duração, Predecessores, etc.)
     */
    public static void calculateSchedule(Graph<String, String> graph, List<Activity> activities) {
        // Map de ID -> objeto Activity
        Map<String, Activity> activityMap = new HashMap<>();
        for (Activity a : activities) {
            activityMap.put(a.getId(), a);

            // Reseta os tempos antes do cálculo
            a.setEarliestStart(0);
            a.setEarliestFinish(0);
            a.setLatestStart(0);
            a.setLatestFinish(0);
            a.setSlack(0);
        }

        // 1) Ordenação Topológica (Kahn)
        List<String> sortedOrder = topologicalSort(graph);

        // 2) Forward Pass (ES, EF)
        forwardPass(sortedOrder, activityMap, graph);

        // 3) projectFinish = maior EF
        double projectFinish = findProjectFinish(activityMap);

        // 4) Backward Pass (LS, LF)
        backwardPass(sortedOrder, graph, activityMap, projectFinish);

        // 5) Calcular Slack
        calculateSlack(activityMap);
    }

    // ------------------------------------------------------------------
    //  1) Ordenação topológica
    // ------------------------------------------------------------------
    private static List<String> topologicalSort(Graph<String, String> graph) throws IllegalArgumentException {
        // inDegree[v] = quantas arestas de entrada v possui
        Map<String, Integer> inDegree = new HashMap<>();
        for (String vertex : graph.vertices()) {
            inDegree.put(vertex, 0);
        }

        // Preenche inDegree
        for (String vertex : graph.vertices()) {
            Collection<Edge<String, String>> outgoing = graph.outgoingEdges(vertex);
            if (outgoing != null) {
                for (Edge<String, String> edge : outgoing) {
                    String dest = edge.getVDest();
                    inDegree.put(dest, inDegree.get(dest) + 1);
                }
            }
        }

        // Fila de vértices com inDegree = 0
        Queue<String> queue = new LinkedList<>();
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        List<String> sortedOrder = new ArrayList<>();

        while (!queue.isEmpty()) {
            String vertex = queue.poll();
            sortedOrder.add(vertex);

            Collection<Edge<String, String>> outgoing = graph.outgoingEdges(vertex);
            if (outgoing != null) {
                for (Edge<String, String> edge : outgoing) {
                    String dest = edge.getVDest();
                    inDegree.put(dest, inDegree.get(dest) - 1);
                    if (inDegree.get(dest) == 0) {
                        queue.add(dest);
                    }
                }
            }
        }

        // Se nem todos os vértices entraram na sortedOrder, existe ciclo
        if (sortedOrder.size() != graph.numVertices()) {
            throw new IllegalArgumentException(
                    "O grafo contém ciclos (ou não foi possível ordená-lo topologicamente)."
            );
        }

        return sortedOrder;
    }

    // ------------------------------------------------------------------
    //  2) Forward Pass (ES, EF)
    // ------------------------------------------------------------------
    private static void forwardPass(
            List<String> sortedOrder,
            Map<String, Activity> activityMap,
            Graph<String, String> graph
    ) {
        // Para cada vértice na ordem topológica...
        for (String actId : sortedOrder) {
            Activity activity = activityMap.get(actId);

            // Se não houver predecessores (atividade inicial)
            if (activity.getPredecessors().isEmpty()) {
                activity.setEarliestStart(0);
                activity.setEarliestFinish(activity.getDuration());
            } else {
                // ES = maior EF dos predecessores
                double maxEF = 0;
                for (String predId : activity.getPredecessors()) {
                    Activity pred = activityMap.get(predId);
                    if (pred.getEarliestFinish() > maxEF) {
                        maxEF = pred.getEarliestFinish();
                    }
                }
                activity.setEarliestStart(maxEF);
                activity.setEarliestFinish(maxEF + activity.getDuration());
            }
        }
    }

    // ------------------------------------------------------------------
    //  3) Determinar o tempo de término do projeto (max EF)
    // ------------------------------------------------------------------
    private static double findProjectFinish(Map<String, Activity> activityMap) {
        double projectFinish = 0;
        for (Activity activity : activityMap.values()) {
            if (activity.getEarliestFinish() > projectFinish) {
                projectFinish = activity.getEarliestFinish();
            }
        }
        return projectFinish;
    }

    // ------------------------------------------------------------------
    //  4) Backward Pass (LS, LF)
    // ------------------------------------------------------------------
    private static void backwardPass(
            List<String> sortedOrder,
            Graph<String, String> graph,
            Map<String, Activity> activityMap,
            double projectFinish
    ) {
        // Percorre em ordem reversa
        ListIterator<String> iter = sortedOrder.listIterator(sortedOrder.size());
        while (iter.hasPrevious()) {
            String actId = iter.previous();
            Activity activity = activityMap.get(actId);

            Collection<Edge<String, String>> outgoing = graph.outgoingEdges(actId);

            // Se a atividade não tem sucessores,
            // assumimos que ela termina no "projectFinish"
            if (outgoing == null || outgoing.isEmpty()) {
                activity.setLatestFinish(projectFinish);
                activity.setLatestStart(projectFinish - activity.getDuration());
            } else {
                // LF = menor LS entre os sucessores
                double minLS = Double.MAX_VALUE;
                for (Edge<String, String> edge : outgoing) {
                    String succId = edge.getVDest();
                    Activity succ = activityMap.get(succId);
                    if (succ.getLatestStart() < minLS) {
                        minLS = succ.getLatestStart();
                    }
                }
                activity.setLatestFinish(minLS);
                activity.setLatestStart(minLS - activity.getDuration());
            }
        }
    }

    // ------------------------------------------------------------------
    //  5) Calcular Slack
    // ------------------------------------------------------------------
    private static void calculateSlack(Map<String, Activity> activityMap) {
        for (Activity activity : activityMap.values()) {
            double slack = activity.getLatestStart() - activity.getEarliestStart();
            activity.setSlack(slack);
        }
    }

    // ------------------------------------------------------------------
    // Método auxiliar para imprimir no formato do screenshot
    // ------------------------------------------------------------------
    public static void printSchedulingInfo(List<Activity> activities) {

        // Cabeçalho
        System.out.println("===== TEMPOS DE AGENDAMENTO =====");
        // Alinhamento: ID (2 chars), ES/EF/LS/LF/Slack (com 5 ou 6 chars), etc.
        System.out.printf("%-2s %5s %5s %5s %5s %5s%n",
                "ID", "ES", "EF", "LS", "LF", "Slack");

        // Corpo
        for (Activity a : activities) {
            // ES, EF, LS, LF, Slack com 2 casas decimais
            // (ou use replace('.', ',') se quiser trocar ponto por vírgula)
            System.out.printf(
                    "%-2s %5.2f %5.2f %5.2f %5.2f %5.2f%n",
                    a.getId(),
                    a.getEarliestStart(),
                    a.getEarliestFinish(),
                    a.getLatestStart(),
                    a.getLatestFinish(),
                    a.getSlack()
            );
        }
        System.out.println("===============================");
    }
}
