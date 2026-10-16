# US050 — Tests and Coverage

## Scope

US050 covers registering an Air Control Area with a unique code, valid geographic
boundaries, and no overlap with existing areas, by a Backoffice Operator.
Registration must also be achievable by a bootstrap process.

---

## Automated Tests

### `GeoBoundaryTest`

Location: `aisafe.core/src/test/java/eapli/aisafe/aircontrolmanagement/domain/GeoBoundaryTest.java`

- `ensureValidBoundaryIsCreated`
- `ensureNorthMustBeGreaterThanSouth`
- `ensureEastMustBeGreaterThanWest`
- `ensureLatitudeOutOfRangeIsRejected`
- `ensureLongitudeOutOfRangeIsRejected`
- `ensureOverlappingBoundariesAreDetected`
- `ensureNonOverlappingBoundariesAreNotDetected`
- `ensureAdjacentBoundariesDoNotOverlap`
- `ensurePointInsideBoundaryIsContained`
- `ensurePointOutsideBoundaryIsNotContained`

### `AirControlAreaCodeTest`

Location: `aisafe.core/src/test/java/eapli/aisafe/aircontrolmanagement/domain/AirControlAreaCodeTest.java`

- `ensureValidCodeIsCreated`
- `ensureEmptyCodeIsRejected`
- `ensureNullCodeIsRejected`
- `ensureCodeIsStoredUppercase`
- `ensureEqualCodesAreEqual`

### `AirControlAreaTest`

Location: `aisafe.core/src/test/java/eapli/aisafe/aircontrolmanagement/domain/AirControlAreaTest.java`

- `shouldCreateAreaWithValidArguments`
- `shouldAcceptZeroMinimumFuel`
- `shouldThrowWhenCodeIsNull`
- `shouldThrowWhenNameIsNull`
- `shouldThrowWhenNameIsBlank`
- `shouldThrowWhenNameIsEmpty`
- `shouldThrowWhenMinimumFuelIsNegative`
- `shouldThrowWhenBoundaryIsNull`
- `shouldBeEqualWhenSameCode`
- `shouldNotBeEqualWhenDifferentCode`
- `shouldBeEqualToItself`
- `shouldNotBeEqualToNull`
- `shouldNotBeEqualToDifferentType`
- `equalObjectsShouldHaveSameHashCode`
- `sameAsShouldReturnTrueForSameCode`
- `sameAsShouldReturnFalseForDifferentCode`
- `sameAsShouldReturnFalseForNonArea`
- `sameAsShouldReturnFalseForNull`
- `toStringShouldContainName`
- `toStringShouldContainFuel`
- `toStringShouldContainAreaCode`

### `RegisterAirControlAreaControllerTest`

Location: `aisafe.core/src/test/java/eapli/aisafe/aircontrolmanagement/application/RegisterAirControlAreaControllerTest.java`

- `ensureValidAreaIsRegistered`
- `ensureDuplicateCodeIsRejected`
- `ensureOverlappingBoundaryIsRejected`
- `ensureNonOverlappingAreasAreAccepted`
- `ensureAdjacentAreaIsAccepted`

---

## Coverage by Acceptance Criterion

- **AC050.1** — Area code must be unique:
  `ensureDuplicateCodeIsRejected`

- **AC050.2** — Geographic boundaries must be valid (lat/lon ranges, north > south, east > west):
  `ensureValidBoundaryIsCreated`,
  `ensureNorthMustBeGreaterThanSouth`,
  `ensureEastMustBeGreaterThanWest`,
  `ensureLatitudeOutOfRangeIsRejected`,
  `ensureLongitudeOutOfRangeIsRejected`

- **AC050.3** — Boundaries must not overlap with existing areas:
  `ensureOverlappingBoundariesAreDetected`,
  `ensureNonOverlappingBoundariesAreNotDetected`,
  `ensureAdjacentBoundariesDoNotOverlap`,
  `ensureOverlappingBoundaryIsRejected`,
  `ensureNonOverlappingAreasAreAccepted`,
  `ensureAdjacentAreaIsAccepted`

- **AC050.4** — Console UI available for Backoffice Operator:
  Validated by manual integration testing (see below).

- **AC050.5** — Registration achievable by bootstrap:
  Validated by `AirControlAreaBootstrap` which pre-loads LPPC and LPPO
  on application startup — verified by running the application.

---

## Acceptance Tests

Authorization (Backoffice Operator role check) and bootstrap are infrastructure
concerns validated by manual integration testing. Domain-level validations
(boundary rules, code uniqueness, overlap detection) are fully covered by the
automated tests above.

### Manual Test — AC050.1 (duplicate code rejected)

1. Run `AISafeBackoffice`.
2. Login as `backoffice@aisafe.com` / `Back1234`.
3. Navigate to `Register Air Control Area`.
4. Enter area code `LPPC` (already created by bootstrap).
5. Enter any valid name, fuel, and non-overlapping boundaries.
6. Expected: error message — `An Air Control Area with code 'LPPC' already exists.`

### Manual Test — AC050.2 (invalid boundary rejected)

1. Navigate to `Register Air Control Area`.
2. Enter area code `TEST`, name `Test Area`, minimum fuel `1000`.
3. For boundaries enter North `30.0` and South `47.0` (south greater than north).
4. Expected: the UI rejects the input immediately with
   `South must be less than North` and re-prompts for a valid value.

### Manual Test — AC050.3 (overlapping boundary rejected)

1. Navigate to `Register Air Control Area`.
2. Enter area code `OVER`, name `Overlap Test`, minimum fuel `1000`.
3. Enter boundaries: North `46.0`, South `37.0`, East `-7.0`, West `-19.0`.
4. Expected: error message — `The boundary overlaps with existing area: LPPC`

### Manual Test — AC050.4 + AC050.5 (full registration and bootstrap)

1. Run `AISafeBackoffice` (bootstrap runs automatically).
2. Confirm in the startup logs that `LPPC` and `LPPO` were created by bootstrap.
3. Login as `backoffice@aisafe.com` / `Back1234`.
4. Navigate to `Register Air Control Area`.
5. Enter: code `AZOR`, name `Azores Oceanic`, fuel `4000`,
   North `42.0`, South `36.0`, East `-22.0`, West `-32.0`.
6. Expected: success message — `Registered: [AZOR] Azores Oceanic | MinFuel: 4000.0 kg | Bounds: ...`