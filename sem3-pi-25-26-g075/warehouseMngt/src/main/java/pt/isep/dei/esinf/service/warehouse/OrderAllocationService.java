package pt.isep.dei.esinf.service.warehouse;

import pt.isep.dei.esinf.domain.*;
import pt.isep.dei.esinf.domain.allocations.AllocationFragment;
import pt.isep.dei.esinf.domain.allocations.BoxAllocation;
import pt.isep.dei.esinf.domain.enums.AllocationStatus;
import pt.isep.dei.esinf.domain.enums.EligibilityMode;
import pt.isep.dei.esinf.domain.results.AllocationResult;
import pt.isep.dei.esinf.domain.warehouse.*;

import java.util.*;

public class OrderAllocationService {

    /**
     * Allocates orders based on current warehouse inventory using FEFO/FIFO order.
     *
     * @param orders    List of orders to process
     * @param warehouse Warehouse with current inventory
     * @param mode      Eligibility mode (STRICT or PARTIAL)
     * @return AllocationResult with eligibilities and allocations
     */
    public AllocationResult allocateOrders(List<Order> orders, Warehouse warehouse, EligibilityMode mode) {
        // Ordenar orders
        List<Order> sortedOrders = new ArrayList<>(orders);
        Collections.sort(sortedOrders);

        List<LineEligibility> eligibilities = new ArrayList<>();
        List<AllocationFragment> allocations = new ArrayList<>();

        // Criar snapshot do inventário
        Map<String, List<BoxAllocation>> inventorySnapshot = createInventorySnapshot(warehouse);

        // Processar cada order
        for (Order order : sortedOrders) {
            // Ordenar linhas por lineNo
            List<OrderLine> sortedLines = new ArrayList<>(order.getLines());
            Collections.sort(sortedLines);

            // Lista temporária para guardar alocações desta order
            List<AllocationFragment> orderAllocations = new ArrayList<>();
            List<LineEligibility> orderEligibilities = new ArrayList<>();

            boolean orderFailed = false;

            // Processar cada linha
            for (OrderLine line : sortedLines) {
                List<AllocationFragment> lineAllocations = new ArrayList<>();
                int remainingQty = line.getRequestedQty();
                int allocatedQty = 0;

                // Alocar dos boxes em ordem FEFO/FIFO
                List<BoxAllocation> skuBoxes = inventorySnapshot.get(line.getSku());

                if (skuBoxes != null) {
                    for (BoxAllocation boxAlloc : skuBoxes) {
                        if (remainingQty <= 0) break;

                        int availableInBox = boxAlloc.availableQty;
                        if (availableInBox <= 0) continue;

                        int toAllocate = Math.min(remainingQty, availableInBox);

                        // Criar fragmento de alocação
                        AllocationFragment fragment = new AllocationFragment(order.getOrderId(), line.getLineNo(), line.getSku(), toAllocate, boxAlloc.getBox().getBoxId(), boxAlloc.getAisle(), boxAlloc.getBay());

                        lineAllocations.add(fragment);
                        allocatedQty += toAllocate;
                        remainingQty -= toAllocate;

                        // Atualizar quantidade disponível no snapshot
                        boxAlloc.availableQty -= toAllocate;
                    }
                }

                // Determinar status baseado no modo
                AllocationStatus status;
                boolean keepAllocations = false;

                if (allocatedQty == line.getRequestedQty()) {
                    status = AllocationStatus.ELIGIBLE;
                    keepAllocations = true;
                } else if (allocatedQty == 0) {
                    status = AllocationStatus.UNDISPATCHABLE;
                    keepAllocations = false;
                    // CRÍTICO: Marcar que a order falhou
                    if (mode == EligibilityMode.STRICT) {
                        orderFailed = true;
                    }
                } else {
                    // 0 < allocated < requested
                    if (mode == EligibilityMode.PARTIAL) {
                        status = AllocationStatus.PARTIAL;
                        keepAllocations = true;
                    } else {
                        // STRICT mode: partial não é aceitável
                        status = AllocationStatus.UNDISPATCHABLE;
                        keepAllocations = false;
                        // CRÍTICO: Marcar que a order falhou
                        orderFailed = true;
                    }
                }

                // Criar registo de eligibility
                LineEligibility eligibility = new LineEligibility(order.getOrderId(), line.getLineNo(), line.getSku(), line.getRequestedQty(), keepAllocations ? allocatedQty : 0, status);
                orderEligibilities.add(eligibility);

                // Adicionar alocações se as vamos manter
                if (keepAllocations) {
                    orderAllocations.addAll(lineAllocations);
                }
            }

            // CRÍTICO: Em STRICT mode, se a order falhou, fazer rollback de TODAS as alocações
            if (mode == EligibilityMode.STRICT && orderFailed) {
                // Rollback de todas as alocações desta order no snapshot
                for (AllocationFragment fragment : orderAllocations) {
                    rollbackAllocation(inventorySnapshot, fragment);
                }
                // Limpar alocações desta order
                orderAllocations.clear();

                // Atualizar todas as eligibilities para UNDISPATCHABLE com 0 allocated
                for (LineEligibility eligibility : orderEligibilities) {
                    LineEligibility undispatchableEligibility = new LineEligibility(eligibility.getOrderId(), eligibility.getLineNo(), eligibility.getSku(), eligibility.getRequestedQty(), 0, // CRÍTICO: Forçar 0 allocated
                            AllocationStatus.UNDISPATCHABLE);
                    eligibilities.add(undispatchableEligibility);
                }
            } else {
                // Order bem sucedida ou PARTIAL mode - manter tudo
                eligibilities.addAll(orderEligibilities);
                allocations.addAll(orderAllocations);
            }
        }

        return new AllocationResult(eligibilities, allocations);
    }

    /**
     * Método auxiliar para obter stock disponível para um SKU
     */
    private int getAvailableStockForSku(Map<String, List<BoxAllocation>> inventorySnapshot, String sku) {
        List<BoxAllocation> skuBoxes = inventorySnapshot.get(sku);
        if (skuBoxes == null) return 0;

        return skuBoxes.stream().mapToInt(BoxAllocation::getAvailableQty).sum();
    }

    /**
     * Creates a snapshot of warehouse inventory for allocation planning.
     * This allows non-destructive planning without modifying actual warehouse state.
     */
    private Map<String, List<BoxAllocation>> createInventorySnapshot(Warehouse warehouse) {
        Map<String, List<BoxAllocation>> snapshot = new HashMap<>();

        // Get all bays sorted by aisle and bay number (FEFO/FIFO order)
        List<Bay> sortedBays = new ArrayList<>(warehouse.getAllBays());
        sortedBays.sort(Comparator.comparingInt(Bay::getAisle).thenComparingInt(Bay::getBayNumber));

        // Collect boxes by SKU in FEFO/FIFO order
        for (Bay bay : sortedBays) {
            for (Box box : bay.getStoredBoxes()) {
                snapshot.computeIfAbsent(box.getSku(), k -> new ArrayList<>()).add(new BoxAllocation(box, box.getQuantity(), bay.getAisle(), bay.getBayNumber()));
            }
        }

        return snapshot;
    }

    /**
     * Rolls back an allocation in the snapshot (used in STRICT mode when line fails)
     */
    private void rollbackAllocation(Map<String, List<BoxAllocation>> snapshot, AllocationFragment fragment) {
        List<BoxAllocation> skuBoxes = snapshot.get(fragment.getSku());
        if (skuBoxes != null) {
            for (BoxAllocation boxAlloc : skuBoxes) {
                if (boxAlloc.getBox().getBoxId().equals(fragment.getBoxId())) {
                    boxAlloc.availableQty += fragment.getQty();
                    break;
                }
            }
        }

    }
}