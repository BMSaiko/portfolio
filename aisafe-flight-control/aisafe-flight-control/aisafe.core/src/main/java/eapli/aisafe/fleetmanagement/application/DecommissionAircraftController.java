package eapli.aisafe.fleetmanagement.application;

import eapli.aisafe.fleetmanagement.domain.Aircraft;
import eapli.aisafe.fleetmanagement.repositories.AircraftRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Role;

import java.util.Optional;

public class DecommissionAircraftController {

    private final AircraftRepository aircraftRepository;
    private final AuthService authService;

    public DecommissionAircraftController(AircraftRepository aircraftRepository, AuthService authService) {
        if (aircraftRepository == null || authService == null) {
            throw new IllegalArgumentException("AircraftRepository and AuthService are mandatory.");
        }
        this.aircraftRepository = aircraftRepository;
        this.authService = authService;
    }

    public Aircraft decommissionAircraft(String registration) {
        // 1. Garantir que o utilizador logado tem as permissões certas
        authService.ensureHasAnyRole(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR);

        // 2. Procurar o avião usando EXCLUSIVAMENTE o ofIdentity() da framework
        Optional<Aircraft> optAircraft = aircraftRepository.ofIdentity(registration);

        if (optAircraft.isEmpty()) {
            throw new IllegalArgumentException("No aircraft found with registration: " + registration);
        }

        Aircraft aircraft = optAircraft.get();

        // 3. Retirar a aeronave (A entidade lança erro se já estiver retirada)
        aircraft.decommission();

        // 4. Guardar as alterações na Base de Dados
        return aircraftRepository.save(aircraft);
    }
}