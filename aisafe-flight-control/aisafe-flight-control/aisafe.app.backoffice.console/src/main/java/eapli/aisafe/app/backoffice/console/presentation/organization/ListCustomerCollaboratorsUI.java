package eapli.aisafe.app.backoffice.console.presentation.organization;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.organization.application.CustomerCollaboratorController;
import eapli.aisafe.organization.domain.CustomerCollaborator;
import eapli.aisafe.organization.domain.CustomerType;
import eapli.aisafe.usermanagement.domain.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Console UI responsible for listing a customer's active collaborators.
 *
 * <p>
 * This UI supports US062 - List customer's collaborators.
 * </p>
 *
 * <p>
 * The operator selects a customer, either an air transport company or an air
 * control area, and the UI lists the active collaborators associated with that
 * customer.
 * </p>
 *
 * <p>
 * The listing includes the collaborator ID so that the operator can use it in
 * US063 - Edit a customer's collaborator and US064 - Disable a customer's
 * collaborator.
 * </p>
 */
public class ListCustomerCollaboratorsUI extends AbstractUI {

    private final CustomerCollaboratorController controller;

    /**
     * Creates the list customer collaborators UI.
     *
     * @param controller customer collaborator application controller
     */
    public ListCustomerCollaboratorsUI(CustomerCollaboratorController controller) {
        this.controller = controller;
    }

    /**
     * Shows the customer collaborator listing flow.
     *
     * @return {@code true} after the listing finishes
     */
    @Override
    protected boolean doShow() {
        printHeader("List Customer Collaborators");

        CustomerType customerType = readCustomerType();
        String customerCode = readCustomerCode(customerType);

        List<CustomerCollaborator> collaborators =
                controller.listActiveCustomerCollaborators(customerType, customerCode);

        if (collaborators.isEmpty()) {
            System.out.println("\nNo active collaborators found for this customer.");
            return true;
        }

        System.out.println("\nActive collaborators:");
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s %-32s %-25s %-15s %-25s%n",
                "ID", "Email", "Name", "Phone", "Position");
        System.out.println("----------------------------------------------------------------------------------------------------");

        for (CustomerCollaborator collaborator : collaborators) {
            User user = controller.userByEmail(collaborator.userEmail().value());

            System.out.printf("%-5d %-32s %-25s %-15s %-25s%n",
                    collaborator.id(),
                    user.email().value(),
                    user.name(),
                    user.phoneNumber(),
                    collaborator.position());
        }

        return true;
    }

    /**
     * Reads the customer type selected by the operator.
     *
     * @return selected customer type
     */
    private CustomerType readCustomerType() {
        System.out.println("\nCustomer type:");
        System.out.println("1. Air transport company");
        System.out.println("2. Air control area");
        System.out.print("Option: ");

        String option = scanner.nextLine();

        if ("1".equals(option)) {
            return CustomerType.AIR_TRANSPORT_COMPANY;
        }

        if ("2".equals(option)) {
            return CustomerType.AIR_CONTROL_AREA;
        }

        throw new IllegalArgumentException("Invalid customer type");
    }

    /**
     * Reads the customer code according to the selected customer type.
     *
     * @param customerType selected customer type
     * @return selected customer code
     */
    private String readCustomerCode(CustomerType customerType) {
        if (customerType == CustomerType.AIR_TRANSPORT_COMPANY) {
            return readAirTransportCompanyCode();
        }

        return readAirControlAreaCode();
    }

    /**
     * Lists existing air transport companies and returns the selected ICAO code.
     *
     * @return selected air transport company ICAO code
     */
    private String readAirTransportCompanyCode() {
        List<String> companyCodes = new ArrayList<>();

        System.out.println("\nExisting air transport companies:");
        System.out.println("--------------------------------------------------------------------------");
        System.out.printf("%-5s %-30s %-10s %-10s%n", "#", "Name", "IATA", "ICAO");
        System.out.println("--------------------------------------------------------------------------");

        int index = 1;
        for (var company : controller.airTransportCompanies()) {
            System.out.printf("%-5d %-30s %-10s %-10s%n",
                    index,
                    company.companyName(),
                    company.iataCode(),
                    company.icaoCode());

            companyCodes.add(company.icaoCode());
            index++;
        }

        if (companyCodes.isEmpty()) {
            throw new IllegalArgumentException("There are no air transport companies registered");
        }

        System.out.print("\nSelect company: ");
        int option = Integer.parseInt(scanner.nextLine());

        if (option < 1 || option > companyCodes.size()) {
            throw new IllegalArgumentException("Invalid company option");
        }

        return companyCodes.get(option - 1);
    }

    /**
     * Lists existing air control areas and returns the selected area code.
     *
     * @return selected air control area code
     */
    private String readAirControlAreaCode() {
        List<String> areaCodes = new ArrayList<>();

        System.out.println("\nExisting air control areas:");
        System.out.println("--------------------------------------------------------------------------");
        System.out.printf("%-5s %-15s %-35s %-15s%n", "#", "Code", "Name", "Min Fuel");
        System.out.println("--------------------------------------------------------------------------");

        int index = 1;
        for (var area : controller.airControlAreas()) {
            String code = area.identity().value();

            System.out.printf("%-5d %-15s %-35s %-15.1f%n",
                    index,
                    code,
                    area.name(),
                    area.minimumFuelRequired());

            areaCodes.add(code);
            index++;
        }

        if (areaCodes.isEmpty()) {
            throw new IllegalArgumentException("There are no air control areas registered");
        }

        System.out.print("\nSelect air control area: ");
        int option = Integer.parseInt(scanner.nextLine());

        if (option < 1 || option > areaCodes.size()) {
            throw new IllegalArgumentException("Invalid air control area option");
        }

        return areaCodes.get(option - 1);
    }

    /**
     * Prints a formatted console header.
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
