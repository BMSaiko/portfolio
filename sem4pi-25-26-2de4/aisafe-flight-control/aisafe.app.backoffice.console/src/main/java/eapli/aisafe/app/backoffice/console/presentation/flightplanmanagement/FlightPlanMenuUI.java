package eapli.aisafe.app.backoffice.console.presentation.flightplanmanagement;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.flightplanmanagement.application.CreateFlightPlanController;
import eapli.aisafe.flightplanmanagement.application.FlightPlanService;
import eapli.aisafe.flightplanmanagement.application.InsertWeatherDataInFlightPlanController;
import eapli.aisafe.flightplanmanagement.application.ValidateFlightPlanController;  // US085

/**
 * Console submenu for flight plan management operations.
 *
 * <p>
 * This menu groups flight plan related operations available in the backoffice
 * console application, including manual flight plan creation, DSL import,
 * listing, detail consultation, import examples, weather data insertion and
 * flight plan validation.
 * </p>
 *
 * <p>
 * Each option delegates to a dedicated UI class or service. Business rules and
 * authorization checks are handled by the corresponding application
 * controllers.
 * </p>
 */
public class FlightPlanMenuUI extends AbstractUI {

    /**
     * Service used for DSL import, listing and flight plan detail lookup.
     */
    private final FlightPlanService flightPlanService;

    /**
     * Controller used to create flight plans manually.
     */
    private final CreateFlightPlanController createFlightPlanController;

    /**
     * Controller used to insert weather data into a pilot's flight plan.
     */
    private final InsertWeatherDataInFlightPlanController insertWeatherDataController;

    /**
     * Controller used to validate flight plans (US085).
     */
    private final ValidateFlightPlanController validateFlightPlanController;  // US085

    /**
     * Creates the flight plan management menu.
     *
     * @param flightPlanService service for flight plan import/list/detail
     * operations
     * @param createFlightPlanController controller for manual flight plan
     * creation
     * @param insertWeatherDataController controller for weather data insertion
     * into flight plans
     * @param validateFlightPlanController controller for flight plan validation
     * (US085)
     */
    public FlightPlanMenuUI(final FlightPlanService flightPlanService,
                            final CreateFlightPlanController createFlightPlanController,
                            final InsertWeatherDataInFlightPlanController insertWeatherDataController,
                            final ValidateFlightPlanController validateFlightPlanController) {
        this.flightPlanService             = flightPlanService;
        this.createFlightPlanController    = createFlightPlanController;
        this.insertWeatherDataController   = insertWeatherDataController;
        this.validateFlightPlanController  = validateFlightPlanController;  // US085
    }

    /**
     * Displays the flight plan management menu and dispatches the selected
     * option.
     *
     * <p>
     * The method keeps showing the submenu until the user selects the back
     * option.
     * </p>
     *
     * @return {@code false} when the user chooses to return to the previous
     * menu
     */
    @Override
    protected boolean doShow() {
        while (true) {
            System.out.println("\n=== Flight Plan Management ===");
            System.out.println("1. Create Flight Plan");
            System.out.println("2. Import Flight Plan from DSL");
            System.out.println("3. List Flight Plans");
            System.out.println("4. Show Flight Plan Details");
            System.out.println("5. Show Import Examples");
            System.out.println("6. Insert Weather Data In Flight Plan");
            System.out.println("7. Validate Flight Plan (US085)");
            System.out.println("0. Back");
            System.out.print("\nOption: ");

            final String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    new CreateFlightPlanUI(createFlightPlanController).show();
                    break;
                case "2":
                    new ImportFlightPlanUI(flightPlanService).show();
                    break;
                case "3":
                    new ListFlightPlansUI(flightPlanService).show();
                    break;
                case "4":
                    new ShowFlightPlanDetailsUI(flightPlanService).show();
                    break;
                case "5":
                    System.out.println(FlightPlanImportExamples.availableExamples());
                    System.out.println(FlightPlanImportExamples.availableAircraftModels());
                    System.out.println(FlightPlanImportExamples.exampleDsl());
                    break;
                case "6":
                    new InsertWeatherDataInFlightPlanUI(insertWeatherDataController).show();
                    break;
                case "7":
                    new ValidateFlightPlanUI(validateFlightPlanController).show();  // US085
                    break;
                case "0":
                    return false;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }
}