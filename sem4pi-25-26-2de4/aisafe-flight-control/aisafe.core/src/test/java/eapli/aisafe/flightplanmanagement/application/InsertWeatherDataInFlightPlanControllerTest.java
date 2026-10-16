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
import eapli.aisafe.flightplanmanagement.domain.*;
import eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository;
import eapli.aisafe.generic.domain.*;
import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.pilotmanagement.domain.Pilot;
import eapli.aisafe.routemanagement.domain.FlightRoute;
import eapli.aisafe.routemanagement.domain.RouteDesignator;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import eapli.aisafe.weather.domain.*;
import eapli.aisafe.weather.repositories.WeatherDataRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;

public class InsertWeatherDataInFlightPlanControllerTest {

    private AuthService authService;
    private InMemoryUserRepository userRepository;
    private InMemoryFlightPlanRepository flightPlanRepository;
    private InMemoryWeatherDataRepository weatherDataRepository;
    private InsertWeatherDataInFlightPlanController controller;

    private FlightPlan pilotFlightPlan;
    private FlightPlan otherPilotFlightPlan;
    private WeatherData weatherData;

    @Before
    public void setUp() {
        userRepository = new InMemoryUserRepository();
        flightPlanRepository = new InMemoryFlightPlanRepository();
        weatherDataRepository = new InMemoryWeatherDataRepository();
        authService = new AuthService(userRepository);

        controller = new InsertWeatherDataInFlightPlanController(
                authService,
                flightPlanRepository,
                weatherDataRepository
        );

        userRepository.save(user("pilot@aisafe.com", "Pilot123", Role.PILOT));
        userRepository.save(user("otherpilot@aisafe.com", "Pilot123", Role.PILOT));
        userRepository.save(user("weather@aisafe.com", "Weather123", Role.WEATHER_PERSON));

        AirTransportCompany company = new AirTransportCompany("TAP Air Portugal", "TP", "TAP");
        AircraftModel model = aircraftModel();

        Pilot pilot = pilot("pilot@aisafe.com", company, model);
        Pilot otherPilot = pilot("otherpilot@aisafe.com", company, model);

        pilotFlightPlan = flightPlan("FP-US082-1", pilot, company, model);
        otherPilotFlightPlan = flightPlan("FP-US082-2", otherPilot, company, model);

        weatherData = weatherData();

        flightPlanRepository.save(pilotFlightPlan);
        flightPlanRepository.save(otherPilotFlightPlan);
        weatherDataRepository.saveWithId(1L, weatherData);
    }

    @After
    public void tearDown() {
        authService.logout();
    }


    @Test
    public void insertWeatherDataShouldAssociateWeatherDataWithOwnFlightPlan() {
        authService.login("pilot@aisafe.com", "Pilot123");
        FlightPlan updated = controller.insertWeatherData("FP-US082-1", 1L);
        assertEquals(1, updated.weatherData().size());
        assertTrue(updated.weatherData().contains(weatherData));
    }

    @Test
    public void insertWeatherDataShouldPersistUpdatedFlightPlan() {
        authService.login("pilot@aisafe.com", "Pilot123");
        controller.insertWeatherData("FP-US082-1", 1L);
        FlightPlan persisted = flightPlanRepository.findById(FlightPlanId.of("FP-US082-1")).orElseThrow();
        assertEquals(1, persisted.weatherData().size());
    }

    @Test
    public void insertWeatherDataShouldKeepDraftStatusWhenPlanWasDraft() {
        authService.login("pilot@aisafe.com", "Pilot123");
        FlightPlan updated = controller.insertWeatherData("FP-US082-1", 1L);
        assertEquals(FlightPlanStatus.DRAFT, updated.status());
    }

    @Test
    public void insertWeatherDataShouldInvalidateValidatedFlightPlan() {
        authService.login("pilot@aisafe.com", "Pilot123");
        pilotFlightPlan.validate();
        FlightPlan updated = controller.insertWeatherData("FP-US082-1", 1L);
        assertEquals(FlightPlanStatus.DRAFT, updated.status());
    }

    @Test
    public void insertWeatherDataShouldInvalidateApprovedFlightPlan() {
        authService.login("pilot@aisafe.com", "Pilot123");
        pilotFlightPlan.approve();
        FlightPlan updated = controller.insertWeatherData("FP-US082-1", 1L);
        assertEquals(FlightPlanStatus.DRAFT, updated.status());
    }

    @Test
    public void myFlightPlansShouldReturnOnlyAuthenticatedPilotPlans() {
        authService.login("pilot@aisafe.com", "Pilot123");
        List<FlightPlan> result = controller.myFlightPlans();
        assertEquals(1, result.size());
        assertEquals(FlightPlanId.of("FP-US082-1"), result.get(0).identity());
    }

    @Test
    public void availableWeatherDataShouldReturnWeatherRecords() {
        authService.login("pilot@aisafe.com", "Pilot123");
        List<WeatherData> result = toList(controller.availableWeatherData());
        assertEquals(1, result.size());
        assertTrue(result.contains(weatherData));
    }

    @Test(expected = SecurityException.class)
    public void insertWeatherDataShouldRejectUnauthenticatedUser() {
        controller.insertWeatherData("FP-US082-1", 1L);
    }

    @Test(expected = SecurityException.class)
    public void insertWeatherDataShouldRejectUserWithoutPilotRole() {
        authService.login("weather@aisafe.com", "Weather123");
        controller.insertWeatherData("FP-US082-1", 1L);
    }

    @Test(expected = SecurityException.class)
    public void insertWeatherDataShouldRejectFlightPlanOwnedByAnotherPilot() {
        authService.login("pilot@aisafe.com", "Pilot123");
        controller.insertWeatherData("FP-US082-2", 1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void insertWeatherDataShouldRejectNullFlightPlanId() {
        authService.login("pilot@aisafe.com", "Pilot123");
        controller.insertWeatherData(null, 1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void insertWeatherDataShouldRejectBlankFlightPlanId() {
        authService.login("pilot@aisafe.com", "Pilot123");
        controller.insertWeatherData("   ", 1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void insertWeatherDataShouldRejectNullWeatherDataId() {
        authService.login("pilot@aisafe.com", "Pilot123");
        controller.insertWeatherData("FP-US082-1", null);
    }

    @Test(expected = IllegalStateException.class)
    public void insertWeatherDataShouldRejectUnknownFlightPlan() {
        authService.login("pilot@aisafe.com", "Pilot123");
        controller.insertWeatherData("UNKNOWN-FP", 1L);
    }

    @Test(expected = IllegalStateException.class)
    public void insertWeatherDataShouldRejectUnknownWeatherData() {
        authService.login("pilot@aisafe.com", "Pilot123");
        controller.insertWeatherData("FP-US082-1", 999L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void insertWeatherDataShouldRejectDuplicateWeatherData() {
        authService.login("pilot@aisafe.com", "Pilot123");
        controller.insertWeatherData("FP-US082-1", 1L);
        controller.insertWeatherData("FP-US082-1", 1L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void insertWeatherDataShouldRejectFlightPlanWithoutPilot() {
        authService.login("pilot@aisafe.com", "Pilot123");
        FlightPlan importedPlanWithoutPilot = importedFlightPlanWithoutPilot();
        flightPlanRepository.save(importedPlanWithoutPilot);
        controller.insertWeatherData("FP-US082-IMPORTED", 1L);
    }


    private User user(String email, String password, Role role) {
        return new User(new Email(email), password, UserName.of(email), PhoneNumber.of("+351910000001"), Set.of(role));
    }

    private FlightPlan flightPlan(String id, Pilot pilot, AirTransportCompany company, AircraftModel model) {
        return new FlightPlan(FlightPlanId.of(id), route(company), aircraft(company, model), pilot, LocalDateTime.now().plusDays(5), 15_000.0);
    }

    private FlightPlan importedFlightPlanWithoutPilot() {
        FlightSegment segment = FlightSegment.of("cruise", Waypoint.of(38.0, -9.0, 10_000), Waypoint.of(41.0, -8.0, 10_000));
        FlightLeg leg = FlightLeg.of("LIS", "OPO", 5_000.0, List.of(segment));
        return new FlightPlan(FlightPlanId.of("FP-US082-IMPORTED"), "LIS-OPO", "LIS", "OPO", aircraftModel(), List.of(leg), 278.0, 5_000.0);
    }

    private WeatherData weatherData() {
        AirControlArea area = new AirControlArea(AirControlAreaCode.of("LIS_AREA"), "Lisbon Area", 500.0, new GeoBoundary(39.0, 38.0, -8.0, -9.0));
        return new WeatherData(area, TimePeriod.of(LocalDateTime.of(2026, 6, 10, 8, 0), LocalDateTime.of(2026, 6, 10, 9, 0)), Temperature.of(19.5), WindSpeed.of(12.0), WindDirection.of(270.0), Humidity.of(65.0), Pressure.of(1013.0));
    }

    private Pilot pilot(String email, AirTransportCompany company, AircraftModel model) {
        return new Pilot(user(email, "Pilot123", Role.PILOT), new Email(email), "Pilot " + email, "Captain", company, Set.of(model));
    }

    private FlightRoute route(AirTransportCompany company) {
        return new FlightRoute(new RouteDesignator("TP123", "TP"), company, airport("LIS"), airport("OPO"));
    }

    private Airport airport(String iata) {
        AirControlArea area = new AirControlArea(AirControlAreaCode.of(iata + "_AREA"), "Area " + iata, 500.0, new GeoBoundary(42.0, 35.0, -6.0, -12.0));
        return new Airport(IataCode.of(iata), IcaoCode.of("L" + iata), "Airport " + iata, "City " + iata, "Portugal", new Coordinates(38.0, -9.0, 100.0), area);
    }

    private Aircraft aircraft(AirTransportCompany company, AircraftModel model) {
        return new Aircraft("CS-TKX", model, company, "Portugal", new CabinConfiguration(8, 24, 120), OperationalStatus.ACTIVE, 2018);
    }

    private AircraftModel aircraftModel() {
        EngineModel engine = new EngineModel(EngineModelId.of("CFM56-7B", "CFM"), EngineType.TURBOFAN, Thrust.of(121_000), Thrust.of(26_000), Speed.of(0.78), TSFC.of(1.6e-4), Coefficient.of(1.0), FuelType.JET_A1);
        return new AircraftModel(AircraftModelId.of("BOEING-737", "Boeing"), AircraftType.PASSENGER, Weight.of(41_413), Weight.of(79_016), Weight.of(62_732), Weight.of(26_020), Altitude.of(41_000), Speed.of(0.785), Area.of(338), 35.79, 9.45, Coefficient.of(0.85), Coefficient.of(0.021), Coefficient.of(0.5), 2, 189, 5_600, Set.of(engine));
    }

    private List<WeatherData> toList(Iterable<WeatherData> iterable) {
        List<WeatherData> result = new ArrayList<>();
        iterable.forEach(result::add);
        return result;
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
            return users.values().stream().filter(User::isActive).filter(user -> user.hasRole(role)).toList();
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

    private static class InMemoryFlightPlanRepository implements FlightPlanRepository {
        private final Map<FlightPlanId, FlightPlan> store = new HashMap<>();

        @Override
        public Optional<FlightPlan> findById(FlightPlanId id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public Iterable<FlightPlan> findAll() {
            return new ArrayList<>(store.values());
        }

        @Override
        public FlightPlan save(FlightPlan flightPlan) {
            store.put(flightPlan.identity(), flightPlan);
            return flightPlan;
        }

        @Override
        public void deleteById(FlightPlanId id) {
            store.remove(id);
        }

        @Override
        public long count() {
            return store.size();
        }

        @Override
        public boolean existsPlannedForRouteFrom(FlightRoute route, LocalDate date) {
            return false;
        }

        @Override
        public List<FlightPlan> findByPilot(Pilot pilot) {
            return store.values().stream().filter(plan -> plan.pilot() != null && plan.pilot().equals(pilot)).toList();
        }

        @Override
        public long countByPilot(Pilot pilot) {
            return findByPilot(pilot).size();
        }

        @Override
        public List<FlightPlan> findByRoute(FlightRoute route) {
            return store.values().stream().filter(plan -> plan.flightRoute() != null && plan.flightRoute().equals(route)).toList();
        }
    }

    private static class InMemoryWeatherDataRepository implements WeatherDataRepository {
        private final Map<Long, WeatherData> store = new HashMap<>();

        void saveWithId(Long id, WeatherData data) {
            store.put(id, data);
        }

        @Override
        public WeatherData save(WeatherData entity) {
            long id = store.size() + 1L;
            store.put(id, entity);
            return entity;
        }

        @Override
        public Optional<WeatherData> findById(Long id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public Iterable<WeatherData> findAll() {
            return new ArrayList<>(store.values());
        }

        @Override
        public void delete(WeatherData entity) {
            store.values().remove(entity);
        }

        @Override
        public void deleteById(Long id) {
            store.remove(id);
        }

        @Override
        public long count() {
            return store.size();
        }

        @Override
        public Iterable<WeatherData> findByAreaAndPeriodStartBetween(AirControlArea area, LocalDateTime start, LocalDateTime end) {
            return store.values().stream().filter(data -> data.area().sameAs(area)).filter(data -> !data.period().start().isBefore(start)).filter(data -> data.period().start().isBefore(end)).toList();
        }
    }
}
