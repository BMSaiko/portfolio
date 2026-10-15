# PAP Gym (Spump)

🇬🇧 English · [🇵🇹 Português](README.pt.md)

Final vocational project (PAP, 12th grade, 2021/22): a gym management system made of a desktop application and a promotional website.

## What it does
- **Desktop app (`spump/`)** — C# / WPF, MVVM pattern, MySQL backend. Manage clients, teachers, rooms, modalities, exercises and group classes; edit training plans; see the classes of the day and class schedules.
- **Website (`site/`)** — static HTML/CSS landing page with a registration form.
- **`img/`** — screenshots of every screen, database diagrams and project attachments.

## Stack
C# · .NET Framework 4.7.2 · WPF/XAML · MySQL · HTML · CSS

## Running
1. Create a MySQL database called `gym` on `localhost` (schema in the project attachments).
2. Open `spump/spump.sln` in Visual Studio, restore NuGet packages and run.
3. The connection string is in `spump/spump/BD.cs`; the password is read from the `GYM_DB_PASSWORD` environment variable.

## Screenshots
See [`img/screenshoots`](img/screenshoots) and [`img/App`](img/App).
