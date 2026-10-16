# Sheets and Picks (LP3)

🇬🇧 English · [🇵🇹 Português](README.pt.md)

> JavaFX desktop application for stock and order management, built with Scrum.

| | |
|---|---|
| **Context** | CTESP — Programming Lab 3 (LP3) |
| **Year** | 2023/24 |
| **Team** | Group 2 — Scrum, six sprints |
| **Stack** | Java · JavaFX 15 · Gradle · ControlsFX · SQL Server (mssql-jdbc) · JUnit · XML/XSD |

## What this project shows
- **Agile delivery**: sprint plannings, groomings and retrospectives plus a UAT test plan (`Docs/`).
- **XML import validated by XSD** schema.
- Role-based desktop UI with JavaFX and JDBC persistence.

## Features
- Login and dashboards per role: admins, operators, clients, suppliers.
- User management (create, update, list per role).
- Stock viewing and confirmation, supplier movements.
- XML import of orders/stock validated against `XSDverificator.xsd`; samples in `XMLs/`.

## Structure
| Path | Content |
|---|---|
| `gp2-lp3/SheetsAndPicks` | Gradle project (JavaFX, modules, jlink) |
| `Docs/Sprints` | Sprint documents (PDF, Portuguese) |
| `Docs/UAT Test Plan.pdf` | User acceptance test plan |
| `XMLs/` | Sample XML inputs |

## Running
```bash
cd gp2-lp3/SheetsAndPicks
./gradlew run
```
Requires JDK 17+ and a SQL Server database; set the JDBC URL in `SHEETS_DB_URL` (see `Controllers/database/DBConnect.java`).

## Notes
- Project documentation (PDFs) is in Portuguese, as delivered.
