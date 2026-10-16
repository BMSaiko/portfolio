package eapli.aisafe.aircontrolmanagement.application;

import eapli.aisafe.aircontrolmanagement.domain.*;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class RegisterAirControlAreaControllerTest {

    // Repositório in-memory
    static class InMemoryRepo implements AirControlAreaRepository {
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

    // AuthService que aprova sempre — para não depender de base de dados nos testes
    static class PermissiveAuthService extends AuthService {
        public PermissiveAuthService() { super(null); }

        @Override
        public void ensureHasAnyRole(Role... roles) { /* sempre passa */ }
    }

    private RegisterAirControlAreaController controller;

    @BeforeEach
    void setUp() {
        controller = new RegisterAirControlAreaController(
                new PermissiveAuthService(), new InMemoryRepo());
    }

    @Test
    void ensureValidAreaIsRegistered() {
        var area = controller.registerAirControlArea(
                "LPPC", "Lisbon Oceanic", 5000, 47.0, 36.0, -6.0, -20.0);
        assertNotNull(area);
        assertEquals("LPPC", area.identity().value());
    }

    @Test
    void ensureDuplicateCodeIsRejected() {
        controller.registerAirControlArea(
                "LPPC", "Lisbon Oceanic", 5000, 47.0, 36.0, -6.0, -20.0);
        assertThrows(IllegalStateException.class, () ->
                controller.registerAirControlArea(
                        "LPPC", "Outro", 3000, 60.0, 50.0, 10.0, 0.0));
    }

    @Test
    void ensureOverlappingBoundaryIsRejected() {
        controller.registerAirControlArea(
                "LPPC", "Lisbon Oceanic", 5000, 47.0, 36.0, -6.0, -20.0);
        assertThrows(IllegalStateException.class, () ->
                controller.registerAirControlArea(
                        "LPPO", "Overlap", 3000, 45.0, 38.0, -8.0, -18.0));
    }

    @Test
    void ensureNonOverlappingAreasAreAccepted() {
        controller.registerAirControlArea(
                "LPPC", "Lisbon Oceanic", 5000, 47.0, 36.0, -6.0, -20.0);
        assertDoesNotThrow(() ->
                controller.registerAirControlArea(
                        "LPPO", "Santa Maria", 5000, 35.0, 25.0, -20.0, -35.0));
    }

    @Test
    void ensureAdjacentAreaIsAccepted() {
        controller.registerAirControlArea(
                "LPPC", "Lisbon Oceanic", 5000, 47.0, 36.0, -10.0, -20.0);
        assertDoesNotThrow(() ->
                controller.registerAirControlArea(
                        "EAST", "East Area", 3000, 47.0, 36.0, -6.0, -10.0));
    }
}