package eapli.aisafe.logging;

import eapli.aisafe.logging.model.LogEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

class LogStoreTest {

    private Path tempFile;
    private LogStore logStore;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = Files.createTempFile("test-log", ".log");
        logStore = new LogStore(tempFile.toString());
    }

    @Test
    @DisplayName("Ensure LogStore is created with default max entries")
    void ensureLogStoreCreatedWithDefaultMaxEntries() {
        assertNotNull(logStore);
        assertEquals(0, logStore.size());
    }

    @Test
    @DisplayName("Ensure LogStore stores a raw line")
    void ensureLogStoreStoresRawLine() {
        String rawLine = "2026-05-25T14:30:22|testuser|127.0.0.1|54321|US078|LOGIN_SUCCESS";
        logStore.store(rawLine);
        assertEquals(1, logStore.size());
    }

    @Test
    @DisplayName("Ensure LogStore stores a LogEntry object")
    void ensureLogStoreStoresLogEntry() {
        LogEntry entry = new LogEntry(
            LocalDateTime.of(2026, 5, 25, 14, 30, 22),
            "testuser", "127.0.0.1", 54321, "US078", "LOGIN_SUCCESS");
        logStore.store(entry);
        assertEquals(1, logStore.size());
    }

    @Test
    @DisplayName("Ensure LogStore retrieves all entries")
    void ensureLogStoreRetrievesAllEntries() {
        logStore.store("2026-05-25T14:30:22|user1|127.0.0.1|54321|US078|LOGIN_SUCCESS");
        logStore.store("2026-05-25T14:31:00|user2|10.0.0.1|12345|US044|LOGOUT");
        List<LogEntry> entries = logStore.getEntries();
        assertEquals(2, entries.size());
    }

    @Test
    @DisplayName("Ensure LogStore retrieves last N entries")
    void ensureLogStoreRetrievesLastNEntries() {
        logStore.store("2026-05-25T14:30:22|user1|127.0.0.1|54321|US078|LOGIN_SUCCESS");
        logStore.store("2026-05-25T14:31:00|user2|10.0.0.1|12345|US044|LOGOUT");
        logStore.store("2026-05-25T14:32:00|user3|192.168.1.1|9999|US086|LOGIN_FAILURE");
        List<LogEntry> entries = logStore.getEntries(2);
        assertEquals(2, entries.size());
    }

    @Test
    @DisplayName("Ensure LogStore returns empty list when no entries")
    void ensureLogStoreReturnsEmptyListWhenNoEntries() {
        List<LogEntry> entries = logStore.getEntries();
        assertTrue(entries.isEmpty());
    }

    @Test
    @DisplayName("Ensure LogStore handles invalid raw line gracefully")
    void ensureLogStoreHandlesInvalidRawLine() {
        assertDoesNotThrow(() -> logStore.store("invalid|line"));
    }

    @Test
    @DisplayName("Ensure LogStore writes to file")
    void ensureLogStoreWritesToFile() throws IOException {
        logStore.store("2026-05-25T14:30:22|testuser|127.0.0.1|54321|US078|LOGIN_SUCCESS");
        List<String> lines = Files.readAllLines(tempFile);
        assertFalse(lines.isEmpty());
        assertTrue(lines.get(0).contains("testuser"));
    }

    @Test
    @DisplayName("Ensure LogStore respects max recent entries limit")
    void ensureLogStoreRespectsMaxRecentEntriesLimit() {
        LogStore smallStore = new LogStore(tempFile.toString(), 3);
        smallStore.store("2026-05-25T14:30:22|user1|127.0.0.1|54321|US078|LOGIN_SUCCESS");
        smallStore.store("2026-05-25T14:31:00|user2|10.0.0.1|12345|US044|LOGOUT");
        smallStore.store("2026-05-25T14:32:00|user3|192.168.1.1|9999|US086|LOGIN_FAILURE");
        smallStore.store("2026-05-25T14:33:00|user4|172.16.0.1|5555|US078|LOGIN_SUCCESS");
        assertEquals(3, smallStore.size());
    }

    @Test
    @DisplayName("Ensure LogStore creates directory if not exists")
    void ensureLogStoreCreatesDirectoryIfNotExists() throws IOException {
        Path dir = Files.createTempDirectory("log-test");
        Path file = dir.resolve("subdir").resolve("test.log");
        LogStore store = new LogStore(file.toString());
        assertNotNull(store);
        assertTrue(Files.exists(dir.resolve("subdir")));
    }
}
