package pt.isep.dei.esinf.domain.warehouse;

public class OrderLine implements Comparable<OrderLine> {
    private final int lineNo;
    private final String sku;
    private final int requestedQty;

    public OrderLine(int lineNo, String sku, int requestedQty) {
        this.lineNo = lineNo;
        this.sku = sku;
        this.requestedQty = requestedQty;
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

    /**
     * Lines are sorted by lineNo ASC
     */
    @Override
    public int compareTo(OrderLine other) {
        return Integer.compare(this.lineNo, other.lineNo);
    }

    @Override
    public String toString() {
        return "OrderLine{" +
                "lineNo=" + lineNo +
                ", sku='" + sku + '\'' +
                ", requestedQty=" + requestedQty +
                '}';
    }
}