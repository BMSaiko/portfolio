# US071 — Tests and Coverage

## Scope

US071 covers the decommissioning of an Aircraft in the Air Transport Company's fleet.
The process is executed by a Company Collaborator. It must change the operational status
of a valid, existing aircraft to `DECOMMISSIONED` (or `RETIRED`). It must also ensure
that an already retired aircraft cannot be retired again.

---

## Automated Tests

### `AircraftTest`

Location: `aisafe.core/src/test/java/eapli/aisafe/fleetmanagement/domain/AircraftTest.java`

- `ensureDecommissionChangesStatusToRetired`
- `ensureDecommissionFailsIfAlreadyRetired`

### `DecommissionAircraftControllerTest`

Location: `aisafe.core/src/test/java/eapli/aisafe/fleetmanagement/application/DecommissionAircraftControllerTest.java`

- `ensureValidAircraftIsDecommissionedSuccessfully`
- `ensureDecommissionFailsIfAircraftNotFound`
- `ensureDecommissionFailsIfAircraftAlreadyRetired`
- `ensureExceptionWhenNoActiveSession`
- `ensureExceptionWhenUserIsNotCompanyCollaborator`

---

## Coverage by Acceptance Criterion

- **AC071.1** — The system must allow retiring an active/maintenance aircraft:
  `ensureDecommissionChangesStatusToRetired`,
  `ensureValidAircraftIsDecommissionedSuccessfully`

- **AC071.2** — The system must reject retiring an aircraft that is already retired:
  `ensureDecommissionFailsIfAlreadyRetired`,
  `ensureDecommissionFailsIfAircraftAlreadyRetired`

- **AC071.3** — The action must be restricted to a Company Collaborator:
  `ensureExceptionWhenUserIsNotCompanyCollaborator`

- **AC071.4** — The specified aircraft registration must exist in the database:
  `ensureDecommissionFailsIfAircraftNotFound`

---

## Acceptance Tests

Authorization (Company Collaborator role check) is an infrastructure concern
validated by manual integration testing. Domain-level state changes (Active -> Retired)
and repeated action blocks are fully covered by the automated tests above.

### Manual Test — AC071.1 (successful decommission)

1. Run `AISafeBackoffice`.
2. Login as `collab@tap.com` / `Collab1234`.
3. Navigate to `Add Aircraft` and add a new active aircraft (e.g., `CS-FLY`).
4. Navigate to `Decommission Aircraft`.
5. Enter aircraft registration `CS-FLY`.
6. Expected: success message — `[SUCCESS] Aircraft CS-FLY decommissioned successfully!`
7. Verify by navigating to `List Fleet`. The status of `CS-FLY` should be `RETIRED` (or `DECOMMISSIONED`).

### Manual Test — AC071.2 (fail if already retired)

1. Run `AISafeBackoffice`.
2. Login as `collab@tap.com` / `Collab1234`.
3. Navigate to `Decommission Aircraft`.
4. Enter the registration of an already retired aircraft (e.g., `CS-FLY` from the previous test).
5. Expected: error message — `[BUSINESS ERROR] Aircraft is already retired`

### Manual Test — AC071.4 (fail if not found)

1. Run `AISafeBackoffice`.
2. Login as `collab@tap.com` / `Collab1234`.
3. Navigate to `Decommission Aircraft`.
4. Enter an invalid/non-existent registration (e.g., `XX-NONE`).
5. Expected: error message — `[BUSINESS ERROR] No aircraft found with registration: XX-NONE`