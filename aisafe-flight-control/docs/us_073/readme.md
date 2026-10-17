# US073 - Create a Flight Route

## 1. Context

This user story belongs to the flight route management and air transport company configuration area.

The goal is to allow an Air Transport Company Collaborator to add a flight route for their company.

This functionality must be available to the Air Transport Company Collaborator remotely, using the Air Transport Company App.

## 1.1 List of Issues

- **Analysis:** Understand the attributes that define a flight route, its relationship with airports and the air transport company, and the naming rules.
- **Design:** Define the domain entity, repository, controller, persistence implementation, UI/Client flow, and bootstrap process.
- **Implement:** Implement flight route creation with validation of the route designator format, uniqueness enforcement, and association to the logged-in collaborator's company and the selected airports.
- **Test:** Validate domain rules (e.g., route designator format, unique name), existence of origin/destination airports, and authorization requirements.

## 2. Requirements

**US073** As an Air Transport Company Collaborator, I want to add a flight route for my company.

A route is between two airports (start and end) and has a name with 2 letters (the company's initials) and up to 4 numbers (e.g. TP123). The route's name must be unique.

### Acceptance Criteria

- **US073.1:** An Air Transport Company Collaborator can create a flight route for their company.
- **US073.2:** The route must have an origin airport and a destination airport (they must be different).
- **US073.3:** The route designator must follow the format: exactly 2 letters (matching the company's IATA code) followed by 1 to 4 digits (e.g. TP123).
- **US073.4:** The route designator must be unique in the system.
- **US073.5:** The route must be associated with the logged-in collaborator's company.
- **US073.6:** Both origin and destination airports must already exist in the system.
- **US073.7:** The access must be done remotely using a specific TCP-based network client application (Air Transport Company App), without direct interaction with the database.
- **US073.8:** The operation must be authenticated and authorized appropriately.

### Dependencies / References

- Depends on **US030** for authentication and authorization.
- Depends on **US052** (Create an airport) to have airports available as origin/destination.
- Depends on **US060** (Register an air transport company) and **US061** (Add a collaborator) for the actor to exist.
- Used by **US074** (Delete/deactivate a flight route).
- Related to flight plan creation (US080) which will reference routes.
- Related to the Air Transport Company App remote access requirements (**US078**).

## 3. Analysis

### 3.1 Understanding

A flight route represents a named, directional connection between two airports operated by a specific air transport company.

The domain model identifies a flight route with:

- Route Designator (unique name — company IATA prefix + numeric suffix).
- Company (owner/operator).
- Origin Airport.
- Destination Airport.
- Active status (routes may be deactivated — see US074).

The route designator serves as the global unique identifier (business ID) for the route. The two-letter prefix of the designator must match the IATA code of the company creating it, ensuring the naming convention is tied to the company identity. Because this is a remote application, data must be transferred via TCP sockets using DTOs, and the server will perform the actual database operations.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| Create `FlightRoute` as a domain entity | It represents a uniquely identifiable asset with its own lifecycle (can be deactivated in US074). |
| Use Route Designator as Business ID | The requirement explicitly states the route name must be unique and follows a structured format. |
| Encapsulate the designator in a Value Object (`RouteDesignator`) | Centralises format validation (regex) and keeps the `FlightRoute` constructor clean. |
| Validate IATA prefix against the company | Ensures the route's naming convention is consistent with the company that owns it. |
| Origin ≠ Destination invariant in domain | A route between the same airport is meaningless; enforced at construction time. |
| Allow only `AIR_TRANSPORT_COMPANY_COLLABORATOR` | Ensures only the proper actor can alter a company's routes. |
| Use DTOs for TCP communication | Prevents exposing domain entities directly over the network and separates concerns. |

## 4. Design

### 4.1 Realization

The creation is handled by `CreateFlightRouteController` on the server side.

The UI/Client collects:
- route designator (e.g. TP123);
- origin airport (ICAO or IATA code);
- destination airport (ICAO or IATA code).

The client sends a formulated request over TCP. The server controller checks authentication, identifies the collaborator's company, validates the designator format and IATA prefix, checks uniqueness of the designator through the repository, fetches both airports from the repository, creates the domain object, persists it, and returns a success response over TCP.

### 4.2 Main Flow

    Air Transport Company App -> FlightRouteMenuUI: create flight route
    FlightRouteMenuUI -> TcpClient: sendRequest(CREATE_FLIGHT_ROUTE, dataDTO)
    TcpClient -> TcpServer: [Network Transfer]
    TcpServer -> CreateFlightRouteController: createFlightRoute(dataDTO)
    CreateFlightRouteController -> AuthService: ensureHasRole(AIR_TRANSPORT_COMPANY_COLLABORATOR)
    CreateFlightRouteController -> AuthService: getAuthenticatedUserCompany()
    CreateFlightRouteController -> FlightRouteRepository: existsByDesignator(designator)
    CreateFlightRouteController -> AirportRepository: findByCode(originCode)
    CreateFlightRouteController -> AirportRepository: findByCode(destinationCode)
    CreateFlightRouteController -> RouteDesignator: new RouteDesignator(designator, company.getIataCode())
    RouteDesignator -> RouteDesignator: validateFormat() // regex: [A-Z]{2}[0-9]{1,4}
    RouteDesignator -> RouteDesignator: validatePrefix(company.getIataCode())
    CreateFlightRouteController -> FlightRoute: new FlightRoute(designator, company, originAirport, destinationAirport)
    FlightRoute -> FlightRoute: validateOriginNotEqualsDestination()
    CreateFlightRouteController -> FlightRouteRepository: save(flightRoute)
    FlightRouteRepository --> CreateFlightRouteController: saved flight route
    CreateFlightRouteController --> TcpServer: success response
    TcpServer -> TcpClient: [Network Transfer]
    TcpClient --> FlightRouteMenuUI: success
    FlightRouteMenuUI --> Air Transport Company Collaborator: success message

### 4.3 Alternative Flows

**Designator already exists:**

    Server Controller detects existing route designator.
    Operation is rejected.
    Server returns an error response via TCP.
    User receives an error message.

**Invalid designator format (wrong prefix or bad format):**

    RouteDesignator value object throws IllegalArgumentException on construction.
    Operation is rejected.
    Server returns an error response via TCP.
    User receives an error message.

**Origin airport not found or Destination airport not found:**

    AirportRepository returns empty Optional.
    Operation is rejected.
    Server returns an error response via TCP.
    User receives an error message.

**Origin equals Destination:**

    FlightRoute constructor invariant fires, throwing IllegalArgumentException.
    Operation is rejected.
    Server returns an error response via TCP.
    User receives an error message.

**Unauthenticated or Unauthorized user:**

    AuthService throws SecurityException.
    Operation is rejected.
    Server returns an authorization error response.

## 5. Implementation

### 5.1 Key Classes

#### aisafe.core

- `eapli.aisafe.routemanagement.domain.FlightRoute`
- `eapli.aisafe.routemanagement.domain.RouteDesignator`
- `eapli.aisafe.routemanagement.repositories.FlightRouteRepository`
- `eapli.aisafe.routemanagement.application.CreateFlightRouteController`

#### aisafe.persistence.impl

- `eapli.aisafe.persistence.impl.jpa.JpaFlightRouteRepository`

#### aisafe.app.company.console (TCP Client)

- `eapli.aisafe.app.company.console.presentation.routes.CreateFlightRouteUI`
- `eapli.aisafe.app.company.console.connection.TcpClient`

#### aisafe.app.server (TCP Server)

- `eapli.aisafe.app.server.connection.TcpServer`
- `eapli.aisafe.app.server.handlers.CreateFlightRouteRequestHandler`

### 5.2 Domain Rules

The `RouteDesignator` value object validates:

    designator cannot be null or blank
    designator must match the regex: ^[A-Z]{2}[0-9]{1,4}$
    first 2 letters of the designator must equal the company's IATA code

The `FlightRoute` constructor validates:

    designator cannot be null
    company cannot be null
    originAirport cannot be null
    destinationAirport cannot be null
    originAirport must not be the same as destinationAirport

### 5.3 Repository Operations

The repository supports:

    save(flightRoute)
    findByDesignator(designator)
    findByCompany(company)
    existsByDesignator(designator)

### 5.4 Persistence

The entity is persisted in table:

    T_FLIGHT_ROUTE

Important constraints:

    DESIGNATOR unique
    ORIGIN_AIRPORT_ID not null (FK to T_AIRPORT)
    DESTINATION_AIRPORT_ID not null (FK to T_AIRPORT)
    COMPANY_ID not null (FK to T_AIR_TRANSPORT_COMPANY)

### 5.5 Proposed Package Structure

```
aisafe.core/
└── src/main/java/eapli/aisafe/
    └── routemanagement/
        ├── application/
        │   └── CreateFlightRouteController.java
        ├── domain/
        │   ├── FlightRoute.java
        │   └── RouteDesignator.java
        └── repositories/
            └── FlightRouteRepository.java

aisafe.persistence.impl/
└── src/main/java/eapli/aisafe/persistence/impl/jpa/
    └── JpaFlightRouteRepository.java

aisafe.app.company.console/
└── src/main/java/eapli/aisafe/app/company/console/
    └── presentation/routes/
        └── CreateFlightRouteUI.java

aisafe.app.server/
└── src/main/java/eapli/aisafe/app/server/
    └── handlers/
        └── CreateFlightRouteRequestHandler.java
```

### 5.6 Bootstrap

The bootstrap process may create a set of default flight routes between pre-existing airports for pre-existing companies, ensuring that flight plan tests (US080) have valid routes available from the start.

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
    2. Route management
    1. Create flight route

Example input:

    Route Designator: TP123
    Origin Airport (ICAO): LPPT
    Destination Airport (ICAO): LPPR

Expected result:

    Flight route TP123 (LPPT -> LPPR) created successfully.

## 7. Tests

The following aspects are tested:

- valid flight route creation with correct designator format;
- designator cannot be null or blank;
- designator format must match `^[A-Z]{2}[0-9]{1,4}$` — invalid formats are rejected;
- designator prefix must match the company's IATA code;
- duplicate designator is rejected;
- origin and destination airports must exist in the system;
- origin airport cannot equal destination airport;
- route is linked to the logged-in collaborator's company;
- unauthenticated users cannot create routes;
- only authorized roles (AIR_TRANSPORT_COMPANY_COLLABORATOR) can create routes;
- client application successfully communicates via TCP and does not directly access the database.

## 8. Observations

The current design fully covers US073.

The flight route is a prerequisite for flight plan creation (US080): a flight plan will be associated with a route, so routes must exist before flight plans can be scheduled.

Future improvements / related stories:
- **US074** — Deactivate a flight route from a given date.
- **US080** — Creating flight plans will reference a `FlightRoute`.
- Consider listing routes (similar to US072 for fleet) as a quality-of-life addition.