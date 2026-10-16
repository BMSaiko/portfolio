package eapli.aisafe.persistence.impl.inmemory;

import eapli.aisafe.flightplanmanagement.domain.FlightPlan;
import eapli.aisafe.flightplanmanagement.domain.FlightPlanId;
import eapli.aisafe.flightplanmanagement.domain.FlightPlanStatus;
import eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository;
import eapli.aisafe.pilotmanagement.domain.Pilot;
import eapli.aisafe.routemanagement.domain.FlightRoute;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory implementation of {@link FlightPlanRepository}.
 *
 * <p>
 * This repository stores {@link FlightPlan} aggregates in a local
 * {@link LinkedHashMap}. It is mainly intended for tests and lightweight
 * scenarios where a database-backed repository is not required.
 * </p>
 *
 * <p>
 * The storage preserves insertion order, which makes test results more
 * predictable when listing flight plans.
 * </p>
 */
public class InMemoryFlightPlanRepository implements FlightPlanRepository {

    /**
     * Internal in-memory storage indexed by flight plan identity.
     */
    private final Map<FlightPlanId, FlightPlan> store = new LinkedHashMap<>();

    /**
     * Finds a flight plan by its identity.
     *
     * @param id flight plan identity
     * @return optional containing the matching flight plan, or empty if none
     * exists
     */
    @Override
    public Optional<FlightPlan> findById(final FlightPlanId id) {
        return Optional.ofNullable(store.get(id));
    }

    /**
     * Returns all stored flight plans.
     *
     * @return iterable collection of all flight plans
     */
    @Override
    public Iterable<FlightPlan> findAll() {
        return new ArrayList<>(store.values());
    }

    /**
     * Saves or replaces a flight plan in memory.
     *
     * @param flightPlan flight plan to save
     * @return the saved flight plan
     */
    @Override
    public FlightPlan save(final FlightPlan flightPlan) {
        store.put(flightPlan.identity(), flightPlan);
        return flightPlan;
    }

    /**
     * Deletes a flight plan by identity.
     *
     * @param id flight plan identity
     */
    @Override
    public void deleteById(final FlightPlanId id) {
        store.remove(id);
    }

    /**
     * Returns the number of stored flight plans.
     *
     * @return repository size
     */
    @Override
    public long count() {
        return store.size();
    }

    /**
     * Checks whether there is at least one planned flight for the given route
     * on or after the provided date.
     *
     * <p>
     * This operation is used by US074 to prevent route deactivation when future
     * or same-day planned flights still exist. Completed and cancelled plans are
     * ignored because they no longer block route deactivation.
     * </p>
     *
     * @param route route to check
     * @param date deactivation reference date
     * @return {@code true} if a non-completed and non-cancelled flight plan
     * exists for the route on or after the date; otherwise {@code false}
     * @throws IllegalArgumentException if {@code route} is {@code null}
     * @throws IllegalArgumentException if {@code date} is {@code null}
     */
    @Override
    public boolean existsPlannedForRouteFrom(final FlightRoute route, final LocalDate date) {
        if (route == null)
            throw new IllegalArgumentException("Flight route is required.");
        if (date == null)
            throw new IllegalArgumentException("Date is required.");

        return store.values().stream()
                .filter(fp -> fp.departureDateTime() != null)
                .filter(fp -> !fp.departureDateTime().toLocalDate().isBefore(date))
                .filter(fp -> fp.departureAirport().equalsIgnoreCase(
                        route.origin().identity().value()))
                .filter(fp -> fp.arrivalAirport().equalsIgnoreCase(
                        route.destination().identity().value()))
                .filter(fp -> fp.status() != FlightPlanStatus.COMPLETED)
                .filter(fp -> fp.status() != FlightPlanStatus.CANCELLED)
                .findAny()
                .isPresent();
    }

    /**
     * Finds all flight plans assigned to the given pilot.
     *
     * <p>
     * Matching is performed by pilot email to avoid issues caused by different
     * object instances representing the same pilot.
     * </p>
     *
     * @param pilot pilot whose flight plans should be returned
     * @return list of flight plans assigned to the pilot
     * @throws IllegalArgumentException if {@code pilot} is {@code null}
     */
    @Override
    public List<FlightPlan> findByPilot(final Pilot pilot) {
        if (pilot == null)
            throw new IllegalArgumentException("Pilot is required.");

        return store.values().stream()
                .filter(fp -> fp.pilot() != null)
                .filter(fp -> fp.pilot().email().equals(pilot.email()))
                .collect(Collectors.toList());
    }

    /**
     * Finds all flight plans associated with the given route.
     *
     * <p>
     * Matching is performed by route identity to avoid issues caused by
     * different object instances representing the same route.
     * </p>
     *
     * @param route route whose flight plans should be returned
     * @return list of flight plans associated with the route
     * @throws IllegalArgumentException if {@code route} is {@code null}
     */
    @Override
    public List<FlightPlan> findByRoute(final FlightRoute route) {
        if (route == null)
            throw new IllegalArgumentException("Flight route is required.");

        return store.values().stream()
                .filter(fp -> fp.flightRoute() != null)
                .filter(fp -> fp.flightRoute().identity().equals(route.identity()))
                .collect(Collectors.toList());
    }

    @Override
    public long countByPilot(Pilot pilot) {
        return 0;
    }
}