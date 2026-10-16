package eapli.aisafe.app.company.console;

import eapli.aisafe.app.company.console.connection.TcpClient;
import eapli.aisafe.app.company.console.presentation.MainMenu;
import eapli.aisafe.tcpprotocol.AtccProtocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class AirTransportCompanyApp {

    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 2002;

    public static void main(final String[] args) {
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT;
        if (args.length >= 1) host = args[0];
        if (args.length >= 2) {
            try { port = Integer.parseInt(args[1]); }
            catch (NumberFormatException e) {
                System.out.println("Invalid port. Using default: " + DEFAULT_PORT);
            }
        }

        System.out.println("============================================");
        System.out.println("  Air Transport Company App (TCP Client)");
        System.out.println("============================================");
        System.out.println();

        TcpClient client = new TcpClient();
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

        try {
            client.connect(host, port);
            System.out.println("Connected to " + host + ":" + port);
            System.out.println();

            boolean running = true;
            while (running) {
                boolean authenticated = false;
                while (!authenticated) {
                    try {
                        System.out.print("Email: ");
                        String email = console.readLine();
                        if (email == null) {
                            System.out.println();
                            System.out.println("[ERROR] Input closed. Exiting.");
                            running = false;
                            break;
                        }
                        email = email.trim();
                        if (email.isEmpty()) {
                            System.out.println("[ERROR] Email cannot be empty.");
                            continue;
                        }
                        System.out.print("Password: ");
                        String password = console.readLine();
                        if (password == null) {
                            System.out.println();
                            System.out.println("[ERROR] Input closed. Exiting.");
                            running = false;
                            break;
                        }
                        password = password.trim();
                        String response = client.sendCommand(AtccProtocol.buildAuth(email, password));
                        if (response == null) {
                            System.out.println("[ERROR] Server closed connection. Reconnecting...");
                            safeReconnect(client, host, port, console);
                            continue;
                        }
                        if (response.startsWith("AUTH_OK")) {
                            System.out.println();
                            System.out.println("[AUTH_OK] Welcome, " + email);
                            System.out.println();
                            authenticated = true;
                        } else {
                            System.out.println("[AUTH_FAIL] Invalid credentials. Try again.");
                            System.out.println();
                        }
                    } catch (IOException ioe) {
                        System.out.println("[ERROR] Connection lost: " + ioe.getMessage());
                        System.out.print("Reconnect? (y/n): ");
                        String retry = console.readLine();
                        if (retry == null || !retry.trim().equalsIgnoreCase("y")) {
                            running = false;
                            break;
                        }
                        safeReconnect(client, host, port, console);
                    }
                }
                if (!running) break;

                MainMenu menu = new MainMenu(client);
                menu.show();

                System.out.println();
                System.out.print("Login again? (y/n): ");
                String choice = console.readLine();
                if (choice == null || !choice.trim().equalsIgnoreCase("y")) {
                    running = false;
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.err.println("Connection error: " + e.getMessage());
        } finally {
            client.disconnect();
            System.out.println("Disconnected. Goodbye.");
        }
    }

    private static void safeReconnect(TcpClient client, String host, int port, BufferedReader console) {
        try { client.disconnect(); } catch (Exception ig) { }
        boolean connected = false;
        while (!connected) {
            try {
                client.connect(host, port);
                System.out.println("Reconnected to " + host + ":" + port);
                connected = true;
            } catch (IOException e) {
                System.out.print("Reconnect failed. Retry? (y/n): ");
                try {
                    String r = console.readLine();
                    if (r == null || !r.trim().equalsIgnoreCase("y")) return;
                } catch (IOException ig) { return; }
            }
        }
    }
}