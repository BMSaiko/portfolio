package eapli.aisafe.fleetmanagement.application;

import eapli.aisafe.fleetmanagement.domain.Aircraft;
import eapli.aisafe.fleetmanagement.repositories.AircraftRepository;
import eapli.aisafe.organization.domain.CustomerCollaborator;
import eapli.aisafe.organization.repositories.CustomerCollaboratorRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;

import java.util.ArrayList;
import java.util.List;

public class ListFleetController {

    private final AircraftRepository aircraftRepository;
    private final CustomerCollaboratorRepository collaboratorRepository;
    private final AuthService authService;

    public ListFleetController(AircraftRepository aircraftRepository,
                               CustomerCollaboratorRepository collaboratorRepository,
                               AuthService authService) {
        if (aircraftRepository == null || collaboratorRepository == null || authService == null) {
            throw new IllegalArgumentException("Repositories and AuthService are mandatory.");
        }
        this.aircraftRepository = aircraftRepository;
        this.collaboratorRepository = collaboratorRepository;
        this.authService = authService;
    }

    /**
     * US072 - List the entire fleet of the logged-in collaborator's company.
     */
    public Iterable<Aircraft> getMyCompanyFleet() {
        // 1. Garantir que é um colaborador
        authService.ensureHasAnyRole(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR);

        // 2. Descobrir quem está logado
        User currentUser = authService.currentUser().orElseThrow(() -> new IllegalStateException("No active session found."));

        // 3. Ir buscar os dados do colaborador através do email
        CustomerCollaborator collab = collaboratorRepository.findByUserEmail(currentUser.email()).orElseThrow(() -> new IllegalStateException("Logged-in user is not registered as a Customer Collaborator."));

        // 4. Descobrir a Companhia e ir buscar a frota!
        String companyCode = collab.customerCode();

        return aircraftRepository.findByCompany(companyCode);
    }

    /**
     * US072a - Filter by Model
     */
    public List<Aircraft> getFleetByModel(String modelName) {
        List<Aircraft> filteredList = new ArrayList<>();

        for (Aircraft aircraft : getMyCompanyFleet()) {
            String currentModelName = aircraft.model().identity().modelId();

            if (currentModelName.equalsIgnoreCase(modelName)) {
                filteredList.add(aircraft);
            }
        }
        return filteredList;
    }

    /**
     * US072b - Filter by Maker
     */
    public List<Aircraft> getFleetByMaker(String makerName) {
        List<Aircraft> filteredList = new ArrayList<>();

        for (Aircraft aircraft : getMyCompanyFleet()) {
            String currentMaker = aircraft.model().identity().makerName();

            if (currentMaker.equalsIgnoreCase(makerName)) {
                filteredList.add(aircraft);
            }
        }
        return filteredList;
    }

    /**
     * US072c - Filter by Capacity
     */
    public List<Aircraft> getFleetByCapacity(int numberOfPassengers) {
        List<Aircraft> filteredList = new ArrayList<>();

        for (Aircraft aircraft : getMyCompanyFleet()) {
            if (aircraft.cabinConfiguration().totalSeats() == numberOfPassengers) {
                filteredList.add(aircraft);
            }
        }
        return filteredList;
    }

    /**
     * US072d - Filter by Age
     */
    public List<Aircraft> getFleetByAge(int ageInYears) {
        List<Aircraft> filteredList = new ArrayList<>();

        for (Aircraft aircraft : getMyCompanyFleet()) {
            if (aircraft.age() == ageInYears) {
                filteredList.add(aircraft);
            }
        }
        return filteredList;
    }
}