# Porto on Wheels

🇬🇧 English · [🇵🇹 Português](README.pt.md)

Group web project: a vehicle rental website for Porto (cars, bikes and scooters) with user accounts, a cart and an admin back-office.

## What it does
- **Public site** — catalogue pages (`cars.php`, `bikes.php`, `scooters.php`), cart, rental booking, registration/login, user profile, contact, FAQs and about pages.
- **Admin (`admin/`)** — dashboard with tables and CRUD for users, cars and bikes.
- **`src/database/`** — small PHP MySQL wrapper (`db.php`) and auth/booking scripts.

## Stack
PHP · MySQL · HTML · CSS · JavaScript · Bootstrap (SB Admin template for the back-office)

## Running
1. Create a MySQL database called `portoonwheels` (the schema is not included in the original repository).
2. Set `PORTOONWHEELS_DB_HOST`, `PORTOONWHEELS_DB_USER` and `PORTOONWHEELS_DB_PASSWORD` (defaults: `localhost`, `root`, empty).
3. Serve the folder with PHP/Apache, e.g. `php -S localhost:8000`.

## Notes
The original database credentials and Mapbox token (`contact.php`, placeholder `<MAPBOX_PUBLIC_TOKEN>`) were removed. Passwords are stored in plain text in this academic version.
