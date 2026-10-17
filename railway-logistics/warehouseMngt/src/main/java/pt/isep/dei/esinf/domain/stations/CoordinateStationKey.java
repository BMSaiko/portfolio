package pt.isep.dei.esinf.domain.stations;

/**
 * Key class for organizing stations by coordinates and then by name.
 * This allows multiple stations at the same coordinates to be stored
 * in a BST, sorted by station name.
 */
public class CoordinateStationKey implements Comparable<CoordinateStationKey> {
    private final Coordinates coordinates;
    private final String stationName;
    private final Station station;

    public CoordinateStationKey(Coordinates coordinates, String stationName, Station station) {
        this.coordinates = coordinates;
        this.stationName = stationName;
        this.station = station;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getStationName() {
        return stationName;
    }

    public Station getStation() {
        return station;
    }

    @Override
    public int compareTo(CoordinateStationKey other) {
        int coordCompare = this.coordinates.compareTo(other.coordinates);
        if (coordCompare != 0) return coordCompare;
        
        return this.stationName.compareTo(other.stationName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CoordinateStationKey that = (CoordinateStationKey) o;
        return coordinates.equals(that.coordinates) && stationName.equals(that.stationName);
    }

    @Override
    public int hashCode() {
        int result = coordinates.hashCode();
        result = 31 * result + stationName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "CoordinateStationKey{" +
                "coordinates=" + coordinates +
                ", stationName='" + stationName + '\'' +
                '}';
    }
}
