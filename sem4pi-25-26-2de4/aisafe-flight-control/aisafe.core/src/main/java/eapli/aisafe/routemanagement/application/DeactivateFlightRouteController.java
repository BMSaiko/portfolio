package eapli.aisafe.routemanagement.application;

import eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository;
import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.organization.domain.CustomerCollaborator;
import eapli.aisafe.organization.domain.CustomerType;
import eapli.aisafe.organization.repositories.AirTransportCompanyRepository;
import eapli.aisafe.organization.repositories.CustomerCollaboratorRepository;
import eapli.aisafe.routemanagement.domain.FlightRoute;
import eapli.aisafe.routemanagement.repositories.FlightRouteRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;

import java.time.LocalDate;

/**
 * Application controller responsible for deactivating flight routes.
 *
 * <p>
 * This controller implements the application logic for US074. It allows an air
 * transport company collaborator to deactivate one of their company's flight
 * routes from a given date onwards.
 * </p>
 *
 * <p>
 * The controller enforces the main business rules of the use case:
 * </p>
 *
 * <ul>
 *     <li>Only air transport company collaborators or administrators can
 *     execute the operation;</li>
 *     <li>The selected route must exist;</li>
 *     <li>The selected route must belong to the authenticated collaborator's
 *     company;</li>
 *     <li>The route cannot be deactivated if there are planned flights from the
 *     deactivation date onwards.</li>
 * </ul>
 */
public class DeactivateFlightRouteController {

    /**
     * Repository used to find and persist flight routes.
     */
    private final FlightRouteRepository routeRepository;

    /**
     * Repository used to check whether planned flights exist for the route after
     * the requested deactivation date.
     */
    private final FlightPlanRepository flightPlanRepository;

    /**
     * Repository used to resolve the collaborator associated with the
     * authenticated user.
     */
    private final CustomerCollaboratorRepository collaboratorRepository;

    /**
     * Repository used to resolve the air transport company associated with the
     * authenticated collaborator.
     */
    private final AirTransportCompanyRepository companyRepository;

    /**
     * Authentication and authorization service.
     */
    private final AuthService authService;

    /**
     * Creates the route deactivation controller.
     *
     * @param routeRepository repository used to access flight routes
     * @param flightPlanRepository repository used to check planned flights
     * @param collaboratorRepository repository used to access collaborators
     * @param companyRepository repository used to access air transport companies
     * @param authService authentication and authorization service
     * @throws IllegalArgumentException if any dependency is {@code null}
     */
    public DeactivateFlightRouteController(
            final FlightRouteRepository routeRepository,
            final FlightPlanRepository flightPlanRepository,
            final CustomerCollaboratorRepository collaboratorRepository,
            final AirTransportCompanyRepository companyRepository,
            final AuthService authService) {

        if (routeRepository == null || flightPlanRepository == null
                || collaboratorRepository == null || companyRepository == null
                || authService == null) {
            throw new IllegalArgumentException("Dependencies cannot be null.");
        }

        this.routeRepository = routeRepository;
        this.flightPlanRepository = flightPlanRepository;
        this.collaboratorRepository = collaboratorRepository;
        this.companyRepository = companyRepository;
        this.authService = authService;
    }

    /**
     * Deactivates a flight route from a given date onwards.
     *
     * <p>
     * The route designator is normalized by trimming whitespace and converting
     * it to uppercase before lookup. If all validations pass, the route is
     * deactivated and persisted.
     * </p>
     *
     * @param designator textual route designator
     * @param deactivationDate date from which the route becomes unavailable
     * @return the saved deactivated route
     * @throws SecurityException if the authenticated user does not have a valid
     *                           role
     * @throws IllegalArgumentException if the route designator is {@code null}
     *                                  or blank
     * @throws IllegalArgumentException if the deactivation date is {@code null}
     * @throws IllegalArgumentException if the route does not exist
     * @throws IllegalArgumentException if the route does not belong to the
     *                                  authenticated collaborator's company
     * @throws IllegalStateException if no active session exists
     * @throws IllegalStateException if the authenticated user is not registered
     *                               as an air transport company collaborator
     * @throws IllegalStateException if there are planned flights from the
     *                               deactivation date onwards
     */
    public FlightRoute deactivateRoute(final String designator, final LocalDate deactivationDate) {
        authService.ensureHasAnyRole(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR, Role.ADMIN);

        if (designator == null || designator.isBlank()) {
            throw new IllegalArgumentException("Route designator is required.");
        }

        if (deactivationDate == null) {
            throw new IllegalArgumentException("Deactivation date is required.");
        }

        final User user = authService.currentUser()
                .orElseThrow(() -> new IllegalStateException("No active session."));

        final AirTransportCompany company = getAuthenticatedUserCompany(user);
        final String normalizedDesignator = designator.trim().toUpperCase();

        final FlightRoute route = routeRepository.findByDesignator(normalizedDesignator)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Flight route not found: " + normalizedDesignator));

        if (!route.company().icaoCode().equalsIgnoreCase(company.icaoCode())) {
            throw new IllegalArgumentException(
                    "Route " + normalizedDesignator
                            + " does not belong to the authenticated company.");
        }

        if (flightPlanRepository.existsPlannedForRouteFrom(route, deactivationDate)) {
            throw new IllegalStateException(
                    "Route " + normalizedDesignator
                            + " cannot be deactivated because there are planned flights from "
                            + deactivationDate + " onwards.");
        }

        route.deactivateFrom(deactivationDate);
        return routeRepository.save(route);
    }

    /**
     * Resolves the air transport company associated with the authenticated user.
     *
     * <p>
     * The user must be registered as a customer collaborator whose customer type
     * is {@link CustomerType#AIR_TRANSPORT_COMPANY}. The collaborator customer
     * code is then used to find the corresponding company by ICAO code.
     * </p>
     *
     * @param user authenticated user
     * @return air transport company associated with the user
     * @throws IllegalStateException if the user is not registered as a
     *                               collaborator
     * @throws IllegalStateException if the collaborator is not associated with
     *                               an air transport company
     * @throws IllegalStateException if no company exists for the collaborator
     *                               customer code
     */
    private AirTransportCompany getAuthenticatedUserCompany(final User user) {
        final Email userEmail = user.identity();

        final CustomerCollaborator collaborator = collaboratorRepository
                .findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException(
                        "Logged user is not registered as a company collaborator."));

        if (collaborator.customerType() != CustomerType.AIR_TRANSPORT_COMPANY) {
            throw new IllegalStateException(
                    "Collaborator does not belong to an Air Transport Company.");
        }

        return companyRepository.findByIcaoCode(collaborator.customerCode())
                .orElseThrow(() -> new IllegalStateException(
                        "Company not found for code: " + collaborator.customerCode()));
    }
}