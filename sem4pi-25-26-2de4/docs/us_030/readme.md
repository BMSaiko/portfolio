# US030 - Authentication and Authorization

## 1. Context

This user story belongs to Sprint 2 and is a mandatory requirement for the EAPLI course unit.

The goal is to ensure that users authenticate before accessing protected functionality and that each functionality is restricted according to the user's roles.

This implementation is also used by later user stories, namely customer and organization management, where Backoffice Operators register air transport companies and manage customer collaborators.

## 1.1 List of Issues

- **Analysis:** Understand the authentication and authorization requirements, existing user roles, and security constraints.
- **Design:** Define a custom architecture using Java 21, JPA/Hibernate, H2 database, PBKDF2 password hashing, and dependency injection.
- **Implement:** Implement login, logout, password hashing, role-based access control, and session management via `ThreadLocal`.
- **Test:** Develop tests to validate authentication, password verification, authorization checks, and user activation status.

## 2. Requirements

**US030** As a Project Manager, I want the system to support and apply authentication and authorization for all its users and functionalities.

### Acceptance Criteria

- **US030.1:** The system must require authentication for any access to its functionalities.
- **US030.2:** The system must support different user roles.
- **US030.3:** The system must enforce authorization rules so users can only access functionalities permitted by their role.
- **US030.4:** Passwords must be stored securely, hashed with PBKDF2 or an equivalent strong algorithm.
- **US030.5:** The system must provide a console-based login interface.
- **US030.6:** The system must provide a logout mechanism that invalidates the session.
- **US030.7:** Authentication and authorization must be enforced in the backoffice application.

### Dependencies / References

- This US is a prerequisite for most other user stories.
- NFR09 states that the system must support and apply authentication and authorization.
- Related US: US031, US060, US061, US062, US063, US064.

## 3. Analysis

### 3.1 Understanding

Authentication verifies the user's identity through email and password.

Authorization determines whether the authenticated user can execute a given functionality.

The system currently supports the following roles:

- `ADMIN`
- `BACKOFFICE_OPERATOR`
- `COLLABORATOR`
- `AIR_TRANSPORT_COMPANY_COLLABORATOR`
- `PILOT`
- `FLIGHT_CONTROL_OPERATOR`
- `WEATHER_PERSON`

A user can have multiple roles. This is useful because some users may perform more than one responsibility in the system.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| No external authentication framework | Keeps the solution simple and under full project control. |
| PBKDF2 password hashing | Secure password storage using standard Java APIs. |
| `ThreadLocal` session | Simple session management for the console application. |
| Constructor dependency injection | Improves testability and avoids hidden dependencies. |
| Role-based access control | Controllers explicitly check required roles. |
| Active/inactive user status | Disabled users cannot authenticate or be listed as active collaborators. |

## 4. Design

### 4.1 Realization

The system uses `AuthService` to authenticate users and store the current user in a `ThreadLocal`.

Controllers receive `AuthService` through constructor injection and call authorization methods before executing business logic.

Example:

    auth.ensureHasAnyRole(Role.ADMIN);
    auth.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);

### 4.2 Login Sequence
The full sequence diagram is in [`sequence_diagram.png`](sequence_diagram.png).
Key interactions:
- **User** enters email and password in `LoginUI`
- **LoginUI** calls `AuthService.login(email, password)`
- **AuthService** creates an `Email` value object, then calls `UserRepository.findActiveByEmail(email)`
- **JpaUserRepository** queries the H2 database for an active user with that email
- If found, **AuthService** calls `User.passwordMatches(password)`
- **User** delegates to `PasswordHasher.verifyPassword()` which uses PBKDF2 to compare hashes
- On success: **AuthService** stores the user in `ThreadLocal` and returns `Optional<User>`
- On failure: returns `Optional.empty()`

### 4.3 Authorization Flow

- `MainMenu` checks whether a user is authenticated.
- Unauthenticated users only see Login and Exit.
- Authenticated users see protected menu options.
- Controllers still enforce authorization rules, even if the UI shows the option.

This means authorization is not only visual; it is enforced in the application layer.

## 5. Implementation

### 5.1 Key Classes

#### aisafe.core

- `eapli.aisafe.usermanagement.domain.Email`
- `eapli.aisafe.usermanagement.domain.Role`
- `eapli.aisafe.usermanagement.domain.User`
- `eapli.aisafe.usermanagement.domain.PasswordHasher`
- `eapli.aisafe.usermanagement.application.AuthService`
- `eapli.aisafe.usermanagement.application.RegisterUserController`
- `eapli.aisafe.usermanagement.repositories.UserRepository`

#### aisafe.persistence.impl

- `eapli.aisafe.persistence.impl.jpa.JpaUserRepository`

#### aisafe.bootstrap

- `eapli.aisafe.bootstrap.MasterUsersBootstrapper`

#### aisafe.app.backoffice.console

- `eapli.aisafe.app.backoffice.console.AISafeBackoffice`
- `eapli.aisafe.app.backoffice.console.presentation.MainMenu`
- `eapli.aisafe.app.backoffice.console.presentation.authz.LoginUI`
- `eapli.aisafe.app.backoffice.console.presentation.authz.LogoutUI`
- `eapli.aisafe.app.backoffice.console.presentation.authz.RegisterUserUI`

### 5.2 Password Hashing

Passwords are hashed using PBKDF2 with HMAC SHA-256.

The stored password contains:

    iterations:salt:hash

This means the plain password is never stored.

### 5.3 Session Management

The current user is stored in:

    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();

Logout removes the current user from the session:

    currentUser.remove();

### 5.4 Integration with New User Stories

The authentication system is used by US060-US064.

Backoffice/customer management controllers require:

    Role.BACKOFFICE_OPERATOR

or:

    Role.ADMIN

Users with role `COLLABORATOR` can later be associated with a customer through US061.

Disabled users cannot login because authentication uses:

    findActiveByEmail(email)

## 6. Demonstration

Run from the project root:

    mvn clean install
    mvn -pl aisafe.app.backoffice.console exec:java

The application starts with:

    1. Login
    0. Exit

### Bootstrap Credentials

| Role | Email | Password |
|---|---|---|
| Admin | `admin@aisafe.com` | `Admin123` |
| Backoffice Operator | `backoffice@aisafe.com` | `Back123` |
| Weather Person | `weather@aisafe.com` | `Weather123` |
| ATCC | `atcc@airline.com` | `Atcc123` |
| Pilot | `pilot@aisafe.com` | `Pilot123` |
| FCO | `fco@control.com` | `Fco123` |

## 7. Tests

The following aspects are tested:

- Password hashing and verification.
- Invalid password rejection.
- Authentication with active users.
- Authorization checks in controllers.
- Logout clearing the current session.
- Disabled users not being able to login.

## 8. Observations

The implementation is suitable for the current console application.

Future improvements may include:

- Centralized session management for remote clients.
- Password reset.
- Password expiration policy.
- A more detailed permission model.
