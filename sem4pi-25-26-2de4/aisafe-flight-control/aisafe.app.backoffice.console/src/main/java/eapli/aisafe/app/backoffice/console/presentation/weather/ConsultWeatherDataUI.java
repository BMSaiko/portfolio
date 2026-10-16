package eapli.aisafe.app.backoffice.console.presentation.weather;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;
import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.weather.application.ConsultWeatherDataController;
import eapli.aisafe.weather.domain.WeatherData;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Console user interface for consulting weather data by air control area and
 * date.
 *
 * <p>
 * This UI supports the weather consultation use case by listing the available
 * air control areas, asking the user to select one of them and requesting a
 * date. The actual authorization and query logic are delegated to
 * {@link ConsultWeatherDataController}.
 * </p>
 *
 * <p>
 * The UI is responsible only for console interaction and result formatting. It
 * does not apply business rules directly.
 * </p>
 */
public class ConsultWeatherDataUI extends AbstractUI {

    /**
     * Application controller used to consult weather data.
     */
    private final ConsultWeatherDataController controller;

    /**
     * Repository used to list air control areas for user selection.
     */
    private final AirControlAreaRepository areaRepo;

    /**
     * Creates the consult weather data UI.
     *
     * @param controller controller responsible for weather consultation
     * @param areaRepo repository used to retrieve available air control areas
     */
    public ConsultWeatherDataUI(ConsultWeatherDataController controller,
                                AirControlAreaRepository areaRepo) {
        this.controller = controller;
        this.areaRepo = areaRepo;
    }

    /**
     * Displays the consultation form, executes the query and prints the matching
     * weather data records.
     *
     * <p>
     * The method first lists all air control areas. After the user selects one
     * area and provides a date in {@code yyyy-MM-dd} format, the query is
     * delegated to the controller and the results are printed in a compact
     * readable format.
     * </p>
     *
     * @return {@code true} to keep the parent menu active
     */
    @Override
    protected boolean doShow() {
        List<AirControlArea> areas = new ArrayList<>();
        areaRepo.findAll().forEach(areas::add);

        if (areas.isEmpty()) {
            System.out.println("No air control areas available.");
            return true;
        }

        System.out.println("Select air control area:");
        for (int i = 0; i < areas.size(); i++) {
            AirControlArea area = areas.get(i);
            System.out.println((i + 1) + ". " + area.name() + " (" + area.identity().value() + ")");
        }

        System.out.print("\nOption: ");
        int option;

        try {
            option = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid option.");
            return true;
        }

        if (option < 1 || option > areas.size()) {
            System.out.println("Invalid option.");
            return true;
        }

        AirControlArea selectedArea = areas.get(option - 1);

        System.out.print("Date (yyyy-MM-dd): ");
        LocalDate date;

        try {
            date = LocalDate.parse(scanner.nextLine());
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format.");
            return true;
        }

        try {
            Iterable<WeatherData> results =
                    controller.consult(selectedArea.identity().value(), date);

            boolean found = false;

            System.out.println();
            System.out.println("Weather data for " + selectedArea.identity().value() + " on " + date + ":");

            for (WeatherData data : results) {
                found = true;
                System.out.printf(
                        "%s | Temp: %.2f C | Wind: %.2f m/s %.2f deg | Humidity: %.2f %% | Pressure: %.2f hPa%n",
                        data.period(),
                        data.temperature().value(),
                        data.windSpeed().value(),
                        data.windDirection().value(),
                        data.humidity().value(),
                        data.pressure().value()
                );
            }

            if (!found) {
                System.out.println("No weather data found.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        return true;
    }
}