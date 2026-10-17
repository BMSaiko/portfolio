# Sheets and Picks (LP3)

🇬🇧 English · [🇵🇹 Português](README.pt.md)

> JavaFX desktop application for stock and order management, built with Scrum.

> Formerly `lp3-g2`.

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

## Documentation

- [Documentation audit](../docs/AUDIT.md#sheets-and-picks) — status and gaps for this project
- [Architecture, data model and known issues](Docs/ARCHITECTURE.md)
- [Setup and troubleshooting](Docs/SETUP.md)
- [ADR 0001 — XSD-validated XML import](Docs/adr/0001-xsd-validated-xml-import.md)
- [Project documentation folder](Docs/)
- [Database model](Docs/Modelo%20de%20base%20de%20dados.png)
- [Domain model diagram](Docs/diagramaModeloDominio.png)
- [UAT test plan](Docs/UAT%20Test%20Plan.pdf)
- [Sprint plannings and retrospectives](Docs/Sprints/)
