# Setup — Biblioteca Ermesinde

> Documentação adicionada em 2026 (portfolio). English version: [SETUP.md](SETUP.md).

Estado de todos os comandos abaixo: **não testado** (o SDK .NET e o Docker não estavam disponíveis quando isto foi escrito). Derivam de `dockerfile`, `docker-compose.yml`, `appsettings.json` e `*.csproj`.

## Pré-requisitos

| Ferramenta | Versão | Para quê |
|---|---|---|
| .NET SDK | 7.0 (`net7.0`) | Compilar e executar a API e os testes |
| SQL Server | 2022 (imagem `mssql/server:2022-latest`) ou VM Vagrant | Base de dados |
| Docker + Docker Compose | recente | Execução em contentores |
| Vagrant + hypervisor | opcional | VM da base de dados (`vagrant/Vagrantfile`) |

## Variáveis de ambiente

| Variável | Usada por | Finalidade |
|---|---|---|
| `SA_PASSWORD` | `BackEndContext.OnConfiguring`, `docker-compose.yml` (`database`, `sonar_biblioteca`) | Password `sa` do SQL Server |
| `SONAR_TOKEN` | `docker-compose.yml`, `dockerfile` (build arg) | Token de análise SonarQube |
| `ASPNETCORE_ENVIRONMENT` | `docker-compose.yml` (`production`) | O Swagger UI só corre em `Development` |

A connection string em `appsettings.json` tem o marcador `<SA_PASSWORD>`. O `BackEndContext.OnConfiguring` constrói a sua própria connection string a partir de `SA_PASSWORD` e sobrepõe-se a ela (ver [ARCHITECTURE](ARCHITECTURE.pt.md), problema 2). Nunca fazer commit de valores reais.

O Jenkins espera ainda uma credencial com id `bitbucket-token`.

## Executar com Docker Compose

```bash
export SA_PASSWORD='<uma password forte>'
export SONAR_TOKEN='<token>'        # só para o passo SonarQube
docker compose up --build
```

| Serviço | URL / porta |
|---|---|
| API | http://localhost:8080 (host 8080 → contentor 80) |
| SQL Server | localhost:1433 |
| Jenkins | http://localhost:8081 |
| SonarQube | http://localhost:9000 |

## Executar a API localmente

```bash
cd Biblioteca_Ermesinde
dotnet restore
dotnet ef database update      # aplica Migrations/ (precisa da ferramenta dotnet-ef)
dotnet run
```

Usar `ASPNETCORE_ENVIRONMENT=Development` para ter o Swagger UI em `/swagger`. O endereço do servidor de base de dados está fixo em `BackEndContext.cs`; alterá-lo para o seu servidor antes de executar.

## Executar os testes

```bash
cd Teste_Biblioteca
dotnet test
```

Resultado esperado pela leitura do código: falhas. Os testes criam controladores sem `DbContext` e referem `ReservaResponse`; ver [ARCHITECTURE](ARCHITECTURE.pt.md), problemas 4 e 5.

## Resolução de problemas

| Sintoma | Causa provável |
|---|---|
| `Login failed for user 'sa'` | `SA_PASSWORD` por definir ou diferente da usada ao criar o contentor SQL Server |
| A API não chega à base de dados | O endereço fixo em `BackEndContext.cs` / compose (`10.1.23.254`) não corresponde à sua rede |
| `404` em `/swagger` | O ambiente não é `Development` |
| O contentor SQL Server termina logo | A password não cumpre as regras de complexidade do SQL Server |
