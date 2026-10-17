package eapli.aisafe.logging.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ActiveSession {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final String username;
    private final String clientIpAddress;
    private final int clientPort;
    private final String serviceType;
    private final LocalDateTime loginTime;

    public ActiveSession(String username, String clientIpAddress, int clientPort,
                         String serviceType, LocalDateTime loginTime) {
        this.username = username;
        this.clientIpAddress = clientIpAddress;
        this.clientPort = clientPort;
        this.serviceType = serviceType;
        this.loginTime = loginTime;
    }

    public String username() { return username; }
    public String clientIpAddress() { return clientIpAddress; }
    public int clientPort() { return clientPort; }
    public String serviceType() { return serviceType; }
    public LocalDateTime loginTime() { return loginTime; }

    public String toJson() {
        return String.format(
                "{\"username\":\"%s\",\"clientIp\":\"%s\",\"clientPort\":%d,\"service\":\"%s\",\"loginTime\":\"%s\"}",
                escapeJson(username), escapeJson(clientIpAddress), clientPort,
                serviceType, loginTime.format(FMT));
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
