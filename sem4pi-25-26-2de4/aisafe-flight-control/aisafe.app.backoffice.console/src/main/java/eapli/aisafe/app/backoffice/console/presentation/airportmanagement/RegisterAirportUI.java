package eapli.aisafe.app.backoffice.console.presentation.airportmanagement;

import eapli.aisafe.airportmanagement.application.RegisterAirportController;
import eapli.aisafe.airportmanagement.domain.Airport;

import java.util.Scanner;

public class RegisterAirportUI {

    private final RegisterAirportController controller;
    private final Scanner scanner;

    public RegisterAirportUI(final RegisterAirportController controller,
                             final Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    public void show() {
        System.out.println("\n=== Register Airport ===");

        final String iata = readCode(
                "IATA Code (3 uppercase letters, e.g. LIS): ",
                "[A-Z]{3}",
                "IATA code must be exactly 3 uppercase letters.");

        final String icao = readCode(
                "ICAO Code (4 uppercase letters, e.g. LPPT): ",
                "[A-Z]{4}",
                "ICAO code must be exactly 4 uppercase letters.");

        final String name    = readNonEmpty("Name (e.g. Humberto Delgado Airport): ");
        final String town    = readNonEmpty("Town: ");
        final String country = readNonEmpty("Country: ");

        System.out.println("\n-- Coordinates --");
        System.out.println("  Latitude: -90 to 90 | Longitude: -180 to 180 | Altitude >= 0\n");

        final double latitude  = readDoubleInRange("Latitude  (e.g.  38.7742): ", -90, 90);
        final double longitude = readDoubleInRange("Longitude (e.g.  -9.1342): ", -180, 180);
        final double altitude  = readDoubleMin("Altitude in metres (e.g. 113.0): ", 0);

        final String areaCode = readCode(
                "Air Control Area Code (2-10 uppercase alphanumeric, e.g. LPPC): ",
                "[A-Z0-9_\\-]{2,10}",
                "Area code must be 2-10 uppercase alphanumeric characters.");

        try {
            final Airport airport = controller.registerAirport(
                    iata, icao, name, town, country,
                    latitude, longitude, altitude, areaCode);
            System.out.println("\nSuccess! Registered: " + airport);
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

    private double readDoubleMin(String prompt, double min) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value >= min) return value;
                System.out.println("  Value must be >= " + min + ".");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid number. Please try again.");
            }
        }
    }
}