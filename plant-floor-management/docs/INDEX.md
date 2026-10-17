# Documentation index — Plant Floor Management (LEI 3rd-semester integrative project, group 152)

> Documentation added in 2026 (portfolio). This is a **map** of the team's original material; none of it was edited. Facts were read from the repository (counts measured by command); anything not verifiable is marked `TODO: confirmar com o BMS`. Portuguese version: [INDEX.pt.md](INDEX.pt.md). See also [SETUP](SETUP.md) and the existing glossaries ([LAPR3 project glossary](../documentation/LPRA3/Sprint3/Glossary/glossary.md), [BDDAD glossary](../documentation/BDDAD/Sprint1/USBD01/glossario.md)), which are linked, not duplicated.

Other members appear in the team's own READMEs; this index names only the author of the repository (Bruno Silva) and points to those READMEs for the rest.

## 1. The four subsystems and where each one lives

| Subsystem | Course unit | Code | Course documentation | Language |
|---|---|---|---|---|
| Database (Oracle) | BDDAD | [`plantFloorManager/`](../plantFloorManager/) (Sprint 2–3 scripts) | [`documentation/BDDAD/`](../documentation/BDDAD/) (Sprint 1–3) | SQL, PL/SQL, Python |
| Machine supervisor (C/Assembly/Arduino) | ARQCP, FSIAP | [`machineSupervisor/`](../machineSupervisor/) | [`documentation/ARQCP/`](../documentation/ARQCP/), [`documentation/FSIAP/`](../documentation/FSIAP/) | C, x86-64 Assembly, Arduino C++, Python |
| Production-plan simulator | ESINF | [`prodPlanSimulator/`](../prodPlanSimulator/) | [`documentation/ESINF/`](../documentation/ESINF/) | Java 17 (Maven) |
| Project management | LPRA3 (LAPR3) | — (`projectManager/` is an empty placeholder) | [`documentation/LPRA3/`](../documentation/LPRA3/) | Markdown, PPTX, XLSX |

## 2. Traceability: user-story family → documents → code

Story prefixes: `USBD` = database, `USAC` = computer architecture (C/Assembly), `USEI` / `USLP` = data structures / programming (Java), `USFA` = FSIAP (sensors).

| Stories | Sprint | Documents | Code / scripts | Notes |
|---|---|---|---|---|
| USBD01–USBD09 | 1 | [`documentation/BDDAD/Sprint1/`](../documentation/BDDAD/Sprint1/) (glossary, conceptual/logical model `.vpp`, legacy dataset script, review) | `USBD04/script_legacy.py` + [doc](../documentation/BDDAD/Sprint1/USBD04/doc_script_legacy.md), `USBD08.sql` | Several files are named `USBD0N` with no extension |
| USBD10–USBD19 | 2 | [`documentation/BDDAD/Sprint2/`](../documentation/BDDAD/Sprint2/) and [`plantFloorManager/Sprint2/`](../plantFloorManager/Sprint2/) (task owners in its [README](../plantFloorManager/Sprint2/README)) | `Create Tables.sql`, `generate_sql_inserts_from_xml.py`, `USBD13.sql` … `USBD19.sql` | Two parallel copies, see finding 2 |
| USBD20–USBD30 | 3 | [`plantFloorManager/Sprint3/`](../plantFloorManager/Sprint3/) (owners in its [README](../plantFloorManager/Sprint3/README); USBD29 is recorded as "Not Made") | `USBD22/scripts/*.py` (dataset → inserts), `USBD28/usbd28.sql` and tests, `USBD23`–`USBD27` SQL | Contributions by Bruno Silva include USBD22 and USBD28 per that README |
| USAC01–USAC10 | 2 | [`documentation/ARQCP/Sprint02/`](../documentation/ARQCP/Sprint02/) | `src/USAC01.S` … `USAC10.S`, `tests/testUSAC*/`; related code in [`machineSupervisor/Sprint02/`](../machineSupervisor/Sprint02/) | Unity tests in `machineSupervisor/Sprint02/utests` |
| USAC11–USAC18 | 3 | [`machineSupervisor/Sprint03/README.MD`](../machineSupervisor/Sprint03/README.MD) (one section per story, in Portuguese) | `Sprint03/src/*.c`, `include/*.h`, `machine.ino`, `Sprint2src/usac*.s`, `data/*.csv`, `Makefile` | `machine.ino` is the Arduino sketch; `machineEmulator.c` stands in for the hardware (explained in the README) |
| USEI01, USEI08 | 3 | [`documentation/ESINF/`](../documentation/ESINF/) ([Technical_Documentation.md](../documentation/ESINF/Technical_Documentation.md), `Sprint2/` complexity analysis, `HowToSurvive.md`) | `prodPlanSimulator/src/main/java/com/example/production/` (Domain, Graphs, Service, UI, utils) | CSV inputs under `documentation/ESINF/USEI01`, `USEI08`, `Sprint03/BDcsv` |
| USLP04, USLP05, USLP07 | 3 | Technical_Documentation.md | `Graphs/USLP04GraphsGene.java` + test; PERT/CPM and scheduling in `Domain/CriticalPathAnalyzer.java`, `ScheduleCalculator.java`; menu option 10–11 in `UI/MainInterface.java` | Only USLP04 and USLP07 are cited in the code; USLP05 is cited in the root README |
| USFA01–USFA03 | 2–3 | [`documentation/FSIAP/`](../documentation/FSIAP/) (Raspberry Pi Pico W setup, sensor code, `USFA01.txt`, `USFA03.txt`) | `read_pico_serial.py`, `read_pico_serial_mq_2.py` | Sensor and serial-reading scripts |
| Scrum (all sprints) | 1–3 | [`documentation/LPRA3/`](../documentation/LPRA3/) (guidelines, retrospective, sprint reports `.pptx`, self-assessments `.xlsx`, burndown) | — | Binary Office files |

## 3. Tests

| Area | Evidence |
|---|---|
| Java | 13 test classes with 38 `@Test` methods in `prodPlanSimulator/src/test` (JUnit 5, Mockito) |
| C/Assembly | `documentation/ARQCP/Sprint02/tests/testUSAC01…10` (one `test.c` + makefile each); Unity in `machineSupervisor/Sprint02/utests` |
| SQL | `USBD13_Test.sql`, `USBD19_Test.sql`, `USBD22/test-plsql`, `USBD28/test1…5.sql` with output screenshots |

## 4. Verified findings

| # | Finding | Evidence |
|---|---|---|
| 1 | The simulator locates its CSV inputs by walking up to the nearest `.git` directory and appending `documentation/ESINF/...`. In this portfolio the Git root is the portfolio repository, not this project, so the paths resolve to `portfolio/documentation/ESINF/...`, which does not exist. "Load Data" is therefore expected to fail here unless the project is cloned/run as its own repository (prediction from the code, not an observed run) | `RepPathUtils.java`, `MainInterface.java` lines 43–47, `DataImportService.java` line 29; `ls portfolio/documentation` = not found |
| 2 | `documentation/BDDAD/Sprint2` and `plantFloorManager/Sprint2` are two diverging copies of the same stories (20 differences: e.g. different `inserts.sql`, `get_product_parts.sql`, extra files on each side) | `diff -rq` |
| 3 | Version-control clutter is tracked: 6 compiled `.o` files, 15 Visual Paradigm `.bak_*` backups, 2 `.lck` lock files, several `.DS_Store` | `git ls-files` |
| 4 | Several stories are plain files without extension (`USBD03`, `USBD5`, `USBD6`, `USBD07`, `USBD09`, `USBD15`, `USBD16`, `README`) so they are not rendered or syntax-highlighted | file listing |
| 5 | `machineSupervisor/Sprint03/README.MD` states that the physical machine (Arduino) could not be completed because members left the course, so a software emulator replaces it | README USAC11 |
| 6 | `projectManager/` and `documentation/empty.txt` are empty placeholders | file listing |
| 7 | `prodPlanSimulator/src/main/resources/config.properties` holds a non-empty local Oracle password (dev default; value not reproduced). The Oracle import (menu option 14) needs a local Oracle XE | `grep -c`, README |
| 8 | Course documents mix Portuguese and English | files |

## 5. Improvement list (not applied)

| Priority | Improvement | Effort |
|---|---|---|
| P1 | Make the simulator resolve data paths relative to the project (or a CLI argument) instead of the Git root | 1 h |
| P1 | Move `config.properties` to an untracked file with a `config.properties.example` and rotate the dev password | 30 min |
| P2 | Remove tracked `.o`, `.bak_*`, `.lck`, `.DS_Store`; extend `.gitignore` | 15 min |
| P2 | Resolve the duplicated BDDAD/plantFloorManager Sprint 2 copies (keep one, link from the other) | 1 h |
| P2 | Add a CI workflow: `mvn -B test` in `prodPlanSimulator` and `make` in `machineSupervisor/Sprint03` | 1 h |
| P3 | Give extension-less US files a `.sql` or `.md` extension | 30 min |
| P3 | Fill or remove the empty `projectManager/` placeholder | 5 min |
