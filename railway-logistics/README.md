# Railway Logistics (Integrative Project, 3rd semester)

🇬🇧 English · [🇵🇹 Português](README.pt.md)

> Railway and warehouse logistics: advanced data structures in Java, low-level code in assembly/C, and an Oracle database.

> Formerly `sem3-pi-25-26-g075`.

| | |
|---|---|
| **Context** | LEI — Integrative Project, 3rd semester (ESINF · ARQCP · BDDAD · LAPR3) |
| **Year** | 2025/26 (Oct 2025–Jan 2026) |
| **Team** | Group 075, 5 members, 3 sprints |
| **Stack** | Java · Oracle SQL · RISC-V Assembly · C · Raspberry Pi · Python · Scrum |

## My contribution
- **Balanced 2D KD-tree** construction (USEI07): requirements, analysis, design, implementation, tests and complexity analysis.
- **Railway hub ranking** with centrality metrics (and tests), and the **railway upgrade plan** service with cycle detection.
- Complexity analyses for several ESINF stories (e.g. USEI11, USEI13).
- ARQCP: **Caesar cipher** and **circular buffer** in assembly, **DHT sensor + traffic-light** control on Raspberry Pi in C, configuration management with encryption.

## What this project shows
- **Data structures & algorithms**: KD-tree, AVL, graph centrality, packing heuristics (FF/FFD/BFD), FEFO/FIFO allocation — each with complexity analysis.
- Low-level skills: RISC-V assembly, C on embedded hardware.
- SQL scripts per sprint plus Scrum reports and burndown (`scrum/`).
- Documentation per user story: requirements, analysis, design, tests.

## Features
- `warehouseMngt` — warehouse management in Java: box allocation, order allocation (STRICT/PARTIAL), trolley packing, train scheduling and travel times.
- `stationMngt` — station-side code per user story (`usac01`…`usac16`): assembly and C.
- `sql-scripts/` — Oracle schema and queries per sprint.
- `docs/` — ESINF, ARQCP, BDDAD and LAPR3 documentation.

## Structure
| Path | Content |
|---|---|
| `warehouseMngt/` | Java (Maven) application |
| `stationMngt/` | Assembly / C per user story |
| `sql-scripts/` | Oracle scripts |
| `scrum/` | Sprint reports and burndown |
| `docs/` | Documentation |

## Running
Java: `cd warehouseMngt && mvn test`. Database features need a local Oracle instance; connection settings in the Java services and `config.txt` files are local development defaults.

## Notes
- Docs are mostly in English.

## Documentation

- [Documentation audit](../docs/AUDIT.md#railway-logistics) — status and gaps for this project
- [Index and traceability](docs/INDEX.md) — US → docs → code → tests for ESINF, LAPR3, BDDAD, ARQCP; findings
- [Setup](docs/SETUP.md) — Java, Oracle scripts, RISC-V toolchain (not tested)
- [Documentation entry point](docs/README.md)
- [Documentation by course unit](docs/)
