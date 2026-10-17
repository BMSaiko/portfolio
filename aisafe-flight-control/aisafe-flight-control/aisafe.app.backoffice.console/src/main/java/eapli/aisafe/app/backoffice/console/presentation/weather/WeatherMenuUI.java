package eapli.aisafe.app.backoffice.console.presentation.weather;

import eapli.aisafe.app.common.console.AbstractUI;

/**
 * Console submenu for weather management operations.
 *
 * <p>
 * This menu groups the weather-related user interfaces available in the
 * backoffice console application. It allows the user to register individual
 * weather data records, import weather data in bulk and consult existing
 * weather data.
 * </p>
 *
 * <p>
 * Each menu option delegates the execution of the selected operation to a
 * dedicated UI class. Authorization and business rules are enforced by the
 * corresponding application controllers.
 * </p>
 */
public class WeatherMenuUI extends AbstractUI {

    /**
     * UI responsible for registering a single weather data record.
     */
    private final RegisterWeatherDataUI registerWeatherDataUI;

    /**
     * UI responsible for importing weather data in bulk.
     */
    private final ImportWeatherDataUI importWeatherDataUI;

    /**
     * UI responsible for consulting weather data.
     */
    private final ConsultWeatherDataUI consultWeatherDataUI;

    /**
     * Creates the weather management menu.
     *
     * @param registerWeatherDataUI UI for registering weather data
     * @param importWeatherDataUI UI for importing bulk weather data
     * @param consultWeatherDataUI UI for consulting weather data
     */
    public WeatherMenuUI(RegisterWeatherDataUI registerWeatherDataUI,
                         ImportWeatherDataUI importWeatherDataUI,
                         ConsultWeatherDataUI consultWeatherDataUI) {
        this.registerWeatherDataUI = registerWeatherDataUI;
        this.importWeatherDataUI = importWeatherDataUI;
        this.consultWeatherDataUI = consultWeatherDataUI;
    }

    /**
     * Displays the weather management menu and dispatches the selected option.
     *
     * <p>
     * The method keeps showing the submenu until the user selects the back
     * option. Each valid option invokes the corresponding weather UI.
     * </p>
     *
     * @return {@code true} when the user returns to the previous menu
     */
    @Override
    protected boolean doShow() {
        while (true) {
            System.out.println();
            System.out.println("Weather Management");
            System.out.println("1. Register weather data");
            System.out.println("2. Import bulk weather data");
            System.out.println("3. Consult weather data");
            System.out.println("0. Back");
            System.out.print("\nOption: ");

            String option = scanner.nextLine();

            switch (option) {
                case "1":
                    registerWeatherDataUI.show();
                    break;
                case "2":
                    importWeatherDataUI.show();
                    break;
                case "3":
                    consultWeatherDataUI.show();
                    break;
                case "0":
                    return true;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}