# RentACar

🇬🇧 English · [🇵🇹 Português](README.pt.md)

> Desktop application to manage a car rental company.

| | |
|---|---|
| **Context** | CTESP — Group project |
| **Year** | 2023/24 |
| **Team** | Group 4 |
| **Stack** | Java 19 · JavaFX · Gradle · SQL Server (JDBC) |

## What this project shows
- JavaFX UI with packages per domain (`Cliente`, `Staff`, `Veiculo`, `Aluguer`, `dash`).
- Use of design patterns (a `Singleton` package for shared state).
- SQL Server persistence over JDBC with a provided schema/data script.

## Features
- Staff login with access levels and a dashboard.
- Management of clients, staff, vehicles and rentals.
- Search/filter in the tables.

## Structure
| Path | Content |
|---|---|
| `RentACar/src/main/java/com/example/rentacar/` | Application code |
| `RentACar/src/main/java/olabomdia.sql` | Schema/data script |

## Running
1. Create the database from `RentACar/src/main/java/olabomdia.sql` and adjust `Database/DatabaseConnection.java`.
2. Set `RENTACAR_DB_PASSWORD`.
3. `cd RentACar && ./gradlew run`

## Notes
- The database password from the original repository was removed.

## Documentation

- [Documentation audit](../docs/AUDIT.md#rentacar) — status and gaps for this project
- [Architecture, data model and known issues](docs/ARCHITECTURE.md)
- [Setup and troubleshooting](docs/SETUP.md)
- [ADR 0001 — JavaFX + JDBC + logic in SQL Server](docs/adr/0001-javafx-jdbc-stored-procedures.md)
