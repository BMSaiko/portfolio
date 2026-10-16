# RentACar

[🇬🇧 English](README.md) · 🇵🇹 Português

> Aplicação desktop para gerir uma empresa de aluguer de automóveis.

| | |
|---|---|
| **Contexto** | CTESP — Projeto de grupo |
| **Ano** | 2023/24 |
| **Equipa** | Grupo 4 |
| **Stack** | Java 19 · JavaFX · Gradle · SQL Server (JDBC) |

## O que este projeto demonstra
- Interface JavaFX com pacotes por domínio (`Cliente`, `Staff`, `Veiculo`, `Aluguer`, `dash`).
- Uso de padrões de desenho (pacote `Singleton` para estado partilhado).
- Persistência em SQL Server via JDBC com script de esquema/dados.

## Funcionalidades
- Login de staff com níveis de acesso e dashboard.
- Gestão de clientes, staff, veículos e alugueres.
- Pesquisa/filtro nas tabelas.

## Estrutura
| Caminho | Conteúdo |
|---|---|
| `RentACar/src/main/java/com/example/rentacar/` | Código da aplicação |
| `RentACar/src/main/java/olabomdia.sql` | Script de esquema/dados |

## Execução
1. Criar a base de dados a partir de `RentACar/src/main/java/olabomdia.sql` e ajustar `Database/DatabaseConnection.java`.
2. Definir `RENTACAR_DB_PASSWORD`.
3. `cd RentACar && ./gradlew run`

## Notas
- A password da base de dados do repositório original foi removida.
