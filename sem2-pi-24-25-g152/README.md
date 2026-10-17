# Railway Network Game (Integrative Project, 2nd semester)

🇬🇧 English · [🇵🇹 Português](README.pt.md)

> Java simulation where the player builds a railway network, with graph algorithms and measured performance.

| | |
|---|---|
| **Context** | LEI — Integrative Project, 2nd semester (ESINF · MATCP · MDISC) |
| **Year** | 2024/25 (Mar–Jun 2025) |
| **Team** | Group 152, 5 members, 3 sprints |
| **Stack** | Java 17 · Maven · JUnit · Jupyter (MATCP) · UML / PlantUML |

## My contribution
- Added cities and industries to maps (US02, US03) with tests and sequence diagrams.
- Implemented **shortest path with waypoints** (US27) with Dijkstra / transitive closure, including its UI.
- Built the **performance-testing framework and analysis** for the connectivity and maintenance-route algorithms (US13/US14/US29), with scenarios, CSV results and charts.
- Worked on the MATCP notebook and LaTeX report.

## What this project shows
- **Graph algorithms** (connectivity, shortest paths, maintenance routes) with **empirical complexity analysis**.
- Layered Java design: controllers, services, DTOs, domain and UI.
- Full software-process documentation per user story (requirements, analysis, design, tests).
- Team delivery with Scrum sprints.

## Features
- Editor: create maps, add cities and industries.
- Player: build stations and railway lines, buy locomotives, run trains, cargo generation, maintenance, financial results, save/load games.
- Scenario files and generated test scenarios in `test_scenarios/`.

## Structure
| Path | Content |
|---|---|
| `src/main/java/pt/ipp/isep/dei/` | Application (controller, domain, dto, ui) |
| `docs/system-documentation/` | Per-US documentation for sprints 1–3 |
| `matcp.ipynb, MATCP/` | MATCP component |
| `test_scenarios/` | Scenario datasets |

## Running
```bash
mvn compile exec:java -Dexec.mainClass=pt.ipp.isep.dei.Main
```

## Notes
- Docs are in English; see `docs/TeamMembersAndTasks.md` for the full task distribution.

## Documentation

- [Documentation audit](../docs/AUDIT.md#sem2-pi-24-25-g152) — status and gaps for this project
- [Index and traceability](docs/INDEX.md) — US → documents → code, tests, findings
- [Setup](docs/SETUP.md) — prerequisites, run, performance mode (not tested)
- [Documentation entry point](docs/README.md)
- [System documentation](docs/system-documentation/)
- [Team members and tasks](docs/TeamMembersAndTasks.md)
