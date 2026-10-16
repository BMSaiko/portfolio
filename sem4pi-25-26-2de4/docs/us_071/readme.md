# US071 - Decommission an aircraft

## 1. Context

This user story belongs to the fleet management and air transport company configuration area.

The goal is to allow an Air Transport Company Collaborator to retire (decommission) an aircraft from their company's fleet.

This functionality must be available to the Air Transport Company Collaborator remotely, using the Air Transport Company App.

## 1.1 List of Issues

- **Analysis:** Understand the conditions required to decommission an aircraft, specifically the business rule that prevents decommissioning if there are pending flights.
- **Design:** Define the domain entity updates (changing operational status), repository queries (checking for pending flights), controller, TCP communication flow, and UI.
- **Implement:** Implement the decommissioning logic, the status state transition, and the validation checks across different aggregates (Fleet and Flights).
- **Test:** Validate the state transition, the rejection of the operation when pending flights exist, and authorization requirements.

## 2. Requirements

**US071** As an Air Transport Company Collaborator, I want to retire an aircraft from my company's fleet.

It's not possible to create flights for a retired aircraft, nor it is possible to retire an aircraft that has pending flights registered in the system.

### Acceptance Criteria

- **US071.1:** An Air Transport Company Collaborator can change the status of an active aircraft to retired/decommissioned.
- **US071.2:** The system must prevent the retirement of an aircraft if it has pending/future flights registered in the system.
- **US071.3:** Once retired, the aircraft must no longer be available for the creation of new flight plans.
- **US071.4:** The access must be done remotely using a specific TCP-based network client application (Air Transport Company App), without direct interaction with the database.
- **US071.5:** The operation must be authenticated and authorized appropriately.
- **US071.6:** The user can only decommission aircraft belonging to their own company.

### Dependencies / References

- Depends on US030 for authentication and authorization.
- Depends on US070 (Add an aircraft) to have an existing fleet.
- Interacts with US080 (Create a flight plan), as the system must verify flight plans associated with the aircraft.
- Related to the Air Transport Company App remote access requirements (US078).

## 3. Analysis

### 3.1 Understanding

Decommissioning an aircraft means changing its OperationalStatus (e.g., from ACTIVE to RETIRED).

Before the status can be changed, the system must enforce a strict business rule: **an aircraft cannot be decommissioned if it is scheduled for future flights**. This requires querying the flight management data to check for any flight plans associated with the aircraft's registration number that have a status of "pending" or "scheduled" and are dated in the future.

Because this is a remote application, data must be transferred via TCP sockets, and the server will perform the validation and database updates.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| Update OperationalStatus rather than deleting | Deleting the aircraft would break historical records (past flights, logs). |
| Query FlightPlanRepository | Required to check if the aircraft is assigned to any pending flights. |
| Restrict to AIR_TRANSPORT_COMPANY_COLLABORATOR | Ensures only authorized fleet managers can retire assets. |
| Enforce Company Ownership | The logged-in user must only be able to retire aircraft belonging to their specific airline. |
| Use DTOs for TCP communication | Prevents exposing domain entities directly over the network. |

## 4. Design

### 4.1 Realization

The decommissioning process is handled by DecommissionAircraftController on the server side.

The UI/Client:
- Requests the list of currently active aircraft for the collaborator's company.
- Allows the user to select an aircraft by its registration number.
- Sends the decommission request to the server.

The Server:
- Receives the request over TCP.
- Checks authentication and role.
- Verifies that the aircraft belongs to the collaborator's company.
- Queries the flight repository to check for pending flights involving this aircraft.
- If no pending flights exist, updates the aircraft's operational status to RETIRED.
- Persists the change and returns a success response.

### 4.2 Main Flow

    Air Transport Company App -> FleetMenuUI: decommission aircraft
    FleetMenuUI -> TcpClient: sendRequest(LIST_ACTIVE_AIRCRAFT)
    TcpServer -> ListAircraftController: getActiveFleet()
    TcpServer --> FleetMenuUI: List of Aircraft DTOs
    FleetMenuUI -> Air Transport Company Collaborator: asks to select aircraft
    Air Transport Company Collaborator -> FleetMenuUI: selects aircraft (e.g., CS-TPA)
    FleetMenuUI -> TcpClient: sendRequest(DECOMMISSION_AIRCRAFT, registrationDTO)
    TcpClient -> TcpServer: [Network Transfer]
    TcpServer -> DecommissionAircraftController: decommissionAircraft(registrationDTO)
    DecommissionAircraftController -> AuthService: ensureHasRole(AIR_TRANSPORT_COMPANY_COLLABORATOR)
    DecommissionAircraftController -> AircraftRepository: findByRegistration(registration)
    DecommissionAircraftController -> FlightPlanRepository: hasPendingFlights(registration)
    DecommissionAircraftController -> Aircraft: decommission()
    DecommissionAircraftController -> AircraftRepository: save(aircraft)
    AircraftRepository --> DecommissionAircraftController: saved aircraft
    DecommissionAircraftController --> TcpServer: success response
    TcpServer -> TcpClient: [Network Transfer]
    TcpClient --> FleetMenuUI: success
    FleetMenuUI --> Air Transport Company Collaborator: success message

### 4.3 Alternative Flows

Aircraft has pending flights:
Server Controller queries the FlightPlanRepository and finds pending flights.
Operation is rejected with an IllegalStateException.
Server returns an error response via TCP.
User receives an error message stating the aircraft is still scheduled for flights.

Aircraft already retired:
The domain entity throws an exception when calling decommission() if it is already retired.
Operation is rejected.
Server returns an error response.

Aircraft belongs to another company:
Server Controller checks ownership against the authenticated user's company.
Operation is rejected due to lack of ownership.
Server returns an authorization error response.

## 5. Implementation

### 5.1 Key Classes

#### aisafe.core

- eapli.aisafe.fleet.domain.Aircraft
- eapli.aisafe.fleet.domain.OperationalStatus
- eapli.aisafe.fleet.repositories.AircraftRepository
- eapli.aisafe.flight.repositories.FlightPlanRepository
- eapli.aisafe.fleet.application.DecommissionAircraftController

#### aisafe.persistence.impl

- eapli.aisafe.persistence.impl.jpa.JpaAircraftRepository
- eapli.aisafe.persistence.impl.jpa.JpaFlightPlanRepository

#### aisafe.app.company.console (TCP Client)

- eapli.aisafe.app.company.console.presentation.fleet.DecommissionAircraftUI
- eapli.aisafe.app.company.console.connection.TcpClient

#### aisafe.app.server (TCP Server)

- eapli.aisafe.app.server.handlers.DecommissionAircraftRequestHandler

### 5.2 Domain Rules

The Aircraft entity implements the state transition:

    public void decommission() {
        if (this.status == OperationalStatus.RETIRED) {
            throw new IllegalStateException("Aircraft is already retired.");
        }
        this.status = OperationalStatus.RETIRED;
    }

The Controller ensures the business rule:

    if (flightPlanRepository.hasPendingFlights(aircraft.identity())) {
        throw new IllegalStateException("Cannot decommission an aircraft with pending flights.");
    }

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
2. Fleet management
2. Decommission aircraft

Example input/output:

    --- Active Fleet ---
    1. CS-TPA (Airbus A320)
    2. CS-TXB (Airbus A321neo)
    
    Select the aircraft to decommission: 1
    
    [System checks for pending flights...]
    
    Expected result:
    Aircraft CS-TPA has been successfully decommissioned.

## 7. Tests

The following aspects are tested:

- successfully changing the status of an active aircraft with no flights to RETIRED;
- verifying that attempting to decommission an aircraft with pending flights throws an exception;
- verifying that attempting to decommission an already retired aircraft throws an exception;
- verifying that a user cannot decommission an aircraft belonging to a different air transport company;
- unauthenticated users cannot access the feature;
- only authorized roles (Air Transport Company Collaborator) can trigger this action;
- ensuring the TCP client properly handles and displays error messages returned by the server.

## 8. Observations

The current implementation fully satisfies US071.

This feature securely prevents the creation of orphaned or invalid flight plans by strictly checking the FlightPlanRepository before proceeding. Furthermore, future US implementations involving flight plan creation (US080) must be designed to filter out RETIRED aircraft from the available aircraft selection list.