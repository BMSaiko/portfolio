package eapli.aisafe.remotelogging.infrastructure;

import eapli.aisafe.remotelogging.domain.EventType;
import eapli.aisafe.remotelogging.domain.RemoteAccessLogEvent;
import eapli.aisafe.remotelogging.domain.ServiceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

class UdpLogClientTest {

    @Test
    @DisplayName("Ensure UDP log event is sent and received")
    void ensureUdpEventIsSentAndReceived() throws Exception {
        final int PORT = 19876;
        final AtomicReference<String> received = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(1);

        DatagramSocket receiver = new DatagramSocket(PORT);
        receiver.setSoTimeout(5000);

        Thread receiverThread = new Thread(() -> {
            try {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                receiver.receive(packet);
                received.set(new String(packet.getData(), 0, packet.getLength(), java.nio.charset.StandardCharsets.UTF_8));
                latch.countDown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        receiverThread.start();

        UdpLogClient client = new UdpLogClient("127.0.0.1", PORT);
        RemoteAccessLogEvent event = new RemoteAccessLogEvent(
            LocalDateTime.of(2026, 5, 25, 14, 30, 22),
            "testuser", "127.0.0.1", 54321,
            ServiceType.US078, EventType.LOGIN_SUCCESS);
        client.sendLogEvent(event);

        assertTrue(latch.await(5, TimeUnit.SECONDS), "UDP packet should be received within 5 seconds");
        String expected = event.toLogString();
        assertEquals(expected, received.get());

        client.close();
        receiver.close();
    }

    @Test
    @DisplayName("Ensure multiple events can be sent")
    void ensureMultipleEventsCanBeSent() throws Exception {
        final int PORT = 19877;
        final AtomicReference<String> received1 = new AtomicReference<>();
        final AtomicReference<String> received2 = new AtomicReference<>();
        final CountDownLatch latch = new CountDownLatch(2);

        DatagramSocket receiver = new DatagramSocket(PORT);
        receiver.setSoTimeout(5000);

        Thread receiverThread = new Thread(() -> {
            try {
                for (int i = 0; i < 2; i++) {
                    byte[] buf = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    receiver.receive(packet);
                    String data = new String(packet.getData(), 0, packet.getLength(), java.nio.charset.StandardCharsets.UTF_8);
                    if (i == 0) received1.set(data);
                    else received2.set(data);
                    latch.countDown();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        receiverThread.start();

        UdpLogClient client = new UdpLogClient("127.0.0.1", PORT);

        RemoteAccessLogEvent event1 = new RemoteAccessLogEvent(
            LocalDateTime.of(2026, 5, 25, 14, 30, 22),
            "user1", "127.0.0.1", 54321,
            ServiceType.US078, EventType.LOGIN_SUCCESS);
        client.sendLogEvent(event1);

        RemoteAccessLogEvent event2 = new RemoteAccessLogEvent(
            LocalDateTime.of(2026, 5, 25, 14, 35, 0),
            "user1", "127.0.0.1", 54321,
            ServiceType.US078, EventType.LOGOUT);
        client.sendLogEvent(event2);

        assertTrue(latch.await(5, TimeUnit.SECONDS), "Both UDP packets should be received");
        assertEquals(event1.toLogString(), received1.get());
        assertEquals(event2.toLogString(), received2.get());

        client.close();
        receiver.close();
    }

    @Test
    @DisplayName("Ensure client does not throw on send failure")
    void ensureClientDoesNotThrowOnFailure() {
        UdpLogClient client = new UdpLogClient("240.0.0.1", 19999);
        RemoteAccessLogEvent event = new RemoteAccessLogEvent(
            LocalDateTime.now(), "user", "127.0.0.1", 54321,
            ServiceType.US044, EventType.LOGIN_FAILURE);
        assertDoesNotThrow(() -> client.sendLogEvent(event));
        client.close();
    }

    @Test
    @DisplayName("Ensure close is idempotent")
    void ensureCloseIsIdempotent() {
        UdpLogClient client = new UdpLogClient("127.0.0.1", 19998);
        assertDoesNotThrow(() -> {
            client.close();
            client.close();
        });
    }
}
