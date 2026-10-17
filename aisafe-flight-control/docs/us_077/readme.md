# US077 - Remove a pilot

## 1. Context

This user story belongs to the Air Companies Configuration area.

The goal is to allow an Air Transport Company Collaborator to make a pilot inactive (remove them) from their company's roster.

This functionality must be available to the Air Transport Company Collaborator remotely, using the Air Transport Company App.

## 1.1 List of Issues

- **Analysis:** Understand the rules for deactivating a pilot, specifically the dependency check to ensure the pilot has no assigned flight plans.
- **Design:** Define the controller, repository interactions (both Pilot and Flight Plan repositories), UI/Client flow, and the TCP communication strategy.
- **Implement:** Implement pilot deactivation logic, including the validation rule that prevents deactivation if flight plans are assigned.
- **Test:** Validate domain rules (e.g., successful deactivation, rejection when flight plans exist) and authorization constraints.

## 2. Requirements

**US077** As an Air Transport Company Collaborator, I want to make a pilot inactive in my company's roster. Cannot deactivate a pilot with flight plans assigned.

### Acceptance Criteria

- **US077.1:** An Air Transport Company Collaborator can make a pilot inactive in their company.
- **US077.2:** The system must check if the pilot has any assigned flight plans before deactivation.
- **US077.3:** If the pilot has flight plans assigned, the deactivation must be rejected with an appropriate error message.
- **US077.4:** The access must be done remotely using a specific TCP-based network client application (Air Transport Company App), and direct interaction with the database is unacceptable.
- **US077.5:** Authentication and authorization must be enforced.

### Dependencies / References

- Depends on US030 for authentication and authorization.
- Depends on US075 (Add a pilot) and US076 (List pilot roster) to have existing pilots to remove.
- Depends on US080 (Create a flight plan) as the system needs to verify if the pilot is linked to any flight plans.
- Related to the Air Transport Company App remote access requirements (US078).

## 3. Analysis

### 3.1 Understanding

Removing a pilot in this context does not mean hard-deleting the record from the database, but rather changing their operational status to "inactive". This ensures historical data integrity.

Before a pilot can be deactivated, the system must query the flight plan data to ensure the pilot is not assigned to any existing flight plans (past, present, or future). If a dependency is found, the action is blocked. Because this is a remote application, data must be transferred via TCP sockets using DTOs, and the server will perform the actual database and business logic operations.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| Soft Delete (Deactivation) | Preserves historical data. Hard deletion would violate foreign key constraints if the pilot is referenced elsewhere in the future. |
| Check dependencies via `FlightPlanRepository` | The rule strictly forbids deactivating pilots with assigned flight plans. The database query is the most efficient way to verify this. |
| Allow only `AIR_TRANSPORT_COMPANY_COLLABORATOR` | Ensures only the proper actor can alter a company's roster. |
| Use DTOs for TCP communication | Prevents exposing domain entities directly over the network and separates concerns. |

## 4. Design

### 4.1 Realization

The deactivation is handled by `RemovePilotController` on the server side.

The UI/Client flow:
- Requests the list of active pilots for the logged-in collaborator's company.
- The user selects a pilot from the list (via ID or Email).
- The client sends a formulated request (`REMOVE_PILOT`) over TCP.

The server controller checks authentication, verifies the pilot belongs to the collaborator's company, queries the `FlightPlanRepository` to check for assigned flight plans, updates the pilot's status to inactive, persists the change via `PilotRepository`, and returns a success response over TCP.

### 4.2 Main Flow

    Air Transport Company App -> RemovePilotUI: remove pilot
    RemovePilotUI -> TcpClient: sendRequest(LIST_ACTIVE_PILOTS)
    TcpClient --> RemovePilotUI: List<PilotDTO>
    RemovePilotUI -> User: shows list
    User -> RemovePilotUI: selects pilot (email)
    RemovePilotUI -> TcpClient: sendRequest(REMOVE_PILOT, email)
    TcpClient -> TcpServer: [Network Transfer]
    TcpServer -> RemovePilotRequestHandler: handle(request)
    RemovePilotRequestHandler -> RemovePilotController: deactivatePilot(email)
    RemovePilotController -> AuthService: ensureHasRole(AIR_TRANSPORT_COMPANY_COLLABORATOR)
    RemovePilotController -> AuthService: getAuthenticatedUserCompany()
    RemovePilotController -> PilotRepository: findByEmailAndCompany(email, company)
    RemovePilotController -> FlightPlanRepository: existsByPilot(pilot)
    RemovePilotController -> Pilot: deactivate()
    RemovePilotController -> PilotRepository: save(pilot)
    PilotRepository --> RemovePilotController: saved pilot
    RemovePilotController --> TcpServer: success response
    TcpServer -> TcpClient: [Network Transfer]
    TcpClient --> RemovePilotUI: success
    RemovePilotUI --> Air Transport Company Collaborator: success message

### 4.3 Alternative Flows

Pilot has assigned flight plans:

    Server Controller detects that `FlightPlanRepository.existsByPilot(pilot)` is true.
    Operation is rejected with an IllegalStateException.
    Server returns an error response via TCP.
    User receives an error message explaining the dependency.

Pilot not found or does not belong to company:

    Server Controller cannot find the pilot for the authenticated user's company.
    Operation is rejected.
    Server returns a not-found/unauthorized error response via TCP.

Unauthenticated or Unauthorized user:

    AuthService throws SecurityException.
    Operation is rejected.
    Server returns an authorization error response.

## 5. Implementation

### 5.1 Key Classes

#### aisafe.core

- `eapli.aisafe.pilotmanagement.domain.Pilot`
- `eapli.aisafe.pilotmanagement.domain.PilotStatus` (Enum: ACTIVE, INACTIVE)
- `eapli.aisafe.pilotmanagement.repositories.PilotRepository`
- `eapli.aisafe.pilotmanagement.application.RemovePilotController`
- `eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository`

#### aisafe.persistence.impl

- `eapli.aisafe.persistence.impl.jpa.JpaPilotRepository`
- `eapli.aisafe.persistence.impl.jpa.JpaFlightPlanRepository`

#### aisafe.app.company.console (TCP Client)

- `eapli.aisafe.app.company.console.presentation.pilotmanagement.RemovePilotUI`
- `eapli.aisafe.app.company.console.connection.TcpClient`

#### aisafe.app.server (TCP Server)

- `eapli.aisafe.app.server.connection.TcpServer`
- `eapli.aisafe.app.server.handlers.RemovePilotRequestHandler`

### 5.2 Domain Rules

The `Pilot` entity includes:

    public void deactivate() {
        // optionally check current state
        this.status = PilotStatus.INACTIVE;
    }

The Controller enforces:

    if (flightPlanRepository.existsByPilot(pilot)) {
        throw new IllegalStateException("Cannot deactivate a pilot with assigned flight plans.");
    }

### 5.3 Repository Operations

The repositories support:

    PilotRepository.findByEmailAndCompany(Email email, AirTransportCompany company)
    FlightPlanRepository.existsByPilot(Pilot pilot)

### 5.4 Persistence

The entity is updated in table:

    T_PILOT

Important fields updated:

    STATUS (changed to INACTIVE)

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
    3. Remove a pilot

Example interaction:

    Select a pilot to deactivate:
    1. John Doe (j.doe@tap.pt)
    2. Jane Smith (j.smith@tap.pt)
    > 1

Expected result (Success):

    Pilot John Doe (j.doe@tap.pt) has been successfully deactivated.

Expected result (Failure - Flight plans assigned):

    Error: Cannot deactivate pilot. John Doe is assigned to existing flight plans.

## 7. Tests

The following aspects are tested:

- successful pilot deactivation when no flight plans are assigned;
- exception is thrown when attempting to deactivate a pilot with assigned flight plans;
- pilot must belong to the logged-in collaborator's company to be deactivated;
- unauthenticated users cannot access the deactivation function;
- only authorized roles (ATCC) can deactivate a pilot;
- client application successfully communicates via TCP and receives correct success/error feedback.

## 8. Observations

The current implementation fully satisfies US077.

The operation correctly relies on soft-deletion (status change) to maintain database integrity and verifies the required business rule regarding flight plans. The TCP client-server architecture fulfills the remote access requirement (US078).