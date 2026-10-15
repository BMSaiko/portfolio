# LP3 Group 2 — Sheets and Picks

🇬🇧 English · [🇵🇹 Português](README.pt.md)

Team project for the *Laboratório de Programação 3* course (ISEP, 2023/24): a JavaFX desktop application for stock and order management, developed with Scrum over six sprints.

## What it does
- Login and role-based dashboards: **admins, operators, clients and suppliers**.
- User management (create, update, list users per role).
- **Stock** viewing and confirmation, supplier movements.
- **XML import** of orders/stock, validated against an XSD schema (`XSDverificator.xsd`); sample files in `XMLs/`.
- SQL Server persistence over JDBC.

## Repository layout
| Path | Content |
|---|---|
| `gp2-lp3/SheetsAndPicks` | Gradle project (JavaFX, modules, jlink) |
| `gp2-lp3/SheetsAndPicks-1.0-SNAPSHOT` | Packaged build |
| `Docs/Sprints` | Sprint plannings, groomings and retrospectives (PDF, Portuguese) |
| `Docs/UAT Test Plan.pdf` | User acceptance test plan (Portuguese) |
| `XMLs` | Sample XML input files |

## Stack
Java · JavaFX 15 · Gradle · ControlsFX · SQL Server (mssql-jdbc) · JUnit

## Running
```bash
cd gp2-lp3/SheetsAndPicks
./gradlew run
```
Requires JDK 17+ and access to the SQL Server database. Set the full JDBC URL (including credentials) in the `SHEETS_DB_URL` environment variable, e.g. `jdbc:sqlserver://host;databaseName=db;user=u;password=p`. See `Controllers/database/DBConnect.java`.

> The project documentation (PDFs) is in Portuguese, as delivered to the course.
