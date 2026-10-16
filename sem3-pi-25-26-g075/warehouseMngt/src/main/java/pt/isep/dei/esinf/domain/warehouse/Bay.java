package pt.isep.dei.esinf.domain.warehouse;

import java.util.LinkedList;
import java.util.List;

public class Bay {
    private final String warehouseId;
    private final int aisle;
    private final int bayNumber;
    private final int capacityBoxes;
    private final LinkedList<Box> storedBoxes;

    public Bay(String warehouseId, int aisle, int bayNumber, int capacityBoxes) {
        this.warehouseId = warehouseId;
        this.aisle = aisle;
        this.bayNumber = bayNumber;
        this.capacityBoxes = capacityBoxes;
        this.storedBoxes = new LinkedList<>();
    }

    public String getLocationCode() {
        return warehouseId + "-" + aisle + "-" + bayNumber;
    }

    /**
     * Adds a box in FEFO/FIFO order using binary search for efficiency
     */
    public boolean addBox(Box box) {
        if (storedBoxes.size() >= capacityBoxes) return false;

        // Find insertion position using binary search concept
        int index = 0;
        for (Box b : storedBoxes) {
            if (box.compareTo(b) < 0) break;
            index++;
        }
        storedBoxes.add(index, box);
        return true;
    }

    public List<Box> getStoredBoxes() {
        return storedBoxes;
    }

    public boolean isEmpty() {
        return storedBoxes.isEmpty();
    }

    /**
     * Dispatches quantity of SKU from front (FEFO/FIFO).
     * Returns actual quantity dispatched.
     * CRITICAL FIX: Must consume from front and handle partial quantities correctly
     */
    public int dispatch(String sku, int requestedQty) {
        int dispatched = 0;
        int remaining = requestedQty;

        // Usar iterator para modificar a lista durante a iteração
        List<Box> boxesToProcess = new LinkedList<>(storedBoxes);

        for (Box box : boxesToProcess) {
            if (remaining <= 0) break;

            // Só processar boxes do SKU correto com quantidade > 0
            if (!box.getSku().equals(sku) || box.getQuantity() <= 0) {
                continue;
            }

            int available = box.getQuantity();

            if (available <= remaining) {
                // Consumir box completo
                storedBoxes.remove(box);
                dispatched += available;
                remaining -= available;
            } else {
                // Consumo parcial - criar novo box com quantidade reduzida
                storedBoxes.remove(box);
                Box reducedBox = new Box(
                        box.getBoxId(),
                        box.getSku(),
                        available - remaining,
                        box.getExpiryDate(),
                        box.getReceivedAt()
                );
                // Inserir o box reduzido na posição correta mantendo FEFO/FIFO
                addBox(reducedBox);

                dispatched += remaining;
                remaining = 0;
            }
        }

        return dispatched;
    }

    /**
     * Relocates a box to this bay in correct FEFO/FIFO position
     */
    public boolean relocate(Box box) {
        return addBox(box);
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public int getAisle() {
        return aisle;
    }

    public int getBayNumber() {
        return bayNumber;
    }

    public int getCapacityBoxes() {
        return capacityBoxes;
    }
}