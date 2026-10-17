package eapli.aisafe.app.company.console.presentation.fleet;

import eapli.aisafe.app.company.console.connection.TcpClient;
import eapli.aisafe.tcpprotocol.AtccProtocol;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AddAircraftUI {
    private final TcpClient client;

    public AddAircraftUI(TcpClient client) {
        this.client = client;
    }

    public void show() throws Exception {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        System.out.println("--- Add Aircraft ---");
        System.out.print("Registration: "); String reg = console.readLine();
        System.out.print("Model ID: "); String model = console.readLine();
        System.out.print("Maker Name: "); String maker = console.readLine();
        System.out.print("Country: "); String country = console.readLine();
        System.out.print("First Class Seats: "); String fc = console.readLine();
        System.out.print("Business Seats: "); String biz = console.readLine();
        System.out.print("Economy Seats: "); String econ = console.readLine();
        System.out.print("Status (ACTIVE/MAINTENANCE/DECOMMISSIONED): "); String status = console.readLine();
        System.out.print("Manufacturing Year: "); String year = console.readLine();

        String payload = reg + "," + model + "," + maker + "," + country + "," + fc + "," + biz + "," + econ + "," + status + "," + year;
        String cmd = AtccProtocol.buildAddAircraft(
            new eapli.aisafe.fleetmanagement.dto.AircraftRegistrationDTO(
                reg, model, maker, country,
                Integer.parseInt(fc), Integer.parseInt(biz), Integer.parseInt(econ),
                status, Integer.parseInt(year)));
        String response = client.sendCommand(cmd);
        System.out.println("[Server] " + response);
    }
}
