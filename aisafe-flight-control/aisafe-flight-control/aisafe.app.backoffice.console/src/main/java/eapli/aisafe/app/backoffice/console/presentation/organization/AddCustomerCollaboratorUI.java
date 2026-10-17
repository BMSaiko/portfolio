package eapli.aisafe.app.backoffice.console.presentation.organization;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.organization.application.CustomerCollaboratorController;
import eapli.aisafe.organization.domain.CustomerType;
import eapli.aisafe.usermanagement.domain.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Console UI responsible for adding a customer's collaborator.
 */
public class AddCustomerCollaboratorUI extends AbstractUI {

    private final CustomerCollaboratorController controller;

    public AddCustomerCollaboratorUI(CustomerCollaboratorController controller) {
        this.controller = controller;
    }

    @Override
    protected boolean doShow() {
        printHeader("Add Customer Collaborator");

        CustomerType customerType = readCustomerType();
        List<User> availableUsers = controller.availableCollaboratorUsers(customerType);

        if (availableUsers.isEmpty()) {
            System.out.println("There are no available users for the selected collaborator type.");
            System.out.println("Create a user with the correct collaborator role first.");
            return true;
        }

        printAvailableUsers(availableUsers, customerType);

        System.out.print("\nSelect user: ");
        int userOption = Integer.parseInt(scanner.nextLine());

        if (userOption < 1 || userOption > availableUsers.size()) {
            throw new IllegalArgumentException("Invalid user option");
        }

        User selectedUser = availableUsers.get(userOption - 1);
        String customerCode = readCustomerCode(customerType);

        System.out.print("Position: ");
        String position = scanner.nextLine();

        controller.addExistingUserAsCollaborator(
                customerType,
                customerCode,
                selectedUser.email().value(),
                position
        );

        System.out.println("\nCustomer collaborator added successfully.");
        System.out.println("User: " + selectedUser.email().value());
        System.out.println("Customer: " + customerType + " / " + customerCode);

        return true;
    }

    private void printAvailableUsers(List<User> availableUsers, CustomerType customerType) {
        System.out.println("\nAvailable users for " + customerType + ":");
        System.out.println("--------------------------------------------------------------------------");
        System.out.printf("%-5s %-32s %-25s %-15s%n", "#", "Email", "Name", "Phone");
        System.out.println("--------------------------------------------------------------------------");

        for (int i = 0; i < availableUsers.size(); i++) {
            User user = availableUsers.get(i);
            System.out.printf("%-5d %-32s %-25s %-15s%n",
                    i + 1,
                    user.email().value(),
                    user.name(),
                    user.phoneNumber());
        }
    }

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

    private String readCustomerCode(CustomerType customerType) {
        if (customerType == CustomerType.AIR_TRANSPORT_COMPANY) {
            return readAirTransportCompanyCode();
        }

        return readAirControlAreaCode();
    }

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

    private void printHeader(String title) {
        System.out.println();
        System.out.println("============================================================");
        System.out.println("  " + title);
        System.out.println("============================================================");
    }
}