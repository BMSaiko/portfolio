package pt.ipp.isep.dei.domain.map;

/**
 * Enhanced Location domain class
 */
public class CityLocation extends Location {

    public CityLocation(int x, int y) {
        super(x, y);
    }

    /**
     * Gets X coordinate
     */
    public int getX() {
        return super.getX();
    }

    /**
     * Gets Y coordinate
     */
    public int getY() {
        return super.getY();
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
}
