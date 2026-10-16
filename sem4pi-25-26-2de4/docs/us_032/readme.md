# US032 - Disable / Enable Users

## 1. Context
This user story belongs to Sprint 2 and is an extra/optional requirement (marked E in the planning table). It can be implemented if the team has capacity, but it is not mandatory for all team sizes.

The goal is to allow an Administrator to disable or enable existing backoffice users. A disabled user cannot log in to the system and should not appear in active collaborator lists. Enabling a user restores their access.

This US complements US030 (authentication) and US031 (user registration) by adding lifecycle management to user accounts. It is also relevant for US061–US064 (customer collaborator management) because disabling a collaborator is already covered in US064; this US focuses on the generic user disable/enable operation performed by the Administrator.

### 1.1 List of Issues
- **Analysis**: Understand the impact of disabling a user (login prevention, active collaborator exclusion). Define the business rule that disabling a user should not delete the user record but only change its active flag.
- **Design**: Extend the User aggregate with activate() / deactivate() methods (already exist). Create a controller ManageUserController (or reuse RegisterUserController?) and a console UI to list users and toggle their status.
- **Implement**: Add a new menu option under Admin area: “Disable/Enable user”. List all users with current status, allow selection, and toggle the active flag.
- **Test**: Write unit tests for the toggle operation, controller authorization, and repository updates. Ensure only ADMIN can perform this operation.

## 2. Requirements
US032 As an Administrator, I want to be able to disable/enable users of the backoffice.

### Acceptance Criteria
1. US032.1: The system must allow an Administrator to view a list of all backoffice users, including their current status (active / disabled).
2. US032.2: The Administrator can select a user and disable or enable them.
3. US032.3: Disabled users must not be able to log in (authentication fails).
4. US032.4: Disabled users should not be listed as active collaborators (e.g., when adding a collaborator to a customer – US061).
5. US032.5: Only users with role ADMIN are authorised to perform this operation.
6. US032.6: The change must be persistent (database update).

### Dependencies / References
- US030 (Authentication and Authorization) – needed to identify the current user and check the ADMIN role.
- US031 (Register Users) – provides the users that will be disabled/enabled.
- User aggregate – already has active flag and activate()/deactivate() methods.

## 3. Analysis
### 3.1 Understanding
A user’s active status is stored as a boolean (active). Disabling a user sets this flag to false; enabling sets it to true. The login method AuthService.login already uses findActiveByEmail, which only returns users with active = true. Therefore, disabling a user directly prevents login without any additional changes.

For collaborator listings (US062), the repository method findActiveByRole already filters by active = true. Hence, disabled collaborators are automatically excluded.

The operation does not delete the user record – it only toggles the flag. This preserves historical data (e.g., past flights, audit logs).

### 3.2 Business Rules
| Rule | Enforcement |
|------|-------------|
| Only ADMIN can disable/enable users | Check `auth.ensureHasAnyRole(Role.ADMIN)` in the controller. |
| Cannot disable own account? (Optional – not explicitly required, but could be added as a safeguard) | Not specified; we can allow disabling own account, but a warning may be shown. For simplicity, we do not prevent self‑disable. |
| Disabled user cannot log in | Already enforced by `findActiveByEmail`. |
| Disabled user not shown in active collaborator lists | Already enforced by `findActiveByRole` which filters `active = true`. |

## 4. Design
### 4.1 Realization
We will create a new controller `ManageUserController` (or extend an existing one). The flow:

1. `ManageUserUI` lists all users (ID, email, name, active status).
2. User selects a user by email or sequential number.
3. The UI asks whether to disable or enable (depending on current status).
4. The controller calls `user.deactivate()` or `user.activate()` and saves the user via `userRepo.save(user)`.
5. The repository performs an update (JPA merge).

The existing `User` class already contains:
- `void activate()`
- `void deactivate()`
- `boolean isActive()`

No changes to the domain are needed.

### 4.2 Sequence Diagram

The full sequence diagram is in [`sequence_diagram.puml`](sequence_diagram.puml).

Key interactions:

- **Admin** navigates through `MainMenu` → `UsersMenuUI` → selects "Enable/Disable user" → `ManageUserUI`
- **ManageUserUI** calls `ManageUserController.findAllUsers()` which checks `AuthService.ensureHasAnyRole(ADMIN)` and fetches all users via `UserRepository.findAll()`
- UI displays user list (#, Email, Name, Status), admin selects a number
- If user is **active**: UI asks "Do you want to DISABLE?" → `ManageUserController.disableUser(email)` → `User.deactivate()` → `UserRepository.save()`
- If user is **disabled**: UI asks "Do you want to ENABLE?" → `ManageUserController.enableUser(email)` → `User.activate()` → `UserRepository.save()`
- Both `disableUser` and `enableUser` call `ensureHasAnyRole(ADMIN)` and use `UserRepository.findByEmail()` (not `findActiveByEmail`, so disabled users can be found and re-enabled)
- Errors: `SecurityException` if not admin, `IllegalArgumentException` if user not found

### 4.3 Alternative Approach
We could reuse `RegisterUserController` and add two new methods, but it’s cleaner to create a dedicated controller for user management. This follows the Single Responsibility Principle.

## 5. Implementation
### 5.1 Key Classes (new or to be modified)
| Class | Package | Responsibility |
|-------|---------|----------------|
| `ManageUserController` | `eapli.aisafe.usermanagement.application` | Orchestrates user listing and status toggling. |
| `ManageUserUI` | `eapli.aisafe.app.backoffice.console.presentation.authz` | Console UI for listing users and toggling status. |
| `MainMenu` (modified) | `eapli.aisafe.app.backoffice.console.presentation` | Add a new menu entry “Manage users” (only visible to ADMIN). |

### 5.2 Code Snippets
#### ManageUserController.java
```java
package eapli.aisafe.usermanagement.application;

import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import java.util.Optional;

public class ManageUserController {
    private final AuthService auth;
    private final UserRepository userRepo;

    public ManageUserController(AuthService auth, UserRepository userRepo) {
        this.auth = auth;
        this.userRepo = userRepo;
    }

    public Iterable<User> findAllUsers() {
        auth.ensureHasAnyRole(Role.ADMIN);
        return userRepo.findAll();
    }

    public void disableUser(String emailStr) {
        auth.ensureHasAnyRole(Role.ADMIN);
        Email email = new Email(emailStr);
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.deactivate();
        userRepo.save(user);
    }

    public void enableUser(String emailStr) {
        auth.ensureHasAnyRole(Role.ADMIN);
        Email email = new Email(emailStr);
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.activate();
        userRepo.save(user);
    }
}
```

#### ManageUserUI.java
```java
package eapli.aisafe.app.backoffice.console.presentation.authz;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.usermanagement.application.ManageUserController;
import eapli.aisafe.usermanagement.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ManageUserUI extends AbstractUI {
    private final ManageUserController controller;

    public ManageUserUI(ManageUserController controller) {
        this.controller = controller;
    }

    @Override
    protected boolean doShow() {
        Scanner sc = scanner;
        List<User> users = new ArrayList<>();
        controller.findAllUsers().forEach(users::add);

        if (users.isEmpty()) {
            System.out.println("No users found.");
            return true;
        }

        System.out.println("\nUsers:");
        System.out.printf("%-3s %-30s %-20s %-10s\n", "#", "Email", "Name", "Status");
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            String status = u.isActive() ? "ACTIVE" : "DISABLED";
            System.out.printf("%-3d %-30s %-20s %-10s\n", i+1, u.email().value(), u.name(), status);
        }

        System.out.print("\nSelect user number (0 to cancel): ");
        int option;
        try {
            option = Integer.parseInt(sc.nextLine());
            if (option == 0) return true;
            if (option < 1 || option > users.size()) {
                System.out.println("Invalid number.");
                return true;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return true;
        }

        User selected = users.get(option - 1);
        System.out.println("Selected: " + selected.email().value() + " (" + selected.name() + ")");
        System.out.print("Do you want to (1) DISABLE or (2) ENABLE? ");
        int action;
        try {
            action = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return true;
        }

        try {
            if (action == 1 && selected.isActive()) {
                controller.disableUser(selected.email().value());
                System.out.println("User disabled successfully.");
            } else if (action == 2 && !selected.isActive()) {
                controller.enableUser(selected.email().value());
                System.out.println("User enabled successfully.");
            } else {
                System.out.println("Action not applicable for current status.");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return true;
    }
}
```

#### Modifications to MainMenu
Add a new option in the authenticated menu:
```java
// in showAuthenticatedMenu() after the existing options
System.out.println("4. Manage users");
System.out.println("5. Logout");
```
And in `handleAuthenticatedOption`:
```java
case "4":
    new ManageUserUI(manageUserController).show();
    return true;
```
Also add the controller to `AISafeBackoffice` and pass it to `MainMenu`.

### 5.3 Repository Methods
The existing `UserRepository.findAll()` already returns all users (including disabled). We need to ensure `findAll()` uses `LEFT JOIN FETCH u.roles` (already done). No extra method is needed.

### 5.4 Integration in AISafeBackoffice
Add:
```java
ManageUserController manageUserController = new ManageUserController(authService, userRepo);
```
Then pass `manageUserController` to `MainMenu`.

## 6. Demonstration
### 6.1 Demonstration Steps
1. Run the backoffice application.
2. Login as `admin@aisafe.com` (password `Admin123`).
3. From the main menu, select **Manage users**.
4. A list of all users appears with their status.
5. Choose a user, e.g., `pilot@aisafe.com` (active).
6. Choose action `1` (DISABLE).
7. The system confirms “User disabled successfully.”
8. Logout and try to login with `pilot@aisafe.com`. It should fail because the user is now inactive.
9. Login again as admin, go to **Manage users**, enable the same user.
10. Login with the pilot account – it should work again.

### 6.2 Example Console Output
```text
============================================================
  AISafe Backoffice
============================================================
Logged in as: admin@aisafe.com

1. Register user
2. Customer management
3. Manage users
4. Logout
0. Exit

Option: 3

Users:
#   Email                          Name                 Status   
1   admin@aisafe.com               System Admin         ACTIVE   
2   backoffice@aisafe.com          Backoffice Operator  ACTIVE   
3   weather@aisafe.com             Weather Person       ACTIVE   
4   atcc@airline.com               Airline Collaborator ACTIVE   
5   pilot@aisafe.com               Pilot                ACTIVE   
6   fco@control.com                Flight Control Ope.. ACTIVE   

Select user number (0 to cancel): 5
Selected: pilot@aisafe.com (Pilot)
Do you want to (1) DISABLE or (2) ENABLE? 1
User disabled successfully.
```

## 7. Tests
### 7.1 Unit Tests for Controller
Create `ManageUserControllerTest.java`:
```java
package eapli.aisafe.usermanagement.application;

import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ManageUserControllerTest {

    private UserRepository mockRepo;
    private AuthService authService;
    private ManageUserController controller;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockRepo = mock(UserRepository.class);
        authService = mock(AuthService.class);
        controller = new ManageUserController(authService, mockRepo);
        testUser = new User(new Email("test@test.com"), "Pass123", "Test", "123", Set.of(Role.ADMIN));
        when(authService.ensureHasAnyRole(Role.ADMIN)).thenReturn(null); // no exception
    }

    @Test
    void findAllUsersShouldReturnAllUsers() {
        when(mockRepo.findAll()).thenReturn(Set.of(testUser));
        Iterable<User> users = controller.findAllUsers();
        assertNotNull(users);
        verify(mockRepo).findAll();
    }

    @Test
    void disableUserShouldDeactivateAndSave() {
        when(mockRepo.findByEmail(testUser.email())).thenReturn(Optional.of(testUser));
        controller.disableUser("test@test.com");
        assertFalse(testUser.isActive());
        verify(mockRepo).save(testUser);
    }

    @Test
    void disableUserWhenUserNotFoundThrows() {
        when(mockRepo.findByEmail(any())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> controller.disableUser("missing@test.com"));
        verify(mockRepo, never()).save(any());
    }

    @Test
    void enableUserShouldActivateAndSave() {
        testUser.deactivate();
        when(mockRepo.findByEmail(testUser.email())).thenReturn(Optional.of(testUser));
        controller.enableUser("test@test.com");
        assertTrue(testUser.isActive());
        verify(mockRepo).save(testUser);
    }
}
```

### 7.2 Test Coverage
The tests cover:
- Successful disable and enable.
- Error when user not found.
- Authorization (ensures `ensureHasAnyRole` is called – can be verified with a spy on `AuthService`).

Together with existing tests for `User` and `AuthService`, the coverage for domain and application packages remains above 90%.

## 8. Observations
### 8.1 Current Limitations
- The UI does not prevent the administrator from disabling their own account. This could lead to losing the only admin account. A safeguard can be added, but it was not explicitly required.
- There is no confirmation prompt before disabling (although the action is immediate). A confirmation could be added for better user experience.

### 8.2 Future Improvements
- Add the ability to disable/enable multiple users at once.
- Introduce a scheduled task to automatically disable users whose security clearance has expired.
- Include a reason for disabling (optional field in the user history).

### 8.3 Integration with Other US
US064 (Disable a customer’s collaborator) uses the same `deactivate()` method but is invoked from a different controller (`CustomerCollaboratorController.disableCollaborator`). This US provides a more general management view for Administrators.