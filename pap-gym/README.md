# PAP Gym (Spump)

🇬🇧 English · [🇵🇹 Português](README.pt.md)

> Gym management: a WPF desktop app plus a promotional website.

| | |
|---|---|
| **Context** | Vocational course (PAP) — final project |
| **Year** | 2021/22 |
| **Team** | Final project (PAP), 12th grade |
| **Stack** | C# · .NET Framework 4.7.2 · WPF/XAML (MVVM) · MySQL · HTML · CSS |

## What this project shows
- Desktop application architecture with **MVVM** on WPF.
- Relational modelling in MySQL (clients, teachers, rooms, modalities, exercises, classes) with diagrams in `img/`.
- End-to-end delivery: app, database, website and documented screens.

## Features
- Manage clients, teachers, rooms, modalities, exercises and group classes.
- Edit training plans; see the classes of the day and class schedules.
- Static HTML/CSS landing page with a registration form (`site/`).
- Screenshots of every screen and database diagrams in `img/`.

## Structure
| Path | Content |
|---|---|
| `spump/` | WPF solution (Visual Studio) |
| `site/` | Promotional website |
| `img/` | Screenshots, DB diagrams, attachments |

## Running
1. Create a MySQL database `gym` on `localhost` (schema in the attachments).
2. Open `spump/spump.sln` in Visual Studio, restore NuGet packages and run.
3. The password is read from the `GYM_DB_PASSWORD` environment variable (`spump/spump/BD.cs`).

## Notes
- UI texts are in Portuguese.
