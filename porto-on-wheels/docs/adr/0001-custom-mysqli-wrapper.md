# ADR 0001 — Framework-less PHP with a custom mysqli wrapper

> Documentation added in 2026 (portfolio). Reconstructed from the repository files; the original team's reasoning is not recorded, so motivations below are marked as inferred. Portuguese version: [0001-custom-mysqli-wrapper.pt.md](0001-custom-mysqli-wrapper.pt.md).

- **Status:** Accepted (as implemented in the repository)
- **Evidence:** `src/database/db.php`, `src/database/conn.php`, `admin/db/*.php`, `src/database/auth.php`
- **Confirm with the author:** TODO: confirmar com o BMS (why no framework; where the `db` class comes from, since it resembles a widely copied PHP/MySQLi snippet)

## Context

The CTESP web project asks for a rental site with a public catalogue, user accounts and an admin area. The team had to deliver a working PHP and MySQL application without a prescribed framework.

## Decision

| Concern | Choice | Alternative not taken (inferred) |
|---|---|---|
| Application structure | One PHP file per page at the repository root, shared header/footer includes | A front controller and router, or a framework such as Laravel |
| Database access | A single `db` class around `mysqli`: `query($sql, ...$params)` prepares the statement, infers parameter types (`s`, `i`, `d`, `b`) and binds them; `fetchAll()` / `fetchArray()` return associative arrays | PDO, an ORM |
| Configuration | `conn.php` builds the connection from environment variables `PORTOONWHEELS_DB_*` (added when the repository was cleaned for the portfolio) | Hardcoded credentials (the original) |
| Admin area | Copy of the SB Admin 2 template with one page per table and a generic `db/insert|update|delete.php` switch on a `table` field | Separate controllers per resource |
| Cart | Browser `localStorage`, sent to the server as JSON at checkout | Server-side session cart |
| Images | Stored as BLOBs in the database | Files on disk with a path column |

## Consequences

- **Good:** very little code and no tooling to learn; every query uses a prepared statement, so the SQL-injection class of bugs is avoided in the data layer; the admin CRUD is uniform.
- **Bad:**
  - The wrapper holds one open statement in the object, so queries cannot be interleaved while iterating a result; `error()` exits with the raw MySQL message.
  - Without a front controller, access control must be repeated in every page, and it was forgotten in `admin/db/*.php` (see [Known issues](../ARCHITECTURE.md#known-issues-verified-in-the-code-not-fixed) #3).
  - Plain-text passwords and trust in client-supplied ids (`userID`, `new_id`) show that security was not part of the design.
  - Business rules (plans, availability, `quantity`) live in the page code and in hardcoded `<option>` lists; there is no stock check when renting.
  - The schema is not versioned, so the database cannot be recreated from the repository.
- **If it were redone:** keep the wrapper idea but move to PDO with exceptions, add one authentication/authorization include that exits on failure, hash passwords with `password_hash`, take identities from the session, and add a `schema.sql`. These are suggestions, not changes made to the project.
