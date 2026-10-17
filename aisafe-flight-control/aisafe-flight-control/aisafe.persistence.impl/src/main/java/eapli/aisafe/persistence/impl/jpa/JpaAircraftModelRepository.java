package eapli.aisafe.persistence.impl.jpa;

import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;
import eapli.aisafe.aircraftmodelmanagement.repositories.AircraftModelRepository;
import jakarta.persistence.*;

import java.util.Optional;

public class JpaAircraftModelRepository implements AircraftModelRepository {

    private final EntityManagerFactory emf;

    public JpaAircraftModelRepository(final String persistenceUnit) {
        this.emf = Persistence.createEntityManagerFactory(persistenceUnit);
    }

    @Override
    public Optional<AircraftModel> findById(final AircraftModelId id) {
        final EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(AircraftModel.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public Iterable<AircraftModel> findAll() {
        final EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT a FROM AircraftModel a ORDER BY a.id.makerName, a.id.modelId",
                    AircraftModel.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Iterable<String> findAllMakers() {
        final EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT DISTINCT a.id.makerName FROM AircraftModel a ORDER BY a.id.makerName",
                    String.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Iterable<AircraftModel> findByMaker(String makerName) {
        final EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT a FROM AircraftModel a WHERE a.id.makerName = :maker ORDER BY a.id.modelId",
                            AircraftModel.class)
                    .setParameter("maker", makerName)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public AircraftModel save(final AircraftModel aircraftModel) {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            final AircraftModel managed = em.merge(aircraftModel);
            em.getTransaction().commit();
            return managed;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new IllegalStateException("Failed to save aircraft model: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}