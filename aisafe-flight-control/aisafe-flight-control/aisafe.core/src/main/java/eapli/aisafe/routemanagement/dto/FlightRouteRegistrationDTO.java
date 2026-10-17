package eapli.aisafe.routemanagement.dto;

/**
 * Data Transfer Object used to carry flight route creation data from the UI
 * to the application controller.
 *
 * <p>Follows the same pattern as {@code AircraftRegistrationDTO} used in
 * US070: plain public fields, no validation logic here.</p>
 *
 * <p>Supports US073 — Create a flight route.</p>
 */
public class FlightRouteRegistrationDTO {

    /** Route designator supplied by the user, e.g. {@code "TP123"}. */
    public final String designator;

    /** IATA code of the origin airport, e.g. {@code "LIS"}. */
    public final String originIataCode;

    /** IATA code of the destination airport, e.g. {@code "OPO"}. */
    public final String destinationIataCode;

    public FlightRouteRegistrationDTO(final String designator,
                                      final String originIataCode,
                                      final String destinationIataCode) {
        this.designator           = designator;
        this.originIataCode       = originIataCode;
        this.destinationIataCode  = destinationIataCode;
    }
}
