# Companhia Aérea — early version

🇬🇧 English · [🇵🇹 Português](README.pt.md)

> Console app to manage airline flights — the first iteration, later refactored in `airline-management-oop`.

> Formerly `companhia-aerea`.

| | |
|---|---|
| **Context** | CTESP — Programming Lab 1 (LP1) |
| **Year** | 2022/23 |
| **Team** | Team project |
| **Stack** | Java · Maven |

## What this project shows
- Modelling a real domain with classes (flight, aircraft, airport, passenger, crew).
- Console menus and input handling in Java.
- Honest iteration: this is the early version; the OOP refactor lives in the next project.

## Features
- Domain: `Voo`, `Aviao` (registration, model, tourist/executive seats), `Aeroporto` (code, country, nearest city), `Passageiro` (email, ticket type, seat), crew.
- Planned menu: add/remove flight, search by route and date, list/edit passengers, flights in a date range, flight with most passengers, totals and averages per route, save/load.

## Structure
| Path | Content |
|---|---|
| `CompanhiaAerea/` | Maven project |

## Running
```bash
cd CompanhiaAerea
mvn compile exec:java -Dexec.mainClass=org.example.Main
```

## Notes
- Work in progress: only “add flight” is implemented in the menu. See the finished version in [`airline-management-oop`](../airline-management-oop).

## Documentation

- [Architecture](docs/ARCHITECTURE.md) — class diagram, intended flow, verified issues (does not compile as committed)
- [Setup](docs/SETUP.md) — status, prerequisites, commands
- [ADR 0001](docs/adr/0001-keep-early-version-as-unrepaired-snapshot.md) — why the early version is kept as is
- [Documentation audit](../docs/AUDIT.md#airline-management-v1) — status and gaps for this project
