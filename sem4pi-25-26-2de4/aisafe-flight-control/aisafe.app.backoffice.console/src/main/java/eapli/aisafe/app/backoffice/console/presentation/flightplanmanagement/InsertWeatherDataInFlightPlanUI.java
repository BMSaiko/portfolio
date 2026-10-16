package eapli.aisafe.app.backoffice.console.presentation.flightplanmanagement;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.flightplanmanagement.application.InsertWeatherDataInFlightPlanController;
import eapli.aisafe.flightplanmanagement.domain.FlightPlan;
import eapli.aisafe.weather.domain.WeatherData;
import eapli.framework.io.util.Console;

/**
 * Console user interface for inserting weather data into a pilot's flight plan.
 *
 * <p>
 * This UI supports US082 by allowing an authenticated pilot to select one of
 * their flight plans and associate it with an existing weather data record.
 * </p>
 *
 * <p>
 * The UI is responsible for displaying selectable flight plans, displaying
 * available weather data records, collecting user input and presenting the
 * result. Authorization, ownership checks and business rules are handled by
 * {@link InsertWeatherDataInFlightPlanController}.
 * </p>
 */
public class InsertWeatherDataInFlightPlanUI extends AbstractUI {

    /**
     * Application controller used to execute the weather data insertion use
     * case.
     */
    private final InsertWeatherDataInFlightPlanController controller;

    /**
     * Creates the insert weather data UI.
     *
     * @param controller controller responsible for inserting weather data into
     *                   flight plans
     */
    public InsertWeatherDataInFlightPlanUI(final InsertWeatherDataInFlightPlanController controller) {
        this.controller = controller;
    }

    /**
     * Displays the insertion form, executes the operation and prints the result.
     *
     * <p>
     * The method first lists the authenticated pilot's flight plans, then lists
     * all available weather data records. The user provides a flight plan ID and
     * a weather data ID. The controller then performs the insertion and returns
     * the updated flight plan.
     * </p>
     *
     * @return {@code false} to return to the flight plan management menu after
     * one execution
     */
    @Override
    protected boolean doShow() {
        System.out.println("\n=== Insert Weather Data In Flight Plan ===");

        try {
            System.out.println("\nYour flight plans:");
            for (FlightPlan plan : controller.myFlightPlans()) {
                System.out.println("  " + plan.identity() + " | " +
                        plan.departureAirport() + " -> " + plan.arrivalAirport() +
                        " | Status: " + plan.status());
            }

            System.out.println("\nAvailable weather data:");
            for (WeatherData data : controller.availableWeatherData()) {
                System.out.println(formatWeatherData(data));
            }

            final String flightPlanId = Console.readLine("\nFlight Plan ID: ");
            final String weatherDataIdText = Console.readLine("Weather Data ID: ");
            final Long weatherDataId = Long.parseLong(weatherDataIdText.trim());

            final FlightPlan updated = controller.insertWeatherData(flightPlanId, weatherDataId);

            System.out.println("\nWeather data added successfully.");
            System.out.println("Flight plan status: " + updated.status());

            if (updated.weatherData().size() > 0) {
                System.out.println("Associated weather records: " + updated.weatherData().size());
            }
        } catch (NumberFormatException e) {
            System.out.println("\nERROR: Weather Data ID must be a number.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("\nERROR: " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("\nACCESS DENIED: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nUNEXPECTED ERROR: " + e.getMessage());
        }

        return false;
    }

    /**
     * Formats a weather data record for console display.
     *
     * @param data weather data record to format
     * @return formatted weather data description
     */
    private String formatWeatherData(final WeatherData data) {
        return String.format(
                "  ID %d | %s | %s to %s | Temp: %.2f C | Wind: %.2f m/s %.2f deg | Humidity: %.2f %% | Pressure: %.2f hPa",
                data.id(),
                data.area().identity(),
                data.period().start(),
                data.period().end(),
                data.temperature().value(),
                data.windSpeed().value(),
                data.windDirection().value(),
                data.humidity().value(),
                data.pressure().value()
        );
    }
}