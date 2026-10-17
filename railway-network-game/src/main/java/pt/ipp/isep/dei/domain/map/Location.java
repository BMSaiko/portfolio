package pt.ipp.isep.dei.domain.map;

/**
 * Enhanced Location domain class
 */
public class Location {
    private int x;
    private int y;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Checks if location is within bounds
     */
    public boolean isWithinBounds(int width, int height) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Calculates distance to another location
     */
    public double distanceTo(Location other) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // Getters and setters
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
}