package eapli.aisafe.app.backoffice.console.presentation.pilotmanagement;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.pilotmanagement.application.ListPilotsController;
import eapli.aisafe.pilotmanagement.domain.Pilot;

public class ListPilotsUI extends AbstractUI {

    private final ListPilotsController controller;

    public ListPilotsUI(ListPilotsController controller) {
        this.controller = controller;
    }

    @Override
    protected boolean doShow() {
        printHeader("Pilots of My Air Transport Company");

        try {
            Iterable<Pilot> pilots = controller.listPilotsOfMyCompany();

            // Verifica se a lista está vazia (usando iterator porque é um Iterable)
            if (!pilots.iterator().hasNext()) {
                System.out.println("No pilots registered for your company yet.");
                return true;
            }

            // Cabeçalho da tabela
            System.out.printf("%-25s | %-30s | %-15s | %-10s%n", "Name", "Email", "Position", "Status");
            System.out.println("------------------------------------------------------------------------------------------");

            // Imprimir cada piloto na tabela
            for (Pilot p : pilots) {
                System.out.printf("%-25s | %-30s | %-15s | %-10s%n",
                        p.getName(),
                        p.email().value(),
                        p.getPosition(),
                        p.isActive() ? "ACTIVE" : "INACTIVE");
            }

            System.out.println("------------------------------------------------------------------------------------------");

        } catch (Exception e) {
            System.out.println("\n[ERROR] Could not list pilots: " + e.getMessage());
        }

        System.out.print("\nPress Enter to go back...");
        scanner.nextLine();

        return true;
    }

    private void printHeader(String title) {
        System.out.println();
        System.out.println("==========================================================================================");
        System.out.println("  " + title);
        System.out.println("==========================================================================================");
    }
}