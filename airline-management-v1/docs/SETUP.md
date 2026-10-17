# Setup — Companhia Aérea (early version)

> Documentation added in 2026 (portfolio). Commands were **not tested** (no JDK or Maven on the machine where this was written). Portuguese version: [SETUP.pt.md](SETUP.pt.md).

## Status

**This project is not expected to build as committed.** `Passageiro.java`, `Voo.java` and `Main.java` reference `Pessoa` and `PilotoTripulante`, which are not in the repository (see [ARCHITECTURE](ARCHITECTURE.md#4-verified-issues-read-from-the-code), issue 1). `mvn compile` should therefore fail with unresolved-symbol errors for those two classes (prediction from reading the code, not an observed run). To see a working program, use [`airline-management-oop`](../../airline-management-oop/docs/SETUP.md).

## Prerequisites

| Tool | Version |
|---|---|
| JDK | 17+ (`pom.xml` sets source/target 17) |
| Maven | 3.x |

No database, environment variables or secrets.

## Commands (from the README)

```bash
cd airline-management-v1/CompanhiaAerea
mvn compile exec:java -Dexec.mainClass=org.example.Main
```

To verify the status described above, run only `mvn compile` and check for the missing-class errors.

## If the missing classes were restored

Even then the program would only run one action: the menu has no loop, only option 1 is implemented, `Main.voos` is `null` (NPE on add), dates cannot be read and the crew list stays empty (issues 2–4 in [ARCHITECTURE](ARCHITECTURE.md)).

## Tests

None.
