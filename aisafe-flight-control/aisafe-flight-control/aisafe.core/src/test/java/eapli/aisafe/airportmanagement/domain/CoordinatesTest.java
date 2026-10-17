package eapli.aisafe.airportmanagement.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CoordinatesTest {

    @Test
    void ensureValidCoordinatesAreCreated() {
        assertDoesNotThrow(() -> new Coordinates(38.77, -9.13, 113.0));
    }

    @Test
    void ensureLatitudeAbove90IsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new Coordinates(91.0, -9.13, 113.0));
    }

    @Test
    void ensureLatitudeBelow90IsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new Coordinates(-91.0, -9.13, 113.0));
    }

    @Test
    void ensureLongitudeAbove180IsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new Coordinates(38.77, 181.0, 113.0));
    }

    @Test
    void ensureLongitudeBelow180IsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new Coordinates(38.77, -181.0, 113.0));
    }

    @Test
    void ensureNegativeAltitudeIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new Coordinates(38.77, -9.13, -1.0));
    }
}
