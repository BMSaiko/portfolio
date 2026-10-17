package eapli.aisafe.airportmanagement.application;

import eapli.aisafe.aircontrolmanagement.domain.*;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;
import eapli.aisafe.airportmanagement.domain.*;
import eapli.aisafe.generic.domain.IataCode;
import eapli.aisafe.generic.domain.IcaoCode;
import eapli.aisafe.airportmanagement.repositories.AirportRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RegisterAirportControllerTest {

    // -------------------------------------------------------
    // Repositório in-memory para Airport
    // -------------------------------------------------------
    static class InMemoryAirportRepository implements AirportRepository {
        private final Map<IataCode, Airport> store = new HashMap<>();

        @Override
        public Optional<Airport> findByIataCode(IataCode code) {
            return Optional.ofNullable(store.get(code));
        }

        @Override
        public Optional<Airport> findByIcaoCode(IcaoCode code) {
            return store.values().stream()
                    .filter(a -> a.icaoCode().equals(code))
                    .findFirst();
        }

        @Override
        public Iterable<Airport> findAll() { return store.values(); }

        @Override
        public Airport save(Airport entity) {
            store.put(entity.identity(), entity);
            return entity;
        }
    }

    // -------------------------------------------------------
    // Repositório in-memory para AirControlArea
    // -------------------------------------------------------
    static class InMemoryAreaRepository implements AirControlAreaRepository {
        private final Map<AirControlAreaCode, AirControlArea> store = new HashMap<>();

        @Override
        public <S extends AirControlArea> S save(S entity) {
            store.put(entity.identity(), entity);
            return entity;
        }

        @Override
        public Optional<AirControlArea> findByCode(AirControlAreaCode code) {
            return Optional.ofNullable(store.get(code));
        }

        @Override
        public Iterable<AirControlArea> findAll() {
            return store.values();
        }

        @Override
        public Optional<AirControlArea> findById(AirControlAreaCode id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public void delete(AirControlArea area) {
            store.remove(area.identity());
        }

        @Override
        public void deleteById(AirControlAreaCode id) {
            store.remove(id);
        }

        @Override
        public long count() {
            return store.size();
        }
    }

    // -------------------------------------------------------
    // AuthService que aprova sempre
    // -------------------------------------------------------
    static class PermissiveAuthService extends AuthService {
        public PermissiveAuthService() { super(null); }

        @Override
        public void ensureHasAnyRole(Role... roles) { /* sempre passa */ }
    }

    // -------------------------------------------------------
    // Setup
    // -------------------------------------------------------
    private RegisterAirportController controller;
    private InMemoryAreaRepository areaRepo;

    // Área válida usada na maioria dos testes
    // LPPC: N:47 S:36 E:-6 W:-20 — contém Lisboa (38.77, -9.13)
    private static final AirControlArea LPPC = new AirControlArea(
            AirControlAreaCode.of("LPPC"),
            "Lisbon Oceanic",
            5000.0,
            new GeoBoundary(47.0, 36.0, -6.0, -20.0)
    );

    @BeforeEach
    void setUp() {
        areaRepo = new InMemoryAreaRepository();
        areaRepo.save(LPPC);
        controller = new RegisterAirportController(
                new PermissiveAuthService(),
                new InMemoryAirportRepository(),
                areaRepo);
    }

    // -------------------------------------------------------
    // Sucesso
    // -------------------------------------------------------

    @Test
    void ensureValidAirportIsRegistered() {
        var airport = controller.registerAirport(
                "LIS", "LPPT", "Humberto Delgado Airport",
                "Lisbon", "Portugal",
                38.7742, -9.1342, 113.0, "LPPC");
        assertNotNull(airport);
        assertEquals("LIS", airport.identity().value());
    }

    // -------------------------------------------------------
    // Códigos duplicados
    // -------------------------------------------------------

    @Test
    void ensureDuplicateIataCodeIsRejected() {
        controller.registerAirport(
                "LIS", "LPPT", "Humberto Delgado Airport",
                "Lisbon", "Portugal",
                38.7742, -9.1342, 113.0, "LPPC");

        assertThrows(IllegalStateException.class, () ->
                controller.registerAirport(
                        "LIS", "LPPR", "Another Airport",
                        "Lisbon", "Portugal",
                        38.7742, -9.1342, 113.0, "LPPC"));
    }

    @Test
    void ensureDuplicateIcaoCodeIsRejected() {
        controller.registerAirport(
                "LIS", "LPPT", "Humberto Delgado Airport",
                "Lisbon", "Portugal",
                38.7742, -9.1342, 113.0, "LPPC");

        assertThrows(IllegalStateException.class, () ->
                controller.registerAirport(
                        "OPO", "LPPT", "Another Airport",
                        "Porto", "Portugal",
                        41.2481, -8.6814, 69.0, "LPPC"));
    }

    // -------------------------------------------------------
    // AirControlArea inexistente
    // -------------------------------------------------------

    @Test
    void ensureNonExistentAreaIsRejected() {
        assertThrows(IllegalStateException.class, () ->
                controller.registerAirport(
                        "LIS", "LPPT", "Humberto Delgado Airport",
                        "Lisbon", "Portugal",
                        38.7742, -9.1342, 113.0, "XXXX"));
    }

    // -------------------------------------------------------
    // Coordenadas fora da boundary
    // -------------------------------------------------------

    @Test
    void ensureCoordinatesOutsideBoundaryAreRejected() {
        // Londres está fora de LPPC
        assertThrows(IllegalStateException.class, () ->
                controller.registerAirport(
                        "LHR", "EGLL", "Heathrow Airport",
                        "London", "UK",
                        51.4775, -0.4614, 25.0, "LPPC"));
    }

    @Test
    void ensureCoordinatesInsideBoundaryAreAccepted() {
        // Faro está dentro de LPPC
        assertDoesNotThrow(() ->
                controller.registerAirport(
                        "FAO", "LPFR", "Faro Airport",
                        "Faro", "Portugal",
                        37.0144, -7.9659, 24.0, "LPPC"));
    }
}
