# Documentation audit

> Documentation added in 2026 (portfolio). Portuguese version: [AUDIT.pt.md](AUDIT.pt.md).

Status: **pass 1 (file inventory)**, 2026-10-16. Scores come from which files exist in the repository (paths cited below). Quality sampling and the improvement lists (P1/P2/P3) are added in later passes. Nothing from the original teams was changed.

## Scale

| Score | Meaning |
|---|---|
| 0 | Nothing found |
| 1 | Only mentioned in the README, or a single loose artefact |
| 2 | Dedicated document(s), incomplete |
| 3 | Dedicated, structured and complete (by inventory; quality not yet sampled) |
| – | Not applicable to the project type |

## Criteria

1. README · 2. Architecture · 3. Setup / reproducible run · 4. Data model · 5. API / contracts · 6. Decisions (ADRs) · 7. Tests · 8. Quality / CI · 9. Security · 10. Requirements / user stories · 11. Team / process · 12. Repo hygiene · 13. Bilingual (EN/PT)

## Matrix

| Project | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | 11 | 12 | 13 | Class |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| [pap-gym](#pap-gym) | 3 | 1 | 2 | 1 | – | 0 | 0 | 0 | 1 | 0 | 0 | 0 | 3 | PARTIAL |
| [lp1-2022-g2-tema1](#lp1-2022-g2-tema1) | 3 | 0 | 1 | – | – | 0 | 0 | 0 | – | 0 | 0 | 0 | 3 | NONE |
| [companhia-aerea](#companhia-aerea) | 3 | 0 | 1 | – | – | 0 | 0 | 0 | – | 0 | 0 | 0 | 3 | NONE |
| [apoo-lp1-companhia-aerea](#apoo-lp1-companhia-aerea) | 3 | 0 | 1 | – | – | 0 | 0 | 0 | – | 0 | 0 | 1 | 3 | NONE |
| [dsos-grupo1-bombastico](#dsos-grupo1-bombastico) | 3 | 0 | 1 | 2 | 0 | 0 | 1 | 0 | 1 | 0 | 0 | 2 | 3 | NONE |
| [lp3-g2](#lp3-g2) | 3 | 2 | 1 | 3 | – | 0 | 2 | 0 | 1 | 2 | 3 | 1 | 3 | PARTIAL |
| [rentacar](#rentacar) | 3 | 0 | 1 | 1 | – | 0 | 0 | 0 | 1 | 0 | 0 | 1 | 3 | NONE |
| [porto-on-wheels](#porto-on-wheels) | 3 | 0 | 1 | 0 | – | 0 | 0 | 0 | 1 | 0 | 0 | 0 | 3 | NONE |
| [biblioteca-ermesinde](#biblioteca-ermesinde) | 3 | 0 | 2 | 2 | 1 | 0 | 1 | 2 | 1 | 1 | 0 | 1 | 3 | PARTIAL |
| [rcomp-25-26-de-g2](#rcomp-25-26-de-g2) | 3 | 3 | 2 | – | – | 0 | – | – | 1 | 2 | 3 | 1 | 3 | PARTIAL |
| [sem2-pi-24-25-g152](#sem2-pi-24-25-g152) | 3 | 3 | 2 | 2 | 1 | 1 | 2 | 0 | 2 | 3 | 3 | 1 | 3 | COMPLETE |
| [sem3-pi-24-25-g152](#sem3-pi-24-25-g152) | 3 | 2 | 1 | 3 | 0 | 0 | 2 | 0 | 1 | 3 | 2 | 1 | 3 | COMPLETE |
| [sem3-pi-25-26-g075](#sem3-pi-25-26-g075) | 3 | 3 | 1 | 2 | 0 | 0 | 2 | 0 | 1 | 3 | 2 | 1 | 3 | COMPLETE |
| [sem4pi-25-26-2de4](#sem4pi-25-26-2de4) | 3 | 3 | 3 | 2 | 1 | 1 | 2 | 2 | 2 | 3 | 3 | 3 | 3 | COMPLETE |

Class rule: **NONE** = no technical docs beyond the README; **PARTIAL** = some dedicated docs but at least one of architecture / setup / ADR missing; **COMPLETE** = per-sprint or per-story docs exist (still gets an improvement list).

## Evidence per project

### pap-gym
- Existing: `pap-gym/img/` (screenshots, icons, logos). README run steps with `GYM_DB_PASSWORD`.
- Gaps: no architecture, ER diagram, ADR, tests, `.gitignore`.
- Added (pass 2): [ARCHITECTURE](../pap-gym/docs/ARCHITECTURE.md) (layers, flows, ER read from the team's diagram, 9 verified issues), [SETUP](../pap-gym/docs/SETUP.md) (reconstructed SQL, commands not tested), [ADR 0001](../pap-gym/docs/adr/0001-wpf-mvvm-navigation-direct-sql.md).
- Findings: all SQL built from user input; room capacity checked only in the UI; `MySql.Data` not restorable from NuGet as committed; README says .NET Framework 4.7.2 but the project targets 4.8; website registration form is not wired; three tables in the diagram are never used; no schema script.

### lp1-2022-g2-tema1
- Existing: README only (static site: `index.html`, `pages/`, `styles/`, `assets/`).
- Gaps: site map, edit/deploy guide, `.gitignore`.

### companhia-aerea
- Existing: README only (8 tracked files).
- Gaps: class diagram, setup, v1→v2 evolution note, `.gitignore`.
- Added (pass 2): [ARCHITECTURE](../companhia-aerea/docs/ARCHITECTURE.md) (Mermaid class diagram incl. missing classes, 7 verified issues), [SETUP](../companhia-aerea/docs/SETUP.md) (build failure predicted, not run), [ADR 0001](../companhia-aerea/docs/adr/0001-keep-early-version-as-unrepaired-snapshot.md) (curation decision, status Proposed). The v1→v2 evolution is in the sibling's ADR.
- Findings: `Pessoa` and `PilotoTripulante` missing so it does not compile; `Main.voos` never initialised; crew never added; date-time reader cannot parse a valid value; menu runs once with only option 1 implemented. Still open: `.gitignore`.

### apoo-lp1-companhia-aerea
- Existing: README only (14 tracked files), `.gitignore`.
- Gaps: UML class diagram (inheritance), setup, v1→v2 ADR.
- Added (pass 2): [ARCHITECTURE](../apoo-lp1-companhia-aerea/docs/ARCHITECTURE.md) (Mermaid class + sequence diagrams, menu, file format, 10 verified issues), [SETUP](../apoo-lp1-companhia-aerea/docs/SETUP.md) (commands not tested), [ADR 0001](../apoo-lp1-companhia-aerea/docs/adr/0001-v1-to-v2-pessoa-hierarchy-and-utils.md) (v1→v2).
- Findings: `;`-separated save format breaks on `;` in text and crashes on malformed files; `NaN` average with no matches; no tests; domain depends on static `Main.voos`. The early version `companhia-aerea` references `Pessoa` and `PilotoTripulante`, which are not in the repository, so it does not compile as committed; its menu lists 11 options but only option 1 is implemented.

### dsos-grupo1-bombastico
- Existing: `dsos-grupo1-bombastico/DB/2023_ERM_DSOS_G1.sql`; 12 test-related files; `.editorconfig`, `.gitattributes`, 13 `.gitignore`.
- Gaps: architecture, ER diagram from the SQL, setup, ADR, how to run the tests.
- Added (pass 2): [ARCHITECTURE](../dsos-grupo1-bombastico/docs/ARCHITECTURE.md) (roles, routes, flows, ER from the dump, 9 verified issues), [SETUP](../dsos-grupo1-bombastico/docs/SETUP.md) (commands not tested), [ADR 0001](../dsos-grupo1-bombastico/docs/adr/0001-laravel-mvc-roles-by-table.md).
- Findings: no role enforcement on routes (any logged-in user can approve companies/proposals), unauthenticated CSV student import storing unhashed passwords, broken project-delivery code, dump and migrations out of sync, personal data and reset tokens in the dump, `.env.example` with non-empty `DB_PASSWORD`/`APP_KEY` (to rotate if real).

### lp3-g2
- Existing: `lp3-g2/Docs/` with use case, sequence and domain model PNGs, `Modelo de base de dados.png`, `drawSQL-sqlsrv-export-2023-12-02.sql`, `UAT Test Plan.pdf`, `Sprints/*.pdf` (plannings, retrospectives).
- Gaps: setup, ADR, written architecture overview, API/contracts.
- Added (pass 2, in the existing `Docs/` folder because Windows treats `docs` and `Docs` as the same path): [ARCHITECTURE](../lp3-g2/Docs/ARCHITECTURE.md) (packages, roles, login and XML-import flows, ER from the SQL export, 7 verified issues), [SETUP](../lp3-g2/Docs/SETUP.md) (commands not tested), [ADR 0001](../lp3-g2/Docs/adr/0001-xsd-validated-xml-import.md).
- Findings: hardcoded XSD paths (`C:\Trabalho\...`), `ConfirmarStock` processes only the last id, SQL built by concatenation, missing `app_utilizador_movimentos.fxml`, empty test class, no FK on `Produto.id_Encomenda`, `VARCHAR` without length in the export.

### rentacar
- Existing: README; SQL file `rentacar/RentACar/src/main/java/olabomdia.sql`.
- Gaps: architecture, ER diagram, setup (`RENTACAR_DB_PASSWORD`), ADR, tests.
- Added (pass 2): [ARCHITECTURE](../rentacar/docs/ARCHITECTURE.md) (packages, login/access flow, two ER sources, 7 verified issues), [SETUP](../rentacar/docs/SETUP.md) (commands not tested), [ADR 0001](../rentacar/docs/adr/0001-javafx-jdbc-stored-procedures.md).
- Findings: `build.gradle` `mainClass` points to a non-existent `HelloApplication`; `olabomdia.sql` is schema-only and lacks the tables/views/procedures the code calls; SQL built by concatenation; no tests.

### porto-on-wheels
- Existing: README only. No SQL schema in the repository (the only `LICENSE` is the vendored Font Awesome one).
- Gaps: architecture, ER diagram (will be marked **inferred** from queries), setup (`PORTOONWHEELS_DB_*`), ADR, plain-text passwords warning, `.gitignore`.
- Added (pass 2): [ARCHITECTURE](../porto-on-wheels/docs/ARCHITECTURE.md) (files, flows, **inferred** ER, 9 verified issues), [SETUP](../porto-on-wheels/docs/SETUP.md) (reconstructed SQL, commands not tested), [ADR 0001](../porto-on-wheels/docs/adr/0001-custom-mysqli-wrapper.md).
- Findings: registration stores the e-mail as the password; `admin/db/*.php` have no authentication; admin redirects without `exit`; profile update trusts a client-supplied id; plain-text passwords; no schema.

### biblioteca-ermesinde
- Existing: `Relatorio_de_DOS.docx`, `docker-compose.yml`, `dockerfile`, `jenkinsfile`, EF migrations in `Biblioteca_Ermesinde/Migrations`, 4 test-related files.
- Gaps found: architecture (API/EF/DB layers), endpoint table, pipeline diagram, setup (`SA_PASSWORD`, `SONAR_TOKEN`), ADR for Jenkins + SonarQube + Docker Compose.
- Added (pass 2): [ARCHITECTURE](../biblioteca-ermesinde/docs/ARCHITECTURE.md) (layers, ER, API table, pipeline, 10 verified issues), [SETUP](../biblioteca-ermesinde/docs/SETUP.md) (commands not tested), [ADR 0001](../biblioteca-ermesinde/docs/adr/0001-jenkins-sonarqube-docker-compose.md).
- Security flag: `docker-compose.yml` contains a literal SonarQube admin password; move to an env var and rotate (original file left untouched).

### rcomp-25-26-de-g2
- Existing: `rcomp-25-26-de-g2/doc/sprint{1,2,3}/` with per-member READMEs, schematics, `planning.md`, `review.md`, `global-inventory.md`, DNS record captures.
- Gaps: index across sprints, ADR, empty-looking folder `sprint2/4444444`, `.gitignore` check.

### sem2-pi-24-25-g152
- Existing: `docs/README.md`, `docs/TeamMembersAndTasks.md`, `docs/auth/`, `docs/datasets/`, `docs/system-documentation/` (523 files, 200 PlantUML), 77 test-related files.
- Gaps: index per US, traceability US → code → tests, CI.

### sem3-pi-24-25-g152
- Existing: `documentation/{ARQCP,BDDAD,ESINF,FSIAP,LPRA3}` with SQL per user story (`Create Tables.sql`, `USBD*.sql`), 136 test-related files.
- Gaps: index, glossary, setup, traceability, ADR, CI. Dev-only local passwords remain (Oracle `sys`, `123456`).

### sem3-pi-25-26-g075
- Existing: `docs/{ARQCP,BDDAD,ESINF,LAPR3}`, `docs/global-artifacts`, `docs/README.md`, 54 PlantUML files, 40 test-related files.
- Gaps: index, glossary, setup, traceability, ADR, CI.

### sem4pi-25-26-2de4
- Existing: `docs/global-artifacts/` (glossary, group composition, `CLOUD_TESTING.md`, `CLOUD_VPN_ACCESS.md`, meetings, PlantUML use cases, requirements PDFs), `docs/us_*`, `docs/Peer-Assessement/`, CI workflow, `LICENSE`, 129 test-related files.
- Gaps: index per US, traceability, ADR, one test file omitted (long path), LF→CRLF warnings.

## Next passes

1. P1 docs to create: `biblioteca-ermesinde`, then `rentacar`, `lp3-g2`, `dsos-grupo1-bombastico`, `porto-on-wheels`, `pap-gym` (`ARCHITECTURE`, `SETUP`, ADR, ER/endpoints, EN+PT).
2. P2: `apoo-lp1-companhia-aerea`, `companhia-aerea`, `lp1-2022-g2-tema1`.
3. P3: improve (do not rewrite) `rcomp`, `sem2`, `sem3` ×2, `sem4`.
4. Per-project improvement lists (P1/P2/P3) in this file.
