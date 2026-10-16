package pt.isep.dei.esinf.warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.isep.dei.esinf.domain.warehouse.Bay;
import pt.isep.dei.esinf.domain.warehouse.Box;
import pt.isep.dei.esinf.domain.warehouse.Warehouse;
import pt.isep.dei.esinf.service.warehouse.WarehouseAllocator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para edge cases e cenários específicos da USEI01 (Warehouse)
 */
class EdgeCasesWarehouseTest {

    private Warehouse warehouse;
    private WarehouseAllocator allocator;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse();
        allocator = new WarehouseAllocator();

        // Configurar warehouse básico
        warehouse.addBay(new Bay("WH01", 1, 1, 5));
        warehouse.addBay(new Bay("WH01", 1, 2, 5));
        warehouse.addBay(new Bay("WH01", 2, 1, 5));
    }

    @Test
    void testEmptyWarehouseOperations() {
        // Operações num warehouse vazio
        Warehouse emptyWarehouse = new Warehouse();

        // Dispatch de SKU inexistente
        int dispatched = emptyWarehouse.dispatch("SKU999", 10);
        assertEquals(0, dispatched, "Should return 0 for non-existent SKU");

        // Relocate de box inexistente
        boolean relocated = emptyWarehouse.relocate("NONEXISTENT", "WH01-1-1");
        assertFalse(relocated, "Should return false for non-existent box");

        // Inventory deve estar vazio
        Map<String, List<Box>> inventory = emptyWarehouse.getInventoryBySku();
        assertTrue(inventory.isEmpty(), "Inventory should be empty");
    }

    @Test
    void testBayCapacityLimits() {
        // Testar limites de capacidade dos bays
        Bay smallBay = new Bay("WH01", 1, 1, 2); // Capacidade apenas 2 boxes

        // Tentar adicionar 3 boxes - só 2 devem ser aceites
        LocalDateTime now = LocalDateTime.now();
        Box box1 = new Box("BOX001", "SKU001", 5, null, now);
        Box box2 = new Box("BOX002", "SKU001", 5, null, now);
        Box box3 = new Box("BOX003", "SKU001", 5, null, now);

        assertTrue(smallBay.addBox(box1));
        assertTrue(smallBay.addBox(box2));
        assertFalse(smallBay.addBox(box3)); // Deve falhar - capacidade excedida

        assertEquals(2, smallBay.getStoredBoxes().size());
    }

    @Test
    void testDispatchZeroQuantity() {
        // Dispatch de quantidade zero
        allocator.allocateBoxesToWarehouse(List.of(
                new Box("BOX001", "SKU001", 10, null, LocalDateTime.now())
        ), warehouse);

        int dispatched = warehouse.dispatch("SKU001", 0);
        assertEquals(0, dispatched, "Should return 0 for zero quantity dispatch");

        // Verificar que stock não foi alterado
        Map<String, List<Box>> inventory = warehouse.getInventoryBySku();
        int totalStock = inventory.get("SKU001").stream()
                .mapToInt(Box::getQuantity)
                .sum();
        assertEquals(10, totalStock, "Stock should remain unchanged");
    }

    @Test
    void testDispatchNegativeQuantity() {
        // Dispatch de quantidade negativa
        allocator.allocateBoxesToWarehouse(List.of(
                new Box("BOX001", "SKU001", 10, null, LocalDateTime.now())
        ), warehouse);

        int dispatched = warehouse.dispatch("SKU001", -5);
        assertEquals(0, dispatched, "Should return 0 for negative quantity");

        // Stock deve permanecer inalterado
        Map<String, List<Box>> inventory = warehouse.getInventoryBySku();
        int totalStock = inventory.get("SKU001").stream()
                .mapToInt(Box::getQuantity)
                .sum();
        assertEquals(10, totalStock);
    }

    @Test
    void testRelocateToNonExistentBay() {
        // Tentar relocar para bay que não existe
        allocator.allocateBoxesToWarehouse(List.of(
                new Box("BOX001", "SKU001", 10, null, LocalDateTime.now())
        ), warehouse);

        boolean relocated = warehouse.relocate("BOX001", "WH01-99-99");
        assertFalse(relocated, "Should return false for non-existent target bay");

        // Box deve permanecer no local original
        Map<String, List<Box>> inventory = warehouse.getInventoryBySku();
        assertTrue(inventory.get("SKU001").stream()
                .anyMatch(box -> box.getBoxId().equals("BOX001")));
    }

    @Test
    void testRelocateToFullBay() {
        // Tentar relocar para bay cheio
        Bay targetBay = new Bay("WH01", 2, 2, 1); // Capacidade 1
        warehouse.addBay(targetBay);

        // Encher o target bay
        targetBay.addBox(new Box("BOX_FILLER", "SKU999", 5, null, LocalDateTime.now()));

        allocator.allocateBoxesToWarehouse(List.of(
                new Box("BOX001", "SKU001", 10, null, LocalDateTime.now())
        ), warehouse);

        boolean relocated = warehouse.relocate("BOX001", "WH01-2-2");
        assertFalse(relocated, "Should return false when target bay is full");

        // Box deve permanecer no local original
        Map<String, List<Box>> inventory = warehouse.getInventoryBySku();
        assertTrue(inventory.get("SKU001").stream()
                .anyMatch(box -> box.getBoxId().equals("BOX001")));
    }

    @Test
    void testFEFO_WithNullExpiryDates() {
        // Testar ordenação FEFO com vários boxes com expiry null
        LocalDateTime now = LocalDateTime.now();

        Box box1 = new Box("BOX001", "SKU001", 5, null, now.minusHours(5));
        Box box2 = new Box("BOX002", "SKU001", 5, now.plusDays(3), now.minusHours(3));
        Box box3 = new Box("BOX003", "SKU001", 5, null, now.minusHours(10));
        Box box4 = new Box("BOX004", "SKU001", 5, now.plusDays(1), now.minusHours(1));

        allocator.allocateBoxesToWarehouse(List.of(box1, box2, box3, box4), warehouse);

        Map<String, List<Box>> inventory = warehouse.getInventoryBySku();
        List<Box> boxes = inventory.get("SKU001");

        // Ordem esperada: BOX004 (expiry mais cedo), BOX002, BOX003 (received mais antigo), BOX001
        assertEquals("BOX004", boxes.get(0).getBoxId());
        assertEquals("BOX002", boxes.get(1).getBoxId());
        assertEquals("BOX003", boxes.get(2).getBoxId());
        assertEquals("BOX001", boxes.get(3).getBoxId());
    }

    @Test
    void testDispatchConsumesFromMultipleBays() {
        // Testar dispatch que consome de múltiplos bays
        LocalDateTime now = LocalDateTime.now();

        // Boxes em bays diferentes
        Box box1 = new Box("BOX001", "SKU001", 3, null, now); // Bay 1-1
        Box box2 = new Box("BOX002", "SKU001", 4, null, now); // Bay 1-2
        Box box3 = new Box("BOX003", "SKU001", 5, null, now); // Bay 2-1

        allocator.allocateBoxesToWarehouse(List.of(box1, box2, box3), warehouse);

        // Dispatch que consome de todos os boxes
        int dispatched = warehouse.dispatch("SKU001", 10);
        assertEquals(10, dispatched);

        // Verificar estado final
        Map<String, List<Box>> inventory = warehouse.getInventoryBySku();
        List<Box> remainingBoxes = inventory.get("SKU001");

        // Deve sobrar 2 unidades no último box
        assertEquals(1, remainingBoxes.size());
        assertEquals("BOX003", remainingBoxes.get(0).getBoxId());
        assertEquals(2, remainingBoxes.get(0).getQuantity());
    }

    @Test
    void testBoxUniquenessAcrossBays() {
        // Testar que boxIds são únicos em todo o warehouse
        LocalDateTime now = LocalDateTime.now();

        Box box1 = new Box("BOX001", "SKU001", 5, null, now);
        Box box2 = new Box("BOX001", "SKU002", 5, null, now); // Mesmo boxId, SKU diferente

        // Primeiro box deve ser aceite
        boolean firstAdded = warehouse.allocateBox(box1);
        assertTrue(firstAdded, "First box with unique ID should be accepted");

        // Segundo box com mesmo ID não deve ser aceite
        boolean secondAdded = warehouse.allocateBox(box2);
        assertFalse(secondAdded, "Should reject box with duplicate ID");

        // Verificar no warehouse
        assertTrue(warehouse.containsBoxId("BOX001"), "Warehouse should contain BOX001");

        Map<String, List<Box>> inventory = warehouse.getInventoryBySku();
        // Apenas SKU001 deve estar presente
        assertTrue(inventory.containsKey("SKU001"));
        assertFalse(inventory.containsKey("SKU002"));

        // Verificar que temos apenas 1 box no total
        int totalBoxes = inventory.values().stream()
                .mapToInt(List::size)
                .sum();
        assertEquals(1, totalBoxes, "Should have only one box in warehouse");
    }

    @Test
    void testBoxUniquenessWithWarehouseAllocator() {
        // Testar que WarehouseAllocator também rejeita duplicates
        LocalDateTime now = LocalDateTime.now();

        List<Box> boxesWithDuplicates = List.of(
                new Box("BOX001", "SKU001", 5, null, now),
                new Box("BOX001", "SKU002", 5, null, now), // Duplicado!
                new Box("BOX002", "SKU003", 5, null, now)  // Único
        );

        allocator.allocateBoxesToWarehouse(boxesWithDuplicates, warehouse);

        Map<String, List<Box>> inventory = warehouse.getInventoryBySku();

        // Apenas 2 SKUs devem estar presentes (SKU002 foi rejeitado)
        assertEquals(2, inventory.size());
        assertTrue(inventory.containsKey("SKU001"));
        assertFalse(inventory.containsKey("SKU002")); // Rejeitado por duplicate
        assertTrue(inventory.containsKey("SKU003"));

        // Verificar boxIds únicos
        Set<String> allBoxIds = warehouse.getAllocatedBoxIds();
        assertEquals(2, allBoxIds.size());
        assertTrue(allBoxIds.contains("BOX001"));
        assertTrue(allBoxIds.contains("BOX002"));
    }

    @Test
    void testGetBayReturnsCorrectBay() {
        // Testar getBay com vários formatos de location code
        Bay bay1 = new Bay("WH01", 1, 1, 10);
        Bay bay2 = new Bay("WH01", 2, 5, 10);
        warehouse.addBay(bay1);
        warehouse.addBay(bay2);

        assertEquals(bay1, warehouse.getBay("WH01-1-1"));
        assertEquals(bay2, warehouse.getBay("WH01-2-5"));
        assertNull(warehouse.getBay("INVALID-FORMAT"));
        assertNull(warehouse.getBay("WH01-99-99"));
    }

    @Test
    void testInventoryGroupingBySku() {
        // Testar que getInventoryBySku agrupa corretamente por SKU
        LocalDateTime now = LocalDateTime.now();

        allocator.allocateBoxesToWarehouse(List.of(
                new Box("BOX001", "SKU001", 5, null, now),
                new Box("BOX002", "SKU001", 3, null, now),
                new Box("BOX003", "SKU002", 7, null, now),
                new Box("BOX004", "SKU001", 2, null, now),
                new Box("BOX005", "SKU003", 4, null, now)
        ), warehouse);

        Map<String, List<Box>> inventory = warehouse.getInventoryBySku();

        assertEquals(3, inventory.size(), "Should have 3 different SKUs");
        assertEquals(3, inventory.get("SKU001").size(), "SKU001 should have 3 boxes");
        assertEquals(1, inventory.get("SKU002").size(), "SKU002 should have 1 box");
        assertEquals(1, inventory.get("SKU003").size(), "SKU003 should have 1 box");

        // Verificar quantidades totais
        int totalSku1 = inventory.get("SKU001").stream().mapToInt(Box::getQuantity).sum();
        assertEquals(10, totalSku1);
    }

    @Test
    void testPartialBoxConsumptionMaintainsFEFO() {
        // Testar que consumo parcial mantém ordenação FEFO
        LocalDateTime now = LocalDateTime.now();

        Box box1 = new Box("BOX001", "SKU001", 10, now.plusDays(5), now);
        Box box2 = new Box("BOX002", "SKU001", 10, now.plusDays(3), now); // Expira primeiro

        allocator.allocateBoxesToWarehouse(List.of(box1, box2), warehouse);

        // Consumir parcialmente do primeiro box (que deve ser BOX002 por FEFO)
        int dispatched = warehouse.dispatch("SKU001", 7);
        assertEquals(7, dispatched);

        Map<String, List<Box>> inventory = warehouse.getInventoryBySku();
        List<Box> remaining = inventory.get("SKU001");

        // BOX002 deve ter 3 unidades restantes e ainda ser o primeiro
        assertEquals(2, remaining.size());
        assertEquals("BOX002", remaining.get(0).getBoxId());
        assertEquals(3, remaining.get(0).getQuantity());
        assertEquals("BOX001", remaining.get(1).getBoxId());
        assertEquals(10, remaining.get(1).getQuantity());
    }
}