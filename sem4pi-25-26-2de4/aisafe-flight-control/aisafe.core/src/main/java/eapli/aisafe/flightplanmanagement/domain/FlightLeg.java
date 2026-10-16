package eapli.aisafe.flightplanmanagement.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing a leg of a flight plan.
 *
 * <p>A leg connects two airports and contains one or more flight segments.
 * Modeled as an entity with its own table to avoid nested ElementCollection
 * limitations in Hibernate.</p>
 */
@Entity
@Table(name = "FLIGHT_LEG")
public class FlightLeg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "flight_plan_id", nullable = false)
    private FlightPlan flightPlan;

    @Column(name = "leg_departure", nullable = false)
    private String departureAirport;

    @Column(name = "leg_arrival", nullable = false)
    private String arrivalAirport;

    @Column(name = "leg_fuel", nullable = false)
    private double fuel;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "FLIGHT_LEG_SEGMENTS", joinColumns = @JoinColumn(name = "leg_id"))
    private List<FlightSegment> segments = new ArrayList<>();

    protected FlightLeg() {
        // for ORM
    }

    public FlightLeg(final String departureAirport, final String arrivalAirport,
                     final double fuel, final List<FlightSegment> segments) {
        if (departureAirport == null || departureAirport.isBlank())
            throw new IllegalArgumentException("Departure airport must not be empty.");
        if (arrivalAirport == null || arrivalAirport.isBlank())
            throw new IllegalArgumentException("Arrival airport must not be empty.");
        if (fuel <= 0)
            throw new IllegalArgumentException("Fuel must be positive.");
        if (segments == null || segments.isEmpty())
            throw new IllegalArgumentException("At least one segment is required.");
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        this.fuel = fuel;
        this.segments = new ArrayList<>(segments);
    }

    public static FlightLeg of(final String departureAirport, final String arrivalAirport,
                               final double fuel, final List<FlightSegment> segments) {
        return new FlightLeg(departureAirport, arrivalAirport, fuel, segments);
    }

    void setFlightPlan(final FlightPlan flightPlan) {
        this.flightPlan = flightPlan;
    }

    public String departureAirport() {
        return departureAirport;
    }

    public String arrivalAirport() {
        return arrivalAirport;
    }

    public double fuel() {
        return fuel;
    }

    public List<FlightSegment> segments() {
        return Collections.unmodifiableList(segments);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlightLeg)) return false;
        FlightLeg other = (FlightLeg) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s -> %s] fuel=%.1f kg | %d segment(s)",
                departureAirport, arrivalAirport, fuel, segments.size());
    }
}
