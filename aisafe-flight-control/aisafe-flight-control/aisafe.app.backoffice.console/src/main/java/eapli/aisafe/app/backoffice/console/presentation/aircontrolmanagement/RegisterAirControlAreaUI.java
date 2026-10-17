package eapli.aisafe.app.backoffice.console.presentation.aircontrolmanagement;

import eapli.aisafe.aircontrolmanagement.application.RegisterAirControlAreaController;
import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;

import java.util.Scanner;

public class RegisterAirControlAreaUI {

    private final RegisterAirControlAreaController controller;
    private final Scanner scanner;

    public RegisterAirControlAreaUI(RegisterAirControlAreaController controller,
                                    Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    public void show() {
        System.out.println("\n=== Register Air Control Area ===");

        final String code = readCode(
                "Area Code (2-10 uppercase alphanumeric, e.g. LPPC): ",
                "[A-Z0-9_\\-]{2,10}",
                "Code must be 2-10 uppercase alphanumeric characters.");

        final String name = readNonEmpty("Name: ");

        final double minFuel = readPositiveDouble("Minimum Fuel Required (kg): ");

        System.out.println("\n-- Geographic Boundaries --");
        System.out.println("  Latitudes: -90 to 90 | Longitudes: -180 to 180");
        System.out.println("  North must be greater than South.");
        System.out.println("  East must be greater than West.\n");

        double north, south, east, west;

        north = readDoubleInRange("North Latitude  (e.g.  47.0): ", -90, 90);
        south = readDoubleInRange("South Latitude  (e.g.  36.0): ", -90, north - 0.0001,
                "South must be less than North (" + north + ").");
        east  = readDoubleInRange("East  Longitude (e.g.  -6.0): ", -180, 180);
        west  = readDoubleInRange("West  Longitude (e.g. -10.0): ", -180, east - 0.0001,
                "West must be less than East (" + east + ").");

        try {
            final AirControlArea area = controller.registerAirControlArea(
                    code, name, minFuel, north, south, east, west);
            System.out.println("\nSuccess! Registered: " + area);
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("\nERROR: " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("\nACCESS DENIED: " + e.getMessage());
        }
    }

    // -------------------------------------------------------
    // Helpers
    // -------------------------------------------------------

    private String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim();
            if (!value.isBlank()) return value;
            System.out.println("  Value must not be empty. Please try again.");
        }
    }

    private String readCode(String prompt, String regex, String errorMsg) {
        while (true) {
            System.out.print(prompt);
            String value = scanner.nextLine().trim().toUpperCase();
            if (value.matches(regex)) return value;
            System.out.println("  " + errorMsg);
        }
    }

    private double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value > 0) return value;
                System.out.println("  Value must be positive. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number. Please try again.");
            }
        }
    }

    private double readDoubleInRange(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value >= min && value <= max) return value;
                System.out.println("  Value must be between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number. Please try again.");
            }
        }
    }

    private double readDoubleInRange(String prompt, double min, double max, String errorMsg) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value >= min && value <= max) return value;
                System.out.println("  " + errorMsg);
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number. Please try again.");
            }
        }
    }
}