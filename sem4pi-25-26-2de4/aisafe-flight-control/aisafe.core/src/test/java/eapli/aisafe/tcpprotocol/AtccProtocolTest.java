package eapli.aisafe.tcpprotocol;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class AtccProtocolTest {

    @Test
    @DisplayName("AUTH command should split email and password by space")
    void authShouldSplitEmailAndPassword() {
        String raw = "AUTH test@example.com mypassword";
        AtccProtocol.ProtocolMessage msg = AtccProtocol.parse(raw);
        assertEquals("AUTH", msg.command());
        assertEquals(2, msg.params().size());
        assertEquals("test@example.com", msg.params().get(0));
        assertEquals("mypassword", msg.params().get(1));
    }

    @Test
    @DisplayName("AUTH with extra spaces should still extract 2 params")
    void authWithExtraSpaces() {
        String raw = "AUTH  test@example.com  mypassword  ";
        AtccProtocol.ProtocolMessage msg = AtccProtocol.parse(raw);
        assertEquals("AUTH", msg.command());
        assertEquals(2, msg.params().size());
        assertEquals("test@example.com", msg.params().get(0));
        assertEquals("mypassword", msg.params().get(1));
    }

    @Test
    @DisplayName("AUTH with only email should have 1 param")
    void authWithOnlyEmail() {
        AtccProtocol.ProtocolMessage msg = AtccProtocol.parse("AUTH test@example.com");
        assertEquals("AUTH", msg.command());
        assertEquals(1, msg.params().size());
    }

    @Test
    @DisplayName("buildAuth + parse roundtrip should produce 2 params")
    void buildAuthRoundtrip() {
        String built = AtccProtocol.buildAuth("test@example.com", "anypassword");
        assertEquals("AUTH test@example.com anypassword", built);
        AtccProtocol.ProtocolMessage msg = AtccProtocol.parse(built);
        assertTrue(msg.params().size() >= 2,
                "AUTH should have at least 2 params, got: " + msg.params());
        assertEquals("test@example.com", msg.params().get(0));
        assertEquals("anypassword", msg.params().get(1));
    }

    @Test
    @DisplayName("LOGOUT should have 0 params and isLogout true")
    void logoutCommand() {
        AtccProtocol.ProtocolMessage msg = AtccProtocol.parse("LOGOUT");
        assertEquals("LOGOUT", msg.command());
        assertEquals(0, msg.params().size());
        assertTrue(msg.isLogout());
    }

    @Test
    @DisplayName("ADD_AIRCRAFT should split by comma")
    void addAircraftSplitByComma() {
        String raw = "ADD_AIRCRAFT CS-AAA,Boeing737,USA,10,20,150,ACTIVE,2024";
        AtccProtocol.ProtocolMessage msg = AtccProtocol.parse(raw);
        assertEquals("ADD_AIRCRAFT", msg.command());
        assertEquals(8, msg.params().size());
        assertEquals("CS-AAA", msg.params().get(0));
        assertEquals("2024", msg.params().get(7));
    }

    @Test
    @DisplayName("DECOMMISSION_AIRCRAFT should keep payload as single param")
    void decommissionSingleParam() {
        String raw = "DECOMMISSION_AIRCRAFT CS-AAA";
        AtccProtocol.ProtocolMessage msg = AtccProtocol.parse(raw);
        assertEquals("DECOMMISSION_AIRCRAFT", msg.command());
        assertEquals(1, msg.params().size());
        assertEquals("CS-AAA", msg.params().get(0));
    }

    @Test
    @DisplayName("LIST_FLEET no filter should have 0 params")
    void listFleetNoFilter() {
        AtccProtocol.ProtocolMessage msg = AtccProtocol.parse("LIST_FLEET");
        assertEquals("LIST_FLEET", msg.command());
        assertEquals(0, msg.params().size());
    }

    @Test
    @DisplayName("LIST_FLEET MODEL should have 2 params")
    void listFleetModelFilter() {
        String raw = "LIST_FLEET MODEL Boeing737";
        AtccProtocol.ProtocolMessage msg = AtccProtocol.parse(raw);
        assertEquals("LIST_FLEET", msg.command());
        assertEquals(2, msg.params().size());
        assertEquals("MODEL", msg.params().get(0));
        assertEquals("Boeing737", msg.params().get(1));
    }

    @Test
    @DisplayName("CREATE_ROUTE should split by comma into 3 params")
    void createRouteSplitByComma() {
        String raw = "CREATE_ROUTE R001,LISB,LAX";
        AtccProtocol.ProtocolMessage msg = AtccProtocol.parse(raw);
        assertEquals("CREATE_ROUTE", msg.command());
        assertEquals(3, msg.params().size());
        assertEquals("R001", msg.params().get(0));
        assertEquals("LISB", msg.params().get(1));
        assertEquals("LAX", msg.params().get(2));
    }

    @Test
    @DisplayName("Empty/null/blank should return UNKNOWN")
    void emptyLineReturnsUnknown() {
        assertEquals("UNKNOWN", AtccProtocol.parse("").command());
        assertEquals("UNKNOWN", AtccProtocol.parse(null).command());
        assertEquals("UNKNOWN", AtccProtocol.parse("   ").command());
    }

    @Test
    @DisplayName("Command should be case-insensitive")
    void commandCaseInsensitive() {
        AtccProtocol.ProtocolMessage msg = AtccProtocol.parse("auth test@example.com pass");
        assertEquals("AUTH", msg.command());
    }

    @Test
    @DisplayName("isAuth should return true only for AUTH")
    void isAuthCheck() {
        assertTrue(AtccProtocol.parse("AUTH test@example.com pass").isAuth());
        assertFalse(AtccProtocol.parse("ADD_AIRCRAFT CS-AAA").isAuth());
        assertFalse(AtccProtocol.parse("LOGOUT").isAuth());
    }
}
