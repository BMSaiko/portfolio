package eapli.aisafe.remotelogging.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class RemoteAccessLogEvent {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final LocalDateTime timestamp;
    private final String username;
    private final String clientIpAddress;
    private final int clientPort;
    private final ServiceType serviceType;
    private final EventType eventType;

    public RemoteAccessLogEvent(LocalDateTime timestamp, String username,
            String clientIpAddress, int clientPort,
            ServiceType serviceType, EventType eventType) {
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (clientIpAddress == null || clientIpAddress.isBlank()) {
            throw new IllegalArgumentException("IP address cannot be empty");
        }
        if (clientPort <= 0 || clientPort > 65535) {
            throw new IllegalArgumentException("Port must be between 1 and 65535");
        }
        if (serviceType == null) {
            throw new IllegalArgumentException("Service type cannot be null");
        }
        if (eventType == null) {
            throw new IllegalArgumentException("Event type cannot be null");
        }
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
    public ServiceType serviceType() { return serviceType; }
    public EventType eventType() { return eventType; }

    public String toLogString() {
        return timestamp.format(FMT) + "|" + username + "|" + clientIpAddress
                + "|" + clientPort + "|" + serviceType.name() + "|" + eventType.name();
    }

    public static RemoteAccessLogEvent fromLogString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 6) {
            throw new IllegalArgumentException("Invalid log format. Expected 6 pipe-delimited fields.");
        }
        LocalDateTime ts = LocalDateTime.parse(parts[0].trim(), FMT);
        String user = parts[1].trim();
        String ip = parts[2].trim();
        int port = Integer.parseInt(parts[3].trim());
        ServiceType svc = ServiceType.valueOf(parts[4].trim());
        EventType evt = EventType.valueOf(parts[5].trim());
        return new RemoteAccessLogEvent(ts, user, ip, port, svc, evt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoteAccessLogEvent that = (RemoteAccessLogEvent) o;
        return clientPort == that.clientPort && timestamp.equals(that.timestamp)
                && username.equals(that.username) && clientIpAddress.equals(that.clientIpAddress)
                && serviceType == that.serviceType && eventType == that.eventType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, username, clientIpAddress, clientPort, serviceType, eventType);
    }
}
