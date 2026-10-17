package eapli.aisafe.organization.repositories;

import eapli.aisafe.generic.repositories.Repository;
import eapli.aisafe.organization.domain.CustomerCollaborator;
import eapli.aisafe.organization.domain.CustomerType;
import eapli.aisafe.usermanagement.domain.Email;

import java.util.Optional;

/**
 * Repository interface for {@link CustomerCollaborator} entities.
 *
 * <p>
 * This repository supports customer collaborator operations required by US061,
 * US062, US063 and US064.
 * </p>
 *
 * <p>
 * Following the eCafeteria pattern, this interface extends the base
 * {@link Repository} interface and the save() method returns void.
 * </p>
 */
public interface CustomerCollaboratorRepository extends Repository<CustomerCollaborator, Long> {

    /**
     * Finds a customer collaborator by internal identifier.
     *
     * @param id collaborator identifier
     * @return the collaborator if found
     */
    Optional<CustomerCollaborator> findById(Long id);

    /**
     * Finds a customer collaborator associated with a given user email.
     *
     * @param email user email
     * @return optional collaborator
     */
    Optional<CustomerCollaborator> findByUserEmail(Email email);

    /**
     * Finds all collaborators associated with a given customer.
     *
     * @param customerType customer type
     * @param customerCode customer business code
     * @return collaborators associated with the customer
     */
    Iterable<CustomerCollaborator> findByCustomer(CustomerType customerType, String customerCode);
}