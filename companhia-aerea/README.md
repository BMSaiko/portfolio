# Companhia Aérea (airline) — early version

🇬🇧 English · [🇵🇹 Português](README.pt.md)

Console application in Java for managing the flights of an airline (*LP1* course, ISEP). This is the early version; the refactored group version is in [`../apoo-lp1-companhia-aerea`](../apoo-lp1-companhia-aerea).

## Domain model
`Voo` (flight) · `Aviao` (aircraft: registration, model, tourist/executive seats) · `Aeroporto` (airport: code, country, nearest city) · `Passageiro` (passenger: email, ticket type, seat) · crew members.

## Planned menu
Add/remove flight, search by route and date, list/edit passengers, search flights in a date range, flight with most passengers, total and average passengers per route, save/load file.

> Status: work in progress — only "add flight" is implemented in the menu.

## Stack
Java · Maven

## Running
```bash
cd CompanhiaAerea
mvn compile exec:java -Dexec.mainClass=org.example.Main
```
