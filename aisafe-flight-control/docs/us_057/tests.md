# US057 — Tests and Coverage

## Scope

US057 covers adding an engine model to an aircraft model's list of certified engines
by a Backoffice Operator. The engine type must be compatible with the existing certified
engines. The same engine model cannot be added twice to the same aircraft model.

---

## Automated Tests

### `AircraftModelTest` — `addCertifiedEngine`

Location: `src/test/java/eapli/aisafe/aircraftmodelmanagement/domain/AircraftModelTest.java`

- `addCertifiedEngine_withCompatibleType_succeeds`
- `addCertifiedEngine_withIncompatibleType_throwsIllegalArgument`
- `addCertifiedEngine_duplicate_throwsIllegalState`
- `addCertifiedEngine_null_throwsIllegalArgument`
- `addCertifiedEngine_doesNotAffectOtherInstances`

---

### `AddEngineToAircraftModelControllerTest`

Location: `src/test/java/eapli/aisafe/aircraftmodelmanagement/application/AddEngineToAircraftModelControllerTest.java`

**Happy path**
- `givenCompatibleEngine_engineIsAdded`
- `givenCompatibleEngine_aircraftIsPersisted`
- `givenCompatibleEngine_returnedModelContainsNewEngine`

**Not found**
- `givenNonExistentAircraftModel_throwsIllegalState`
- `givenNonExistentEngineModel_throwsIllegalState`

**Domain invariants**
- `givenIncompatibleEngineType_throwsIllegalArgument`
- `givenDuplicateEngine_throwsIllegalState`

**Authorization**
- `givenUnauthorizedUser_throwsSecurityException`
- `givenUnauthorizedUser_aircraftIsNotModified`

**Listing**
- `allAircraftModels_returnsRegisteredModels`
- `allEngineModels_returnsAllEngines`

---

## Coverage by Acceptance Criterion

- **AC057.1** — Engine is added to the aircraft model's certified list and persisted:
  `givenCompatibleEngine_engineIsAdded`,
  `givenCompatibleEngine_aircraftIsPersisted`,
  `givenCompatibleEngine_returnedModelContainsNewEngine`,
  `addCertifiedEngine_withCompatibleType_succeeds`

- **AC057.2** — Engine type must be compatible with existing certified engines:
  `givenIncompatibleEngineType_throwsIllegalArgument`,
  `addCertifiedEngine_withIncompatibleType_throwsIllegalArgument`

- **AC057.3** — Same engine cannot be added twice:
  `givenDuplicateEngine_throwsIllegalState`,
  `addCertifiedEngine_duplicate_throwsIllegalState`

- **AC057.4** — Aircraft model and engine model must exist:
  `givenNonExistentAircraftModel_throwsIllegalState`,
  `givenNonExistentEngineModel_throwsIllegalState`

- **AC057.5** — Only authorized Backoffice Operators can add engines:
  `givenUnauthorizedUser_throwsSecurityException`,
  `givenUnauthorizedUser_aircraftIsNotModified`

---

## Acceptance Tests

**Manual test — AC057.1 (full flow):**
1. Log in as Backoffice Operator and navigate to `Aircraft Models > Add Engine to Model`.
2. Select model `777-200ER / Boeing` and engine `RR Trent 970 / RR` (TURBOFAN).
3. Expected: confirmation message and engine visible in the model's certified engines list.

**Manual test — AC057.2 (incompatible engine type rejected):**
1. Attempt to add a TURBOPROP engine to a model that already has TURBOFAN engines.
2. Expected: the system rejects the operation with an engine type incompatibility message.

**Manual test — AC057.3 (duplicate engine rejected):**
1. Attempt to add `GE 90B / GE` to a model that already has `GE 90B / GE` certified.
2. Expected: the system rejects the operation indicating the engine is already certified.

**Manual test — AC057.5 (unauthorized user):**
1. Log in without Backoffice Operator role and attempt to add an engine to a model.
2. Expected: access is denied and the model is not modified.