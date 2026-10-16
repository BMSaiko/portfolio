package eapli.aisafe.aircontrolmanagement.domain;

import java.util.Objects;
import jakarta.persistence.*;
import eapli.framework.domain.model.AggregateRoot;

/**
 * Aggregate root that represents an air control area managed by the system.
 *
 * <p>
 * An air control area is a geographical region used by flight control,
 * routing, weather consultation and simulation-related operations. It is
 * identified by an {@link AirControlAreaCode}, has a human-readable name, a
 * minimum fuel requirement and a geographical boundary.
 * </p>
 *
 * <p>
 * This aggregate is also used as the target area for weather data records,
 * allowing the system to consult weather conditions for a specific air control
 * area and date.
 * </p>
 */
@Entity
@Table(name = "AIR_CONTROL_AREA")
public class AirControlArea implements AggregateRoot<AirControlAreaCode> {

    /**
     * Unique business identifier of the air control area.
     */
    @EmbeddedId
    private AirControlAreaCode areaCode;

    /**
     * Human-readable name of the air control area.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Minimum fuel required to operate safely in this air control area.
     */
    @Column(nullable = false)
    private double minimumFuelRequired;

    /**
     * Geographical boundary that defines the area limits.
     */
    @Embedded
    private GeoBoundary geoBoundary;

    /**
     * Protected constructor required by JPA.
     *
     * <p>
     * Application code should use the public constructor to create valid
     * instances.
     * </p>
     */
    protected AirControlArea() {}

    /**
     * Creates an air control area.
     *
     * @param code unique area code
     * @param name human-readable area name
     * @param minimumFuelRequired minimum fuel required in kilograms
     * @param boundary geographical boundary of the area
     * @throws IllegalArgumentException if {@code code} is {@code null}
     * @throws IllegalArgumentException if {@code name} is {@code null} or blank
     * @throws IllegalArgumentException if {@code minimumFuelRequired} is negative
     * @throws IllegalArgumentException if {@code boundary} is {@code null}
     */
    public AirControlArea(final AirControlAreaCode code,
                          final String name,
                          final double minimumFuelRequired,
                          final GeoBoundary boundary) {
        if (code == null)
            throw new IllegalArgumentException("Area code must not be null.");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Name must not be empty.");
        if (minimumFuelRequired < 0)
            throw new IllegalArgumentException(
                    "Minimum fuel required must not be negative.");
        if (boundary == null)
            throw new IllegalArgumentException("Boundary must not be null.");
        this.areaCode = code;
        this.name = name;
        this.minimumFuelRequired = minimumFuelRequired;
        this.geoBoundary = boundary;
    }

    /**
     * Returns the aggregate identity.
     *
     * @return air control area code
     */
    @Override
    public AirControlAreaCode identity() { return areaCode; }

    /**
     * Checks whether another object represents the same air control area
     * aggregate.
     *
     * @param other object to compare with this air control area
     * @return {@code true} if the given object is an {@code AirControlArea}
     * with the same area code; otherwise {@code false}
     */
    @Override
    public boolean sameAs(Object other) {
        if (!(other instanceof AirControlArea)) return false;
        return this.areaCode.equals(((AirControlArea) other).areaCode);
    }

    /**
     * Returns the geographical boundary of this air control area.
     *
     * @return geographical boundary
     */
    public GeoBoundary geoBoundary() { return geoBoundary; }

    /**
     * Returns the human-readable name of this air control area.
     *
     * @return area name
     */
    public String name() { return name; }

    /**
     * Returns the minimum fuel required for this air control area.
     *
     * @return minimum fuel required in kilograms
     */
    public double minimumFuelRequired() { return minimumFuelRequired; }

    /**
     * Checks equality using the air control area identity.
     *
     * @param o object to compare with this air control area
     * @return {@code true} if both objects have the same area code; otherwise
     * {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AirControlArea)) return false;
        return areaCode.equals(((AirControlArea) o).areaCode);
    }

    /**
     * Returns the hash code based on the air control area identity.
     *
     * @return hash code based on area code
     */
    @Override
    public int hashCode() { return Objects.hash(areaCode); }

    /**
     * Returns a textual representation of the air control area.
     *
     * @return formatted air control area description
     */
    @Override
    public String toString() {
        return String.format("[%s] %s | MinFuel: %.1f kg | Bounds: %s",
                areaCode, name, minimumFuelRequired, geoBoundary);
    }
}