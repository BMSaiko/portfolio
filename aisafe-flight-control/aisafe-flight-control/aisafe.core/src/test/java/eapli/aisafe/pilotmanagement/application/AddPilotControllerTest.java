package eapli.aisafe.pilotmanagement.application;

import eapli.aisafe.aircraftmodelmanagement.domain.*;
import eapli.aisafe.aircraftmodelmanagement.repositories.AircraftModelRepository;
import eapli.aisafe.enginemanagement.domain.*;
import eapli.aisafe.organization.domain.*;
import eapli.aisafe.organization.repositories.*;
import eapli.aisafe.generic.domain.*;
import eapli.aisafe.pilotmanagement.domain.Pilot;
import eapli.aisafe.pilotmanagement.repositories.PilotRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.*;
import eapli.aisafe.usermanagement.repositories.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AddPilotControllerTest {

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
    // MÁQUINA DE FAZER REPOSITÓRIOS FALSOS (Adaptada para Pilotos)
    // -------------------------------------------------------
    @SuppressWarnings("unchecked")
    private <T> T createFakeRepo(Class<T> repoInterface, Object objectToReturn) {
        return (T) Proxy.newProxyInstance(
                repoInterface.getClassLoader(),
                new Class<?>[] { repoInterface },
                (proxy, method, args) -> {
                    String name = method.getName();

                    // Se procurar um Optional (findBy, findActiveByEmail)
                    if (name.startsWith("findBy") || name.equals("findById") || name.equals("findActiveByEmail")) {
                        return Optional.ofNullable(objectToReturn);
                    }
                    // Se for para devolver uma Lista (findAll)
                    if (name.equals("findAll")) {
                        if (objectToReturn instanceof Iterable) return objectToReturn;
                        return objectToReturn == null ? Collections.emptyList() : Collections.singletonList(objectToReturn);
                    }
                    // Simula a verificação de duplicados (existsBy...)
                    if (name.startsWith("existsBy")) {
                        if (objectToReturn instanceof Boolean) return objectToReturn; // Permite passar true/false diretamente
                        return objectToReturn != null;
                    }
                    // Simula a gravação (save)
                    if (name.equals("save")) {
                        return args[0]; // Devolve a própria entidade que entrou
                    }
                    return null;
                }
        );
    }

    // -------------------------------------------------------
    // Setup das "Cobaias"
    // -------------------------------------------------------
    private AddPilotController controller;
    private PilotRepository pilotRepo;
    private UserRepository userRepo;
    private AircraftModelRepository modelRepo;
    private CustomerCollaboratorRepository collabRepo;
    private AirTransportCompanyRepository companyRepo;

    private CustomerCollaborator dummyCollab;
    private AirTransportCompany dummyCompany;
    private AircraftModel dummyModel;
    private User dummyUser;

    @BeforeEach
    void setUp() {
        dummyCompany = new AirTransportCompany("TAP Air Portugal", "TP", "TAP");
        dummyCollab = new CustomerCollaborator(CustomerType.AIR_TRANSPORT_COMPANY, "TAP", new Email("collab@tap.pt"), "Manager");

        EngineModel engine = new EngineModel(EngineModelId.of("GE 90B", "GE"), EngineType.TURBOFAN, Thrust.of(489000), Thrust.of(300000), Speed.of(0.84), TSFC.of(0.00016), Coefficient.of(1.0), FuelType.JET_A1);
        dummyModel = new AircraftModel(AircraftModelId.of("777", "Boeing"), AircraftType.PASSENGER, Weight.of(145149), Weight.of(347452), Weight.of(224982), Weight.of(145538), Altitude.of(43100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 13080, Set.of(engine));

        dummyUser = new User(new Email("existente@tap.pt"), "Password123", UserName.of("Ze"), PhoneNumber.of("1234567"), Set.of(Role.PILOT));
    }

    // -------------------------------------------------------
    // SUCESSO ABSOLUTO (Caminho Feliz)
    // -------------------------------------------------------
    @Test
    void ensureValidPilotIsAddedSuccessfully() {
        // Preparar Repos (Nenhum email existe ainda)
        pilotRepo = createFakeRepo(PilotRepository.class, false); // existsByEmail = false
        userRepo = createFakeRepo(UserRepository.class, null);    // findActiveByEmail = empty
        collabRepo = createFakeRepo(CustomerCollaboratorRepository.class, dummyCollab);
        companyRepo = createFakeRepo(AirTransportCompanyRepository.class, dummyCompany);
        modelRepo = createFakeRepo(AircraftModelRepository.class, dummyModel);

        controller = new AddPilotController(new PermissiveAuthService(), pilotRepo, userRepo, modelRepo, collabRepo, companyRepo);

        Pilot pilot = controller.addPilot(
                "pilot@tap.pt", "Joao Silva", "912345678", "Captain", List.of(dummyModel), "Password123!"
        );

        assertNotNull(pilot);
        assertEquals("pilot@tap.pt", pilot.email().value());
        assertTrue(pilot.isActive());
    }

    // -------------------------------------------------------
    // FALHA: Email já existe num PILOTO
    // -------------------------------------------------------
    @Test
    void ensureDuplicatePilotEmailIsRejected() {
        // Dizemos ao fakeRepo que existsByEmail vai devolver TRUE
        pilotRepo = createFakeRepo(PilotRepository.class, true);
        userRepo = createFakeRepo(UserRepository.class, null);
        collabRepo = createFakeRepo(CustomerCollaboratorRepository.class, dummyCollab);
        companyRepo = createFakeRepo(AirTransportCompanyRepository.class, dummyCompany);
        modelRepo = createFakeRepo(AircraftModelRepository.class, dummyModel);

        controller = new AddPilotController(new PermissiveAuthService(), pilotRepo, userRepo, modelRepo, collabRepo, companyRepo);

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                controller.addPilot("existente@tap.pt", "Joao Silva", "912345678", "Captain", List.of(dummyModel), "Pass123!")
        );
        assertEquals("Já existe um piloto registado com este email.", ex.getMessage());
    }

    // -------------------------------------------------------
    // FALHA: Email já existe num USER DO SISTEMA
    // -------------------------------------------------------
    @Test
    void ensureDuplicateUserEmailIsRejected() {
        pilotRepo = createFakeRepo(PilotRepository.class, false);
        // Dizemos ao userRepo que vai encontrar um user quando procurar o email
        userRepo = createFakeRepo(UserRepository.class, dummyUser);

        collabRepo = createFakeRepo(CustomerCollaboratorRepository.class, dummyCollab);
        companyRepo = createFakeRepo(AirTransportCompanyRepository.class, dummyCompany);
        modelRepo = createFakeRepo(AircraftModelRepository.class, dummyModel);

        controller = new AddPilotController(new PermissiveAuthService(), pilotRepo, userRepo, modelRepo, collabRepo, companyRepo);

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                controller.addPilot("existente@tap.pt", "Joao Silva", "912345678", "Captain", List.of(dummyModel), "Pass123!")
        );
        assertEquals("Já existe um utilizador de sistema ativo com este email.", ex.getMessage());
    }

    // -------------------------------------------------------
    // FALHA: Lista de Modelos (Certificações) Vazia
    // -------------------------------------------------------
    @Test
    void ensureEmptyCertificationsListIsRejected() {
        pilotRepo = createFakeRepo(PilotRepository.class, false);
        userRepo = createFakeRepo(UserRepository.class, null);
        collabRepo = createFakeRepo(CustomerCollaboratorRepository.class, dummyCollab);
        companyRepo = createFakeRepo(AirTransportCompanyRepository.class, dummyCompany);
        modelRepo = createFakeRepo(AircraftModelRepository.class, dummyModel);

        controller = new AddPilotController(new PermissiveAuthService(), pilotRepo, userRepo, modelRepo, collabRepo, companyRepo);

        // Passamos uma lista vazia de modelos de avião!
        List<AircraftModel> emptyList = new ArrayList<>();

        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                controller.addPilot("pilot@tap.pt", "Joao", "912345678", "Cap", emptyList, "Pass123!")
        );
        assertEquals("Um piloto tem de estar certificado para pelo menos um modelo de aeronave.", ex.getMessage());
    }

    // -------------------------------------------------------
    // FALHA: Utilizador não é de uma "Air Transport Company"
    // -------------------------------------------------------
    @Test
    void ensureFailsIfCollaboratorIsNotFromAirTransportCompany() {
        pilotRepo = createFakeRepo(PilotRepository.class, false);
        userRepo = createFakeRepo(UserRepository.class, null);

        // Criamos um colaborador de um TIPO ERRADO (ex: AIR_CONTROL_AREA em vez de AIR_TRANSPORT_COMPANY)
        CustomerCollaborator badCollab = new CustomerCollaborator(CustomerType.AIR_CONTROL_AREA, "LPPT", new Email("collab@tap.pt"), "Manager");
        collabRepo = createFakeRepo(CustomerCollaboratorRepository.class, badCollab);

        companyRepo = createFakeRepo(AirTransportCompanyRepository.class, dummyCompany);
        modelRepo = createFakeRepo(AircraftModelRepository.class, dummyModel);

        controller = new AddPilotController(new PermissiveAuthService(), pilotRepo, userRepo, modelRepo, collabRepo, companyRepo);

        Exception ex = assertThrows(IllegalStateException.class, () ->
                controller.addPilot("pilot@tap.pt", "Joao", "912345678", "Cap", List.of(dummyModel), "Pass123!")
        );
        assertEquals("Apenas colaboradores de Companhias de Transporte Aéreo podem adicionar pilotos.", ex.getMessage());
    }

    // -------------------------------------------------------
    // FALHA: Lista de Modelos (Certificações) a NULL
    // -------------------------------------------------------
    @Test
    void ensureNullCertificationsListIsRejected() {
        pilotRepo = createFakeRepo(PilotRepository.class, false);
        userRepo = createFakeRepo(UserRepository.class, null);
        collabRepo = createFakeRepo(CustomerCollaboratorRepository.class, dummyCollab);
        companyRepo = createFakeRepo(AirTransportCompanyRepository.class, dummyCompany);
        modelRepo = createFakeRepo(AircraftModelRepository.class, dummyModel);

        controller = new AddPilotController(new PermissiveAuthService(), pilotRepo, userRepo, modelRepo, collabRepo, companyRepo);

        // Passar literalmente null na lista de aviões!
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                controller.addPilot("pilot@tap.pt", "Joao", "912345678", "Cap", null, "Pass123!")
        );
        assertEquals("Um piloto tem de estar certificado para pelo menos um modelo de aeronave.", ex.getMessage());
    }

    // -------------------------------------------------------
    // SUCESSO: Listar modelos disponíveis
    // -------------------------------------------------------
    @Test
    void ensureGetAvailableAircraftModelsReturnsList() {
        // Configuramos o ModelRepo para devolver uma lista com o nosso dummyModel no findAll()
        modelRepo = createFakeRepo(AircraftModelRepository.class, List.of(dummyModel));

        // Os outros repositórios não importam para este método
        controller = new AddPilotController(new PermissiveAuthService(), null, null, modelRepo, null, null);

        Iterable<AircraftModel> result = controller.getAvailableAircraftModels();

        assertNotNull(result);
        assertTrue(result.iterator().hasNext()); // Garante que a lista não vem vazia
    }

    // -------------------------------------------------------
    // FALHA: Nenhum utilizador com sessão iniciada
    // -------------------------------------------------------
    @Test
    void ensureFailsIfNoActiveSession() {
        // Criar um AuthService falso que finge que NÃO HÁ NINGUÉM LOGADO
        AuthService noSessionAuth = new AuthService(null) {
            @Override public void ensureHasAnyRole(Role... roles) {} // Passa nas roles
            @Override public Optional<User> currentUser() { return Optional.empty(); } // Mas falha a dar o user
        };

        controller = new AddPilotController(noSessionAuth, null, null, null, null, null);

        Exception ex = assertThrows(IllegalStateException.class, () ->
                controller.addPilot("pilot@tap.pt", "Joao", "912345678", "Cap", List.of(dummyModel), "Pass123!")
        );
        assertEquals("Nenhum utilizador com sessão iniciada.", ex.getMessage());
    }

    // -------------------------------------------------------
    // FALHA: Colaborador não encontrado na Base de Dados
    // -------------------------------------------------------
    @Test
    void ensureFailsIfCollaboratorNotFound() {
        // Dizemos ao CollabRepo para devolver NULL quando procurar
        collabRepo = createFakeRepo(CustomerCollaboratorRepository.class, null);

        controller = new AddPilotController(new PermissiveAuthService(), null, null, null, collabRepo, null);

        Exception ex = assertThrows(IllegalStateException.class, () ->
                controller.addPilot("pilot@tap.pt", "Joao", "912345678", "Cap", List.of(dummyModel), "Pass123!")
        );
        assertEquals("Perfil de colaborador não encontrado.", ex.getMessage());
    }

    // -------------------------------------------------------
    // FALHA: Companhia Aérea não encontrada
    // -------------------------------------------------------
    @Test
    void ensureFailsIfCompanyNotFound() {
        collabRepo = createFakeRepo(CustomerCollaboratorRepository.class, dummyCollab);
        // Dizemos ao CompanyRepo para devolver NULL (companhia desapareceu)
        companyRepo = createFakeRepo(AirTransportCompanyRepository.class, null);

        controller = new AddPilotController(new PermissiveAuthService(), null, null, null, collabRepo, companyRepo);

        Exception ex = assertThrows(IllegalStateException.class, () ->
                controller.addPilot("pilot@tap.pt", "Joao", "912345678", "Cap", List.of(dummyModel), "Pass123!")
        );
        assertEquals("Companhia não encontrada para o código: TAP", ex.getMessage());
    }
}