package eapli.aisafe.logging;

import eapli.aisafe.logging.model.LogEntry;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogStore {

    private static final Logger LOGGER = Logger.getLogger(LogStore.class.getName());
    private static final DateTimeFormatter FMT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final String logFilePath;
    private final CopyOnWriteArrayList<LogEntry> recentEntries;
    private final int maxRecentEntries;
    private final ReentrantLock fileLock = new ReentrantLock();

    public LogStore(String logFilePath, int maxRecentEntries) {
        this.logFilePath = logFilePath;
        this.maxRecentEntries = maxRecentEntries;
        this.recentEntries = new CopyOnWriteArrayList<>();
        ensureLogDirectoryExists();
    }

    public LogStore(String logFilePath) {
        this(logFilePath, 500);
    }

    private void ensureLogDirectoryExists() {
        try {
            Path path = Paths.get(logFilePath);
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to create log directory", e);
        }
    }

    public void store(String rawLine) {
        try {
            LogEntry entry = LogEntry.fromLogString(rawLine);
            store(entry);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to parse log entry: " + rawLine, e);
        }
    }

    public void store(LogEntry entry) {
        fileLock.lock();
        try {
            writeToFile(entry);
            addToRecent(entry);
        } finally {
            fileLock.unlock();
        }
    }

    private void writeToFile(LogEntry entry) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(logFilePath, true), StandardCharsets.UTF_8))) {
            writer.write(entry.toLogString());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to write log entry to file", e);
        }
    }

    private void addToRecent(LogEntry entry) {
        recentEntries.add(entry);
        while (recentEntries.size() > maxRecentEntries) {
            recentEntries.remove(0);
        }
    }

    public List<LogEntry> getEntries() {
        return new ArrayList<>(recentEntries);
    }

    public List<LogEntry> getEntries(int count) {
        int size = recentEntries.size();
        int fromIndex = Math.max(0, size - count);
        return new ArrayList<>(recentEntries.subList(fromIndex, size));
    }

    public int size() {
        return recentEntries.size();
    }
}
