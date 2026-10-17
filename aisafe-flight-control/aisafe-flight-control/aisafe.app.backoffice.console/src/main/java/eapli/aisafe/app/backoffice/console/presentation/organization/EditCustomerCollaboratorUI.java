package eapli.aisafe.app.backoffice.console.presentation.organization;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.organization.application.CustomerCollaboratorController;

/**
 * Console UI responsible for editing a customer's collaborator.
 *
 * <p>
 * This UI supports US063 - Edit a customer's collaborator. According to the
 * requirement, only the collaborator's email and phone number can be changed.
 * All other information, such as name, roles, customer and position, remains
 * unchanged and is protected by the application layer.
 * </p>
 */
public class EditCustomerCollaboratorUI extends AbstractUI {

    private final CustomerCollaboratorController controller;

    /**
     * Creates the edit customer collaborator UI.
     *
     * @param controller controller responsible for customer collaborator
     *                   operations
     */
    public EditCustomerCollaboratorUI(CustomerCollaboratorController controller) {
        this.controller = controller;
    }

    /**
     * Shows the edit collaborator flow and delegates the update to the
     * controller.
     *
     * @return {@code true} after the operation is processed
     */
    @Override
    protected boolean doShow() {
        printHeader("Edit Customer Collaborator");

        System.out.print("Collaborator ID: ");
        Long collaboratorId = Long.parseLong(scanner.nextLine());

        System.out.print("New email: ");
        String newEmail = scanner.nextLine();

        System.out.print("New phone number: ");
        String newPhoneNumber = scanner.nextLine();

        controller.editCollaborator(collaboratorId, newEmail, newPhoneNumber);

        System.out.println("\nCustomer collaborator updated successfully.");

        return true;
    }

    /**
     * Prints a formatted screen header.
     *
     * @param title header title
     */
    private void printHeader(String title) {
        System.out.println();
        System.out.println("============================================================");
        System.out.println("  " + title);
        System.out.println("============================================================");
    }
}
