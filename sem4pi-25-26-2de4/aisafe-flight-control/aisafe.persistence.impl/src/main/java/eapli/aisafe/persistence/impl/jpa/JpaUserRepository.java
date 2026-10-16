package eapli.aisafe.persistence.impl.jpa;

import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import eapli.framework.infrastructure.repositories.impl.jpa.JpaAutoTxRepository;

import jakarta.persistence.TypedQuery;
import java.util.Optional;

public class JpaUserRepository extends JpaAutoTxRepository<User, Email, Email> implements UserRepository {

    public JpaUserRepository() {
        super("aisafePU", "email");
    }

    @Override
    public User save(User user) {
        return super.save(user);
    }

    @Override
    public Optional<User> findById(Email id) {
        return Optional.ofNullable(entityManager().find(User.class, id));
    }

    @Override
    public Iterable<User> findAll() {
        TypedQuery<User> query = entityManager().createQuery("SELECT u FROM User u", User.class);
        return query.getResultList();
    }

    @Override
    public void delete(User user) {
        entityManager().remove(user);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return findByEmail(email).isPresent();
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        TypedQuery<User> query = entityManager().createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        return query.getResultStream().findFirst();
    }

    @Override
    public Iterable<User> findActiveByRole(Role role) {
        TypedQuery<User> query = entityManager().createQuery(
                "SELECT u FROM User u WHERE :role MEMBER OF u.roles AND u.active = true", User.class);
        query.setParameter("role", role);
        return query.getResultList();
    }

    @Override
    public Optional<User> findActiveByEmail(Email email) {
        TypedQuery<User> query = entityManager().createQuery(
                "SELECT u FROM User u WHERE u.email = :email AND u.active = true", User.class);
        query.setParameter("email", email);
        return query.getResultStream().findFirst();
    }
}