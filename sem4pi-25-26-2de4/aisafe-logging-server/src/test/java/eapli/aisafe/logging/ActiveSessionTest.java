package eapli.aisafe.logging;

import eapli.aisafe.logging.model.ActiveSession;
import eapli.aisafe.logging.model.LogEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Collection;

class ActiveSessionTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 5, 25, 14, 30, 22);
    private static final String USER = "testuser";
    private static final String IP = "127.0.0.1";
    private static final int PORT = 54321;
    private static final String SERVICE = "US078";

    private ActiveSessionTracker tracker;

    @BeforeEach
    void setUp() {
        tracker = new ActiveSessionTracker();
    }

    @Test
    @DisplayName("Ensure tracker starts with no active sessions")
    void ensureTrackerStartsWithNoActiveSessions() {
        assertEquals(0, tracker.getActiveSessionCount());
        assertTrue(tracker.getActiveSessions().isEmpty());
    }

    @Test
    @DisplayName("Ensure LOGIN_SUCCESS adds a session")
    void ensureLoginSuccessAddsSession() {
        LogEntry entry = new LogEntry(NOW, USER, IP, PORT, SERVICE, "LOGIN_SUCCESS");
        tracker.update(entry);
        assertEquals(1, tracker.getActiveSessionCount());
    }

    @Test
    @DisplayName("Ensure LOGOUT removes the session")
    void ensureLogoutRemovesSession() {
        tracker.update(new LogEntry(NOW, USER, IP, PORT, SERVICE, "LOGIN_SUCCESS"));
        tracker.update(new LogEntry(NOW.plusMinutes(5), USER, IP, PORT, SERVICE, "LOGOUT"));
        assertEquals(0, tracker.getActiveSessionCount());
    }

    @Test
    @DisplayName("Ensure DISCONNECT removes the session")
    void ensureDisconnectRemovesSession() {
        tracker.update(new LogEntry(NOW, USER, IP, PORT, SERVICE, "LOGIN_SUCCESS"));
        tracker.update(new LogEntry(NOW.plusMinutes(5), USER, IP, PORT, SERVICE, "DISCONNECT"));
        assertEquals(0, tracker.getActiveSessionCount());
    }

    @Test
    @DisplayName("Ensure LOGIN_FAILURE removes the session")
    void ensureLoginFailureRemovesSession() {
        tracker.update(new LogEntry(NOW, USER, IP, PORT, SERVICE, "LOGIN_SUCCESS"));
        tracker.update(new LogEntry(NOW.plusMinutes(1), USER, IP, PORT, SERVICE, "LOGIN_FAILURE"));
        assertEquals(0, tracker.getActiveSessionCount());
    }

    @Test
    @DisplayName("Ensure multiple users can have independent sessions")
    void ensureMultipleUsersIndependentSessions() {
        tracker.update(new LogEntry(NOW, "user1", "10.0.0.1", 1234, "US078", "LOGIN_SUCCESS"));
        tracker.update(new LogEntry(NOW, "user2", "10.0.0.2", 5678, "US044", "LOGIN_SUCCESS"));
        tracker.update(new LogEntry(NOW, "user3", "10.0.0.3", 9999, "US086", "LOGIN_SUCCESS"));
        assertEquals(3, tracker.getActiveSessionCount());
    }

    @Test
    @DisplayName("Ensure getActiveSessions returns unmodifiable collection")
    void ensureGetActiveSessionsReturnsUnmodifiable() {
        tracker.update(new LogEntry(NOW, USER, IP, PORT, SERVICE, "LOGIN_SUCCESS"));
        Collection<ActiveSession> sessions = tracker.getActiveSessions();
        assertThrows(UnsupportedOperationException.class, () -> sessions.add(
            new ActiveSession("hack", "192.168.1.1", 1111, "US078", NOW)));
    }

    @Test
    @DisplayName("Ensure same user on different IPs creates separate sessions")
    void ensureSameUserDifferentIPsSeparateSessions() {
        tracker.update(new LogEntry(NOW, USER, "10.0.0.1", 1234, "US078", "LOGIN_SUCCESS"));
        tracker.update(new LogEntry(NOW, USER, "10.0.0.2", 5678, "US044", "LOGIN_SUCCESS"));
        assertEquals(2, tracker.getActiveSessionCount());
    }

    @Test
    @DisplayName("Ensure same user same IP same port replaces session")
    void ensureSameUserSameIPReplacesSession() {
        tracker.update(new LogEntry(NOW, USER, IP, PORT, SERVICE, "LOGIN_SUCCESS"));
        tracker.update(new LogEntry(NOW.plusMinutes(5), USER, IP, PORT, SERVICE, "LOGIN_SUCCESS"));
        assertEquals(1, tracker.getActiveSessionCount());
    }

    @Test
    @DisplayName("Ensure session count is accurate after mixed operations")
    void ensureSessionCountAfterMixedOperations() {
        tracker.update(new LogEntry(NOW, "user1", "10.0.0.1", 1234, "US078", "LOGIN_SUCCESS"));
        tracker.update(new LogEntry(NOW.plusMinutes(1), "user2", "10.0.0.2", 5678, "US044", "LOGIN_SUCCESS"));
        assertEquals(2, tracker.getActiveSessionCount());
        tracker.update(new LogEntry(NOW.plusMinutes(2), "user1", "10.0.0.1", 1234, "US078", "LOGOUT"));
        assertEquals(1, tracker.getActiveSessionCount());
    }

    @Test
    @DisplayName("Ensure session count decrements correctly after logout")
    void ensureCountDecrementsAfterLogout() {
        tracker.update(new LogEntry(NOW, "user1", "10.0.0.1", 1234, "US078", "LOGIN_SUCCESS"));
        tracker.update(new LogEntry(NOW, "user2", "10.0.0.2", 5678, "US044", "LOGIN_SUCCESS"));
        assertEquals(2, tracker.getActiveSessionCount());
        tracker.update(new LogEntry(NOW.plusMinutes(1), "user1", "10.0.0.1", 1234, "US078", "LOGOUT"));
        assertEquals(1, tracker.getActiveSessionCount());
        tracker.update(new LogEntry(NOW.plusMinutes(2), "user2", "10.0.0.2", 5678, "US044", "LOGOUT"));
        assertEquals(0, tracker.getActiveSessionCount());
    }

    @Test
    @DisplayName("Ensure logout of non-existent session does not crash")
    void ensureLogoutNonExistentSessionNoCrash() {
        assertDoesNotThrow(() -> {
            tracker.update(new LogEntry(NOW, "ghost", "10.0.0.99", 1111, "US086", "LOGOUT"));
        });
        assertEquals(0, tracker.getActiveSessionCount());
    }
}
