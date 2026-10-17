package eapli.aisafe.routemanagement.repositories;

import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.routemanagement.domain.FlightRoute;

import java.util.List;
import java.util.Optional;

/**
 * Repository contract for accessing and persisting {@link FlightRoute}
 * entities.
 *
 * <p>
 * This repository supports route management use cases by providing operations
 * to find routes by designator, list routes by company, persist route changes
 * and delete routes.
 * </p>
 *
 * <p>
 * The route designator is represented as a {@link String} in this contract to
 * simplify lookup from user input and application controllers.
 * </p>
 */
public interface FlightRouteRepository {

    /**
     * Finds a flight route by its designator value.
     *
     * @param designatorValue textual route designator value
     * @return an optional containing the matching route if found, or empty
     * otherwise
     */
    Optional<FlightRoute> findByDesignator(String designatorValue);

    /**
     * Checks whether a route exists with the given designator value.
     *
     * @param designatorValue textual route designator value
     * @return {@code true} if a route exists with the given designator;
     * otherwise {@code false}
     */
    boolean existsByDesignator(String designatorValue);

    /**
     * Finds all active routes owned by the given air transport company.
     *
     * @param company air transport company whose active routes should be listed
     * @return list of active routes owned by the company
     */
    List<FlightRoute> findActiveByCompany(AirTransportCompany company);

    /**
     * Finds all routes owned by the given air transport company, regardless of
     * activation status.
     *
     * @param company air transport company whose routes should be listed
     * @return list of routes owned by the company
     */
    List<FlightRoute> findAllByCompany(AirTransportCompany company);

    /**
     * Persists or updates a flight route.
     *
     * @param route route to save
     * @return the saved route
     */
    FlightRoute save(FlightRoute route);

    /**
     * Returns all registered flight routes.
     *
     * @return iterable collection of all routes
     */
    Iterable<FlightRoute> findAll();

    /**
     * Deletes a flight route.
     *
     * @param route route to delete
     */
    void delete(FlightRoute route);
}