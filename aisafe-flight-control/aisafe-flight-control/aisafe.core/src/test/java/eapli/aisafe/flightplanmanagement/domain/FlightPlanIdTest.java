package eapli.aisafe.flightplanmanagement.domain;

import org.junit.Test;
import static org.junit.Assert.*;

public class FlightPlanIdTest {

    @Test(expected = IllegalArgumentException.class)
    public void testFlightPlanIdNull() {
        FlightPlanId.of(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFlightPlanIdEmpty() {
        FlightPlanId.of("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFlightPlanIdBlank() {
        FlightPlanId.of("   ");
    }

    @Test
    public void testFlightPlanIdValid() {
        final FlightPlanId id = FlightPlanId.of("FP-001");
        assertEquals("FP-001", id.value());
    }

    @Test
    public void testFlightPlanIdTrim() {
        final FlightPlanId id = FlightPlanId.of("  FP-001  ");
        assertEquals("FP-001", id.value());
    }

    @Test
    public void testFlightPlanIdEquals() {
        final FlightPlanId id1 = FlightPlanId.of("FP-001");
        final FlightPlanId id2 = FlightPlanId.of("FP-001");
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }

    @Test
    public void testFlightPlanIdNotEquals() {
        final FlightPlanId id1 = FlightPlanId.of("FP-001");
        final FlightPlanId id2 = FlightPlanId.of("FP-002");
        assertNotEquals(id1, id2);
    }

    @Test
    public void testFlightPlanIdToString() {
        final FlightPlanId id = FlightPlanId.of("FP-001");
        assertEquals("FP-001", id.toString());
    }
}
