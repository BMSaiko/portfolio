# APOO / LP1 — Airline Management (Group 3)

🇬🇧 English · [🇵🇹 Português](README.pt.md)

Java console application for managing airline flights, built by group 3 for the *APOO* and *LP1* courses (ISEP). It extends the [early version](../companhia-aerea) with a cleaner object-oriented design.

## Design
- Domain: `Voo`, `Aviao`, `Aeroporto`, `Passageiro`, `Tripulante`, with `Pessoa` as common superclass (inheritance).
- `utils` package: `Console` (input helpers), `Data` (date handling), `File` (save/load persistence).

## Stack
Java · Maven

## Running
```bash
cd grupo3-apoo-lp1-projeto-companhiaarea
mvn compile exec:java -Dexec.mainClass=org.companhiaaerea.Main
```
