# US055 - Create an Aircraft Model

## 1. Context

This user story belongs to Sprint 2 and is a mandatory requirement for the EAPLI course unit.

The goal is to allow Backoffice Operators to register aircraft models in the system. An aircraft model defines the physical and aerodynamic characteristics used in flight physics calculations and is a foundational entity used by aircraft instances (US070), flight plans (US080), and flight simulation (US085, US100).

### 1.1 List of Issues

- **Analysis:** Understand the aircraft model requirements, physical parameters, engine certification rules, and uniqueness constraints.
- **Design:** Define the domain model for `AircraftModel`, `AircraftModelId`, and `AircraftType`, and the controller/repository structure. Apply domain model changes agreed with the professor (removal of `CertifiedEngineEntry`).
- **Implement:** Implement registration with modelId+maker uniqueness, at least one certified engine model, and physical parameter validation.
- **Test:** Develop unit and integration tests to validate domain rules, value object behaviour, and controller logic.

---

## 2. Requirements

**US055** As a Backoffice Operator, I want to register a new aircraft model. Any model must have at least one engine model (let's assume that all engines are of the same type). Aircraft has a model name and manufacturer, and their combination must be unique. At least one certified engine model must be associated. An aircraft model will have a type, maximum range and other flight characteristics.

This must also be achieved by a bootstrap process.

### Acceptance Criteria

- **US055.1:** The combination of (modelId + makerName) must be unique in the system.
- **US055.2:** Aircraft type must be one of the allowed values: PASSENGER, CARGO, MIXED.
- **US055.3:** At least one certified engine model must be associated at registration time.
- **US055.4:** All certified engines must be of the same type (e.g. all TURBOFAN).
- **US055.5:** All physical parameters must be positive and physically coherent.
- **US055.6:** The system must provide a console UI for Backoffice Operators to register an aircraft model.
- **US055.7:** Registration must also be achievable through a bootstrap process (shared with US056 in `EngineModelBootstrap`).

### Dependencies / References

- **US056** (prerequisite): Engine models must exist before they can be certified for an aircraft model.
- **US070** depends on this US: adding an aircraft to a fleet requires an existing aircraft model.
- **US057** depends on this US: adding an engine model to an aircraft model's certified list.
- **US058** depends on this US: removing an engine model from an aircraft model's certified list.

---

## 3. Analysis

### 3.1 Understanding

An aircraft model represents a type of commercial aircraft produced by a specific manufacturer. It defines the aerodynamic and performance characteristics used throughout the system for flight simulation and validation.

The following rules apply:

- The (modelId + makerName) combination must be unique across the system.
- Aircraft type must be one of the defined categories (PASSENGER, CARGO, MIXED).
- At least one certified engine model must be associated at registration time.
- All certified engines must be of the same `EngineType` — the requirement states "all engines are of the same type".
- The `aspectRatio` may be provided directly or computed from `wingSpan² / wingArea` — both are accepted.

### 3.2 Domain Model Changes (Sprint 2)

Following the professor's recommendations, two changes are applied to the domain model:

| Change | Rationale |
|---|---|
| Remove `CertifiedEngineEntry` | Intermediate entity was unnecessary. `AircraftModel` now holds a direct `@ManyToMany` relationship to `EngineModel`. |
| Add `Aircraft` → `EngineModel` | Allows the system to know the specific engine installed in a given aircraft instance, not just the certified options for its model. This is implemented in US070. |

### 3.3 New Attributes in AircraftModel

The following attributes are added relative to the previous domain model, justified by the physics formulas in section 3.3 of the requirements:

| Attribute | Unit | Justification |
|---|---|---|
| `wingSpan` | m | Required to compute `aspectRatio = wingSpan² / wingArea` |
| `aspectRatio` | — | Used in the drag polar formula: `Cd = Cd0 + Cl² / (π · AR · e)` |
| `oswaldFactor` | — | Efficiency factor `e` in the drag polar formula |
| `VMO` | knots | Maximum operating speed — used for flight validation |
| `MMO` | Mach | Maximum operating Mach — used for flight validation |
| `numberOfEngines` | — | Total thrust = single engine thrust × numberOfEngines |

### 3.4 Key Decisions

| Decision | Rationale |
|---|---|
| `AircraftModelId` as Value Object and `@EmbeddedId` | Encapsulates the uniqueness rule (modelId + makerName) at the domain level. Used as JPA primary key. |
| `AircraftType` as Enum | Restricts classification to the allowed values. |
| `dragCoefficient` as single `double` | The drag coefficient function (Cd vs Mach) is out of scope for this US. A single Cd0 value at cruise is sufficient for registration. Full drag polar will be addressed when flight simulation is implemented. |
| Direct `@ManyToMany` to `EngineModel` | Replaces the removed `CertifiedEngineEntry`. Simpler and sufficient for the domain invariants required. |
| Engine type homogeneity enforced in domain | All certified engines must share the same `EngineType`. Validated in `AircraftModel` on construction and when adding engines (US057). |
| Maker stored as `String makerName` | `Maker` aggregate is not yet implemented. When it is, this can be migrated to a reference. Consistent with `EngineModelId.makerId`. |

---

## 4. Design

### 4.1 Domain Model

```
AircraftModel               (Aggregate Root)
├── AircraftModelId         (Value Object, EmbeddedId)
│   ├── modelId             (String)
│   └── makerName           (String)
├── aircraftType            (AircraftType enum)
├── emptyWeight             (double, kg)
├── MTOW                    (double, kg)
├── MZFW                    (double, kg)
├── maxFuelCapacity         (double, kg)
├── serviceCeiling          (double, ft)
├── cruiseSpeedMach         (double, Mach)
├── wingArea                (double, m²)
├── wingSpan                (double, m)
├── aspectRatio             (double)
├── oswaldFactor            (double)
├── dragCoefficient         (double, Cd0)
├── liftCoefficient         (double, Cl)
├── vmo                     (double, knots)
├── mmo                     (double, Mach)
├── numberOfEngines         (int)
├── maxPassengerCapacity    (int)
├── maxRange                (double, km)
└── certifiedEngines        (Set<EngineModel>)  ← replaces CertifiedEngineEntry
```

### 4.2 Registration Sequence

```
BackofficeOperator -> RegisterAircraftModelUI : enter aircraft model data
RegisterAircraftModelUI -> RegisterAircraftModelController : registerAircraftModel(...)

RegisterAircraftModelController -> AuthService : ensureHasAnyRole(BACKOFFICE_OPERATOR, ADMIN)
alt unauthorized
    RegisterAircraftModelController --> RegisterAircraftModelUI : SecurityException
end

RegisterAircraftModelController -> AircraftModelId : of(modelId, makerName)
alt blank modelId or makerName
    AircraftModelId --> RegisterAircraftModelController : IllegalArgumentException
end

RegisterAircraftModelController -> AircraftModelRepository : findById(id)
alt combination already exists
    RegisterAircraftModelController --> RegisterAircraftModelUI : IllegalStateException
end

RegisterAircraftModelController -> EngineModelRepository : findById(engineId) [for each]
alt engine not found
    RegisterAircraftModelController --> RegisterAircraftModelUI : IllegalStateException
end

RegisterAircraftModelController -> AircraftModel : new AircraftModel(...)
alt invalid parameters or engine type mismatch
    AircraftModel --> RegisterAircraftModelController : IllegalArgumentException
end

RegisterAircraftModelController -> AircraftModelRepository : save(model)
RegisterAircraftModelController --> RegisterAircraftModelUI : AircraftModel
RegisterAircraftModelUI --> BackofficeOperator : success message
```

### 4.3 Acceptance Tests

**Test 1:** Verifies that an aircraft model with a duplicate (modelId + makerName) is rejected.
Refers to: US055.1

```java
@Test
void givenDuplicateModelIdAndMaker_throwsIllegalState() { ... }
```

**Test 2:** Verifies that registration without any engine model is rejected.
Refers to: US055.3

```java
@Test
void givenEmptyEngineList_throwsIllegalArgument() { ... }
```

**Test 3:** Verifies that engines of different types cannot be certified together.
Refers to: US055.4

```java
@Test
void givenEnginesOfDifferentTypes_throwsIllegalArgument() { ... }
```

**Test 4:** Verifies that invalid physical parameters are rejected.
Refers to: US055.5

```java
@Test
void givenZeroEmptyWeight_throwsIllegalArgument() { ... }
```

**Test 5:** Verifies that a valid aircraft model is successfully created and persisted.
Refers to: US055.1, US055.2, US055.3, US055.5

```java
@Test
void givenValidData_aircraftModelIsRegistered() { ... }
```

---

## 5. Implementation

### 5.1 Key Classes

#### aisafe.core

- `eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel`
- `eapli.aisafe.aircraftmodelmanagement.domain.AircraftModelId`
- `eapli.aisafe.aircraftmodelmanagement.domain.AircraftType`
- `eapli.aisafe.aircraftmodelmanagement.application.RegisterAircraftModelController`
- `eapli.aisafe.aircraftmodelmanagement.repositories.AircraftModelRepository`

#### aisafe.persistence.impl

- `eapli.aisafe.persistence.impl.jpa.JpaAircraftModelRepository`

#### aisafe.bootstrap

- `eapli.aisafe.bootstrap.EngineModelBootstrap` *(shared with US056 — loads both engines and aircraft models)*

#### aisafe.app.backoffice.console

- `eapli.aisafe.app.backoffice.console.presentation.aircraftmodelmanagement.RegisterAircraftModelUI`

### 5.2 Physical Parameter Validation

The `AircraftModel` aggregate root validates all invariants on construction:

- `emptyWeight`, `MTOW`, `MZFW`, `maxFuelCapacity`, `wingArea`, `wingSpan`, `maxRange` must be strictly positive.
- `MTOW` must be greater than `emptyWeight`.
- `MZFW` must be less than or equal to `MTOW`.
- `aspectRatio`, `oswaldFactor` must be strictly positive.
- `oswaldFactor` must be ≤ 1.0 (physical limit).
- `dragCoefficient`, `liftCoefficient` must be strictly positive.
- `numberOfEngines` must be ≥ 1.
- `maxPassengerCapacity` must be ≥ 0.
- `certifiedEngines` must have at least one entry.

### 5.3 Engine Type Homogeneity

The `AircraftModel` constructor validates that all engines in `certifiedEngines` share the same `EngineType`:

```java
EngineType expectedType = certifiedEngines.iterator().next().engineType();
for (EngineModel engine : certifiedEngines) {
    if (engine.engineType() != expectedType)
        throw new IllegalArgumentException("All certified engines must be of the same type.");
}
```

### 5.4 Bootstrap Strategy

The `EngineModelBootstrap` is shared across US055 and US056. It receives both repositories and:

1. Loads engine models (US056 data).
2. Loads aircraft models (US055 data), associating the correct certified engines.

This mirrors the approach used in `AirControlAreaBootstrap` for US050 and US052. The constructor signature was updated accordingly:

```java
new EngineModelBootstrap(engineModelRepo, aircraftModelRepo).load();
```

### 5.5 Authorization

The controller enforces that only users with the `BACKOFFICE_OPERATOR` or `ADMIN` role can register aircraft models:

```java
authService.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);
```

---

## 6. Demonstration

Run the application and log in as Backoffice Operator (`backoffice@aisafe.com` / `Back123`).

Navigate to: **Backoffice Config → Register Aircraft Model**

Enter when prompted:

- Model ID (e.g. `777-200ER`)
- Maker Name (e.g. `Boeing`)
- Aircraft Type (e.g. `1` for PASSENGER)
- Empty Weight in kg (e.g. `145149`)
- MTOW in kg (e.g. `347452`)
- MZFW in kg (e.g. `224982`)
- Max Fuel Capacity in kg (e.g. `145538`)
- Service Ceiling in ft (e.g. `43100`)
- Cruise Speed in Mach (e.g. `0.84`)
- Wing Area in m² (e.g. `440`)
- Wing Span in m (e.g. `60.93`)
- Aspect Ratio (e.g. `9.0`)
- Oswald Factor (e.g. `0.87`)
- Drag Coefficient Cd0 (e.g. `0.017`)
- Lift Coefficient Cl (e.g. `0.5`)
- VMO in knots (e.g. `350`)
- MMO in Mach (e.g. `0.89`)
- Number of Engines (e.g. `2`)
- Max Passenger Capacity (e.g. `400`)
- Max Range in km (e.g. `13080`)
- Certified engine model(s) (selected from existing engine models)

The system confirms success or displays the specific validation error.

### Bootstrap Pre-loaded Aircraft Models

| Model ID | Maker | Type | Engines | Engine Model |
|---|---|---|---|---|
| `777-200ER` | `Boeing` | PASSENGER | 2 | GE 90B |
| `A380` | `Airbus` | PASSENGER | 4 | RR Trent 970 |

---

## 7. Tests

| Class | Tests | What is covered |
|---|---|---|
| `AircraftModelTest` | 20 | Entity creation, physical invariants, engine type homogeneity, equality, immutability |
| `AircraftModelIdTest` | 11 | Blank modelId/maker rejection, case-insensitive equality, hashCode, toString |
| `RegisterAircraftModelControllerTest` | 14 | Duplicate rejection, missing/unknown engine, type mismatch, type parsing, authorization |

---

## 8. Observations

The `dragCoefficient` field stores a single Cd0 value at cruise conditions. The full drag polar function (Cd as a function of Mach) is deliberately deferred to when flight simulation (US085, US100) is implemented, following the YAGNI principle and keeping this US focused on its scope.

The `EngineModelBootstrap` is shared with US056 and will be extended for US057 and US058. This mirrors the pattern established by `AirControlAreaBootstrap` for US050 and US052, keeping bootstrap logic cohesive and avoiding duplication.

The `makerName` field is stored as a plain `String` for now. When the `Maker` aggregate is implemented by another team member, this field can be migrated to a reference without breaking the rest of the system.

Future improvements may include:

- Reference to `Maker` aggregate once implemented.
- Full drag polar (Cd vs Mach) when flight simulation is implemented.
- Validation that `wingSpan² / wingArea ≈ aspectRatio` when both are provided.
- Support for mixed engine configurations (currently out of scope per requirements).