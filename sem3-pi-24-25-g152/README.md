# LEI 3rd semester — Plant floor management

🇬🇧 English · [🇵🇹 Português](README.pt.md)

Integrative Project, LEI 3rd semester (ISEP, 2024/25) — Group project (5+ members).

## What it does
Four subsystems for an industrial plant: `plantFloorManager` (Oracle PL/SQL scripts), `machineSupervisor` (C and Assembly, Arduino firmware), `prodPlanSimulator` (Java production-plan simulator) and `projectManager` (PERT/CPM project scheduler). Database and analysis documentation in `documentation/`.

## Stack
Oracle PL/SQL · C · Assembly · Arduino · Java

## Notes
Oracle scripts need a local Oracle XE; the JDBC password in `prodPlanSimulator/src/main/resources/config.properties` is a local dev default.
Build outputs, IDE folders and jars were left out.
