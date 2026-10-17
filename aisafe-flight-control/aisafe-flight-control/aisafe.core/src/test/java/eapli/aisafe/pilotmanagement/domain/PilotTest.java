package eapli.aisafe.pilotmanagement.domain;

import eapli.aisafe.aircraftmodelmanagement.domain.*;
import eapli.aisafe.enginemanagement.domain.*;
import eapli.aisafe.generic.domain.*;
import eapli.aisafe.organization.domain.*;
import eapli.aisafe.usermanagement.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PilotTest {

    private User validUser;
    private Email validEmail;
    private AirTransportCompany validCompany;
    private Set<AircraftModel> validCertifications;

    @BeforeEach
    void setUp() {
        // 1. Criar o Email
        validEmail = new Email("joao.silva@tap.pt");

        // 2. Criar o User (Ajustado com os Value Objects corretos)
        Set<Role> roles = new HashSet<>();
        roles.add(Role.PILOT);
        validUser = new User(validEmail, "Password123!", UserName.of("João Silva"), PhoneNumber.of("912345678"), roles);

        // 3. Criar a AirTransportCompany real
        validCompany = new AirTransportCompany("TAP Air Portugal", "TP", "TAP");

        // 4. Criar a lista de certificações com um avião válido real
        validCertifications = new HashSet<>();
        validCertifications.add(createValidAircraftModel());
    }

    @Test
    void ensurePilotIsCreatedWithValidDataAndIsActive() {
        Pilot pilot = new Pilot(validUser, validEmail, "João Silva", "Captain", validCompany, validCertifications);

        assertNotNull(pilot);
        assertEquals(validEmail, pilot.email(), "O email guardado deve ser igual ao passado no construtor");
        assertEquals(validCompany, pilot.company(), "A companhia guardada deve ser igual à passada no construtor");
        assertTrue(pilot.isActive(), "Um piloto acabado de criar deve estar sempre ATIVO");
    }

    @Test
    void ensurePilotCannotBeCreatedWithNullUser() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Pilot(null, validEmail, "João Silva", "Captain", validCompany, validCertifications);
        });
        assertEquals("Pilot attributes cannot be null or empty.", exception.getMessage());
    }

    @Test
    void ensurePilotCannotBeCreatedWithNullEmail() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Pilot(validUser, null, "João Silva", "Captain", validCompany, validCertifications);
        });
        assertEquals("Pilot attributes cannot be null or empty.", exception.getMessage());
    }

    @Test
    void ensurePilotCannotBeCreatedWithNullOrEmptyName() {
        // Testar com Null
        assertThrows(IllegalArgumentException.class, () -> {
            new Pilot(validUser, validEmail, null, "Captain", validCompany, validCertifications);
        });

        // Testar com Vazio/Branco
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Pilot(validUser, validEmail, "   ", "Captain", validCompany, validCertifications);
        });
        assertEquals("Pilot attributes cannot be null or empty.", exception.getMessage());
    }

    @Test
    void ensurePilotCannotBeCreatedWithNullOrEmptyPosition() {
        // Testar com Null
        assertThrows(IllegalArgumentException.class, () -> {
            new Pilot(validUser, validEmail, "João Silva", null, validCompany, validCertifications);
        });

        // Testar com Vazio
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Pilot(validUser, validEmail, "João Silva", "", validCompany, validCertifications);
        });
        assertEquals("Pilot attributes cannot be null or empty.", exception.getMessage());
    }

    @Test
    void ensurePilotCannotBeCreatedWithNullCompany() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Pilot(validUser, validEmail, "João Silva", "Captain", null, validCertifications);
        });
        assertEquals("Pilot attributes cannot be null or empty.", exception.getMessage());
    }

    @Test
    void ensurePilotCannotBeCreatedWithoutCertifications() {
        // Testar com lista a NULL
        Exception exNull = assertThrows(IllegalArgumentException.class, () -> {
            new Pilot(validUser, validEmail, "João Silva", "Captain", validCompany, null);
        });
        assertEquals("A pilot must be certified for at least one aircraft model.", exNull.getMessage());

        // Testar com lista VAZIA
        Exception exEmpty = assertThrows(IllegalArgumentException.class, () -> {
            new Pilot(validUser, validEmail, "João Silva", "Captain", validCompany, new HashSet<>());
        });
        assertEquals("A pilot must be certified for at least one aircraft model.", exEmpty.getMessage());
    }

    @Test
    void ensureDeactivatePilotChangesStatusToInactive() {
        Pilot pilot = new Pilot(validUser, validEmail, "João Silva", "Captain", validCompany, validCertifications);
        assertTrue(pilot.isActive(), "O piloto devia estar ativo inicialmente");

        // Aciona o método da tua US077
        pilot.deactivate();

        // Verifica
        assertFalse(pilot.isActive(), "O piloto devia estar inativo após o deactivate()");
    }

    // =====================================================================================
    // MÉTODO AUXILIAR PARA CONSTRUIR UM AIRCRAFT MODEL (100% REAL)
    // =====================================================================================
    private AircraftModel createValidAircraftModel() {
        // 1. Instanciar o Motor
        EngineModelId engineId = EngineModelId.of("GE90", "GE");

        EngineModel engine = new EngineModel(
                engineId,
                EngineType.TURBOFAN,
                Thrust.of(100000),      // thrustAtStandstill
                Thrust.of(80000),       // thrustAtCruiseSpeed (tem de ser < standstill)
                Speed.of(0.8),          // cruiseSpeedMach
                TSFC.of(1.5),           // tsfc
                Coefficient.of(1.0),    // lapseRateFactor
                FuelType.JET_A1         // fuelType
        );

        Set<EngineModel> engines = new HashSet<>();
        engines.add(engine);

        // Usa o primeiro valor do Enum AircraftType (para não termos de adivinhar o nome exato se for PASSENGER, CARGO, etc)
        AircraftType safeAircraftType = AircraftType.values()[0];

        // 2. Construir e retornar o AircraftModel
        return new AircraftModel(
                AircraftModelId.of("777-200ER", "Boeing"),
                safeAircraftType,
                Weight.of(135000),      // emptyWeight
                Weight.of(297550),      // mtow (must be > empty)
                Weight.of(195040),      // mzfw (must be <= mtow)
                Weight.of(137460),      // maxFuelCapacity
                Altitude.of(43100),     // serviceCeiling
                Speed.of(0.84),         // cruiseSpeedMach
                Area.of(427.8),         // wingArea
                60.9,                   // wingSpan
                8.6,                    // aspectRatio
                Coefficient.of(0.85),   // oswaldFactor
                Coefficient.of(0.026),  // dragCoefficient
                Coefficient.of(0.5),    // liftCoefficient
                2,                      // numberOfEngines
                440,                    // maxPassengerCapacity
                13080,                  // maxRange
                engines                 // certifiedEngines
        );
    }
}