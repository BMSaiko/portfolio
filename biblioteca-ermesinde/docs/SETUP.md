# Setup — Biblioteca Ermesinde

> Documentation added in 2026 (portfolio). Portuguese version: [SETUP.pt.md](SETUP.pt.md).

Status of every command below: **not tested** (the .NET SDK and Docker were not available when this was written). They are derived from `dockerfile`, `docker-compose.yml`, `appsettings.json` and `*.csproj`.

## Prerequisites

| Tool | Version | Needed for |
|---|---|---|
| .NET SDK | 7.0 (`net7.0`) | Build and run the API and tests |
| SQL Server | 2022 (container image `mssql/server:2022-latest`) or a Vagrant VM | Database |
| Docker + Docker Compose | recent | Containerised run |
| Vagrant + a hypervisor | optional | Database VM (`vagrant/Vagrantfile`) |

## Environment variables

| Variable | Used by | Purpose |
|---|---|---|
| `SA_PASSWORD` | `BackEndContext.OnConfiguring`, `docker-compose.yml` (`database`, `sonar_biblioteca`) | SQL Server `sa` password |
| `SONAR_TOKEN` | `docker-compose.yml`, `dockerfile` (build arg) | SonarQube analysis token |
| `ASPNETCORE_ENVIRONMENT` | `docker-compose.yml` (`production`) | Swagger UI only runs in `Development` |

The connection string in `appsettings.json` contains the placeholder `<SA_PASSWORD>`. `BackEndContext.OnConfiguring` builds its own connection string from `SA_PASSWORD` and overrides it (see [ARCHITECTURE](ARCHITECTURE.md), issue 2). Never commit real values.

Jenkins also expects a credential with id `bitbucket-token`.

## Run with Docker Compose

```bash
export SA_PASSWORD='<a strong password>'
export SONAR_TOKEN='<token>'        # only for the SonarQube step
docker compose up --build
```

| Service | URL / port |
|---|---|
| API | http://localhost:8080 (host 8080 → container 80) |
| SQL Server | localhost:1433 |
| Jenkins | http://localhost:8081 |
| SonarQube | http://localhost:9000 |

## Run the API locally

```bash
cd Biblioteca_Ermesinde
dotnet restore
dotnet ef database update      # applies Migrations/ (needs the dotnet-ef tool)
dotnet run
```

Use `ASPNETCORE_ENVIRONMENT=Development` to get Swagger UI at `/swagger`. The database server address is hard-coded in `BackEndContext.cs`; change it to your own server before running.

## Run the tests

```bash
cd Teste_Biblioteca
dotnet test
```

Expected result by reading the code: failures. The tests build controllers without a `DbContext` and reference `ReservaResponse`; see [ARCHITECTURE](ARCHITECTURE.md), issues 4 and 5.

## Troubleshooting

| Symptom | Likely cause |
|---|---|
| `Login failed for user 'sa'` | `SA_PASSWORD` unset or different from the one the SQL Server container was created with |
| API cannot reach the database | Hard-coded server address in `BackEndContext.cs` / compose (`10.1.23.254`) does not match your network |
| `404` on `/swagger` | Environment is not `Development` |
| SQL Server container exits immediately | Password does not satisfy SQL Server complexity rules |
