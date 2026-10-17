# Índice da documentação — Jogo de Rede Ferroviária (Projeto Integrador do 2.º semestre LEI, grupo 152)

> Documentação adicionada em 2026 (portfolio). Isto é um **mapa** dos documentos originais da equipa; nenhum foi editado. Os factos foram lidos do repositório (contagens medidas por comando); o que não é verificável está marcado `TODO: confirmar com o BMS`. Versão inglesa: [INDEX.md](INDEX.md). Ver também [SETUP](SETUP.pt.md), o [ponto de entrada](README.md) da equipa, a [distribuição de tarefas](TeamMembersAndTasks.md) e o [glossário](system-documentation/sprint2/global-artifacts/01.requirements-engineering/glossary.md) do domínio (não duplicado aqui).

## 1. Como a documentação está organizada

Cada pasta de sprint em [`system-documentation/`](system-documentation/) tem uma pasta por user story (US) segundo o template da cadeira: `01.requirements-engineering`, `02.analysis`, `03.design` (PlantUML em `puml/`, renderizado em `svg/`) e `04.tests-and-implementation`, mais uma pasta `global-artifacts/` (glossário, diagrama de casos de uso, especificação suplementar, modelo de domínio). As histórias de algoritmos do Sprint 3 (US13, US14, US27, US28, US29) são ficheiros Markdown únicos.

Equipa: 1221514 (Bruno Silva), 1221515, 1210491, 1221519, 1221526 ([TeamMembersAndTasks](TeamMembersAndTasks.md)).

## 2. Rastreabilidade: user story → documentos → código

"Responsável" vem de `TeamMembersAndTasks.md` (Sprint 1 / Sprint 2); essa tabela **não tem linhas do Sprint 3**, por isso os responsáveis do Sprint 3 são `TODO: confirmar com o BMS`. "Código" é o controller com o nome da história; está marcado *(pelo nome)* quando o código não cita o número da US.

| US | Assunto | Resp. S1 / S2 | Sprint 1 | Sprint 2 | Sprint 3 | Código |
|---|---|---|---|---|---|---|
| US01 | Editor cria um mapa | 1221519 | [docs](system-documentation/sprint1/US01) | [docs](system-documentation/sprint2/US01) | [docs](system-documentation/sprint3/US01) | `MapController` *(pelo nome)* |
| US02 | Editor adiciona uma indústria | 1221514 | [docs](system-documentation/sprint1/US02) | sem pasta | [docs](system-documentation/sprint3/US02) | `AddIndustryController` *(pelo nome)* |
| US03 | Editor adiciona uma cidade | 1221514 | [docs](system-documentation/sprint1/US03) | sem pasta | [docs](system-documentation/sprint3/US03) | `AddCityController` *(pelo nome)* |
| US04 | Editor cria um cenário | 1221515 | [docs](system-documentation/sprint1/US04) | [docs](system-documentation/sprint2/US04) | [docs](system-documentation/sprint3/US04) | `CreateScenarioController` *(pelo nome)* |
| US05 | Jogador constrói uma estação | 1221526 | [docs](system-documentation/sprint1/US05) | [docs](system-documentation/sprint2/US05) | [docs](system-documentation/sprint3/US05) | `BuildStationController` *(pelo nome)* |
| US06 | Jogador melhora uma estação | 1221526 | [docs](system-documentation/sprint1/US06) | [docs](system-documentation/sprint2/US06) | [docs](system-documentation/sprint3/US06) | `UpgradeStationController` *(pelo nome)* |
| US07 | Listar estações / detalhes | 1210491 | [docs](system-documentation/sprint1/US07) | [docs](system-documentation/sprint2/US07) | — | não identificado |
| US08 | Construir uma linha ferroviária | 1221515 | [docs](system-documentation/sprint1/US08) | [docs](system-documentation/sprint2/US08) | [docs](system-documentation/sprint3/US08) | `BuildRailwayLineController` *(pelo nome)* |
| US09 | Comprar uma locomotiva | 1221526 | [docs](system-documentation/sprint1/US09) | [docs](system-documentation/sprint2/US09) | [docs](system-documentation/sprint3/US09) | `BuyLocomotiveController` *(pelo nome)* |
| US10 | Atribuir um comboio a uma rota | 1210491 | [docs](system-documentation/sprint1/US10) | [docs](system-documentation/sprint2/US10) | — | `TrainController` *(pelo nome)* |
| US11 | Listar todos os comboios | 1221519 | [docs](system-documentation/sprint1/US11) | [docs](system-documentation/sprint2/US11) | [docs](system-documentation/sprint3/US11) | `TrainController` *(pelo nome)* |
| US12 | Simulador de geração de cargas | 1221515 | [docs](system-documentation/sprint1/US12) | [docs](system-documentation/sprint2/US12) | [docs](system-documentation/sprint3/US12) | `CargoGenerationController` *(pelo nome)* |
| US13 | Um comboio pode viajar entre duas estações | — / 1221514 | — | [doc do algoritmo](system-documentation/sprint2/US13/algorithm_documentation.md) | [doc do algoritmo](system-documentation/sprint3/US13/algorithm_documentation.md) | `RailwayConnectivityController` (cita US13) |
| US14 | Rota de manutenção por cada linha uma vez | — / 1221515 e 1221526 | — | [doc do algoritmo](system-documentation/sprint2/US14/algorithm_documentation.md) | [doc do algoritmo](system-documentation/sprint3/US14/algorithm_documentation.md) | `RailwayMaintenanceController` (cita US14) |
| US15 | Estatística do lucro médio anual | — / 1221526 | — | [LaTeX](system-documentation/sprint2/US15/doc.tex) | — | nenhum (análise, não código da aplicação) |
| US16 | Boxplots de chegada de passageiros | — / 1210491 | — | sem pasta | — | nenhum |
| US17 | Histograma de carga por estação | — / 1221515 | — | [LaTeX + histograma](system-documentation/sprint2/us17/) | — | nenhum |
| US18 | Gráficos circulares de comboios/passageiros/correio | — / 1221519 | — | [LaTeX](system-documentation/sprint2/US18/doc.tex) | — | nenhum |
| US23 | Guardar uma simulação em ficheiro | — | — | — | [docs](system-documentation/sprint3/US23) | `SaveGameController` *(pelo nome)* |
| US27 | Caminho mais curto com pontos intermédios | — | — | — | [doc](system-documentation/sprint3/US27/us27_documentation.md) | `controller/us27/TransitiveClosureShortestPathController` (cita US27) |
| US28 | Análise de complexidade de algoritmos | — | — | — | [doc](system-documentation/sprint3/US28) | não identificado |
| US29 | Desempenho assintótico de US13/US14 | — | — | — | [doc](system-documentation/sprint3/US29/us29_documentation.md), [MATCP/US29](../MATCP/US29/us29_documentation.md) | `Main` (modo `PERFORMANCE_TEST`) e `src/test/.../performance/` (cita US29) |
| US30, US31 | Notebooks MATCP | — | — | — | [MATCP/US30](../MATCP/US30/us30.md), [MATCP/US31](../MATCP/US31/us31.md) | Notebooks Jupyter, sem Java |

Autenticação: [`auth/`](auth/) descreve o componente reutilizado `AuthLib`. Datasets: [`datasets/`](datasets/). Entrada MATCP do Sprint 3: [README_MATCP.md](../README_MATCP.md).

## 3. Testes

`src/test/java` só tem o **harness de desempenho** (testes de tempo de US13/US14, exportação CSV/relatório, `US29PerformanceTestRunner` com 4 métodos `@Test`) e 60 CSVs de cenários gerados em `src/test/resources/test_scenarios/`. Não há testes unitários para US01–US12. Só os documentos de testes de US01 e US11 (sprint 2) têm excertos `@Test`.

## 4. Achados verificados

| # | Achado | Evidência |
|---|---|---|
| 1 | O `Main` arranca apenas um menu de consola para a funcionalidade de conectividade (5 opções: verificar US13, estações alcançáveis, rota de manutenção, caminho mais curto com pontos intermédios, equipa). Nenhuma das UIs de editor/jogador de US01–US12 (`AddIndustryUI`, `AddCityUI`, `BuildStationUI`, `BuildRailwayLineUI`, `BuyLocomotiveUI`, `UpgradeStationUI`, `CargoGenerationUI`, `CreateScenarioUI`, `SaveGameUI`, `ViewFinancialResultsUI`) é instanciada em lado nenhum, por isso a lista de funcionalidades do README (construir estações, comprar locomotivas, guardar/carregar…) descreve código inalcançável a partir da aplicação | `Main.java`, `RailwayConnectivityUI.java`; `grep "new <UI>"` = 0 para cada uma das dez |
| 2 | A "Structure" do README lista `test_scenarios/` na raiz; está em `src/test/resources/test_scenarios/` | listagem de diretórios |
| 3 | `TeamMembersAndTasks.md` acaba na US18 e não tem linhas do Sprint 3, apesar de o cabeçalho dizer que a tabela tem de estar sempre atualizada; a US16 aparece lá mas não tem pasta de documentação. As ligações do Sprint 2 para US02 e US03 apontam para pastas que não existem (US02/US03 estão documentadas só nos Sprints 1 e 3) | [TeamMembersAndTasks](TeamMembersAndTasks.md) |
| 4 | `docs/README.md` só liga os Sprints 1 e 2 | [README](README.md) |
| 5 | A pasta `us17` do Sprint 2 está em minúsculas e as outras usam `US`; US15/US17/US18 são só LaTeX (sem Markdown) | [sprint2](system-documentation/sprint2) |
| 6 | `execution-times.csv` está guardado duas vezes com conteúdo idêntico (`docs/datasets/`, `MATCP/US29/`) | `cmp` = idêntico |
| 7 | O `AuthLib` é obtido de um repositório Maven externo (`repo.repsy.io/mvn/pmaio/lei-soft`); se esse repositório desaparecer o build não o resolve | `pom.xml` |
| 8 | O projeto ainda se chama `project-template` no POM | `pom.xml` |

## 5. Lista de melhorias (não aplicadas)

| Prioridade | Melhoria | Esforço |
|---|---|---|
| P1 | Alinhar a lista de funcionalidades do README com o que o `Main` expõe, ou acrescentar um ponto de entrada para as UIs de US01–US12 | 1–2 h |
| P1 | Acrescentar as linhas (e responsáveis) do Sprint 3 ao `TeamMembersAndTasks.md` | 20 min |
| P2 | Acrescentar testes unitários às classes de domínio de US01–US12 (os documentos de testes já os descrevem) | 1–2 dias |
| P2 | Acrescentar um job GitHub Actions: `mvn -B verify` e verificação de links Markdown | 1 h |
| P3 | Renomear `us17` → `US17`; acrescentar resumos Markdown para US15/17/18 | 30 min |
| P3 | Manter uma só cópia de `execution-times.csv`; definir um `artifactId` real no POM | 15 min |
