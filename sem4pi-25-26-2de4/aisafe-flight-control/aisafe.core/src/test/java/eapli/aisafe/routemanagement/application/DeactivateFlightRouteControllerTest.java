package eapli.aisafe.routemanagement.application;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.domain.GeoBoundary;
import eapli.aisafe.airportmanagement.domain.Airport;
import eapli.aisafe.airportmanagement.domain.Coordinates;
import eapli.aisafe.flightplanmanagement.domain.FlightPlan;
import eapli.aisafe.flightplanmanagement.domain.FlightPlanId;
import eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository;
import eapli.aisafe.generic.domain.IataCode;
import eapli.aisafe.generic.domain.IcaoCode;
import eapli.aisafe.generic.domain.PhoneNumber;
import eapli.aisafe.generic.domain.UserName;
import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.organization.domain.CustomerCollaborator;
import eapli.aisafe.organization.domain.CustomerType;
import eapli.aisafe.organization.repositories.AirTransportCompanyRepository;
import eapli.aisafe.organization.repositories.CustomerCollaboratorRepository;
import eapli.aisafe.pilotmanagement.domain.Pilot;
import eapli.aisafe.routemanagement.domain.FlightRoute;
import eapli.aisafe.routemanagement.domain.RouteDesignator;
import eapli.aisafe.routemanagement.repositories.FlightRouteRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DeactivateFlightRouteControllerTest {

    private AuthService authService;
    private InMemoryUserRepository userRepo;
    private InMemoryRouteRepository routeRepo;
    private StubFlightPlanRepository flightPlanRepo;
    private InMemoryCustomerCollaboratorRepository collaboratorRepo;
    private InMemoryCompanyRepository companyRepo;
    private DeactivateFlightRouteController controller;

    private AirTransportCompany tap;
    private AirTransportCompany ryanair;
    private FlightRoute tapRoute;

    @BeforeEach
    void setUp() {
        userRepo = new InMemoryUserRepository();
        authService = new AuthService(userRepo);
        routeRepo = new InMemoryRouteRepository();
        flightPlanRepo = new StubFlightPlanRepository();
        collaboratorRepo = new InMemoryCustomerCollaboratorRepository();
        companyRepo = new InMemoryCompanyRepository();

        controller = new DeactivateFlightRouteController(
                routeRepo,
                flightPlanRepo,
                collaboratorRepo,
                companyRepo,
                authService);

        tap = new AirTransportCompany("TAP Air Portugal", "TP", "TAP");
        ryanair = new AirTransportCompany("Ryanair", "FR", "RYR");

        companyRepo.save(tap);
        companyRepo.save(ryanair);

        Email tapUserEmail = new Email("atcc@airline.com");
        userRepo.save(new User(
                tapUserEmail,
                "Atcc1234",
                UserName.of("TAP Collaborator"),
                PhoneNumber.of("123456789"),
                Set.of(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR)
        ));

        collaboratorRepo.save(new CustomerCollaborator(
                CustomerType.AIR_TRANSPORT_COMPANY,
                "TAP",
                tapUserEmail,
                "Operations Collaborator"
        ));

        Email ryanairUserEmail = new Email("ryanair@airline.com");
        userRepo.save(new User(
                ryanairUserEmail,
                "Ryan1234",
                UserName.of("Ryanair Collaborator"),
                PhoneNumber.of("123456788"),
                Set.of(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR)
        ));

        collaboratorRepo.save(new CustomerCollaborator(
                CustomerType.AIR_TRANSPORT_COMPANY,
                "RYR",
                ryanairUserEmail,
                "Fleet Collaborator"
        ));

        Email adminEmail = new Email("admin@aisafe.com");
        userRepo.save(new User(
                adminEmail,
                "Admin123",
                UserName.of("Admin"),
                PhoneNumber.of("123456787"),
                Set.of(Role.ADMIN)
        ));

        collaboratorRepo.save(new CustomerCollaborator(
                CustomerType.AIR_TRANSPORT_COMPANY,
                "TAP",
                adminEmail,
                "Administrator"
        ));

        Email pilotEmail = new Email("pilot@aisafe.com");
        userRepo.save(new User(
                pilotEmail,
                "Pilot123",
                UserName.of("Pilot"),
                PhoneNumber.of("123456786"),
                Set.of(Role.PILOT)
        ));

        tapRoute = new FlightRoute(
                new RouteDesignator("TP777", "TP"),
                tap,
                airport("LIS", "LPPT", "Lisbon"),
                airport("OPO", "LPPR", "Porto")
        );

        routeRepo.save(tapRoute);
        routeRepo.saveCalls = 0;
    }

    @AfterEach
    void tearDown() {
        authService.logout();
    }

    @Test
    void deactivateRouteShouldDeactivateExistingRoute() {
        authService.login("atcc@airline.com", "Atcc1234");

        FlightRoute result = controller.deactivateRoute("TP777", LocalDate.of(2026, 6, 10));

        assertFalse(result.isActive());
        assertEquals(LocalDate.of(2026, 6, 10), result.deactivatedFrom());
        assertEquals(result, routeRepo.findByDesignator("TP777").orElseThrow());
        assertEquals(1, routeRepo.saveCalls);
    }

    @Test
    void deactivateRouteShouldNormalizeDesignator() {
        authService.login("atcc@airline.com", "Atcc1234");

        FlightRoute result = controller.deactivateRoute(" tp777 ", LocalDate.of(2026, 6, 10));

        assertFalse(result.isActive());
        assertEquals(LocalDate.of(2026, 6, 10), result.deactivatedFrom());
    }

    @Test
    void deactivateRouteShouldAllowAdminWhenAdminHasCompanyCollaboratorAssociation() {
        authService.login("admin@aisafe.com", "Admin123");

        FlightRoute result = controller.deactivateRoute("TP777", LocalDate.of(2026, 6, 10));

        assertFalse(result.isActive());
    }

    @Test
    void deactivateRouteShouldThrowWhenUserIsNotAuthenticated() {
        assertThrows(SecurityException.class,
                () -> controller.deactivateRoute("TP777", LocalDate.of(2026, 6, 10)));
    }

    @Test
    void deactivateRouteShouldThrowWhenUserDoesNotHaveRequiredRole() {
        authService.login("pilot@aisafe.com", "Pilot123");

        assertThrows(SecurityException.class,
                () -> controller.deactivateRoute("TP777", LocalDate.of(2026, 6, 10)));
    }

    @Test
    void deactivateRouteShouldThrowWhenDesignatorIsNull() {
        authService.login("atcc@airline.com", "Atcc1234");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.deactivateRoute(null, LocalDate.of(2026, 6, 10)));

        assertEquals("Route designator is required.", ex.getMessage());
    }

    @Test
    void deactivateRouteShouldThrowWhenDesignatorIsBlank() {
        authService.login("atcc@airline.com", "Atcc1234");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.deactivateRoute("   ", LocalDate.of(2026, 6, 10)));

        assertEquals("Route designator is required.", ex.getMessage());
    }

    @Test
    void deactivateRouteShouldThrowWhenDateIsNull() {
        authService.login("atcc@airline.com", "Atcc1234");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.deactivateRoute("TP777", null));

        assertEquals("Deactivation date is required.", ex.getMessage());
    }

    @Test
    void deactivateRouteShouldThrowWhenRouteDoesNotExist() {
        authService.login("atcc@airline.com", "Atcc1234");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.deactivateRoute("TP999", LocalDate.of(2026, 6, 10)));

        assertEquals("Flight route not found: TP999", ex.getMessage());
    }

    @Test
    void deactivateRouteShouldThrowWhenRouteBelongsToAnotherCompany() {
        FlightRoute ryanairRoute = new FlightRoute(
                new RouteDesignator("FR123", "FR"),
                ryanair,
                airport("LIS", "LPPT", "Lisbon"),
                airport("MAD", "LEMD", "Madrid")
        );
        routeRepo.save(ryanairRoute);

        authService.login("atcc@airline.com", "Atcc1234");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> controller.deactivateRoute("FR123", LocalDate.of(2026, 6, 10)));

        assertEquals("Route FR123 does not belong to the authenticated company.", ex.getMessage());
        assertTrue(ryanairRoute.isActive());
    }

    @Test
    void deactivateRouteShouldThrowWhenThereArePlannedFlightsFromDateOnwards() {
        authService.login("atcc@airline.com", "Atcc1234");
        flightPlanRepo.existsPlannedForRouteFrom = true;

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> controller.deactivateRoute("TP777", LocalDate.of(2026, 6, 10)));

        assertEquals(
                "Route TP777 cannot be deactivated because there are planned flights from 2026-06-10 onwards.",
                ex.getMessage());
        assertTrue(tapRoute.isActive());
    }

    @Test
    void deactivateRouteShouldThrowWhenRouteIsAlreadyInactive() {
        authService.login("atcc@airline.com", "Atcc1234");

        controller.deactivateRoute("TP777", LocalDate.of(2026, 6, 10));

        assertThrows(IllegalStateException.class,
                () -> controller.deactivateRoute("TP777", LocalDate.of(2026, 6, 11)));
    }

    private Airport airport(String iata, String icao, String name) {
        AirControlArea area = new AirControlArea(
                AirControlAreaCode.of("LPPC"),
                "Lisbon FIR",
                5000.0,
                new GeoBoundary(47.0, 36.0, -6.0, -20.0)
        );

        return new Airport(
                IataCode.of(iata),
                IcaoCode.of(icao),
                name,
                name,
                "Portugal",
                new Coordinates(38.0, -9.0, 100),
                area
        );
    }

    private static class InMemoryUserRepository implements UserRepository {
        private final Map<String, User> users = new HashMap<>();

        @Override
        public Optional<User> findByEmail(Email email) {
            return Optional.ofNullable(users.get(email.value()));
        }

        @Override
        public Optional<User> findActiveByEmail(Email email) {
            User user = users.get(email.value());
            return user != null && user.isActive() ? Optional.of(user) : Optional.empty();
        }

        @Override
        public Iterable<User> findActiveByRole(Role role) {
            return users.values().stream()
                    .filter(User::isActive)
                    .filter(user -> user.hasRole(role))
                    .toList();
        }

        @Override
        public boolean existsByEmail(Email email) {
            return users.containsKey(email.value());
        }

        @Override
        public User save(User entity) {
            users.put(entity.email().value(), entity);
            return entity;
        }

        @Override
        public Iterable<User> findAll() {
            return new ArrayList<>(users.values());
        }

        @Override
        public Optional<User> findById(Email email) {
            return findByEmail(email);
        }

        @Override
        public void delete(User entity) {
            users.remove(entity.email().value());
        }

        @Override
        public void deleteById(Email email) {
            users.remove(email.value());
        }

        @Override
        public long count() {
            return users.size();
        }
    }

    private static class InMemoryRouteRepository implements FlightRouteRepository {
        private final Map<String, FlightRoute> routes = new HashMap<>();
        private int saveCalls = 0;

        @Override
        public Optional<FlightRoute> findByDesignator(String designatorValue) {
            return Optional.ofNullable(routes.get(designatorValue.trim().toUpperCase()));
        }

        @Override
        public boolean existsByDesignator(String designatorValue) {
            return routes.containsKey(designatorValue.trim().toUpperCase());
        }

        @Override
        public List<FlightRoute> findActiveByCompany(AirTransportCompany company) {
            return routes.values().stream()
                    .filter(route -> route.company().icaoCode().equalsIgnoreCase(company.icaoCode()))
                    .filter(FlightRoute::isActive)
                    .toList();
        }

        @Override
        public List<FlightRoute> findAllByCompany(AirTransportCompany company) {
            return routes.values().stream()
                    .filter(route -> route.company().icaoCode().equalsIgnoreCase(company.icaoCode()))
                    .toList();
        }

        @Override
        public FlightRoute save(FlightRoute route) {
            saveCalls++;
            routes.put(route.identity().value(), route);
            return route;
        }

        @Override
        public Iterable<FlightRoute> findAll() {
            return new ArrayList<>(routes.values());
        }

        @Override
        public void delete(FlightRoute route) {
            routes.remove(route.identity().value());
        }
    }

    private static class StubFlightPlanRepository implements FlightPlanRepository {
        private boolean existsPlannedForRouteFrom = false;

        @Override
        public Optional<FlightPlan> findById(FlightPlanId id) {
            return Optional.empty();
        }

        @Override
        public Iterable<FlightPlan> findAll() {
            return List.of();
        }

        @Override
        public FlightPlan save(FlightPlan flightPlan) {
            return flightPlan;
        }

        @Override
        public void deleteById(FlightPlanId id) {
        }

        @Override
        public long count() {
            return 0;
        }

        @Override
        public boolean existsPlannedForRouteFrom(FlightRoute route, LocalDate date) {
            return existsPlannedForRouteFrom;
        }

        // CORRIGIDO: Implementação do método em falta exigido pelo compilador
        @Override
        public List<FlightPlan> findByPilot(Pilot pilot) {
            return List.of(); // Para o teste da rota, não precisamos de dados de pilotos
        }

        // CORRIGIDO: Implementação do método em falta exigido pelo compilador
        @Override
        public long countByPilot(Pilot pilot) {
            return 0;
        }

        @Override
        public List<FlightPlan> findByRoute(FlightRoute route) {
            return List.of();
        }
    }

    private static class InMemoryCustomerCollaboratorRepository implements CustomerCollaboratorRepository {
        private final Map<Long, CustomerCollaborator> byId = new HashMap<>();
        private final Map<String, CustomerCollaborator> byEmail = new HashMap<>();
        private long nextId = 1;

        @Override
        public Optional<CustomerCollaborator> findById(Long id) {
            return Optional.ofNullable(byId.get(id));
        }

        @Override
        public Optional<CustomerCollaborator> findByUserEmail(Email email) {
            return Optional.ofNullable(byEmail.get(email.value()));
        }

        @Override
        public Iterable<CustomerCollaborator> findByCustomer(CustomerType customerType, String customerCode) {
            return byEmail.values().stream()
                    .filter(c -> c.customerType() == customerType)
                    .filter(c -> c.customerCode().equalsIgnoreCase(customerCode))
                    .toList();
        }

        @Override
        public CustomerCollaborator save(CustomerCollaborator entity) {
            byId.put(nextId++, entity);
            byEmail.put(entity.userEmail().value(), entity);
            return entity;
        }

        @Override
        public Iterable<CustomerCollaborator> findAll() {
            return new ArrayList<>(byEmail.values());
        }

        @Override
        public void delete(CustomerCollaborator entity) {
            byEmail.remove(entity.userEmail().value());
        }

        @Override
        public void deleteById(Long id) {
            byId.remove(id);
        }

        @Override
        public long count() {
            return byEmail.size();
        }
    }

    private static class InMemoryCompanyRepository implements AirTransportCompanyRepository {
        private final Map<Long, AirTransportCompany> byId = new HashMap<>();
        private final Map<String, AirTransportCompany> byIata = new HashMap<>();
        private final Map<String, AirTransportCompany> byIcao = new HashMap<>();
        private long nextId = 1;

        @Override
        public Optional<AirTransportCompany> findByIataCode(String iataCode) {
            return Optional.ofNullable(byIata.get(iataCode.trim().toUpperCase()));
        }

        @Override
        public Optional<AirTransportCompany> findByIcaoCode(String icaoCode) {
            return Optional.ofNullable(byIcao.get(icaoCode.trim().toUpperCase()));
        }

        @Override
        public boolean existsByIataCode(String iataCode) {
            return byIata.containsKey(iataCode.trim().toUpperCase());
        }

        @Override
        public boolean existsByIcaoCode(String icaoCode) {
            return byIcao.containsKey(icaoCode.trim().toUpperCase());
        }

        @Override
        public AirTransportCompany save(AirTransportCompany entity) {
            byId.put(nextId++, entity);
            byIata.put(entity.iataCode(), entity);
            byIcao.put(entity.icaoCode(), entity);
            return entity;
        }

        @Override
        public Iterable<AirTransportCompany> findAll() {
            return new ArrayList<>(byIcao.values());
        }

        @Override
        public Optional<AirTransportCompany> findById(Long id) {
            return Optional.ofNullable(byId.get(id));
        }

        @Override
        public void delete(AirTransportCompany entity) {
            byIata.remove(entity.iataCode());
            byIcao.remove(entity.icaoCode());
        }

        @Override
        public void deleteById(Long id) {
            byId.remove(id);
        }

        @Override
        public long count() {
            return byIcao.size();
        }
    }
}
