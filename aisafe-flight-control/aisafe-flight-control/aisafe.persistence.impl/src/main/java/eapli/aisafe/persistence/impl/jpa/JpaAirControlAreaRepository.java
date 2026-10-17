package eapli.aisafe.persistence.impl.jpa;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.Optional;

/**
 * JPA implementation of {@link AirControlAreaRepository}.
 *
 * <p>
 * This repository persists and retrieves {@link AirControlArea} aggregate roots
 * using a JPA {@link EntityManagerFactory}. Each operation creates its own
 * {@link EntityManager}, performs the required query or transaction, and closes
 * the entity manager afterwards.
 * </p>
 *
 * <p>
 * The aggregate identity is {@link AirControlAreaCode}, which is embedded in
 * the {@link AirControlArea} entity as its business identifier.
 * </p>
 */
public class JpaAirControlAreaRepository implements AirControlAreaRepository {

    /**
     * Factory used to create entity managers for repository operations.
     */
    private final EntityManagerFactory emf;

    /**
     * Creates a JPA repository for air control areas.
     *
     * @param persistenceUnit name of the JPA persistence unit to use
     */
    public JpaAirControlAreaRepository(final String persistenceUnit) {
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
     * Finds an air control area by its business code.
     *
     * @param code air control area code to search for
     * @return optional containing the matching area, or empty if none exists
     */
    @Override
    public Optional<AirControlArea> findByCode(final AirControlAreaCode code) {
        EntityManager em = entityManager();
        try {
            TypedQuery<AirControlArea> query = em.createQuery(
                    "SELECT a FROM AirControlArea a WHERE a.areaCode = :code",
                    AirControlArea.class);
            query.setParameter("code", code);
            return query.getResultStream().findFirst();
        } finally {
            em.close();
        }
    }

    /**
     * Returns all air control areas stored in the database.
     *
     * @return iterable collection of all air control areas
     */
    @Override
    public Iterable<AirControlArea> findAll() {
        EntityManager em = entityManager();
        try {
            return em.createQuery(
                    "SELECT a FROM AirControlArea a",
                    AirControlArea.class).getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Persists or updates an air control area.
     *
     * @param area air control area to save
     * @return the managed and persisted air control area
     * @throws IllegalStateException if the transaction fails
     */
    @Override
    public AirControlArea save(final AirControlArea area) {
        EntityManager em = entityManager();
        try {
            em.getTransaction().begin();
            AirControlArea saved = em.merge(area);
            em.getTransaction().commit();
            return saved;
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw new IllegalStateException("Failed to save AirControlArea", e);
        } finally {
            em.close();
        }
    }

    /**
     * Finds an air control area by its identity.
     *
     * <p>
     * This method delegates to {@link #findByCode(AirControlAreaCode)} because
     * the aggregate identity is the area code.
     * </p>
     *
     * @param id air control area identity
     * @return optional containing the matching area, or empty if none exists
     */
    @Override
    public Optional<AirControlArea> findById(AirControlAreaCode id) {
        return findByCode(id);
    }

    /**
     * Deletes an air control area.
     *
     * @param area air control area to delete
     * @throws IllegalStateException if the transaction fails
     */
    @Override
    public void delete(AirControlArea area) {
        EntityManager em = entityManager();
        try {
            em.getTransaction().begin();
            em.remove(em.merge(area));
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw new IllegalStateException("Failed to delete AirControlArea", e);
        } finally {
            em.close();
        }
    }

    /**
     * Counts the number of stored air control areas.
     *
     * @return total number of air control areas
     */
    @Override
    public long count() {
        EntityManager em = entityManager();
        try {
            return em.createQuery("SELECT COUNT(a) FROM AirControlArea a", Long.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    /**
     * Deletes an air control area by identity.
     *
     * <p>
     * If no area exists with the given identity, the method completes without
     * removing any entity.
     * </p>
     *
     * @param id air control area identity
     * @throws IllegalStateException if the transaction fails
     */
    @Override
    public void deleteById(AirControlAreaCode id) {
        EntityManager em = entityManager();
        try {
            em.getTransaction().begin();
            AirControlArea area = em.find(AirControlArea.class, id);
            if (area != null) {
                em.remove(area);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw new IllegalStateException("Failed to delete AirControlArea by id", e);
        } finally {
            em.close();
        }
    }
}