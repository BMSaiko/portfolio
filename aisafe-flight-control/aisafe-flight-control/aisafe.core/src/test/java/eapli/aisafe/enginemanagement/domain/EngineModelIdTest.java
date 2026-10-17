package eapli.aisafe.enginemanagement.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EngineModelIdTest {

    // ── factory method validation ─────────────────────────────────────────────

    @Test
    void givenValidNameAndMaker_idIsCreated() {
        final EngineModelId id = EngineModelId.of("GE 90B", "GE");
        assertEquals("GE 90B", id.name());
        assertEquals("GE", id.makerId());
    }

    @Test
    void givenNullName_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                EngineModelId.of(null, "GE"));
    }

    @Test
    void givenBlankName_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                EngineModelId.of("   ", "GE"));
    }

    @Test
    void givenNullMaker_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                EngineModelId.of("GE 90B", null));
    }

    @Test
    void givenBlankMaker_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                EngineModelId.of("GE 90B", ""));
    }

    // ── equality (name is case-insensitive) ───────────────────────────────────

    @Test
    void givenSameNameAndMaker_idsAreEqual() {
        final EngineModelId a = EngineModelId.of("GE 90B", "GE");
        final EngineModelId b = EngineModelId.of("GE 90B", "GE");
        assertEquals(a, b);
    }

    @Test
    void givenNameDifferentCase_idsAreEqual() {
        final EngineModelId a = EngineModelId.of("GE 90B", "GE");
        final EngineModelId b = EngineModelId.of("ge 90b", "GE");
        assertEquals(a, b);
    }

    @Test
    void givenDifferentMaker_idsAreNotEqual() {
        final EngineModelId a = EngineModelId.of("GE 90B", "GE");
        final EngineModelId b = EngineModelId.of("GE 90B", "RR");
        assertNotEquals(a, b);
    }

    @Test
    void givenDifferentName_idsAreNotEqual() {
        final EngineModelId a = EngineModelId.of("GE 90B", "GE");
        final EngineModelId b = EngineModelId.of("GE 90C", "GE");
        assertNotEquals(a, b);
    }

    // ── hashCode consistency with equals ──────────────────────────────────────

    @Test
    void equalIds_haveSameHashCode() {
        final EngineModelId a = EngineModelId.of("GE 90B", "GE");
        final EngineModelId b = EngineModelId.of("ge 90b", "GE");
        assertEquals(a.hashCode(), b.hashCode());
    }

    // ── toString ──────────────────────────────────────────────────────────────

    @Test
    void toString_containsNameAndMaker() {
        final EngineModelId id = EngineModelId.of("GE 90B", "GE");
        assertTrue(id.toString().contains("GE 90B"));
        assertTrue(id.toString().contains("GE"));
    }
}