package eapli.aisafe.app.company.console.presentation;

import eapli.aisafe.app.company.console.connection.TcpClient;
import eapli.aisafe.app.company.console.presentation.fleet.AddAircraftUI;
import eapli.aisafe.app.company.console.presentation.fleet.DecommissionAircraftUI;
import eapli.aisafe.app.company.console.presentation.fleet.ListFleetUI;
import eapli.aisafe.app.company.console.presentation.pilots.AddPilotUI;
import eapli.aisafe.app.company.console.presentation.pilots.ListPilotsRosterUI;
import eapli.aisafe.app.company.console.presentation.pilots.RemovePilotUI;
import eapli.aisafe.app.company.console.presentation.routes.CreateFlightRouteUI;
import eapli.aisafe.app.company.console.presentation.routes.DeactivateFlightRouteUI;
import eapli.aisafe.tcpprotocol.AtccProtocol;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Main console menu for the air transport company client application.
 *
 * <p>
 * This menu is shown after a company collaborator successfully authenticates
 * with the TCP server. It groups the available company-side operations, such as
 * fleet management and route management.
 * </p>
 *
 * <p>
 * The menu delegates each operation to a dedicated UI class and uses
 * {@link TcpClient} to communicate with the server through the ATCC protocol.
 * </p>
 */
public class MainMenu {

    /**
     * TCP client used to send commands to the server.
     */
    private final TcpClient client;

    /**
     * Creates the main menu.
     *
     * @param client TCP client connected to the AISafe server
     */
    public MainMenu(TcpClient client) {
        this.client = client;
    }

    /**
     * Displays the main menu and dispatches the selected user option.
     *
     * <p>
     * The menu remains active until the user selects the logout option. On
     * logout, a logout command is sent to the server and the menu loop ends.
     * </p>
     *
     * @throws Exception if console input fails or if one of the delegated UIs
     *                   fails while communicating with the server
     */
    public void show() throws Exception {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        boolean running = true;

        while (running) {
            System.out.println("=== Main Menu ===");
            System.out.println("1. Fleet Management");
            System.out.println("2. Route Management");
            System.out.println("3. Pilot Management");
            System.out.println("0. Logout");
            System.out.print("Option: ");

            String opt = console.readLine();

            switch (opt) {
                case "1":
                    showFleetMenu(console);
                    break;
                case "2":
                    showRoutesMenu(console);
                    break;
                case "3":
                    showPilotsMenu(console);
                    break;
                case "0":
                    String bye = client.sendCommand(AtccProtocol.buildLogout());
                    System.out.println("[Server] " + bye);
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }

            System.out.println();
        }
    }

    /**
     * Displays the fleet management submenu.
     *
     * <p>
     * The submenu provides access to aircraft creation, aircraft
     * decommissioning and fleet listing operations.
     * </p>
     *
     * @param console console reader shared with the main menu
     * @throws Exception if console input fails or if a delegated fleet UI fails
     */
    private void showFleetMenu(BufferedReader console) throws Exception {
        System.out.println("--- Fleet Management ---");
        System.out.println("1. Add Aircraft");
        System.out.println("2. Decommission Aircraft");
        System.out.println("3. List Fleet");
        System.out.println("0. Back");
        System.out.print("Option: ");

        String opt = console.readLine();

        switch (opt) {
            case "1":
                new AddAircraftUI(client).show();
                break;
            case "2":
                new DecommissionAircraftUI(client).show();
                break;
            case "3":
                new ListFleetUI(client).show();
                break;
            case "0":
                return;
            default:
                System.out.println("Invalid option.");
        }
    }

    /**
     * Displays the route management submenu.
     *
     * <p>
     * The submenu provides access to flight route creation and flight route
     * deactivation operations.
     * </p>
     *
     * @param console console reader shared with the main menu
     * @throws Exception if console input fails or if a delegated route UI fails
     */
    private void showRoutesMenu(BufferedReader console) throws Exception {
        System.out.println("--- Route Management ---");
        System.out.println("1. Create Flight Routes");
        System.out.println("2. Deactivate Flight Route");
        System.out.println("0. Back");
        System.out.print("Option: ");

        String opt = console.readLine();

        switch (opt) {
            case "1":
                new CreateFlightRouteUI(client).show();
                break;
            case "2":
                new DeactivateFlightRouteUI(client).show();
                break;
            case "0":
                return;
            default:
                System.out.println("Invalid option.");
        }
    }

    /**
     Displays the pilot management submenu.
     */
    private void showPilotsMenu(BufferedReader console) throws Exception {
        System.out.println("--- Pilot Management ---");
        System.out.println("1. Add Pilot");
        System.out.println("2. List Pilot Roster");
        System.out.println("3. Remove (Deactivate) Pilot");
        System.out.println("0. Back");
        System.out.print("Option: ");

        String opt = console.readLine();

        switch (opt) {
            case "1":
                new AddPilotUI(client).show();
                break;
            case "2":
                new ListPilotsRosterUI(client).show();
                break;
            case "3":
                new RemovePilotUI(client).show();
                break;
            case "0":
                return;
            default:
                System.out.println("Invalid option.");
        }
    }
}