# Porto on Wheels

[🇬🇧 English](README.md) · 🇵🇹 Português

Projeto web de grupo: website de aluguer de veículos no Porto (carros, bicicletas e trotinetes) com contas de utilizador, carrinho e back-office de administração.

## O que faz
- **Site público** — catálogo (`cars.php`, `bikes.php`, `scooters.php`), carrinho, reserva de aluguer, registo/login, perfil, contacto, FAQs e sobre nós.
- **Admin (`admin/`)** — dashboard com tabelas e CRUD de utilizadores, carros e bicicletas.
- **`src/database/`** — pequeno wrapper PHP/MySQL (`db.php`) e scripts de autenticação/reserva.

## Stack
PHP · MySQL · HTML · CSS · JavaScript · Bootstrap (template SB Admin no back-office)

## Execução
1. Criar uma base de dados MySQL `portoonwheels` (o esquema não vem no repositório original).
2. Definir `PORTOONWHEELS_DB_HOST`, `PORTOONWHEELS_DB_USER` e `PORTOONWHEELS_DB_PASSWORD` (omissão: `localhost`, `root`, vazia).
3. Servir a pasta com PHP/Apache, p. ex. `php -S localhost:8000`.

## Notas
As credenciais originais da base de dados foram removidas. Nesta versão académica as passwords estão em texto simples.
