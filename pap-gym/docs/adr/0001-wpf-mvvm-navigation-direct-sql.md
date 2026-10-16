# ADR 0001 — WPF with MVVM navigation and direct SQL in the views

> Documentation added in 2026 (portfolio). Reconstructed from the repository files; the original author's reasoning is not recorded, so motivations below are marked as inferred. Portuguese version: [0001-wpf-mvvm-navigation-direct-sql.pt.md](0001-wpf-mvvm-navigation-direct-sql.pt.md).

- **Status:** Accepted (as implemented in the repository)
- **Evidence:** `spump/spump/MVM/ViewModel/MainViewModel.cs`, `App.xaml`, `Core/RelayCommand.cs`, `BD.cs`, `MVM/View/*.xaml.cs`, `img/screenshoots/`
- **Confirm with the author:** TODO: confirmar com o BMS (whether MVVM was a course requirement; how much of the design was taught vs chosen)

## Context

The PAP final project asks for a complete system: a desktop application for a gym with its database, plus a website, delivered in the 12th grade. The application manages clients, teachers, rooms, modalities, exercises, training plans and group classes with enrolment.

## Decision

| Concern | Choice | Alternative not taken (inferred) |
|---|---|---|
| UI technology | WPF/XAML on .NET Framework | WinForms, a web app |
| Navigation | MVVM: `MainViewModel` exposes `CurrentView` and `RelayCommand`s; `App.xaml` maps each view model type to a `UserControl` with a `DataTemplate` | A `Frame` with pages, or code-behind window switching |
| View logic | Code-behind in each view runs SQL and fills `DataGrid`s | View models that own the data and commands |
| Persistence | MySQL via `MySql.Data`; one `BD` class that builds the connection string and opens/closes the connection | An ORM (Entity Framework), a data-access layer |
| Screens | One list view plus one `BD*` form per entity, opened as a separate window | Inline editing in the grid |
| Documentation | Screenshots of every screen and database diagrams in `img/BD` | A written report in the repository |

## Consequences

- **Good:** the shell is clean and extensible (adding a screen is one view model, one view and one `DataTemplate`); the database model is normalised with link tables (`clientesporaula`, `musculosporexercicios`); every screen and table is documented with images.
- **Bad:**
  - MVVM is applied only to navigation. The eight screen view models are 12-line shells and the data logic sits in the views' code-behind, so nothing can be unit-tested and the views are tied to MySQL.
  - SQL built from user input (injection), no capacity guard in the database and a connection opened per screen.
  - Business concepts are encoded in the schema loosely: a training plan has no header table (rows share a name), and plan `quantidade` is a constant.
  - No script creates the database, so the system cannot be reproduced from the repository alone.
- **If it were redone:** keep the navigation shell; give each view model its data (`ObservableCollection`) and commands; put the queries behind a repository using parameterised `MySqlCommand`s; add a `schema.sql`; and move the room-capacity check into a transaction or constraint. These are suggestions, not changes made to the project.
