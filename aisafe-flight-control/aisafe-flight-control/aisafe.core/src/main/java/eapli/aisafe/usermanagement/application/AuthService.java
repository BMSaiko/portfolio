package eapli.aisafe.usermanagement.application;

import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import java.util.Optional;

/**
 * Application service responsible for authentication, logout, current user
 * session access and role-based authorization.
 *
 * <p>
 * This service supports US030 - Authentication and Authorization. It is also
 * used by other controllers to enforce access control before executing
 * protected operations.
 * </p>
 *
 * <p>
 * The current authenticated user is stored in a {@link ThreadLocal}, which is
 * enough for the current console application model.
 * </p>
 */
public class AuthService {
    private final UserRepository userRepo;
    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();

    /**
     * Creates the authentication service.
     *
     * @param userRepo repository used to find users
     */
    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Attempts to authenticate a user with email and password.
     *
     * <p>
     * Only active users can authenticate. If authentication succeeds, the user
     * is stored in the current thread session.
     * </p>
     *
     * @param emailStr email entered by the user
     * @param password plain password entered by the user
     * @return authenticated user, or empty if authentication fails
     */
    public Optional<User> login(String emailStr, String password) {
        Email email = new Email(emailStr);
        Optional<User> userOpt = userRepo.findActiveByEmail(email);
        if (userOpt.isPresent() && userOpt.get().passwordMatches(password)) {
            currentUser.set(userOpt.get());
            return userOpt;
        }
        return Optional.empty();
    }

    /**
     * Logs out the current user by clearing the current session.
     */
    public void logout() {
        currentUser.remove();
    }

    /**
     * Returns the currently authenticated user.
     *
     * @return optional current user
     */
    public Optional<User> currentUser() {
        return Optional.ofNullable(currentUser.get());
    }
    /**
     * Ensures that a user is currently authenticated.
     *
     * @throws SecurityException if there is no authenticated user
     */
    public void ensureAuthenticated() {
        if (currentUser.get() == null)
            throw new SecurityException("User not authenticated");
    }

    /**
     * Ensures that the current user has at least one of the required roles.
     *
     * @param roles accepted roles
     * @throws SecurityException if the user is not authenticated or does not
     *                           have any of the required roles
     */
    public void ensureHasAnyRole(Role... roles) {
        ensureAuthenticated();
        User user = currentUser.get();
        for (Role r : roles) {
            if (user.hasRole(r)) {
                return;
            }
        }
        throw new SecurityException("User does not have required role");
    }
}
