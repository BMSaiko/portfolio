package eapli.aisafe.app.backoffice.console;

import eapli.aisafe.aircraftmodelmanagement.application.AddEngineToAircraftModelController;
import eapli.aisafe.aircraftmodelmanagement.application.RegisterAircraftModelController;
import eapli.aisafe.aircraftmodelmanagement.application.RemoveEngineFromAircraftModelController;
import eapli.aisafe.aircraftmodelmanagement.repositories.AircraftModelRepository;
import eapli.aisafe.aircontrolmanagement.application.RegisterAirControlAreaController;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;
import eapli.aisafe.airportmanagement.application.RegisterAirportController;
import eapli.aisafe.airportmanagement.repositories.AirportRepository;
import eapli.aisafe.app.backoffice.console.presentation.MainMenu;
import eapli.aisafe.bootstrap.*;
import eapli.aisafe.enginemanagement.application.RegisterEngineModelController;
import eapli.aisafe.enginemanagement.repositories.EngineModelRepository;
import eapli.aisafe.flightplanmanagement.application.CreateFlightPlanController;
import eapli.aisafe.flightplanmanagement.application.FlightPlanService;
import eapli.aisafe.flightplanmanagement.application.InsertWeatherDataInFlightPlanController;
import eapli.aisafe.flightplanmanagement.application.ValidateFlightPlanController;  // US085
import eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository;
import eapli.aisafe.fleetmanagement.application.AddAircraftController;
import eapli.aisafe.fleetmanagement.application.DecommissionAircraftController;
import eapli.aisafe.fleetmanagement.application.ListFleetController;
import eapli.aisafe.fleetmanagement.repositories.AircraftRepository;
import eapli.aisafe.organization.application.CustomerCollaboratorController;
import eapli.aisafe.organization.application.RegisterAirTransportCompanyController;
import eapli.aisafe.organization.repositories.AirTransportCompanyRepository;
import eapli.aisafe.organization.repositories.CustomerCollaboratorRepository;
import eapli.aisafe.persistence.impl.jpa.*;
import eapli.aisafe.routemanagement.application.CreateFlightRouteController;
import eapli.aisafe.routemanagement.repositories.FlightRouteRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.application.ManageUserController;
import eapli.aisafe.usermanagement.application.RegisterUserController;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import eapli.aisafe.weather.application.AutoDetectWeatherDataImporter;
import eapli.aisafe.weather.application.ConsultWeatherDataController;
import eapli.aisafe.weather.application.CsvWeatherDataImporter;
import eapli.aisafe.weather.application.CwWindWeatherDataImporter;
import eapli.aisafe.weather.application.ImportWeatherDataController;
import eapli.aisafe.weather.application.RegisterWeatherDataController;
import eapli.aisafe.weather.repositories.WeatherDataRepository;
import eapli.aisafe.pilotmanagement.application.AddPilotController;
import eapli.aisafe.pilotmanagement.application.ListPilotsController;
import eapli.aisafe.pilotmanagement.repositories.PilotRepository;
import eapli.aisafe.pilotmanagement.application.RemovePilotController;
import eapli.aisafe.reportmanagement.application.MonthlyReportService;
import eapli.aisafe.reportmanagement.repositories.MonthlyStatisticsRepository;
import eapli.aisafe.app.backoffice.console.presentation.reportmanagement.GenerateMonthlyReportUI;
import eapli.aisafe.app.backoffice.console.presentation.reportmanagement.ReportsMenuUI;
import eapli.aisafe.persistence.impl.jpa.JpaMonthlyStatisticsRepository;

/**
 * Entry point for the AISafe backoffice console application.
 *
 * <p>
 * This class is responsible for wiring the backoffice application dependencies,
 * creating repository implementations, creating application controllers,
 * bootstrapping initial data and starting the main console menu.
 * </p>
 *
 * <p>
 * The backoffice console supports administrative, weather, aircraft, route,
 * pilot and flight plan management operations. It uses JPA repositories backed
 * by the configured persistence unit and delegates user interaction to
 * {@link MainMenu}.
 * </p>
 *
 * <p>
 * This class acts as the composition root of the console application. Business
 * logic should remain inside domain objects and application controllers rather
 * than being implemented here.
 * </p>
 */
public class AISafeBackoffice {

    /**
     * Name of the JPA persistence unit used by the backoffice application.
     */
    private static final String PERSISTENCE_UNIT = "aisafePU";

    /**
     * Starts the AISafe backoffice console application.
     *
     * <p>
     * The method performs the following steps:
     * </p>
     *
     * <ol>
     *     <li>Creates JPA repository instances;</li>
     *     <li>Creates the authentication service;</li>
     *     <li>Creates application controllers;</li>
     *     <li>Loads bootstrap data needed for demonstration and testing;</li>
     *     <li>Creates and shows the main menu.</li>
     * </ol>
     *
     * @param args command-line arguments; currently not used
     */
    public static void main(String[] args) {

        System.out.println("============================================================");
        System.out.println("  AISafe Flight Control - Backoffice");
        System.out.println("============================================================\n");

        // ── Repositories ──────────────────────────────────────────────────────

        UserRepository userRepo = new JpaUserRepository();
        AirTransportCompanyRepository companyRepo = new JpaAirTransportCompanyRepository(PERSISTENCE_UNIT);
        CustomerCollaboratorRepository collaboratorRepo = new JpaCustomerCollaboratorRepository(PERSISTENCE_UNIT);
        AirControlAreaRepository areaRepo = new JpaAirControlAreaRepository(PERSISTENCE_UNIT);
        WeatherDataRepository weatherRepo = new JpaWeatherDataRepository(PERSISTENCE_UNIT);
        AirportRepository airportRepo = new JpaAirportRepository(PERSISTENCE_UNIT);
        EngineModelRepository engineModelRepo = new JpaEngineModelRepository(PERSISTENCE_UNIT);
        AircraftModelRepository aircraftModelRepo = new JpaAircraftModelRepository(PERSISTENCE_UNIT);
        AircraftRepository aircraftRepo = new JpaAircraftRepository(PERSISTENCE_UNIT);
        FlightPlanRepository flightPlanRepo = new JpaFlightPlanRepository(PERSISTENCE_UNIT);
        FlightRouteRepository flightRouteRepo = new JpaFlightRouteRepository(PERSISTENCE_UNIT);
        PilotRepository pilotRepo = new JpaPilotRepository(PERSISTENCE_UNIT);
        MonthlyStatisticsRepository monthlyStatsRepo = new JpaMonthlyStatisticsRepository(PERSISTENCE_UNIT);

        // ── Auth ──────────────────────────────────────────────────────────────

        AuthService authService = new AuthService(userRepo);

        // ── Controllers ───────────────────────────────────────────────────────

        RegisterUserController registerUserController =
                new RegisterUserController(authService, userRepo);

        ManageUserController manageUserController =
                new ManageUserController(authService, userRepo);

        RegisterAirTransportCompanyController companyController =
                new RegisterAirTransportCompanyController(authService, companyRepo);

        CustomerCollaboratorController collaboratorController =
                new CustomerCollaboratorController(authService, userRepo, companyRepo, areaRepo, collaboratorRepo);

        RegisterWeatherDataController weatherController =
                new RegisterWeatherDataController(authService, areaRepo, weatherRepo);

        ImportWeatherDataController importWeatherDataController =
                new ImportWeatherDataController(
                        authService,
                        areaRepo,
                        weatherRepo,
                        new AutoDetectWeatherDataImporter(
                                new CsvWeatherDataImporter(),
                                new CwWindWeatherDataImporter()));

        ConsultWeatherDataController consultWeatherDataController =
                new ConsultWeatherDataController(authService, areaRepo, weatherRepo);

        RegisterAirControlAreaController airControlAreaController =
                new RegisterAirControlAreaController(authService, areaRepo);

        RegisterAirportController airportController =
                new RegisterAirportController(authService, airportRepo, areaRepo);

        RegisterEngineModelController engineModelController =
                new RegisterEngineModelController(authService, engineModelRepo);

        RegisterAircraftModelController aircraftModelController =
                new RegisterAircraftModelController(authService, aircraftModelRepo, engineModelRepo);

        AddEngineToAircraftModelController addEngineToAircraftModelController =
                new AddEngineToAircraftModelController(authService, aircraftModelRepo, engineModelRepo);

        RemoveEngineFromAircraftModelController removeEngineFromAircraftModelController =
                new RemoveEngineFromAircraftModelController(authService, aircraftModelRepo, engineModelRepo, aircraftRepo);

        AddAircraftController addAircraftController =
                new AddAircraftController(aircraftRepo, aircraftModelRepo, collaboratorRepo, companyRepo, authService);

        DecommissionAircraftController decommissionAircraftController =
                new DecommissionAircraftController(aircraftRepo, authService);

        ListFleetController listFleetController =
                new ListFleetController(aircraftRepo, collaboratorRepo, authService);

        CreateFlightRouteController createFlightRouteController =
                new CreateFlightRouteController(flightRouteRepo, airportRepo, collaboratorRepo, companyRepo, authService);

        FlightPlanService flightPlanService =
                new FlightPlanService(authService, flightPlanRepo, aircraftModelRepo);

        CreateFlightPlanController createFlightPlanController =
                new CreateFlightPlanController(authService, flightPlanRepo, flightRouteRepo, aircraftRepo, pilotRepo);

        InsertWeatherDataInFlightPlanController insertWeatherDataController =
                new InsertWeatherDataInFlightPlanController(authService, flightPlanRepo, weatherRepo);

        // US085 — only needs the FlightPlan repository
        ValidateFlightPlanController validateFlightPlanController =
                new ValidateFlightPlanController(flightPlanRepo);              // US085

        AddPilotController addPilotController =
                new AddPilotController(authService, pilotRepo, userRepo, aircraftModelRepo, collaboratorRepo, companyRepo);

        ListPilotsController listPilotsController =
                new ListPilotsController(authService, pilotRepo, collaboratorRepo, companyRepo);

        RemovePilotController removePilotController =
                new RemovePilotController(authService, pilotRepo, collaboratorRepo, companyRepo, flightPlanRepo);

        MonthlyReportService monthlyReportService =
                new MonthlyReportService(authService, flightPlanRepo, monthlyStatsRepo);

        GenerateMonthlyReportUI generateMonthlyReportUI =
                new GenerateMonthlyReportUI(monthlyReportService);

        ReportsMenuUI reportsMenuUI = new ReportsMenuUI(generateMonthlyReportUI);

        // ── Bootstrap ─────────────────────────────────────────────────────────

        new MasterUsersBootstrapper(userRepo, companyRepo, collaboratorRepo, areaRepo).bootstrap();
        new AirControlAreaBootstrap(areaRepo, airportRepo).bootstrap();
        new FlightRouteBootstrap(flightRouteRepo, airportRepo, companyRepo).bootstrap();
        new EngineModelBootstrap(engineModelRepo, aircraftModelRepo).load();
        new FleetBootstrap(aircraftRepo, aircraftModelRepo, companyRepo, pilotRepo, userRepo).bootstrap();
        new FlightPlanBootstrap(flightPlanRepo, flightRouteRepo, aircraftRepo, pilotRepo, aircraftModelRepo).bootstrap();

        // ── Main menu ─────────────────────────────────────────────────────────

        MainMenu mainMenu = new MainMenu(
                authService,
                registerUserController,
                companyController,
                collaboratorController,
                weatherController,
                importWeatherDataController,
                consultWeatherDataController,
                areaRepo,
                manageUserController,
                airControlAreaController,
                airportController,
                engineModelController,
                aircraftModelController,
                addEngineToAircraftModelController,
                removeEngineFromAircraftModelController,
                addAircraftController,
                decommissionAircraftController,
                listFleetController,
                createFlightRouteController,
                flightPlanService,
                createFlightPlanController,
                addPilotController,
                listPilotsController,
                insertWeatherDataController,
                reportsMenuUI,
                removePilotController,
                validateFlightPlanController);  // US085 — last parameter

        mainMenu.show();

        System.out.println("\nAISafe - Goodbye!");
    }
}