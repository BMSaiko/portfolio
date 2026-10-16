# RentACar

🇬🇧 English · [🇵🇹 Português](README.pt.md)

Group project (ISEP, 2023/24): desktop application for managing a car rental company.

## What it does
- Staff login with access levels and a dashboard.
- Management of **clients, staff, vehicles and rentals**, with search/filtering in the tables.
- Persistence in SQL Server via JDBC (`olabomdia.sql` in `src/main/java/` has the schema/data script).

## Stack
Java 19 · JavaFX · Gradle · SQL Server (JDBC)

## Running
1. Create the database from `RentACar/src/main/java/olabomdia.sql` and point `Database/DatabaseConnection.java` to it.
2. Set the `RENTACAR_DB_PASSWORD` environment variable.
3. `cd RentACar && ./gradlew run`

## Notes
The database password committed in the original repository was removed.
