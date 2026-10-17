package eapli.aisafe.flightplanmanagement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Value object representing the unique identity of a FlightPlan.
 *
 * <p>Must implement {@link Serializable} as required by JPA for
 * classes used as {@code @EmbeddedId}.</p>
 */
@Embeddable
public class FlightPlanId implements Serializable, Comparable<FlightPlanId> {

    private static final long serialVersionUID = 1L;

    @Column(name = "flight_plan_id", nullable = false)
    private String value;

    protected FlightPlanId() {
        // for ORM
    }

    private FlightPlanId(final String value) {
        this.value = value;
    }

    public static FlightPlanId of(final String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Flight plan ID must not be empty.");
        return new FlightPlanId(value.trim());
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlightPlanId)) return false;
        return value.equals(((FlightPlanId) o).value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public int compareTo(FlightPlanId other) {
        return this.value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value;
    }
}
