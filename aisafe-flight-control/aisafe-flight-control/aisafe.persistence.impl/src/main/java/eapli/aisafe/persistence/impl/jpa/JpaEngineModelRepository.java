package eapli.aisafe.persistence.impl.jpa;

import eapli.aisafe.enginemanagement.domain.EngineModel;
import eapli.aisafe.enginemanagement.domain.EngineModelId;
import eapli.aisafe.enginemanagement.domain.EngineType;
import eapli.aisafe.enginemanagement.repositories.EngineModelRepository;

import jakarta.persistence.*;
import java.util.List;
import java.util.Optional;

public class JpaEngineModelRepository implements EngineModelRepository {

    private final EntityManagerFactory emf;

    public JpaEngineModelRepository(final String persistenceUnit) {
        this.emf = Persistence.createEntityManagerFactory(persistenceUnit);
    }

    @Override
    public Optional<EngineModel> findById(final EngineModelId id) {
        final EntityManager em = emf.createEntityManager();
        try {
            return Optional.ofNullable(em.find(EngineModel.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public Iterable<EngineModel> findAll() {
        final EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT e FROM EngineModel e ORDER BY e.id.name",
                    EngineModel.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Iterable<EngineModel> findByType(final EngineType type) {
        final EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "SELECT e FROM EngineModel e WHERE e.engineType = :type ORDER BY e.id.name",
                            EngineModel.class)
                    .setParameter("type", type)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public EngineModel save(final EngineModel engineModel) {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            final EngineModel managed = em.merge(engineModel);
            em.getTransaction().commit();
            return managed;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new IllegalStateException("Failed to save engine model: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
