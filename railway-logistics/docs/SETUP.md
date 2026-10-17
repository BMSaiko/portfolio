# Setup — Railway Logistics

> Documentation added in 2026 (portfolio). Commands were **not tested** (this machine has no JDK, Maven, Oracle or RISC-V toolchain). Everything is read from `pom.xml`, the Makefiles and the READMEs. Portuguese version: [SETUP.pt.md](SETUP.pt.md).

## 1. `warehouseMngt` (Java)

| Item | Value |
|---|---|
| JDK / Maven | 17 / 3.x |
| Artifact | `logistics-on-rails` 1.0.0-SNAPSHOT |
| Libraries | JUnit 5, Mockito, AssertJ, Apache Commons CSV, Jackson, Oracle JDBC driver (database features) |
| Data | CSV files in `src/main/resources/data/` (items, bays, wagons, orders, order lines, returns, stations, lines, European train stations), loaded from the classpath |

```bash
cd railway-logistics/warehouseMngt
mvn test          # the README's command; 17 test classes, 169 @Test methods
mvn compile exec:java -Dexec.mainClass=pt.isep.dei.esinf.Main   # not in the README; the POM declares no exec plugin
```

The entry point opens a console menu that first asks for an access password (a constant in `MainMenuController`; see [INDEX §4](INDEX.md#4-verified-findings), finding 5). The warehouse and station features work from the CSV files; the freight, train-scheduling and traffic-manager menus connect to Oracle.

### Oracle

Needs an Oracle instance with the schema from `sql-scripts/` (below). The JDBC URL and credentials are **hard-coded** in the Java sources as local development defaults (host name of a team machine); change them in `FreightManagerMenuController`, `TrainSchedulerMenuController`, `TrafficManagerService`, `TravelTimeCalculatorService` and `TrainMovementService` before running against your own database. Do not commit real credentials.

## 2. `sql-scripts` (Oracle)

Run in this order per sprint, with SQL*Plus or SQL Developer:

1. `Sprint1/Model Files/CREATE_ALTER.sql`, then `INSERTS.sql`.
2. `Sprint2/Model Files/CREATE_ALTER.sql`, then `inserts.sql`.
3. `Sprint3/Model Files/Create_Alter.sql`, then `inserts.sql` (and `p_populate_route.sql`).
4. Per story: `User Stories/<USBDnn>/` — functions, inserts, then the `*_test.sql` or anonymous-block script.

`generate_sql_query.py` imports `pandas` (it is meant to turn the Excel datasets into inserts; `pip install pandas openpyxl`, not run; TODO: confirmar com o BMS the exact input file). `.vpp` files open in Visual Paradigm. Whether each sprint script is cumulative or standalone is not stated — TODO: confirmar com o BMS.

## 3. `stationMngt` (RISC-V assembly + C)

Each `usacNN` folder is self-contained with a `Makefile`.

| Requirement | From the Makefile |
|---|---|
| Cross compiler | `riscv32-linux-gcc` with `-march=rv32imd -mabi=ilp32d` |
| Emulator | `qemu-riscv32 -L $(LIBRV32)` |
| Sysroot | `$HOME/bin/bootlin/riscv32-buildroot-linux-gnu/sysroot` (Bootlin buildroot toolchain) |

```bash
cd railway-logistics/stationMngt/usac01
make         # builds program.elf
make run     # runs it under qemu-riscv32
```

`usac11-12` builds two test binaries (`usac11_test.elf`, `usac12_test.elf`) and reads `config.txt`; `usac16` uses `config.txt` and `raspberrycode.txt` (a sensor sketch, not built by `make`).

## 4. Documentation

PlantUML sources (`*.puml`, 54 files) are the editable diagrams; some have rendered `.svg` files next to them (28 `.svg` in total). Re-render with PlantUML if you change a diagram.
