# US070 - Add an aircraft to an air transport company

## 1. Context

This user story belongs to the fleet management and air transport company configuration area.

The goal is to allow an Air Transport Company Collaborator to add an aircraft to their company's fleet.

This functionality must be available to the Air Transport Company Collaborator remotely, using the Air Transport Company App.

## 1.1 List of Issues

- **Analysis:** Understand the attributes that define an aircraft, its relationship with the aircraft model, and the capacity rules.
- **Design:** Define the domain entity, repository, controller, persistence implementation, UI/Client flow, and bootstrap process.
- **Implement:** Implement aircraft registration with validation of registration numbers, seat capacities, and association to the logged-in collaborator's company.
- **Test:** Validate domain rules (e.g., total seats versus capacity), unique registration enforcement, and authorization requirements.

## 2. Requirements

**US070** As an Air Transport Company Collaborator, I want to add an aircraft to my company's fleet.

The aircraft is of a given model, including the engines, and the number of seats of each type/class must be provided. The total number of seats cannot exceed the model's capacity. An aircraft is identified by the aircraft registration number, which is unique. Also, an aircraft is registered in a country (it may not be the company's home country). An aircraft also has an operational status.

### Acceptance Criteria

- **US070.1:** An Air Transport Company Collaborator can add an aircraft to their fleet.
- **US070.2:** The aircraft must be associated with a specific aircraft model.
- **US070.3:** The user must provide the number of seats for each type/class.
- **US070.4:** The total number of seats across all classes cannot exceed the associated model's capacity.
- **US070.5:** The aircraft registration number must be unique in the system.
- **US070.6:** The aircraft must include a country of registration.
- **US070.7:** The aircraft must have an assigned operational status.
- **US070.8:** The access must be done remotely using a specific TCP-based network client application (Air Transport Company App), without direct interaction with the database.
- **US070.9:** The operation must be authenticated and authorized appropriately.

### Dependencies / References

- Depends on US030 for authentication and authorization.
- Depends on US055 (Create an aircraft model) and US057 (Add an engine model to an aircraft model) to have models available.
- Used by US071 (Decommission an aircraft) and US072 (List company fleet).
- Related to the Air Transport Company App remote access requirements (US078).

## 3. Analysis

### 3.1 Understanding

An aircraft represents a specific physical unit of a generalized aircraft model.

The domain model identifies an aircraft with:

- Registration (unique ID).
- Company (owner/operator).
- Cabin configuration (number of seats in each class).
- Operational status.
- Registration Country.

The registration number is the global unique identifier for the aircraft. The system must ensure that the sum of all seats configured for the aircraft's cabin does not surpass the maximum passenger capacity defined in its Aircraft Model. Because this is a remote application, data must be transferred via TCP sockets using DTOs, and the server will perform the actual database operations.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| Create `Aircraft` as a domain entity | It represents a specific, uniquely identifiable asset in the domain. |
| Use Registration Number as Business ID | The requirement explicitly states the registration number is unique and identifies the aircraft. |
| Link Aircraft to Air Transport Company | Required to build the company's fleet and ensure isolated data access. |
| Calculate total seats on creation | To immediately enforce the rule that total seats cannot exceed the model's capacity. |
| Allow only `AIR_TRANSPORT_COMPANY_COLLABORATOR` | Ensures only the proper actor can alter a company's fleet. |
| Use DTOs for TCP communication | Prevents exposing domain entities directly over the network and separates concerns. |

## 4. Design

### 4.1 Realization

The registration is handled by `AddAircraftController` on the server side.

The UI/Client collects:
- aircraft model ID;
- registration number;
- registration country;
- seat configuration (e.g., First Class, Business, Economy);
- operational status.

The client sends a formulated request over TCP. The server controller checks authentication, identifies the collaborator's company, validates uniqueness of the registration number through the repository, fetches the model to validate seat capacity, creates the domain object, persists it, and returns a success response over TCP.

### 4.2 Main Flow

    Air Transport Company App -> FleetMenuUI: add aircraft
    FleetMenuUI -> TcpClient: sendRequest(ADD_AIRCRAFT, dataDTO)
    TcpClient -> TcpServer: [Network Transfer]
    TcpServer -> AddAircraftController: addAircraft(dataDTO)
    AddAircraftController -> AuthService: ensureHasRole(AIR_TRANSPORT_COMPANY_COLLABORATOR)
    AddAircraftController -> AuthService: getAuthenticatedUserCompany()
    AddAircraftController -> AircraftRepository: existsByRegistration(registration)
    AddAircraftController -> AircraftModelRepository: findById(modelId)
    AddAircraftController -> Aircraft: new Aircraft(registration, model, company, country, seatConfig, status)
    Aircraft -> Aircraft: validateSeatCapacity(model.getCapacity(), seatConfig)
    AddAircraftController -> AircraftRepository: save(aircraft)
    AircraftRepository --> AddAircraftController: saved aircraft
    AddAircraftController --> TcpServer: success response
    TcpServer -> TcpClient: [Network Transfer]
    TcpClient --> FleetMenuUI: success
    FleetMenuUI --> Air Transport Company Collaborator: success message

### 4.3 Alternative Flows

Registration already exists:

    Server Controller detects existing registration number.
    Operation is rejected.
    Server returns an error response via TCP.
    User receives an error message.

Seat capacity exceeded:

    Domain constructor validates the total sum of seats against the model's capacity.
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

- `eapli.aisafe.fleet.domain.Aircraft`
- `eapli.aisafe.fleet.domain.CabinConfiguration`
- `eapli.aisafe.fleet.repositories.AircraftRepository`
- `eapli.aisafe.fleet.application.AddAircraftController`

#### aisafe.persistence.impl

- `eapli.aisafe.persistence.impl.jpa.JpaAircraftRepository`

#### aisafe.app.company.console (TCP Client)

- `eapli.aisafe.app.company.console.presentation.fleet.AddAircraftUI`
- `eapli.aisafe.app.company.console.connection.TcpClient`

#### aisafe.app.server (TCP Server)

- `eapli.aisafe.app.server.connection.TcpServer`
- `eapli.aisafe.app.server.handlers.AddAircraftRequestHandler`

### 5.2 Domain Rules

The `Aircraft` constructor validates:

    registration cannot be null or blank
    country cannot be null or blank
    totalSeats = sum(seatConfig.values())
    if (totalSeats > model.getCapacity()) throw IllegalArgumentException

### 5.3 Repository Operations

The repository supports:

    save(aircraft)
    findByRegistration(registration)
    findByCompany(company)
    existsByRegistration(registration)

### 5.4 Persistence

The entity is persisted in table:

    T_AIRCRAFT

Important constraints:

    REGISTRATION_NUMBER unique

### 5.5 Bootstrap

The bootstrap process creates initial aircraft models and assigns some default aircraft to pre-existing companies. This ensures that fleet listings and flight plans have testable data out-of-the-box.

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
    1. Add aircraft

Example input:

    Aircraft Model ID: 1 (Airbus A320)
    Registration: CS-TPA
    Country: Portugal
    First Class Seats: 0
    Business Seats: 16
    Economy Seats: 144
    Operational Status: ACTIVE

Expected result:

    Aircraft CS-TPA registered successfully in the fleet.

## 7. Tests

The following aspects are tested:

- valid aircraft creation;
- registration number cannot be blank;
- total seats exactly equal to model capacity is accepted;
- total seats less than model capacity is accepted;
- total seats greater than model capacity is rejected;
- duplicate registration number is rejected;
- aircraft must be linked to the logged-in collaborator's company;
- unauthenticated users cannot add aircraft;
- only authorized roles can add aircraft;
- client application successfully communicates via TCP and does not directly access the database.

## 8. Observations

The current implementation fully satisfies US070.

The aircraft is successfully integrated with the fleet listing (US072). The use of the TCP client-server architecture fulfills the remote access requirement (US078).

Future improvements may include:
- Creating user interfaces for US071 (Decommission an aircraft).
- Linking aircraft to flight plans (US080).