# Biblioteca Ermesinde

[🇬🇧 English](README.md) · 🇵🇹 Português

Projeto final do CTESP em Desenvolvimento Ágil de Software (ISEP, 2023/24): API REST de gestão de uma biblioteca, com uma cadeia DevOps completa à volta.

## O que faz
- **API (`Biblioteca_Ermesinde/`)** — ASP.NET Core 7 com Entity Framework Core e SQL Server. Controllers de clientes, livros e reservas.
- **Testes (`Teste_Biblioteca/`)** — testes unitários xUnit + Moq.
- **DevOps** — `dockerfile` + `docker-compose.yml` (API, SQL Server, SonarQube), pipeline CI `jenkinsfile`, análise SonarQube e VM `vagrant/` com SQL Server.
- **`Relatorio_de_DOS.docx`** — relatório do projeto.

## Stack
C# · .NET 7 · ASP.NET Core · EF Core · SQL Server · xUnit · Moq · Docker · Jenkins · SonarQube · Vagrant

## Execução
1. Definir a variável de ambiente `SA_PASSWORD` (e `SONAR_TOKEN` para o passo Sonar do build).
2. `docker compose up --build`, ou arrancar a VM SQL Server com `vagrant up` e correr a API no Visual Studio.
3. Ajustar a connection string em `appsettings*.json` (placeholder `<SA_PASSWORD>`).

## Notas
Credenciais e tokens presentes no repositório original foram substituídos por variáveis de ambiente / placeholders.
