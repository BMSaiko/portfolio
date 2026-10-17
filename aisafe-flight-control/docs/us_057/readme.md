# US057 - Add an Engine Model to an Aircraft Model

## 1. Context

This user story belongs to Sprint 2 and is an extra requirement for the EAPLI course unit.

The goal is to allow Backoffice Operators to add a certified engine model to an existing aircraft model's list of certified engines, expanding the list of engine configurations available for that aircraft variant.

### 1.1 List of Issues

- **Analysis:** Understand the certified engine association rules and compatibility constraints between engine types and existing aircraft model engines.
- **Design:** Define the domain method `addCertifiedEngine()` on `AircraftModel` and the application controller/UI structure.
- **Implement:** Implement the addition with engine type compatibility validation, duplicate detection, and persistence.
- **Test:** Develop unit tests to validate domain rules, controller logic, and authorization.

---

## 2. Requirements

**US057** As a Backoffice Operator, I want to add an engine model to an aircraft model's list of certified engines.

Engine type must be compatible with the engines already certified for the aircraft model. The same engine model cannot be added twice to the same aircraft model.

### Acceptance Criteria

- **US057.1:** The aircraft model must exist in the system.
- **US057.2:** The engine model must exist in the system.
- **US057.3:** The engine type of the new engine must be compatible (same `EngineType`) as the engines already certified for the aircraft model.
- **US057.4:** The same engine model cannot be added twice to the same aircraft model.
- **US057.5:** The system must provide a console UI for Backoffice Operators to perform this operation.

### Dependencies / References

- **US055** (prerequisite): The aircraft model must already be registered.
- **US056** (prerequisite): The engine model must already be registered.
- **US058** depends on this US: removing an engine model from an aircraft model's certified list is the inverse operation.

---

## 3. Analysis

### 3.1 Understanding

An aircraft model may be certified to use more than one engine model, as long as all certified engines share the same `EngineType`. This reflects real-world aviation practice where aircraft variants may be offered with engines from different manufacturers but of the same category (e.g. two different turbofan options for the same airframe).

The following rules apply:

- The aircraft model must already exist.
- The engine model must already exist.
- The engine type of the new engine must match the `EngineType` of the engines already certified for the aircraft model.
- The same engine model (same identity: name + makerId) cannot appear twice in the certified engine list.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| `addCertifiedEngine()` on `AircraftModel` domain entity | Domain invariants (type compatibility, duplicate check) are enforced at the aggregate root level, keeping business rules inside the domain layer. |
| Overloaded `validateEngineTypeHomogeneity()` as instance method | The original static method is reused for construction. A new instance overload checks a single candidate engine against the existing set, avoiding code duplication. |
| Controller fetches both entities before delegating to domain | Ensures clear separation: repository lookups in the application layer, business rule enforcement in the domain layer. |
| `save()` reused for update | The existing `AircraftModelRepository.save()` handles both creation and update, consistent with the pattern established in US055. |

---

## 4. Design

### 4.1 Domain Model (changes to AircraftModel)

```
AircraftModel                   (Aggregate Root)
├── certifiedEngines            (Set<EngineModel>)
│
└── addCertifiedEngine(engine)  (new domain method — US057)
    ├── null check
    ├── duplicate check         (IllegalStateException if already present)
    └── type compatibility      (IllegalArgumentException if EngineType mismatch)
```

### 4.2 Addition Sequence

```
BackofficeOperator -> AddEngineToAircraftModelUI
    : select aircraft model and engine model

AddEngineToAircraftModelUI -> AddEngineToAircraftModelController
    : addEngineToAircraftModel(aircraftModelId, engineModelId)

AddEngineToAircraftModelController -> AuthService
    : ensureHasAnyRole(BACKOFFICE_OPERATOR, ADMIN)
        [unauthorized] --> AddEngineToAircraftModelUI : SecurityException

AddEngineToAircraftModelController -> AircraftModelRepository
    : findById(aircraftModelId)
        [not found] --> AddEngineToAircraftModelUI : IllegalStateException

AddEngineToAircraftModelController -> EngineModelRepository
    : findById(engineModelId)
        [not found] --> AddEngineToAircraftModelUI : IllegalStateException

AddEngineToAircraftModelController -> AircraftModel
    : addCertifiedEngine(engine)
        [duplicate engine]       --> AddEngineToAircraftModelController : IllegalStateException
        [incompatible type]      --> AddEngineToAircraftModelController : IllegalArgumentException

AddEngineToAircraftModelController -> AircraftModelRepository
    : save(aircraft)

AddEngineToAircraftModelController --> AddEngineToAircraftModelUI
    : updated AircraftModel

AddEngineToAircraftModelUI --> BackofficeOperator
    : success message + updated engine list
```

### 4.3 Acceptance Tests

**Test 1:** Verifies that a compatible engine is successfully added.
Refers to: US057.1, US057.2, US057.3

```java
@Test
void givenCompatibleEngine_engineIsAdded() { ... }
```

**Test 2:** Verifies that an engine with an incompatible type is rejected.
Refers to: US057.3

```java
@Test
void givenIncompatibleEngineType_throwsIllegalArgument() { ... }
```

**Test 3:** Verifies that adding a duplicate engine is rejected.
Refers to: US057.4

```java
@Test
void givenDuplicateEngine_throwsIllegalState() { ... }
```

**Test 4:** Verifies that a non-existent aircraft model is rejected.
Refers to: US057.1

```java
@Test
void givenNonExistentAircraftModel_throwsIllegalState() { ... }
```

**Test 5:** Verifies that a non-existent engine model is rejected.
Refers to: US057.2

```java
@Test
void givenNonExistentEngineModel_throwsIllegalState() { ... }
```

---

## 5. Implementation

### 5.1 Key Classes

#### aisafe.core

- `eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel` *(modified)*
- `eapli.aisafe.aircraftmodelmanagement.application.AddEngineToAircraftModelController`
- `eapli.aisafe.aircraftmodelmanagement.repositories.AircraftModelRepository` *(unchanged)*
- `eapli.aisafe.enginemanagement.repositories.EngineModelRepository` *(unchanged)*

#### aisafe.app.backoffice.console

- `eapli.aisafe.app.backoffice.console.presentation.aircraftmodelmanagement.AddEngineToAircraftModelUI`

### 5.2 Domain Method — addCertifiedEngine()

The method enforces two invariants before mutating the aggregate:

```java
public void addCertifiedEngine(final EngineModel engine) {
    if (engine == null)
        throw new IllegalArgumentException("Engine model must not be null.");
    if (certifiedEngines.contains(engine))
        throw new IllegalStateException(
                "Engine model '" + engine.name()
                + "' is already certified for this aircraft.");
    validateEngineTypeHomogeneity(engine);
    certifiedEngines.add(engine);
}
```

The instance overload of `validateEngineTypeHomogeneity` checks the candidate against the existing certified set:

```java
private void validateEngineTypeHomogeneity(final EngineModel candidate) {
    if (certifiedEngines.isEmpty()) return;
    final EngineType existing = certifiedEngines.iterator().next().engineType();
    if (candidate.engineType() != existing)
        throw new IllegalArgumentException(
                "Engine type mismatch. Aircraft model uses " + existing
                + " engines but candidate engine '"
                + candidate.name() + "' is " + candidate.engineType() + ".");
}
```

### 5.3 Authorization

The controller enforces that only users with the `BACKOFFICE_OPERATOR` or `ADMIN` role can perform this operation:

```java
authService.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);
```

### 5.4 Persistence

No new repository method is required. The existing `AircraftModelRepository.save()` persists the updated aggregate, consistent with the pattern from US055.

---

## 6. Demonstration

Run the application and log in as Backoffice Operator (`backoffice@aisafe.com` / `Back1234`).

Navigate to: **Add Engine to Aircraft Model**

The UI presents:

1. A numbered list of all registered aircraft models, showing the count of currently certified engines.
2. The currently certified engines for the selected aircraft model.
3. A numbered list of all registered engine models to choose from.
4. A confirmation summary before submitting.

On success, the updated list of certified engines is displayed.

### Example Interaction

```
=== Add Engine Model to Aircraft Model ===

-- Select Aircraft Model --
  1. 777-200ER (Boeing) | Engines: 1 certified
  2. A380 (Airbus) | Engines: 1 certified

Select aircraft model: 1

  Selected: 777-200ER (Boeing)
  Currently certified engines:
    - GE 90B (GE) [TURBOFAN]

-- Select Engine Model to Add --
  1. GE 90B (GE) [TURBOFAN]
  2. RR Trent 970 (RR) [TURBOFAN]

Select engine model to add: 2

  Summary:
    Aircraft : 777-200ER (Boeing)
    Engine   : RR Trent 970 (RR) [TURBOFAN]

Confirm? (y/n): y

Success! Updated certified engines for 777-200ER (Boeing):
  - GE 90B (GE) [TURBOFAN]
  - RR Trent 970 (RR) [TURBOFAN]
```

---

## 7. Tests

| Class | Tests | What is covered |
|---|---|---|
| `AircraftModelTest` | 5 (added) | `addCertifiedEngine()` happy path, type mismatch, duplicate, null, isolation between instances |
| `AddEngineToAircraftModelControllerTest` | 9 | Happy path, persistence, not found cases, domain invariants via controller, authorization |

---

## 8. Observations

The `addCertifiedEngine()` method is intentionally placed on the `AircraftModel` aggregate root rather than in the application layer. This keeps the engine type compatibility and duplicate detection rules inside the domain, where they belong, and makes them reusable regardless of which application use case triggers the mutation.

The static `validateEngineTypeHomogeneity(Set<EngineModel>)` used in the constructor is preserved unchanged. The new instance overload `validateEngineTypeHomogeneity(EngineModel)` checks a single candidate against the existing certified set, avoiding any code duplication between construction and post-construction mutation.