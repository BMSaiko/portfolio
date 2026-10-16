package eapli.aisafe.usermanagement.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void validEmailShouldBeAccepted() {
        assertDoesNotThrow(() -> new Email("user@domain.com"));
        assertDoesNotThrow(() -> new Email("user.name+tag@domain.co.uk"));
    }

    @Test
    void invalidEmailShouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> new Email("invalid"));
        assertThrows(IllegalArgumentException.class, () -> new Email("user@"));
        assertThrows(IllegalArgumentException.class, () -> new Email("user@domain."));
        assertThrows(IllegalArgumentException.class, () -> new Email(null));
    }

    @Test
    void equalsAndHashCode() {
        Email e1 = new Email("a@b.com");
        Email e2 = new Email("a@b.com");
        Email e3 = new Email("c@d.com");
        assertEquals(e1, e2);
        assertNotEquals(e1, e3);
        assertEquals(e1.hashCode(), e2.hashCode());
    }

    @Test
    void toStringReturnsAddress() {
        Email e = new Email("test@example.com");
        assertEquals("test@example.com", e.toString());
    }
}