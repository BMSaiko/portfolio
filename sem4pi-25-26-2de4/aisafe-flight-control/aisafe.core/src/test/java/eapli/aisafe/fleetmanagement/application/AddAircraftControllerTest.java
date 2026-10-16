package eapli.aisafe.fleetmanagement.application;

import eapli.aisafe.aircraftmodelmanagement.domain.*;
import eapli.aisafe.aircraftmodelmanagement.repositories.AircraftModelRepository;
import eapli.aisafe.enginemanagement.domain.*;
import eapli.aisafe.fleetmanagement.domain.*;
import eapli.aisafe.fleetmanagement.dto.AircraftRegistrationDTO;
import eapli.aisafe.fleetmanagement.repositories.AircraftRepository;
import eapli.aisafe.organization.domain.*;
import eapli.aisafe.organization.repositories.*;
import eapli.aisafe.generic.domain.*;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AddAircraftControllerTest {

    static class PermissiveAuthService extends AuthService {
        public PermissiveAuthService() { super(null); }

        @Override
        public void ensureHasAnyRole(Role... roles) {
            // Não faz nada, deixa sempre passar!
        }

        @Override
        public Optional<User> currentUser() {
            return Optional.of(new User(new Email("collab@tap.pt"), "Password123", UserName.of("John"), PhoneNumber.of("1234567"), Set.of(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR)));
        }
    }

    // -------------------------------------------------------
    // MÁQUINA DE FAZER REPOSITÓRIOS FALSOS
    // -------------------------------------------------------
    @SuppressWarnings("unchecked")
    private <T> T createFakeRepo(Class<T> repoInterface, Object objectToReturn) {
        return (T) Proxy.newProxyInstance(
                repoInterface.getClassLoader(),
                new Class<?>[] { repoInterface },
                (proxy, method, args) -> {
                    String name = method.getName();
                    // Se o controller procurar algo, devolvemos o objeto falso que preparámos
                    if (name.startsWith("findBy") || name.equals("findById") || name.equals("ofIdentity")) {
                        return Optional.ofNullable(objectToReturn);
                    }
                    // Simula a verificação de duplicados
                    if (name.equals("existsByRegistration") || name.equals("existsByIataCode")) {
                        return objectToReturn != null;
                    }
                    // Simula a gravação devolvendo o que lá tentaram meter
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
    private AddAircraftController controller;
    private AircraftRepository aircraftRepo;
    private AircraftModelRepository modelRepo;
    private CustomerCollaboratorRepository collabRepo;
    private AirTransportCompanyRepository companyRepo;

    // Entidades Válidas Prontas a Usar
    private CustomerCollaborator dummyCollab;
    private AirTransportCompany dummyCompany;
    private AircraftModel dummyModel;

    @BeforeEach
    void setUp() {
        dummyCompany = new AirTransportCompany("TAP Air Portugal", "TP", "TAP");
        dummyCollab = new CustomerCollaborator(CustomerType.AIR_TRANSPORT_COMPANY, "TAP", new Email("collab@tap.pt"), "Manager");

        EngineModel engine = new EngineModel(EngineModelId.of("GE 90B", "GE"), EngineType.TURBOFAN, Thrust.of(489000), Thrust.of(300000), Speed.of(0.84), TSFC.of(0.00016), Coefficient.of(1.0), FuelType.JET_A1);
        dummyModel = new AircraftModel(AircraftModelId.of("777", "Boeing"), AircraftType.PASSENGER, Weight.of(145149), Weight.of(347452), Weight.of(224982), Weight.of(145538), Altitude.of(43100), Speed.of(0.84), Area.of(440), 60.93, 9.0, Coefficient.of(0.87), Coefficient.of(0.017), Coefficient.of(0.5), 2, 400, 13080, Set.of(engine));
    }

    // -------------------------------------------------------
    // Sucesso
    // -------------------------------------------------------
    @Test
    void ensureValidAircraftIsRegistered() {
        // Criar repositórios que DEVOLVEM sempre sucesso!
        modelRepo = createFakeRepo(AircraftModelRepository.class, dummyModel);
        collabRepo = createFakeRepo(CustomerCollaboratorRepository.class, dummyCollab);
        companyRepo = createFakeRepo(AirTransportCompanyRepository.class, dummyCompany);
        // O repositório da aeronave tem de dizer que a matrícula AINDA NÃO EXISTE (passamos null)
        aircraftRepo = createFakeRepo(AircraftRepository.class, null);

        controller = new AddAircraftController(aircraftRepo, modelRepo, collabRepo, companyRepo, new PermissiveAuthService());

        AircraftRegistrationDTO dto = new AircraftRegistrationDTO(
                "CS-TPA", "777", "Boeing", "Portugal", 10, 20, 150, "ACTIVE", 2005
        );

        Aircraft aircraft = controller.addAircraft(dto);

        assertNotNull(aircraft);
        assertEquals("CS-TPA", aircraft.identity());
    }

    // -------------------------------------------------------
    // Matrículas duplicadas
    // -------------------------------------------------------
    @Test
    void ensureDuplicateRegistrationIsRejected() {
        modelRepo = createFakeRepo(AircraftModelRepository.class, dummyModel);
        collabRepo = createFakeRepo(CustomerCollaboratorRepository.class, dummyCollab);
        companyRepo = createFakeRepo(AirTransportCompanyRepository.class, dummyCompany);

        // O repositório da aeronave tem de dizer que a matrícula JÁ EXISTE! 
        // Passamos uma string para enganar o fakeRepo e ele devolver true no existsByRegistration
        aircraftRepo = createFakeRepo(AircraftRepository.class, "Eu ja existo");

        controller = new AddAircraftController(aircraftRepo, modelRepo, collabRepo, companyRepo, new PermissiveAuthService());

        AircraftRegistrationDTO dto = new AircraftRegistrationDTO(
                "CS-TPA", "777", "Boeing", "Portugal", 10, 20, 150, "ACTIVE", 2005
        );

        // Deve estourar porque a matrícula já existe
        assertThrows(RuntimeException.class, () -> controller.addAircraft(dto));
    }

    // -------------------------------------------------------
    // Modelo Inexistente
    // -------------------------------------------------------
    @Test
    void ensureNonExistentModelIsRejected() {
        // Passamos NULL ao ModelRepo para simular que não encontrou o avião na Base de Dados
        modelRepo = createFakeRepo(AircraftModelRepository.class, null);
        collabRepo = createFakeRepo(CustomerCollaboratorRepository.class, dummyCollab);
        companyRepo = createFakeRepo(AirTransportCompanyRepository.class, dummyCompany);
        aircraftRepo = createFakeRepo(AircraftRepository.class, null);

        controller = new AddAircraftController(aircraftRepo, modelRepo, collabRepo, companyRepo, new PermissiveAuthService());

        AircraftRegistrationDTO dto = new AircraftRegistrationDTO(
                "CS-TPA", "A380", "Airbus", "Portugal", 10, 20, 150, "ACTIVE", 2005
        );

        // Deve estourar porque o modelo A380 não foi encontrado
        assertThrows(RuntimeException.class, () -> controller.addAircraft(dto));
    }

    // -------------------------------------------------------
    // Falha na Sessão / Autenticação
    // -------------------------------------------------------
    @Test
    void ensureFailsIfNoActiveSession() {
        // Criar um AuthService falso que finge que NÃO HÁ NINGUÉM LOGADO
        AuthService noSessionAuth = new AuthService(null) {
            @Override public void ensureHasAnyRole(Role... roles) {
                throw new IllegalStateException("Sem permissões");
            }
            @Override public Optional<User> currentUser() { return Optional.empty(); }
        };

        modelRepo = createFakeRepo(AircraftModelRepository.class, dummyModel);
        collabRepo = createFakeRepo(CustomerCollaboratorRepository.class, dummyCollab);
        companyRepo = createFakeRepo(AirTransportCompanyRepository.class, dummyCompany);
        aircraftRepo = createFakeRepo(AircraftRepository.class, null);

        controller = new AddAircraftController(aircraftRepo, modelRepo, collabRepo, companyRepo, noSessionAuth);

        AircraftRegistrationDTO dto = new AircraftRegistrationDTO(
                "CS-TPA", "777", "Boeing", "Portugal", 10, 20, 150, "ACTIVE", 2005
        );

        assertThrows(RuntimeException.class, () -> controller.addAircraft(dto));
    }

    // -------------------------------------------------------
    // Colaborador Inexistente
    // -------------------------------------------------------
    @Test
    void ensureFailsIfCollaboratorNotFound() {
        modelRepo = createFakeRepo(AircraftModelRepository.class, dummyModel);

        // Colaborador devolve NULL (simula que o utilizador está logado mas não está na BD de colaboradores)
        collabRepo = createFakeRepo(CustomerCollaboratorRepository.class, null);

        companyRepo = createFakeRepo(AirTransportCompanyRepository.class, dummyCompany);
        aircraftRepo = createFakeRepo(AircraftRepository.class, null);

        controller = new AddAircraftController(aircraftRepo, modelRepo, collabRepo, companyRepo, new PermissiveAuthService());

        AircraftRegistrationDTO dto = new AircraftRegistrationDTO(
                "CS-TPA", "777", "Boeing", "Portugal", 10, 20, 150, "ACTIVE", 2005
        );

        assertThrows(RuntimeException.class, () -> controller.addAircraft(dto));
    }

    // -------------------------------------------------------
    // Companhia Aérea Inexistente
    // -------------------------------------------------------
    @Test
    void ensureFailsIfCompanyNotFound() {
        modelRepo = createFakeRepo(AircraftModelRepository.class, dummyModel);
        collabRepo = createFakeRepo(CustomerCollaboratorRepository.class, dummyCollab);

        // Companhia devolve NULL (simula que a TAP desapareceu da BD)
        companyRepo = createFakeRepo(AirTransportCompanyRepository.class, null);

        aircraftRepo = createFakeRepo(AircraftRepository.class, null);

        controller = new AddAircraftController(aircraftRepo, modelRepo, collabRepo, companyRepo, new PermissiveAuthService());

        AircraftRegistrationDTO dto = new AircraftRegistrationDTO(
                "CS-TPA", "777", "Boeing", "Portugal", 10, 20, 150, "ACTIVE", 2005
        );

        assertThrows(RuntimeException.class, () -> controller.addAircraft(dto));
    }
}