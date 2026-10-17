package eapli.aisafe.app.backoffice.console.presentation.aircraftmodelmanagement;

import eapli.aisafe.aircraftmodelmanagement.application.AddEngineToAircraftModelController;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;
import eapli.aisafe.enginemanagement.domain.EngineModel;
import eapli.aisafe.enginemanagement.domain.EngineModelId;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console UI for US057 – Add an engine model to an aircraft model.
 */
public class AddEngineToAircraftModelUI {

    private final AddEngineToAircraftModelController controller;
    private final Scanner scanner;

    public AddEngineToAircraftModelUI(final AddEngineToAircraftModelController controller,
                                      final Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    public void show() {
        System.out.println("\n=== Add Engine Model to Aircraft Model ===");

        // ── Step 1: select aircraft model ─────────────────────────────────────
        final List<AircraftModel> aircraft = new ArrayList<>();
        controller.allAircraftModels().forEach(aircraft::add);

        if (aircraft.isEmpty()) {
            System.out.println("No aircraft models registered. Register an aircraft model first.");
            return;
        }

        System.out.println("\n-- Select Aircraft Model --");
        for (int i = 0; i < aircraft.size(); i++) {
            final AircraftModel a = aircraft.get(i);
            System.out.printf("  %d. %s (%s) | Engines: %d certified%n",
                    i + 1, a.modelId(), a.makerName(), a.certifiedEngines().size());
        }

        final int aircraftIndex = readIndexChoice("Select aircraft model: ", aircraft.size());
        final AircraftModel selected = aircraft.get(aircraftIndex);

        System.out.println("\n  Selected: " + selected.modelId() + " (" + selected.makerName() + ")");
        System.out.println("  Currently certified engines:");
        selected.certifiedEngines().forEach(e ->
                System.out.printf("    - %s (%s) [%s]%n", e.name(), e.makerId(), e.engineType()));

        // ── Step 2: select engine model ───────────────────────────────────────
        final List<EngineModel> engines = new ArrayList<>();
        controller.allEngineModels().forEach(engines::add);

        if (engines.isEmpty()) {
            System.out.println("\nNo engine models registered. Register an engine model first.");
            return;
        }

        System.out.println("\n-- Select Engine Model to Add --");
        for (int i = 0; i < engines.size(); i++) {
            final EngineModel e = engines.get(i);
            System.out.printf("  %d. %s (%s) [%s]%n", i + 1, e.name(), e.makerId(), e.engineType());
        }

        final int engineIndex = readIndexChoice("Select engine model to add: ", engines.size());
        final EngineModel engineToAdd = engines.get(engineIndex);

        // ── Step 3: confirm ───────────────────────────────────────────────────
        System.out.println("\n  Summary:");
        System.out.printf("    Aircraft : %s (%s)%n", selected.modelId(), selected.makerName());
        System.out.printf("    Engine   : %s (%s) [%s]%n",
                engineToAdd.name(), engineToAdd.makerId(), engineToAdd.engineType());

        if (!confirm()) {
            System.out.println("Operation cancelled.");
            return;
        }

        // ── Step 4: submit ────────────────────────────────────────────────────
        try {
            final AircraftModelId aircraftModelId =
                    AircraftModelId.of(selected.modelId(), selected.makerName());
            final EngineModelId engineModelId =
                    EngineModelId.of(engineToAdd.name(), engineToAdd.makerId());

            final AircraftModel updated =
                    controller.addEngineToAircraftModel(aircraftModelId, engineModelId);

            System.out.println("\nSuccess! Updated certified engines for "
                    + updated.modelId() + " (" + updated.makerName() + "):");
            updated.certifiedEngines().forEach(e ->
                    System.out.printf("  - %s (%s) [%s]%n", e.name(), e.makerId(), e.engineType()));

        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("\nERROR: " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("\nACCESS DENIED: " + e.getMessage());
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private int readIndexChoice(final String prompt, final int max) {
        while (true) {
            System.out.print(prompt);
            try {
                final int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= max) return choice - 1;
                System.out.printf("  Please enter a number between 1 and %d.%n", max);
            } catch (NumberFormatException ignored) {
                System.out.println("  Invalid input. Please enter a number.");
            }
        }
    }

    private boolean confirm() {
        while (true) {
            System.out.print("Confirm? (y/n): ");
            final String input = scanner.nextLine().trim().toLowerCase();
            if ("y".equals(input)) return true;
            if ("n".equals(input)) return false;
            System.out.println("  Please enter 'y' or 'n'.");
        }
    }
}
