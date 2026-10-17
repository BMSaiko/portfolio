# USEI03 - Trolley Packing with Multiple Heuristics

## 4. Tests

**Test 1:** Check that empty allocations return empty plan

```java
@Test
@Name("Should return empty plan when no allocations provided")
void shouldReturnEmptyPlan_WhenNoAllocationsProvided() {
    double trolleyCapacity = 50.0;
    
    PickingPlan plan = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, items);
    
    assertEquals(0, plan.getTotalTrolleys());
    assertFalse(plan.getNotes().isEmpty());
    assertTrue(plan.getNotes().contains("No allocations provided"));
}
```

**Test 2:** Check that FFD uses fewer or equal trolleys than FF

```java
@Test
@Name("Should use fewer or equal trolleys with first fit decreasing compared to first fit")
void shouldUseFewerOrEqualTrolleys_WithFirstFitDecreasing_ComparedToFirstFit() {
    addAllocation("ORD001", 1, "SKU0001", 3, "BOX001", 1, 1); // 3 * 9.38 = 28.14kg
    addAllocation("ORD002", 1, "SKU0002", 2, "BOX002", 1, 2);  // 2 * 8.71 = 17.42kg
    addAllocation("ORD003", 1, "SKU0001", 2, "BOX003", 2, 1); // 2 * 9.38 = 18.76kg
    double trolleyCapacity = 50.0;
    
    PickingPlan planFFD = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT_DECREASING, items);
    PickingPlan planFF = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, items);
    
    assertTrue(planFFD.getTotalTrolleys() <= planFF.getTotalTrolleys());
}
```

**Test 3:** Check that allocation achieves 100% utilization when exact match

```java
@Test
@Name("Should achieve 100% utilization when allocation exactly matches trolley capacity")
void shouldAchieve100PercentUtilization_WhenAllocationExactlyMatchesTrolleyCapacity() {
    addAllocation("ORD001", 1, "SKU0007", 50, "BOX001", 1, 1); // 50 * 0.88 = 44kg
    double trolleyCapacity = 44.0;
    
    PickingPlan plan = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, items);
    
    assertEquals(1, plan.getTotalTrolleys());
    assertEquals(1, plan.getTotalAllocations());
    assertEquals(100.0, plan.getAverageUtilization());
    
    Trolley trolley = plan.getTrolleys().get(0);
    assertEquals(44.0, trolley.getCurrentWeight());
    assertEquals(0.0, trolley.getRemainingCapacity());
    assertTrue(plan.getNotes().isEmpty());
}
```

**Test 4:** Check that multiple allocations pack in same trolley when capacity allows

```java
@Test
@Name("Should pack multiple allocations in same trolley when capacity allows")
void shouldPackMultipleAllocations_InSameTrolley_WhenCapacityAllows() {
    addAllocation("ORD001", 1, "SKU0001", 2, "BOX001", 1, 1); // 2 * 9.38 = 18.76kg
    addAllocation("ORD001", 2, "SKU0007", 15, "BOX002", 1, 2); // 15 * 0.88 = 13.2kg
    addAllocation("ORD002", 1, "SKU0002", 2, "BOX003", 2, 1);  // 2 * 8.71 = 17.42kg
    // Total: 49.38kg (fits in 50kg trolley)
    double trolleyCapacity = 50.0;
    
    PickingPlan plan = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, items);
    
    assertEquals(1, plan.getTotalTrolleys());
    assertEquals(3, plan.getTotalAllocations());
    
    Trolley trolley = plan.getTrolleys().get(0);
    assertEquals(49.38, trolley.getCurrentWeight(), 0.01);
    assertEquals(0.62, trolley.getRemainingCapacity(), 0.01);
    assertEquals(3, trolley.getAllocations().size());
    assertEquals(0, plan.getNotes().size());
}
```

**Test 5:** Check that multiple trolleys are used when weight exceeds capacity

```java
@Test
@Name("Should use multiple trolleys when total weight exceeds single trolley capacity")
void shouldUseMultipleTrolleys_WhenTotalWeightExceedsSingleTrolleyCapacity() {
    addAllocation("ORD001", 1, "SKU0008", 3, "BOX001", 1, 1);  // 3 * 10.92 = 32.76kg
    addAllocation("ORD002", 1, "SKU0002", 3, "BOX002", 1, 2);  // 3 * 8.71 = 26.13kg
    // Total: 58.89kg (exceeds 50kg trolley)
    double trolleyCapacity = 50.0;
    
    PickingPlan plan = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, items);
    
    assertEquals(2, plan.getTotalTrolleys());
    assertEquals(2, plan.getTotalAllocations());
    
    List<Trolley> trolleys = plan.getTrolleys();
    assertEquals(32.76, trolleys.get(0).getCurrentWeight(), 0.01);
    assertEquals(26.13, trolleys.get(1).getCurrentWeight(), 0.01);
}
```

**Test 6:** Check that zero capacity returns empty plan with note

```java
@Test
@Name("Should return empty plan when trolley capacity is zero")
void shouldReturnEmptyPlan_WhenTrolleyCapacityIsZero() {
    addAllocation("ORD001", 1, "SKU0001", 5, "BOX001", 1, 1); // 5 * 9.38 = 46.9kg
    double trolleyCapacity = 0.0;
    
    PickingPlan plan = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, items);
    
    assertNotNull(plan);
    assertEquals(0, plan.getTotalTrolleys());
    assertEquals(0, plan.getTotalWeight());
    assertFalse(plan.getNotes().isEmpty());
    assertTrue(plan.getNotes().contains("Trolley capacity is zero or negative, thus it cannot pack allocations."));
}
```

**Test 7:** Check that BFD achieves optimal packing efficiency

```java
@Test
@Name("Should achieve optimal packing efficiency with best fit decreasing")
void shouldAchieveOptimalPackingEfficiency_WithBestFitDecreasing() {
    addAllocation("ORD001", 1, "SKU0002", 3, "BOX001", 1, 1);  // 3 * 8.71 = 26.13kg
    addAllocation("ORD002", 1, "SKU0002", 3, "BOX002", 1, 2);  // 3 * 8.71 = 26.13kg
    addAllocation("ORD003", 1, "SKU0001", 2, "BOX003", 2, 1);  // 2 * 9.38 = 18.76kg
    addAllocation("ORD004", 1, "SKU0001", 2, "BOX004", 2, 2);  // 2 * 9.38 = 18.76kg
    addAllocation("ORD005", 1, "SKU0007", 5, "BOX005", 3, 1);  // 5 * 0.88 = 4.4kg
    double trolleyCapacity = 50.0;
    
    PickingPlan planBFD = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.BEST_FIT_DECREASING, items);
    
    assertEquals(3, planBFD.getTotalTrolleys());
}
```

## 5. Construction (Implementation)

### 5.1 TrolleyPackingService Class

```java
public class TrolleyPackingService {
    Map<String, Double> skuWeightCache = new HashMap<>();
    
    public PickingPlan packAllocations(List<AllocationFragment> allocations, double trolleyCapacity, 
                                       PackingHeuristic heuristic, Map<String, Item> items) {
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
        
        List<WeightedAllocation> weightedAllocations = calculateWeights(allocations, items);
        
        switch (heuristic) {
            case FIRST_FIT -> packWithFirstFit(weightedAllocations, plan);
            case FIRST_FIT_DECREASING -> packWithFirstFitDecreasing(weightedAllocations, plan);
            case BEST_FIT_DECREASING -> packWithBestFitDecreasing(weightedAllocations, plan);
        }
        
        return plan;
    }
    
    private List<WeightedAllocation> calculateWeights(List<AllocationFragment> allocations, Map<String, Item> items) {
        List<WeightedAllocation> weightedAllocations = new ArrayList<>();
        
        for (AllocationFragment allocation : allocations) {
            String sku = allocation.getSku();
            double unitWeight = skuWeightCache.computeIfAbsent(sku, k -> {
                Item item = items.get(k);
                return item != null ? item.getUnitWeight() : 0.0;
            });
            
            double totalWeight = unitWeight * allocation.getQty();
            weightedAllocations.add(new WeightedAllocation(allocation, totalWeight));
        }
        
        return weightedAllocations;
    }
    
    private void packWithFirstFit(List<WeightedAllocation> allocations, PickingPlan plan) {
        allocations.stream().forEach(allocation -> packAllocation(allocation, plan, false));
    }
    
    private void packWithFirstFitDecreasing(List<WeightedAllocation> allocations, PickingPlan plan) {
        Collections.sort(allocations);
        allocations.stream().forEach(allocation -> packAllocation(allocation, plan, false));
    }
    
    private void packWithBestFitDecreasing(List<WeightedAllocation> allocations, PickingPlan plan) {
        Collections.sort(allocations);
        allocations.stream().forEach(allocation -> packAllocation(allocation, plan, true));
    }
}
```

### 5.2 WeightedAllocation Class

```java
public class WeightedAllocation implements Comparable<WeightedAllocation> {
    private final AllocationFragment allocation;
    private final double weight;
    
    public WeightedAllocation(AllocationFragment allocation, double weight) {
        this.allocation = allocation;
        this.weight = weight;
    }
    
    @Override
    public int compareTo(WeightedAllocation other) {
        return Double.compare(other.weight, this.weight); // Descending order
    }
}
```

### 5.3 Trolley Class

```java
public class Trolley {
    private static int nextId = 1;
    private final int id;
    private final double capacity;
    private double currentWeight;
    private final List<WeightedAllocation> allocations;
    
    public Trolley(double capacity) {
        this.id = nextId++;
        this.capacity = capacity;
        this.currentWeight = 0.0;
        this.allocations = new ArrayList<>();
    }
    
    public boolean addAllocation(WeightedAllocation allocation) {
        if (currentWeight + allocation.getWeight() > capacity) {
            return false;
        }
        allocations.add(allocation);
        currentWeight += allocation.getWeight();
        return true;
    }
    
    public double getRemainingCapacity() {
        return capacity - currentWeight;
    }
    
    public double getUtilization() {
        return (currentWeight / capacity) * 100.0;
    }
}
```

### 5.4 PickingPlan Class

```java
public class PickingPlan {
    private final PackingHeuristic heuristic;
    private final double trolleyCapacity;
    private final List<Trolley> trolleys;
    private final List<String> notes;
    
    public double getAverageUtilization() {
        if (trolleys.isEmpty()) return 0.0;
        return trolleys.stream()
                .mapToDouble(Trolley::getUtilization)
                .average()
                .orElse(0.0);
    }
    
    public double getTotalWeight() {
        return trolleys.stream()
                .mapToDouble(Trolley::getCurrentWeight)
                .sum();
    }
}
```

## 6. Integration and Demo

The trolley packing functionality was integrated with the order allocation system. A demo shows:

- Loading allocation results from USEI02
- Selecting trolley capacity and packing heuristic
- Calculating weights for all allocations
- Packing allocations using FF, FFD, or BFD algorithms
- Displaying trolley assignments and utilization statistics
- Comparing results across different heuristics

## 7. Observations

The implementation successfully handles:
- Three bin packing heuristics with different performance characteristics
- Weight calculation with SKU weight caching for performance
- Capacity constraint enforcement
- Allocation splitting for items exceeding trolley capacity
- Utilization statistics and reporting

FFD and BFD heuristics generally produce better packing efficiency than FF, though the improvement depends on the specific allocation weights. The system provides flexibility to choose the appropriate heuristic based on warehouse operations requirements.

Future enhancements could include:
- Additional heuristics (Next Fit, Worst Fit)
- Volume-based packing constraints
- Integration with USEI04 for picking path optimization
- Real-time trolley tracking and load balancing
