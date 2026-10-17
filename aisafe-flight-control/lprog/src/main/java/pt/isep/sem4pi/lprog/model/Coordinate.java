package pt.isep.sem4pi.lprog.model;

public class Coordinate {
    private double lat;
    private double lon;
    private double altitude;

    public Coordinate(double lat, double lon, double altitude) {
        this.lat = lat;
        this.lon = lon;
        this.altitude = altitude;
    }

    public double getLat() { return lat; }
    public double getLon() { return lon; }
    public double getAltitude() { return altitude; }

    public boolean equalsCoord(Coordinate other) {
        if (other == null) return false;
        return Double.compare(this.lat, other.lat) == 0 &&
                Double.compare(this.lon, other.lon) == 0 &&
                Double.compare(this.altitude, other.altitude) == 0;
    }
}