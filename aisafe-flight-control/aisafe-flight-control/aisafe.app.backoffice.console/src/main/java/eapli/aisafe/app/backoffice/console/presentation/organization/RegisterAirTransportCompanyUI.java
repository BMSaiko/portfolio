package eapli.aisafe.app.backoffice.console.presentation.organization;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.organization.application.RegisterAirTransportCompanyController;

/**
 * Console UI responsible for registering an air transport company.
 *
 * <p>
 * This UI supports US060 - Register an air transport company. It collects the
 * company name, IATA code and ICAO code from the Backoffice Operator and
 * delegates the registration to {@link RegisterAirTransportCompanyController}.
 * </p>
 */
public class RegisterAirTransportCompanyUI extends AbstractUI {

    private final RegisterAirTransportCompanyController controller;

    /**
     * Creates the register air transport company UI.
     *
     * @param controller controller responsible for registering air transport
     *                   companies
     */
    public RegisterAirTransportCompanyUI(RegisterAirTransportCompanyController controller) {
        this.controller = controller;
    }

    /**
     * Shows the registration form and submits the collected data to the
     * controller.
     *
     * @return {@code true} after the operation is processed
     */
    @Override
    protected boolean doShow() {
        printHeader("Register Air Transport Company");

        System.out.print("Company name: ");
        String name = scanner.nextLine();

        System.out.print("IATA code (2 letters): ");
        String iataCode = scanner.nextLine();

        System.out.print("ICAO code (2-3 letters): ");
        String icaoCode = scanner.nextLine();

        controller.registerCompany(name, iataCode, icaoCode);

        System.out.println("\nAir transport company registered successfully.");
        System.out.println("Name: " + name);
        System.out.println("IATA: " + iataCode);
        System.out.println("ICAO: " + icaoCode);

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
