package eapli.aisafe.airportmanagement.domain;

import eapli.aisafe.generic.domain.IcaoCode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IcaoCodeTest {

    @Test
    void ensureValidCodeIsCreated() {
        assertDoesNotThrow(() -> IcaoCode.of("LPPT"));
    }

    @Test
    void ensureNullCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> IcaoCode.of(null));
    }

    @Test
    void ensureEmptyCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> IcaoCode.of(""));
    }

    @Test
    void ensureCodeWithWrongLengthIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> IcaoCode.of("LPP"));
        assertThrows(IllegalArgumentException.class, () -> IcaoCode.of("LPPTT"));
    }

    @Test
    void ensureCodeIsStoredUppercase() {
        assertEquals("LPPT", IcaoCode.of("lppt").value());
    }

    @Test
    void ensureEqualCodesAreEqual() {
        assertEquals(IcaoCode.of("LPPT"), IcaoCode.of("LPPT"));
    }
}

