package eapli.aisafe.pilotmanagement.application;

import eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository;
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

import java.util.ArrayList;
import java.util.List;

public class RemovePilotController {

    private final AuthService authService;
    private final PilotRepository pilotRepository;
    private final CustomerCollaboratorRepository collaboratorRepository;
    private final AirTransportCompanyRepository companyRepository;
    private final FlightPlanRepository flightPlanRepository;

    public RemovePilotController(AuthService authService,
                                 PilotRepository pilotRepository,
                                 CustomerCollaboratorRepository collaboratorRepository,
                                 AirTransportCompanyRepository companyRepository,
                                 FlightPlanRepository flightPlanRepository) {
        this.authService = authService;
        this.pilotRepository = pilotRepository;
        this.collaboratorRepository = collaboratorRepository;
        this.companyRepository = companyRepository;
        this.flightPlanRepository = flightPlanRepository;
    }

    public Iterable<Pilot> activePilotsOfMyCompany() {
        authService.ensureHasAnyRole(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR);
        User authenticatedUser = authService.currentUser().orElseThrow(() -> new IllegalStateException("No user logged in."));
        CustomerCollaborator collaborator = collaboratorRepository.findByUserEmail(authenticatedUser.email())
                .orElseThrow(() -> new IllegalStateException("Collaborator not found."));

        if (collaborator.customerType() != CustomerType.AIR_TRANSPORT_COMPANY) {
            throw new IllegalStateException("Only Air Transport Company collaborators can remove pilots.");
        }

        AirTransportCompany company = companyRepository.findByIcaoCode(collaborator.customerCode())
                .orElseThrow(() -> new IllegalStateException("Company not found."));

        // Retorna APENAS os pilotos que estão ACTIVE
        List<Pilot> activePilots = new ArrayList<>();
        for (Pilot p : pilotRepository.findByCompany(company)) {
            if (p.isActive()) {
                activePilots.add(p);
            }
        }
        return activePilots;
    }

    public Pilot deactivatePilot(Pilot pilot) {
        authService.ensureHasAnyRole(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR);

        // A REGRA DE OURO DA US077: Não desativar se tiver voos!
        if (flightPlanRepository.countByPilot(pilot) > 0) {
            throw new IllegalStateException("Cannot deactivate a pilot with assigned flight plans.");
        }

        pilot.deactivate();
        return pilotRepository.save(pilot);
    }
}