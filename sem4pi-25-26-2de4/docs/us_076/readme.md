# US076 - List pilot roster

## 1. Context

This user story belongs to the Air Companies Configuration area.

The goal is to allow an Air Transport Company Collaborator to view the list of all pilots currently assigned to their company.

This functionality must be available to the Air Transport Company Collaborator remotely, using the Air Transport Company App.

## 1.1 List of Issues

- **Analysis:** Understand the requirements for listing pilots, ensuring data is filtered so the collaborator only sees pilots from their own company.
- **Design:** Define the controller, repository queries, DTO mapping, UI/Client flow, and the TCP communication strategy for data retrieval.
- **Implement:** Implement the listing logic, fetching pilots associated with the authenticated user's company and sending them securely over TCP.
- **Test:** Validate that the roster only returns the correct pilots and that authorization constraints are strictly followed.

## 2. Requirements

**US076** As an Air Transport Company Collaborator, I want to list my company's pilot roster.

### Acceptance Criteria

- **US076.1:** An Air Transport Company Collaborator can request to see the list of pilots in their company.
- **US076.2:** The system must only return pilots that belong to the logged-in collaborator's Air Transport Company.
- **US076.3:** The returned list must include essential pilot details (e.g., Name, Email, Position, Status).
- **US076.4:** The access must be done remotely using a specific TCP-based network client application (Air Transport Company App), and direct interaction with the database is unacceptable.
- **US076.5:** Authentication and authorization must be enforced.

### Dependencies / References

- Depends on US030 for authentication and authorization.
- Depends on US075 (Add a pilot) to have data available to be listed.
- Related to the Air Transport Company App remote access requirements (US078).

## 3. Analysis

### 3.1 Understanding

Listing the pilot roster is a read-only operation. The critical business rule is data isolation: a collaborator from Company A must never be able to see the pilot roster of Company B.

To achieve this, the system will identify the company of the currently authenticated user and use it to filter the database query. Because this is a remote application, the list of domain entities cannot be sent directly over the network. The server must convert the `Pilot` entities into `PilotDTO` objects before transmitting them via TCP sockets to the client application.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| Filter by Authenticated User's Company | Ensures strict data privacy and isolates records between different air transport companies. |
| Use `PilotDTO` for data transfer | Prevents exposing domain entities directly over the network, reducing bandwidth and separating concerns. |
| Allow only `AIR_TRANSPORT_COMPANY_COLLABORATOR` | Ensures only the proper actor can view a company's roster. |
| TCP-based Client/Server architecture | Meets the strict non-functional requirement for remote access without direct DB interaction. |

## 4. Design

### 4.1 Realization

The listing operation is handled by `ListPilotRosterController` on the server side.

The UI/Client flow:
- The user selects the option to view the pilot roster.
- The client sends a formulated request (`LIST_PILOT_ROSTER`) over TCP.

The server controller checks authentication, retrieves the collaborator's company, queries the `PilotRepository` for all pilots linked to that company, converts the results into a list of DTOs, and returns them in a success response over TCP. The client then formats and displays this data to the user.

### 4.2 Main Flow

    Air Transport Company App -> ListPilotRosterUI: list roster
    ListPilotRosterUI -> TcpClient: sendRequest(LIST_PILOT_ROSTER)
    TcpClient -> TcpServer: [Network Transfer]
    TcpServer -> ListPilotRosterRequestHandler: handle(request)
    ListPilotRosterRequestHandler -> ListPilotRosterController: getCompanyRoster()
    ListPilotRosterController -> AuthService: ensureHasRole(AIR_TRANSPORT_COMPANY_COLLABORATOR)
    ListPilotRosterController -> AuthService: getAuthenticatedUserCompany()
    ListPilotRosterController -> PilotRepository: findByCompany(company)
    PilotRepository --> ListPilotRosterController: List<Pilot>
    ListPilotRosterController -> PilotMapper: toDTOList(pilots)
    ListPilotRosterController --> TcpServer: List<PilotDTO>
    TcpServer -> TcpClient: [Network Transfer]
    TcpClient --> ListPilotRosterUI: List<PilotDTO>
    ListPilotRosterUI --> Air Transport Company Collaborator: displays formatted list

### 4.3 Alternative Flows

Empty Roster:

    Server Controller queries the repository and finds no pilots for the company.
    Server returns an empty list (or a specific code) via TCP.
    User receives a message stating "The pilot roster is currently empty."

Unauthenticated or Unauthorized user:

    AuthService throws SecurityException.
    Operation is rejected.
    Server returns an authorization error response.

## 5. Implementation

### 5.1 Key Classes

#### aisafe.core

- `eapli.aisafe.pilotmanagement.domain.Pilot`
- `eapli.aisafe.pilotmanagement.dto.PilotDTO`
- `eapli.aisafe.pilotmanagement.dto.PilotMapper`
- `eapli.aisafe.pilotmanagement.repositories.PilotRepository`
- `eapli.aisafe.pilotmanagement.application.ListPilotRosterController`

#### aisafe.persistence.impl

- `eapli.aisafe.persistence.impl.jpa.JpaPilotRepository`

#### aisafe.app.company.console (TCP Client)

- `eapli.aisafe.app.company.console.presentation.pilotmanagement.ListPilotRosterUI`
- `eapli.aisafe.app.company.console.connection.TcpClient`

#### aisafe.app.server (TCP Server)

- `eapli.aisafe.app.server.connection.TcpServer`
- `eapli.aisafe.app.server.handlers.ListPilotRosterRequestHandler`

### 5.2 Domain Rules

The Controller enforces:

    AirTransportCompany company = authService.getAuthenticatedUserCompany();
    return pilotRepository.findByCompany(company);

### 5.3 Repository Operations

The repository supports:

    PilotRepository.findByCompany(AirTransportCompany company)

### 5.4 Persistence

Data is read from the following table:

    T_PILOT

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
    2. List pilot roster

Expected result (Success):

    ======================================================
                     PILOT ROSTER
    ======================================================
    NAME            | EMAIL              | POSITION | STATUS
    ------------------------------------------------------
    John Doe        | j.doe@tap.pt       | Captain  | ACTIVE
    Jane Smith      | j.smith@tap.pt     | Co-Pilot | ACTIVE
    ======================================================

Expected result (Empty):

    There are currently no pilots registered in your company's roster.

## 7. Tests

The following aspects are tested:

- successful retrieval of pilots belonging to the authenticated user's company;
- ensuring pilots from other companies are NOT returned in the list;
- successful handling of an empty roster;
- unauthenticated users cannot access the listing function;
- only authorized roles (ATCC) can list the roster;
- client application successfully communicates via TCP and correctly deserializes the DTO list.

## 8. Observations

The current implementation fully satisfies US076.

The listing relies securely on the authenticated user's session to filter the data, ensuring multi-tenant data isolation. The TCP client-server architecture fulfills the remote access requirement (US078).