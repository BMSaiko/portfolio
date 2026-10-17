package pt.isep.dei.esinf.domain;

import pt.isep.dei.esinf.domain.enums.AllocationStatus;

public class LineEligibility {
    private final String orderId;
    private final int lineNo;
    private final String sku;
    private final int requestedQty;
    private final int allocatedQty;
    private final AllocationStatus status;

    public LineEligibility(String orderId, int lineNo, String sku, int requestedQty, int allocatedQty, AllocationStatus status) {
        this.orderId = orderId;
        this.lineNo = lineNo;
        this.sku = sku;
        this.requestedQty = requestedQty;
        this.allocatedQty = allocatedQty;
        this.status = status;
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

    public int getRequestedQty() {
        return requestedQty;
    }

    public int getAllocatedQty() {
        return allocatedQty;
    }

    public AllocationStatus getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return String.format("orderId=%s | lineNo=%d | sku=%s | requested=%d | allocated=%d | status=%s",
                orderId, lineNo, sku, requestedQty, allocatedQty, status);
    }
}