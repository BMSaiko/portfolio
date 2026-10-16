# Setup — RentACar

> Documentation added in 2026 (portfolio). Derived from the source code and build files. **No command below was run**: this machine has no JDK, Gradle or access to the course database (marked `not tested`). Portuguese version: [SETUP.pt.md](SETUP.pt.md).

## Prerequisites

| Item | Version | Source |
|---|---|---|
| JDK | 19 (`sourceCompatibility = '19'`) | `RentACar/build.gradle` |
| Gradle | 8.0.2 via the wrapper (`./gradlew`) | `gradle/wrapper/gradle-wrapper.properties` |
| JavaFX | 19.0.2, downloaded by the `org.openjfx.javafxplugin` | `build.gradle` |
| SQL Server | reachable from your machine | `Database/DatabaseConnection.java` |

## Environment variables

| Variable | Used by | Meaning |
|---|---|---|
| `RENTACAR_DB_PASSWORD` | `DatabaseConnection` | Password of the SQL Server user. Without it the URL contains `password=null` and the login fails. |

Host (`ctespbd.dei.isep.ipp.pt`), database (`2023_DIAS_G4_ERMESINDE`) and user (`2023_DIAS_G4_ERMESINDE`) are hardcoded in `DatabaseConnection.java`. That server belonged to the course and is probably only reachable from the ISEP network or VPN. TODO: confirmar com o BMS (whether it is still online).

## Steps

1. **Database.** Either use a database that already has the application's objects, or build your own:
   - `RentACar/src/main/java/olabomdia.sql` creates only the base tables (no data, procedures, functions or views); see [ARCHITECTURE](ARCHITECTURE.md#data-model) section B for what is missing. The application cannot log in without `dbo.verify_login` and the `Funcionario` table (`nome`, `nivel_acesso`).
   - Point the connection at your server by editing the URL in `Database/DatabaseConnection.java`.
2. **Set the password** (not tested):
   - PowerShell: `$env:RENTACAR_DB_PASSWORD = "<password>"`
   - bash: `export RENTACAR_DB_PASSWORD='<password>'`
3. **Run** (not tested):
   ```
   cd RentACar
   ./gradlew run
   ```
   As committed, this is expected to fail with a missing main class: `build.gradle` sets `mainClass = 'com.example.rentacar.HelloApplication'`, which does not exist. Use one of these:
   - Run `com.example.rentacar.Rent_a_carApplication` from the IDE (IntelliJ: it needs the JavaFX Gradle plugin, which the project already applies).
   - Or change that line locally to `com.example.rentacar.Rent_a_carApplication` (the original file was left untouched in this repository).
4. **Package** (not tested): `./gradlew jlink` builds a runtime image; `./gradlew jlinkZip` zips it into `build/distributions/` (task group `distribution`). The jlink launcher is named `app`.

## Login

Credentials live in the database (`dbo.verify_login`, table `Funcionario`). None are stored in the repository. `nivel_acesso` 1, 2 and 3+ show progressively more of the menu (see [ARCHITECTURE](ARCHITECTURE.md#login-and-access-levels)).

## Troubleshooting

| Symptom | Likely cause | Fix |
|---|---|---|
| `Could not find or load main class ...HelloApplication` / `ClassNotFoundException` | Wrong `mainClass` in `build.gradle` | See step 3 |
| `Connection to the SQL Server database failed: ...` on the console | Server unreachable (not on ISEP network/VPN) or wrong password | Check network, `RENTACAR_DB_PASSWORD`, firewall; or point to your own server |
| Login always fails but the connection works | `dbo.verify_login` or `Funcionario` missing, or no staff row | Create them (not in the repository) |
| `NullPointerException` after a failed connection | `DatabaseConnection` keeps a `null` connection | Fix the connection first |
| `Unsupported class file major version` / `invalid source release: 19` | JDK older than 19 | Install JDK 19+ and set `JAVA_HOME` |
| Module errors such as `Module javafx.controls not found` | Launching with plain `java` instead of Gradle/IDE | Use `./gradlew run` or the jlink image |
| Accented text is garbled | Source encoding | The build sets `UTF-8`; make sure the IDE does too |

## Tests

None exist in the repository (JUnit 5 is declared in `build.gradle`, `src/test` is absent). `./gradlew test` would run nothing.

## Security notes

- Never commit a real password; the original one was removed from the repository.
- Treat the application as a teaching project: input is concatenated into SQL (see [Known issues](ARCHITECTURE.md#known-issues-verified-in-the-code-not-fixed)).
