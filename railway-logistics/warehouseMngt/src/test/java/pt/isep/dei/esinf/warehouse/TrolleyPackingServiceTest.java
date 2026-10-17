package pt.isep.dei.esinf.warehouse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jdk.jfr.Name;
import pt.isep.dei.esinf.domain.warehouse.Item;
import pt.isep.dei.esinf.domain.allocations.AllocationFragment;
import pt.isep.dei.esinf.domain.enums.PackingHeuristic;
import pt.isep.dei.esinf.domain.picking.PickingPlan;
import pt.isep.dei.esinf.domain.picking.Trolley;
import pt.isep.dei.esinf.service.data.DataInitializationService;
import pt.isep.dei.esinf.service.warehouse.TrolleyPackingService;


/**
 * Unit tests for TrolleyPackingService.
 * Covers various packing heuristics and edge cases.
 */
class TrolleyPackingServiceTest {

    private TrolleyPackingService packingService;
    private Map<String, Item> items;
    private List<AllocationFragment> allocations;

    @BeforeEach
    void setUp() {
        packingService = new TrolleyPackingService();
        items = loadItemsFromCsv();
        allocations = new ArrayList<>();
    }

    private Map<String, Item> loadItemsFromCsv() {
        try {
            DataInitializationService dataInitService = new DataInitializationService();
            dataInitService.initializeWarehouse();

            return dataInitService.getCsvImportService().getItems();
        } catch (Exception e) {
            System.err.println("Failed to load items from CSV: " + e.getMessage());
            System.exit(1);
        }
        return null;
    }


    private void addAllocation(String orderId, int lineNo, String sku, int qty, String boxId, int aisle, int bay) {
        allocations.add(new AllocationFragment(orderId, lineNo, sku, qty, boxId, aisle, bay));
    }

    @Test
    @Name("Should return empty plan when no allocations provided")
    void shouldReturnEmptyPlan_WhenNoAllocationsProvided() {
        double trolleyCapacity = 50.0;

        PickingPlan plan = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, items);

        assertEquals(0, plan.getTotalTrolleys());
        assertFalse(plan.getNotes().isEmpty());
        assertTrue(plan.getNotes().contains("No allocations provided"));
    }

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

    @Test
    @Name("Should demonstrate different trolley counts across heuristics")
    void shouldDemonstrateDifferentTrolleyCount_AcrossHeuristics() {
        addAllocation("ORD001", 1, "SKU0002", 3, "BOX001", 1, 1);  // 3 * 8.71 = 26.13kg
        addAllocation("ORD002", 1, "SKU0001", 3, "BOX002", 1, 2); // 3 * 9.38 = 28.14kg  
        addAllocation("ORD003", 1, "SKU0001", 2, "BOX003", 2, 1); // 2 * 9.38 = 18.76kg
        addAllocation("ORD004", 1, "SKU0001", 3, "BOX004", 2, 2); // 3 * 9.38 = 28.14kg
        double trolleyCapacity = 50.0;


        PickingPlan planFF = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, items);
        PickingPlan planFFD = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT_DECREASING, items);
        PickingPlan planBFD = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.BEST_FIT_DECREASING, items);

        assertTrue(planFFD.getTotalTrolleys() <= planFF.getTotalTrolleys());
        assertTrue(planBFD.getTotalTrolleys() <= planFF.getTotalTrolleys());
    }

    @Test
    @Name("Should pack single allocation using first fit algorithm")
    void shouldPackSingleAllocation_UsingFirstFitAlgorithm() {
        addAllocation("ORD001", 1, "SKU0001", 10, "BOX001", 1, 1); // 10 * 9.38 = 93.8kg total
        double trolleyCapacity = 100.0;


        PickingPlan plan = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, items);


        assertNotNull(plan);
        assertEquals(1, plan.getTotalTrolleys());
        assertEquals(PackingHeuristic.FIRST_FIT, plan.getHeuristic());
        assertEquals(trolleyCapacity, plan.getTrolleyCapacity());
    }


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

        // Verify trolley weights
        List<Trolley> trolleys = plan.getTrolleys();
        assertEquals(32.76, trolleys.get(0).getCurrentWeight(), 0.01);
        assertEquals(26.13, trolleys.get(1).getCurrentWeight(), 0.01);
    }


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
        assertEquals(1, plan.getNotes().size());

        assertTrue(plan.getNotes().contains("Trolley capacity is zero or negative, thus it cannot pack allocations."));
    }

    @Test
    @Name("Should handle negative trolley capacity gracefully")
    void shouldHandleNegativeTrolleyCapacity_Gracefully() {
        addAllocation("ORD001", 1, "SKU0001", 5, "BOX001", 1, 1); // 5 * 9.38 = 46.9kg
        double trolleyCapacity = -10.0;

        PickingPlan plan = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, items);

        assertNotNull(plan);

        assertEquals(0, plan.getTotalTrolleys());
        assertEquals(0, plan.getTotalWeight());

        assertFalse(plan.getNotes().isEmpty());
        assertEquals(1, plan.getNotes().size());

        assertTrue(plan.getNotes().contains("Trolley capacity is zero or negative, thus it cannot pack allocations."));
    }


    @Test
    @Name("Should handle zero quantity allocations properly")
    void shouldHandleZeroQuantityAllocations_Properly() {
        addAllocation("ORD001", 1, "SKU0001", 0, "BOX001", 1, 1); // 0 * 9.38 = 0kg
        addAllocation("ORD002", 1, "SKU0002", 5, "BOX002", 1, 2); // 5 * 8.71 = 43.55kg
        double trolleyCapacity = 50.0;

        PickingPlan plan = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, items);

        assertEquals(1, plan.getTotalTrolleys());
        assertEquals(1, plan.getTotalAllocations()); // Both allocations should be processed

        Trolley trolley = plan.getTrolleys().get(0);
        assertEquals(43.55, trolley.getCurrentWeight(), 0.01); // Only SKU0002 contributes weight
    }


    @Test
    @Name("Should produce same results across all heuristics when allocation perfectly fits trolley")
    void shouldProduceSameResults_AcrossAllHeuristics_WhenAllocationPerfectlyFitsTrolley() {
        addAllocation("ORD001", 1, "SKU0007", 50, "BOX001", 1, 1); // 50 * 0.88 = 44kg (fits exactly in 44kg trolley)
        double trolleyCapacity = 44.0;

        PickingPlan planFF = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, items);
        PickingPlan planFFD = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT_DECREASING, items);
        PickingPlan planBFD = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.BEST_FIT_DECREASING, items);

        assertEquals(1, planFF.getTotalTrolleys());
        assertEquals(1, planFFD.getTotalTrolleys());
        assertEquals(1, planBFD.getTotalTrolleys());

        assertEquals(100.0, planFF.getAverageUtilization());
        assertEquals(100.0, planFFD.getAverageUtilization());
        assertEquals(100.0, planBFD.getAverageUtilization());
    }

    @Test
    @Name("Should achieve optimal packing efficiency with best fit decreasing")
    void shouldAchieveOptimalPackingEfficiency_WithBestFitDecreasing() {
        addAllocation("ORD001", 1, "SKU0002", 3, "BOX001", 1, 1);  // 3 * 8.71 = 26.13kg
        addAllocation("ORD002", 1, "SKU0002", 3, "BOX002", 1, 2);  // 3 * 8.71 = 26.13kg
        addAllocation("ORD003", 1, "SKU0001", 2, "BOX003", 2, 1);  // 2 * 9.38 = 18.76kg
        addAllocation("ORD004", 1, "SKU0001", 2, "BOX004", 2, 2);  // 2 * 9.38 = 18.76kg
        addAllocation("ORD005", 1, "SKU0007", 5, "BOX005", 3, 1);  // 5 * 0.88 = 4.4kg
        double trolleyCapacity = 45.0;

        PickingPlan planBFD = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.BEST_FIT_DECREASING, items);

        // Should pack efficiently
        assertEquals(3, planBFD.getTotalTrolleys());
    }

    @Test
    @Name("Should demonstrate sorting effect of first fit decreasing algorithm")
    void shouldDemonstrateSortingEffect_OfFirstFitDecreasingAlgorithm() {
        addAllocation("ORD001", 1, "SKU0007", 5, "BOX001", 1, 1);   // 5 * 0.88 = 4.4kg (lightest)
        addAllocation("ORD002", 1, "SKU0001", 2, "BOX002", 1, 2);  // 2 * 9.38 = 18.76kg
        addAllocation("ORD003", 1, "SKU0002", 2, "BOX003", 2, 1);   // 2 * 8.71 = 17.42kg  
        addAllocation("ORD004", 1, "SKU0008", 2, "BOX004", 2, 2);   // 2 * 10.92 = 21.84kg (heaviest)
        double trolleyCapacity = 50.0;

        PickingPlan planFFD = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT_DECREASING, items);
        PickingPlan planFF = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, items);

        assertTrue(planFFD.getTotalTrolleys() <= planFF.getTotalTrolleys());
        assertTrue(planFFD.getAverageUtilization() >= planFF.getAverageUtilization() - 1.0); // Allow small tolerance
    }


    @Test
    @Name("Should achieve high average utilization with heavy allocations")
    void shouldAchieveHighAverageUtilization_WithHeavyAllocations() {
        addAllocation("ORD001", 1, "SKU0008", 4, "BOX001", 1, 1); // 4 * 10.92 = 43.68kg (~87% utilization)
        addAllocation("ORD002", 1, "SKU0001", 4, "BOX002", 1, 2); // 4 * 9.38 = 37.52kg (~75% utilization)
        double trolleyCapacity = 50.0;

        PickingPlan plan = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.BEST_FIT_DECREASING, items);

        assertEquals(2, plan.getTotalTrolleys());
        assertTrue(plan.getAverageUtilization() > 80.0); // Good average utilization expected
    }

    @Test
    @Name("Should show low utilization with large trolley capacity and light items")
    void shouldShowLowUtilization_WithLargeTrolleyCapacityAndLightItems() {
        for (int i = 1; i <= 5; i++) {
            addAllocation("ORD00" + i, 1, "SKU0007", 5, "BOX00" + i, 1, i); // 5 * 0.88 = 4.4kg each
        }
        double trolleyCapacity = 100.0; // Large trolley

        PickingPlan plan = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, items);

        assertEquals(1, plan.getTotalTrolleys());
        assertEquals(22.0, plan.getAverageUtilization()); // 22kg / 100kg = 22%
    }

    @Test
    @Name("Should handle large number of allocations efficiently")
    void shouldHandleLargeNumberOfAllocations_Efficiently() {
        for (int i = 1; i <= 20; i++) {
            addAllocation("ORD" + String.format("%03d", i), 1, "SKU0007", 3, "BOX" + String.format("%03d", i), 1, 1); // 3 * 0.88 = 2.64kg each
        }
        double trolleyCapacity = 50.0;

        PickingPlan plan = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, items);

        assertTrue(plan.getTotalTrolleys() >= 1);
        assertEquals(20, plan.getTotalAllocations());

        // Verify no trolley is overloaded
        for (Trolley trolley : plan.getTrolleys()) {
            assertTrue(trolley.getCurrentWeight() <= trolleyCapacity + 0.1);
        }
    }

    @Test
    @Name("Should pack multiple orders efficiently in single trolley")
    void shouldPackMultipleOrders_EfficientlyInSingleTrolley() {
        addAllocation("ORD001", 1, "SKU0002", 4, "BOX001", 1, 1);  // 4 * 8.71 = 34.84kg
        addAllocation("ORD002", 1, "SKU0007", 10, "BOX002", 1, 2); // 10 * 0.88 = 8.8kg
        addAllocation("ORD003", 1, "SKU0005", 1, "BOX003", 2, 1);  // 1 * 6.05 = 6.05kg
        // Total: 49.69kg (fits in 50kg trolley)
        double trolleyCapacity = 50.0;

        PickingPlan plan = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, items);

        assertEquals(1, plan.getTotalTrolleys());
        assertEquals(3, plan.getTotalAllocations());
        assertEquals(99.38, plan.getAverageUtilization(), 1.0); // ~99.38% utilization
    }

    @Test
    @Name("Should preserve all allocation fragment information during packing")
    void shouldPreserveAllAllocationFragmentInformation_DuringPacking() {
        addAllocation("ORD001", 5, "SKU0002", 6, "BOX123", 3, 7); // 6 * 8.71 = 52.26kg
        double trolleyCapacity = 60.0;

        PickingPlan plan = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, items);

        assertEquals(1, plan.getTotalTrolleys());
        Trolley trolley = plan.getTrolleys().get(0);
        assertEquals(1, trolley.getAllocations().size());

        AllocationFragment allocation = trolley.getAllocations().get(0);
        assertEquals("ORD001", allocation.getOrderId());
        assertEquals(5, allocation.getLineNo());
        assertEquals("SKU0002", allocation.getSku());
        assertEquals(6, allocation.getQty());
        assertEquals("BOX123", allocation.getBoxId());
        assertEquals(3, allocation.getAisle());
        assertEquals(7, allocation.getBay());
    }

    @Test
    @Name("Should organize allocations from same aisle efficiently")
    void shouldOrganizeAllocationsFromSameAisle_Efficiently() {
        addAllocation("ORD001", 1, "SKU0001", 3, "BOX001", 1, 1); // Aisle 1, Bay 1 - 3 * 9.38 = 28.14kg
        addAllocation("ORD001", 2, "SKU0007", 10, "BOX002", 1, 2); // Aisle 1, Bay 2 - 10 * 0.88 = 8.8kg
        addAllocation("ORD001", 3, "SKU0005", 2, "BOX003", 1, 3); // Aisle 1, Bay 3 - 2 * 6.05 = 12.1kg
        // Total: 49.04kg (fits in 50kg trolley)
        double trolleyCapacity = 50.0;

        PickingPlan plan = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, items);

        assertEquals(1, plan.getTotalTrolleys());
        Trolley trolley = plan.getTrolleys().get(0);

        // Verify all allocations from same aisle
        for (AllocationFragment allocation : trolley.getAllocations()) {
            assertEquals(1, allocation.getAisle());
        }
    }

    
    @Test
    @Name("Should handle allocation heavier than trolley capacity")
    void shouldHandleAllocationHeavierThanTrolleyCapacity() {
        addAllocation("ORD001", 1, "SKU0002", 1, "BOX001", 1, 1); // 1 * 8.71 = 8.71kg
        double trolleyCapacity = 1.0; // Much smaller than item weight

        PickingPlan plan = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, items);

        assertNotNull(plan);
        assertTrue(plan.getTotalTrolleys() >= 1);
    }

    @Test
    @Name("Should handle identical allocations in different boxes separately")
    void shouldHandleIdenticalAllocationsInDifferentBoxes_Separately() {
        addAllocation("ORD001", 1, "SKU0001", 5, "BOX001", 1, 1); // 5 * 9.38 = 46.9kg
        addAllocation("ORD001", 1, "SKU0001", 5, "BOX002", 1, 2); // 5 * 9.38 = 46.9kg
        addAllocation("ORD001", 1, "SKU0001", 5, "BOX003", 2, 1); // 5 * 9.38 = 46.9kg
        double trolleyCapacity = 50.0;

        PickingPlan plan = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, items);

        assertEquals(3, plan.getTotalTrolleys()); // Each allocation needs its own trolley as each weighs ~47kg
        assertEquals(3, plan.getTotalAllocations());

        // Verify all three boxes are represented
        List<String> boxIds = plan.getTrolleys().stream()
                .flatMap(trolley -> trolley.getAllocations().stream())
                .map(AllocationFragment::getBoxId)
                .toList();
        assertTrue(boxIds.contains("BOX001"));
        assertTrue(boxIds.contains("BOX002"));
        assertTrue(boxIds.contains("BOX003"));
    }


    @Test
    @Name("Should generate notes for oversized allocations exceeding capacity")
    void shouldGenerateNotesForOversizedAllocations_ExceedingCapacity() {
        addAllocation("ORD001", 1, "SKU0011", 10, "BOX001", 1, 1); // 10 * 11.41 = 114.1kg
        double trolleyCapacity = 50.0;

        PickingPlan plan = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, items);

        assertNotNull(plan);
        assertTrue(plan.getTotalTrolleys() >= 1);
        assertFalse(plan.getNotes().isEmpty()); // Should generate notes for oversized allocation
    }

    @Test
    @Name("Should pack many light items with expected trolley distribution")
    void shouldPackManyLightItems_WithExpectedTrolleyDistribution() {
        for (int i = 1; i <= 50; i++) {
            addAllocation("ORD" + String.format("%03d", i), 1, "SKU0010", 10, "BOX" + String.format("%03d", i), 1, 1); // 10 * 0.4 = 4kg each
        }
        double trolleyCapacity = 50.0;

        PickingPlan plan = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.BEST_FIT_DECREASING, items);

        assertEquals(5, plan.getTotalTrolleys());
        assertEquals(50, plan.getTotalAllocations());

        // Should achieve high utilization with light items
        assertEquals(80.0, plan.getAverageUtilization());
    }

    @Test
    @Name("Should handle decimal weights with high precision")
    void shouldHandleDecimalWeights_WithHighPrecision() {
        addAllocation("ORD001", 1, "SKU0001", 3, "BOX001", 1, 1); // 3 * 9.38 = 28.14kg
        addAllocation("ORD002", 1, "SKU0010", 55, "BOX002", 1, 2); // 55 * 0.4 = 22kg
        // Total: 50.14kg
        double trolleyCapacity = 50.14;

        PickingPlan plan = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, items);

        assertEquals(1, plan.getTotalTrolleys());

        Trolley trolley = plan.getTrolleys().get(0);
        assertEquals(50.14, trolley.getCurrentWeight(), 0.001); // Precise weight check
        assertEquals(0.0, trolley.getRemainingCapacity(), 0.001);
    }


    @Test
    @Name("Should simulate realistic warehouse picking route with mixed items")
    void shouldSimulateRealisticWarehousePickingRoute_WithMixedItems() {
        // Aisle 1 - Electronics
        addAllocation("ORD001", 1, "SKU0002", 2, "BOX001", 1, 1);   // 2 * 8.71 = 17.42kg
        addAllocation("ORD001", 2, "SKU0007", 5, "BOX002", 1, 5);   // 5 * 0.88 = 4.4kg

        // Aisle 2 - Tools  
        addAllocation("ORD001", 3, "SKU0003", 1, "BOX003", 2, 2);   // 1 * 6.51 = 6.51kg
        addAllocation("ORD002", 1, "SKU0001", 1, "BOX004", 2, 8);   // 1 * 9.38 = 9.38kg

        // Aisle 3 - Books
        addAllocation("ORD002", 2, "SKU0005", 2, "BOX005", 3, 1);   // 2 * 6.05 = 12.1kg
        addAllocation("ORD003", 1, "SKU0008", 1, "BOX006", 3, 3);   // 1 * 10.92 = 10.92kg

        double trolleyCapacity = 30.0; // Smaller trolley for realistic scenario

        PickingPlan plan = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.BEST_FIT_DECREASING, items);

        assertTrue(plan.getTotalTrolleys() >= 2);
        assertEquals(6, plan.getTotalAllocations());

        // Verify total weight is preserved
        double totalWeight = plan.getTrolleys().stream().mapToDouble(Trolley::getCurrentWeight).sum();
        assertEquals(60.73, totalWeight, 0.01);
    }


    @Test
    @Name("Should handle null or empty items map gracefully")
    void shouldHandleNullOrEmptyItemsMap_Gracefully() {
        addAllocation("ORD001", 1, "SKU0001", 5, "BOX001", 1, 1); // 5 * 9.38 = 46.9kg
        double trolleyCapacity = 50.0;

        PickingPlan planWithNullItems = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, null);
        PickingPlan planWithEmptyItems = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, new HashMap<>());

        assertNotNull(planWithNullItems);
        assertNotNull(planWithEmptyItems);

        assertEquals(0, planWithNullItems.getTotalTrolleys());
        assertEquals(0.0, planWithNullItems.getTotalAllocations());
        assertEquals(0.0, planWithNullItems.getTotalWeight());

        assertEquals(0, planWithEmptyItems.getTotalTrolleys());
        assertEquals(0.0, planWithEmptyItems.getTotalAllocations());
        assertEquals(0.0, planWithEmptyItems.getTotalWeight());


        assertFalse(planWithNullItems.getNotes().isEmpty());
        assertFalse(planWithEmptyItems.getNotes().isEmpty());

        assertTrue(planWithNullItems.getNotes().contains("No items provided"));
        assertTrue(planWithEmptyItems.getNotes().contains("No items provided"));
    }


    @Test
    @Name("Should maintain consistency across all packing heuristics")
    void shouldMaintainConsistency_AcrossAllPackingHeuristics() {
        addAllocation("ORD001", 1, "SKU0003", 3, "BOX001", 1, 1);  // 3 * 6.51 = 19.53kg
        addAllocation("ORD001", 2, "SKU0002", 3, "BOX002", 1, 2);  // 3 * 8.71 = 26.13kg  
        addAllocation("ORD002", 1, "SKU0001", 3, "BOX003", 2, 1); // 3 * 9.38 = 28.14kg
        addAllocation("ORD002", 2, "SKU0010", 15, "BOX004", 2, 2); // 15 * 0.4 = 6kg
        // Total: 79.8kg

        double trolleyCapacity = 50.0;

        PickingPlan planFF = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT, items);
        PickingPlan planFFD = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.FIRST_FIT_DECREASING, items);
        PickingPlan planBFD = packingService.packAllocations(allocations, trolleyCapacity, PackingHeuristic.BEST_FIT_DECREASING, items);

        assertNotNull(planFF);
        assertNotNull(planFFD);
        assertNotNull(planBFD);

        assertEquals(4, planFF.getTotalAllocations());
        assertEquals(4, planFFD.getTotalAllocations());
        assertEquals(4, planBFD.getTotalAllocations());

        assertTrue(planFF.getTotalTrolleys() >= 2);
        assertTrue(planFFD.getTotalTrolleys() >= 2);
        assertTrue(planBFD.getTotalTrolleys() >= 2);

        assertEquals(PackingHeuristic.FIRST_FIT, planFF.getHeuristic());
        assertEquals(PackingHeuristic.FIRST_FIT_DECREASING, planFFD.getHeuristic());
        assertEquals(PackingHeuristic.BEST_FIT_DECREASING, planBFD.getHeuristic());
    }
}