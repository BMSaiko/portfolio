package eapli.aisafe.persistence.impl.jpa;

import eapli.aisafe.airportmanagement.domain.Airport;
import eapli.aisafe.generic.domain.IataCode;
import eapli.aisafe.generic.domain.IcaoCode;
import eapli.aisafe.airportmanagement.repositories.AirportRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.Optional;

public class JpaAirportRepository implements AirportRepository {

    private final EntityManagerFactory emf;

    public JpaAirportRepository(final String persistenceUnit) {
        this.emf = Persistence.createEntityManagerFactory(persistenceUnit);
    }

    private EntityManager entityManager() {
        return emf.createEntityManager();
    }

    @Override
    public Optional<Airport> findByIataCode(final IataCode iataCode) {
        EntityManager em = entityManager();
        try {
            TypedQuery<Airport> query = em.createQuery(
                    "SELECT a FROM Airport a WHERE a.iataCode = :iata",
                    Airport.class);
            query.setParameter("iata", iataCode);
            return query.getResultStream().findFirst();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Airport> findByIcaoCode(final IcaoCode icaoCode) {
        EntityManager em = entityManager();
        try {
            TypedQuery<Airport> query = em.createQuery(
                    "SELECT a FROM Airport a WHERE a.icaoCode = :icao",
                    Airport.class);
            query.setParameter("icao", icaoCode);
            return query.getResultStream().findFirst();
        } finally {
            em.close();
        }
    }

    @Override
    public Iterable<Airport> findAll() {
        EntityManager em = entityManager();
        try {
            TypedQuery<Airport> query = em.createQuery(
                    "SELECT a FROM Airport a", Airport.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Airport save(final Airport airport) {
        EntityManager em = entityManager();
        try {
            em.getTransaction().begin();
            Airport saved = em.merge(airport);
            em.getTransaction().commit();
            return saved;
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw new IllegalStateException("Failed to save Airport", e);
        } finally {
            em.close();
        }
    }
}
