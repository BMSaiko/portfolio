# ADR 0001 — Laravel MVC with Eloquent and roles resolved by table membership

> Documentation added in 2026 (portfolio). Reconstructed from the repository files; the original team's reasoning is not recorded, so motivations below are marked as inferred. Portuguese version: [0001-laravel-mvc-roles-by-table.pt.md](0001-laravel-mvc-roles-by-table.pt.md).

- **Status:** Accepted (as implemented in the repository)
- **Evidence:** `routes/web.php`, `app/Http/Controllers/Login.php`, `app/Models/*`, `database/migrations/*`, `DB/2023_ERM_DSOS_G1.sql`
- **Confirm with the author:** TODO: confirmar com o BMS (why Laravel; why no role column; role of the Inertia/Vue packages)

## Context

The DSOS brief asks for a web platform where companies publish internship or project proposals, teachers supervise them and students apply, with approval states and project delivery. The team needed three user types that share login and personal data but differ in what they can do.

## Decision

| Concern | Choice | Alternative not taken (inferred) |
|---|---|---|
| Framework | Laravel 10, MVC, Blade views rendered on the server | Separate SPA plus API (the Inertia/Vue packages are installed but the views are Blade) |
| Persistence | Eloquent models over MySQL, with a SQL dump as the shipped database | Migrations plus seeders as the single source |
| Identity | `acesso` (username + bcrypt) separated from `dados_pessoais` (profile) | A single `users` table (a default Laravel `users` table and `User` model also remain) |
| Roles | One table per role (`aluno`, `empresa`, `docentes`, `admin`), each pointing to `acesso` and `dados_pessoais`; the role is found by testing which table contains the user | A `role` column or a permissions package |
| Workflow state | Lookup tables `estado_proposta` and `estado_propostas_aluno` referenced by id | Enum columns |
| Bulk onboarding | CSV import of students | Self-registration |

## Consequences

- **Good:** each role keeps its own attributes (`n_mecanografico`/`Turma` for students, `diretor`/`aprovada` for companies, `regente` for teachers) without nullable columns; states are data, not code; the whole app is plain PHP with no build step.
- **Bad:**
  - Every request that needs the role runs up to four queries to discover it, and the order of the checks (student, company, admin, teacher) decides the result if a user is in two tables.
  - Nothing enforces roles at route level (only `auth`), so authorization depends on each controller and several actions have none.
  - The dump and the migrations drifted apart, so there are two competing schema sources.
  - Magic numbers for states (`id_estado = 1`) are repeated in controllers.
- **If it were redone:** a single role resolution in a middleware (`role:admin`) applied to the route groups, `Gate`/policies for approvals, migrations kept in sync with seeders, and enums or constants for states. These are suggestions, not changes made to the project.
