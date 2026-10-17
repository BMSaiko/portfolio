package eapli.aisafe.app.server.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class WeatherApp {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 5050;
    private static final String CMD_DISCONNECT = "DISCONNECT";

    public static void main(String[] args) {

        System.out.println("======================================");
        System.out.println("        AISAFE WEATHER CLIENT         ");
        System.out.println("======================================");
        System.out.println("Server: " + SERVER_HOST + ":" + SERVER_PORT);
        System.out.println();
        System.out.println("Available commands:");
        System.out.println("LOGIN <email> <password>");
        System.out.println("STATUS");
        System.out.println("CONSULT_WEATHER <area> <date>");
        System.out.println("IMPORT_WEATHER <csv_path>");
        System.out.println("REGISTER_WEATHER <area> <date> <temp> <windSpeed> <windDir> <humidity> <pressure>");
        System.out.println("DISCONNECT");
        System.out.println("======================================");
        System.out.println();

        try (
                Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                BufferedReader serverInput = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );
                PrintWriter serverOutput = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader keyboardInput = new BufferedReader(
                        new InputStreamReader(System.in)
                )
        ) {

            System.out.println(serverInput.readLine());
            System.out.println();

            String command;

            do {
                System.out.print("> ");
                command = keyboardInput.readLine();

                if (command == null || command.isBlank()) {
                    continue;
                }

                serverOutput.println(command);

                final String response = serverInput.readLine();

                if (response == null) {
                    System.out.println("Server closed connection.");
                    break;
                }

                System.out.println(response);

            } while (!CMD_DISCONNECT.equalsIgnoreCase(command));

        } catch (final IOException e) {
            System.err.println("Error connecting to Weather TCP Server: " + e.getMessage());
        }
    }
}