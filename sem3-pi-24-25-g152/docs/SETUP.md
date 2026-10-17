# Setup — Plant Floor Management

> Documentation added in 2026 (portfolio). Commands were **not tested** (this machine has no JDK, Maven, GCC, Oracle or Arduino tooling). Everything is read from `pom.xml`, the Makefiles, `run.bat` and the team's READMEs. Portuguese version: [SETUP.pt.md](SETUP.pt.md).

## 1. `prodPlanSimulator` (Java)

| Item | Value |
|---|---|
| JDK / Maven | 17 / 3.x |
| Main class | `org.example.Main` (exec plugin) |
| Libraries | ojdbc8 19.3, JUnit 5.10, Mockito 5.3, SLF4J, Log4j 2 |

```bash
cd sem3-pi-24-25-g152/prodPlanSimulator
mvn test                    # 13 test classes, 38 @Test methods
mvn clean compile exec:java # what run.bat does (Windows)
```

The console menu has 15 options (load data, run simulation, production tree, critical path, PERT/CPM from CSV, scheduling, AVL tree, graph generation, import BOO/BOM from Oracle, update average production times, …).

**Data paths — read before running.** The simulator finds `documentation/ESINF/USEI01/*.csv`, `USEI08/*.csv` and `Sprint03/BDcsv` by walking up to the nearest `.git` directory. Inside the portfolio that is the portfolio root, so these paths do not exist and "Load Data" is expected to fail ([INDEX §4](INDEX.md#4-verified-findings), finding 1). Work-around: copy this project to its own Git repository, or run it from a checkout whose root contains `documentation/ESINF/`. Menu option 10 (PERT/CPM) asks for an explicit CSV path and does not depend on this.

**Oracle (menu option 14).** Needs a local Oracle XE reachable with the JDBC settings in `src/main/resources/config.properties` (`db.url`, `db.username`, `db.password`, `db.internal_logon`); those are a local development default. Do not commit real credentials; see the improvement list in the INDEX.

## 2. `plantFloorManager` (Oracle PL/SQL)

Scripts only; there is no runner.

1. Install Oracle XE and connect with SQL*Plus or SQL Developer.
2. Create the schema with `documentation/BDDAD/Sprint2/USBD10/Modelo Fisico-Create Tables/Create Tables.sql` (Sprint 2) or `documentation/BDDAD/Sprint3/DatasetS3_V01/create_db.sql` (Sprint 3 dataset).
3. Load data with `inserts.sql` files. They are generated from the XML datasets by the Python scripts (`USBD11/generate_sql_inserts_from_xml.py`, `USBD22/scripts/generate_all.py`, `DatasetS3_V01/generate_sql_query.py`) and import only the Python standard library (imports checked; scripts not run).
4. Run each story's script (`USBD13.sql` … `USBD28.sql`) and its tests (`*_Test.sql`, `USBD28/test1…5.sql`, `USBD22/test-plsql`).

Two diverging copies of the Sprint 2 scripts exist ([INDEX §4](INDEX.md#4-verified-findings), finding 2); `documentation/BDDAD/` is the course deliverable and `plantFloorManager/` the working copy — TODO: confirmar com o BMS which is authoritative.

## 3. `machineSupervisor` (C / Assembly / Arduino)

Linux/WSL with GCC on x86-64 is required (the Assembly is `.s`/`.S` for the GNU assembler).

```bash
cd sem3-pi-24-25-g152/machineSupervisor/Sprint03
make          # builds ./prog from src/*.c and Sprint2src/usac01..10.s
./prog        # reads data/machine_setup.csv and data/machine_*_operations.csv
```

The Makefile builds `prog` with `gcc -g -Wall`. `src/machine.ino` is the Arduino sketch for the physical machine; the program uses `machineEmulator.c` instead (see the README for why). The sketch needs the Arduino IDE configured for a Raspberry Pi Pico W ([guide](../documentation/FSIAP/Sprint2/US01/Setting_Up_Arduino_IDE_for_Raspberry_Pi_Pico.md)); serial reading on WSL: [guide](../documentation/ARQCP/SerialForWSL/serialporforwsl.md).

Sprint 2 Assembly tests: in each `documentation/ARQCP/Sprint02/tests/testUSAC0N/`, run `make`.

## 4. FSIAP scripts (Python)

`documentation/FSIAP/Sprint2/US02/read_pico_serial.py` and `Sprint3/US03/read_pico_serial_mq_2.py` read sensor output from a Pico W over a serial port (they `import serial`, i.e. need `pyserial`; TODO: confirmar com o BMS the exact serial port).
