# Documentation index — Railway Logistics (LEI 3rd-semester integrative project, group 075)

> Documentation added in 2026 (portfolio). This is a **map** of the team's original documents; none was edited. Facts were read from the repository (counts measured by command); anything not verifiable is marked `TODO: confirmar com o BMS`. Portuguese version: [INDEX.pt.md](INDEX.pt.md). See also [SETUP](SETUP.md), the team's [entry point](README.md) (describes USEI01–03 only, see finding 1) and the existing [glossary](global-artifacts/glossary.md), [business rules](global-artifacts/business-rules.md) and [domain model](global-artifacts/domain-model.puml), which are linked, not duplicated.

## 1. Layout

| Path | Course unit | Content |
|---|---|---|
| [`docs/ESINF/`](ESINF/) | ESINF (data structures) | USEI01–03, 06–08, 11–13, in the course template: `01.requirements-engineering`, `02.analysis`, `03.design` (PlantUML), `04.tests-and-implementation`, plus complexity analyses |
| [`docs/LAPR3/`](LAPR3/) | LAPR3 (programming/Java + DB) | USLP07–10, same template |
| [`docs/BDDAD/`](BDDAD/) | BDDAD (database) | USBD28, 30, 33, 39–42 (one Markdown each) |
| [`docs/ARQCP/`](ARQCP/) | ARQCP (computer architecture) | USAC01–09, RISC-V assembly functions |
| [`docs/global-artifacts/`](global-artifacts/) | all | glossary, business rules, domain model, global class diagram, rationale of associations |
| [`../warehouseMngt/`](../warehouseMngt/) | ESINF, LAPR3 | Java 17 / Maven application `logistics-on-rails` |
| [`../stationMngt/`](../stationMngt/) | ARQCP | RISC-V assembly + C, one folder per story |
| [`../sql-scripts/`](../sql-scripts/) | BDDAD | Oracle scripts and models per sprint |
| [`../scrum/`](../scrum/) | LAPR3 | sprint reports (`.pptx`), self-assessments and burndown (`.xlsx`) |

## 2. Traceability: user story → documents → code → tests

"By name" = the class is named after the story's subject but does not cite the story id; otherwise the cited file is named. In this project the ids are cited mostly in the tests, hardly ever in the production code.

### ESINF (Java, `warehouseMngt/src`, package `pt.isep.dei.esinf`)

| US | Subject | Docs | Code | Tests |
|---|---|---|---|---|
| USEI01 | Box allocation, FEFO/FIFO | [USEI01](ESINF/USEI01/USEI01-README.md) | `service/warehouse/WarehouseAllocator` *(by name)* | `warehouse/WarehouseAllocatorTest`, `EdgeCasesWarehouseTest`, `WarehouseIntegrationTest`, `PerformanceWarehouseTest` (cite USEI01) |
| USEI02 | Order allocation STRICT / PARTIAL | [USEI02](ESINF/USEI02/USEI02-README.md) | `service/warehouse/OrderAllocationService` *(by name)* | `orders/OrderAllocationServiceTest`, `EdgeCasesOrderAllocationTest`, `OrderAllocationIntegrationTest`, `PerformanceOrderAllocationTest` (cite USEI02) |
| USEI03 | Trolley packing (FF / FFD / BFD) | [USEI03](ESINF/USEI03/USEI03-README.md) | `service/warehouse/TrolleyPackingService` *(by name)* | `warehouse/TrolleyPackingServiceTest` |
| USEI06 | Station search | [USEI06](ESINF/USEI06/USEI06-README.md), [complexity](ESINF/USEI06/complexity.md) | `service/StationSearchService`, `ui/SearchStationsMenu` *(by name)* | `stations/StationSearchServiceTest` (cites USEI06); `BalancedTreeBuilder` also mentions USEI06 |
| USEI07 | Balanced 2D-tree of stations | [USEI07](ESINF/USEI07/USEI07-README.md), [complexity](ESINF/USEI07/Complexity-Analysis.md) | `service/spatial/BalancedTreeBuilder` *(by name)* | `service/spatial/BalancedTreeTest` (cites USEI07), `PerformanceTests` |
| USEI08 | 2D range query | [USEI08](ESINF/USEI08/USEI08-README.md), [complexity](ESINF/USEI08/complexity.md) | implementing class not identified | `stations/StationRangeSearchTest` (cites USEI08) |
| USEI11 | Station upgrade order (dependencies) | [USEI11](ESINF/USEI11/USEI11-README.md), [complexity](ESINF/USEI11/complexity.pdf) | `service/railway/RailwayUpgradeService`, `controller/UpgradePlanController`, `ui/UpgradePlanMenu` *(by name)* | `upgrade/UpgradePlanServiceTest`, `UpgradePlanPerformanceTest` (cite USEI11) |
| USEI12 | Minimal backbone network | [USEI12](ESINF/USEI12/USEI12-README.md), [complexity](ESINF/USEI12/complexity.pdf) | `service/railway/MinimalBackboneService` *(by name)* | `service/railway/MinimalBackboneServiceTest` |
| USEI13 | Hub centrality | [USEI13](ESINF/USEI13/USEI13-README.md), [complexity](ESINF/USEI13/complexity.md) | `service/railway/HubCentralityAnalysisService` *(by name)* | `service/railway/HubCentralityAnalysisServiceTest` |

### LAPR3 (Java + Oracle)

| US | Subject | Docs | Code *(by name)* |
|---|---|---|---|
| USLP07 | Train dispatch: scheduling and route calculation | [USLP07](LAPR3/USLP07/) | `service/TrainSchedulerService`, `service/TravelTimeCalculatorService`, `controller/TrainSchedulerMenuController`; SQL: [`sql-scripts/Sprint2/uslp07.sql`](../sql-scripts/Sprint2/uslp07.sql) |
| USLP08 | Freight route planner | [USLP08](LAPR3/USLP08/) | `service/FreightRoutePlannerService`, `controller/FreightManagerMenuController` |
| USLP09 | Train assembly: rolling-stock selection | [USLP09](LAPR3/USLP09/) | not identified |
| USLP10 | Same title as USLP07 (finding 2) | [USLP10](LAPR3/USLP10/) (no test document) | not identified |
| USLP03 | — (no documentation) | — | SQL only: [`sql-scripts/Sprint1/uslp03.sql`](../sql-scripts/Sprint1/uslp03.sql) |

### BDDAD (Oracle, `sql-scripts/`)

| US | Subject | Docs | Scripts |
|---|---|---|---|
| USBD09, USBD20 | Sprint 1 | none | [`Sprint1/User Stories/`](../sql-scripts/Sprint1/User%20Stories/) |
| USBD28 | Locomotives supporting multiple gauges | [USBD28](BDDAD/USBD28/README.md) | `Sprint2/User Stories/USBD28/` (function, inserts, test) |
| USBD30 | Facilities with grain silos but no warehouses | [USBD30](BDDAD/USBD30/README.md) | `Sprint2/User Stories/USBD30/` |
| USBD33 | Validate train maximum length | [USBD33](BDDAD/USBD33/USBD33_documentation.md) | `Sprint3/User Stories/USBD33/` |
| USBD39 | Associate locomotive with planned train | [USBD39](BDDAD/USBD39/USBD39_documentation.md) | `Sprint3/User Stories/USBD39/` |
| USBD40 | Register freight and wagons | [USBD40](BDDAD/USBD40/USBD40_documentation.md) | `Sprint3/User Stories/USBD40/` |
| USBD41 | Remove freight from planned train | [USBD41](BDDAD/USBD41/USBD41_documentation.md) | `Sprint3/User Stories/USBD41/` |
| USBD42 | Register train movement | [USBD42](BDDAD/USBD42/USBD42_documentation.md) | `Sprint3/User Stories/USBD42/` (Java side *(by name)*: `service/warehouse/TrainMovementService`) |

### ARQCP (RISC-V assembly, `stationMngt/`)

| US | Function (from its doc) | Docs | Code | Tests |
|---|---|---|---|---|
| USAC01 | `encrypt_data` | [USAC01](ARQCP/USAC01/) | [`stationMngt/usac01/`](../stationMngt/usac01/) | `main.c` driver |
| USAC02 | `decrypt_data` | [USAC02](ARQCP/USAC02/) | `stationMngt/usac02/` | `main.c` driver |
| USAC03 | `extract_data` | [USAC03](ARQCP/USAC03/) | `stationMngt/usac03/` | `main.c` driver |
| USAC04 | `format_command` | [USAC04](ARQCP/USAC04/) | `stationMngt/usac04/` | `main.c` driver |
| USAC05 | `enqueue_value` (circular buffer) | [USAC05](ARQCP/USAC05/) | `stationMngt/usac05/` | `main.c` driver |
| USAC06 | `dequeue_value` | [USAC06](ARQCP/USAC06/) | `stationMngt/usac06/` | `main.c` driver |
| USAC07 | `move_n_to_array` | [USAC07](ARQCP/USAC07/) | `stationMngt/usac07/` | `main.c` driver |
| USAC08 | `sort_array` (bubble sort) | [USAC08](ARQCP/USAC08/) | `stationMngt/usac08/` | `main.c` driver |
| USAC09 | `median` | [USAC09](ARQCP/USAC09/) | `stationMngt/usac09/` | `main.c` driver |
| USAC11–12, 13–14, 16 | manager, sensor sketch, configuration | none | `stationMngt/usac11-12/`, `usac13-14/`, `usac16/` | `test_usac11.c`, `test_usac12.c`, `usac1314_test.c` |

## 3. Tests

`warehouseMngt/src/test` has 17 test classes with 169 `@Test` methods (JUnit 5, Mockito, AssertJ), mixing unit, edge-case, integration and performance tests. Database features are not covered by Java tests; the SQL stories carry their own `*_test.sql` and anonymous-block scripts. The assembly stories are exercised by C drivers rather than a test framework.

## 4. Verified findings

| # | Finding | Evidence |
|---|---|---|
| 1 | `docs/README.md` ("Warehouse Management System") details only USEI01–03, while `docs/ESINF` holds nine stories (USEI01–03, 06–08, 11–13) and the folder also has LAPR3, BDDAD and ARQCP documents | [README](README.md) |
| 2 | USLP07 and USLP10 have the same title ("Train Dispatch - Scheduling and Route Calculation") and USLP10 has no `04.tests-and-implementation` folder | [USLP07](LAPR3/USLP07/), [USLP10](LAPR3/USLP10/) |
| 3 | ARQCP documents mix `README.MD` / `README.md` / `REAME.md` (typo in USAC03) | `docs/ARQCP/` |
| 4 | `stationMngt/usac11-12`, `usac13-14`, `usac16` have code and tests but no documentation; USAC10 and USAC15 have no folder | listings |
| 5 | The Oracle JDBC URL (a team machine's host name) is hard-coded in 5 Java files and 5 files hold password literals, plus the menu's own access password constant. Values not reproduced | `grep` in `warehouseMngt/src` |
| 6 | `stationMngt/usac11-12/config.txt` and `usac16/config.txt` contain a `[USERS]` block with a name, user name, password and Caesar key (local dev defaults; not reproduced) | files |
| 7 | The assembly Makefiles require a `riscv32-linux-gcc` cross-compiler and `qemu-riscv32` with a sysroot at `$HOME/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot`; the README does not say so | `stationMngt/*/Makefile` |
| 8 | The POM has no exec plugin; the README only gives `mvn test`, so the application entry (`pt.isep.dei.esinf.Main`) has no documented run command | `pom.xml`, README |
| 9 | `usac13-14/raspberrycode.txt` is an Arduino-style sketch (DHT11 on pin 28, 2 soil-humidity, 4 air-humidity and 4 air-temperature sensors), stored as `.txt` | file |

## 5. Improvement list (not applied)

| Priority | Improvement | Effort |
|---|---|---|
| P1 | Move the JDBC URL and credentials out of the Java source (environment variables or an untracked properties file with an `.example`); rotate the dev passwords | 1–2 h |
| P1 | Update `docs/README.md` to list all ESINF/LAPR3/BDDAD/ARQCP stories, or link this index | 20 min |
| P2 | Document USAC11–16 and add the missing toolchain prerequisites to the README | 1 h |
| P2 | Fix the USLP10 title and add its test document | 30 min |
| P2 | Add a GitHub Actions job: `mvn -B test` in `warehouseMngt` | 30 min |
| P3 | Rename `REAME.md`, normalise `README.MD`; rename `raspberrycode.txt` to `.ino` | 10 min |
