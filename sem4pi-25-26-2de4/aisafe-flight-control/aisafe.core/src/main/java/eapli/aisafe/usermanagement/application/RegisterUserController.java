package eapli.aisafe.usermanagement.application;

import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import eapli.aisafe.generic.domain.UserName;
import eapli.aisafe.generic.domain.PhoneNumber;
import java.util.Set;

public class RegisterUserController {
    private final AuthService auth;
    private final UserRepository userRepo;

    public RegisterUserController(AuthService auth, UserRepository userRepo) {
        this.auth = auth;
        this.userRepo = userRepo;
    }

    public void registerUser(String emailStr, String password, String name,
                             String phone, Set<Role> roles) {
        auth.ensureHasAnyRole(Role.ADMIN);

        Email email = new Email(emailStr);
        if (userRepo.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered");
        }

        User newUser = new User(email, password, UserName.of(name), PhoneNumber.of(phone), roles);
        userRepo.save(newUser);
    }
}
