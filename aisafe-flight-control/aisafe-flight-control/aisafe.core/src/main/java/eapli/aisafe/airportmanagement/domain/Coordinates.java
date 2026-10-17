package eapli.aisafe.airportmanagement.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class Coordinates implements Serializable {

    private double latitude;
    private double longitude;
    private double altitude;

    protected Coordinates() {}

    public Coordinates(final double latitude,
                       final double longitude,
                       final double altitude) {
        if (latitude < -90 || latitude > 90)
            throw new IllegalArgumentException(
                    "Latitude must be between -90 and 90.");
        if (longitude < -180 || longitude > 180)
            throw new IllegalArgumentException(
                    "Longitude must be between -180 and 180.");
        if (altitude < 0)
            throw new IllegalArgumentException(
                    "Altitude must not be negative.");
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public double latitude() { return latitude; }
    public double longitude() { return longitude; }
    public double altitude() { return altitude; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordinates)) return false;
        Coordinates c = (Coordinates) o;
        return Double.compare(latitude, c.latitude) == 0
                && Double.compare(longitude, c.longitude) == 0
                && Double.compare(altitude, c.altitude) == 0;
    }

    @Override
    public int hashCode() { return Objects.hash(latitude, longitude, altitude); }

    @Override
    public String toString() {
        return String.format("(%.4f, %.4f, %.1fm)", latitude, longitude, altitude);
    }
}
