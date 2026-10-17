package eapli.aisafe.persistence.impl.jpa;

import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.routemanagement.domain.FlightRoute;
import eapli.aisafe.routemanagement.repositories.FlightRouteRepository;

import jakarta.persistence.*;
import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of {@link FlightRouteRepository}.
 *
 * <p>
 * This repository persists and retrieves {@link FlightRoute} entities using a
 * JPA {@link EntityManagerFactory}. Each operation creates its own
 * {@link EntityManager}, performs the required query or transaction, and closes
 * the entity manager afterwards.
 * </p>
 *
 * <p>
 * This implementation follows the same manual entity manager pattern used by
 * other repository implementations in the project, without extending a
 * framework base repository class.
 * </p>
 */
public class JpaFlightRouteRepository implements FlightRouteRepository {

    /**
     * Factory used to create entity managers for repository operations.
     */
    private final EntityManagerFactory emf;

    /**
     * Creates a JPA repository for flight routes.
     *
     * @param persistenceUnit name of the JPA persistence unit to use
     */
    public JpaFlightRouteRepository(final String persistenceUnit) {
        this.emf = Persistence.createEntityManagerFactory(persistenceUnit);
    }

    /**
     * Creates a new entity manager.
     *
     * @return a new entity manager
     */
    private EntityManager entityManager() {
        return emf.createEntityManager();
    }

    /**
     * Finds a flight route by its designator value.
     *
     * <p>
     * The provided designator is trimmed and converted to uppercase before
     * lookup to match the normalized route designator stored in the domain.
     * </p>
     *
     * @param designatorValue textual route designator value
     * @return optional containing the matching route if found, or empty
     * otherwise
     */
    @Override
    public Optional<FlightRoute> findByDesignator(final String designatorValue) {
        EntityManager em = entityManager();
        try {
            TypedQuery<FlightRoute> query = em.createQuery(
                    "SELECT r FROM FlightRoute r WHERE r.designator.value = :d",
                    FlightRoute.class);
            query.setParameter("d", designatorValue.trim().toUpperCase());
            return query.getResultStream().findFirst();
        } finally {
            em.close();
        }
    }

    /**
     * Checks whether a route exists with the given designator value.
     *
     * @param designatorValue textual route designator value
     * @return {@code true} if a matching route exists; otherwise {@code false}
     */
    @Override
    public boolean existsByDesignator(final String designatorValue) {
        return findByDesignator(designatorValue).isPresent();
    }

    /**
     * Finds all active routes owned by the given air transport company.
     *
     * @param company air transport company whose active routes should be listed
     * @return list of active routes owned by the company
     */
    @Override
    public List<FlightRoute> findActiveByCompany(final AirTransportCompany company) {
        EntityManager em = entityManager();
        try {
            TypedQuery<FlightRoute> query = em.createQuery(
                    "SELECT r FROM FlightRoute r WHERE r.company = :c AND r.active = true",
                    FlightRoute.class);
            query.setParameter("c", company);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Finds all routes owned by the given air transport company.
     *
     * @param company air transport company whose routes should be listed
     * @return list of all routes owned by the company
     */
    @Override
    public List<FlightRoute> findAllByCompany(final AirTransportCompany company) {
        EntityManager em = entityManager();
        try {
            TypedQuery<FlightRoute> query = em.createQuery(
                    "SELECT r FROM FlightRoute r WHERE r.company = :c",
                    FlightRoute.class);
            query.setParameter("c", company);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Returns all registered flight routes.
     *
     * @return iterable collection of all routes
     */
    @Override
    public Iterable<FlightRoute> findAll() {
        EntityManager em = entityManager();
        try {
            return em.createQuery("SELECT r FROM FlightRoute r", FlightRoute.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Persists or updates a flight route.
     *
     * @param route route to save
     * @return the managed and persisted route
     * @throws IllegalStateException if the transaction fails
     */
    @Override
    public FlightRoute save(final FlightRoute route) {
        EntityManager em = entityManager();
        try {
            em.getTransaction().begin();
            FlightRoute saved = em.merge(route);
            em.getTransaction().commit();
            return saved;
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw new IllegalStateException("Failed to save FlightRoute", e);
        } finally {
            em.close();
        }
    }

    /**
     * Deletes a flight route.
     *
     * <p>
     * If the provided route is detached, it is first merged into the current
     * persistence context and then removed.
     * </p>
     *
     * @param route route to delete
     * @throws IllegalStateException if the transaction fails
     */
    @Override
    public void delete(final FlightRoute route) {
        EntityManager em = entityManager();
        try {
            em.getTransaction().begin();
            FlightRoute managed = em.contains(route) ? route : em.merge(route);
            em.remove(managed);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw new IllegalStateException("Failed to delete FlightRoute", e);
        } finally {
            em.close();
        }
    }
}