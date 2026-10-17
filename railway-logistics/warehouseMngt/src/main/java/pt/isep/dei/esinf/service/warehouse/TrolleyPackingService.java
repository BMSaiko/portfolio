package pt.isep.dei.esinf.service.warehouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.isep.dei.esinf.domain.warehouse.Item;
import pt.isep.dei.esinf.domain.allocations.AllocationFragment;
import pt.isep.dei.esinf.domain.allocations.TrolleyAllocation;
import pt.isep.dei.esinf.domain.enums.PackingHeuristic;
import pt.isep.dei.esinf.domain.picking.PickingPlan;
import pt.isep.dei.esinf.domain.picking.Trolley;

public class TrolleyPackingService {
    Map<String, Double> skuWeightCache = new HashMap<>();

    /**
     * Pack allocation fragments into trolleys using the specified heuristic.
     *
     * @param allocations     List of allocation fragments from USEI02
     * @param trolleyCapacity Weight capacity of each trolley in kg
     * @param heuristic       Packing heuristic to use
     * @param items           Map of items to get weight information
     * @return PickingPlan with packed trolleys
     */
    public PickingPlan packAllocations(List<AllocationFragment> allocations, double trolleyCapacity, PackingHeuristic heuristic, Map<String, Item> items) {
        PickingPlan plan = new PickingPlan(heuristic, trolleyCapacity);

        if (items == null || items.isEmpty()) {
            plan.addNote("No items provided");
            return plan;
        }

        if (allocations == null || allocations.isEmpty()) {
            plan.addNote("No allocations provided");
            return plan;
        }

        if (trolleyCapacity <= 0) {
            plan.addNote("Trolley capacity is zero or negative, thus it cannot pack allocations.");
            return plan;
        }


        List<TrolleyAllocation> trolleyAllocations = calculateWeights(allocations, items);

        switch (heuristic) {
            case FIRST_FIT -> packWithFirstFit(trolleyAllocations, plan);
            case FIRST_FIT_DECREASING -> packWithFirstFitDecreasing(trolleyAllocations, plan);
            case BEST_FIT_DECREASING -> packWithBestFitDecreasing(trolleyAllocations, plan);
        }

        return plan;
    }

    /**
     * Calculate weights for each allocation fragment.
     * @param allocations  List of allocation fragments
     * @param items Map of items to get weight information
     * @return List of WeightedAllocation
     */
    private List<TrolleyAllocation> calculateWeights(List<AllocationFragment> allocations, Map<String, Item> items) {
        List<TrolleyAllocation> trolleyAllocations = new ArrayList<>();


        for (AllocationFragment allocation : allocations) {
            String sku = allocation.getSku();
            double unitWeight = skuWeightCache.computeIfAbsent(sku, _item -> {
                Item item = items.get(_item);

                if (item != null) return item.getUnitWeight();
            
                return 0.0;
            });

            double totalWeight = unitWeight * allocation.getQty();
            trolleyAllocations.add(new TrolleyAllocation(allocation, totalWeight));
        }

        return trolleyAllocations;
    }

    /**
     * First Fit: Place item in first available trolley where it fits.
     */
    private void packWithFirstFit(List<TrolleyAllocation> allocations, PickingPlan plan) {
        allocations.stream().forEach(allocation -> packAllocation(allocation, plan, false));
    }

    /**
     * First Fit Decreasing: Sort by weight descending, then use First Fit.
     */
    private void packWithFirstFitDecreasing(List<TrolleyAllocation> allocations, PickingPlan plan) {
        Collections.sort(allocations);

        allocations.stream().forEach(allocation -> packAllocation(allocation, plan, false));
    }

    /**
     * Best Fit Decreasing: Sort by weight descending, then place in trolley with tightest fit.
     */
    private void packWithBestFitDecreasing(List<TrolleyAllocation> allocations, PickingPlan plan) {
        Collections.sort(allocations);

        allocations.stream().forEach(allocation -> packAllocation(allocation, plan, true));
    }

    /**
     * Pack a single allocation into the best available trolley.
     *
     * @param weightedAlloc The allocation to pack
     * @param plan          The picking plan being built
     * @param useBestFit    Whether to use best fit (tightest) or first fit
     */
    private void packAllocation(TrolleyAllocation weightedAlloc, PickingPlan plan, boolean useBestFit) {
        List<Trolley> trolleys = plan.getTrolleys();
        double trolleyCapacity = plan.getTrolleyCapacity();


        double weightPerUnit = weightedAlloc.getWeight() / weightedAlloc.getAllocation().getQty();
        int remainingQty = weightedAlloc.getAllocation().getQty();
        AllocationFragment originalAlloc = weightedAlloc.getAllocation();

        while (remainingQty > 0) {
            double currentTrolleyWeight = remainingQty * weightPerUnit;
            Trolley trolley = findBestTrolley(trolleys, currentTrolleyWeight, useBestFit);

            if (trolley == null) {
                trolley = new Trolley(trolleys.size() + 1, trolleyCapacity);
                plan.addTrolley(trolley);
            }

            int maxUnitsFit = (int) Math.floor(trolley.getRemainingCapacity() / weightPerUnit);
            int unitsToFit = Math.min(remainingQty, maxUnitsFit);

            if (unitsToFit == 0) {
                plan.addNote(
                    String.format(
                        "Deferred allocation: Order %s Line %d - insufficient capacity",
                        originalAlloc.getOrderId(), originalAlloc.getLineNo()
                    )
                );
                break;
            }

            AllocationFragment fragment = createPartialAllocation(originalAlloc, unitsToFit);
            trolley.addAllocation(fragment, unitsToFit * weightPerUnit);
            remainingQty -= unitsToFit;

            if (remainingQty > 0) {
                plan.addNote(
                    String.format(
                        "Split allocation: Order %s Line %d - %d units in trolley %d, %d units remaining",
                        originalAlloc.getOrderId(), originalAlloc.getLineNo(), unitsToFit, trolley.getNumber(), remainingQty
                    )
                );
            }
        }
    }


    /**
     * Find the best trolley to place an allocation.
     *
     * @param trolleys   List of existing trolleys
     * @param weight     Weight to place
     * @param useBestFit If true, find tightest fit; if false, find first fit
     * @return Best trolley or null if none can accommodate
     */
    private Trolley findBestTrolley(List<Trolley> trolleys, double weight, boolean useBestFit) {
        Trolley bestTrolley = null;
        double bestRemainingCapacity = Double.MAX_VALUE;

        for (Trolley trolley : trolleys) {
            if (trolley.canFit(weight)) {
                if (!useBestFit) {
                    return trolley;
                } else {
                    double remainingAfterPlacement = trolley.getRemainingCapacity() - weight;
                    if (remainingAfterPlacement < bestRemainingCapacity) {
                        bestRemainingCapacity = remainingAfterPlacement;
                        bestTrolley = trolley;
                    }
                }
            }
        }

        return bestTrolley;
    }


    /**
     * Create a partial allocation from the original allocation.
     *
     * @param original Original allocation fragment
     * @param qty      Quantity for the partial allocation
     * @return New allocation fragment with the specified quantity
     */
    private AllocationFragment createPartialAllocation(AllocationFragment original, int qty) {
        return new AllocationFragment(
                original.getOrderId(),
                original.getLineNo(),
                original.getSku(),
                qty,
                original.getBoxId(),
                original.getAisle(),
                original.getBay()
        );
    }
}