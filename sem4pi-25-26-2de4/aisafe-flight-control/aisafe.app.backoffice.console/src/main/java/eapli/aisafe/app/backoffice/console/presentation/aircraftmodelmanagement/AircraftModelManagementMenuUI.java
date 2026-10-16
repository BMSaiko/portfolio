package eapli.aisafe.app.backoffice.console.presentation.aircraftmodelmanagement;

import eapli.aisafe.aircraftmodelmanagement.application.AddEngineToAircraftModelController;
import eapli.aisafe.aircraftmodelmanagement.application.RegisterAircraftModelController;
import eapli.aisafe.aircraftmodelmanagement.application.RemoveEngineFromAircraftModelController;
import eapli.aisafe.app.common.console.AbstractUI;

import java.util.Scanner;

/**
 * Console submenu for Aircraft Model Management.
 * Groups US055, US057 and US058 under a single menu entry.
 */
public class AircraftModelManagementMenuUI extends AbstractUI {

    private final RegisterAircraftModelController registerController;
    private final AddEngineToAircraftModelController addEngineController;
    private final RemoveEngineFromAircraftModelController removeEngineController;
    private final Scanner scanner;

    public AircraftModelManagementMenuUI(
            final RegisterAircraftModelController registerController,
            final AddEngineToAircraftModelController addEngineController,
            final RemoveEngineFromAircraftModelController removeEngineController,
            final Scanner scanner) {
        this.registerController      = registerController;
        this.addEngineController     = addEngineController;
        this.removeEngineController  = removeEngineController;
        this.scanner                 = scanner;
    }

    @Override
    protected boolean doShow() {
        while (true) {
            System.out.println("\n============================================================");
            System.out.println("  Aircraft Model Management");
            System.out.println("============================================================");
            System.out.println("1. Register Aircraft Model");
            System.out.println("2. Add Engine to Aircraft Model");
            System.out.println("3. Remove Engine from Aircraft Model");
            System.out.println("0. Back");
            System.out.print("\nOption: ");

            final String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    new RegisterAircraftModelUI(registerController, scanner).show();
                    break;
                case "2":
                    new AddEngineToAircraftModelUI(addEngineController, scanner).show();
                    break;
                case "3":
                    new RemoveEngineFromAircraftModelUI(removeEngineController, scanner).show();
                    break;
                case "0":
                    return false;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}
