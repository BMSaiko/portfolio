package eapli.aisafe.fleetmanagement.application;

import eapli.aisafe.aircraftmodelmanagement.domain.*;
import eapli.aisafe.enginemanagement.domain.*;
import eapli.aisafe.fleetmanagement.domain.*;
import eapli.aisafe.fleetmanagement.repositories.AircraftRepository;
import eapli.aisafe.organization.domain.*;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import eapli.aisafe.generic.domain.*;

class DecommissionAircraftControllerTest {

    // -------------------------------------------------------
    // AuthService Bypass
    // -------------------------------------------------------
    static class PermissiveAuthService extends AuthService {
        public PermissiveAuthService() { super(null); }
        @Override public void ensureHasAnyRole(Role... roles) {}
    }

    // -------------------------------------------------------
    // MÁQUINA DE REPOSITÓRIOS FALSOS
    // -------------------------------------------------------
    @SuppressWarnings("unchecked")
    private <T> T createFakeRepo(Class<T> repoInterface, Object objectToReturn) {
        return (T) Proxy.newProxyInstance(
                repoInterface.getClassLoader(),
                new Class<?>[] { repoInterface },
                (proxy, method, args) -> {
                    String name = method.getName();
                    // Interceta a chamada do Controller (ofIdentity)
                    if (name.equals("ofIdentity")) {
                        return Optional.ofNullable(objectToReturn);
                    }
                    if (name.equals("save")) {
                        return args[0];
                    }
                    return null;
                }
        );
    }

    // -------------------------------------------------------
    // Setup
    // -------------------------------------------------------
    private DecommissionAircraftController controller;
    private Aircraft activeAircraft;

    @BeforeEach
    void setUp() {
        AirTransportCompany dummyCompany = new AirTransportCompany("TAP Air Portugal", "TP", "TAP");
        EngineModel engine = new EngineModel(EngineModelId.of("GE 90B", "GE"), EngineType.TURBOFAN, Thrust.of(489000), Thrust.of(300000), Speed.of(0.84), TSFC.of(0.00016), Coefficient.of(1.0), FuelType.JET_A1);
        AircraftModel dummyModel = new AircraftModel(AircraftModelId.of("777", "Boeing"), AircraftType.PASSENGER, Weight.of(145149), Weight.of(347452), Weight.of(224982), Weight.of(145538), Altitude.of(43100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 13080, Set.of(engine));
        CabinConfiguration validCabin = new CabinConfiguration(10, 20, 150);

        // Avião sempre fresquinho e ATIVO antes de cada teste
        activeAircraft = new Aircraft("CS-TPA", dummyModel, dummyCompany, "Portugal", validCabin, OperationalStatus.ACTIVE);
    }

    // -------------------------------------------------------
    // Testes
    // -------------------------------------------------------

    @Test
    void ensureDecommissionIsSuccessful() {
        // Passamos o avião ATIVO ao repositório falso
        AircraftRepository aircraftRepo = createFakeRepo(AircraftRepository.class, activeAircraft);
        controller = new DecommissionAircraftController(aircraftRepo, new PermissiveAuthService());

        Aircraft result = controller.decommissionAircraft("CS-TPA");

        assertNotNull(result);
        assertEquals(OperationalStatus.RETIRED, result.status(), "O estado devia ter mudado para RETIRED.");
    }

    @Test
    void ensureFailsIfAircraftNotFound() {
        // Passamos NULL ao repositório para simular que o avião não existe na Base de Dados
        AircraftRepository aircraftRepo = createFakeRepo(AircraftRepository.class, null);
        controller = new DecommissionAircraftController(aircraftRepo, new PermissiveAuthService());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> controller.decommissionAircraft("CS-TPA"));
        assertTrue(exception.getMessage().contains("No aircraft found"));
    }

    @Test
    void ensureFailsIfAlreadyRetired() {
        // Retiramos o avião manualmente ANTES de o controlador correr
        activeAircraft.decommission();

        AircraftRepository aircraftRepo = createFakeRepo(AircraftRepository.class, activeAircraft);
        controller = new DecommissionAircraftController(aircraftRepo, new PermissiveAuthService());

        // Deve estoirar com IllegalStateException lançado pela classe Aircraft
        Exception exception = assertThrows(IllegalStateException.class, () -> controller.decommissionAircraft("CS-TPA"));
        assertTrue(exception.getMessage().contains("already retired"));
    }

    @Test
    void ensureFailsIfNoActiveSession() {
        // AuthService que estoira se tentarem aceder sem login
        AuthService noSessionAuth = new AuthService(null) {
            @Override public void ensureHasAnyRole(Role... roles) {
                throw new IllegalStateException("Sem permissões");
            }
        };
        AircraftRepository aircraftRepo = createFakeRepo(AircraftRepository.class, activeAircraft);
        controller = new DecommissionAircraftController(aircraftRepo, noSessionAuth);

        assertThrows(IllegalStateException.class, () -> controller.decommissionAircraft("CS-TPA"));
    }

    // -------------------------------------------------------
    // Validações do Construtor
    // -------------------------------------------------------
    @Test
    void ensureCannotCreateControllerWithNullRepository() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new DecommissionAircraftController(null, new PermissiveAuthService())
        );
        assertTrue(exception.getMessage().contains("mandatory"));
    }

    @Test
    void ensureCannotCreateControllerWithNullAuthService() {
        AircraftRepository aircraftRepo = createFakeRepo(AircraftRepository.class, activeAircraft);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new DecommissionAircraftController(aircraftRepo, null)
        );
        assertTrue(exception.getMessage().contains("mandatory"));
    }
}