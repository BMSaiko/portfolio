# LP3 Grupo 2 — Sheets and Picks

[🇬🇧 English](README.md) · 🇵🇹 Português

Projeto de grupo da UC de Laboratório de Programação 3 (ISEP, 2023/24): aplicação desktop em JavaFX para gestão de stock e encomendas, desenvolvida com Scrum ao longo de seis sprints.

## O que faz
- Login e dashboards por perfil: **administradores, operadores, clientes e fornecedores**.
- Gestão de utilizadores (criar, atualizar, listar por perfil).
- Visualização e confirmação de **stock**, movimentos de fornecedores.
- **Importação de XML** de encomendas/stock, validada por esquema XSD (`XSDverificator.xsd`); ficheiros de exemplo em `XMLs/`.
- Persistência em SQL Server via JDBC.

## Estrutura
| Caminho | Conteúdo |
|---|---|
| `gp2-lp3/SheetsAndPicks` | Projeto Gradle (JavaFX, módulos, jlink) |
| `gp2-lp3/SheetsAndPicks-1.0-SNAPSHOT` | Build empacotado |
| `Docs/Sprints` | Plannings, groomings e retrospetivas dos sprints (PDF) |
| `Docs/UAT Test Plan.pdf` | Plano de testes de aceitação |
| `XMLs` | Ficheiros XML de exemplo |

## Stack
Java · JavaFX 15 · Gradle · ControlsFX · SQL Server (mssql-jdbc) · JUnit

## Como executar
```bash
cd gp2-lp3/SheetsAndPicks
./gradlew run
```
Requer JDK 17+ e acesso à base de dados SQL Server. Definir o URL JDBC completo (com credenciais) na variável de ambiente `SHEETS_DB_URL`, p. ex. `jdbc:sqlserver://host;databaseName=db;user=u;password=p`. Ver `Controllers/database/DBConnect.java`.
