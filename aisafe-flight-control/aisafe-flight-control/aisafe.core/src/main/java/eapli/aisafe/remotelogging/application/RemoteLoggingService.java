package eapli.aisafe.remotelogging.application;

import eapli.aisafe.remotelogging.domain.EventType;
import eapli.aisafe.remotelogging.domain.RemoteAccessLogEvent;
import eapli.aisafe.remotelogging.domain.ServiceType;
import eapli.aisafe.remotelogging.infrastructure.UdpLogClient;

import java.time.LocalDateTime;
import java.util.logging.Logger;

public class RemoteLoggingService {

    private static final Logger LOGGER = Logger.getLogger(RemoteLoggingService.class.getName());

    private final UdpLogClient udpLogClient;

    public RemoteLoggingService(String serverHost, int serverPort) {
        this.udpLogClient = new UdpLogClient(serverHost, serverPort);
    }

    public void logEvent(String username, String clientIp, int clientPort,
                         ServiceType service, EventType eventType) {
        RemoteAccessLogEvent event = new RemoteAccessLogEvent(
                LocalDateTime.now(), username, clientIp, clientPort, service, eventType);
        udpLogClient.sendLogEvent(event);
        LOGGER.fine("Logged " + eventType + " for " + username + " on " + service);
    }

    public void close() {
        udpLogClient.close();
    }
}
