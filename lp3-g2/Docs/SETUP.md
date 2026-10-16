# Setup — Sheets and Picks (LP3)

> Documentation added in 2026 (portfolio). Derived from the source code and build files. **No command below was run**: this machine has no JDK or Gradle and the course database is not available (marked `not tested`). Portuguese version: [SETUP.pt.md](SETUP.pt.md).

## Prerequisites

| Item | Version | Source |
|---|---|---|
| JDK | 17 or newer (`JavaVersion.VERSION_17`) | `gp2-lp3/SheetsAndPicks/build.gradle` |
| Gradle | 8.4 via the wrapper (`./gradlew`) | `gradle/wrapper/gradle-wrapper.properties` |
| JavaFX | downloaded by `org.openjfx.javafxplugin` | `build.gradle` |
| SQL Server | reachable from your machine | `Controllers/database/DBConnect.java` |

## Environment variables

| Variable | Used by | Meaning |
|---|---|---|
| `SHEETS_DB_URL` | `DBConnect` | Full JDBC URL **including credentials**: `jdbc:sqlserver://<host>;databaseName=<db>;user=<user>;password=<password>;` |

If it is missing or blank, `DBConnect` throws `IllegalStateException("Defina a variável de ambiente SHEETS_DB_URL ...")`.

## Steps

1. **Database.** `Docs/drawSQL-sqlsrv-export-2023-12-02.sql` creates the 14 base tables only. The application also needs views (`Listar_Stock`, `Listar_Stock_Confirmar`, `Ver_pais`), stored procedures (`Criar_Funcionario`, `Criar_Encomenda`, `ConfirmacaodeStock`, ...) and seed rows (at least one `permissao`, `acesso` and `Funcionarios` row, see [ARCHITECTURE](ARCHITECTURE.md#data-model)). None of these is in the repository. TODO: confirmar com o BMS (export the final database).
   - In the export, `VARCHAR` columns have no length (they would be `VARCHAR(1)` in SQL Server); set sensible lengths before running it.
2. **Create a user.** The password column stores an unsalted SHA-256 hex digest. To compute one (PowerShell, not tested):
   ```
   [BitConverter]::ToString([Security.Cryptography.SHA256]::Create().ComputeHash([Text.Encoding]::UTF8.GetBytes("<password>"))).Replace("-","").ToLower()
   ```
   Insert it in `acesso.password` and link the row to `Funcionarios` and a `permissao.Tipo` of `Admin`, `Operador` or `Fornecedor` (exact spelling; `MainController` compares these strings).
3. **Set the URL** (not tested):
   - PowerShell: `$env:SHEETS_DB_URL = "jdbc:sqlserver://localhost;databaseName=SheetsAndPicks;user=sa;password=<password>;encrypt=false;"`
   - bash: `export SHEETS_DB_URL='jdbc:sqlserver://localhost;databaseName=SheetsAndPicks;user=sa;password=<password>;encrypt=false;'`
4. **Run** (not tested):
   ```
   cd lp3-g2/gp2-lp3/SheetsAndPicks
   ./gradlew run
   ```
   Main class: `com.example.sheetsandpicks.MainApplication` (opens the login screen).
5. **Package** (not tested): `./gradlew jlink` / `./gradlew jlinkZip` (zip in `build/distributions/`). The launcher is named `app`.

## XML import

Sample files are in `XMLs/` (`XML-Sample-4.2.xml` … `4.6.xml`). Validation reads the XSD from a hardcoded location (see [Known issues](ARCHITECTURE.md#known-issues-verified-in-the-code-not-fixed) #1):

- the upload screen looks for `C:\Trabalho\Fichalp3\gp2-lp3\XSDverificator.xsd`;
- the stock view looks for `SheetsAndPicks/src/main/XSDverificator.xsd` relative to the process working directory.

Workarounds that do not modify the original source (not tested): copy `gp2-lp3/XSDverificator.xsd` to that Windows path, and start the app with `gp2-lp3` as working directory (for example from a jlink image run from there) so the relative path resolves. `./gradlew run` uses `SheetsAndPicks` as working directory, where the relative path does not resolve.

## Tests

`src/test/java/Verifylogintest.java` is an empty class. `./gradlew test` runs nothing useful (JUnit 4 declared, JUnit Platform commented out).

## Troubleshooting

| Symptom | Likely cause | Fix |
|---|---|---|
| `IllegalStateException: Defina a variável de ambiente SHEETS_DB_URL` | Variable not set in the process that launches Gradle/the IDE | Set it in that shell or in the IDE run configuration |
| `Connection to the SQL Server database failed: ...` | Wrong host, port 1433 closed, bad credentials, TLS settings of newer SQL Server | Check the URL; try `encrypt=false;trustServerCertificate=true;` for a local instance (the driver is 9.4) |
| Login always fails | No `acesso` row, hash mismatch, or `username` unknown | Recreate the user with a SHA-256 hex digest (step 2) |
| Login works but menus are empty | `permissao.Tipo` is not exactly `Admin` / `Operador` / `Fornecedor` | Fix the row |
| Error dialog "Erro na validação do XML" with a file-not-found message | XSD path (see above) | Use the workarounds |
| `Module javafx.controls not found` | Launched with plain `java` | Use Gradle/IDE or the jlink image |
| Blank screen / `LoadException` when opening the fallback user movements view | `app_utilizador_movimentos.fxml` does not exist | Known issue #4 |

## Security notes

- The JDBC URL carries a password; keep it in your shell/IDE, never in a committed file.
- Treat the app as a teaching project: input is concatenated into SQL and passwords are unsalted SHA-256.
