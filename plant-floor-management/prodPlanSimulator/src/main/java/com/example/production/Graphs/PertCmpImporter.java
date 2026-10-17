package com.example.production.Graphs;

import com.example.production.Domain.Activity;
import com.example.production.Graphs.Map.MapGraph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsável por importar atividades de um arquivo CSV e construir o grafo PERT/CPM diretamente.
 */
public class PertCmpImporter {
    private final String filePath;
    private final List<Activity> activitiesList;
    private final MapGraph<String, String> pertCpmGraph;

    public PertCmpImporter(String filePath) {
        this.filePath = filePath;
        this.activitiesList = new ArrayList<>();
        // Se quiser grafo não-dirigido, passe false. Para PERT/CPM, costuma ser true (direcionado).
        this.pertCpmGraph = new MapGraph<>(true);
    }

    /**
     * Executa o processo de importação e construção do grafo diretamente.
     */
    public void importAndBuildGraph() throws IOException {
        // Lê e armazena cada linha (já separada em colunas).
        List<String[]> csvLines = readCSV();
        if (csvLines.isEmpty()) {
            System.out.println("Nenhuma linha válida encontrada no CSV.");
            return;
        }

        // Cria vértices e atividades.
        addVertices(csvLines);

        // Cria arestas (dependências).
        addEdges(csvLines);

        System.out.println("Grafo PERT/CPM construído com sucesso.");
    }

    /**
     * Lê o arquivo CSV, ignorando a primeira linha (cabeçalho), e separando cada linha por vírgula.
     */
    private List<String[]> readCSV() throws IOException {
        List<String[]> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            boolean isFirstLine = true;
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                // Ignora linhas vazias
                if (line.isEmpty()) {
                    continue;
                }
                // Pula o cabeçalho (primeira linha)
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                // Divide pelos caracteres de vírgula
                // -1 faz o split manter colunas vazias, se houver
                String[] parts = line.split(",", -1);

                // Verifica se tem pelo menos 6 colunas
                if (parts.length < 6) {
                    System.out.println("Linha inválida (esperava >=6 colunas, recebeu "
                            + parts.length + "): " + line);
                    continue;
                }
                lines.add(parts);
            }
        }
        return lines;
    }

    /**
     * Cria as atividades e adiciona como vértices no grafo.
     */
    private void addVertices(List<String[]> csvLines) {
        for (String[] parts : csvLines) {
            // parts[0] -> activity ID
            // parts[1] -> descrição
            // parts[2] -> duração (double)
            // parts[3] -> unidade da duração (ex.: "dias")
            // parts[4] -> custo (double)
            // parts[5] -> string com predecessores (pode estar vazia ou separada por vírgulas)

            String actId = parts[0].trim();
            String actDescr = parts[1].trim();

            double duration = 0.0;
            try {
                duration = Double.parseDouble(parts[2].trim());
            } catch (NumberFormatException e) {
                System.out.println("Erro de parse na duração: " + parts[2]);
            }

            String durationUnit = parts[3].trim();

            double cost = 0.0;
            try {
                cost = Double.parseDouble(parts[4].trim());
            } catch (NumberFormatException e) {
                System.out.println("Erro de parse no custo: " + parts[4]);
            }

            String predecessorsStr = parts[5].trim();
            // Divide se tiver múltiplos predecessores separados por vírgula
            List<String> predecessors = new ArrayList<>();
            if (!predecessorsStr.isEmpty()) {
                // Remove aspas duplas extras (caso existam)
                predecessorsStr = predecessorsStr.replaceAll("\"", "");
                // Separa por vírgulas
                for (String pred : predecessorsStr.split(",")) {
                    if (!pred.trim().isEmpty()) {
                        predecessors.add(pred.trim());
                    }
                }
            }

            // Cria objeto da classe Activity
            Activity activity = new Activity(
                    actId,
                    actDescr,
                    duration,
                    durationUnit,
                    cost,
                    "USD",
                    predecessors
            );
            activitiesList.add(activity);

            // Adiciona ao grafo como vértice
            boolean inserted = pertCpmGraph.addVertex(actId);
            if (!inserted) {
                // Se já existir, pode lançar exceção ou exibir warning
                System.out.println("Vértice duplicado: " + actId);
            }
        }
    }

    /**
     * Cria as arestas (dependências) no grafo, de cada predecessor para a atividade.
     */
    private void addEdges(List<String[]> csvLines) {
        for (String[] parts : csvLines) {
            String actId = parts[0].trim();
            String predecessorsStr = parts[5].trim();

            if (!predecessorsStr.isEmpty()) {
                predecessorsStr = predecessorsStr.replaceAll("\"", "");
                for (String pred : predecessorsStr.split(",")) {
                    String predId = pred.trim();
                    if (predId.isEmpty()) continue;

                    // Se não existir o vértice do predecessor,
                    // significa que esse CSV pode estar mal organizado
                    if (!pertCpmGraph.validVertex(predId)) {
                        throw new IllegalArgumentException("Predecessor inexistente: "
                                + predId + " para atividade " + actId);
                    }

                    // Cria a aresta. Ex.: "Dependency" como etiqueta
                    boolean edgeInserted = pertCpmGraph.addEdge(predId, actId, "Dependency");
                    if (!edgeInserted) {
                        // Se a aresta já existir, só informa
                        System.out.println("Aresta já existe de " + predId + " para " + actId);
                    }
                }
            }
        }
    }

    /**
     * Retorna o grafo PERT/CPM construído.
     */
    public Graph<String, String> getPertCpmGraph() {
        return pertCpmGraph;
    }

    /**
     * Retorna a lista de atividades importadas do CSV.
     */
    public List<Activity> getActivitiesList() {
        return activitiesList;
    }
}
