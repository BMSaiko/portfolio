# US058 — Tests and Coverage

## Scope

US058 covers removing an engine model from an aircraft model's list of certified engines
by a Backoffice Operator. Removal is blocked if any actual aircraft instances use that
aircraft model. The aircraft model must always retain at least one certified engine.

---

## Automated Tests

### `RemoveEngineFromAircraftModelControllerTest`

Location: `src/test/java/eapli/aisafe/aircraftmodelmanagement/application/RemoveEngineFromAircraftModelControllerTest.java`

**Happy path**
- `givenTwoEngines_noAircraft_engineIsRemoved`
- `givenTwoEngines_noAircraft_correctEngineRemains`
- `givenTwoEngines_noAircraft_saveIsCalled`

**Aircraft block**
- `givenAircraftUsingModel_throwsIllegalState`
- `givenAircraftUsingModel_saveIsNeverCalled`

**Domain invariants**
- `givenLastEngine_removalThrowsIllegalState`
- `givenEngineNotCertified_throwsIllegalState`

**Not found**
- `givenNonExistentAircraftModel_throwsIllegalState`
- `givenNonExistentEngineModel_throwsIllegalState`

**Authorization**
- `givenUnauthorizedUser_throwsSecurityException`
- `givenUnauthorizedUser_saveIsNeverCalled`

---

## Coverage by Acceptance Criterion

- **AC058.1** — Engine is removed and aircraft model is persisted:
  `givenTwoEngines_noAircraft_engineIsRemoved`,
  `givenTwoEngines_noAircraft_correctEngineRemains`,
  `givenTwoEngines_noAircraft_saveIsCalled`

- **AC058.2** — Removal is blocked if actual aircraft use the model:
  `givenAircraftUsingModel_throwsIllegalState`,
  `givenAircraftUsingModel_saveIsNeverCalled`

- **AC058.3** — Aircraft model must retain at least one certified engine:
  `givenLastEngine_removalThrowsIllegalState`

- **AC058.4** — Engine must be certified on the model to be removed:
  `givenEngineNotCertified_throwsIllegalState`

- **AC058.5** — Aircraft model and engine model must exist:
  `givenNonExistentAircraftModel_throwsIllegalState`,
  `givenNonExistentEngineModel_throwsIllegalState`

- **AC058.6** — Only authorized Backoffice Operators can remove engines:
  `givenUnauthorizedUser_throwsSecurityException`,
  `givenUnauthorizedUser_saveIsNeverCalled`

---

## Acceptance Tests

**Manual test — AC058.1 (full flow):**
1. Log in as Backoffice Operator and navigate to `Aircraft Models > Remove Engine from Model`.
2. Select model `777-200ER / Boeing` (with two certified engines) and choose `RR Trent 970 / RR`.
3. Expected: confirmation message and engine no longer visible in the certified engines list.

**Manual test — AC058.2 (blocked by active aircraft):**
1. Ensure at least one aircraft instance exists using model `777-200ER / Boeing`.
2. Attempt to remove any engine from that model.
3. Expected: the system rejects the operation indicating active aircraft prevent the change.

**Manual test — AC058.3 (last engine cannot be removed):**
1. With a model that has only one certified engine, attempt to remove it.
2. Expected: the system rejects the operation indicating at least one engine must remain.

**Manual test — AC058.4 (engine not certified):**
1. Attempt to remove an engine that is not in the model's certified list.
2. Expected: the system rejects the operation with an appropriate message.

**Manual test — AC058.6 (unauthorized user):**
1. Log in without Backoffice Operator role and attempt to remove an engine from a model.
2. Expected: access is denied and the model is not modified.