# Documentation index — AISafe Flight Control Platform (LEI 4th-semester integrative project, group 2DE4)

> Documentation added in 2026 (portfolio). This is a **map** of the team's original documents; none was edited. Facts were read from the repository (counts measured by command); anything not verifiable is marked `TODO: confirmar com o BMS`. Portuguese version: [INDEX.pt.md](INDEX.pt.md). See also [SETUP](SETUP.md) and the existing [glossary](global-artifacts/glossary.md), [cloud testing](global-artifacts/CLOUD_TESTING.md) and [cloud VPN access](global-artifacts/CLOUD_VPN_ACCESS.md) notes, which are linked, not duplicated.

The project README points to `docs/readme.md`, which does not exist (finding 1); this index is the entry point for `docs/`.

## 1. Layout

| Path | Course unit | Content |
|---|---|---|
| [`docs/us_NNN/`](./) | EAPLI, LPROG, RCOMP | 46 per-story folders (`readme.md`, often with a PlantUML sequence diagram and PNG; `tests.md` for US071/US072) |
| [`docs/global-artifacts/`](global-artifacts/) | all | glossary, group composition, domain model (final + earlier versions), use-case diagram, meetings (`Meetings/`), requirement PDFs (`sem4/`), cloud notes, peer-assessment template |
| [`docs/Peer-Assessement/`](Peer-Assessement/) | LAPR4 | individual peer assessments |
| [`../scomp/docs/us_NNN/`](../scomp/docs/) | SCOMP | 11 stories (US100–US112), plus [Sprint 2](../scomp/README-sprint2.md) and [Sprint 3](../scomp/README-sprint3.md) READMEs |
| [`../aisafe-flight-control/`](../aisafe-flight-control/) | EAPLI | Maven multi-module Java application (7 modules) |
| [`../aisafe-logging-server/`](../aisafe-logging-server/) | RCOMP | remote-access logging server (UDP, HTTP) |
| [`../lprog/`](../lprog/) | LPROG | `FlightDSL.g4` (ANTLR4), validator/importer, C `flight_tester` |
| [`../scomp/`](../scomp/) | SCOMP | C flight simulator (processes, shared memory, threads, semaphores) |
| [`../eapli.base/`](../eapli.base/) | EAPLI | copy of the course's example framework project (`exemplo.*` modules) |

## 2. Traceability: user-story family → documents → code → tests

Test counts are `@Test`/`@ParameterizedTest` occurrences in `src/test/java` per module: `aisafe.core` 656 (62 files), `aisafe.app.backoffice.console` 89 (9), `aisafe.app.server` 6 (1), `aisafe-logging-server` 39 (3), `lprog` 93 (9), `eapli.base` 6 (2). Story ids are cited in the source (comments, test names); "cited in" lists where.

| Stories | Subject | Docs | Code (modules where the id is cited) |
|---|---|---|---|
| US001–US005 | Technical constraints, repository, structure, CI server, automated deployment | `docs/us_001`…`us_005` | [`.github/workflows/maven-ci.yml`](../.github/workflows/maven-ci.yml), root `pom.xml`, [`aisafe-flight-control/scripts/`](../aisafe-flight-control/scripts/) |
| US010–US011 | Domain model, aggregate justification | [`us_010`](us_010/), [`us_011`](us_011/) (39 diagram files), [`global-artifacts/US10-DomainModel-FINAL`](global-artifacts/US10-DomainModel-FINAL.puml) | `aisafe.core` |
| US030–US033 | Authentication/authorization, register/disable/list users | `us_030`…`us_033` | `aisafe.core`, `aisafe.app.backoffice.console`, `aisafe.bootstrap` (+ tests) |
| US041–US043 | Weather data: register, bulk import, consult | `us_041`…`us_043` | `aisafe.core`, `aisafe.app.backoffice.console` (+ tests) |
| US050–US058 | Air control areas, airports, aircraft and engine models | `us_050`, `us_052`, `us_055`…`us_058` | `aisafe.core`, `aisafe.app.backoffice.console`, `aisafe.bootstrap` (+ tests) |
| US060–US064 | Air transport companies and collaborators | `us_060`…`us_064` | `aisafe.core`, `aisafe.app.backoffice.console`, `aisafe.bootstrap` (+ tests) |
| US070–US078 | Fleet, flight routes, pilots, collaborator remote access | `us_070`…`us_078` | `aisafe.core`, `aisafe.app.company.console`, `aisafe.persistence.impl`; US078 also in `aisafe.app.server` and `aisafe-logging-server` tests |
| US080–US085 | Flight plans (manual, from file), weather in flights, DSL specification, validation | `us_080`…`us_085` | `lprog` (grammar, validator, importer), `aisafe.core`, `aisafe.app.backoffice.console`, `aisafe.app.server` |
| US090–US091 | External logging of remote accesses, log visualisation | `us_090`, `us_091` | [`aisafe-logging-server`](../aisafe-logging-server/) |
| US100–US112 | SCOMP simulation: shared memory, violation thread, condition variable, semaphores, reports | [`scomp/docs/us_100`…`us_112`](../scomp/docs/), `docs/us_107`, `us_111`, `us_112` (a second, different document per id) | [`scomp/*.c`](../scomp/); US112 (monthly report) is cited in `aisafe.bootstrap`, implemented by `MonthlyReportGenerator` in `aisafe.core`; sample outputs in [`aisafe-flight-control/reports/monthly/`](../aisafe-flight-control/reports/monthly/) (Jan–Mar 2026) |
| US120–US121 | Flight DSL (Sprint 3), flight plan from file | `us_120`, `us_121` | `lprog` tests |
| US666 | — (not a course story id) | [`global-artifacts/us_666`](global-artifacts/us_666/readme.md) | — |

Ids cited in the code but without a documentation folder: **US044, US086** (logging/weather server side). Ids with a documentation folder but never cited in code: US001–US005, US010–US011, US041, US075–US076, US090–US091, US103, US121 (many are process or UI-only stories).

## 3. Tests and CI

- **CI** ([maven-ci.yml](../.github/workflows/maven-ci.yml)): on push/PR to `master`/`main` and nightly (02:00 UTC), JDK 21, runs `mvn -B clean verify --file eapli.base/pom.xml` and uploads Surefire and JaCoCo reports. It builds **`eapli.base` only** (the example framework), not `aisafe-flight-control`, `lprog`, `aisafe-logging-server` or `scomp` (finding 3).
- **Java**: ~880 test methods in the AISafe modules (above). Acceptance-style tests exist for the back-office (8 tracked files with "acceptance" in the name).
- **C**: no automated tests; `scomp/config/*.cfg` are scenario inputs (collisions, stress, invalid parameters) used by hand ([README-sprint3 §4](../scomp/README-sprint3.md)).

## 4. Verified findings

| # | Finding | Evidence |
|---|---|---|
| 1 | The project README sends readers to `docs/readme.md`; no such file exists in `docs/` | `ls docs/*.md` |
| 2 | The documentation of the same story id is split across two trees: `docs/us_107`, `us_111`, `us_112` and `scomp/docs/us_107`, `us_111`, `us_112` have different content | `diff` |
| 3 | CI only builds `eapli.base`; the AISafe Java modules, the ANTLR grammar and the C simulator are not built or tested in the pipeline | `maven-ci.yml` |
| 4 | The persistence unit uses a file-based H2 database (`jdbc:h2:./database/aisafe`) with `hibernate.hbm2ddl.auto=create`, i.e. the schema is re-created on every start; user `sa` | `aisafe.persistence.impl/.../persistence.xml` |
| 5 | The cloud scripts default to the team's ISEP cloud VM (host name and IP, reachable only on the university VPN); passwords come from the DEI Cloud panel and are not stored | `scripts/run-cloud.sh`, `deploy-to-cloud.sh` |
| 6 | `libs/` is empty (the README says `libs/*.jar` were left out) and one test file with a very long path was not checked out on Windows, so Windows builds lack it | README notes, `ls libs` |
| 7 | `eapli.base` is a copy of the course example framework (`exemplo.*` modules) rather than the project's own code; it is what CI exercises | `eapli.base/` |
| 8 | SCOMP Sprint 3 reports two outputs (per-process US109 report and per-thread US111 report) and linear interpolation between waypoints instead of the physics formulas in the requirements; both are stated by the team as known limitations | [README-sprint3](../scomp/README-sprint3.md) §3 |
| 9 | Text is a mix of English and Portuguese (SCOMP notes and meeting minutes are largely Portuguese) | files |
| 10 | Several Markdown files use CRLF line endings (the READMEs and the glossary, for example) while `.gitattributes` has `* text=auto`, which is why Git reports LF→CRLF warnings on Windows | `.gitattributes`, `grep -c $''` |

## 5. Improvement list (not applied)

| Priority | Improvement | Effort |
|---|---|---|
| P1 | Point the CI at the AISafe modules (`mvn -B verify` in `aisafe-flight-control` and the root) and add `make` for `scomp` | 1 h |
| P1 | Create `docs/readme.md` (or fix the README link) pointing to this index | 10 min |
| P2 | Merge or cross-link the two `us_107/111/112` document sets | 30 min |
| P2 | Change `hbm2ddl.auto` from `create` to `update` (or `validate`) for any non-demo use | 15 min |
| P2 | Add document folders for US044 and US086 | 1 h |
| P3 | Add a top-level `Makefile`/script listing the build and run commands per component | 30 min |
| P3 | Add C tests (for example CTest or Unity) for the SCOMP simulator | 1 day |
