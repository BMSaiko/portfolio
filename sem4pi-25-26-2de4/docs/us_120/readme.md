# US120 - Flight DSL Specification and Validation (Sprint 3)

## 1. Context

This user story belongs to the Languages and Programming (LPROG) course unit and is part of Sprint 3 of the Integrative Project.

US120 is the evolution of US083 (Sprint 2). While US083 established the full DSL pipeline — grammar, lexer, parser, visitor, listener, and basic semantic validation — US120 extends it with **complete semantic validation** as formally specified in section 3.4.5 of the project requirements, and **refactors the semantic layer** into a maintainable, extensible rule-based architecture.

The result is a fully compliant Flight DSL processor that performs lexical, syntactic, and semantic analysis in a single pipeline, collecting all errors before reporting.

## 1.1 List of Issues

- **Analysis:** Map all semantic rules from section 3.4.5 against the existing Sprint 2 implementation to identify gaps.
- **Design:** Define a `SemanticRule` interface and organise rules into cohesive classes; refactor `SemanticValidator` to delegate to the rule chain.
- **Implement:** Implement the missing semantic rules (intra-leg time coherence, airport code format, calendar date validity, flight type explicit check); refactor `SemanticValidator`.
- **Test:** Add unit tests for each new rule class; verify all rules against valid and invalid DSL inputs.

## 2. Requirements

**US120** As a Project Manager, I want the team to specify and implement the Flight Description DSL, so that flight plans can be formally defined and validated.

### Acceptance Criteria

- **US120.1:** The informal lexical and syntactic specification of the DSL is documented.
- **US120.2:** A formal grammar is defined using ANTLR4, including both lexer and parser rules.
- **US120.3:** The system performs lexical, syntactic, and **semantic** analysis of flight plan files.
- **US120.4:** Listeners and visitors are used in the implementation.
- **US120.5:** An internal representation (AST or domain objects) is produced from valid inputs.
- **US120.6:** Invalid inputs generate clear and informative error messages.

### Differences from US083

| Aspect | US083 (Sprint 2) | US120 (Sprint 3) |
|---|---|---|
| Semantic validation | Partial (inline in `SemanticValidator`) | Complete (all rules from §3.4.5) |
| Listener/Visitor | Implicit requirement | Explicitly required |
| Semantic architecture | Single monolithic method | Rule chain (`SemanticRule` interface) |
| Intra-leg time coherence | Not implemented | ✅ Implemented |
| Airport code format | Implicit (grammar) | ✅ Explicit semantic rule |
| Calendar date validity | Not implemented | ✅ Implemented |
| Flight type check | Grammar-only | ✅ Explicit semantic rule with domain message |

### Out of Scope

The following aspects are explicitly excluded:

- Flight physics calculations and fuel consumption estimation.
- Safety or collision detection.
- Weather impact computation.
- Flight simulation.
- The method of integration of the ANTLR solution into the broader system.

### Dependencies / References

- **US083** — Sprint 2 foundation. US120 builds directly on it; the grammar, pipeline, and domain model are unchanged.
- **US121** — Create a flight plan from a file. Depends on US120 for full DSL validation (lexical + syntactic + semantic).
- **US085** — Test/validate flight plan. Integration with the C component; uses the flight plan produced by the US120 pipeline.
- **NFR11** — LPROG Assessment. Completeness and rigor of semantic validation, quality of error reporting, and innovation are central criteria.

## 3. Analysis

### 3.1 Understanding

The domain model and grammar established in US083 remain valid and unchanged. US120 focuses exclusively on completing and restructuring the semantic validation layer.

After a full audit of the Sprint 2 `SemanticValidator` against the rules in section 3.4.5, the following gaps were identified:

| Rule (§3.4.5) | Sprint 2 status | Sprint 3 action |
|---|---|---|
| Flight ID unique in file | ✅ Listener | No change |
| Flight type valid | ✅ Grammar | Added explicit semantic check with domain message |
| Each leg: 1 departure, 1 arrival | ✅ Grammar | Safety net added in `LegLevelRules` |
| Each leg: ≥ 1 segment | ✅ Grammar | Safety net added in `LegLevelRules` |
| Fuel strictly positive | ✅ Inline | Moved to `LegLevelRules` |
| Arrival airport leg N == departure leg N+1 | ✅ Inline | Moved to `LegSequenceRules` |
| Arrival time leg N < departure time leg N+1 | ✅ Inline | Moved to `LegSequenceRules` |
| Route origin == first leg departure | ✅ Inline | Moved to `RouteCoherenceRules` |
| Route destination == last leg arrival | ✅ Inline | Moved to `RouteCoherenceRules` |
| Same airport not visited twice | ✅ Inline | Moved to `RouteCoherenceRules` |
| Segment start != end | ✅ Inline | Moved to `SegmentCoherenceRules` |
| Numeric values positive (altitude, wind) | ✅ Inline | Moved to `SegmentCoherenceRules` |
| **Intra-leg: departure < arrival time** | ❌ Missing | ✅ Added to `LegLevelRules` |
| **Airport code format (IATA/ICAO)** | ⚠️ Grammar only | ✅ Added to `DomainConsistencyRules` |
| **Calendar date validity** | ❌ Missing | ✅ Added to `DomainConsistencyRules` |

### 3.2 Key Decisions

| Decision | Rationale |
|---|---|
| Introduce `SemanticRule` interface | Enables open/closed principle — new rules added without touching existing classes. Each class is cohesive and independently testable. |
| One class per rule category | Mirrors the structure of §3.4.5 directly; easier to map requirements to code during assessment. |
| Keep `FlightDSLListener` for cross-plan rules | The listener has visibility over all `FlightPlan` objects simultaneously, making it the natural place for duplicate ID detection. |
| Grammar unchanged | All Sprint 2 DSL files remain valid; no breaking changes. |
| Intra-leg time check in `LegLevelRules` | It is a per-leg property (departure and arrival of the same leg), distinct from inter-leg sequencing. |
| Airport code format as explicit semantic rule | The grammar guarantees the token type but an explicit semantic check produces a clear, domain-level error message with context (which leg, departure or arrival). |

## 4. Design

### 4.1 Pipeline Architecture

The pipeline is unchanged from US083. The only difference is that `SemanticValidator` now delegates to a rule chain instead of a single monolithic method.

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
       ├──► FlightDSLListener  (cross-plan checks: duplicate IDs)
       │
       └──► FlightDSLVisitor   (builds domain objects)
                   │
                   ▼
           List<FlightPlan>
                   │
                   ▼
           SemanticValidator
                   │
                   ├──► FlightLevelRules
                   ├──► LegLevelRules
                   ├──► LegSequenceRules
                   ├──► RouteCoherenceRules
                   ├──► SegmentCoherenceRules
                   └──► DomainConsistencyRules
                               │
                               ▼
                   List<DSLError> (all errors collected)
                               │
                               ▼
               Result: FlightPlan | List<DSLError>
```

### 4.2 Semantic Rule Chain

```
SemanticValidator
       │
       │  for each FlightPlan:
       │    for each SemanticRule:
       │      errors.addAll(rule.validate(plan))
       │
       └──► all errors collected before returning
```

### 4.3 Alternative Flows

Unchanged from US083 for lexical and syntactic errors.

**Semantic error (new — all rules run):**
```
SemanticValidator iterates the full rule chain for each FlightPlan.
No rule short-circuits the chain — all rules run regardless of prior errors.
All errors are collected and returned in a single List<DSLError>.
```

## 5. Implementation

### 5.1 File Structure

```
lprog/
│
├── src/main/antlr4/
│   └── pt/isep/sem4pi/lprog/
│       └── FlightDSL.g4                         ← unchanged from US083
│
├── src/main/java/
│   └── pt/isep/sem4pi/lprog/
│       │
│       ├── FlightDSLValidator.java               ← unchanged from US083
│       │
│       ├── error/
│       │   ├── DSLError.java                    ← unchanged
│       │   └── DSLErrorListener.java            ← unchanged
│       │
│       ├── listener/
│       │   └── FlightDSLListener.java           ← unchanged
│       │
│       ├── visitor/
│       │   └── FlightDSLVisitor.java            ← unchanged
│       │
│       ├── semantic/
│       │   ├── SemanticValidator.java           ← refactored: delegates to rule chain
│       │   └── rules/                           ← NEW
│       │       ├── SemanticRule.java            ← interface
│       │       ├── FlightLevelRules.java        ← flight ID, type validity
│       │       ├── LegLevelRules.java           ← per-leg structure, fuel, intra-leg times
│       │       ├── LegSequenceRules.java        ← inter-leg airport and time coherence
│       │       ├── RouteCoherenceRules.java     ← route vs legs, no airport twice
│       │       ├── SegmentCoherenceRules.java   ← coordinates, altitude, wind
│       │       └── DomainConsistencyRules.java  ← airport code format, calendar dates
│       │
│       └── model/                               ← unchanged
│           ├── FlightPlan.java
│           ├── FlightLeg.java
│           ├── FlightSegment.java
│           ├── Departure.java
│           ├── Arrival.java
│           ├── Fuel.java
│           ├── Route.java
│           └── Coordinate.java
│
├── src/test/java/
│   └── pt/isep/sem4pi/lprog/
│       ├── FlightDSLParserTest.java              ← unchanged from US083
│       ├── SemanticValidatorTest.java            ← updated: error message strings aligned with refactored rules
│       └── semantic/rules/                       ← NEW
│           ├── FlightLevelRulesTest.java
│           ├── LegLevelRulesTest.java
│           ├── DomainConsistencyRulesTest.java
│           └── SegmentCoherenceRulesTest.java
│
└── src/main/resources/
    └── examples/
        ├── valid_opo_mad.dsl                     ← unchanged
        ├── valid_lis_fco_vit.dsl                 ← unchanged
        ├── invalid_missing_fuel.dsl              ← unchanged
        ├── invalid_semantic_errors.dsl           ← unchanged
        ├── invalid_leg_sequence.dsl              ← NEW
        ├── invalid_airport_twice.dsl             ← NEW
        └── invalid_intraleg_times.dsl            ← NEW
```

### 5.2 SemanticRule Interface

```java
public interface SemanticRule {
    List<DSLError> validate(FlightPlan plan);
}
```

Each rule class implements this interface. The `SemanticValidator` holds a fixed `List<SemanticRule>` and iterates it for every `FlightPlan`, accumulating all errors.

### 5.3 Rule Classes — Responsibility Summary

| Class | Rules enforced |
|---|---|
| `FlightLevelRules` | Flight ID non-blank; type must be `regular` or `charter` |
| `LegLevelRules` | Departure/arrival presence; ≥1 segment; fuel > 0; departure time < arrival time (intra-leg) |
| `LegSequenceRules` | Arrival airport leg N == departure leg N+1; arrival time leg N < departure time leg N+1 |
| `RouteCoherenceRules` | Route origin == first leg departure; route destination == last leg arrival; no airport visited twice |
| `SegmentCoherenceRules` | Start != end; altitude > 0; wind speed ≥ 0 |
| `DomainConsistencyRules` | Airport code format (IATA 3 / ICAO 4 uppercase letters); calendar date validity |

### 5.4 Refactored SemanticValidator

```java
public class SemanticValidator {

    private final List<SemanticRule> rules = List.of(
            new FlightLevelRules(),
            new LegLevelRules(),
            new LegSequenceRules(),
            new RouteCoherenceRules(),
            new SegmentCoherenceRules(),
            new DomainConsistencyRules()
    );

    public List<DSLError> validate(FlightDSLParser.FlightFileContext tree) {
        List<DSLError> errors = new ArrayList<>();

        // Step 1: Listener — cross-plan checks (duplicate IDs)
        FlightDSLListener listener = new FlightDSLListener();
        ParseTreeWalker.DEFAULT.walk(listener, tree);
        errors.addAll(listener.getSemanticErrors());

        // Step 2: Visitor — build domain model
        FlightDSLVisitor visitor = new FlightDSLVisitor();
        List<FlightPlan> plans = visitor.visitFlightFile(tree);

        // Step 3: Rule chain — all rules, all plans, all errors collected
        for (FlightPlan plan : plans) {
            for (SemanticRule rule : rules) {
                errors.addAll(rule.validate(plan));
            }
        }
        return errors;
    }
}
```

### 5.5 Error Reporting

Unchanged from US083. Every error is a `DSLError` with type (`LEXICAL`, `SYNTACTIC`, `SEMANTIC`), line, column, and message. All semantic errors are collected before returning — no fail-fast.

Example outputs from the new rules:

```
[SEMANTIC] line 4:0  — Leg 1: a hora de partida (2025-06-01T14:00) deve preceder a hora de chegada (2025-06-01T12:00).
[SEMANTIC] line 3:0  — Leg 1 (departure): código de aeroporto inválido 'opo'. Deve ser IATA (3 letras maiúsculas) ou ICAO (4 letras maiúsculas).
[SEMANTIC] line 16:0 — O aeroporto 'OPO' não pode ser visitado mais do que uma vez no mesmo voo.
[SEMANTIC] line 5:0  — O aeroporto de chegada da Leg 1 (MAD) deve coincidir com o aeroporto de partida da Leg 2 (LIS).
```

## 6. Demonstration

### Valid multi-leg flight (passes all rules)

```
flight TP1000 {
    type: regular;
    leg {
        departure: OPO at 2026-05-10 10:00;
        arrival: LIS at 2026-05-10 11:00;
        route: OPO -> LIS;
        segment { mode: cruise; start: (41.2, -8.6, 10000 m); end: (38.7, -9.1, 10000 m); }
        fuel: 3000 kg;
    }
    leg {
        departure: LIS at 2026-05-10 12:00;
        arrival: MAD at 2026-05-10 14:00;
        route: LIS -> MAD;
        segment { mode: cruise; start: (38.7, -9.1, 10000 m); end: (40.4, -3.5, 10000 m); }
        fuel: 4000 kg;
    }
}
```

**Expected:** `DSL Import successful! No syntax or basic semantic errors.`

### Invalid — intra-leg time conflict

```
flight TP006 {
  type: regular;
  leg {
    departure: OPO at 2025-06-01 14:00;
    arrival: MAD at 2025-06-01 12:00;   // departs after arrival
    route: OPO -> MAD;
    segment { mode: cruise; start: (41.2, -8.6, 9000 m); end: (40.4, -3.7, 9000 m); }
    fuel: 5000 kg;
  }
}
```

**Expected:**
```
[SEMANTIC] line 4:0 — Leg 1: a hora de partida (2025-06-01T14:00) deve preceder a hora de chegada (2025-06-01T12:00).
```

### Invalid — airport visited twice

```
flight TP005 {
  type: charter;
  leg {
    departure: OPO at 2025-06-01 10:00;
    arrival: MAD at 2025-06-01 12:00;
    route: OPO -> MAD;
    segment { mode: cruise; start: (41.2, -8.6, 9000 m); end: (40.4, -3.7, 9000 m); }
    fuel: 5000 kg;
  }
  leg {
    departure: MAD at 2025-06-01 14:00;
    arrival: OPO at 2025-06-01 16:00;   // OPO already visited
    route: MAD -> OPO;
    segment { mode: cruise; start: (40.4, -3.7, 9000 m); end: (41.2, -8.6, 9000 m); }
    fuel: 5000 kg;
  }
}
```

**Expected:**
```
[SEMANTIC] line 16:0 — O aeroporto 'OPO' não pode ser visitado mais do que uma vez no mesmo voo.
```

## 7. Tests

### 7.1 Existing tests (Sprint 2 — unchanged)

`FlightDSLParserTest.java` — lexical and syntactic validation (10 test cases).

`SemanticValidatorTest.java` — full pipeline semantic tests (9 test cases), covering: valid multi-leg plan, duplicate ID, negative fuel, route mismatch, airport visited twice, identical segment coordinates, negative altitude/wind, leg sequence airport mismatch, leg sequence time conflict.

### 7.2 New unit tests (Sprint 3 — direct rule class testing)

Tests build `FlightPlan` domain objects programmatically — no parser, no DSL files required.

**`FlightLevelRulesTest.java`** (7 tests)
- `regular` and `charter` types accepted (including case-insensitive)
- Invalid type `express` rejected with domain message
- `null` type rejected
- Blank ID rejected
- `null` ID rejected

**`LegLevelRulesTest.java`** (8 tests)
- Departure before arrival accepted
- `null` times accepted (AT is optional in grammar)
- Positive fuel accepted
- Departure after arrival rejected
- Departure equal to arrival rejected
- Zero fuel rejected
- Negative fuel rejected
- Leg with no segments rejected

**`DomainConsistencyRulesTest.java`** (6 tests)
- Valid IATA code (3 uppercase letters) accepted
- Valid ICAO code (4 uppercase letters) accepted
- 2-letter code rejected
- 5-letter code rejected
- Lowercase code rejected
- Code with digits rejected

**`SegmentCoherenceRulesTest.java`** (6 tests)
- Valid segment accepted
- Wind speed 0 accepted (calm conditions)
- Identical start/end rejected — error message includes `"Leg 1, Segmento 1"` context
- Negative start altitude rejected
- Negative end altitude rejected
- Negative wind speed rejected

## 8. Observations

The `rules/` package was designed with extensibility in mind. Adding a new semantic rule requires only implementing `SemanticRule` and adding it to the list in `SemanticValidator` — no existing code is modified. This satisfies the open/closed principle and makes the system easy to extend for future sprints.

The grammar (`FlightDSL.g4`) was not modified in US120. All DSL files valid under US083 remain valid under US120. The new semantic rules operate on the domain model produced by the visitor, not on the parse tree directly.

The `FlightDSLListener` continues to handle cross-plan rules (duplicate flight IDs) because it has simultaneous visibility over all plans in the file. Per-plan rules are handled by the rule chain.

`LegSequenceRules` and `RouteCoherenceRules` were not given dedicated test classes in Sprint 3 because they are already thoroughly covered by `SemanticValidatorTest` (Sprint 2) through the full pipeline. Duplicating those tests at the unit level would add no coverage value.