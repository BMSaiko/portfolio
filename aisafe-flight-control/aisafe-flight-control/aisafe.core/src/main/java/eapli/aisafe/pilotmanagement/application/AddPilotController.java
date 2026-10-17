package eapli.aisafe.pilotmanagement.application;

import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.aircraftmodelmanagement.repositories.AircraftModelRepository;
import eapli.aisafe.generic.domain.PhoneNumber;
import eapli.aisafe.generic.domain.UserName;
import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.organization.domain.CustomerCollaborator;
import eapli.aisafe.organization.domain.CustomerType;
import eapli.aisafe.organization.repositories.AirTransportCompanyRepository;
import eapli.aisafe.organization.repositories.CustomerCollaboratorRepository;
import eapli.aisafe.pilotmanagement.domain.Pilot;
import eapli.aisafe.pilotmanagement.repositories.PilotRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddPilotController {

    private final AuthService authService;
    private final PilotRepository pilotRepository;
    private final UserRepository userRepository;
    private final AircraftModelRepository aircraftModelRepository;
    private final CustomerCollaboratorRepository collaboratorRepository;
    private final AirTransportCompanyRepository companyRepository;

    public AddPilotController(AuthService authService, PilotRepository pilotRepository,
                              UserRepository userRepository, AircraftModelRepository aircraftModelRepository,
                              CustomerCollaboratorRepository collaboratorRepository,
                              AirTransportCompanyRepository companyRepository) {
        this.authService = authService;
        this.pilotRepository = pilotRepository;
        this.userRepository = userRepository;
        this.aircraftModelRepository = aircraftModelRepository;
        this.collaboratorRepository = collaboratorRepository;
        this.companyRepository = companyRepository;
    }

    // Devolve todos os modelos existentes na base de dados para a UI
    public Iterable<AircraftModel> getAvailableAircraftModels() {
        return aircraftModelRepository.findAll();
    }

    public Pilot addPilot(String rawEmail, String name, String rawPhoneNumber, String position, List<AircraftModel> selectedModels, String password) {

        // 1. Verificar Permissões (usando ensureHasAnyRole)
        authService.ensureHasAnyRole(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR);

        // 2. Obter o utilizador autenticado
        User authenticatedUser = authService.currentUser()
                .orElseThrow(() -> new IllegalStateException("Nenhum utilizador com sessão iniciada."));

        // 3. Obter o perfil de colaborador
        CustomerCollaborator collaborator = collaboratorRepository.findByUserEmail(authenticatedUser.email())
                .orElseThrow(() -> new IllegalStateException("Perfil de colaborador não encontrado."));

        // 4. Garantir que o colaborador pertence a uma Air Transport Company
        if (collaborator.customerType() != CustomerType.AIR_TRANSPORT_COMPANY) {
            throw new IllegalStateException("Apenas colaboradores de Companhias de Transporte Aéreo podem adicionar pilotos.");
        }

        // 5. Ir buscar a entidade AirTransportCompany real
        String companyCode = collaborator.customerCode();
        AirTransportCompany company = companyRepository.findByIcaoCode(companyCode)
                .orElseThrow(() -> new IllegalStateException("Companhia não encontrada para o código: " + companyCode));

        Email email = new Email(rawEmail);

        // 6. Verificar duplicação
        if (pilotRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Já existe um piloto registado com este email.");
        }
        if (userRepository.findActiveByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Já existe um utilizador de sistema ativo com este email.");
        }

        // 7. Validar e converter as certificações (Modelos de Avião)
        if (selectedModels == null || selectedModels.isEmpty()) {
            throw new IllegalArgumentException("Um piloto tem de estar certificado para pelo menos um modelo de aeronave.");
        }
        Set<AircraftModel> certifications = new HashSet<>(selectedModels);

        // 8. Criar Value Objects usando os métodos ".of()"
        UserName userName = UserName.of(name);
        PhoneNumber phone = PhoneNumber.of(rawPhoneNumber);

        Set<Role> userRoles = new HashSet<>();
        userRoles.add(Role.PILOT);

        User systemUser = new User(email, password, userName, phone, userRoles);

        // 9. Criar e guardar o Piloto
        Pilot pilot = new Pilot(systemUser, email, name, position, company, certifications);

        return pilotRepository.save(pilot);
    }
}