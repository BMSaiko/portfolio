# US058 - Remove an Engine Model from an Aircraft Model

## 1. Context

This user story belongs to Sprint 2 and is an extra requirement for the EAPLI course unit.

The goal is to allow Backoffice Operators to remove a certified engine model from an existing aircraft model's list of certified engines, subject to safety and integrity constraints.

### 1.1 List of Issues

- **Analysis:** Understand the removal constraints — at least one engine must remain, and removal is blocked if actual aircraft instances use the model.
- **Design:** Define the domain method `removeCertifiedEngine()` on `AircraftModel`, extend `AircraftRepository` with `existsByAircraftModelId()`, and define the controller/UI structure.
- **Implement:** Implement the removal with last-engine protection, actual-aircraft check, and persistence.
- **Test:** Develop unit tests to validate domain rules, controller logic, and authorization.

---

## 2. Requirements

**US058** As a Backoffice Operator, I want to remove an engine model from an aircraft model's list of certified engines. This is not possible if there are actual aircraft using that model. The aircraft model must retain at least one certified engine model.

### Acceptance Criteria

- **US058.1:** The aircraft model must exist in the system.
- **US058.2:** The engine model must be currently certified for the aircraft model.
- **US058.3:** Removal is not permitted if any actual `Aircraft` instance references this aircraft model.
- **US058.4:** The aircraft model must retain at least one certified engine after removal.
- **US058.5:** The system must provide a console UI for Backoffice Operators to perform this operation.

### Dependencies / References

- **US055** (prerequisite): The aircraft model must already be registered.
- **US056** (prerequisite): The engine model must already be registered.
- **US057** (related): Adding an engine to an aircraft model is the inverse operation.
- **US070** (related): The `Aircraft` entity created by this US is the one checked in US058.3.

---

## 3. Analysis

### 3.1 Understanding

Removing a certified engine from an aircraft model is a destructive operation with two hard constraints:

- The aircraft model must always retain at least one certified engine — an aircraft model with no certified engines is not physically meaningful.
- If any actual `Aircraft` instance (a concrete aircraft registered in the fleet) references this aircraft model, the certified engine list must not be modified. Changing it retroactively would invalidate the configuration of real aircraft in service.

The following rules apply:

- The aircraft model must already exist.
- The engine model must be currently certified for the selected aircraft model.
- No `Aircraft` instance may reference the aircraft model at the time of removal.
- After removal, at least one certified engine must remain.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| `removeCertifiedEngine()` on `AircraftModel` domain entity | Domain invariants (last-engine check, membership check) are enforced at the aggregate root, keeping business rules inside the domain layer. |
| Actual-aircraft check in the application layer (controller) | Requires repository access, which belongs to the application layer, not the domain. The domain method handles only pure structural invariants. |
| `existsByAircraftModelId()` added to `AircraftRepository` | Minimal contract addition — the controller only needs to know if any aircraft exists for the model, not retrieve them. |
| UI early-exit when only one engine is certified | Improves UX by preventing the operator from selecting the last engine and reaching an inevitable error. The domain still enforces the invariant as a safety net. |
| `save()` reused for update | The existing `AircraftModelRepository.save()` handles both creation and update, consistent with the pattern established in US055–US057. |

---

## 4. Design

### 4.1 Domain Model (changes to AircraftModel)

```
AircraftModel                      (Aggregate Root)
├── certifiedEngines               (Set<EngineModel>)
│
└── removeCertifiedEngine(engine)  (new domain method — US058)
    ├── null check
    ├── membership check           (IllegalStateException if not certified)
    └── last-engine guard          (IllegalStateException if only one remains)
```

### 4.2 Repository Extension

```
AircraftRepository
└── existsByAircraftModelId(AircraftModelId id) : boolean   (new method — US058)
```

### 4.3 Removal Sequence

```
BackofficeOperator -> RemoveEngineFromAircraftModelUI
    : select aircraft model and engine to remove

RemoveEngineFromAircraftModelUI -> RemoveEngineFromAircraftModelController
    : removeEngineFromAircraftModel(aircraftModelId, engineModelId)

RemoveEngineFromAircraftModelController -> AuthService
    : ensureHasAnyRole(BACKOFFICE_OPERATOR, ADMIN)
        [unauthorized] --> RemoveEngineFromAircraftModelUI : SecurityException

RemoveEngineFromAircraftModelController -> AircraftModelRepository
    : findById(aircraftModelId)
        [not found] --> RemoveEngineFromAircraftModelUI : IllegalStateException

RemoveEngineFromAircraftModelController -> AircraftRepository
    : existsByAircraftModelId(aircraftModelId)
        [aircraft exist] --> RemoveEngineFromAircraftModelUI : IllegalStateException

RemoveEngineFromAircraftModelController -> EngineModelRepository
    : findById(engineModelId)
        [not found] --> RemoveEngineFromAircraftModelUI : IllegalStateException

RemoveEngineFromAircraftModelController -> AircraftModel
    : removeCertifiedEngine(engine)
        [not certified]      --> RemoveEngineFromAircraftModelController : IllegalStateException
        [last engine]        --> RemoveEngineFromAircraftModelController : IllegalStateException

RemoveEngineFromAircraftModelController -> AircraftModelRepository
    : save(aircraft)

RemoveEngineFromAircraftModelController --> RemoveEngineFromAircraftModelUI
    : updated AircraftModel

RemoveEngineFromAircraftModelUI --> BackofficeOperator
    : success message + updated engine list
```

### 4.4 Acceptance Tests

**Test 1:** Verifies that a certified engine is successfully removed when more than one is certified and no actual aircraft exist.
Refers to: US058.1, US058.2, US058.4

```java
@Test
void givenMultipleEngines_noAircraft_engineIsRemoved() { ... }
```

**Test 2:** Verifies that removing the last certified engine is rejected.
Refers to: US058.4

```java
@Test
void givenLastEngine_removalThrowsIllegalState() { ... }
```

**Test 3:** Verifies that removal is blocked when actual aircraft use the model.
Refers to: US058.3

```java
@Test
void givenAircraftUsingModel_removalThrowsIllegalState() { ... }
```

**Test 4:** Verifies that removing an engine not certified for the model is rejected.
Refers to: US058.2

```java
@Test
void givenEngineNotCertified_removalThrowsIllegalState() { ... }
```

---

## 5. Implementation

### 5.1 Key Classes

#### aisafe.core

- `eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel` *(modified)*
- `eapli.aisafe.aircraftmodelmanagement.application.RemoveEngineFromAircraftModelController`
- `eapli.aisafe.aircraftmodelmanagement.repositories.AircraftModelRepository` *(unchanged)*
- `eapli.aisafe.enginemanagement.repositories.EngineModelRepository` *(unchanged)*
- `eapli.aisafe.fleetmanagement.repositories.AircraftRepository` *(extended)*

#### aisafe.persistence.impl

- `eapli.aisafe.persistence.impl.jpa.JpaAircraftRepository` *(extended)*

#### aisafe.app.backoffice.console

- `eapli.aisafe.app.backoffice.console.presentation.aircraftmodelmanagement.RemoveEngineFromAircraftModelUI`

### 5.2 Domain Method — removeCertifiedEngine()

The method enforces two invariants before mutating the aggregate:

```java
public void removeCertifiedEngine(final EngineModel engine) {
    if (engine == null)
        throw new IllegalArgumentException("Engine model must not be null.");
    if (!certifiedEngines.contains(engine))
        throw new IllegalStateException(
                "Engine model '" + engine.name()
                        + "' is not certified for this aircraft model.");
    if (certifiedEngines.size() == 1)
        throw new IllegalStateException(
                "Cannot remove the last certified engine. "
                        + "An aircraft model must retain at least one certified engine.");
    certifiedEngines.remove(engine);
}
```

### 5.3 Repository Extension — existsByAircraftModelId()

Added to `AircraftRepository` interface and implemented in `JpaAircraftRepository`:

```java
// JpaAircraftRepository
@Override
public boolean existsByAircraftModelId(AircraftModelId id) {
    final var q = entityManager().createQuery(
            "SELECT COUNT(a) FROM Aircraft a WHERE a.model.id = :id", Long.class);
    q.setParameter("id", id);
    return q.getSingleResult() > 0;
}
```

### 5.4 Authorization

The controller enforces that only users with the `BACKOFFICE_OPERATOR` or `ADMIN` role can perform this operation:

```java
authService.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN);
```

### 5.5 Actual-Aircraft Check

The check is performed in the application layer before delegating to the domain:

```java
if (aircraftRepository.existsByAircraftModelId(aircraftModelId))
    throw new IllegalStateException(
            "Cannot modify certified engines: there are actual aircraft "
                    + "registered under model '" + aircraftModelId + "'.");
```

---

## 6. Demonstration

Run the application and log in as Backoffice Operator (`backoffice@aisafe.com` / `Back1234`).

Navigate to: **Remove Engine from Aircraft Model**

The UI presents:

1. A numbered list of all registered aircraft models with their certified engine count.
2. The currently certified engines for the selected aircraft model.
3. If only one engine is certified, the UI exits early with a warning.
4. A confirmation summary before submitting.

On success, the updated list of certified engines is displayed.

### Example Interaction

```
=== Remove Engine Model from Aircraft Model ===

-- Select Aircraft Model --
  1. A380 (Airbus) | Engines: 1 certified
  2. 777-200ER (Boeing) | Engines: 2 certified

Select aircraft model: 2

  Selected: 777-200ER (Boeing)
  Currently certified engines:
    1. GE 90B (GE) [TURBOFAN]
    2. RR Trent 970 (RR) [TURBOFAN]

Select engine to remove: 2

  Summary:
    Aircraft : 777-200ER (Boeing)
    Remove   : RR Trent 970 (RR) [TURBOFAN]

Confirm? (y/n): y

Success! Updated certified engines for 777-200ER (Boeing):
  - GE 90B (GE) [TURBOFAN]
```

---

## 7. Tests

| Class | Tests | What is covered |
|---|---|---|
| `AircraftModelTest` | 3 (added) | `removeCertifiedEngine()` happy path, last-engine guard, engine not certified |
| `RemoveEngineFromAircraftModelControllerTest` | 9 | Happy path, persistence, not found cases, last-engine guard, actual-aircraft block, authorization |

---

## 8. Observations

The actual-aircraft check is deliberately placed in the application layer (`RemoveEngineFromAircraftModelController`) rather than in the domain. The domain entity `AircraftModel` has no knowledge of repositories, which is correct. The controller orchestrates the repository lookup and delegates structural invariant enforcement to the domain.

The UI provides an early-exit warning when only one engine is certified, preventing the operator from reaching an inevitable domain error. The domain still enforces the last-engine invariant as a safety net regardless of how the operation is triggered.

The `existsByAircraftModelId()` method added to `AircraftRepository` and implemented in `JpaAircraftRepository` uses a JPQL COUNT query navigating the `a.model.id` path, which resolves correctly because `AircraftModel` uses `AircraftModelId` as `@EmbeddedId`.