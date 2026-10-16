package eapli.aisafe.aircraftmodelmanagement.domain;

import eapli.aisafe.enginemanagement.domain.EngineModel;
import eapli.aisafe.enginemanagement.domain.EngineModelId;
import eapli.aisafe.enginemanagement.domain.EngineType;
import eapli.aisafe.enginemanagement.domain.FuelType;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import eapli.aisafe.generic.domain.*;

class AircraftModelTest {

    // ── test data ─────────────────────────────────────────────────────────────

    private AircraftModelId validId() {
        return AircraftModelId.of("777-200ER", "Boeing");
    }

    private EngineModel turbofanEngine(final String name, final String maker) {
        return new EngineModel(EngineModelId.of(name, maker), EngineType.TURBOFAN, Thrust.of(489_000), Thrust.of(300_000), Speed.of(0.84), TSFC.of(1.6e-4), Coefficient.of(1.0), FuelType.JET_A1);
    }

    private EngineModel turbopropEngine(final String name, final String maker) {
        return new EngineModel(EngineModelId.of(name, maker), EngineType.TURBOPROP, Thrust.of(100_000), Thrust.of(60_000), Speed.of(0.5), TSFC.of(1.8e-4), Coefficient.of(1.0), FuelType.JET_A1);
    }

    private Set<EngineModel> singleTurbofan() {
        final Set<EngineModel> engines = new HashSet<>();
        engines.add(turbofanEngine("GE 90B", "GE"));
        return engines;
    }

    private AircraftModel validAircraftModel() {
        return new AircraftModel(validId(), AircraftType.PASSENGER, Weight.of(145_149), Weight.of(347_452), Weight.of(224_982), Weight.of(145_538), Altitude.of(43_100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 13_080, singleTurbofan());
    }

    // ── construction: happy path ──────────────────────────────────────────────

    @Test
    void givenValidData_aircraftModelIsCreated() {
        final AircraftModel model = validAircraftModel();
        assertEquals("777-200ER", model.modelId());
        assertEquals("Boeing", model.makerName());
        assertEquals(AircraftType.PASSENGER, model.aircraftType());
        assertEquals(2, model.numberOfEngines());
        assertEquals(1, model.certifiedEngines().size());
    }

    @Test
    void givenMultipleEnginesOfSameType_aircraftModelIsCreated() {
        final Set<EngineModel> engines = new HashSet<>();
        engines.add(turbofanEngine("GE 90B", "GE"));
        engines.add(turbofanEngine("RR Trent 970", "RR"));
        assertDoesNotThrow(() -> new AircraftModel(validId(), AircraftType.PASSENGER, Weight.of(145_149), Weight.of(347_452), Weight.of(224_982), Weight.of(145_538), Altitude.of(43_100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 13_080, engines));
    }

    @Test
    void givenZeroMaxPassengerCapacity_isValid() {
        assertDoesNotThrow(() -> new AircraftModel(validId(), AircraftType.CARGO, Weight.of(145_149), Weight.of(347_452), Weight.of(224_982), Weight.of(145_538), Altitude.of(43_100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 0, 13_080, singleTurbofan()));
    }

    // ── construction: null guards ─────────────────────────────────────────────

    @Test
    void givenNullId_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new AircraftModel(null, AircraftType.PASSENGER, Weight.of(145_149), Weight.of(347_452), Weight.of(224_982), Weight.of(145_538), Altitude.of(43_100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 13_080, singleTurbofan()));
    }

    @Test
    void givenNullAircraftType_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new AircraftModel(validId(), null, Weight.of(145_149), Weight.of(347_452), Weight.of(224_982), Weight.of(145_538), Altitude.of(43_100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 13_080, singleTurbofan()));
    }

    @Test
    void givenNullCertifiedEngines_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new AircraftModel(validId(), AircraftType.PASSENGER, Weight.of(145_149), Weight.of(347_452), Weight.of(224_982), Weight.of(145_538), Altitude.of(43_100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 13_080, null));
    }

    @Test
    void givenEmptyCertifiedEngines_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new AircraftModel(validId(), AircraftType.PASSENGER, Weight.of(145_149), Weight.of(347_452), Weight.of(224_982), Weight.of(145_538), Altitude.of(43_100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 13_080, new HashSet<>()));
    }

    // ── construction: weight invariants ───────────────────────────────────────

    @Test
    void givenZeroEmptyWeight_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new AircraftModel(validId(), AircraftType.PASSENGER, Weight.of(0), Weight.of(347_452), Weight.of(224_982), Weight.of(145_538), Altitude.of(43_100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 13_080, singleTurbofan()));
    }

    @Test
    void givenMtowLessThanEmptyWeight_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new AircraftModel(validId(), AircraftType.PASSENGER, Weight.of(347_452), Weight.of(145_149), Weight.of(224_982), Weight.of(145_538), Altitude.of(43_100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 13_080, singleTurbofan()));
    }

    @Test
    void givenMzfwExceedsMtow_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new AircraftModel(validId(), AircraftType.PASSENGER, Weight.of(145_149), Weight.of(347_452), Weight.of(400_000), Weight.of(145_538), Altitude.of(43_100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 13_080, singleTurbofan()));
    }

    // ── construction: aerodynamic invariants ──────────────────────────────────

    @Test
    void givenOswaldFactorAboveOne_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new AircraftModel(validId(), AircraftType.PASSENGER, Weight.of(145_149), Weight.of(347_452), Weight.of(224_982), Weight.of(145_538), Altitude.of(43_100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(1.1), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 13_080, singleTurbofan()));
    }

    @Test
    void givenOswaldFactorZero_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new AircraftModel(validId(), AircraftType.PASSENGER, Weight.of(145_149), Weight.of(347_452), Weight.of(224_982), Weight.of(145_538), Altitude.of(43_100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.0), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 13_080, singleTurbofan()));
    }

    @Test
    void givenNegativeWingArea_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new AircraftModel(validId(), AircraftType.PASSENGER, Weight.of(145_149), Weight.of(347_452), Weight.of(224_982), Weight.of(145_538), Altitude.of(43_100), Speed.of(0.84), Area.of(-440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 13_080, singleTurbofan()));
    }

    // ── construction: engine invariants ───────────────────────────────────────

    @Test
    void givenEnginesOfDifferentTypes_throwsIllegalArgument() {
        final Set<EngineModel> mixedEngines = new HashSet<>();
        mixedEngines.add(turbofanEngine("GE 90B", "GE"));
        mixedEngines.add(turbopropEngine("SomeProp", "Maker"));

        assertThrows(IllegalArgumentException.class, () ->
                new AircraftModel(validId(), AircraftType.PASSENGER, Weight.of(145_149), Weight.of(347_452), Weight.of(224_982), Weight.of(145_538), Altitude.of(43_100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 13_080, mixedEngines));
    }

    @Test
    void givenNumberOfEnginesZero_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new AircraftModel(validId(), AircraftType.PASSENGER, Weight.of(145_149), Weight.of(347_452), Weight.of(224_982), Weight.of(145_538), Altitude.of(43_100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 0, 400, 13_080, singleTurbofan()));
    }

    // ── construction: other invariants ────────────────────────────────────────

    @Test
    void givenNegativeMaxPassengerCapacity_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new AircraftModel(validId(), AircraftType.PASSENGER, Weight.of(145_149), Weight.of(347_452), Weight.of(224_982), Weight.of(145_538), Altitude.of(43_100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, -1, 13_080, singleTurbofan()));
    }

    @Test
    void givenZeroMaxRange_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new AircraftModel(validId(), AircraftType.PASSENGER, Weight.of(145_149), Weight.of(347_452), Weight.of(224_982), Weight.of(145_538), Altitude.of(43_100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 0, singleTurbofan()));
    }

    // ── equality ──────────────────────────────────────────────────────────────

    @Test
    void twoModelsWithSameId_areEqual() {
        final AircraftModel a = validAircraftModel();
        final AircraftModel b = new AircraftModel(validId(), AircraftType.CARGO, Weight.of(145_149), Weight.of(347_452), Weight.of(224_982), Weight.of(145_538), Altitude.of(43_100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 0, 13_080, singleTurbofan());
        assertEquals(a, b);
    }

    @Test
    void twoModelsWithDifferentId_areNotEqual() {
        final AircraftModel a = validAircraftModel();
        final AircraftModel b = new AircraftModel(AircraftModelId.of("A380", "Airbus"), AircraftType.PASSENGER, Weight.of(276_800), Weight.of(575_000), Weight.of(361_000), Weight.of(256_000), Altitude.of(43_000), Speed.of(0.85), Area.of(845), 79.75, 7.53, Coefficient.of(0.84), Coefficient.of(0.020), Coefficient.of(0.45), 4, 555, 15_200, singleTurbofan());
        assertNotEquals(a, b);
    }

    @Test
    void model_isNotEqualToNull() {
        assertNotEquals(null, validAircraftModel());
    }

    @Test
    void equalModels_haveSameHashCode() {
        final AircraftModel a = validAircraftModel();
        final AircraftModel b = new AircraftModel(validId(), AircraftType.CARGO, Weight.of(145_149), Weight.of(347_452), Weight.of(224_982), Weight.of(145_538), Altitude.of(43_100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 0, 13_080, singleTurbofan());
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void sameAs_returnsTrueForSameId() {
        final AircraftModel a = validAircraftModel();
        final AircraftModel b = new AircraftModel(validId(), AircraftType.CARGO, Weight.of(145_149), Weight.of(347_452), Weight.of(224_982), Weight.of(145_538), Altitude.of(43_100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 0, 13_080, singleTurbofan());
        assertTrue(a.sameAs(b));
    }

    @Test
    void sameAs_returnsFalseForNonAircraftModel() {
        assertFalse(validAircraftModel().sameAs("not an aircraft model"));
    }

    // ── certifiedEngines immutability ─────────────────────────────────────────

    @Test
    void certifiedEngines_returnsUnmodifiableSet() {
        final AircraftModel model = validAircraftModel();
        assertThrows(UnsupportedOperationException.class, () ->
                model.certifiedEngines().add(turbofanEngine("Extra", "Maker")));
    }

    // ── toString ──────────────────────────────────────────────────────────────

    @Test
    void toString_containsModelIdAndType() {
        final String str = validAircraftModel().toString();
        assertTrue(str.contains("777-200ER"));
        assertTrue(str.contains("PASSENGER"));
    }

    // ── US057: addCertifiedEngine ─────────────────────────────────────────────

    @Test
    void addCertifiedEngine_withCompatibleType_succeeds() {
        final AircraftModel model = validAircraftModel();
        final EngineModel extra = turbofanEngine("RR Trent 970", "RR");
        model.addCertifiedEngine(extra);
        assertEquals(2, model.certifiedEngines().size());
        assertTrue(model.certifiedEngines().contains(extra));
    }

    @Test
    void addCertifiedEngine_withIncompatibleType_throwsIllegalArgument() {
        final AircraftModel model = validAircraftModel();
        final EngineModel turboprop = turbopropEngine("SomeProp", "Maker");
        assertThrows(IllegalArgumentException.class,
                () -> model.addCertifiedEngine(turboprop));
    }

    @Test
    void addCertifiedEngine_duplicate_throwsIllegalState() {
        final AircraftModel model = validAircraftModel();
        final EngineModel duplicate = turbofanEngine("GE 90B", "GE");
        assertThrows(IllegalStateException.class,
                () -> model.addCertifiedEngine(duplicate));
    }

    @Test
    void addCertifiedEngine_null_throwsIllegalArgument() {
        final AircraftModel model = validAircraftModel();
        assertThrows(IllegalArgumentException.class,
                () -> model.addCertifiedEngine(null));
    }

    @Test
    void addCertifiedEngine_doesNotAffectOtherInstances() {
        final AircraftModel a = validAircraftModel();
        final AircraftModel b = validAircraftModel();
        a.addCertifiedEngine(turbofanEngine("RR Trent 970", "RR"));
        assertEquals(1, b.certifiedEngines().size());
    }
}