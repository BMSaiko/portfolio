package eapli.aisafe.aircontrolmanagement.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GeoBoundaryTest {

    @Test
    void ensureValidBoundaryIsCreated() {
        assertDoesNotThrow(() -> new GeoBoundary(47.0, 36.0, -6.0, -20.0));
    }

    @Test
    void ensureNorthMustBeGreaterThanSouth() {
        assertThrows(IllegalArgumentException.class, () ->
                new GeoBoundary(36.0, 47.0, -6.0, -20.0));
    }

    @Test
    void ensureEastMustBeGreaterThanWest() {
        assertThrows(IllegalArgumentException.class, () ->
                new GeoBoundary(47.0, 36.0, -20.0, -6.0));
    }

    @Test
    void ensureLatitudeOutOfRangeIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new GeoBoundary(91.0, 36.0, -6.0, -20.0));
    }

    @Test
    void ensureLongitudeOutOfRangeIsRejected() {
        assertThrows(IllegalArgumentException.class, () ->
                new GeoBoundary(47.0, 36.0, 181.0, -20.0));
    }

    @Test
    void ensureOverlappingBoundariesAreDetected() {
        GeoBoundary a = new GeoBoundary(47.0, 36.0, -6.0, -20.0);
        GeoBoundary b = new GeoBoundary(45.0, 38.0, -8.0, -18.0);
        assertTrue(a.overlapsWith(b));
    }

    @Test
    void ensureNonOverlappingBoundariesAreNotDetected() {
        GeoBoundary a = new GeoBoundary(47.0, 36.0, -6.0, -10.0);
        GeoBoundary b = new GeoBoundary(60.0, 50.0, -6.0, -10.0);
        assertFalse(a.overlapsWith(b));
    }

    @Test
    void ensureAdjacentBoundariesDoNotOverlap() {
        GeoBoundary a = new GeoBoundary(47.0, 36.0, -10.0, -20.0);
        GeoBoundary b = new GeoBoundary(47.0, 36.0,  -6.0, -10.0);
        assertFalse(a.overlapsWith(b));
    }

    @Test
    void ensurePointInsideBoundaryIsContained() {
        GeoBoundary b = new GeoBoundary(47.0, 36.0, -6.0, -10.0);
        assertTrue(b.contains(41.0, -8.0));
    }

    @Test
    void ensurePointOutsideBoundaryIsNotContained() {
        GeoBoundary b = new GeoBoundary(47.0, 36.0, -6.0, -10.0);
        assertFalse(b.contains(51.5, -0.1));
    }
}
