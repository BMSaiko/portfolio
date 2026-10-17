package eapli.aisafe.bootstrap;

import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;
import eapli.aisafe.aircraftmodelmanagement.repositories.AircraftModelRepository;
import eapli.aisafe.fleetmanagement.domain.Aircraft;
import eapli.aisafe.fleetmanagement.domain.OperationalStatus;
import eapli.aisafe.fleetmanagement.repositories.AircraftRepository;
import eapli.aisafe.flightplanmanagement.domain.*;
import eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository;
import eapli.aisafe.pilotmanagement.domain.Pilot;
import eapli.aisafe.pilotmanagement.repositories.PilotRepository;
import eapli.aisafe.routemanagement.domain.FlightRoute;
import eapli.aisafe.routemanagement.repositories.FlightRouteRepository;
import eapli.aisafe.usermanagement.domain.Email;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Bootstrapper that creates sample flight plans with varied statuses,
 * routes and aircraft models for US112 Monthly Report edge case testing.
 *
 * Edge cases covered:
 * - Jan: mixed statuses (validated + draft), 2 aircraft types
 * - Feb: approved + validated + draft, 2 aircraft types
 * - Mar: 4 flights, 3 routes, mixed statuses
 * - Apr: all reachable statuses (DRAFT, VALIDATED, FAILED, APPROVED)
 * - May: all approved (100% validated, diverse routes)
 * - Jun: single flight (minimal month)
 * - Aug: 3 aircraft types (fleet diversity)
 * - Sep-Dec: no flights (empty months — tested via report generation)
 */
public class FlightPlanBootstrap {

    private static final Logger LOGGER =
            Logger.getLogger(FlightPlanBootstrap.class.getName());

    private final FlightPlanRepository flightPlanRepository;
    private final FlightRouteRepository routeRepository;
    private final AircraftRepository aircraftRepository;
    private final PilotRepository pilotRepository;
    private final AircraftModelRepository aircraftModelRepository;

    public FlightPlanBootstrap(
            final FlightPlanRepository flightPlanRepository,
            final FlightRouteRepository routeRepository,
            final AircraftRepository aircraftRepository,
            final PilotRepository pilotRepository,
            final AircraftModelRepository aircraftModelRepository) {
        this.flightPlanRepository = flightPlanRepository;
        this.routeRepository = routeRepository;
        this.aircraftRepository = aircraftRepository;
        this.pilotRepository = pilotRepository;
        this.aircraftModelRepository = aircraftModelRepository;
    }

    public void bootstrap() {
        if (flightPlanRepository.count() > 0) {
            LOGGER.info("Flight plans already exist — skipping bootstrap.");
            return;
        }

        LOGGER.info("Bootstrapping flight plans...");

        Optional<FlightRoute> tp123 = routeRepository.findByDesignator("TP123");
        Optional<FlightRoute> tp456 = routeRepository.findByDesignator("TP456");
        Optional<FlightRoute> tp789 = routeRepository.findByDesignator("TP789");
        Optional<Aircraft> csTkx = aircraftRepository.findByRegistration("CS-TKX");
        Optional<Aircraft> csTpa = aircraftRepository.findByRegistration("CS-TPA");
        Optional<Aircraft> csTni = aircraftRepository.findByRegistration("CS-TNI");
        Optional<Pilot> pilot = pilotRepository.findByEmail(new Email("pilot@aisafe.com"));

        if (tp123.isEmpty() || tp456.isEmpty() || csTkx.isEmpty() || pilot.isEmpty()) {
            LOGGER.warning("Bootstrap: missing prerequisites — skipping flight plans.");
            return;
        }

        FlightRoute route123 = tp123.get();
        FlightRoute route456 = tp456.get();
        FlightRoute route789 = tp789.orElse(route456);
        Aircraft ac1 = csTkx.get();
        Aircraft ac2 = csTpa.orElse(ac1);
        Aircraft ac3 = csTni.orElse(ac1);
        Pilot p = pilot.get();

        // ──────────────────────────────────────────────────────────────
        // JANUARY 2026 — 3 flights
        // 2 validated (different aircraft), 1 draft
        // Expected: SUMMARY 3 flights | 2 validated | FLEET 2 types
        // ──────────────────────────────────────────────────────────────
        createPlan("FPJ01", route123, ac1, p,
                LocalDateTime.of(2026, 1, 5, 8, 0), FlightPlanStatus.VALIDATED);
        createPlan("FPJ02", route123, ac2, p,
                LocalDateTime.of(2026, 1, 12, 14, 30), FlightPlanStatus.VALIDATED);
        createPlan("FPJ03", route456, ac1, p,
                LocalDateTime.of(2026, 1, 20, 10, 0), FlightPlanStatus.DRAFT);

        // ──────────────────────────────────────────────────────────────
        // FEBRUARY 2026 — 3 flights
        // 1 approved, 1 validated, 1 draft
        // Expected: SUMMARY 3 flights | 2 validated | FLEET 2 types
        // ──────────────────────────────────────────────────────────────
        createPlan("FPF01", route123, ac1, p,
                LocalDateTime.of(2026, 2, 3, 9, 0), FlightPlanStatus.APPROVED);
        createPlan("FPF02", route456, ac2, p,
                LocalDateTime.of(2026, 2, 14, 16, 0), FlightPlanStatus.VALIDATED);
        createPlan("FPF03", route123, ac1, p,
                LocalDateTime.of(2026, 2, 25, 11, 0), FlightPlanStatus.DRAFT);

        // ──────────────────────────────────────────────────────────────
        // MARCH 2026 — 4 flights, 3 routes
        // 2 validated, 1 approved, 1 draft
        // Expected: SUMMARY 4 flights | 3 validated | TOP ROUTES 3 routes
        // ──────────────────────────────────────────────────────────────
        createPlan("FPM01", route123, ac2, p,
                LocalDateTime.of(2026, 3, 2, 8, 30), FlightPlanStatus.APPROVED);
        createPlan("FPM02", route456, ac1, p,
                LocalDateTime.of(2026, 3, 10, 13, 0), FlightPlanStatus.VALIDATED);
        createPlan("FPM03", route789, ac2, p,
                LocalDateTime.of(2026, 3, 18, 17, 0), FlightPlanStatus.VALIDATED);
        createPlan("FPM04", route123, ac1, p,
                LocalDateTime.of(2026, 3, 28, 9, 0), FlightPlanStatus.DRAFT);

        // ──────────────────────────────────────────────────────────────
        // APRIL 2026 — ALL REACHABLE STATUSES (edge case)
        // DRAFT, VALIDATED, FAILED, APPROVED
        // Expected: SUMMARY 4 flights | 2 validated (VALIDATED+APPROVED)
        // FLEET: 2 types | TOP ROUTES: 3 routes
        // ──────────────────────────────────────────────────────────────
        createPlan("FPA01", route123, ac1, p,
                LocalDateTime.of(2026, 4, 1, 8, 0), FlightPlanStatus.DRAFT);
        createPlan("FPA02", route123, ac2, p,
                LocalDateTime.of(2026, 4, 5, 10, 0), FlightPlanStatus.VALIDATED);
        createPlan("FPA03", route456, ac1, p,
                LocalDateTime.of(2026, 4, 10, 14, 0), FlightPlanStatus.FAILED);
        createPlan("FPA04", route789, ac2, p,
                LocalDateTime.of(2026, 4, 15, 9, 0), FlightPlanStatus.APPROVED);

        // ──────────────────────────────────────────────────────────────
        // MAY 2026 — ALL APPROVED (edge case: 100% validated)
        // 3 flights, all approved, 3 different routes
        // Expected: SUMMARY 3 flights | 3 validated | 100% pass rate
        // ──────────────────────────────────────────────────────────────
        createPlan("FMM01", route123, ac1, p,
                LocalDateTime.of(2026, 5, 5, 8, 0), FlightPlanStatus.APPROVED);
        createPlan("FMM02", route456, ac2, p,
                LocalDateTime.of(2026, 5, 12, 14, 0), FlightPlanStatus.APPROVED);
        createPlan("FMM03", route789, ac1, p,
                LocalDateTime.of(2026, 5, 20, 10, 0), FlightPlanStatus.APPROVED);

        // ──────────────────────────────────────────────────────────────
        // JUNE 2026 — SINGLE FLIGHT (edge case: minimal month)
        // Expected: SUMMARY 1 flight | 1 validated | FLEET 1 type
        // ──────────────────────────────────────────────────────────────
        createPlan("FJN01", route123, ac1, p,
                LocalDateTime.of(2026, 6, 2, 8, 0), FlightPlanStatus.VALIDATED);

        // ──────────────────────────────────────────────────────────────
        // AUGUST 2026 — 3 AIRCRAFT TYPES (edge case: fleet diversity)
        // One flight per aircraft model
        // Expected: FLEET 3 active aircraft types
        // ──────────────────────────────────────────────────────────────
        createPlan("FAG01", route123, ac1, p,
                LocalDateTime.of(2026, 8, 5, 8, 0), FlightPlanStatus.VALIDATED);
        createPlan("FAG02", route456, ac2, p,
                LocalDateTime.of(2026, 8, 12, 14, 0), FlightPlanStatus.VALIDATED);
        createPlan("FAG03", route789, ac3, p,
                LocalDateTime.of(2026, 8, 20, 10, 0), FlightPlanStatus.VALIDATED);

        // ──────────────────────────────────────────────────────────────
        // SEPTEMBER–DECEMBER 2026 — NO FLIGHTS (empty months)
        // Tested by generating reports for these months
        // Expected: 0 flights | N/A most used | No routes
        // ──────────────────────────────────────────────────────────────
        // (no flight plans created — these months are intentionally empty)

        LOGGER.info("Bootstrapping flight plans complete.");
    }

    private void createPlan(String id, FlightRoute route, Aircraft aircraft,
                            Pilot pilot, LocalDateTime departure,
                            FlightPlanStatus targetStatus) {
        FlightPlanId fpId = FlightPlanId.of(id);
        double fuel = 15_000.0;

        FlightPlan plan = new FlightPlan(fpId, route, aircraft, pilot, departure, fuel);

        if (targetStatus == FlightPlanStatus.VALIDATED
                || targetStatus == FlightPlanStatus.APPROVED) {
            plan.validate();
        }
        if (targetStatus == FlightPlanStatus.APPROVED) {
            plan.approve();
        }
        if (targetStatus == FlightPlanStatus.FAILED) {
            plan.invalidate();
        }
        // DRAFT: no transition needed (default state)

        flightPlanRepository.save(plan);
        LOGGER.info(String.format(
                "Bootstrap: created %s [%s] %s->%s status=%s",
                id, route.identity(),
                route.origin().identity(), route.destination().identity(),
                targetStatus));
    }
}
