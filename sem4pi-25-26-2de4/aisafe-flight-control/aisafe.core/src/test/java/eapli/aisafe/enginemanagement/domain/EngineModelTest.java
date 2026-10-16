package eapli.aisafe.enginemanagement.domain;

import eapli.aisafe.generic.domain.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EngineModelTest {

    // ── test data ─────────────────────────────────────────────────────────────

    private static final String    NAME      = "GE 90B";
    private static final String    MAKER     = "GE";
    private static final double    THRUST_SL = 489_000.0;
    private static final double    THRUST_CR = 300_000.0;
    private static final double    MACH_VAL  = 0.84;
    private static final double    TSFC_VAL  = 1.6e-4;
    private static final double    LAPSE_VAL = 1.0;
    private static final FuelType  FUEL      = FuelType.JET_A1;

    private EngineModelId validId() {
        return EngineModelId.of(NAME, MAKER);
    }

    private EngineModel validEngine() {
        return new EngineModel(validId(), EngineType.TURBOFAN, Thrust.of(THRUST_SL), Thrust.of(THRUST_CR), Speed.of(MACH_VAL), TSFC.of(TSFC_VAL), Coefficient.of(LAPSE_VAL), FUEL);
    }

    // ── construction: happy path ──────────────────────────────────────────────

    @Test
    void givenValidData_engineModelIsCreated() {
        final EngineModel engine = validEngine();
        assertEquals(NAME, engine.name());
        assertEquals(MAKER, engine.makerId());
        assertEquals(EngineType.TURBOFAN, engine.engineType());
        assertEquals(FUEL, engine.fuelType());
    }

    @Test
    void givenLapseFactorNearZero_engineModelIsCreated() {
        assertDoesNotThrow(() ->
                new EngineModel(validId(), EngineType.TURBOFAN, Thrust.of(THRUST_SL), Thrust.of(THRUST_CR), Speed.of(MACH_VAL), TSFC.of(TSFC_VAL), Coefficient.of(0.001), FUEL));
    }

    @Test
    void givenAllEngineTypes_engineModelIsCreated() {
        for (final EngineType type : EngineType.values()) {
            assertDoesNotThrow(() ->
                    new EngineModel(validId(), type, Thrust.of(THRUST_SL), Thrust.of(THRUST_CR), Speed.of(MACH_VAL), TSFC.of(TSFC_VAL), Coefficient.of(LAPSE_VAL), FUEL));
        }
    }

    @Test
    void givenAllFuelTypes_engineModelIsCreated() {
        for (final FuelType fuel : FuelType.values()) {
            assertDoesNotThrow(() ->
                    new EngineModel(validId(), EngineType.TURBOFAN, Thrust.of(THRUST_SL), Thrust.of(THRUST_CR), Speed.of(MACH_VAL), TSFC.of(TSFC_VAL), Coefficient.of(LAPSE_VAL), fuel));
        }
    }

    // ── construction: null guards ─────────────────────────────────────────────

    @Test
    void givenNullId_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new EngineModel(null, EngineType.TURBOFAN, Thrust.of(THRUST_SL), Thrust.of(THRUST_CR), Speed.of(MACH_VAL), TSFC.of(TSFC_VAL), Coefficient.of(LAPSE_VAL), FUEL));
    }

    @Test
    void givenNullEngineType_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new EngineModel(validId(), null, Thrust.of(THRUST_SL), Thrust.of(THRUST_CR), Speed.of(MACH_VAL), TSFC.of(TSFC_VAL), Coefficient.of(LAPSE_VAL), FUEL));
    }

    @Test
    void givenNullFuelType_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new EngineModel(validId(), EngineType.TURBOFAN, Thrust.of(THRUST_SL), Thrust.of(THRUST_CR), Speed.of(MACH_VAL), TSFC.of(TSFC_VAL), Coefficient.of(LAPSE_VAL), null));
    }

    // ── construction: thrust invariants ──────────────────────────────────────

    @Test
    void givenZeroThrustAtStandstill_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new EngineModel(validId(), EngineType.TURBOFAN, Thrust.of(0), Thrust.of(THRUST_CR), Speed.of(MACH_VAL), TSFC.of(TSFC_VAL), Coefficient.of(LAPSE_VAL), FUEL));
    }

    @Test
    void givenNegativeThrustAtStandstill_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new EngineModel(validId(), EngineType.TURBOFAN, Thrust.of(-1), Thrust.of(THRUST_CR), Speed.of(MACH_VAL), TSFC.of(TSFC_VAL), Coefficient.of(LAPSE_VAL), FUEL));
    }

    @Test
    void givenZeroThrustAtCruise_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new EngineModel(validId(), EngineType.TURBOFAN, Thrust.of(THRUST_SL), Thrust.of(0), Speed.of(MACH_VAL), TSFC.of(TSFC_VAL), Coefficient.of(LAPSE_VAL), FUEL));
    }

    @Test
    void givenNegativeThrustAtCruise_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new EngineModel(validId(), EngineType.TURBOFAN, Thrust.of(THRUST_SL), Thrust.of(-1), Speed.of(MACH_VAL), TSFC.of(TSFC_VAL), Coefficient.of(LAPSE_VAL), FUEL));
    }

    @Test
    void givenCruiseThrustExceedsStandstillThrust_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new EngineModel(validId(), EngineType.TURBOFAN, Thrust.of(300_000), Thrust.of(400_000), Speed.of(MACH_VAL), TSFC.of(TSFC_VAL), Coefficient.of(LAPSE_VAL), FUEL));
    }

    @Test
    void givenCruiseThrustEqualToStandstillThrust_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new EngineModel(validId(), EngineType.TURBOFAN, Thrust.of(300_000), Thrust.of(300_000), Speed.of(MACH_VAL), TSFC.of(TSFC_VAL), Coefficient.of(LAPSE_VAL), FUEL));
    }

    // ── construction: other parameter invariants ──────────────────────────────

    @Test
    void givenZeroCruiseMach_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new EngineModel(validId(), EngineType.TURBOFAN, Thrust.of(THRUST_SL), Thrust.of(THRUST_CR), Speed.of(0), TSFC.of(TSFC_VAL), Coefficient.of(LAPSE_VAL), FUEL));
    }

    @Test
    void givenNegativeTsfc_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new EngineModel(validId(), EngineType.TURBOFAN, Thrust.of(THRUST_SL), Thrust.of(THRUST_CR), Speed.of(MACH_VAL), TSFC.of(-1e-4), Coefficient.of(LAPSE_VAL), FUEL));
    }

    @Test
    void givenZeroTsfc_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new EngineModel(validId(), EngineType.TURBOFAN, Thrust.of(THRUST_SL), Thrust.of(THRUST_CR), Speed.of(MACH_VAL), TSFC.of(0), Coefficient.of(LAPSE_VAL), FUEL));
    }

    @Test
    void givenNegativeLapseFactor_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new EngineModel(validId(), EngineType.TURBOFAN, Thrust.of(THRUST_SL), Thrust.of(THRUST_CR), Speed.of(MACH_VAL), TSFC.of(TSFC_VAL), Coefficient.of(-0.1), FUEL));
    }

    // ── equality ──────────────────────────────────────────────────────────────

    @Test
    void twoEnginesWithSameId_areEqual() {
        final EngineModel a = validEngine();
        final EngineModel b = new EngineModel(validId(), EngineType.TURBOJET, Thrust.of(500_000), Thrust.of(310_000), Speed.of(0.9), TSFC.of(1.7e-4), Coefficient.of(0.96), FuelType.JET_A);
        assertEquals(a, b);
    }

    @Test
    void twoEnginesWithDifferentId_areNotEqual() {
        final EngineModel a = validEngine();
        final EngineModel b = new EngineModel(EngineModelId.of("RR Trent 970", "RR"), EngineType.TURBOFAN, Thrust.of(348_310), Thrust.of(180_000), Speed.of(0.85), TSFC.of(1.6e-4), Coefficient.of(0.96), FUEL);
        assertNotEquals(a, b);
    }

    @Test
    void engine_isNotEqualToNull() {
        assertNotEquals(null, validEngine());
    }

    @Test
    void equalEngines_haveSameHashCode() {
        final EngineModel a = validEngine();
        final EngineModel b = new EngineModel(validId(), EngineType.TURBOJET, Thrust.of(500_000), Thrust.of(310_000), Speed.of(0.9), TSFC.of(1.7e-4), Coefficient.of(0.96), FuelType.JET_A);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void sameAs_returnsTrueForSameId() {
        final EngineModel a = validEngine();
        final EngineModel b = new EngineModel(validId(), EngineType.TURBOJET, Thrust.of(500_000), Thrust.of(310_000), Speed.of(0.9), TSFC.of(1.7e-4), Coefficient.of(0.96), FuelType.JET_A);
        assertTrue(a.sameAs(b));
    }

    // ── physics: thrustAtAltitude ─────────────────────────────────────────────

    @Test
    void thrustAtSeaLevelDensity_equalsStandstillThrust() {
        final double thrust = validEngine().thrustAtAltitude(1.225);
        assertEquals(THRUST_SL, thrust, 1.0);
    }

    @Test
    void thrustAtHighAltitude_isLessThanAtSeaLevel() {
        final double thrust = validEngine().thrustAtAltitude(0.364);
        assertTrue(thrust < THRUST_SL);
    }

    @Test
    void thrustAtHighAltitude_isPositive() {
        assertTrue(validEngine().thrustAtAltitude(0.364) > 0);
    }

    @Test
    void thrustWithLapseFactorNearZero_thrustDecreasesLess() {
        final EngineModel flat = new EngineModel(validId(), EngineType.TURBOFAN, Thrust.of(THRUST_SL), Thrust.of(THRUST_CR), Speed.of(MACH_VAL), TSFC.of(TSFC_VAL), Coefficient.of(0.001), FUEL);
        // With a very small positive coefficient, thrust at altitude should be close to but less than standstill thrust
        final double thrust = flat.thrustAtAltitude(0.364);
        assertTrue(thrust < THRUST_SL);
        assertTrue(thrust > THRUST_CR);
    }

    // ── toString ──────────────────────────────────────────────────────────────

    @Test
    void toString_containsNameTypeAndFuel() {
        final String str = validEngine().toString();
        assertTrue(str.contains(NAME));
        assertTrue(str.contains("TURBOFAN"));
        assertTrue(str.contains("JET A-1"));
    }
}