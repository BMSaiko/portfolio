# US070 — Tests and Coverage

## Scope

US070 covers the registration of a new Aircraft into an Air Transport Company's fleet.
The process is executed by a Company Collaborator and requires specifying a unique
registration number, an existing aircraft model (Maker and Model), country of registration,
cabin configuration (first, business, and economy seats), operational status, and manufacturing year.

---

## Automated Tests

### `CabinConfigurationTest`

Location: `aisafe.core/src/test/java/eapli/aisafe/fleetmanagement/domain/CabinConfigurationTest.java`

- `ensureValidConfigurationIsCreated`
- `ensureFirstClassSeatsCannotBeNegative`
- `ensureBusinessClassSeatsCannotBeNegative`
- `ensureEconomyClassSeatsCannotBeNegative`
- `ensureTotalSeatsAreCalculatedCorrectly`
- `ensureZeroSeatsAcrossAllClassesIsRejected`

### `AircraftTest`

Location: `aisafe.core/src/test/java/eapli/aisafe/fleetmanagement/domain/AircraftTest.java`

- `shouldCreateAircraftWithValidArguments`
- `shouldThrowWhenRegistrationIsNull`
- `shouldThrowWhenRegistrationIsBlank`
- `shouldThrowWhenModelIsNull`
- `shouldThrowWhenCompanyIsNull`
- `shouldThrowWhenCountryIsBlank`
- `shouldThrowWhenCabinConfigurationIsNull`
- `shouldThrowWhenOperationalStatusIsNull`
- `shouldThrowWhenManufacturingYearIsInvalid`
- `shouldCalculateAgeCorrectlyBasedOnCurrentYear`
- `shouldBeEqualWhenSameRegistration`
- `shouldNotBeEqualWhenDifferentRegistration`

### `AddAircraftControllerTest`

Location: `aisafe.core/src/test/java/eapli/aisafe/fleetmanagement/application/AddAircraftControllerTest.java`

- `ensureValidAircraftIsAddedToFleet`
- `ensureDuplicateRegistrationIsRejected`
- `ensureExceptionWhenNoActiveSession`
- `ensureExceptionWhenUserIsNotCompanyCollaborator`
- `ensureExceptionWhenAircraftModelDoesNotExist`
- `ensureAircraftIsAssignedToLoggedCollaboratorCompany`

---

## Coverage by Acceptance Criterion

- **AC070.1** — Aircraft registration must be unique across the system:
  `ensureDuplicateRegistrationIsRejected`,
  `shouldBeEqualWhenSameRegistration`

- **AC070.2** — Aircraft must be linked to an existing, valid Aircraft Model:
  `ensureExceptionWhenAircraftModelDoesNotExist`,
  `shouldThrowWhenModelIsNull`

- **AC070.3** — Cabin configuration must contain valid, non-negative seat numbers:
  `ensureFirstClassSeatsCannotBeNegative`,
  `ensureBusinessClassSeatsCannotBeNegative`,
  `ensureTotalSeatsAreCalculatedCorrectly`

- **AC070.4** — The aircraft must belong to the logged-in collaborator's company:
  `ensureExceptionWhenUserIsNotCompanyCollaborator`,
  `ensureAircraftIsAssignedToLoggedCollaboratorCompany`

- **AC070.5** — UI must provide dynamic selection for Maker, Model, and Status:
  Validated by manual integration testing (see below).

---

## Acceptance Tests

Authorization (Company Collaborator role check) and UI dynamic menu generation
are infrastructure and presentation concerns validated by manual integration testing.
Domain-level validations (seat counts, unique registration, year boundaries) are
fully covered by the automated tests above.

### Manual Test — AC070.1 (duplicate registration rejected)

1. Run `AISafeBackoffice`.
2. Login as `collab@tap.com` / `Collab1234`.
3. Navigate to `Add Aircraft`.
4. Enter aircraft registration `CS-TPA` (assuming it already exists in the DB).
5. Follow the steps to select Maker, Model, and fill the remaining details.
6. Expected: error message — `[BUSINESS ERROR] An aircraft already exists with registration: CS-TPA`

### Manual Test — AC070.5 (dynamic menus load successfully)

1. Navigate to `Add Aircraft`.
2. Enter aircraft registration `CS-NEW`.
3. Expected: The system queries the DB and presents a numbered list of available Makers (e.g., `1. Airbus`, `2. Boeing`).
4. Select `Boeing`.
5. Expected: The system dynamically presents only models belonging to Boeing (e.g., `777-200ER`, `737-800`).
6. Select a model, fill out the seat configuration, and expected to see the numbered Operational Status menu.

### Manual Test — Full successful registration flow

1. Login as a valid company collaborator.
2. Navigate to `Add Aircraft`.
3. Enter Registration: `CS-WIN`.
4. Select Maker: `Airbus`.
5. Select Model: `A320-200`.
6. Enter Country: `Portugal`.
7. Enter Seats: `10` First, `20` Business, `120` Economy.
8. Select Status: `1` (ACTIVE).
9. Enter Manufacturing Year: `2020`.
10. Expected: success message — `[SUCCESS] Aircraft CS-WIN successfully added to the fleet!`
11. (Optional) Navigate to `List Fleet` and verify the aircraft appears correctly.