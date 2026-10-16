package eapli.aisafe.app.company.console.presentation.fleet;

import eapli.aisafe.app.company.console.connection.TcpClient;
import eapli.aisafe.tcpprotocol.AtccProtocol;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class DecommissionAircraftUI {
    private final TcpClient client;

    public DecommissionAircraftUI(TcpClient client) {
        this.client = client;
    }

    public void show() throws Exception {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        System.out.println("--- Decommission Aircraft ---");
        System.out.print("Registration: ");
        String reg = console.readLine();
        String cmd = AtccProtocol.buildDecommissionAircraft(reg);
        String response = client.sendCommand(cmd);
        System.out.println("[Server] " + response);
    }
}
