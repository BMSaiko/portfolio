package pt.isep.dei.esinf.domain.railway;

/**
 * Represents a railway track connection between two stations
 */
public class TrackConnection {
    private final double distance;
    private final int capacity;
    private final double cost;

    public TrackConnection(double distance, int capacity, double cost) {
        this.distance = distance;
        this.capacity = capacity;
        this.cost = cost;
    }

    public double getDistance() { return distance; }
    public int getCapacity() { return capacity; }
    public double getCost() { return cost; }

    @Override
    public String toString() {
        return String.format("Distance: %.2f km, Capacity: %d, Cost: %.2f",
                distance, capacity, cost);
    }
}