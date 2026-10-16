package pt.isep.dei.esinf.orders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.isep.dei.esinf.domain.*;
import pt.isep.dei.esinf.domain.allocations.AllocationFragment;
import pt.isep.dei.esinf.domain.enums.EligibilityMode;
import pt.isep.dei.esinf.domain.results.AllocationResult;
import pt.isep.dei.esinf.domain.enums.AllocationStatus;
import pt.isep.dei.esinf.domain.warehouse.*;
import pt.isep.dei.esinf.service.warehouse.OrderAllocationService;
import pt.isep.dei.esinf.service.warehouse.WarehouseAllocator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de integração para OrderAllocationService (USEI02)
 * Testa a integração com Warehouse, Bay, Box e outros componentes
 */
class OrderAllocationIntegrationTest {

    private Warehouse warehouse;
    private OrderAllocationService allocationService;
    private WarehouseAllocator allocator;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse();
        allocationService = new OrderAllocationService();
        allocator = new WarehouseAllocator();

        // Configurar warehouse básico para testes
        warehouse.addBay(new Bay("WH01", 1, 1, 10));
        warehouse.addBay(new Bay("WH01", 1, 2, 10));
        warehouse.addBay(new Bay("WH01", 2, 1, 10));
        warehouse.addBay(new Bay("WH01", 2, 2, 10));
    }

    @Test
    void testIntegrationWithRealWarehouseState() {
        // Teste que verifica a integração com o estado real do warehouse
        LocalDateTime now = LocalDateTime.now();

        // Configurar inventário complexo
        allocator.allocateBoxesToWarehouse(List.of(
                new Box("BOX001", "SKU001", 15, now.plusDays(5), now.minusHours(5)),
                new Box("BOX002", "SKU001", 10, now.plusDays(3), now.minusHours(3)), // Expira primeiro
                new Box("BOX003", "SKU002", 20, null, now.minusDays(1)),
                new Box("BOX004", "SKU001", 5, now.plusDays(10), now.minusHours(1)),
                new Box("BOX005", "SKU003", 8, now.plusDays(7), now)
        ), warehouse);

        // DEBUG: Verificar stock inicial
        Map<String, List<Box>> initialInventory = warehouse.getInventoryBySku();
        int initialSku1 = getStockForSku(initialInventory, "SKU001");
        System.out.println("Initial SKU001 stock: " + initialSku1); // 15 + 10 + 5 = 30

        // Criar orders que vão testar vários aspectos
        Order highPriorityOrder = new Order("ORD_HIGH", LocalDate.now().plusDays(1), 1);
        highPriorityOrder.addLine(new OrderLine(1, "SKU001", 12));
        highPriorityOrder.addLine(new OrderLine(2, "SKU002", 5));

        Order mediumPriorityOrder = new Order("ORD_MEDIUM", LocalDate.now().plusDays(1), 2);
        mediumPriorityOrder.addLine(new OrderLine(1, "SKU001", 10));
        mediumPriorityOrder.addLine(new OrderLine(2, "SKU003", 15)); // Stock insuficiente (só 8 disponíveis)

        Order lowPriorityOrder = new Order("ORD_LOW", LocalDate.now().plusDays(2), 3);
        lowPriorityOrder.addLine(new OrderLine(1, "SKU001", 8));

        List<Order> orders = List.of(highPriorityOrder, mediumPriorityOrder, lowPriorityOrder);

        // Act: Processar em STRICT mode
        AllocationResult result = allocationService.allocateOrders(
                orders, warehouse, EligibilityMode.STRICT);

        // DEBUG
        System.out.println("=== DEBUG Integration Test ===");
        result.getEligibilities().forEach(e -> {
            System.out.printf("Order: %s, Line: %d, SKU: %s, Req: %d, Alloc: %d, Status: %s%n",
                    e.getOrderId(), e.getLineNo(), e.getSku(),
                    e.getRequestedQty(), e.getAllocatedQty(), e.getStatus());
        });

        // Verificar alocações
        System.out.println("Total allocations: " + result.getAllocations().size());
        result.getAllocations().forEach(a -> {
            System.out.printf("  Allocation: Order: %s, Line: %d, SKU: %s, Qty: %d, Box: %s%n",
                    a.getOrderId(), a.getLineNo(), a.getSku(), a.getQty(), a.getBoxId());
        });

        // Assert: Verificar integração correta
        assertEquals(5, result.getEligibilities().size()); // 2 + 2 + 1 lines

        // Verificar ordenação por prioridade
        assertEquals("ORD_HIGH", result.getEligibilities().get(0).getOrderId());
        assertEquals("ORD_HIGH", result.getEligibilities().get(1).getOrderId());
        assertEquals("ORD_MEDIUM", result.getEligibilities().get(2).getOrderId());
        assertEquals("ORD_MEDIUM", result.getEligibilities().get(3).getOrderId());
        assertEquals("ORD_LOW", result.getEligibilities().get(4).getOrderId());

        // Verificar que ORD_MEDIUM linha 2 é UNDISPATCHABLE (stock insuficiente)
        LineEligibility mediumLine2 = result.getEligibilities().stream()
                .filter(e -> e.getOrderId().equals("ORD_MEDIUM") && e.getLineNo() == 2)
                .findFirst()
                .orElseThrow();
        assertEquals(AllocationStatus.UNDISPATCHABLE, mediumLine2.getStatus());
        assertEquals(0, mediumLine2.getAllocatedQty());

        // CORREÇÃO: Em STRICT mode, se ORD_MEDIUM linha 2 falha, toda a order ORD_MEDIUM deve ser rollback
        // Isso significa que ORD_MEDIUM linha 1 também deve ser UNDISPATCHABLE
        LineEligibility mediumLine1 = result.getEligibilities().stream()
                .filter(e -> e.getOrderId().equals("ORD_MEDIUM") && e.getLineNo() == 1)
                .findFirst()
                .orElseThrow();
        assertEquals(AllocationStatus.UNDISPATCHABLE, mediumLine1.getStatus());
        assertEquals(0, mediumLine1.getAllocatedQty());

        // CORREÇÃO: ORD_LOW deve ser ELIGIBLE porque há stock suficiente
        // Stock SKU001: 30 total
        // ORD_HIGH usa 12, ORD_MEDIUM falha (rollback), sobra 18 para ORD_LOW (mais que suficiente)
        LineEligibility lowOrderEligibility = result.getEligibilities().stream()
                .filter(e -> e.getOrderId().equals("ORD_LOW"))
                .findFirst()
                .orElseThrow();
        assertEquals(AllocationStatus.ELIGIBLE, lowOrderEligibility.getStatus());
        assertEquals(8, lowOrderEligibility.getAllocatedQty());

        // CORREÇÃO: Verificar alocações FEFO para SKU001
        List<AllocationFragment> sku001Allocations = result.getAllocations().stream()
                .filter(alloc -> alloc.getSku().equals("SKU001"))
                .toList();

        // Devemos ter alocações apenas para ORD_HIGH e ORD_LOW (ORD_MEDIUM foi rollback)
        // ORD_HIGH: 12 unidades
        // ORD_LOW: 8 unidades
        assertTrue(sku001Allocations.size() > 0, "Should have allocations for SKU001");

        // Verificar que as alocações são das orders corretas
        long highOrderAllocations = sku001Allocations.stream()
                .filter(alloc -> alloc.getOrderId().equals("ORD_HIGH"))
                .count();
        long lowOrderAllocations = sku001Allocations.stream()
                .filter(alloc -> alloc.getOrderId().equals("ORD_LOW"))
                .count();
        long mediumOrderAllocations = sku001Allocations.stream()
                .filter(alloc -> alloc.getOrderId().equals("ORD_MEDIUM"))
                .count();

        assertTrue(highOrderAllocations > 0, "Should have allocations for ORD_HIGH");
        assertTrue(lowOrderAllocations > 0, "Should have allocations for ORD_LOW");
        assertEquals(0, mediumOrderAllocations, "Should have NO allocations for ORD_MEDIUM (rollback)");
    }

    @Test
    void testIntegrationWithWarehouseDispatch() {
        // Teste que integra Order Allocation com operações reais de Dispatch
        LocalDateTime now = LocalDateTime.now();

        // Configurar inventário
        allocator.allocateBoxesToWarehouse(List.of(
                new Box("BOX001", "SKU001", 20, now.plusDays(5), now),
                new Box("BOX002", "SKU002", 15, null, now.minusDays(1)),
                new Box("BOX003", "SKU003", 25, now.plusDays(3), now.minusHours(2))
        ), warehouse);

        // Capturar estado inicial do warehouse
        Map<String, List<Box>> initialInventory = warehouse.getInventoryBySku();
        int initialSku1 = getStockForSku(initialInventory, "SKU001");
        int initialSku2 = getStockForSku(initialInventory, "SKU002");
        int initialSku3 = getStockForSku(initialInventory, "SKU003");

        // Fazer order allocation
        Order order = new Order("ORD001", LocalDate.now().plusDays(1), 1);
        order.addLine(new OrderLine(1, "SKU001", 15));
        order.addLine(new OrderLine(2, "SKU002", 10));
        order.addLine(new OrderLine(3, "SKU003", 30)); // Stock insuficiente (só 25)

        AllocationResult allocationResult = allocationService.allocateOrders(
                List.of(order), warehouse, EligibilityMode.PARTIAL);

        // Verificar que order allocation não alterou o warehouse (non-destructive)
        Map<String, List<Box>> inventoryAfterAllocation = warehouse.getInventoryBySku();
        int sku1AfterAllocation = getStockForSku(inventoryAfterAllocation, "SKU001");
        int sku2AfterAllocation = getStockForSku(inventoryAfterAllocation, "SKU002");
        int sku3AfterAllocation = getStockForSku(inventoryAfterAllocation, "SKU003");

        assertEquals(initialSku1, sku1AfterAllocation,
                "Order allocation should not modify actual warehouse inventory - SKU001");
        assertEquals(initialSku2, sku2AfterAllocation,
                "Order allocation should not modify actual warehouse inventory - SKU002");
        assertEquals(initialSku3, sku3AfterAllocation,
                "Order allocation should not modify actual warehouse inventory - SKU003");

        // Agora executar dispatch baseado nas alocações
        int totalDispatched = 0;
        for (AllocationFragment allocation : allocationResult.getAllocations()) {
            int dispatched = warehouse.dispatch(allocation.getSku(), allocation.getQty());
            assertEquals(allocation.getQty(), dispatched,
                    "Dispatch should match allocation quantity for " + allocation.getSku());
            totalDispatched += dispatched;
        }

        // Verificar estado final do warehouse
        Map<String, List<Box>> finalInventory = warehouse.getInventoryBySku();
        int finalSku1 = getStockForSku(finalInventory, "SKU001");
        int finalSku2 = getStockForSku(finalInventory, "SKU002");
        int finalSku3 = getStockForSku(finalInventory, "SKU003");

        // CORREÇÃO: Cálculos corretos
        // SKU001: 20 inicial - 15 alocado = 5 final
        // SKU002: 15 inicial - 10 alocado = 5 final
        // SKU003: 25 inicial - 25 alocado (PARTIAL mode) = 0 final
        assertEquals(5, finalSku1);
        assertEquals(5, finalSku2);
        assertEquals(0, finalSku3); // Foi alocado todo o stock disponível em PARTIAL mode
    }

    @Test
    void testIntegrationWithMultipleOrderProcessingRounds() {
        // Testar processamento sequencial de orders (cenário real)
        LocalDateTime now = LocalDateTime.now();

        allocator.allocateBoxesToWarehouse(List.of(
                new Box("BOX001", "SKU001", 50, null, now),
                new Box("BOX002", "SKU002", 30, null, now.minusHours(1))
        ), warehouse);

        // DEBUG: Stock inicial
        Map<String, List<Box>> initialInventory = warehouse.getInventoryBySku();
        int initialSku1 = getStockForSku(initialInventory, "SKU001");
        int initialSku2 = getStockForSku(initialInventory, "SKU002");
        System.out.println("Initial SKU001: " + initialSku1 + ", SKU002: " + initialSku2);

        // Primeira ronda de orders
        Order round1Order1 = new Order("ORD_R1_1", LocalDate.now().plusDays(1), 1);
        round1Order1.addLine(new OrderLine(1, "SKU001", 20));

        Order round1Order2 = new Order("ORD_R1_2", LocalDate.now().plusDays(1), 1);
        round1Order2.addLine(new OrderLine(1, "SKU001", 15));

        AllocationResult round1Result = allocationService.allocateOrders(
                List.of(round1Order1, round1Order2), warehouse, EligibilityMode.STRICT);

        // Executar dispatch da primeira ronda
        int round1Dispatched = 0;
        for (AllocationFragment allocation : round1Result.getAllocations()) {
            int dispatched = warehouse.dispatch(allocation.getSku(), allocation.getQty());
            round1Dispatched += dispatched;
        }
        System.out.println("Round 1 dispatched: " + round1Dispatched + " units");

        // Verificar stock após primeira ronda
        Map<String, List<Box>> afterRound1 = warehouse.getInventoryBySku();
        int afterRound1Sku1 = getStockForSku(afterRound1, "SKU001");
        int afterRound1Sku2 = getStockForSku(afterRound1, "SKU002");
        System.out.println("After round 1 - SKU001: " + afterRound1Sku1 + ", SKU002: " + afterRound1Sku2);

        // Segunda ronda de orders (com stock reduzido)
        Order round2Order1 = new Order("ORD_R2_1", LocalDate.now().plusDays(2), 1);
        round2Order1.addLine(new OrderLine(1, "SKU001", 20)); // Stock insuficiente agora (só 15 disponíveis)

        Order round2Order2 = new Order("ORD_R2_2", LocalDate.now().plusDays(2), 1);
        round2Order2.addLine(new OrderLine(1, "SKU002", 10));

        AllocationResult round2Result = allocationService.allocateOrders(
                List.of(round2Order1, round2Order2), warehouse, EligibilityMode.STRICT);

        // DEBUG
        System.out.println("=== DEBUG Multiple Rounds ===");
        round2Result.getEligibilities().forEach(e -> {
            System.out.printf("Order: %s, Line: %d, SKU: %s, Req: %d, Alloc: %d, Status: %s%n",
                    e.getOrderId(), e.getLineNo(), e.getSku(),
                    e.getRequestedQty(), e.getAllocatedQty(), e.getStatus());
        });

        // Verificar resultados da segunda ronda
        LineEligibility round2Line1 = round2Result.getEligibilities().stream()
                .filter(e -> e.getOrderId().equals("ORD_R2_1"))
                .findFirst()
                .orElseThrow();
        assertEquals(AllocationStatus.UNDISPATCHABLE, round2Line1.getStatus());

        LineEligibility round2Line2 = round2Result.getEligibilities().stream()
                .filter(e -> e.getOrderId().equals("ORD_R2_2"))
                .findFirst()
                .orElseThrow();
        assertEquals(AllocationStatus.ELIGIBLE, round2Line2.getStatus());

        // Executar dispatch da segunda ronda (apenas para ORD_R2_2)
        int round2Dispatched = 0;
        for (AllocationFragment allocation : round2Result.getAllocations()) {
            int dispatched = warehouse.dispatch(allocation.getSku(), allocation.getQty());
            round2Dispatched += dispatched;
        }
        System.out.println("Round 2 dispatched: " + round2Dispatched + " units");

        // Verificar stock final
        Map<String, List<Box>> finalInventory = warehouse.getInventoryBySku();
        int finalSku1 = getStockForSku(finalInventory, "SKU001");
        int finalSku2 = getStockForSku(finalInventory, "SKU002");

        System.out.println("Final stock - SKU001: " + finalSku1 + ", SKU002: " + finalSku2);

        // CORREÇÃO: Cálculos corretos
        // SKU001: 50 inicial - 20 (R1_1) - 15 (R1_2) = 15 final (ORD_R2_1 não foi despachado)
        // SKU002: 30 inicial - 10 (R2_2) = 20 final
        assertEquals(15, finalSku1, "SKU001 should have 15 units remaining");
        assertEquals(20, finalSku2, "SKU002 should have 20 units remaining");

        // CORREÇÃO: Verificar totais despachados
        assertEquals(35, round1Dispatched, "Round 1 should dispatch 35 units (20 + 15)");
        assertEquals(10, round2Dispatched, "Round 2 should dispatch 10 units (only ORD_R2_2)");
    }

    @Test
    void testIntegrationWithComplexOrderSequencing() {
        // Testar ordenação complexa de orders (priority + dueDate + orderId)
        LocalDateTime now = LocalDateTime.now();

        allocator.allocateBoxesToWarehouse(List.of(
                new Box("BOX001", "SKU001", 10, null, now)
        ), warehouse);

        // Orders com diferentes combinações de priority/dueDate/orderId
        Order order1 = new Order("ORD_C", LocalDate.now().plusDays(3), 1); // Prioridade 1, dueDate mais tarde
        order1.addLine(new OrderLine(1, "SKU001", 3));

        Order order2 = new Order("ORD_A", LocalDate.now().plusDays(1), 2); // Prioridade 2, dueDate mais cedo
        order2.addLine(new OrderLine(1, "SKU001", 3));

        Order order3 = new Order("ORD_B", LocalDate.now().plusDays(1), 1); // Prioridade 1, dueDate mais cedo
        order3.addLine(new OrderLine(1, "SKU001", 3));

        Order order4 = new Order("ORD_D", LocalDate.now().plusDays(2), 1); // Prioridade 1, dueDate intermédio
        order4.addLine(new OrderLine(1, "SKU001", 3));

        // Processar em ordem "aleatória"
        List<Order> orders = List.of(order1, order2, order3, order4);

        AllocationResult result = allocationService.allocateOrders(
                orders, warehouse, EligibilityMode.STRICT);

        // Verificar ordenação correta:
        // 1. ORD_B (priority 1, dueDate mais cedo)
        // 2. ORD_D (priority 1, dueDate intermédio)
        // 3. ORD_C (priority 1, dueDate mais tarde)
        // 4. ORD_A (priority 2)
        assertEquals("ORD_B", result.getEligibilities().get(0).getOrderId());
        assertEquals("ORD_D", result.getEligibilities().get(1).getOrderId());
        assertEquals("ORD_C", result.getEligibilities().get(2).getOrderId());
        assertEquals("ORD_A", result.getEligibilities().get(3).getOrderId());

        // Verificar que apenas as 3 primeiras orders são ELIGIBLE (stock limitado)
        assertEquals(AllocationStatus.ELIGIBLE, result.getEligibilities().get(0).getStatus());
        assertEquals(AllocationStatus.ELIGIBLE, result.getEligibilities().get(1).getStatus());
        assertEquals(AllocationStatus.ELIGIBLE, result.getEligibilities().get(2).getStatus());
        assertEquals(AllocationStatus.UNDISPATCHABLE, result.getEligibilities().get(3).getStatus());
    }

    @Test
    void testIntegrationWithBayAndAisleInformation() {
        // Testar que as alocações incluem informação correta de bay/aisle
        LocalDateTime now = LocalDateTime.now();

        // Boxes em bays diferentes
        Box box1 = new Box("BOX001", "SKU001", 10, null, now);
        Box box2 = new Box("BOX002", "SKU001", 10, null, now);
        Box box3 = new Box("BOX003", "SKU002", 10, null, now);

        allocator.allocateBoxesToWarehouse(List.of(box1, box2, box3), warehouse);

        Order order = new Order("ORD001", LocalDate.now().plusDays(1), 1);
        order.addLine(new OrderLine(1, "SKU001", 15)); // Vai consumir de 2 boxes
        order.addLine(new OrderLine(2, "SKU002", 5));

        AllocationResult result = allocationService.allocateOrders(
                List.of(order), warehouse, EligibilityMode.STRICT);

        // Verificar que as alocações têm informação correta de localização
        List<AllocationFragment> allocations = result.getAllocations();
        assertEquals(3, allocations.size()); // 2 para SKU001 + 1 para SKU002

        // Verificar informações de bay/aisle
        for (AllocationFragment allocation : allocations) {
            assertNotNull(allocation.getAisle());
            assertNotNull(allocation.getBay());
            assertTrue(allocation.getAisle() >= 1 && allocation.getAisle() <= 2);
            assertTrue(allocation.getBay() >= 1 && allocation.getBay() <= 2);

            // Verificar que boxId corresponde a um box real
            String expectedBoxId = allocation.getBoxId();
            boolean boxExists = warehouse.getInventoryBySku().values().stream()
                    .flatMap(List::stream)
                    .anyMatch(box -> box.getBoxId().equals(expectedBoxId));
            assertTrue(boxExists, "Allocation references non-existent box: " + expectedBoxId);
        }

        // Verificar que SKU001 foi alocado de múltiplos boxes
        long sku1AllocationSources = allocations.stream()
                .filter(alloc -> alloc.getSku().equals("SKU001"))
                .map(AllocationFragment::getBoxId)
                .distinct()
                .count();
        assertEquals(2, sku1AllocationSources, "SKU001 should be allocated from 2 different boxes");
    }

    @Test
    void testIntegrationWithMixedEligibilityModes() {
        // Testar uso sequencial de diferentes modos de eligibility
        LocalDateTime now = LocalDateTime.now();

        allocator.allocateBoxesToWarehouse(List.of(
                new Box("BOX001", "SKU001", 25, null, now),
                new Box("BOX002", "SKU002", 15, null, now)
        ), warehouse);

        Order order = new Order("ORD001", LocalDate.now().plusDays(1), 1);
        order.addLine(new OrderLine(1, "SKU001", 30)); // Stock insuficiente
        order.addLine(new OrderLine(2, "SKU002", 10));

        // Primeiro em STRICT mode
        AllocationResult strictResult = allocationService.allocateOrders(
                List.of(order), warehouse, EligibilityMode.STRICT);

        // Verificar que em STRICT mode, toda a order é UNDISPATCHABLE
        assertEquals(2, strictResult.getEligibilities().size());
        assertTrue(strictResult.getEligibilities().stream()
                .allMatch(e -> e.getStatus() == AllocationStatus.UNDISPATCHABLE));
        assertTrue(strictResult.getAllocations().isEmpty());

        // Agora em PARTIAL mode
        AllocationResult partialResult = allocationService.allocateOrders(
                List.of(order), warehouse, EligibilityMode.PARTIAL);

        // Verificar que em PARTIAL mode, temos resultados diferentes
        assertEquals(2, partialResult.getEligibilities().size());

        LineEligibility line1Partial = partialResult.getEligibilities().stream()
                .filter(e -> e.getLineNo() == 1)
                .findFirst()
                .orElseThrow();
        assertEquals(AllocationStatus.PARTIAL, line1Partial.getStatus());
        assertEquals(25, line1Partial.getAllocatedQty()); // Stock disponível
        assertEquals(30, line1Partial.getRequestedQty());

        LineEligibility line2Partial = partialResult.getEligibilities().stream()
                .filter(e -> e.getLineNo() == 2)
                .findFirst()
                .orElseThrow();
        assertEquals(AllocationStatus.ELIGIBLE, line2Partial.getStatus());
        assertEquals(10, line2Partial.getAllocatedQty());

        // Verificar que temos alocações em PARTIAL mode
        assertFalse(partialResult.getAllocations().isEmpty());
        assertEquals(2, partialResult.getAllocations().size()); // 1 para cada SKU
    }

    @Test
    void testIntegrationErrorHandling() {
        // Testar comportamento com dados problemáticos
        LocalDateTime now = LocalDateTime.now();

        allocator.allocateBoxesToWarehouse(List.of(
                new Box("BOX001", "SKU001", 10, null, now)
        ), warehouse);

        // Order com SKU inexistente
        Order orderWithUnknownSku = new Order("ORD_UNKNOWN", LocalDate.now().plusDays(1), 1);
        orderWithUnknownSku.addLine(new OrderLine(1, "SKU999", 5)); // SKU não existe

        // Order com quantidade zero
        Order orderWithZeroQty = new Order("ORD_ZERO", LocalDate.now().plusDays(1), 1);
        orderWithZeroQty.addLine(new OrderLine(1, "SKU001", 0));

        // Order normal para comparação
        Order normalOrder = new Order("ORD_NORMAL", LocalDate.now().plusDays(1), 1);
        normalOrder.addLine(new OrderLine(1, "SKU001", 3));

        List<Order> mixedOrders = List.of(orderWithUnknownSku, orderWithZeroQty, normalOrder);

        // Não deve lançar exceção
        AllocationResult result = allocationService.allocateOrders(
                mixedOrders, warehouse, EligibilityMode.STRICT);

        // Verificar tratamento correto dos casos problemáticos
        assertEquals(3, result.getEligibilities().size());

        // SKU inexistente deve ser UNDISPATCHABLE
        LineEligibility unknownSkuEligibility = result.getEligibilities().stream()
                .filter(e -> e.getOrderId().equals("ORD_UNKNOWN"))
                .findFirst()
                .orElseThrow();
        assertEquals(AllocationStatus.UNDISPATCHABLE, unknownSkuEligibility.getStatus());
        assertEquals(0, unknownSkuEligibility.getAllocatedQty());

        // Quantidade zero deve ser ELIGIBLE com 0 alocado
        LineEligibility zeroQtyEligibility = result.getEligibilities().stream()
                .filter(e -> e.getOrderId().equals("ORD_ZERO"))
                .findFirst()
                .orElseThrow();
        assertEquals(AllocationStatus.ELIGIBLE, zeroQtyEligibility.getStatus());
        assertEquals(0, zeroQtyEligibility.getAllocatedQty());
        assertEquals(0, zeroQtyEligibility.getRequestedQty());

        // Order normal deve funcionar
        LineEligibility normalEligibility = result.getEligibilities().stream()
                .filter(e -> e.getOrderId().equals("ORD_NORMAL"))
                .findFirst()
                .orElseThrow();
        assertEquals(AllocationStatus.ELIGIBLE, normalEligibility.getStatus());
        assertEquals(3, normalEligibility.getAllocatedQty());
    }

    /**
     * Método auxiliar para obter stock de um SKU com null safety
     */
    private int getStockForSku(Map<String, List<Box>> inventory, String sku) {
        if (inventory.containsKey(sku)) {
            return inventory.get(sku).stream()
                    .mapToInt(Box::getQuantity)
                    .sum();
        }
        return 0;
    }
}