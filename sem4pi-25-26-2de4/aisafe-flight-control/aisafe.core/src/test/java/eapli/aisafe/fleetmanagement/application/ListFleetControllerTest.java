package eapli.aisafe.fleetmanagement.application;

import eapli.aisafe.aircraftmodelmanagement.domain.*;
import eapli.aisafe.enginemanagement.domain.*;
import eapli.aisafe.fleetmanagement.domain.*;
import eapli.aisafe.fleetmanagement.repositories.AircraftRepository;
import eapli.aisafe.organization.domain.*;
import eapli.aisafe.organization.repositories.CustomerCollaboratorRepository;
import eapli.aisafe.generic.domain.*;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ListFleetControllerTest {

    // -------------------------------------------------------
    // AuthService Bypass (Simula sempre um Colaborador Logado)
    // -------------------------------------------------------
    static class PermissiveAuthService extends AuthService {
        public PermissiveAuthService() { super(null); }
        @Override public void ensureHasAnyRole(Role... roles) {}
        @Override public Optional<User> currentUser() {
            return Optional.of(new User(new Email("collab@tap.pt"), "Password123", UserName.of("John"), PhoneNumber.of("1234567"), Set.of(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR)));
        }
    }

    // -------------------------------------------------------
    // MÁQUINA DE REPOSITÓRIOS FALSOS
    // -------------------------------------------------------
    @SuppressWarnings("unchecked")
    private <T> T createFakeRepo(Class<T> repoInterface, Object objectToReturn) {
        return (T) Proxy.newProxyInstance(
                repoInterface.getClassLoader(),
                new Class<?>[] { repoInterface },
                (proxy, method, args) -> objectToReturn
        );
    }

    // -------------------------------------------------------
    // Setup
    // -------------------------------------------------------
    private ListFleetController controller;
    private Aircraft oldBoeing;
    private Aircraft newAirbus;

    @BeforeEach
    void setUp() {
        AirTransportCompany dummyCompany = new AirTransportCompany("TAP Air Portugal", "TP", "TAP");

        // Colaborador "Falso" da TAP
        CustomerCollaborator dummyCollab = new CustomerCollaborator(CustomerType.AIR_TRANSPORT_COMPANY, "TAP", new Email("collab@tap.pt"), "Manager");
        CustomerCollaboratorRepository collabRepo = createFakeRepo(CustomerCollaboratorRepository.class, Optional.of(dummyCollab));

        EngineModel engine = new EngineModel(EngineModelId.of("GE 90B", "GE"), EngineType.TURBOFAN, Thrust.of(489000), Thrust.of(300000), Speed.of(0.84), TSFC.of(0.00016), Coefficient.of(1.0), FuelType.JET_A1);

        // Criar dois Modelos Diferentes (Boeing e Airbus)
        AircraftModel boeingModel = new AircraftModel(AircraftModelId.of("777", "Boeing"), AircraftType.PASSENGER, Weight.of(145149), Weight.of(347452), Weight.of(224982), Weight.of(145538), Altitude.of(43100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 13080, Set.of(engine));
        AircraftModel airbusModel = new AircraftModel(AircraftModelId.of("A320", "Airbus"), AircraftType.PASSENGER, Weight.of(145149), Weight.of(347452), Weight.of(224982), Weight.of(145538), Altitude.of(43100), Speed.of(0.84), Area.of(150), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 13080, Set.of(engine));

        int currentYear = Year.now().getValue();

        // Avião 1: Um Boeing 777 gigante (400 lugares) e VELHO (20 anos)
        oldBoeing = new Aircraft("CS-TPA", boeingModel, dummyCompany, "Portugal", new CabinConfiguration(10, 40, 350), OperationalStatus.ACTIVE, currentYear - 20);

        // Avião 2: Um Airbus A320 pequeno (150 lugares) e NOVO (0 anos)
        newAirbus = new Aircraft("CS-TPB", airbusModel, dummyCompany, "Portugal", new CabinConfiguration(0, 0, 150), OperationalStatus.ACTIVE, currentYear);

        // Repositório de Aeronaves que devolve a nossa "Frota" inteira
        List<Aircraft> fakeFleet = List.of(oldBoeing, newAirbus);
        AircraftRepository aircraftRepo = createFakeRepo(AircraftRepository.class, fakeFleet);

        // Instanciar o Controlador
        controller = new ListFleetController(aircraftRepo, collabRepo, new PermissiveAuthService());
    }

    // -------------------------------------------------------
    // Testes da US072 (Listagens e Filtros)
    // -------------------------------------------------------

    @Test
    void ensureListEntireFleetReturnsAll() {
        Iterable<Aircraft> fleet = controller.getMyCompanyFleet();

        int count = 0;
        for (Aircraft a : fleet) count++;

        assertEquals(2, count, "Deveriam ser retornados 2 aviões (a frota inteira).");
    }

    @Test
    void ensureFilterByModelWorks() {
        List<Aircraft> result = controller.getFleetByModel("777");

        assertEquals(1, result.size());
        assertEquals("CS-TPA", result.get(0).identity(), "Deveria ter filtrado apenas o Boeing 777.");
    }

    @Test
    void ensureFilterByMakerWorks() {
        List<Aircraft> result = controller.getFleetByMaker("Airbus");

        assertEquals(1, result.size());
        assertEquals("CS-TPB", result.get(0).identity(), "Deveria ter filtrado apenas o Airbus.");
    }

    @Test
    void ensureFilterByCapacityWorks() {
        // Pedimos aviões com capacidade EXATA de 150 lugares
        List<Aircraft> result = controller.getFleetByCapacity(150);

        assertEquals(1, result.size());
        assertEquals("CS-TPB", result.get(0).identity(), "Deveria ter filtrado o Airbus que tem 150 lugares.");
    }

    @Test
    void ensureFilterByAgeWorks() {
        // Pedimos aviões com exatos 20 anos
        List<Aircraft> result = controller.getFleetByAge(20);

        assertEquals(1, result.size());
        assertEquals("CS-TPA", result.get(0).identity(), "Deveria ter filtrado o velho Boeing.");
    }
}