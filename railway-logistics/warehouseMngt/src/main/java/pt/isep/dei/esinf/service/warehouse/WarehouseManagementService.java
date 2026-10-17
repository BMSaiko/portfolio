package pt.isep.dei.esinf.service.warehouse;

import pt.isep.dei.esinf.domain.*;
import pt.isep.dei.esinf.domain.allocations.AllocationFragment;
import pt.isep.dei.esinf.domain.enums.EligibilityMode;
import pt.isep.dei.esinf.domain.results.AllocationResult;
import pt.isep.dei.esinf.domain.enums.AllocationStatus;
import pt.isep.dei.esinf.domain.enums.PackingHeuristic;
import pt.isep.dei.esinf.domain.picking.PickingPlan;
import pt.isep.dei.esinf.domain.picking.Trolley;
import pt.isep.dei.esinf.domain.warehouse.*;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Service class responsible for warehouse management operations
 */
public class WarehouseManagementService {

    private final OrderAllocationService orderAllocationService;
    private final TrolleyPackingService trolleyPackingService;

    public WarehouseManagementService() {
        this.orderAllocationService = new OrderAllocationService();
        this.trolleyPackingService = new TrolleyPackingService();
    }

    /**
     * Gets bay contents information for display
     */
    public void displayBayContents(Warehouse warehouse) {
        System.out.println("\n=== Bay Contents ===");
        for (Bay bay : warehouse.getAllBays()) {
            if (!bay.isEmpty()) {
                System.out.println("\nBay " + bay.getLocationCode() + " (" + bay.getStoredBoxes().size() + "/" + bay.getCapacityBoxes() + " boxes):");
                for (Box box : bay.getStoredBoxes()) {
                    System.out.println("  - " + box);
                }
            }
        }
    }

    /**
     * Displays available SKUs and handles SKU dispatch operation
     */
    public boolean dispatchSku(Warehouse warehouse, String sku, int quantity) {
        var inventory = warehouse.getInventoryBySku();

        if (!inventory.containsKey(sku)) {
            return false;
        }

        int availableQty = inventory.get(sku).stream().mapToInt(Box::getQuantity).sum();
        System.out.println("Available quantity for " + sku + ": " + availableQty);

        if (quantity <= 0) {
            return false;
        }

        int dispatched = warehouse.dispatch(sku, quantity);
        System.out.println("✓ Dispatched " + dispatched + " units of SKU " + sku);
        if (dispatched < quantity) {
            System.out.println("⚠ Warning: Only " + dispatched + " units available (requested " + quantity + ")");
        }
        return true;
    }

    /**
     * Gets all available boxes in the warehouse
     */
    public Map<String, String> getAllAvailableBoxes(Warehouse warehouse) {
        Map<String, String> allBoxes = new TreeMap<>();

        for (Bay bay : warehouse.getAllBays()) {
            for (Box box : bay.getStoredBoxes()) {
                allBoxes.put(box.getBoxId(), String.format("%s | SKU: %s | Qty: %d | Bay: %s", box.getBoxId(), box.getSku(), box.getQuantity(), bay.getLocationCode()));
            }
        }
        return allBoxes;
    }

    /**
     * Relocates a box to a new bay
     */
    public boolean relocateBox(Warehouse warehouse, String boxId, String newBayLocation) {
        Bay targetBay = warehouse.getBay(newBayLocation);
        if (targetBay == null) {
            return false;
        }

        if (targetBay.getStoredBoxes().size() >= targetBay.getCapacityBoxes()) {
            return false;
        }

        return warehouse.relocate(boxId, newBayLocation);
    }

    /**
     * Displays inventory grouped by SKU
     */
    public void displayInventoryBySku(Warehouse warehouse) {
        System.out.println("\n=== Inventory by SKU ===");
        warehouse.getInventoryBySku().forEach((sku, boxes) -> {
            int total = boxes.stream().mapToInt(Box::getQuantity).sum();
            System.out.println(sku + ": " + total + " units in " + boxes.size() + " boxes");
        });
    }

    /**
     * Processes orders with the specified eligibility mode
     */
    public AllocationResult processOrders(List<Order> orders, Warehouse warehouse, EligibilityMode mode) {
        System.out.println("Processing orders in " + mode + " mode...\n");

        AllocationResult result = orderAllocationService.allocateOrders(orders, warehouse, mode);

        System.out.println("=== LINE ELIGIBILITIES ===");
        for (LineEligibility elig : result.getEligibilities()) {
            System.out.println(elig);
        }

        System.out.println("\n=== ALLOCATIONS ===");
        for (AllocationFragment alloc : result.getAllocations()) {
            System.out.println(alloc);
        }

        System.out.println("\nSummary:");
        System.out.println("Total lines: " + result.getEligibilities().size());
        System.out.println("Eligible: " + result.getEligibilities().stream().filter(e -> e.getStatus() == AllocationStatus.ELIGIBLE).count());
        System.out.println("Partial: " + result.getEligibilities().stream().filter(e -> e.getStatus() == AllocationStatus.PARTIAL).count());
        System.out.println("Undispatchable: " + result.getEligibilities().stream().filter(e -> e.getStatus() == AllocationStatus.UNDISPATCHABLE).count());
        System.out.println("Total allocations: " + result.getAllocations().size());

        return result;
    }

    /**
     * Packs trolleys using the specified heuristic
     */
    public void packTrolleys(AllocationResult allocationResult, double trolleyCapacity, PackingHeuristic heuristic, Map<String, Item> items) {
        System.out.println("\n=== " + heuristic.getDisplayName() + " ===");
        System.out.printf("Trolley capacity: %.1fkg%n", trolleyCapacity);

        PickingPlan plan = trolleyPackingService.packAllocations(allocationResult.getAllocations(), trolleyCapacity, heuristic, items);


        if (!plan.getNotes().isEmpty()) {
            System.out.println("\nNotes:");
            for (String note : plan.getNotes()) {
                System.out.println("• " + note);
            }
        }

        System.out.println("\nTrolley Details:");


        for (Trolley trolley : plan.getTrolleys()) {
            System.out.printf("%n%s%n", trolley.toString());

            if (!trolley.getAllocations().isEmpty()) {
                System.out.println("Picking plan:");
                for (AllocationFragment allocation : trolley.getAllocations()) {
                    System.out.printf(allocation.toString());
                }
                System.out.println("----------------------------------------------------------------");
            }

        }

        System.out.println("\n\n\nSummary:");
        System.out.printf("• Total trolleys: %d%n", plan.getTotalTrolleys());
        System.out.printf("• Average utilization: %.1f%%%n", plan.getAverageUtilization());
        System.out.printf("• Total allocations: %d%n", plan.getTotalAllocations());
    }
}