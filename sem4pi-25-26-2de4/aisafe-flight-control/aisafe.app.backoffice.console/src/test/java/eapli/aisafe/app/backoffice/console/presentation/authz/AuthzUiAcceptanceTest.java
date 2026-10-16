package eapli.aisafe.app.backoffice.console.presentation.authz;

import eapli.aisafe.usermanagement.application.AuthService;
import eapli.aisafe.usermanagement.application.ManageUserController;
import eapli.aisafe.usermanagement.application.RegisterUserController;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UI acceptance-style tests for authentication and user management stories:
 *
 * <ul>
 *     <li>US030 - Authentication and Authorization (login, logout)</li>
 *     <li>US031 - Register users</li>
 *     <li>US032 - Disable/Enable users</li>
 *     <li>US033 - List users</li>
 * </ul>
 *
 * <p>
 * These tests simulate console input/output and verify that each UI delegates
 * the expected operation to its controller and displays the correct messages.
 * </p>
 */
class AuthzUiAcceptanceTest {

    private final PrintStream originalOut = System.out;
    private final java.io.InputStream originalIn = System.in;

    private CapturingUserRepository userRepo;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepo = new CapturingUserRepository();
        authService = new AuthService(userRepo);
    }

    @AfterEach
    void restoreConsole() {
        System.setOut(originalOut);
        System.setIn(originalIn);
        authService.logout();
    }

    // ==================== HELPERS ====================

    private ConsoleResult runUiWithInput(String input, Runnable uiAction) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        uiAction.run();
        return new ConsoleResult(output.toString(StandardCharsets.UTF_8));
    }

    private User user(String email, String name, String phoneNumber, Role role) {
        return new User(new Email(email), "Password123", eapli.aisafe.generic.domain.UserName.of(name), eapli.aisafe.generic.domain.PhoneNumber.of(phoneNumber), Set.of(role));
    }

    private record ConsoleResult(String output) {}

    // ==================== US030 - LOGIN ====================

    @Test
    void us030LoginWithValidCredentialsShouldSucceed() {
        User admin = user("admin@aisafe.com", "Admin User", "123456789", Role.ADMIN);
        userRepo.addUser(admin);

        ConsoleResult result = runUiWithInput(
                "admin@aisafe.com\nPassword123\n",
                () -> new LoginUI(authService).show()
        );

        assertTrue(result.output.contains("Email:"));
        assertTrue(result.output.contains("Password:"));
        assertTrue(result.output.contains("Login successful!"));
        assertTrue(authService.currentUser().isPresent());
        assertEquals("admin@aisafe.com", authService.currentUser().get().email().value());
    }

    @Test
    void us030LoginWithWrongPasswordShouldFail() {
        User admin = user("admin@aisafe.com", "Admin User", "123456789", Role.ADMIN);
        userRepo.addUser(admin);

        ConsoleResult result = runUiWithInput(
                "admin@aisafe.com\nWrongPass1\n",
                () -> new LoginUI(authService).show()
        );

        assertTrue(result.output.contains("Invalid credentials or inactive user."));
        assertFalse(authService.currentUser().isPresent());
    }

    @Test
    void us030LoginWithNonExistentEmailShouldFail() {
        ConsoleResult result = runUiWithInput(
                "nobody@aisafe.com\nSomePass1\n",
                () -> new LoginUI(authService).show()
        );

        assertTrue(result.output.contains("Invalid credentials or inactive user."));
        assertFalse(authService.currentUser().isPresent());
    }

    @Test
    void us030LoginWithDisabledUserShouldFail() {
        User disabled = user("disabled@aisafe.com", "Disabled User", "123456789", Role.ADMIN);
        disabled.deactivate();
        userRepo.addUser(disabled);

        ConsoleResult result = runUiWithInput(
                "disabled@aisafe.com\nPassword123\n",
                () -> new LoginUI(authService).show()
        );

        assertTrue(result.output.contains("Invalid credentials or inactive user."));
        assertFalse(authService.currentUser().isPresent());
    }

    // ==================== US030 - LOGOUT ====================

    @Test
    void us030LogoutShouldClearSession() {
        User admin = user("admin@aisafe.com", "Admin User", "123456789", Role.ADMIN);
        userRepo.addUser(admin);
        authService.login("admin@aisafe.com", "Password123");
        assertTrue(authService.currentUser().isPresent());

        ConsoleResult result = runUiWithInput(
                "",
                () -> new LogoutUI(authService).show()
        );

        assertTrue(result.output.contains("Logged out."));
        assertFalse(authService.currentUser().isPresent());
    }

    // ==================== US031 - REGISTER USER ====================

    @Test
    void us031RegisterUserShouldSucceedWhenAdmin() {
        User admin = user("admin@aisafe.com", "Admin User", "123456789", Role.ADMIN);
        userRepo.addUser(admin);
        authService.login("admin@aisafe.com", "Password123");

        CapturingRegisterUserController controller = new CapturingRegisterUserController(authService, userRepo);

        ConsoleResult result = runUiWithInput(
                "newuser@test.com\nPassword123\nNew User\n987654321\n4\n0\n",
                () -> new RegisterUserUI(controller).show()
        );

        assertEquals("newuser@test.com", controller.capturedEmail);
        assertEquals("Password123", controller.capturedPassword);
        assertEquals("New User", controller.capturedName);
        assertEquals("987654321", controller.capturedPhone);
        assertNotNull(controller.capturedRoles);
        assertTrue(controller.capturedRoles.contains(Role.PILOT));

        assertTrue(result.output.contains("Email:"));
        assertTrue(result.output.contains("Password"));
        assertTrue(result.output.contains("Full name:"));
        assertTrue(result.output.contains("Phone number:"));
        assertTrue(result.output.contains("User registered successfully."));
    }

    @Test
    void us031RegisterUserWithDuplicateEmailShouldFail() {
        User admin = user("admin@aisafe.com", "Admin User", "123456789", Role.ADMIN);
        userRepo.addUser(admin);
        User existing = user("existing@test.com", "Existing", "123456789", Role.PILOT);
        userRepo.addUser(existing);
        authService.login("admin@aisafe.com", "Password123");

        CapturingRegisterUserController controller = new CapturingRegisterUserController(authService, userRepo);

        ConsoleResult result = runUiWithInput(
                "existing@test.com\nPassword123\nNew User\n987654321\n4\n0\n",
                () -> new RegisterUserUI(controller).show()
        );

        assertTrue(result.output.contains("Error: Email already registered"));
    }

    @Test
    void us031RegisterUserWithInvalidPasswordShouldFail() {
        User admin = user("admin@aisafe.com", "Admin User", "123456789", Role.ADMIN);
        userRepo.addUser(admin);
        authService.login("admin@aisafe.com", "Password123");

        CapturingRegisterUserController controller = new CapturingRegisterUserController(authService, userRepo);

        ConsoleResult result = runUiWithInput(
                "newuser@test.com\nshort\nNew User\n987654321\n4\n0\n",
                () -> new RegisterUserUI(controller).show()
        );

        assertTrue(result.output.contains("Error:"));
    }

    @Test
    void us031RegisterUserWithoutAdminRoleShouldFail() {
        User pilot = user("pilot@aisafe.com", "Pilot", "123456789", Role.PILOT);
        userRepo.addUser(pilot);
        authService.login("pilot@aisafe.com", "Password123");

        CapturingRegisterUserController controller = new CapturingRegisterUserController(authService, userRepo);

        ConsoleResult result = runUiWithInput(
                "newuser@test.com\nPassword123\nNew User\n987654321\n4\n0\n",
                () -> new RegisterUserUI(controller).show()
        );

        assertTrue(result.output.contains("Error: User does not have required role"));
    }

    @Test
    void us031RegisterUserWithCollaboratorSubtypeATCC() {
        User admin = user("admin@aisafe.com", "Admin User", "123456789", Role.ADMIN);
        userRepo.addUser(admin);
        authService.login("admin@aisafe.com", "Password123");

        CapturingRegisterUserController controller = new CapturingRegisterUserController(authService, userRepo);

        // role 3 (COLLABORATOR), sub-type 1 (ATCC), 0 finish
        ConsoleResult result = runUiWithInput(
                "collab@test.com\nPassword123\nCollab User\n987654321\n3\n1\n0\n",
                () -> new RegisterUserUI(controller).show()
        );

        assertEquals("collab@test.com", controller.capturedEmail);
        assertNotNull(controller.capturedRoles);
        assertTrue(controller.capturedRoles.contains(Role.AIR_TRANSPORT_COMPANY_COLLABORATOR));
        assertFalse(controller.capturedRoles.contains(Role.COLLABORATOR));
        assertTrue(result.output.contains("User registered successfully."));
    }

    @Test
    void us031RegisterUserWithCollaboratorSubtypeFCO() {
        User admin = user("admin@aisafe.com", "Admin User", "123456789", Role.ADMIN);
        userRepo.addUser(admin);
        authService.login("admin@aisafe.com", "Password123");

        CapturingRegisterUserController controller = new CapturingRegisterUserController(authService, userRepo);

        // role 3 (COLLABORATOR), sub-type 2 (FCO), 0 finish
        ConsoleResult result = runUiWithInput(
                "fco@test.com\nPassword123\nFCO User\n987654321\n3\n2\n0\n",
                () -> new RegisterUserUI(controller).show()
        );

        assertEquals("fco@test.com", controller.capturedEmail);
        assertNotNull(controller.capturedRoles);
        assertTrue(controller.capturedRoles.contains(Role.FLIGHT_CONTROL_OPERATOR));
        assertFalse(controller.capturedRoles.contains(Role.COLLABORATOR));
        assertTrue(result.output.contains("User registered successfully."));
    }

    @Test
    void us031RegisterUserWithCollaboratorNoSubtype() {
        User admin = user("admin@aisafe.com", "Admin User", "123456789", Role.ADMIN);
        userRepo.addUser(admin);
        authService.login("admin@aisafe.com", "Password123");

        CapturingRegisterUserController controller = new CapturingRegisterUserController(authService, userRepo);

        // role 3 (COLLABORATOR), 0 (None), 0 finish
        ConsoleResult result = runUiWithInput(
                "gencollab@test.com\nPassword123\nGen Collab\n987654321\n3\n0\n0\n",
                () -> new RegisterUserUI(controller).show()
        );

        assertEquals("gencollab@test.com", controller.capturedEmail);
        assertNotNull(controller.capturedRoles);
        assertTrue(controller.capturedRoles.contains(Role.COLLABORATOR));
        assertTrue(result.output.contains("User registered successfully."));
    }

    // ==================== US032 - DISABLE/ENABLE USER ====================

    @Test
    void us032DisableUserShouldSucceedWhenAdmin() {
        User admin = user("admin@aisafe.com", "Admin User", "123456789", Role.ADMIN);
        userRepo.addUser(admin);
        authService.login("admin@aisafe.com", "Password123");

        User target = user("target@test.com", "Target User", "987654321", Role.PILOT);
        userRepo.addUser(target);

        CapturingManageUserController controller = new CapturingManageUserController(authService, userRepo);

        // admin is #1, target is #2
        ConsoleResult result = runUiWithInput(
                "2\ny\n",
                () -> new ManageUserUI(controller).show()
        );

        assertEquals("target@test.com", controller.disabledEmail);
        assertFalse(target.isActive());

        assertTrue(result.output.contains("Users:"));
        assertTrue(result.output.contains("target@test.com"));
        assertTrue(result.output.contains("ACTIVE"));
        assertTrue(result.output.contains("Do you want to DISABLE this user?"));
        assertTrue(result.output.contains("User disabled successfully."));
    }

    @Test
    void us032EnableUserShouldSucceedWhenAdmin() {
        User admin = user("admin@aisafe.com", "Admin User", "123456789", Role.ADMIN);
        userRepo.addUser(admin);
        authService.login("admin@aisafe.com", "Password123");

        User target = user("disabled@test.com", "Disabled User", "987654321", Role.PILOT);
        target.deactivate();
        userRepo.addUser(target);

        CapturingManageUserController controller = new CapturingManageUserController(authService, userRepo);

        // admin is #1, target is #2
        ConsoleResult result = runUiWithInput(
                "2\ny\n",
                () -> new ManageUserUI(controller).show()
        );

        assertEquals("disabled@test.com", controller.enabledEmail);
        assertTrue(target.isActive());

        assertTrue(result.output.contains("DISABLED"));
        assertTrue(result.output.contains("Do you want to ENABLE this user?"));
        assertTrue(result.output.contains("User enabled successfully."));
    }

    @Test
    void us032DisableUserCancelledByOperator() {
        User admin = user("admin@aisafe.com", "Admin User", "123456789", Role.ADMIN);
        userRepo.addUser(admin);
        authService.login("admin@aisafe.com", "Password123");

        User target = user("target@test.com", "Target User", "987654321", Role.PILOT);
        userRepo.addUser(target);

        CapturingManageUserController controller = new CapturingManageUserController(authService, userRepo);

        // admin is #1, target is #2
        ConsoleResult result = runUiWithInput(
                "2\nn\n",
                () -> new ManageUserUI(controller).show()
        );

        assertNull(controller.disabledEmail);
        assertTrue(target.isActive());
        assertTrue(result.output.contains("Operation cancelled."));
    }

    @Test
    void us032ManageUserWithCancelledSelection() {
        User admin = user("admin@aisafe.com", "Admin User", "123456789", Role.ADMIN);
        userRepo.addUser(admin);
        authService.login("admin@aisafe.com", "Password123");

        User target = user("target@test.com", "Target User", "987654321", Role.PILOT);
        userRepo.addUser(target);

        CapturingManageUserController controller = new CapturingManageUserController(authService, userRepo);

        ConsoleResult result = runUiWithInput(
                "0\n",
                () -> new ManageUserUI(controller).show()
        );

        assertNull(controller.disabledEmail);
        assertNull(controller.enabledEmail);
        assertTrue(target.isActive());
    }

    @Test
    void us032ManageUserWithInvalidNumber() {
        User admin = user("admin@aisafe.com", "Admin User", "123456789", Role.ADMIN);
        userRepo.addUser(admin);
        authService.login("admin@aisafe.com", "Password123");

        User target = user("target@test.com", "Target User", "987654321", Role.PILOT);
        userRepo.addUser(target);

        CapturingManageUserController controller = new CapturingManageUserController(authService, userRepo);

        ConsoleResult result = runUiWithInput(
                "99\n",
                () -> new ManageUserUI(controller).show()
        );

        assertNull(controller.disabledEmail);
        assertTrue(result.output.contains("Invalid number."));
    }

    @Test
    void us032ManageUserWithNoUsers() {
        User admin = user("admin@aisafe.com", "Admin User", "123456789", Role.ADMIN);
        userRepo.addUser(admin);
        authService.login("admin@aisafe.com", "Password123");

        CapturingManageUserController controller = new CapturingManageUserController(authService, userRepo);

        ConsoleResult result = runUiWithInput(
                "0",
                () -> new ManageUserUI(controller).show()
        );

        assertNull(controller.disabledEmail);
        assertNull(controller.enabledEmail);
        assertTrue(result.output.contains("admin@aisafe.com"));
    }

    // ==================== US033 - LIST USERS ====================

    @Test
    void us033ListUsersShouldDisplayAllUsersWithDetails() {
        User admin = user("admin@aisafe.com", "Admin User", "123456789", Role.ADMIN);
        userRepo.addUser(admin);
        authService.login("admin@aisafe.com", "Password123");

        User pilot = user("pilot@aisafe.com", "Pilot User", "987654321", Role.PILOT);
        userRepo.addUser(pilot);

        User disabled = user("disabled@aisafe.com", "Disabled User", "555555555", Role.WEATHER_PERSON);
        disabled.deactivate();
        userRepo.addUser(disabled);

        CapturingManageUserController controller = new CapturingManageUserController(authService, userRepo);

        ConsoleResult result = runUiWithInput(
                "",
                () -> new ListUsersUI(controller).show()
        );

        assertTrue(result.output.contains("Backoffice Users"));
        assertTrue(result.output.contains("admin@aisafe.com"));
        assertTrue(result.output.contains("Admin User"));
        assertTrue(result.output.contains("pilot@aisafe.com"));
        assertTrue(result.output.contains("Pilot User"));
        assertTrue(result.output.contains("disabled@aisafe.com"));
        assertTrue(result.output.contains("Disabled User"));
        assertTrue(result.output.contains("ACTIVE"));
        assertTrue(result.output.contains("DISABLED"));
        assertTrue(result.output.contains("Total users: 3"));
    }

    @Test
    void us033ListUsersShouldShowRoles() {
        User admin = user("admin@aisafe.com", "Admin User", "123456789", Role.ADMIN);
        userRepo.addUser(admin);
        authService.login("admin@aisafe.com", "Password123");

        User multiRole = user("multi@test.com", "Multi Role", "123456789", Role.PILOT);
        userRepo.addUser(multiRole);

        CapturingManageUserController controller = new CapturingManageUserController(authService, userRepo);

        ConsoleResult result = runUiWithInput(
                "",
                () -> new ListUsersUI(controller).show()
        );

        assertTrue(result.output.contains("ADMIN"));
        assertTrue(result.output.contains("PILOT"));
    }

    // ==================== INNER CLASSES ====================

    /**
     * In-memory user repository for acceptance tests.
     */
    private static class CapturingUserRepository implements UserRepository {
        private final Map<String, User> users = new LinkedHashMap<>();

        void addUser(User user) {
            users.put(user.email().value(), user);
        }

        @Override
        public Optional<User> findByEmail(Email email) {
            return Optional.ofNullable(users.get(email.value()));
        }

        @Override
        public Optional<User> findActiveByEmail(Email email) {
            User user = users.get(email.value());
            if (user != null && user.isActive()) {
                return Optional.of(user);
            }
            return Optional.empty();
        }

        @Override
        public Iterable<User> findActiveByRole(Role role) {
            return users.values().stream()
                    .filter(User::isActive)
                    .filter(u -> u.hasRole(role))
                    .toList();
        }

        @Override
        public boolean existsByEmail(Email email) {
            return users.containsKey(email.value());
        }

        @Override
        public Iterable<User> findAll() {
            return new ArrayList<>(users.values());
        }

        @Override
        public User save(User entity) {
            users.put(entity.email().value(), entity);
            return entity;
        }

        @Override
        public void deleteById(Email email) {
            users.remove(email.value());
        }

        @Override
        public long count() {
            return users.size();
        }

        @Override
        public void delete(User entity) {
            users.remove(entity.email().value());
        }

        @Override
        public Optional<User> findById(Email email) {
            return findByEmail(email);
        }
    }

    /**
     * Capturing controller that records registerUser arguments.
     */
    private static class CapturingRegisterUserController extends RegisterUserController {
        String capturedEmail;
        String capturedPassword;
        String capturedName;
        String capturedPhone;
        Set<Role> capturedRoles;

        CapturingRegisterUserController(AuthService auth, UserRepository userRepo) {
            super(auth, userRepo);
        }

        @Override
        public void registerUser(String emailStr, String password, String name,
                                 String phone, Set<Role> roles) {
            this.capturedEmail = emailStr;
            this.capturedPassword = password;
            this.capturedName = name;
            this.capturedPhone = phone;
            this.capturedRoles = roles;
            super.registerUser(emailStr, password, name, phone, roles);
        }
    }

    /**
     * Capturing controller that records disable/enable operations.
     */
    private static class CapturingManageUserController extends ManageUserController {
        String disabledEmail;
        String enabledEmail;

        CapturingManageUserController(AuthService auth, UserRepository userRepo) {
            super(auth, userRepo);
        }

        @Override
        public void disableUser(String emailStr) {
            this.disabledEmail = emailStr;
            super.disableUser(emailStr);
        }

        @Override
        public void enableUser(String emailStr) {
            this.enabledEmail = emailStr;
            super.enableUser(emailStr);
        }
    }
}

