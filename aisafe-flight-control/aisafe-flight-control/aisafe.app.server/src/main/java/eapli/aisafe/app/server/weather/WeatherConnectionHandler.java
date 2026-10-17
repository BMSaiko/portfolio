package eapli.aisafe.app.server.weather;

import eapli.aisafe.remotelogging.domain.EventType;
import eapli.aisafe.remotelogging.domain.RemoteAccessLogEvent;
import eapli.aisafe.remotelogging.domain.ServiceType;
import eapli.aisafe.remotelogging.infrastructure.UdpLogClient;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.LocalDateTime;
import java.util.Optional;

public class WeatherConnectionHandler implements Runnable {

    private static final String CMD_STATUS = "STATUS";
    private static final String CMD_LOGIN = "LOGIN";
    private static final String CMD_DISCONNECT = "DISCONNECT";
    private static final String CMD_CONSULT_WEATHER = "CONSULT_WEATHER";
    private static final String CMD_IMPORT_WEATHER = "IMPORT_WEATHER";
    private static final String CMD_REGISTER_WEATHER = "REGISTER_WEATHER";

    private static final String LOG_SERVER_HOST = "localhost";
    private static final int LOG_SERVER_PORT = 9999;

    private final Socket clientSocket;
    private final AuthService authService;
    private final UdpLogClient udpLogClient;

    private boolean authenticated = false;
    private String authenticatedUser;
    private boolean normalDisconnect = false;

    public WeatherConnectionHandler(final Socket clientSocket,
                                    final AuthService authService,
                                    final Object consultWeatherDataController) {
        this.clientSocket = clientSocket;
        this.authService = authService;
        this.udpLogClient = new UdpLogClient(LOG_SERVER_HOST, LOG_SERVER_PORT);
    }

    @Override
    public void run() {
        try {
            handleClient();
        } catch (final IOException e) {
            System.err.println("Error handling weather client: " + e.getMessage());
        } finally {
            if (authenticated) {
                if (normalDisconnect) {
                    logEvent(authenticatedUser, EventType.LOGOUT);
                } else {
                    logEvent(authenticatedUser, EventType.DISCONNECT);
                }
            }

            if (authService != null) {
                authService.logout();
            }

            udpLogClient.close();
            closeClientSocket();
        }
    }

    private void handleClient() throws IOException {
        try (
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream())
                );
                PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            output.println("CONNECTED");

            String request;
            while ((request = input.readLine()) != null) {
                final String command = request.trim();
                final String response = processRequest(command);

                output.println(response);

                if (CMD_DISCONNECT.equalsIgnoreCase(command)) {
                    normalDisconnect = true;
                    break;
                }
            }
        }
    }

    private String processRequest(final String command) {
        if (command.isBlank()) {
            return "ERROR EMPTY_COMMAND";
        }

        final String[] parts = command.split("\\s+");
        final String operation = parts[0].toUpperCase();

        return switch (operation) {
            case CMD_STATUS -> processStatus();
            case CMD_LOGIN -> processLogin(parts);
            case CMD_CONSULT_WEATHER -> processConsultWeather(parts);
            case CMD_IMPORT_WEATHER -> processImportWeather(parts);
            case CMD_REGISTER_WEATHER -> processRegisterWeather(parts);
            case CMD_DISCONNECT -> "CONNECTION_CLOSED";
            default -> "ERROR UNKNOWN_COMMAND";
        };
    }

    private String processStatus() {
        if (!authenticated) {
            return "ERROR NOT_AUTHENTICATED";
        }

        return "STATUS_OK USER " + authenticatedUser;
    }

    private String processLogin(final String[] parts) {
        if (parts.length != 3) {
            return "ERROR INVALID_LOGIN_FORMAT";
        }

        if (authService == null) {
            return "ERROR AUTH_SERVICE_NOT_CONFIGURED";
        }

        final String username = parts[1];
        final String password = parts[2];

        final Optional<User> user = authService.login(username, password);

        if (user.isEmpty()) {
            logEvent(username, EventType.LOGIN_FAILURE);
            return "ERROR INVALID_CREDENTIALS";
        }

        authenticated = true;
        authenticatedUser = username;

        logEvent(username, EventType.LOGIN_SUCCESS);

        return "LOGIN_OK";
    }

    private String processConsultWeather(final String[] parts) {
        if (!authenticated) {
            return "ERROR NOT_AUTHENTICATED";
        }

        if (parts.length != 3) {
            return "ERROR INVALID_CONSULT_WEATHER_FORMAT";
        }

        try {
            parseDate(parts[2]);
            return "WEATHER_DATA_EMPTY";
        } catch (final DateTimeParseException e) {
            return "ERROR INVALID_DATE_FORMAT";
        }
    }

    private String processImportWeather(final String[] parts) {
        if (!authenticated) {
            return "ERROR NOT_AUTHENTICATED";
        }

        if (parts.length != 2) {
            return "ERROR INVALID_IMPORT_WEATHER_FORMAT";
        }

        return "IMPORT_WEATHER_OK";
    }

    private String processRegisterWeather(final String[] parts) {
        if (!authenticated) {
            return "ERROR NOT_AUTHENTICATED";
        }

        if (parts.length < 8) {
            return "ERROR INVALID_REGISTER_WEATHER_FORMAT";
        }

        try {
            parseDate(parts[2]);
            return "REGISTER_WEATHER_OK";
        } catch (final DateTimeParseException e) {
            return "ERROR INVALID_DATE_FORMAT";
        }
    }

    private void logEvent(final String username, final EventType eventType) {
        final String clientIp = clientSocket.getInetAddress().getHostAddress();
        final int clientPort = clientSocket.getPort();

        final RemoteAccessLogEvent event = new RemoteAccessLogEvent(
                LocalDateTime.now(),
                username,
                clientIp,
                clientPort,
                ServiceType.US044,
                eventType
        );

        udpLogClient.sendLogEvent(event);   // ← ESTA LINHA ESTÁ EM FALTA
    }

    private LocalDate parseDate(final String dateText) {
        final DateTimeFormatter[] formats = {
                DateTimeFormatter.ISO_LOCAL_DATE,
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        };

        for (final DateTimeFormatter format : formats) {
            try {
                return LocalDate.parse(dateText, format);
            } catch (final DateTimeParseException ignored) {
            }
        }

        throw new DateTimeParseException("Invalid date format", dateText, 0);
    }

    private void closeClientSocket() {
        try {
            if (!clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (final IOException e) {
            System.err.println("Error closing weather client socket: " + e.getMessage());
        }
    }
}