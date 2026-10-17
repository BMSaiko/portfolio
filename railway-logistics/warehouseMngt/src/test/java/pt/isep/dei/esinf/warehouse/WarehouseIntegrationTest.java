package pt.isep.dei.esinf.warehouse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.isep.dei.esinf.domain.allocations.AllocationFragment;
import pt.isep.dei.esinf.domain.enums.EligibilityMode;
import pt.isep.dei.esinf.domain.results.AllocationResult;
import pt.isep.dei.esinf.domain.warehouse.*;
import pt.isep.dei.esinf.service.warehouse.OrderAllocationService;
import pt.isep.dei.esinf.service.warehouse.WarehouseAllocator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração entre USEI01 e USEI02
 */
class WarehouseIntegrationTest {

    private Warehouse warehouse;
    private OrderAllocationService allocationService;
    private WarehouseAllocator allocator;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse();
        allocationService = new OrderAllocationService();
        allocator = new WarehouseAllocator();

        // Configurar warehouse para testes de integração
        warehouse.addBay(new Bay("WH01", 1, 1, 10));
        warehouse.addBay(new Bay("WH01", 1, 2, 10));
        warehouse.addBay(new Bay("WH01", 2, 1, 10));
    }

    @Test
    void testCompleteWorkflow() {
        // Teste completo: Alocar -> Order Allocation -> Dispatch -> Verificar estado
        LocalDateTime now = LocalDateTime.now();

        // 1. Alocar boxes
        allocator.allocateBoxesToWarehouse(List.of(
                new Box("BOX001", "SKU001", 20, now.plusDays(5), now),
                new Box("BOX002", "SKU002", 15, null, now.minusDays(1)),
                new Box("BOX003", "SKU001", 10, now.plusDays(3), now.minusHours(2))
        ), warehouse);

        // DEBUG: Verificar stock inicial
        Map<String, List<Box>> initialInventory = warehouse.getInventoryBySku();
        int initialSku1 = initialInventory.get("SKU001").stream()
                .mapToInt(Box::getQuantity)
                .sum();
        int initialSku2 = initialInventory.get("SKU002").stream()
                .mapToInt(Box::getQuantity)
                .sum();

        System.out.println("=== DEBUG Workflow Test ===");
        System.out.println("Initial SKU001 stock: " + initialSku1); // 20 + 10 = 30
        System.out.println("Initial SKU002 stock: " + initialSku2); // 15

        // 2. Criar e processar orders
        Order order1 = new Order("ORD001", LocalDate.now().plusDays(1), 1);
        order1.addLine(new OrderLine(1, "SKU001", 25)); // Vai usar 25 de 30 disponíveis
        order1.addLine(new OrderLine(2, "SKU002", 10)); // Vai usar 10 de 15 disponíveis

        Order order2 = new Order("ORD002", LocalDate.now().plusDays(2), 2);
        order2.addLine(new OrderLine(1, "SKU001", 5));  // Vai usar 5 de 5 restantes

        AllocationResult result = allocationService.allocateOrders(
                List.of(order1, order2), warehouse, EligibilityMode.PARTIAL);

        // DEBUG: Verificar resultados da alocação
        System.out.println("Allocation results:");
        System.out.println("Total eligibilities: " + result.getEligibilities().size());
        System.out.println("Total allocations: " + result.getAllocations().size());

        result.getEligibilities().forEach(e -> {
            System.out.printf("  Order: %s, Line: %d, SKU: %s, Req: %d, Alloc: %d, Status: %s%n",
                    e.getOrderId(), e.getLineNo(), e.getSku(),
                    e.getRequestedQty(), e.getAllocatedQty(), e.getStatus());
        });

        result.getAllocations().forEach(a -> {
            System.out.printf("  Allocation: Order: %s, Line: %d, SKU: %s, Qty: %d, Box: %s%n",
                    a.getOrderId(), a.getLineNo(), a.getSku(), a.getQty(), a.getBoxId());
        });

        // 3. Verificar alocações
        assertEquals(3, result.getEligibilities().size());

        // Calcular total de alocações corretamente
        int totalAllocatedSku1 = result.getAllocations().stream()
                .filter(alloc -> alloc.getSku().equals("SKU001"))
                .mapToInt(AllocationFragment::getQty)
                .sum();
        int totalAllocatedSku2 = result.getAllocations().stream()
                .filter(alloc -> alloc.getSku().equals("SKU002"))
                .mapToInt(AllocationFragment::getQty)
                .sum();

        System.out.println("Total allocated SKU001: " + totalAllocatedSku1);
        System.out.println("Total allocated SKU002: " + totalAllocatedSku2);

        // 4. Executar dispatch baseado nas alocações
        int totalDispatched = 0;
        for (AllocationFragment allocation : result.getAllocations()) {
            int dispatched = warehouse.dispatch(allocation.getSku(), allocation.getQty());
            totalDispatched += dispatched;
            System.out.printf("Dispatched: SKU %s, Qty %d (from allocation %d)%n",
                    allocation.getSku(), dispatched, allocation.getQty());
        }

        System.out.println("Total dispatched: " + totalDispatched);

        // O total dispatched deve ser a soma de todas as alocações
        int expectedTotalDispatched = result.getAllocations().stream()
                .mapToInt(AllocationFragment::getQty)
                .sum();

        assertEquals(expectedTotalDispatched, totalDispatched,
                "Total dispatched should match total allocated quantities");

        // 5. Verificar estado final do warehouse
        Map<String, List<Box>> finalInventory = warehouse.getInventoryBySku();

        // CORREÇÃO: Verificar se os SKUs ainda existem no inventory (podem ter sido removidos se stock = 0)
        int finalSku1 = 0;
        int finalSku2 = 0;

        if (finalInventory.containsKey("SKU001")) {
            finalSku1 = finalInventory.get("SKU001").stream()
                    .mapToInt(Box::getQuantity)
                    .sum();
        }

        if (finalInventory.containsKey("SKU002")) {
            finalSku2 = finalInventory.get("SKU002").stream()
                    .mapToInt(Box::getQuantity)
                    .sum();
        }

        System.out.println("Final SKU001 stock: " + finalSku1);
        System.out.println("Final SKU002 stock: " + finalSku2);

        // Cálculo correto dos stocks finais esperados
        int expectedFinalSku1 = initialSku1 - totalAllocatedSku1;
        int expectedFinalSku2 = initialSku2 - totalAllocatedSku2;

        System.out.println("Expected final SKU001: " + expectedFinalSku1);
        System.out.println("Expected final SKU002: " + expectedFinalSku2);

        assertEquals(expectedFinalSku1, finalSku1,
                "Final SKU001 stock should be initial minus allocated");
        assertEquals(expectedFinalSku2, finalSku2,
                "Final SKU002 stock should be initial minus allocated");

        // Verificação adicional - garantir que o dispatch foi bem sucedido
        assertTrue(totalDispatched > 0, "Should have dispatched some quantities");
        assertTrue(finalSku1 < initialSku1, "SKU001 stock should have decreased");
        assertTrue(finalSku2 < initialSku2, "SKU002 stock should have decreased");

        // CORREÇÃO: Verificações específicas baseadas no que sabemos do teste
        // SKU001: 30 inicial - 30 alocado = 0 final
        assertEquals(0, finalSku1, "SKU001 should be completely depleted");
        // SKU002: 15 inicial - 10 alocado = 5 final
        assertEquals(5, finalSku2, "SKU002 should have 5 units remaining");

        // CORREÇÃO: Verificar que o total dispatched está correto
        assertEquals(40, totalDispatched, "Total dispatched should be 40 (30 SKU001 + 10 SKU002)");
    }

    @Test
    void testFEFO_ConsistencyBetweenAllocationAndDispatch() {
        // Verificar que Allocation e Dispatch usam a mesma ordem FEFO
        LocalDateTime now = LocalDateTime.now();

        // Boxes com diferentes expiry dates
        Box earlyExpiry = new Box("BOX_EARLY", "SKU001", 10, now.plusDays(1), now);
        Box mediumExpiry = new Box("BOX_MEDIUM", "SKU001", 10, now.plusDays(3), now);
        Box lateExpiry = new Box("BOX_LATE", "SKU001", 10, now.plusDays(5), now);

        allocator.allocateBoxesToWarehouse(List.of(earlyExpiry, mediumExpiry, lateExpiry), warehouse);

        // Order que vai usar FEFO
        Order order = new Order("ORD_FEFO", LocalDate.now().plusDays(1), 1);
        order.addLine(new OrderLine(1, "SKU001", 15));

        AllocationResult allocationResult = allocationService.allocateOrders(
                List.of(order), warehouse, EligibilityMode.STRICT);

        // Verificar que alocou dos boxes com expiry mais cedo primeiro
        List<String> allocatedBoxes = allocationResult.getAllocations().stream()
                .map(alloc -> alloc.getBoxId())
                .toList();

        // Primeira alocação deve ser do BOX_EARLY
        assertEquals("BOX_EARLY", allocatedBoxes.get(0));
        // Segunda alocação deve ser parcial do BOX_MEDIUM
        assertEquals("BOX_MEDIUM", allocatedBoxes.get(1));

        // Agora fazer dispatch e verificar que usa mesma ordem
        int dispatched = warehouse.dispatch("SKU001", 15);
        assertEquals(15, dispatched);

        // Verificar estado dos boxes restantes
        Map<String, List<Box>> inventory = warehouse.getInventoryBySku();
        List<Box> remaining = inventory.get("SKU001");

        // BOX_EARLY e BOX_MEDIUM devem estar consumidos, BOX_LATE deve ter 5 unidades
        assertEquals(2, remaining.size());
        assertEquals("BOX_MEDIUM", remaining.get(0).getBoxId());
        assertEquals(5, remaining.get(0).getQuantity());
        assertEquals("BOX_LATE", remaining.get(1).getBoxId());
        assertEquals(10, remaining.get(1).getQuantity());
    }

    @Test
    void testInventorySnapshotIsolation() {
        // Verificar que Order Allocation não afeta o warehouse real
        LocalDateTime now = LocalDateTime.now();

        allocator.allocateBoxesToWarehouse(List.of(
                new Box("BOX001", "SKU001", 10, null, now),
                new Box("BOX002", "SKU002", 10, null, now)
        ), warehouse);

        // Capturar estado inicial
        Map<String, List<Box>> inventoryBefore = warehouse.getInventoryBySku();
        int initialSku1 = inventoryBefore.get("SKU001").stream()
                .mapToInt(Box::getQuantity)
                .sum();

        // Processar orders (não deve alterar warehouse)
        Order order = new Order("ORD001", LocalDate.now().plusDays(1), 1);
        order.addLine(new OrderLine(1, "SKU001", 5));

        allocationService.allocateOrders(
                List.of(order), warehouse, EligibilityMode.STRICT);

        // Verificar que warehouse permanece inalterado
        Map<String, List<Box>> inventoryAfter = warehouse.getInventoryBySku();
        int finalSku1 = inventoryAfter.get("SKU001").stream()
                .mapToInt(Box::getQuantity)
                .sum();

        assertEquals(initialSku1, finalSku1, "Warehouse stock should not change after allocation planning");

        // Agora fazer dispatch real
        int dispatched = warehouse.dispatch("SKU001", 5);
        assertEquals(5, dispatched);

        // Verificar que agora sim alterou
        Map<String, List<Box>> inventoryAfterDispatch = warehouse.getInventoryBySku();
        int finalAfterDispatch = inventoryAfterDispatch.get("SKU001").stream()
                .mapToInt(Box::getQuantity)
                .sum();
        assertEquals(initialSku1 - 5, finalAfterDispatch);
    }
}