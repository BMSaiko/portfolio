package pt.isep.dei.esinf.domain.picking;

import pt.isep.dei.esinf.domain.enums.PackingHeuristic;

import java.util.ArrayList;
import java.util.List;

public class PickingPlan {
    private final PackingHeuristic heuristic;
    private final double trolleyCapacity;
    private final List<Trolley> trolleys;
    private final List<String> notes;

    public PickingPlan(PackingHeuristic heuristic, double trolleyCapacity) {
        this.heuristic = heuristic;
        this.trolleyCapacity = trolleyCapacity;
        this.trolleys = new ArrayList<>();
        this.notes = new ArrayList<>();
    }

    public PackingHeuristic getHeuristic() {
        return heuristic;
    }

    public double getTrolleyCapacity() {
        return trolleyCapacity;
    }

    public List<Trolley> getTrolleys() {
        return new ArrayList<>(trolleys);
    }

    public List<String> getNotes() {
        return new ArrayList<>(notes);
    }

    public void addTrolley(Trolley trolley) {
        trolleys.add(trolley);
    }

    public void addNote(String note) {
        notes.add(note);
    }

    public int getTotalTrolleys() {
        return trolleys.size();
    }

    public double getAverageUtilization() {
        if (trolleys.isEmpty()) return 0.0;
        return trolleys
                .stream()
                .mapToDouble(Trolley::getUtilization)
                .average()
                .orElse(0.0);
    }

    public int getTotalAllocations() {
        return trolleys
                .stream()
                .mapToInt(t -> t.getAllocations().size())
                .sum();
    }

    public double getTotalWeight() {
        return trolleys
                .stream()
                .mapToDouble(Trolley::getCurrentWeight)
                .sum();
    }

    @Override
    public String toString() {
        return String.format("PickingPlan [%s]: %d trolleys (%.1fkg each), %.1f%% avg utilization, %d total allocations",
                heuristic, getTotalTrolleys(), trolleyCapacity, getAverageUtilization(), getTotalAllocations());
    }
}