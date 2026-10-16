# Biblioteca Ermesinde

🇬🇧 English · [🇵🇹 Português](README.pt.md)

> Library REST API with a complete DevOps pipeline: containers, CI, static analysis and a provisioned database VM.

| | |
|---|---|
| **Context** | CTESP — Final project (Agile Software Development) |
| **Year** | 2023/24 |
| **Team** | Team project |
| **Stack** | C# · .NET 7 · ASP.NET Core · EF Core · SQL Server · xUnit · Moq · Docker · Jenkins · SonarQube · Vagrant |

## What this project shows
- **REST API** in ASP.NET Core with Entity Framework Core migrations.
- **Automated tests** (xUnit + Moq) and **SonarQube** static analysis.
- **CI/CD and infrastructure**: Dockerfile, Docker Compose (API + SQL Server + SonarQube), Jenkins pipeline, Vagrant VM.
- Secrets handled through environment variables in this public version.

## Features
- Clients, books and reservations (`Cliente`, `Livro`, `Reserva` controllers).
- Create a reservation, get a reservation, list the books of a client.
- EF Core migrations and models (`Cliente`, `Livro`, `Reserva`, `ReservaLivro`).

## Structure
| Path | Content |
|---|---|
| `Biblioteca_Ermesinde/` | ASP.NET Core API |
| `Teste_Biblioteca/` | xUnit + Moq tests |
| `dockerfile, docker-compose.yml, jenkinsfile, vagrant/` | DevOps toolchain |
| `Relatorio_de_DOS.docx` | Project report (Portuguese) |

## Running
1. Set `SA_PASSWORD` (and `SONAR_TOKEN` for the Sonar build step).
2. `docker compose up --build`, or start the SQL Server VM with `vagrant up` and run the API from Visual Studio.
3. Connection string placeholder `<SA_PASSWORD>` in `appsettings*.json`.

## Notes
- Tokens and passwords of the original repository were replaced by variables/placeholders; the Jenkins job expects a `bitbucket-token` credential.

## Documentation

- [Documentation audit](../docs/AUDIT.md#biblioteca-ermesinde) — status and gaps for this project
- [DOS report (Portuguese)](Relatorio_de_DOS.docx)
- [Docker Compose setup](docker-compose.yml)
- [Jenkins pipeline](jenkinsfile)
