package pt.isep.dei.esinf.domain.stations;

import java.awt.geom.Point2D;
import java.util.Objects;

import pt.isep.dei.esinf.utils.interfaces.KDTreeNodeInterface;


public class Station implements Comparable<Station>, KDTreeNodeInterface {
    private String id;
    private String name;
    private TimeZone timeZone;
    private Coordinates coordinates;
    private boolean isCity;
    private boolean isMainStation;
    private boolean isAirport;
    private double coordX;
    private double coordY;
    private double latitude;
    private double longitude;

    public Station(String id, String name, double coordX, double coordY, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.coordX = coordX;
        this.coordY = coordY;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Station(String id, String name, double coordX, double coordY) {
        this(id, name, coordX, coordY, 0.0, 0.0);
    }

    public Station(String name, TimeZone timeZone, Coordinates coordinates, boolean isCity, boolean isMainStation, boolean isAirport) {
        this.name = name;
        this.timeZone = timeZone;
        this.coordinates = coordinates;
        this.isCity = isCity;
        this.isMainStation = isMainStation;
        this.isAirport = isAirport;
    }

    public Station(String name, Coordinates coordinates, boolean isCity, boolean isMainStation, boolean isAirport) {
        this.isCity = isCity;
        this.isAirport = isAirport;
        this.isMainStation = isMainStation;
        this.coordinates = coordinates;
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getCoordX() {
        return coordX;
    }

    public void setCoordX(double coordX) {
        this.coordX = coordX;
    }

    public double getCoordY() {
        return coordY;
    }

    public void setCoordY(double coordY) {
        this.coordY = coordY;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public boolean isCity() {
        return isCity;
    }

    public void setCity(boolean city) {
        isCity = city;
    }

    public boolean isMainStation() {
        return isMainStation;
    }

    public void setMainStation(boolean mainStation) {
        isMainStation = mainStation;
    }

    public boolean isAirport() {
        return isAirport;
    }

    public void setAirport(boolean airport) {
        isAirport = airport;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Station station = (Station) o;

        if (timeZone == null) {
        return station.timeZone == null && 
               Objects.equals(id, station.id);
        }

        return isCity == station.isCity && isMainStation == station.isMainStation && isAirport == station.isAirport && name.equals(station.name) && timeZone.equals(station.timeZone) && coordinates.equals(station.coordinates);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + Objects.hashCode(timeZone);
        result = 31 * result + Objects.hashCode(coordinates);
        result = 31 * result + Boolean.hashCode(isCity);
        result = 31 * result + Boolean.hashCode(isMainStation);
        result = 31 * result + Boolean.hashCode(isAirport);
        result = 31 * result + Double.hashCode(coordX);
        result = 31 * result + Double.hashCode(coordY);
        result = 31 * result + Double.hashCode(latitude);
        result = 31 * result + Double.hashCode(longitude);
        return result;
    }

    @Override
    public String toString() {
        return "Station{" +
                "name='" + name + '\'' +
                ", timeZone=" + timeZone +
                ", coordinates=" + coordinates +
                ", isCity: " + (isCity ? "yes" : "no") +
                ", isMainStation: " + (isMainStation ? "yes" : "no") +
                ", isAirport: " + (isAirport ? "yes" : "no") +
                '}';
    }

    
    /**
     * Validates if station has valid data
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        if (name == null || name.trim().isEmpty()) return false;
        if (coordinates == null || !coordinates.isValid()) return false;

        return timeZone == null || timeZone.isValid();
    }

    @Override
    public int compareTo(Station o) {
        return this.name.compareTo(o.name);
    }

    /**
     * Implementation of KDTreeNodeInterface
     * @return Point2D with x=longitude, y=latitude
     */
    @Override
    public Point2D.Double getCoords() {
        if (coordinates == null) {
            return null;
        }
        return new Point2D.Double(coordinates.getLongitude(), coordinates.getLatitude());
    }
}
