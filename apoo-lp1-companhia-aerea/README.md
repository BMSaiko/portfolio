# Airline Management — OOP refactor (Group 3)

🇬🇧 English · [🇵🇹 Português](README.pt.md)

> Java console app for airline flights, rebuilt with a cleaner object-oriented design.

| | |
|---|---|
| **Context** | CTESP — Object-Oriented Programming (APOO) / LP1 |
| **Year** | 2022/23 |
| **Team** | Group 3 |
| **Stack** | Java · Maven |

## What this project shows
- **Inheritance** (`Pessoa` → `Passageiro`, `Tripulante`) and encapsulation.
- Separation of domain and utilities (`Console`, `Data`, `File`).
- File persistence (save/load) in plain Java.

## Features
- Domain: `Voo`, `Aviao`, `Aeroporto`, `Passageiro`, `Tripulante`, `Pessoa` (superclass).
- `utils`: input helpers, date handling and file persistence.

## Structure
| Path | Content |
|---|---|
| `grupo3-apoo-lp1-projeto-companhiaarea/` | Maven project |

## Running
```bash
cd grupo3-apoo-lp1-projeto-companhiaarea
mvn compile exec:java -Dexec.mainClass=org.companhiaaerea.Main
```

## Notes
- Builds on the [early version](../companhia-aerea).

## Documentation

- [Architecture](docs/ARCHITECTURE.md) — class diagram, menu, flows, persistence format, verified issues
- [Setup](docs/SETUP.md) — prerequisites, build and run, troubleshooting
- [ADR 0001](docs/adr/0001-v1-to-v2-pessoa-hierarchy-and-utils.md) — from the early version to this refactor
- [Documentation audit](../docs/AUDIT.md#apoo-lp1-companhia-aerea) — status and gaps for this project
