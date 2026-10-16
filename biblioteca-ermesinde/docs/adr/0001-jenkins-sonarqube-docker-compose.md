# ADR 0001 — Jenkins + SonarQube + Docker Compose as the delivery toolchain

> Documentation added in 2026 (portfolio). Reconstructed from the repository files; the original team's reasoning is not recorded, so motivations below are marked as inferred. Portuguese version: [0001-jenkins-sonarqube-docker-compose.pt.md](0001-jenkins-sonarqube-docker-compose.pt.md).

- **Status:** Accepted (as implemented in the repository)
- **Evidence:** `jenkinsfile`, `dockerfile`, `docker-compose.yml`, `vagrant/Vagrantfile`, `Relatorio_de_DOS.docx`
- **Confirm with the author:** TODO: confirmar com o BMS (motivations, why Bitbucket as source)

## Context

The course (CTESP, Agile Software Development) asks for a REST API delivered with an automated toolchain: build automation, static analysis and reproducible environments. The API depends on SQL Server, which differs between developer machines.

## Decision

Run the whole toolchain as containers described in one `docker-compose.yml`:

| Concern | Choice | File |
|---|---|---|
| API runtime | Multi-stage `dockerfile` (`sdk:7.0` build, `aspnet:7.0` runtime) | `dockerfile` |
| Database | SQL Server 2022 container (or a Vagrant VM) | `docker-compose.yml`, `vagrant/Vagrantfile` |
| CI | Jenkins LTS, pipeline as code (`Checkout` → `Build`) | `jenkinsfile` |
| Static analysis | SonarQube with `dotnet-sonarscanner` | `dockerfile`, `docker-compose.yml` |
| Secrets | Environment variables and a Jenkins credential | `docker-compose.yml`, `jenkinsfile` |

## Consequences

Positive
- One command (`docker compose up --build`) brings up API, database, CI and analysis.
- Pipeline is versioned with the code.
- Secrets are meant to come from the environment instead of the repository.

Negative / trade-offs (verified in the files)
- The Jenkins pipeline has no test or Sonar stage; Sonar runs in a separate Docker stage that the final image does not depend on.
- The Sonar scanner points to `localhost:9000`, which is not the Sonar container from inside a build.
- Hard-coded database address and a literal SonarQube admin password remain in `docker-compose.yml` / `BackEndContext.cs`.
- `latest` tags (`sonarqube:latest`) make builds non-reproducible.

## Alternatives (inferred, not documented by the team)

- GitHub Actions or Azure Pipelines instead of a self-hosted Jenkins: less infrastructure to run.
- SonarCloud instead of a self-hosted SonarQube.
