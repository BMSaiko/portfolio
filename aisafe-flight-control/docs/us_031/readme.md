# US031 - Register Users

## 1. Context

This user story belongs to Sprint 2 and is a mandatory requirement for the EAPLI course unit.

The goal is to allow an Administrator to register new backoffice users. The user creation must also be possible through a bootstrap process so that the system starts with a set of predefined users (already covered in US030).

This US is directly based on US030 because it uses the same authentication and authorization mechanisms. Only authenticated users with role ADMIN are allowed to register new users.

### 1.1 List of Issues

- **Analysis:** Understand which information is required to create a user (email, password, name, phone, roles). Identify business rules (unique email, password strength, at least one role).
- **Design:** Define the controller class (RegisterUserController) and the UI (RegisterUserUI). Adopt a void return to avoid LazyInitializationException when persisting users.
- **Implement:** Write the registration logic, integrate with existing UserRepository and AuthService, and add the corresponding menu entry.
- **Test:** Develop tests that cover success scenarios (admin registers a user), failures (duplicate email, non‑admin user trying to register), and password policy enforcement.

## 2. Requirements

**US031** As an Administrator, I want to be able to register users of the backoffice.

### Acceptance Criteria

- **US031.1:** The system must allow an Administrator to register a new user providing email, password, full name, phone number and one or more roles.
- **US031.2:** The email must be unique in the system.
- **US031.3:** The password must meet the security policy (minimum 8 characters, at least one digit and one uppercase letter).
- **US031.4:** Newly registered users are created with active status.
- **US031.5:** The registration must enforce authorization: only users with role ADMIN can perform this operation.
- **US031.6:** The bootstrap process must also be capable of creating users – this has already been implemented in US030 (master users) and is reused.

### Dependencies / References

- **US030** (Authentication and Authorization) – needed to identify the current user and check the ADMIN role.
- **NFR09** – the system must support authentication and authorization for all functionalities.
- **NFR03** (test coverage) indirectly applies to the controller and domain classes.

## 3. Analysis

### 3.1 Understanding

A new user is represented by the aggregate User (already defined in the domain). The following information is mandatory:

- **Email** (must be unique and valid)
- **Password** (hashed with PBKDF2)
- **Full name**
- **Phone number**
- **At least one role**

Roles are selected from the existing Role enumeration:

- `ADMIN`
- `BACKOFFICE_OPERATOR`
- `COLLABORATOR`
- `AIR_TRANSPORT_COMPANY_COLLABORATOR`
- `PILOT`
- `FLIGHT_CONTROL_OPERATOR`
- `WEATHER_PERSON`

A user can have multiple roles (the business rule "at least one role" is enforced by the UI but can also be checked in the controller).

### 3.2 Business Rules

| Rule | Enforcement |
|---|---|
| Email must be unique | Checked in the controller before calling `userRepo.save()`. |
| Password strength (≥8 chars, digit, uppercase) | The `User` constructor (or `setPassword`) already validates against a simple rule; the UI must also display a hint. |
| Only ADMIN can register | `auth.ensureHasAnyRole(Role.ADMIN)` is called at the beginning of `registerUser()`. |
| Active user is created by default | The `User` constructor sets `active = true`. |

## 4. Design

### 4.1 Realization

The registration process follows the layered architecture:

1. The `RegisterUserUI` collects the user data.
2. It calls `RegisterUserController.registerUser(...)`.
3. The controller checks authorization (`ensureHasAnyRole`), validates email uniqueness, creates the `User` entity, and saves it.
4. The repository persists the user; the controller returns `void` to avoid accessing the persisted user outside the transaction (preventing `LazyInitializationException`).

### 4.2 Sequence Diagram

The full sequence diagram is in [`sequence_diagram.puml`](sequence_diagram.puml).

Key interactions:

- **Admin** navigates through `MainMenu` → `UsersMenuUI` → selects "Register user" → `RegisterUserUI`
- **RegisterUserUI** collects email, password, name, phone, and roles (with collaborator subtype selection)
- **RegisterUserController.registerUser()** is called, which:
  1. Calls `AuthService.ensureHasAnyRole(Role.ADMIN)` — throws `SecurityException` if not admin
  2. Creates `Email` value object and checks `UserRepository.existsByEmail()` — throws `IllegalArgumentException` if duplicate
  3. Creates `new User(email, password, name, phone, roles)` — password is hashed via `PasswordHasher`
  4. Calls `UserRepository.save(newUser)` — returns `void` to avoid `LazyInitializationException`
- **RegisterUserUI** shows "User registered successfully." or the error message

### 4.3 Important Design Choice

The controller returns `void` instead of the created `User` entity.

This avoids the `LazyInitializationException` that would occur if the UI (outside the transaction) tried to access the roles collection of the returned entity. Since the UI only needs to show a success message, no entity is needed – the controller only signals success or failure.

## 5. Implementation

### 5.1 Key Classes

| Class | Package | Responsibility |
|---|---|---|
| `RegisterUserController` | `eapli.aisafe.usermanagement.application` | Orchestrates the registration, checks authorization and business rules. |
| `RegisterUserUI` | `eapli.aisafe.app.backoffice.console.presentation.authz` | Console UI that collects user data and invokes the controller. |
| `User` (domain) | `eapli.aisafe.usermanagement.domain` | Encapsulates user data and password hashing. |
| `UserRepository` | `eapli.aisafe.usermanagement.repositories` | Provides `existsByEmail` and `save`. |

### 5.2 Code Snippets

**RegisterUserController** (simplified)

```java
public void registerUser(String emailStr, String password, String name,
                         String phone, Set<Role> roles) {
    auth.ensureHasAnyRole(Role.ADMIN);
    Email email = new Email(emailStr);
    if (userRepo.existsByEmail(email))
        throw new IllegalArgumentException("Email already registered");
    User newUser = new User(email, password, name, phone, roles);
    userRepo.save(newUser);
}
```

**RegisterUserUI – Role Selection Loop**

The UI presents a numbered list of roles and allows the user to pick multiple roles until they enter 0.

```java
Set<Role> roles = new HashSet<>();
Role[] all = Role.values();
while (true) {
    System.out.print("Role number (0 to finish): ");
    int opt = Integer.parseInt(sc.nextLine());
    if (opt == 0) break;
    if (opt >= 1 && opt <= all.length) {
        roles.add(all[opt - 1]);
    }
}
```

### 5.3 Bootstrap Integration

The bootstrap process (US030) already creates master users, including an admin. That admin can then be used to register additional users through this US.

## 6. Demonstration

### 6.1 Running the Application

From the project root:

```bash
mvn clean compile exec:java -pl aisafe.app.backoffice.console
```

### 6.2 Steps to Register a New User

1. Login with the admin account: `admin@aisafe.com` / `Admin123`
2. From the main menu choose **1. Register user**.
3. Fill in the required information:
   - Email (unique)
   - Password (must respect policy)
   - Full name
   - Phone number
4. Select one or more roles by typing the corresponding numbers (0 to finish).
5. Confirm – the system shows "User registered successfully."

### 6.3 Example Output

```
Email: john.doe@example.com
Password (min 8 chars, one digit, one uppercase): Secure123
Full name: John Doe
Phone number: +351912345678

Select roles (type the number, 0 to finish):
1. ADMIN
2. BACKOFFICE_OPERATOR
3. COLLABORATOR
...
0. Finish selection

Role number (0 to finish): 3
Added: COLLABORATOR
Role number (0 to finish): 0
Final roles: [COLLABORATOR]
User registered successfully.
```

## 7. Tests

The following test scenarios are covered in `RegisterUserControllerTest`:

| Test | Description |
|---|---|
| `registerUserShouldSucceedWhenAdmin` | Valid registration by an admin user succeeds. |
| `registerUserWithExistingEmailThrows` | Duplicate email causes `IllegalArgumentException`. |
| `registerUserWithoutAuthenticationThrows` | No logged‑in user triggers `SecurityException`. |
| `registerUserWithNonAdminRoleThrows` | A user with role `PILOT` (non‑admin) is not authorised and the call throws `SecurityException`. |

Additionally, domain tests ensure that password hashing and email validation work correctly.

## 8. Observations

### 8.1 Current Limitations

- There is no email confirmation step – the user becomes active immediately.
- Phone number format is not strictly validated (only non‑empty check).
- The administrator cannot edit or disable users – that is covered by US032 and US033 (optional in this sprint, but could be added later).

### 8.2 Future Improvements

- Introduce a more sophisticated password policy (configurable via a service).
- Add the possibility to define default role sets for different user types.
- Send a welcome email after registration.
- Allow mass import of users from a CSV file (future US).

### 8.3 Critical Perspective

The chosen design (controller returning `void`) is a direct consequence of the JPA `LazyInitializationException` that plagued early implementations. By keeping the return type `void`, the UI never accesses a detached entity, making the code simpler and more robust. This is a good example of adapting to implementation constraints while keeping the architecture clean.

The registration functionality is now ready and can be used as a foundation for more advanced user management features.