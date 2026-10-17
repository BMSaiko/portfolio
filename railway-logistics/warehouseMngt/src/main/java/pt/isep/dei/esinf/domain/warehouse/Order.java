package pt.isep.dei.esinf.domain.warehouse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Order implements Comparable<Order> {
    private final String orderId;
    private final LocalDate dueDate;
    private final int priority;
    private final List<OrderLine> lines;

    public Order(String orderId, LocalDate dueDate, int priority) {
        this.orderId = orderId;
        this.dueDate = dueDate;
        this.priority = priority;
        this.lines = new ArrayList<>();
    }

    public void addLine(OrderLine line) {
        this.lines.add(line);
    }

    public String getOrderId() {
        return orderId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public List<OrderLine> getLines() {
        return lines;
    }

    /**
     * Orders are sorted by: priority ASC, dueDate ASC, orderId ASC
     */
    @Override
    public int compareTo(Order other) {
        int cmp = Integer.compare(this.priority, other.priority);
        if (cmp != 0) return cmp;

        cmp = this.dueDate.compareTo(other.dueDate);
        if (cmp != 0) return cmp;

        return this.orderId.compareTo(other.orderId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return orderId.equals(order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", dueDate=" + dueDate +
                ", priority=" + priority +
                ", lines=" + lines.size() +
                '}';
    }
}
