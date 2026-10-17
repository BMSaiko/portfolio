# Railway Network Game (Integrative Project, 2nd semester)

[🇬🇧 English](README.md) · 🇵🇹 Português

> Simulação em Java em que o jogador constrói uma rede ferroviária, com algoritmos de grafos e desempenho medido.

| | |
|---|---|
| **Contexto** | LEI — Projeto Integrador do 2.º semestre (ESINF · MATCP · MDISC) |
| **Ano** | 2024/25 (Mar–Jun 2025) |
| **Equipa** | Grupo 152, 5 elementos, 3 sprints |
| **Stack** | Java 17 · Maven · JUnit · Jupyter (MATCP) · UML / PlantUML |

## A minha contribuição
- Adição de cidades e indústrias aos mapas (US02, US03) com testes e diagramas de sequência.
- Implementação do **caminho mais curto com waypoints** (US27) com Dijkstra / fecho transitivo, incluindo a UI.
- Criação da **framework de testes de desempenho e análise** dos algoritmos de conectividade e de rota de manutenção (US13/US14/US29), com cenários, resultados CSV e gráficos.
- Trabalho no notebook de MATCP e no relatório em LaTeX.

## O que este projeto demonstra
- **Algoritmos de grafos** (conectividade, caminhos mais curtos, rotas de manutenção) com **análise empírica de complexidade**.
- Design Java em camadas: controllers, serviços, DTOs, domínio e UI.
- Documentação completa do processo por user story (requisitos, análise, desenho, testes).
- Entrega em equipa com sprints Scrum.

## Funcionalidades
- Editor: criar mapas, adicionar cidades e indústrias.
- Jogador: construir estações e linhas, comprar locomotivas, circular comboios, geração de carga, manutenção, resultados financeiros, guardar/carregar jogos.
- Ficheiros de cenário e cenários de teste gerados em `test_scenarios/`.

## Estrutura
| Caminho | Conteúdo |
|---|---|
| `src/main/java/pt/ipp/isep/dei/` | Aplicação (controller, domain, dto, ui) |
| `docs/system-documentation/` | Documentação por US dos sprints 1–3 |
| `matcp.ipynb, MATCP/` | Componente de MATCP |
| `test_scenarios/` | Datasets de cenários |

## Execução
```bash
mvn compile exec:java -Dexec.mainClass=pt.ipp.isep.dei.Main
```

## Notas
- Documentação em inglês; ver `docs/TeamMembersAndTasks.md` para a distribuição de tarefas.

## Documentação

- [Auditoria da documentação](../docs/AUDIT.pt.md#sem2-pi-24-25-g152) — estado e lacunas deste projeto
- [Índice e rastreabilidade](docs/INDEX.pt.md) — US → documentos → código, testes, achados
- [Setup](docs/SETUP.pt.md) — pré-requisitos, execução, modo de desempenho (não testado)
- [Ponto de entrada da documentação](docs/README.md)
- [Documentação do sistema](docs/system-documentation/)
- [Membros da equipa e tarefas](docs/TeamMembersAndTasks.md)
