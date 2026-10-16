package eapli.aisafe.persistence.impl.jpa;

import eapli.aisafe.reportmanagement.domain.MonthlyStatistics;
import eapli.aisafe.reportmanagement.repositories.MonthlyStatisticsRepository;

import jakarta.persistence.*;
import java.util.Optional;

/**
 * JPA implementation of MonthlyStatisticsRepository.
 */
public class JpaMonthlyStatisticsRepository implements MonthlyStatisticsRepository {

    private final EntityManagerFactory emf;

    public JpaMonthlyStatisticsRepository(final String persistenceUnit) {
        this.emf = Persistence.createEntityManagerFactory(persistenceUnit);
    }

    @Override
    public Optional<MonthlyStatistics> findByYearAndMonth(final int year, final int month) {
        final EntityManager em = emf.createEntityManager();
        try {
            final TypedQuery<MonthlyStatistics> query = em.createQuery(
                    "SELECT s FROM MonthlyStatistics s WHERE s.year = :year AND s.month = :month",
                    MonthlyStatistics.class);
            query.setParameter("year", year);
            query.setParameter("month", month);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public MonthlyStatistics save(final MonthlyStatistics statistics) {
        final EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            final MonthlyStatistics managed = em.merge(statistics);
            em.getTransaction().commit();
            return managed;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw new IllegalStateException("Failed to save monthly statistics: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public long count() {
        final EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT COUNT(s) FROM MonthlyStatistics s", Long.class)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
}
