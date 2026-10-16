# Biblioteca Ermesinde

🇬🇧 English · [🇵🇹 Português](README.pt.md)

Final project of the CTESP in Agile Software Development (ISEP, 2023/24): a library management REST API with a full DevOps toolchain around it.

## What it does
- **API (`Biblioteca_Ermesinde/`)** — ASP.NET Core 7 with Entity Framework Core and SQL Server. Controllers for clients, books and reservations.
- **Tests (`Teste_Biblioteca/`)** — xUnit + Moq unit tests.
- **DevOps** — `dockerfile` + `docker-compose.yml` (API, SQL Server, SonarQube), `jenkinsfile` CI pipeline, SonarQube analysis and a `vagrant/` VM running SQL Server.
- **`Relatorio_de_DOS.docx`** — project report.

## Stack
C# · .NET 7 · ASP.NET Core · EF Core · SQL Server · xUnit · Moq · Docker · Jenkins · SonarQube · Vagrant

## Running
1. Set the `SA_PASSWORD` environment variable (and `SONAR_TOKEN` for the Sonar build step).
2. `docker compose up --build`, or start the SQL Server VM with `vagrant up` and run the API from Visual Studio.
3. Update the connection string in `appsettings*.json` (`<SA_PASSWORD>` placeholder).

## Notes
Credentials and tokens present in the original repository were replaced by environment variables / placeholders.
