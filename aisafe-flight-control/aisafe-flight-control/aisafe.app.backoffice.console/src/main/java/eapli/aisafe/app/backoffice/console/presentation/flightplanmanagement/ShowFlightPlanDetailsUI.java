package eapli.aisafe.app.backoffice.console.presentation.flightplanmanagement;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.flightplanmanagement.application.FlightPlanService;
import eapli.aisafe.flightplanmanagement.domain.FlightPlan;
import eapli.aisafe.flightplanmanagement.domain.FlightLeg;
import eapli.aisafe.flightplanmanagement.domain.FlightSegment;
import eapli.aisafe.weather.domain.WeatherData;
import eapli.framework.io.util.Console;

import java.util.Optional;

/**
 * Console user interface for displaying detailed information about a flight
 * plan.
 *
 * <p>
 * This UI asks the user for a flight plan identifier and delegates the lookup
 * to {@link FlightPlanService}. When the flight plan exists, the UI displays
 * its main data, associated weather records, legs and segments.
 * </p>
 *
 * <p>
 * The class is responsible only for console interaction and formatting.
 * Authorization and lookup rules are handled by the service layer.
 * </p>
 */
public class ShowFlightPlanDetailsUI extends AbstractUI {

    /**
     * Service used to find and retrieve flight plan details.
     */
    private final FlightPlanService flightPlanService;

    /**
     * Creates the flight plan details UI.
     *
     * @param flightPlanService service used to retrieve flight plans
     */
    public ShowFlightPlanDetailsUI(final FlightPlanService flightPlanService) {
        this.flightPlanService = flightPlanService;
    }

    /**
     * Displays the flight plan details screen.
     *
     * <p>
     * The user provides a flight plan ID. If the plan exists, this method prints
     * general plan information, weather data associated through US082, and all
     * legs and segments. If the plan does not exist, an appropriate message is
     * shown.
     * </p>
     *
     * @return {@code false} to return to the flight plan management menu after
     * one execution
     */
    @Override
    protected boolean doShow() {
        System.out.println("\n=== Flight Plan Details ===");

        try {
            final String id = Console.readLine("Flight Plan ID: ");
            final Optional<FlightPlan> result = flightPlanService.findFlightPlan(id);

            if (result.isPresent()) {
                final FlightPlan fp = result.get();

                System.out.println("\n" + fp);
                System.out.println("  Aircraft Model: " + fp.aircraftModel());
                System.out.println("  Departure: " + fp.departureAirport());
                System.out.println("  Arrival: " + fp.arrivalAirport());
                System.out.println("  Total Distance: " + fp.totalDistance() + " km");
                System.out.println("  Estimated Fuel: " + fp.estimatedFuel() + " kg");
                System.out.println("  Status: " + fp.status());

                if (fp.departureDateTime() != null) {
                    System.out.println("  Departure Date/Time: " + fp.departureDateTime());
                }

                System.out.println("\n  Weather Data (" + fp.weatherData().size() + "):");
                for (int i = 0; i < fp.weatherData().size(); i++) {
                    final WeatherData data = fp.weatherData().get(i);
                    System.out.printf(
                            "    %d. ID %d | %s | %s to %s | Temp: %.2f C | Wind: %.2f m/s %.2f deg | Humidity: %.2f %% | Pressure: %.2f hPa%n",
                            i + 1,
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

                System.out.println("\n  Legs (" + fp.legs().size() + "):");
                for (int i = 0; i < fp.legs().size(); i++) {
                    final FlightLeg leg = fp.legs().get(i);
                    System.out.println("    Leg " + (i + 1) + ": " + leg);

                    for (int j = 0; j < leg.segments().size(); j++) {
                        final FlightSegment seg = leg.segments().get(j);
                        System.out.println("      Segment " + (j + 1) + ": " + seg);
                    }
                }
            } else {
                System.out.println("Flight plan not found: " + id);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\nERROR: " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("\nACCESS DENIED: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nUNEXPECTED ERROR: " + e.getMessage());
        }

        return false;
    }
}