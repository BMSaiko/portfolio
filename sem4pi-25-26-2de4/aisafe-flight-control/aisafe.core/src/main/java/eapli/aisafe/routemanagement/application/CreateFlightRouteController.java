package eapli.aisafe.routemanagement.application;

import eapli.aisafe.airportmanagement.domain.Airport;
import eapli.aisafe.generic.domain.IataCode;
import eapli.aisafe.airportmanagement.repositories.AirportRepository;
import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.organization.domain.CustomerCollaborator;
import eapli.aisafe.organization.domain.CustomerType;
import eapli.aisafe.organization.repositories.AirTransportCompanyRepository;
import eapli.aisafe.organization.repositories.CustomerCollaboratorRepository;
import eapli.aisafe.routemanagement.domain.FlightRoute;
import eapli.aisafe.routemanagement.domain.RouteDesignator;
import eapli.aisafe.routemanagement.dto.FlightRouteRegistrationDTO;
import eapli.aisafe.routemanagement.repositories.FlightRouteRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.User;

public class CreateFlightRouteController {

    private final FlightRouteRepository         routeRepository;
    private final AirportRepository             airportRepository;
    private final CustomerCollaboratorRepository collaboratorRepository;
    private final AirTransportCompanyRepository  companyRepository;
    private final AuthService                    authService;

    public CreateFlightRouteController(
            final FlightRouteRepository          routeRepository,
            final AirportRepository              airportRepository,
            final CustomerCollaboratorRepository collaboratorRepository,
            final AirTransportCompanyRepository  companyRepository,
            final AuthService                    authService) {

        if (routeRepository == null || airportRepository == null
                || collaboratorRepository == null
                || companyRepository == null || authService == null) {
            throw new IllegalArgumentException("Dependencies cannot be null.");
        }

        this.routeRepository       = routeRepository;
        this.airportRepository     = airportRepository;
        this.collaboratorRepository = collaboratorRepository;
        this.companyRepository     = companyRepository;
        this.authService           = authService;
    }

    public Iterable<Airport> getAvailableAirports() {
        return airportRepository.findAll();
    }

    public FlightRoute createRoute(final FlightRouteRegistrationDTO dto) {
        final User user = authService.currentUser()
                .orElseThrow(() -> new IllegalStateException("No active session."));

        final AirTransportCompany company = getAuthenticatedUserCompany(user);

        if (routeRepository.existsByDesignator(dto.designator.trim().toUpperCase())) {
            throw new IllegalArgumentException(
                    "A route with designator '" + dto.designator.trim().toUpperCase()
                            + "' already exists.");
        }

        final Airport origin = airportRepository
                .findByIataCode(IataCode.of(dto.originIataCode))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Origin airport with IATA code '" + dto.originIataCode + "' not found."));

        final Airport destination = airportRepository
                .findByIataCode(IataCode.of(dto.destinationIataCode))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Destination airport with IATA code '" + dto.destinationIataCode
                                + "' not found."));

        final RouteDesignator designator =
                new RouteDesignator(dto.designator, company.iataCode());

        final FlightRoute route = new FlightRoute(designator, company, origin, destination);

        return routeRepository.save(route);
    }

    private AirTransportCompany getAuthenticatedUserCompany(final User user) {
        final Email userEmail = (Email) user.identity();

        final CustomerCollaborator collab = collaboratorRepository
                .findByUserEmail(userEmail)
                .orElseThrow(() -> new IllegalStateException(
                        "Logged user is not registered as a company collaborator."));

        if (collab.customerType() != CustomerType.AIR_TRANSPORT_COMPANY) {
            throw new IllegalStateException(
                    "Collaborator does not belong to an Air Transport Company.");
        }

        return companyRepository.findByIcaoCode(collab.customerCode())
                .orElseThrow(() -> new IllegalStateException(
                        "Company not found for code: " + collab.customerCode()));
    }
}
