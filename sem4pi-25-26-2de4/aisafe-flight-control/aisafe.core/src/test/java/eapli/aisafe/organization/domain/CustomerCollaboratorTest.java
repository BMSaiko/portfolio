package eapli.aisafe.organization.domain;

import eapli.aisafe.usermanagement.domain.Email;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link CustomerCollaborator} domain entity.
 *
 * <p>
 * These tests validate the domain rules required by US061 - Add a customer's
 * collaborator. They focus on the collaborator association itself, including
 * customer type, customer code, user email, position, and email changes used by
 * US063.
 * </p>
 */
class CustomerCollaboratorTest {

    /**
     * Ensures that a valid collaborator can be created for an air transport
     * company.
     *
     * <p>
     * The customer code is normalized to uppercase.
     * </p>
     */
    @Test
    void createsValidCustomerCollaborator() {
        Email email = new Email("collab@aisafe.com");

        CustomerCollaborator collaborator = new CustomerCollaborator(
                CustomerType.AIR_TRANSPORT_COMPANY,
                "tap",
                email,
                "Operations Manager"
        );

        assertEquals(CustomerType.AIR_TRANSPORT_COMPANY, collaborator.customerType());
        assertEquals("TAP", collaborator.customerCode());
        assertEquals(email, collaborator.userEmail());
        assertEquals("Operations Manager", collaborator.position());
    }

    /**
     * Ensures that a valid collaborator can be created for an air control area.
     *
     * <p>
     * The air control area code is normalized to uppercase.
     * </p>
     */
    @Test
    void createsCollaboratorForAirControlArea() {
        Email email = new Email("control@aisafe.com");

        CustomerCollaborator collaborator = new CustomerCollaborator(
                CustomerType.AIR_CONTROL_AREA,
                "lis_area",
                email,
                "Flight Control Operator"
        );

        assertEquals(CustomerType.AIR_CONTROL_AREA, collaborator.customerType());
        assertEquals("LIS_AREA", collaborator.customerCode());
    }

    /**
     * Ensures that a collaborator cannot be created without a customer type.
     */
    @Test
    void rejectsNullCustomerType() {
        assertThrows(IllegalArgumentException.class,
                () -> new CustomerCollaborator(
                        null,
                        "TAP",
                        new Email("collab@aisafe.com"),
                        "Manager"
                ));
    }

    /**
     * Ensures that a collaborator cannot be created without a valid customer
     * code.
     */
    @Test
    void rejectsBlankCustomerCode() {
        assertThrows(IllegalArgumentException.class,
                () -> new CustomerCollaborator(
                        CustomerType.AIR_TRANSPORT_COMPANY,
                        " ",
                        new Email("collab@aisafe.com"),
                        "Manager"
                ));
    }

    /**
     * Ensures that a collaborator cannot be created without an associated user
     * email.
     */
    @Test
    void rejectsNullEmail() {
        assertThrows(IllegalArgumentException.class,
                () -> new CustomerCollaborator(
                        CustomerType.AIR_TRANSPORT_COMPANY,
                        "TAP",
                        null,
                        "Manager"
                ));
    }

    /**
     * Ensures that a collaborator cannot be created without a valid position.
     */
    @Test
    void rejectsBlankPosition() {
        assertThrows(IllegalArgumentException.class,
                () -> new CustomerCollaborator(
                        CustomerType.AIR_TRANSPORT_COMPANY,
                        "TAP",
                        new Email("collab@aisafe.com"),
                        " "
                ));
    }

    /**
     * Ensures that the collaborator email reference can be updated.
     *
     * <p>
     * This behavior supports US063, where editing a collaborator may require
     * changing the email associated with the system user.
     * </p>
     */
    @Test
    void changesUserEmail() {
        CustomerCollaborator collaborator = new CustomerCollaborator(
                CustomerType.AIR_TRANSPORT_COMPANY,
                "TAP",
                new Email("old@aisafe.com"),
                "Manager"
        );

        collaborator.changeUserEmail(new Email("new@aisafe.com"));

        assertEquals(new Email("new@aisafe.com"), collaborator.userEmail());
    }
}
