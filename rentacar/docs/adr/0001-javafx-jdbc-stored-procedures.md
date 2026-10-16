# ADR 0001 — JavaFX desktop client with direct JDBC and logic in SQL Server

> Documentation added in 2026 (portfolio). Reconstructed from the repository files; the original team's reasoning is not recorded, so motivations below are marked as inferred. Portuguese version: [0001-javafx-jdbc-stored-procedures.pt.md](0001-javafx-jdbc-stored-procedures.pt.md).

- **Status:** Accepted (as implemented in the repository)
- **Evidence:** `RentACar/build.gradle`, `module-info.java`, `Database/DatabaseConnection.java`, `Menu/LoginService.java`, `Aluguer/Criar_AluguerController.java`, `src/main/resources/**/*.fxml`
- **Confirm with the author:** TODO: confirmar com o BMS (motivations, why not Swing/web, who wrote the stored procedures)

## Context

The CTESP group project asks for an application that manages a car-rental company: clients, staff, vehicles, rentals, quotes and payments, with maintenance dashboards. The course provides a shared SQL Server instance per group, and the team works in Java.

## Decision

| Concern | Choice | Alternatives not taken (inferred) |
|---|---|---|
| UI | JavaFX 19 with FXML views, one controller per screen | Swing (no declarative layout), a web UI (would need a server tier) |
| Persistence | Plain JDBC (`mssql-jdbc`) through a singleton `DatabaseConnection` | JPA/Hibernate, a DAO layer |
| Business rules | Stored procedures (`Alugar_carro`, `Atualizar_aluguer`, `InserirCliente`, ...), a login function and views (`ListarVeiculo`, `ConsultarAluguer`) in SQL Server | Rules in Java services |
| Shared session state | `SingletonData` / `Controller` (Singleton pattern) | Dependency injection, passing the user through constructors |
| Build | Gradle with the module, JavaFX and jlink plugins | Maven |

## Consequences

- **Good:** each screen is small and easy to find (FXML + controller); rental logic is centralised in the database; the app is a single deployable image (`jlink`).
- **Bad:**
  - The database holds logic that is not versioned: the committed `olabomdia.sql` lacks the procedures, views and tables the code calls.
  - SQL is built by string concatenation (SQL injection) and the connection is a shared singleton with no error recovery.
  - Access control is UI-only.
  - The Java code cannot be tested without a live database.
- **If it were redone:** `PreparedStatement`/`CallableStatement` for every query, a repository layer behind interfaces (so controllers can be tested with fakes), schema and procedures in versioned migration scripts, and server-side authorization. These are suggestions, not changes made to the project.
