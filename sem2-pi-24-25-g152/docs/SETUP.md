# Setup — Railway Network Game

> Documentation added in 2026 (portfolio). Commands were **not tested** (no JDK or Maven on the machine where this was written); everything is read from `pom.xml`, `Main.java` and the READMEs. Portuguese version: [SETUP.pt.md](SETUP.pt.md).

## Prerequisites

| Tool | Version / source |
|---|---|
| JDK | 17 (`java.version` in `pom.xml`) |
| Maven | 3.x |
| Internet access | Dependencies include `AuthLib` (`pt.isep.lei.esoft`, from `https://repo.repsy.io/mvn/pmaio/lei-soft`, declared in the POM), GraphStream `gs-core`, JUnit Jupiter, commons-lang3 |
| Jupyter (optional) | Only for the MATCP notebooks (`matcp.ipynb`, `MATCP/US30`, `MATCP/US31`) |

No database, environment variables or secrets. If the external Maven repository is no longer available, dependency resolution of `AuthLib` fails (prediction from the POM, not an observed run).

## Run (from the README)

```bash
cd sem2-pi-24-25-g152
mvn compile exec:java -Dexec.mainClass=pt.ipp.isep.dei.Main
```

The program asks for a scenario (1–4, default 1) loaded from `src/main/resources/scenario<N>_stations.csv` / `scenario<N>_lines.csv`, then shows a console menu:

1. Check if a train can travel between two stations (US13)
2. Find stations of a type reachable from a source station
3. Find a maintenance route through every railway line once (US14)
4. Find the shortest path with waypoints (US27)
5. Meet the dev team
6. Exit

The editor/player features of US01–US12 are **not reachable** from this menu ([INDEX §4](INDEX.md#4-verified-findings), finding 1).

## Performance mode (US29)

`Main` accepts `PERFORMANCE_TEST <stationsFile> <linesFile> <US13|US14> [param]` and prints `EXECUTION_TIME:<ms>` and `RESULT:<bool>`; `param` is the train type for US13 (default `DIESEL`) or a boolean (`electrifiedOnly`) for US14. The output format is meant to be parsed by a script ([US29 doc](system-documentation/sprint3/US29/us29_documentation.md)). Not run here.

## Tests

```bash
mvn test
```

Not run. The only test sources are the performance harness and `US29PerformanceTestRunner` ([INDEX §3](INDEX.md#3-tests)); the performance tests generate large scenarios and may take a long time. TODO: confirmar com o BMS the intended way to run the US29 measurements.

## Package

The POM configures `maven-assembly-plugin` with `jar-with-dependencies` and `mainClass` `pt.ipp.isep.dei.Main`:

```bash
mvn package
```

Not run; the exact output name depends on the POM's `finalName`/`artifactId` (`project-template`).
