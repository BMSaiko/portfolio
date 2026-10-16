package eapli.aisafe.app.backoffice.console.presentation.aircraftmodelmanagement;

import eapli.aisafe.aircraftmodelmanagement.application.RemoveEngineFromAircraftModelController;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;
import eapli.aisafe.enginemanagement.domain.EngineModel;
import eapli.aisafe.enginemanagement.domain.EngineModelId;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console UI for US058 – Remove an engine model from an aircraft model.
 */
public class RemoveEngineFromAircraftModelUI {

    private final RemoveEngineFromAircraftModelController controller;
    private final Scanner scanner;

    public RemoveEngineFromAircraftModelUI(
            final RemoveEngineFromAircraftModelController controller,
            final Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    public void show() {
        System.out.println("\n=== Remove Engine Model from Aircraft Model ===");

        // ── Step 1: select aircraft model ─────────────────────────────────────
        final List<AircraftModel> aircraft = new ArrayList<>();
        controller.allAircraftModels().forEach(aircraft::add);

        if (aircraft.isEmpty()) {
            System.out.println("No aircraft models registered.");
            return;
        }

        System.out.println("\n-- Select Aircraft Model --");
        for (int i = 0; i < aircraft.size(); i++) {
            final AircraftModel a = aircraft.get(i);
            System.out.printf("  %d. %s (%s) | Engines: %d certified%n",
                    i + 1, a.modelId(), a.makerName(),
                    a.certifiedEngines().size());
        }

        final int aircraftIndex = readIndexChoice(
                "Select aircraft model: ", aircraft.size());
        final AircraftModel selected = aircraft.get(aircraftIndex);

        // ── Step 2: show certified engines and select one to remove ───────────
        final List<EngineModel> certified =
                new ArrayList<>(selected.certifiedEngines());

        System.out.printf("%n  Selected: %s (%s)%n",
                selected.modelId(), selected.makerName());
        System.out.println("  Currently certified engines:");
        for (int i = 0; i < certified.size(); i++) {
            final EngineModel e = certified.get(i);
            System.out.printf("    %d. %s (%s) [%s]%n",
                    i + 1, e.name(), e.makerId(), e.engineType());
        }

        if (certified.size() == 1) {
            System.out.println(
                    "\n  WARNING: This aircraft model has only one certified engine.");
            System.out.println(
                    "  Removal is not permitted — at least one must remain.");
            return;
        }

        final int engineIndex = readIndexChoice(
                "Select engine to remove: ", certified.size());
        final EngineModel engineToRemove = certified.get(engineIndex);

        // ── Step 3: confirm ───────────────────────────────────────────────────
        System.out.println("\n  Summary:");
        System.out.printf("    Aircraft : %s (%s)%n",
                selected.modelId(), selected.makerName());
        System.out.printf("    Remove   : %s (%s) [%s]%n",
                engineToRemove.name(), engineToRemove.makerId(),
                engineToRemove.engineType());

        if (!confirm()) {
            System.out.println("Operation cancelled.");
            return;
        }

        // ── Step 4: submit ────────────────────────────────────────────────────
        try {
            final AircraftModelId aircraftModelId =
                    AircraftModelId.of(selected.modelId(), selected.makerName());
            final EngineModelId engineModelId =
                    EngineModelId.of(engineToRemove.name(), engineToRemove.makerId());

            final AircraftModel updated =
                    controller.removeEngineFromAircraftModel(
                            aircraftModelId, engineModelId);

            System.out.println("\nSuccess! Updated certified engines for "
                    + updated.modelId() + " (" + updated.makerName() + "):");
            updated.certifiedEngines().forEach(e ->
                    System.out.printf("  - %s (%s) [%s]%n",
                            e.name(), e.makerId(), e.engineType()));

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
                System.out.printf(
                        "  Please enter a number between 1 and %d.%n", max);
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
