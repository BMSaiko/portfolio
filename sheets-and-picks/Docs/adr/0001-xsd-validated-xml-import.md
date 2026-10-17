# ADR 0001 — Validate supplier XML against an XSD before importing it

> Documentation added in 2026 (portfolio). Reconstructed from the repository files; the original team's reasoning is not recorded, so motivations below are marked as inferred. Portuguese version: [0001-xsd-validated-xml-import.pt.md](0001-xsd-validated-xml-import.pt.md).

- **Status:** Accepted (as implemented in the repository)
- **Evidence:** `gp2-lp3/XSDverificator.xsd` (copy in `SheetsAndPicks/src/main/`), `ConfirmaXMLController.java`, `BL/Stock/StockFunctions.java`, `XMLs/XML-Sample-4.*.xml`
- **Confirm with the author:** TODO: confirmar com o BMS (who defined the XML format: the course brief or the team)

## Context

The LP3 brief has suppliers send order confirmations as XML files (`OrderConfirmation`, with header, supplier address and line items with part numbers, price, quantity and units of measure). The application must load them into stock without letting malformed files reach the database.

## Decision

| Concern | Choice | Alternative not taken (inferred) |
|---|---|---|
| Contract | An XSD (`XSDverificator.xsd`) describing `OrderConfirmation` | Ad-hoc checks while parsing |
| Validation | `javax.xml.validation` (`SchemaFactory` + `Validator`) before any parsing | Catch parse errors in the DOM loop |
| Parsing | DOM (`DocumentBuilder`), reading each `OrderConfirmationLineItem` | SAX/StAX, JAXB binding |
| Persistence | One call per line item to the stored procedure `Criar_Encomenda` via `DBstatements.enviarDados` | Batch insert in a transaction |
| Feedback | JavaFX success/error dialogs with the validator message | Log file |

## Consequences

- **Good:** invalid files are rejected with a precise message (line and element) before touching the database; the XSD doubles as documentation of the format; five sample files exist in `XMLs/`.
- **Bad:**
  - The XSD path is hardcoded in two places and only one is relative (see [Known issues](../ARCHITECTURE.md#known-issues-verified-in-the-code-not-fixed) #1), so validation depends on the machine.
  - Each line item is a separate call with no surrounding transaction: a failure in item N leaves items 1..N-1 inserted.
  - The parser uses `getElementsByTagName(...).item(0)`, so it assumes a fixed shape (two `ProductIdentifier`, three `Value` elements per line) that the XSD must keep enforcing.
  - VAT (23%) is hardcoded in the Java code rather than coming from the `Pais.taxa` column.
- **If it were redone:** load the XSD from the classpath, wrap one file in one transaction, and take the tax rate from the database. These are suggestions, not changes made to the project.
