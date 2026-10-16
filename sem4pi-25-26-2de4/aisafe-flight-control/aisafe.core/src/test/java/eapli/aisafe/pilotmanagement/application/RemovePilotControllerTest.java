package eapli.aisafe.pilotmanagement.application;

import eapli.aisafe.aircraftmodelmanagement.domain.*;
import eapli.aisafe.enginemanagement.domain.*;
import eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository;
import eapli.aisafe.organization.domain.*;
import eapli.aisafe.organization.repositories.*;
import eapli.aisafe.generic.domain.*;
import eapli.aisafe.pilotmanagement.domain.Pilot;
import eapli.aisafe.pilotmanagement.repositories.PilotRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RemovePilotControllerTest {

    // -------------------------------------------------------
    // SESSÃO FINGIDA (PERMISSIVE)
    // -------------------------------------------------------
    static class PermissiveAuthService extends AuthService {
        public PermissiveAuthService() { super(null); }

        @Override
        public void ensureHasAnyRole(Role... roles) {}

        @Override
        public Optional<User> currentUser() {
            return Optional.of(new User(new Email("collab@tap.pt"), "Password123", UserName.of("John"), PhoneNumber.of("1234567"), Set.of(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR)));
        }
    }

    // -------------------------------------------------------
    // MÁQUINA DE FAZER REPOSITÓRIOS FALSOS (Upgrade para Count)
    // -------------------------------------------------------
    @SuppressWarnings("unchecked")
    private <T> T createFakeRepo(Class<T> repoInterface, Object objectToReturn) {
        return (T) Proxy.newProxyInstance(
                repoInterface.getClassLoader(),
                new Class<?>[] { repoInterface },
                (proxy, method, args) -> {
                    String name = method.getName();

                    if (name.equals("findAll") || name.equals("findByCompany")) {
                        if (objectToReturn instanceof Iterable) return objectToReturn;
                        return objectToReturn == null ? Collections.emptyList() : Collections.singletonList(objectToReturn);
                    }
                    if (name.startsWith("findBy") || name.equals("findById")) {
                        return Optional.ofNullable(objectToReturn);
                    }
                    // NOVA REGRA: Lidar com o countByPilot
                    if (name.startsWith("count")) {
                        if (objectToReturn instanceof Number) return ((Number) objectToReturn).longValue();
                        return 0L;
                    }
                    if (name.equals("save")) {
                        return args[0]; // Devolve o piloto que entrou
                    }
                    return null;
                }
        );
    }

    // -------------------------------------------------------
    // Setup das "Cobaias"
    // -------------------------------------------------------
    private RemovePilotController controller;
    private PilotRepository pilotRepo;
    private CustomerCollaboratorRepository collabRepo;
    private AirTransportCompanyRepository companyRepo;
    private FlightPlanRepository flightPlanRepo;

    private CustomerCollaborator dummyCollab;
    private AirTransportCompany dummyCompany;
    private Pilot dummyActivePilot;
    private Pilot dummyInactivePilot;

    @BeforeEach
    void setUp() {
        dummyCompany = new AirTransportCompany("TAP Air Portugal", "TP", "TAP");
        dummyCollab = new CustomerCollaborator(CustomerType.AIR_TRANSPORT_COMPANY, "TAP", new Email("collab@tap.pt"), "Manager");

        EngineModel engine = new EngineModel(EngineModelId.of("GE 90B", "GE"), EngineType.TURBOFAN, Thrust.of(489000), Thrust.of(300000), Speed.of(0.84), TSFC.of(0.00016), Coefficient.of(1.0), FuelType.JET_A1);
        AircraftModel dummyModel = new AircraftModel(AircraftModelId.of("777", "Boeing"), AircraftType.PASSENGER, Weight.of(145149), Weight.of(347452), Weight.of(224982), Weight.of(145538), Altitude.of(43100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 13080, Set.of(engine));

        User activeUser = new User(new Email("active@tap.pt"), "Pass1234", UserName.of("Ze"), PhoneNumber.of("1234567"), Set.of(Role.PILOT));
        dummyActivePilot = new Pilot(activeUser, new Email("active@tap.pt"), "Piloto Ativo", "Captain", dummyCompany, Set.of(dummyModel));

        User inactiveUser = new User(new Email("inactive@tap.pt"), "Pass1234    ", UserName.of("Ze2"), PhoneNumber.of("1234567"), Set.of(Role.PILOT));
        dummyInactivePilot = new Pilot(inactiveUser, new Email("inactive@tap.pt"), "Piloto Inativo", "Captain", dummyCompany, Set.of(dummyModel));
        dummyInactivePilot.deactivate(); // Desativar para o teste
    }

    // ====================================================================================
    // TESTES DO MÉTODO: activePilotsOfMyCompany()
    // ====================================================================================

    @Test
    void ensureCanListActivePilotsSuccessfully() {
        collabRepo = createFakeRepo(CustomerCollaboratorRepository.class, dummyCollab);
        companyRepo = createFakeRepo(AirTransportCompanyRepository.class, dummyCompany);
        flightPlanRepo = createFakeRepo(FlightPlanRepository.class, 0L);

        // Passamos os dois pilotos (um ativo, um inativo)
        pilotRepo = createFakeRepo(PilotRepository.class, List.of(dummyActivePilot, dummyInactivePilot));

        controller = new RemovePilotController(new PermissiveAuthService(), pilotRepo, collabRepo, companyRepo, flightPlanRepo);

        Iterable<Pilot> pilots = controller.activePilotsOfMyCompany();

        assertNotNull(pilots);
        List<Pilot> resultList = new ArrayList<>();
        pilots.forEach(resultList::add);

        assertEquals(1, resultList.size(), "Só deve retornar 1 piloto (o ativo)");
        assertEquals("Piloto Ativo", resultList.get(0).getName(), "O piloto retornado tem de ser o ativo");
    }

    @Test
    void ensureActivePilotsFailsIfNoSession() {
        AuthService noSessionAuth = new AuthService(null) {
            @Override public void ensureHasAnyRole(Role... roles) {}
            @Override public Optional<User> currentUser() { return Optional.empty(); }
        };

        controller = new RemovePilotController(noSessionAuth, null, null, null, null);

        Exception ex = assertThrows(IllegalStateException.class, () -> controller.activePilotsOfMyCompany());
        assertEquals("No user logged in.", ex.getMessage());
    }

    @Test
    void ensureActivePilotsFailsIfCollaboratorNotFound() {
        collabRepo = createFakeRepo(CustomerCollaboratorRepository.class, null);
        controller = new RemovePilotController(new PermissiveAuthService(), null, collabRepo, null, null);

        Exception ex = assertThrows(IllegalStateException.class, () -> controller.activePilotsOfMyCompany());
        assertEquals("Collaborator not found.", ex.getMessage());
    }

    @Test
    void ensureActivePilotsFailsIfCollaboratorNotATC() {
        CustomerCollaborator badCollab = new CustomerCollaborator(CustomerType.AIR_CONTROL_AREA, "LPPT", new Email("collab@tap.pt"), "Manager");
        collabRepo = createFakeRepo(CustomerCollaboratorRepository.class, badCollab);

        controller = new RemovePilotController(new PermissiveAuthService(), null, collabRepo, null, null);

        Exception ex = assertThrows(IllegalStateException.class, () -> controller.activePilotsOfMyCompany());
        assertEquals("Only Air Transport Company collaborators can remove pilots.", ex.getMessage());
    }

    @Test
    void ensureActivePilotsFailsIfCompanyNotFound() {
        collabRepo = createFakeRepo(CustomerCollaboratorRepository.class, dummyCollab);
        companyRepo = createFakeRepo(AirTransportCompanyRepository.class, null);

        controller = new RemovePilotController(new PermissiveAuthService(), null, collabRepo, companyRepo, null);

        Exception ex = assertThrows(IllegalStateException.class, () -> controller.activePilotsOfMyCompany());
        assertEquals("Company not found.", ex.getMessage());
    }

    // ====================================================================================
    // TESTES DO MÉTODO: deactivatePilot(Pilot pilot)
    // ====================================================================================

    @Test
    void ensureCanDeactivatePilotSuccessfully() {
        pilotRepo = createFakeRepo(PilotRepository.class, null);

        // Simula que o piloto NÃO TEM voos (0L)
        flightPlanRepo = createFakeRepo(FlightPlanRepository.class, 0L);

        controller = new RemovePilotController(new PermissiveAuthService(), pilotRepo, null, null, flightPlanRepo);

        // Garantimos que está ativo antes de começar
        assertTrue(dummyActivePilot.isActive());

        Pilot result = controller.deactivatePilot(dummyActivePilot);

        // Verificamos se o resultado ficou INATIVO
        assertFalse(result.isActive(), "O piloto deveria ter ficado inativo após a operação");
    }

    @Test
    void ensureCannotDeactivatePilotWithAssignedFlightPlans() {
        pilotRepo = createFakeRepo(PilotRepository.class, null);

        // Simula que o piloto TEM voos (1L) -> isto deve fazer estourar
        flightPlanRepo = createFakeRepo(FlightPlanRepository.class, 1L);

        controller = new RemovePilotController(new PermissiveAuthService(), pilotRepo, null, null, flightPlanRepo);

        Exception ex = assertThrows(IllegalStateException.class, () ->
                controller.deactivatePilot(dummyActivePilot)
        );
        assertEquals("Cannot deactivate a pilot with assigned flight plans.", ex.getMessage());

        // Garantimos que NÃO MUDOU o estado (continuou ativo porque deu erro)
        assertTrue(dummyActivePilot.isActive());
    }
}