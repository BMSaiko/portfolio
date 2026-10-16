package eapli.aisafe.app.backoffice.console.presentation.weather;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;
import eapli.aisafe.weather.application.RegisterWeatherDataController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class RegisterWeatherDataUI extends AbstractUI {

    private final RegisterWeatherDataController controller;
    private final AirControlAreaRepository areaRepo;

    public RegisterWeatherDataUI(RegisterWeatherDataController controller,
                                 AirControlAreaRepository areaRepo) {
        this.controller = controller;
        this.areaRepo = areaRepo;
    }

    @Override
    protected boolean doShow() {
        Scanner sc = scanner;
        List<AirControlArea> areas = new ArrayList<>();
        areaRepo.findAll().forEach(areas::add);

        if (areas.isEmpty()) {
            System.out.println("No air control areas available. Please create one first (US050).");
            return true;
        }

        System.out.println("Select air control area:");
        for (int i = 0; i < areas.size(); i++) {
            System.out.println((i + 1) + ". " + areas.get(i).name() + " (" + areas.get(i).identity().value() + ")");
        }
        int opt;
        while (true) {
            System.out.print("\nOption: ");
            try {
                opt = Integer.parseInt(sc.nextLine());
                if (opt >= 1 && opt <= areas.size()) break;
                System.out.println("Invalid option.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
        AirControlArea selectedArea = areas.get(opt - 1);

        System.out.print("\nStart timestamp (yyyy-MM-dd HH:mm): ");
        String startStr = sc.nextLine();
        LocalDateTime start;
        try {
            start = LocalDateTime.parse(startStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date/time format. Use yyyy-MM-dd HH:mm.");
            return true;
        }

        System.out.print("End timestamp (yyyy-MM-dd HH:mm): ");
        String endStr = sc.nextLine();
        LocalDateTime end;
        try {
            end = LocalDateTime.parse(endStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date/time format. Use yyyy-MM-dd HH:mm.");
            return true;
        }

        double temperature, windSpeed, windDirection, humidity, pressure;

        try {
            System.out.print("Temperature (ºC): ");
            temperature = Double.parseDouble(sc.nextLine());
            System.out.print("Wind speed (m/s): ");
            windSpeed = Double.parseDouble(sc.nextLine());
            System.out.print("Wind direction (degrees, 0-360): ");
            windDirection = Double.parseDouble(sc.nextLine());
            System.out.print("Humidity (%): ");
            humidity = Double.parseDouble(sc.nextLine());
            System.out.print("Pressure (hPa): ");
            pressure = Double.parseDouble(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format.");
            return true;
        }

        try {
            controller.register(selectedArea.identity().value(), start, end,
                    temperature, windSpeed, windDirection, humidity, pressure);
            System.out.println("Weather data registered successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return true;
    }
}
