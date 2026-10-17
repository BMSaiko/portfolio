# Setup — AISafe Flight Control Platform

> Documentation added in 2026 (portfolio). Commands were **not tested** (this machine has no JDK, Maven or GCC). Everything is read from the POMs, scripts, CI workflow and the team's READMEs. Portuguese version: [SETUP.pt.md](SETUP.pt.md).

## Prerequisites

| Tool | Version / source |
|---|---|
| JDK | 21 (`maven.compiler.source/target` and `<release>` in the POMs; CI uses Temurin 21) |
| Maven | 3.x (scripts use `mvnw.cmd` if present, else `mvn`) |
| GCC + make + POSIX threads/semaphores | for `scomp` (Linux/WSL) and `lprog/src/main/c/flight_tester` |
| Internet | Maven dependencies; the ANTLR4 plugin generates the DSL parser during the build |

Database: embedded **H2 file database** (`./database/aisafe`), created automatically; nothing to install. Note that the schema is re-created at every start ([INDEX §4](INDEX.md#4-verified-findings), finding 4).

## Build (from the README)

```bash
cd sem4pi-25-26-2de4
mvn clean install                      # root POM: eapli.base + lprog (ANTLR grammar)
cd aisafe-flight-control && mvn clean install
cd ../scomp && make                    # C simulator
```

`libs/` is empty on purpose. A back-office acceptance test file with a very long path is not in the repository ([README](../README.md) notes).

## Run

### Back office (Java, console)

```bash
cd aisafe-flight-control
mvn -DskipTests install
mvn exec:java -pl aisafe.app.backoffice.console
```

This is what `scripts/run-backoffice.bat` does on Windows. Load sample data first with `scripts/bootstrap.bat` or the `aisafe.bootstrap` module (users, areas, airports, aircraft models, companies, and 12 months of data according to the README).

Other entry points (class names, found by `main`): `AirTransportCompanyApp` (company console), `AtccTcpServer` and `WeatherServerMain` (server, `aisafe.app.server`), `RemoteAccessesLoggingServer` (`aisafe-logging-server`). The scripts `run-us078-us090.bat` and the `phase1`…`phase6` batch files wire server, client, logging and dashboard together on Windows; the cloud variants (`run-cloud.sh`, `deploy-to-cloud.sh`, `setup-cloud-vs.sh`) need the ISEP VPN and a cloud VM (see [cloud testing](global-artifacts/CLOUD_TESTING.md) and [VPN access](global-artifacts/CLOUD_VPN_ACCESS.md)). TODO: confirmar com o BMS the minimal local sequence (bootstrap → server → client → logging server) and the TCP/UDP ports; `LOGGING_UDP_PORT=9999` and `LOGGING_HTTP_PORT=8080` appear in `run-cloud.sh`.

### Flight DSL (`lprog`)

The grammar is `lprog/src/main/antlr4/pt/isep/sem4pi/lprog/FlightDSL.g4`; `mvn clean install` at the root generates the parser. The C tester builds with its own Makefile in `lprog/src/main/c/flight_tester/`.

### SCOMP simulator (C)

From [README-sprint3](../scomp/README-sprint3.md) §4:

```bash
cd scomp
make clean && make
./air_sim config/opo_mad.cfg            # 2 flights, no violations
./air_sim config/opo_mad_stress.cfg     # 8 flights, violations and terminations
```

Other scenarios are in `scomp/config/` (`collisions.cfg`, `invalid_params.cfg`, `progressive_collisions.cfg`, `single_flight.cfg`, `three_violations.cfg`). Reports are written next to the executable (for example `simulation_report_<timestamp>.txt`).

## Tests

```bash
cd aisafe-flight-control && mvn test     # ~880 JUnit 5 test methods across the AISafe modules
```

Not run. The CI workflow runs tests only for `eapli.base` ([INDEX §3](INDEX.md#3-tests-and-ci)). The C simulator has no automated tests.

## Diagrams

`generate-plantuml-diagrams.sh` at the repository root regenerates the PlantUML diagrams (needs PlantUML; not run).
