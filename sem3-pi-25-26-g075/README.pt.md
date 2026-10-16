# Railway Logistics (Integrative Project, 3rd semester)

[🇬🇧 English](README.md) · 🇵🇹 Português

> Logística ferroviária e de armazéns: estruturas de dados avançadas em Java, código de baixo nível em assembly/C e base de dados Oracle.

| | |
|---|---|
| **Contexto** | LEI — Projeto Integrador do 3.º semestre (ESINF · ARQCP · BDDAD · LAPR3) |
| **Ano** | 2025/26 (Oct 2025–Jan 2026) |
| **Equipa** | Grupo 075, 5 elementos, 3 sprints |
| **Stack** | Java · Oracle SQL · RISC-V Assembly · C · Raspberry Pi · Python · Scrum |

## A minha contribuição
- Construção de uma **KD-tree 2D balanceada** (USEI07): requisitos, análise, desenho, implementação, testes e análise de complexidade.
- **Ranking de hubs ferroviários** com métricas de centralidade (e testes) e serviço de **plano de upgrade da rede** com deteção de ciclos.
- Análises de complexidade de várias histórias ESINF (p. ex. USEI11, USEI13).
- ARQCP: **cifra de César** e **buffer circular** em assembly, controlo de **sensor DHT + semáforo** em Raspberry Pi (C), gestão de configuração com encriptação.

## O que este projeto demonstra
- **Estruturas de dados e algoritmos**: KD-tree, AVL, centralidade em grafos, heurísticas de empacotamento (FF/FFD/BFD), alocação FEFO/FIFO — cada uma com análise de complexidade.
- Competências de baixo nível: assembly RISC-V, C em hardware embebido.
- Scripts SQL por sprint e relatórios Scrum com burndown (`scrum/`).
- Documentação por user story: requisitos, análise, desenho, testes.

## Funcionalidades
- `warehouseMngt` — gestão de armazém em Java: alocação de caixas, alocação de encomendas (STRICT/PARTIAL), empacotamento de trolleys, agendamento de comboios e tempos de viagem.
- `stationMngt` — código do lado da estação por user story (`usac01`…`usac16`): assembly e C.
- `sql-scripts/` — esquema e consultas Oracle por sprint.
- `docs/` — documentação de ESINF, ARQCP, BDDAD e LAPR3.

## Estrutura
| Caminho | Conteúdo |
|---|---|
| `warehouseMngt/` | Aplicação Java (Maven) |
| `stationMngt/` | Assembly / C por user story |
| `sql-scripts/` | Scripts Oracle |
| `scrum/` | Relatórios de sprint e burndown |
| `docs/` | Documentação |

## Execução
Java: `cd warehouseMngt && mvn test`. As funcionalidades com base de dados precisam de uma instância Oracle local; as definições de ligação nos serviços Java e nos `config.txt` são predefinições locais de desenvolvimento.

## Notas
- A documentação está maioritariamente em inglês.
