package eapli.aisafe.app.company.console.presentation.routes;

import eapli.aisafe.app.company.console.connection.TcpClient;
import eapli.aisafe.tcpprotocol.AtccProtocol;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class CreateFlightRouteUI {
    private final TcpClient client;

    public CreateFlightRouteUI(TcpClient client) {
        this.client = client;
    }

    public void show() throws Exception {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        System.out.println("--- Create Flight Routes ---");
        System.out.print("Route Designator: ");
        String designator = console.readLine();
        System.out.print("Origin IATA Code: ");
        String origin = console.readLine();
        System.out.print("Destination IATA Code: ");
        String dest = console.readLine();
        String cmd = AtccProtocol.buildCreateRoute(designator, origin.toUpperCase(), dest.toUpperCase());
        String response = client.sendCommand(cmd);
        System.out.println("[Server] " + response);
    }
}
