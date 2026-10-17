package eapli.aisafe.app.company.console.presentation.fleet;

import eapli.aisafe.app.company.console.connection.TcpClient;
import eapli.aisafe.tcpprotocol.AtccProtocol;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ListFleetUI {
    private final TcpClient client;

    public ListFleetUI(TcpClient client) {
        this.client = client;
    }

    public void show() throws Exception {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        System.out.println("--- List Fleet ---");
        System.out.println("Filter: ALL | MODEL | MAKER | CAPACITY | AGE");
        System.out.print("Filter type: ");
        String filter = console.readLine();
        String value = "";
        if (!filter.equalsIgnoreCase("ALL")) {
            System.out.print("Filter value: ");
            value = console.readLine();
        }
        String cmd = AtccProtocol.buildListFleet(filter.toUpperCase(), value);
        String response = client.sendCommand(cmd);
        System.out.println("[Server] " + response);
    }
}
