# Índice da documentação — Gestão do Plant Floor (Projeto Integrador do 3.º semestre LEI, grupo 152)

> Documentação adicionada em 2026 (portfolio). Isto é um **mapa** do material original da equipa; nada foi editado. Os factos foram lidos do repositório (contagens medidas por comando); o que não é verificável está marcado `TODO: confirmar com o BMS`. Versão inglesa: [INDEX.md](INDEX.md). Ver também [SETUP](SETUP.pt.md) e os glossários existentes ([glossário do projeto LAPR3](../documentation/LPRA3/Sprint3/Glossary/glossary.md), [glossário BDDAD](../documentation/BDDAD/Sprint1/USBD01/glossario.md)), que são ligados, não duplicados.

Os restantes membros aparecem nos READMEs da própria equipa; este índice só nomeia o autor do repositório (Bruno Silva) e remete para esses READMEs.

## 1. Os quatro subsistemas e onde vive cada um

| Subsistema | UC | Código | Documentação da UC | Linguagem |
|---|---|---|---|---|
| Base de dados (Oracle) | BDDAD | [`plantFloorManager/`](../plantFloorManager/) (scripts dos Sprints 2–3) | [`documentation/BDDAD/`](../documentation/BDDAD/) (Sprints 1–3) | SQL, PL/SQL, Python |
| Supervisor de máquinas (C/Assembly/Arduino) | ARQCP, FSIAP | [`machineSupervisor/`](../machineSupervisor/) | [`documentation/ARQCP/`](../documentation/ARQCP/), [`documentation/FSIAP/`](../documentation/FSIAP/) | C, Assembly x86-64, C++ Arduino, Python |
| Simulador do plano de produção | ESINF | [`prodPlanSimulator/`](../prodPlanSimulator/) | [`documentation/ESINF/`](../documentation/ESINF/) | Java 17 (Maven) |
| Gestão de projeto | LPRA3 (LAPR3) | — (`projectManager/` é um placeholder vazio) | [`documentation/LPRA3/`](../documentation/LPRA3/) | Markdown, PPTX, XLSX |

## 2. Rastreabilidade: família de user stories → documentos → código

Prefixos: `USBD` = base de dados, `USAC` = arquitetura de computadores (C/Assembly), `USEI` / `USLP` = estruturas de dados / programação (Java), `USFA` = FSIAP (sensores).

| Histórias | Sprint | Documentos | Código / scripts | Notas |
|---|---|---|---|---|
| USBD01–USBD09 | 1 | [`documentation/BDDAD/Sprint1/`](../documentation/BDDAD/Sprint1/) (glossário, modelo conceptual/lógico `.vpp`, script legado do dataset, revisão) | `USBD04/script_legacy.py` + [doc](../documentation/BDDAD/Sprint1/USBD04/doc_script_legacy.md), `USBD08.sql` | Vários ficheiros chamam-se `USBD0N` sem extensão |
| USBD10–USBD19 | 2 | [`documentation/BDDAD/Sprint2/`](../documentation/BDDAD/Sprint2/) e [`plantFloorManager/Sprint2/`](../plantFloorManager/Sprint2/) (responsáveis no [README](../plantFloorManager/Sprint2/README)) | `Create Tables.sql`, `generate_sql_inserts_from_xml.py`, `USBD13.sql` … `USBD19.sql` | Duas cópias paralelas, ver achado 2 |
| USBD20–USBD30 | 3 | [`plantFloorManager/Sprint3/`](../plantFloorManager/Sprint3/) (responsáveis no [README](../plantFloorManager/Sprint3/README); a USBD29 está registada como "Not Made") | `USBD22/scripts/*.py` (dataset → inserts), `USBD28/usbd28.sql` e testes, SQL de `USBD23`–`USBD27` | Segundo esse README, o Bruno Silva fez a USBD22 e a USBD28 |
| USAC01–USAC10 | 2 | [`documentation/ARQCP/Sprint02/`](../documentation/ARQCP/Sprint02/) | `src/USAC01.S` … `USAC10.S`, `tests/testUSAC*/`; código relacionado em [`machineSupervisor/Sprint02/`](../machineSupervisor/Sprint02/) | Testes Unity em `machineSupervisor/Sprint02/utests` |
| USAC11–USAC18 | 3 | [`machineSupervisor/Sprint03/README.MD`](../machineSupervisor/Sprint03/README.MD) (uma secção por história, em português) | `Sprint03/src/*.c`, `include/*.h`, `machine.ino`, `Sprint2src/usac*.s`, `data/*.csv`, `Makefile` | `machine.ino` é o sketch Arduino; `machineEmulator.c` substitui o hardware (explicado no README) |
| USEI01, USEI08 | 3 | [`documentation/ESINF/`](../documentation/ESINF/) ([Technical_Documentation.md](../documentation/ESINF/Technical_Documentation.md), análise de complexidade em `Sprint2/`, `HowToSurvive.md`) | `prodPlanSimulator/src/main/java/com/example/production/` (Domain, Graphs, Service, UI, utils) | CSVs de entrada em `documentation/ESINF/USEI01`, `USEI08`, `Sprint03/BDcsv` |
| USLP04, USLP05, USLP07 | 3 | Technical_Documentation.md | `Graphs/USLP04GraphsGene.java` + teste; PERT/CPM e escalonamento em `Domain/CriticalPathAnalyzer.java`, `ScheduleCalculator.java`; opções 10–11 do menu em `UI/MainInterface.java` | Só USLP04 e USLP07 são citadas no código; a USLP05 é citada no README da raiz |
| USFA01–USFA03 | 2–3 | [`documentation/FSIAP/`](../documentation/FSIAP/) (setup do Raspberry Pi Pico W, código dos sensores, `USFA01.txt`, `USFA03.txt`) | `read_pico_serial.py`, `read_pico_serial_mq_2.py` | Scripts de sensores e leitura série |
| Scrum (todos os sprints) | 1–3 | [`documentation/LPRA3/`](../documentation/LPRA3/) (diretrizes, retrospetiva, relatórios de sprint `.pptx`, autoavaliações `.xlsx`, burndown) | — | Ficheiros Office binários |

## 3. Testes

| Área | Evidência |
|---|---|
| Java | 13 classes de teste com 38 métodos `@Test` em `prodPlanSimulator/src/test` (JUnit 5, Mockito) |
| C/Assembly | `documentation/ARQCP/Sprint02/tests/testUSAC01…10` (um `test.c` + makefile cada); Unity em `machineSupervisor/Sprint02/utests` |
| SQL | `USBD13_Test.sql`, `USBD19_Test.sql`, `USBD22/test-plsql`, `USBD28/test1…5.sql` com capturas dos resultados |

## 4. Achados verificados

| # | Achado | Evidência |
|---|---|---|
| 1 | O simulador localiza os CSV de entrada subindo até ao `.git` mais próximo e juntando `documentation/ESINF/...`. Neste portfolio a raiz Git é o repositório do portfolio, não este projeto, por isso os caminhos resolvem para `portfolio/documentation/ESINF/...`, que não existe. Espera-se portanto que "Load Data" falhe aqui, a não ser que o projeto seja clonado/corrido como repositório próprio (previsão a partir do código, não uma execução observada) | `RepPathUtils.java`, `MainInterface.java` linhas 43–47, `DataImportService.java` linha 29; `ls portfolio/documentation` = não existe |
| 2 | `documentation/BDDAD/Sprint2` e `plantFloorManager/Sprint2` são duas cópias divergentes das mesmas histórias (20 diferenças: por exemplo `inserts.sql` e `get_product_parts.sql` diferentes, ficheiros extra de cada lado) | `diff -rq` |
| 3 | Há lixo de controlo de versões versionado: 6 ficheiros `.o` compilados, 15 cópias de segurança `.bak_*` do Visual Paradigm, 2 ficheiros de bloqueio `.lck`, vários `.DS_Store` | `git ls-files` |
| 4 | Várias histórias são ficheiros sem extensão (`USBD03`, `USBD5`, `USBD6`, `USBD07`, `USBD09`, `USBD15`, `USBD16`, `README`), por isso não são renderizados nem coloridos | listagem de ficheiros |
| 5 | `machineSupervisor/Sprint03/README.MD` diz que a máquina física (Arduino) não pôde ser concluída porque membros desistiram da cadeira, por isso um emulador em software a substitui | README USAC11 |
| 6 | `projectManager/` e `documentation/empty.txt` são placeholders vazios | listagem de ficheiros |
| 7 | `prodPlanSimulator/src/main/resources/config.properties` tem uma password Oracle local não vazia (valor por omissão de desenvolvimento; não reproduzido). A importação Oracle (opção 14 do menu) precisa de um Oracle XE local | `grep -c`, README |
| 8 | Os documentos das UCs misturam português e inglês | ficheiros |

## 5. Lista de melhorias (não aplicadas)

| Prioridade | Melhoria | Esforço |
|---|---|---|
| P1 | Fazer o simulador resolver os caminhos de dados relativamente ao projeto (ou por argumento) em vez da raiz Git | 1 h |
| P1 | Passar `config.properties` para um ficheiro não versionado com um `config.properties.example` e rodar a password de desenvolvimento | 30 min |
| P2 | Remover `.o`, `.bak_*`, `.lck`, `.DS_Store` versionados; completar o `.gitignore` | 15 min |
| P2 | Resolver as cópias duplicadas BDDAD/plantFloorManager do Sprint 2 (manter uma, ligar a outra) | 1 h |
| P2 | Acrescentar CI: `mvn -B test` em `prodPlanSimulator` e `make` em `machineSupervisor/Sprint03` | 1 h |
| P3 | Dar extensão `.sql` ou `.md` aos ficheiros de US sem extensão | 30 min |
| P3 | Preencher ou remover o placeholder vazio `projectManager/` | 5 min |
