package pt.isep.dei.esinf.service.warehouse;

import pt.isep.dei.esinf.domain.warehouse.Box;
import pt.isep.dei.esinf.domain.warehouse.Warehouse;

import java.util.*;

public class WarehouseAllocator {

    /**
     * Aloca os boxes no armazém, rejeitando boxes com IDs duplicados.
     */
    public void allocateBoxesToWarehouse(List<Box> boxes, Warehouse warehouse) {
        // Agrupar boxes por SKU
        Map<String, List<Box>> grouped = new HashMap<>();

        // Primeiro: filtrar boxes com IDs duplicados
        List<Box> validBoxes = new ArrayList<>();
        Set<String> seenBoxIds = new HashSet<>();

        for (Box box : boxes) {
            if (seenBoxIds.contains(box.getBoxId())) {
                System.err.println("Duplicate boxId in input list: " + box.getBoxId() + " - SKU: " + box.getSku());
                continue;
            }
            seenBoxIds.add(box.getBoxId());
            validBoxes.add(box);
        }

        for (Box box : validBoxes) {
            grouped.computeIfAbsent(box.getSku(), k -> new ArrayList<>()).add(box);
        }

        // Para cada SKU, ordenar os boxes e alocar
        for (Map.Entry<String, List<Box>> entry : grouped.entrySet()) {
            String sku = entry.getKey();
            List<Box> skuBoxes = entry.getValue();
            Collections.sort(skuBoxes); // FEFO/FIFO ordenação via Box.compareTo()

            for (Box box : skuBoxes) {
                boolean success = warehouse.allocateBox(box);
                if (!success) {
                    System.err.println("Box " + box.getBoxId() + " could not be allocated (duplicate ID or no space).");
                }
            }
        }
    }
}