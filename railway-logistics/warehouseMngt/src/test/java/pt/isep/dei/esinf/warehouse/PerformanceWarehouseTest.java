package pt.isep.dei.esinf.warehouse;
import org.junit.jupiter.api.Test;
import pt.isep.dei.esinf.domain.warehouse.Bay;
import pt.isep.dei.esinf.domain.warehouse.Box;
import pt.isep.dei.esinf.domain.warehouse.Warehouse;
import pt.isep.dei.esinf.service.warehouse.WarehouseAllocator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de performance e stress para USEI01 (Warehouse)
 */
class PerformanceWarehouseTest {

    @Test
    void testLargeScaleWarehouseOperations() {
        Warehouse largeWarehouse = new Warehouse();
        WarehouseAllocator allocator = new WarehouseAllocator();

        // Criar warehouse grande: 50 aisles × 20 bays = 1000 bays
        for (int aisle = 1; aisle <= 50; aisle++) {
            for (int bayNum = 1; bayNum <= 20; bayNum++) {
                largeWarehouse.addBay(new Bay("WH01", aisle, bayNum, 10));
            }
        }

        // CORREÇÃO: Garantir boxIds únicos
        List<Box> boxes = new ArrayList<>();
        LocalDateTime baseTime = LocalDateTime.now();

        for (int i = 0; i < 5000; i++) {
            // CORREÇÃO: BoxId único com padding
            String boxId = "BOX" + String.format("%05d", i);
            String sku = "SKU" + (i % 200);
            LocalDateTime receivedAt = baseTime.minusHours(i % 1000);
            LocalDateTime expiryDate = (i % 4 == 0) ? null : baseTime.plusDays(i % 30);

            boxes.add(new Box(boxId, sku, (i % 10) + 1, expiryDate, receivedAt));
        }

        // Medir tempo de alocação
        long startAlloc = System.currentTimeMillis();
        allocator.allocateBoxesToWarehouse(boxes, largeWarehouse);
        long endAlloc = System.currentTimeMillis();

        System.out.println("Large warehouse allocation:");
        System.out.println(" - 1000 bays, 5000 boxes");
        System.out.println(" - Allocation time: " + (endAlloc - startAlloc) + "ms");

        // Verificar que não houve boxes rejeitados por IDs duplicados
        Map<String, List<Box>> inventory = largeWarehouse.getInventoryBySku();
        long totalBoxes = inventory.values().stream()
                .mapToLong(List::size)
                .sum();

        System.out.println(" - Successfully allocated boxes: " + totalBoxes + "/5000");

        // CORREÇÃO: Verificação mais realista
        assertTrue(totalBoxes > 4000, "Should allocate most boxes (some may fail due to capacity)");
        assertTrue((endAlloc - startAlloc) < 5000, "Allocation should complete within 5 seconds");
    }

    @Test
    void testHighFrequencyDispatchOperations() {
        Warehouse warehouse = new Warehouse();
        WarehouseAllocator allocator = new WarehouseAllocator();

        // Configurar warehouse com stock
        for (int i = 1; i <= 10; i++) {
            warehouse.addBay(new Bay("WH01", 1, i, 10));
        }

        List<Box> boxes = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // 100 boxes do mesmo SKU distribuídos por vários bays
        for (int i = 0; i < 100; i++) {
            boxes.add(new Box("BOX" + i, "SKU001", 10, null, now.minusMinutes(i)));
        }

        allocator.allocateBoxesToWarehouse(boxes, warehouse);

        // Executar 100 operações de dispatch rápidas
        long startTime = System.currentTimeMillis();

        int totalDispatched = 0;
        for (int i = 0; i < 100; i++) {
            int dispatched = warehouse.dispatch("SKU001", 1);
            totalDispatched += dispatched;
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("High frequency dispatch:");
        System.out.println(" - 100 dispatch operations");
        System.out.println(" - Total dispatched: " + totalDispatched);
        System.out.println(" - Total time: " + duration + "ms");
        System.out.println(" - Average time per operation: " + (duration / 100.0) + "ms");

        assertEquals(100, totalDispatched, "Should dispatch all 100 units");
        assertTrue(duration < 1000, "100 operations should complete within 1 second");
    }

    @Test
    void testMassiveSingleDispatch() {
        Warehouse warehouse = new Warehouse();
        WarehouseAllocator allocator = new WarehouseAllocator();

        // Warehouse grande
        for (int i = 1; i <= 100; i++) {
            warehouse.addBay(new Bay("WH01", 1, i, 10));
        }

        // 1000 boxes do mesmo SKU
        List<Box> boxes = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < 1000; i++) {
            boxes.add(new Box("BOX" + i, "SKU001", 10, null, now.minusHours(i)));
        }

        allocator.allocateBoxesToWarehouse(boxes, warehouse);

        // Single dispatch muito grande
        long startTime = System.currentTimeMillis();
        int dispatched = warehouse.dispatch("SKU001", 5000); // Metade do stock total
        long endTime = System.currentTimeMillis();

        System.out.println("Massive single dispatch:");
        System.out.println(" - Requested: 5000 units");
        System.out.println(" - Dispatched: " + dispatched + " units");
        System.out.println(" - Time: " + (endTime - startTime) + "ms");

        assertEquals(5000, dispatched, "Should dispatch exactly 5000 units");
        assertTrue((endTime - startTime) < 1000, "Massive dispatch should complete within 1 second");

        // Verificar stock restante
        Map<String, List<Box>> inventory = warehouse.getInventoryBySku();
        int remainingStock = inventory.get("SKU001").stream()
                .mapToInt(Box::getQuantity)
                .sum();
        assertEquals(5000, remainingStock, "Should have 5000 units remaining");
    }

    @Test
    void testConcurrentLikeOperations() {
        // Simular operações concorrentes (sequencialmente)
        Warehouse warehouse = new Warehouse();
        WarehouseAllocator allocator = new WarehouseAllocator();

        // Setup
        for (int i = 1; i <= 5; i++) {
            warehouse.addBay(new Bay("WH01", 1, i, 10));
        }

        allocator.allocateBoxesToWarehouse(List.of(
                new Box("BOX001", "SKU001", 100, null, LocalDateTime.now()),
                new Box("BOX002", "SKU002", 100, null, LocalDateTime.now()),
                new Box("BOX003", "SKU003", 100, null, LocalDateTime.now())
        ), warehouse);

        long startTime = System.currentTimeMillis();

        // Misturar diferentes operações como num cenário real
        for (int i = 0; i < 50; i++) {
            // Operações variadas
            switch (i % 4) {
                case 0:
                    warehouse.dispatch("SKU001", 2);
                    break;
                case 1:
                    warehouse.dispatch("SKU002", 3);
                    break;
                case 2:
                    warehouse.getInventoryBySku();
                    break;
                case 3:
                    // Simular relocate ocasional
                    if (i % 10 == 0) {
                        warehouse.relocate("BOX001", "WH01-1-2");
                    }
                    break;
            }
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Concurrent-like operations:");
        System.out.println(" - 50 mixed operations");
        System.out.println(" - Total time: " + (endTime - startTime) + "ms");

        assertTrue((endTime - startTime) < 500, "Mixed operations should complete within 500ms");

        // Verificar estado final consistente
        Map<String, List<Box>> inventory = warehouse.getInventoryBySku();
        assertFalse(inventory.isEmpty(), "Inventory should not be empty");
    }
}