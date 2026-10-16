# Plant Floor Management (Integrative Project, 3rd semester)

🇬🇧 English · [🇵🇹 Português](README.pt.md)

> Four integrated subsystems for an industrial plant: database, machine firmware, production-plan simulator and PERT/CPM scheduling.

| | |
|---|---|
| **Context** | LEI — Integrative Project, 3rd semester (BDDAD · ESINF · ARQCP · LPRA3 · FSIAP) |
| **Year** | 2024/25 (Oct 2024–Jan 2025) |
| **Team** | Group 152, 5+ members, 3 sprints |
| **Stack** | Java · Oracle PL/SQL · C · Assembly · Arduino · Python · JUnit |

## My contribution
- Contributions to the **Java production simulator** (domain, graphs, scheduling, tests, user stories such as USLP05/USLP07).
- Oracle **database work**: schema/queries and PL/SQL for several USBD stories (e.g. USBD04, USBD22, USBD28) and a **Python script** generating inserts from the sprint datasets.
- Scrum artefacts (burndown charts, reports), glossary and FSIAP deliverable.

## What this project shows
- Cross-stack work: **SQL/PL-SQL, Java, C, Assembly and Arduino** in one product.
- Data structures and algorithms: AVL and production trees, graphs, **critical-path (PERT/CPM)** analysis.
- Data import pipelines and test coverage on the Java side.
- Scrum with burndown tracking.

## Features
- `plantFloorManager` — Oracle PL/SQL scripts per sprint.
- `machineSupervisor` — C and Assembly supervision of machines (Arduino code in Sprint 3).
- `prodPlanSimulator` — Java simulator: bill of operations, activity graph, critical-path analysis, average production time.
- `documentation/` — BDDAD, ESINF, ARQCP, LPRA3 (Scrum) and FSIAP deliverables.

## Structure
| Path | Content |
|---|---|
| `plantFloorManager/` | Oracle scripts |
| `machineSupervisor/` | C / Assembly / Arduino |
| `prodPlanSimulator/` | Java (Maven) simulator |
| `documentation/` | Course deliverables |

## Running
Java: `cd prodPlanSimulator && mvn test`. Oracle scripts need a local Oracle XE; the JDBC settings in `src/main/resources/config.properties` are a local development default.

## Notes
- Some documents are in Portuguese.

## Documentation

- [Documentation audit](../docs/AUDIT.md#sem3-pi-24-25-g152) — status and gaps for this project
- [Documentation by course unit](documentation/)
