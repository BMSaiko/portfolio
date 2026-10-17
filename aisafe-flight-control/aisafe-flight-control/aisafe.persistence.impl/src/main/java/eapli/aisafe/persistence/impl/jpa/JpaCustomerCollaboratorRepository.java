package eapli.aisafe.persistence.impl.jpa;

import eapli.aisafe.organization.domain.CustomerCollaborator;
import eapli.aisafe.organization.domain.CustomerType;
import eapli.aisafe.organization.repositories.CustomerCollaboratorRepository;
import eapli.aisafe.usermanagement.domain.Email;

import jakarta.persistence.*;
import java.util.Optional;

public class JpaCustomerCollaboratorRepository implements CustomerCollaboratorRepository {

    private final EntityManagerFactory emf;

    public JpaCustomerCollaboratorRepository(String persistenceUnit) {
        this.emf = Persistence.createEntityManagerFactory(persistenceUnit);
    }

    private EntityManager entityManager() {
        return emf.createEntityManager();
    }

    @Override
    public CustomerCollaborator save(CustomerCollaborator collaborator) {
        EntityManager em = entityManager();
        try {
            em.getTransaction().begin();
            if (collaborator.id() == null) {
                em.persist(collaborator);
            } else {
                em.merge(collaborator);
            }
            em.getTransaction().commit();
        } catch (PersistenceException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new IllegalStateException("Failed to save customer collaborator", e);
        } finally {
            em.close();
        }
        return collaborator;
    }

    @Override
    public Optional<CustomerCollaborator> findById(Long id) {
        EntityManager em = entityManager();
        try {
            return Optional.ofNullable(em.find(CustomerCollaborator.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<CustomerCollaborator> findByUserEmail(Email email) {
        EntityManager em = entityManager();
        try {
            TypedQuery<CustomerCollaborator> query = em.createQuery(
                    "SELECT c FROM CustomerCollaborator c WHERE c.userEmail = :email",
                    CustomerCollaborator.class);
            query.setParameter("email", email);
            return query.getResultStream().findFirst();
        } finally {
            em.close();
        }
    }

    @Override
    public Iterable<CustomerCollaborator> findByCustomer(CustomerType customerType, String customerCode) {
        EntityManager em = entityManager();
        try {
            TypedQuery<CustomerCollaborator> query = em.createQuery(
                    "SELECT c FROM CustomerCollaborator c WHERE c.customerType = :type AND c.customerCode = :code",
                    CustomerCollaborator.class);
            query.setParameter("type", customerType);
            query.setParameter("code", customerCode.trim().toUpperCase());
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Iterable<CustomerCollaborator> findAll() {
        EntityManager em = entityManager();
        try {
            TypedQuery<CustomerCollaborator> query = em.createQuery(
                    "SELECT c FROM CustomerCollaborator c", CustomerCollaborator.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(CustomerCollaborator collaborator) {
        EntityManager em = entityManager();
        try {
            em.getTransaction().begin();
            em.remove(em.contains(collaborator) ? collaborator : em.merge(collaborator));
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            throw new IllegalStateException("Failed to delete customer collaborator", e);
        } finally {
            em.close();
        }
    }

    @Override
    public long count() {
        EntityManager em = entityManager();
        try {
            TypedQuery<Long> query = em.createQuery("SELECT COUNT(c) FROM CustomerCollaborator c", Long.class);
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
            CustomerCollaborator entity = em.find(CustomerCollaborator.class, id);
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
