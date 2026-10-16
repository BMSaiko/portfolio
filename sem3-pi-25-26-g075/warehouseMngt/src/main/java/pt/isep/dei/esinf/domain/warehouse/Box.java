package pt.isep.dei.esinf.domain.warehouse;

import java.time.LocalDateTime;
import java.util.Objects;

public class Box implements Comparable<Box> {
    private final String boxId;
    private final String sku;
    private final int quantity;
    private final LocalDateTime expiryDate; // null if non-perishable
    private final LocalDateTime receivedAt;

    public Box(String boxId, String sku, int quantity, LocalDateTime expiryDate, LocalDateTime receivedAt) {
        this.boxId = boxId;
        this.sku = sku;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.receivedAt = receivedAt;
    }

    public String getBoxId() {
        return boxId;
    }

    public String getSku() {
        return sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    /**
     * Compara dois boxes para ordenação FEFO/FIFO.
     */
    @Override
    public int compareTo(Box other) {
        if (this.expiryDate != null && other.expiryDate != null) {
            int cmp = this.expiryDate.compareTo(other.expiryDate);
            if (cmp != 0) return cmp;
        } else if (this.expiryDate != null) {
            return -1;
        } else if (other.expiryDate != null) {
            return 1;
        }

        int cmp = this.receivedAt.compareTo(other.receivedAt);
        if (cmp != 0) return cmp;

        return this.boxId.compareTo(other.boxId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Box)) return false;
        Box box = (Box) o;
        return boxId.equals(box.boxId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boxId);
    }

    @Override
    public String toString() {
        return "Box{" +
                "boxId='" + boxId + '\'' +
                ", sku='" + sku + '\'' +
                ", qty=" + quantity +
                ", expiry=" + expiryDate +
                ", received=" + receivedAt +
                '}';
    }
}