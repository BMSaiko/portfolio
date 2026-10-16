package eapli.aisafe.logging;

import eapli.aisafe.logging.model.ActiveSession;
import eapli.aisafe.logging.model.LogEntry;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ActiveSessionTracker {

    private final ConcurrentHashMap<String, ActiveSession> activeSessions = new ConcurrentHashMap<>();

    public void update(LogEntry entry) {
        String key = buildKey(entry.username(), entry.clientIpAddress(), entry.clientPort());

        switch (entry.eventType()) {
            case "LOGIN_SUCCESS":
                ActiveSession session = new ActiveSession(
                        entry.username(), entry.clientIpAddress(), entry.clientPort(),
                        entry.serviceType(), entry.timestamp());
                activeSessions.put(key, session);
                break;
            case "LOGOUT":
            case "DISCONNECT":
            case "LOGIN_FAILURE":
                activeSessions.remove(key);
                break;
        }
    }

    public Collection<ActiveSession> getActiveSessions() {
        return Collections.unmodifiableCollection(activeSessions.values());
    }

    public int getActiveSessionCount() {
        return activeSessions.size();
    }

    private String buildKey(String username, String ip, int port) {
        return username + "|" + ip + "|" + port;
    }
}
