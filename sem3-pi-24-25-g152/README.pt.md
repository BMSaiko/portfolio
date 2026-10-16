# Plant Floor Management (Integrative Project, 3rd semester)

[🇬🇧 English](README.md) · 🇵🇹 Português

> Quatro subsistemas integrados para uma fábrica: base de dados, firmware de máquinas, simulador de planos de produção e escalonamento PERT/CPM.

| | |
|---|---|
| **Contexto** | LEI — Projeto Integrador do 3.º semestre (BDDAD · ESINF · ARQCP · LPRA3 · FSIAP) |
| **Ano** | 2024/25 (Oct 2024–Jan 2025) |
| **Equipa** | Grupo 152, 5+ elementos, 3 sprints |
| **Stack** | Java · Oracle PL/SQL · C · Assembly · Arduino · Python · JUnit |

## A minha contribuição
- Contribuições para o **simulador de produção em Java** (domínio, grafos, escalonamento, testes, user stories como USLP05/USLP07).
- Trabalho de **base de dados** Oracle: esquema/consultas e PL/SQL em várias histórias USBD (p. ex. USBD04, USBD22, USBD28) e **script Python** que gera inserts a partir dos datasets dos sprints.
- Artefactos Scrum (burndown, relatórios), glossário e entrega de FSIAP.

## O que este projeto demonstra
- Trabalho multi-tecnologia: **SQL/PL-SQL, Java, C, Assembly e Arduino** num só produto.
- Estruturas de dados e algoritmos: árvores AVL e de produção, grafos, análise de **caminho crítico (PERT/CPM)**.
- Pipelines de importação de dados e cobertura de testes no lado Java.
- Scrum com acompanhamento de burndown.

## Funcionalidades
- `plantFloorManager` — scripts Oracle PL/SQL por sprint.
- `machineSupervisor` — supervisão de máquinas em C e Assembly (código Arduino no Sprint 3).
- `prodPlanSimulator` — simulador Java: lista de operações, grafo de atividades, caminho crítico, tempo médio de produção.
- `documentation/` — entregas de BDDAD, ESINF, ARQCP, LPRA3 (Scrum) e FSIAP.

## Estrutura
| Caminho | Conteúdo |
|---|---|
| `plantFloorManager/` | Scripts Oracle |
| `machineSupervisor/` | C / Assembly / Arduino |
| `prodPlanSimulator/` | Simulador Java (Maven) |
| `documentation/` | Entregas das UCs |

## Execução
Java: `cd prodPlanSimulator && mvn test`. Os scripts Oracle precisam de um Oracle XE local; as definições JDBC em `src/main/resources/config.properties` são a predefinição local de desenvolvimento.

## Notas
- Alguns documentos estão em português.
