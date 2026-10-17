package eapli.aisafe.flightplanmanagement.application;

import eapli.aisafe.flightplanmanagement.domain.FlightPlan;
import eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * Application controller for listing flight plans.
 */
public class ListFlightPlanController {

    private final AuthService authService;
    private final FlightPlanRepository flightPlanRepository;

    public ListFlightPlanController(final AuthService authService,
                                    final FlightPlanRepository flightPlanRepository) {
        this.authService = authService;
        this.flightPlanRepository = flightPlanRepository;
    }

    /**
     * Lists all flight plans.
     *
     * @return list of all flight plans
     */
    public List<FlightPlan> listAll() {
        authService.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);
        final List<FlightPlan> result = new ArrayList<>();
        flightPlanRepository.findAll().forEach(result::add);
        return result;
    }
}
