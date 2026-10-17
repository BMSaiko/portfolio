package pt.isep.dei.esinf.domain.allocations;

public class TrolleyAllocation implements Comparable<TrolleyAllocation> {
    private final AllocationFragment allocation;
    private final double weight;

    public TrolleyAllocation(AllocationFragment allocation, double weight) {
        this.allocation = allocation;
        this.weight = weight;
    }

    public AllocationFragment getAllocation() {
        return allocation;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return String.format("%s | weight=%.2fkg", allocation.toString(), weight);
    }

    @Override
    public int compareTo(TrolleyAllocation other) {
        return Double.compare(other.getWeight(), this.getWeight());
    }
}