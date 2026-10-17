# ADR 0001 — From the early version to the refactor: `Pessoa` hierarchy, `ler()` in the domain, `utils` and file persistence

> Documentation added in 2026 (portfolio). Reconstructed by comparing this project with [`companhia-aerea`](../../../companhia-aerea) (the early version); the team's own reasoning is not recorded, so motivations are marked as inferred. Portuguese version: [0001-v1-to-v2-pessoa-hierarchy-and-utils.pt.md](0001-v1-to-v2-pessoa-hierarchy-and-utils.pt.md).

- **Status:** Accepted (as implemented in the repository)
- **Evidence:** `companhia-aerea/CompanhiaAerea/src/main/java/org/example/*.java` (5 files), `grupo3-apoo-lp1-projeto-companhiaarea/src/main/java/org/companhiaaerea/**` (10 files)
- **Confirm with the author:** TODO: confirmar com o BMS (why the rewrite was done, who decided the layout)

## Context

The early version (`org.example`, 5 classes) models flights, aircraft, airports and passengers, and reads all input inside `Main` (`lerNumVoo`, `lerAviao`, `lerAeroporto`, `lerDataHora`, `lerPassageiros`, `lerTripulacao`). Its `Passageiro` extends `Pessoa` and the flight holds `PilotoTripulante` objects, but **neither class is in the repository** (`git ls-files` finds no `Pessoa.java` or `PilotoTripulante.java` there), so that folder does not compile as committed.

## Decision

| Concern | Early version | Refactor | Why (inferred) |
|---|---|---|---|
| Package | `org.example` | `org.companhiaaerea` | Meaningful namespace |
| People | `Passageiro extends Pessoa`, `PilotoTripulante` (both missing from repo) | Concrete `Pessoa` superclass; `Passageiro` and `Tripulante` extend it; crew role is a `Categoria` enum (5 values) | Remove duplication of identity/contact fields; one crew class instead of pilot-only |
| Input | Static helpers in `Main` | `static ler()` on each domain class + `utils.Console`/`utils.Data` with regex-validated prompts | `Main` stops growing; validation is reusable |
| Persistence | Menu lists "save file" / "read file" (options 10–11) but the `case` bodies are empty | `utils.File`: `;`-separated text, one flight per line, in `<home>/companhia_aerea/` | Data survives between runs without a database |
| Features | 11 options printed, only option 1 (add flight) implemented; options 2–11 are empty `case` branches | 10 working menu options in a loop: search by date/route, busiest flight, passenger lists, edit passengers, average and total passengers, save/load | Finish the assignment (the early version is a skeleton of the same menu) |

## Consequences

**Positive**

- A real inheritance hierarchy (`Pessoa` → `Passageiro`, `Tripulante`) and encapsulation, which is the point of the course unit.
- The project compiles on its own (all 10 classes present) and runs from the README command.
- Validation and file code live outside `Main`.

**Negative / debt (verified, see [ARCHITECTURE](../ARCHITECTURE.md#5-verified-issues-read-from-the-code))**

- Domain classes mix model and console I/O (`ler()`), and `Voo`/`File` depend on the static `Main.voos`.
- The `;` format is fragile (free text with `;`, malformed files crash the loader).
- No automated tests.

## Alternatives not taken (inferred)

- Java serialization or JSON/CSV libraries for persistence — would remove the hand-written parser but needs a dependency or `Serializable` plumbing.
- A separate repository/service class instead of a static list in `Main`.
- Keeping `PilotoTripulante` and adding a sibling for cabin crew instead of a `Categoria` enum.
