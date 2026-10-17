package pt.isep.dei.esinf.domain.warehouse;

import java.util.*;
import java.util.stream.Collectors;

public class Warehouse {
    // Maps location code to Bay
    private final Map<String, Bay> bays = new LinkedHashMap<>();

    // Maps SKU to sorted list of bays containing that SKU
    private final Map<String, List<Bay>> skuToBays = new HashMap<>();

    private final Set<String> allocatedBoxIds = new HashSet<>(); // controlar IDs únicos

    public void addBay(Bay bay) {
        String key = bay.getLocationCode();
        bays.put(key, bay);
    }

    public Bay getBay(String locationCode) {
        return bays.get(locationCode);
    }

    public Collection<Bay> getAllBays() {
        return bays.values();
    }

    /**
     * Allocates a box to the warehouse.
     * Tries to find a bay with the same SKU first, then any available bay.
     */
    /**
     * Allocates a box to the warehouse.
     * REJECTS boxes with duplicate IDs across the entire warehouse.
     */
    public boolean allocateBox(Box box) {
        // CRÍTICO: Verificar se o boxId já existe no warehouse
        if (allocatedBoxIds.contains(box.getBoxId())) {
            System.err.println("Duplicate boxId detected: " + box.getBoxId());
            return false;
        }

        // Get sorted list of all bays
        List<Bay> sortedBays = new ArrayList<>(bays.values());
        sortedBays.sort(Comparator
                .comparingInt(Bay::getAisle)
                .thenComparingInt(Bay::getBayNumber));

        // Try to allocate
        for (Bay bay : sortedBays) {
            if (bay.addBox(box)) {
                // Update SKU to bays mapping
                skuToBays.computeIfAbsent(box.getSku(), k -> new ArrayList<>());
                if (!skuToBays.get(box.getSku()).contains(bay)) {
                    skuToBays.get(box.getSku()).add(bay);
                }

                // Adicionar boxId ao conjunto de IDs alocados
                allocatedBoxIds.add(box.getBoxId());
                return true;
            }
        }
        return false;
    }

    /**
     * CRITICAL FIX: Dispatch must traverse bays in ascending order
     * and consume from multiple bays if necessary
     */
    public int dispatch(String sku, int quantity) {
        int totalDispatched = 0;
        int remaining = quantity;

        // Primeiro, calcular stock total disponível
        int totalAvailable = getTotalStockBySku(sku);

        // Se pedido excede disponível, ajustar
        if (quantity > totalAvailable) {
            remaining = totalAvailable;
        }

        // Obter bays ordenados
        List<Bay> sortedBays = getBaysSorted();

        // Dispatch dos bays em ordem
        for (Bay bay : sortedBays) {
            if (remaining <= 0) break;

            // Verificar se o bay tem o SKU (mais eficiente)
            boolean hasSku = bayContainsSku(bay, sku);

            if (hasSku) {
                // Capturar boxes antes do dispatch para verificar quais são removidos
                Set<String> boxesBefore = bay.getStoredBoxes().stream()
                        .map(Box::getBoxId)
                        .collect(Collectors.toSet());

                int dispatchedFromBay = bay.dispatch(sku, remaining);
                totalDispatched += dispatchedFromBay;
                remaining -= dispatchedFromBay;

                // Verificar quais boxes foram completamente removidos
                Set<String> boxesAfter = bay.getStoredBoxes().stream()
                        .map(Box::getBoxId)
                        .collect(Collectors.toSet());

                // Remover boxIds que desapareceram completamente
                boxesBefore.forEach(boxId -> {
                    if (!boxesAfter.contains(boxId)) {
                        allocatedBoxIds.remove(boxId);
                    }
                });

                // Atualizar mapping SKU->Bays se o bay ficou sem este SKU
                if (!bayContainsSku(bay, sku)) {
                    updateSkuToBaysMapping(sku);
                }
            }
        }

        return totalDispatched;
    }

    /**
     * Removes a box from its current location and relocates it to a new bay.
     * The box must be removed from source bay first, then inserted into target bay.
     */
    public boolean relocate(String boxId, String newLocationCode) {
        // Verificar se o box existe
        if (!allocatedBoxIds.contains(boxId)) {
            return false;
        }

        // Find and remove box from current location
        Box boxToRelocate = null;
        Bay sourceBay = null;

        for (Bay bay : bays.values()) {
            for (Box box : bay.getStoredBoxes()) {
                if (box.getBoxId().equals(boxId)) {
                    boxToRelocate = box;
                    sourceBay = bay;
                    break;
                }
            }
            if (boxToRelocate != null) break;
        }

        if (boxToRelocate == null) return false;

        // Remove from source
        sourceBay.getStoredBoxes().remove(boxToRelocate);

        // Add to target bay
        Bay targetBay = bays.get(newLocationCode);
        if (targetBay == null) {
            // Rollback - add back to source
            sourceBay.addBox(boxToRelocate);
            return false;
        }

        boolean success = targetBay.relocate(boxToRelocate);
        if (!success) {
            // Rollback - add back to source
            sourceBay.addBox(boxToRelocate);
            return false;
        }

        // O boxId mantém-se no conjunto (apenas mudou de localização)
        return true;
    }

    /**
     * Returns inventory grouped by SKU with boxes in FEFO/FIFO order
     */
    public Map<String, List<Box>> getInventoryBySku() {
        Map<String, List<Box>> inventory = new LinkedHashMap<>();

        // Get all bays sorted
        List<Bay> sortedBays = new ArrayList<>(bays.values());
        sortedBays.sort(Comparator
                .comparingInt(Bay::getAisle)
                .thenComparingInt(Bay::getBayNumber));

        // Collect boxes by SKU in order
        for (Bay bay : sortedBays) {
            for (Box box : bay.getStoredBoxes()) {
                inventory.computeIfAbsent(box.getSku(), k -> new ArrayList<>()).add(box);
            }
        }

        return inventory;
    }

    /**
     * Método para verificar se um boxId já existe
     */
    public boolean containsBoxId(String boxId) {
        return allocatedBoxIds.contains(boxId);
    }

    /**
     * Método para obter todos os boxIds alocados (para testes)
     */
    public Set<String> getAllocatedBoxIds() {
        return new HashSet<>(allocatedBoxIds);
    }

    /**
     * Método auxiliar para verificar se um bay contém um SKU
     */
    private boolean bayContainsSku(Bay bay, String sku) {
        return bay.getStoredBoxes().stream()
                .anyMatch(box -> box.getSku().equals(sku) && box.getQuantity() > 0);
    }

    /**
     * Calcular stock total por SKU
     */
    public int getTotalStockBySku(String sku) {
        return getBaysSorted().stream()
                .flatMap(bay -> bay.getStoredBoxes().stream())
                .filter(box -> box.getSku().equals(sku))
                .mapToInt(Box::getQuantity)
                .sum();
    }

    /**
     * Atualizar mapping SKU para bays
     */
    private void updateSkuToBaysMapping(String sku) {
        List<Bay> baysWithSku = getBaysSorted().stream()
                .filter(bay -> bayContainsSku(bay, sku))
                .collect(Collectors.toList());

        if (baysWithSku.isEmpty()) {
            skuToBays.remove(sku);
        } else {
            skuToBays.put(sku, baysWithSku);
        }
    }

    /**
     * Obter bays ordenados por aisle e bay number
     */
    private List<Bay> getBaysSorted() {
        return bays.values().stream()
                .sorted(Comparator
                        .comparingInt(Bay::getAisle)
                        .thenComparingInt(Bay::getBayNumber))
                .collect(Collectors.toList());
    }
}