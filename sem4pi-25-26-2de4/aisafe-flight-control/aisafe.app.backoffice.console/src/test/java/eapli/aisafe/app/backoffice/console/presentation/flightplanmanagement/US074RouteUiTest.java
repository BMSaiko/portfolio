package eapli.aisafe.app.backoffice.console.presentation.flightplanmanagement;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.domain.GeoBoundary;
import eapli.aisafe.airportmanagement.domain.Airport;
import eapli.aisafe.airportmanagement.domain.Coordinates;
import eapli.aisafe.fleetmanagement.repositories.AircraftRepository;
import eapli.aisafe.flightplanmanagement.application.CreateFlightPlanController;
import eapli.aisafe.flightplanmanagement.domain.FlightPlan;
import eapli.aisafe.flightplanmanagement.dto.FlightPlanRegistrationDTO;
import eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository;
import eapli.aisafe.generic.domain.IataCode;
import eapli.aisafe.generic.domain.IcaoCode;
import eapli.aisafe.generic.domain.PhoneNumber;
import eapli.aisafe.generic.domain.UserName;
import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.persistence.impl.inmemory.InMemoryAircraftRepository;
import eapli.aisafe.persistence.impl.inmemory.InMemoryFlightPlanRepository;
import eapli.aisafe.persistence.impl.inmemory.InMemoryFlightRouteRepository;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UI acceptance tests for the US074 rule that deactivated routes must not be
 * used to create new flight plans.
 */
class US074RouteUiTest {

    private final PrintStream originalOut = System.out;
    private final java.io.InputStream originalIn = System.in;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(new InMemoryUserRepository());
    }

    @AfterEach
    void restoreConsole() {
        System.setOut(originalOut);
        System.setIn(originalIn);
        authService.logout();
    }

    @Test
    void us074CreateFlightPlanShouldRejectRouteAfterDeactivationDate() {
        FlightRoute deactivatedRoute = route("TP777");
        deactivatedRoute.deactivateFrom(LocalDate.of(2026, 7, 1));

        CapturingCreateFlightPlanController controller =
                new CapturingCreateFlightPlanController(authService, List.of(deactivatedRoute));
        controller.errorToThrow = new IllegalStateException(
                "Route TP777 is deactivated from 2026-07-01 and cannot be used on 2026-07-10.");

        CreateFlightPlanUI ui = new CreateFlightPlanUI(controller);

        ConsoleResult console = runUiWithInput(
                String.join("\n",
                        "1",
                        "CS-TKX",
                        "pilot@aisafe.com",
                        "2026-07-10 10:00",
                        "12000") + "\n",
                ui::show);

        assertNotNull(controller.capturedDto, console.output);
        assertEquals("TP777", controller.capturedDto.routeDesignator);
        assertTrue(console.output.contains("Available routes:"));
        assertTrue(console.output.contains("TP777"));
        assertTrue(console.output.contains("deactivated from 2026-07-01"));
        assertTrue(console.output.contains("ERROR: Route TP777 is deactivated from 2026-07-01"));
    }

    @Test
    void us074CreateFlightPlanShouldNotShowAlreadyUnavailableRoutes() {
        FlightRoute deactivatedRoute = route("TP778");
        deactivatedRoute.deactivateFrom(LocalDate.of(2026, 1, 1));

        CapturingCreateFlightPlanController controller =
                new CapturingCreateFlightPlanController(authService, List.of(deactivatedRoute));

        CreateFlightPlanUI ui = new CreateFlightPlanUI(controller);

        ConsoleResult console = runUiWithInput(
                "",
                ui::show);

        assertTrue(console.output.contains("No available routes found."));
    }

    private ConsoleResult runUiWithInput(String input, Runnable uiAction) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Thread writerThread = null;

        try {
            PipedInputStream inputStream = new PipedInputStream();
            PipedOutputStream inputWriter = new PipedOutputStream(inputStream);
            writerThread = scriptedConsoleWriter(input, inputWriter);

            System.setIn(inputStream);
            System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
            writerThread.start();
            uiAction.run();
            writerThread.join(1000);
        } catch (IOException | InterruptedException e) {
            throw new AssertionError("Failed to run scripted console input.", e);
        }

        return new ConsoleResult(output.toString(StandardCharsets.UTF_8));
    }

    private static FlightRoute route(String designator) {
        AirControlArea area = new AirControlArea(
                AirControlAreaCode.of("LIS_AREA"),
                "Lisbon Area",
                500.0,
                new GeoBoundary(47.0, 36.0, -6.0, -20.0));
        Airport origin = airport("LIS", "LPPT", "Lisbon Airport", area);
        Airport destination = airport("OPO", "LPPR", "Porto Airport", area);
        AirTransportCompany company = new AirTransportCompany("TAP Air Portugal", "TP", "TAP");
        return new FlightRoute(new RouteDesignator(designator, company.iataCode()), company, origin, destination);
    }

    private static Airport airport(String iata, String icao, String name, AirControlArea area) {
        return new Airport(
                IataCode.of(iata),
                IcaoCode.of(icao),
                name,
                "Test Town",
                "Portugal",
                new Coordinates(38.0, -9.0, 100.0),
                area);
    }

    private record ConsoleResult(String output) {
    }

    private Thread scriptedConsoleWriter(String input, PipedOutputStream inputWriter) {
        return new Thread(() -> {
            try (inputWriter) {
                for (String line : input.split("(?<=\\n)")) {
                    inputWriter.write(line.getBytes(StandardCharsets.UTF_8));
                    inputWriter.flush();
                    Thread.sleep(500);
                }
            } catch (IOException | InterruptedException e) {
                throw new AssertionError("Failed to write scripted console input.", e);
            }
        });
    }

    /**
     * Test double that exposes controlled routes and captures the DTO sent by
     * the UI before simulating the route availability rule.
     */
    private static class CapturingCreateFlightPlanController extends CreateFlightPlanController {
        private final Iterable<FlightRoute> routes;
        private FlightPlanRegistrationDTO capturedDto;
        private RuntimeException errorToThrow;

        CapturingCreateFlightPlanController(AuthService authService, Iterable<FlightRoute> routes) {
            super(authService,
                    new InMemoryFlightPlanRepository(),
                    new InMemoryFlightRouteRepository(),
                    new InMemoryAircraftRepository(),
                    new NeverUsedPilotRepository());
            this.routes = routes;
        }

        @Override
        public Iterable<FlightRoute> activeRoutes() {
            return routes;
        }

        @Override
        public FlightPlan createFlightPlan(FlightPlanRegistrationDTO dto) {
            capturedDto = dto;
            if (errorToThrow != null) {
                throw errorToThrow;
            }
            throw new UnsupportedOperationException("This test only verifies route rejection.");
        }
    }

    /**
     * Minimal pilot repository because the fake controller does not reach the
     * real pilot lookup path.
     */
    private static class NeverUsedPilotRepository implements PilotRepository {
        @Override
        public Optional<Pilot> findByEmail(Email email) {
            return Optional.empty();
        }

        @Override
        public List<Pilot> findByCompany(AirTransportCompany company) {
            return List.of();
        }

        @Override
        public boolean existsByEmail(Email email) {
            return false;
        }

        @Override
        public <S extends Pilot> S save(S entity) {
            return entity;
        }

        @Override
        public Iterable<Pilot> findAll() {
            return List.of();
        }

        @Override
        public Optional<Pilot> findById(Long id) {
            return Optional.empty();
        }

        @Override
        public void delete(Pilot entity) {
        }

        @Override
        public void deleteById(Long id) {
        }

        @Override
        public long count() {
            return 0;
        }
    }

    /**
     * Minimal user repository required by AuthService construction.
     */
    private static class InMemoryUserRepository implements UserRepository {
        private final Map<String, User> users = new HashMap<>();

        @Override
        public Optional<User> findByEmail(Email email) {
            return Optional.ofNullable(users.get(email.value()));
        }

        @Override
        public Optional<User> findActiveByEmail(Email email) {
            return findByEmail(email).filter(User::isActive);
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
        public <S extends User> S save(S entity) {
            users.put(entity.email().value(), entity);
            return entity;
        }

        @Override
        public Iterable<User> findAll() {
            return new ArrayList<>(users.values());
        }

        @Override
        public Optional<User> findById(Email id) {
            return findByEmail(id);
        }

        @Override
        public void delete(User entity) {
            users.remove(entity.email().value());
        }

        @Override
        public void deleteById(Email id) {
            users.remove(id.value());
        }

        @Override
        public long count() {
            return users.size();
        }

        @SuppressWarnings("unused")
        void addUser(String email, Role role) {
            save(new User(
                    new Email(email),
                    "Password123",
                    UserName.of("Test User"),
                    PhoneNumber.of("123456789"),
                    Set.of(role)));
        }
    }
}
