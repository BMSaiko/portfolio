package eapli.aisafe.flightplanmanagement.dto;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for flight plan registration (US080).
 *
 * <p>Carries the raw input from the UI / TCP client to the
 * {@link eapli.aisafe.flightplanmanagement.application.CreateFlightPlanController}.
 * Validation of the values themselves is performed by the controller and domain.</p>
 */
public class FlightPlanRegistrationDTO {

    /** Route designator (e.g. {@code "TP123"}). */
    public final String routeDesignator;

    /** Aircraft registration number (e.g. {@code "CS-TKX"}). */
    public final String aircraftRegistration;

    /** Email address of the pilot to be assigned. */
    public final String pilotEmail;

    /** Planned departure date and time. */
    public final LocalDateTime departureDateTime;

    /** Estimated fuel on board in kg. */
    public final double estimatedFuel;

    public FlightPlanRegistrationDTO(final String routeDesignator,
                                     final String aircraftRegistration,
                                     final String pilotEmail,
                                     final LocalDateTime departureDateTime,
                                     final double estimatedFuel) {
        if (routeDesignator == null || routeDesignator.isBlank())
            throw new IllegalArgumentException("Route designator must not be blank.");
        if (aircraftRegistration == null || aircraftRegistration.isBlank())
            throw new IllegalArgumentException("Aircraft registration must not be blank.");
        if (pilotEmail == null || pilotEmail.isBlank())
            throw new IllegalArgumentException("Pilot email must not be blank.");
        if (departureDateTime == null)
            throw new IllegalArgumentException("Departure date/time must not be null.");
        if (estimatedFuel <= 0)
            throw new IllegalArgumentException("Estimated fuel must be positive.");

        this.routeDesignator      = routeDesignator.trim().toUpperCase();
        this.aircraftRegistration = aircraftRegistration.trim().toUpperCase();
        this.pilotEmail           = pilotEmail.trim().toLowerCase();
        this.departureDateTime    = departureDateTime;
        this.estimatedFuel        = estimatedFuel;
    }
}