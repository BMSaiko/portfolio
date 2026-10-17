package eapli.aisafe.bootstrap;

import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId;
import eapli.aisafe.aircraftmodelmanagement.repositories.AircraftModelRepository;
import eapli.aisafe.fleetmanagement.domain.Aircraft;
import eapli.aisafe.fleetmanagement.domain.CabinConfiguration;
import eapli.aisafe.fleetmanagement.domain.OperationalStatus;
import eapli.aisafe.fleetmanagement.repositories.AircraftRepository;
import eapli.aisafe.generic.domain.PhoneNumber;
import eapli.aisafe.generic.domain.UserName;
import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.organization.repositories.AirTransportCompanyRepository;
import eapli.aisafe.pilotmanagement.domain.Pilot;
import eapli.aisafe.pilotmanagement.repositories.PilotRepository;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;

import java.util.Set;
import java.util.logging.Logger;

/**
 * Bootstrapper that creates sample aircraft and pilots for demo purposes.
 *
 * <p>Prerequisites (must run after):</p>
 * <ul>
 *   <li>{@link EngineModelBootstrap} — aircraft models must exist.</li>
 *   <li>{@link MasterUsersBootstrapper} — companies must exist.</li>
 * </ul>
 *
 * <p>Idempotent: skips if aircraft already exist.</p>
 */
public class FleetBootstrap {

    private static final Logger LOGGER =
            Logger.getLogger(FleetBootstrap.class.getName());

    private final AircraftRepository       aircraftRepo;
    private final AircraftModelRepository  aircraftModelRepo;
    private final AirTransportCompanyRepository companyRepo;
    private final PilotRepository          pilotRepo;
    private final UserRepository           userRepo;

    public FleetBootstrap(final AircraftRepository aircraftRepo,
                          final AircraftModelRepository aircraftModelRepo,
                          final AirTransportCompanyRepository companyRepo,
                          final PilotRepository pilotRepo,
                          final UserRepository userRepo) {
        this.aircraftRepo     = aircraftRepo;
        this.aircraftModelRepo = aircraftModelRepo;
        this.companyRepo      = companyRepo;
        this.pilotRepo        = pilotRepo;
        this.userRepo         = userRepo;
    }

    public void bootstrap() {
        final AirTransportCompany tap = companyRepo.findByIataCode("TP").orElse(null);
        if (tap == null) {
            LOGGER.warning("FleetBootstrap: TAP not found — skipping.");
            return;
        }

        final AircraftModel b737 = aircraftModelRepo
                .findById(AircraftModelId.of("BOEING-737", "Boeing")).orElse(null);
        if (b737 == null) {
            LOGGER.warning("FleetBootstrap: BOEING-737 model not found — skipping.");
            return;
        }

        final AircraftModel a320 = aircraftModelRepo
                .findById(AircraftModelId.of("A320-200", "Airbus")).orElse(null);
        if (a320 == null) {
            LOGGER.warning("FleetBootstrap: A320-200 model not found — skipping.");
            return;
        }

        final AircraftModel b777 = aircraftModelRepo
                .findById(AircraftModelId.of("777-200ER", "Boeing")).orElse(null);
        if (b777 == null) {
            LOGGER.warning("FleetBootstrap: 777-200ER model not found — skipping.");
            return;
        }

        bootstrapAircraft(tap, b737, a320, b777);
        bootstrapPilot(tap, b737);
    }

    // -------------------------------------------------------------------------

    private void bootstrapAircraft(final AirTransportCompany tap,
                                   final AircraftModel b737,
                                   final AircraftModel a320,
                                   final AircraftModel b777) {
        if (aircraftRepo.findAll().iterator().hasNext()) {
            LOGGER.info("FleetBootstrap: aircraft already exist — skipping.");
            return;
        }

        LOGGER.info("FleetBootstrap: creating aircraft...");

        aircraftRepo.save(new Aircraft(
                "CS-TKX", a320, tap, "Portugal",
                new CabinConfiguration(8, 24, 120),
                OperationalStatus.ACTIVE, 2018));

        aircraftRepo.save(new Aircraft(
                "CS-TPA", b737, tap, "Portugal",
                new CabinConfiguration(8, 24, 120),
                OperationalStatus.ACTIVE, 2020));

        aircraftRepo.save(new Aircraft(
                "CS-TNI", b777, tap, "Portugal",
                new CabinConfiguration(10, 40, 260),
                OperationalStatus.ACTIVE, 2019));

        LOGGER.info("FleetBootstrap: aircraft CS-TKX (A320-200), CS-TPA (BOEING-737), CS-TNI (777-200ER) created.");
    }

    private void bootstrapPilot(final AirTransportCompany tap,
                                final AircraftModel b737) {
        final Email pilotEmail = new Email("pilot@aisafe.com");

        if (pilotRepo.existsByEmail(pilotEmail)) {
            LOGGER.info("FleetBootstrap: pilot already exists — skipping.");
            return;
        }

        LOGGER.info("FleetBootstrap: creating pilot...");

        // The user pilot@aisafe.com is created by MasterUsersBootstrapper
        // but without a Pilot domain entity. We create it here.
        // If the User doesn't exist yet (first run order issue), create it.
        if (!userRepo.existsByEmail(pilotEmail)) {
            userRepo.save(new User(
                    pilotEmail, "Pilot123",
                    UserName.of("Demo Pilot"),
                    PhoneNumber.of("+351910000001"),
                    Set.of(Role.PILOT)));
        }

        final User pilotUser = userRepo.findActiveByEmail(pilotEmail)
                .orElseThrow(() -> new IllegalStateException(
                        "FleetBootstrap: pilot user not found after creation."));

        pilotRepo.save(new Pilot(
                pilotUser,
                pilotEmail,
                "Demo Pilot",
                "Captain",
                tap,
                Set.of(b737)));

        LOGGER.info("FleetBootstrap: pilot pilot@aisafe.com created for TAP.");
    }
}