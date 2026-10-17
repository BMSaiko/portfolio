# Setup — Airline Management (OOP refactor, Group 3)

> Documentation added in 2026 (portfolio). Commands below were **not tested** on the machine where this was written (no JDK or Maven available); they follow from `pom.xml` and the README. Portuguese version: [SETUP.pt.md](SETUP.pt.md).

## Prerequisites

| Tool | Version | Why |
|---|---|---|
| JDK | 17 or newer | `pom.xml` sets `maven.compiler.source/target` to 17 |
| Maven | 3.x | Build; the POM has no plugins or dependencies |

No database, no environment variables, no secrets.

## Build and run

```bash
cd apoo-lp1-companhia-aerea/grupo3-apoo-lp1-projeto-companhiaarea
mvn compile exec:java -Dexec.mainClass=org.companhiaaerea.Main
```

`exec:java` is invoked by its short prefix (the POM does not configure the plugin), so Maven downloads `exec-maven-plugin` on first use — an internet connection is needed once. Alternative without the plugin:

```bash
mvn compile
java -cp target/classes org.companhiaaerea.Main
```

## Using the app

1. Choose `1` to add a flight; answer the prompts (flight `VXXX`, aircraft, airports, `dd/MM/yyyy HH:mm` dates, passengers — enter `0` as identification number to stop — and crew).
2. Use `3`–`8` to search and compute statistics (dates as `dd/MM/yyyy`).
3. Use `9` to save and `10` to load. Files are written to `<your home>/companhia_aerea/<name>.voos` (for example `C:\Users\<you>\companhia_aerea\` on Windows). Loading **replaces** the flights currently in memory.
4. `0` exits and offers to save when there are flights.

## Troubleshooting

| Symptom | Likely cause / fix |
|---|---|
| `error: release version 17 not supported` | JDK older than 17; install JDK 17+ and check `java -version` / `mvn -version` |
| `No plugin found for prefix 'exec'` | Offline on first run; connect once or use the `java -cp` alternative |
| Exception when loading a file | File was edited by hand or a field contained `;` (see [ARCHITECTURE](ARCHITECTURE.md#5-verified-issues-read-from-the-code), issues 1–2); delete or regenerate it |
| `ArrayIndexOutOfBoundsException` after option 10 | Typed a number outside the listed files |
| `NaN` as average (option 7) | No flights in the chosen interval |
| Accents look wrong in the console | Console code page; the sources are UTF-8 (`project.build.sourceEncoding`) — try `chcp 65001` on Windows |

## Tests

None. There is no `src/test` folder and the POM declares no test framework, so `mvn test` has nothing to run.
