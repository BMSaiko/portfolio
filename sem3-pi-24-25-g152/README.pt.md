# LEI 3rd semester — Plant floor management

[🇬🇧 English](README.md) · 🇵🇹 Português

Projeto Integrador do 3.º semestre da LEI (ISEP, 2024/25) — Projeto de grupo (5+ elementos).

## O que faz
Quatro subsistemas para uma fábrica: `plantFloorManager` (scripts Oracle PL/SQL), `machineSupervisor` (C e Assembly, firmware Arduino), `prodPlanSimulator` (simulador de planos de produção em Java) e `projectManager` (planeamento PERT/CPM). Documentação de BD e análise em `documentation/`.

## Stack
Oracle PL/SQL · C · Assembly · Arduino · Java

## Notas
Os scripts Oracle precisam de um Oracle XE local; a password JDBC em `prodPlanSimulator/src/main/resources/config.properties` é a predefinição local de desenvolvimento.
Pastas de build, de IDE e jars ficaram de fora.
