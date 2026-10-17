package eapli.aisafe.app.company.console.presentation.pilots;

import eapli.aisafe.app.company.console.connection.TcpClient;
import eapli.aisafe.tcpprotocol.AtccProtocol;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AddPilotUI {
    private final TcpClient client;

    public AddPilotUI(TcpClient client) {
        this.client = client;
    }

    public void show() throws Exception {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        System.out.println("\n--- Add Pilot ---");

        System.out.print("Pilot Name: ");
        String name = console.readLine();

        System.out.print("Pilot Email: ");
        String email = console.readLine();

        System.out.print("Phone Number: ");
        String phone = console.readLine();

        System.out.print("Position (e.g. Captain, First Officer): ");
        String position = console.readLine();

        System.out.print("System Password: ");
        String password = console.readLine();

        System.out.println("\nFetching available Aircraft Models from server...");
        String modelsResponse = client.sendCommand(AtccProtocol.buildListModels());

        String displayModels = modelsResponse.startsWith("OK ") ? modelsResponse.substring(3) : modelsResponse;

        System.out.println("\n=== Available Aircraft Models ===");
        System.out.println(displayModels);
        System.out.println("=================================");

        System.out.println("\n[!] Certifications format: ModelID:MakerName (e.g., B738:Boeing)");
        System.out.println("[!] For multiple models, separate with '|' (e.g., B738:Boeing|A320:Airbus)");
        System.out.print("Certified Aircraft Models: ");
        String models = console.readLine();

        String cmd = AtccProtocol.buildAddPilot(email, name, phone, position, password, models);
        String response = client.sendCommand(cmd);

        System.out.println("\n[Server] " + response);
    }
}