package eapli.aisafe.organization.application;

import eapli.aisafe.aircontrolmanagement.domain.AirControlArea;
import eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode;
import eapli.aisafe.aircontrolmanagement.domain.GeoBoundary;
import eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository;
import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.organization.domain.CustomerCollaborator;
import eapli.aisafe.organization.domain.CustomerType;
import eapli.aisafe.organization.repositories.AirTransportCompanyRepository;
import eapli.aisafe.organization.repositories.CustomerCollaboratorRepository;
import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.generic.domain.PhoneNumber;
import eapli.aisafe.generic.domain.UserName;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CustomerCollaboratorController}.
 *
 * <p>
 * These tests validate the application behavior required by:
 * </p>
 *
 * <ul>
 *     <li>US061 - Add a customer's collaborator.</li>
 *     <li>US062 - List customer's collaborators.</li>
 *     <li>US063 - Edit a customer's collaborator.</li>
 *     <li>US064 - Disable a customer's collaborator.</li>
 * </ul>
 *
 * <p>
 * The test class uses in-memory repository implementations to keep the tests
 * isolated from JPA, Hibernate and the physical H2 database.
 * </p>
 */
class CustomerCollaboratorControllerTest {

    private InMemoryUserRepository userRepo;
    private InMemoryAirTransportCompanyRepository companyRepo;
    private InMemoryAirControlAreaRepository airControlAreaRepo;
    private InMemoryCustomerCollaboratorRepository collaboratorRepo;
    private AuthService auth;
    private CustomerCollaboratorController controller;

    /**
     * Creates a fresh test context before each test.
     *
     * <p>
     * The authentication session is explicitly cleared because
     * {@link AuthService} stores the current user in a static
     * {@link ThreadLocal}.
     * </p>
     */
    @BeforeEach
    void setUp() {
        userRepo = new InMemoryUserRepository();
        companyRepo = new InMemoryAirTransportCompanyRepository();
        airControlAreaRepo = new InMemoryAirControlAreaRepository();
        collaboratorRepo = new InMemoryCustomerCollaboratorRepository();

        auth = new AuthService(userRepo);
        auth.logout();

        controller = new CustomerCollaboratorController(
                auth,
                userRepo,
                companyRepo,
                airControlAreaRepo,
                collaboratorRepo
        );

        userRepo.save(new User(
                new Email("backoffice@aisafe.com"),
                "Back1234",
                UserName.of("Backoffice"),
                PhoneNumber.of("+351900000001"),
                Set.of(Role.BACKOFFICE_OPERATOR)
        ));

        userRepo.save(new User(
                new Email("collab@aisafe.com"),
                "Collab123",
                UserName.of("Collaborator"),
                PhoneNumber.of("+351900000002"),
                Set.of(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR, Role.FLIGHT_CONTROL_OPERATOR)
        ));

        userRepo.save(new User(
                new Email("second.collab@aisafe.com"),
                "Second123",
                UserName.of("Second Collaborator"),
                PhoneNumber.of("+351900000004"),
                Set.of(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR, Role.FLIGHT_CONTROL_OPERATOR)
        ));

        userRepo.save(new User(
                new Email("other@aisafe.com"),
                "Other123",
                UserName.of("Other"),
                PhoneNumber.of("+351900000003"),
                Set.of(Role.PILOT)
        ));

        companyRepo.save(new AirTransportCompany("TAP Air Portugal", "TP", "TAP"));

        airControlAreaRepo.save(new AirControlArea(
                AirControlAreaCode.of("LIS_AREA"),
                "Lisbon Area",
                500.0,
                new GeoBoundary(38.0, -9.0, 39.0, -8.0)
        ));

        auth.login("backoffice@aisafe.com", "Back1234");
    }

    /**
     * Clears the authentication session after each test.
     */
    @AfterEach
    void tearDown() {
        auth.logout();
    }

    /**
     * Ensures that listing available collaborator users requires authentication.
     */
    @Test
    void availableCollaboratorUsersRequiresAuthentication() {
        auth.logout();

        assertThrows(SecurityException.class,
                () -> controller.availableCollaboratorUsers(CustomerType.AIR_TRANSPORT_COMPANY));
    }

    /**
     * Ensures that only active users with the required collaborator role are
     * listed as available collaborator users.
     */
    @Test
    void listsOnlyAvailableUsersWithCollaboratorRole() {
        List<User> available = controller.availableCollaboratorUsers(CustomerType.AIR_TRANSPORT_COMPANY);

        assertEquals(2, available.size());
        assertTrue(available.stream().anyMatch(u -> u.email().equals(new Email("collab@aisafe.com"))));
        assertTrue(available.stream().anyMatch(u -> u.email().equals(new Email("second.collab@aisafe.com"))));
        assertFalse(available.stream().anyMatch(u -> u.email().equals(new Email("other@aisafe.com"))));
    }

    /**
     * Ensures that a user already associated with a customer is no longer listed
     * as available for a second collaborator association.
     */
    @Test
    void alreadyAssociatedCollaboratorUserIsNotAvailableAgain() {
        controller.addExistingUserAsCollaborator(
                CustomerType.AIR_TRANSPORT_COMPANY,
                "TAP",
                "collab@aisafe.com",
                "Operations Manager"
        );

        List<User> available = controller.availableCollaboratorUsers(CustomerType.AIR_TRANSPORT_COMPANY);

        assertEquals(1, available.size());
        assertEquals(new Email("second.collab@aisafe.com"), available.get(0).email());
    }

    /**
     * Ensures that an existing user with the required role can be associated
     * with an air transport company.
     */
    @Test
    void addsExistingCollaboratorUserToAirTransportCompany() {
        controller.addExistingUserAsCollaborator(
                CustomerType.AIR_TRANSPORT_COMPANY,
                "TAP",
                "collab@aisafe.com",
                "Operations Manager"
        );

        CustomerCollaborator collaborator = collaboratorRepo.findByUserEmail(new Email("collab@aisafe.com"))
                .orElseThrow();

        assertNotNull(collaborator.id());
        assertEquals(CustomerType.AIR_TRANSPORT_COMPANY, collaborator.customerType());
        assertEquals("TAP", collaborator.customerCode());
        assertEquals(new Email("collab@aisafe.com"), collaborator.userEmail());
        assertEquals("Operations Manager", collaborator.position());
    }

    /**
     * Ensures that an existing user with the required role can be associated
     * with an existing air control area.
     */
    @Test
    void addsExistingCollaboratorUserToAirControlArea() {
        controller.addExistingUserAsCollaborator(
                CustomerType.AIR_CONTROL_AREA,
                "LIS_AREA",
                "collab@aisafe.com",
                "Flight Control Operator"
        );

        CustomerCollaborator collaborator = collaboratorRepo.findByUserEmail(new Email("collab@aisafe.com"))
                .orElseThrow();

        assertNotNull(collaborator.id());
        assertEquals(CustomerType.AIR_CONTROL_AREA, collaborator.customerType());
        assertEquals("LIS_AREA", collaborator.customerCode());
        assertEquals(new Email("collab@aisafe.com"), collaborator.userEmail());
    }

    /**
     * Ensures that the controller exposes registered air control areas so the UI
     * can list real areas instead of asking for a manually typed code.
     */
    @Test
    void listsAirControlAreas() {
        List<AirControlArea> areas = new ArrayList<>();
        controller.airControlAreas().forEach(areas::add);

        assertEquals(1, areas.size());
        assertEquals(AirControlAreaCode.of("LIS_AREA"), areas.get(0).identity());
    }

    /**
     * Ensures that adding a customer collaborator requires authentication.
     */
    @Test
    void addCollaboratorRequiresAuthentication() {
        auth.logout();

        assertThrows(SecurityException.class,
                () -> controller.addExistingUserAsCollaborator(
                        CustomerType.AIR_TRANSPORT_COMPANY,
                        "TAP",
                        "collab@aisafe.com",
                        "Operations Manager"
                ));
    }

    /**
     * Ensures that inactive users cannot be associated as customer
     * collaborators.
     */
    @Test
    void rejectsInactiveUserWhenAddingCollaborator() {
        User user = userRepo.findByEmail(new Email("collab@aisafe.com")).orElseThrow();
        user.deactivate();
        userRepo.save(user);

        assertThrows(IllegalArgumentException.class,
                () -> controller.addExistingUserAsCollaborator(
                        CustomerType.AIR_TRANSPORT_COMPANY,
                        "TAP",
                        "collab@aisafe.com",
                        "Operations Manager"
                ));
    }

    /**
     * Ensures that users without the required collaborator role cannot be
     * associated as customer collaborators.
     */
    @Test
    void rejectsUserWithoutCollaboratorRole() {
        assertThrows(IllegalArgumentException.class,
                () -> controller.addExistingUserAsCollaborator(
                        CustomerType.AIR_TRANSPORT_COMPANY,
                        "TAP",
                        "other@aisafe.com",
                        "Operations Manager"
                ));
    }

    /**
     * Ensures that the same system user cannot be associated as a collaborator
     * more than once.
     */
    @Test
    void rejectsAlreadyAssociatedUser() {
        controller.addExistingUserAsCollaborator(
                CustomerType.AIR_TRANSPORT_COMPANY,
                "TAP",
                "collab@aisafe.com",
                "Operations Manager"
        );

        assertThrows(IllegalArgumentException.class,
                () -> controller.addExistingUserAsCollaborator(
                        CustomerType.AIR_TRANSPORT_COMPANY,
                        "TAP",
                        "collab@aisafe.com",
                        "Another Position"
                ));
    }

    /**
     * Ensures that associating a collaborator with an unknown air transport
     * company is rejected.
     */
    @Test
    void rejectsUnknownAirTransportCompany() {
        assertThrows(IllegalArgumentException.class,
                () -> controller.addExistingUserAsCollaborator(
                        CustomerType.AIR_TRANSPORT_COMPANY,
                        "XXX",
                        "collab@aisafe.com",
                        "Operations Manager"
                ));
    }

    /**
     * Ensures that associating a collaborator with an unknown air control area
     * is rejected.
     */
    @Test
    void rejectsUnknownAirControlArea() {
        assertThrows(IllegalArgumentException.class,
                () -> controller.addExistingUserAsCollaborator(
                        CustomerType.AIR_CONTROL_AREA,
                        "UNKNOWN",
                        "collab@aisafe.com",
                        "Flight Control Operator"
                ));
    }

    /**
     * Ensures that only active collaborators for the selected air transport
     * company are listed.
     */
    @Test
    void listsOnlyActiveCollaboratorsForCustomer() {
        controller.addExistingUserAsCollaborator(
                CustomerType.AIR_TRANSPORT_COMPANY,
                "TAP",
                "collab@aisafe.com",
                "Operations Manager"
        );

        controller.addExistingUserAsCollaborator(
                CustomerType.AIR_CONTROL_AREA,
                "LIS_AREA",
                "second.collab@aisafe.com",
                "Flight Control Operator"
        );

        List<User> users = controller.listActiveCollaborators(
                CustomerType.AIR_TRANSPORT_COMPANY,
                "TAP"
        );

        assertEquals(1, users.size());
        assertEquals(new Email("collab@aisafe.com"), users.get(0).email());
    }

    /**
     * Ensures that active collaborators for an air control area can be listed.
     */
    @Test
    void listsOnlyActiveCollaboratorsForAirControlArea() {
        controller.addExistingUserAsCollaborator(
                CustomerType.AIR_CONTROL_AREA,
                "LIS_AREA",
                "collab@aisafe.com",
                "Flight Control Operator"
        );

        List<User> users = controller.listActiveCollaborators(
                CustomerType.AIR_CONTROL_AREA,
                "LIS_AREA"
        );

        assertEquals(1, users.size());
        assertEquals(new Email("collab@aisafe.com"), users.get(0).email());
    }

    /**
     * Ensures that listing customer collaborators requires authentication.
     */
    @Test
    void listCollaboratorsRequiresAuthentication() {
        auth.logout();

        assertThrows(SecurityException.class,
                () -> controller.listActiveCollaborators(
                        CustomerType.AIR_TRANSPORT_COMPANY,
                        "TAP"
                ));
    }

    /**
     * Ensures that disabled collaborators are not listed as active
     * collaborators.
     */
    @Test
    void disabledCollaboratorsAreNotListed() {
        controller.addExistingUserAsCollaborator(
                CustomerType.AIR_TRANSPORT_COMPANY,
                "TAP",
                "collab@aisafe.com",
                "Operations Manager"
        );

        CustomerCollaborator collaborator = collaboratorRepo.findByUserEmail(new Email("collab@aisafe.com"))
                .orElseThrow();

        controller.disableCollaborator(collaborator.id());

        List<User> users = controller.listActiveCollaborators(
                CustomerType.AIR_TRANSPORT_COMPANY,
                "TAP"
        );

        assertTrue(users.isEmpty());
    }

    /**
     * Ensures that disabling a collaborator requires authentication.
     */
    @Test
    void disableCollaboratorRequiresAuthentication() {
        controller.addExistingUserAsCollaborator(
                CustomerType.AIR_TRANSPORT_COMPANY,
                "TAP",
                "collab@aisafe.com",
                "Operations Manager"
        );

        CustomerCollaborator collaborator = collaboratorRepo.findByUserEmail(new Email("collab@aisafe.com"))
                .orElseThrow();

        auth.logout();

        assertThrows(SecurityException.class,
                () -> controller.disableCollaborator(collaborator.id()));
    }

    /**
     * Ensures that disabling a collaborator prevents the associated user from
     * logging in.
     */
    @Test
    void disableCollaboratorPreventsLogin() {
        controller.addExistingUserAsCollaborator(
                CustomerType.AIR_TRANSPORT_COMPANY,
                "TAP",
                "collab@aisafe.com",
                "Operations Manager"
        );

        CustomerCollaborator collaborator = collaboratorRepo.findByUserEmail(new Email("collab@aisafe.com"))
                .orElseThrow();

        controller.disableCollaborator(collaborator.id());

        AuthService otherAuth = new AuthService(userRepo);
        otherAuth.logout();

        assertTrue(otherAuth.login("collab@aisafe.com", "Collab123").isEmpty());
    }

    /**
     * Ensures that editing a collaborator can change both email and phone
     * number.
     */
    @Test
    void editsCollaboratorEmailAndPhoneNumber() {
        controller.addExistingUserAsCollaborator(
                CustomerType.AIR_TRANSPORT_COMPANY,
                "TAP",
                "collab@aisafe.com",
                "Operations Manager"
        );

        CustomerCollaborator collaborator = collaboratorRepo.findByUserEmail(new Email("collab@aisafe.com"))
                .orElseThrow();

        controller.editCollaborator(
                collaborator.id(),
                "new.collab@aisafe.com",
                "+351900000099"
        );

        assertTrue(userRepo.findByEmail(new Email("collab@aisafe.com")).isEmpty());

        User updatedUser = userRepo.findByEmail(new Email("new.collab@aisafe.com"))
                .orElseThrow();

        assertEquals("+351900000099", updatedUser.phoneNumber().value());

        CustomerCollaborator updatedCollaborator = collaboratorRepo.findById(collaborator.id())
                .orElseThrow();

        assertEquals(new Email("new.collab@aisafe.com"), updatedCollaborator.userEmail());
    }

    /**
     * Ensures that when the email remains the same, only the phone number is
     * updated.
     */
    @Test
    void editsOnlyPhoneWhenEmailIsTheSame() {
        controller.addExistingUserAsCollaborator(
                CustomerType.AIR_TRANSPORT_COMPANY,
                "TAP",
                "collab@aisafe.com",
                "Operations Manager"
        );

        CustomerCollaborator collaborator = collaboratorRepo.findByUserEmail(new Email("collab@aisafe.com"))
                .orElseThrow();

        controller.editCollaborator(
                collaborator.id(),
                "collab@aisafe.com",
                "+351900000099"
        );

        User updatedUser = userRepo.findByEmail(new Email("collab@aisafe.com"))
                .orElseThrow();

        assertEquals("+351900000099", updatedUser.phoneNumber().value());
        assertEquals(new Email("collab@aisafe.com"),
                collaboratorRepo.findById(collaborator.id()).orElseThrow().userEmail());
    }

    /**
     * Ensures that editing a collaborator requires authentication.
     */
    @Test
    void editCollaboratorRequiresAuthentication() {
        controller.addExistingUserAsCollaborator(
                CustomerType.AIR_TRANSPORT_COMPANY,
                "TAP",
                "collab@aisafe.com",
                "Operations Manager"
        );

        CustomerCollaborator collaborator = collaboratorRepo.findByUserEmail(new Email("collab@aisafe.com"))
                .orElseThrow();

        auth.logout();

        assertThrows(SecurityException.class,
                () -> controller.editCollaborator(
                        collaborator.id(),
                        "new.collab@aisafe.com",
                        "+351900000099"
                ));
    }

    /**
     * Ensures that editing a collaborator does not change name, roles, position,
     * customer type or customer code.
     */
    @Test
    void editCollaboratorKeepsNameRolesPositionAndCustomer() {
        controller.addExistingUserAsCollaborator(
                CustomerType.AIR_TRANSPORT_COMPANY,
                "TAP",
                "collab@aisafe.com",
                "Operations Manager"
        );

        CustomerCollaborator collaborator = collaboratorRepo.findByUserEmail(new Email("collab@aisafe.com"))
                .orElseThrow();

        controller.editCollaborator(
                collaborator.id(),
                "new.collab@aisafe.com",
                "+351900000099"
        );

        User updatedUser = userRepo.findByEmail(new Email("new.collab@aisafe.com"))
                .orElseThrow();

        CustomerCollaborator updatedCollaborator = collaboratorRepo.findById(collaborator.id())
                .orElseThrow();

        assertEquals("Collaborator", updatedUser.name().value());
        assertTrue(updatedUser.hasRole(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR));
        assertEquals("Operations Manager", updatedCollaborator.position());
        assertEquals(CustomerType.AIR_TRANSPORT_COMPANY, updatedCollaborator.customerType());
        assertEquals("TAP", updatedCollaborator.customerCode());
    }

    /**
     * Ensures that editing a collaborator rejects an email already used by
     * another user.
     */
    @Test
    void editCollaboratorRejectsEmailAlreadyUsedByAnotherUser() {
        controller.addExistingUserAsCollaborator(
                CustomerType.AIR_TRANSPORT_COMPANY,
                "TAP",
                "collab@aisafe.com",
                "Operations Manager"
        );

        CustomerCollaborator collaborator = collaboratorRepo.findByUserEmail(new Email("collab@aisafe.com"))
                .orElseThrow();

        assertThrows(IllegalArgumentException.class,
                () -> controller.editCollaborator(
                        collaborator.id(),
                        "other@aisafe.com",
                        "+351900000099"
                ));
    }

    /**
     * Ensures that editing an unknown collaborator is rejected.
     */
    @Test
    void editUnknownCollaboratorIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> controller.editCollaborator(
                        999L,
                        "new.collab@aisafe.com",
                        "+351900000099"
                ));
    }

    /**
     * Ensures that disabling an unknown collaborator is rejected.
     */
    @Test
    void disableUnknownCollaboratorIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> controller.disableCollaborator(999L));
    }

    /**
     * In-memory implementation of {@link UserRepository} used by controller
     * tests.
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
        public boolean existsByEmail(Email email) {
            return users.containsKey(email);
        }

        @Override
        public long count() {
            return users.size();
        }

        @Override
        public void deleteById(Email id) {
            users.remove(id);
        }
    }

    /**
     * In-memory implementation of {@link AirTransportCompanyRepository} used by
     * controller tests.
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

        @Override
        public void deleteById(Long id) {
            // Not needed for these tests.
        }
    }

    /**
     * In-memory implementation of {@link AirControlAreaRepository} used by
     * controller tests.
     */
    private static class InMemoryAirControlAreaRepository implements AirControlAreaRepository {
        private final Map<AirControlAreaCode, AirControlArea> areas = new HashMap<>();

        @Override
        public AirControlArea save(AirControlArea area) {
            areas.put(area.identity(), area);
            return area;
        }

        @Override
        public Optional<AirControlArea> findByCode(AirControlAreaCode code) {
            return Optional.ofNullable(areas.get(code));
        }

        @Override
        public Optional<AirControlArea> findById(AirControlAreaCode id) {
            return findByCode(id);
        }

        @Override
        public Iterable<AirControlArea> findAll() {
            return areas.values();
        }

        @Override
        public void delete(AirControlArea area) {
            areas.remove(area.identity());
        }

        @Override
        public long count() {
            return areas.size();
        }

        @Override
        public void deleteById(AirControlAreaCode id) {
            areas.remove(id);
        }
    }

    /**
     * In-memory implementation of {@link CustomerCollaboratorRepository} used by
     * controller tests.
     *
     * <p>
     * IDs are assigned through reflection because the production entity uses a
     * generated private identifier managed by JPA.
     * </p>
     */
    private static class InMemoryCustomerCollaboratorRepository implements CustomerCollaboratorRepository {
        private final Map<Long, CustomerCollaborator> byId = new HashMap<>();
        private final Map<Email, CustomerCollaborator> byEmail = new HashMap<>();
        private long nextId = 1L;

        @Override
        public CustomerCollaborator save(CustomerCollaborator collaborator) {
            if (collaborator.id() == null) {
                setId(collaborator, nextId++);
            }

            removeOldEmailIndexIfEmailChanged(collaborator);

            byId.put(collaborator.id(), collaborator);
            byEmail.put(collaborator.userEmail(), collaborator);
            return collaborator;
        }

        @Override
        public Optional<CustomerCollaborator> findById(Long id) {
            return Optional.ofNullable(byId.get(id));
        }

        @Override
        public void delete(CustomerCollaborator collaborator) {
            byId.remove(collaborator.id());
            byEmail.remove(collaborator.userEmail());
        }

        @Override
        public Optional<CustomerCollaborator> findByUserEmail(Email email) {
            return Optional.ofNullable(byEmail.get(email));
        }

        @Override
        public Iterable<CustomerCollaborator> findByCustomer(CustomerType customerType, String customerCode) {
            List<CustomerCollaborator> result = new ArrayList<>();
            String normalizedCode = customerCode.trim().toUpperCase();

            for (CustomerCollaborator collaborator : byId.values()) {
                if (collaborator.customerType() == customerType &&
                        collaborator.customerCode().equals(normalizedCode)) {
                    result.add(collaborator);
                }
            }

            return result;
        }

        @Override
        public Iterable<CustomerCollaborator> findAll() {
            return byId.values();
        }

        @Override
        public long count() {
            return byId.size();
        }

        @Override
        public void deleteById(Long id) {
            CustomerCollaborator collaborator = byId.remove(id);
            if (collaborator != null) {
                byEmail.remove(collaborator.userEmail());
            }
        }

        private void setId(CustomerCollaborator collaborator, Long id) {
            try {
                Field field = CustomerCollaborator.class.getDeclaredField("id");
                field.setAccessible(true);
                field.set(collaborator, id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void removeOldEmailIndexIfEmailChanged(CustomerCollaborator collaborator) {
            byEmail.entrySet().removeIf(entry ->
                    entry.getValue().id() != null &&
                            entry.getValue().id().equals(collaborator.id()) &&
                            !entry.getKey().equals(collaborator.userEmail())
            );
        }
    }
}