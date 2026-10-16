package eapli.aisafe.aircraftmodelmanagement.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AircraftModelIdTest {

    // ── factory method validation ─────────────────────────────────────────────

    @Test
    void givenValidModelIdAndMaker_idIsCreated() {
        final AircraftModelId id = AircraftModelId.of("777-200ER", "Boeing");
        assertEquals("777-200ER", id.modelId());
        assertEquals("Boeing", id.makerName());
    }

    @Test
    void givenNullModelId_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                AircraftModelId.of(null, "Boeing"));
    }

    @Test
    void givenBlankModelId_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                AircraftModelId.of("   ", "Boeing"));
    }

    @Test
    void givenNullMaker_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                AircraftModelId.of("777-200ER", null));
    }

    @Test
    void givenBlankMaker_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                AircraftModelId.of("777-200ER", ""));
    }

    // ── equality (case-insensitive) ───────────────────────────────────────────

    @Test
    void givenSameModelIdAndMaker_idsAreEqual() {
        final AircraftModelId a = AircraftModelId.of("777-200ER", "Boeing");
        final AircraftModelId b = AircraftModelId.of("777-200ER", "Boeing");
        assertEquals(a, b);
    }

    @Test
    void givenModelIdDifferentCase_idsAreEqual() {
        final AircraftModelId a = AircraftModelId.of("777-200ER", "Boeing");
        final AircraftModelId b = AircraftModelId.of("777-200er", "boeing");
        assertEquals(a, b);
    }

    @Test
    void givenDifferentModelId_idsAreNotEqual() {
        final AircraftModelId a = AircraftModelId.of("777-200ER", "Boeing");
        final AircraftModelId b = AircraftModelId.of("A380", "Boeing");
        assertNotEquals(a, b);
    }

    @Test
    void givenDifferentMaker_idsAreNotEqual() {
        final AircraftModelId a = AircraftModelId.of("777-200ER", "Boeing");
        final AircraftModelId b = AircraftModelId.of("777-200ER", "Airbus");
        assertNotEquals(a, b);
    }

    // ── hashCode consistency ──────────────────────────────────────────────────

    @Test
    void equalIds_haveSameHashCode() {
        final AircraftModelId a = AircraftModelId.of("777-200ER", "Boeing");
        final AircraftModelId b = AircraftModelId.of("777-200er", "boeing");
        assertEquals(a.hashCode(), b.hashCode());
    }

    // ── toString ──────────────────────────────────────────────────────────────

    @Test
    void toString_containsModelIdAndMaker() {
        final AircraftModelId id = AircraftModelId.of("777-200ER", "Boeing");
        assertTrue(id.toString().contains("777-200ER"));
        assertTrue(id.toString().contains("Boeing"));
    }
}