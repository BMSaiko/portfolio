package eapli.aisafe.app.backoffice.console.presentation.pilotmanagement;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.pilotmanagement.application.AddPilotController;

import java.util.ArrayList;
import java.util.List;

public class AddPilotUI extends AbstractUI {

    private final AddPilotController controller;

    public AddPilotUI(AddPilotController controller) {
        this.controller = controller;
    }

    @Override
    protected boolean doShow() {
        printHeader("Register a New Pilot");

        System.out.print("Pilot name: ");
        String name = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        String phoneNumber = "";
        while (true) {
            System.out.print("Phone Number (9 digits): ");
            phoneNumber = scanner.nextLine().trim();
            if (phoneNumber.matches("\\d{9}")) {
                break;
            } else {
                System.out.println("Invalid phone number! Please enter exactly 9 numeric digits.");
            }
        }

        // POSIÇÃO ESCONDIDA E FIXADA!
        String position = "Pilot";

        System.out.print("Temporary Password: ");
        String password = scanner.nextLine();

        // ---------------------------------------------------------
        // SELEÇÃO DE MODELOS DE AVIÃO A PARTIR DA BASE DE DADOS
        // ---------------------------------------------------------

        // 1. Ir buscar os modelos ao Controller/DB e guardar numa Lista
        List<AircraftModel> availableModels = new ArrayList<>();
        controller.getAvailableAircraftModels().forEach(availableModels::add);

        if (availableModels.isEmpty()) {
            System.out.println("\n[ERROR] No Aircraft Models found in the database!");
            System.out.println("Please run the bootstrap or register models first.");
            return false; // Cancela a operação se não houver aviões
        }

        List<AircraftModel> selectedModels = new ArrayList<>();
        System.out.println("\n--- Aircraft Model Certifications ---");

        while (true) {
            System.out.println("\nSelect an Aircraft Model the pilot is certified for:");
            // Mostra o menu de opções
            for (int i = 0; i < availableModels.size(); i++) {
                System.out.println((i + 1) + ". " + availableModels.get(i).identity().toString());
            }
            System.out.println("0. Finish selection");

            System.out.print("Option: ");
            String optionStr = scanner.nextLine();
            int option;

            try {
                option = Integer.parseInt(optionStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid option. Please enter a number.");
                continue;
            }

            if (option == 0) {
                if (selectedModels.isEmpty()) {
                    System.out.println("Error: A pilot must be certified for at least one aircraft model.");
                    continue;
                }
                break; // Sai do ciclo e avança para a criação
            } else if (option > 0 && option <= availableModels.size()) {
                AircraftModel selected = availableModels.get(option - 1);

                // Evita que adiciones o mesmo avião duas vezes
                if (!selectedModels.contains(selected)) {
                    selectedModels.add(selected);
                    System.out.println(">> Added: " + selected.identity().toString());
                } else {
                    System.out.println(">> Model already added to this pilot!");
                }
            } else {
                System.out.println("Invalid option. Try again.");
            }
        }

        try {
            controller.addPilot(email, name, phoneNumber, position, selectedModels, password);
            System.out.println("\nPilot registered successfully!");
            System.out.println("Name: " + name);
            System.out.println("Email: " + email);

            // Nova formatação para mostrar a lista de modelos de avião!
            System.out.println("Certifications:");
            for (AircraftModel model : selectedModels) {
                System.out.println("  - " + model.identity().toString());
            }

        } catch (Exception e) {
            System.out.println("\n[ERROR] Failed to register pilot: " + e.getMessage());
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