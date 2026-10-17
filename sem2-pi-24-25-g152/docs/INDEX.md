# Documentation index — Railway Network Game (LEI 2nd-semester integrative project, group 152)

> Documentation added in 2026 (portfolio). This is a **map** of the team's original documents; none was edited. Facts were read from the repository (counts measured by command); anything not verifiable is marked `TODO: confirmar com o BMS`. Portuguese version: [INDEX.pt.md](INDEX.pt.md). See also [SETUP](SETUP.md), the team's [entry point](README.md), [task distribution](TeamMembersAndTasks.md) and the domain [glossary](system-documentation/sprint2/global-artifacts/01.requirements-engineering/glossary.md) (not duplicated here).

## 1. How the documentation is organised

Each sprint folder under [`system-documentation/`](system-documentation/) holds one folder per user story (US) following the course template: `01.requirements-engineering`, `02.analysis`, `03.design` (PlantUML in `puml/`, rendered in `svg/`) and `04.tests-and-implementation`, plus a `global-artifacts/` folder (glossary, use-case diagram, supplementary specification, domain model). Sprint 3 stories on algorithms (US13, US14, US27, US28, US29) are single Markdown files instead.

Team: 1221514 (Bruno Silva), 1221515, 1210491, 1221519, 1221526 ([TeamMembersAndTasks](TeamMembersAndTasks.md)).

## 2. Traceability: user story → documents → code

"Owner" is from `TeamMembersAndTasks.md` (Sprint 1 / Sprint 2); that table has **no Sprint 3 rows**, so Sprint 3 owners are `TODO: confirmar com o BMS`. "Code" is the controller named after the story; it is marked *(by name)* when the code does not cite the US number.

| US | Subject | Owner S1 / S2 | Sprint 1 | Sprint 2 | Sprint 3 | Code |
|---|---|---|---|---|---|---|
| US01 | Editor creates a map | 1221519 | [docs](system-documentation/sprint1/US01) | [docs](system-documentation/sprint2/US01) | [docs](system-documentation/sprint3/US01) | `MapController` *(by name)* |
| US02 | Editor adds an industry | 1221514 | [docs](system-documentation/sprint1/US02) | no folder | [docs](system-documentation/sprint3/US02) | `AddIndustryController` *(by name)* |
| US03 | Editor adds a city | 1221514 | [docs](system-documentation/sprint1/US03) | no folder | [docs](system-documentation/sprint3/US03) | `AddCityController` *(by name)* |
| US04 | Editor creates a scenario | 1221515 | [docs](system-documentation/sprint1/US04) | [docs](system-documentation/sprint2/US04) | [docs](system-documentation/sprint3/US04) | `CreateScenarioController` *(by name)* |
| US05 | Player builds a station | 1221526 | [docs](system-documentation/sprint1/US05) | [docs](system-documentation/sprint2/US05) | [docs](system-documentation/sprint3/US05) | `BuildStationController` *(by name)* |
| US06 | Player upgrades a station | 1221526 | [docs](system-documentation/sprint1/US06) | [docs](system-documentation/sprint2/US06) | [docs](system-documentation/sprint3/US06) | `UpgradeStationController` *(by name)* |
| US07 | List stations / details | 1210491 | [docs](system-documentation/sprint1/US07) | [docs](system-documentation/sprint2/US07) | — | not identified |
| US08 | Build a railway line | 1221515 | [docs](system-documentation/sprint1/US08) | [docs](system-documentation/sprint2/US08) | [docs](system-documentation/sprint3/US08) | `BuildRailwayLineController` *(by name)* |
| US09 | Buy a locomotive | 1221526 | [docs](system-documentation/sprint1/US09) | [docs](system-documentation/sprint2/US09) | [docs](system-documentation/sprint3/US09) | `BuyLocomotiveController` *(by name)* |
| US10 | Assign a train to a route | 1210491 | [docs](system-documentation/sprint1/US10) | [docs](system-documentation/sprint2/US10) | — | `TrainController` *(by name)* |
| US11 | List all trains | 1221519 | [docs](system-documentation/sprint1/US11) | [docs](system-documentation/sprint2/US11) | [docs](system-documentation/sprint3/US11) | `TrainController` *(by name)* |
| US12 | Cargo generation simulator | 1221515 | [docs](system-documentation/sprint1/US12) | [docs](system-documentation/sprint2/US12) | [docs](system-documentation/sprint3/US12) | `CargoGenerationController` *(by name)* |
| US13 | Can a train travel between two stations | — / 1221514 | — | [algorithm doc](system-documentation/sprint2/US13/algorithm_documentation.md) | [algorithm doc](system-documentation/sprint3/US13/algorithm_documentation.md) | `RailwayConnectivityController` (cites US13) |
| US14 | Maintenance route over every line once | — / 1221515 & 1221526 | — | [algorithm doc](system-documentation/sprint2/US14/algorithm_documentation.md) | [algorithm doc](system-documentation/sprint3/US14/algorithm_documentation.md) | `RailwayMaintenanceController` (cites US14) |
| US15 | Average annual profit statistics | — / 1221526 | — | [LaTeX](system-documentation/sprint2/US15/doc.tex) | — | none (analysis, not application code) |
| US16 | Passenger-arrival boxplots | — / 1210491 | — | no folder | — | none |
| US17 | Cargo histogram per station | — / 1221515 | — | [LaTeX + histogram](system-documentation/sprint2/us17/) | — | none |
| US18 | Train/passenger/mail pie charts | — / 1221519 | — | [LaTeX](system-documentation/sprint2/US18/doc.tex) | — | none |
| US23 | Save a simulation to file | — | — | — | [docs](system-documentation/sprint3/US23) | `SaveGameController` *(by name)* |
| US27 | Shortest path with waypoints | — | — | — | [doc](system-documentation/sprint3/US27/us27_documentation.md) | `controller/us27/TransitiveClosureShortestPathController` (cites US27) |
| US28 | Algorithm complexity analysis | — | — | — | [doc](system-documentation/sprint3/US28) | not identified |
| US29 | Asymptotic performance of US13/US14 | — | — | — | [doc](system-documentation/sprint3/US29/us29_documentation.md), [MATCP/US29](../MATCP/US29/us29_documentation.md) | `Main` (`PERFORMANCE_TEST` mode) and `src/test/.../performance/` (cites US29) |
| US30, US31 | MATCP notebooks | — | — | — | [MATCP/US30](../MATCP/US30/us30.md), [MATCP/US31](../MATCP/US31/us31.md) | Jupyter notebooks, no Java |

Authentication: [`auth/`](auth/) describes the reused `AuthLib` component. Datasets: [`datasets/`](datasets/). Sprint 3 MATCP entry: [README_MATCP.md](../README_MATCP.md).

## 3. Tests

`src/test/java` contains only the **performance harness** (US13/US14 timing tests, CSV/report export, `US29PerformanceTestRunner` with 4 `@Test` methods) and 60 generated scenario CSVs in `src/test/resources/test_scenarios/`. There are no unit tests for US01–US12. Only the US01 and US11 test documents (sprint 2) contain `@Test` snippets.

## 4. Verified findings

| # | Finding | Evidence |
|---|---|---|
| 1 | `Main` starts only a console menu for the connectivity feature (5 options: US13 check, reachable stations, maintenance route, shortest path with waypoints, dev team). None of the editor/player UIs of US01–US12 (`AddIndustryUI`, `AddCityUI`, `BuildStationUI`, `BuildRailwayLineUI`, `BuyLocomotiveUI`, `UpgradeStationUI`, `CargoGenerationUI`, `CreateScenarioUI`, `SaveGameUI`, `ViewFinancialResultsUI`) is instantiated anywhere, so the README feature list (build stations, buy locomotives, save/load…) describes code that cannot be reached from the application | `Main.java`, `RailwayConnectivityUI.java`; `grep "new <UI>"` = 0 for each of the ten |
| 2 | README "Structure" lists `test_scenarios/` at the root; it is `src/test/resources/test_scenarios/` | directory listing |
| 3 | `TeamMembersAndTasks.md` ends at US18 and has no Sprint 3 rows although its header says the table must always be up to date; US16 appears there but has no documentation folder. Its Sprint 2 links for US02 and US03 point to folders that do not exist (US02/US03 are documented in Sprint 1 and Sprint 3 only) | [TeamMembersAndTasks](TeamMembersAndTasks.md) |
| 4 | `docs/README.md` links only Sprint 1 and Sprint 2 | [README](README.md) |
| 5 | Sprint 2 `us17` is lower-case while the other folders use `US`; US15/US17/US18 are LaTeX only (no Markdown) | [sprint2](system-documentation/sprint2) |
| 6 | `execution-times.csv` is stored twice with identical content (`docs/datasets/`, `MATCP/US29/`) | `cmp` = identical |
| 7 | `AuthLib` is resolved from an external Maven repository (`repo.repsy.io/mvn/pmaio/lei-soft`); if that repository is gone the build cannot resolve it | `pom.xml` |
| 8 | The project is still named `project-template` in the POM | `pom.xml` |

## 5. Improvement list (not applied)

| Priority | Improvement | Effort |
|---|---|---|
| P1 | Align the README feature list with what `Main` exposes, or add a menu entry point for the US01–US12 UIs | 1–2 h |
| P1 | Add the Sprint 3 rows (and owners) to `TeamMembersAndTasks.md` | 20 min |
| P2 | Add unit tests for the US01–US12 domain classes (the test documents already describe them) | 1–2 days |
| P2 | Add a GitHub Actions job: `mvn -B verify` and a Markdown link check | 1 h |
| P3 | Rename `us17` → `US17`; add Markdown summaries for US15/17/18 | 30 min |
| P3 | Keep a single copy of `execution-times.csv`; set a real `artifactId` in the POM | 15 min |
