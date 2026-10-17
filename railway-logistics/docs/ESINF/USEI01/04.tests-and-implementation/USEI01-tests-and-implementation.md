# USEI01 - Warehouse Management - Box Allocation and FEFO/FIFO Dispatch

## 4. Tests

**Test 1:** Check that warehouse is loaded correctly with all bays and inventory

```java
@Test
void testWarehouseLoadedCorrectly() {
    assertFalse(warehouse.getAllBays().isEmpty());
    assertTrue(warehouse.getAllBays().size() >= 100);

    Map<String, List<Box>> inventory = warehouse.getInventoryBySku();
    assertFalse(inventory.isEmpty());
    assertTrue(inventory.size() >= 10);

    inventory.forEach((sku, boxes) -> {
        assertFalse(boxes.isEmpty(), "SKU " + sku + " should have boxes");
        int totalQty = boxes.stream().mapToInt(Box::getQuantity).sum();
        assertTrue(totalQty > 0, "SKU " + sku + " should have positive quantity");
    });
}
```

**Test 2:** Check that FEFO ordering is respected for perishable goods

```java
@Test
void testFEFO_OrderingWithRealData() {
    Map<String, List<Box>> inventory = warehouse.getInventoryBySku();
    String skuWithExpiry = "SKU0005";
    
    if (inventory.containsKey(skuWithExpiry)) {
        List<Box> boxes = inventory.get(skuWithExpiry);
        
        for (int i = 0; i < boxes.size() - 1; i++) {
            Box current = boxes.get(i);
            Box next = boxes.get(i + 1);
            
            if (current.getExpiryDate() != null && next.getExpiryDate() != null) {
                assertTrue(current.getExpiryDate().compareTo(next.getExpiryDate()) <= 0,
                        "Boxes should be ordered by expiry date (FEFO)");
            }
            else if (current.getExpiryDate() == null && next.getExpiryDate() != null) {
                fail("Non-perishable box should not come before perishable box");
            }
        }
    }
}
```

**Test 3:** Check that dispatch operations consume correct quantities

```java
@Test
void testDispatchWithRealData() {
    Map<String, List<Box>> inventoryBefore = warehouse.getInventoryBySku();
    
    String testSku = inventoryBefore.keySet().stream()
            .filter(sku -> {
                int totalQty = inventoryBefore.get(sku).stream()
                        .mapToInt(Box::getQuantity).sum();
                return totalQty >= 10;
            })
            .findFirst()
            .orElse(null);
    
    if (testSku != null) {
        int totalBefore = inventoryBefore.get(testSku).stream()
                .mapToInt(Box::getQuantity).sum();
        
        int dispatched = warehouse.dispatch(testSku, 5);
        
        assertEquals(5, dispatched, "Should dispatch requested quantity when available");
        
        Map<String, List<Box>> inventoryAfter = warehouse.getInventoryBySku();
        int totalAfter = inventoryAfter.get(testSku).stream()
                .mapToInt(Box::getQuantity).sum();
        
        assertEquals(totalBefore - 5, totalAfter,
                "Inventory should decrease by dispatched quantity");
    }
}
```

**Test 4:** Check that bay capacity is respected

```java
@Test
void testBayCapacityRespected() {
    for (Bay bay : warehouse.getAllBays()) {
        assertTrue(bay.getStoredBoxes().size() <= bay.getCapacityBoxes(),
                "Bay " + bay.getLocationCode() + " should not exceed capacity");
    }
}
```

**Test 5:** Check that all boxIds are unique in the warehouse

```java
@Test
void testBoxUniqueness() {
    Map<String, List<Box>> inventory = warehouse.getInventoryBySku();
    
    long totalBoxes = inventory.values().stream()
            .mapToLong(List::size)
            .sum();
    
    long uniqueBoxes = inventory.values().stream()
            .flatMap(List::stream)
            .map(Box::getBoxId)
            .distinct()
            .count();
    
    assertEquals(totalBoxes, uniqueBoxes, "All box IDs should be unique in the warehouse");
}
```

## 5. Construction (Implementation)

### 5.1 WarehouseAllocator Class

```java
public class WarehouseAllocator {
    public void allocateBoxesToWarehouse(List<Box> boxes, Warehouse warehouse) {
        Map<String, List<Box>> grouped = new HashMap<>();
        List<Box> validBoxes = new ArrayList<>();
        Set<String> seenBoxIds = new HashSet<>();
        
        for (Box box : boxes) {
            if (seenBoxIds.contains(box.getBoxId())) {
                System.err.println("Duplicate boxId in input list: " + box.getBoxId());
                continue;
            }
            seenBoxIds.add(box.getBoxId());
            validBoxes.add(box);
        }
        
        for (Box box : validBoxes) {
            grouped.computeIfAbsent(box.getSku(), k -> new ArrayList<>()).add(box);
        }
        
        for (Map.Entry<String, List<Box>> entry : grouped.entrySet()) {
            List<Box> skuBoxes = entry.getValue();
            Collections.sort(skuBoxes);
            
            for (Box box : skuBoxes) {
                boolean success = warehouse.allocateBox(box);
                if (!success) {
                    System.err.println("Box " + box.getBoxId() + " could not be allocated");
                }
            }
        }
    }
}
```

### 5.2 Warehouse Class

```java
public class Warehouse {
    private final Map<String, Bay> bays = new LinkedHashMap<>();
    private final Set<String> allocatedBoxIds = new HashSet<>();
    
    public boolean allocateBox(Box box) {
        if (allocatedBoxIds.contains(box.getBoxId())) {
            System.err.println("Duplicate boxId detected: " + box.getBoxId());
            return false;
        }
        
        List<Bay> sortedBays = new ArrayList<>(bays.values());
        sortedBays.sort(Comparator
                .comparingInt(Bay::getAisle)
                .thenComparingInt(Bay::getBayNumber));
        
        for (Bay bay : sortedBays) {
            if (bay.addBox(box)) {
                allocatedBoxIds.add(box.getBoxId());
                return true;
            }
        }
        
        return false;
    }
}
```

### 5.3 Box Class with FEFO/FIFO Ordering

```java
public class Box implements Comparable<Box> {
    private String boxId;
    private String sku;
    private int quantity;
    private LocalDate expiryDate;
    private Instant arrivalTimestamp;
    
    @Override
    public int compareTo(Box other) {
        boolean thisPerishable = this.expiryDate != null;
        boolean otherPerishable = other.expiryDate != null;
        
        if (thisPerishable && !otherPerishable) return -1;
        if (!thisPerishable && otherPerishable) return 1;
        
        if (thisPerishable && otherPerishable) {
            return this.expiryDate.compareTo(other.expiryDate);
        }
        
        return this.arrivalTimestamp.compareTo(other.arrivalTimestamp);
    }
}
```

## 6. Integration and Demo

The warehouse allocation functionality was integrated with the CSV import system and menu controller. A demo shows:

- Loading warehouse structure from bays.csv
- Importing box data from wagons.csv
- Allocating boxes to bays following FEFO/FIFO rules
- Displaying inventory organized by SKU
- Dispatching SKUs and consuming stock from the front of the list

## 7. Observations

The implementation successfully handles:
- Unique boxId validation across the entire warehouse
- FEFO ordering for perishable goods (by expiry date)
- FIFO ordering for non-perishable goods (by arrival timestamp)
- Bay capacity management
- Efficient dispatch operations consuming from the front of the list

Future enhancements could include:
- Box relocation between bays
- Inventory reports and alerts for expiring goods
- Capacity optimization algorithms
- Integration with returns management system
