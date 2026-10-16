package eapli.aisafe.logging.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogEntry {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final LocalDateTime timestamp;
    private final String username;
    private final String clientIpAddress;
    private final int clientPort;
    private final String serviceType;
    private final String eventType;

    public LogEntry(LocalDateTime timestamp, String username, String clientIpAddress,
                    int clientPort, String serviceType, String eventType) {
        this.timestamp = timestamp;
        this.username = username;
        this.clientIpAddress = clientIpAddress;
        this.clientPort = clientPort;
        this.serviceType = serviceType;
        this.eventType = eventType;
    }

    public LocalDateTime timestamp() { return timestamp; }
    public String username() { return username; }
    public String clientIpAddress() { return clientIpAddress; }
    public int clientPort() { return clientPort; }
    public String serviceType() { return serviceType; }
    public String eventType() { return eventType; }

    public static LogEntry fromLogString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 6) {
            throw new IllegalArgumentException("Invalid log format");
        }
        LocalDateTime ts = LocalDateTime.parse(parts[0].trim(), FMT);
        return new LogEntry(ts, parts[1].trim(), parts[2].trim(),
                Integer.parseInt(parts[3].trim()), parts[4].trim(), parts[5].trim());
    }

    public String toLogString() {
        return timestamp.format(FMT) + "|" + username + "|" + clientIpAddress
                + "|" + clientPort + "|" + serviceType + "|" + eventType;
    }

    public String toJson() {
        return String.format(
                "{\"timestamp\":\"%s\",\"username\":\"%s\",\"clientIp\":\"%s\",\"clientPort\":%d,\"service\":\"%s\",\"event\":\"%s\"}",
                timestamp.format(FMT), escapeJson(username), escapeJson(clientIpAddress),
                clientPort, serviceType, eventType);
    }

    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
