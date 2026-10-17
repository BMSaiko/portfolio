package eapli.aisafe.generic.repositories;

import java.util.Optional;

/**
 * Base repository interface following the Repository pattern from Domain-Driven Design.
 *
 * <p>
 * This interface defines the basic contract for all repositories in the system.
 * Following the EAPLI framework pattern, all repositories should extend this interface.
 * </p>
 *
 * @param <T> the entity type
 * @param <K> the identity type (key)
 */
public interface Repository<T, K> extends eapli.framework.infrastructure.repositories.Repository<T, K> {

    /**
     * Saves an entity.
     *
     * @param entity the entity to save
     * @return the saved entity
     */
    <S extends T> S save(S entity);

    /**
     * Returns all entities.
     *
     * @return an iterable of all entities
     */
    Iterable<T> findAll();

    /**
     * Finds an entity by its identity.
     *
     * @param id the entity identity
     * @return an Optional containing the entity if found
     */
    Optional<T> findById(K id);

    /**
     * Deletes an entity.
     *
     * @param entity the entity to delete
     */
    void delete(T entity);
}
