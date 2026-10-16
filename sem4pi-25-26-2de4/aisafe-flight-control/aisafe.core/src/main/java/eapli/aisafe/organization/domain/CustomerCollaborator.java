package eapli.aisafe.organization.domain;

import eapli.aisafe.usermanagement.domain.Email;

import jakarta.persistence.*;

/**
 * Domain entity representing a customer's collaborator association.
 *
 * <p>
 * This entity links an existing system user to a customer. A customer can be an
 * air transport company or an air control area, represented by
 * {@link CustomerType}.
 * </p>
 *
 * <p>
 * This entity supports:
 * </p>
 *
 * <ul>
 *     <li>US061 - Add a customer's collaborator.</li>
 *     <li>US062 - List customer's collaborators.</li>
 *     <li>US063 - Edit a customer's collaborator.</li>
 *     <li>US064 - Disable a customer's collaborator.</li>
 * </ul>
 *
 * <p>
 * The associated user email is unique, ensuring that each collaborator is a
 * distinct system user.
 * </p>
 */
@Entity
@Table(name = "T_CUSTOMER_COLLABORATOR")
public class CustomerCollaborator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerType customerType;

    @Column(nullable = false)
    private String customerCode;

    @Embedded
    @AttributeOverride(name = "address", column = @Column(name = "USER_EMAIL", nullable = false, unique = true))
    private Email userEmail;

    @Column(nullable = false)
    private String position;

    /**
     * Protected constructor required by JPA.
     */
    protected CustomerCollaborator() {
    }

    /**
     * Creates a customer collaborator association.
     *
     * @param customerType type of customer
     * @param customerCode customer business code
     * @param userEmail email of the associated system user
     * @param position collaborator position in the customer organization
     * @throws IllegalArgumentException if any required value is missing
     */
    public CustomerCollaborator(CustomerType customerType, String customerCode, Email userEmail, String position) {
        if (customerType == null) {
            throw new IllegalArgumentException("Customer type is mandatory");
        }

        if (customerCode == null || customerCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer code is mandatory");
        }

        if (userEmail == null) {
            throw new IllegalArgumentException("Collaborator email is mandatory");
        }

        if (position == null || position.trim().isEmpty()) {
            throw new IllegalArgumentException("Position is mandatory");
        }

        this.customerType = customerType;
        this.customerCode = customerCode.trim().toUpperCase();
        this.userEmail = userEmail;
        this.position = position.trim();
    }

    /**
     * Returns the internal persistence identifier.
     *
     * @return entity identifier
     */
    public Long id() {
        return id;
    }

    /**
     * Returns the customer type.
     *
     * @return customer type
     */
    public CustomerType customerType() {
        return customerType;
    }

    /**
     * Returns the customer business code.
     *
     * @return customer code
     */
    public String customerCode() {
        return customerCode;
    }

    /**
     * Returns the email of the associated system user.
     *
     * @return user email
     */
    public Email userEmail() {
        return userEmail;
    }

    /**
     * Returns the collaborator position in the customer organization.
     *
     * @return collaborator position
     */
    public String position() {
        return position;
    }

    /**
     * Changes the email reference to the associated system user.
     *
     * <p>
     * This method supports US063, where editing a collaborator may require
     * changing the associated user's email.
     * </p>
     *
     * @param newEmail new associated user email
     * @throws IllegalArgumentException if the email is null
     */
    public void changeUserEmail(Email newEmail) {
        if (newEmail == null) {
            throw new IllegalArgumentException("Email is mandatory");
        }
        this.userEmail = newEmail;
    }
}