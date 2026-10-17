package eapli.aisafe.fleetmanagement.application;

import eapli.aisafe.fleetmanagement.domain.*;
import eapli.aisafe.fleetmanagement.dto.AircraftRegistrationDTO;
import eapli.aisafe.fleetmanagement.repositories.AircraftRepository;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;
import eapli.aisafe.aircraftmodelmanagement.repositories.AircraftModelRepository;
import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.organization.domain.CustomerCollaborator;
import eapli.aisafe.organization.domain.CustomerType;
import eapli.aisafe.organization.repositories.AirTransportCompanyRepository;
import eapli.aisafe.organization.repositories.CustomerCollaboratorRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.User;

public class AddAircraftController {

    private final AircraftRepository aircraftRepository;
    private final AircraftModelRepository modelRepository;
    private final CustomerCollaboratorRepository collaboratorRepository;
    private final AirTransportCompanyRepository companyRepository;
    private final AuthService authService;

    public AddAircraftController(AircraftRepository aircraftRepository,
                                 AircraftModelRepository modelRepository,
                                 CustomerCollaboratorRepository collaboratorRepository,
                                 AirTransportCompanyRepository companyRepository,
                                 AuthService authService) {
        if (aircraftRepository == null || modelRepository == null || collaboratorRepository == null || companyRepository == null || authService == null) {
            throw new IllegalArgumentException("Dependencies cannot be null.");
        }
        this.aircraftRepository = aircraftRepository;
        this.modelRepository = modelRepository;
        this.collaboratorRepository = collaboratorRepository;
        this.companyRepository = companyRepository;
        this.authService = authService;
    }

    // --- MÉTODOS NOVOS PARA OS MENUS DE SELEÇÃO DA UI ---

    public Iterable<String> getAircraftMakers() {
        return modelRepository.findAllMakers();
    }

    public Iterable<AircraftModel> getModelsByMaker(String makerName) {
        if (makerName == null || makerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Maker name cannot be empty.");
        }
        return modelRepository.findByMaker(makerName);
    }

    // ----------------------------------------------------

    public Aircraft addAircraft(AircraftRegistrationDTO dto) {
        // 1. Obter o utilizador autenticado usando currentUser()
        User user = authService.currentUser()
                .orElseThrow(() -> new IllegalStateException("No active session."));

        // 2. Obter a companhia do colaborador autenticado
        AirTransportCompany company = getAuthenticatedUserCompany(user);

        // 3. Verificar se a matrícula já existe
        if (aircraftRepository.existsByRegistration(dto.registration)) {
            throw new IllegalArgumentException("An aircraft already exists with registration: " + dto.registration);
        }

        // 4. Procurar o modelo usando a fábrica AircraftModelId.of(...) que exige Model ID e Maker Name
        AircraftModelId modelId = AircraftModelId.of(dto.modelId, dto.makerName);
        AircraftModel model = modelRepository.findById(modelId)
                .orElseThrow(() -> new IllegalArgumentException("Aircraft model not found."));

        // 5. Configurar Value Objects e Enums
        CabinConfiguration cabinConfig = new CabinConfiguration(dto.firstClassSeats, dto.businessSeats, dto.economySeats);
        OperationalStatus status = OperationalStatus.valueOf(dto.status.toUpperCase());

        // 6. Criar e guardar a aeronave
        Aircraft newAircraft = new Aircraft(dto.registration, model, company, dto.country, cabinConfig, status, dto.manufacturingYear);
        return aircraftRepository.save(newAircraft);
    }

    private AirTransportCompany getAuthenticatedUserCompany(User user) {
        // Obter o email do utilizador logado
        Email userEmail = (Email) user.identity();

        // Procura o colaborador pelo Email
        CustomerCollaborator collab = collaboratorRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException("Logged user is not registered as a company collaborator."));

        // Verifica se o colaborador pertence de facto a uma Companhia de Transporte Aéreo
        if (collab.customerType() != CustomerType.AIR_TRANSPORT_COMPANY) {
            throw new IllegalStateException("Collaborator does not belong to an Air Transport Company.");
        }

        // Busca a Companhia usando o código ICAO guardado no Colaborador
        return companyRepository.findByIcaoCode(collab.customerCode())
                .orElseThrow(() -> new IllegalStateException("Company not found for code: " + collab.customerCode()));
    }
}