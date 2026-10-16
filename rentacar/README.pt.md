# RentACar

[🇬🇧 English](README.md) · 🇵🇹 Português

Projeto de grupo (ISEP, 2023/24): aplicação desktop de gestão de uma empresa de aluguer de automóveis.

## O que faz
- Login de staff com níveis de acesso e dashboard.
- Gestão de **clientes, staff, veículos e alugueres**, com pesquisa/filtro nas tabelas.
- Persistência em SQL Server via JDBC (`olabomdia.sql` em `src/main/java/` tem o script de esquema/dados).

## Stack
Java 19 · JavaFX · Gradle · SQL Server (JDBC)

## Execução
1. Criar a base de dados a partir de `RentACar/src/main/java/olabomdia.sql` e apontar `Database/DatabaseConnection.java` para ela.
2. Definir a variável de ambiente `RENTACAR_DB_PASSWORD`.
3. `cd RentACar && ./gradlew run`

## Notas
A password da base de dados que estava no repositório original foi removida.
