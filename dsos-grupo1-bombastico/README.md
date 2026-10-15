# DSOS Group 1 — Project Proposals Platform

🇬🇧 English · [🇵🇹 Português](README.pt.md)

Web application built with Laravel for the *Desenvolvimento de Sistemas Orientados a Serviços* course (ISEP, 2023/24). It manages the lifecycle of internship and project proposals between **students, teachers and companies**.

## What it does
- Registration, login and password recovery.
- Companies/teachers publish **proposals**; students apply, teachers apply as supervisors.
- Accept or reject student applications; track proposal and application states.
- Project delivery (file submission) and **feedback** on projects.
- Profile editing, company and student listings, application PDF.

## Structure
| Path | Content |
|---|---|
| `DSOS-grupo1-Bombastico/` | Laravel app (`app/Models`, `app/Http/Controllers`, `routes/web.php`, migrations) |
| `DB/2023_ERM_DSOS_G1.sql` | Database dump (users, students, teachers, companies, proposals, projects, relations) |

## Stack
PHP · Laravel · MySQL · Blade · Bootstrap (Material Dashboard) · Composer · npm

## Running
```bash
cd DSOS-grupo1-Bombastico
composer install
cp .env.example .env      # set DB credentials
php artisan key:generate
php artisan migrate       # or import ../DB/2023_ERM_DSOS_G1.sql
php artisan serve
```
