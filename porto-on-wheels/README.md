# Porto on Wheels

🇬🇧 English · [🇵🇹 Português](README.pt.md)

> Vehicle rental website (cars, bikes, scooters) with accounts, cart and admin back-office.

| | |
|---|---|
| **Context** | CTESP — Group web project |
| **Year** | 2023/24 |
| **Team** | Team project |
| **Stack** | PHP · MySQL · HTML · CSS · JavaScript · Bootstrap (SB Admin back-office) |

## What this project shows
- Server-rendered PHP with a small custom **MySQL wrapper** using prepared statements (`src/database/db.php`).
- Public site + authenticated user area + admin area with CRUD.
- Responsive front-end built on Bootstrap.

## Features
- Catalogue pages: cars, bikes, scooters; cart and rental booking.
- Registration, login, user profile; contact, FAQs, about.
- Admin dashboard with tables and CRUD for users, cars and bikes.

## Structure
| Path | Content |
|---|---|
| `admin/` | Back-office (dashboard, tables, CRUD) |
| `src/database/` | DB wrapper, auth and booking scripts |
| `assets/` | CSS, JS, images |

## Running
1. Create a MySQL database `portoonwheels` (schema not included in the original repo).
2. Set `PORTOONWHEELS_DB_HOST`, `PORTOONWHEELS_DB_USER`, `PORTOONWHEELS_DB_PASSWORD`.
3. `php -S localhost:8000`

## Notes
- Original DB credentials and the Mapbox token (`contact.php`, `<MAPBOX_PUBLIC_TOKEN>`) were removed, so the map needs your own token.
- Academic version: passwords are stored in plain text — not suitable for production.

## Documentation

- [Documentation audit](../docs/AUDIT.md#porto-on-wheels) — status and gaps for this project
- [Architecture, inferred data model and known issues](docs/ARCHITECTURE.md)
- [Setup and troubleshooting](docs/SETUP.md)
- [ADR 0001 — Framework-less PHP with a custom mysqli wrapper](docs/adr/0001-custom-mysqli-wrapper.md)
