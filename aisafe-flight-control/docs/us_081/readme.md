# US081 - Create a Flight Plan from a File

## 1. Context

This user story belongs to the Integrative Project (LAPR4) and is part of Sprint 2. It enables a Backoffice Operator or Administrator to import a flight plan from a DSL (Domain Specific Language) file, validate it through the ANTLR-based parsing pipeline (US083), construct the corresponding domain objects, and persist them into the system.

Flight plans in AISafe cannot be manually created in their full form — they must be described using the Flight DSL and imported through this functionality. US081 is the primary consumer of the DSL validation engine built in US083, and it provides the bridge between the textual DSL representation and the JPA-persisted domain model.

## 1.1 List of Issues

- **Analysis:** Understand the end-to-end flow from DSL file input to persisted `FlightPlan` aggregate, including authentication, validation, domain construction, and persistence.
- **Design:** Define the application service layer, the controller, the console UI, and the mapping from the LPROG internal model to the AISafe domain model.
- **Implement:** Implement the `FlightPlanService` facade, the `ImportFlightPlanController`, the `ImportFlightPlanUI` console interface, and the `FlightPlanImportExamples` helper.
- **Test:** Validate the import pipeline with valid and invalid DSL files, verify proper error handling for missing aircraft models, and confirm correct domain object construction.


## 2. Requirements

**US081** — As a Backoffice Operator, I want to create a flight plan by importing it from a file, so that flight plans can be formally defined using the Flight DSL and stored in the system.

### Acceptance Criteria

- **US081.1:** The system must allow a Backoffice Operator or Administrator to import a flight plan from a `.dsl` file.
- **US081.2:** The system must validate the file using the Flight DSL validation pipeline (US083) — lexical, syntactic, and semantic checks.
- **US081.3:** If the DSL file is invalid, the system must display all validation errors to the user.
- **US081.4:** If the DSL file is valid, the system must create and persist a `FlightPlan` aggregate with its legs, segments, and associated aircraft model.
- **US081.5:** The system must verify that the referenced aircraft model exists before creating the flight plan.
- **US081.6:** The system must require the user to provide the aircraft model ID, maker name, and total distance at import time.
- **US081.7:** The flight plan must be created with `DRAFT` status.
- **US081.8:** Only users with the `BACKOFFICE_OPERATOR` or `ADMIN` role may import flight plans.

### Out of Scope

- Editing an existing flight plan after import.
- Manual creation of flight plans without a DSL file.
- Flight plan approval or activation workflows (handled by other user stories).
- Real-time flight simulation or physics calculations.

### Dependencies / References

- **US083** — Flight DSL Specification and Validation. US081 depends on US083 to perform lexical, syntactic, and semantic validation of the uploaded file. The `FlightPlanImportService` and `ImportResult` types from the `lprog` module are used directly.
- **US085** — Test/validate flight plan. Shares the same DSL validation infrastructure.
- **NFR09** — Authentication and Authorization. Role-based access control is enforced.
- **NFR11** — LPROG Assessment. The quality of integration between the DSL parser and the domain layer is an assessment criterion.


## 3. Analysis

### 3.1 Understanding

The import process follows a sequential pipeline:

1. **User input** — The Backoffice Operator provides a file path, aircraft model ID, maker name, and total distance via the console UI.
2. **Authentication** — The system verifies the user has `BACKOFFICE_OPERATOR` or `ADMIN` role.
3. **DSL validation** — The file is passed to `FlightPlanImportService` (from the `lprog` module), which orchestrates the ANTLR lexer, parser, visitor/listener, and semantic validator.
4. **Result handling** — If validation fails, all errors are collected and displayed. If successful, the parsed `FlightPlan` model objects are returned.
5. **Domain construction** — The parsed model is mapped to AISafe domain objects: `FlightPlan`, `FlightLeg`, `FlightSegment`, `Waypoint`.
6. **Aircraft model lookup** — The system looks up the `AircraftModel` by ID and maker name.
7. **Persistence** — The `FlightPlan` aggregate is saved via the `FlightPlanRepository`.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| Two-layer service architecture (`FlightPlanService` + `ImportFlightPlanController`) | `FlightPlanService` acts as a facade for the UI layer; `ImportFlightPlanController` encapsulates the core import logic. Both are implemented for flexibility. |
| Map LPROG model to AISafe domain objects | The LPROG module produces its own internal `FlightPlan` representation. US081 translates this into the AISafe domain `FlightPlan` aggregate to maintain separation of concerns. |
| Require aircraft model at import time | The DSL file does not contain aircraft model information. The operator must provide it so the `FlightPlan` aggregate can reference a valid `AircraftModel`. |
| Set status to DRAFT on creation | A newly imported flight plan is not yet validated or approved. The lifecycle starts at `DRAFT` and transitions through `VALIDATED` → `APPROVED` → `ACTIVE`. |
| Collect all DSL errors before reporting | Consistent with US083's approach — all lexical, syntactic, and semantic errors are collected and reported together. |
| Use `IllegalArgumentException` for validation failures | DSL validation errors are treated as invalid input (`IllegalArgumentException`), while missing domain references use `IllegalStateException`. |


## 4. Design

### 4.1 Architecture Overview

```
┌──────────────────────────────────────────────────────────────┐
│                    Presentation Layer                         │
│  ImportFlightPlanUI ──► FlightPlanService (facade)           │
│         │                      │                              │
│         │              FlightPlanMenuUI                       │
│         │              FlightPlanImportExamples               │
└─────────┼──────────────────────┼──────────────────────────────┘
          │                      │
┌─────────┼──────────────────────┼──────────────────────────────┐
│         ▼                      ▼         Application Layer     │
│  ImportFlightPlanController                                    │
│         │                                                      │
│         ├──► FlightPlanImportService (lprog module)            │
│         │         └──► FlightDSLValidator                      │
│         │               ├── ANTLR Lexer/Parser                │
│         │               ├── FlightDSLVisitor                   │
│         │               ├── FlightDSLListener                  │
│         │               └── SemanticValidator                  │
│         │                                                      │
│         ├──► AircraftModelRepository.findById()                │
│         └──► FlightPlanRepository.save()                       │
└──────────────────────────────────────────────────────────────┘
          │
┌─────────┼──────────────────────────────────────────────────────┐
│         ▼                    Domain Layer                       │
│  FlightPlan (Aggregate Root)                                   │
│    ├── FlightPlanId, route, departureAirport, arrivalAirport   │
│    ├── AircraftModel (reference)                               │
│    ├── FlightPlanStatus (DRAFT)                                │
│    ├── List<FlightLeg>                                         │
│    │     ├── departureAirport, arrivalAirport, fuel            │
│    │     └── List<FlightSegment>                               │
│    │           ├── mode (climb/cruise/descend)                 │
│    │           ├── Waypoint start (lat, lon, alt)              │
│    │           └── Waypoint end (lat, lon, alt)                │
│    ├── totalDistance, estimatedFuel                            │
│    └── departureDateTime                                       │
└──────────────────────────────────────────────────────────────┘
```

### 4.2 Main Flow

```
User → ImportFlightPlanUI: provide filePath, modelId, makerName, distance
ImportFlightPlanUI → FlightPlanService: importFlightPlan(path, modelId, makerName, distance)
FlightPlanService → AuthService: ensureHasAnyRole(BACKOFFICE_OPERATOR, ADMIN)
FlightPlanService → FlightPlanImportService: importFlightPlan(filePath)
FlightPlanImportService → FlightDSLValidator: validate(filePath)
FlightDSLValidator → ANTLR: lexing + parsing
FlightDSLValidator → FlightDSLVisitor: build internal FlightPlan model
FlightDSLValidator → SemanticValidator: validate semantics
FlightDSLValidator → FlightPlanImportService: ValidationResult
FlightPlanImportService → FlightPlanService: ImportResult

if ImportResult.isSuccess():
    FlightPlanService → AircraftModelRepository: findById(modelId, makerName)
    FlightPlanService → FlightPlan: new FlightPlan(id, route, ...)
    FlightPlanService → FlightPlanRepository: save(flightPlan)
    FlightPlanService → User: success message
else:
    FlightPlanService → User: display all DSLErrors
```

### 4.3 Alternative Flows

**DSL validation failure (lexical/syntactic/semantic errors):**
```
FlightPlanImportService returns ImportResult.failure(errors)
FlightPlanService throws IllegalArgumentException with all errors
ImportFlightPlanUI catches and displays: "ERROR: DSL validation failed: ..."
```

**Aircraft model not found:**
```
AircraftModelRepository.findById() returns empty
FlightPlanService throws IllegalStateException
ImportFlightPlanUI catches and displays: "ERROR: Aircraft model not found: ..."
```

**Access denied (wrong role):**
```
AuthService.ensureHasAnyRole() throws SecurityException
ImportFlightPlanUI catches and displays: "ACCESS DENIED: ..."
```

### 4.4 Mapping: LPROG Model → AISafe Domain

| LPROG Model (`pt.isep.sem4pi.lprog.model`) | AISafe Domain (`eapli.aisafe.flightplanmanagement.domain`) |
|---|---|
| `FlightPlan.identifier` | `FlightPlanId.of(id)` |
| `FlightPlan.legs[0].departure.airportCode` | `FlightPlan.departureAirport` |
| `FlightPlan.legs[last].arrival.airportCode` | `FlightPlan.arrivalAirport` |
| Leg departure/arrival codes joined by `-` | `FlightPlan.route` (e.g. `LIS-FCO-VIT`) |
| `FlightLeg.departure.airportCode` | `FlightLeg.departureAirport` |
| `FlightLeg.arrival.airportCode` | `FlightLeg.arrivalAirport` |
| `FlightLeg.fuel.quantity` | `FlightLeg.fuel` |
| `FlightSegment.mode` | `FlightSegment.mode` |
| `FlightSegment.start.lat/lon/altitude` | `Waypoint.of(lat, lon, alt)` → `FlightSegment.start` |
| `FlightSegment.end.lat/lon/altitude` | `Waypoint.of(lat, lon, alt)` → `FlightSegment.end` |
| Sum of all legs' fuel | `FlightPlan.estimatedFuel` |
| User-provided parameter | `FlightPlan.totalDistance` |
| `LocalDateTime.now()` at import time | `FlightPlan.departureDateTime` |
| Hardcoded `DRAFT` | `FlightPlan.status` |


## 5. Implementation

### 5.1 File Structure

```
aisafe-flight-control/
├── aisafe.core/
│   └── src/
│       ├── main/java/eapli/aisafe/flightplanmanagement/
│       │   ├── application/
│       │   │   ├── FlightPlanService.java              ← Facade for UI layer
│       │   │   └── ImportFlightPlanController.java     ← Core import logic
│       │   └── domain/
│       │       ├── FlightPlan.java                      ← Aggregate root (JPA @Entity)
│       │       ├── FlightPlanId.java                    ← Value object (@Embeddable)
│       │       ├── FlightPlanStatus.java                ← Enum (DRAFT, VALIDATED, ...)
│       │       ├── FlightLeg.java                       ← Entity with segments
│       │       ├── FlightSegment.java                   ← Value object (@Embeddable)
│       │       └── Waypoint.java                        ← Value object (@Embeddable)
│       └── test/
│           ├── java/eapli/aisafe/flightplanmanagement/application/
│           │   └── ImportFlightPlanControllerTest.java  ← Unit tests (mocked repos)
│           └── resources/
│               ├── valid_flight.dsl                     ← Test DSL (LIS → FRA)
│               └── invalid_flight.dsl                   ← Test DSL (syntax errors)
│
├── aisafe.app.backoffice.console/
│   └── src/main/java/eapli/aisafe/app/backoffice/console/
│       └── presentation/flightplanmanagement/
│           ├── FlightPlanMenuUI.java                    ← Submenu dispatcher
│           ├── ImportFlightPlanUI.java                  ← Console UI for import
│           └── FlightPlanImportExamples.java            ← DSL examples helper
│
└── lprog/                                               ← External sibling module
    └── src/
        ├── main/java/pt/isep/sem4pi/lprog/
        │   ├── application/
        │   │   ├── FlightPlanImportService.java         ← Orchestrates DSL validation
        │   │   └── ImportResult.java                    ← Success/failure result
        │   └── FlightDSLValidator.java                  ← ANTLR pipeline orchestrator
        └── test/
            └── java/pt.isep.sem4pi.lprog/
                └── FlightPlanImportServiceTest.java     ← DSL integration tests
```

### 5.2 Key Classes

#### `FlightPlanService` (Facade)
- **Package:** `eapli.aisafe.flightplanmanagement.application`
- **Responsibility:** Coordinates DSL validation, domain construction, and persistence. Acts as the entry point for the UI layer.
- **Dependencies:** `AuthService`, `FlightPlanRepository`, `AircraftModelRepository`, `FlightPlanImportService`

#### `ImportFlightPlanController` (Core Logic)
- **Package:** `eapli.aisafe.flightplanmanagement.application`
- **Responsibility:** Contains the same import logic as `FlightPlanService`, available as a standalone controller for programmatic use.
- **Dependencies:** `AuthService`, `FlightPlanRepository`, `AircraftModelRepository`, `FlightPlanImportService`

#### `ImportFlightPlanUI` (Console UI)
- **Package:** `eapli.aisafe.app.backoffice.console.presentation.flightplanmanagement`
- **Responsibility:** Prompts the user for file path, aircraft model ID, maker name, and distance. Delegates to `FlightPlanService`.
- **Error handling:** Catches `IllegalArgumentException` (DSL errors), `IllegalStateException` (missing aircraft), `SecurityException` (access denied), and generic `Exception`.

#### `FlightPlanImportExamples` (Helper)
- **Package:** `eapli.aisafe.app.backoffice.console.presentation.flightplanmanagement`
- **Responsibility:** Provides formatted strings showing available DSL example files, registered aircraft models, and an example DSL template.

### 5.3 Console UI Flow

```
=== Flight Plan Management ===
1. Import Flight Plan from DSL
2. List Flight Plans
3. Show Flight Plan Details
4. Show Import Examples
0. Back

Option: 1

=== Import Flight Plan from DSL ===
DSL file path: lprog/src/main/resources/examples/valid_opo_mad.dsl
Aircraft Model ID (e.g. BOEING-737): BOEING-737
Maker Name (e.g. Boeing): Boeing
Total distance (km): 2340

Success! Flight plan imported:
  [AA123] OPO-MAD (OPO -> MAD) | Status: DRAFT | Distance: 2340.0 km | Fuel: 40200.0 kg
```

### 5.4 Error Display Examples

**DSL validation failure:**
```
ERROR: DSL validation failed:
  - [SYNTACTIC] line 33:16 — missing ';' at 'kg'
  - [SYNTACTIC] line 35:0 — missing '}' at '<EOF>'
```

**Aircraft model not found:**
```
ERROR: Aircraft model not found: UNKNOWN (Unknown)
```

**Access denied:**
```
ACCESS DENIED: User does not have the required role.
```


## 6. Demonstration

### Valid Import

**Input file** (`valid_opo_mad.dsl`):
```dsl
flight AA123 {
    type: regular;

    leg {
        departure: OPO at 2025-06-20 10:00;
        arrival: MAD at 2025-06-20 11:15;

        route: OPO -> MAD;

        segment {
            mode: climb;
            start: (41.262891, -8.68522, 69 m);
            end:   (42.0, -8.01, 9249 m);
        }

        segment {
            mode: cruise;
            start: (42.0, -8.01, 9249 m);
            end:   (41.0, -4.3, 9249 m);
        }

        segment {
            mode: descend;
            start: (41.0, -4.3, 9249 m);
            end:   (40.4895, -3.5643, 610 m);
        }

        fuel: 40200 kg;
    }
}
```

**User input:**
- Aircraft Model ID: `BOEING-737`
- Maker Name: `Boeing`
- Total distance: `2340`

**Expected result:**
```
Success! Flight plan imported:
  [AA123] OPO-MAD (OPO -> MAD) | Status: DRAFT | Distance: 2340.0 km | Fuel: 40200.0 kg
```

**Persisted state:**
- `FlightPlan` with id `AA123`, route `OPO-MAD`, status `DRAFT`
- 1 `FlightLeg`: OPO → MAD, fuel 40200.0
- 3 `FlightSegment`s: climb, cruise, descend
- `estimatedFuel` = 40200.0 (sum of all legs' fuel)

### Multi-Leg Import

**Input file** (`valid_lis_fco_vit.dsl`):
```dsl
flight TP456 {
    type: regular;

    leg {
        departure: LIS at 2025-07-10 09:00;
        arrival: FCO at 2025-07-10 13:30;
        route: LIS -> FCO;
        // ... 3 segments ...
        fuel: 12000 kg;
    }

    leg {
        departure: FCO at 2025-07-10 15:00;
        arrival: VIT at 2025-07-10 17:15;
        route: FCO -> VIT;
        // ... 3 segments ...
        fuel: 8000 kg;
    }
}
```

**Expected result:**
- Route: `LIS-FCO-VIT`
- Departure airport: `LIS`
- Arrival airport: `VIT`
- 2 legs, 6 segments total
- `estimatedFuel` = 20000.0 (12000 + 8000)

### Invalid Import (DSL Errors)

**Input file** (`invalid_missing_fuel.dsl`):
```dsl
flight AA123 {
    type: regular;
    leg {
        departure: OPO;
        arrival: MAD;
        route: OPO -> MAD;
        segment {
            mode: climb;
            start: (41.262891, -8.68522, 69 m);
            end:   (42.0, -8.01, 9249 m);
        }
        fuel: 40200 kg    // ← missing semicolon
    }                     // ← missing closing brace for flight
}
```

**Expected result:**
```
ERROR: DSL validation failed:
  - [SYNTACTIC] line 16:16 — missing ';' at '<EOF>'
  - [SYNTACTIC] line 17:0 — missing '}' at '<EOF>'
```


## 7. Tests

### 7.1 Unit Tests (`ImportFlightPlanControllerTest.java`)

| Test | Description | Expected Outcome |
|---|---|---|
| `testImportValidFlightPlanFromDSL` | Import a valid DSL file with a known aircraft model | FlightPlan is created with correct ID, route, airports, legs, segments, fuel, and DRAFT status. Repository `save()` is called. |
| `testImportInvalidFlightPlan` | Import a DSL file with syntax errors (missing semicolon, missing brace) | `IllegalArgumentException` is thrown |
| `testImportWithUnknownAircraftModel` | Import a valid DSL file but provide a non-existent aircraft model ID | `IllegalStateException` is thrown with "Aircraft model not found" message |

### 7.2 DSL Integration Tests (`FlightPlanImportServiceTest.java` in `lprog` module)

| Test | Description | Expected Outcome |
|---|---|---|
| `shouldImportValidFlightPlan` | Parse `valid_opo_mad.dsl` | `ImportResult.isSuccess()` returns `true` |
| `shouldRejectInvalidFlightPlan` | Parse `invalid_missing_fuel.dsl` | `ImportResult.isSuccess()` returns `false`, errors list is non-empty |

### 7.3 Test DSL Files

| File | Location | Purpose |
|---|---|---|
| `valid_flight.dsl` | `aisafe.core/src/test/resources/` | Valid single-leg flight (LIS → FRA), used in controller tests |
| `invalid_flight.dsl` | `aisafe.core/src/test/resources/` | Invalid flight with missing semicolon and closing brace |
| `valid_opo_mad.dsl` | `lprog/src/main/resources/examples/` | Valid single-leg flight (OPO → MAD), used in LPROG tests |
| `valid_lis_fco_vit.dsl` | `lprog/src/main/resources/examples/` | Valid multi-leg flight (LIS → FCO → VIT) |
| `invalid_missing_fuel.dsl` | `lprog/src/main/resources/examples/` | Invalid flight with syntax errors |
| `invalid_semantic_errors.dsl` | `lprog/src/main/resources/examples/` | Invalid flight with semantic errors (wrong type, bad route, identical coordinates, negative fuel) |


## 8. Observations

- **Dependency on US083:** US081 cannot function without the `lprog` module (US083). The `lprog` module must be built and installed to the local Maven repository (`cd lprog ; mvn clean install`) before the `aisafe.core` module can compile.

- **Two service classes:** Both `FlightPlanService` and `ImportFlightPlanController` contain nearly identical logic. `FlightPlanService` serves as the UI facade (with additional methods like `listFlightPlans` and `findFlightPlan`), while `ImportFlightPlanController` is a focused controller for programmatic use. This provides flexibility for future integration.

- **Aircraft model is external input:** The DSL file does not contain aircraft model information. The operator must provide the model ID, maker name, and total distance at import time. This is a deliberate design decision — the DSL describes the flight geometry, while the aircraft characteristics are managed separately in the domain.

- **Estimated fuel calculation:** The `estimatedFuel` is computed as the sum of all legs' fuel values from the DSL file. This is a simple aggregation; more sophisticated fuel calculations (based on distance, aircraft performance, weather) are out of scope for this user story.

- **Status lifecycle:** A newly imported flight plan always starts as `DRAFT`. The `validate()` and `approve()` methods on `FlightPlan` allow transitioning to `VALIDATED` and `APPROVED` states, which are handled by separate user stories.

- **Role enforcement:** The `authService.ensureHasAnyRole(Role.BACKOFFICE_OPERATOR, Role.ADMIN)` call is made at the beginning of the import method, before any file processing occurs. This follows the "fail fast" principle — there is no point validating a DSL file if the user is not authorized.

- **Error propagation:** DSL errors are wrapped in `IllegalArgumentException` and propagated to the UI layer, which catches them and displays them to the user. This keeps the domain layer clean of UI concerns while providing actionable feedback.
