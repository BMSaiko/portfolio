package eapli.aisafe.routemanagement.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RouteDesignator} value object — US073.
 */
class RouteDesignatorTest {

    // -----------------------------------------------------------------------
    // Valid cases
    // -----------------------------------------------------------------------

    @Test
    void validDesignatorWithTwoDigitsIsAccepted() {
        RouteDesignator d = new RouteDesignator("TP12", "TP");
        assertEquals("TP12", d.value());
    }

    @Test
    void validDesignatorWithOneDigitIsAccepted() {
        assertDoesNotThrow(() -> new RouteDesignator("TP1", "TP"));
    }

    @Test
    void validDesignatorWithThreeDigitsIsAccepted() {
        assertDoesNotThrow(() -> new RouteDesignator("TP123", "TP"));
    }

    @Test
    void validDesignatorWithFourDigitsIsAccepted() {
        assertDoesNotThrow(() -> new RouteDesignator("TP1234", "TP"));
    }

    @Test
    void designatorIsNormalisedToUppercase() {
        RouteDesignator d = new RouteDesignator("tp12", "TP");
        assertEquals("TP12", d.value());
    }

    @Test
    void designatorPrefixMatchIsCaseInsensitive() {
        assertDoesNotThrow(() -> new RouteDesignator("tp10", "tp"));
    }

    @Test
    void toStringReturnsValue() {
        RouteDesignator d = new RouteDesignator("TP123", "TP");
        assertEquals("TP123", d.toString());
    }

    @Test
    void equalDesignatorsAreEqual() {
        RouteDesignator a = new RouteDesignator("TP1", "TP");
        RouteDesignator b = new RouteDesignator("TP1", "TP");
        assertEquals(a, b);
    }

    @Test
    void differentDesignatorsAreNotEqual() {
        RouteDesignator a = new RouteDesignator("TP1", "TP");
        RouteDesignator b = new RouteDesignator("TP2", "TP");
        assertNotEquals(a, b);
    }

    @Test
    void compareToIsConsistentWithValue() {
        RouteDesignator a = new RouteDesignator("TP1", "TP");
        RouteDesignator b = new RouteDesignator("TP2", "TP");
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
        assertEquals(0, a.compareTo(new RouteDesignator("TP1", "TP")));
    }

    // -----------------------------------------------------------------------
    // Invalid format
    // -----------------------------------------------------------------------

    @Test
    void nullDesignatorIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new RouteDesignator(null, "TP"));
    }

    @Test
    void blankDesignatorIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new RouteDesignator("  ", "TP"));
    }

    @Test
    void designatorWithNoDigitsIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new RouteDesignator("TPAB", "TP"));
    }

    @Test
    void designatorWithFiveDigitsIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new RouteDesignator("TP12345", "TP"));
    }

    @Test
    void designatorWithThreeLetterPrefixIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new RouteDesignator("TAP1", "TP"));
    }

    @Test
    void designatorWithOneLetterIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new RouteDesignator("T1", "TP"));
    }

    // -----------------------------------------------------------------------
    // IATA prefix mismatch
    // -----------------------------------------------------------------------

    @Test
    void designatorPrefixMustMatchCompanyIataCode() {
        assertThrows(IllegalArgumentException.class,
                () -> new RouteDesignator("FR100", "TP"));
    }

    @Test
    void nullCompanyIataCodeIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new RouteDesignator("TP1", null));
    }

    @Test
    void blankCompanyIataCodeIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new RouteDesignator("TP1", "  "));
    }
}
