package eapli.aisafe.app.backoffice.console.presentation.enginemanagement;

import eapli.aisafe.enginemanagement.application.RegisterEngineModelController;
import eapli.aisafe.enginemanagement.domain.EngineModel;
import eapli.aisafe.enginemanagement.domain.EngineType;
import eapli.aisafe.enginemanagement.domain.FuelType;

import java.util.Scanner;

/**
 * Console UI for US056 – Create an aircraft engine model.
 */
public class RegisterEngineModelUI {

    private final RegisterEngineModelController controller;
    private final Scanner scanner;

    public RegisterEngineModelUI(final RegisterEngineModelController controller,
                                 final Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    public void show() {
        System.out.println("\n=== Register Aircraft Engine Model ===");

        System.out.print("Engine Model Name (e.g. GE 90B): ");
        final String name = scanner.nextLine().trim();

        System.out.print("Maker ID (e.g. GE): ");
        final String makerId = scanner.nextLine().trim();

        final String engineType = readEngineType();
        final FuelType fuelType = readFuelType();

        System.out.println("\n-- Thrust Parameters --");
        final double thrustStandstill = readPositiveDouble(
                "Thrust at standstill / sea level (N): ");
        final double thrustCruise = readDoubleLessThan(
                "Thrust at cruise speed (N): ", thrustStandstill,
                "Cruise thrust must be less than standstill thrust (%.0f N).");
        final double cruiseMach = readPositiveDouble(
                "Cruise speed at which above thrust was measured (Mach, e.g. 0.84): ");

        System.out.println("\n-- Fuel Efficiency --");
        System.out.println("  Note: TSFC typical range for turbofans is 1.5E-4 to 2.5E-4 (SI: N/N/s).");
        final double tsfc = readPositiveDouble(
                "TSFC – Thrust Specific Fuel Consumption (N/N/s, SI, e.g. 1.6E-4): ");

        System.out.println("\n-- Altitude Performance --");
        final double lapseFactor = readNonNegativeDouble(
                "Lapse-rate factor (e.g. 1.0 for turbofans, 0.96 for some turbofans): ");

        try {
            final EngineModel model = controller.registerEngineModel(
                    name, makerId, engineType,
                    thrustStandstill, thrustCruise, cruiseMach,
                    tsfc, lapseFactor, fuelType);

            System.out.println("\nSuccess! Registered engine model:");
            System.out.println("  " + model);

        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("\nERROR: " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("\nACCESS DENIED: " + e.getMessage());
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private String readEngineType() {
        final EngineType[] types = EngineType.values();
        System.out.println("\nEngine Type:");
        for (int i = 0; i < types.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, types[i]);
        }
        while (true) {
            System.out.print("Select (1-" + types.length + "): ");
            try {
                final int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= types.length)
                    return types[choice - 1].name();
            } catch (NumberFormatException ignored) {}
            System.out.println("  Invalid choice. Please try again.");
        }
    }

    private FuelType readFuelType() {
        final FuelType[] fuels = controller.allFuelTypes();
        System.out.println("\nFuel Type:");
        for (int i = 0; i < fuels.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, fuels[i]);
        }
        while (true) {
            System.out.print("Select (1-" + fuels.length + "): ");
            try {
                final int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= fuels.length)
                    return fuels[choice - 1];
            } catch (NumberFormatException ignored) {}
            System.out.println("  Invalid choice. Please try again.");
        }
    }

    private double readPositiveDouble(final String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                final double value = Double.parseDouble(scanner.nextLine().trim());
                if (value > 0) return value;
                System.out.println("  Value must be positive. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number. Please enter a numeric value (e.g. 489000 or 1.6E-4).");
            }
        }
    }

    private double readNonNegativeDouble(final String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                final double value = Double.parseDouble(scanner.nextLine().trim());
                if (value >= 0) return value;
                System.out.println("  Value must be zero or positive. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number. Please enter a numeric value (e.g. 1.0).");
            }
        }
    }

    private double readDoubleLessThan(final String prompt, final double max,
                                      final String errorFormat) {
        while (true) {
            System.out.print(prompt);
            try {
                final double value = Double.parseDouble(scanner.nextLine().trim());
                if (value > 0 && value < max) return value;
                if (value <= 0)
                    System.out.println("  Value must be positive. Please try again.");
                else
                    System.out.printf("  " + errorFormat + "%n", max);
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number. Please enter a numeric value.");
            }
        }
    }
}