package eapli.aisafe.app.backoffice.console.presentation;

import eapli.aisafe.aircraftmodelmanagement.application.AddEngineToAircraftModelController;
import eapli.aisafe.aircraftmodelmanagement.application.RegisterAircraftModelController;
import eapli.aisafe.aircraftmodelmanagement.application.RemoveEngineFromAircraftModelController;
import eapli.aisafe.aircontrolmanagement.application.RegisterAirControlAreaController;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;
import eapli.aisafe.airportmanagement.application.RegisterAirportController;
import eapli.aisafe.app.backoffice.console.presentation.aircraftmodelmanagement.AircraftModelManagementMenuUI;
import eapli.aisafe.app.backoffice.console.presentation.aircontrolmanagement.RegisterAirControlAreaUI;
import eapli.aisafe.app.backoffice.console.presentation.airportmanagement.RegisterAirportUI;
import eapli.aisafe.app.backoffice.console.presentation.authz.LoginUI;
import eapli.aisafe.app.backoffice.console.presentation.authz.LogoutUI;
import eapli.aisafe.app.backoffice.console.presentation.authz.UsersMenuUI;
import eapli.aisafe.app.backoffice.console.presentation.enginemanagement.RegisterEngineModelUI;
import eapli.aisafe.app.backoffice.console.presentation.fleetmanagement.AddAircraftUI;
import eapli.aisafe.app.backoffice.console.presentation.fleetmanagement.DecommissionAircraftUI;
import eapli.aisafe.app.backoffice.console.presentation.fleetmanagement.ListFleetUI;
import eapli.aisafe.app.backoffice.console.presentation.flightplanmanagement.FlightPlanMenuUI;
import eapli.aisafe.app.backoffice.console.presentation.organization.OrganizationMenuUI;
import eapli.aisafe.app.backoffice.console.presentation.pilotmanagement.AddPilotUI;
import eapli.aisafe.app.backoffice.console.presentation.pilotmanagement.ListPilotsUI;
import eapli.aisafe.app.backoffice.console.presentation.reportmanagement.ReportsMenuUI;
import eapli.aisafe.app.backoffice.console.presentation.routemanagement.CreateFlightRouteUI;
import eapli.aisafe.app.backoffice.console.presentation.weather.ConsultWeatherDataUI;
import eapli.aisafe.app.backoffice.console.presentation.weather.ImportWeatherDataUI;
import eapli.aisafe.app.backoffice.console.presentation.weather.RegisterWeatherDataUI;
import eapli.aisafe.app.backoffice.console.presentation.weather.WeatherMenuUI;
import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.enginemanagement.application.RegisterEngineModelController;
import eapli.aisafe.fleetmanagement.application.AddAircraftController;
import eapli.aisafe.fleetmanagement.application.DecommissionAircraftController;
import eapli.aisafe.fleetmanagement.application.ListFleetController;
import eapli.aisafe.flightplanmanagement.application.CreateFlightPlanController;
import eapli.aisafe.flightplanmanagement.application.FlightPlanService;
import eapli.aisafe.flightplanmanagement.application.InsertWeatherDataInFlightPlanController;
import eapli.aisafe.flightplanmanagement.application.ValidateFlightPlanController;  // US085
import eapli.aisafe.organization.application.CustomerCollaboratorController;
import eapli.aisafe.organization.application.RegisterAirTransportCompanyController;
import eapli.aisafe.pilotmanagement.application.AddPilotController;
import eapli.aisafe.pilotmanagement.application.ListPilotsController;
import eapli.aisafe.pilotmanagement.application.RemovePilotController;
import eapli.aisafe.app.backoffice.console.presentation.pilotmanagement.RemovePilotUI;
import eapli.aisafe.routemanagement.application.CreateFlightRouteController;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.application.ManageUserController;
import eapli.aisafe.usermanagement.application.RegisterUserController;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.weather.application.ConsultWeatherDataController;
import eapli.aisafe.weather.application.ImportWeatherDataController;
import eapli.aisafe.weather.application.RegisterWeatherDataController;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main menu of the AISafe backoffice console application.
 *
 * <p>
 * This class is responsible for presenting the available backoffice operations
 * according to the authenticated user's roles. It acts as the main navigation
 * point after application startup and delegates each selected option to a
 * dedicated UI class.
 * </p>
 *
 * <p>
 * The menu supports both anonymous and authenticated states. Anonymous users
 * can only log in or exit. Authenticated users see a dynamic list of options
 * based on their roles.
 * </p>
 */
public class MainMenu extends AbstractUI {

    /**
     * Authentication service used to inspect the current session and user roles.
     */
    private final AuthService auth;
    private final RegisterUserController registerController;
    private final RegisterAirTransportCompanyController companyController;
    private final CustomerCollaboratorController collaboratorController;
    private final RegisterWeatherDataController weatherController;
    private final ImportWeatherDataController importWeatherDataController;
    private final ConsultWeatherDataController consultWeatherDataController;
    private final AirControlAreaRepository areaRepo;
    private final ManageUserController manageUserController;
    private final RegisterAirControlAreaController airControlAreaController;
    private final RegisterAirportController airportController;
    private final RegisterEngineModelController engineModelController;
    private final RegisterAircraftModelController aircraftModelController;
    private final AddEngineToAircraftModelController addEngineToAircraftModelController;
    private final RemoveEngineFromAircraftModelController removeEngineFromAircraftModelController;
    private final AddAircraftController addAircraftController;
    private final DecommissionAircraftController decommissionAircraftController;
    private final ListFleetController listFleetController;
    private final CreateFlightRouteController createFlightRouteController;
    private final FlightPlanService flightPlanService;
    private final CreateFlightPlanController createFlightPlanController;
    private final AddPilotController addPilotController;
    private final ListPilotsController listPilotsController;
    private final RemovePilotController removePilotController;
    private final ReportsMenuUI reportsMenuUI;
    private final InsertWeatherDataInFlightPlanController insertWeatherDataController;
    private final ValidateFlightPlanController validateFlightPlanController;  // US085

    /**
     * Creates the main backoffice menu with all controllers and services needed
     * by the available UI options.
     */
    public MainMenu(AuthService auth,
                    RegisterUserController registerController,
                    RegisterAirTransportCompanyController companyController,
                    CustomerCollaboratorController collaboratorController,
                    RegisterWeatherDataController weatherController,
                    ImportWeatherDataController importWeatherDataController,
                    ConsultWeatherDataController consultWeatherDataController,
                    AirControlAreaRepository areaRepo,
                    ManageUserController manageUserController,
                    RegisterAirControlAreaController airControlAreaController,
                    RegisterAirportController airportController,
                    RegisterEngineModelController engineModelController,
                    RegisterAircraftModelController aircraftModelController,
                    AddEngineToAircraftModelController addEngineToAircraftModelController,
                    RemoveEngineFromAircraftModelController removeEngineFromAircraftModelController,
                    AddAircraftController addAircraftController,
                    DecommissionAircraftController decommissionAircraftController,
                    ListFleetController listFleetController,
                    CreateFlightRouteController createFlightRouteController,
                    FlightPlanService flightPlanService,
                    CreateFlightPlanController createFlightPlanController,
                    AddPilotController addPilotController,
                    ListPilotsController listPilotsController,
                    InsertWeatherDataInFlightPlanController insertWeatherDataController,
                    ReportsMenuUI reportsMenuUI,
                    RemovePilotController removePilotController,
                    ValidateFlightPlanController validateFlightPlanController) {  // US085
        this.auth = auth;
        this.registerController = registerController;
        this.companyController = companyController;
        this.collaboratorController = collaboratorController;
        this.weatherController = weatherController;
        this.importWeatherDataController = importWeatherDataController;
        this.consultWeatherDataController = consultWeatherDataController;
        this.areaRepo = areaRepo;
        this.manageUserController = manageUserController;
        this.airControlAreaController = airControlAreaController;
        this.airportController = airportController;
        this.engineModelController = engineModelController;
        this.aircraftModelController = aircraftModelController;
        this.addEngineToAircraftModelController = addEngineToAircraftModelController;
        this.removeEngineFromAircraftModelController = removeEngineFromAircraftModelController;
        this.addAircraftController = addAircraftController;
        this.decommissionAircraftController = decommissionAircraftController;
        this.listFleetController = listFleetController;
        this.createFlightRouteController = createFlightRouteController;
        this.flightPlanService = flightPlanService;
        this.createFlightPlanController = createFlightPlanController;
        this.addPilotController = addPilotController;
        this.listPilotsController = listPilotsController;
        this.insertWeatherDataController = insertWeatherDataController;
        this.reportsMenuUI = reportsMenuUI;
        this.removePilotController = removePilotController;
        this.validateFlightPlanController = validateFlightPlanController;  // US085
    }

    @Override
    protected boolean doShow() {
        Scanner sc = scanner;

        while (true) {
            printHeader("AISafe Backoffice");

            if (!auth.currentUser().isPresent()) {
                System.out.println("1. Login");
                System.out.println("0. Exit");
                System.out.print("\nOption: ");

                String option = sc.nextLine();
                if (!handleAnonymousOption(option)) {
                    return false;
                }
            } else {
                System.out.println("Logged in as: " + auth.currentUser().get().email().value());
                System.out.println();

                int optionNum = 1;

                if (hasRole(Role.ADMIN)) {
                    System.out.println(optionNum + ". Users");
                    optionNum++;
                }
                if (hasRole(Role.ADMIN) || hasRole(Role.BACKOFFICE_OPERATOR)) {
                    System.out.println(optionNum + ". Customer management");
                    optionNum++;
                }
                if (hasRole(Role.WEATHER_PERSON)
                        || hasRole(Role.PILOT)
                        || hasRole(Role.FLIGHT_CONTROL_OPERATOR)
                        || hasRole(Role.ADMIN)) {
                    System.out.println(optionNum + ". Weather management");
                    optionNum++;
                }
                if (hasRole(Role.BACKOFFICE_OPERATOR) || hasRole(Role.ADMIN)) {
                    System.out.println(optionNum + ". Register Air Control Area");
                    optionNum++;
                }
                if (hasRole(Role.BACKOFFICE_OPERATOR) || hasRole(Role.ADMIN)) {
                    System.out.println(optionNum + ". Register Airport");
                    optionNum++;
                }
                if (hasRole(Role.BACKOFFICE_OPERATOR) || hasRole(Role.ADMIN)) {
                    System.out.println(optionNum + ". Register Engine Model");
                    optionNum++;
                }
                if (hasRole(Role.BACKOFFICE_OPERATOR) || hasRole(Role.ADMIN)) {
                    System.out.println(optionNum + ". Aircraft Model Management");
                    optionNum++;
                }
                if (hasRole(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR) || hasRole(Role.ADMIN)) {
                    System.out.println(optionNum + ". Add Aircraft to Fleet");
                    optionNum++;
                }
                if (hasRole(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR) || hasRole(Role.ADMIN)) {
                    System.out.println(optionNum + ". Decommission Aircraft");
                    optionNum++;
                }
                if (hasRole(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR) || hasRole(Role.ADMIN)) {
                    System.out.println(optionNum + ". List Fleet");
                    optionNum++;
                }
                if (hasRole(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR) || hasRole(Role.ADMIN)) {
                    System.out.println(optionNum + ". Add Pilot");
                    optionNum++;
                }
                if (hasRole(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR) || hasRole(Role.ADMIN)) {
                    System.out.println(optionNum + ". List Pilots");
                    optionNum++;
                }
                if (hasRole(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR) || hasRole(Role.ADMIN)) {
                    System.out.println(optionNum + ". Remove Pilot");
                    optionNum++;
                }
                if (hasRole(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR) || hasRole(Role.ADMIN)) {
                    System.out.println(optionNum + ". Create Flight Route");
                    optionNum++;
                }
                if (hasRole(Role.BACKOFFICE_OPERATOR) || hasRole(Role.PILOT) || hasRole(Role.ADMIN)) {
                    System.out.println(optionNum + ". Flight Plan Management");
                    optionNum++;
                }
                if (hasRole(Role.ADMIN) || hasRole(Role.FLIGHT_CONTROL_OPERATOR)) {
                    System.out.println(optionNum + ". Reports");
                    optionNum++;
                }

                System.out.println(optionNum + ". Logout");
                int logoutOption = optionNum;
                optionNum++;
                System.out.println(optionNum + ". Exit");

                System.out.print("\nOption: ");
                String option = sc.nextLine();

                if (!handleAuthenticatedOption(option)) {
                    return false;
                }
            }
        }
    }

    private boolean handleAnonymousOption(String option) {
        if ("1".equals(option)) {
            new LoginUI(auth).show();
            return true;
        } else if ("0".equals(option)) {
            return false;
        } else {
            System.out.println("Invalid option.");
            return true;
        }
    }

    private boolean handleAuthenticatedOption(String option) {
        List<String> availableActions = new ArrayList<>();
        int optionNum = 1;

        if (hasRole(Role.ADMIN)) { availableActions.add("USERS"); optionNum++; }
        if (hasRole(Role.ADMIN) || hasRole(Role.BACKOFFICE_OPERATOR)) { availableActions.add("CUSTOMER_MANAGEMENT"); optionNum++; }
        if (hasRole(Role.WEATHER_PERSON) || hasRole(Role.PILOT) || hasRole(Role.FLIGHT_CONTROL_OPERATOR) || hasRole(Role.ADMIN)) { availableActions.add("WEATHER_MANAGEMENT"); optionNum++; }
        if (hasRole(Role.BACKOFFICE_OPERATOR) || hasRole(Role.ADMIN)) { availableActions.add("REGISTER_AIR_CONTROL_AREA"); optionNum++; }
        if (hasRole(Role.BACKOFFICE_OPERATOR) || hasRole(Role.ADMIN)) { availableActions.add("REGISTER_AIRPORT"); optionNum++; }
        if (hasRole(Role.BACKOFFICE_OPERATOR) || hasRole(Role.ADMIN)) { availableActions.add("REGISTER_ENGINE_MODEL"); optionNum++; }
        if (hasRole(Role.BACKOFFICE_OPERATOR) || hasRole(Role.ADMIN)) { availableActions.add("AIRCRAFT_MODEL_MANAGEMENT"); optionNum++; }
        if (hasRole(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR) || hasRole(Role.ADMIN)) { availableActions.add("ADD_AIRCRAFT"); optionNum++; }
        if (hasRole(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR) || hasRole(Role.ADMIN)) { availableActions.add("DECOMMISSION_AIRCRAFT"); optionNum++; }
        if (hasRole(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR) || hasRole(Role.ADMIN)) { availableActions.add("LIST_FLEET"); optionNum++; }
        if (hasRole(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR) || hasRole(Role.ADMIN)) { availableActions.add("ADD_PILOT"); optionNum++; }
        if (hasRole(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR) || hasRole(Role.ADMIN)) { availableActions.add("LIST_PILOTS"); optionNum++; }
        if (hasRole(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR) || hasRole(Role.ADMIN)) { availableActions.add("REMOVE_PILOT"); optionNum++; }
        if (hasRole(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR) || hasRole(Role.ADMIN)) { availableActions.add("CREATE_FLIGHT_ROUTE"); optionNum++; }
        if (hasRole(Role.BACKOFFICE_OPERATOR) || hasRole(Role.PILOT) || hasRole(Role.ADMIN)) { availableActions.add("FLIGHT_PLAN_MANAGEMENT"); optionNum++; }
        if (hasRole(Role.ADMIN) || hasRole(Role.FLIGHT_CONTROL_OPERATOR)) { availableActions.add("REPORTS"); optionNum++; }

        int logoutOption = optionNum;
        optionNum++;
        int exitOption = optionNum;

        if (option.equals(String.valueOf(logoutOption))) {
            new LogoutUI(auth).show();
            return true;
        } else if (option.equals(String.valueOf(exitOption))) {
            return false;
        } else {
            try {
                int opt = Integer.parseInt(option);
                if (opt >= 1 && opt < logoutOption) {
                    String action = availableActions.get(opt - 1);
                    switch (action) {
                        case "USERS":
                            new UsersMenuUI(registerController, manageUserController).show();
                            return true;
                        case "CUSTOMER_MANAGEMENT":
                            new OrganizationMenuUI(companyController, collaboratorController).show();
                            return true;
                        case "WEATHER_MANAGEMENT":
                            new WeatherMenuUI(
                                    new RegisterWeatherDataUI(weatherController, areaRepo),
                                    new ImportWeatherDataUI(importWeatherDataController),
                                    new ConsultWeatherDataUI(consultWeatherDataController, areaRepo)
                            ).show();
                            return true;
                        case "REGISTER_AIR_CONTROL_AREA":
                            new RegisterAirControlAreaUI(airControlAreaController, scanner).show();
                            return true;
                        case "REGISTER_AIRPORT":
                            new RegisterAirportUI(airportController, scanner).show();
                            return true;
                        case "REGISTER_ENGINE_MODEL":
                            new RegisterEngineModelUI(engineModelController, scanner).show();
                            return true;
                        case "AIRCRAFT_MODEL_MANAGEMENT":
                            new AircraftModelManagementMenuUI(
                                    aircraftModelController,
                                    addEngineToAircraftModelController,
                                    removeEngineFromAircraftModelController,
                                    scanner
                            ).show();
                            return true;
                        case "ADD_AIRCRAFT":
                            new AddAircraftUI(addAircraftController, scanner).show();
                            return true;
                        case "DECOMMISSION_AIRCRAFT":
                            new DecommissionAircraftUI(decommissionAircraftController, scanner).show();
                            return true;
                        case "LIST_FLEET":
                            new ListFleetUI(listFleetController, scanner).show();
                            return true;
                        case "ADD_PILOT":
                            new AddPilotUI(addPilotController).show();
                            return true;
                        case "LIST_PILOTS":
                            new ListPilotsUI(listPilotsController).show();
                            return true;
                        case "REMOVE_PILOT":
                            new RemovePilotUI(removePilotController).show();
                            return true;
                        case "CREATE_FLIGHT_ROUTE":
                            new CreateFlightRouteUI(createFlightRouteController, scanner).show();
                            return true;
                        case "FLIGHT_PLAN_MANAGEMENT":
                            new FlightPlanMenuUI(
                                    flightPlanService,
                                    createFlightPlanController,
                                    insertWeatherDataController,
                                    validateFlightPlanController  // US085
                            ).show();
                            return true;
                        case "REPORTS":
                            reportsMenuUI.show();
                            return true;
                        default:
                            break;
                    }
                }
            } catch (NumberFormatException e) {
                // Invalid input, fall through to invalid option message.
            }
            System.out.println("Invalid option.");
            return true;
        }
    }

    private void printHeader(String title) {
        System.out.println();
        System.out.println("============================================================");
        System.out.println("  " + title);
        System.out.println("============================================================");
    }

    private boolean hasRole(Role role) {
        return auth.currentUser().map(u -> u.hasRole(role)).orElse(false);
    }
}