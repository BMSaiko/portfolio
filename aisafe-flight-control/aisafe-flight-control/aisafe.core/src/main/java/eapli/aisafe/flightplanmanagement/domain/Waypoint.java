package eapli.aisafe.flightplanmanagement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

/**
 * Value object representing a geographic waypoint with altitude.
 */
@Embeddable
public class Waypoint {

    @Column(name = "wp_latitude", nullable = false)
    private double latitude;

    @Column(name = "wp_longitude", nullable = false)
    private double longitude;

    @Column(name = "wp_altitude", nullable = false)
    private double altitude;

    protected Waypoint() {
        // for ORM
    }

    private Waypoint(final double latitude, final double longitude, final double altitude) {
        if (altitude < 0)
            throw new IllegalArgumentException("Altitude must not be negative.");
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }

    public static Waypoint of(final double latitude, final double longitude, final double altitude) {
        return new Waypoint(latitude, longitude, altitude);
    }

    public double latitude() {
        return latitude;
    }

    public double longitude() {
        return longitude;
    }

    public double altitude() {
        return altitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Waypoint)) return false;
        Waypoint other = (Waypoint) o;
        return Double.compare(latitude, other.latitude) == 0
                && Double.compare(longitude, other.longitude) == 0
                && Double.compare(altitude, other.altitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, altitude);
    }

    @Override
    public String toString() {
        return String.format("(%.6f, %.6f, %.1f)", latitude, longitude, altitude);
    }
}
