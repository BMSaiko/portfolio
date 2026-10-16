# US055 — Tests and Coverage

## Scope

US055 covers the registration of a new aircraft model by a Backoffice Operator.
The model name and manufacturer combination must be unique. At least one certified
engine model must be associated, and all certified engines must be of the same type.
The model must include valid aerodynamic and weight flight characteristics.

---

## Automated Tests

### `AircraftModelTest`

Location: `src/test/java/eapli/aisafe/aircraftmodelmanagement/domain/AircraftModelTest.java`

**Construction — happy path**
- `givenValidData_aircraftModelIsCreated`
- `givenMultipleEnginesOfSameType_aircraftModelIsCreated`
- `givenZeroMaxPassengerCapacity_isValid`

**Construction — null guards**
- `givenNullId_throwsIllegalArgument`
- `givenNullAircraftType_throwsIllegalArgument`
- `givenNullCertifiedEngines_throwsIllegalArgument`
- `givenEmptyCertifiedEngines_throwsIllegalArgument`

**Construction — weight invariants**
- `givenZeroEmptyWeight_throwsIllegalArgument`
- `givenMtowLessThanEmptyWeight_throwsIllegalArgument`
- `givenMzfwExceedsMtow_throwsIllegalArgument`

**Construction — aerodynamic invariants**
- `givenOswaldFactorAboveOne_throwsIllegalArgument`
- `givenOswaldFactorZero_throwsIllegalArgument`
- `givenNegativeWingArea_throwsIllegalArgument`

**Construction — engine invariants**
- `givenEnginesOfDifferentTypes_throwsIllegalArgument`
- `givenNumberOfEnginesZero_throwsIllegalArgument`

**Construction — other invariants**
- `givenNegativeMaxPassengerCapacity_throwsIllegalArgument`
- `givenZeroMaxRange_throwsIllegalArgument`

**Equality**
- `twoModelsWithSameId_areEqual`
- `twoModelsWithDifferentId_areNotEqual`
- `model_isNotEqualToNull`
- `equalModels_haveSameHashCode`
- `sameAs_returnsTrueForSameId`
- `sameAs_returnsFalseForNonAircraftModel`

**Immutability**
- `certifiedEngines_returnsUnmodifiableSet`

**toString**
- `toString_containsModelIdAndType`

---

### `RegisterAircraftModelControllerTest`

Location: `src/test/java/eapli/aisafe/aircraftmodelmanagement/application/RegisterAircraftModelControllerTest.java`

**Happy path**
- `givenValidData_aircraftModelIsRegistered`
- `givenValidData_aircraftModelIsPersisted`
- `givenTwoDifferentModels_bothAreRegistered`

**Uniqueness**
- `givenDuplicateModelIdAndMaker_throwsIllegalState`
- `givenDuplicateCaseInsensitive_throwsIllegalState`

**Engine validation**
- `givenNullEngineList_throwsIllegalArgument`
- `givenEmptyEngineList_throwsIllegalArgument`
- `givenNonExistentEngine_throwsIllegalState`
- `givenEnginesOfDifferentTypes_throwsIllegalArgument`

**Aircraft type parsing**
- `givenUnknownAircraftType_throwsIllegalArgument`
- `givenLowerCaseAircraftType_isAccepted`

**Authorization**
- `givenUnauthorizedUser_throwsSecurityException`
- `givenUnauthorizedUser_nothingIsPersisted`

**Listing**
- `allAircraftModels_afterRegistration_returnsOne`
- `allEngineModels_returnsPreLoadedEngines`

---

## Coverage by Acceptance Criterion

- **AC055.1** — (model name + manufacturer) must be unique:
  `givenDuplicateModelIdAndMaker_throwsIllegalState`,
  `givenDuplicateCaseInsensitive_throwsIllegalState`

- **AC055.2** — At least one certified engine model must be associated:
  `givenNullEngineList_throwsIllegalArgument`,
  `givenEmptyEngineList_throwsIllegalArgument`,
  `givenNullCertifiedEngines_throwsIllegalArgument`,
  `givenEmptyCertifiedEngines_throwsIllegalArgument`

- **AC055.3** — All certified engines must be of the same type:
  `givenEnginesOfDifferentTypes_throwsIllegalArgument` (domain + controller),
  `givenNonExistentEngine_throwsIllegalState`

- **AC055.4** — Valid flight characteristics (weights, aerodynamics, capacity, range):
  `givenZeroEmptyWeight_throwsIllegalArgument`,
  `givenMtowLessThanEmptyWeight_throwsIllegalArgument`,
  `givenMzfwExceedsMtow_throwsIllegalArgument`,
  `givenOswaldFactorAboveOne_throwsIllegalArgument`,
  `givenOswaldFactorZero_throwsIllegalArgument`,
  `givenNegativeWingArea_throwsIllegalArgument`,
  `givenNegativeMaxPassengerCapacity_throwsIllegalArgument`,
  `givenZeroMaxRange_throwsIllegalArgument`,
  `givenNumberOfEnginesZero_throwsIllegalArgument`

- **AC055.5** — Only authorized Backoffice Operators can register models:
  `givenUnauthorizedUser_throwsSecurityException`,
  `givenUnauthorizedUser_nothingIsPersisted`

- **AC055.6** — Persistence and bootstrap:
  `givenValidData_aircraftModelIsPersisted`,
  `givenTwoDifferentModels_bothAreRegistered`
  Enforced via bootstrap — no isolated unit test needed.

---

## Acceptance Tests

Authorization and database uniqueness constraints are infrastructure concerns
primarily validated by manual integration testing.
Domain-level field validations are fully covered by the automated unit tests above.

**Manual test — AC055.1 (full registration flow):**
1. Run the backoffice application and log in as a Backoffice Operator.
2. Navigate to `Aircraft Models > Create Aircraft Model`.
3. Provide: model `777-200ER`, maker `Boeing`, type `PASSENGER`, and all required
   flight characteristics (weights, aerodynamics, range, seats, engines).
4. Select at least one certified engine model from the list (e.g. `GE 90B`).
5. Expected: confirmation message displayed and model visible in the aircraft model list.

**Manual test — AC055.1 (duplicate rejected):**
1. After registering `777-200ER / Boeing`, attempt to register the same combination again.
2. Expected: the system rejects the operation with a uniqueness violation message.

**Manual test — AC055.2 (no engine selected):**
1. Fill all fields but do not select any engine model.
2. Expected: the system rejects the operation, indicating at least one engine is required.

**Manual test — AC055.3 (incompatible engine types):**
1. Attempt to associate two engine models of different types (e.g. TURBOFAN + TURBOPROP).
2. Expected: the system rejects the operation, indicating all engines must be of the same type.

**Manual test — AC055.5 (unauthorized user):**
1. Log in as a user without the Backoffice Operator role.
2. Attempt to access the `Create Aircraft Model` option.
3. Expected: access is denied and no model is registered.