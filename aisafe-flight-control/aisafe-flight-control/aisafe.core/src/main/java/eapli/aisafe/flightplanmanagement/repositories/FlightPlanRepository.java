package eapli.aisafe.flightplanmanagement.repositories;

import eapli.aisafe.flightplanmanagement.domain.FlightPlan;
import eapli.aisafe.flightplanmanagement.domain.FlightPlanId;
import eapli.aisafe.pilotmanagement.domain.Pilot;
import eapli.aisafe.routemanagement.domain.FlightRoute;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link FlightPlan} entities.
 */
public interface FlightPlanRepository {

    /**
     * Finds a flight plan by its identity.
     *
     * @param id the flight plan identity
     * @return an Optional containing the flight plan if found
     */
    Optional<FlightPlan> findById(FlightPlanId id);

    /**
     * Returns all registered flight plans.
     *
     * @return an iterable of all flight plans
     */
    Iterable<FlightPlan> findAll();

    /**
     * Persists a flight plan.
     *
     * @param flightPlan the flight plan to save
     * @return the saved flight plan
     */
    FlightPlan save(FlightPlan flightPlan);

    /**
     * Deletes a flight plan by its identity.
     *
     * @param id the flight plan identity
     */
    void deleteById(FlightPlanId id);

    /**
     * Returns the number of flight plans.
     *
     * @return the count of flight plans
     */
    long count();

    /**
     * Returns true if any planned flight exists for the given route on or after
     * the given date. Used by US074 to prevent deactivating a route with pending flights.
     *
     * @param route the flight route
     * @param date  the reference date
     * @return true if a planned flight exists
     */
    boolean existsPlannedForRouteFrom(FlightRoute route, LocalDate date);

    /**
     * Returns all flight plans assigned to a given pilot.
     * Used by US077 to prevent deactivating a pilot with assigned flight plans.
     *
     * @param pilot the pilot
     * @return list of flight plans assigned to the pilot
     */
    List<FlightPlan> findByPilot(Pilot pilot);

    /**
     * Returns all flight plans for a given route.
     *
     * @param route the flight route
     * @return list of flight plans for the route
     */
    List<FlightPlan> findByRoute(FlightRoute route);

    long countByPilot(Pilot pilot);
}