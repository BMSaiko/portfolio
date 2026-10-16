package eapli.aisafe.aircontrolmanagement.domain;

import jakarta.persistence.Embeddable;
import eapli.framework.domain.model.ValueObject;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class GeoBoundary implements ValueObject, Serializable {

    private double northLatitude;
    private double southLatitude;
    private double eastLongitude;
    private double westLongitude;

    protected GeoBoundary() {}

    public GeoBoundary(double northLat, double southLat,
                       double eastLon, double westLon) {
        validate(northLat, southLat, eastLon, westLon);
        this.northLatitude = northLat;
        this.southLatitude = southLat;
        this.eastLongitude = eastLon;
        this.westLongitude = westLon;
    }

    private void validate(double n, double s, double e, double w) {
        if (n < -90 || n > 90 || s < -90 || s > 90)
            throw new IllegalArgumentException(
                    "Latitudes must be between -90 and 90.");
        if (e < -180 || e > 180 || w < -180 || w > 180)
            throw new IllegalArgumentException(
                    "Longitudes must be between -180 and 180.");
        if (n <= s)
            throw new IllegalArgumentException(
                    "North latitude must be greater than south latitude.");
        if (e <= w)
            throw new IllegalArgumentException(
                    "East longitude must be greater than west longitude.");
    }

    /**
     * Returns true if this boundary overlaps with another.
     * Two rectangles overlap if they intersect on both axes simultaneously.
     * Areas that merely touch at an edge are NOT considered overlapping.
     */
    public boolean overlapsWith(GeoBoundary other) {
        boolean latOverlap = this.northLatitude > other.southLatitude
                && other.northLatitude > this.southLatitude;
        boolean lonOverlap = this.eastLongitude > other.westLongitude
                && other.eastLongitude > this.westLongitude;
        return latOverlap && lonOverlap;
    }

    /**
     * Returns true if the given point is inside this boundary.
     * Used by US052 to validate that airport coordinates are within the area.
     */
    public boolean contains(double latitude, double longitude) {
        return latitude >= southLatitude && latitude <= northLatitude
                && longitude >= westLongitude && longitude <= eastLongitude;
    }

    public double northLatitude() { return northLatitude; }
    public double southLatitude() { return southLatitude; }
    public double eastLongitude() { return eastLongitude; }
    public double westLongitude() { return westLongitude; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeoBoundary)) return false;
        GeoBoundary g = (GeoBoundary) o;
        return Double.compare(northLatitude, g.northLatitude) == 0
                && Double.compare(southLatitude, g.southLatitude) == 0
                && Double.compare(eastLongitude, g.eastLongitude) == 0
                && Double.compare(westLongitude, g.westLongitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(northLatitude, southLatitude,
                eastLongitude, westLongitude);
    }

    @Override
    public String toString() {
        return String.format("N:%.4f S:%.4f E:%.4f W:%.4f",
                northLatitude, southLatitude, eastLongitude, westLongitude);
    }
}