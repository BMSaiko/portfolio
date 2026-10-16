package eapli.aisafe.persistence.impl.jpa;

import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;
import eapli.aisafe.fleetmanagement.domain.Aircraft;
import eapli.aisafe.fleetmanagement.repositories.AircraftRepository;
import eapli.framework.infrastructure.repositories.impl.jpa.JpaAutoTxRepository;

import java.util.Optional;

public class JpaAircraftRepository extends JpaAutoTxRepository<Aircraft, String, String> implements AircraftRepository {

    public JpaAircraftRepository(String puname) {
        super(puname, "registration"); // "registration" é o nome do atributo @Id na classe Aircraft
    }

    @Override
    public boolean existsByRegistration(String registration) {
        final var q = entityManager().createQuery(
                "SELECT COUNT(a) FROM Aircraft a WHERE a.registration = :registration", Long.class);
        q.setParameter("registration", registration);
        return q.getSingleResult() > 0;
    }

    @Override
    public boolean existsByAircraftModelId(AircraftModelId id) {
        final var q = entityManager().createQuery(
                "SELECT COUNT(a) FROM Aircraft a "
                        + "WHERE a.model.id = :id", Long.class);
        q.setParameter("id", id);
        return q.getSingleResult() > 0;
    }

    @Override
    public Iterable<Aircraft> findByCompany(String companyIcaoCode) {
        final var q = entityManager().createQuery(
                "SELECT a FROM Aircraft a WHERE a.company.icaoCode = :code", Aircraft.class);
        q.setParameter("code", companyIcaoCode);
        return q.getResultList();
    }

    @Override
    public Optional<Aircraft> findByRegistration(String registration) {
        final var q = entityManager().createQuery(
                "SELECT a FROM Aircraft a WHERE a.registration = :registration",
                Aircraft.class);
        q.setParameter("registration", registration);
        final var results = q.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}