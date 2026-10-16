# Sheets and Picks (LP3)

[🇬🇧 English](README.md) · 🇵🇹 Português

> Aplicação desktop JavaFX para gestão de stock e encomendas, desenvolvida com Scrum.

| | |
|---|---|
| **Contexto** | CTESP — Laboratório de Programação 3 (LP3) |
| **Ano** | 2023/24 |
| **Equipa** | Grupo 2 — Scrum, seis sprints |
| **Stack** | Java · JavaFX 15 · Gradle · ControlsFX · SQL Server (mssql-jdbc) · JUnit · XML/XSD |

## O que este projeto demonstra
- **Entrega ágil**: plannings, groomings e retrospetivas de sprint e plano de testes UAT (`Docs/`).
- **Importação de XML validada por esquema XSD**.
- Interface desktop por perfis com JavaFX e persistência JDBC.

## Funcionalidades
- Login e dashboards por perfil: administradores, operadores, clientes, fornecedores.
- Gestão de utilizadores (criar, atualizar, listar por perfil).
- Consulta e confirmação de stock, movimentos de fornecedores.
- Importação de XML de encomendas/stock validada por `XSDverificator.xsd`; exemplos em `XMLs/`.

## Estrutura
| Caminho | Conteúdo |
|---|---|
| `gp2-lp3/SheetsAndPicks` | Projeto Gradle (JavaFX, módulos, jlink) |
| `Docs/Sprints` | Documentos de sprint (PDF, português) |
| `Docs/UAT Test Plan.pdf` | Plano de testes de aceitação |
| `XMLs/` | XML de exemplo |

## Execução
```bash
cd gp2-lp3/SheetsAndPicks
./gradlew run
```
Requer JDK 17+ e uma base de dados SQL Server; definir o URL JDBC em `SHEETS_DB_URL` (ver `Controllers/database/DBConnect.java`).

## Notas
- A documentação do projeto (PDFs) está em português, tal como entregue.
