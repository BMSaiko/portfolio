package pt.isep.dei.esinf.orders;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.isep.dei.esinf.domain.*;
import pt.isep.dei.esinf.domain.enums.EligibilityMode;
import pt.isep.dei.esinf.domain.results.AllocationResult;
import pt.isep.dei.esinf.domain.enums.AllocationStatus;
import pt.isep.dei.esinf.domain.warehouse.*;
import pt.isep.dei.esinf.service.warehouse.OrderAllocationService;
import pt.isep.dei.esinf.service.warehouse.WarehouseAllocator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para edge cases e cenários específicos da USEI02
 */
class EdgeCasesOrderAllocationTest {

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
    }

    @Test
    void testEmptyOrderList() {
        // Testar com lista de orders vazia
        List<Order> emptyOrders = List.of();

        AllocationResult result = allocationService.allocateOrders(
                emptyOrders, warehouse, EligibilityMode.STRICT);

        assertNotNull(result);
        assertTrue(result.getEligibilities().isEmpty());
        assertTrue(result.getAllocations().isEmpty());
    }

    @Test
    void testOrderWithZeroQuantityLine() {
        // Order com linha de quantidade zero
        Order order = new Order("ORD_ZERO", LocalDate.now().plusDays(1), 1);
        order.addLine(new OrderLine(1, "SKU001", 0)); // Quantidade zero

        // Adicionar stock para garantir que não é problema de stock
        allocator.allocateBoxesToWarehouse(List.of(
                new Box("BOX001", "SKU001", 10, null, java.time.LocalDateTime.now())
        ), warehouse);

        AllocationResult result = allocationService.allocateOrders(
                List.of(order), warehouse, EligibilityMode.STRICT);

        assertEquals(1, result.getEligibilities().size());
        LineEligibility eligibility = result.getEligibilities().get(0);
        assertEquals(AllocationStatus.ELIGIBLE, eligibility.getStatus());
        assertEquals(0, eligibility.getAllocatedQty());
        assertEquals(0, eligibility.getRequestedQty());
    }

    @Test
    void testOrderWithNegativePriority() {
        // Testar order com prioridade negativa
        Order order = new Order("ORD_NEG_PRIO", LocalDate.now().plusDays(1), -1);
        order.addLine(new OrderLine(1, "SKU001", 5));

        allocator.allocateBoxesToWarehouse(List.of(
                new Box("BOX001", "SKU001", 10, null, java.time.LocalDateTime.now())
        ), warehouse);

        // Deve processar normalmente (prioridade negativa é válida)
        AllocationResult result = allocationService.allocateOrders(
                List.of(order), warehouse, EligibilityMode.STRICT);

        assertNotNull(result);
        assertEquals(1, result.getEligibilities().size());
    }

    @Test
    void testMultipleOrdersSamePriorityAndDueDate() {
        // Várias orders com mesma priority e dueDate - devem ordenar por orderId
        Order order1 = new Order("ORD_C", LocalDate.now().plusDays(1), 1);
        order1.addLine(new OrderLine(1, "SKU001", 2));

        Order order2 = new Order("ORD_A", LocalDate.now().plusDays(1), 1);
        order2.addLine(new OrderLine(1, "SKU001", 2));

        Order order3 = new Order("ORD_B", LocalDate.now().plusDays(1), 1);
        order3.addLine(new OrderLine(1, "SKU001", 2));

        allocator.allocateBoxesToWarehouse(List.of(
                new Box("BOX001", "SKU001", 10, null, java.time.LocalDateTime.now())
        ), warehouse);

        AllocationResult result = allocationService.allocateOrders(
                List.of(order1, order2, order3), warehouse, EligibilityMode.STRICT);

        // Verificar ordenação: ORD_A, ORD_B, ORD_C
        List<LineEligibility> eligibilities = result.getEligibilities();
        assertEquals("ORD_A", eligibilities.get(0).getOrderId());
        assertEquals("ORD_B", eligibilities.get(1).getOrderId());
        assertEquals("ORD_C", eligibilities.get(2).getOrderId());
    }

    @Test
    void testOrderLinesWithSameLineNo() {
        // Order com linhas com mesmo lineNo (cenário de erro)
        Order order = new Order("ORD_DUP_LINE", LocalDate.now().plusDays(1), 1);
        order.addLine(new OrderLine(1, "SKU001", 2));
        order.addLine(new OrderLine(1, "SKU002", 3)); // Mesmo lineNo!

        allocator.allocateBoxesToWarehouse(List.of(
                new Box("BOX001", "SKU001", 10, null, java.time.LocalDateTime.now()),
                new Box("BOX002", "SKU002", 10, null, java.time.LocalDateTime.now())
        ), warehouse);

        // Deve processar ambas as linhas (mesmo com lineNo duplicado)
        AllocationResult result = allocationService.allocateOrders(
                List.of(order), warehouse, EligibilityMode.STRICT);

        assertEquals(2, result.getEligibilities().size());

        // Ambas as linhas devem ter lineNo = 1
        assertEquals(1, result.getEligibilities().get(0).getLineNo());
        assertEquals(1, result.getEligibilities().get(1).getLineNo());
    }

    @Test
    void testFEFO_WithIdenticalExpiryDates() {
        // Boxes com exactamente a mesma expiry date - devem ordenar por receivedAt
        LocalDateTime now = java.time.LocalDateTime.now();
        LocalDateTime sameExpiry = now.plusDays(5);

        Box box1 = new Box("BOX001", "SKU001", 5, sameExpiry, now.minusHours(3)); // Mais antigo
        Box box2 = new Box("BOX002", "SKU001", 5, sameExpiry, now.minusHours(1)); // Mais recente
        Box box3 = new Box("BOX003", "SKU001", 5, sameExpiry, now.minusHours(5)); // Mais antigo

        allocator.allocateBoxesToWarehouse(List.of(box1, box2, box3), warehouse);

        Order order = new Order("ORD_FEFO", LocalDate.now().plusDays(1), 1);
        order.addLine(new OrderLine(1, "SKU001", 15));

        AllocationResult result = allocationService.allocateOrders(
                List.of(order), warehouse, EligibilityMode.STRICT);

        // Deve alocar na ordem: BOX003 (received mais antigo), BOX001, BOX002
        assertEquals(3, result.getAllocations().size());
        assertEquals("BOX003", result.getAllocations().get(0).getBoxId());
        assertEquals("BOX001", result.getAllocations().get(1).getBoxId());
        assertEquals("BOX002", result.getAllocations().get(2).getBoxId());
    }

    @Test
    void testMixedPerishableNonPerishableAllocation() {
        // Mix de boxes perishable e non-perishable
        LocalDateTime now = java.time.LocalDateTime.now();

        Box perishable1 = new Box("BOX_P1", "SKU001", 3, now.plusDays(3), now);
        Box nonPerishable = new Box("BOX_NP", "SKU001", 5, null, now.minusDays(1)); // Mais antigo
        Box perishable2 = new Box("BOX_P2", "SKU001", 4, now.plusDays(5), now);

        allocator.allocateBoxesToWarehouse(List.of(perishable1, nonPerishable, perishable2), warehouse);

        Order order = new Order("ORD_MIXED", LocalDate.now().plusDays(1), 1);
        order.addLine(new OrderLine(1, "SKU001", 10));

        AllocationResult result = allocationService.allocateOrders(
                List.of(order), warehouse, EligibilityMode.STRICT);

        // Deve alocar: perishable1 (expiry mais cedo), perishable2, nonPerishable (último)
        assertEquals(3, result.getAllocations().size());
        assertEquals("BOX_P1", result.getAllocations().get(0).getBoxId());
        assertEquals("BOX_P2", result.getAllocations().get(1).getBoxId());
        assertEquals("BOX_NP", result.getAllocations().get(2).getBoxId());
    }

    @Test
    void testAllocationRollbackInStrictMode() {
        // Order com múltiplas linhas onde uma falha em STRICT mode
        // Deve fazer rollback de todas as alocações da order
        LocalDateTime now = LocalDateTime.now();

        allocator.allocateBoxesToWarehouse(List.of(
                new Box("BOX001", "SKU001", 5, null, now),
                new Box("BOX002", "SKU002", 5, null, now)
        ), warehouse);

        Order order = new Order("ORD_ROLLBACK", LocalDate.now().plusDays(1), 1);
        order.addLine(new OrderLine(1, "SKU001", 3)); // Esta vai ser alocada inicialmente
        order.addLine(new OrderLine(2, "SKU002", 10)); // Esta vai falhar (stock insuficiente)
        order.addLine(new OrderLine(3, "SKU001", 2)); // Esta também vai ser alocada inicialmente

        AllocationResult result = allocationService.allocateOrders(
                List.of(order), warehouse, EligibilityMode.STRICT);

        // DEBUG: Mostrar resultados para diagnóstico
        System.out.println("=== DEBUG Rollback Test ===");
        for (LineEligibility eligibility : result.getEligibilities()) {
            System.out.printf("Order: %s, Line: %d, SKU: %s, Requested: %d, Allocated: %d, Status: %s%n",
                    eligibility.getOrderId(), eligibility.getLineNo(), eligibility.getSku(),
                    eligibility.getRequestedQty(), eligibility.getAllocatedQty(), eligibility.getStatus());
        }
        System.out.println("Total allocations: " + result.getAllocations().size());

        // Em STRICT mode, todas as linhas devem ser UNDISPATCHABLE porque uma falhou
        assertEquals(3, result.getEligibilities().size(), "Should have 3 eligibility records");

        for (LineEligibility eligibility : result.getEligibilities()) {
            assertEquals(AllocationStatus.UNDISPATCHABLE, eligibility.getStatus(),
                    "All lines should be UNDISPATCHABLE when one fails in STRICT mode");
            assertEquals(0, eligibility.getAllocatedQty(),
                    "All lines should have 0 allocated quantity when order fails in STRICT mode");
        }

        // Não deve haver alocações mantidas
        assertTrue(result.getAllocations().isEmpty(),
                "No allocations should be kept when order fails in STRICT mode");
    }

    @Test
    void testVeryLargeQuantities() {
        // Testar com quantidades muito grandes (evitar problemas de integer overflow)
        Order order = new Order("ORD_LARGE", LocalDate.now().plusDays(1), 1);
        order.addLine(new OrderLine(1, "SKU001", Integer.MAX_VALUE)); // Quantidade máxima

        allocator.allocateBoxesToWarehouse(List.of(
                new Box("BOX001", "SKU001", 10, null, java.time.LocalDateTime.now())
        ), warehouse);

        // Não deve lançar exceção
        AllocationResult result = allocationService.allocateOrders(
                List.of(order), warehouse, EligibilityMode.STRICT);

        assertNotNull(result);
        assertEquals(1, result.getEligibilities().size());
        assertEquals(AllocationStatus.UNDISPATCHABLE, result.getEligibilities().get(0).getStatus());
    }

    @Test
    void testOrderWithNonExistentSKU() {
        // Order com SKU que não existe no inventário
        Order order = new Order("ORD_UNKNOWN", LocalDate.now().plusDays(1), 1);
        order.addLine(new OrderLine(1, "SKU999", 5)); // SKU inexistente

        AllocationResult result = allocationService.allocateOrders(
                List.of(order), warehouse, EligibilityMode.STRICT);

        assertEquals(1, result.getEligibilities().size());
        LineEligibility eligibility = result.getEligibilities().get(0);
        assertEquals(AllocationStatus.UNDISPATCHABLE, eligibility.getStatus());
        assertEquals(0, eligibility.getAllocatedQty());
        assertEquals(5, eligibility.getRequestedQty());
    }

    @Test
    void testPartialModeDoesNotRollback() {
        // Em PARTIAL mode, as linhas bem sucedidas devem manter as alocações
        LocalDateTime now = LocalDateTime.now();

        allocator.allocateBoxesToWarehouse(List.of(
                new Box("BOX001", "SKU001", 5, null, now),
                new Box("BOX002", "SKU002", 5, null, now)
        ), warehouse);

        Order order = new Order("ORD_PARTIAL", LocalDate.now().plusDays(1), 1);
        order.addLine(new OrderLine(1, "SKU001", 3)); // ELIGIBLE
        order.addLine(new OrderLine(2, "SKU002", 10)); // PARTIAL (5 allocated)
        order.addLine(new OrderLine(3, "SKU001", 2)); // ELIGIBLE

        AllocationResult result = allocationService.allocateOrders(
                List.of(order), warehouse, EligibilityMode.PARTIAL);

        // DEBUG
        System.out.println("=== DEBUG Partial Mode Test ===");
        for (LineEligibility eligibility : result.getEligibilities()) {
            System.out.printf("Order: %s, Line: %d, SKU: %s, Requested: %d, Allocated: %d, Status: %s%n",
                    eligibility.getOrderId(), eligibility.getLineNo(), eligibility.getSku(),
                    eligibility.getRequestedQty(), eligibility.getAllocatedQty(), eligibility.getStatus());
        }

        // Em PARTIAL mode, as linhas bem sucedidas mantêm as alocações
        assertEquals(3, result.getEligibilities().size());

        // Linha 1: ELIGIBLE
        LineEligibility line1 = getEligibilityByLineNo(result, 1);
        assertEquals(AllocationStatus.ELIGIBLE, line1.getStatus());
        assertEquals(3, line1.getAllocatedQty());

        // Linha 2: PARTIAL
        LineEligibility line2 = getEligibilityByLineNo(result, 2);
        assertEquals(AllocationStatus.PARTIAL, line2.getStatus());
        assertEquals(5, line2.getAllocatedQty()); // Apenas 5 de 10

        // Linha 3: ELIGIBLE
        LineEligibility line3 = getEligibilityByLineNo(result, 3);
        assertEquals(AllocationStatus.ELIGIBLE, line3.getStatus());
        assertEquals(2, line3.getAllocatedQty());

        // Deve haver alocações mantidas
        assertFalse(result.getAllocations().isEmpty());

        // Verificar que temos alocações para as linhas 1 e 3 (ELIGIBLE) e linha 2 (PARTIAL)
        long allocationsForLine1 = result.getAllocations().stream()
                .filter(alloc -> alloc.getLineNo() == 1)
                .count();
        long allocationsForLine2 = result.getAllocations().stream()
                .filter(alloc -> alloc.getLineNo() == 2)
                .count();
        long allocationsForLine3 = result.getAllocations().stream()
                .filter(alloc -> alloc.getLineNo() == 3)
                .count();

        assertTrue(allocationsForLine1 > 0, "Should have allocations for line 1");
        assertTrue(allocationsForLine2 > 0, "Should have allocations for line 2");
        assertTrue(allocationsForLine3 > 0, "Should have allocations for line 3");
    }

    /**
     * Método auxiliar para obter eligibility por lineNo
     */
    private LineEligibility getEligibilityByLineNo(AllocationResult result, int lineNo) {
        return result.getEligibilities().stream()
                .filter(e -> e.getLineNo() == lineNo)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Line " + lineNo + " not found"));
    }
}