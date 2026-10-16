# Porto on Wheels

[🇬🇧 English](README.md) · 🇵🇹 Português

> Website de aluguer de veículos (carros, bicicletas, trotinetes) com contas, carrinho e back-office.

| | |
|---|---|
| **Contexto** | CTESP — Projeto web de grupo |
| **Ano** | 2023/24 |
| **Equipa** | Projeto de equipa |
| **Stack** | PHP · MySQL · HTML · CSS · JavaScript · Bootstrap (SB Admin back-office) |

## O que este projeto demonstra
- PHP renderizado no servidor com um pequeno **wrapper MySQL** próprio com prepared statements (`src/database/db.php`).
- Site público + área de utilizador autenticado + área de administração com CRUD.
- Front-end responsivo em Bootstrap.

## Funcionalidades
- Catálogo de carros, bicicletas e trotinetes; carrinho e reserva de aluguer.
- Registo, login, perfil de utilizador; contacto, FAQs, sobre nós.
- Dashboard de administração com tabelas e CRUD de utilizadores, carros e bicicletas.

## Estrutura
| Caminho | Conteúdo |
|---|---|
| `admin/` | Back-office (dashboard, tabelas, CRUD) |
| `src/database/` | Wrapper de BD, scripts de autenticação e reservas |
| `assets/` | CSS, JS, imagens |

## Execução
1. Criar uma base de dados MySQL `portoonwheels` (o esquema não vem no repositório original).
2. Definir `PORTOONWHEELS_DB_HOST`, `PORTOONWHEELS_DB_USER`, `PORTOONWHEELS_DB_PASSWORD`.
3. `php -S localhost:8000`

## Notas
- As credenciais originais da BD e o token Mapbox (`contact.php`, `<MAPBOX_PUBLIC_TOKEN>`) foram removidos; o mapa precisa de um token próprio.
- Versão académica: as passwords estão em texto simples — não adequado a produção.
