package eapli.aisafe.usermanagement.application;

import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;

import java.util.Optional;

public class ManageUserController {
    private final AuthService auth;
    private final UserRepository userRepo;

    public ManageUserController(AuthService auth, UserRepository userRepo) {
        this.auth = auth;
        this.userRepo = userRepo;
    }

    public Iterable<User> findAllUsers() {
        auth.ensureHasAnyRole(Role.ADMIN);
        return userRepo.findAll();
    }

    public void disableUser(String emailStr) {
        auth.ensureHasAnyRole(Role.ADMIN);
        Email email = new Email(emailStr);
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with email: " + emailStr);
        }
        User user = userOpt.get();
        user.deactivate();
        userRepo.save(user);
    }

    public void enableUser(String emailStr) {
        auth.ensureHasAnyRole(Role.ADMIN);
        Email email = new Email(emailStr);
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found with email: " + emailStr);
        }
        User user = userOpt.get();
        user.activate();
        userRepo.save(user);
    }
}