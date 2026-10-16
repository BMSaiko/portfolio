# US056 - Create an Aircraft Engine Model

## 1. Context

This user story belongs to Sprint 2 and is a mandatory requirement for the EAPLI course unit.

The goal is to allow Backoffice Operators to register aircraft engine models in the system. Engine models are a foundational entity used by aircraft models (US055), which must have at least one certified engine model associated.

### 1.1 List of Issues

- **Analysis:** Understand the engine model requirements, physical parameters, and uniqueness constraints.
- **Design:** Define the domain model for `EngineModel`, `EngineModelId`, and `EngineType`, and the controller/repository structure.
- **Implement:** Implement registration with name+maker uniqueness, physical parameter validation, and engine type classification.
- **Test:** Develop unit tests to validate domain rules, value object behaviour, and controller logic.

---

## 2. Requirements

**US056** As a Backoffice Operator, I want to register a new engine model to be used by aircraft. The model's name and manufacturer combination must be unique. Other important information regarding the type (see section 3.2), power, fuel, and efficiency should be included.

This must also be achievable by a bootstrap process.

### Acceptance Criteria

- **US056.1:** The combination of (name + maker) must be unique in the system.
- **US056.2:** Engine type must be one of the allowed values: TURBOPROP, TURBOFAN, TURBOJET, RAMJET, ELECTRIC_PROPELLER.
- **US056.3:** All physical parameters (thrust, TSFC, lapse-rate factor) must be positive and physically coherent (cruise thrust cannot exceed standstill thrust).
- **US056.4:** The system must provide a console UI for Backoffice Operators to register an engine model.
- **US056.5:** Registration must also be achievable through a bootstrap process.

### Dependencies / References

- **US030** (prerequisite): Authentication and authorization must be in place so that only Backoffice Operators can access this functionality.
- **US055** depends on this US: an aircraft model must have at least one certified engine model associated. The engine model must exist before it can be certified for an aircraft model.
- **US057** depends on this US: adding an engine model to an aircraft model's certified list requires the engine model to already be registered.

---

## 3. Analysis

### 3.1 Understanding

An engine model represents a type of aircraft engine produced by a specific manufacturer. It defines the performance characteristics used in flight physics calculations: thrust at different speeds, fuel efficiency (TSFC), and how thrust degrades with altitude (lapse-rate factor).

The following rules apply:

- The (name + makerId) combination must be unique across the system.
- Engine type must be one of the defined categories from section 3.2 of the requirements.
- Thrust at cruise speed cannot exceed thrust at standstill — physically, engines produce maximum thrust when stationary.
- TSFC must be positive — zero or negative fuel consumption is not physically meaningful.
- The lapse-rate factor governs how thrust decreases with altitude: `T_alt = T_sea * (rho / rho0) ^ lapseRateFactor`.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| `EngineModelId` as a Value Object and `@EmbeddedId` | Encapsulates the uniqueness rule (name + makerId) at the domain level. Used as the JPA primary key, avoiding a surrogate key. |
| `EngineType` as an Enum | Restricts engine classification to the allowed values defined in the requirements. `fromString()` provides case-insensitive parsing for UI input. |
| Physical coherence validated in `EngineModel` constructor | Domain invariants (cruise thrust ≤ standstill thrust, positive TSFC) are enforced at construction time, making it impossible to create an invalid engine model. |
| `thrustAtAltitude(airDensity)` method on `EngineModel` | Encapsulates the lapse-rate physics formula inside the domain entity, making it available for flight simulation (US085, US100) without coupling to the application layer. |
| `allEngineModels()` exposed in controller | Allows US055 to retrieve available engines for association when registering an aircraft model, without bypassing the application layer. |
| Bootstrap support | Ensures the system can be started with pre-loaded engine models matching the provided XML test data. |

---

## 4. Design

### 4.1 Domain Model

```
EngineModel             (Aggregate Root)
├── EngineModelId       (Value Object, EmbeddedId)
│   ├── name            (String)
│   └── makerId         (String)
├── engineType          (EngineType enum)
├── thrustAtStandstill  (double, N)
├── thrustAtCruiseSpeed (double, N)
├── cruiseSpeedMach     (double, Mach)
├── tsfc                (double, N/N/s SI)
└── lapseRateFactor     (double)
```

### 4.2 Registration Sequence

```
BackofficeOperator -> RegisterEngineModelUI : enter engine model data
RegisterEngineModelUI -> RegisterEngineModelController : registerEngineModel(...)

RegisterEngineModelController -> AuthService : ensureHasAnyRole(BACKOFFICE_OPERATOR, ADMIN)
alt unauthorized
    RegisterEngineModelController --> RegisterEngineModelUI : SecurityException
end

RegisterEngineModelController -> EngineModelId : of(name, makerId)
alt blank name or makerId
    EngineModelId --> RegisterEngineModelController : IllegalArgumentException
end

RegisterEngineModelController -> EngineModelRepository : findById(id)
alt combination already exists
    RegisterEngineModelController --> RegisterEngineModelUI : IllegalStateException
end

RegisterEngineModelController -> EngineType : fromString(engineType)
alt unknown engine type
    EngineType --> RegisterEngineModelController : IllegalArgumentException
end

RegisterEngineModelController -> EngineModel : new EngineModel(...)
alt invalid physical parameters
    EngineModel --> RegisterEngineModelController : IllegalArgumentException
end

RegisterEngineModelController -> EngineModelRepository : save(model)
RegisterEngineModelController --> RegisterEngineModelUI : EngineModel
RegisterEngineModelUI --> BackofficeOperator : success message
```

### 4.3 Acceptance Tests

**Test 1:** Verifies that an engine model with a duplicate (name + maker) combination is rejected.
Refers to: US056.1

```java
@Test
void ensureDuplicateNameAndMakerCombinationIsRejected() { ... }
```

**Test 2:** Verifies that an unknown engine type string is rejected.
Refers to: US056.2

```java
@Test
void ensureUnknownEngineTypeIsRejected() { ... }
```

**Test 3:** Verifies that cruise thrust exceeding standstill thrust is rejected.
Refers to: US056.3

```java
@Test
void ensureCruiseThrustCannotExceedStandstillThrust() { ... }
```

**Test 4:** Verifies that negative or zero TSFC is rejected.
Refers to: US056.3

```java
@Test
void ensureNegativeOrZeroTsfcIsRejected() { ... }
```

**Test 5:** Verifies that a valid engine model is successfully created.
Refers to: US056.1, US056.2, US056.3

```java
@Test
void ensureValidEngineModelIsCreated() { ... }
```

---

## 5. Implementation

### 5.1 Key Classes

#### aisafe.core

- `eapli.aisafe.enginemanagement.domain.EngineModel`
- `eapli.aisafe.enginemanagement.domain.EngineModelId`
- `eapli.aisafe.enginemanagement.domain.EngineType`
- `eapli.aisafe.enginemanagement.application.RegisterEngineModelController`
- `eapli.aisafe.enginemanagement.repositories.EngineModelRepository`

#### aisafe.persistence.impl

- `eapli.aisafe.persistence.impl.jpa.JpaEngineModelRepository`

#### aisafe.bootstrap

- `eapli.aisafe.bootstrap.EngineModelBootstrap`

#### aisafe.app.backoffice.console

- `eapli.aisafe.app.backoffice.console.presentation.enginemanagement.RegisterEngineModelUI`

### 5.2 Physical Parameter Validation

The `EngineModel` aggregate root validates all invariants on construction:

- `thrustAtStandstill` must be strictly positive.
- `thrustAtCruiseSpeed` must be strictly positive and must not exceed `thrustAtStandstill`.
- `cruiseSpeedMach` must be strictly positive.
- `tsfc` must be strictly positive.
- `lapseRateFactor` must not be negative.

Any violation throws `IllegalArgumentException` immediately, before the entity is persisted.

### 5.3 Altitude Thrust Calculation

The `EngineModel` entity encapsulates the lapse-rate formula used during flight simulation:

```
T_altitude = T_sea_level * (rho / rho0) ^ lapseRateFactor
```

Where `rho0 = 1.225 kg/m³` (ISA sea level air density). This method is implemented directly on the domain entity so that it is available to any component that needs thrust calculations, including flight simulation and flight plan validation.

### 5.4 Uniqueness via EmbeddedId

The `EngineModelId` value object encapsulates both `name` and `makerId`. It is used as the JPA `@EmbeddedId`, meaning uniqueness is enforced both at the domain level (equality check before save) and at the database level (composite primary key). Name comparison is case-insensitive.

### 5.5 Authorization

The controller enforces that only users with the `BACKOFFICE_OPERATOR` or `ADMIN` role can register engine models:

```java
authService.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);
```

---

## 6. Demonstration

Run the application and log in as Backoffice Operator (`backoffice@aisafe.com` / `Back123`).

Navigate to: **Backoffice Config → Create Engine Model**

Enter when prompted:

- Engine Model Name (e.g. `GE 90B`)
- Maker ID (e.g. `GE`)
- Engine Type (select from list, e.g. `2` for TURBOFAN)
- Thrust at standstill / sea level in N (e.g. `489000`)
- Thrust at cruise speed in N (e.g. `300000`)
- Cruise speed in Mach (e.g. `0.84`)
- TSFC in N/N/s (e.g. `1.6E-4`)
- Lapse-rate factor (e.g. `1.0`)

The system confirms success or displays the specific validation error.

### Bootstrap Pre-loaded Engine Models

The following engine models are pre-loaded to match the provided XML aircraft test data:

| Name | Maker | Type | Thrust SL (N) | Thrust Cruise (N) | Cruise Mach | TSFC | Lapse Factor |
|---|---|---|---|---|---|---|---|
| `GE 90B` | `GE` | TURBOFAN | 489 000 | 300 000 | 0.84 | 1.6E-4 | 1.0 |
| `RR Trent 970` | `RR` | TURBOFAN | 348 310 | 180 000 | 0.85 | 1.6E-4 | 0.96 |

---

## 7. Tests

| Class | Tests | What is covered |
|---|---|---|
| `EngineModelTest` | 13 | Entity creation, physical invariants, equality, `thrustAtAltitude()` |
| `EngineModelIdTest` | 4 | Blank name/maker rejection, case-insensitive equality, `toString()` |
| `EngineTypeTest` | 3 | `fromString()` case-insensitivity, unknown value rejection |

---

## 8. Observations

The `thrustAtAltitude(double airDensity)` method is already implemented and tested as part of this US. It is not used directly during registration but is intentionally prepared for US085 (test/validate flight plan) and US100 (simulate flights), where thrust at cruise altitude must be computed from sea-level specifications.

The `allEngineModels()` method exposed by `RegisterEngineModelController` is used by US055 when the operator selects certified engine models for a new aircraft model.

Future improvements may include:

- Support for engines with multiple thrust ratings (e.g. different de-rating levels).
- Thrust curves beyond the two-point linear interpolation currently used.
- Validation that the maker ID references a registered `Maker` entity (once US — Create aircraft/engine maker — is implemented).