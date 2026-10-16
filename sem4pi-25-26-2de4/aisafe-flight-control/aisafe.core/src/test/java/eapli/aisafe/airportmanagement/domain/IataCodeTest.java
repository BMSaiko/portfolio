package eapli.aisafe.airportmanagement.domain;

import eapli.aisafe.generic.domain.IataCode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IataCodeTest {

    @Test
    void ensureValidCodeIsCreated() {
        assertDoesNotThrow(() -> IataCode.of("LIS"));
    }

    @Test
    void ensureNullCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> IataCode.of(null));
    }

    @Test
    void ensureEmptyCodeIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> IataCode.of(""));
    }

    @Test
    void ensureCodeWithWrongLengthIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> IataCode.of("LI"));
        assertThrows(IllegalArgumentException.class, () -> IataCode.of("LISB"));
    }

    @Test
    void ensureCodeIsStoredUppercase() {
        assertEquals("LIS", IataCode.of("lis").value());
    }

    @Test
    void ensureEqualCodesAreEqual() {
        assertEquals(IataCode.of("LIS"), IataCode.of("LIS"));
    }
}
