package eapli.aisafe.app.server;

import eapli.aisafe.tcpprotocol.AtccProtocol;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

class AtccConnectionHandlerTest {

    private Socket[] createConnectedSockets() throws IOException {
        ServerSocket ss = new ServerSocket(0);
        int port = ss.getLocalPort();
        Socket client = new Socket("127.0.0.1", port);
        Socket server = ss.accept();
        ss.close();
        return new Socket[]{server, client};
    }

    @Test
    @DisplayName("AUTH with null authService rejects login (fail-closed)")
    void authNullServiceRejects() throws Exception {
        Socket[] s = createConnectedSockets();
        Thread t = new Thread(new AtccConnectionHandler(s[0]));
        t.start();

        BufferedReader r = new BufferedReader(new InputStreamReader(s[1].getInputStream(), StandardCharsets.UTF_8));
        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s[1].getOutputStream(), StandardCharsets.UTF_8));

        w.write("AUTH test@example.com anypassword");
        w.newLine();
        w.flush();

        String resp = r.readLine();
        assertNotNull(resp);
        assertTrue(resp.startsWith("AUTH_FAIL"), "Expected AUTH_FAIL with null authService, got: " + resp);

        s[1].close();
        t.join(2000);
    }

    @Test
    @DisplayName("AUTH with no params returns AUTH_FAIL")
    void authNoParamsFails() throws Exception {
        Socket[] s = createConnectedSockets();
        Thread t = new Thread(new AtccConnectionHandler(s[0]));
        t.start();

        BufferedReader r = new BufferedReader(new InputStreamReader(s[1].getInputStream(), StandardCharsets.UTF_8));
        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s[1].getOutputStream(), StandardCharsets.UTF_8));

        w.write("AUTH");
        w.newLine();
        w.flush();

        assertEquals("AUTH_FAIL", r.readLine());

        s[1].close();
        t.join(2000);
    }

    @Test
    @DisplayName("AUTH with only email returns AUTH_FAIL")
    void authOnlyEmailFails() throws Exception {
        Socket[] s = createConnectedSockets();
        Thread t = new Thread(new AtccConnectionHandler(s[0]));
        t.start();

        BufferedReader r = new BufferedReader(new InputStreamReader(s[1].getInputStream(), StandardCharsets.UTF_8));
        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s[1].getOutputStream(), StandardCharsets.UTF_8));

        w.write("AUTH test@example.com");
        w.newLine();
        w.flush();

        assertEquals("AUTH_FAIL", r.readLine());

        s[1].close();
        t.join(2000);
    }

    @Test
    @DisplayName("buildAuth roundtrip: output parses to >= 2 params")
    void buildAuthRoundtrip() {
        String built = AtccProtocol.buildAuth("user@test.com", "pass123");
        AtccProtocol.ProtocolMessage msg = AtccProtocol.parse(built);

        assertTrue(msg.params().size() >= 2,
                "CRITICAL: handleAuth needs params.size() >= 2. Got: " + msg.params());
        assertEquals("user@test.com", msg.params().get(0));
        assertEquals("pass123", msg.params().get(1));
    }

    @Test
    @DisplayName("Full roundtrip: buildAuth -> parse -> verify email+password")
    void fullRoundtrip() {
        String email = "atcc@airline.com";
        String pass = "Atcc1234";
        String cmd = AtccProtocol.buildAuth(email, pass);
        AtccProtocol.ProtocolMessage msg = AtccProtocol.parse(cmd);

        assertEquals("AUTH", msg.command());
        assertTrue(msg.params().size() >= 2,
                "params.size() must be >= 2. Got: " + msg.params());
        assertEquals(email, msg.params().get(0));
        assertEquals(pass, msg.params().get(1));
    }

    @Test
    @DisplayName("AUTH with blank email returns AUTH_FAIL")
    void authBlankEmailFails() throws Exception {
        Socket[] s = createConnectedSockets();
        Thread t = new Thread(new AtccConnectionHandler(s[0]));
        t.start();

        BufferedReader r = new BufferedReader(new InputStreamReader(s[1].getInputStream(), StandardCharsets.UTF_8));
        BufferedWriter w = new BufferedWriter(new OutputStreamWriter(s[1].getOutputStream(), StandardCharsets.UTF_8));

        w.write("AUTH           password123");
        w.newLine();
        w.flush();

        assertEquals("AUTH_FAIL", r.readLine());

        s[1].close();
        t.join(2000);
    }
}
