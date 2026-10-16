package eapli.aisafe.app.company.console.presentation.pilots;

import eapli.aisafe.app.company.console.connection.TcpClient;
import eapli.aisafe.tcpprotocol.AtccProtocol;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class RemovePilotUI {
    private final TcpClient client;

    public RemovePilotUI(TcpClient client) {
        this.client = client;
    }

    public void show() throws Exception {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        System.out.println("--- Remove (Deactivate) Pilot ---");
        System.out.print("Pilot Email or ID: ");
        String pilotId = console.readLine();

        // Assumindo que criam o método buildRemovePilot no AtccProtocol
        String cmd = AtccProtocol.buildRemovePilot(pilotId);
        String response = client.sendCommand(cmd);

        System.out.println("[Server] " + response);
    }
}