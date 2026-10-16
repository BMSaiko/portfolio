package eapli.aisafe.pilotmanagement.application;

import eapli.aisafe.aircraftmodelmanagement.domain.*;
import eapli.aisafe.enginemanagement.domain.*;
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

class ListPilotsControllerTest {

    // -------------------------------------------------------
    // SESSÃO FINGIDA (PERMISSIVE)
    // -------------------------------------------------------
    static class PermissiveAuthService extends AuthService {
        public PermissiveAuthService() { super(null); }

        @Override
        public void ensureHasAnyRole(Role... roles) {
            // Deixa sempre passar!
        }

        @Override
        public Optional<User> currentUser() {
            return Optional.of(new User(new Email("collab@tap.pt"), "Password123", UserName.of("John"), PhoneNumber.of("1234567"), Set.of(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR)));
        }
    }

    // -------------------------------------------------------
    // MÁQUINA DE FAZER REPOSITÓRIOS FALSOS (Adaptada)
    // -------------------------------------------------------
    @SuppressWarnings("unchecked")
    private <T> T createFakeRepo(Class<T> repoInterface, Object objectToReturn) {
        return (T) Proxy.newProxyInstance(
                repoInterface.getClassLoader(),
                new Class<?>[] { repoInterface },
                (proxy, method, args) -> {
                    String name = method.getName();

                    // Se for para devolver uma Lista (findAll, findByCompany)
                    if (name.equals("findAll") || name.equals("findByCompany")) {
                        if (objectToReturn instanceof Iterable) return objectToReturn;
                        return objectToReturn == null ? Collections.emptyList() : Collections.singletonList(objectToReturn);
                    }
                    // Se procurar um Optional
                    if (name.startsWith("findBy") || name.equals("findById") || name.equals("findActiveByEmail")) {
                        return Optional.ofNullable(objectToReturn);
                    }
                    return null;
                }
        );
    }

    // -------------------------------------------------------
    // Setup das "Cobaias"
    // -------------------------------------------------------
    private ListPilotsController controller;
    private PilotRepository pilotRepo;
    private CustomerCollaboratorRepository collabRepo;
    private AirTransportCompanyRepository companyRepo;

    private CustomerCollaborator dummyCollab;
    private AirTransportCompany dummyCompany;
    private Pilot dummyPilot;

    @BeforeEach
    void setUp() {
        dummyCompany = new AirTransportCompany("TAP Air Portugal", "TP", "TAP");
        dummyCollab = new CustomerCollaborator(CustomerType.AIR_TRANSPORT_COMPANY, "TAP", new Email("collab@tap.pt"), "Manager");

        // Construir um avião falso para as certificações do piloto
        EngineModel engine = new EngineModel(EngineModelId.of("GE 90B", "GE"), EngineType.TURBOFAN, Thrust.of(489000), Thrust.of(300000), Speed.of(0.84), TSFC.of(0.00016), Coefficient.of(1.0), FuelType.JET_A1);
        AircraftModel dummyModel = new AircraftModel(AircraftModelId.of("777", "Boeing"), AircraftType.PASSENGER, Weight.of(145149), Weight.of(347452), Weight.of(224982), Weight.of(145538), Altitude.of(43100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 13080, Set.of(engine));

        // Construir o Piloto falso
        User pilotUser = new User(new Email("pilot@tap.pt"), "Password123", UserName.of("Ze"), PhoneNumber.of("1234567"), Set.of(Role.PILOT));
        dummyPilot = new Pilot(pilotUser, new Email("pilot@tap.pt"), "Joao Silva", "Captain", dummyCompany, Set.of(dummyModel));
    }

    // -------------------------------------------------------
    // SUCESSO: Listar os pilotos
    // -------------------------------------------------------
    @Test
    void ensureCanListPilotsOfCompanySuccessfully() {
        // Configuramos os repositórios para devolverem sucesso
        collabRepo = createFakeRepo(CustomerCollaboratorRepository.class, dummyCollab);
        companyRepo = createFakeRepo(AirTransportCompanyRepository.class, dummyCompany);
        pilotRepo = createFakeRepo(PilotRepository.class, dummyPilot); // Retorna lista com 1 piloto

        controller = new ListPilotsController(new PermissiveAuthService(), pilotRepo, collabRepo, companyRepo);

        Iterable<Pilot> pilots = controller.listPilotsOfMyCompany();

        assertNotNull(pilots);
        assertTrue(pilots.iterator().hasNext(), "A lista de pilotos não deve estar vazia");
        assertEquals("Joao Silva", pilots.iterator().next().getName(), "O piloto retornado deve ser o nosso dummyPilot");
    }

    // -------------------------------------------------------
    // FALHA: Sem sessão iniciada
    // -------------------------------------------------------
    @Test
    void ensureFailsIfNoActiveSession() {
        AuthService noSessionAuth = new AuthService(null) {
            @Override public void ensureHasAnyRole(Role... roles) {} // Passa na role
            @Override public Optional<User> currentUser() { return Optional.empty(); } // Falha no user
        };

        controller = new ListPilotsController(noSessionAuth, null, null, null);

        Exception ex = assertThrows(IllegalStateException.class, () ->
                controller.listPilotsOfMyCompany()
        );
        assertEquals("No user is currently logged in.", ex.getMessage());
    }

    // -------------------------------------------------------
    // FALHA: Colaborador não encontrado
    // -------------------------------------------------------
    @Test
    void ensureFailsIfCollaboratorNotFound() {
        // Retorna null ao procurar o colaborador
        collabRepo = createFakeRepo(CustomerCollaboratorRepository.class, null);

        controller = new ListPilotsController(new PermissiveAuthService(), null, collabRepo, null);

        Exception ex = assertThrows(IllegalStateException.class, () ->
                controller.listPilotsOfMyCompany()
        );
        assertEquals("Collaborator profile not found.", ex.getMessage());
    }

    // -------------------------------------------------------
    // FALHA: Colaborador não pertence a uma Air Transport Company
    // -------------------------------------------------------
    @Test
    void ensureFailsIfCollaboratorNotFromAirTransportCompany() {
        // Criar um Colaborador que é do tipo AIR_CONTROL_AREA
        CustomerCollaborator badCollab = new CustomerCollaborator(CustomerType.AIR_CONTROL_AREA, "LPPT", new Email("collab@tap.pt"), "Manager");
        collabRepo = createFakeRepo(CustomerCollaboratorRepository.class, badCollab);

        controller = new ListPilotsController(new PermissiveAuthService(), null, collabRepo, null);

        Exception ex = assertThrows(IllegalStateException.class, () ->
                controller.listPilotsOfMyCompany()
        );
        assertEquals("Only Air Transport Company collaborators can list pilots.", ex.getMessage());
    }

    // -------------------------------------------------------
    // FALHA: Companhia Aérea não encontrada
    // -------------------------------------------------------
    @Test
    void ensureFailsIfCompanyNotFound() {
        collabRepo = createFakeRepo(CustomerCollaboratorRepository.class, dummyCollab);
        // Retorna null ao procurar a companhia
        companyRepo = createFakeRepo(AirTransportCompanyRepository.class, null);

        controller = new ListPilotsController(new PermissiveAuthService(), null, collabRepo, companyRepo);

        Exception ex = assertThrows(IllegalStateException.class, () ->
                controller.listPilotsOfMyCompany()
        );
        assertEquals("Company not found for code: TAP", ex.getMessage());
    }
}