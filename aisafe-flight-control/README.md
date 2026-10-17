# AISafe — Flight Control Platform (Integrative Project, 4th semester)

🇬🇧 English · [🇵🇹 Português](README.pt.md)

> Air-traffic / flight-plan management with a DDD Java back office, a DSL for flight plans, C simulation and network servers.

> Formerly `sem4pi-25-26-2de4`.

| | |
|---|---|
| **Context** | LEI — Integrative Project, 4th semester (EAPLI · SCOMP · RCOMP · LPROG) |
| **Year** | 2025/26 (Mar–Jun 2026) |
| **Team** | Group 2DE4, 5 members, 3 sprints |
| **Stack** | Java · EAPLI framework · Maven · JPA · ANTLR4 · C (processes, threads, shared memory) · TCP/UDP · PlantUML · JUnit 5 |

## My contribution
- Core **domain and persistence layers** (`aisafe.core`, `aisafe.persistence.impl`) and the back-office console.
- **US121** — flight-plan import from a DSL file with a validation layer before ANTLR parsing; unit tests migrated to JUnit 5.
- **US112** monthly reports and **US111** simulation reports; **US107** thread notification via condition variables; **US090** external UDP logging of remote accesses.
- **DDD** domain model, aggregate justification and sequence diagrams; bootstrap data covering 12 months and edge cases.

## What this project shows
- **Domain-Driven Design** with the EAPLI framework: aggregates, repositories, application services, layered architecture.
- **Language processing**: a flight-plan DSL with an ANTLR4 grammar.
- **Systems programming in C**: processes (`fork`), POSIX threads, mutexes/condition variables and shared memory for a flight simulator that detects safety violations.
- **Networking**: TCP servers for remote access and UDP logging; cloud/VPN deployment notes.
- Rigorous documentation: user stories with analysis, design and tests; meeting minutes.

## Features
- `aisafe-flight-control` — Java modules: core, persistence, bootstrap, back-office console, company console, server.
- `aisafe-logging-server` — central logging server.
- `lprog/` — `FlightDSL.g4` grammar and a C flight tester.
- `scomp/` — C simulator: flight processes, shared memory, report thread (see `README-sprint2.md`, `README-sprint3.md`).
- `docs/` — per-US documentation, domain model and meetings.

## Structure
| Path | Content |
|---|---|
| `aisafe-flight-control/` | Maven multi-module Java application |
| `aisafe-logging-server/` | Logging server |
| `lprog/` | ANTLR grammar + C tester |
| `scomp/` | C simulation (Makefile) |
| `docs/` | Documentation |

## Running
```bash
mvn clean install                 # root: eapli.base + lprog (ANTLR grammar)
cd aisafe-flight-control && mvn clean install
cd ../scomp && make               # C simulator
```
See `docs/readme.md` for the planning and technical documentation.

## Notes
- One test file with a very long path (`InsertWeatherDataInFlightPlanUiAcceptanceTest.java`) could not be checked out on Windows and is not included.
- Build outputs, `libs/*.jar` and IDE folders were left out.

## Documentation

- [Documentation audit](../docs/AUDIT.md#aisafe-flight-control) — status and gaps for this project
- [Index and traceability](docs/INDEX.md) — US family → docs → code → tests, CI, findings
- [Setup](docs/SETUP.md) — build and run per component (not tested)
- [Global artefacts (use cases, meetings, requirements)](docs/global-artifacts/)
- [Glossary](docs/global-artifacts/glossary.md)
- [Cloud testing](docs/global-artifacts/CLOUD_TESTING.md)
- [Cloud VPN access](docs/global-artifacts/CLOUD_VPN_ACCESS.md)
