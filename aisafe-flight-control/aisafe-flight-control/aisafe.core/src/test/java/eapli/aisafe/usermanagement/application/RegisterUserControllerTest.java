package eapli.aisafe.usermanagement.application;

import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.generic.domain.UserName;
import eapli.aisafe.generic.domain.PhoneNumber;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RegisterUserControllerTest {

    private UserRepository mockRepo;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        mockRepo = mock(UserRepository.class);
        authService = new AuthService(mockRepo);
        // Garante que o ThreadLocal fica limpo antes de cada teste
        authService.logout();
    }

    // ==================== LOGIN TESTS ====================

    @Test
    void loginWithValidCredentialsShouldSucceed() {
        Email email = new Email("admin@aisafe.com");
        User activeUser = new User(email, "Admin123", UserName.of("Admin"), PhoneNumber.of("1234567"), Set.of(Role.ADMIN));
        when(mockRepo.findActiveByEmail(email)).thenReturn(Optional.of(activeUser));

        Optional<User> result = authService.login("admin@aisafe.com", "Admin123");

        assertTrue(result.isPresent());
        assertEquals(activeUser, result.get());
        assertTrue(authService.currentUser().isPresent());
        assertEquals(activeUser, authService.currentUser().get());
    }

    @Test
    void loginWithInactiveUserShouldFail() {
        // Um utilizador inactivo NUNCA é devolvido por findActiveByEmail
        Email email = new Email("inactive@aisafe.com");
        when(mockRepo.findActiveByEmail(email)).thenReturn(Optional.empty());

        Optional<User> result = authService.login("inactive@aisafe.com", "anyPassword");

        assertFalse(result.isPresent());
        assertFalse(authService.currentUser().isPresent());
    }

    @Test
    void loginWithWrongPasswordShouldFail() {
        Email email = new Email("admin@aisafe.com");
        User activeUser = new User(email, "Admin123", UserName.of("Admin"), PhoneNumber.of("1234567"), Set.of(Role.ADMIN));
        when(mockRepo.findActiveByEmail(email)).thenReturn(Optional.of(activeUser));

        Optional<User> result = authService.login("admin@aisafe.com", "WrongPassword");

        assertFalse(result.isPresent());
        assertFalse(authService.currentUser().isPresent());
    }

    @Test
    void loginWithNonExistentEmailShouldFail() {
        Email email = new Email("unknown@aisafe.com");
        when(mockRepo.findActiveByEmail(email)).thenReturn(Optional.empty());

        Optional<User> result = authService.login("unknown@aisafe.com", "any");

        assertFalse(result.isPresent());
        assertFalse(authService.currentUser().isPresent());
    }

    // ==================== SESSION TESTS ====================

    @Test
    void currentUserWhenNotLoggedInReturnsEmpty() {
        // O setUp já fez logout, portanto não há sessão
        assertFalse(authService.currentUser().isPresent());
    }

    @Test
    void logoutClearsSession() {
        // Simula login primeiro
        Email email = new Email("admin@aisafe.com");
        User user = new User(email, "Admin123", UserName.of("Admin"), PhoneNumber.of("1234567"), Set.of(Role.ADMIN));
        when(mockRepo.findActiveByEmail(email)).thenReturn(Optional.of(user));
        authService.login("admin@aisafe.com", "Admin123");

        assertTrue(authService.currentUser().isPresent());

        authService.logout();
        assertFalse(authService.currentUser().isPresent());
    }

    // ==================== REGISTER USER TESTS ====================

    @Test
    void registerUserShouldSucceedWhenAdmin() {
        // Setup admin user
        Email adminEmail = new Email("admin@aisafe.com");
        User admin = new User(adminEmail, "Admin123", UserName.of("Admin"), PhoneNumber.of("1234567"), Set.of(Role.ADMIN));
        when(mockRepo.findActiveByEmail(adminEmail)).thenReturn(Optional.of(admin));
        authService.login("admin@aisafe.com", "Admin123");

        // Setup controller with real AuthService
        RegisterUserController controller = new RegisterUserController(authService, mockRepo);

        // Mock that email doesn't exist yet
        Email newUserEmail = new Email("newuser@test.com");
        when(mockRepo.existsByEmail(newUserEmail)).thenReturn(false);

        // Register new user
        assertDoesNotThrow(() -> controller.registerUser(
                "newuser@test.com", "Password123", "New User", "123456789", Set.of(Role.PILOT)));

        // Verify save was called
        verify(mockRepo).save(any(User.class));
    }

    @Test
    void registerUserWithExistingEmailThrows() {
        // Setup admin user
        Email adminEmail = new Email("admin@aisafe.com");
        User admin = new User(adminEmail, "Admin123", UserName.of("Admin"), PhoneNumber.of("1234567"), Set.of(Role.ADMIN));
        when(mockRepo.findActiveByEmail(adminEmail)).thenReturn(Optional.of(admin));
        authService.login("admin@aisafe.com", "Admin123");

        // Setup controller
        RegisterUserController controller = new RegisterUserController(authService, mockRepo);

        // Mock that email already exists
        Email existingEmail = new Email("existing@test.com");
        when(mockRepo.existsByEmail(existingEmail)).thenReturn(true);

        // Try to register with existing email
        assertThrows(IllegalArgumentException.class, () -> controller.registerUser(
                "existing@test.com", "Password123", "Existing User", "123", Set.of(Role.PILOT)));

        verify(mockRepo, never()).save(any(User.class));
    }

    @Test
    void registerUserWithoutAdminRoleThrows() {
        // Setup pilot user (non-admin)
        Email pilotEmail = new Email("pilot@aisafe.com");
        User pilot = new User(pilotEmail, "Pilot123", UserName.of("Pilot"), PhoneNumber.of("1234567"), Set.of(Role.PILOT));
        when(mockRepo.findActiveByEmail(pilotEmail)).thenReturn(Optional.of(pilot));
        authService.login("pilot@aisafe.com", "Pilot123");

        // Setup controller
        RegisterUserController controller = new RegisterUserController(authService, mockRepo);

        // Try to register - should throw SecurityException
        assertThrows(SecurityException.class, () -> controller.registerUser(
                "newuser@test.com", "Password123", "New User", "123", Set.of(Role.PILOT)));

        verify(mockRepo, never()).save(any(User.class));
    }

    @Test
    void registerUserWithInvalidPasswordThrows() {
        // Setup admin user
        Email adminEmail = new Email("admin@aisafe.com");
        User admin = new User(adminEmail, "Admin123", UserName.of("Admin"), PhoneNumber.of("1234567"), Set.of(Role.ADMIN));
        when(mockRepo.findActiveByEmail(adminEmail)).thenReturn(Optional.of(admin));
        authService.login("admin@aisafe.com", "Admin123");

        // Setup controller
        RegisterUserController controller = new RegisterUserController(authService, mockRepo);

        // Try to register with invalid password (too short)
        assertThrows(IllegalArgumentException.class, () -> controller.registerUser(
                "newuser@test.com", "short", "New User", "123", Set.of(Role.PILOT)));
    }

    @Test
    void ensureHasAnyRoleWhenUserLacksRequiredRoleThrows() {
        Email email = new Email("pilot@aisafe.com");
        User pilot = new User(email, "Pilot123", UserName.of("Pilot"), PhoneNumber.of("1234567"), Set.of(Role.PILOT));
        when(mockRepo.findActiveByEmail(email)).thenReturn(Optional.of(pilot));
        authService.login("pilot@aisafe.com", "Pilot123");

        assertThrows(SecurityException.class, () -> authService.ensureHasAnyRole(Role.ADMIN));
    }

    @Test
    void ensureHasAnyRoleWhenNotAuthenticatedThrows() {
        // Já fizemos logout no setUp, portanto não há sessão
        assertThrows(SecurityException.class, () -> authService.ensureHasAnyRole(Role.ADMIN));
    }
}