# US083 - Flight DSL Specification and Validation

## 1. Context

This user story belongs to the Languages and Programming (LPROG) course unit and is part of Sprint 2 of the Integrative Project.

The goal is to formally specify and implement a Domain Specific Language (DSL) — the **Flight DSL** — that allows flight plans to be described in a structured, machine-readable textual format.

The Flight DSL will be used by the system to import, validate, and process flight plans. It is a core component of the AISafe system, as flight plans cannot be manually created in their full form without a valid DSL representation.

This functionality is a technical infrastructure story: it does not directly expose a UI to an end user, but it provides the parsing and validation engine used by US081 (Create a flight plan from a file) and US085 (Test/validate flight plan).

## 1.1 List of Issues

- **Analysis:** Understand the structure of a flight plan, identify the required lexical elements, syntactic constructs, and semantic validation rules described in the system specification.
- **Design:** Define the grammar structure, the pipeline architecture (lexer → parser → visitor/listener → semantic validator), and the internal representation model.
- **Implement:** Implement the formal ANTLR grammar, the parse tree traversal (visitor and listener), the domain object model, the semantic validator, and the error reporting mechanism.
- **Test:** Validate the grammar against valid and invalid inputs, verify that all semantic rules are enforced, and confirm that error messages include type, line, and column information.

## 2. Requirements

**US083** As a Project Manager, I want the team to specify and implement the Flight Description DSL, so that flight plans can be formally defined and validated.

### Acceptance Criteria

- **US083.1:** The informal lexical and syntactic specification of the DSL is documented.
- **US083.2:** A formal grammar is defined using ANTLR4, including both lexer and parser rules.
- **US083.3:** The system performs lexical and syntactic validation of flight plan files.
- **US083.4:** An internal representation (domain objects) is produced from valid inputs.
- **US083.5:** Invalid inputs generate clear and informative error messages, including error type, line number, and column number.
- **US083.6:** At least one ANTLR visitor and one ANTLR listener must be used in the implementation.
- **US083.7:** The implementation language must be Java.

### Out of Scope

The following aspects are explicitly excluded from this user story:

- Flight physics calculations and fuel consumption estimation.
- Safety or collision detection.
- Weather impact computation.
- Flight simulation.
- The method of integration of the ANTLR solution into the broader system (this is addressed at project architecture level).

### Dependencies / References

- **US081** — Create a flight plan from a file. Depends on US083 to perform lexical, syntactic, and semantic validation of the uploaded file.
- **US085** — Test/validate flight plan. The validation component relies on the DSL validation provided by US083.
- **NFR11** — LPROG Assessment. The quality of the DSL design, grammar clarity, semantic validation completeness, and error reporting quality are central assessment criteria.

## 3. Analysis

### 3.1 Understanding

A **flight plan** represents a complete flight operation. It is composed of one or more **legs**, where each leg is a single non-stop journey segment between two airports.

The DSL must be able to represent the following concepts:

- **Flight** — top-level construct. Has a unique identifier, a type (regular or charter), and one or more legs.
- **Leg** — a single takeoff-to-landing segment. Has departure information, arrival information, a route definition, one or more flight segments, and fuel information.
- **Segment** — a linear path between two geographic points. Has start coordinates, end coordinates, altitude, and optionally wind information.
- **Departure / Arrival** — defined by an airport code (IATA or ICAO) and optionally a date and time.
- **Fuel** — a positive numeric quantity with a unit (e.g., `kg` or `l`).

The language is **hierarchical and block-based**, using curly braces `{ }` to delimit blocks, and is designed to be human-readable.

**Keywords are case-insensitive.** Identifiers and literals are case-sensitive.

#### Informal Lexical Specification

The Flight DSL is composed of the following lexical elements:

| Element | Description | Examples |
|---|---|---|
| Keywords | Reserved words defining structure | `flight`, `leg`, `departure`, `arrival`, `route`, `segment`, `fuel`, `type`, `regular`, `charter` |
| Identifiers | Named entities, flight IDs | `AA123`, `FLIGHT_001` |
| Airport codes | IATA (3 letters) or ICAO (4 letters) | `OPO`, `LPPR`, `MAD`, `LEMD` |
| Numeric literals | Integers and floating-point numbers | `40200`, `9249.5`, `-8.685` |
| Date literals | Calendar date values | `2026-05-17` |
| Time literals | Time of day values | `10:30`, `08:00:00` |
| Units | Qualify numeric values | `kg`, `l`, `m`, `km`, `m/s`, `knot` |
| Separators | Structural symbols | `{ }`, `( )`, `[ ]`, `,`, `:`, `;` |
| Whitespace | Ignored, used for readability | spaces, tabs, newlines |
| Comments | Inline documentation, ignored by parser | `// comment`, `/* block comment */` |

#### Informal Syntactic Specification

The general structure of a flight plan file is as follows:

```
flight <identifier> {
    type: <regular | charter>;

    leg {
        departure: <airport_code> [at <date> <time>];
        arrival:   <airport_code> [at <date> <time>];

        route: <origin_airport> -> <destination_airport>;

        segment {
            mode: <climb | cruise | descend>;
            start: (<latitude>, <longitude>, <altitude> <unit>);
            end:   (<latitude>, <longitude>, <altitude> <unit>);
            [wind: <direction> <speed> <unit>;]
        }

        // more segments...

        fuel: <quantity> <unit>;
    }

    // more legs...
}
```

Multiple flights may be defined in a single file, each as a top-level `flight` block.

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| Use ANTLR4 as the grammar tool | Explicitly required by the project specification. |
| Combined grammar file (lexer + parser in `FlightDSL.g4`) | Simpler for a language of this size; avoids unnecessary file fragmentation. |
| Use domain objects as internal representation | More idiomatic in Java than raw AST nodes; directly usable by US081 and US085 without conversion. |
| Use a Visitor to build domain objects | Visitors provide clean separation between tree traversal and object construction logic. |
| Use a Listener for secondary concerns | The Listener will be used for collecting metadata (e.g., line numbers, context tracking) to support error reporting. Both are required by the specification. |
| Collect all semantic errors before reporting | The specification requires all semantic errors to be collected in a single execution, not fail-fast. |
| Normalize airport codes to uppercase | Prevents duplicate detection issues caused by casing differences. |
| Support comments in the DSL | Improves expressiveness and real-world usability; compatible with the Core DSL. |

## 4. Design

### 4.1 Pipeline Architecture

The Flight DSL validation follows a sequential pipeline:

```
Input file (.dsl)
       │
       ▼
 ANTLR Lexer (FlightDSL.g4)
       │  lexical errors → DSLErrorListener
       ▼
 ANTLR Parser (FlightDSL.g4)
       │  syntax errors → DSLErrorListener
       ▼
 Parse Tree
       │
       ├──► FlightDSLListener  (metadata / line tracking)
       │
       └──► FlightDSLVisitor   (builds domain objects)
                   │
                   ▼
           FlightPlan (domain model)
                   │
                   ▼
           SemanticValidator
                   │  semantic errors → List<DSLError>
                   ▼
       Result: FlightPlan | List<DSLError>
```

### 4.2 Main Flow

```
Caller -> FlightDSLValidator: validate(filePath)
FlightDSLValidator -> ANTLR CharStream: fromPath(filePath)
FlightDSLValidator -> FlightDSLLexer: new FlightDSLLexer(charStream)
FlightDSLValidator -> DSLErrorListener: attach to lexer
FlightDSLValidator -> CommonTokenStream: new CommonTokenStream(lexer)
FlightDSLValidator -> FlightDSLParser: new FlightDSLParser(tokenStream)
FlightDSLValidator -> DSLErrorListener: attach to parser
FlightDSLValidator -> FlightDSLParser: flightFile() [parse]
FlightDSLValidator -> FlightDSLListener: walk parse tree
FlightDSLValidator -> FlightDSLVisitor: visit(parseTree)
FlightDSLVisitor --> FlightDSLValidator: FlightPlan
FlightDSLValidator -> SemanticValidator: validate(flightPlan)
SemanticValidator --> FlightDSLValidator: List<DSLError>
FlightDSLValidator --> Caller: ValidationResult (FlightPlan or errors)
```

### 4.3 Alternative Flows

**Lexical error (e.g. unknown token):**
```
DSLErrorListener captures error with line and column.
Pipeline is aborted after lexer phase.
Caller receives List<DSLError> with LEXICAL type.
```

**Syntactic error (e.g. missing closing brace):**
```
DSLErrorListener captures error with line and column.
Pipeline is aborted after parser phase.
Caller receives List<DSLError> with SYNTACTIC type.
```

**Semantic error (e.g. arrival airport does not match next leg departure):**
```
SemanticValidator collects all violations.
All errors are reported in a single pass.
Caller receives List<DSLError> with SEMANTIC type.
```

**File not found or unreadable:**
```
FlightDSLValidator throws IOException.
Caller handles the exception.
```

## 5. Implementation

### 5.1 File Structure

```
lprog/
│
├── src/main/antlr4/
│   └── pt/isep/sem4pi/lprog/
│       └── FlightDSL.g4                    ← [1240895] gramática completa (lexer + parser)
│
├── src/main/java/
│   └── pt/isep/sem4pi/lprog/
│       │
│       ├── FlightDSLValidator.java          ← [1240895] orquestra o pipeline completo
│       │
│       ├── error/
│       │   ├── DSLError.java               ← [1240895] modelo de erro (tipo, linha, coluna, mensagem)
│       │   └── DSLErrorListener.java       ← [1240895] custom ANTLR error listener
│       │
│       ├── listener/
│       │   └── FlightDSLListener.java      ← [1220708] listener (metadata, line tracking)
│       │
│       ├── visitor/
│       │   └── FlightDSLVisitor.java       ← [1220708] constrói os domain objects
│       │
│       ├── semantic/
│       │   └── SemanticValidator.java      ← [1220708] todas as regras semânticas
│       │
│       └── model/                          ← [1220708] representação interna do plano de voo
│           ├── FlightPlan.java
│           ├── FlightLeg.java
│           ├── FlightSegment.java
│           ├── Departure.java
│           ├── Arrival.java
│           └── Fuel.java
│
├── src/test/java/
│   └── pt/isep/sem4pi/lprog/
│       ├── FlightDSLParserTest.java         ← [1240895] testes léxico-sintáticos
│       └── SemanticValidatorTest.java       ← [1220708] testes semânticos
│
└── src/main/resources/
    └── examples/
        ├── valid_opo_mad.dsl               ← exemplo válido (baseado no JSON fornecido)
        └── invalid_missing_fuel.dsl        ← exemplo inválido para testes de erro
```

### 5.2 Grammar Overview (`FlightDSL.g4`)

The grammar defines the following top-level parser rules:

| Rule | Description |
|---|---|
| `flightFile` | Root rule. One or more `flightPlan` definitions. |
| `flightPlan` | A `flight` block with identifier, type, and one or more legs. |
| `legBlock` | A `leg` block with departure, arrival, route, segments, and fuel. |
| `departureBlock` | Departure airport code and optional date/time. |
| `arrivalBlock` | Arrival airport code and optional date/time. |
| `routeBlock` | Origin and destination airport codes with `->` notation. |
| `segmentBlock` | A flight segment with mode, start, end, and optional wind. |
| `fuelBlock` | A fuel quantity with unit. |
| `coordinate` | A `(latitude, longitude, altitude unit)` triplet. |

Key lexer tokens include:

| Token | Pattern |
|---|---|
| `FLIGHT` | `[Ff][Ll][Ii][Gg][Hh][Tt]` (case-insensitive) |
| `LEG` | `[Ll][Ee][Gg]` |
| `IDENTIFIER` | `[A-Za-z][A-Za-z0-9_]*` |
| `IATA_CODE` | `[A-Z]{3}` |
| `ICAO_CODE` | `[A-Z]{4}` |
| `INTEGER` | `[0-9]+` |
| `FLOAT` | `[0-9]+ '.' [0-9]+` |
| `DATE` | `[0-9]{4} '-' [0-9]{2} '-' [0-9]{2}` |
| `TIME` | `[0-9]{2} ':' [0-9]{2} (':' [0-9]{2})?` |
| `UNIT` | `'kg' \| 'l' \| 'm' \| 'km' \| 'm/s' \| 'knot'` |
| `WS` | `[ \t\r\n]+` → skip |
| `LINE_COMMENT` | `'//' ~[\r\n]*` → skip |
| `BLOCK_COMMENT` | `'/*' .*? '*/'` → skip |

### 5.3 Domain Model

The internal representation produced by the Visitor:

**`FlightPlan`**
- `String identifier`
- `String type` (regular | charter)
- `List<FlightLeg> legs`

**`FlightLeg`**
- `Departure departure`
- `Arrival arrival`
- `String routeOrigin`
- `String routeDestination`
- `List<FlightSegment> segments`
- `Fuel fuel`

**`FlightSegment`**
- `String mode` (climb | cruise | descend)
- `double startLatitude`, `startLongitude`, `startAltitude`
- `String startAltitudeUnit`
- `double endLatitude`, `endLongitude`, `endAltitude`
- `String endAltitudeUnit`
- `Double windDirection` (optional)
- `Double windSpeed` (optional)
- `String windUnit` (optional)

**`Departure`**
- `String airportCode`
- `LocalDate date` (optional)
- `LocalTime time` (optional)

**`Arrival`**
- `String airportCode`
- `LocalDate date` (optional)
- `LocalTime time` (optional)

**`Fuel`**
- `double quantity`
- `String unit`

### 5.4 Semantic Validation Rules

The `SemanticValidator` enforces the following rules. All violations are collected before reporting.

**Flight-level rules:**
- The flight identifier must be unique within the file.
- The flight type must be one of: `regular`, `charter`.

**Leg-level rules:**
- Each leg must contain exactly one departure and one arrival.
- Each leg must contain at least one segment.
- Fuel quantity must be strictly positive.

**Leg sequence coherence:**
- The arrival airport of leg N must match the departure airport of leg N+1.
- If times are provided, the arrival time of leg N must precede the departure time of leg N+1.

**Route coherence:**
- The route origin must match the first leg's departure airport.
- The route destination must match the last leg's arrival airport.
- The same airport cannot appear twice in a flight.

**Segment coherence:**
- Segment start and end coordinates must be different.
- Each segment must belong to exactly one leg.

**Domain consistency:**
- Airport codes must respect the defined format (IATA: 3 letters, ICAO: 4 letters).
- Numeric values (altitudes, fuel quantities, wind speed) must be positive.
- Date and time values must represent valid calendar values.

### 5.5 Error Reporting

Every error is represented as a `DSLError` with the following fields:

| Field | Type | Description |
|---|---|---|
| `type` | `ErrorType` | `LEXICAL`, `SYNTACTIC`, or `SEMANTIC` |
| `line` | `int` | Line number in the source file |
| `column` | `int` | Column number in the source file |
| `message` | `String` | Human-readable description of the error |

Example error messages:

```
[LEXICAL]   line 3:12  — Unexpected character '@'
[SYNTACTIC] line 7:0   — Missing closing '}' for leg block
[SEMANTIC]  line 15:0  — Arrival airport 'MAD' of leg 1 does not match departure airport 'LIS' of leg 2
[SEMANTIC]  line 22:0  — Fuel quantity must be strictly positive, got: -500.0
```

### 5.6 Maven Dependencies

Add to the module's `pom.xml`:

```xml
<!-- ANTLR4 runtime -->
<dependency>
    <groupId>org.antlr</groupId>
    <artifactId>antlr4-runtime</artifactId>
    <version>4.13.1</version>
</dependency>

<!-- ANTLR4 Maven plugin (generates Java sources from .g4) -->
<plugin>
    <groupId>org.antlr</groupId>
    <artifactId>antlr4-maven-plugin</artifactId>
    <version>4.13.1</version>
    <executions>
        <execution>
            <goals>
                <goal>antlr4</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## 6. Demonstration

Given the following example input file (`valid_opo_mad.dsl`), based on the flight data provided in the project:

```
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

**Expected result:**

```
Validation successful.
Flight plan 'AA123' loaded:
  - Type: regular
  - Legs: 1
  - Leg 1: OPO -> MAD
    - Segments: 3 (climb, cruise, descend)
    - Fuel: 40200.0 kg
```

**Example of invalid input and corresponding errors:**

```
flight AA123 {
    type: express;   // invalid type

    leg {
        departure: OPO;
        arrival: MAD;
        route: LIS -> MAD;  // route origin doesn't match departure

        segment {
            mode: climb;
            start: (41.26, -8.68, 69 m);
            end:   (41.26, -8.68, 69 m);  // start == end
        }

        fuel: -500 kg;  // negative fuel
    }
}
```

```
[SEMANTIC] line 2:10  — Invalid flight type 'express'. Must be 'regular' or 'charter'.
[SEMANTIC] line 7:16  — Route origin 'LIS' does not match leg departure airport 'OPO'.
[SEMANTIC] line 12:0  — Segment start and end coordinates must be different.
[SEMANTIC] line 15:16 — Fuel quantity must be strictly positive, got: -500.0.
```

## 7. Tests

### 7.1 Lexical and Syntactic Tests (`FlightDSLParserTest.java`)

- Valid flight plan parses without errors.
- Unknown token produces a LEXICAL error with correct line and column.
- Missing closing brace produces a SYNTACTIC error.
- Case-insensitive keywords are accepted (`FLIGHT`, `Flight`, `flight`).
- IATA and ICAO codes are recognized correctly.
- Integer and floating-point values are correctly tokenized.
- Date and time literals are correctly recognized.
- Comments (line and block) are correctly ignored.
- Empty file produces a SYNTACTIC error.
- Multiple flights in a single file are parsed correctly.

### 7.2 Semantic Tests (`SemanticValidatorTest.java`)

- Valid flight plan passes all semantic rules.
- Duplicate flight identifier in same file is rejected.
- Invalid flight type is rejected.
- Leg with no segments is rejected.
- Negative fuel quantity is rejected.
- Zero fuel quantity is rejected.
- Arrival airport of leg N not matching departure of leg N+1 is rejected.
- Route origin not matching first leg departure is rejected.
- Route destination not matching last leg arrival is rejected.
- Same airport appearing twice in a flight is rejected.
- Segment with identical start and end coordinates is rejected.
- Invalid airport code format is rejected.
- All semantic errors are collected in a single validation pass (not fail-fast).

## 8. Observations

The implementation of US083 is the foundation for both US081 and US085. It must be completed and tested before those user stories can be integrated.

The grammar should be designed with extensibility in mind. The specification encourages teams to go beyond the Core DSL — possible extensions include additional units, optional wind data per segment, altitude slot definitions, and richer date/time constructs. Any extension must preserve compatibility with the Core DSL and be formally specified in the grammar.

The use of both a Visitor and a Listener is a hard requirement from the specification. The recommended split is: the Visitor is responsible for constructing the domain object tree; the Listener handles secondary concerns such as metadata collection, line number tracking for semantic error reporting, and logging.

The ANTLR Maven plugin generates Java source files from the `.g4` grammar automatically during the build (`mvn generate-sources`). Generated files should not be committed to the repository and should be added to `.gitignore`.