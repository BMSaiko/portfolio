# US072 — Tests and Coverage

## Scope

US072 covers the ability of a Company Collaborator to list the aircraft belonging
to their specific Air Transport Company's fleet. It also covers the ability to filter
this list dynamically by Aircraft Model (US072a), Maker (US072b), Capacity (US072c),
and Age (US072d).

---

## Automated Tests

### `AircraftTest`

Location: `aisafe.core/src/test/java/eapli/aisafe/fleetmanagement/domain/AircraftTest.java`

- `shouldCalculateAgeCorrectlyBasedOnCurrentYear`
- `ensureCapacityIsCalculatedCorrectlyFromCabinConfiguration`

### `ListFleetControllerTest`

Location: `aisafe.core/src/test/java/eapli/aisafe/fleetmanagement/application/ListFleetControllerTest.java`

- `ensureGetMyCompanyFleetReturnsOnlyCompanyAircraft`
- `ensureGetFleetByModelFiltersCorrectly`
- `ensureGetFleetByMakerFiltersCorrectly`
- `ensureGetFleetByCapacityFiltersCorrectly`
- `ensureGetFleetByAgeFiltersCorrectly`
- `ensureExceptionWhenNoActiveSession`
- `ensureExceptionWhenUserIsNotCompanyCollaborator`

### `JpaAircraftRepositoryTest` (Integration Tests)

Location: `aisafe.persistence.impl/src/test/java/eapli/aisafe/persistence/impl/jpa/JpaAircraftRepositoryTest.java`

- `ensureFindByCompanyReturnsCorrectList`
- `ensureFindByCompanyAndModelReturnsCorrectList`
- `ensureFindByCompanyAndMakerReturnsCorrectList`
- `ensureFindByCompanyAndCapacityReturnsCorrectList`
- `ensureFindByCompanyAndAgeReturnsCorrectList`

---

## Coverage by Acceptance Criterion

- **AC072.1** — The system must list only the aircraft belonging to the logged-in user's company:
  `ensureGetMyCompanyFleetReturnsOnlyCompanyAircraft`,
  `ensureFindByCompanyReturnsCorrectList`

- **AC072.2** — The UI must provide dynamic filters based on the existing fleet data:
  Validated by manual integration testing of the UI Streams (see below).

- **AC072.3** — Filtering by Model (US072a), Maker (US072b), Capacity (US072c), and Age (US072d) must return accurate results:
  `ensureGetFleetByModelFiltersCorrectly`,
  `ensureGetFleetByMakerFiltersCorrectly`,
  `ensureGetFleetByCapacityFiltersCorrectly`,
  `ensureGetFleetByAgeFiltersCorrectly`,
  `shouldCalculateAgeCorrectlyBasedOnCurrentYear`

---

## Acceptance Tests

Authorization (Company Collaborator role check) and the generation of dynamic
menu options in the UI (using Java Streams) are presentation concerns validated
by manual integration testing. The filtering logic and repository queries are fully
covered by the automated and integration tests above.

### Manual Test — AC072.1 (List entire fleet securely)

1. Run `AISafeBackoffice`.
2. Login as `collab@tap.com` / `Collab1234` (Assuming TAP Air Portugal).
3. Navigate to `List Fleet`.
4. Select Option `1. List Entire Fleet`.
5. Expected: A list of aircraft is displayed. Verify that NO aircraft belonging to another company (e.g., Ryanair or EasyJet) appears on this list.

### Manual Test — AC072.2 & AC072.3 (Dynamic Filtering)

1. Run `AISafeBackoffice`.
2. Login as `collab@tap.com` / `Collab1234`.
3. Navigate to `List Fleet`.
4. Select Option `3. Filter by Maker`.
5. Expected: The system displays a numbered list of ONLY the makers that exist in the company's current fleet (e.g., `1. Airbus`, `2. Boeing`).
6. Select Option `1` (Airbus).
7. Expected: The system prints a table showing exclusively Airbus aircraft.
8. Repeat the process for Age (Option 5), select a specific age (e.g., `5 years old`), and verify the filtered table.