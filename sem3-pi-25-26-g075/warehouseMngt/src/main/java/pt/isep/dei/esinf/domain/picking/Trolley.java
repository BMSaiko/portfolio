package pt.isep.dei.esinf.domain.picking;

import pt.isep.dei.esinf.domain.allocations.AllocationFragment;

import java.util.ArrayList;
import java.util.List;

public class Trolley {
    private final int number;
    private final double maxWeight;
    private final List<AllocationFragment> allocations;
    private double currentWeight;

    public Trolley(int trolleyNumber, double capacityWeight) {
        this.number = trolleyNumber;
        this.maxWeight = capacityWeight;
        this.allocations = new ArrayList<>();
        this.currentWeight = 0.0;
    }

    public int getNumber() {
        return number;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public List<AllocationFragment> getAllocations() {
        return new ArrayList<>(allocations);
    }

    public double getCurrentWeight() {
        return currentWeight;
    }

    public double getRemainingCapacity() {
        return maxWeight - currentWeight;
    }

    public double getUtilization() {
        return maxWeight > 0 ? (currentWeight / maxWeight) * 100 : 0;
    }

    public boolean canFit(double weight) {
        return weight <= getRemainingCapacity();
    }

    public void addAllocation(AllocationFragment allocation, double weight) {
        allocations.add(allocation);
        currentWeight += weight;
    }

    public boolean isEmpty() {
        return allocations.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("Trolley #%d: %.1f/%.1fkg (%.1f%% full) - %d allocations",
                number, currentWeight, maxWeight, getUtilization(), allocations.size());
    }
}