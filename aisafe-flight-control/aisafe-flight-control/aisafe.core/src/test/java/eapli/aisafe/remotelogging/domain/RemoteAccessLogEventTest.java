package eapli.aisafe.remotelogging.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class RemoteAccessLogEventTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 5, 25, 14, 30, 22);
    private static final String USER = "testuser";
    private static final String IP = "127.0.0.1";
    private static final int PORT = 54321;

    @Test
    @DisplayName("Ensure timestamp cannot be null")
    void ensureTimestampCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new RemoteAccessLogEvent(null, USER, IP, PORT, ServiceType.US078, EventType.LOGIN_SUCCESS));
    }

    @Test
    @DisplayName("Ensure username cannot be empty")
    void ensureUsernameCannotBeEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
            new RemoteAccessLogEvent(NOW, "", IP, PORT, ServiceType.US078, EventType.LOGIN_SUCCESS));
    }

    @Test
    @DisplayName("Ensure username cannot be null")
    void ensureUsernameCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new RemoteAccessLogEvent(NOW, null, IP, PORT, ServiceType.US078, EventType.LOGIN_SUCCESS));
    }

    @Test
    @DisplayName("Ensure IP address cannot be empty")
    void ensureIpCannotBeEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
            new RemoteAccessLogEvent(NOW, USER, "", PORT, ServiceType.US078, EventType.LOGIN_SUCCESS));
    }

    @Test
    @DisplayName("Ensure IP address cannot be null")
    void ensureIpCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new RemoteAccessLogEvent(NOW, USER, null, PORT, ServiceType.US078, EventType.LOGIN_SUCCESS));
    }

    @Test
    @DisplayName("Ensure port must be positive")
    void ensurePortMustBePositive() {
        assertThrows(IllegalArgumentException.class, () ->
            new RemoteAccessLogEvent(NOW, USER, IP, 0, ServiceType.US078, EventType.LOGIN_SUCCESS));
    }

    @Test
    @DisplayName("Ensure port cannot exceed 65535")
    void ensurePortCannotExceedMax() {
        assertThrows(IllegalArgumentException.class, () ->
            new RemoteAccessLogEvent(NOW, USER, IP, 65536, ServiceType.US078, EventType.LOGIN_SUCCESS));
    }

    @Test
    @DisplayName("Ensure service type cannot be null")
    void ensureServiceTypeCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new RemoteAccessLogEvent(NOW, USER, IP, PORT, null, EventType.LOGIN_SUCCESS));
    }

    @Test
    @DisplayName("Ensure event type cannot be null")
    void ensureEventTypeCannotBeNull() {
        assertThrows(IllegalArgumentException.class, () ->
            new RemoteAccessLogEvent(NOW, USER, IP, PORT, ServiceType.US078, null));
    }

    @Test
    @DisplayName("Ensure valid event is created")
    void ensureValidEventIsCreated() {
        RemoteAccessLogEvent event = new RemoteAccessLogEvent(
            NOW, USER, IP, PORT, ServiceType.US078, EventType.LOGIN_SUCCESS);
        assertEquals(NOW, event.timestamp());
        assertEquals(USER, event.username());
        assertEquals(IP, event.clientIpAddress());
        assertEquals(PORT, event.clientPort());
        assertEquals(ServiceType.US078, event.serviceType());
        assertEquals(EventType.LOGIN_SUCCESS, event.eventType());
    }

    @Test
    @DisplayName("Ensure toLogString produces pipe-delimited format")
    void ensureToLogStringFormat() {
        RemoteAccessLogEvent event = new RemoteAccessLogEvent(
            NOW, USER, IP, PORT, ServiceType.US078, EventType.LOGIN_SUCCESS);
        String log = event.toLogString();
        assertTrue(log.contains("2026-05-25T14:30:22"));
        assertTrue(log.contains(USER));
        assertTrue(log.contains(IP));
        assertTrue(log.contains(String.valueOf(PORT)));
        assertTrue(log.contains("US078"));
        assertTrue(log.contains("LOGIN_SUCCESS"));
    }

    @Test
    @DisplayName("Ensure toLogString and fromLogString roundtrip")
    void ensureLogStringRoundtrip() {
        RemoteAccessLogEvent original = new RemoteAccessLogEvent(
            NOW, USER, IP, PORT, ServiceType.US044, EventType.LOGIN_FAILURE);
        String logString = original.toLogString();
        RemoteAccessLogEvent parsed = RemoteAccessLogEvent.fromLogString(logString);
        assertEquals(original, parsed);
    }

    @Test
    @DisplayName("Ensure fromLogString rejects invalid format")
    void ensureFromLogStringRejectsInvalid() {
        assertThrows(IllegalArgumentException.class, () ->
            RemoteAccessLogEvent.fromLogString("invalid|format"));
    }

    @Test
    @DisplayName("Ensure all service types work in roundtrip")
    void ensureAllServiceTypesRoundtrip() {
        for (ServiceType svc : ServiceType.values()) {
            RemoteAccessLogEvent event = new RemoteAccessLogEvent(
                NOW, USER, IP, PORT, svc, EventType.LOGOUT);
            String log = event.toLogString();
            RemoteAccessLogEvent parsed = RemoteAccessLogEvent.fromLogString(log);
            assertEquals(event, parsed);
        }
    }

    @Test
    @DisplayName("Ensure all event types work in roundtrip")
    void ensureAllEventTypesRoundtrip() {
        for (EventType evt : EventType.values()) {
            RemoteAccessLogEvent event = new RemoteAccessLogEvent(
                NOW, USER, IP, PORT, ServiceType.US078, evt);
            String log = event.toLogString();
            RemoteAccessLogEvent parsed = RemoteAccessLogEvent.fromLogString(log);
            assertEquals(event, parsed);
        }
    }
}
