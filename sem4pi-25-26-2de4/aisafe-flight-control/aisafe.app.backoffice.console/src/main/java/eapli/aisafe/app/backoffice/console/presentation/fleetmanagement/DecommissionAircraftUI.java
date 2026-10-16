package eapli.aisafe.app.backoffice.console.presentation.fleetmanagement;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.fleetmanagement.application.DecommissionAircraftController;
import eapli.framework.io.util.Console;

import java.util.Scanner;

public class DecommissionAircraftUI extends AbstractUI {

    private final DecommissionAircraftController controller;
    private final Scanner scanner;

    public DecommissionAircraftUI(DecommissionAircraftController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    @Override
    protected boolean doShow() {
        System.out.println("\n--- Decommission Aircraft ---");

        try {
            String registration = Console.readLine("Enter the Aircraft Registration to decommission (e.g., CS-TPA): ");

            // Call the controller
            controller.decommissionAircraft(registration);

            System.out.println("\n[SUCCESS] Aircraft " + registration + " has been successfully decommissioned!");
            System.out.println("Its operational status is now: RETIRED.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("\n[BUSINESS ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n[UNEXPECTED ERROR] " + e.getMessage());
        }

        return false;
    }
}