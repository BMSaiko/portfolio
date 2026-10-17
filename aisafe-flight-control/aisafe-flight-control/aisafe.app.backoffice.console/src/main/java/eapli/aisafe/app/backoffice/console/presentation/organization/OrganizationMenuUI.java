package eapli.aisafe.app.backoffice.console.presentation.organization;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.organization.application.CustomerCollaboratorController;
import eapli.aisafe.organization.application.RegisterAirTransportCompanyController;

/**
 * Console menu for customer and organization management operations.
 *
 * <p>
 * This menu groups the UI entry points for the organization-related user
 * stories:
 * </p>
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
 * This class only coordinates menu navigation. Each user story has its own
 * dedicated UI class.
 * </p>
 */
public class OrganizationMenuUI extends AbstractUI {

    private final RegisterAirTransportCompanyController companyController;
    private final CustomerCollaboratorController collaboratorController;

    /**
     * Creates the organization/customer management menu.
     *
     * @param companyController      controller used by US060
     * @param collaboratorController controller used by US061-US064
     */
    public OrganizationMenuUI(RegisterAirTransportCompanyController companyController,
                              CustomerCollaboratorController collaboratorController) {
        this.companyController = companyController;
        this.collaboratorController = collaboratorController;
    }

    /**
     * Shows the customer management menu and dispatches the selected option to
     * the corresponding UI.
     *
     * @return {@code false} when the user chooses to go back
     */
    @Override
    protected boolean doShow() {
        while (true) {
            printHeader("Customer Management");

            System.out.println("1. Register air transport company");
            System.out.println("2. Add customer's collaborator");
            System.out.println("3. List customer's collaborators");
            System.out.println("4. Edit customer's collaborator");
            System.out.println("5. Disable customer's collaborator");
            System.out.println("0. Back");

            System.out.print("\nOption: ");
            String option = scanner.nextLine();

            try {
                switch (option) {
                    case "1":
                        new RegisterAirTransportCompanyUI(companyController).show();
                        pause();
                        break;
                    case "2":
                        new AddCustomerCollaboratorUI(collaboratorController).show();
                        pause();
                        break;
                    case "3":
                        new ListCustomerCollaboratorsUI(collaboratorController).show();
                        pause();
                        break;
                    case "4":
                        new EditCustomerCollaboratorUI(collaboratorController).show();
                        pause();
                        break;
                    case "5":
                        new DisableCustomerCollaboratorUI(collaboratorController).show();
                        pause();
                        break;
                    case "0":
                        return false;
                    default:
                        System.out.println("Invalid option.");
                        pause();
                }
            } catch (Exception e) {
                System.out.println("\nError: " + e.getMessage());
                pause();
            }
        }
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

    /**
     * Waits for user confirmation before returning to the menu.
     */
    private void pause() {
        System.out.print("\nPress ENTER to continue...");
        scanner.nextLine();
    }
}
