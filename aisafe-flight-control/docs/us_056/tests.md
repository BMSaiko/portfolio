# US056 — Tests and Coverage

## Scope

US056 covers the registration of a new aircraft engine model by a Backoffice Operator.
The engine name and manufacturer combination must be unique. The model must include
valid physical parameters (thrust, TSFC, lapse-rate factor, cruise speed) and a
fuel type selected from the pre-defined list.

---

## Automated Tests

### `EngineModelTest`

Location: `src/test/java/eapli/aisafe/enginemanagement/domain/EngineModelTest.java`

**Construction — happy path**
- `givenValidData_engineModelIsCreated`
- `givenLapseFactorZero_engineModelIsCreated`
- `givenAllEngineTypes_engineModelIsCreated`
- `givenAllFuelTypes_engineModelIsCreated`

**Construction — null guards**
- `givenNullId_throwsIllegalArgument`
- `givenNullEngineType_throwsIllegalArgument`
- `givenNullFuelType_throwsIllegalArgument`

**Construction — thrust invariants**
- `givenZeroThrustAtStandstill_throwsIllegalArgument`
- `givenNegativeThrustAtStandstill_throwsIllegalArgument`
- `givenZeroThrustAtCruise_throwsIllegalArgument`
- `givenNegativeThrustAtCruise_throwsIllegalArgument`
- `givenCruiseThrustExceedsStandstillThrust_throwsIllegalArgument`
- `givenCruiseThrustEqualToStandstillThrust_throwsIllegalArgument`

**Construction — other parameter invariants**
- `givenZeroCruiseMach_throwsIllegalArgument`
- `givenNegativeTsfc_throwsIllegalArgument`
- `givenZeroTsfc_throwsIllegalArgument`
- `givenNegativeLapseFactor_throwsIllegalArgument`

**Equality**
- `twoEnginesWithSameId_areEqual`
- `twoEnginesWithDifferentId_areNotEqual`
- `engine_isNotEqualToNull`
- `equalEngines_haveSameHashCode`
- `sameAs_returnsTrueForSameId`

**Physics — thrustAtAltitude**
- `thrustAtSeaLevelDensity_equalsStandstillThrust`
- `thrustAtHighAltitude_isLessThanAtSeaLevel`
- `thrustAtHighAltitude_isPositive`
- `thrustWithLapseFactorZero_isAlwaysStandstillThrust`

**toString**
- `toString_containsNameTypeAndFuel`

---

### `RegisterEngineModelControllerTest`

Location: `src/test/java/eapli/aisafe/enginemanagement/application/RegisterEngineModelControllerTest.java`

**Happy path**
- `givenValidData_engineModelIsRegistered`
- `givenValidData_engineModelIsPersisted`
- `givenTwoDifferentModels_bothAreRegistered`
- `givenSameNameDifferentMaker_bothAreRegistered`

**Uniqueness**
- `givenDuplicateNameAndMaker_throwsIllegalState`
- `givenDuplicateNameCaseInsensitive_throwsIllegalState`

**Engine type parsing**
- `givenLowerCaseEngineType_isAccepted`
- `givenUnknownEngineType_throwsIllegalArgument`

**Fuel type**
- `givenAllFuelTypes_engineModelIsRegistered`
- `givenNullFuelType_throwsIllegalArgument`

**Physical parameter validation**
- `givenCruiseThrustExceedsStandstill_throwsIllegalArgument`
- `givenNegativeTsfc_throwsIllegalArgument`
- `givenNegativeLapseFactor_throwsIllegalArgument`

**Authorization**
- `givenUnauthorizedUser_throwsSecurityException`
- `givenUnauthorizedUser_nothingIsPersisted`

**Listing**
- `allEngineModels_afterRegistration_returnsOne`
- `allFuelTypes_returnsAllDefinedFuels`

---

## Coverage by Acceptance Criterion

- **AC056.1** — (engine name + manufacturer) must be unique:
  `givenDuplicateNameAndMaker_throwsIllegalState`,
  `givenDuplicateNameCaseInsensitive_throwsIllegalState`,
  `givenSameNameDifferentMaker_bothAreRegistered`

- **AC056.2** — Engine type must be valid and parseable:
  `givenLowerCaseEngineType_isAccepted`,
  `givenUnknownEngineType_throwsIllegalArgument`,
  `givenAllEngineTypes_engineModelIsCreated`

- **AC056.3** — Fuel type must be selected from the pre-defined list:
  `givenAllFuelTypes_engineModelIsRegistered`,
  `givenAllFuelTypes_engineModelIsCreated`,
  `givenNullFuelType_throwsIllegalArgument`,
  `allFuelTypes_returnsAllDefinedFuels`

- **AC056.4** — Physical parameters (thrust, TSFC, lapse-rate, cruise speed) must be valid:
  `givenZeroThrustAtStandstill_throwsIllegalArgument`,
  `givenNegativeThrustAtStandstill_throwsIllegalArgument`,
  `givenZeroThrustAtCruise_throwsIllegalArgument`,
  `givenNegativeThrustAtCruise_throwsIllegalArgument`,
  `givenCruiseThrustExceedsStandstillThrust_throwsIllegalArgument`,
  `givenCruiseThrustEqualToStandstillThrust_throwsIllegalArgument`,
  `givenZeroCruiseMach_throwsIllegalArgument`,
  `givenNegativeTsfc_throwsIllegalArgument`,
  `givenZeroTsfc_throwsIllegalArgument`,
  `givenNegativeLapseFactor_throwsIllegalArgument`,
  `givenCruiseThrustExceedsStandstill_throwsIllegalArgument` (controller)

- **AC056.5** — Thrust lapse-rate formula produces physically coherent results:
  `thrustAtSeaLevelDensity_equalsStandstillThrust`,
  `thrustAtHighAltitude_isLessThanAtSeaLevel`,
  `thrustAtHighAltitude_isPositive`,
  `thrustWithLapseFactorZero_isAlwaysStandstillThrust`

- **AC056.6** — Only authorized Backoffice Operators can register engine models:
  `givenUnauthorizedUser_throwsSecurityException`,
  `givenUnauthorizedUser_nothingIsPersisted`

- **AC056.7** — Persistence and bootstrap:
  `givenValidData_engineModelIsPersisted`,
  `givenTwoDifferentModels_bothAreRegistered`
  Enforced via bootstrap — no isolated unit test needed.

---

## Acceptance Tests

Authorization and database uniqueness constraints are infrastructure concerns
primarily validated by manual integration testing.
Domain-level field validations are fully covered by the automated unit tests above.

**Manual test — AC056.1 (full registration flow):**
1. Run the backoffice application and log in as a Backoffice Operator.
2. Navigate to `Engine Models > Create Engine Model`.
3. Provide: name `GE 90B`, maker `GE`, type `TURBOFAN`, thrust at standstill `489000 N`,
   thrust at cruise `300000 N`, cruise speed `Mach 0.84`, TSFC `1.6e-4`, lapse factor `1.0`,
   fuel type `JET A-1`.
4. Expected: confirmation message displayed and engine visible in the engine model list.

**Manual test — AC056.1 (duplicate rejected):**
1. After registering `GE 90B / GE`, attempt to register the same combination again.
2. Expected: the system rejects the operation with a uniqueness violation message.

**Manual test — AC056.1 (same name, different maker accepted):**
1. Register `Trent 970 / RR` and then `Trent 970 / GE`.
2. Expected: both are accepted, since the identity is the name+maker combination.

**Manual test — AC056.3 (fuel type selection):**
1. At the fuel type prompt, verify all pre-defined fuel types are listed.
2. Select each available fuel type and confirm registration succeeds.
3. Expected: engine model is registered for every valid fuel type.

**Manual test — AC056.4 (invalid thrust rejected):**
1. Attempt to register an engine with cruise thrust greater than or equal to standstill thrust.
2. Expected: the system rejects the operation with an appropriate validation message.

**Manual test — AC056.6 (unauthorized user):**
1. Log in as a user without the Backoffice Operator role.
2. Attempt to access the `Create Engine Model` option.
3. Expected: access is denied and no engine model is registered.