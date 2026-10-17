# US075 - Add a pilot

## 1. Context

This user story belongs to the Air Companies Configuration area.

The goal is to allow an Air Transport Company Collaborator to add a pilot to their company's roster.

This functionality must be available to the Air Transport Company Collaborator remotely, using the Air Transport Company App.

## 1.1 List of Issues

- **Analysis:** Understand the attributes that define a pilot, their dual role as a company employee and a system user, and their aircraft certifications.
- **Design:** Define the domain entity (Pilot/System User), repository, controller, persistence implementation, UI/Client flow, and the TCP communication strategy.
- **Implement:** Implement pilot registration with validation for valid company email, name, position, and model certifications.
- **Test:** Validate domain rules (e.g., valid company email domain), minimum certification requirements, and authorization constraints.

## 2. Requirements

**US075** As an Air Transport Company Collaborator, I want to add a pilot to my company.

A pilot is a system's user. A pilot is certified to pilot one or more aircraft models. An Air Transport Company may have several collaborators (ATCC and pilots) interacting with the system; thus, a list of these collaborators must be stored for each company. Email (valid company email), name and position will probably be enough for the purpose of the system.

### Acceptance Criteria

- **US075.1:** An Air Transport Company Collaborator can add a pilot to their company.
- **US075.2:** The pilot must be registered as a user of the system.
- **US075.3:** The pilot must be certified for at least one or more aircraft models.
- **US075.4:** The pilot's profile must include a valid company email, name, and position.
- **US075.5:** The access must be done remotely using a specific TCP-based network client application (Air Transport Company App), and direct interaction with the database is unacceptable.
- **US075.6:** Authentication and authorization must be enforced.

### Dependencies / References

- Depends on US030 for authentication and authorization.
- Depends on US055 (Create an aircraft model) so that pilots can be certified to fly existing models.
- Used by US076 (List pilot roster) and US077 (Remove a pilot).
- Related to the Air Transport Company App remote access requirements (US078).

## 3. Analysis

### 3.1 Understanding

A pilot represents both an employee of an Air Transport Company and a user capable of logging into the system.

The domain model identifies a pilot with:

- System User credentials (for authentication).
- Email (must belong to the company's domain).
- Name.
- Position.
- A list of certified Aircraft Models.
- The associated Air Transport Company.

The system must ensure the email domain matches the company or is otherwise validated as a legitimate company email. Because this is a remote application, data must be transferred via TCP sockets using DTOs, and the server will perform the actual database and user management operations.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| Create `Pilot` as a domain entity linked to `User` | A pilot needs system access but also holds domain-specific attributes like certifications and company link. |
| Use Email as Business ID | Emails are universally unique and serve as the user's login identifier. |
| Link Pilot to Air Transport Company | Required to build the company's roster and isolate data access. |
| Enforce at least one certification on creation | The business rules state a pilot is certified to pilot "one or more" models. |
| Allow only `AIR_TRANSPORT_COMPANY_COLLABORATOR` | Ensures only the proper actor can alter a company's roster. |
| Use DTOs for TCP communication | Prevents exposing domain entities directly over the network and separates concerns. |

## 4. Design

### 4.1 Realization

The registration is handled by `AddPilotController` on the server side.

The UI/Client collects:
- Name;
- Company Email;
- Position;
- List of Aircraft Model IDs for certification;
- Password (or auto-generates one for the system user).

The client sends a formulated request over TCP. The server controller checks authentication, identifies the collaborator's company, validates the email uniqueness and format, fetches the selected aircraft models, creates the system user via an authorization service, creates the pilot domain object, persists it, and returns a success response over TCP.

### 4.2 Main Flow

    Air Transport Company App -> PilotRosterUI: add pilot
    PilotRosterUI -> TcpClient: sendRequest(ADD_PILOT, dataDTO)
    TcpClient -> TcpServer: [Network Transfer]
    TcpServer -> AddPilotController: addPilot(dataDTO)
    AddPilotController -> AuthService: ensureHasRole(AIR_TRANSPORT_COMPANY_COLLABORATOR)
    AddPilotController -> AuthService: getAuthenticatedUserCompany()
    AddPilotController -> PilotRepository: existsByEmail(email)
    AddPilotController -> AircraftModelRepository: findByIds(modelIds)
    AddPilotController -> UserManagementService: registerUser(email, password, role=PILOT)
    AddPilotController -> Pilot: new Pilot(systemUser, name, position, company, certifications)
    Pilot -> Pilot: validateEmailDomain(company.getDomain())
    Pilot -> Pilot: validateCertifications(certifications)
    AddPilotController -> PilotRepository: save(pilot)
    PilotRepository --> AddPilotController: saved pilot
    AddPilotController --> TcpServer: success response
    TcpServer -> TcpClient: [Network Transfer]
    TcpClient --> PilotRosterUI: success
    PilotRosterUI --> Air Transport Company Collaborator: success message

### 4.3 Alternative Flows

Email already exists / User already registered:

    Server Controller detects existing email.
    Operation is rejected.
    Server returns an error response via TCP.
    User receives an error message.

No certifications provided:

    Domain constructor validates the certifications list is not empty.
    Operation is rejected with an illegal argument exception.
    Server returns an error response via TCP.
    User receives an error message.

Unauthenticated or Unauthorized user:

    AuthService throws SecurityException.
    Operation is rejected.
    Server returns an authorization error response.

## 5. Implementation

### 5.1 Key Classes

#### aisafe.core

- `eapli.aisafe.pilotmanagement.domain.Pilot`
- `eapli.aisafe.pilotmanagement.domain.Certification`
- `eapli.aisafe.pilotmanagement.repositories.PilotRepository`
- `eapli.aisafe.pilotmanagement.application.AddPilotController`

#### aisafe.persistence.impl

- `eapli.aisafe.persistence.impl.jpa.JpaPilotRepository`

#### aisafe.app.company.console (TCP Client)

- `eapli.aisafe.app.company.console.presentation.pilotmanagement.AddPilotUI`
- `eapli.aisafe.app.company.console.connection.TcpClient`

#### aisafe.app.server (TCP Server)

- `eapli.aisafe.app.server.connection.TcpServer`
- `eapli.aisafe.app.server.handlers.AddPilotRequestHandler`

### 5.2 Domain Rules

The `Pilot` constructor validates:

    email cannot be null or blank and must be a valid format
    name cannot be null or blank
    certifications.size() >= 1
    if (certifications.isEmpty()) throw IllegalArgumentException

### 5.3 Repository Operations

The repository supports:

    save(pilot)
    findByEmail(email)
    findByCompany(company)
    existsByEmail(email)

### 5.4 Persistence

The entity is persisted in tables:

    T_PILOT
    T_PILOT_CERTIFICATIONS

Important constraints:

    EMAIL unique
    SYSTEM_USER_ID unique foreign key

### 5.5 Bootstrap

The bootstrap process registers initial pilots and assigns them to pre-existing companies and aircraft models. This ensures the roster lists and flight planning components have testable data out-of-the-box.

## 6. Demonstration

Run the server application:

    mvn clean install
    mvn -pl aisafe.app.server exec:java

Run the Company App Client in a separate terminal:

    mvn -pl aisafe.app.company.console exec:java

Login with:

    collaborator@tap.pt
    Collab123

Menu path:

    1. Login
    3. Pilot Roster Management
    1. Add a pilot

Example input:

    Name: John Doe
    Email: j.doe@tap.pt
    Position: Senior Captain
    Select Aircraft Models for Certification (comma separated IDs): 1, 3

Expected result:

    Pilot John Doe (j.doe@tap.pt) registered successfully and added to the roster.

## 7. Tests

The following aspects are tested:

- valid pilot creation with system user integration;
- email must not be blank and must follow valid format;
- pilot with zero certifications is rejected;
- duplicate email address is rejected;
- pilot must be linked to the logged-in collaborator's company;
- unauthenticated users cannot add a pilot;
- only authorized roles (ATCC) can add a pilot;
- client application successfully communicates via TCP and does not directly access the database.

## 8. Observations

The current implementation fully satisfies US075.

The pilot is successfully integrated with the roster listing (US076) and user management. The use of the TCP client-server architecture fulfills the remote access requirement (US078).

Future improvements may include:
- Linking pilots to specific flight plans during flight creation (US080).
- Implementing UI for deactivating a pilot (US077).