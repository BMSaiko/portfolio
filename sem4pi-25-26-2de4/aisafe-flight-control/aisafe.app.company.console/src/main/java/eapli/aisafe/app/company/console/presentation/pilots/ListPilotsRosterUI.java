package eapli.aisafe.app.company.console.presentation.pilots;

import eapli.aisafe.app.company.console.connection.TcpClient;
import eapli.aisafe.tcpprotocol.AtccProtocol;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ListPilotsRosterUI {
    private final TcpClient client;

    public ListPilotsRosterUI(TcpClient client) {
        this.client = client;
    }

    public void show() throws Exception {
        System.out.println("--- List Pilot Roster ---");

        // Assumindo que criam o método buildListPilots no AtccProtocol
        String cmd = AtccProtocol.buildListPilots();
        String response = client.sendCommand(cmd);

        System.out.println("[Server] " + response);
    }
}