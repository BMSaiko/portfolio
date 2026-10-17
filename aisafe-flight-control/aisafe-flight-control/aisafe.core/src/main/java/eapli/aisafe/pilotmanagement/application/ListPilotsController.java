package eapli.aisafe.pilotmanagement.application;

import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.organization.domain.CustomerCollaborator;
import eapli.aisafe.organization.domain.CustomerType;
import eapli.aisafe.organization.repositories.AirTransportCompanyRepository;
import eapli.aisafe.organization.repositories.CustomerCollaboratorRepository;
import eapli.aisafe.pilotmanagement.domain.Pilot;
import eapli.aisafe.pilotmanagement.repositories.PilotRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;

public class ListPilotsController {

    private final AuthService authService;
    private final PilotRepository pilotRepository;
    private final CustomerCollaboratorRepository collaboratorRepository;
    private final AirTransportCompanyRepository companyRepository;

    public ListPilotsController(AuthService authService,
                                PilotRepository pilotRepository,
                                CustomerCollaboratorRepository collaboratorRepository,
                                AirTransportCompanyRepository companyRepository) {
        this.authService = authService;
        this.pilotRepository = pilotRepository;
        this.collaboratorRepository = collaboratorRepository;
        this.companyRepository = companyRepository;
    }

    public Iterable<Pilot> listPilotsOfMyCompany() {
        // 1. Garantir que é um colaborador de companhia
        authService.ensureHasAnyRole(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR);

        // 2. Ir buscar o utilizador com login feito
        User authenticatedUser = authService.currentUser()
                .orElseThrow(() -> new IllegalStateException("No user is currently logged in."));

        // 3. Descobrir o perfil de colaborador dele
        CustomerCollaborator collaborator = collaboratorRepository.findByUserEmail(authenticatedUser.email())
                .orElseThrow(() -> new IllegalStateException("Collaborator profile not found."));

        if (collaborator.customerType() != CustomerType.AIR_TRANSPORT_COMPANY) {
            throw new IllegalStateException("Only Air Transport Company collaborators can list pilots.");
        }

        // 4. Ir buscar a Companhia verdadeira
        String companyCode = collaborator.customerCode();
        AirTransportCompany company = companyRepository.findByIcaoCode(companyCode)
                .orElseThrow(() -> new IllegalStateException("Company not found for code: " + companyCode));

        // 5. O momento da magia: Devolve todos os pilotos daquela companhia!
        return pilotRepository.findByCompany(company);
    }
}