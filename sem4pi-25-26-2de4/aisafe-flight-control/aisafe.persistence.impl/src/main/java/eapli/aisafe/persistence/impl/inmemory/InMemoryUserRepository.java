package eapli.aisafe.persistence.impl.inmemory;

import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;

import java.util.*;

public class InMemoryUserRepository implements UserRepository {

    private final Map<String, User> users = new HashMap<>();

    public void add(User user) {
        users.put(user.email().value(), user);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        return Optional.ofNullable(users.get(email.value()));
    }

    @Override
    public Optional<User> findActiveByEmail(Email email) {
        User user = users.get(email.value());
        if (user != null && user.isActive()) return Optional.of(user);
        return Optional.empty();
    }

    @Override
    public Iterable<User> findActiveByRole(Role role) {
        return users.values().stream()
                .filter(User::isActive)
                .filter(u -> u.hasRole(role))
                .toList();
    }

    @Override
    public boolean existsByEmail(Email email) {
        return users.containsKey(email.value());
    }

    @Override
    public Iterable<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User entity) {
        users.put(entity.email().value(), entity);
        return entity;
    }

    @Override
    public void deleteById(Email email) {
        users.remove(email.value());
    }

    @Override
    public void delete(User entity) {
        users.remove(entity.email().value());
    }

    @Override
    public long count() {
        return users.size();
    }

    @Override
    public Optional<User> findById(Email email) {
        return findByEmail(email);
    }
}
