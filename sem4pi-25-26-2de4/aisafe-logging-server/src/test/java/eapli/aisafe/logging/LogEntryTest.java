package eapli.aisafe.logging;

import eapli.aisafe.logging.model.LogEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class LogEntryTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 5, 25, 14, 30, 22);
    private static final String USER = "testuser";
    private static final String IP = "127.0.0.1";
    private static final int PORT = 54321;
    private static final String SERVICE = "US078";
    private static final String EVENT = "LOGIN_SUCCESS";

    @Test
    @DisplayName("Ensure LogEntry is created with valid data")
    void ensureLogEntryIsCreatedWithValidData() {
        LogEntry entry = new LogEntry(NOW, USER, IP, PORT, SERVICE, EVENT);
        assertEquals(NOW, entry.timestamp());
        assertEquals(USER, entry.username());
        assertEquals(IP, entry.clientIpAddress());
        assertEquals(PORT, entry.clientPort());
        assertEquals(SERVICE, entry.serviceType());
        assertEquals(EVENT, entry.eventType());
    }

    @Test
    @DisplayName("Ensure LogEntry serialization roundtrip works")
    void ensureLogEntrySerializationRoundtrip() {
        LogEntry original = new LogEntry(NOW, USER, IP, PORT, SERVICE, EVENT);
        String logString = original.toLogString();
        LogEntry parsed = LogEntry.fromLogString(logString);
        assertEquals(original, parsed);
    }

    @Test
    @DisplayName("Ensure toLogString produces pipe-delimited format")
    void ensureToLogStringProducesPipeDelimitedFormat() {
        LogEntry entry = new LogEntry(NOW, USER, IP, PORT, SERVICE, EVENT);
        String logString = entry.toLogString();
        assertTrue(logString.contains("2026-05-25T14:30:22"));
        assertTrue(logString.contains(USER));
        assertTrue(logString.contains(IP));
        assertTrue(logString.contains(String.valueOf(PORT)));
        assertTrue(logString.contains(SERVICE));
        assertTrue(logString.contains(EVENT));
    }

    @Test
    @DisplayName("Ensure LogEntry JSON output contains all fields")
    void ensureLogEntryJsonContainsAllFields() {
        LogEntry entry = new LogEntry(NOW, USER, IP, PORT, SERVICE, EVENT);
        String json = entry.toJson();
        assertTrue(json.contains(""timestamp""));
        assertTrue(json.contains(""username""));
        assertTrue(json.contains(""clientIp""));
        assertTrue(json.contains(""clientPort""));
        assertTrue(json.contains(""service""));
        assertTrue(json.contains(""event""));
        assertTrue(json.contains(USER));
        assertTrue(json.contains(IP));
    }

    @Test
    @DisplayName("Ensure LogEntry equality works correctly")
    void ensureLogEntryEqualityWorksCorrectly() {
        LogEntry entry1 = new LogEntry(NOW, USER, IP, PORT, SERVICE, EVENT);
        LogEntry entry2 = new LogEntry(NOW, USER, IP, PORT, SERVICE, EVENT);
        assertEquals(entry1, entry2);
        assertEquals(entry1.hashCode(), entry2.hashCode());
    }

    @Test
    @DisplayName("Ensure LogEntry inequality for different timestamps")
    void ensureLogEntryInequalityForDifferentTimestamps() {
        LogEntry entry1 = new LogEntry(NOW, USER, IP, PORT, SERVICE, EVENT);
        LogEntry entry2 = new LogEntry(NOW.plusSeconds(1), USER, IP, PORT, SERVICE, EVENT);
        assertNotEquals(entry1, entry2);
    }

    @Test
    @DisplayName("Ensure LogEntry inequality for different usernames")
    void ensureLogEntryInequalityForDifferentUsernames() {
        LogEntry entry1 = new LogEntry(NOW, USER, IP, PORT, SERVICE, EVENT);
        LogEntry entry2 = new LogEntry(NOW, "otheruser", IP, PORT, SERVICE, EVENT);
        assertNotEquals(entry1, entry2);
    }

    @Test
    @DisplayName("Ensure LogEntry inequality for different IPs")
    void ensureLogEntryInequalityForDifferentIps() {
        LogEntry entry1 = new LogEntry(NOW, USER, IP, PORT, SERVICE, EVENT);
        LogEntry entry2 = new LogEntry(NOW, USER, "10.0.0.1", PORT, SERVICE, EVENT);
        assertNotEquals(entry1, entry2);
    }

    @Test
    @DisplayName("Ensure LogEntry inequality for different ports")
    void ensureLogEntryInequalityForDifferentPorts() {
        LogEntry entry1 = new LogEntry(NOW, USER, IP, PORT, SERVICE, EVENT);
        LogEntry entry2 = new LogEntry(NOW, USER, IP, 12345, SERVICE, EVENT);
        assertNotEquals(entry1, entry2);
    }

    @Test
    @DisplayName("Ensure LogEntry is not equal to null")
    void ensureLogEntryIsNotEqualToNull() {
        LogEntry entry = new LogEntry(NOW, USER, IP, PORT, SERVICE, EVENT);
        assertNotEquals(null, entry);
    }

    @Test
    @DisplayName("Ensure LogEntry is not equal to different type")
    void ensureLogEntryIsNotEqualToDifferentType() {
        LogEntry entry = new LogEntry(NOW, USER, IP, PORT, SERVICE, EVENT);
        assertNotEquals(entry, "not a log entry");
    }

    @Test
    @DisplayName("Ensure LogEntry with special characters in JSON")
    void ensureLogEntryWithSpecialCharactersInJson() {
        LogEntry entry = new LogEntry(NOW, "user\"name", IP, PORT, SERVICE, EVENT);
        String json = entry.toJson();
        assertTrue(json.contains("user\\\"name"));
    }

    @Test
    @DisplayName("Ensure LogEntry serialization with all event types")
    void ensureLogEntrySerializationWithAllEventTypes() {
        String[] eventTypes = {"LOGIN_SUCCESS", "LOGIN_FAILURE", "LOGOUT", "DISCONNECT"};
        for (String eventType : eventTypes) {
            LogEntry entry = new LogEntry(NOW, USER, IP, PORT, SERVICE, eventType);
            String logString = entry.toLogString();
            LogEntry parsed = LogEntry.fromLogString(logString);
            assertEquals(entry, parsed);
        }
    }

    @Test
    @DisplayName("Ensure LogEntry serialization with all service types")
    void ensureLogEntrySerializationWithAllServiceTypes() {
        String[] serviceTypes = {"US044", "US078", "US086"};
        for (String svc : serviceTypes) {
            LogEntry entry = new LogEntry(NOW, USER, IP, PORT, svc, EVENT);
            String logString = entry.toLogString();
            LogEntry parsed = LogEntry.fromLogString(logString);
            assertEquals(entry, parsed);
        }
    }

    @Test
    @DisplayName("Ensure fromLogString rejects invalid format")
    void ensureFromLogStringRejectsInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () ->
            LogEntry.fromLogString("invalid|format"));
    }

    @Test
    @DisplayName("Ensure LogEntry with different service types are not equal")
    void ensureLogEntryDifferentServicesNotEqual() {
        LogEntry entry1 = new LogEntry(NOW, USER, IP, PORT, "US044", EVENT);
        LogEntry entry2 = new LogEntry(NOW, USER, IP, PORT, "US078", EVENT);
        assertNotEquals(entry1, entry2);
    }

    @Test
    @DisplayName("Ensure LogEntry with different event types are not equal")
    void ensureLogEntryDifferentEventsNotEqual() {
        LogEntry entry1 = new LogEntry(NOW, USER, IP, PORT, SERVICE, "LOGIN_SUCCESS");
        LogEntry entry2 = new LogEntry(NOW, USER, IP, PORT, SERVICE, "LOGOUT");
        assertNotEquals(entry1, entry2);
    }
}
