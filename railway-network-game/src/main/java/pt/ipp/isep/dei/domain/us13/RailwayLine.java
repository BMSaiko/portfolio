package pt.ipp.isep.dei.domain.us13;

/**
 * Represents a railway line connecting two stations.
 */
public class RailwayLine {
    private final Station source;
    private final Station destination;
    private final boolean isElectrified;
    private final int distance;
    
    /**
     * Creates a new railway line.
     * 
     * @param source the source station
     * @param destination the destination station
     * @param isElectrified whether the line is electrified
     * @param distance the distance of the line
     */
    public RailwayLine(Station source, Station destination, boolean isElectrified, int distance) {
        this.source = source;
        this.destination = destination;
        this.isElectrified = isElectrified;
        this.distance = distance;
    }
    
    /**
     * Gets the source station.
     * 
     * @return the source station
     */
    public Station getSource() {
        return source;
    }
    
    /**
     * Gets the destination station.
     * 
     * @return the destination station
     */
    public Station getDestination() {
        return destination;
    }
    
    /**
     * Checks if this line is electrified.
     * 
     * @return true if the line is electrified, false otherwise
     */
    public boolean isElectrified() {
        return isElectrified;
    }
    
    /**
     * Gets the distance of the line.
     * 
     * @return the distance
     */
    public int getDistance() {
        return distance;
    }
    
    /**
     * Gets the other station in the line, given one station.
     * 
     * @param station one of the stations in the line
     * @return the other station in the line
     * @throws IllegalArgumentException if the given station is not part of this line
     */
    public Station getOtherStation(Station station) {
        if (source.equals(station)) {
            return destination;
        } else if (destination.equals(station)) {
            return source;
        }
        throw new IllegalArgumentException("Station " + station + " is not part of this line");
    }
    
    @Override
    public String toString() {
        return source + " -> " + destination + " (" + (isElectrified ? "electrified" : "not electrified") + ", " + distance + " km)";
    }
}