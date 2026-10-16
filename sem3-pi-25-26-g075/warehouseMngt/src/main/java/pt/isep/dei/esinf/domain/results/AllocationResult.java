package pt.isep.dei.esinf.domain.results;

import pt.isep.dei.esinf.domain.LineEligibility;
import pt.isep.dei.esinf.domain.allocations.AllocationFragment;

import java.util.List;

public class AllocationResult {
    private final List<LineEligibility> eligibilities;
    private final List<AllocationFragment> allocations;

    public AllocationResult(List<LineEligibility> eligibilities,
                            List<AllocationFragment> allocations) {
        this.eligibilities = eligibilities;
        this.allocations = allocations;
    }

    public List<LineEligibility> getEligibilities() {
        return eligibilities;
    }

    public List<AllocationFragment> getAllocations() {
        return allocations;
    }
}