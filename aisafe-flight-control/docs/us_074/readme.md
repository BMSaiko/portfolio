# US074 - Delete a Flight Route

## 1. Context

This user story belongs to the flight route management and air transport company configuration area.

The goal is to allow an Air Transport Company Collaborator to deactivate a flight route from a given date onwards.

No more flights can be created on a deleted route after the deactivation date. However, a route cannot be deleted if planned flights already exist after that date.

## 1.1 List of Issues

- **Analysis:** Understand route lifecycle, planned flight dependencies, and the meaning of deletion as deactivation.
- **Design:** Define route deactivation behavior, controller checks, repository operations, TCP/client flow, and persistence changes.
- **Implement:** Implement route deactivation from a date with validation against future planned flight plans.
- **Test:** Validate successful deactivation, nonexistent route, already inactive route, planned flight plans after date, ownership checks, and authorization.

## 2. Requirements

**US074** As an Air Transport Company Collaborator, I want to deactivate a flight route from a given date onwards.

No more flights can be created on a deleted flight route. But a route cannot be deleted if there are planned flights after that date.

### Acceptance Criteria

- **US074.1:** An Air Transport Company Collaborator can deactivate one of the company's flight routes.
- **US074.2:** The user must provide the route designator and deactivation date.
- **US074.3:** A deactivated route cannot be used to create new flights after the deactivation date.
- **US074.4:** A route cannot be deactivated if planned flight plans exist from the given date onwards.
- **US074.5:** The route is not physically deleted; it is marked inactive/deactivated.
- **US074.6:** The operation must be authenticated and authorized appropriately.
- **US074.7:** A collaborator cannot deactivate a route belonging to another company.

### Dependencies / References

- Depends on **US030** for authentication and authorization.
- Depends on **US060** and **US061** because the actor is an air transport company collaborator.
- Depends on **US073** because flight routes must exist before they can be deactivated.
- Depends on flight plan functionality to check planned operations after the deactivation date.
- Related to flight plan creation/import because inactive routes must not be used after their deactivation date.

## 3. Analysis

### 3.1 Understanding

Deleting a flight route is a business deactivation. The route remains stored for historical consistency, but from the given date onwards it cannot be used for new planned flights.

The operation must check:

- the user is authenticated and has the Air Transport Company Collaborator role;
- the route designator and deactivation date were provided;
- the route exists;
- the route belongs to the logged-in collaborator's company;
- no planned flight plans exist from that date onwards;
- the route is not already inactive.

### 3.2 Assumption About Planned Flights

At the current project stage, there is no separate `Flight` aggregate representing a concrete scheduled flight. The available model stores planned flight information in `FlightPlan`.

Therefore, for this implementation, "planned flights after that date" are interpreted as `FlightPlan` records that:

- match the route origin and destination;
- have a non-null `departureDateTime`;
- have `departureDateTime` on or after the deactivation date;
- are not `COMPLETED`;
- are not `CANCELLED`.

### 3.3 Key Decisions

| Decision | Rationale |
|---|---|
| Use logical deletion/deactivation | Keeps historical route information available. |
| Store a deactivation date | The requirement says the route is deactivated from a given date onwards. |
| Check planned flight plans before deactivation | Prevents invalidating already planned future operations. |
| Restrict operation to the owning company | A collaborator can only manage routes belonging to their company. |
| Compare company ownership by ICAO code | JPA may load different entity instances, so object reference equality is not reliable. |
| Keep route designator as lookup key | Route designators are unique and meaningful to users. |

## 4. Design

### 4.1 Realization

The operation is handled by `DeactivateFlightRouteController`.

The Company App collects:

- route designator;
- deactivation date.

The Company App sends the request to the TCP server using the `DEACTIVATE_ROUTE` command. The server handles the command in `AtccConnectionHandler`, invokes the controller, and returns either an `OK` or `ERROR` response.

The controller checks authorization, identifies the logged-in collaborator's company, retrieves the route, validates ownership, checks whether planned flight plans exist from the date onwards, applies the deactivation, and persists the route.

### 4.2 Main Flow

```text
Air Transport Company Collaborator -> AirTransportCompanyApp: select Route Management
AirTransportCompanyApp -> DeactivateFlightRouteUI: select Deactivate Flight Route
DeactivateFlightRouteUI -> TcpClient: send DEACTIVATE_ROUTE designator,date
TcpClient -> AtccTcpServer: TCP request
AtccTcpServer -> AtccConnectionHandler: process message
AtccConnectionHandler -> DeactivateFlightRouteController: deactivateRoute(designator, date)
DeactivateFlightRouteController -> AuthService: ensureHasAnyRole(AIR_TRANSPORT_COMPANY_COLLABORATOR, ADMIN)
DeactivateFlightRouteController -> CustomerCollaboratorRepository: findByUserEmail(user.email)
DeactivateFlightRouteController -> AirTransportCompanyRepository: findByIcaoCode(collaborator.customerCode)
DeactivateFlightRouteController -> FlightRouteRepository: findByDesignator(designator)
DeactivateFlightRouteController -> FlightPlanRepository: existsPlannedForRouteFrom(route, date)
DeactivateFlightRouteController -> FlightRoute: deactivateFrom(date)
DeactivateFlightRouteController -> FlightRouteRepository: save(route)
AtccConnectionHandler -> TcpClient: OK response
DeactivateFlightRouteUI -> Air Transport Company Collaborator: show success message
```

### 4.3 Alternative Flows

**Route not found:**

The repository returns an empty `Optional`. The operation is rejected and the user receives an error message.

**Route belongs to another company:**

The controller compares the route company's ICAO code with the authenticated collaborator's company ICAO code. If they do not match, the operation is rejected.

**Planned flight plans exist from date onwards:**

`FlightPlanRepository.existsPlannedForRouteFrom(route, date)` returns `true`. The operation is rejected because future planned operations would be affected.

**Invalid deactivation date:**

The TCP protocol parser rejects invalid date formats. The user receives an error message asking for `yyyy-MM-dd`.

**Route already inactive:**

`FlightRoute.deactivateFrom(date)` throws an `IllegalStateException`. The operation is rejected.

**Unauthenticated or unauthorized user:**

`AuthService` rejects the operation. The operation is not executed.

## 5. Implementation

### 5.1 Key Classes

#### aisafe.core

- `eapli.aisafe.routemanagement.domain.FlightRoute`
- `eapli.aisafe.routemanagement.domain.RouteDesignator`
- `eapli.aisafe.routemanagement.repositories.FlightRouteRepository`
- `eapli.aisafe.routemanagement.application.DeactivateFlightRouteController`
- `eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository`

#### aisafe.persistence.impl

- `eapli.aisafe.persistence.impl.jpa.JpaFlightRouteRepository`
- `eapli.aisafe.persistence.impl.jpa.JpaFlightPlanRepository`
- `eapli.aisafe.persistence.impl.inmemory.InMemoryFlightPlanRepository`

#### aisafe.app.company.console

- `eapli.aisafe.app.company.console.presentation.MainMenu`
- `eapli.aisafe.app.company.console.presentation.routes.DeactivateFlightRouteUI`
- `eapli.aisafe.app.company.console.connection.TcpClient`

#### aisafe.app.server

- `eapli.aisafe.app.server.AtccTcpServer`
- `eapli.aisafe.app.server.AtccConnectionHandler`

#### aisafe.core TCP protocol

- `eapli.aisafe.tcpprotocol.AtccProtocol`

### 5.2 Domain Rules

The `FlightRoute` entity validates:

- designator cannot be null;
- company cannot be null;
- origin airport cannot be null;
- destination airport cannot be null;
- origin and destination must be different;
- deactivation date cannot be null;
- already inactive routes cannot be deactivated again.

The route availability rule is:

- active routes are available on any date;
- inactive routes are available only before `deactivatedFrom`;
- inactive routes are not available on or after `deactivatedFrom`.

### 5.3 Repository Operations

`FlightRouteRepository` supports:

- `save(route)`;
- `findByDesignator(designator)`;
- `existsByDesignator(designator)`;
- `findActiveByCompany(company)`;
- `findAllByCompany(company)`;
- `findAll()`;
- `delete(route)`.

`FlightPlanRepository` supports:

- `existsPlannedForRouteFrom(route, date)`.

This method checks for flight plans on the route's origin/destination with `departureDateTime` on or after the deactivation date and status different from `COMPLETED` and `CANCELLED`.

### 5.4 Persistence

The route is persisted in table:

```text
T_FLIGHT_ROUTE
```

Important fields:

```text
DESIGNATOR
ACTIVE
DEACTIVATED_FROM
COMPANY_ID
ORIGIN_IATA
DESTINATION_IATA
```

The deactivation is persisted through `FlightRouteRepository.save(route)`.

## 6. Demonstration

Run the TCP server application:

```text
eapli.aisafe.app.server.AtccTcpServer
```

Run the Company App Client in a separate terminal:

```text
eapli.aisafe.app.company.console.AirTransportCompanyApp
```

Login with:

```text
Email: atcc@airline.com
Password: Atcc1234
```

Create a route first:

```text
=== Main Menu ===
Option: 2

--- Route Management ---
Option: 1

Route Designator: TP777
Origin IATA Code: LIS
Destination IATA Code: OPO
```

Expected result:

```text
[Server] OK Route TP777 created.
```

Deactivate the route:

```text
=== Main Menu ===
Option: 2

--- Route Management ---
Option: 2

Route Designator: TP777
Deactivation Date (yyyy-MM-dd): 2026-06-10
```

Expected result:

```text
[Server] OK Route TP777 deactivated from 2026-06-10
```

Trying to deactivate the same route again should fail:

```text
[Server] ERROR Route TP777 is already inactive.
```

Trying to deactivate a route from another company should fail:

```text
[Server] ERROR Route FR123 does not belong to the authenticated company.
```

If planned flight plans exist from the given date onwards, the operation should fail:

```text
[Server] ERROR Route TP777 cannot be deactivated because there are planned flights from 2026-06-10 onwards.
```

## 7. Tests

The following aspects are tested:

- valid route deactivation with date;
- route availability before, on, and after the deactivation date;
- route not found is rejected;
- route from another company is rejected;
- route cannot be deactivated if planned flight plans exist from the date onwards;
- null or blank route designator is rejected;
- null deactivation date is rejected;
- already inactive routes cannot be deactivated again;
- unauthenticated users cannot deactivate routes;
- users without the required role cannot deactivate routes;
- authorized company collaborators can deactivate their own routes;
- ownership is checked by company code rather than object instance equality.

Relevant test classes:

- `eapli.aisafe.routemanagement.domain.FlightRouteTest`
- `eapli.aisafe.routemanagement.application.DeactivateFlightRouteControllerTest`

## 8. Observations

The route is not physically removed from the database.

The current implementation checks planned operations using `FlightPlan` because the project does not yet include a separate `Flight` aggregate. If a future sprint introduces a concrete `Flight` entity, the planned-flight check should be moved or extended to that model.

Future improvements:

- allow listing inactive routes;
- allow route reactivation if business rules permit;
- show blocking planned flight plans to help the collaborator decide a valid deactivation date.
