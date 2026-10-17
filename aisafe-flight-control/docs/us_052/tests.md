# US052 — Tests and Coverage

## Scope

US052 covers registering an Airport with a unique IATA code, a unique ICAO code,
associated with an existing Air Control Area, and with coordinates that fall within
the geographic boundary of that area, by a Backoffice Operator.
Registration must also be achievable by a bootstrap process.

---

## Automated Tests

### `IataCodeTest`

Location: `aisafe.core/src/test/java/eapli/aisafe/airportmanagement/domain/IataCodeTest.java`

- `ensureValidCodeIsCreated`
- `ensureNullCodeIsRejected`
- `ensureEmptyCodeIsRejected`
- `ensureCodeWithWrongLengthIsRejected`
- `ensureCodeIsStoredUppercase`
- `ensureEqualCodesAreEqual`

### `IcaoCodeTest`

Location: `aisafe.core/src/test/java/eapli/aisafe/airportmanagement/domain/IcaoCodeTest.java`

- `ensureValidCodeIsCreated`
- `ensureNullCodeIsRejected`
- `ensureEmptyCodeIsRejected`
- `ensureCodeWithWrongLengthIsRejected`
- `ensureCodeIsStoredUppercase`
- `ensureEqualCodesAreEqual`

### `CoordinatesTest`

Location: `aisafe.core/src/test/java/eapli/aisafe/airportmanagement/domain/CoordinatesTest.java`

- `ensureValidCoordinatesAreCreated`
- `ensureLatitudeAbove90IsRejected`
- `ensureLatitudeBelow90IsRejected`
- `ensureLongitudeAbove180IsRejected`
- `ensureLongitudeBelow180IsRejected`
- `ensureNegativeAltitudeIsRejected`

### `AirportTest`

Location: `aisafe.core/src/test/java/eapli/aisafe/airportmanagement/domain/AirportTest.java`

- `ensureValidAirportIsCreated`
- `ensureIdentityIsCorrect`
- `ensureSameIataCodeMeansSameAs`
- `ensureDifferentIataCodeMeansNotSameAs`
- `ensureNullIataCodeIsRejected`
- `ensureNullAirControlAreaIsRejected`

### `RegisterAirportControllerTest`

Location: `aisafe.core/src/test/java/eapli/aisafe/airportmanagement/application/RegisterAirportControllerTest.java`

- `ensureValidAirportIsRegistered`
- `ensureDuplicateIataCodeIsRejected`
- `ensureDuplicateIcaoCodeIsRejected`
- `ensureNonExistentAreaIsRejected`
- `ensureCoordinatesOutsideBoundaryAreRejected`
- `ensureCoordinatesInsideBoundaryAreAccepted`

---

## Coverage by Acceptance Criterion

- **AC052.1** — IATA code must be unique:
  `ensureDuplicateIataCodeIsRejected`,
  `ensureValidCodeIsCreated` (IataCodeTest),
  `ensureCodeWithWrongLengthIsRejected` (IataCodeTest),
  `ensureNullCodeIsRejected` (IataCodeTest)

- **AC052.2** — ICAO code must be unique:
  `ensureDuplicateIcaoCodeIsRejected`,
  `ensureValidCodeIsCreated` (IcaoCodeTest),
  `ensureCodeWithWrongLengthIsRejected` (IcaoCodeTest),
  `ensureNullCodeIsRejected` (IcaoCodeTest)

- **AC052.3** — Airport must be associated with an existing Air Control Area:
  `ensureNonExistentAreaIsRejected`,
  `ensureNullAirControlAreaIsRejected`,
  `ensureValidAirportIsRegistered`

- **AC052.4** — Airport coordinates must fall within the Air Control Area boundary:
  `ensureCoordinatesOutsideBoundaryAreRejected`,
  `ensureCoordinatesInsideBoundaryAreAccepted`,
  `ensureValidCoordinatesAreCreated`,
  `ensureLatitudeAbove90IsRejected`,
  `ensureLatitudeBelow90IsRejected`,
  `ensureLongitudeAbove180IsRejected`,
  `ensureLongitudeBelow180IsRejected`

- **AC052.5** — Console UI available for Backoffice Operator:
  Validated by manual integration testing (see below).

- **AC052.6** — Registration achievable by bootstrap:
  Validated by `AirControlAreaBootstrap` which pre-loads LIS, OPO and FAO
  airports on application startup — verified by running the application.

---

## Acceptance Tests

Authorization (Backoffice Operator role check) and bootstrap are infrastructure
concerns validated by manual integration testing. Domain-level validations
(code uniqueness, coordinate containment, area existence) are fully covered
by the automated tests above.

### Manual Test — AC052.1 (duplicate IATA code rejected)

1. Run `AISafeBackoffice`.
2. Login as `backoffice@aisafe.com` / `Back1234`.
3. Navigate to `Register Airport`.
4. Enter IATA code `LIS` (already created by bootstrap).
5. Enter any valid remaining fields with area code `LPPC`.
6. Expected: error message — `An airport with IATA code 'LIS' already exists.`

### Manual Test — AC052.2 (duplicate ICAO code rejected)

1. Navigate to `Register Airport`.
2. Enter a new IATA code (e.g. `XXX`) but ICAO code `LPPT` (already created by bootstrap).
3. Enter any valid remaining fields with area code `LPPC`.
4. Expected: error message — `An airport with ICAO code 'LPPT' already exists.`

### Manual Test — AC052.3 (non-existent Air Control Area rejected)

1. Navigate to `Register Airport`.
2. Enter IATA `TST`, ICAO `TTST`, valid name, town, country, coordinates.
3. Enter Air Control Area Code `XXXX` (does not exist).
4. Expected: error message — `Air Control Area 'XXXX' does not exist.`

### Manual Test — AC052.4 (coordinates outside boundary rejected)

1. Navigate to `Register Airport`.
2. Enter IATA `LHR`, ICAO `EGLL`, name `Heathrow Airport`, town `London`, country `UK`.
3. Enter latitude `51.4775`, longitude `-0.4614`, altitude `25.0`.
4. Enter Air Control Area Code `LPPC` (N:47 S:36 E:-6 W:-20).
5. Expected: error message — `Coordinates (51.4775, -0.4614) are outside the boundary of Air Control Area 'LPPC'.`

### Manual Test — AC052.5 + AC052.6 (full registration and bootstrap)

1. Run `AISafeBackoffice` (bootstrap runs automatically).
2. Confirm in the startup logs that LIS, OPO and FAO were created by bootstrap.
3. Login as `backoffice@aisafe.com` / `Back1234`.
4. Navigate to `Register Airport`.
5. Enter: IATA `PDL`, ICAO `LPPD`, name `Joao Paulo II Airport`,
   town `Ponta Delgada`, country `Portugal`,
   latitude `37.7412`, longitude `-25.6979`, altitude `109.0`,
   area code `AZOR` (register AZOR first via Register Air Control Area if not present:
   North `42.0`, South `36.0`, East `-22.0`, West `-32.0`, fuel `4000`).
6. Expected: success message — `Registered: [PDL/LPPD] Joao Paulo II Airport, Ponta Delgada, Portugal | (37.7412, -25.6979, 109.0m) | Area: AZOR`