package eapli.aisafe.app.company.console.presentation.routes;

import eapli.aisafe.app.company.console.connection.TcpClient;
import eapli.aisafe.tcpprotocol.AtccProtocol;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Console user interface for deactivating a flight route.
 *
 * <p>
 * This UI supports the company-side interaction for US074. It asks the air
 * transport company collaborator for the route designator and the deactivation
 * date, builds the corresponding ATCC protocol command and sends it to the
 * server through {@link TcpClient}.
 * </p>
 *
 * <p>
 * Business rules are enforced by the server-side application controller. This
 * class is responsible only for collecting user input, sending the request and
 * displaying the server response.
 * </p>
 */
public class DeactivateFlightRouteUI {

    /**
     * TCP client used to communicate with the AISafe server.
     */
    private final TcpClient client;

    /**
     * Creates the deactivate flight route UI.
     *
     * @param client TCP client connected to the AISafe server
     */
    public DeactivateFlightRouteUI(TcpClient client) {
        this.client = client;
    }

    /**
     * Displays the deactivation form, sends the command to the server and prints
     * the response.
     *
     * <p>
     * The deactivation date must be entered in {@code yyyy-MM-dd} format because
     * that is the format expected by the server-side route deactivation handler.
     * </p>
     *
     * @throws Exception if console input fails or if communication with the
     *                   server fails
     */
    public void show() throws Exception {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        System.out.println("--- Deactivate Flight Route ---");

        System.out.print("Route Designator: ");
        String designator = console.readLine();

        System.out.print("Deactivation Date (yyyy-MM-dd): ");
        String date = console.readLine();

        String cmd = AtccProtocol.buildDeactivateRoute(designator, date);
        String response = client.sendCommand(cmd);

        System.out.println("[Server] " + response);
    }
}