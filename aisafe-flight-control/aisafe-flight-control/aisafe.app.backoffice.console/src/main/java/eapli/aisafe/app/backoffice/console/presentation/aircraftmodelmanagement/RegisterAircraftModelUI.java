package eapli.aisafe.app.backoffice.console.presentation.aircraftmodelmanagement;

import eapli.aisafe.aircraftmodelmanagement.application.RegisterAircraftModelController;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftType;
import eapli.aisafe.enginemanagement.domain.EngineModel;
import eapli.aisafe.enginemanagement.domain.EngineModelId;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RegisterAircraftModelUI {

    private final RegisterAircraftModelController controller;
    private final Scanner scanner;

    public RegisterAircraftModelUI(final RegisterAircraftModelController controller,
                                   final Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    public void show() {
        System.out.println("\n=== Register Aircraft Model ===");

        // ── Group 1: Identification ───────────────────────────────────────────
        String modelId, makerName, aircraftType;
        while (true) {
            System.out.println("\n-- Identification --");
            System.out.print("Model ID (e.g. 777-200ER): ");
            modelId = scanner.nextLine().trim();
            System.out.print("Maker Name (e.g. Boeing): ");
            makerName = scanner.nextLine().trim();
            aircraftType = readAircraftType();

            System.out.println("\n  Summary:");
            System.out.printf("    Model ID     : %s%n", modelId);
            System.out.printf("    Maker        : %s%n", makerName);
            System.out.printf("    Aircraft Type: %s%n", aircraftType);
            if (confirm()) break;
        }

        // ── Group 2: Weights ──────────────────────────────────────────────────
        double emptyWeight, mtow, mzfw, maxFuelCapacity;
        while (true) {
            System.out.println("\n-- Weights --");
            emptyWeight     = readPositiveDouble("Empty Weight (kg): ");
            mtow            = readDoubleGreaterThan("MTOW – Max Take-Off Weight (kg): ",
                    emptyWeight, "MTOW must be greater than empty weight (%.0f kg).");
            mzfw            = readDoubleBetween("MZFW – Max Zero Fuel Weight (kg): ",
                    emptyWeight, mtow,
                    "MZFW must be between empty weight (%.0f kg) and MTOW (%.0f kg).");
            maxFuelCapacity = readPositiveDouble("Max Fuel Capacity (kg): ");

            System.out.println("\n  Summary:");
            System.out.printf("    Empty Weight : %.0f kg%n", emptyWeight);
            System.out.printf("    MTOW         : %.0f kg%n", mtow);
            System.out.printf("    MZFW         : %.0f kg%n", mzfw);
            System.out.printf("    Max Fuel     : %.0f kg%n", maxFuelCapacity);
            if (confirm()) break;
        }

        // ── Group 3: Performance ──────────────────────────────────────────────
        double serviceCeiling, cruiseSpeedMach, maxRange;
        while (true) {
            System.out.println("\n-- Performance --");
            serviceCeiling  = readPositiveDouble("Service Ceiling (ft): ");
            cruiseSpeedMach = readPositiveDouble("Cruise Speed (Mach, e.g. 0.84): ");
            maxRange        = readPositiveDouble("Max Range (km): ");

            System.out.println("\n  Summary:");
            System.out.printf("    Service Ceiling : %.0f ft%n", serviceCeiling);
            System.out.printf("    Cruise Speed    : %.2f Mach%n", cruiseSpeedMach);
            System.out.printf("    Max Range       : %.0f km%n", maxRange);
            if (confirm()) break;
        }

        // ── Group 4: Aerodynamics ─────────────────────────────────────────────
        double wingArea, wingSpan, aspectRatio, oswaldFactor, dragCoeff, liftCoeff;
        while (true) {
            System.out.println("\n-- Aerodynamics --");
            wingArea     = readPositiveDouble("Wing Area (m²): ");
            wingSpan     = readPositiveDouble("Wing Span (m): ");
            aspectRatio  = readPositiveDouble("Aspect Ratio (e.g. 9.0): ");
            oswaldFactor = readOswaldFactor();
            dragCoeff    = readPositiveDouble("Drag Coefficient Cd0 (e.g. 0.017): ");
            liftCoeff    = readPositiveDouble("Lift Coefficient Cl (e.g. 0.5): ");

            System.out.println("\n  Summary:");
            System.out.printf("    Wing Area    : %.1f m²%n", wingArea);
            System.out.printf("    Wing Span    : %.2f m%n", wingSpan);
            System.out.printf("    Aspect Ratio : %.2f%n", aspectRatio);
            System.out.printf("    Oswald Factor: %.2f%n", oswaldFactor);
            System.out.printf("    Cd0          : %.4f%n", dragCoeff);
            System.out.printf("    Cl           : %.3f%n", liftCoeff);
            if (confirm()) break;
        }

        // ── Group 5: Configuration ────────────────────────────────────────────
        int numberOfEngines, maxPassengerCapacity;
        while (true) {
            System.out.println("\n-- Configuration --");
            numberOfEngines      = readPositiveInt("Number of Engines: ");
            maxPassengerCapacity = readNonNegativeInt("Max Passenger Capacity: ");

            System.out.println("\n  Summary:");
            System.out.printf("    Engines  : %d%n", numberOfEngines);
            System.out.printf("    Pax Cap. : %d%n", maxPassengerCapacity);
            if (confirm()) break;
        }

        // ── Group 6: Certified Engines ────────────────────────────────────────
        List<EngineModelId> engineIds;
        while (true) {
            engineIds = selectEngines();
            if (engineIds.isEmpty()) {
                System.out.println("  At least one engine model must be selected.");
                continue;
            }
            System.out.println("\n  Summary:");
            System.out.println("    Certified engines:");
            for (final EngineModelId id : engineIds) {
                System.out.printf("      - %s%n", id);
            }
            if (confirm()) break;
        }

        // ── Submit ────────────────────────────────────────────────────────────
        try {
            final AircraftModel model = controller.registerAircraftModel(
                    modelId, makerName, aircraftType,
                    emptyWeight, mtow, mzfw, maxFuelCapacity,
                    serviceCeiling, cruiseSpeedMach,
                    wingArea, wingSpan, aspectRatio, oswaldFactor,
                    dragCoeff, liftCoeff,
                    numberOfEngines, maxPassengerCapacity, maxRange,
                    engineIds);

            System.out.println("\nSuccess! Registered aircraft model:");
            System.out.println("  " + model);

        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("\nERROR: " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("\nACCESS DENIED: " + e.getMessage());
        }
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private String readAircraftType() {
        final AircraftType[] types = AircraftType.values();
        System.out.println("Aircraft Type:");
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

    private List<EngineModelId> selectEngines() {
        final List<EngineModel> available = new ArrayList<>();
        controller.allEngineModels().forEach(available::add);

        if (available.isEmpty()) {
            System.out.println("\n  No engine models available. Register engine models first.");
            return new ArrayList<>();
        }

        System.out.println("\n-- Certified Engine Models --");
        System.out.println("  Available engines:");
        for (int i = 0; i < available.size(); i++) {
            final EngineModel e = available.get(i);
            System.out.printf("    %d. %s (%s) [%s]%n",
                    i + 1, e.name(), e.makerId(), e.engineType());
        }
        System.out.println("  Enter engine numbers to certify (comma-separated, e.g. 1,2):");
        System.out.print("  > ");
        final String input = scanner.nextLine().trim();

        final List<EngineModelId> selected = new ArrayList<>();
        for (final String part : input.split(",")) {
            try {
                final int idx = Integer.parseInt(part.trim()) - 1;
                if (idx >= 0 && idx < available.size()) {
                    final EngineModelId id = available.get(idx).identity();
                    if (!selected.contains(id)) selected.add(id);
                } else {
                    System.out.printf("  Skipping invalid index: %d%n", idx + 1);
                }
            } catch (NumberFormatException ignored) {
                System.out.printf("  Skipping invalid input: '%s'%n", part.trim());
            }
        }
        return selected;
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

    private double readPositiveDouble(final String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                final double value = Double.parseDouble(scanner.nextLine().trim());
                if (value > 0) return value;
                System.out.println("  Value must be positive. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number. Please enter a numeric value.");
            }
        }
    }

    private double readDoubleGreaterThan(final String prompt, final double min,
                                         final String errorFormat) {
        while (true) {
            System.out.print(prompt);
            try {
                final double value = Double.parseDouble(scanner.nextLine().trim());
                if (value > min) return value;
                System.out.printf("  " + errorFormat + "%n", min);
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number. Please enter a numeric value.");
            }
        }
    }

    private double readDoubleBetween(final String prompt, final double min, final double max,
                                     final String errorFormat) {
        while (true) {
            System.out.print(prompt);
            try {
                final double value = Double.parseDouble(scanner.nextLine().trim());
                if (value > min && value <= max) return value;
                System.out.printf("  " + errorFormat + "%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number. Please enter a numeric value.");
            }
        }
    }

    private double readOswaldFactor() {
        while (true) {
            System.out.print("Oswald Efficiency Factor e (0 < e ≤ 1, e.g. 0.87): ");
            try {
                final double value = Double.parseDouble(scanner.nextLine().trim());
                if (value > 0 && value <= 1.0) return value;
                System.out.println("  Value must be between 0 (exclusive) and 1 (inclusive).");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number. Please enter a numeric value.");
            }
        }
    }

    private int readPositiveInt(final String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                final int value = Integer.parseInt(scanner.nextLine().trim());
                if (value > 0) return value;
                System.out.println("  Value must be positive. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number. Please enter a whole number.");
            }
        }
    }

    private int readNonNegativeInt(final String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                final int value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= 0) return value;
                System.out.println("  Value must be zero or positive. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number. Please enter a whole number.");
            }
        }
    }
}