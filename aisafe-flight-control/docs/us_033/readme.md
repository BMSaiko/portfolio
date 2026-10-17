# US033 – List Users

## Context

This user story belongs to Sprint 2 and is marked as extra/optional (E) for some team sizes in the planning table. It provides a simple but valuable administrative view: listing all backoffice users with their current status (active/disabled).

The goal is to allow an Administrator to see who is registered in the system, what roles they have, and whether the account is active. This is particularly useful for auditing, for checking the effect of disable/enable operations (US032), and for managing collaborators (US061–US064).

### List of Issues

- **Analysis:** Determine which user information should be shown (email, name, phone, roles, active status) and whether filtering or pagination is needed.
- **Design:** Create a controller (ListUsersController) that retrieves all users and a UI that displays them in a clean, readable format.
- **Implement:** Add a menu option for "List users" that is only visible to users with role ADMIN.
- **Test:** Verify that the controller returns all users (including disabled), that authorization is enforced, and that the UI correctly formats the output.

## Requirements

**US033** As an Administrator, I want to be able to list the users of the backoffice, including their status.

### Acceptance Criteria

- **US033.1:** The system must display a list of all backoffice users.
- **US033.2:** For each user, the following information must be shown: email, full name, phone number, assigned roles, and whether the account is active or disabled.
- **US033.3:** Only authenticated users with the role ADMIN are allowed to list users.
- **US033.4:** The list must include both active and disabled users (no filtering by status).
- **US033.5:** The list must be presented in a human‑readable, tabular format (console).

### Dependencies / References

- **US030** (Authentication and Authorization) – needed to identify the current user and check the ADMIN role.
- **US031** (Register Users) – provides the users that will be listed.
- **US032** (Disable/Enable Users) – not required, but the status shown is the same as used by US032.

## Analysis

### Understanding

The system's User aggregate already holds all necessary data:

- **Email** (value object)
- **name** (String)
- **phoneNumber** (String)
- **roles** (Set<Role>)
- **active** (boolean)

The UserRepository provides a `findAll()` method that returns all users (including those with `active = false`). No additional query is required.

The operation is read‑only and does not modify any state.

### Business Rules

| Rule | Enforcement |
|------|-------------|
| Only ADMIN can list users | The controller calls `auth.ensureHasAnyRole(Role.ADMIN)`. |
| Disabled users are included | `findAll()` returns all records; the UI shows the status column. |
| No pagination required | The project backlog does not request pagination for this initial version. |

### Format of the Output

A typical console output example:

```text
================================================================================
Backoffice Users
================================================================================
Email                     Name               Phone          Roles                Status
--------------------------------------------------------------------------------
admin@aisafe.com          System Admin       +35191111111   ADMIN                ACTIVE
backoffice@aisafe.com     Backoffice Ops      +35192222222   BACKOFFICE_OPERATOR  ACTIVE
weather@aisafe.com        Weather Person     +35193333333   WEATHER_PERSON       ACTIVE
pilot@aisafe.com          John Pilot         +35194444444   PILOT                DISABLED
...
--------------------------------------------------------------------------------
Total users: 12
```

## Design

### Realization

The implementation follows the same layered architecture as US030–US032:

1. The user selects "List users" from the main menu (only visible after authentication and role check).
2. The ListUsersUI calls `ListUsersController.findAllUsers()`.
3. The controller delegates to `UserRepository.findAll()` and returns the iterable.
4. The UI iterates over the collection and prints each user's details.

Because the operation is read‑only, no transaction write is needed. The UI runs inside an active transaction context (managed by the JPA layer), but since the controller returns a detached collection, we ensure that roles are fetched eagerly (or we use JOIN FETCH in the repository query) to avoid LazyInitializationException.

### Sequence Diagram

The full sequence diagram is in [`sequence_diagram.puml`](sequence_diagram.puml).

Key interactions:

- **Admin** navigates through `MainMenu` → `UsersMenuUI` → selects "List users" → `ListUsersUI`
- **ListUsersUI** calls `ManageUserController.findAllUsers()` which checks `AuthService.ensureHasAnyRole(ADMIN)` and fetches all users via `UserRepository.findAll()`
- `findAll()` uses `LEFT JOIN FETCH u.roles` to eagerly load roles, avoiding `LazyInitializationException` when the UI accesses `user.roles()` outside the transaction
- UI iterates and prints a formatted table: Email, Name, Phone, Roles, Status (ACTIVE/DISABLED), plus total count
- Errors: `SecurityException` if not admin

### Important Design Decision: Eager Fetching of Roles

The default JPA `findAll()` returns entities with lazy associations. If accessed outside the transaction, `user.roles()` would throw a `LazyInitializationException`. To avoid that, the repository method must use an entity graph or a JPQL query with JOIN FETCH.

We will override `findAll()` in `JpaUserRepository`:

```java
@Override
public Iterable<User> findAll() {
    return match(
        entityManager()
            .createQuery("SELECT DISTINCT u FROM User u JOIN FETCH u.roles", User.class)
            .getResultList()
    );
}
```

This ensures that roles are loaded within the persistence context and are available even after the transaction ends.

## Implementation

### Key Classes

| Class | Package | Responsibility |
|-------|---------|----------------|
| `ListUsersController` | `eapli.aisafe.usermanagement.application` | Orchestrates retrieval of all users, enforces authorization. |
| `ListUsersUI` | `eapli.aisafe.app.backoffice.console.presentation.authz` | Console UI that fetches and displays users. |
| `UserRepository` (interface) | `eapli.aisafe.usermanagement.repositories` | Declares `findAll()` (already exists). |
| `JpaUserRepository` (implementation) | `eapli.aisafe.persistence.impl.jpa` | Overrides `findAll()` with JOIN FETCH. |
| `MainMenu` (modified) | `eapli.aisafe.app.backoffice.console.presentation` | Adds menu option for "List users". |

### Code Snippets

**ListUsersController.java**

```java
package eapli.aisafe.usermanagement.application;

import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;

public class ListUsersController {
    private final AuthService auth;
    private final UserRepository userRepo;

    public ListUsersController(AuthService auth, UserRepository userRepo) {
        this.auth = auth;
        this.userRepo = userRepo;
    }

    public Iterable<User> findAllUsers() {
        auth.ensureHasAnyRole(Role.ADMIN);
        return userRepo.findAll();
    }
}
```

**ListUsersUI.java**

```java
package eapli.aisafe.app.backoffice.console.presentation.authz;

import eapli.aisafe.app.common.console.AbstractUI;
import eapli.aisafe.usermanagement.application.ListUsersController;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;

public class ListUsersUI extends AbstractUI {

    private final ListUsersController controller;

    public ListUsersUI(ListUsersController controller) {
        this.controller = controller;
    }

    @Override
    protected boolean doShow() {
        Iterable<User> users = controller.findAllUsers();

        System.out.println("\n================================================================================");
        System.out.println("Backoffice Users");
        System.out.println("================================================================================");
        System.out.printf("%-30s %-20s %-15s %-20s %-8s%n",
                "Email", "Name", "Phone", "Roles", "Status");
        System.out.println("--------------------------------------------------------------------------------");

        int count = 0;
        for (User u : users) {
            String roles = u.roles().stream()
                    .map(Role::name)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            String status = u.isActive() ? "ACTIVE" : "DISABLED";
            System.out.printf("%-30s %-20s %-15s %-20s %-8s%n",
                    u.email().value(),
                    truncate(u.name(), 20),
                    u.phoneNumber(),
                    truncate(roles, 20),
                    status);
            count++;
        }
        System.out.println("--------------------------------------------------------------------------------");
        System.out.printf("Total users: %d%n%n", count);
        return true;
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return "";
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen - 3) + "...";
    }
}
```

**JpaUserRepository.java** (modified findAll)

```java
@Override
public Iterable<User> findAll() {
    return match(
        entityManager()
            .createQuery("SELECT DISTINCT u FROM User u JOIN FETCH u.roles", User.class)
            .getResultList()
    );
}
```

**MainMenu modifications**

Add the option after login (assuming menu options are numbered):

```java
// Inside showAuthenticatedMenu()
System.out.println("2. List users");
System.out.println("3. Register user");
System.out.println("4. Manage users (disable/enable)");
System.out.println("5. Logout");
System.out.println("0. Exit");
```

And in the switch/case:

```java
case "2":
    new ListUsersUI(listUsersController).show();
    break;
```

The controller must be instantiated in `AISafeBackoffice` and passed to `MainMenu` (similar to `ManageUserController`).

### Bootstrap Consideration

The bootstrap process (US030) already creates several master users. Those users will appear in the list automatically.

## Demonstration

### Steps to Run

Build and run the backoffice application:

```bash
mvn clean install
mvn -pl aisafe.app.backoffice.console exec:java
```

Login with an administrator account: `admin@aisafe.com / Admin123`.

From the main menu, choose `2. List users`.

The system displays the complete list of backoffice users, including their roles and status.

### Example Output

```text
================================================================================
Backoffice Users
================================================================================
Email                         Name                 Phone           Roles                 Status
--------------------------------------------------------------------------------
admin@aisafe.com              System Admin         +35191111111    ADMIN                 ACTIVE
backoffice@aisafe.com         Backoffice Ops       +35192222222    BACKOFFICE_OPERATOR   ACTIVE
weather@aisafe.com            Weather Person       +35193333333    WEATHER_PERSON        ACTIVE
atcc@airline.com              Airline ATCC         +35194444444    AIR_TRANSPORT_COMP... ACTIVE
pilot@aisafe.com              John Pilot           +35195555555    PILOT                 ACTIVE
fco@control.com               Flight Ctrl Op       +35196666666    FLIGHT_CONTROL_OPE... ACTIVE
newuser@example.com           Jane Cooper          +35197777777    COLLABORATOR          DISABLED
--------------------------------------------------------------------------------
Total users: 7
```

## Tests

### Unit Tests for Controller

Create `ListUsersControllerTest.java`:

```java
package eapli.aisafe.usermanagement.application;

import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.Role;
import eapli.aisafe.usermanagement.domain.User;
import eapli.aisafe.usermanagement.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ListUsersControllerTest {

    private UserRepository mockRepo;
    private AuthService authService;
    private ListUsersController controller;

    @BeforeEach
    void setUp() {
        mockRepo = mock(UserRepository.class);
        authService = mock(AuthService.class);
        controller = new ListUsersController(authService, mockRepo);
        when(authService.ensureHasAnyRole(Role.ADMIN)).thenReturn(null); // no exception for admin
    }

    @Test
    void findAllUsersShouldReturnAllUsers() {
        User user1 = new User(new Email("a@test.com"), "Pass123", "A", "123", Set.of(Role.ADMIN));
        User user2 = new User(new Email("b@test.com"), "Pass123", "B", "456", Set.of(Role.PILOT));
        user2.deactivate(); // disabled user
        when(mockRepo.findAll()).thenReturn(Set.of(user1, user2));

        Iterable<User> result = controller.findAllUsers();

        assertNotNull(result);
        assertEquals(2, ((Set<?>) result).size());
        verify(mockRepo).findAll();
    }

    @Test
    void findAllUsersWithoutAdminRoleThrows() {
        doThrow(new SecurityException("Not authorized"))
            .when(authService).ensureHasAnyRole(Role.ADMIN);
        assertThrows(SecurityException.class, () -> controller.findAllUsers());
        verify(mockRepo, never()).findAll();
    }
}
```

### Additional Tests (Optional)

- UI tests are not automated for console applications, but manual testing confirms the output format.
- Repository test (integration) can be written to verify that JOIN FETCH loads roles without throwing `LazyInitializationException`.

## Observations

### Current Limitations

- No filtering or search capabilities (e.g., by role, by name, by status). That could be added as an extension.
- No pagination – if the number of users grows large, the console output may become cluttered. Future sprints could add `--page` options.
- The phone number is printed as stored; no formatting validation is applied.

### Future Improvements

- Add an option to export the user list to a CSV file.
- Allow filtering by status (active/disabled) or by role.
- Show the date of last login (if that information is stored in the future).
- Integrate with US061–US064 to list only collaborators of a specific customer.

### Integration with Other User Stories

- **US032** (Disable/Enable Users) – The list shows the current status, so the Administrator can see the effect of disabling/enabling at a glance.
- **US061** (Add customer collaborator) – The list helps verify that a collaborator already exists before trying to add them again.
- **US064** (Disable customer collaborator) – The list confirms that the collaborator has been disabled.