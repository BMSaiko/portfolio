package com.example.production.Domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa uma atividade do projeto no PERT/CPM.
 */
public class Activity {
    private final String id;
    private final String description;
    private final double duration;
    private final String durationUnit;
    private final double cost;
    private final String costUnit;
    private final List<String> predecessors; // IDs das atividades predecessoras

    // Novos campos para PERT/CPM
    private double earliestStart;   // ES
    private double earliestFinish;  // EF
    private double latestStart;     // LS
    private double latestFinish;    // LF
    private double slack;           // Slack

    public Activity(String id, String description, double duration, String durationUnit, double cost, String costUnit, List<String> predecessors) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da atividade não pode ser nulo ou vazio.");
        }
        this.id = id.trim();
        this.description = description != null ? description.trim() : "";
        this.duration = duration;
        this.durationUnit = durationUnit != null ? durationUnit.trim() : "";
        this.cost = cost;
        this.costUnit = costUnit != null ? costUnit.trim() : "";
        this.predecessors = predecessors != null ? predecessors : new ArrayList<>();

        // Inicialização dos tempos
        this.earliestStart = 0.0;
        this.earliestFinish = 0.0;
        this.latestStart = 0.0;
        this.latestFinish = 0.0;
        this.slack = 0.0;
    }

    // Getters existentes...
    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getDuration() {
        return duration;
    }

    public String getDurationUnit() {
        return durationUnit;
    }

    public double getCost() {
        return cost;
    }

    public String getCostUnit() {
        return costUnit;
    }

    public List<String> getPredecessors() {
        return predecessors;
    }

    // Novos getters e setters
    public double getEarliestStart() {
        return earliestStart;
    }

    public void setEarliestStart(double earliestStart) {
        this.earliestStart = earliestStart;
    }

    public double getEarliestFinish() {
        return earliestFinish;
    }

    public void setEarliestFinish(double earliestFinish) {
        this.earliestFinish = earliestFinish;
    }

    public double getLatestStart() {
        return latestStart;
    }

    public void setLatestStart(double latestStart) {
        this.latestStart = latestStart;
    }

    public double getLatestFinish() {
        return latestFinish;
    }

    public void setLatestFinish(double latestFinish) {
        this.latestFinish = latestFinish;
    }

    public double getSlack() {
        return slack;
    }

    public void setSlack(double slack) {
        this.slack = slack;
    }

    @Override
    public String toString() {
        return String.format("Atividade[ID=%s, Descrição=%s, Duração=%.2f %s, Custo=%.2f %s, ES=%.2f, EF=%.2f, LS=%.2f, LF=%.2f, Slack=%.2f]",
                id, description, duration, durationUnit, cost, costUnit,
                earliestStart, earliestFinish, latestStart, latestFinish, slack);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Activity activity = (Activity) o;
        return id.equals(activity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
