package eapli.aisafe.routemanagement.application;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.domain.GeoBoundary;
import eapli.aisafe.airportmanagement.domain.Airport;
import eapli.aisafe.airportmanagement.domain.Coordinates;
import eapli.aisafe.airportmanagement.repositories.AirportRepository;
import eapli.aisafe.generic.domain.IataCode;
import eapli.aisafe.generic.domain.IcaoCode;
import eapli.aisafe.generic.domain.PhoneNumber;
import eapli.aisafe.generic.domain.UserName;
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
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CreateFlightRouteController} — US073.
 *
 * <p>Uses in-memory repository stubs following the same pattern as
 * {@code DeactivateFlightRouteControllerTest}.</p>
 */
class CreateFlightRouteControllerTest {

    private AuthService authService;
    private InMemoryUserRepository userRepo;
    private InMemoryRouteRepository routeRepo;
    private InMemoryAirportRepository airportRepo;
    private InMemoryCollaboratorRepository collaboratorRepo;
    private InMemoryCompanyRepository companyRepo;
    private CreateFlightRouteController controller;

    private AirTransportCompany tap;
    private Airport lisbon;
    private Airport porto;

    @BeforeEach
    void setUp() {
        userRepo        = new InMemoryUserRepository();
        authService     = new AuthService(userRepo);
        routeRepo       = new InMemoryRouteRepository();
        airportRepo     = new InMemoryAirportRepository();
        collaboratorRepo = new InMemoryCollaboratorRepository();
        companyRepo     = new InMemoryCompanyRepository();

        controller = new CreateFlightRouteController(
                routeRepo, airportRepo, collaboratorRepo, companyRepo, authService);

        tap = new AirTransportCompany("TAP Air Portugal", "TP", "TAP");
        companyRepo.save(tap);

        AirControlArea area = new AirControlArea(
                AirControlAreaCode.of("LPPC"), "Lisbon FIR", 500.0,
                new GeoBoundary(47.0, 33.0, 5.0, -18.0));

        lisbon = new Airport(IataCode.of("LIS"), IcaoCode.of("LPPT"),
                "Humberto Delgado", "Lisbon", "Portugal",
                new Coordinates(38.7742, -9.1342, 113), area);

        porto = new Airport(IataCode.of("OPO"), IcaoCode.of("LPPR"),
                "Francisco Sá Carneiro", "Porto", "Portugal",
                new Coordinates(41.2481, -8.6814, 69), area);

        airportRepo.save(lisbon);
        airportRepo.save(porto);

        // TAP collaborator
        Email tapEmail = new Email("collab@tap.pt");
        userRepo.save(new User(tapEmail, "Collab123",
                UserName.of("TAP Collaborator"), PhoneNumber.of("351910000000"),
                Set.of(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR)));
        collaboratorRepo.save(new CustomerCollaborator(
                CustomerType.AIR_TRANSPORT_COMPANY, "TAP", tapEmail, "Operations"));

        // Ryanair collaborator (for cross-company tests)
        AirTransportCompany ryanair = new AirTransportCompany("Ryanair", "FR", "RYR");
        companyRepo.save(ryanair);
        Email ryrEmail = new Email("collab@ryr.pt");
        userRepo.save(new User(ryrEmail, "Ryr12345",
                UserName.of("Ryanair Collaborator"), PhoneNumber.of("351910000001"),
                Set.of(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR)));
        collaboratorRepo.save(new CustomerCollaborator(
                CustomerType.AIR_TRANSPORT_COMPANY, "RYR", ryrEmail, "Fleet"));

        // Pilot (wrong role)
        Email pilotEmail = new Email("pilot@aisafe.com");
        userRepo.save(new User(pilotEmail, "Pilot123",
                UserName.of("Pilot"), PhoneNumber.of("351910000002"),
                Set.of(Role.PILOT)));
    }

    @AfterEach
    void tearDown() {
        authService.logout();
    }

    // -----------------------------------------------------------------------
    // Happy path
    // -----------------------------------------------------------------------

    @Test
    void createRouteSuccessfully() {
        authService.login("collab@tap.pt", "Collab123");

        FlightRoute result = controller.createRoute(
                new FlightRouteRegistrationDTO("TP123", "LIS", "OPO"));

        assertNotNull(result);
        assertEquals("TP123", result.identity().value());
        assertTrue(result.isActive());
        assertEquals(result, routeRepo.findByDesignator("TP123").orElseThrow());
        assertEquals(1, routeRepo.saveCalls);
    }

    @Test
    void getAvailableAirportsReturnsSavedAirports() {
        List<Airport> airports = new ArrayList<>();
        controller.getAvailableAirports().forEach(airports::add);
        assertEquals(2, airports.size());
    }

    // -----------------------------------------------------------------------
    // Authentication
    // -----------------------------------------------------------------------

    @Test
    void noActiveSessionThrows() {
        // Not logged in
        assertThrows(IllegalStateException.class, () ->
                controller.createRoute(new FlightRouteRegistrationDTO("TP1", "LIS", "OPO")));
    }

    // -----------------------------------------------------------------------
    // Collaborator / company resolution
    // -----------------------------------------------------------------------

    @Test
    void collaboratorNotFromAirTransportCompanyThrows() {
        authService.login("pilot@aisafe.com", "Pilot123");

        // Pilot has no CustomerCollaborator entry → not a company collaborator
        assertThrows(IllegalStateException.class, () ->
                controller.createRoute(new FlightRouteRegistrationDTO("TP1", "LIS", "OPO")));
    }

    // -----------------------------------------------------------------------
    // Duplicate designator
    // -----------------------------------------------------------------------

    @Test
    void duplicateDesignatorIsRejected() {
        authService.login("collab@tap.pt", "Collab123");

        controller.createRoute(new FlightRouteRegistrationDTO("TP123", "LIS", "OPO"));
        int savesAfterFirst = routeRepo.saveCalls;

        assertThrows(IllegalArgumentException.class, () ->
                controller.createRoute(new FlightRouteRegistrationDTO("TP123", "LIS", "OPO")));

        assertEquals(savesAfterFirst, routeRepo.saveCalls); // no extra save
    }

    // -----------------------------------------------------------------------
    // Airport not found
    // -----------------------------------------------------------------------

    @Test
    void originAirportNotFoundThrows() {
        authService.login("collab@tap.pt", "Collab123");

        assertThrows(IllegalArgumentException.class, () ->
                controller.createRoute(new FlightRouteRegistrationDTO("TP123", "XXX", "OPO")));

        assertEquals(0, routeRepo.saveCalls);
    }

    @Test
    void destinationAirportNotFoundThrows() {
        authService.login("collab@tap.pt", "Collab123");

        assertThrows(IllegalArgumentException.class, () ->
                controller.createRoute(new FlightRouteRegistrationDTO("TP123", "LIS", "XXX")));

        assertEquals(0, routeRepo.saveCalls);
    }

    // -----------------------------------------------------------------------
    // Domain validation
    // -----------------------------------------------------------------------

    @Test
    void wrongPrefixInDesignatorIsRejected() {
        authService.login("collab@tap.pt", "Collab123");

        // FR prefix ≠ TP company IATA
        assertThrows(IllegalArgumentException.class, () ->
                controller.createRoute(new FlightRouteRegistrationDTO("FR100", "LIS", "OPO")));

        assertEquals(0, routeRepo.saveCalls);
    }

    @Test
    void originEqualsDestinationIsRejected() {
        authService.login("collab@tap.pt", "Collab123");

        assertThrows(IllegalArgumentException.class, () ->
                controller.createRoute(new FlightRouteRegistrationDTO("TP1", "LIS", "LIS")));

        assertEquals(0, routeRepo.saveCalls);
    }

    @Test
    void ryanairCollaboratorCannotCreateTapRoute() {
        authService.login("collab@ryr.pt", "Ryr12345");

        // Ryanair collaborator → company IATA is FR → TP prefix mismatch
        assertThrows(IllegalArgumentException.class, () ->
                controller.createRoute(new FlightRouteRegistrationDTO("TP123", "LIS", "OPO")));

        assertEquals(0, routeRepo.saveCalls);
    }

    // -----------------------------------------------------------------------
    // Inner in-memory repositories
    // -----------------------------------------------------------------------

    private static class InMemoryUserRepository implements UserRepository {
        private final Map<String, User> store = new HashMap<>();

        @Override public Optional<User> findByEmail(Email e) { return Optional.ofNullable(store.get(e.value())); }
        @Override public Optional<User> findActiveByEmail(Email e) {
            User u = store.get(e.value());
            return u != null && u.isActive() ? Optional.of(u) : Optional.empty();
        }
        @Override public Iterable<User> findActiveByRole(Role r) {
            return store.values().stream().filter(u -> u.isActive() && u.hasRole(r)).toList();
        }
        @Override public boolean existsByEmail(Email e) { return store.containsKey(e.value()); }
        @Override public User save(User u) { store.put(u.email().value(), u); return u; }
        @Override public Iterable<User> findAll() { return new ArrayList<>(store.values()); }
        @Override public Optional<User> findById(Email e) { return findByEmail(e); }
        @Override public void delete(User u) { store.remove(u.email().value()); }
        @Override public void deleteById(Email e) { store.remove(e.value()); }
        @Override public long count() { return store.size(); }
    }

    private static class InMemoryRouteRepository implements FlightRouteRepository {
        private final Map<String, FlightRoute> store = new HashMap<>();
        int saveCalls = 0;

        @Override public Optional<FlightRoute> findByDesignator(String d) {
            return Optional.ofNullable(store.get(d.trim().toUpperCase()));
        }
        @Override public boolean existsByDesignator(String d) {
            return store.containsKey(d.trim().toUpperCase());
        }
        @Override public List<FlightRoute> findActiveByCompany(AirTransportCompany c) {
            return store.values().stream()
                    .filter(r -> r.company().icaoCode().equalsIgnoreCase(c.icaoCode()) && r.isActive()).toList();
        }
        @Override public List<FlightRoute> findAllByCompany(AirTransportCompany c) {
            return store.values().stream()
                    .filter(r -> r.company().icaoCode().equalsIgnoreCase(c.icaoCode())).toList();
        }
        @Override public FlightRoute save(FlightRoute r) {
            saveCalls++;
            store.put(r.identity().value(), r);
            return r;
        }
        @Override public Iterable<FlightRoute> findAll() { return new ArrayList<>(store.values()); }
        @Override public void delete(FlightRoute r) { store.remove(r.identity().value()); }
    }

    private static class InMemoryAirportRepository implements AirportRepository {
        private final Map<String, Airport> byIata = new HashMap<>();

        @Override public Optional<Airport> findByIataCode(IataCode code) {
            return Optional.ofNullable(byIata.get(code.value()));
        }
        @Override public Optional<Airport> findByIcaoCode(eapli.aisafe.generic.domain.IcaoCode code) {
            return byIata.values().stream().filter(a -> a.icaoCode().equals(code)).findFirst();
        }
        @Override public Iterable<Airport> findAll() { return new ArrayList<>(byIata.values()); }
        @Override public Airport save(Airport a) { byIata.put(a.identity().value(), a); return a; }
    }

    private static class InMemoryCollaboratorRepository implements CustomerCollaboratorRepository {
        private final Map<String, CustomerCollaborator> byEmail = new HashMap<>();
        private long nextId = 1;

        @Override public Optional<CustomerCollaborator> findByUserEmail(Email e) {
            return Optional.ofNullable(byEmail.get(e.value()));
        }
        @Override public Iterable<CustomerCollaborator> findByCustomer(CustomerType t, String code) {
            return byEmail.values().stream()
                    .filter(c -> c.customerType() == t && c.customerCode().equalsIgnoreCase(code)).toList();
        }
        @Override public CustomerCollaborator save(CustomerCollaborator c) {
            byEmail.put(c.userEmail().value(), c); return c;
        }
        @Override public Iterable<CustomerCollaborator> findAll() { return new ArrayList<>(byEmail.values()); }
        @Override public Optional<CustomerCollaborator> findById(Long id) { return Optional.empty(); }
        @Override public void delete(CustomerCollaborator c) { byEmail.remove(c.userEmail().value()); }
        @Override public void deleteById(Long id) {}
        @Override public long count() { return byEmail.size(); }
    }

    private static class InMemoryCompanyRepository implements AirTransportCompanyRepository {
        private final Map<String, AirTransportCompany> byIcao = new HashMap<>();
        private final Map<String, AirTransportCompany> byIata = new HashMap<>();

        @Override public Optional<AirTransportCompany> findByIcaoCode(String code) {
            return Optional.ofNullable(byIcao.get(code.trim().toUpperCase()));
        }
        @Override public Optional<AirTransportCompany> findByIataCode(String code) {
            return Optional.ofNullable(byIata.get(code.trim().toUpperCase()));
        }
        @Override public boolean existsByIataCode(String code) { return byIata.containsKey(code.trim().toUpperCase()); }
        @Override public boolean existsByIcaoCode(String code) { return byIcao.containsKey(code.trim().toUpperCase()); }
        @Override public AirTransportCompany save(AirTransportCompany c) {
            byIcao.put(c.icaoCode(), c); byIata.put(c.iataCode(), c); return c;
        }
        @Override public Iterable<AirTransportCompany> findAll() { return new ArrayList<>(byIcao.values()); }
        @Override public Optional<AirTransportCompany> findById(Long id) { return Optional.empty(); }
        @Override public void delete(AirTransportCompany c) { byIcao.remove(c.icaoCode()); byIata.remove(c.iataCode()); }
        @Override public void deleteById(Long id) {}
        @Override public long count() { return byIcao.size(); }
    }
}