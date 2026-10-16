package eapli.aisafe.organization.application;

import eapli.aisafe.generic.domain.PhoneNumber;
import eapli.aisafe.generic.domain.UserName;
import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.organization.repositories.AirTransportCompanyRepository;
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
 * Unit tests for {@link RegisterAirTransportCompanyController}.
 *
 * <p>
 * These tests validate the application behavior required by US060 - Register an
 * air transport company. They verify authorization, successful registration,
 * and duplicate IATA/ICAO code prevention.
 * </p>
 *
 * <p>
 * In-memory repositories are used so the tests remain isolated from JPA,
 * Hibernate, and the physical database.
 * </p>
 */
class RegisterAirTransportCompanyControllerTest {

    private InMemoryUserRepository userRepo;
    private InMemoryAirTransportCompanyRepository companyRepo;
    private AuthService auth;
    private RegisterAirTransportCompanyController controller;

    /**
     * Creates fresh in-memory repositories and an authenticated environment for
     * each test.
     *
     * <p>
     * The authentication session is explicitly cleared because {@link AuthService}
     * stores the current user in a static {@link ThreadLocal}.
     * </p>
     */
    @BeforeEach
    void setUp() {
        userRepo = new InMemoryUserRepository();
        companyRepo = new InMemoryAirTransportCompanyRepository();
        auth = new AuthService(userRepo);
        auth.logout();

        controller = new RegisterAirTransportCompanyController(auth, companyRepo);

        userRepo.save(new User(
                new Email("backoffice@aisafe.com"),
                "Back1234",
                UserName.of("Backoffice"),
                PhoneNumber.of("+351900000001"),
                Set.of(Role.BACKOFFICE_OPERATOR)
        ));
    }

    /**
     * Clears the authentication session after each test to avoid leaking the
     * current user into another test.
     */
    @AfterEach
    void tearDown() {
        auth.logout();
    }

    /**
     * Ensures that an authenticated Backoffice Operator can register an air
     * transport company.
     */
    @Test
    void backofficeOperatorCanRegisterAirTransportCompany() {
        auth.login("backoffice@aisafe.com", "Back1234");

        controller.registerCompany("Lufthansa", "LH", "DLH");

        AirTransportCompany company = companyRepo.findByIataCode("LH").orElseThrow();
        assertEquals("Lufthansa", company.companyName());
        assertTrue(companyRepo.existsByIataCode("LH"));
        assertTrue(companyRepo.existsByIcaoCode("DLH"));
    }

    /**
     * Ensures that company registration is rejected when there is no
     * authenticated user.
     */
    @Test
    void registerCompanyRequiresAuthenticatedUser() {
        auth.logout();

        assertThrows(SecurityException.class,
                () -> controller.registerCompany("Lufthansa", "LH", "DLH"));
    }

    /**
     * Ensures that two air transport companies cannot share the same IATA code.
     */
    @Test
    void duplicateIataCodeIsRejected() {
        auth.login("backoffice@aisafe.com", "Back1234");

        controller.registerCompany("Lufthansa", "LH", "DLH");

        assertThrows(IllegalArgumentException.class,
                () -> controller.registerCompany("Other", "LH", "OTH"));
    }

    /**
     * Ensures that two air transport companies cannot share the same ICAO code.
     */
    @Test
    void duplicateIcaoCodeIsRejected() {
        auth.login("backoffice@aisafe.com", "Back1234");

        controller.registerCompany("Lufthansa", "LH", "DLH");

        assertThrows(IllegalArgumentException.class,
                () -> controller.registerCompany("Other", "OT", "DLH"));
    }

    /**
     * In-memory implementation of {@link AirTransportCompanyRepository} used by
     * the tests.
     *
     * <p>
     * It indexes companies by both IATA and ICAO code to simulate the uniqueness
     * checks required by the controller.
     * </p>
     */
    private static class InMemoryAirTransportCompanyRepository implements AirTransportCompanyRepository {
        private final Map<String, AirTransportCompany> byIata = new HashMap<>();
        private final Map<String, AirTransportCompany> byIcao = new HashMap<>();

        @Override
        public AirTransportCompany save(AirTransportCompany company) {
            byIata.put(company.iataCode(), company);
            byIcao.put(company.icaoCode(), company);
            return company;
        }

        @Override
        public void delete(AirTransportCompany company) {
            byIata.remove(company.iataCode());
            byIcao.remove(company.icaoCode());
        }

        @Override
        public void deleteById(Long id) {
            // No-op for in-memory implementation
        }

        @Override
        public Optional<AirTransportCompany> findById(Long id) {
            return Optional.empty();
        }

        @Override
        public Optional<AirTransportCompany> findByIataCode(String iataCode) {
            return Optional.ofNullable(byIata.get(iataCode.trim().toUpperCase()));
        }

        @Override
        public Optional<AirTransportCompany> findByIcaoCode(String icaoCode) {
            return Optional.ofNullable(byIcao.get(icaoCode.trim().toUpperCase()));
        }

        @Override
        public Iterable<AirTransportCompany> findAll() {
            return byIcao.values();
        }

        @Override
        public boolean existsByIataCode(String iataCode) {
            return findByIataCode(iataCode).isPresent();
        }

        @Override
        public boolean existsByIcaoCode(String icaoCode) {
            return findByIcaoCode(icaoCode).isPresent();
        }

        @Override
        public long count() {
            return byIcao.size();
        }
    }

    /**
     * In-memory implementation of {@link UserRepository} used by the tests.
     *
     * <p>
     * It provides only the repository behavior needed by {@link AuthService} and
     * the controller under test.
     * </p>
     */
    private static class InMemoryUserRepository implements UserRepository {
        private final Map<Email, User> users = new HashMap<>();

        @Override
        public User save(User user) {
            users.put(user.email(), user);
            return user;
        }

        @Override
        public Optional<User> findById(Email id) {
            return Optional.ofNullable(users.get(id));
        }

        @Override
        public Optional<User> findByEmail(Email email) {
            return Optional.ofNullable(users.get(email));
        }

        @Override
        public Iterable<User> findAll() {
            return users.values();
        }

        @Override
        public Optional<User> findActiveByEmail(Email email) {
            return findByEmail(email).filter(User::isActive);
        }

        @Override
        public Iterable<User> findActiveByRole(Role role) {
            List<User> result = new ArrayList<>();
            for (User user : users.values()) {
                if (user.isActive() && user.hasRole(role)) {
                    result.add(user);
                }
            }
            return result;
        }

        @Override
        public void delete(User user) {
            users.remove(user.email());
        }

        @Override
        public void deleteById(Email id) {
            // No-op for in-memory implementation
        }

        @Override
        public boolean existsByEmail(Email email) {
            return users.containsKey(email);
        }

        @Override
        public long count() {
            return users.size();
        }
    }
}
