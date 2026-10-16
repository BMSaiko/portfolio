# US080 - Create a Flight Plan

## 1. Context

This user story belongs to Sprint 3 and is evaluated under EAPLI.

The goal is to allow a **Pilot** to manually register a flight plan for an existing route, providing the aircraft, departure date/time, fuel quantity, and pilot information. The flight plan is created with status `DRAFT` and is prepared for a multi-step validation process.

This US is distinct from **US081** (which creates a flight plan from a DSL file). US080 is a manual, form-based creation; US081 is a DSL import.

### 1.1 List of Issues

- **Analysis:** Understand the flight plan requirements, the role of `FlightRoute` and `Pilot`, and the business rule that the pilot must belong to the route's company.
- **Design:** Define the `CreateFlightPlanController`, the new domain fields (`Pilot`, `FlightRoute`, `Aircraft`), the DTO, and all supporting artifacts.
- **Implement:** Implement the controller, DTO, updated domain, UI, JPA and InMemory repositories, and bootstrap.
- **Test:** Develop unit tests for domain construction and controller behaviour.

---

## 2. Requirements

**US080** As a Pilot, I want to register a flight plan for a route. I must add the aircraft, departure date/time, fuel quantity, and pilot. The pilot must be of the route's company. Flight plan status is set to "draft" when created and must undergo a multi-step validation process.

### Acceptance Criteria

- **US080.1:** The pilot must belong to the same company as the route.
- **US080.2:** The route must exist and be active.
- **US080.3:** The aircraft must exist and not be decommissioned.
- **US080.4:** Fuel quantity must be strictly positive.
- **US080.5:** Departure date/time must be strictly in the future.
- **US080.6:** The flight plan is created with status `DRAFT`.
- **US080.7:** The system must provide a console UI for Pilots to register a flight plan.

### Dependencies / References

- **US073** (prerequisite): A flight route must exist for the flight plan to reference.
- **US075** (prerequisite): A pilot must be registered in the system.
- **US070/US071** (prerequisite): An aircraft must exist and not be decommissioned.
- **US085** depends on this US: flight plan validation operates on an existing `DRAFT` plan.
- **US082** depends on this US: weather data is added to an existing flight plan.

---

## 3. Analysis

### 3.1 Understanding

A flight plan is the operational declaration of a flight along a registered route. It is manually created by a Pilot, who selects a route, assigns an aircraft, sets a departure date/time, and specifies the estimated fuel on board.

The following rules apply:

- The route must exist and be active. No flight plan can reference a deactivated route.
- The pilot must belong to the same company as the route — the key cross-aggregate business rule.
- The aircraft must not be decommissioned (`OperationalStatus.RETIRED`).
- Fuel quantity must be positive.
- Departure date/time must be in the future.
- The flight plan is always created with status `DRAFT`. No validation or approval happens at this stage.
- The `FlightPlan` aggregate already existed (built for US081). This US extended it with `Pilot`, `FlightRoute`, and `Aircraft` references and added a new constructor for manual creation (no legs at creation time).

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| Extend the existing `FlightPlan` aggregate | The entity already had `status`, `departureDateTime`, `estimatedFuel`, and `aircraftModel`. Adding `pilot`, `flightRoute`, and `aircraft` keeps the aggregate cohesive. |
| New constructor for US080 (no legs) | US080 creates a plan shell; legs are populated later via DSL import (US081). The legs-not-empty guard was moved to the VALIDATED transition, not construction. |
| Company cross-check in Controller | The controller fetches both pilot and route and verifies `pilot.company().equals(route.company())`. This avoids coupling domain aggregates. |
| `Aircraft` (concrete tail) instead of `AircraftModel` | US080 specifies a concrete aircraft. `aircraftModel` is derived from `aircraft.model()` and kept for US081 backward-compatibility. |
| DTO validates inputs before Controller | `FlightPlanRegistrationDTO` rejects null/blank/non-positive values at construction, keeping the controller focused on business rules. |
| Bootstrap via `FleetBootstrap` | Aircraft (CS-TKX) and pilot (`pilot@aisafe.com`) are created by `FleetBootstrap`, which also runs the route bootstrap via `FlightRouteBootstrap`. This ensures US080 can be demonstrated without manual setup. |

---

## 4. Design

### 4.1 Domain Model

```
FlightPlan                    (Aggregate Root — extended)
├── FlightPlanId              (Value Object, EmbeddedId)
├── flightRoute               (ManyToOne → FlightRoute)   ← NEW
├── aircraft                  (ManyToOne → Aircraft)       ← NEW
├── aircraftModel             (ManyToOne → AircraftModel)  derived from aircraft
├── pilot                     (ManyToOne → Pilot)          ← NEW
├── departureAirport          String  (derived from route.origin)
├── arrivalAirport            String  (derived from route.destination)
├── status                    DRAFT
├── legs                      List<FlightLeg>  (empty at US080 creation)
├── totalDistance             0.0 at creation
├── estimatedFuel             double
└── departureDateTime         LocalDateTime

FlightRoute                   (from US073)
├── RouteDesignator           (EmbeddedId)
├── company                   AirTransportCompany
├── origin                    Airport
├── destination               Airport
└── active                    boolean

Pilot                         (from US075)
├── email                     Email
├── company                   AirTransportCompany
├── certifications            Set<AircraftModel>
└── status                    ACTIVE / INACTIVE

Aircraft                      (from US070)
├── registration              String (PK)
├── model                     AircraftModel
├── company                   AirTransportCompany
└── status                    OperationalStatus
```

### 4.2 Registration Sequence

```
Pilot -> CreateFlightPlanUI : select route, enter aircraft/pilot/date/fuel
CreateFlightPlanUI -> CreateFlightPlanController : createFlightPlan(dto)

CreateFlightPlanController -> AuthService : ensureHasAnyRole(PILOT)

CreateFlightPlanController -> FlightRouteRepository : findByDesignator(routeDesignator)
alt route not found
    CreateFlightPlanController --> CreateFlightPlanUI : IllegalStateException
end
alt route inactive
    CreateFlightPlanController --> CreateFlightPlanUI : IllegalStateException
end

CreateFlightPlanController -> AircraftRepository : findByRegistration(registration)
alt aircraft not found or RETIRED
    CreateFlightPlanController --> CreateFlightPlanUI : IllegalStateException
end

CreateFlightPlanController -> PilotRepository : findByEmail(email)
alt pilot not found or INACTIVE
    CreateFlightPlanController --> CreateFlightPlanUI : IllegalStateException
end

CreateFlightPlanController -> (check) : pilot.company() == route.company()
alt company mismatch
    CreateFlightPlanController --> CreateFlightPlanUI : IllegalArgumentException
end

CreateFlightPlanController -> (check) : departureDateTime in the future
alt past date
    CreateFlightPlanController --> CreateFlightPlanUI : IllegalArgumentException
end

CreateFlightPlanController -> FlightPlan : new FlightPlan(id, route, aircraft, pilot, dateTime, fuel)
CreateFlightPlanController -> FlightPlanRepository : save(flightPlan)
CreateFlightPlanController --> CreateFlightPlanUI : FlightPlan (status=DRAFT)
CreateFlightPlanUI --> Pilot : success message with flight plan ID
```

### 4.3 Acceptance Tests

**Test 1:** Flight plan is created with DRAFT status.
Refers to: US080.6

```java
@Test
void ensureManualFlightPlanIsCreatedWithDraftStatus() { ... }
```

**Test 2:** Pilot from a different company is rejected.
Refers to: US080.1

```java
@Test
void ensurePilotFromDifferentCompanyIsRejected() { ... }
```

**Test 3:** Deactivated route is rejected.
Refers to: US080.2

```java
@Test
void ensureDeactivatedRouteIsRejected() { ... }
```

**Test 4:** Decommissioned aircraft is rejected.
Refers to: US080.3

```java
@Test
void ensureDecommissionedAircraftIsRejected() { ... }
```

**Test 5:** Departure date in the past is rejected.
Refers to: US080.5

```java
@Test
void ensurePastDepartureDateTimeIsRejected() { ... }
```

**Test 6:** Zero or negative fuel is rejected by the DTO.
Refers to: US080.4

```java
@Test
void ensureZeroFuelIsRejectedByDTO() { ... }
```

---

## 5. Implementation

### 5.1 Key Classes

#### aisafe.core

- `eapli.aisafe.flightplanmanagement.domain.FlightPlan` ← extended
- `eapli.aisafe.flightplanmanagement.domain.FlightPlanId`
- `eapli.aisafe.flightplanmanagement.domain.FlightPlanStatus`
- `eapli.aisafe.flightplanmanagement.application.CreateFlightPlanController` ← new
- `eapli.aisafe.flightplanmanagement.dto.FlightPlanRegistrationDTO` ← new
- `eapli.aisafe.flightplanmanagement.repositories.FlightPlanRepository` ← extended

#### aisafe.persistence.impl

- `eapli.aisafe.persistence.impl.jpa.JpaFlightPlanRepository` ← extended
- `eapli.aisafe.persistence.impl.inmemory.InMemoryFlightPlanRepository` ← extended

#### aisafe.bootstrap

- `eapli.aisafe.bootstrap.FlightRouteBootstrap` ← new
- `eapli.aisafe.bootstrap.FleetBootstrap` ← new (aircraft + pilot)

#### aisafe.app.backoffice.console

- `eapli.aisafe.app.backoffice.console.presentation.flightplanmanagement.CreateFlightPlanUI` ← new
- `eapli.aisafe.app.backoffice.console.presentation.flightplanmanagement.FlightPlanMenuUI` ← extended
- `eapli.aisafe.app.backoffice.console.presentation.MainMenu` ← extended
- `eapli.aisafe.app.backoffice.console.AISafeBackoffice` ← extended

### 5.2 Controller Business Rules (in order)

The `CreateFlightPlanController.createFlightPlan(dto)` enforces rules in this order:

1. Role `PILOT` required — `authService.ensureHasAnyRole(Role.PILOT)`
2. Route exists — `routeRepository.findByDesignator(...)`
3. Route is active — `route.isActive()`
4. Aircraft exists — `aircraftRepository.findByRegistration(...)`
5. Aircraft not retired — `aircraft.status() != RETIRED`
6. Pilot exists — `pilotRepository.findByEmail(...)`
7. Pilot is active — `pilot.isActive()`
8. **Company cross-check** — `pilot.company().equals(route.company())`
9. Departure in the future — `departureDateTime.isAfter(LocalDateTime.now())`
10. Fuel positive — enforced by `FlightPlanRegistrationDTO` constructor

### 5.3 FlightPlan Domain Changes

Two constructors coexist:

| Constructor | Used by | Legs required |
|---|---|---|
| `FlightPlan(id, route, aircraft, pilot, dateTime, fuel)` | US080 | No — empty at creation |
| `FlightPlan(id, routeStr, depAirport, arrAirport, model, legs, dist, fuel, dateTime)` | US081 | Yes — at least one |

New JPA fields added to `FlightPlan`:

```java
@ManyToOne
@JoinColumn(name = "route_designator")
private FlightRoute flightRoute;

@ManyToOne
@JoinColumn(name = "aircraft_registration")
private Aircraft aircraft;

@ManyToOne
@JoinColumn(name = "pilot_id")
private Pilot pilot;
```

### 5.4 New Repository Methods

`FlightPlanRepository`:
- `List<FlightPlan> findByPilot(Pilot pilot)` — used by future US077 (deactivate pilot)
- `List<FlightPlan> findByRoute(FlightRoute route)` — used by future US074 (deactivate route)

### 5.5 Bootstrap Execution Order

```
MasterUsersBootstrapper     (users, companies, collaborators)
AirControlAreaBootstrap     (air control areas, airports)
FlightRouteBootstrap        (routes TP123, TP456, TP789 — depends on airports + companies)
EngineModelBootstrap        (engine models, aircraft models)
FleetBootstrap              (aircraft CS-TKX, CS-TPA + pilot pilot@aisafe.com)
FlightPlanBootstrap         (sample DRAFT plans — depends on all above)
```

### 5.6 Authorization

```java
authService.ensureHasAnyRole(Role.PILOT);
```

The `PILOT` role also sees the "Flight Plan Management" menu entry (in addition to `BACKOFFICE_OPERATOR` and `ADMIN`).

---

## 6. Demonstration

Run the application and log in as a Pilot:

```
Email:    pilot@aisafe.com
Password: Pilot123
```

Navigate to: **1. Flight Plan Management → 1. Create Flight Plan**

```
Available active routes:
  1. TP123  (LIS -> OPO)  [TP]
  2. TP456  (OPO -> FAO)  [TP]
  3. TP789  (LIS -> FAO)  [TP]

Select route: 1
Aircraft registration: CS-TKX
Pilot e-mail: pilot@aisafe.com
Departure date/time: 2026-09-01 10:00
Estimated fuel on board (kg): 15000
```

Expected output:

```
Flight plan created successfully!
[<uuid>] LIS -> OPO | Aircraft: CS-TKX | Pilot: pilot@aisafe.com | Status: DRAFT | Fuel: 15000.0 kg
```

Verify by navigating to **3. List Flight Plans** — the new plan appears with status `DRAFT`.

---

## 7. Tests

| Class | Tests | What is covered |
|---|---|---|
| `FlightPlanTest` | 8 (US080 section) | Manual constructor, DRAFT status, no legs at creation, airports derived from route, null/invalid guard rejections |
| `CreateFlightPlanControllerTest` | 9 | Happy path, company mismatch, deactivated route, unknown route, retired aircraft, unknown aircraft, inactive pilot, unknown pilot, past departure |
| `ImportFlightPlanControllerTest` | 5 | Existing US081 tests, extended with status and persistence verification |

---

## 8. Observations

- The existing `ImportFlightPlanController` (US081) is unaffected. Both US080 and US081 produce a `FlightPlan` in `DRAFT` state via different constructors.
- The `FlightPlanService` facade was kept for US081 listing/import operations. The US080 controller is independent and does not use it.
- The `FleetBootstrap` (to be implemented) will create aircraft `CS-TKX` and pilot `pilot@aisafe.com` assigned to TAP, making the full US080 demo flow autonomous without manual data entry.
- Future improvements: add a listing UI for routes before flight plan creation; validate that the aircraft's company matches the route's company in addition to the pilot check.