# US085 - Test/Validate a Flight Plan

## 1. Context

This user story belongs to the flight plan management area, specifically the
validation pipeline for flight plans described using the Flight DSL.

The goal is to allow a Pilot to test/validate a flight plan they have created,
ensuring it is both syntactically/semantically correct (DSL validation) and
physically plausible (C-based test component), and that the result is
persisted and accessible remotely via the Pilot App.

This user story spans two course units:

- **LPROG** — DSL validation pipeline (ANTLR/Java) + physical test component (C) + Java↔C bridge.
- **EAPLI** — persistence of the validation result, TCP remote access via Pilot App, and domain status lifecycle.

### 1.1 List of Issues

- **Analysis:** Understand what "test/validate" means in two layers — DSL correctness (ANTLR) and physical plausibility (C). Define the contract between layers, the data flow between Java and C, and the domain status lifecycle.
- **Design:** Define `ValidateFlightPlanService`, the `FlightPlanTestInput` / `FlightPlanTestResult` DTOs, the C stdin/stdout JSON protocol, the Makefile/Maven integration, the EAPLI controller, and the TCP handler.
- **Implement (Eixo 1 — LPROG):** Implement `flight_tester.c`, the Java bridge (`FlightPlanTestRunner`), and the orchestrating service (`ValidateFlightPlanService`).
- **Implement (Eixo 2 — EAPLI):** Implement `ValidateFlightPlanController`, `ValidateFlightPlanRequestHandler`, and `ValidateFlightPlanUI`.
- **Test:** Validate the full pipeline end-to-end — DSL errors, physical issues, and the TCP flow.

---

## 2. Requirements

**US085** As a Pilot, I want to test/validate a flight I've made.

### Acceptance Criteria

- **US085.1:** The validation must include validation of the flight plan described using the DSL and its physical test.
- **US085.2:** The physical test component must be implemented in the C language.
- **US085.3:** The DSL validation covers lexical, syntactic, and semantic correctness.
- **US085.4:** The C test component receives the parsed flight plan data and evaluates physical plausibility (fuel positivity, altitude coherence, segment coordinate distinctness).
- **US085.5:** All errors (DSL and physical) must be reported clearly, with type, line/column (for DSL errors), and a descriptive message.
- **US085.6:** A flight plan that fails DSL validation does not proceed to the C test component.
- **US085.7:** A flight plan that passes all validation stages is marked as validated in the system.
- **US085.8:** The operation must be accessible remotely via a TCP-based Pilot App client without direct database interaction.
- **US085.9:** The operation must be authenticated and authorised (PILOT role).

### Dependencies / References

- Depends on **US083** (Flight DSL specification and validation) — `FlightDSL.g4` and `SemanticValidator` are prerequisites.
- Depends on **US081** (Create a flight plan from a file) — the DSL file and `FlightPlanImportService` are the input artefacts.
- Depends on **US080** (Create a flight plan) — `FlightPlan` domain entity and repository (Eixo 2).
- Depends on **US086** (Pilot remote access) — TCP infrastructure for the Pilot App.
- Used by **US100** (Simulate flights) — simulation presupposes a validated flight plan.
- Related to **US082** (Insert weather data) — inserting weather data after a successful test invalidates the result, requiring re-validation.

---

## 3. Analysis

### 3.1 Understanding

A "test/validate" operation for a flight plan has two distinct and sequential layers, followed by a persistence step:

**Layer 1 — DSL Validation (Java + ANTLR)**

The flight plan is described in the Core Flight DSL. This layer ensures the file is well-formed:

- **Lexical:** tokens are valid (no unrecognised characters or malformed literals).
- **Syntactic:** the structure follows the grammar rules (`FlightDSL.g4`).
- **Semantic:** domain coherence rules are satisfied — leg sequence coherence, route coherence, segment coherence, domain consistency (§3.4.5 of the project requirements).

If any error is found at this layer, the pipeline stops immediately. The C component is never invoked.

**Layer 2 — Physical Test (C component)**

After a structurally valid parse tree is produced, the extracted flight plan data is passed to an external C program (`flight_tester`) that evaluates physical plausibility:

- Fuel quantity is strictly positive.
- Altitude values within segments are non-negative.
- Segment start and end coordinates are distinct.
- CLIMB segments: end altitude > start altitude.
- DESCEND segments: end altitude < start altitude.
- CRUISE segments: altitude variation within tolerance (50 m).

**Layer 3 — Persistence (EAPLI)**

After both layers complete, the result is persisted to the `FlightPlan` domain entity. The status transitions are:

```
DRAFT  ──► (validation passes) ──► VALIDATED
DRAFT  ──► (validation fails)  ──► FAILED
VALIDATED ──► (weather inserted, US082) ──► DRAFT  (re-validation required)
```

### 3.2 Java↔C Integration: ProcessBuilder

The C component is a standalone executable compiled from `flight_tester.c`.
Java invokes it as a child process using `ProcessBuilder`. Communication happens
through **stdin** (Java writes a JSON payload) and **stdout** (C writes a JSON result back).

```
Java (FlightPlanTestRunner)
    │
    ├─ builds FlightPlanTestInput  (from parsed FlightPlan model objects)
    ├─ normalises units            (ft→m, l→kg via JET A-1 density 0.804 kg/l)
    ├─ serialises to JSON string
    │
    └─► ProcessBuilder → flight_tester[.exe]   (C executable)
                              │  ← reads JSON from stdin
                              │  → writes JSON result to stdout
                              │  → exit 0 (pass) | 1 (fail) | 2 (parse error)
                              │
    ◄─ reads stdout ──────────┘
    │
    └─ deserialises FlightPlanTestResult
         ├─ passed: true / false
         └─ issues: [ { "field": "...", "message": "..." }, ... ]
```

**Windows compatibility:** `FlightPlanTestRunner.resolveExecutable()` automatically
appends `.exe` on Windows if not already present, so the same VM option
(`-Dflight.tester.path=.../bin/flight_tester`) works on all platforms.

**Why ProcessBuilder and not JNI?**

| Criterion | ProcessBuilder | JNI |
|---|---|---|
| Complexity | Low — standard Java API | High — requires native headers and bindings |
| Crash isolation | C crash does not kill JVM | C crash can kill the entire JVM |
| Testability | C program testable independently | Requires full JVM context |
| Project alignment | Consistent with US100 (fork/pipes/signals in C) | Different paradigm |

### 3.3 Key Decisions

| Decision | Rationale |
|---|---|
| DSL validation first, C test second | A broken parse tree cannot produce reliable domain data; failing fast avoids misleading physics results. |
| ProcessBuilder for Java↔C bridge | Crash isolation, independent testability, consistent with US100. |
| JSON over stdin/stdout | Simple, language-agnostic, no shared memory or IPC complexity. |
| C component in `lprog/src/main/c/` | LPROG responsibility; co-located with the DSL module. |
| Makefile compiled via Maven exec plugin | Reproducible build without manual `make` invocation. |
| `ValidateFlightPlanService` as the stable facade | Single entry point; Eixo 2 controller calls it without any changes to LPROG code. |
| `FlightPlanTestInputBuilder` handles unit normalisation | Isolates the conversion logic (ft→m, l→kg); C component always receives SI units. |
| Domain status stored on `FlightPlan` entity | Allows US100 (simulation) to query validation status before executing. |

---

## 4. Design

### 4.1 Full Pipeline Overview

```
[Pilot App — TCP request]
        │  (Eixo 2)
        ▼
ValidateFlightPlanController
        │  fetches FlightPlan + DSL file path from repository
        │
        ▼
ValidateFlightPlanService.validate(Path)      ← Eixo 1 facade (LPROG)
        │
        ├─► FlightDSLValidator.validate()
        │       ├─ LEXICAL errors?   ──► US085Result.dslFailure()   ─► STOP
        │       ├─ SYNTACTIC errors? ──► US085Result.dslFailure()   ─► STOP
        │       └─ SEMANTIC errors?  ──► US085Result.dslFailure()   ─► STOP
        │
        ├─► FlightDSLVisitor → List<FlightPlan>
        │
        ├─► FlightPlanTestInputBuilder.from()
        │       └─ unit normalisation (ft→m, l→kg)
        │
        ├─► FlightPlanTestRunner.run()
        │       └─► ProcessBuilder → flight_tester[.exe]
        │                ← JSON stdin / JSON stdout →
        │
        └─► US085Result  (SUCCESS | DSL_FAILURE | PHYSICAL_FAILURE | EXECUTION_ERROR | IO_ERROR)
        │
        ▼  (Eixo 2)
ValidateFlightPlanController
        │  updates FlightPlan.status → VALIDATED / FAILED
        │  saves via repository
        │
        ▼
TCP Response → Pilot App
```

### 4.2 Main Flow — Eixo 1 (Sequence)

```
ValidateFlightPlanService.validate(filePath)
    │
    ├─► FlightDSLValidator.validate(filePath)  →  ValidationResult
    │       [if not valid] → return US085Result.dslFailure(errors)
    │
    ├─► ValidationResult.getFlightPlans()      →  List<FlightPlan>
    │
    ├─► FlightPlanTestInputBuilder.from(plan)  →  FlightPlanTestInput
    │
    ├─► FlightPlanTestRunner.run(input)
    │       ├─ resolveExecutable()  (adds .exe on Windows)
    │       ├─ ProcessBuilder.start()
    │       ├─ write JSON to stdin
    │       ├─ read JSON from stdout
    │       └─ waitFor(10s timeout)   →  FlightPlanTestResult
    │
    └─► return US085Result
```

### 4.3 Main Flow — Eixo 2 (Sequence)

```
Pilot App (TCP)
    │
    └─► ValidateFlightPlanRequestHandler.handle(request)
            │
            ├─► AuthService.ensureHasRole(PILOT)
            │
            ├─► ValidateFlightPlanController.validate(flightPlanId)
            │       │
            │       ├─► FlightPlanRepository.findById(flightPlanId)
            │       ├─► ValidateFlightPlanService.validate(dslFilePath)  [Eixo 1]
            │       ├─► flightPlan.setStatus(VALIDATED | FAILED)
            │       └─► FlightPlanRepository.save(flightPlan)
            │
            └─► TCP response  →  Pilot App
                    ├─ success: "Flight plan <id> validated successfully."
                    └─ failure: list of DSL errors or physical issues
```

### 4.4 Alternative Flows

**DSL file not found:**
`FlightDSLValidator` throws `IOException` → `US085Result.ioError(message)` → TCP error response.

**Lexical / syntactic / semantic DSL errors:**
Pipeline stops, C never invoked → `US085Result.dslFailure(errors)` → error list returned to Pilot.

**C executable not found (binary not compiled):**
`FlightPlanTestRunner` catches the `IOException` → `US085Result.executionError(...)`.

**C process timeout:**
`process.waitFor(10, SECONDS)` returns false → process destroyed → execution error result.

**Physical test fails:**
`FlightPlanTestResult.isPassed() == false` → `US085Result.physicalFailure(issues)` → `FlightPlan.status = FAILED`.

**Unauthenticated or unauthorised user (Eixo 2):**
`AuthService` throws `SecurityException` → TCP authorisation error response.

---

## 5. Implementation

### 5.1 Package Structure

#### `lprog` module — Eixo 1 (LPROG)

```
lprog/
├── src/
│   ├── main/
│   │   ├── antlr4/pt/isep/sem4pi/lprog/
│   │   │   └── FlightDSL.g4                             EXISTS (Sprint 2)
│   │   ├── c/flight_tester/
│   │   │   ├── flight_tester.c                          NEW — C main entry point
│   │   │   ├── physics.h / physics.c                    NEW — physical coherence checks
│   │   │   ├── json_parser.h / json_parser.c            NEW — minimal JSON read/write
│   │   │   └── Makefile                                 NEW — produces bin/flight_tester[.exe]
│   │   └── java/pt/isep/sem4pi/lprog/
│   │       ├── FlightDSLValidator.java                  EXISTS (Sprint 2)
│   │       ├── Main.java                                UPDATED — accepts --validate arg
│   │       ├── application/
│   │       │   ├── ValidateFlightPlanService.java        NEW — full pipeline facade
│   │       │   └── US085Result.java                     NEW — aggregated result type
│   │       ├── testing/
│   │       │   ├── FlightPlanTestInput.java              NEW — DTO sent to C (JSON)
│   │       │   ├── FlightPlanTestResult.java             NEW — DTO received from C (JSON)
│   │       │   ├── PhysicalIssue.java                   NEW — one physical validation error
│   │       │   ├── FlightPlanTestInputBuilder.java       NEW — model→DTO + unit normalisation
│   │       │   └── FlightPlanTestRunner.java             NEW — ProcessBuilder bridge
│   │       ├── error/                                   EXISTS (Sprint 2)
│   │       ├── listener/                                EXISTS (Sprint 2)
│   │       ├── model/                                   EXISTS (Sprint 2)
│   │       ├── semantic/                                EXISTS (Sprint 2)
│   │       └── visitor/                                 EXISTS (Sprint 2)
│   └── test/java/pt/isep/sem4pi/lprog/
│       └── testing/
│           ├── FlightPlanTestRunnerTest.java             NEW
│           └── FlightPlanTestInputBuilderTest.java       NEW
└── pom.xml                                              UPDATED — exec-maven-plugin + surefire
```

#### `aisafe.core` module — Eixo 2 (EAPLI)

```
aisafe.core/src/main/java/eapli/aisafe/
└── flightplanmanagement/
    ├── application/
    │   └── ValidateFlightPlanController.java            NEW
    └── domain/
        └── FlightPlanStatus.java                       NEW (or update existing)
            // DRAFT | VALIDATED | FAILED
```

#### `aisafe.app.server` module — Eixo 2 (EAPLI)

```
aisafe.app.server/src/main/java/eapli/aisafe/app/server/
└── handlers/
    └── ValidateFlightPlanRequestHandler.java           NEW
```

#### `aisafe.app.pilot.console` module — Eixo 2 (EAPLI)

```
aisafe.app.pilot.console/src/main/java/eapli/aisafe/app/pilot/console/
└── presentation/flightplans/
    └── ValidateFlightPlanUI.java                       NEW
```

### 5.2 Class Responsibilities

#### `ValidateFlightPlanService` (LPROG — NEW)

The stable facade for the entire Eixo 1 pipeline. Called by `Main` for standalone
demo and by the EAPLI controller in Eixo 2. No changes needed between the two axes.

```java
public US085Result validate(Path filePath)    // from file
public US085Result validateString(String dsl) // from string (useful in tests)
```

#### `FlightPlanTestInputBuilder` (LPROG — NEW)

Converts `FlightPlan` (from the DSL visitor) into `FlightPlanTestInput`, handling
unit normalisation:

- Altitude: `ft × (1/3.28084)`, `km × 1000`, `m` unchanged.
- Fuel: `l × 0.804` (JET A-1 density at 15 °C), `kg` unchanged.
- Segment modes are uppercased to match C constants (`CLIMB`, `CRUISE`, `DESCEND`).

Helper methods (`normaliseAltitude`, `normaliseFuel`) are `package-private` (`static`
without modifier) to allow direct unit testing.

#### `FlightPlanTestRunner` (LPROG — NEW)

Owns the `ProcessBuilder` logic. Resolves the executable path via:

1. System property `flight.tester.path` (set by VM options / Maven Surefire).
2. Default: `src/main/c/flight_tester/bin/flight_tester`.

On Windows, `.exe` is appended automatically if not already present.
Handles timeout (10 s), exit code 2 (C parse error), and empty stdout.

#### `ValidateFlightPlanController` (EAPLI — NEW)

```java
public US085Result validate(String flightPlanId) {
    FlightPlan plan = repository.findById(flightPlanId);
    Path dslFile    = plan.getDslFilePath();
    US085Result result = service.validate(dslFile);  // calls Eixo 1 service
    plan.setStatus(result.isSuccess()
        ? FlightPlanStatus.VALIDATED
        : FlightPlanStatus.FAILED);
    repository.save(plan);
    return result;
}
```

#### `ValidateFlightPlanRequestHandler` (EAPLI — NEW)

TCP handler that:
1. Reads the `VALIDATE_FLIGHT_PLAN` request from the TCP stream.
2. Checks authentication (`AuthService.ensureHasRole(PILOT)`).
3. Delegates to `ValidateFlightPlanController`.
4. Serialises `US085Result` and writes the TCP response.

#### `ValidateFlightPlanUI` (EAPLI — NEW)

Console UI in the Pilot App that:
1. Prompts the Pilot for the flight plan ID (or file path).
2. Sends a `VALIDATE_FLIGHT_PLAN` TCP request to the server.
3. Displays the result — success message or error list.

### 5.3 C Component Structure

```
flight_tester.c   — main(): read stdin → parse JSON → validate → write stdout
physics.c / .h    — validate_flight(), check_fuel(), check_segment()
json_parser.c / .h — read_stdin(), parse_flight_input(), write_output()
Makefile          — make (release), make test (smoke tests), make clean
```

**Physical checks performed by `physics.c`:**

| Check | Rule |
|---|---|
| Fuel positive | `fuelKg > 0` |
| Altitude non-negative | `altM >= 0` |
| Segment coords distinct | `Δlat < 0.0001 AND Δlon < 0.0001 AND Δalt < 0.0001` → issue |
| CLIMB coherence | `endAlt > startAlt` |
| DESCEND coherence | `endAlt < startAlt` |
| CRUISE coherence | `abs(endAlt - startAlt) <= 50 m` |

> Precise fuel consumption (Breguet range equation) is **out of scope** per §3.4.6
> of the project requirements. The C component applies simplified coherence checks only.

### 5.4 JSON Protocol (Java↔C Contract)

**Input (Java → C via stdin):**
```json
{
  "flightId": "TP100",
  "legs": [
    {
      "index": 0,
      "departureAirport": "LPPT",
      "arrivalAirport": "LPPR",
      "fuelKg": 14500.0,
      "segments": [
        {
          "index": 0,
          "mode": "CLIMB",
          "startLat": 38.78, "startLon": -9.13, "startAltM": 0.0,
          "endLat":   38.85, "endLon":  -9.00, "endAltM": 8000.0
        }
      ]
    }
  ]
}
```

**Output (C → Java via stdout):**
```json
{ "passed": true, "issues": [] }

{ "passed": false, "issues": [
    { "field": "leg[0].fuel", "message": "Fuel quantity must be strictly positive, but got -500.00 kg." }
  ]
}
```

**Exit codes:** `0` = passed, `1` = one or more issues, `2` = JSON parse error.

### 5.5 Maven Integration (`pom.xml`)

```xml
<!-- Compile C component automatically during mvn compile -->
<plugin>
  <groupId>org.codehaus.mojo</groupId>
  <artifactId>exec-maven-plugin</artifactId>
  <version>3.1.0</version>
  <executions>
    <execution>
      <id>compile-c-component</id>
      <phase>compile</phase>
      <goals><goal>exec</goal></goals>
      <configuration>
        <executable>make</executable>
        <workingDirectory>${project.basedir}/src/main/c/flight_tester</workingDirectory>
      </configuration>
    </execution>
    <execution>
      <id>clean-c-component</id>
      <phase>clean</phase>
      <goals><goal>exec</goal></goals>
      <configuration>
        <executable>make</executable>
        <arguments><argument>clean</argument></arguments>
        <workingDirectory>${project.basedir}/src/main/c/flight_tester</workingDirectory>
      </configuration>
    </execution>
  </executions>
</plugin>

<!-- Pass executable path to tests automatically -->
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <version>3.2.2</version>
  <configuration>
    <systemPropertyVariables>
      <flight.tester.path>
        ${project.basedir}/src/main/c/flight_tester/bin/flight_tester
      </flight.tester.path>
    </systemPropertyVariables>
  </configuration>
</plugin>
```

---

## 6. Demonstration

### Eixo 1 — Standalone (LPROG)

```bash
# 1. Compile Java + C
cd lprog
mvn clean compile

# 2. Run smoke tests on the C component directly
cd src/main/c/flight_tester
make test

# 3. Run the full pipeline via Main
# (from the lprog module root, with the path set in Run Configuration VM options)
# VM options: -Dflight.tester.path=src/main/c/flight_tester/bin/flight_tester
# Program arguments: --validate

# Actual output observed:
# === Step 1: Parsing DSL ===
# DSL Import successful! No syntax or basic semantic errors.
# === Step 2: Running Physics Validation (C Component) ===
# Validating flight: AA123
#   [SUCCESS] Passed all physical constraints in C.
# Process finished with exit code 0
```

### Eixo 2 — Remote via Pilot App (EAPLI)

```bash
# Run the server
mvn -pl aisafe.app.server exec:java

# Run the Pilot App client in a separate terminal
mvn -pl aisafe.app.pilot.console exec:java

# Login with:
#   pilot@tap.pt
#   Pilot123

# Menu path:
#   1. Login
#   2. Flight plan management
#   3. Validate flight plan

# Example input:
#   Flight Plan ID: TP100

# Expected output (success):
#   Flight plan TP100 has been validated successfully.

# Expected output (DSL failure):
#   Validation failed — 1 error(s):
#     [SEMANTIC] Line 8: Arrival airport of leg 0 (LPPR) does not match
#                        departure airport of leg 1 (LEMD).

# Expected output (physical failure):
#   DSL valid. Physical test failed — 1 issue(s):
#     [leg[0].segment[0].altitude] CLIMB segment must have end altitude
#     (100.00 m) > start altitude (8000.00 m).
```

---

## 7. Tests

### Eixo 1 — C component (smoke tests via `make test`)

- Valid flight: `{ "passed": true, "issues": [] }`, exit 0. ✅
- Negative fuel: issue on `leg[0].fuel`, exit 1. ✅
- CLIMB with descending altitude: issue mentioning `CLIMB`, exit 1. ✅
- DESCEND with ascending altitude: issue mentioning `DESCEND`, exit 1. ✅
- CRUISE with large altitude change: issue mentioning `CRUISE`, exit 1. ✅
- Identical start/end coordinates: issue on `coordinates`, exit 1. ✅
- Malformed JSON: exit 2, error on stderr. ✅

### Eixo 1 — Java unit tests

**`FlightPlanTestInputBuilderTest`:**
- `normaliseAltitude` — ft, km, m, null, unknown unit.
- `normaliseFuel` — kg, l, liter, null.
- `from(null)` returns null.
- Flight ID, leg count, airport codes, segment count are correctly mapped.
- Segment mode is uppercased.
- Fuel in litres is converted to kg.
- `toJson()` contains flightId, airport codes, and segment mode.

**`FlightPlanTestRunnerTest`:**
- JSON parsing: passed, failed with issues, multiple issues, escaped quotes.
- Invalid JSON and empty string return execution error.
- Windows `.exe` suffix: added when missing, unchanged when already present, unchanged on Linux.
- Process integration: valid flight passes, negative fuel fails with fuel issue, CLIMB wrong direction fails.

### Eixo 2 — Integration tests (EAPLI)

- Authenticated PILOT can trigger validation via TCP.
- Unauthenticated request is rejected.
- Non-PILOT role is rejected.
- Successful validation updates `FlightPlan.status` to `VALIDATED`.
- Failed validation updates `FlightPlan.status` to `FAILED`.
- Flight plan not found returns appropriate error response.
- Inserting weather data (US082) after validation reverts status to `DRAFT`.

---

## 8. Observations

### Eixo 1 status — complete ✅

All LPROG components are implemented, compiled, and tested. The pipeline runs
end-to-end: a valid DSL file passes both DSL validation and the C physical test.
The `ValidateFlightPlanService` is the stable API boundary — Eixo 2 calls it
without any modifications to LPROG code.

### Eixo 2 status — pending US080 ⏳

The EAPLI integration requires the `FlightPlan` domain entity and repository
from US080. The design is complete and the three new files
(`ValidateFlightPlanController`, `ValidateFlightPlanRequestHandler`,
`ValidateFlightPlanUI`) can be implemented as soon as US080 is available.

### US082 interaction

Per the project requirements, inserting weather data into a flight plan that has
already been tested voids the test result. The EAPLI controller for US082 must
therefore set `FlightPlan.status = DRAFT` after a successful weather insert,
forcing the Pilot to re-run validation. This is enforced at the domain level,
not in this controller.

### Windows build note

The C component must be compiled with GCC on Windows (e.g. via Chocolatey:
`choco install mingw`). The Makefile produces `bin/flight_tester` on Linux/macOS
and the same binary is named `flight_tester.exe` when compiled on Windows.
`FlightPlanTestRunner` handles the `.exe` suffix automatically.