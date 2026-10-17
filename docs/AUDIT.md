# Documentation audit

> Documentation added in 2026 (portfolio). Portuguese version: [AUDIT.pt.md](AUDIT.pt.md).

Status: **pass 3 (re-scored after the added docs)**, 2026-10-17. Pass 1 (2026-10-16) scored the files that existed in the repository; pass 3 re-scores the criteria that the added `ARCHITECTURE`, `SETUP`, `INDEX` and ADR files cover, using the rules below. Criteria not touched by the added docs keep their pass 1 score. Nothing from the original teams was changed, and no finding was fixed.

## Scale

| Score | Meaning |
|---|---|
| 0 | Nothing found |
| 1 | Only mentioned in the README, or a single loose artefact |
| 2 | Dedicated document(s), incomplete |
| 3 | Dedicated, structured, complete and checked against the code |
| – | Not applicable to the project type |

Pass 3 rules (what was applied to each criterion):

- **Architecture (2)**: 3 when an `ARCHITECTURE.md` was written from the code and its issue list was verified; the P3 projects keep their pass 1 score.
- **Setup (3)**: 3 only if the commands were run. Every `SETUP` says which were not tested (no JDK, Gradle, dotnet, PHP, MySQL, Docker or Visual Studio on this machine), so they score 2; only `amd-static-website` (local server run) scores 3. `aisafe-flight-control` goes from 3 to 2 under this rule.
- **Data model (4)**: 3 when the ER is drawn from the real SQL/migrations; 2 when it comes from two sources or a diagram without a schema script (`rentacar`, `pap-gym`) or is inferred (`porto-on-wheels`).
- **API / contracts (5)**: `biblioteca-ermesinde` 2 (endpoint table); `project-proposals-platform` 1 (routes documented, no contracts).
- **Decisions (6)**: 2 for one ADR per P1/P2 project (single ADR, so not 3). The P3 projects have no ADR because the team documents already hold the decisions.
- **Security (9)**: 2 when the known limitations are documented (none of them fixed).
- **Class**: NONE = README only; PARTIAL = some dedicated docs but architecture, setup or ADR missing; COMPLETE = architecture, setup and (ADR or index with traceability) present. Shown as pass 1 → pass 3.

## Criteria

1. README · 2. Architecture · 3. Setup / reproducible run · 4. Data model · 5. API / contracts · 6. Decisions (ADRs) · 7. Tests · 8. Quality / CI · 9. Security · 10. Requirements / user stories · 11. Team / process · 12. Repo hygiene · 13. Bilingual (EN/PT)

## Matrix

| Project | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | 10 | 11 | 12 | 13 | Class |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| [pap-gym](#pap-gym) | 3 | 3 | 2 | 2 | – | 2 | 0 | 0 | 2 | 0 | 0 | 0 | 3 | PARTIAL → COMPLETE |
| [amd-static-website](#amd-static-website) | 3 | 3 | 3 | – | – | 2 | 0 | 0 | – | 0 | 0 | 0 | 3 | NONE → COMPLETE |
| [airline-management-v1](#airline-management-v1) | 3 | 3 | 2 | – | – | 2 | 0 | 0 | – | 0 | 0 | 0 | 3 | NONE → COMPLETE |
| [airline-management-oop](#airline-management-oop) | 3 | 3 | 2 | – | – | 2 | 0 | 0 | – | 0 | 0 | 1 | 3 | NONE → COMPLETE |
| [project-proposals-platform](#project-proposals-platform) | 3 | 3 | 2 | 3 | 1 | 2 | 1 | 0 | 2 | 0 | 0 | 2 | 3 | NONE → COMPLETE |
| [sheets-and-picks](#sheets-and-picks) | 3 | 3 | 2 | 3 | – | 2 | 2 | 0 | 2 | 2 | 3 | 1 | 3 | PARTIAL → COMPLETE |
| [rentacar](#rentacar) | 3 | 3 | 2 | 2 | – | 2 | 0 | 0 | 2 | 0 | 0 | 1 | 3 | NONE → COMPLETE |
| [porto-on-wheels](#porto-on-wheels) | 3 | 3 | 2 | 2 | – | 2 | 0 | 0 | 2 | 0 | 0 | 0 | 3 | NONE → COMPLETE |
| [biblioteca-ermesinde](#biblioteca-ermesinde) | 3 | 3 | 2 | 3 | 2 | 2 | 1 | 2 | 2 | 1 | 0 | 1 | 3 | PARTIAL → COMPLETE |
| [airport-campus-network](#airport-campus-network) | 3 | 3 | 2 | – | – | 0 | – | – | 2 | 2 | 3 | 1 | 3 | PARTIAL → COMPLETE |
| [railway-network-game](#railway-network-game) | 3 | 3 | 2 | 2 | 1 | 1 | 2 | 0 | 2 | 3 | 3 | 1 | 3 | COMPLETE |
| [plant-floor-management](#plant-floor-management) | 3 | 2 | 2 | 3 | 0 | 0 | 2 | 0 | 2 | 3 | 2 | 1 | 3 | COMPLETE |
| [railway-logistics](#railway-logistics) | 3 | 3 | 2 | 2 | 0 | 0 | 2 | 0 | 2 | 3 | 2 | 1 | 3 | COMPLETE |
| [aisafe-flight-control](#aisafe-flight-control) | 3 | 3 | 2 | 2 | 1 | 1 | 2 | 2 | 2 | 3 | 3 | 3 | 3 | COMPLETE |

Class column: pass 1 → pass 3 (rule in the list above).

## Evidence per project

### pap-gym
- Existing: `pap-gym/img/` (screenshots, icons, logos). README run steps with `GYM_DB_PASSWORD`.
- Gaps: no architecture, ER diagram, ADR, tests, `.gitignore`.
- Added (pass 2): [ARCHITECTURE](../pap-gym/docs/ARCHITECTURE.md) (layers, flows, ER read from the team's diagram, 9 verified issues), [SETUP](../pap-gym/docs/SETUP.md) (reconstructed SQL, commands not tested), [ADR 0001](../pap-gym/docs/adr/0001-wpf-mvvm-navigation-direct-sql.md).
- Findings: all SQL built from user input; room capacity checked only in the UI; `MySql.Data` not restorable from NuGet as committed; README says .NET Framework 4.7.2 but the project targets 4.8; website registration form is not wired; three tables in the diagram are never used; no schema script.
- Improvements (P1/P2/P3, effort S/M/L; not applied): P1 (M) parameterise all SQL; P1 (S) enforce room capacity outside the UI; P1 (S) add `.gitignore` (`bin/`, `obj/`); P2 (S) restore `MySql.Data` through NuGet and align the README with the project's .NET Framework 4.8; P2 (S) ship the schema script (SETUP has a reconstructed one); P3 (S) wire or remove the website registration form, add `LICENSE`; P3 (L) first tests.

### amd-static-website
- Existing: README only (static site: `index.html`, `pages/`, `styles/`, `assets/`).
- Gaps: site map, edit/deploy guide, `.gitignore`.
- Added (pass 2): [ARCHITECTURE](../amd-static-website/docs/ARCHITECTURE.md) (Mermaid site map, pages/styles table, 9 verified issues), [SETUP](../amd-static-website/docs/SETUP.md) (local server run and checked; editing and deploy guide), [ADR 0001](../amd-static-website/docs/adr/0001-static-html-css-no-build.md) (status Inferred).
- Findings: root-absolute URLs break on `file://` and under a sub-path (the README's "open `index.html`" gives broken links/styles); `/pages/amdvsintel` link without `.html` returns 404 on a plain server; 3 image references differ in case from the tracked files; `socket_strx4` link missing its host; nav duplicated in 5 files; 3 unreferenced assets; 1 `<img>` without `alt`. Still open: `.gitignore`, licence.
- Improvements (P1/P2/P3, effort S/M/L; not applied): P1 (S) make URLs relative, or make the README say to use a local server; P1 (S) fix the `/pages/amdvsintel` link (404); P2 (S) fix the 3 case-mismatched image references, the `socket_strx4` link and the `<img>` without `alt`; P2 (M) de-duplicate the navigation repeated in 5 files; P3 (S) `.gitignore`, `LICENSE`, remove 3 unreferenced assets.

### airline-management-v1
- Existing: README only (8 tracked files).
- Gaps: class diagram, setup, v1→v2 evolution note, `.gitignore`.
- Added (pass 2): [ARCHITECTURE](../airline-management-v1/docs/ARCHITECTURE.md) (Mermaid class diagram incl. missing classes, 7 verified issues), [SETUP](../airline-management-v1/docs/SETUP.md) (build failure predicted, not run), [ADR 0001](../airline-management-v1/docs/adr/0001-keep-early-version-as-unrepaired-snapshot.md) (curation decision, status Proposed). The v1→v2 evolution is in the sibling's ADR.
- Findings: `Pessoa` and `PilotoTripulante` missing so it does not compile; `Main.voos` never initialised; crew never added; date-time reader cannot parse a valid value; menu runs once with only option 1 implemented. Still open: `.gitignore`.
- Improvements (P1/P2/P3, effort S/M/L; not applied): Kept as an unrepaired snapshot on purpose (ADR 0001), so no code fixes. P2 (S) state in the README that it does not compile and link to `airline-management-oop`; P3 (S) add `.gitignore`.

### airline-management-oop
- Existing: README only (14 tracked files), `.gitignore`.
- Gaps: UML class diagram (inheritance), setup, v1→v2 ADR.
- Added (pass 2): [ARCHITECTURE](../airline-management-oop/docs/ARCHITECTURE.md) (Mermaid class + sequence diagrams, menu, file format, 10 verified issues), [SETUP](../airline-management-oop/docs/SETUP.md) (commands not tested), [ADR 0001](../airline-management-oop/docs/adr/0001-v1-to-v2-pessoa-hierarchy-and-utils.md) (v1→v2).
- Findings: `;`-separated save format breaks on `;` in text and crashes on malformed files; `NaN` average with no matches; no tests; domain depends on static `Main.voos`. The early version `airline-management-v1` references `Pessoa` and `PilotoTripulante`, which are not in the repository, so it does not compile as committed; its menu lists 11 options but only option 1 is implemented.
- Improvements (P1/P2/P3, effort S/M/L; not applied): P1 (S) guard the `NaN` average and handle malformed save files; P2 (M) replace the `;` format with an escaped one (CSV/JSON); P2 (M) unit tests; P2 (M) remove the dependency of the domain on static `Main.voos`; P3 (S) `LICENSE`, `.editorconfig`.

### project-proposals-platform
- Existing: `project-proposals-platform/DB/2023_ERM_DSOS_G1.sql`; 12 test-related files; `.editorconfig`, `.gitattributes`, 13 `.gitignore`.
- Gaps: architecture, ER diagram from the SQL, setup, ADR, how to run the tests.
- Added (pass 2): [ARCHITECTURE](../project-proposals-platform/docs/ARCHITECTURE.md) (roles, routes, flows, ER from the dump, 9 verified issues), [SETUP](../project-proposals-platform/docs/SETUP.md) (commands not tested), [ADR 0001](../project-proposals-platform/docs/adr/0001-laravel-mvc-roles-by-table.md).
- Findings: no role enforcement on routes (any logged-in user can approve companies/proposals), unauthenticated CSV student import storing unhashed passwords, broken project-delivery code, dump and migrations out of sync, personal data and reset tokens in the dump, `.env.example` with non-empty `DB_PASSWORD`/`APP_KEY` (to rotate if real).
- Improvements (P1/P2/P3, effort S/M/L; not applied): P1 (S) empty or rotate `DB_PASSWORD`/`APP_KEY` in `.env.example`; P1 (M) clean personal data and reset tokens from the SQL dump; P1 (M) role checks on the routes, and authentication plus hashing on the CSV student import; P2 (S) fix project delivery and resync dump and migrations; P3 (M) GitHub Actions running the existing test files; P3 (S) `LICENSE`. `.editorconfig` and `.gitattributes` already exist.

### sheets-and-picks
- Existing: `sheets-and-picks/Docs/` with use case, sequence and domain model PNGs, `Modelo de base de dados.png`, `drawSQL-sqlsrv-export-2023-12-02.sql`, `UAT Test Plan.pdf`, `Sprints/*.pdf` (plannings, retrospectives).
- Gaps: setup, ADR, written architecture overview, API/contracts.
- Added (pass 2, in the existing `Docs/` folder because Windows treats `docs` and `Docs` as the same path): [ARCHITECTURE](../sheets-and-picks/Docs/ARCHITECTURE.md) (packages, roles, login and XML-import flows, ER from the SQL export, 7 verified issues), [SETUP](../sheets-and-picks/Docs/SETUP.md) (commands not tested), [ADR 0001](../sheets-and-picks/Docs/adr/0001-xsd-validated-xml-import.md).
- Findings: hardcoded XSD paths (`C:\Trabalho\...`), `ConfirmarStock` processes only the last id, SQL built by concatenation, missing `app_utilizador_movimentos.fxml`, empty test class, no FK on `Produto.id_Encomenda`, `VARCHAR` without length in the export.
- Improvements (P1/P2/P3, effort S/M/L; not applied): P1 (S) make the XSD paths configurable; P1 (S) fix `ConfirmarStock` (processes only the last id); P2 (S) add the missing `app_utilizador_movimentos.fxml` or drop the reference; add the FK on `Produto.id_Encomenda` and `VARCHAR` lengths in the export; P2 (M) parameterised queries and a real test class plus CI; P3 (S) `.editorconfig`, `.gitattributes`, `LICENSE`.

### rentacar
- Existing: README; SQL file `rentacar/RentACar/src/main/java/olabomdia.sql`.
- Gaps: architecture, ER diagram, setup (`RENTACAR_DB_PASSWORD`), ADR, tests.
- Added (pass 2): [ARCHITECTURE](../rentacar/docs/ARCHITECTURE.md) (packages, login/access flow, two ER sources, 7 verified issues), [SETUP](../rentacar/docs/SETUP.md) (commands not tested), [ADR 0001](../rentacar/docs/adr/0001-javafx-jdbc-stored-procedures.md).
- Findings: `build.gradle` `mainClass` points to a non-existent `HelloApplication`; `olabomdia.sql` is schema-only and lacks the tables/views/procedures the code calls; SQL built by concatenation; no tests.
- Improvements (P1/P2/P3, effort S/M/L; not applied): P1 (S) fix `mainClass` so `./gradlew run` works; P1 (M) complete the SQL script with the tables, views and procedures the code calls; P2 (M) parameterised queries; P3 (M) first tests plus CI; P3 (S) `.editorconfig`, `.gitattributes`, `LICENSE`; README says "schema/data script" but the file is schema-only.

### porto-on-wheels
- Existing: README only. No SQL schema in the repository (the only `LICENSE` is the vendored Font Awesome one).
- Gaps: architecture, ER diagram (will be marked **inferred** from queries), setup (`PORTOONWHEELS_DB_*`), ADR, plain-text passwords warning, `.gitignore`.
- Added (pass 2): [ARCHITECTURE](../porto-on-wheels/docs/ARCHITECTURE.md) (files, flows, **inferred** ER, 9 verified issues), [SETUP](../porto-on-wheels/docs/SETUP.md) (reconstructed SQL, commands not tested), [ADR 0001](../porto-on-wheels/docs/adr/0001-custom-mysqli-wrapper.md).
- Findings: registration stores the e-mail as the password; `admin/db/*.php` have no authentication; admin redirects without `exit`; profile update trusts a client-supplied id; plain-text passwords; no schema.
- Improvements (P1/P2/P3, effort S/M/L; not applied): P1 (M) stop storing the e-mail as the password and hash passwords; P1 (M) authentication on `admin/db/*.php` and `exit` after admin redirects; P1 (S) add `.gitignore`; P2 (S) validate the profile id on the server; P2 (S) ship a schema script (SETUP has a reconstructed one); P3 (S) project `LICENSE` (the only one is vendored Font Awesome), `.editorconfig`.

### biblioteca-ermesinde
- Existing: `Relatorio_de_DOS.docx`, `docker-compose.yml`, `dockerfile`, `jenkinsfile`, EF migrations in `Biblioteca_Ermesinde/Migrations`, 4 test-related files.
- Gaps found: architecture (API/EF/DB layers), endpoint table, pipeline diagram, setup (`SA_PASSWORD`, `SONAR_TOKEN`), ADR for Jenkins + SonarQube + Docker Compose.
- Added (pass 2): [ARCHITECTURE](../biblioteca-ermesinde/docs/ARCHITECTURE.md) (layers, ER, API table, pipeline, 10 verified issues), [SETUP](../biblioteca-ermesinde/docs/SETUP.md) (commands not tested), [ADR 0001](../biblioteca-ermesinde/docs/adr/0001-jenkins-sonarqube-docker-compose.md).
- Security flag: `docker-compose.yml` contains a literal SonarQube admin password; move to an env var and rotate (original file left untouched).
- Improvements (P1/P2/P3, effort S/M/L; not applied): P1 (S) move the SonarQube admin password to an environment variable and rotate it; P1 (S) fix `PostReserva` (`.First()`) and run the 4 test-related files in the pipeline; P2 (M) GitHub Actions build and test mirroring the `jenkinsfile`; P2 (S) `.editorconfig`, `.gitattributes`, `LICENSE`; P3 (S) request/response examples for the endpoints, badges.

### airport-campus-network
- Existing: `airport-campus-network/doc/sprint{1,2,3}/` with per-member READMEs, schematics, `planning.md`, `review.md`, `global-inventory.md`, DNS record captures.
- Gaps: index across sprints, ADR, empty-looking folder `sprint2/4444444`, `.gitignore` check.
- Added (pass 2, improve-not-rewrite): [INDEX](../airport-campus-network/docs/INDEX.md) (sprints, task → deliverable → review traceability, design at a glance, improvement list), [SETUP](../airport-campus-network/docs/SETUP.md) (Packet Tracer 9.0.0, files to open; not tested), [GLOSSARY](../airport-campus-network/docs/GLOSSARY.md). Original `doc/` untouched; no ADR (team decisions are already in the plannings).
- Findings: README says "group of 3" but Sprint 1 has 4 members and an unfinished Terminal 5; global inventory is partial (T5 missing); Sprint 3 planning gives the T2 DNS server `.129` (the gateway) while the README implements `.130`; `sprint2/4444444` is the course placeholder; mojibake in the T2 Sprint 3 README; 32 running-configs hold password hashes (`password 7` in the ISP router).
- Improvement list (P1/P2/P3, not applied): [docs/INDEX.md §5](../airport-campus-network/docs/INDEX.md).

### railway-network-game
- Existing: `docs/README.md`, `docs/TeamMembersAndTasks.md`, `docs/auth/`, `docs/datasets/`, `docs/system-documentation/` (523 files, 200 PlantUML), 77 test-related files.
- Gaps: index per US, traceability US → code → tests, CI.
- Added (pass 2, improve-not-rewrite): [INDEX](../railway-network-game/docs/INDEX.md) (US → docs → code table, tests, findings, improvement list), [SETUP](../railway-network-game/docs/SETUP.md) (not tested). Original docs untouched; the domain glossary already exists and is linked, not duplicated.
- Findings: `Main` exposes only the connectivity console menu (US13/US14/US27), none of the ten editor/player UIs of US01–US12 is instantiated; no unit tests (only a performance harness and `US29PerformanceTestRunner`); `TeamMembersAndTasks.md` has no Sprint 3 rows and links to non-existent Sprint 2 US02/US03 folders; `AuthLib` resolved from an external Maven repository; POM artifact still `project-template`.
- Improvement list (P1/P2/P3, not applied): [docs/INDEX.md §5](../railway-network-game/docs/INDEX.md).

### plant-floor-management
- Existing: `documentation/{ARQCP,BDDAD,ESINF,FSIAP,LPRA3}` with SQL per user story (`Create Tables.sql`, `USBD*.sql`), 136 test-related files.
- Gaps: index, glossary, setup, traceability, ADR, CI. Dev-only local passwords remain (Oracle `sys`, `123456`).
- Added (pass 2, improve-not-rewrite): [INDEX](../plant-floor-management/docs/INDEX.md) (4 subsystems, US family → docs → code, tests, findings, improvement list), [SETUP](../plant-floor-management/docs/SETUP.md) (per subsystem; not tested). Existing glossaries (LAPR3, BDDAD) are linked, not duplicated; no ADR (decisions are in the course deliverables).
- Findings: the simulator resolves its CSV paths from the nearest `.git` root, so inside the portfolio they point to a non-existent `portfolio/documentation/ESINF` (predicted, not run); BDDAD Sprint 2 exists as two diverging copies (20 differences); 6 `.o`, 15 `.bak_*`, 2 `.lck` and `.DS_Store` tracked; extension-less story files; physical Arduino machine replaced by an emulator; non-empty local Oracle password in `config.properties`.
- Improvement list (P1/P2/P3, not applied): [docs/INDEX.md §5](../plant-floor-management/docs/INDEX.md).

### railway-logistics
- Existing: `docs/{ARQCP,BDDAD,ESINF,LAPR3}`, `docs/global-artifacts`, `docs/README.md`, 54 PlantUML files, 40 test-related files.
- Gaps: index, glossary, setup, traceability, ADR, CI.
- Added (pass 2, improve-not-rewrite): [INDEX](../railway-logistics/docs/INDEX.md) (layout, US → docs → code → tests for ESINF/LAPR3/BDDAD/ARQCP, findings, improvement list), [SETUP](../railway-logistics/docs/SETUP.md) (Java, Oracle, RISC-V; not tested). The existing glossary, business rules and domain model are linked, not duplicated; no ADR.
- Findings: `docs/README.md` details only USEI01–03 although nine ESINF stories exist; USLP07 and USLP10 share a title and USLP10 lacks a test document; USAC11–16 have code but no docs; JDBC URL hard-coded in 5 Java files and password literals in 5 (values not reproduced); assembly Makefiles need a RISC-V cross toolchain and QEMU not mentioned in the README; no run command for the Java entry point; story ids are cited mostly in tests, not production code.
- Improvement list (P1/P2/P3, not applied): [docs/INDEX.md §5](../railway-logistics/docs/INDEX.md).

### aisafe-flight-control
- Existing: `docs/global-artifacts/` (glossary, group composition, `CLOUD_TESTING.md`, `CLOUD_VPN_ACCESS.md`, meetings, PlantUML use cases, requirements PDFs), `docs/us_*`, `docs/Peer-Assessement/`, CI workflow, `LICENSE`, 129 test-related files.
- Gaps: index per US, traceability, ADR, one test file omitted (long path), LF→CRLF warnings.
- Added (pass 2, improve-not-rewrite): [INDEX](../aisafe-flight-control/docs/INDEX.md) (layout, US family → docs → code → tests, CI, findings, improvement list), [SETUP](../aisafe-flight-control/docs/SETUP.md) (per component; not tested). Existing glossary and cloud notes are linked, not duplicated; no ADR.
- Findings: README links to a missing `docs/readme.md`; US100–US112 docs live in two trees (`docs/` and `scomp/docs/`) with different content for US107/111/112; CI builds only `eapli.base` (the example framework), not the AISafe modules, `lprog` or `scomp`; H2 schema re-created on every start (`hbm2ddl.auto=create`); US044 and US086 are cited in code without docs; cloud scripts depend on the ISEP VPN.
- Improvement list (P1/P2/P3, not applied): [docs/INDEX.md §5](../aisafe-flight-control/docs/INDEX.md).

## Next passes

1. Done: pass 1 (inventory), pass 2 (docs for the 14 projects, one commit each), pass 3 (re-scoring and improvement lists, this file).
2. Pending, global verification before closing: secret grep, relative-link check over every added `.md`, additions-only diff, Mermaid syntax check.
3. Decisions that stay with the BMS: rotate and move the credentials listed in the findings, clean the SQL dump of `project-proposals-platform`, and apply (or not) any improvement above. None was applied.
