package eapli.aisafe.flightplanmanagement.domain;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import java.util.Objects;

/**
 * Value object representing a flight segment (climb, cruise, or descend).
 */
@Embeddable
public class FlightSegment {

    @Column(name = "seg_mode", nullable = false)
    private String mode;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "seg_start_lat")),
            @AttributeOverride(name = "longitude", column = @Column(name = "seg_start_lon")),
            @AttributeOverride(name = "altitude", column = @Column(name = "seg_start_alt"))
    })
    private Waypoint start;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "seg_end_lat")),
            @AttributeOverride(name = "longitude", column = @Column(name = "seg_end_lon")),
            @AttributeOverride(name = "altitude", column = @Column(name = "seg_end_alt"))
    })
    private Waypoint end;

    protected FlightSegment() {
        // for ORM
    }

    private FlightSegment(final String mode, final Waypoint start, final Waypoint end) {
        if (mode == null || mode.isBlank())
            throw new IllegalArgumentException("Segment mode must not be empty.");
        if (start == null)
            throw new IllegalArgumentException("Segment start waypoint must not be null.");
        if (end == null)
            throw new IllegalArgumentException("Segment end waypoint must not be null.");
        if (start.equals(end))
            throw new IllegalArgumentException("Segment start and end waypoints must be different.");
        this.mode = mode;
        this.start = start;
        this.end = end;
    }

    public static FlightSegment of(final String mode, final Waypoint start, final Waypoint end) {
        return new FlightSegment(mode, start, end);
    }

    public String mode() {
        return mode;
    }

    public Waypoint start() {
        return start;
    }

    public Waypoint end() {
        return end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlightSegment)) return false;
        FlightSegment other = (FlightSegment) o;
        return Objects.equals(mode, other.mode)
                && Objects.equals(start, other.start)
                && Objects.equals(end, other.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mode, start, end);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s -> %s", mode, start, end);
    }
}
