package eapli.aisafe.persistence.impl.jpa;

import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.organization.repositories.AirTransportCompanyRepository;

import jakarta.persistence.*;
import java.util.Optional;

/**
 * JPA implementation of {@link AirTransportCompanyRepository}.
 */
public class JpaAirTransportCompanyRepository implements AirTransportCompanyRepository {

    private final EntityManagerFactory emf;

    public JpaAirTransportCompanyRepository(String persistenceUnit) {
        this.emf = Persistence.createEntityManagerFactory(persistenceUnit);
    }

    private EntityManager entityManager() {
        return emf.createEntityManager();
    }

    @Override
    public AirTransportCompany save(AirTransportCompany company) {
        EntityManager em = entityManager();
        try {
            em.getTransaction().begin();
            if (company.id() == null) {
                em.persist(company);
            } else {
                em.merge(company);
            }
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new IllegalStateException("Failed to save air transport company", e);
        } finally {
            em.close();
        }
        return company;
    }

    @Override
    public Optional<AirTransportCompany> findById(Long id) {
        EntityManager em = entityManager();
        try {
            return Optional.ofNullable(em.find(AirTransportCompany.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<AirTransportCompany> findByIataCode(String iataCode) {
        return findOne("SELECT c FROM AirTransportCompany c WHERE c.iataCode = :code", iataCode);
    }

    @Override
    public Optional<AirTransportCompany> findByIcaoCode(String icaoCode) {
        return findOne("SELECT c FROM AirTransportCompany c WHERE c.icaoCode = :code", icaoCode);
    }

    @Override
    public Iterable<AirTransportCompany> findAll() {
        EntityManager em = entityManager();
        try {
            TypedQuery<AirTransportCompany> query = em.createQuery("SELECT c FROM AirTransportCompany c", AirTransportCompany.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(AirTransportCompany company) {
        EntityManager em = entityManager();
        try {
            em.getTransaction().begin();
            em.remove(em.contains(company) ? company : em.merge(company));
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw new IllegalStateException("Failed to delete air transport company", e);
        } finally {
            em.close();
        }
    }

    @Override
    public boolean existsByIataCode(String iataCode) {
        return findByIataCode(iataCode).isPresent();
    }

    @Override
    public boolean existsByIcaoCode(String icaoCode) {
        return findByIcaoCode(icaoCode).isPresent();
    }

    private Optional<AirTransportCompany> findOne(String jpql, String code) {
        EntityManager em = entityManager();
        try {
            TypedQuery<AirTransportCompany> query = em.createQuery(jpql, AirTransportCompany.class);
            query.setParameter("code", code.trim().toUpperCase());
            return query.getResultStream().findFirst();
        } finally {
            em.close();
        }
    }

    @Override
    public long count() {
        EntityManager em = entityManager();
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(c) FROM AirTransportCompany c", Long.class);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(Long id) {
        EntityManager em = entityManager();
        try {
            em.getTransaction().begin();
            AirTransportCompany entity = em.find(AirTransportCompany.class, id);
            if (entity != null) {
                em.remove(entity);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new IllegalStateException("Failed to delete by id", e);
        } finally {
            em.close();
        }
    }
}
