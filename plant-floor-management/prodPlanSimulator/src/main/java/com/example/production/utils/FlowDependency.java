package com.example.production.Utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Manages the flow dependencies of each article through workstations.
 */
public class FlowDependency {

    private final Map<String, Map<Integer, Integer>> workstationDependencies = new ConcurrentHashMap<>();

    /**
     * Records the flow of each article through a workstation.
     *
     * @param articleId     The ID of the article.
     * @param workstationId The ID of the workstation.
     */
    public void recordFlow(int articleId, String workstationId) {
        workstationDependencies.computeIfAbsent(workstationId, _ -> new ConcurrentHashMap<>());
        Map<Integer, Integer> articleCounts = workstationDependencies.get(workstationId);
        articleCounts.merge(articleId, 1, Integer::sum);
    }

    /**
     * Displays the flow dependency report.
     */
    public void displayFlowDependencies() {
        for (String workstation : workstationDependencies.keySet()) {
            System.out.print(workstation + " : ");
            Map<Integer, Integer> articleCounts = workstationDependencies.get(workstation);
            articleCounts.forEach((articleId, count) -> {
                System.out.print("(" + articleId + "," + count + ") ");
            });
            System.out.println();
        }
    }

    /**
     * Gets the map of workstation dependencies.
     *
     * @return A map containing workstation IDs as keys and their corresponding article count maps as values
     */
    public Map<String, Map<Integer, Integer>> getWorkstationDependencies() {
        return workstationDependencies;
    }
    
}