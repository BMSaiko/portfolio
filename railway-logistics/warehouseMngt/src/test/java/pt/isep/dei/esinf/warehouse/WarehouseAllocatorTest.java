package pt.isep.dei.esinf.warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.isep.dei.esinf.domain.warehouse.Bay;
import pt.isep.dei.esinf.domain.warehouse.Box;
import pt.isep.dei.esinf.domain.warehouse.Warehouse;
import pt.isep.dei.esinf.service.data.CsvImportService;
import pt.isep.dei.esinf.service.warehouse.WarehouseAllocator;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes da USEI01 com dados reais dos CSV
 */
class WarehouseAllocatorTest {

    private Warehouse warehouse;
    private WarehouseAllocator allocator;
    private CsvImportService csvImportService;

    @BeforeEach
    void setUp() throws Exception {
        warehouse = new Warehouse();
        allocator = new WarehouseAllocator();
        csvImportService = new CsvImportService();

        loadRealData();
    }

    private void loadRealData() throws Exception {
        // Carregar items
        InputStream itemsIS = getClass().getResourceAsStream("/data/items.csv");
        csvImportService.importItems(itemsIS);

        // Carregar bays
        InputStream baysIS = getClass().getResourceAsStream("/data/bays.csv");
        csvImportService.importBays(baysIS);
        csvImportService.getBays().values().forEach(warehouse::addBay);

        // Carregar wagons e alocar boxes
        InputStream wagonsIS = getClass().getResourceAsStream("/data/wagons.csv");
        csvImportService.importWagons(wagonsIS);
        allocator.allocateBoxesToWarehouse(csvImportService.getBoxes(), warehouse);
    }

    @Test
    void testWarehouseLoadedCorrectly() {
        // Verificar que o warehouse foi carregado corretamente
        assertFalse(warehouse.getAllBays().isEmpty());
        assertTrue(warehouse.getAllBays().size() >= 100); // Temos 100 bays no CSV

        Map<String, List<Box>> inventory = warehouse.getInventoryBySku();
        assertFalse(inventory.isEmpty());

        // Verificar que temos vários SKUs
        assertTrue(inventory.size() >= 10);

        // Verificar quantidades totais
        inventory.forEach((sku, boxes) -> {
            assertFalse(boxes.isEmpty(), "SKU " + sku + " should have boxes");
            int totalQty = boxes.stream().mapToInt(Box::getQuantity).sum();
            assertTrue(totalQty > 0, "SKU " + sku + " should have positive quantity");
        });
    }

    @Test
    void testFEFO_OrderingWithRealData() {
        Map<String, List<Box>> inventory = warehouse.getInventoryBySku();

        // Testar FEFO para um SKU com expiry dates
        String skuWithExpiry = "SKU0005"; // SKU que tem expiry dates nos dados
        if (inventory.containsKey(skuWithExpiry)) {
            List<Box> boxes = inventory.get(skuWithExpiry);

            // Verificar que estão ordenados por FEFO
            for (int i = 0; i < boxes.size() - 1; i++) {
                Box current = boxes.get(i);
                Box next = boxes.get(i + 1);

                // Se ambos têm expiry date, current deve expirar primeiro ou igual
                if (current.getExpiryDate() != null && next.getExpiryDate() != null) {
                    assertTrue(current.getExpiryDate().compareTo(next.getExpiryDate()) <= 0,
                            "Boxes should be ordered by expiry date (FEFO)");
                }
                // Se current tem expiry e next não tem, current deve vir primeiro
                else if (current.getExpiryDate() != null && next.getExpiryDate() == null) {
                    // OK - perishable before non-perishable
                }
                // Se current não tem expiry e next tem, é um erro!
                else if (current.getExpiryDate() == null && next.getExpiryDate() != null) {
                    fail("Non-perishable box should not come before perishable box");
                }
            }
        }
    }

    @Test
    void testDispatchWithRealData() {
        Map<String, List<Box>> inventoryBefore = warehouse.getInventoryBySku();

        // Encontrar um SKU com stock suficiente
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

            // Dispatch 5 unidades
            int dispatched = warehouse.dispatch(testSku, 5);

            // Verificar que dispatch retornou a quantidade correta
            assertEquals(5, dispatched, "Should dispatch requested quantity when available");

            Map<String, List<Box>> inventoryAfter = warehouse.getInventoryBySku();
            int totalAfter = inventoryAfter.get(testSku).stream()
                    .mapToInt(Box::getQuantity).sum();

            // Verificar que o inventário diminuiu corretamente
            assertEquals(totalBefore - 5, totalAfter,
                    "Inventory should decrease by dispatched quantity");
        }
    }

    @Test
    void testPartialDispatch() {
        Map<String, List<Box>> inventory = warehouse.getInventoryBySku();

        // Encontrar um SKU com stock conhecido
        String testSku = "SKU0001"; // Usar um SKU específico para teste consistente

        if (inventory.containsKey(testSku)) {
            int totalAvailable = inventory.get(testSku).stream()
                    .mapToInt(Box::getQuantity).sum();

            System.out.println("Testing partial dispatch for SKU: " + testSku);
            System.out.println("Total available: " + totalAvailable);

            // Pedir mais do que está disponível
            int requested = totalAvailable + 10;
            int dispatched = warehouse.dispatch(testSku, requested);

            System.out.println("Requested: " + requested);
            System.out.println("Dispatched: " + dispatched);

            // Deve retornar apenas o disponível
            assertEquals(totalAvailable, dispatched,
                    "Should dispatch only available quantity when request exceeds stock");

            // Verificar que o inventário ficou vazio para este SKU
            Map<String, List<Box>> inventoryAfter = warehouse.getInventoryBySku();

            if (inventoryAfter.containsKey(testSku)) {
                int totalAfter = inventoryAfter.get(testSku).stream()
                        .mapToInt(Box::getQuantity).sum();

                assertEquals(0, totalAfter, "Inventory should be empty after dispatching all stock");
            } else {
                // SKU pode ter sido removido se todo o stock foi despachado
                assertTrue(true, "SKU removed from inventory after complete dispatch");
            }
        } else {
            System.out.println("SKU0001 not found in inventory, skipping test");
        }
    }

    @Test
    void testBayCapacityRespected() {
        // Verificar que nenhum bay excede a capacidade
        for (Bay bay : warehouse.getAllBays()) {
            assertTrue(bay.getStoredBoxes().size() <= bay.getCapacityBoxes(),
                    "Bay " + bay.getLocationCode() + " should not exceed capacity");
        }
    }

    @Test
    void testBoxUniqueness() {
        // Verificar que todos os boxIds são únicos no warehouse
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
}