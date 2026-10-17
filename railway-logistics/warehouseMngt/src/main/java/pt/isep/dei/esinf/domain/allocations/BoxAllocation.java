package pt.isep.dei.esinf.domain.allocations;

import pt.isep.dei.esinf.domain.warehouse.Box;

public class BoxAllocation {
    private final Box box;
    private final int aisle;
    private final int bay;
    public int availableQty;

    public BoxAllocation(Box box, int availableQty, int aisle, int bay) {
        this.box = box;
        this.availableQty = availableQty;
        this.aisle = aisle;
        this.bay = bay;
    }


    public Box getBox() {
        return box;
    }

    public int getAisle() {
        return aisle;
    }

    public int getBay() {
        return bay;
    }

    public int getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(int availableQty) {
        this.availableQty = availableQty;
    }
}
