package eapli.aisafe.fleetmanagement.domain;

import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftType;
import eapli.aisafe.enginemanagement.domain.EngineModel;
import eapli.aisafe.enginemanagement.domain.EngineModelId;
import eapli.aisafe.enginemanagement.domain.EngineType;
import eapli.aisafe.enginemanagement.domain.FuelType;
import eapli.aisafe.organization.domain.AirTransportCompany;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import eapli.aisafe.generic.domain.*;

class AircraftTest {

    private AircraftModel realModel;
    private AirTransportCompany realCompany;
    private CabinConfiguration validCabin;

    @BeforeEach
    void setUp() {
        realCompany = new AirTransportCompany("TAP Air Portugal", "TP", "TAP");
        validCabin = new CabinConfiguration(10, 20, 150);

        EngineModelId engineId = EngineModelId.of("GE 90B", "GE");
        EngineModel engine = new EngineModel(engineId, EngineType.TURBOFAN, Thrust.of(489000), Thrust.of(300000), Speed.of(0.84), TSFC.of(0.00016), Coefficient.of(1.0), FuelType.JET_A1);

        AircraftModelId modelId = AircraftModelId.of("777-200ER", "Boeing");
        realModel = new AircraftModel(modelId, AircraftType.PASSENGER, Weight.of(145149), Weight.of(347452), Weight.of(224982), Weight.of(145538), Altitude.of(43100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 13080, Set.of(engine));
    }

    @Test
    void ensureAircraftCreationIsSuccessful() {
        Aircraft aircraft = new Aircraft("CS-TPA", realModel, realCompany, "Portugal", validCabin, OperationalStatus.ACTIVE);
        assertNotNull(aircraft);
        assertEquals("CS-TPA", aircraft.identity());
    }

    @Test
    void ensureAircraftCannotHaveNullRegistration() {
        assertThrows(IllegalArgumentException.class, () ->
                new Aircraft(null, realModel, realCompany, "Portugal", validCabin, OperationalStatus.ACTIVE));
    }

    @Test
    void ensureAircraftCannotHaveEmptyRegistration() {
        assertThrows(IllegalArgumentException.class, () ->
                new Aircraft("   ", realModel, realCompany, "Portugal", validCabin, OperationalStatus.ACTIVE));
    }

    @Test
    void ensureAircraftCannotHaveNullModel() {
        assertThrows(IllegalArgumentException.class, () ->
                new Aircraft("CS-TPA", null, realCompany, "Portugal", validCabin, OperationalStatus.ACTIVE));
    }

    @Test
    void ensureAircraftCannotHaveNullCompany() {
        assertThrows(IllegalArgumentException.class, () ->
                new Aircraft("CS-TPA", realModel, null, "Portugal", validCabin, OperationalStatus.ACTIVE));
    }

    @Test
    void ensureAircraftCannotHaveNullCabinConfiguration() {
        assertThrows(IllegalArgumentException.class, () ->
                new Aircraft("CS-TPA", realModel, realCompany, "Portugal", null, OperationalStatus.ACTIVE));
    }

    @Test
    void ensureAircraftCannotHaveNullStatus() {
        assertThrows(IllegalArgumentException.class, () ->
                new Aircraft("CS-TPA", realModel, realCompany, "Portugal", validCabin, null));
    }

    @Test
    void ensureAircraftCannotExceedModelPassengerCapacity() {
        CabinConfiguration hugeCabin = new CabinConfiguration(100, 100, 201); // 401 lugares num avião de 400
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Aircraft("CS-TPA", realModel, realCompany, "Portugal", hugeCabin, OperationalStatus.ACTIVE));
        assertTrue(exception.getMessage().contains("exceeds the model's maximum capacity"));
    }

    @Test
    void ensureDecommissionChangesStatus() {
        Aircraft aircraft = new Aircraft("CS-TPA", realModel, realCompany, "Portugal", validCabin, OperationalStatus.ACTIVE);
        aircraft.decommission();

        Exception exception = assertThrows(IllegalStateException.class, aircraft::decommission);
        assertTrue(exception.getMessage().toLowerCase().contains("already"));
    }
}