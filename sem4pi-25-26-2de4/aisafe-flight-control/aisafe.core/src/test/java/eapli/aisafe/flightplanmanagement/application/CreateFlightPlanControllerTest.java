package eapli.aisafe.flightplanmanagement.application;

import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftType;
import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.domain.GeoBoundary;
import eapli.aisafe.airportmanagement.domain.Airport;
import eapli.aisafe.airportmanagement.domain.Coordinates;
import eapli.aisafe.enginemanagement.domain.EngineModel;
import eapli.aisafe.enginemanagement.domain.EngineModelId;
import eapli.aisafe.enginemanagement.domain.EngineType;
import eapli.aisafe.enginemanagement.domain.FuelType;
import eapli.aisafe.fleetmanagement.domain.Aircraft;
import eapli.aisafe.fleetmanagement.domain.CabinConfiguration;
import eapli.aisafe.fleetmanagement.domain.OperationalStatus;
import eapli.aisafe.fleetmanagement.repositories.AircraftRepository;
import eapli.aisafe.flightplanmanagement.domain.FlightPlan;
import eapli.aisafe.flightplanmanagement.domain.FlightPlanId;
import eapli.aisafe.flightplanmanagement.domain.FlightPlanStatus;
import eapli.aisafe.flightplanmanagement.dto.FlightPlanRegistrationDTO;
import eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository;
import eapli.aisafe.generic.domain.*;
import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.pilotmanagement.domain.Pilot;
import eapli.aisafe.pilotmanagement.repositories.PilotRepository;
import eapli.aisafe.routemanagement.domain.FlightRoute;
import eapli.aisafe.routemanagement.domain.RouteDesignator;
import eapli.aisafe.routemanagement.repositories.FlightRouteRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link CreateFlightPlanController}.
 *
 * <p>
 * This test class uses explicit in-memory repositories instead of Mockito.
 * This avoids Byte Buddy inline mock issues when the tests are executed with
 * newer JDK versions, while still validating the controller behavior through
 * realistic repository interactions.
 * </p>
 *
 * <p>
 * The tests cover the normal flight plan creation flow, authorization,
 * route availability, aircraft availability, pilot validity, company ownership,
 * departure date validation, fuel validation, and the US074 rule that a route
 * deactivated from a given date can only be used before that date.
 * </p>
 */
public class CreateFlightPlanControllerTest {

    private AuthService authService;
    private InMemoryUserRepository userRepository;
    private InMemoryFlightPlanRepository flightPlanRepository;
    private InMemoryRouteRepository routeRepository;
    private InMemoryAircraftRepository aircraftRepository;
    private InMemoryPilotRepository pilotRepository;
    private CreateFlightPlanController controller;
    private User activePilotUser;

    private AirTransportCompany tapCompany;
    private AirTransportCompany ryrCompany;
    private FlightRoute activeRoute;
    private Aircraft activeAircraft;
    private Pilot activePilot;

    /**
     * Creates the in-memory repositories, authenticates the default pilot and
     * registers the shared domain fixtures used by the tests.
     */
    @Before
    public void setUp() {
        userRepository = new InMemoryUserRepository();
        authService = new AuthService(userRepository);

        flightPlanRepository = new InMemoryFlightPlanRepository();
        routeRepository = new InMemoryRouteRepository();
        aircraftRepository = new InMemoryAircraftRepository();
        pilotRepository = new InMemoryPilotRepository();

        controller = new CreateFlightPlanController(
                authService,
                flightPlanRepository,
                routeRepository,
                aircraftRepository,
                pilotRepository);

        tapCompany = new AirTransportCompany("TAP Air Portugal", "TP", "TAP");
        ryrCompany = new AirTransportCompany("Ryanair", "FR", "RYR");

        activeRoute = buildRoute(tapCompany, "TP123", "LIS", "OPO");
        activeAircraft = buildActiveAircraft(tapCompany);
        activePilotUser = buildPilotUser("pilot@tap.pt", "Pilot123", "Test Pilot");
        activePilot = buildPilot(tapCompany, activePilotUser, "pilot@tap.pt", "Test Pilot");

        routeRepository.save(activeRoute);
        aircraftRepository.save(activeAircraft);
        pilotRepository.save(activePilot);
        userRepository.save(activePilotUser);

        authService.login("pilot@tap.pt", "Pilot123");
    }

    /**
     * Ensures that a valid request creates a flight plan in DRAFT status.
     */
    @Test
    public void ensureFlightPlanIsCreatedWithDraftStatus() {
        final FlightPlan result = controller.createFlightPlan(validDTO());

        assertNotNull(result);
        assertEquals(FlightPlanStatus.DRAFT, result.status());
    }

    /**
     * Ensures that the created flight plan keeps the airports from the selected
     * route.
     */
    @Test
    public void ensureCreatedFlightPlanHasCorrectAirports() {
        final FlightPlan result = controller.createFlightPlan(validDTO());

        assertEquals("LIS", result.departureAirport());
        assertEquals("OPO", result.arrivalAirport());
    }

    /**
     * Ensures that a valid flight plan is persisted.
     */
    @Test
    public void ensureCreatedFlightPlanIsPersisted() {
        controller.createFlightPlan(validDTO());

        assertEquals(1, flightPlanRepository.count());
    }

    /**
     * Ensures that a non-authenticated user cannot create a flight plan.
     */
    @Test(expected = SecurityException.class)
    public void ensureOnlyPilotCanCreateFlightPlan() {
        authService.logout();

        controller.createFlightPlan(validDTO());
    }

    /**
     * Ensures that a pilot from another company cannot create a flight plan for
     * a route owned by TAP.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ensurePilotFromDifferentCompanyIsRejected() {
        pilotRepository.save(buildPilot(
                ryrCompany,
                activePilotUser,
                "pilot@tap.pt",
                "Ryanair Pilot"));

        controller.createFlightPlan(validDTO());
    }

    /**
     * Ensures that a route deactivated from today cannot be used for a future
     * departure.
     */
    @Test(expected = IllegalStateException.class)
    public void ensureDeactivatedRouteIsRejected() {
        activeRoute.deactivateFrom(LocalDate.now());

        controller.createFlightPlan(validDTO());
    }

    /**
     * Ensures that a route deactivated in the future can still be used before
     * its deactivation date.
     */
    @Test
    public void ensureRouteDeactivatedAfterDepartureDateIsAccepted() {
        final LocalDate deactivationDate = LocalDate.now().plusDays(10);
        final LocalDateTime departureBeforeDeactivation =
                LocalDateTime.now().plusDays(7);

        activeRoute.deactivateFrom(deactivationDate);

        final FlightPlanRegistrationDTO dto = new FlightPlanRegistrationDTO(
                "TP123",
                "CS-TKX",
                "pilot@tap.pt",
                departureBeforeDeactivation,
                15_000.0);

        final FlightPlan result = controller.createFlightPlan(dto);

        assertNotNull(result);
        assertEquals(FlightPlanStatus.DRAFT, result.status());
    }

    /**
     * Ensures that a route cannot be used on its deactivation date.
     */
    @Test(expected = IllegalStateException.class)
    public void ensureRouteDeactivatedOnDepartureDateIsRejected() {
        final LocalDate deactivationDate = LocalDate.now().plusDays(7);
        activeRoute.deactivateFrom(deactivationDate);

        final FlightPlanRegistrationDTO dto = new FlightPlanRegistrationDTO(
                "TP123",
                "CS-TKX",
                "pilot@tap.pt",
                deactivationDate.atTime(10, 0),
                15_000.0);

        controller.createFlightPlan(dto);
    }

    /**
     * Ensures that a route cannot be used after its deactivation date.
     */
    @Test(expected = IllegalStateException.class)
    public void ensureRouteDeactivatedBeforeDepartureDateIsRejected() {
        final LocalDate deactivationDate = LocalDate.now().plusDays(7);
        activeRoute.deactivateFrom(deactivationDate);

        final FlightPlanRegistrationDTO dto = new FlightPlanRegistrationDTO(
                "TP123",
                "CS-TKX",
                "pilot@tap.pt",
                LocalDateTime.now().plusDays(10),
                15_000.0);

        controller.createFlightPlan(dto);
    }

    /**
     * Ensures that an unknown route is rejected.
     */
    @Test(expected = IllegalStateException.class)
    public void ensureUnknownRouteIsRejected() {
        routeRepository.delete(activeRoute);

        controller.createFlightPlan(validDTO());
    }

    /**
     * Ensures that a retired aircraft cannot be used in a flight plan.
     */
    @Test(expected = IllegalStateException.class)
    public void ensureDecommissionedAircraftIsRejected() {
        aircraftRepository.save(buildRetiredAircraft(tapCompany));

        controller.createFlightPlan(validDTO());
    }

    /**
     * Ensures that an unknown aircraft registration is rejected.
     */
    @Test(expected = IllegalStateException.class)
    public void ensureUnknownAircraftIsRejected() {
        aircraftRepository.delete(activeAircraft);

        controller.createFlightPlan(validDTO());
    }

    /**
     * Ensures that an inactive pilot cannot be assigned to a flight plan.
     */
    @Test(expected = IllegalStateException.class)
    public void ensureInactivePilotIsRejected() {
        pilotRepository.save(buildInactivePilot(tapCompany));

        controller.createFlightPlan(validDTO());
    }

    /**
     * Ensures that an unknown pilot email is rejected.
     */
    @Test(expected = IllegalStateException.class)
    public void ensureUnknownPilotIsRejected() {
        pilotRepository.delete(activePilot);

        controller.createFlightPlan(validDTO());
    }

    /**
     * Ensures that past departure dates are rejected.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ensurePastDepartureDateTimeIsRejected() {
        final FlightPlanRegistrationDTO pastDTO = new FlightPlanRegistrationDTO(
                "TP123",
                "CS-TKX",
                "pilot@tap.pt",
                LocalDateTime.now().minusDays(1),
                15_000.0);

        controller.createFlightPlan(pastDTO);
    }

    /**
     * Ensures that zero fuel is rejected by the DTO.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ensureZeroFuelIsRejectedByDTO() {
        new FlightPlanRegistrationDTO(
                "TP123",
                "CS-TKX",
                "pilot@tap.pt",
                LocalDateTime.now().plusDays(1),
                0.0);
    }

    /**
     * Ensures that negative fuel is rejected by the DTO.
     */
    @Test(expected = IllegalArgumentException.class)
    public void ensureNegativeFuelIsRejectedByDTO() {
        new FlightPlanRegistrationDTO(
                "TP123",
                "CS-TKX",
                "pilot@tap.pt",
                LocalDateTime.now().plusDays(1),
                -500.0);
    }

    /**
     * Creates a valid flight plan registration request.
     *
     * @return valid DTO
     */
    private FlightPlanRegistrationDTO validDTO() {
        return new FlightPlanRegistrationDTO(
                "TP123",
                "CS-TKX",
                "pilot@tap.pt",
                LocalDateTime.now().plusDays(7),
                15_000.0);
    }

    /**
     * Creates a reusable aircraft model fixture.
     *
     * @return aircraft model fixture
     */
    private AircraftModel dummyAircraftModel() {
        final Set<EngineModel> engines = new HashSet<>();
        engines.add(new EngineModel(
                EngineModelId.of("CFM56-7B", "CFM"),
                EngineType.TURBOFAN,
                Thrust.of(121_000),
                Thrust.of(26_000),
                Speed.of(0.78),
                TSFC.of(1.6e-4),
                Coefficient.of(1.0),
                FuelType.JET_A1));

        return new AircraftModel(
                AircraftModelId.of("BOEING-737", "Boeing"),
                AircraftType.PASSENGER,
                Weight.of(41_413),
                Weight.of(79_016),
                Weight.of(62_732),
                Weight.of(26_020),
                Altitude.of(41_000),
                Speed.of(0.785),
                Area.of(338),
                35.79,
                9.45,
                Coefficient.of(0.85),
                Coefficient.of(0.021),
                Coefficient.of(0.5),
                2,
                189,
                5_600,
                engines);
    }

    /**
     * Creates an airport fixture.
     *
     * @param iata IATA airport code
     * @return airport fixture
     */
    private Airport airport(final String iata) {
        final AirControlArea area = new AirControlArea(
                AirControlAreaCode.of("LPPC"),
                "Lisbon FIR",
                5000.0,
                new GeoBoundary(47.0, 36.0, -6.0, -20.0));

        return new Airport(
                IataCode.of(iata),
                IcaoCode.of("L" + iata),
                "Airport " + iata,
                "City " + iata,
                "Country " + iata,
                new Coordinates(38.0, -9.0, 100),
                area);
    }

    /**
     * Creates a flight route fixture.
     *
     * @param company route owner company
     * @param designator route designator
     * @param origin origin airport code
     * @param destination destination airport code
     * @return flight route fixture
     */
    private FlightRoute buildRoute(final AirTransportCompany company,
                                   final String designator,
                                   final String origin,
                                   final String destination) {
        return new FlightRoute(
                new RouteDesignator(designator, company.iataCode()),
                company,
                airport(origin),
                airport(destination));
    }

    /**
     * Creates an active aircraft fixture.
     *
     * @param company aircraft owner company
     * @return active aircraft fixture
     */
    private Aircraft buildActiveAircraft(final AirTransportCompany company) {
        return new Aircraft(
                "CS-TKX",
                dummyAircraftModel(),
                company,
                "Portugal",
                new CabinConfiguration(12, 24, 150),
                OperationalStatus.ACTIVE,
                2018);
    }

    /**
     * Creates a retired aircraft fixture.
     *
     * @param company aircraft owner company
     * @return retired aircraft fixture
     */
    private Aircraft buildRetiredAircraft(final AirTransportCompany company) {
        final Aircraft aircraft = new Aircraft(
                "CS-TKX",
                dummyAircraftModel(),
                company,
                "Portugal",
                new CabinConfiguration(12, 24, 150),
                OperationalStatus.ACTIVE,
                2005);

        aircraft.decommission();
        return aircraft;
    }

    private User buildPilotUser(final String email,
                                final String password,
                                final String name) {
        return new User(
                new Email(email),
                password,
                UserName.of(name),
                PhoneNumber.of("+351910000000"),
                Set.of(Role.PILOT));
    }

    private Pilot buildPilot(final AirTransportCompany company,
                             final User user,
                             final String email,
                             final String name) {
        return new Pilot(
                user,
                new Email(email),
                name,
                "Captain",
                company,
                Set.of(dummyAircraftModel()));
    }

    private Pilot buildActivePilot(final AirTransportCompany company) {
        final User user = buildPilotUser("pilot@tap.pt", "Pilot123", "Test Pilot");
        return buildPilot(company, user, "pilot@tap.pt", "Test Pilot");
    }

    /**
     * Creates an inactive pilot fixture.
     *
     * @param company pilot company
     * @return inactive pilot fixture
     */
    private Pilot buildInactivePilot(final AirTransportCompany company) {
        final Pilot pilot = buildActivePilot(company);
        pilot.deactivate();
        return pilot;
    }

    /**
     * Simple in-memory user repository for authentication.
     */
    private static class InMemoryUserRepository implements UserRepository {
        private final Map<String, User> users = new HashMap<>();

        @Override
        public Optional<User> findByEmail(final Email email) {
            return Optional.ofNullable(users.get(email.value()));
        }

        @Override
        public Optional<User> findActiveByEmail(final Email email) {
            return findByEmail(email).filter(User::isActive);
        }

        @Override
        public Iterable<User> findActiveByRole(final Role role) {
            return users.values().stream()
                    .filter(User::isActive)
                    .filter(user -> user.hasRole(role))
                    .toList();
        }

        @Override
        public boolean existsByEmail(final Email email) {
            return users.containsKey(email.value());
        }

        @Override
        public User save(final User entity) {
            users.put(entity.identity().value(), entity);
            return entity;
        }

        @Override
        public Iterable<User> findAll() {
            return new ArrayList<>(users.values());
        }

        @Override
        public Optional<User> findById(final Email id) {
            return findByEmail(id);
        }

        @Override
        public void delete(final User entity) {
            users.remove(entity.identity().value());
        }

        @Override
        public void deleteById(final Email id) {
            users.remove(id.value());
        }

        @Override
        public long count() {
            return users.size();
        }
    }

    /**
     * Simple in-memory route repository.
     */
    private static class InMemoryRouteRepository implements FlightRouteRepository {
        private final Map<String, FlightRoute> routes = new HashMap<>();

        @Override
        public Optional<FlightRoute> findByDesignator(final String designatorValue) {
            return Optional.ofNullable(routes.get(designatorValue.trim().toUpperCase()));
        }

        @Override
        public boolean existsByDesignator(final String designatorValue) {
            return findByDesignator(designatorValue).isPresent();
        }

        @Override
        public List<FlightRoute> findActiveByCompany(final AirTransportCompany company) {
            return routes.values().stream()
                    .filter(route -> route.company().iataCode().equalsIgnoreCase(company.iataCode()))
                    .filter(FlightRoute::isActive)
                    .toList();
        }

        @Override
        public List<FlightRoute> findAllByCompany(final AirTransportCompany company) {
            return routes.values().stream()
                    .filter(route -> route.company().iataCode().equalsIgnoreCase(company.iataCode()))
                    .toList();
        }

        @Override
        public FlightRoute save(final FlightRoute route) {
            routes.put(route.identity().value(), route);
            return route;
        }

        @Override
        public Iterable<FlightRoute> findAll() {
            return new ArrayList<>(routes.values());
        }

        @Override
        public void delete(final FlightRoute route) {
            routes.remove(route.identity().value());
        }
    }

    /**
     * Simple in-memory aircraft repository.
     */
    private static class InMemoryAircraftRepository implements AircraftRepository {
        private final Map<String, Aircraft> aircraft = new HashMap<>();

        @Override
        public boolean existsByRegistration(final String registration) {
            return aircraft.containsKey(registration);
        }

        @Override
        public boolean existsByAircraftModelId(final AircraftModelId id) {
            return aircraft.values().stream()
                    .anyMatch(a -> a.model().identity().equals(id));
        }

        @Override
        public Iterable<Aircraft> findByCompany(final String companyIataCode) {
            return aircraft.values().stream()
                    .filter(a -> a.company().iataCode().equalsIgnoreCase(companyIataCode))
                    .toList();
        }

        @Override
        public Optional<Aircraft> findByRegistration(final String registration) {
            return Optional.ofNullable(aircraft.get(registration));
        }

        @Override
        public Iterable<Aircraft> findAll() {
            return new ArrayList<>(aircraft.values());
        }

        @Override
        public Aircraft save(final Aircraft entity) {
            aircraft.put(entity.identity(), entity);
            return entity;
        }

        @Override
        public void delete(final Aircraft entity) {
            aircraft.remove(entity.identity());
        }

        public Optional<Aircraft> findById(final String id) {
            return findByRegistration(id);
        }

        public Optional<Aircraft> ofIdentity(final String id) {
            return findById(id);
        }

        public void deleteOfIdentity(final String id) {
            aircraft.remove(id);
        }

        @Override
        public long count() {
            return aircraft.size();
        }
    }

    /**
     * Simple in-memory pilot repository.
     */
    private static class InMemoryPilotRepository implements PilotRepository {
        private final Map<String, Pilot> pilots = new HashMap<>();

        @Override
        public Optional<Pilot> findByEmail(final Email email) {
            return Optional.ofNullable(pilots.get(email.value()));
        }

        @Override
        public List<Pilot> findByCompany(final AirTransportCompany company) {
            return pilots.values().stream()
                    .filter(pilot -> pilot.company().iataCode().equalsIgnoreCase(company.iataCode()))
                    .toList();
        }

        @Override
        public boolean existsByEmail(final Email email) {
            return pilots.containsKey(email.value());
        }

        @Override
        public Pilot save(final Pilot entity) {
            pilots.put(entity.email().value(), entity);
            return entity;
        }

        @Override
        public Iterable<Pilot> findAll() {
            return new ArrayList<>(pilots.values());
        }

        @Override
        public Optional<Pilot> findById(final Long id) {
            return Optional.empty();
        }

        @Override
        public void delete(final Pilot entity) {
            pilots.remove(entity.email().value());
        }

        @Override
        public void deleteById(final Long id) {
        }

        @Override
        public long count() {
            return pilots.size();
        }
    }

    /**
     * Simple in-memory flight plan repository.
     */
    private static class InMemoryFlightPlanRepository implements FlightPlanRepository {
        private final Map<FlightPlanId, FlightPlan> plans = new HashMap<>();

        @Override
        public Optional<FlightPlan> findById(final FlightPlanId id) {
            return Optional.ofNullable(plans.get(id));
        }

        @Override
        public Iterable<FlightPlan> findAll() {
            return new ArrayList<>(plans.values());
        }

        @Override
        public FlightPlan save(final FlightPlan flightPlan) {
            plans.put(flightPlan.identity(), flightPlan);
            return flightPlan;
        }

        @Override
        public void deleteById(final FlightPlanId id) {
            plans.remove(id);
        }

        @Override
        public long count() {
            return plans.size();
        }

        @Override
        public boolean existsPlannedForRouteFrom(final FlightRoute route, final LocalDate date) {
            return false;
        }

        @Override
        public List<FlightPlan> findByPilot(final Pilot pilot) {
            return plans.values().stream()
                    .filter(plan -> plan.pilot() != null)
                    .filter(plan -> plan.pilot().email().equals(pilot.email()))
                    .toList();
        }

        @Override
        public List<FlightPlan> findByRoute(final FlightRoute route) {
            return plans.values().stream()
                    .filter(plan -> plan.flightRoute() != null)
                    .filter(plan -> plan.flightRoute().identity().equals(route.identity()))
                    .toList();
        }

        @Override
        public long countByPilot(final Pilot pilot) {
            return findByPilot(pilot).size();
        }
    }
}