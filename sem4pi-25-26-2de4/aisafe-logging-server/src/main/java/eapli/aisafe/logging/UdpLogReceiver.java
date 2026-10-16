package eapli.aisafe.logging;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UdpLogReceiver implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(UdpLogReceiver.class.getName());
    private static final int BUFFER_SIZE = 4096;

    private final int port;
    private final LogStore logStore;
    private final ActiveSessionTracker sessionTracker;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private DatagramSocket socket;

    public UdpLogReceiver(int port, LogStore logStore, ActiveSessionTracker sessionTracker) {
        this.port = port;
        this.logStore = logStore;
        this.sessionTracker = sessionTracker;
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(port);
            running.set(true);
            LOGGER.info("[UDP Receiver] Listening on port " + port);

            byte[] buffer = new byte[BUFFER_SIZE];

            while (running.get()) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);
                    String data = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                    data = data.trim();
                    if (!data.isEmpty()) {
                        processEvent(data);
                    }
                } catch (IOException e) {
                    if (running.get()) {
                        LOGGER.log(Level.WARNING, "[UDP Receiver] Error receiving packet", e);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "[UDP Receiver] Failed to bind to port " + port, e);
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    private void processEvent(String rawData) {
        try {
            logStore.store(rawData);
            sessionTracker.update(logStore.getEntries(1).get(0));
            LOGGER.fine("[UDP Receiver] Stored event: " + rawData);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "[UDP Receiver] Failed to process event: " + rawData, e);
        }
    }

    public void stop() {
        running.set(false);
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public boolean isRunning() {
        return running.get();
    }
}
