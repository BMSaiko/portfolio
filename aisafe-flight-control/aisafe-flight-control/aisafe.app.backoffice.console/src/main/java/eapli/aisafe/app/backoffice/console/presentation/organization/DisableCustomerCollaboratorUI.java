package eapli.aisafe.app.backoffice.console.presentation.organization;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.organization.application.CustomerCollaboratorController;

/**
 * Console UI responsible for disabling a customer's collaborator.
 *
 * <p>
 * This UI supports US064 - Disable a customer's collaborator. It asks for the
 * collaborator identifier and confirmation before delegating the operation to
 * {@link CustomerCollaboratorController}.
 * </p>
 *
 * <p>
 * Disabling a collaborator deactivates the associated system user, preventing
 * that user from logging in and from appearing in active collaborator listings.
 * </p>
 */
public class DisableCustomerCollaboratorUI extends AbstractUI {

    private final CustomerCollaboratorController controller;

    /**
     * Creates the disable customer collaborator UI.
     *
     * @param controller controller responsible for customer collaborator
     *                   operations
     */
    public DisableCustomerCollaboratorUI(CustomerCollaboratorController controller) {
        this.controller = controller;
    }

    /**
     * Shows the disable collaborator flow.
     *
     * @return {@code true} after the operation is processed or cancelled
     */
    @Override
    protected boolean doShow() {
        printHeader("Disable Customer Collaborator");

        System.out.print("Collaborator ID: ");
        Long collaboratorId = Long.parseLong(scanner.nextLine());

        System.out.print("Confirm disable collaborator? (Y/N): ");
        String confirmation = scanner.nextLine();

        if (!confirmation.equalsIgnoreCase("Y")) {
            System.out.println("\nOperation cancelled.");
            return true;
        }

        controller.disableCollaborator(collaboratorId);

        System.out.println("\nCustomer collaborator disabled successfully.");

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
