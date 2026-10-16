package eapli.aisafe.app.backoffice.console.presentation.pilotmanagement;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.pilotmanagement.application.RemovePilotController;
import eapli.aisafe.pilotmanagement.domain.Pilot;

import java.util.ArrayList;
import java.util.List;

public class RemovePilotUI extends AbstractUI {

    private final RemovePilotController controller;

    public RemovePilotUI(RemovePilotController controller) {
        this.controller = controller;
    }

    @Override
    protected boolean doShow() {
        printHeader("Remove a Pilot (Deactivate)");

        try {
            List<Pilot> pilots = new ArrayList<>();
            controller.activePilotsOfMyCompany().forEach(pilots::add);

            if (pilots.isEmpty()) {
                System.out.println("There are no active pilots in your company to remove.");
                return false;
            }

            System.out.println("Select the pilot you want to remove:\n");
            for (int i = 0; i < pilots.size(); i++) {
                System.out.printf("%d. %s (%s)%n", i + 1, pilots.get(i).getName(), pilots.get(i).email().value());
            }
            System.out.println("0. Cancel");

            System.out.print("\nOption: ");
            String optionStr = scanner.nextLine();
            int option = Integer.parseInt(optionStr);

            if (option == 0) {
                return false;
            }

            if (option > 0 && option <= pilots.size()) {
                Pilot selectedPilot = pilots.get(option - 1);

                System.out.print("Are you sure you want to deactivate " + selectedPilot.getName() + "? (Y/N): ");
                String confirm = scanner.nextLine();

                if (confirm.equalsIgnoreCase("Y")) {
                    controller.deactivatePilot(selectedPilot);
                    System.out.println("\n[SUCCESS] Pilot deactivated successfully!");
                } else {
                    System.out.println("\nOperation cancelled.");
                }
            } else {
                System.out.println("Invalid option.");
            }

        } catch (NumberFormatException e) {
            System.out.println("\n[ERROR] Invalid input. Please enter a number.");
        } catch (Exception e) {
            System.out.println("\n[ERROR] Could not remove pilot: " + e.getMessage());
        }

        return true;
    }

    private void printHeader(String title) {
        System.out.println();
        System.out.println("============================================================");
        System.out.println("  " + title);
        System.out.println("============================================================");
    }
}