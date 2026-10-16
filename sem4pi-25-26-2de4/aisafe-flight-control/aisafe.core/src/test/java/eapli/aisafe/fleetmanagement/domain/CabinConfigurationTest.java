package eapli.aisafe.fleetmanagement.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CabinConfigurationTest {

    @Test
    void ensureCabinConfigurationCalculatesTotalSeatsCorrectly() {
        CabinConfiguration cabin = new CabinConfiguration(10, 20, 150);
        assertEquals(180, cabin.totalSeats(), "Total seats should be 180.");
    }

    @Test
    void ensureCabinConfigurationFailsWithNegativeSeats() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CabinConfiguration(-5, 20, 150);
        });
        assertEquals("Seat numbers cannot be negative.", exception.getMessage());
    }

    @Test
    void ensureCabinConfigurationAllowsZeroSeatsInAClass() {
        CabinConfiguration cabin = new CabinConfiguration(0, 0, 100);
        assertEquals(100, cabin.totalSeats(), "Total seats should be 100, allowing 0 in premium classes.");
    }
}