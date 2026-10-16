# Setup — Porto on Wheels

> Documentation added in 2026 (portfolio). Derived from the source code. **No command below was run**: this machine has no PHP or MySQL set up for the project (marked `not tested`). The original repository has no schema, so the SQL in step 2 is **reconstructed from the queries in the code** and may not match the original database. Portuguese version: [SETUP.pt.md](SETUP.pt.md).

## Prerequisites

| Item | Version | Source |
|---|---|---|
| PHP | 7.4 or newer with `mysqli`; `file_uploads` on and `upload_max_filesize` large enough for product images | `src/database/db.php`, `admin/db/insert.php` |
| MySQL / MariaDB | any recent version | `conn.php` |
| Internet access | for the CDN assets (Bootstrap, DataTables, Font Awesome) and the Mapbox map | `src/layout/header.php`, `contact.php` |

There is no Composer or npm step.

## Environment variables

| Variable | Used by | Default |
|---|---|---|
| `PORTOONWHEELS_DB_HOST` | `src/database/conn.php` | `localhost` |
| `PORTOONWHEELS_DB_USER` | `src/database/conn.php` | `root` |
| `PORTOONWHEELS_DB_PASSWORD` | `src/database/conn.php` | empty |

The database name `portoonwheels` is fixed in `conn.php`. For the map, replace the placeholder `<MAPBOX_PUBLIC_TOKEN>` in `contact.php` with your own public token (do not commit it).

## Steps

Run from `porto-on-wheels/` (not tested).

1. **Set the variables**
   - PowerShell: `$env:PORTOONWHEELS_DB_USER = "<user>"; $env:PORTOONWHEELS_DB_PASSWORD = "<password>"`
   - bash: `export PORTOONWHEELS_DB_USER='<user>' PORTOONWHEELS_DB_PASSWORD='<password>'`

   Use a shell that exports them to the PHP process (built-in server started from the same shell).
2. **Create the database** (reconstructed, inferred types):
   ```sql
   CREATE DATABASE portoonwheels CHARACTER SET utf8mb4;
   USE portoonwheels;
   CREATE TABLE accounts (
     id INT AUTO_INCREMENT PRIMARY KEY,
     name VARCHAR(100), email VARCHAR(150), password VARCHAR(255),
     phone_number VARCHAR(30), role VARCHAR(20) DEFAULT 'USER',
     profile_img MEDIUMBLOB
   );
   CREATE TABLE products (
     id INT AUTO_INCREMENT PRIMARY KEY,
     type_id INT NOT NULL,            -- 1 bikes, 2 scooters, 3 cars
     name VARCHAR(100), quantity INT, image MEDIUMBLOB
   );
   CREATE TABLE plans (id INT AUTO_INCREMENT PRIMARY KEY, hours INT, price DECIMAL(8,2));
   CREATE TABLE partners (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100), image MEDIUMBLOB);
   CREATE TABLE rents (
     id INT AUTO_INCREMENT PRIMARY KEY,
     product_id INT, account_id INT, date VARCHAR(30),
     quantity INT, plan INT, payment TINYINT DEFAULT 0, state TINYINT DEFAULT 0
   );
   ```
   `role` must be exactly `ADMIN` for an administrator. `rents.date` is a string because the form sends `yyyy-dd-mm HH:MM`; the original type is unknown.
3. **Create an admin**: register a normal user at `register.php`, then set `role = 'ADMIN'` in `accounts`. Because of known issue #2 the stored password equals the e-mail address, so log in with the e-mail as password, or update the `password` column by hand.
4. **Run**
   ```
   php -S localhost:8000
   ```
   Open `http://localhost:8000/` (public site) and `http://localhost:8000/admin/login.php` (back-office).

## Tests

None. There is no test suite and no CI.

## Troubleshooting

| Symptom | Likely cause | Fix |
|---|---|---|
| `Failed to connect to MySQL - Access denied` printed on the page | Wrong user or password, or the variables are not exported in the shell that started `php -S` | Re-export and restart the server |
| `Failed to connect to MySQL - Unknown database 'portoonwheels'` | Database not created | Step 2 |
| `Unable to prepare MySQL statement` | A table or column differs from the code | Align the schema with the queries (see [ARCHITECTURE](ARCHITECTURE.md#data-model-inferred)) |
| `Class "mysqli" not found` | `mysqli` extension disabled | Enable `extension=mysqli` in `php.ini` |
| Registered user cannot log in | Known issue #2 (e-mail stored as password) | Log in with the e-mail as password |
| Product image upload fails or images are empty | Upload limits or `max_allowed_packet` too small | Raise `upload_max_filesize`, `post_max_size` and `max_allowed_packet` |
| Map is blank | Mapbox placeholder token | Add your own token |
| Styles missing | No internet access (CDN) | Connect, or host the CDN files locally |

## Security notes

- Passwords are plain text and several admin endpoints are unauthenticated (see [Known issues](ARCHITECTURE.md#known-issues-verified-in-the-code-not-fixed) #1 and #3): run only on localhost and never expose it to a network.
- Never commit real database credentials or a Mapbox token.
