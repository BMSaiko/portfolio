# Setup — Gestão do Plant Floor

> Documentação adicionada em 2026 (portfolio). Os comandos **não foram testados** (esta máquina não tem JDK, Maven, GCC, Oracle nem ferramentas Arduino). Tudo vem do `pom.xml`, dos Makefiles, do `run.bat` e dos READMEs da equipa. Versão inglesa: [SETUP.md](SETUP.md).

## 1. `prodPlanSimulator` (Java)

| Item | Valor |
|---|---|
| JDK / Maven | 17 / 3.x |
| Classe principal | `org.example.Main` (plugin exec) |
| Bibliotecas | ojdbc8 19.3, JUnit 5.10, Mockito 5.3, SLF4J, Log4j 2 |

```bash
cd plant-floor-management/prodPlanSimulator
mvn test                    # 13 classes de teste, 38 métodos @Test
mvn clean compile exec:java # o que o run.bat faz (Windows)
```

O menu de consola tem 15 opções (carregar dados, correr simulação, árvore de produção, caminho crítico, PERT/CPM a partir de CSV, escalonamento, árvore AVL, geração de grafo, importar BOO/BOM do Oracle, atualizar tempos médios de produção, …).

**Caminhos de dados — ler antes de correr.** O simulador encontra `documentation/ESINF/USEI01/*.csv`, `USEI08/*.csv` e `Sprint03/BDcsv` subindo até ao `.git` mais próximo. Dentro do portfolio esse é a raiz do portfolio, por isso estes caminhos não existem e espera-se que "Load Data" falhe ([INDEX §4](INDEX.pt.md#4-achados-verificados), achado 1). Contorno: copiar este projeto para um repositório Git próprio, ou correr a partir de um checkout cuja raiz contenha `documentation/ESINF/`. A opção 10 do menu (PERT/CPM) pede um caminho explícito para o CSV e não depende disto.

**Oracle (opção 14 do menu).** Precisa de um Oracle XE local acessível com as definições JDBC de `src/main/resources/config.properties` (`db.url`, `db.username`, `db.password`, `db.internal_logon`); são um valor por omissão de desenvolvimento local. Não versionar credenciais reais; ver a lista de melhorias no INDEX.

## 2. `plantFloorManager` (Oracle PL/SQL)

Só scripts; não há runner.

1. Instalar o Oracle XE e ligar com SQL*Plus ou SQL Developer.
2. Criar o esquema com `documentation/BDDAD/Sprint2/USBD10/Modelo Fisico-Create Tables/Create Tables.sql` (Sprint 2) ou `documentation/BDDAD/Sprint3/DatasetS3_V01/create_db.sql` (dataset do Sprint 3).
3. Carregar dados com os `inserts.sql`. São gerados a partir dos datasets XML pelos scripts Python (`USBD11/generate_sql_inserts_from_xml.py`, `USBD22/scripts/generate_all.py`, `DatasetS3_V01/generate_sql_query.py`), e só importam a biblioteca padrão do Python (imports verificados; scripts não executados).
4. Correr o script de cada história (`USBD13.sql` … `USBD28.sql`) e os seus testes (`*_Test.sql`, `USBD28/test1…5.sql`, `USBD22/test-plsql`).

Existem duas cópias divergentes dos scripts do Sprint 2 ([INDEX §4](INDEX.pt.md#4-achados-verificados), achado 2); `documentation/BDDAD/` é a entrega da UC e `plantFloorManager/` a cópia de trabalho — TODO: confirmar com o BMS qual é a de referência.

## 3. `machineSupervisor` (C / Assembly / Arduino)

Requer Linux/WSL com GCC em x86-64 (o Assembly é `.s`/`.S` para o assembler GNU).

```bash
cd plant-floor-management/machineSupervisor/Sprint03
make          # compila ./prog a partir de src/*.c e Sprint2src/usac01..10.s
./prog        # lê data/machine_setup.csv e data/machine_*_operations.csv
```

O Makefile compila `prog` com `gcc -g -Wall`. `src/machine.ino` é o sketch Arduino da máquina física; o programa usa o `machineEmulator.c` (ver o README para o motivo). O sketch precisa do Arduino IDE configurado para um Raspberry Pi Pico W ([guia](../documentation/FSIAP/Sprint2/US01/Setting_Up_Arduino_IDE_for_Raspberry_Pi_Pico.md)); leitura série em WSL: [guia](../documentation/ARQCP/SerialForWSL/serialporforwsl.md).

Testes de Assembly do Sprint 2: em cada `documentation/ARQCP/Sprint02/tests/testUSAC0N/`, correr `make`.

## 4. Scripts FSIAP (Python)

`documentation/FSIAP/Sprint2/US02/read_pico_serial.py` e `Sprint3/US03/read_pico_serial_mq_2.py` leem a saída de sensores de um Pico W por porta série (fazem `import serial`, ou seja precisam de `pyserial`; TODO: confirmar com o BMS a porta série exata).
