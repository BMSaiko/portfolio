package eapli.aisafe.persistence.impl.jpa;

import eapli.aisafe.flightplanmanagement.domain.FlightPlan;
import eapli.aisafe.flightplanmanagement.domain.FlightPlanId;
import eapli.aisafe.flightplanmanagement.domain.FlightPlanStatus;
import eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository;
import eapli.aisafe.pilotmanagement.domain.Pilot;
import eapli.aisafe.routemanagement.domain.FlightRoute;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of {@link FlightPlanRepository}.
 *
 * <p>
 * This repository persists and retrieves {@link FlightPlan} aggregate roots
 * using a JPA {@link EntityManagerFactory}. Each operation creates its own
 * {@link EntityManager}, performs the required query or transaction, and closes
 * the entity manager afterwards.
 * </p>
 *
 * <p>
 * The aggregate identity is {@link FlightPlanId}. This implementation also
 * provides query operations required by route deactivation, pilot management and
 * flight plan weather association use cases.
 * </p>
 */
public class JpaFlightPlanRepository implements FlightPlanRepository {

    /**
     * Factory used to create entity managers for repository operations.
     */
    private final EntityManagerFactory emf;

    /**
     * Creates a JPA repository for flight plans.
     *
     * @param persistenceUnit name of the JPA persistence unit to use
     */
    public JpaFlightPlanRepository(final String persistenceUnit) {
        this.emf = Persistence.createEntityManagerFactory(persistenceUnit);
    }

    /**
     * Finds a flight plan by its identity.
     *
     * @param id flight plan identity
     * @return optional containing the matching flight plan, or empty if none
     * exists
     */
    @Override
    public Optional<FlightPlan> findById(final FlightPlanId id) {
        final EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(FlightPlan.class, id));
        } finally {
            em.close();
        }
    }

    /**
     * Returns all registered flight plans ordered by their identifier value.
     *
     * @return iterable collection of all flight plans
     */
    @Override
    public Iterable<FlightPlan> findAll() {
        final EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT f FROM FlightPlan f ORDER BY f.id.value",
                    FlightPlan.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Persists or updates a flight plan.
     *
     * <p>
     * This method uses {@link EntityManager#merge(Object)} so it can persist new
     * instances and update detached instances. The operation is executed inside
     * an explicit transaction.
     * </p>
     *
     * @param flightPlan flight plan to save
     * @return the managed and persisted flight plan
     * @throws IllegalStateException if the transaction fails
     */
    @Override
    public FlightPlan save(final FlightPlan flightPlan) {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            final FlightPlan managed = em.merge(flightPlan);
            em.getTransaction().commit();
            return managed;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new IllegalStateException("Failed to save flight plan: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    /**
     * Deletes a flight plan by its identity.
     *
     * <p>
     * If no flight plan exists with the given identity, the method completes
     * without removing any entity.
     * </p>
     *
     * @param id flight plan identity
     * @throws IllegalStateException if the transaction fails
     */
    @Override
    public void deleteById(final FlightPlanId id) {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            final FlightPlan fp = em.find(FlightPlan.class, id);
            if (fp != null) em.remove(fp);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new IllegalStateException("Failed to delete flight plan: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    /**
     * Counts the number of registered flight plans.
     *
     * @return total number of flight plans
     */
    @Override
    public long count() {
        final EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(f) FROM FlightPlan f", Long.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Checks whether there are planned flights for a route on or after a given
     * date.
     *
     * <p>
     * This method is used by the route deactivation use case. A route cannot be
     * deactivated from a date onwards when there are planned flight plans using
     * that route on or after the requested date.
     * </p>
     *
     * <p>
     * Completed and cancelled flight plans are ignored because they no longer
     * represent planned future flights.
     * </p>
     *
     * @param route route to check
     * @param date reference date for the planned-flight search
     * @return {@code true} if at least one planned flight exists for the route
     * on or after the given date; otherwise {@code false}
     * @throws IllegalArgumentException if {@code route} is {@code null}
     * @throws IllegalArgumentException if {@code date} is {@code null}
     */
    @Override
    public boolean existsPlannedForRouteFrom(final FlightRoute route, final LocalDate date) {
        if (route == null)
            throw new IllegalArgumentException("Flight route is required.");
        if (date == null)
            throw new IllegalArgumentException("Date is required.");

        final EntityManager em = emf.createEntityManager();
        try {
            final LocalDateTime from = date.atStartOfDay();
            final Long count = em.createQuery(
                            "SELECT COUNT(f) FROM FlightPlan f " +
                                    "WHERE f.departureAirport = :origin " +
                                    "AND f.arrivalAirport = :destination " +
                                    "AND f.departureDateTime IS NOT NULL " +
                                    "AND f.departureDateTime >= :from " +
                                    "AND f.status <> :completed " +
                                    "AND f.status <> :cancelled",
                            Long.class)
                    .setParameter("origin",      route.origin().identity().value())
                    .setParameter("destination", route.destination().identity().value())
                    .setParameter("from",        from)
                    .setParameter("completed",   FlightPlanStatus.COMPLETED)
                    .setParameter("cancelled",   FlightPlanStatus.CANCELLED)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    /**
     * Finds all flight plans assigned to a given pilot.
     *
     * @param pilot pilot whose flight plans should be returned
     * @return list of flight plans assigned to the pilot
     * @throws IllegalArgumentException if {@code pilot} is {@code null}
     */
    @Override
    public List<FlightPlan> findByPilot(final Pilot pilot) {
        if (pilot == null)
            throw new IllegalArgumentException("Pilot is required.");

        final EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT f FROM FlightPlan f WHERE f.pilot = :pilot",
                            FlightPlan.class)
                    .setParameter("pilot", pilot)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Finds all flight plans associated with a given route.
     *
     * @param route route whose flight plans should be returned
     * @return list of flight plans associated with the route
     * @throws IllegalArgumentException if {@code route} is {@code null}
     */
    @Override
    public List<FlightPlan> findByRoute(final FlightRoute route) {
        if (route == null)
            throw new IllegalArgumentException("Flight route is required.");

        final EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT f FROM FlightPlan f WHERE f.flightRoute = :route",
                            FlightPlan.class)
                    .setParameter("route", route)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public long countByPilot(Pilot pilot) {
        final EntityManager em = emf.createEntityManager(); // Abre a ligação manualmente
        try {
            final jakarta.persistence.TypedQuery<Long> q = em.createQuery(
                    "SELECT COUNT(f) FROM FlightPlan f WHERE f.pilot = :pilot", Long.class);
            q.setParameter("pilot", pilot);
            return q.getSingleResult();
        } catch (Exception e) {
            return 0;
        } finally {
            em.close(); // Fecha a ligação no fim, como os teus colegas fizeram
        }
    }
}