package eapli.aisafe.bootstrap;

import eapli.aisafe.airportmanagement.repositories.AirportRepository;
import eapli.aisafe.generic.domain.IataCode;
import eapli.aisafe.organization.repositories.AirTransportCompanyRepository;
import eapli.aisafe.routemanagement.domain.FlightRoute;
import eapli.aisafe.routemanagement.domain.RouteDesignator;
import eapli.aisafe.routemanagement.repositories.FlightRouteRepository;

import java.util.logging.Logger;

public class FlightRouteBootstrap {

    private static final Logger LOGGER =
            Logger.getLogger(FlightRouteBootstrap.class.getName());

    private final FlightRouteRepository routeRepo;
    private final AirportRepository airportRepo;
    private final AirTransportCompanyRepository companyRepo;

    public FlightRouteBootstrap(final FlightRouteRepository routeRepo,
                                final AirportRepository airportRepo,
                                final AirTransportCompanyRepository companyRepo) {
        this.routeRepo   = routeRepo;
        this.airportRepo = airportRepo;
        this.companyRepo = companyRepo;
    }

    public void bootstrap() {
        if (routeRepo.findAll().iterator().hasNext()) {
            LOGGER.info("Routes already exist — skipping bootstrap.");
            return;
        }

        LOGGER.info("Bootstrapping flight routes...");

        final var tap = companyRepo.findByIataCode("TP")
                .orElse(null);
        if (tap == null) {
            LOGGER.warning("TAP not found — skipping route bootstrap.");
            return;
        }

        final var lis = airportRepo.findByIataCode(IataCode.of("LIS")).orElse(null);
        final var opo = airportRepo.findByIataCode(IataCode.of("OPO")).orElse(null);
        final var fao = airportRepo.findByIataCode(IataCode.of("FAO")).orElse(null);

        if (lis == null || opo == null || fao == null) {
            LOGGER.warning("Airports not found — skipping route bootstrap.");
            return;
        }

        routeRepo.save(new FlightRoute(new RouteDesignator("TP123", "TP"), tap, lis, opo));
        routeRepo.save(new FlightRoute(new RouteDesignator("TP456", "TP"), tap, opo, fao));
        routeRepo.save(new FlightRoute(new RouteDesignator("TP789", "TP"), tap, lis, fao));
    }
}
