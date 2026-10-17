package pt.isep.dei.esinf.domain.allocations;

public class AllocationFragment {
    private final String orderId;
    private final int lineNo;
    private final String sku;
    private final int qty;
    private final String boxId;
    private final int aisle;
    private final int bay;

    public AllocationFragment(String orderId, int lineNo, String sku,
                              int qty, String boxId, int aisle, int bay) {
        this.orderId = orderId;
        this.lineNo = lineNo;
        this.sku = sku;
        this.qty = qty;
        this.boxId = boxId;
        this.aisle = aisle;
        this.bay = bay;
    }

    public String getOrderId() {
        return orderId;
    }

    public int getLineNo() {
        return lineNo;
    }

    public String getSku() {
        return sku;
    }

    public int getQty() {
        return qty;
    }

    public String getBoxId() {
        return boxId;
    }

    public int getAisle() {
        return aisle;
    }

    public int getBay() {
        return bay;
    }

    @Override
    public String toString() {
        return String.format("orderId=%s | lineNo=%d | sku=%s | qty=%d | boxId=%s | aisle=%d | bay=%d",
                orderId, lineNo, sku, qty, boxId, aisle, bay);
    }
}