# Setup — Logística Ferroviária

> Documentação adicionada em 2026 (portfolio). Os comandos **não foram testados** (esta máquina não tem JDK, Maven, Oracle nem toolchain RISC-V). Tudo vem do `pom.xml`, dos Makefiles e dos READMEs. Versão inglesa: [SETUP.md](SETUP.md).

## 1. `warehouseMngt` (Java)

| Item | Valor |
|---|---|
| JDK / Maven | 17 / 3.x |
| Artefacto | `logistics-on-rails` 1.0.0-SNAPSHOT |
| Bibliotecas | JUnit 5, Mockito, AssertJ, Apache Commons CSV, Jackson, driver JDBC Oracle (funcionalidades de BD) |
| Dados | Ficheiros CSV em `src/main/resources/data/` (itens, baías, vagões, encomendas, linhas de encomenda, devoluções, estações, linhas, estações europeias), carregados do classpath |

```bash
cd sem3-pi-25-26-g075/warehouseMngt
mvn test          # o comando do README; 17 classes de teste, 169 métodos @Test
mvn compile exec:java -Dexec.mainClass=pt.isep.dei.esinf.Main   # não está no README; o POM não declara o plugin exec
```

O ponto de entrada abre um menu de consola que começa por pedir uma password de acesso (uma constante em `MainMenuController`; ver [INDEX §4](INDEX.pt.md#4-achados-verificados), achado 5). As funcionalidades de armazém e de estações funcionam a partir dos CSV; os menus de carga, escalonamento de comboios e gestor de tráfego ligam ao Oracle.

### Oracle

Precisa de uma instância Oracle com o esquema de `sql-scripts/` (abaixo). O URL JDBC e as credenciais estão **fixos** no código Java como valores locais de desenvolvimento (nome de uma máquina da equipa); alterá-los em `FreightManagerMenuController`, `TrainSchedulerMenuController`, `TrafficManagerService`, `TravelTimeCalculatorService` e `TrainMovementService` antes de correr contra a vossa base de dados. Não versionar credenciais reais.

## 2. `sql-scripts` (Oracle)

Correr por esta ordem em cada sprint, com SQL*Plus ou SQL Developer:

1. `Sprint1/Model Files/CREATE_ALTER.sql`, depois `INSERTS.sql`.
2. `Sprint2/Model Files/CREATE_ALTER.sql`, depois `inserts.sql`.
3. `Sprint3/Model Files/Create_Alter.sql`, depois `inserts.sql` (e `p_populate_route.sql`).
4. Por história: `User Stories/<USBDnn>/` — funções, inserts, e por fim o `*_test.sql` ou o bloco anónimo.

O `generate_sql_query.py` importa `pandas` (destina-se a transformar os datasets Excel em inserts; `pip install pandas openpyxl`, não executado; TODO: confirmar com o BMS o ficheiro de entrada exato). Os `.vpp` abrem no Visual Paradigm. Não está dito se cada script de sprint é cumulativo ou autónomo — TODO: confirmar com o BMS.

## 3. `stationMngt` (Assembly RISC-V + C)

Cada pasta `usacNN` é autónoma, com um `Makefile`.

| Requisito | Do Makefile |
|---|---|
| Compilador cruzado | `riscv32-linux-gcc` com `-march=rv32imd -mabi=ilp32d` |
| Emulador | `qemu-riscv32 -L $(LIBRV32)` |
| Sysroot | `$HOME/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot` (toolchain Bootlin buildroot) |

```bash
cd sem3-pi-25-26-g075/stationMngt/usac01
make         # compila program.elf
make run     # corre-o em qemu-riscv32
```

`usac11-12` compila dois binários de teste (`usac11_test.elf`, `usac12_test.elf`) e lê `config.txt`; `usac16` usa `config.txt` e `raspberrycode.txt` (um sketch de sensores, não compilado pelo `make`).

## 4. Documentação

As fontes PlantUML (`*.puml`, 54 ficheiros) são os diagramas editáveis; alguns têm ficheiros `.svg` renderizados ao lado (28 `.svg` no total). Voltar a renderizar com o PlantUML se um diagrama for alterado.
