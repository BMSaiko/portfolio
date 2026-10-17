# US052 - Register an Airport

## 1. Context

This user story belongs to Sprint 2 and is a mandatory requirement for the EAPLI course unit.

The goal is to allow Backoffice Operators to register airports in the system. Each airport must have a unique IATA code and a unique ICAO code, must be associated with an existing Air Control Area, and its geographic coordinates must fall within the geographic boundary of that area. Registration must also be achievable through a bootstrap process.

### 1.1 List of Issues

- **Analysis:** Understand the airport attributes, uniqueness constraints, and the relationship with Air Control Areas and their geographic boundaries.
- **Design:** Define the domain model for `Airport`, `IataCode`, `IcaoCode`, and `Coordinates`, and the controller/repository structure.
- **Implement:** Implement registration with code uniqueness, coordinate validation, and geographic boundary containment check.
- **Test:** Develop unit and integration tests to validate domain rules, coordinate containment, and controller behaviour.

---

## 2. Requirements

**US052** As a Backoffice Operator, I want to register an airport. The IATA and ICAO codes must be unique in the system. The airport coordinates must fall within the geographic boundary of its associated Air Control Area. This must also be achievable by a bootstrap process.

### Acceptance Criteria

- **US052.1:** The IATA code must be unique in the system.
- **US052.2:** The ICAO code must be unique in the system.
- **US052.3:** The airport must be associated with an existing Air Control Area.
- **US052.4:** The airport coordinates (latitude, longitude) must fall within the geographic boundary of the associated Air Control Area.
- **US052.5:** The system must provide a console UI for Backoffice Operators to register an airport.
- **US052.6:** Registration must also be achievable through a bootstrap process.

### Dependencies / References

- **US030** (prerequisite): Authentication and authorization must be in place so that only Backoffice Operators can access this functionality.
- **US050** (prerequisite): An Air Control Area must already exist before an airport can be registered. The `GeoBoundary.contains()` method implemented in US050 is used here directly.
- **US055 / US056** depend on this US: flight routes require origin and destination airports.

---

## 3. Analysis

### 3.1 Understanding

An airport is a physical location from which flights depart and arrive. It is identified by two internationally recognised codes — the IATA code (3 letters, e.g. `LIS`) and the ICAO code (4 letters, e.g. `LPPT`) — both of which must be unique across the system.

Every airport belongs to exactly one Air Control Area. Since the Air Control Area defines a geographic zone, the airport's position (latitude and longitude) must be contained within that zone. This is validated using the `GeoBoundary.contains()` method already implemented in US050.

The `Coordinates` value object stores latitude, longitude, and altitude. Only latitude and longitude are used for the containment check, as `GeoBoundary` operates in two dimensions.

The following rules apply:

- IATA code must be unique, exactly 3 uppercase letters.
- ICAO code must be unique, exactly 4 uppercase letters.
- The referenced Air Control Area must exist in the system.
- The airport's latitude and longitude must be contained within the Air Control Area's `GeoBoundary`.
- Altitude is stored but not used in boundary validation.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| `IataCode` and `IcaoCode` as Value Objects | Enforces format rules at the domain level. Immutable and self-validating on construction. |
| `Coordinates` as a Value Object (`@Embedded`) | Groups the three spatial attributes together. Keeps `Airport` clean and consistent with the domain model. |
| Containment check via `GeoBoundary.contains()` | Reuses logic already implemented and tested in US050. No duplication. |
| Containment check in the Controller | The controller retrieves the `AirControlArea` and delegates the spatial check to `GeoBoundary`. This keeps the domain model free of cross-aggregate dependencies. |
| `iataCode` as `@EmbeddedId` | The IATA code is the primary identifier for airports in aviation. Used as JPA primary key. |
| Both IATA and ICAO uniqueness enforced | The controller checks both codes independently before persisting. |

---

## 4. Design

### 4.1 Domain Model

```
Airport                     (Aggregate Root)
├── IataCode                (Value Object, EmbeddedId)  — e.g. "LIS"
├── IcaoCode                (Value Object)              — e.g. "LPPT"
├── name                    (String)
├── town                    (String)
├── country                 (String)
├── Coordinates             (Value Object, Embedded)
│   ├── latitude            (double)
│   ├── longitude           (double)
│   └── altitude            (double)
└── airControlArea          (AirControlArea, @ManyToOne)
```

### 4.2 Package Structure

```
aisafe.core/
└── eapli/aisafe/airportmanagement/
    ├── application/
    │   └── RegisterAirportController.java
    ├── domain/
    │   ├── Airport.java
    │   ├── IataCode.java
    │   ├── IcaoCode.java
    │   └── Coordinates.java
    └── repositories/
        └── AirportRepository.java

aisafe.persistence.impl/
└── eapli/aisafe/persistence/impl/jpa/
    └── JpaAirportRepository.java

aisafe.bootstrap/
└── eapli/aisafe/bootstrap/
    └── AirportBootstrap.java

aisafe.app.backoffice.console/
└── eapli/aisafe/app/backoffice/console/presentation/airportmanagement/
    └── RegisterAirportUI.java
```

### 4.3 Registration Sequence

```
BackofficeOperator -> RegisterAirportUI : enter airport data
RegisterAirportUI -> RegisterAirportController : registerAirport(...)

RegisterAirportController -> AirportRepository : findByIataCode(iataCode)
alt IATA code already exists
    RegisterAirportController --> RegisterAirportUI : IllegalStateException
end

RegisterAirportController -> AirportRepository : findByIcaoCode(icaoCode)
alt ICAO code already exists
    RegisterAirportController --> RegisterAirportUI : IllegalStateException
end

RegisterAirportController -> AirControlAreaRepository : findByCode(areaCode)
alt Air Control Area does not exist
    RegisterAirportController --> RegisterAirportUI : IllegalStateException
end

RegisterAirportController -> GeoBoundary : contains(latitude, longitude)
alt coordinates outside boundary
    RegisterAirportController --> RegisterAirportUI : IllegalStateException
end

RegisterAirportController -> Airport : new Airport(...)
RegisterAirportController -> AirportRepository : save(airport)
RegisterAirportController --> RegisterAirportUI : Airport
RegisterAirportUI --> BackofficeOperator : success message
```

### 4.4 Acceptance Tests

**Test 1:** Verifies that an airport with a duplicate IATA code is rejected.
Refers to: US052.1

```java
@Test
void ensureDuplicateIataCodeIsRejected() { ... }
```

**Test 2:** Verifies that an airport with a duplicate ICAO code is rejected.
Refers to: US052.2

```java
@Test
void ensureDuplicateIcaoCodeIsRejected() { ... }
```

**Test 3:** Verifies that registering an airport for a non-existent Air Control Area is rejected.
Refers to: US052.3

```java
@Test
void ensureNonExistentAirControlAreaIsRejected() { ... }
```

**Test 4:** Verifies that an airport with coordinates outside the Air Control Area boundary is rejected.
Refers to: US052.4

```java
@Test
void ensureCoordinatesOutsideBoundaryAreRejected() { ... }
```

**Test 5:** Verifies that an airport with valid data and coordinates inside the boundary is accepted.
Refers to: US052.1 – US052.4

```java
@Test
void ensureValidAirportIsRegistered() { ... }
```

---

## 5. Implementation

### 5.1 Key Classes

#### aisafe.core

- `eapli.aisafe.airportmanagement.domain.Airport`
- `eapli.aisafe.airportmanagement.domain.IataCode`
- `eapli.aisafe.airportmanagement.domain.IcaoCode`
- `eapli.aisafe.airportmanagement.domain.Coordinates`
- `eapli.aisafe.airportmanagement.application.RegisterAirportController`
- `eapli.aisafe.airportmanagement.repositories.AirportRepository`

#### aisafe.persistence.impl

- `eapli.aisafe.persistence.impl.jpa.JpaAirportRepository`

#### aisafe.bootstrap

- `eapli.aisafe.bootstrap.AirportBootstrap`

#### aisafe.app.backoffice.console

- `eapli.aisafe.app.backoffice.console.presentation.airportmanagement.RegisterAirportUI`

### 5.2 Code Validation Rules

| Code | Format | Example |
|---|---|---|
| IATA | Exactly 3 uppercase letters | `LIS`, `OPO`, `FAO` |
| ICAO | Exactly 4 uppercase letters | `LPPT`, `LPPR`, `LPFR` |

Both are validated in their respective value object constructors using a regex match. Any violation throws `IllegalArgumentException` immediately.

### 5.3 Coordinate Containment Check

The controller retrieves the `AirControlArea` and calls:

```java
area.geoBoundary().contains(latitude, longitude)
```

This method was implemented and fully tested as part of US050. The altitude is not used in this check — it is stored for informational purposes only.

If the coordinates fall outside the boundary, an `IllegalStateException` is thrown before the airport is created.

### 5.4 Authorization

The controller enforces that only users with the `BACKOFFICE_OPERATOR` role can register airports:

```java
auth.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);
```

---

## 6. Demonstration

Run the application and log in as Backoffice Operator (`backoffice@aisafe.com` / `Back123`).

Navigate to: **Airport Management → Register Airport**

Enter when prompted:

- IATA Code (e.g. `LIS`)
- ICAO Code (e.g. `LPPT`)
- Name (e.g. `Humberto Delgado Airport`)
- Town (e.g. `Lisbon`)
- Country (e.g. `Portugal`)
- Latitude (e.g. `38.7742`)
- Longitude (e.g. `-9.1342`)
- Altitude in metres (e.g. `113.0`)
- Air Control Area Code (e.g. `LPPC`)

The system confirms success or displays the specific validation error.

### Bootstrap Pre-loaded Airports

| IATA | ICAO | Name | Town | Lat | Lon | Alt | Area |
|---|---|---|---|---|---|---|---|
| `LIS` | `LPPT` | Humberto Delgado Airport | Lisbon | 38.7742 | -9.1342 | 113.0 | `LPPC` |
| `OPO` | `LPPR` | Francisco Sá Carneiro Airport | Porto | 41.2481 | -8.6814 | 69.0 | `LPPC` |
| `FAO` | `LPFR` | Faro Airport | Faro | 37.0144 | -7.9659 | 24.0 | `LPPC` |

---

## 7. Tests

| Class | Tests | What is covered |
|---|---|---|
| `IataCodeTest` | 5 | Format rules, nulls, case insensitivity, equality |
| `IcaoCodeTest` | 5 | Format rules, nulls, case insensitivity, equality |
| `CoordinatesTest` | 4 | Valid construction, invalid latitude/longitude ranges |
| `AirportTest` | 4 | Entity creation, identity, `sameAs()` |
| `RegisterAirportControllerTest` | 7 | Duplicate IATA, duplicate ICAO, missing area, coordinates outside boundary, valid registration |

---

## 8. Observations

The containment logic (`GeoBoundary.contains()`) is entirely owned by US050 and reused here without modification. This means that any future changes to the boundary model (e.g. support for non-rectangular areas) will automatically be reflected in US052 as well.

The `Coordinates` value object stores altitude even though it is not used in boundary validation. This is intentional — altitude may be relevant in future flight planning or approach path calculations.

Future improvements may include:

- A listing UI for registered airports.
- Filtering airports by Air Control Area.
- Validation of country codes against an ISO 3166-1 reference list.