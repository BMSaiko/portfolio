# Biblioteca Ermesinde

[🇬🇧 English](README.md) · 🇵🇹 Português

> API REST de biblioteca com pipeline DevOps completo: contentores, CI, análise estática e VM de base de dados provisionada.

| | |
|---|---|
| **Contexto** | CTESP — Projeto final (Desenvolvimento Ágil de Software) |
| **Ano** | 2023/24 |
| **Equipa** | Projeto de equipa |
| **Stack** | C# · .NET 7 · ASP.NET Core · EF Core · SQL Server · xUnit · Moq · Docker · Jenkins · SonarQube · Vagrant |

## O que este projeto demonstra
- **API REST** em ASP.NET Core com migrações do Entity Framework Core.
- **Testes automáticos** (xUnit + Moq) e análise estática com **SonarQube**.
- **CI/CD e infraestrutura**: Dockerfile, Docker Compose (API + SQL Server + SonarQube), pipeline Jenkins, VM Vagrant.
- Segredos tratados via variáveis de ambiente nesta versão pública.

## Funcionalidades
- Clientes, livros e reservas (controllers `Cliente`, `Livro`, `Reserva`).
- Criar reserva, consultar reserva, listar os livros de um cliente.
- Migrações EF Core e modelos (`Cliente`, `Livro`, `Reserva`, `ReservaLivro`).

## Estrutura
| Caminho | Conteúdo |
|---|---|
| `Biblioteca_Ermesinde/` | API ASP.NET Core |
| `Teste_Biblioteca/` | Testes xUnit + Moq |
| `dockerfile, docker-compose.yml, jenkinsfile, vagrant/` | Cadeia DevOps |
| `Relatorio_de_DOS.docx` | Relatório do projeto |

## Execução
1. Definir `SA_PASSWORD` (e `SONAR_TOKEN` para o passo Sonar do build).
2. `docker compose up --build`, ou arrancar a VM SQL Server com `vagrant up` e correr a API no Visual Studio.
3. Placeholder `<SA_PASSWORD>` na connection string de `appsettings*.json`.

## Notas
- Tokens e passwords do repositório original foram substituídos por variáveis/placeholders; o job Jenkins espera uma credencial `bitbucket-token`.
