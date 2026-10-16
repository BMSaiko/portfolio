package eapli.aisafe.app.backoffice.console.presentation.fleetmanagement;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.fleetmanagement.application.ListFleetController;
import eapli.aisafe.fleetmanagement.domain.Aircraft;
import eapli.framework.io.util.Console;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ListFleetUI extends AbstractUI {

    private final ListFleetController controller;
    private final Scanner scanner;

    public ListFleetUI(ListFleetController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    @Override
    protected boolean doShow() {
        System.out.println("\n=== List My Company's Fleet ===");
        System.out.println("1. List Entire Fleet (US072)");
        System.out.println("2. Filter by Model (US072a)");
        System.out.println("3. Filter by Maker (US072b)");
        System.out.println("4. Filter by Capacity (US072c)");
        System.out.println("5. Filter by Age (US072d)");
        System.out.println("0. Cancel");

        int option = Console.readInteger("\nSelect a filter option: ");

        if (option == 0) return false;

        try {
            // Vai buscar a frota toda para extrair as opções disponíveis dinamicamente
            List<Aircraft> myFleet = StreamSupport.stream(controller.getMyCompanyFleet().spliterator(), false)
                    .collect(Collectors.toList());

            if (myFleet.isEmpty()) {
                System.out.println("\n[INFO] Your company's fleet is currently empty.");
                return false;
            }

            Iterable<Aircraft> fleet = null;

            switch (option) {
                case 1:
                    fleet = myFleet;
                    break;
                case 2:
                    List<String> models = myFleet.stream()
                            .map(a -> a.model().identity().modelId())
                            .distinct().sorted().collect(Collectors.toList());
                    System.out.println("\n--- Select Model ---");
                    for (int i = 0; i < models.size(); i++) {
                        System.out.printf("%d. %s\n", i + 1, models.get(i));
                    }
                    int modelIdx = Console.readInteger("Option: ") - 1;
                    fleet = controller.getFleetByModel(models.get(modelIdx));
                    break;
                case 3:
                    List<String> makers = myFleet.stream()
                            .map(a -> a.model().identity().makerName())
                            .distinct().sorted().collect(Collectors.toList());
                    System.out.println("\n--- Select Maker ---");
                    for (int i = 0; i < makers.size(); i++) {
                        System.out.printf("%d. %s\n", i + 1, makers.get(i));
                    }
                    int makerIdx = Console.readInteger("Option: ") - 1;
                    fleet = controller.getFleetByMaker(makers.get(makerIdx));
                    break;
                case 4:
                    List<Integer> capacities = myFleet.stream()
                            .map(a -> a.cabinConfiguration().totalSeats())
                            .distinct().sorted().collect(Collectors.toList());
                    System.out.println("\n--- Select Capacity ---");
                    for (int i = 0; i < capacities.size(); i++) {
                        System.out.printf("%d. %d seats\n", i + 1, capacities.get(i));
                    }
                    int capIdx = Console.readInteger("Option: ") - 1;
                    fleet = controller.getFleetByCapacity(capacities.get(capIdx));
                    break;
                case 5:
                    List<Integer> ages = myFleet.stream()
                            .map(Aircraft::age)
                            .distinct().sorted().collect(Collectors.toList());
                    System.out.println("\n--- Select Aircraft Age ---");
                    for (int i = 0; i < ages.size(); i++) {
                        System.out.printf("%d. %d years old\n", i + 1, ages.get(i));
                    }
                    int ageIdx = Console.readInteger("Option: ") - 1;
                    fleet = controller.getFleetByAge(ages.get(ageIdx));
                    break;
                default:
                    System.out.println("Invalid option.");
                    return true; // Mantém a UI aberta
            }

            // Função que imprime a lista
            if (fleet != null) {
                printFleet(fleet);
            }

        } catch (IndexOutOfBoundsException e) {
            System.out.println("\n[ERROR] Invalid option selected from the menu.");
        } catch (Exception e) {
            System.out.println("\n[ERROR] " + e.getMessage());
        }

        return false;
    }

    private void printFleet(Iterable<Aircraft> fleet) {
        System.out.println("\n--- Search Results ---");
        boolean found = false;

        for (Aircraft a : fleet) {
            found = true;
            System.out.printf("Registration: %-8s | Model: %-6s | Maker: %-10s | Capacity: %-4d | Age: %-3d | Status: %s\n",
                    a.identity(),
                    a.model().identity().modelId(),
                    a.model().identity().makerName(),
                    a.cabinConfiguration().totalSeats(),
                    a.age(),
                    a.status());
        }

        if (!found) {
            System.out.println("No aircraft found matching your criteria.");
        }
        System.out.println("----------------------\n");
    }
}