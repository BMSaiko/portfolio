package eapli.aisafe.app.server.weather;

import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.weather.application.ConsultWeatherDataController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class WeatherTcpServer {

    private final int port;
    private final AuthService authService;
    private final ConsultWeatherDataController consultWeatherDataController;

    private boolean running = false;
    private ServerSocket serverSocket;

    public WeatherTcpServer(final int port,
                            final AuthService authService,
                            final ConsultWeatherDataController consultWeatherDataController) {
        this.port = port;
        this.authService = authService;
        this.consultWeatherDataController = consultWeatherDataController;
    }

    public void start() throws IOException {

        serverSocket = new ServerSocket(port);
        running = true;

        System.out.println("\nWeather TCP Server listening on port " + port);

        while (running) {
            try {
                final Socket clientSocket = serverSocket.accept();

                System.out.println("New weather client connected: "
                        + clientSocket.getInetAddress());

                final WeatherConnectionHandler handler =
                        new WeatherConnectionHandler(
                                clientSocket,
                                authService,
                                consultWeatherDataController
                        );

                final Thread clientThread = new Thread(handler);
                clientThread.start();

            } catch (final IOException e) {

                if (running) {
                    System.err.println("Error accepting client connection: "
                            + e.getMessage());
                }
            }
        }
    }

    public void stop() {

        running = false;

        try {

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

        } catch (final IOException e) {

            System.err.println("Error stopping weather TCP server: "
                    + e.getMessage());
        }
    }
}