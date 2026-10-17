package eapli.aisafe.bootstrap;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.domain.GeoBoundary;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;
import eapli.aisafe.airportmanagement.domain.Airport;
import eapli.aisafe.airportmanagement.domain.Coordinates;
import eapli.aisafe.generic.domain.IataCode;
import eapli.aisafe.generic.domain.IcaoCode;
import eapli.aisafe.airportmanagement.repositories.AirportRepository;

import java.util.logging.Logger;

/**
 * Bootstrapper responsible for creating initial air control areas and airports.
 *
 * <p>
 * This bootstrapper supports US050 and US052 by ensuring that the application
 * has valid initial geographic data when it starts.
 * </p>
 *
 * <p>
 * The bootstrap process is idempotent: each record is only created if it does
 * not already exist, identified by its unique code.
 * </p>
 */
public class AirControlAreaBootstrap {

    private static final Logger LOGGER =
            Logger.getLogger(AirControlAreaBootstrap.class.getName());

    private final AirControlAreaRepository areaRepo;
    private final AirportRepository airportRepo;

    public AirControlAreaBootstrap(final AirControlAreaRepository areaRepo,
                                   final AirportRepository airportRepo) {
        this.areaRepo = areaRepo;
        this.airportRepo = airportRepo;
    }

    /**
     * Executes all bootstrap steps.
     * Areas must be bootstrapped before airports.
     */
    public void bootstrap() {
        bootstrapAirControlAreas();
        bootstrapAirports();
    }

    private void bootstrapAirControlAreas() {
        LOGGER.info("Bootstrapping air control areas...");

        if (areaRepo.findByCode(AirControlAreaCode.of("LPPC")).isEmpty()) {
            areaRepo.save(new AirControlArea(
                    AirControlAreaCode.of("LPPC"),
                    "Lisbon Oceanic",
                    5000.0,
                    new GeoBoundary(47.0, 36.0, -6.0, -20.0)));
        }

        if (areaRepo.findByCode(AirControlAreaCode.of("LPPO")).isEmpty()) {
            areaRepo.save(new AirControlArea(
                    AirControlAreaCode.of("LPPO"),
                    "Santa Maria Oceanic",
                    5000.0,
                    new GeoBoundary(35.0, 25.0, -20.0, -35.0)));
        }
    }

    private void bootstrapAirports() {
        LOGGER.info("Bootstrapping airports...");

        if (airportRepo.findAll().iterator().hasNext()) return;

        AirControlArea lppc = areaRepo
                .findByCode(AirControlAreaCode.of("LPPC"))
                .orElseThrow(() -> new IllegalStateException(
                        "LPPC area not found â€” cannot bootstrap airports."));

        airportRepo.save(new Airport(
                IataCode.of("LIS"), IcaoCode.of("LPPT"),
                "Humberto Delgado Airport", "Lisbon", "Portugal",
                new Coordinates(38.7742, -9.1342, 113.0), lppc));

        airportRepo.save(new Airport(
                IataCode.of("OPO"), IcaoCode.of("LPPR"),
                "Francisco Sa Carneiro Airport", "Porto", "Portugal",
                new Coordinates(41.2481, -8.6814, 69.0), lppc));

        airportRepo.save(new Airport(
                IataCode.of("FAO"), IcaoCode.of("LPFR"),
                "Faro Airport", "Faro", "Portugal",
                new Coordinates(37.0144, -7.9659, 24.0), lppc));
    }
}
