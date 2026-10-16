package eapli.aisafe.usermanagement.repositories;

import eapli.aisafe.generic.repositories.Repository;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;

import java.util.Optional;

/**
 * Repository interface for {@link User} entities.
 *
 * <p>
 * This repository supports user management, authentication and authorization.
 * It is used by US030, US031 and the customer collaborator user stories
 * US061-US064.
 * </p>
 *
 * <p>
 * Following the eCafeteria pattern, this interface extends the base
 * {@link Repository} interface and the save() method returns void to avoid
 * LazyInitializationException.
 * </p>
 */
public interface UserRepository extends Repository<User, Email> {

    /**
     * Finds a user by email.
     *
     * @param email user email
     * @return optional user
     */
    Optional<User> findByEmail(Email email);

    /**
     * Finds an active user by email.
     *
     * <p>
     * This is used by authentication so inactive users cannot login.
     * </p>
     *
     * @param email user email
     * @return optional active user
     */
    Optional<User> findActiveByEmail(Email email);

    /**
     * Finds all active users with a given role.
     *
     * <p>
     * This is used by US061 to list users that can be associated as customer
     * collaborators.
     * </p>
     *
     * @param role role to search for
     * @return active users with the given role
     */
    Iterable<User> findActiveByRole(Role role);

    /**
     * Checks whether a user with the given email exists.
     *
     * @param email user email
     * @return {@code true} if the email is already registered
     */
    boolean existsByEmail(Email email);
}