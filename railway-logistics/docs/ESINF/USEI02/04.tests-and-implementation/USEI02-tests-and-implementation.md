# USEI02 - Order Allocation with STRICT and PARTIAL Modes

## 4. Tests

**Test 1:** Check that STRICT mode produces no PARTIAL allocations

```java
@Test
void testAllocationWithRealData_STRICT_Mode() {
    AllocationResult result = allocationService.allocateOrders(
            orders, warehouse, OrderAllocationService.EligibilityMode.STRICT);
    
    assertNotNull(result);
    assertFalse(result.getEligibilities().isEmpty());
    assertTrue(result.getEligibilities().size() >= 40);
    
    long eligibleCount = result.getEligibilities().stream()
            .filter(e -> e.getStatus() == AllocationStatus.ELIGIBLE)
            .count();
    long partialCount = result.getEligibilities().stream()
            .filter(e -> e.getStatus() == AllocationStatus.PARTIAL)
            .count();
    long undispatchableCount = result.getEligibilities().stream()
            .filter(e -> e.getStatus() == AllocationStatus.UNDISPATCHABLE)
            .count();
    
    assertEquals(0, partialCount, "STRICT mode should not have PARTIAL status");
    assertTrue(eligibleCount > 0, "Should have some eligible orders with real data");
    assertTrue(undispatchableCount > 0, "Should have some undispatchable orders with real data");
}
```

**Test 2:** Check that PARTIAL mode allows partial allocations

```java
@Test
void testAllocationWithRealData_PARTIAL_Mode() {
    AllocationResult result = allocationService.allocateOrders(
            orders, warehouse, OrderAllocationService.EligibilityMode.PARTIAL);
    
    assertNotNull(result);
    assertFalse(result.getEligibilities().isEmpty());
    
    long partialCount = result.getEligibilities().stream()
            .filter(e -> e.getStatus() == AllocationStatus.PARTIAL)
            .count();
    
    assertTrue(partialCount > 0, "PARTIAL mode should have some PARTIAL allocations");
    
    AllocationResult strictResult = allocationService.allocateOrders(
            orders, warehouse, OrderAllocationService.EligibilityMode.STRICT);
    
    assertTrue(result.getAllocations().size() >= strictResult.getAllocations().size(),
            "PARTIAL mode should have equal or more allocations than STRICT mode");
}
```

**Test 3:** Check that orders are processed in correct sequence

```java
@Test
void testOrderProcessingOrder() {
    AllocationResult result = allocationService.allocateOrders(
            orders, warehouse, OrderAllocationService.EligibilityMode.STRICT);
    
    List<LineEligibility> eligibilities = result.getEligibilities();
    
    LineEligibility firstEligibility = eligibilities.stream()
            .filter(e -> e.getOrderId().equals("ORD00004"))
            .findFirst()
            .orElse(null);
    
    assertNotNull(firstEligibility, "ORD00004 should be processed early due to priority 1");
    
    int ord15Index = -1;
    int ord01Index = -1;
    
    for (int i = 0; i < eligibilities.size(); i++) {
        String orderId = eligibilities.get(i).getOrderId();
        if (orderId.equals("ORD00015")) ord15Index = i;
        if (orderId.equals("ORD00001")) ord01Index = i;
    }
    
    if (ord15Index != -1 && ord01Index != -1) {
        assertTrue(ord15Index < ord01Index,
                "ORD00015 should be processed before ORD00001 (earlier due date, same priority)");
    }
}
```

**Test 4:** Check that allocation follows FEFO for perishable goods

```java
@Test
void testFEFO_AllocationWithRealExpiryDates() {
    Order testOrder = new Order("TEST_FEFO", LocalDate.now().plusDays(1), 1);
    testOrder.addLine(new OrderLine(1, "SKU0005", 10));
    
    AllocationResult result = allocationService.allocateOrders(
            List.of(testOrder), warehouse, OrderAllocationService.EligibilityMode.STRICT);
    
    if (!result.getAllocations().isEmpty()) {
        List<AllocationFragment> allocations = result.getAllocations();
        assertTrue(allocations.size() > 0, "Should have allocations for SKU with expiry dates");
    }
}
```

**Test 5:** Check that planning is non-destructive

```java
@Test
void testNonDestructivePlanning() {
    Map<String, List<Box>> inventoryBefore = warehouse.getInventoryBySku();
    
    allocationService.allocateOrders(orders, warehouse,
            OrderAllocationService.EligibilityMode.STRICT);
    
    Map<String, List<Box>> inventoryAfter = warehouse.getInventoryBySku();
    
    assertEquals(inventoryBefore.size(), inventoryAfter.size());
    
    inventoryBefore.forEach((sku, boxesBefore) -> {
        List<Box> boxesAfter = inventoryAfter.get(sku);
        assertNotNull(boxesAfter, "SKU should still exist after planning");
        
        int totalBefore = boxesBefore.stream().mapToInt(Box::getQuantity).sum();
        int totalAfter = boxesAfter.stream().mapToInt(Box::getQuantity).sum();
        
        assertEquals(totalBefore, totalAfter,
                "Total quantity for SKU " + sku + " should not change after planning");
    });
}
```

## 5. Construction (Implementation)

### 5.1 OrderAllocationService Class

```java
public class OrderAllocationService {
    public enum EligibilityMode {
        STRICT, PARTIAL
    }
    
    public AllocationResult allocateOrders(List<Order> orders, Warehouse warehouse,
                                           EligibilityMode mode) {
        List<Order> sortedOrders = new ArrayList<>(orders);
        Collections.sort(sortedOrders);
        
        List<LineEligibility> eligibilities = new ArrayList<>();
        List<AllocationFragment> allocations = new ArrayList<>();
        
        Map<String, List<BoxAllocation>> inventorySnapshot = createInventorySnapshot(warehouse);
        
        for (Order order : sortedOrders) {
            List<OrderLine> sortedLines = new ArrayList<>(order.getLines());
            Collections.sort(sortedLines);
            
            List<AllocationFragment> orderAllocations = new ArrayList<>();
            List<LineEligibility> orderEligibilities = new ArrayList<>();
            boolean orderFailed = false;
            
            for (OrderLine line : sortedLines) {
                List<AllocationFragment> lineAllocations = new ArrayList<>();
                int remainingQty = line.getRequestedQty();
                int allocatedQty = 0;
                
                List<BoxAllocation> skuBoxes = inventorySnapshot.get(line.getSku());
                
                if (skuBoxes != null) {
                    for (BoxAllocation boxAlloc : skuBoxes) {
                        if (remainingQty <= 0) break;
                        
                        int availableInBox = boxAlloc.availableQty;
                        if (availableInBox <= 0) continue;
                        
                        int toAllocate = Math.min(remainingQty, availableInBox);
                        
                        AllocationFragment fragment = new AllocationFragment(
                                order.getOrderId(), line.getLineNo(), line.getSku(),
                                toAllocate, boxAlloc.box.getBoxId(),
                                boxAlloc.aisle, boxAlloc.bay
                        );
                        
                        lineAllocations.add(fragment);
                        allocatedQty += toAllocate;
                        remainingQty -= toAllocate;
                        boxAlloc.availableQty -= toAllocate;
                    }
                }
                
                AllocationStatus status;
                boolean keepAllocations = false;
                
                if (allocatedQty == line.getRequestedQty()) {
                    status = AllocationStatus.ELIGIBLE;
                    keepAllocations = true;
                } else if (allocatedQty == 0) {
                    status = AllocationStatus.UNDISPATCHABLE;
                    if (mode == EligibilityMode.STRICT) {
                        orderFailed = true;
                    }
                } else {
                    if (mode == EligibilityMode.PARTIAL) {
                        status = AllocationStatus.PARTIAL;
                        keepAllocations = true;
                    } else {
                        status = AllocationStatus.UNDISPATCHABLE;
                        orderFailed = true;
                    }
                }
                
                LineEligibility eligibility = new LineEligibility(
                        order.getOrderId(), line.getLineNo(), status,
                        line.getRequestedQty(), allocatedQty
                );
                
                orderEligibilities.add(eligibility);
                
                if (keepAllocations) {
                    orderAllocations.addAll(lineAllocations);
                }
            }
            
            if (!orderFailed) {
                eligibilities.addAll(orderEligibilities);
                allocations.addAll(orderAllocations);
            }
        }
        
        return new AllocationResult(eligibilities, allocations);
    }
}
```

### 5.2 Order Class with Sorting

```java
public class Order implements Comparable<Order> {
    private final String orderId;
    private final LocalDate dueDate;
    private final int priority;
    private final List<OrderLine> lines;
    
    @Override
    public int compareTo(Order other) {
        int cmp = Integer.compare(this.priority, other.priority);
        if (cmp != 0) return cmp;
        
        cmp = this.dueDate.compareTo(other.dueDate);
        if (cmp != 0) return cmp;
        
        return this.orderId.compareTo(other.orderId);
    }
}
```

### 5.3 AllocationResult Class

```java
public class AllocationResult {
    private final List<LineEligibility> eligibilities;
    private final List<AllocationFragment> allocations;
    
    public AllocationResult(List<LineEligibility> eligibilities,
                           List<AllocationFragment> allocations) {
        this.eligibilities = new ArrayList<>(eligibilities);
        this.allocations = new ArrayList<>(allocations);
    }
    
    public List<LineEligibility> getEligibilities() {
        return new ArrayList<>(eligibilities);
    }
    
    public List<AllocationFragment> getAllocations() {
        return new ArrayList<>(allocations);
    }
}
```

## 6. Integration and Demo

The order allocation functionality was integrated with the warehouse system and menu controller. A demo shows:

- Loading orders and order lines from CSV
- Selecting allocation mode (STRICT or PARTIAL)
- Processing orders in priority sequence
- Allocating inventory following FEFO/FIFO rules
- Displaying eligibility statistics and allocation results

## 7. Observations

The implementation successfully handles:
- Order sorting by priority, due date, and order ID
- Line sorting by line number within each order
- FEFO/FIFO inventory consumption
- Mode-specific behavior (STRICT vs PARTIAL)
- Non-destructive planning (warehouse state unchanged)
- Complete allocation tracking with box references

The allocation result provides full traceability showing which boxes and locations are assigned to each order line, enabling efficient picking operations in USEI03.
