package eapli.aisafe.routemanagement.domain;

import eapli.aisafe.airportmanagement.domain.Airport;
import eapli.aisafe.organization.domain.AirTransportCompany;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Domain entity that represents a flight route operated by an air transport
 * company.
 *
 * <p>
 * A flight route connects an origin airport to a destination airport and is
 * identified by a {@link RouteDesignator}. Routes are owned by an
 * {@link AirTransportCompany} and may be deactivated from a specific date
 * onwards.
 * </p>
 *
 * <p>
 * This entity supports route management use cases such as creating flight
 * routes and deactivating a route from a given date. Once deactivated, the route
 * is no longer available for flights on or after the deactivation date.
 * </p>
 */
@Entity
@Table(name = "T_FLIGHT_ROUTE")
public class FlightRoute {

    /**
     * Business identifier of the route.
     */
    @EmbeddedId
    private RouteDesignator designator;

    /**
     * Air transport company that owns and operates this route.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    private AirTransportCompany company;

    /**
     * Origin airport of the route.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "ORIGIN_IATA", nullable = false)
    private Airport origin;

    /**
     * Destination airport of the route.
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "DESTINATION_IATA", nullable = false)
    private Airport destination;

    /**
     * Indicates whether the route is currently active.
     */
    @Column(name = "ACTIVE", nullable = false)
    private boolean active;

    /**
     * Date from which this route is deactivated.
     *
     * <p>
     * This value is {@code null} while the route is active.
     * </p>
     */
    @Column(name = "DEACTIVATED_FROM")
    private LocalDate deactivatedFrom;

    /**
     * Protected constructor required by JPA.
     */
    protected FlightRoute() {
    }

    /**
     * Creates an active flight route.
     *
     * @param designator route designator
     * @param company company that owns the route
     * @param origin origin airport
     * @param destination destination airport
     * @throws IllegalArgumentException if {@code designator} is {@code null}
     * @throws IllegalArgumentException if {@code company} is {@code null}
     * @throws IllegalArgumentException if {@code origin} is {@code null}
     * @throws IllegalArgumentException if {@code destination} is {@code null}
     * @throws IllegalArgumentException if origin and destination are the same
     */
    public FlightRoute(final RouteDesignator designator,
                       final AirTransportCompany company,
                       final Airport origin,
                       final Airport destination) {
        if (designator == null)
            throw new IllegalArgumentException("Route designator cannot be null.");
        if (company == null)
            throw new IllegalArgumentException("Company cannot be null.");
        if (origin == null)
            throw new IllegalArgumentException("Origin airport cannot be null.");
        if (destination == null)
            throw new IllegalArgumentException("Destination airport cannot be null.");
        if (origin.equals(destination))
            throw new IllegalArgumentException("Origin and destination airports must be different.");

        this.designator = designator;
        this.company = company;
        this.origin = origin;
        this.destination = destination;
        this.active = true;
        this.deactivatedFrom = null;
    }

    /**
     * Deactivates this route from the given date onwards.
     *
     * <p>
     * After this operation, {@link #isAvailableOn(LocalDate)} returns
     * {@code false} for the deactivation date and for any later date.
     * </p>
     *
     * @param date date from which the route becomes unavailable
     * @throws IllegalArgumentException if {@code date} is {@code null}
     * @throws IllegalStateException if the route is already inactive
     */
    public void deactivateFrom(final LocalDate date) {
        if (date == null)
            throw new IllegalArgumentException("Deactivation date is required.");
        if (!this.active)
            throw new IllegalStateException("Route " + designator + " is already inactive.");

        this.active = false;
        this.deactivatedFrom = date;
    }

    /**
     * Deactivates this route from the current date onwards.
     */
    public void deactivate() {
        deactivateFrom(LocalDate.now());
    }

    /**
     * Checks whether the route is available on a given date.
     *
     * <p>
     * Active routes are always available. Inactive routes are available only for
     * dates before the deactivation date.
     * </p>
     *
     * @param date date to check
     * @return {@code true} if the route is available on the given date;
     * otherwise {@code false}
     * @throws IllegalArgumentException if {@code date} is {@code null}
     */
    public boolean isAvailableOn(final LocalDate date) {
        if (date == null)
            throw new IllegalArgumentException("Date is required.");

        return active || date.isBefore(deactivatedFrom);
    }

    /**
     * Returns the route identity.
     *
     * @return route designator
     */
    public RouteDesignator identity() {
        return designator;
    }

    /**
     * Returns the company that owns this route.
     *
     * @return owning air transport company
     */
    public AirTransportCompany company() {
        return company;
    }

    /**
     * Returns the origin airport.
     *
     * @return origin airport
     */
    public Airport origin() {
        return origin;
    }

    /**
     * Returns the destination airport.
     *
     * @return destination airport
     */
    public Airport destination() {
        return destination;
    }

    /**
     * Indicates whether this route is currently active.
     *
     * @return {@code true} if the route is active; otherwise {@code false}
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Returns the date from which the route is deactivated.
     *
     * @return deactivation date, or {@code null} if the route is active
     */
    public LocalDate deactivatedFrom() {
        return deactivatedFrom;
    }

    /**
     * Checks equality using the route designator.
     *
     * @param o object to compare with this route
     * @return {@code true} if both routes have the same designator; otherwise
     * {@code false}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof FlightRoute)) return false;
        return designator.equals(((FlightRoute) o).designator);
    }

    /**
     * Returns the hash code based on the route designator.
     *
     * @return hash code based on the route identity
     */
    @Override
    public int hashCode() {
        return Objects.hash(designator);
    }

    /**
     * Returns a textual representation of the route and its activation status.
     *
     * @return formatted route description
     */
    @Override
    public String toString() {
        if (active) {
            return String.format("Route %s: %s -> %s [ACTIVE]",
                    designator, origin.identity(), destination.identity());
        }

        return String.format("Route %s: %s -> %s [INACTIVE from %s]",
                designator, origin.identity(), destination.identity(), deactivatedFrom);
    }
}