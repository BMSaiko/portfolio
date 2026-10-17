# ADR 0001 — Keep the early version as an unrepaired historical snapshot

> Documentation added in 2026 (portfolio). Unlike the other ADRs, this records a **portfolio curation decision**, not a decision of the original team. Portuguese version: [0001-keep-early-version-as-unrepaired-snapshot.pt.md](0001-keep-early-version-as-unrepaired-snapshot.pt.md).

- **Status:** Proposed — TODO: confirmar com o BMS
- **Evidence:** `CompanhiaAerea/src/main/java/org/example/*.java`; [ARCHITECTURE](../ARCHITECTURE.md#4-verified-issues-read-from-the-code); the README already says "Work in progress: only 'add flight' is implemented"
- **Related:** [`airline-management-oop` ADR 0001](../../../airline-management-oop/docs/adr/0001-v1-to-v2-pessoa-hierarchy-and-utils.md) (what the refactor changed)

## Context

The project is the first iteration of the airline exercise. It does not compile (two referenced classes are missing), only one menu action is drafted and several bugs are present (null list, empty crew, unreadable date-time). A finished version exists in a sibling project.

## Decision

Leave the source untouched and document its state honestly. Do not add the missing classes or fix the bugs.

## Consequences

- **Positive:** the portfolio shows genuine iteration (early draft → refactor) without rewriting a team's history; no invented code.
- **Negative:** a visitor cannot run this project; the README and SETUP must say so clearly (they do).

## Alternatives not taken

- Reconstruct `Pessoa` / `PilotoTripulante` so it compiles — would be invented authorship; reconsider only if the originals are found.
- Remove the project — loses the before/after comparison that the refactor ADR relies on.
