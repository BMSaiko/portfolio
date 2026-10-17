package eapli.aisafe.flightplanmanagement.application;

import eapli.aisafe.fleetmanagement.domain.Aircraft;
import eapli.aisafe.fleetmanagement.domain.OperationalStatus;
import eapli.aisafe.fleetmanagement.repositories.AircraftRepository;
import eapli.aisafe.flightplanmanagement.domain.FlightPlan;
import eapli.aisafe.flightplanmanagement.domain.FlightPlanId;
import eapli.aisafe.flightplanmanagement.dto.FlightPlanRegistrationDTO;
import eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository;
import eapli.aisafe.pilotmanagement.domain.Pilot;
import eapli.aisafe.pilotmanagement.repositories.PilotRepository;
import eapli.aisafe.routemanagement.domain.FlightRoute;
import eapli.aisafe.routemanagement.repositories.FlightRouteRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Application controller for US080 — Create a Flight Plan (manual entry).
 *
 * <p>Business rules enforced:</p>
 * <ol>
 *   <li>Only users with role {@code PILOT} may invoke this operation.</li>
 *   <li>The route must exist and be available on the departure date.</li>
 *   <li>The aircraft must exist and not be retired/decommissioned.</li>
 *   <li>The pilot must exist and be active.</li>
 *   <li>The pilot must belong to the same company as the route.</li>
 *   <li>Departure date/time must be strictly in the future.</li>
 *   <li>Estimated fuel must be strictly positive (enforced by domain/DTO).</li>
 * </ol>
 *
 * <p>The created flight plan always has status {@link eapli.aisafe.flightplanmanagement.domain.FlightPlanStatus#DRAFT}.</p>
 */
public class CreateFlightPlanController {

    private final AuthService authService;
    private final FlightPlanRepository flightPlanRepository;
    private final FlightRouteRepository routeRepository;
    private final AircraftRepository aircraftRepository;
    private final PilotRepository pilotRepository;

    public CreateFlightPlanController(
            final AuthService authService,
            final FlightPlanRepository flightPlanRepository,
            final FlightRouteRepository routeRepository,
            final AircraftRepository aircraftRepository,
            final PilotRepository pilotRepository) {

        if (authService == null || flightPlanRepository == null || routeRepository == null
                || aircraftRepository == null || pilotRepository == null)
            throw new IllegalArgumentException("Dependencies must not be null.");

        this.authService          = authService;
        this.flightPlanRepository = flightPlanRepository;
        this.routeRepository      = routeRepository;
        this.aircraftRepository   = aircraftRepository;
        this.pilotRepository      = pilotRepository;
    }


    /** Returns all active routes — for the UI to present a selection list. */
    public Iterable<FlightRoute> activeRoutes() {
        authService.ensureHasAnyRole(Role.PILOT);
        return routeRepository.findAll();
    }

    /** Returns all non-retired aircraft — for the UI to present a selection list. */
    public Iterable<Aircraft> availableAircraft() {
        authService.ensureHasAnyRole(Role.PILOT);
        return aircraftRepository.findAll();
    }



    /**
     * Creates and persists a flight plan based on the provided DTO.
     *
     * @param dto the registration data (never null)
     * @return the persisted {@link FlightPlan} in DRAFT status
     * @throws IllegalArgumentException if any business rule is violated
     * @throws IllegalStateException    if a required entity is not found
     */
    public FlightPlan createFlightPlan(final FlightPlanRegistrationDTO dto) {
        authService.ensureHasAnyRole(Role.PILOT);

        // 1 ── Route
        final FlightRoute route = routeRepository.findByDesignator(dto.routeDesignator).orElseThrow(() -> new IllegalStateException(
                        "Route not found: " + dto.routeDesignator));

        if (!route.isAvailableOn(dto.departureDateTime.toLocalDate())) {
            throw new IllegalStateException(
                    "Route " + dto.routeDesignator
                            + " is deactivated from " + route.deactivatedFrom()
                            + " and cannot be used on " + dto.departureDateTime.toLocalDate() + ".");
        }

        // 2 ── Aircraft
        final Aircraft aircraft = aircraftRepository.findByRegistration(dto.aircraftRegistration).orElseThrow(() -> new IllegalStateException("Aircraft not found: " + dto.aircraftRegistration));

        if (aircraft.status() == OperationalStatus.RETIRED)
            throw new IllegalStateException(
                    "Aircraft " + dto.aircraftRegistration + " is decommissioned.");

        // 3 ── Pilot
        final Pilot pilot = pilotRepository.findByEmail(new Email(dto.pilotEmail)).orElseThrow(() -> new IllegalStateException("Pilot not found: " + dto.pilotEmail));

        if (!pilot.isActive())
            throw new IllegalStateException("Pilot " + dto.pilotEmail + " is inactive.");

        // 4 ── Company cross-check (key business rule)
        if (!pilot.company().iataCode().equals(route.company().iataCode()))
            throw new IllegalArgumentException(
                    "Pilot '" + dto.pilotEmail
                            + "' does not belong to the company that owns route '"
                            + dto.routeDesignator + "'.");

        // 5 ── Departure date/time
        if (!dto.departureDateTime.isAfter(LocalDateTime.now()))
            throw new IllegalArgumentException(
                    "Departure date/time must be in the future.");

        // 6 ── Create and persist
        final FlightPlanId id = FlightPlanId.of(UUID.randomUUID().toString());

        final FlightPlan flightPlan = new FlightPlan(
                id,
                route,
                aircraft,
                pilot,
                dto.departureDateTime,
                dto.estimatedFuel);

        return flightPlanRepository.save(flightPlan);
    }
}
