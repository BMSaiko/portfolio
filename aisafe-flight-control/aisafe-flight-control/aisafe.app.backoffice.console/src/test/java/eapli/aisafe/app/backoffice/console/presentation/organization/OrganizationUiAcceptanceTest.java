package eapli.aisafe.app.backoffice.console.presentation.organization;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.domain.GeoBoundary;
import eapli.aisafe.organization.application.CustomerCollaboratorController;
import eapli.aisafe.organization.application.RegisterAirTransportCompanyController;
import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.organization.domain.CustomerCollaborator;
import eapli.aisafe.organization.domain.CustomerType;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UI acceptance-style tests for organization user stories:
 *
 * <ul>
 *     <li>US060 - Register an air transport company.</li>
 *     <li>US061 - Add a customer's collaborator.</li>
 *     <li>US062 - List customer's collaborators.</li>
 *     <li>US063 - Edit a customer's collaborator.</li>
 *     <li>US064 - Disable a customer's collaborator.</li>
 * </ul>
 *
 * <p>
 * These tests simulate console input/output and verify that each UI delegates
 * the expected operation to its controller.
 * </p>
 */
class OrganizationUiAcceptanceTest {

    private final PrintStream originalOut = System.out;
    private final java.io.InputStream originalIn = System.in;

    /**
     * Restores the original console streams after each test.
     *
     * <p>
     * Each UI test replaces {@link System#in} and {@link System#out} to simulate
     * operator input and capture console output. Restoring the streams avoids
     * side effects between tests.
     * </p>
     */
    @AfterEach
    void restoreConsole() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    /**
     * Verifies US060 through the console UI.
     *
     * <p>
     * The test simulates a Backoffice Operator typing the company name, IATA
     * code and ICAO code. It then confirms that the UI sends exactly those
     * values to the registration controller and displays a success message.
     * </p>
     */
    @Test
    void us060RegisterAirTransportCompanyUiSubmitsTypedCompanyData() {
        CapturingRegisterAirTransportCompanyController controller =
                new CapturingRegisterAirTransportCompanyController();

        ConsoleResult result = runUiWithInput(
                "TAP Air Portugal\nTP\nTAP\n",
                () -> new RegisterAirTransportCompanyUI(controller).show()
        );

        assertEquals("TAP Air Portugal", controller.name);
        assertEquals("TP", controller.iataCode);
        assertEquals("TAP", controller.icaoCode);

        assertTrue(result.output.contains("Register Air Transport Company"));
        assertTrue(result.output.contains("Air transport company registered successfully."));
        assertTrue(result.output.contains("Name: TAP Air Portugal"));
        assertTrue(result.output.contains("IATA: TP"));
        assertTrue(result.output.contains("ICAO: TAP"));
    }

    /**
     * Verifies the US061 UI flow for an air transport company collaborator.
     *
     * <p>
     * The test simulates selecting the customer type "air transport company",
     * selecting an available user, selecting an existing company and entering a
     * position. It confirms that the UI delegates the expected customer type,
     * customer code, user email and position to the controller.
     * </p>
     */
    @Test
    void us061AddCustomerCollaboratorUiAddsSelectedUserToSelectedCompany() {
        CapturingCustomerCollaboratorController controller =
                new CapturingCustomerCollaboratorController();

        controller.availableUsers = List.of(user(
                "collab@aisafe.com",
                "Collaborator",
                "+351900000002",
                Role.AIR_TRANSPORT_COMPANY_COLLABORATOR
        ));

        controller.airTransportCompanies = List.of(
                new AirTransportCompany("TAP Air Portugal", "TP", "TAP")
        );

        ConsoleResult result = runUiWithInput(
                "1\n1\n1\nOperations Manager\n",
                () -> new AddCustomerCollaboratorUI(controller).show()
        );

        assertEquals(CustomerType.AIR_TRANSPORT_COMPANY, controller.availableCollaboratorUsersCustomerType);
        assertEquals(CustomerType.AIR_TRANSPORT_COMPANY, controller.addedCustomerType);
        assertEquals("TAP", controller.addedCustomerCode);
        assertEquals("collab@aisafe.com", controller.addedUserEmail);
        assertEquals("Operations Manager", controller.addedPosition);

        assertTrue(result.output.contains("Add Customer Collaborator"));
        assertTrue(result.output.contains("Available users"));
        assertTrue(result.output.contains("Customer collaborator added successfully."));
        assertTrue(result.output.contains("collab@aisafe.com"));
    }

    /**
     * Verifies the US061 UI flow for an air control area collaborator.
     *
     * <p>
     * The test simulates selecting the customer type "air control area",
     * selecting an available flight control operator, selecting an existing air
     * control area and entering a position. It ensures the UI calls the
     * controller with the selected air control area code and user email.
     * </p>
     */
    @Test
    void us061AddCustomerCollaboratorUiAddsSelectedUserToSelectedAirControlArea() {
        CapturingCustomerCollaboratorController controller =
                new CapturingCustomerCollaboratorController();

        controller.availableUsers = List.of(user(
                "operator@aisafe.com",
                "Flight Operator",
                "+351900000003",
                Role.FLIGHT_CONTROL_OPERATOR
        ));

        controller.airControlAreas = List.of(new AirControlArea(
                AirControlAreaCode.of("LIS_AREA"),
                "Lisbon Area",
                500.0,
                new GeoBoundary(38.0, -9.0, 39.0, -8.0)
        ));

        ConsoleResult result = runUiWithInput(
                "2\n1\n1\nFlight Control Operator\n",
                () -> new AddCustomerCollaboratorUI(controller).show()
        );

        assertEquals(CustomerType.AIR_CONTROL_AREA, controller.availableCollaboratorUsersCustomerType);
        assertEquals(CustomerType.AIR_CONTROL_AREA, controller.addedCustomerType);
        assertEquals("LIS_AREA", controller.addedCustomerCode);
        assertEquals("operator@aisafe.com", controller.addedUserEmail);
        assertEquals("Flight Control Operator", controller.addedPosition);

        assertTrue(result.output.contains("Existing air control areas"));
        assertTrue(result.output.contains("Customer collaborator added successfully."));
    }

    /**
     * Verifies US062 when active collaborators exist for the selected customer.
     *
     * <p>
     * The test simulates selecting an air transport company and confirms that
     * the UI asks the controller for active collaborators of that customer. It
     * also checks that the collaborator email, name and position are printed to
     * the console.
     * </p>
     */
    @Test
    void us062ListCustomerCollaboratorsUiShowsActiveCollaboratorsForSelectedCompany() {
        CapturingCustomerCollaboratorController controller =
                new CapturingCustomerCollaboratorController();

        controller.airTransportCompanies = List.of(
                new AirTransportCompany("TAP Air Portugal", "TP", "TAP")
        );

        CustomerCollaborator collaborator = collaborator(
                10L,
                CustomerType.AIR_TRANSPORT_COMPANY,
                "TAP",
                "collab@aisafe.com",
                "Operations Manager"
        );

        controller.activeCollaborators = List.of(collaborator);
        controller.usersByEmail = Map.of(
                "collab@aisafe.com",
                user("collab@aisafe.com", "Collaborator", "+351900000002",
                        Role.AIR_TRANSPORT_COMPANY_COLLABORATOR)
        );

        ConsoleResult result = runUiWithInput(
                "1\n1\n",
                () -> new ListCustomerCollaboratorsUI(controller).show()
        );

        assertEquals(CustomerType.AIR_TRANSPORT_COMPANY, controller.listCustomerType);
        assertEquals("TAP", controller.listCustomerCode);

        assertTrue(result.output.contains("List Customer Collaborators"));
        assertTrue(result.output.contains("Active collaborators"));
        assertTrue(result.output.contains("collab@aisafe.com"));
        assertTrue(result.output.contains("Collaborator"));
        assertTrue(result.output.contains("Operations Manager"));
    }

    /**
     * Verifies US062 when the selected customer has no active collaborators.
     *
     * <p>
     * The test simulates selecting an air transport company whose collaborator
     * list is empty. It confirms that the UI shows the expected empty-state
     * message instead of printing a collaborator table.
     * </p>
     */
    @Test
    void us062ListCustomerCollaboratorsUiShowsEmptyMessageWhenNoCollaboratorsExist() {
        CapturingCustomerCollaboratorController controller =
                new CapturingCustomerCollaboratorController();

        controller.airTransportCompanies = List.of(
                new AirTransportCompany("TAP Air Portugal", "TP", "TAP")
        );

        controller.activeCollaborators = List.of();

        ConsoleResult result = runUiWithInput(
                "1\n1\n",
                () -> new ListCustomerCollaboratorsUI(controller).show()
        );

        assertEquals(CustomerType.AIR_TRANSPORT_COMPANY, controller.listCustomerType);
        assertEquals("TAP", controller.listCustomerCode);
        assertTrue(result.output.contains("No active collaborators found for this customer."));
    }

    /**
     * Verifies US063 through the edit collaborator UI.
     *
     * <p>
     * The test simulates entering a collaborator ID, a new email and a new
     * phone number. It confirms that the UI delegates those three values to the
     * controller and shows a success message.
     * </p>
     */
    @Test
    void us063EditCustomerCollaboratorUiSubmitsCollaboratorIdEmailAndPhone() {
        CapturingCustomerCollaboratorController controller =
                new CapturingCustomerCollaboratorController();

        ConsoleResult result = runUiWithInput(
                "10\nnew.collab@aisafe.com\n+351900000099\n",
                () -> new EditCustomerCollaboratorUI(controller).show()
        );

        assertEquals(10L, controller.editedCollaboratorId);
        assertEquals("new.collab@aisafe.com", controller.editedEmail);
        assertEquals("+351900000099", controller.editedPhoneNumber);

        assertTrue(result.output.contains("Edit Customer Collaborator"));
        assertTrue(result.output.contains("Customer collaborator updated successfully."));
    }

    /**
     * Verifies US064 when the operator confirms the disable operation.
     *
     * <p>
     * The test simulates entering a collaborator ID and confirming with "Y".
     * It confirms that the UI calls the controller to disable that collaborator
     * and prints the success message.
     * </p>
     */
    @Test
    void us064DisableCustomerCollaboratorUiDisablesWhenOperatorConfirms() {
        CapturingCustomerCollaboratorController controller =
                new CapturingCustomerCollaboratorController();

        ConsoleResult result = runUiWithInput(
                "10\nY\n",
                () -> new DisableCustomerCollaboratorUI(controller).show()
        );

        assertEquals(10L, controller.disabledCollaboratorId);
        assertTrue(result.output.contains("Disable Customer Collaborator"));
        assertTrue(result.output.contains("Customer collaborator disabled successfully."));
    }

    /**
     * Verifies US064 when the operator cancels the disable operation.
     *
     * <p>
     * The test simulates entering a collaborator ID but answering "N" to the
     * confirmation prompt. It confirms that the controller is not called and
     * that the UI reports the operation as cancelled.
     * </p>
     */
    @Test
    void us064DisableCustomerCollaboratorUiDoesNotDisableWhenOperatorCancels() {
        CapturingCustomerCollaboratorController controller =
                new CapturingCustomerCollaboratorController();

        ConsoleResult result = runUiWithInput(
                "10\nN\n",
                () -> new DisableCustomerCollaboratorUI(controller).show()
        );

        assertNull(controller.disabledCollaboratorId);
        assertTrue(result.output.contains("Disable Customer Collaborator"));
        assertTrue(result.output.contains("Operation cancelled."));
    }

    /**
     * Runs a UI flow with simulated console input and captures its output.
     *
     * @param input text that will be provided through {@link System#in}
     * @param uiAction UI action to execute
     * @return captured console output
     */
    private ConsoleResult runUiWithInput(String input, Runnable uiAction) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));

        uiAction.run();

        return new ConsoleResult(output.toString(StandardCharsets.UTF_8));
    }

    /**
     * Creates a test user with one role.
     *
     * @param email user email
     * @param name user full name
     * @param phoneNumber user phone number
     * @param role assigned role
     * @return created user
     */
    private User user(String email, String name, String phoneNumber, Role role) {
        return new User(
                new Email(email),
                "Password123",
                eapli.aisafe.generic.domain.UserName.of(name),
                eapli.aisafe.generic.domain.PhoneNumber.of(phoneNumber),
                Set.of(role)
        );
    }

    /**
     * Creates a customer collaborator with a fixed identifier.
     *
     * <p>
     * The production entity normally receives its ID from persistence. These UI
     * tests need a visible collaborator ID for listing, so reflection is used to
     * set the private generated identifier.
     * </p>
     *
     * @param id collaborator identifier to expose in the UI table
     * @param customerType associated customer type
     * @param customerCode associated customer code
     * @param email associated user email
     * @param position collaborator position
     * @return created collaborator
     */
    private CustomerCollaborator collaborator(Long id,
                                              CustomerType customerType,
                                              String customerCode,
                                              String email,
                                              String position) {
        CustomerCollaborator collaborator = new CustomerCollaborator(
                customerType,
                customerCode,
                new Email(email),
                position
        );

        setCollaboratorId(collaborator, id);
        return collaborator;
    }

    /**
     * Assigns a generated collaborator identifier for test purposes.
     *
     * @param collaborator collaborator instance
     * @param id identifier to assign
     */
    private void setCollaboratorId(CustomerCollaborator collaborator, Long id) {
        try {
            Field field = CustomerCollaborator.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(collaborator, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Captured result of a console UI execution.
     *
     * @param output text written to {@link System#out}
     */
    private record ConsoleResult(String output) {
    }

    /**
     * Test double for {@link RegisterAirTransportCompanyController}.
     *
     * <p>
     * It captures the values received from the UI without touching persistence
     * or authentication.
     * </p>
     */
    private static class CapturingRegisterAirTransportCompanyController
            extends RegisterAirTransportCompanyController {

        String name;
        String iataCode;
        String icaoCode;

        CapturingRegisterAirTransportCompanyController() {
            super(null, null);
        }

        @Override
        public void registerCompany(String name, String iataCode, String icaoCode) {
            this.name = name;
            this.iataCode = iataCode;
            this.icaoCode = icaoCode;
        }
    }

    /**
     * Test double for {@link CustomerCollaboratorController}.
     *
     * <p>
     * It provides predefined lists to the UI and records which controller
     * methods were invoked, allowing the tests to verify the UI workflow without
     * depending on repositories, authentication, or a database.
     * </p>
     */
    private static class CapturingCustomerCollaboratorController
            extends CustomerCollaboratorController {

        List<User> availableUsers = new ArrayList<>();
        List<AirTransportCompany> airTransportCompanies = new ArrayList<>();
        List<AirControlArea> airControlAreas = new ArrayList<>();
        List<CustomerCollaborator> activeCollaborators = new ArrayList<>();
        Map<String, User> usersByEmail = Map.of();

        CustomerType availableCollaboratorUsersCustomerType;

        CustomerType addedCustomerType;
        String addedCustomerCode;
        String addedUserEmail;
        String addedPosition;

        CustomerType listCustomerType;
        String listCustomerCode;

        Long editedCollaboratorId;
        String editedEmail;
        String editedPhoneNumber;

        Long disabledCollaboratorId;

        CapturingCustomerCollaboratorController() {
            super(null, null, null, null, null);
        }

        @Override
        public List<User> availableCollaboratorUsers(CustomerType customerType) {
            this.availableCollaboratorUsersCustomerType = customerType;
            return availableUsers;
        }

        @Override
        public Iterable<AirTransportCompany> airTransportCompanies() {
            return airTransportCompanies;
        }

        @Override
        public Iterable<AirControlArea> airControlAreas() {
            return airControlAreas;
        }

        @Override
        public void addExistingUserAsCollaborator(CustomerType customerType,
                                                  String customerCode,
                                                  String userEmail,
                                                  String position) {
            this.addedCustomerType = customerType;
            this.addedCustomerCode = customerCode;
            this.addedUserEmail = userEmail;
            this.addedPosition = position;
        }

        @Override
        public List<CustomerCollaborator> listActiveCustomerCollaborators(CustomerType customerType,
                                                                          String customerCode) {
            this.listCustomerType = customerType;
            this.listCustomerCode = customerCode;
            return activeCollaborators;
        }

        @Override
        public User userByEmail(String email) {
            User user = usersByEmail.get(email);

            if (user == null) {
                throw new IllegalArgumentException("User not found");
            }

            return user;
        }

        @Override
        public void editCollaborator(Long collaboratorId,
                                     String newEmailStr,
                                     String newPhoneNumber) {
            this.editedCollaboratorId = collaboratorId;
            this.editedEmail = newEmailStr;
            this.editedPhoneNumber = newPhoneNumber;
        }

        @Override
        public void disableCollaborator(Long collaboratorId) {
            this.disabledCollaboratorId = collaboratorId;
        }
    }
}