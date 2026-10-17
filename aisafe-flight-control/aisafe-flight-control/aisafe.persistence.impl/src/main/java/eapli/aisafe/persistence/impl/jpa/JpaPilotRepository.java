package eapli.aisafe.persistence.impl.jpa;

import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.pilotmanagement.domain.Pilot;
import eapli.aisafe.pilotmanagement.repositories.PilotRepository;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.framework.domain.repositories.TransactionalContext;
import eapli.framework.infrastructure.repositories.impl.jpa.JpaAutoTxRepository;

import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * JPA Implementation for the Pilot Repository.
 */
public class JpaPilotRepository extends JpaAutoTxRepository<Pilot, Long, Long> implements PilotRepository {

    public JpaPilotRepository(final TransactionalContext autoTx) {
        super(autoTx, "id"); // Assume que a Primary Key na Base de dados se chama "id"
    }

    public JpaPilotRepository(final String puname) {
        super(puname, "id");
    }

    @Override
    public boolean existsByEmail(Email email) {
        final TypedQuery<Long> q = entityManager().createQuery(
                "SELECT COUNT(p) FROM Pilot p WHERE p.email = :email", Long.class);
        q.setParameter("email", email);
        return q.getSingleResult() > 0;
    }

    @Override
    public Optional<Pilot> findByEmail(Email email) {
        final TypedQuery<Pilot> q = entityManager().createQuery(
                "SELECT p FROM Pilot p WHERE p.email = :email", Pilot.class);
        q.setParameter("email", email);
        return q.getResultStream().findFirst();
    }

    @Override
    public List<Pilot> findByCompany(AirTransportCompany company) {
        final TypedQuery<Pilot> q = entityManager().createQuery(
                "SELECT p FROM Pilot p WHERE p.company = :company", Pilot.class);
        q.setParameter("company", company);
        return q.getResultList();
    }
}