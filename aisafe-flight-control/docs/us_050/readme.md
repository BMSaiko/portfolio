# US050 - Register an Air Control Area

## 1. Context

This user story belongs to Sprint 2 and is a mandatory requirement for the EAPLI course unit.

The goal is to allow Backoffice Operators to register Air Control Areas in the system. These areas define geographic zones of airspace and are a foundational entity used by airports (US052), weather data (US041), and flight validation.

### 1.1 List of Issues

- **Analysis:** Understand the Air Control Area requirements, geographic boundary rules, and uniqueness constraints.
- **Design:** Define the domain model for `AirControlArea`, `GeoBoundary`, and `AirControlAreaCode`, and the controller/repository structure.
- **Implement:** Implement registration with area code uniqueness, geographic boundary validation, and overlap detection.
- **Test:** Develop unit and integration tests to validate domain rules, boundary logic, and controller behaviour.

---

## 2. Requirements

**US050** As a Backoffice Operator, I want to register an air control area. The area code must be unique in the system. Geographic boundaries must be valid. This must also be achievable by a bootstrap process.

### Acceptance Criteria

- **US050.1:** The area code must be unique in the system.
- **US050.2:** Geographic boundaries must be valid (latitudes between -90 and 90, longitudes between -180 and 180, north > south, east > west).
- **US050.3:** The geographic boundary of a new area must not overlap with any existing area.
- **US050.4:** The system must provide a console UI for Backoffice Operators to register an area.
- **US050.5:** Registration must also be achievable through a bootstrap process.

### Dependencies / References

- **US030** (prerequisite): Authentication and authorization must be in place so that only Backoffice Operators can access this functionality.
- **US052** depends on this US: airports must be associated with an existing Air Control Area, and their coordinates must fall within its geographic boundary.
- **US041** depends on this US: weather data is registered for a specific Air Control Area.

---

## 3. Analysis

### 3.1 Understanding

An Air Control Area is a geographic zone of airspace managed by a flight control entity. It is defined by a unique code, a name, a minimum fuel requirement (used during flight validation), and a rectangular geographic boundary.

The following rules apply:

- The area code must be unique across the system.
- Geographic boundaries must be geometrically valid.
- No two areas may have overlapping boundaries, since the airspace cannot be managed by two different control areas simultaneously.
- The `GeoBoundary` value object encapsulates all coordinate validation and overlap/containment logic, keeping the domain rule enforcement close to the data it protects.
- The `contains()` method on `GeoBoundary` is intentionally prepared for use by US052, where airport coordinates must fall within the associated area's boundary.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| `GeoBoundary` as a Value Object | Encapsulates all coordinate validation and spatial logic in one place. Immutable and self-validating on construction. |
| `AirControlAreaCode` as a Value Object and `@EmbeddedId` | Enforces format rules and uniqueness at the domain level. Used as the JPA primary key. |
| Overlap detection in the Controller | The controller iterates all existing areas and delegates to `GeoBoundary.overlapsWith()`. Keeps domain logic pure and avoids complex JPQL spatial queries. |
| `contains()` method pre-implemented | Prepares US052 without creating a dependency — US052 simply calls this method when validating airport coordinates. |
| Bootstrap support | Ensures the system can be started with pre-loaded areas for testing and demonstration. |
| `javax.persistence` | Consistent with the existing project (JpaUserRepository). |

---

## 4. Design

### 4.1 Domain Model

```
AirControlArea          (Aggregate Root)
├── AirControlAreaCode  (Value Object, EmbeddedId)
├── name                (String)
├── minimumFuelRequired (double)
└── GeoBoundary         (Value Object, Embedded)
    ├── northLatitude
    ├── southLatitude
    ├── eastLongitude
    └── westLongitude
```

### 4.2 Registration Sequence

```
BackofficeOperator -> RegisterAirControlAreaUI : enter area data
RegisterAirControlAreaUI -> RegisterAirControlAreaController : registerAirControlArea(...)

RegisterAirControlAreaController -> AirControlAreaRepository : findByCode(code)
alt code already exists
    RegisterAirControlAreaController --> RegisterAirControlAreaUI : IllegalStateException
end

RegisterAirControlAreaController -> GeoBoundary : new GeoBoundary(n, s, e, w)
alt invalid coordinates
    GeoBoundary --> RegisterAirControlAreaController : IllegalArgumentException
end

RegisterAirControlAreaController -> AirControlAreaRepository : findAll()
loop for each existing area
    RegisterAirControlAreaController -> GeoBoundary : overlapsWith(existingBoundary)
    alt overlap detected
        RegisterAirControlAreaController --> RegisterAirControlAreaUI : IllegalStateException
    end
end

RegisterAirControlAreaController -> AirControlArea : new AirControlArea(...)
RegisterAirControlAreaController -> AirControlAreaRepository : save(area)
RegisterAirControlAreaController --> RegisterAirControlAreaUI : AirControlArea
RegisterAirControlAreaUI --> BackofficeOperator : success message
```

### 4.3 Acceptance Tests

**Test 1:** Verifies that an area with a duplicate code is rejected.
Refers to: US050.1

```java
@Test
void ensureDuplicateCodeIsRejected() { ... }
```

**Test 2:** Verifies that invalid geographic boundaries are rejected.
Refers to: US050.2

```java
@Test
void ensureNorthMustBeGreaterThanSouth() { ... }
```

**Test 3:** Verifies that a boundary overlapping an existing area is rejected.
Refers to: US050.3

```java
@Test
void ensureOverlappingBoundaryIsRejected() { ... }
```

**Test 4:** Verifies that two non-overlapping areas can both be registered.
Refers to: US050.3

```java
@Test
void ensureTwoNonOverlappingAreasCanBothBeRegistered() { ... }
```

**Test 5:** Verifies that adjacent (touching) areas are accepted.
Refers to: US050.3

```java
@Test
void ensureAdjacentAreaIsAccepted() { ... }
```

---

## 5. Implementation

### 5.1 Key Classes

#### aisafe.core

- `eapli.aisafe.aircontrolmanagement.domain.AirControlArea`
- `eapli.aisafe.aircontrolmanagement.domain.AirControlAreaCode`
- `eapli.aisafe.aircontrolmanagement.domain.GeoBoundary`
- `eapli.aisafe.aircontrolmanagement.application.RegisterAirControlAreaController`
- `eapli.aisafe.aircontrolmanagement.repositories.AirControlAreaRepository`

#### aisafe.persistence.impl

- `eapli.aisafe.persistence.impl.jpa.JpaAirControlAreaRepository`

#### aisafe.bootstrap

- `eapli.aisafe.bootstrap.AirControlAreaBootstrap`

#### aisafe.app.backoffice.console

- `eapli.aisafe.app.backoffice.console.presentation.aircontrolmanagement.RegisterAirControlAreaUI`

### 5.2 Geographic Boundary Validation

The `GeoBoundary` value object validates all rules on construction:

- Latitudes must be between -90 and 90.
- Longitudes must be between -180 and 180.
- North latitude must be strictly greater than south latitude.
- East longitude must be strictly greater than west longitude.

Any violation throws `IllegalArgumentException` immediately, before the entity is created.

### 5.3 Overlap Detection

The controller fetches all existing areas and calls `GeoBoundary.overlapsWith()` for each. Two rectangles overlap if and only if they intersect on both the latitude and longitude axes simultaneously:

```
latOverlap = this.north > other.south && other.north > this.south
lonOverlap = this.east  > other.west  && other.east  > this.west
overlap    = latOverlap && lonOverlap
```

Areas that only touch at a boundary edge are **not** considered overlapping.

### 5.4 Authorization

The controller enforces that only users with the `BACKOFFICE_OPERATOR` role can register areas:

```java
auth.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);
```

---

## 6. Demonstration

Run the application and log in as Backoffice Operator (`backoffice@aisafe.com` / `Back123`).

Navigate to: **Air Control Management → Register Air Control Area**

Enter when prompted:

- Area Code (e.g. `LPPC`)
- Name (e.g. `Lisbon Oceanic`)
- Minimum Fuel Required in kg (e.g. `5000`)
- North Latitude (e.g. `47.0`)
- South Latitude (e.g. `36.0`)
- East Longitude (e.g. `-6.0`)
- West Longitude (e.g. `-20.0`)

The system confirms success or displays the specific validation error.

### Bootstrap Pre-loaded Areas

| Code | Name | Min Fuel (kg) | Boundaries |
|---|---|---|---|
| `LPPC` | Lisbon Oceanic | 5000 | N:47 S:36 E:-6 W:-20 |
| `LPPO` | Santa Maria Oceanic | 5000 | N:35 S:25 E:-20 W:-35 |

---

## 7. Tests

| Class | Tests | What is covered |
|---|---|---|
| `GeoBoundaryTest` | 13 | Coordinate validation, `overlapsWith()`, `contains()` |
| `AirControlAreaCodeTest` | 6 | Format rules, nulls, case insensitivity, equality |
| `AirControlAreaTest` | 5 | Entity creation, identity, `sameAs()` |
| `RegisterAirControlAreaControllerTest` | 8 | Duplicate code, overlap detection, valid registrations |

---

## 8. Observations

The `GeoBoundary.contains(latitude, longitude)` method is already implemented and tested as part of this US. It is not used here directly but is intentionally prepared for US052, where airport coordinates must fall within the associated Air Control Area's boundary.

Future improvements may include:

- Support for areas that cross the 180° meridian.
- Spatial indexing for more efficient overlap queries at scale.
- A listing UI for registered air control areas.