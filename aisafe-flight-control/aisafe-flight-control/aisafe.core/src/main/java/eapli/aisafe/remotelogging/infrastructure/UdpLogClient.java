package eapli.aisafe.remotelogging.infrastructure;

import eapli.aisafe.remotelogging.domain.RemoteAccessLogEvent;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UdpLogClient {

    private static final Logger LOGGER = Logger.getLogger(UdpLogClient.class.getName());
    private static final int MAX_BYTES = 1024;

    private final String serverHost;
    private final int serverPort;
    private DatagramSocket socket;

    public UdpLogClient(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void sendLogEvent(RemoteAccessLogEvent event) {
        try {
            if (socket == null || socket.isClosed()) {
                socket = new DatagramSocket();
            }
            String data = event.toLogString();
            byte[] buf = data.getBytes(StandardCharsets.UTF_8);
            if (buf.length > MAX_BYTES) {
                LOGGER.warning("Log event exceeds " + MAX_BYTES + " bytes, truncating");
                buf = Arrays.copyOf(buf, MAX_BYTES);
            }
            InetAddress address = InetAddress.getByName(serverHost);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, serverPort);
            socket.send(packet);
            LOGGER.fine("Sent log event: " + data);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to send log event", e);
        }
    }

    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
